package com.ljx.gulimall.integration.controller;

import com.ljx.common.utils.R;
import com.ljx.gulimall.integration.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/sendCode")
    public R<Void> sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        smsService.sendSms(phone, code);

        return R.ok();
    }

}
