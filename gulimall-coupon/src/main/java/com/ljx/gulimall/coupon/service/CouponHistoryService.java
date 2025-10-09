package com.ljx.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:45:38
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

