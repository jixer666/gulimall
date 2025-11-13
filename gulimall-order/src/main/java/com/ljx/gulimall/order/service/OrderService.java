package com.ljx.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.order.model.vo.OrderSubmitVO;
import com.ljx.gulimall.order.model.entity.OrderEntity;
import com.ljx.gulimall.order.model.vo.OrderConfirmVO;
import com.ljx.gulimall.order.model.vo.OrderSubmitDTO;

import java.util.Map;

/**
 * 订单
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 22:04:49
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVO getOrderConfirm();

    OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO);
}

