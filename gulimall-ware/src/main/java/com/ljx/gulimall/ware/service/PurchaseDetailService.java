package com.ljx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.ware.model.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 * 
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 22:01:01
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

