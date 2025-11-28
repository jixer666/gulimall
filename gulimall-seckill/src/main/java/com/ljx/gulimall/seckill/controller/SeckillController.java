package com.ljx.gulimall.seckill.controller;

import com.ljx.common.utils.R;
import com.ljx.gulimall.seckill.model.dto.SeckillSessionSkuDTO;
import com.ljx.gulimall.seckill.model.vo.SeckillSkuVo;
import com.ljx.gulimall.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @GetMapping("/getCurrentSeckillSkus")
    public R getCurrentSeckillSkus() {
        List<SeckillSessionSkuDTO> result = seckillService.getCurrentSeckillSkus();

        return R.ok().setData(result);
    }

    @GetMapping(value = "/sku/seckill/{skuId}")
    public R<SeckillSkuVo> getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {

        SeckillSkuVo to = seckillService.getSkuSeckillInfo(skuId);

        return R.ok().setData(to);
    }

}
