package com.ljx.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.ljx.gulimall.order.config.AlipayConfig;
import com.ljx.gulimall.order.model.vo.PayAsyncVo;
import com.ljx.gulimall.order.model.vo.PayVo;
import com.ljx.gulimall.order.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PayWebController {

    @Autowired
    private PayService payService;

    @Autowired
    private AlipayConfig alipayConfig;

    @ResponseBody
    @GetMapping(value = "/aliPayOrder", produces = "text/html")
    public String aliPayOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo payVo = payService.getOrderPay(orderSn);
        String pay = alipayConfig.pay(payVo);
        System.out.println(pay);
        return pay;
    }

    @ResponseBody
    @PostMapping("/pay/callback")
    public String payCallback(PayAsyncVo payAsyncVo,
                              HttpServletRequest request) throws AlipayApiException {
        System.out.println("支付回调");
        // 验签
        Boolean checkResult = payService.rsaCheck(request);
        if (!checkResult) {
            System.out.println("验签失败");
            return "error";
        }
        System.out.println("验签成功");
        System.out.println("支付回调处理结束");

        return payService.payCallback(payAsyncVo);
    }


}
