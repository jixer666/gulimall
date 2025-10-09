package com.ljx.gulimall.product.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PublishStatusEnum {

    NEW_SPU(0, "新建"),
    SPU_UP(1, "商品上架"),
    SPU_DOWN(2, "商品下架");
    private Integer key;
    private String value;

}
