package com.ljx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljx.common.exception.RRException;
import com.ljx.common.utils.AssertUtil;
import com.ljx.gulimall.product.model.vo.SkuVo;
import com.ljx.gulimall.product.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.product.dao.SkuInfoDao;
import com.ljx.gulimall.product.model.entity.SkuInfoEntity;


@Slf4j
@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuInfoDao skuInfoDao;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getBySpuId(Long spuId) {
        AssertUtil.isNotEmpty(spuId, "spu ID不能为空");

        return list(new LambdaQueryWrapper<SkuInfoEntity>()
                .eq(SkuInfoEntity::getSpuId, spuId));
    }


    @Override
    public SkuVo getSkuItemBySkuId(Long skuId) {
        AssertUtil.isNotEmpty(skuId, "sku ID不能为空");

        SkuVo skuVo = new SkuVo();

//        // sku基本信息
//        SkuInfoEntity skuInfo = skuInfoDao.selectById(skuId);
//        skuVo.setInfo(skuInfo);
//
//        // sku图片列表
//        skuVo.setImages(skuImagesService.list());
//
//        // spu销售属性
//        skuVo.setSaleAttr(skuSaleAttrValueService.getSkuSaleAttrValueBySpuId(skuInfo.getSpuId()));
//
//        // spu详情信息
//        skuVo.setDesc(spuInfoDescService.getById(skuInfo.getSpuId()));
//
//        // spu规格属性
//        skuVo.setGroupAttrs(attrGroupService.getAttrGroupBySpuIdAndCategoryId(skuInfo.getSpuId(), skuInfo.getCatalogId()));


        CompletableFuture<SkuInfoEntity> skuFuture = CompletableFuture.supplyAsync(() -> {
            // sku基本信息
            SkuInfoEntity skuInfo = skuInfoDao.selectById(skuId);
            skuVo.setInfo(skuInfo);

            return skuInfo;
        }, threadPoolExecutor);

        CompletableFuture<Void> imageFuture = skuFuture.thenAcceptAsync((res) -> {
            // sku图片列表
            skuVo.setImages(skuImagesService.selectBySkuId(res.getSkuId()));
        }, threadPoolExecutor);

        CompletableFuture<Void> saleAttrFuture = skuFuture.thenAcceptAsync((res) -> {
            // spu销售属性
            skuVo.setSaleAttr(skuSaleAttrValueService.getSkuSaleAttrValueBySpuId(res.getSpuId()));
        }, threadPoolExecutor);

        CompletableFuture<Void> spuFuture = skuFuture.thenAcceptAsync((res) -> {
            // spu详情信息
            skuVo.setDesc(spuInfoDescService.getById(res.getSpuId()));
        }, threadPoolExecutor);

        CompletableFuture<Void> spuGroupFuture = skuFuture.thenAcceptAsync((res) -> {
            // spu规格属性
            skuVo.setGroupAttrs(attrGroupService.getAttrGroupBySpuIdAndCategoryId(res.getSpuId(), res.getCatalogId()));
        }, threadPoolExecutor);

        try {
            CompletableFuture.allOf(imageFuture, saleAttrFuture, spuFuture, spuGroupFuture).get();
        } catch (Exception e) {
            log.error("异步编排出错：{}", e.getMessage(), e);
            throw new RRException("异步编排出错");
        }

        return skuVo;
    }
}