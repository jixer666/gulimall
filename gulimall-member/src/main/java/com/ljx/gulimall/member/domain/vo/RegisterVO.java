package com.ljx.gulimall.member.domain.vo;

import com.ljx.common.utils.AssertUtil;
import lombok.Data;

@Data
public class RegisterVO {

    private String userName;

    private String password;

    private String phone;

    public void checkParams() {
        AssertUtil.isNotEmpty(this, "参数不能为空");
        AssertUtil.isNotEmpty(userName, "账号不能为空");
        AssertUtil.isNotEmpty(password, "密码不能为空");
        AssertUtil.isNotEmpty(phone, "手机号不能为空");
    }
}