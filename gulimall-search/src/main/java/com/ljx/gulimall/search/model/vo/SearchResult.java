package com.ljx.gulimall.search.model.vo;

import com.ljx.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SearchResult {

    private List<SkuEsModel> product;

    private List<BranInfo> brands;

    private List<AttrInfo> attrs;

    private List<CategoryInfo> categories;

    private Integer pageNum;

    private Long totalPageSize;

    private Long total;

    private List<Integer> pages;

    private List<NavInfo> navs;

    private Map<String, Boolean> chooseMap = new HashMap<>();

    @Data
    public static class AttrInfo {
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    public static class BranInfo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CategoryInfo {
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class NavInfo {
        private String navName;
        private String navValue;
        private String link;
    }

}
