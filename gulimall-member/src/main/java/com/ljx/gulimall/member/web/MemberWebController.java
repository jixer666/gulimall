package com.ljx.gulimall.member.web;

import com.ljx.common.utils.R;
import com.ljx.gulimall.member.feign.OrderServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MemberWebController {

    @Autowired
    private OrderServiceFeign orderServiceFeign;

    @GetMapping(value = "/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum",required = false,defaultValue = "0") Integer pageNum,
                                  Model model, HttpServletRequest request) {
        Map<String,Object> page = new HashMap<>();
        page.put("page", pageNum.toString());

        R result = orderServiceFeign.listWithItem(page);
        model.addAttribute("orders", result.get("data"));

        return "orderList";
    }

}