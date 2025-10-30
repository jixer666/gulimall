package com.ljx.auth.controller;

import com.ljx.auth.domain.vo.LoginVO;
import com.ljx.auth.domain.vo.RegisterVO;
import com.ljx.auth.service.LoginService;
import com.ljx.common.utils.R;
import feign.Param;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/sms/sendCode")
    public R<Void> sendSms(@Param("phone") String phone) {
        loginService.sendSms(phone);

        return R.ok();
    }

    @PostMapping("/register")
//    public String register(@Valid RegisterVO registerVO, BindingResult bindingResult, Model model) {
    public String register(@Valid RegisterVO registerVO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Map<String, String> map = new HashMap<>();
            bindingResult.getFieldErrors().stream().forEach(item -> {
                map.put(item.getField(), item.getDefaultMessage());
            });
//            model.addAttribute("errors", map);
            redirectAttributes.addFlashAttribute("errors", map);

            // 不能用forward:/reg.html，请求->不能用forward（GulimallWebConfig配置了/reg为页面渲染）

            // 重定向不会带上数据，需要用redirectAttributes，原理就是将数据暂存到session，然后302重定向后重新调用请求从session获取数据
            // model会失效的原因是302重定向后重新调用请求后数据会丢失
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        loginService.register(registerVO);

        return "redirect:http://auth.gulimall.com/login.html";
    }

    @PostMapping("/login")
    public String login(@Valid LoginVO loginVO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            Map<String, String> map = new HashMap<>();
            bindingResult.getFieldErrors().stream().forEach(item -> {
                map.put(item.getField(), item.getDefaultMessage());
            });
            redirectAttributes.addFlashAttribute("errors", map);


            return "redirect:http://auth.gulimall.com/login.html";
        }

        R result = loginService.login(loginVO);
        String errorMsg = result.getErrorMsg();
        if (result.getCode() != 0) {
            Map<String, String> map = new HashMap<>();
            map.put("msg", errorMsg);
            redirectAttributes.addFlashAttribute("errors", map);

            return "redirect:http://auth.gulimall.com/login.html";
        }

        return "redirect:http://gulimall.com";
    }
}
