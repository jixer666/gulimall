package com.ljx.gulimall.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.ljx.common.constant.CacheConstant;
import com.ljx.common.utils.AssertUtil;
import com.ljx.common.utils.R;
import com.ljx.gulimall.member.domain.vo.GithubUserVO;
import com.ljx.gulimall.member.domain.vo.LoginVO;
import com.ljx.gulimall.member.domain.vo.MemberVO;
import com.ljx.gulimall.member.domain.vo.RegisterVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.Map;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.member.dao.MemberDao;
import com.ljx.gulimall.member.domain.entity.MemberEntity;
import com.ljx.gulimall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberDao memberDao;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public R register(RegisterVO registerVO) {
        registerVO.checkParams();

        try {
            // 检查账号唯一性
            checkUsername(registerVO.getUserName());
            // 检查手机号唯一性
            checkPhone(registerVO.getPhone());

            MemberEntity member = MemberEntity.buildByRegisterVo(registerVO);
            memberDao.insert(member);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }

        return R.ok();
    }

    private void checkPhone(String phone) {
        AssertUtil.isNotEmpty(phone, "手机号不能为空");

        MemberEntity member = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        AssertUtil.isEmpty(member, "手机号已被注册");
    }

    private void checkUsername(String userName) {
        AssertUtil.isNotEmpty(userName, "账号不能为空");

        MemberEntity member = selectByUserName(userName);
        AssertUtil.isEmpty(member, "账号被注册");
    }

    private MemberEntity selectByUserName(String userName) {
        AssertUtil.isNotEmpty(userName, "账号不能为空");

        return memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("userName", userName));
    }

    @Override
    public R login(LoginVO loginVO) {
        loginVO.checkParams();

        MemberEntity member = selectByUserName(loginVO.getUserName());
        if (member == null) {
            return R.error("账号不存在");
        }
        if (!new BCryptPasswordEncoder().matches(loginVO.getPassword(), member.getPassword())) {
            return R.error("账号或者密码不正确");
        }

        return R.ok();
    }

    @Override
    public R<MemberVO> githubLogin(GithubUserVO githubUserVO) {
        githubUserVO.checkParams(githubUserVO);

        MemberEntity member = selectByUserName(githubUserVO.getName());
        if (Objects.isNull(member)) {
            member = MemberEntity.buildByGithubUserVo(githubUserVO);
            memberDao.insert(member);
        }

        return R.ok().setData(BeanUtil.copyProperties(member, MemberVO.class));
    }
}