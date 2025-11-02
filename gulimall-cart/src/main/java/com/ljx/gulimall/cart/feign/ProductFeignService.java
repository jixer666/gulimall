package com.ljx.gulimall.cart.feign;

import com.ljx.common.utils.R;
import com.ljx.gulimall.cart.domain.entity.SkuInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuinfo/info/{skuId}")
    R<SkuInfoEntity> getSkuInfo( @PathVariable("skuId") Long skuId);

    @GetMapping("product/skusaleattrvalue/list/{skuId}")
    R<List<String>> getListBySkuId(@PathVariable("skuId") Long skuId);

}
