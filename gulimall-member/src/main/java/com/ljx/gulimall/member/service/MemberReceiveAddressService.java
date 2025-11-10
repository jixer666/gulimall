package com.ljx.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.member.domain.entity.MemberReceiveAddressEntity;
import com.ljx.gulimall.member.domain.vo.MemberReceiveAddressVO;

import java.util.List;
import java.util.Map;

/**
 * 会员收货地址
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:52:55
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MemberReceiveAddressVO> listByMemberId(Long memberId);
}

