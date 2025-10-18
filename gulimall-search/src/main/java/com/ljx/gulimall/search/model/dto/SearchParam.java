package com.ljx.gulimall.search.model.dto;

import com.ljx.gulimall.search.constant.EsConstant;
import lombok.Data;

import java.util.List;

@Data
public class SearchParam {

    private String keyword;

    private Long category3Id;

    /**
     * hasScore_desc
     */
    private String sort;

    /**
     * 是否有货
     * 0：有货 / 1：无货
     */
    private Integer hasStock = 1;

    /**
     * 价格区间查询
     * 1_500
     */
    private String skuPrice;

    // 品牌ID列表
    private Long brandId;

    /**
     * 属性查询
     *  : 代表有多个
     * 1_0-安卓（Android）:苹果（IOS）
     */
    private List<String> attrs;

    private Integer pageNum = EsConstant.PRODUCT_PAGE_NUM;

    private Integer pageSize = EsConstant.PRODUCT_PAGE_SIZE;


}
