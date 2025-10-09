package com.ljx.gulimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ljx.gulimall.product.model.dto.CategoryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ljx.gulimall.product.model.entity.CategoryEntity;
import com.ljx.gulimall.product.service.CategoryService;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.R;



/**
 * 商品三级分类
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 21:57:30
 */
@RestController
@RequestMapping("product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:category:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/list/tree")
    // @RequiresPermissions("product:category:list")
    public R listTree(){
        List<CategoryDto> result = categoryService.listTree();

        return R.ok().put("data", result);
    }

    @PostMapping("/update/sort")
    public R updateSort(@RequestBody List<CategoryEntity> categoryList){
        categoryService.updateBatchById(categoryList);

        return R.ok();
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    // RequiresPermissions("product:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:category:save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:category:delete")
    public R delete(@RequestBody Long[] catIds){
		categoryService.removeByIds(Arrays.asList(catIds));

        return R.ok();
    }

}
