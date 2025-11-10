package com.ljx.gulimall.cart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.ljx.common.context.GulimallThreadContext;
import com.ljx.common.domain.dto.UserInfoDTO;
import com.ljx.common.exception.RRException;
import com.ljx.common.utils.AssertUtil;
import com.ljx.common.utils.R;
import com.ljx.gulimall.cart.constant.CartCacheConstant;
import com.ljx.gulimall.cart.domain.entity.SkuInfoEntity;
import com.ljx.gulimall.cart.domain.vo.CartItemVo;
import com.ljx.gulimall.cart.domain.vo.CartVo;
import com.ljx.gulimall.cart.feign.ProductFeignService;
import com.ljx.gulimall.cart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static java.time.chrono.JapaneseEra.values;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public CartItemVo addToCart(Long skuId, Integer num) {
        AssertUtil.isNotEmpty(skuId, "skuId不能为空");
        AssertUtil.isNotEmpty(num, "count不能为空");

        BoundHashOperations<String, Object, Object> cartCacheOps = getCartCacheOps();
        Object cartItemVOStr = cartCacheOps.get(skuId);
        if (Objects.nonNull(cartItemVOStr)) {
            // 已经在redis中缓存了，只需要修改数量
            CartItemVo cartItemVo = JSONUtil.toBean(cartItemVOStr.toString(), CartItemVo.class);
            cartItemVo.setCount(num);
            cartCacheOps.put(skuId, JSONUtil.toJsonStr(cartItemVo));
            return cartItemVo;
        }

        // 没有缓存就需要走一遍查询
        CartItemVo cartItemVo = new CartItemVo();
        cartItemVo.setCount(num);

        try {
            // 查询sku信息
            CompletableFuture<Void> getSkuInfoFuture = CompletableFuture.runAsync(() -> {
                R<SkuInfoEntity> skuInfoEntityR = productFeignService.getSkuInfo(skuId);
                if (skuInfoEntityR.getCode() == 0) {
                    SkuInfoEntity skuInfo = skuInfoEntityR.getDataObj(SkuInfoEntity.class);
                    cartItemVo.setSkuId(skuId);
                    cartItemVo.setPrice(skuInfo.getPrice());
                    cartItemVo.setTitle(skuInfo.getSkuName());
                    cartItemVo.setImage(skuInfo.getSkuDefaultImg());
                    cartItemVo.setCheck(false);
                    cartItemVo.setTotalPrice(skuInfo.getPrice());
                    cartItemVo.setPrice(skuInfo.getPrice());
                }
            }, threadPoolExecutor);

            // 查询销售属性
            CompletableFuture<Void> getSkuAttrValuesFuture = CompletableFuture.runAsync(() -> {
                R<List<String>> listR = productFeignService.getListBySkuId(skuId);
                if (listR.getCode() == 0) {
                    List skuAttrValues = listR.getDataObj(List.class);
                    cartItemVo.setSkuAttrValues(skuAttrValues);
                }
            }, threadPoolExecutor);

            CompletableFuture.allOf(getSkuInfoFuture, getSkuAttrValuesFuture).get();

            cartCacheOps.put(skuId, JSONUtil.toJsonStr(cartItemVo));

            return cartItemVo;
        } catch (Exception e) {
            log.error("查询sku信息出错：{}", e.getMessage(), e);
            throw new RRException("查询sku信息出错");
        }
    }


    private BoundHashOperations<String, Object, Object> getCartCacheOps() {
        UserInfoDTO userInfo = GulimallThreadContext.getUserInfo();
        String userCacheKey;
        if (Objects.isNull(userInfo.getUserId())) {
            userCacheKey = CartCacheConstant.USER_CART_SKU_INFO + userInfo.getUserKey();
        } else {
            userCacheKey = CartCacheConstant.USER_CART_SKU_INFO + userInfo.getUserId();
        }

        return getCartCacheOpsByKey(userCacheKey);
    }

    private BoundHashOperations<String, Object, Object> getCartCacheOpsByKey(String key) {
        return redisTemplate.boundHashOps(key);
    }

    @Override
    public CartItemVo getCartInfoBySkuId(Long skuId) {
        BoundHashOperations<String, Object, Object> cartCacheOps = getCartCacheOps();
        Object cartItemVOStr = cartCacheOps.get(skuId);

        return JSONUtil.toBean(cartItemVOStr.toString(), CartItemVo.class);
    }


    @Override
    public CartVo getCartInfo() {
        CartVo cartVo = new CartVo();

        UserInfoDTO userInfo = GulimallThreadContext.getUserInfo();

        BoundHashOperations<String, Object, Object> cartCacheOpsByKey = getCartCacheOpsByKey(CartCacheConstant.USER_CART_SKU_INFO + userInfo.getUserKey());
        List<CartItemVo> tempCartItemVo = cartCacheOpsByKey.values().stream().map(item -> {
                    return JSONUtil.toBean(item.toString(), CartItemVo.class);
                }).collect(Collectors.toList());

        if (Objects.isNull(userInfo.getUserId())) {
            // 未登录
           cartVo.setItems(tempCartItemVo);
        } else {
            // 已登录
            for (CartItemVo cartItemVo : tempCartItemVo) {
                addToCart(cartItemVo.getSkuId(), cartItemVo.getCount());
            }

            // 清除临时购物车数据
            redisTemplate.delete(CartCacheConstant.USER_CART_SKU_INFO + userInfo.getUserKey());

            cartVo.setItems(getCartCacheOpsByKey(
                    CartCacheConstant.USER_CART_SKU_INFO + userInfo.getUserId()).values().stream().map(item -> {
                return JSONUtil.toBean(item.toString(), CartItemVo.class);
            }).collect(Collectors.toList()));

        }

        return cartVo;
    }

    @Override
    public void checkItem(Long skuId, Integer checked) {
        BoundHashOperations<String, Object, Object> cartCacheOps = getCartCacheOps();

        Object o = cartCacheOps.get(skuId);
        if (Objects.isNull(o)) {
            // 出错了。购物车中不存在
            return;
        }

        CartItemVo cartItemVo = JSONUtil.toBean(o.toString(), CartItemVo.class);
        if (checked == 1) {
            cartItemVo.setCheck(true);
        } else {
            cartItemVo.setCheck(false);
        }

        getCartCacheOps().put(skuId, JSONUtil.toJsonStr(cartItemVo));
    }

    @Override
    public void countItem(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartCacheOps = getCartCacheOps();

        Object o = cartCacheOps.get(skuId);
        if (Objects.isNull(o)) {
            // 出错了。购物车中不存在
            return;
        }

        CartItemVo cartItemVo = JSONUtil.toBean(o.toString(), CartItemVo.class);
        cartItemVo.setCount(num);

        getCartCacheOps().put(skuId, JSONUtil.toJsonStr(cartItemVo));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartCacheOps = getCartCacheOps();
        cartCacheOps.delete(skuId);

        // 对于真实项目：需要考虑的点
        // 1、校验参数 2、验证权限 3、考虑这个删除是否设计其他的更新操作，若需要就需要加上事务（若是分布式事务又需要考虑其他情况） 4、若删除的这个查询是加了缓存的，也需要删除缓存
    }


    @Override
    public List<CartItemVo> getCartItems() {

        return getCartCacheOps().values().stream().map(
                item -> JSONUtil.toBean(item.toString(), CartItemVo.class)
        ).filter(CartItemVo::getCheck).collect(Collectors.toList());
    }
}
