package com.ljx.ssoclient2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class SsoClientController {


    @GetMapping("/employees")
    public String employees(HttpSession session, HttpServletRequest request, Model model,
                            @RequestParam(value = "sso_token", required = false) String ssoToken) {
        if (!StringUtils.isEmpty(ssoToken)) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity("http://testssoserver.com:8080/userInfo?sso_token=" + ssoToken, String.class);
            String body = response.getBody();

            session.setAttribute("user", body);
        }

        Object user = session.getAttribute("user");
        if (Objects.isNull(user)){
            return "redirect:http://testssoserver.com:8080/login.html?redirect_url=http://testssoclient2.com:8082" + request.getRequestURI();
        }

        // 模拟数据
        List<String> emps = new ArrayList<>();
        emps.add("张三");
        emps.add("李四");
        model.addAttribute("emps", emps);

        return "employees";
    }
}
