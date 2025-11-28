package com.ljx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.product.model.entity.SkuInfoEntity;
import com.ljx.gulimall.product.model.vo.SkuVo;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:57:30
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuInfoEntity> getBySpuId(Long spuId);

    SkuVo getSkuItemBySkuId(Long skuId);

    List<SkuInfoEntity> getByIds(List<Long> skuIds);
}

