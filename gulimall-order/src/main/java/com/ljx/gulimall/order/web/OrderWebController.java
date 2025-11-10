package com.ljx.gulimall.order.web;

import com.ljx.gulimall.order.model.dto.OrderSubmitDTO;
import com.ljx.gulimall.order.model.vo.OrderConfirmVO;
import com.ljx.gulimall.order.model.vo.OrderSubmitVO;
import com.ljx.gulimall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/submitOrder")
    public String submitOrder(OrderSubmitVO orderSubmitVO){
        OrderSubmitDTO orderSubmitDTO = orderService.submitOrder(orderSubmitVO);
        if (orderSubmitDTO.getCode() == 0) {

        } else {
            return "redirect:http:order.gulimall.com";
        }

        return "confirm";
    }


}
