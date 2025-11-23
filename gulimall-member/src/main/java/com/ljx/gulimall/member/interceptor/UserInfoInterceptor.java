package com.ljx.gulimall.member.interceptor;

import com.ljx.common.constant.AuthConstant;
import com.ljx.common.context.GulimallThreadContext;
import com.ljx.common.domain.dto.UserInfoDTO;
import com.ljx.common.domain.vo.MemberVO;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class UserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (new AntPathMatcher().match("/member/member/login/github", request.getRequestURI()) ||
                new AntPathMatcher().match("/memberOrder.html", request.getRequestURI()) ||
                new AntPathMatcher().match("/member/memberreceiveaddress/info/**", request.getRequestURI())) {
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
