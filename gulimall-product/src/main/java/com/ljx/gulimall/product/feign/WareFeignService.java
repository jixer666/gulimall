package com.ljx.gulimall.product.feign;

import com.ljx.common.utils.R;
import com.ljx.gulimall.product.model.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-07-22  20:55
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {


    @PostMapping("/ware/waresku/hashstock")
    public R<List<SkuHasStockVo>> getHashStock(@RequestBody List<Long> skuIds);
}
