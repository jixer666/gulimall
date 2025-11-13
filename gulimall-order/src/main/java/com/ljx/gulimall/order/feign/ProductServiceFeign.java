package com.ljx.gulimall.order.feign;

import com.ljx.common.utils.R;
import com.ljx.gulimall.order.model.entity.SpuInfoEntity;
import com.ljx.gulimall.order.model.vo.CartItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("gulimall-product")
public interface ProductServiceFeign {

    @GetMapping("/product/spuinfo/skuId/{skuId}")
    R<SpuInfoEntity> getBySkuId(@PathVariable("skuId") Long skuId);

}
