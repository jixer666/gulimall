package com.ljx.gulimall.product.dao;

import com.ljx.gulimall.product.model.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ljx.gulimall.product.model.vo.SpuSaleAttrVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * sku销售属性&值
 * 
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:57:30
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SpuSaleAttrVO> selectSkuSaleAttrValueBySpuId(Long spuId);
}
