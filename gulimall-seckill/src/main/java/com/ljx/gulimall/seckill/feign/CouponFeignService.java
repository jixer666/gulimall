package com.ljx.gulimall.seckill.feign;

import com.ljx.common.utils.R;
import com.ljx.gulimall.seckill.model.dto.SeckillSessionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-11-24  20:57
 */
@FeignClient("gulimall-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/seckillsession/getLast3DayData")
    R<List<SeckillSessionDTO>> getLast3DayData();

}
