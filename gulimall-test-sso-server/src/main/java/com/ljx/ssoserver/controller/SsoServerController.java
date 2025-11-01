package com.ljx.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class SsoServerController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/login.html")
    public String login(Model model,
                        @RequestParam("redirect_url") String redirectUrl,
                        @CookieValue(value = "sso_token", required = false) String ssoToken) {
        if (!StringUtils.isEmpty(ssoToken)) {
            return "redirect:" + redirectUrl + "?sso_token=" + ssoToken;
        }

        model.addAttribute("redirect_url", redirectUrl);

        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("redirect_url") String redirectUrl,
                          HttpServletResponse response) {
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            String ssoToken = UUID.randomUUID().toString().replace("_", "");
            stringRedisTemplate.opsForValue().set(ssoToken, username, 5, TimeUnit.MINUTES);
            Cookie cookie = new Cookie("sso_token", ssoToken);
            response.addCookie(cookie);

            return "redirect:" + redirectUrl + "?sso_token=" + ssoToken;
        }

        return "login";
    }

    @ResponseBody
    @GetMapping("/userInfo")
    public String getUserInfo(@RequestParam("sso_token") String ssoToken) {
        return stringRedisTemplate.opsForValue().get(ssoToken);
    }
}
