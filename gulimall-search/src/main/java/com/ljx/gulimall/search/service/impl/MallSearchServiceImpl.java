package com.ljx.gulimall.search.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.ljx.common.to.es.SkuEsModel;
import com.ljx.gulimall.search.config.GulimallSearchConfig;
import com.ljx.gulimall.search.constant.EsConstant;
import com.ljx.gulimall.search.feign.ProductFeignService;
import com.ljx.gulimall.search.model.dto.SearchParam;
import com.ljx.gulimall.search.model.vo.AttrEntityVO;
import com.ljx.gulimall.search.model.vo.SearchResult;
import com.ljx.gulimall.search.service.MallSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public SearchResult searchFromIndex(SearchParam searchParam) {
        SearchResult result = new SearchResult();

        SearchRequest searchRequest = buildSearchRequest(searchParam, result);

        SearchResponse searchResponse = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, GulimallSearchConfig.COMMON_OPTIONS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buildSearchResponse(searchResponse, searchParam, result);
    }


    private SearchResult buildSearchResponse(SearchResponse searchResponse, SearchParam searchParam, SearchResult result) {
        SearchHits searchHits = searchResponse.getHits();

        // 设置产品
        List<SkuEsModel> products = Arrays.stream(searchHits.getHits()).map(item -> {
            String sourceAsString = item.getSourceAsString();
            SkuEsModel skuEsModel = JSONUtil.toBean(sourceAsString, SkuEsModel.class);

            if (StrUtil.isNotEmpty(searchParam.getKeyword())) {
                // 若存在搜索就高亮显示
                HighlightField skuTitle = item.getHighlightFields().get("skuTitle");
                String skuTitleValue = skuTitle.getFragments()[0].string();
                skuEsModel.setSkuTitle(skuTitleValue);
            }

            return skuEsModel;
        }).collect(Collectors.toList());
        result.setProduct(products);

        // 设置属性（聚合分析）
        ParsedNested attrAgg = searchResponse.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        List<SearchResult.AttrInfo> attrs = attrIdAgg.getBuckets().stream().map(item -> {
            SearchResult.AttrInfo attrInfo = new SearchResult.AttrInfo();

            // id
            attrInfo.setAttrId(item.getKeyAsNumber().longValue());
            ParsedStringTerms attrNameAgg = item.getAggregations().get("attr_name_agg");
            if (CollUtil.isNotEmpty(attrNameAgg.getBuckets())) {
                // name
                String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
                attrInfo.setAttrName(attrName);
            }
            // values
            ParsedStringTerms attrValueAgg = item.getAggregations().get("attr_value_agg");
            if (CollUtil.isNotEmpty(attrValueAgg.getBuckets())) {
                List<String> attrValues = attrValueAgg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
                attrInfo.setAttrValue(attrValues);
            }

            return attrInfo;
        }).collect(Collectors.toList());
        result.setAttrs(attrs);

        // 设置商品
        ParsedLongTerms brandAgg = searchResponse.getAggregations().get("brand_agg");
        List<SearchResult.BranInfo> brands = brandAgg.getBuckets().stream().map(item -> {
            SearchResult.BranInfo branInfo = new SearchResult.BranInfo();

            // id
            branInfo.setBrandId(item.getKeyAsNumber().longValue());
            // name
            ParsedStringTerms attrNameAgg = item.getAggregations().get("brand_name_agg");
            if (CollUtil.isNotEmpty(attrNameAgg.getBuckets())) {
                String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
                branInfo.setBrandName(attrName);
            }

            // img
            ParsedStringTerms attrImgAgg = item.getAggregations().get("brand_img_agg");
            if (CollUtil.isNotEmpty(attrImgAgg.getBuckets())) {
                String attrImg = attrImgAgg.getBuckets().get(0).getKeyAsString();
                branInfo.setBrandImg(attrImg);
            }

            return branInfo;
        }).collect(Collectors.toList());
        result.setBrands(brands);

        // 设置分类
        ParsedLongTerms catalogAgg = searchResponse.getAggregations().get("catalog_agg");
        List<SearchResult.CategoryInfo> categoryInfos = catalogAgg.getBuckets().stream().map(item -> {
            SearchResult.CategoryInfo categoryInfo = new SearchResult.CategoryInfo();

            // id
            categoryInfo.setCatalogId(item.getKeyAsNumber().longValue());
            // name
            ParsedStringTerms categoryNameAgg = item.getAggregations().get("catalog_name_agg");
            if (CollUtil.isNotEmpty(categoryNameAgg.getBuckets())) {
                String categoryName = categoryNameAgg.getBuckets().get(0).getKeyAsString();
                categoryInfo.setCatalogName(categoryName);
            }

            return categoryInfo;
        }).collect(Collectors.toList());
        result.setCategories(categoryInfos);

        // 设置分页
        result.setPageNum(searchParam.getPageNum());
        result.setTotal(searchHits.getTotalHits());
        result.setTotalPageSize(result.getTotal() % searchParam.getPageSize() == 0
                ? result.getTotal() / searchParam.getPageSize() : result.getTotal() / searchParam.getPageSize() + 1
        );

         // 分页
        List<Integer> pages = getPages(searchParam, result);
        result.setPages(pages);

        // 面包屑
        StringBuilder url = new StringBuilder();
        if (CollUtil.isNotEmpty(searchParam.getAttrs())) {
            List<SearchResult.NavInfo> navInfos = searchParam.getAttrs().stream().map(item -> {
                SearchResult.NavInfo navInfo = new SearchResult.NavInfo();

                String[] splits = item.split("_");
                navInfo.setNavValue(splits[1]);
                AttrEntityVO attrEntityVO = productFeignService.getAttrInfo(Long.parseLong(splits[0])).getDataObj(AttrEntityVO.class);
                navInfo.setNavName(Objects.isNull(attrEntityVO) ? "未知属性" : attrEntityVO.getAttrName());

                if (url.toString().contains("?")) {
                    url.append("&attrs=").append(item);
                } else {
                    url.append("?attrs=").append(item);
                }
                navInfo.setLink(url.toString());

                return navInfo;
            }).collect(Collectors.toList());
            result.setNavs(navInfos);
        }

        return result;
    }

    private static List<Integer> getPages(SearchParam searchParam, SearchResult result) {
        List<Integer> pages = new ArrayList<>();
        for(int i = 1; i <= result.getTotalPageSize(); i++) {
            pages.add(i);
        }
        return pages;
    }

    private SearchRequest buildSearchRequest(SearchParam searchParam, SearchResult result) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 查询关键字
        if (StrUtil.isNotEmpty(searchParam.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", searchParam.getKeyword()));
            result.getChooseMap().put("skuTitle", true);
        }
        // 查询品牌
        if (Objects.nonNull(searchParam.getBrandId())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandId", searchParam.getBrandId()));
            result.getChooseMap().put("brandId", true);
        }
        // 查询分类
        if (Objects.nonNull(searchParam.getCategory3Id())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", searchParam.getCategory3Id()));
            result.getChooseMap().put("catalogId", true);
        }
        // 查询价格
        if (StrUtil.isNotEmpty(searchParam.getSkuPrice())) {
            result.getChooseMap().put("skuPrice", true);
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] prices = searchParam.getSkuPrice().split("_");
            if (prices.length == 2) {
                rangeQuery.gte(prices[0]).lte(prices[1]);
            } else {
                if (searchParam.getSkuPrice().startsWith("_")) {
                    rangeQuery.lte(prices[0]);
                } else {
                    rangeQuery.gte(prices[0]);
                }
            }
            boolQueryBuilder.filter(rangeQuery);
        }
        // 查询属性
        if (CollUtil.isNotEmpty(searchParam.getAttrs())) {
            for (String attrStr : searchParam.getAttrs()) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

                String[] attrs = attrStr.split("_");
                String attrId = attrs[0];
                String[] attrsValue = attrs[1].split(":");

                boolQuery.filter(QueryBuilders.termsQuery("attrs.attrId", attrId));
                boolQuery.filter(QueryBuilders.termsQuery("attrs.attrValue", attrsValue));

                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", boolQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);

                result.getChooseMap().put(attrId, true);
            }
        }
        // 查询是否有库存
        boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", searchParam.getHasStock().equals(1)));

        searchSourceBuilder.query(boolQueryBuilder);

        // 分页
        searchSourceBuilder.from((searchParam.getPageNum() - 1)* EsConstant.PRODUCT_PAGE_SIZE);
        searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);
        // 排序
        if (StrUtil.isNotEmpty(searchParam.getSort())) {
            String[] sorts = searchParam.getSort().split("_");
            String sortField = sorts[0];
            SortOrder sortOrder = "asc".equalsIgnoreCase(sorts[1]) ? SortOrder.ASC : SortOrder.DESC;
            searchSourceBuilder.sort(sortField, sortOrder);
        }
        // 高亮
        if (StrUtil.isNotEmpty(searchParam.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            searchSourceBuilder.highlighter(highlightBuilder);
        }

        // 聚合分析
        // 品牌聚合
        TermsAggregationBuilder aggregationBuilders = AggregationBuilders.terms("brand_agg");
        aggregationBuilders.field("brandId").size(50);
        // 品牌子聚合
        aggregationBuilders.subAggregation(AggregationBuilders.terms("brand_name_agg")
                .field("brandName.keyword").size(1));
        aggregationBuilders.subAggregation(AggregationBuilders.terms("brand_img_agg")
                .field("brandImg.keyword").size(1));
        searchSourceBuilder.aggregation(aggregationBuilders);

        // 分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg");
        catalogAgg.field("catalogId").size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName.keyword").size(1));
        searchSourceBuilder.aggregation(catalogAgg);

        // 属性聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        // 属性子聚合
        TermsAggregationBuilder attIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        // 属性子子聚合
        attIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attrAgg.subAggregation(attIdAgg);
        searchSourceBuilder.aggregation(attrAgg);


        log.info("构建DSL语句：{}", searchSourceBuilder.toString());
        return new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}).source(searchSourceBuilder);
    }

}
