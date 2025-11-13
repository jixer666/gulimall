package com.ljx.gulimall.order.model.vo;

import com.ljx.gulimall.order.model.entity.OrderEntity;
import lombok.Data;

@Data
public class OrderSubmitVO {

    private Integer code;

    private OrderEntity order;
}
