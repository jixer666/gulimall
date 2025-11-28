package com.ljx.gulimall.product.feign;

import com.ljx.common.utils.R;
import com.ljx.gulimall.product.model.vo.SeckillSkuVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-11-28  20:55
 */
@FeignClient("gulimall-seckill")
public interface SeckillFeignService {

    @GetMapping(value = "/sku/seckill/{skuId}")
    R<SeckillSkuVo> getSkuSeckillInfo(@PathVariable("skuId") Long skuId);

}
