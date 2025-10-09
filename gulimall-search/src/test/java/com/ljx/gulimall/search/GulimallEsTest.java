package com.ljx.gulimall.search;

import com.ljx.gulimall.search.config.GulimallSearchConfig;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-07-19  23:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallEsTest {

    @Autowired
    private RestHighLevelClient client;


    @Test
    public void test() {
        System.out.println(client);
    }


    /**
     * 保存
     */
    @Test
    public void testSave() throws IOException {
        IndexRequest request = new IndexRequest("megacorp", "employee", "5")
                .source("first_name", "jixer",
                        "age", 22);
        IndexResponse response = client.index(request, GulimallSearchConfig.COMMON_OPTIONS);
        System.out.println(response);
    }

    /**
     * 查询
     * @throws IOException
     */
    @Test
    public void testQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("megacorp");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("first_name", "John"));
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, GulimallSearchConfig.COMMON_OPTIONS);
        System.out.println(searchResponse);

    }

}
