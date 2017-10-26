package com.bonc.esexplore.fulltextsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.esexplore.until.DimensionInit;
import com.bonc.esexplore.until.SetESConfig;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/9/8.
 */
public class DimensionSearch {

    public static String esDimensionIndex;

    public static HashMap<String,Object> dimensionSearch(String searchWord ,TransportClient client) throws IOException {
        HashMap<String,String> propertiesMap = SetESConfig.getPropertiesES();
        esDimensionIndex = propertiesMap.get("esDimensionIndex");
        SearchRequestBuilder searchRequestBuilderTopic = client.prepareSearch(esDimensionIndex)
                .setTypes("dimension")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        QueryBuilder qb = QueryBuilders.matchQuery("searchword",searchWord);
        boolQueryBuilder.must().add(qb);

        SearchResponse response = searchRequestBuilderTopic.setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(500)
                .get();

        //对结果遍历放到list中
        SearchHits hits = response.getHits();
        long count = response.getHits().getTotalHits();

        System.out.println("匹配到维度的个数有" + count);
        //结构初始化
        HashMap<String,Object> resHm = DimensionInit.dimensionInit();
        List<HashMap<String,Object>> resList = (List<HashMap<String,Object>>)resHm.get("selectType");

        for (int i = 0; i < hits.getHits().length; i++) {
            SearchHit searchHit = hits.getHits()[i];
            JSONObject jsonData = JSON.parseObject(searchHit.getSourceAsString());
            //渠道
            if(jsonData.get("dimensiontype").toString().equals("channel")){
                HashMap<String,Object> hmTmp = new HashMap<>();
                List<String> value = new ArrayList<>();
                //先添加旧的
                value.addAll((List<String>)resList.get(0).get("1"));
                //再添加新的
                value.add(jsonData.get("code").toString());
                hmTmp.put("1",value);
                resList.set(0,hmTmp);
            }
            //产品
            if(jsonData.get("dimensiontype").toString().equals("product")){
                HashMap<String,Object> hmTmp = new HashMap<>();
                List<String> value = new ArrayList<>();
                //先添加旧的
                value.addAll((List<String>)resList.get(1).get("2"));
                //再添加新的
                value.add(jsonData.get("code").toString());
                hmTmp.put("2",value);
                resList.set(1,hmTmp);
            }
            //业务
            if(jsonData.get("dimensiontype").toString().equals("service")){
                HashMap<String,Object> hmTmp = new HashMap<>();
                List<String> value = new ArrayList<>();
                //先添加旧的
                value.addAll((List<String>)resList.get(2).get("3"));
                //再添加新的
                value.add(jsonData.get("code").toString());
                hmTmp.put("3",value);
                resList.set(2,hmTmp);
            }
            //省份地市
            if(jsonData.get("dimensiontype").toString().equals("pro")){
                resHm.put("provId",jsonData.get("code").toString());
            }
            if(jsonData.get("dimensiontype").toString().equals("area")){
                resHm.put("provId",jsonData.get("relationproid").toString());
                resHm.put("cityId",jsonData.get("code").toString());
            }
        }

        resHm.put("selectType",resList);

        return resHm;
    }

}
