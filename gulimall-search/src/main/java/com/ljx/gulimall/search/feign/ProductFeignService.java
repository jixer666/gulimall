package com.ljx.gulimall.search.feign;

import com.ljx.common.utils.R;
import com.ljx.gulimall.search.model.vo.AttrEntityVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-product")
public interface ProductFeignService {

    @GetMapping("/product/attr/info/{attrId}")
    public R<AttrEntityVO> getAttrInfo(@PathVariable("attrId") Long attrId);

}
