package com.ljx.gulimall.order.interceptor;

import com.ljx.common.constant.AuthConstant;
import com.ljx.common.context.GulimallThreadContext;
import com.ljx.common.domain.dto.UserInfoDTO;
import com.ljx.common.domain.vo.MemberVO;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
import java.util.UUID;

@Component
public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (new AntPathMatcher().match("/order/order/getByOrderSn/**", request.getRequestURI()) ||
                new AntPathMatcher().match("/aliPayOrder", request.getRequestURI()) ||
                new AntPathMatcher().match("/pay/callback", request.getRequestURI()) ||
                new AntPathMatcher().match("/test/**", request.getRequestURI())) {
            return true;
        }

        UserInfoDTO userInfoDTO = new UserInfoDTO();

        MemberVO memberVO = (MemberVO) request.getSession().getAttribute(AuthConstant.LOGIN_USER);
        if (Objects.isNull(memberVO)) {
            request.getSession().setAttribute(AuthConstant.LOGIN_USER_ERROR_MSG, "请先登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }
        userInfoDTO.setUserId(memberVO.getId());

        GulimallThreadContext.setUserInfo(userInfoDTO);

        return true;
    }

}
