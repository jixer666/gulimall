package com.ljx.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.member.entity.MemberEntity;

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
}

