package com.ljx.gulimall.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljx.common.context.GulimallThreadContext;
import com.ljx.common.domain.dto.UserInfoDTO;
import com.ljx.common.exception.RRException;
import com.ljx.common.utils.AssertUtil;
import com.ljx.common.utils.R;
import com.ljx.gulimall.order.constant.OrderConstants;
import com.ljx.gulimall.order.feign.CartServiceFeign;
import com.ljx.gulimall.order.feign.MemberServiceFeign;
import com.ljx.gulimall.order.feign.ProductServiceFeign;
import com.ljx.gulimall.order.feign.WareServiceFeign;
import com.ljx.gulimall.order.model.dto.OrderDTO;
import com.ljx.gulimall.order.model.dto.OrderStockLockDTO;
import com.ljx.gulimall.order.model.dto.OrderWithItemDTO;
import com.ljx.gulimall.order.model.vo.OrderSubmitVO;
import com.ljx.gulimall.order.model.entity.MemberEntity;
import com.ljx.gulimall.order.model.entity.OrderItemEntity;
import com.ljx.gulimall.order.model.entity.SpuInfoEntity;
import com.ljx.gulimall.order.model.enums.OrderStatusEnum;
import com.ljx.gulimall.order.model.vo.*;
import com.ljx.gulimall.order.service.OrderItemService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
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

    @Autowired
    private ProductServiceFeign productServiceFeign;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderService orderService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        Long userId = GulimallThreadContext.getUserInfo().getUserId();

        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",userId)
        );

        List<OrderWithItemDTO> orderWithItemDTOList = page.getRecords().stream().map(item -> {
            OrderWithItemDTO orderWithItemDTO = BeanUtil.copyProperties(item, OrderWithItemDTO.class);
            orderWithItemDTO.setItemEntities(orderItemService.list(
                    new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderSn, item.getOrderSn()))
            );
            return orderWithItemDTO;
        }).collect(Collectors.toList());

        return new PageUtils(orderWithItemDTOList, Integer.parseInt(String.valueOf(page.getTotal())),
                Integer.parseInt(String.valueOf(page.getPages())),
                Integer.parseInt(String.valueOf(page.getCurrent())));
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
        stringRedisTemplate.opsForValue().set(OrderConstants.ORDER_TOKEN + GulimallThreadContext.getUserInfo().getUserId(),
                uuid, 30, TimeUnit.MINUTES);
        orderConfirmVO.setOrderToken(uuid);
    }

    @Override
//    @GlobalTransactional // 在高并发模式下不适用，原因：加了很多锁导致请求串行执行
    public OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO) {
        // 校验参数直接跳过

        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setCode(0);

        UserInfoDTO userInfo = GulimallThreadContext.getUserInfo();

        // 查询和删除需要保证原子性
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(OrderConstants.ORDER_TOKEN + userInfo.getUserId()), orderSubmitDTO.getOrderToken());
        if (result == 0) {
            // 校验失败
            orderSubmitVO.setCode(1);
            return orderSubmitVO;
        }

        // 构建订单
        OrderDTO orderDTO = buildOrder(orderSubmitDTO);
        if (Math.abs(orderDTO.getOrderEntity().getPayAmount().subtract(orderSubmitDTO.getPayPrice()).doubleValue()) > 0.01) {
            orderSubmitVO.setCode(2);
            return orderSubmitVO;
        }
        orderSubmitVO.setOrder(orderDTO.getOrderEntity());

        List<OrderStockLockVO> orderStockLockVOS = orderDTO.getOrderItems().stream().map(item -> {
            OrderStockLockVO orderStockLockVO = new OrderStockLockVO();
            orderStockLockVO.setSkuId(item.getSkuId());
            orderStockLockVO.setCount(item.getSkuQuantity());
            return orderStockLockVO;
        }).collect(Collectors.toList());

        transactionTemplate.execute(res -> {
            // 保存订单
            saveOrder(orderDTO);

            // 锁定库存
            R<Boolean> lockOrderStockResult = wareServiceFeign.lockOrderStock(new OrderStockLockDTO(orderDTO.getOrderEntity().getOrderSn(), orderStockLockVOS));
            // int i = 1 / 0; // 模拟出错，测试事务回滚
            if (lockOrderStockResult.getCode() == 0) {
                // 发送延时队列，30分钟后自动到期
                rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", orderDTO.getOrderEntity().getOrderSn());
                return orderSubmitVO;
            } else {
                orderSubmitVO.setCode(3);
                return orderSubmitVO;
            }
        });


        return orderSubmitVO;
    }

    public void saveOrder(OrderDTO orderDTO) {
        save(orderDTO.getOrderEntity());
        orderItemService.saveBatch(orderDTO.getOrderItems());
    }

    private OrderDTO buildOrder(OrderSubmitDTO orderSubmitDTO) {
        OrderDTO orderDTO = new OrderDTO();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(IdUtil.fastUUID());
        orderEntity.setMemberId(GulimallThreadContext.getUserInfo().getUserId());

        // 构建运费信息
        buildFare(orderSubmitDTO, orderEntity);

        // 构建订单明细
        List<OrderItemEntity> orderItems = buildOrderItem(orderEntity.getOrderSn());

        // 验价
        calculatePrice(orderEntity, orderItems);

        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());

        orderDTO.setOrderEntity(orderEntity);
        orderDTO.setOrderItems(orderItems);

        return orderDTO;
    }

    private List<OrderItemEntity> buildOrderItem(String orderSn) {
        R<List<CartItemVo>> cartItemsResult = cartServiceFeign.getCartItems();
        if (cartItemsResult.getCode() == 0) {
            List<CartItemVo> cartItems = cartItemsResult.getDataList(CartItemVo.class);
            return cartItems.stream().map(item -> {
                OrderItemEntity orderItemEntity = new OrderItemEntity();
                orderItemEntity.setOrderSn(orderSn);

                // sku信息
                orderItemEntity.setSkuId(item.getSkuId());
                orderItemEntity.setSkuName(item.getTitle());
                orderItemEntity.setSkuPrice(item.getPrice());
                orderItemEntity.setSkuPic(item.getImage());
                orderItemEntity.setSkuAttrsVals(String.join(";", item.getSkuAttrValues()));
                orderItemEntity.setSkuQuantity(item.getCount());

                // spu信息
                R<SpuInfoEntity> spuInfoEntityR = productServiceFeign.getBySkuId(orderItemEntity.getSkuId());
                if (spuInfoEntityR.getCode() == 0) {
                    SpuInfoEntity spuInfoEntity = spuInfoEntityR.getDataObj(SpuInfoEntity.class);
                    orderItemEntity.setSpuId(spuInfoEntity.getId());
                    orderItemEntity.setSpuName(spuInfoEntity.getSpuName());
                    orderItemEntity.setSpuBrand(spuInfoEntity.getBrandId().toString());  // 偷懒了，不想再查数据库
                    orderItemEntity.setSpuPic(orderItemEntity.getSkuPic());
                }

                // 积分信息
                orderItemEntity.setGiftIntegration(item.getPrice().intValue() * item.getCount());
                orderItemEntity.setGiftGrowth(item.getPrice().intValue() * item.getCount());

                // 计算价格
                orderItemEntity.setPromotionAmount(new BigDecimal("0"));
                orderItemEntity.setCouponAmount(new BigDecimal("0"));
                orderItemEntity.setIntegrationAmount(new BigDecimal("0"));

                BigDecimal totalPrice = new BigDecimal(item.getPrice().toBigInteger()).multiply(new BigDecimal(item.getCount().toString()));
                orderItemEntity.setRealAmount(totalPrice.subtract(orderItemEntity.getPromotionAmount()
                        .subtract(orderItemEntity.getCouponAmount())
                        .subtract(orderItemEntity.getIntegrationAmount())));

                return orderItemEntity;
            }).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    private void buildFare(OrderSubmitDTO orderSubmitDTO, OrderEntity orderEntity) {
        R<AddressFareVO> addIdFareResult = wareServiceFeign.getAddIdFare(orderSubmitDTO.getAddrId());
        if (addIdFareResult.getCode() == 0) {
            AddressFareVO addressFareVO = addIdFareResult.getDataObj(AddressFareVO.class);
            orderEntity.setFreightAmount(addressFareVO.getFare());
            orderEntity.setReceiverName(addressFareVO.getAddress().getName());
            orderEntity.setReceiverPhone(addressFareVO.getAddress().getPhone());
            orderEntity.setReceiverPostCode(addressFareVO.getAddress().getPostCode());
            orderEntity.setReceiverProvince(addressFareVO.getAddress().getProvince());
            orderEntity.setReceiverCity(addressFareVO.getAddress().getCity());
            orderEntity.setReceiverRegion(addressFareVO.getAddress().getRegion());
        }
    }

    private void calculatePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        BigDecimal totalPrice = new BigDecimal("0");
        BigDecimal integration = new BigDecimal("0");
        BigDecimal promotion = new BigDecimal("0");
        BigDecimal coupon = new BigDecimal("0");
        Integer gift = 0;
        Integer giftGrowth = 0;


        for (OrderItemEntity orderItem : orderItems) {
            totalPrice = totalPrice.add(orderItem.getRealAmount());
            integration = integration.add(orderItem.getIntegrationAmount());
            promotion =  promotion.add(orderItem.getPromotionAmount());
            coupon =  coupon.add(orderItem.getCouponAmount());
            gift = gift + orderItem.getGiftGrowth();
            giftGrowth = giftGrowth +  orderItem.getGiftGrowth();
        }

        // 订单总额
        orderEntity.setTotalAmount(totalPrice);
        // 应付总额=订单金额+运费
        orderEntity.setPayAmount(totalPrice.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);
        // 积分
        orderEntity.setIntegration(gift);
        orderEntity.setGrowth(giftGrowth);

        orderEntity.setDeleteStatus(0);
    }

    private void deleteOrderToken() {
        stringRedisTemplate.delete(OrderConstants.ORDER_TOKEN + GulimallThreadContext.getUserInfo().getUserId());
    }

    private String getOrderToken() {
        return stringRedisTemplate.opsForValue().get(OrderConstants.ORDER_TOKEN + GulimallThreadContext.getUserInfo().getUserId());
    }

    @Override
    public OrderEntity getByOrderSn(String orderSn) {
        AssertUtil.isNotEmpty(orderSn, "订单号不能为空");

        return orderService.getOne(new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderSn, orderSn));
    }

    @Override
    public void closeOrder(String orderSn) {
        OrderEntity order = getByOrderSn(orderSn);
        if (!order.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            return;
        }
        order.setStatus(OrderStatusEnum.CANCLED.getCode());
        updateById(order);

        rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderSn);
    }


}