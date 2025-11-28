package com.ljx.gulimall.product.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillSkuVo {

    private Long skuId;

    private Long startTime;

    private Long endTime;

    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private Integer seckillCount;
    /**
     * 每人限购数量
     */
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

}
