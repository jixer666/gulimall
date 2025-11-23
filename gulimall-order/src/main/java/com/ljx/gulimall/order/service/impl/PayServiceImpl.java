package com.ljx.gulimall.order.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ljx.gulimall.order.config.AlipayConfig;
import com.ljx.gulimall.order.model.entity.OrderEntity;
import com.ljx.gulimall.order.model.entity.OrderItemEntity;
import com.ljx.gulimall.order.model.entity.PaymentInfoEntity;
import com.ljx.gulimall.order.model.enums.OrderStatusEnum;
import com.ljx.gulimall.order.model.vo.PayAsyncVo;
import com.ljx.gulimall.order.model.vo.PayVo;
import com.ljx.gulimall.order.service.OrderItemService;
import com.ljx.gulimall.order.service.OrderService;
import com.ljx.gulimall.order.service.PayService;
import com.ljx.gulimall.order.service.PaymentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private AlipayConfig alipayConfig;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity orderInfo = orderService.getByOrderSn(orderSn);

        // 保留两位小数点，向上取值
        BigDecimal payAmount = orderInfo.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(payAmount.toString());
        payVo.setOut_trade_no(orderInfo.getOrderSn());

        //查询订单项的数据
        List<OrderItemEntity> orderItemInfo = orderItemService.list(
                new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity orderItemEntity = orderItemInfo.get(0);
        payVo.setBody(orderItemEntity.getSkuAttrsVals());
        payVo.setSubject(orderItemEntity.getSkuName());

        return payVo;

    }

    @Override
    public Boolean rsaCheck(HttpServletRequest request) throws AlipayApiException {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        return AlipaySignature.rsaCheckV1(params, alipayConfig.getAlipay_public_key(),
                alipayConfig.getCharset(), alipayConfig.getSign_type()); //调用SDK验证签名
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String payCallback(PayAsyncVo payAsyncVo) {
        try {
            // 保存支付记录
            PaymentInfoEntity paymentInfo = buildPauymentInfo(payAsyncVo);
            paymentInfoService.save(paymentInfo);

            // 更新订单状态
            orderService.update(new LambdaUpdateWrapper<OrderEntity>()
                    .eq(OrderEntity::getOrderSn, payAsyncVo.getOut_trade_no())
                    .eq(OrderEntity::getStatus, OrderStatusEnum.CREATE_NEW.getCode())
                    .set(OrderEntity::getStatus, OrderStatusEnum.PAYED.getCode())
            );
            return "success";
        } catch (Exception e) {
            log.error("回调出错：{}", e.getMessage(), e);
            return "error";
        }
    }

    private static PaymentInfoEntity buildPauymentInfo(PayAsyncVo payAsyncVo) {
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setOrderSn(payAsyncVo.getOut_trade_no());
        paymentInfo.setAlipayTradeNo(payAsyncVo.getTrade_no());
        paymentInfo.setTotalAmount(new BigDecimal(payAsyncVo.getBuyer_pay_amount()));
        paymentInfo.setSubject(payAsyncVo.getBody());
        paymentInfo.setPaymentStatus(payAsyncVo.getTrade_status());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setCallbackTime(DateUtil.parse(payAsyncVo.getNotify_time()));
        return paymentInfo;
    }
}
