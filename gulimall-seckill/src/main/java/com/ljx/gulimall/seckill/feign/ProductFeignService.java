package com.ljx.gulimall.seckill.feign;

import com.ljx.common.utils.R;
import com.ljx.gulimall.seckill.model.dto.SeckillSessionDTO;
import com.ljx.gulimall.seckill.model.entity.SkuInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-11-24  20:57
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    @PostMapping("/product/skuinfo/infos")
    R<List<SkuInfoEntity>> getInfoBySkuIds(@RequestBody List<Long> skuIds);

}
