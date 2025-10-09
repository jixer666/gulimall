package com.ljx.gulimall.search.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.ljx.common.to.es.SkuEsModel;
import com.ljx.gulimall.search.config.GulimallSearchConfig;
import com.ljx.gulimall.search.constant.EsConstant;
import com.ljx.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-07-23  20:45
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean produceStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        if (CollUtil.isEmpty(skuEsModels)) {
            return true;
        }

        // 批量创建 ES 索引
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX, EsConstant.PRODUCT_TYPE);
            indexRequest.id(skuEsModel.getSkuId().toString());
            indexRequest.source(JSONUtil.toJsonStr(skuEsModel), XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, GulimallSearchConfig.COMMON_OPTIONS);
        boolean result = bulkResponse.hasFailures();
        if (result) {
            List<String> ids = Arrays.stream(bulkResponse.getItems()).filter(BulkItemResponse::isFailed)
                    .map(BulkItemResponse::getId).collect(Collectors.toList());
            log.error("商品上架出错：{}", ids);
        }

        return !result;
    }
}
