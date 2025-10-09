package com.ljx.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.order.entity.RefundInfoEntity;

import java.util.Map;

/**
 * 退款信息
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 22:04:49
 */
public interface RefundInfoService extends IService<RefundInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

