package com.ljx.gulimall.product.feign.callback;

import com.ljx.common.utils.R;
import com.ljx.gulimall.product.feign.SeckillFeignService;
import com.ljx.gulimall.product.model.vo.SeckillSkuVo;
import org.springframework.stereotype.Component;

@Component
public class SeckillCallbackHandler implements SeckillFeignService {
    @Override
    public R<SeckillSkuVo> getSkuSeckillInfo(Long skuId) {
        System.out.println("熔断方法调用");
        return R.error("触发熔断，秒杀服务远程调用出错");
    }
}
