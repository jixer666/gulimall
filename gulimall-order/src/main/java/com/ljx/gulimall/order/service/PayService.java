package com.ljx.gulimall.order.service;

import com.alipay.api.AlipayApiException;
import com.ljx.gulimall.order.model.vo.PayAsyncVo;
import com.ljx.gulimall.order.model.vo.PayVo;

import javax.servlet.http.HttpServletRequest;

public interface PayService {
    PayVo getOrderPay(String orderSn);

    String payCallback(PayAsyncVo payAsyncVo);

    Boolean rsaCheck(HttpServletRequest request) throws AlipayApiException;
}
