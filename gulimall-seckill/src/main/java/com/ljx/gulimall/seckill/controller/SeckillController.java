package com.ljx.gulimall.seckill.controller;

import com.ljx.common.utils.R;
import com.ljx.gulimall.seckill.model.dto.SeckillSessionSkuDTO;
import com.ljx.gulimall.seckill.model.vo.SeckillSkuVo;
import com.ljx.gulimall.seckill.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @ResponseBody
    @GetMapping("/getCurrentSeckillSkus")
    public R getCurrentSeckillSkus() {
        List<SeckillSessionSkuDTO> result = seckillService.getCurrentSeckillSkus();

        return R.ok().setData(result);
    }

    @ResponseBody
    @GetMapping(value = "/sku/seckill/{skuId}")
    public R<SeckillSkuVo> getSkuSeckillInfo(@PathVariable("skuId") Long skuId) {

        SeckillSkuVo to = seckillService.getSkuSeckillInfo(skuId);

        return R.ok().setData(to);
    }

    @ResponseBody
    @GetMapping(value = "/kill")
    public String kill(@RequestParam("skillId") Long skillId,
                       @RequestParam("key") Long key,
                       @RequestParam("code") Long code) {
        // 因此处的秒杀做的太粗糙了，就不做了，还是不如黑马点评，唯一收获就放入消息队列前的操作前是在redis中进行的，响应很快
        // 用信号量缓存库存这个操作还需探究

        return "success";
    }

}
