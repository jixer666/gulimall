package com.ljx.gulimall.order.service.impl;

import com.ljx.common.context.GulimallThreadContext;
import com.ljx.common.domain.dto.UserInfoDTO;
import com.ljx.common.exception.RRException;
import com.ljx.common.utils.R;
import com.ljx.gulimall.order.constant.OrderConstants;
import com.ljx.gulimall.order.feign.CartServiceFeign;
import com.ljx.gulimall.order.feign.MemberServiceFeign;
import com.ljx.gulimall.order.feign.WareServiceFeign;
import com.ljx.gulimall.order.model.dto.OrderSubmitDTO;
import com.ljx.gulimall.order.model.entity.MemberEntity;
import com.ljx.gulimall.order.model.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.order.dao.OrderDao;
import com.ljx.gulimall.order.model.entity.OrderEntity;
import com.ljx.gulimall.order.service.OrderService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    private MemberServiceFeign memberServiceFeign;

    @Autowired
    private CartServiceFeign cartServiceFeign;

    @Autowired
    private WareServiceFeign wareServiceFeign;

    @Autowired
    private ThreadPoolExecutor ThreadPoolExecutor;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVO getOrderConfirm() {
        OrderConfirmVO orderConfirmVO = new OrderConfirmVO();

        UserInfoDTO userInfo = GulimallThreadContext.getUserInfo();
        RequestAttributes currentRequestAttributes = RequestContextHolder.getRequestAttributes();

        try {
            // 会员收货地址
            CompletableFuture<Void> orderFuture = CompletableFuture.runAsync(() -> {
                // 每个线程值都需要重新设置，防止请求头为空，因为RequestContextHolder本质上是一个ThreadLocal
                RequestContextHolder.setRequestAttributes(currentRequestAttributes);
                R<List<MemberReceiveAddressVO>> memberAddressResult = memberServiceFeign.listByMemberId(userInfo.getUserId());
                if (memberAddressResult.getCode() == 0) {
                    orderConfirmVO.setMemberAddressVos(memberAddressResult.getDataList(MemberReceiveAddressVO.class));
                }
            }, ThreadPoolExecutor);

            // 订单商品列表
            CompletableFuture<Void> productFuture = CompletableFuture.runAsync(() -> {
                RequestContextHolder.setRequestAttributes(currentRequestAttributes);
                R<List<CartItemVo>> cartItemsResult = cartServiceFeign.getCartItems();
                if (cartItemsResult.getCode() == 0) {
                    orderConfirmVO.setItems(cartItemsResult.getDataList(CartItemVo.class));
                }
            }, ThreadPoolExecutor).thenRunAsync(() -> {
                List<Long> skuIds = orderConfirmVO.getItems().stream().map(CartItemVo::getSkuId).collect(Collectors.toList());
                R<List<SkuHasStockVo>> hashStockResult = wareServiceFeign.getHashStock(skuIds);
                if (hashStockResult.getCode() == 0) {
                    Map<Long, Boolean> hashStockMap = hashStockResult.getDataList(SkuHasStockVo.class)
                            .stream()
                            .collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHashStock));
                    orderConfirmVO.setStocks(hashStockMap);
                }
            });

            // 用户积分，因偷懒就没有将用户信息存入userInfo
            CompletableFuture<Void> memberFuture = CompletableFuture.runAsync(() -> {
                RequestContextHolder.setRequestAttributes(currentRequestAttributes);
                R<MemberEntity> memberInfoResult = memberServiceFeign.getMemberInfo(userInfo.getUserId());
                if (memberInfoResult.getCode() == 0) {
                    orderConfirmVO.setIntegration(memberInfoResult.getDataObj(MemberEntity.class).getIntegration());
                }
            }, ThreadPoolExecutor);

            // 防重token
            saveOrderToken(orderConfirmVO);

            CompletableFuture.allOf(orderFuture, productFuture, memberFuture).get();
        } catch (Exception e) {
            log.error("远程调用异常：{}", e.getMessage(), e);
            throw new RRException("远程调用异常");
        }

        return orderConfirmVO;
    }

    private void saveOrderToken(OrderConfirmVO orderConfirmVO) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(OrderConstants.ORDER_TOKEN + uuid, "", 30, TimeUnit.MINUTES);
        orderConfirmVO.setOrderToken(uuid);
    }

    @Override
    public OrderSubmitDTO submitOrder(OrderSubmitVO orderSubmitVO) {
        // 校验参数直接跳过

        OrderSubmitDTO  orderSubmitDTO = new OrderSubmitDTO();
        orderSubmitDTO.setCode(0);

        // todo 代做

        UserInfoDTO userInfo = GulimallThreadContext.getUserInfo();


        return orderSubmitDTO;
    }
}