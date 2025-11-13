package com.ljx.gulimall.order.web;

import com.ljx.gulimall.order.model.vo.OrderSubmitVO;
import com.ljx.gulimall.order.model.vo.OrderConfirmVO;
import com.ljx.gulimall.order.model.vo.OrderSubmitDTO;
import com.ljx.gulimall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model){
        OrderConfirmVO orderConfirmVO = orderService.getOrderConfirm();
        model.addAttribute("confirmOrderData", orderConfirmVO);

        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitDTO orderSubmitDTO, Model model, RedirectAttributes attributes){
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(orderSubmitDTO);
        if (orderSubmitVO.getCode() == 0) {
            model.addAttribute("submitOrderResp", orderSubmitVO);
            return "pay";
        } else {
            String msg = "下单失败";
            switch (orderSubmitVO.getCode()) {
                case 1: msg += "令牌订单信息过期，请刷新再次提交"; break;
                case 2: msg += "订单商品价格发生变化，请确认后再次提交"; break;
                case 3: msg += "库存锁定失败，商品库存不足"; break;
            }
            attributes.addFlashAttribute("msg", msg);
            return "redirect:http://order.gulimall.com/toTrade";

        }
    }


}
