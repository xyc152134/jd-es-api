package com.xyc.jdesapi.service;

import com.alibaba.fastjson.JSON;
import com.xyc.jdesapi.pojo.JDinfo;
import com.xyc.jdesapi.util.HtmlParseUitl;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JDinfoService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private HtmlParseUitl htmlParseUitl;
    //1.解析数据
    public Boolean  parseContent(String keywords) throws Exception {
        List<JDinfo> list = htmlParseUitl.parseJD(keywords);
        //插入es中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");
        for (int i = 0; i < list.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("jd_goods").source(JSON.toJSONString(list.get(i)), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }
//2.获得这些数据实现搜索功能
    public List<Map<String,Object>> searchByPage(String keyword,int pageNo,int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo=1;
        }
        //条件搜索
        SearchRequest request = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();//
        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        //精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //执行搜索
        request.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        ArrayList<Map<String ,Object>> list=new ArrayList<>();
        for (SearchHit documentFields : response.getHits().getHits()) {
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();//原来的结果
            list.add(sourceAsMap) ;
        }
        return list;

    }
    public List<Map<String,Object>> searchByPageHight(String keyword,int pageNo,int pageSize) throws IOException {
        if (pageNo<=1){
            pageNo=1;
        }
        //条件搜索
        SearchRequest request = new SearchRequest("jd_goods");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();//
        //分页
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);
        //精确匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //高亮
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        highlightBuilder.field("title");
        //是否开启多个高亮
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        request.source(searchSourceBuilder);
        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        //解析结果
        ArrayList<Map<String ,Object>> list=new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            //解析高亮字段 将原来的字段替换为新的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //新的结果
            HighlightField title = highlightFields.get("title");
            //原来的结果
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if(title!=null){
                Text[] fragments = title.fragments();
                String n_title="";
                for (Text text : fragments) {
                    n_title +=text;
                }
                sourceAsMap.put("title",n_title);//替换
            }
            list.add(sourceAsMap) ;
        }
        return list;

    }
}
