package com.xyc.esapi;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class EsApiApplicationTests {
@Autowired
@Qualifier("restHighLevelClient")
private RestHighLevelClient client ;
    //api
    //测试创建索引
    @Test
    void  testCreateIndex() throws IOException {
        //1.创建索引
        CreateIndexRequest request=new CreateIndexRequest("test_index");
        //2.执行请求
        CreateIndexResponse response=client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(response);
    }


}
