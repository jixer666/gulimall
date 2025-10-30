package com.ljx.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.R;
import com.ljx.gulimall.member.domain.entity.MemberEntity;
import com.ljx.gulimall.member.domain.vo.GithubUserVO;
import com.ljx.gulimall.member.domain.vo.LoginVO;
import com.ljx.gulimall.member.domain.vo.MemberVO;
import com.ljx.gulimall.member.domain.vo.RegisterVO;

import java.util.Map;

/**
 * 会员
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:52:55
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    R register(RegisterVO registerVO);

    R login(LoginVO loginVO);

    R<MemberVO> githubLogin(GithubUserVO githubUserVO);
}

