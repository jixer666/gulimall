package com.ljx.gulimall.seckill.service;

import com.ljx.gulimall.seckill.model.dto.SeckillSessionSkuDTO;
import com.ljx.gulimall.seckill.model.vo.SeckillSkuVo;

import java.util.List;

public interface SeckillService {
    void preLoadSeckillData();

    List<SeckillSessionSkuDTO>  getCurrentSeckillSkus();

    SeckillSkuVo getSkuSeckillInfo(Long skuId);
}
