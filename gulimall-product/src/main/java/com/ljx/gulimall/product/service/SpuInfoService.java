package com.ljx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.product.model.entity.SpuInfoEntity;

import java.util.Map;

/**
 * spu信息
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:57:30
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void upSpu(Long spuId);

    SpuInfoEntity getBySkuId(Long skuId);
}

