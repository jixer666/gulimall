package com.ljx.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ljx.gulimall.ware.model.vo.OrderStockLockVO;
import com.ljx.gulimall.ware.model.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ljx.gulimall.ware.model.entity.WareSkuEntity;
import com.ljx.gulimall.ware.service.WareSkuService;
import com.ljx.common.utils.PageUtils;
import com.ljx.common.utils.R;



/**
 * 商品库存
 *
 * @author LiJunXi
 * @email 2770063826@qq.com
 * @date 2025-07-15 22:01:01
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/hashstock")
    public R<List<SkuHasStockVo>> getHashStock(@RequestBody List<Long> skuIds) {
        List<SkuHasStockVo> result = wareSkuService.getHashStock(skuIds);

        return R.ok().setData(result);
    }

    @PostMapping("/lock")
    public R<Boolean> lockOrderStock(@RequestBody List<OrderStockLockVO> orderStockLockVOS) {
        Boolean result = wareSkuService.lockOrderStock(orderStockLockVOS);
        if (result) {
            return R.ok().setData(true);
        } else {
            return R.error();
        }
    }

}
