package com.ljx.gulimall.cart.interceptor;

import com.ljx.common.constant.AuthConstant;
import com.ljx.common.context.GulimallThreadContext;
import com.ljx.common.domain.vo.MemberVO;
import com.ljx.common.domain.dto.UserInfoDTO;
import com.ljx.gulimall.cart.constant.CartConstant;
import org.springframework.stereotype.Component;
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
        UserInfoDTO userInfoDTO = new UserInfoDTO();

        // 设置用户id
        MemberVO memberVO = (MemberVO) request.getSession().getAttribute(AuthConstant.LOGIN_USER);
        if (Objects.nonNull(memberVO)) {
            userInfoDTO.setUserId(memberVO.getId());
        }

        // 设置临时用户key
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(CartConstant.CART_COOKIE_NAME)) {
                userInfoDTO.setUserKey(cookie.getValue());
            }
        }

        if (StringUtils.isEmpty(userInfoDTO.getUserKey())) {
            // 若没有临时用户Key，就要先保存
            userInfoDTO.setUserKey(UUID.randomUUID().toString());
            userInfoDTO.setIsFirstSave(true);
        }

        GulimallThreadContext.setUserInfo(userInfoDTO);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoDTO userInfo = GulimallThreadContext.getUserInfo();
        if (userInfo.getIsFirstSave()) {
            Cookie cookie = new Cookie(CartConstant.CART_COOKIE_NAME, userInfo.getUserKey());
            cookie.setDomain(CartConstant.CART_COOKIE_DOMAIN);
            cookie.setMaxAge(CartConstant.CART_COOKIE_EXPIRE);
            response.addCookie(cookie);
        }
    }
}
