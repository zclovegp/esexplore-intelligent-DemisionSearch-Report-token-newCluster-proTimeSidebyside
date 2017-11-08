package com.bonc.esexplore.fulltextsearch;

import com.bonc.esexplore.dataform.DataForm;
import com.bonc.esexplore.until.CodeTable;
import com.bonc.esexplore.until.OneBecomeMore;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/6/1.
 */
public class NormalOrNothingGet {

    public static int count=0;
    public static int count2=0;

    //有输入搜索词
    public static List<HashMap<String, Object>> normalGet(TransportClient client, String field, String searchWord, String from, String size, HashMap<String,Object> hashMapAuth, String index, String type, String K_T_R, String filterWhat, String tabId, HashMap<String, Integer> ratingMap,List<HashMap<String, Object>> dimensionList){
        //确定index和type
        SearchRequestBuilder searchRequestBuilderTopic = client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        List<String> AUTHORITY = new ArrayList<>();
        //如果有筛选条件的那么添加
        if(null!=hashMapAuth.get(K_T_R)) {
            AUTHORITY = (List<String>) hashMapAuth.get(K_T_R);
        }

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        QueryBuilder must_what;
        //存放搜索内容的
        if(type.equals("T")||type.equals("K")) {
            must_what = QueryBuilders.matchQuery(field, searchWord).boost(10);
            QueryBuilder must_what2 = QueryBuilders.matchPhraseQuery(field, searchWord).boost(10);
            boolQueryBuilder.should().add(must_what);
            boolQueryBuilder.should().add(must_what2);
        }else{
            must_what = QueryBuilders.matchQuery(field, searchWord);
            boolQueryBuilder.must().add(must_what);
        }

        //存放筛选条件的,如果有才会add没有那么不会add
        for (int i=0;i<AUTHORITY.size();i++){
            QueryBuilder mustNot_what = QueryBuilders.termQuery(filterWhat,AUTHORITY.get(i));
            boolQueryBuilder.mustNot().add(mustNot_what);
        }
        //对于tab影响的总数要考虑进去
        if(!CodeTable.getTabCodeValue(tabId).equals("全部")){
            QueryBuilder must_add = QueryBuilders.termQuery("Acct_Type",CodeTable.getTabCodeValue(tabId));
            boolQueryBuilder.must().add(must_add);
        }

        //对于专题还要把desc考虑进去
        if(type.equals("T")){
            QueryBuilder topic = QueryBuilders.matchPhraseQuery("Desc", searchWord).boost(1);
            boolQueryBuilder.should().add(topic);
            boolQueryBuilder.minimumShouldMatch(1);
        }

        //对于指标还要把desc考虑进去
        if(type.equals("K")){
            QueryBuilder kpi = QueryBuilders.matchPhraseQuery("KPI_Desc", searchWord).boost(1);
            boolQueryBuilder.should().add(kpi);
            boolQueryBuilder.minimumShouldMatch(1);
        }

        SearchResponse responseTopic = searchRequestBuilderTopic.setQuery(boolQueryBuilder)
                .addSort("_score", SortOrder.DESC).addSort(field+"_Length", SortOrder.ASC)
                //.setFrom(Integer.parseInt(from) - 1)
                .setFrom(0)
                //.setSize(Integer.parseInt(size))
                .setSize(500)
                .get();

        //对结果遍历放到list中
        SearchHits hits = responseTopic.getHits();
        System.out.println("用户有输入搜索词:"+searchWord);
        System.out.println("from--->" + from + ",size--->"+size+",field"+field);
        List<HashMap<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < hits.getHits().length; i++) {
            SearchHit searchHit = hits.getHits()[i];
            HashMap<String, Object> tmpMap = DataForm.dataForm(searchHit.getType(), searchHit, ratingMap);
            resultList.add(tmpMap);
        }
        //专题不加维度
        if (!type.equals("K")){
            return resultList;
        }

        //一个指标和多个维度组合进行拼接
        List<HashMap<String, Object>> resultListF = OneBecomeMore.oneBecomeMore(resultList, dimensionList);
        count = resultListF.size();

        return resultListF;

    }

    //什么都不输入
    public static List<HashMap<String, Object>> nothingGet(TransportClient client,String from,String size,HashMap<String,Object> hashMapAuth,String index,String type,String K_T_R,String filterWhat,String tabId,HashMap<String, Integer> ratingMap,List<HashMap<String, Object>> dimensionList) {
        //确定index和type
        SearchRequestBuilder searchRequestBuilderTopic = client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        List<String> AUTHORITY = new ArrayList<>();
        if (null != hashMapAuth.get(K_T_R)) {
            AUTHORITY = (List<String>) hashMapAuth.get(K_T_R);
        }
        //存放搜索内容的
        QueryBuilder must_what = QueryBuilders.matchAllQuery();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must().add(must_what);
        //存放筛选条件的,如果有才会add没有那么不会add
        for (int i = 0; i < AUTHORITY.size(); i++) {
            QueryBuilder mustNot_what = QueryBuilders.termQuery(filterWhat, AUTHORITY.get(i));
            boolQueryBuilder.mustNot().add(mustNot_what);
        }
        //对于tab影响的总数要考虑进去
        if(!CodeTable.getTabCodeValue(tabId).equals("全部")){
            QueryBuilder must_add = QueryBuilders.termQuery("Acct_Type",CodeTable.getTabCodeValue(tabId));
            boolQueryBuilder.must().add(must_add);
        }

        SearchResponse responseTopic = searchRequestBuilderTopic.setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(10000)
                .get();

        //对结果遍历放到list中
        SearchHits hits = responseTopic.getHits();
        System.out.println("用户没有输入搜索词");
        System.out.println("from--->" + from + ",size--->"+size);
        List<HashMap<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < hits.getHits().length; i++) {
            SearchHit searchHit = hits.getHits()[i];
            HashMap<String, Object> tmpMap = DataForm.dataForm(searchHit.getType(), searchHit, ratingMap);
            resultList.add(tmpMap);
        }

        if (!type.equals("K")){
            return resultList;
        }

        List<HashMap<String, Object>> resultListF = OneBecomeMore.oneBecomeMore(resultList, dimensionList);
        count2 = resultListF.size();

        return resultListF;
    }

    //统计总共个数（有搜索词）
    public static String normalGetCount(TransportClient client, String field, String searchWord, HashMap<String,Object> hashMapAuth, String index, String type,String K_T_R,String filterWhat,String tabId){
        //确定index和type
        SearchRequestBuilder searchRequestBuilderTopic = client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        List<String> AUTHORITY = new ArrayList<>();
        //如果有筛选条件的那么添加
        if(null!=hashMapAuth.get(K_T_R)) {
            AUTHORITY = (List<String>) hashMapAuth.get(K_T_R);
        }

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        QueryBuilder must_what;
        //存放搜索内容的
        if(type.equals("T")||type.equals("K")) {
            must_what = QueryBuilders.matchQuery(field, searchWord).boost(10);
            QueryBuilder must_what2 = QueryBuilders.matchPhraseQuery(field, searchWord).boost(10);
            boolQueryBuilder.should().add(must_what);
            boolQueryBuilder.should().add(must_what2);
            String tmp = null;
        }else{
            must_what = QueryBuilders.matchQuery(field, searchWord);
            boolQueryBuilder.must().add(must_what);
        }

        //存放筛选条件的,如果有才会add没有那么不会add
        for (int i=0;i<AUTHORITY.size();i++){
            QueryBuilder mustNot_what = QueryBuilders.termQuery(filterWhat,AUTHORITY.get(i));
            boolQueryBuilder.mustNot().add(mustNot_what);
        }
        //对于tab影响的总数要考虑进去
        if(!CodeTable.getTabCodeValue(tabId).equals("全部")){
            QueryBuilder must_add = QueryBuilders.termQuery("Acct_Type",CodeTable.getTabCodeValue(tabId));
            boolQueryBuilder.must().add(must_add);
        }

        //对于专题还要把desc考虑进去
        if(type.equals("T")){
            QueryBuilder topic = QueryBuilders.matchPhraseQuery("Desc", searchWord).boost(1);
            boolQueryBuilder.should().add(topic);
            boolQueryBuilder.minimumShouldMatch(1);
        }

        //对于指标还要把desc考虑进去
        if(type.equals("K")){
            QueryBuilder kpi = QueryBuilders.matchPhraseQuery("KPI_Desc", searchWord).boost(1);
            boolQueryBuilder.should().add(kpi);
            boolQueryBuilder.minimumShouldMatch(1);
        }

        SearchResponse responseTopic = searchRequestBuilderTopic.setQuery(boolQueryBuilder)
                .get();

        SearchHits hits = responseTopic.getHits();

        if (!type.equals("K")){
            return hits.getTotalHits()+"";
        }

        //return hits.getTotalHits()+"";
        return count+"";

    }

    //统计总共个数（无搜索词）
    public static String nothingGetCount(TransportClient client, HashMap<String,Object> hashMapAuth, String index, String type,String K_T_R,String filterWhat,String tabId) {
        //确定index和type
        SearchRequestBuilder searchRequestBuilderTopic = client.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        List<String> AUTHORITY = new ArrayList<>();
        if (null != hashMapAuth.get(K_T_R)) {
            AUTHORITY = (List<String>) hashMapAuth.get(K_T_R);
        }
        //存放搜索内容的
        QueryBuilder must_what = QueryBuilders.matchAllQuery();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must().add(must_what);
        //存放筛选条件的,如果有才会add没有那么不会add
        for (int i = 0; i < AUTHORITY.size(); i++) {
            QueryBuilder mustNot_what = QueryBuilders.termQuery(filterWhat, AUTHORITY.get(i));
            boolQueryBuilder.mustNot().add(mustNot_what);
        }
        //对于tab影响的总数要考虑进去
        if(!CodeTable.getTabCodeValue(tabId).equals("全部")){
            QueryBuilder must_add = QueryBuilders.termQuery("Acct_Type",CodeTable.getTabCodeValue(tabId));
            boolQueryBuilder.must().add(must_add);
        }

        SearchResponse responseTopic = searchRequestBuilderTopic.setQuery(boolQueryBuilder)
                .get();

        SearchHits hits = responseTopic.getHits();

        if (!type.equals("K")){
            return hits.getTotalHits()+"";
        }

        //return hits.getTotalHits()+"";
        return count2+"";
    }

}
