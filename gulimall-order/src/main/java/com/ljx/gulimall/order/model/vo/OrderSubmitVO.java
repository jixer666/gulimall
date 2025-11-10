package com.ljx.gulimall.order.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVO {

    /** 地址 **/
    private Long addrId;

    /** 支付方式 **/
    private Integer payType;

    private String orderToken;

    /** 应付价格 **/
    private BigDecimal payPrice;

    /** 订单备注 **/
    private String remarks;




}
