package com.ljx.gulimall.product.model.vo;

import com.ljx.gulimall.product.model.entity.SkuImagesEntity;
import com.ljx.gulimall.product.model.entity.SkuInfoEntity;
import com.ljx.gulimall.product.model.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuVo {

    // spu信息
    private SkuInfoEntity info;

    // 是否有库存
    private boolean hasStock = true;

    // sku图片
    private List<SkuImagesEntity> images;

    // spu销售属性
    private List<SpuSaleAttrVO> saleAttr;

    // spu详情信息
    private SpuInfoDescEntity desc;

    // spu规格信息
    private List<SpuItemAttrGroupVO> groupAttrs;

    private SeckillSkuVo seckillSkuVo;


}
