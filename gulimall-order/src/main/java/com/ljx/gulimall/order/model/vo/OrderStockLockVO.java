package com.ljx.gulimall.order.model.vo;

import lombok.Data;

@Data
public class OrderStockLockVO {

    private Long skuId;

    private Integer count;

    private Boolean isLocked;

}
