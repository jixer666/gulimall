package com.ljx.gulimall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ljx.common.to.es.SkuEsModel;
import com.ljx.common.utils.AssertUtil;
import com.ljx.common.utils.R;
import com.ljx.gulimall.product.feign.SearchFeignService;
import com.ljx.gulimall.product.feign.WareFeignService;
import com.ljx.gulimall.product.model.entity.*;
import com.ljx.gulimall.product.model.enums.PublishStatusEnum;
import com.ljx.gulimall.product.model.vo.SkuHasStockVo;
import com.ljx.gulimall.product.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.Query;

import com.ljx.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SpuInfoDao spuInfoDao;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    @Transactional
    public void upSpu(Long spuId) {
        AssertUtil.isNotEmpty(spuId, "spu ID不能为空");

        List<SkuInfoEntity> skuList = skuInfoService.getBySpuId(spuId);

        List<Long> brandIds = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        List<Long> skuIds = new ArrayList<>();
        skuList.stream().forEach(item -> {
            brandIds.add(item.getBrandId());
            categoryIds.add(item.getCatalogId());
            skuIds.add(item.getSkuId());
        });
        Map<Long, BrandEntity> brandMap = brandService.listByIds(brandIds).stream().collect(Collectors.toMap(BrandEntity::getBrandId, Function.identity()));
        Map<Long, CategoryEntity> categoryMap = categoryService.listByIds(categoryIds).stream().collect(Collectors.toMap(CategoryEntity::getCatId, Function.identity()));

        // 获取 SkuEsModel 的 Attr 参数
        List<ProductAttrValueEntity> productAttrValueList = productAttrValueService.getBySpuId(spuId);
        Map<Long, ProductAttrValueEntity> productAttrMap = productAttrValueList.stream().collect(Collectors.toMap(ProductAttrValueEntity::getAttrId, Function.identity()));
        List<Long> attrtIds = productAttrValueList.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<SkuEsModel.Attrs> attrsList = attrService.selectSearchAttrIds(attrtIds).stream().map(item -> {
            ProductAttrValueEntity productAttrValue = productAttrMap.get(item.getAttrId());
            SkuEsModel.Attrs attrs = new SkuEsModel.Attrs();
            attrs.setAttrName(productAttrValue.getAttrName());
            attrs.setAttrValue(productAttrValue.getAttrValue());
            attrs.setAttrId(productAttrValue.getAttrId());
            return attrs;
        }).collect(Collectors.toList());

        // 调用 ware 远程服务，查询是否存在库存
        Map<Long, Boolean> stockMap = wareFeignService.getHashStock(skuIds).getDataList(SkuHasStockVo.class)
                .stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHashStock));

        List<SkuEsModel> skuEsModels = new ArrayList<>();
        for (SkuInfoEntity skuInfo : skuList) {
            SkuEsModel skuEsModel = BeanUtil.copyProperties(skuInfo, SkuEsModel.class);
            skuEsModel.setSkuPrice(skuInfo.getPrice());
            skuEsModel.setSkuImg(skuInfo.getSkuDefaultImg());
            // 是否存在库存
            skuEsModel.setHasStock(stockMap.getOrDefault(skuInfo.getSkuId(), true));
            // 热度评分
            skuEsModel.setHotScore(0L);
            // 品牌
            BrandEntity brand = brandMap.getOrDefault(skuInfo.getBrandId(), new BrandEntity());
            skuEsModel.setBrandName(brand.getName());
            skuEsModel.setBrandImg(brand.getLogo());
            // 分类
            CategoryEntity category = categoryMap.getOrDefault(skuInfo.getCatalogId(), new CategoryEntity());
            skuEsModel.setCatalogName(category.getName());
            // 商品属性
            skuEsModel.setAttrs(attrsList);

            skuEsModels.add(skuEsModel);
        }

        // 调用es
        R<Boolean> r = searchFeignService.produceStatusUp(skuEsModels);
        if (r.getDataObj(Boolean.class)) {
            updateSpuStatus(spuId, PublishStatusEnum.SPU_DOWN.getKey());
        } else {
            // 调用失败，进行重试，最多重试3次
            int count = 3;
            while (count-- > 0) {
                try {
                    Thread.sleep(2000);
                    R<Boolean> retryResult = searchFeignService.produceStatusUp(skuEsModels);
                    if (retryResult.getDataObj(Boolean.class)) {
                        updateSpuStatus(spuId, PublishStatusEnum.SPU_DOWN.getKey());
                        break;
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        }

    }

    private void updateSpuStatus(Long spuId, Integer key) {
        AssertUtil.isNotEmpty(spuId, "spuId不能为空");
        AssertUtil.isNotEmpty(key, "key不能为空");

        SpuInfoEntity spuInfo = spuInfoDao.selectOne(new LambdaQueryWrapper<SpuInfoEntity>()
                .eq(SpuInfoEntity::getId, spuId));
        spuInfo.setPublishStatus(key);
        spuInfo.setUpdateTime(new Date());
        spuInfoDao.updateById(spuInfo);
    }
}