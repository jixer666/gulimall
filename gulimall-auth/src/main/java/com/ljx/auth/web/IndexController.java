package com.ljx.auth.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {

    @GetMapping("/login.html")
    public String getLoginPage(Model model) {

        return "login";
    }

    @GetMapping("/reg.html")
    public String getRegisterPage(Model model) {

        return "reg";
    }




}
