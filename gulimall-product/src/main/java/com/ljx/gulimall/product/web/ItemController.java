package com.ljx.gulimall.product.web;

import com.ljx.gulimall.product.model.vo.SkuVo;
import com.ljx.gulimall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String getItemPage(@PathVariable Long skuId, Model model) {
        SkuVo skuVo = skuInfoService.getSkuItemBySkuId(skuId);
        model.addAttribute("item", skuVo);

        return "item";
    }

}
