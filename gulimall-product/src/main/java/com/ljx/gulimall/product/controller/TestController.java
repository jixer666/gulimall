package com.ljx.gulimall.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-07-29  20:43
 */
@RestController
@RequestMapping("product/test")
public class TestController {

    @GetMapping("/t1")
    public String test1() {
        return "success";
    }
}
