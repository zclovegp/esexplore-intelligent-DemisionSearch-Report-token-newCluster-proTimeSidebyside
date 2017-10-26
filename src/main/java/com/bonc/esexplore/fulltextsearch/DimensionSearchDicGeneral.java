/*
package com.bonc.esexplore.fulltextsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;

*/
/**
 * Created by zhaoc on 2017/9/5.
 *//*

public class DimensionSearchDicGeneral {
    public static void main(String[] args) throws IOException {
        Settings settings = Settings.builder()
                .put("cluster.name", "Logs_Collect_V1")
                .put("client.transport.sniff", true)
                .build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.249.216.108"), 9300))
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.249.216.109"), 9300))
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("10.249.216.110"), 9300));

        SearchRequestBuilder searchRequestBuilderTopic = client.prepareSearch("dimension_search")
                .setTypes("dimension")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        QueryBuilder qb = QueryBuilders.matchAllQuery();
        boolQueryBuilder.must().add(qb);

        SearchResponse response = searchRequestBuilderTopic.setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(500)
                .get();

        //对结果遍历放到list中
        SearchHits hits = response.getHits();
        long count = response.getHits().getTotalHits();

        System.out.println("匹配到的个数有"+count);

        FileWriter fw = new FileWriter("C:\\Users\\zhaoc\\Desktop\\zc.txt");
        for (int i = 0; i < hits.getHits().length; i++) {
            SearchHit searchHit = hits.getHits()[i];
            JSONObject jsonData = JSON.parseObject(searchHit.getSourceAsString());
            System.out.println(jsonData.get("searchword").toString());
            fw.write(jsonData.get("searchword").toString().toLowerCase()+"\r\n");
        }
        fw.close();
    }
}
*/
