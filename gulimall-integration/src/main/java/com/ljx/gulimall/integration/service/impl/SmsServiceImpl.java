package com.ljx.gulimall.integration.service.impl;

import com.ljx.gulimall.integration.service.SmsService;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Override
    public void sendSms(String phone, String code) {
        System.out.printf("手机【%s】发送验证码【%s】%n", phone, code);
    }
}
