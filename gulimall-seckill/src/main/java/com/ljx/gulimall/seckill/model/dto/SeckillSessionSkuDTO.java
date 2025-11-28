package com.ljx.gulimall.seckill.model.dto;

import com.ljx.gulimall.seckill.model.entity.SeckillSkuRelationEntity;
import com.ljx.gulimall.seckill.model.entity.SkuInfoEntity;
import lombok.Data;

import java.util.Date;

@Data
public class SeckillSessionSkuDTO {

    private SeckillSkuRelationEntity seckillSessionSkuInfo;

    private SkuInfoEntity skuInfo;

    // 商品的随机码（防止恶意攻击）
    private String token;

    private Long startTime;

    private Long endTime;
}
