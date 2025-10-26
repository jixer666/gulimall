package com.ljx.gulimall.product.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class SpuItemAttrGroupVO {
    private String groupName;
    private List<SpuBaseAttrVO> attrValues;
}