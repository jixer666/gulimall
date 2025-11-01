package com.ljx.auth.service;

import com.ljx.auth.domain.vo.LoginVO;
import com.ljx.auth.domain.vo.RegisterVO;
import com.ljx.common.utils.R;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

public interface LoginService {
    void sendSms(String phone);

    void register(RegisterVO registerVO);

    R login(@Valid LoginVO loginVO, HttpSession session);

    String githubCallback(String code, String state, HttpSession session);
}
