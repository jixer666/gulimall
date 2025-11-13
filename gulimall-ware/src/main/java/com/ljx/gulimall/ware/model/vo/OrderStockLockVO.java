package com.ljx.gulimall.ware.model.vo;

import lombok.Data;

@Data
public class OrderStockLockVO {

    private Long skuId;

    private Integer count;

    private Boolean isLocked;

}
