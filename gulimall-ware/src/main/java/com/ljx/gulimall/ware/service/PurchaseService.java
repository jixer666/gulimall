package com.ljx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.ware.model.entity.PurchaseEntity;

import java.util.Map;

/**
 * 采购信息
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 22:01:01
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

