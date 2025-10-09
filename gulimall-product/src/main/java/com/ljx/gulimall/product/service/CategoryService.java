package com.ljx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ljx.common.utils.PageUtils;
import com.ljx.gulimall.product.model.dto.CategoryDto;
import com.ljx.gulimall.product.model.entity.CategoryEntity;
import com.ljx.gulimall.product.model.vo.Category2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:57:30
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<CategoryDto> listTree();

    List<CategoryEntity> getCategoryByPId(Long categoryId);

    Map<String, List<Category2Vo>> getCategoryList();

    Map<String, List<Category2Vo>> getCategoryListWithCache();

    List<String> getCache();

    Map<String, List<Category2Vo>> getCategoryListWithSpringCache();

    void deleteCategorySpringCache();

    void testDeleteCacheAll();

    List<String> updateCache();
}

