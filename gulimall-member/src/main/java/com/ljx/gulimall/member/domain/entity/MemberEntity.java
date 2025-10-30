package com.ljx.gulimall.member.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.ljx.gulimall.member.domain.vo.GithubUserVO;
import com.ljx.gulimall.member.domain.vo.RegisterVO;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 会员
 * 
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:52:55
 */
@Data
@TableName("ums_member")
public class MemberEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * 会员等级id
	 */
	private Long levelId;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 昵称
	 */
	private String nickname;
	/**
	 * 手机号码
	 */
	private String mobile;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 头像
	 */
	private String header;
	/**
	 * 性别
	 */
	private Integer gender;
	/**
	 * 生日
	 */
	private Date birth;
	/**
	 * 所在城市
	 */
	private String city;
	/**
	 * 职业
	 */
	private String job;
	/**
	 * 个性签名
	 */
	private String sign;
	/**
	 * 用户来源
	 */
	private Integer sourceType;
	/**
	 * 积分
	 */
	private Integer integration;
	/**
	 * 成长值
	 */
	private Integer growth;
	/**
	 * 启用状态
	 */
	private Integer status;
	/**
	 * 注册时间
	 */
	private Date createTime;

    public static MemberEntity buildByRegisterVo(RegisterVO registerVO) {
        MemberEntity member = new MemberEntity();
        member.setUsername(registerVO.getUserName());
        member.setPassword(registerVO.getPassword());
        member.setMobile(registerVO.getPhone());
        // 使用 md5 + 盐 进行加密，防止被彩虹表破解
        member.setPassword(new BCryptPasswordEncoder().encode(registerVO.getPassword()));

        return member;
    }

    public static MemberEntity buildByGithubUserVo(GithubUserVO githubUserVO) {
        MemberEntity member = new MemberEntity();
        member.setUsername(githubUserVO.getLogin());
        member.setNickname(githubUserVO.getName());

        return member;
    }
}
