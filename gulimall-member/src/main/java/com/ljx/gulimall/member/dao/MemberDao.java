package com.ljx.gulimall.member.dao;

import com.ljx.gulimall.member.domain.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:52:55
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
