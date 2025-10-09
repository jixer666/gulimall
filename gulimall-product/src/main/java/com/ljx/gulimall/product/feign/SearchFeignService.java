package com.ljx.gulimall.product.feign;

import com.ljx.common.to.es.SkuEsModel;
import com.ljx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-07-22  20:55
 */
@FeignClient("gulimall-search")
public interface SearchFeignService {

    @PostMapping("/search/save/product")
    public R produceStatusUp(@RequestBody List<SkuEsModel> skuEsModels);

}
