package com.ljx.gulimall.integration.controller;

import com.ljx.common.utils.R;
import com.ljx.gulimall.integration.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-06-18  21:58
 */
@RestController
@RequestMapping("integration/oss")
public class OssController {

    @Autowired
    private OssService ossService;

    @RequestMapping("/policy")
    public R policy() {
        Map<String, String> respMap = ossService.getPolicy();
        return R.ok().put("data",respMap);
    }

}