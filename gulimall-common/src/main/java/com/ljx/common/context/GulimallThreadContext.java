package com.ljx.common.context;

import com.ljx.common.domain.dto.UserInfoDTO;

public class GulimallThreadContext {

    private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static UserInfoDTO getUserInfo(){
        return USER_THREAD_LOCAL.get();
    }

    public static void setUserInfo(UserInfoDTO userInfoDTO){
        USER_THREAD_LOCAL.set(userInfoDTO);
    }

}
