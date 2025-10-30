package com.ljx.auth.domain.vo;

import com.ljx.common.utils.AssertUtil;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class RegisterVO {

    @NotEmpty(message = "用户名不能为空")
    @Length(min = 6, max = 19, message="用户名长度在6-18字符")
    private String userName;

    @NotEmpty(message = "密码必须填写")
    @Length(min = 6,max = 18,message = "密码必须是6—18位字符")
    private String password;

    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[1]([3-9])[0-9]{9}$", message = "手机号格式不正确")
    private String phone;

    @NotEmpty(message = "验证码不能为空")
    private String code;

    public void checkParams() {
        AssertUtil.isNotEmpty(this, "参数不能为空");
        AssertUtil.isNotEmpty(userName, "账号不能为空");
        AssertUtil.isNotEmpty(password, "密码不能为空");
        AssertUtil.isNotEmpty(phone, "手机号不能为空");
        AssertUtil.isNotEmpty(code, "验证码不能为空");
    }
}
