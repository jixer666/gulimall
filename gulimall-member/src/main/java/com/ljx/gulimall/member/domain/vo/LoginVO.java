package com.ljx.gulimall.member.domain.vo;

import com.ljx.common.utils.AssertUtil;
import lombok.Data;

@Data
public class LoginVO {

    private String userName;

    private String password;


    public void checkParams() {
        AssertUtil.isNotEmpty(this, "参数不能为空");
        AssertUtil.isNotEmpty(userName, "账号不能为空");
        AssertUtil.isNotEmpty(password, "密码不能为空");
    }
}