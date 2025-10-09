package com.ljx.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.member.entity.GrowthChangeHistoryEntity;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:52:55
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

