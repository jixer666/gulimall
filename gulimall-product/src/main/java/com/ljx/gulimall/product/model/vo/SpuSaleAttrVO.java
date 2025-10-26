package com.ljx.gulimall.product.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class SpuSaleAttrVO {
    private Long attrId;
    private String attrName;
    // 第一版本
    // private String attrValues;
    // 第二版本
    private List<SpuSaleAttrValueItemVO> attrValues;
}