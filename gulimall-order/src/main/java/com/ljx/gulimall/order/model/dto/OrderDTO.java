package com.ljx.gulimall.order.model.dto;

import com.ljx.gulimall.order.model.entity.OrderEntity;
import com.ljx.gulimall.order.model.entity.OrderItemEntity;
import lombok.Data;

import java.util.List;

@Data
public class OrderDTO {

    private OrderEntity orderEntity;

    private List<OrderItemEntity> orderItems;


}
