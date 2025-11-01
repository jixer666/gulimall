package com.ljx.auth.controller;

import com.ljx.auth.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Autowired
    private LoginService loginService;

    @GetMapping("/github/callback")
    public String githubCallback(@RequestParam("code") String code, @RequestParam("state") String state, HttpSession session, HttpServletRequest request) {
        return loginService.githubCallback(code, state, session);
    }

}
