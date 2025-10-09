package com.ljx.gulimall.search.controller;

import com.ljx.common.to.es.SkuEsModel;
import com.ljx.common.utils.R;
import com.ljx.gulimall.search.service.ProductSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-07-23  20:43
 */
@RestController
@RequestMapping("/search/save")
public class ElasticSaveController {

    @Autowired
    private ProductSaveService productSaveService;

    @PostMapping("/product")
    public R<Boolean> produceStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        Boolean result;
        try {
            result = productSaveService.produceStatusUp(skuEsModels);
        } catch (Exception e) {
            return R.error().setData(false);
        }
        if (!result) {
            return R.error().setData(false);
        }
        return R.ok().setData(true);
    }


}
