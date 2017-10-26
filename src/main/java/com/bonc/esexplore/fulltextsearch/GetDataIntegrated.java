package com.bonc.esexplore.fulltextsearch;

import com.bonc.esexplore.dataform.DataForm;
import com.bonc.esexplore.until.CodeTable;
import com.bonc.esexplore.until.SetESConfig;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/5/16.
 */
/*
* 切记描述使用matchPhraseQuery匹配
* */
public class GetDataIntegrated {

    public static String esIndex;

    public static List<HashMap<String, Object>> getData(TransportClient client, String searchWord, String from, String size, HashMap<String,Object> hashMapAuth, String tabId, HashMap<String, Integer> ratingMap){
        HashMap<String,String> propertiesMap = SetESConfig.getPropertiesES();
        esIndex = propertiesMap.get("esIndex");
        //有输入的
        if (!searchWord.equals("")) {
            List<String> K_AUTHORITY = new ArrayList<>();
            List<String> T_AUTHORITY = new ArrayList<>();
            List<String> R_AUTHORITY = new ArrayList<>();
            if(null!=hashMapAuth.get("K_AUTHORITY")) {
                K_AUTHORITY = (List<String>) hashMapAuth.get("K_AUTHORITY");
            }
            if(null!=hashMapAuth.get("T_AUTHORITY")) {
                T_AUTHORITY = (List<String>) hashMapAuth.get("T_AUTHORITY");
            }
            if(null!=hashMapAuth.get("R_AUTHORITY")) {
                R_AUTHORITY = (List<String>) hashMapAuth.get("R_AUTHORITY");
            }

            //设置权重
            QueryBuilder qbMatchKpiCode = QueryBuilders.matchQuery("KPI_Name", searchWord).boost(3);
            QueryBuilder qbMatchKpiCode2 = QueryBuilders.matchPhraseQuery("KPI_Name", searchWord).boost(100);
            QueryBuilder qbMatchTopic = QueryBuilders.matchQuery("Subject_Name", searchWord).boost(25);
            QueryBuilder qbMatchTopic2 = QueryBuilders.matchPhraseQuery("Subject_Name", searchWord).boost(50);
            QueryBuilder qbMatchReport = QueryBuilders.matchQuery("Report_Name", searchWord).boost(30);
            QueryBuilder qbMatchReport2 = QueryBuilders.matchPhraseQuery("Report_Name", searchWord).boost(30);
            QueryBuilder topicDesc = QueryBuilders.matchPhraseQuery("Desc", searchWord).boost(5);
            QueryBuilder kpiDesc = QueryBuilders.matchPhraseQuery("KPI_Desc", searchWord).boost(5);

            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            //添加搜索的词
            boolQueryBuilder.should().add(qbMatchTopic);
            boolQueryBuilder.should().add(qbMatchKpiCode);
            boolQueryBuilder.should().add(qbMatchReport);
            boolQueryBuilder.should().add(topicDesc);
            boolQueryBuilder.should().add(kpiDesc);
            boolQueryBuilder.should().add(qbMatchKpiCode2);
            boolQueryBuilder.should().add(qbMatchTopic2);
            boolQueryBuilder.should().add(qbMatchReport2);

            //存放筛选条件的,如果有才会add没有那么不会add
            for (int i=0;i<K_AUTHORITY.size();i++){
                QueryBuilder mustNot_what = QueryBuilders.termQuery("KPI_Code",K_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }
            for (int i=0;i<T_AUTHORITY.size();i++){
                QueryBuilder mustNot_what = QueryBuilders.termQuery("Subject_Code",T_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }
            for (int i=0;i<R_AUTHORITY.size();i++){
                QueryBuilder mustNot_what = QueryBuilders.termQuery("Report_Code",R_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }

            //对于tabId影响的总数要考虑进去
            if(!CodeTable.getTabCodeValue(tabId).equals("全部")){
                QueryBuilder must_add = QueryBuilders.termQuery("Acct_Type",CodeTable.getTabCodeValue(tabId));
                boolQueryBuilder.must().add(must_add);
            }

            boolQueryBuilder.minimumShouldMatch(1);

            //综合的DFS的sql
            SearchResponse response = client.prepareSearch(esIndex)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder)
                    .addSort("_score", SortOrder.DESC).addSort("KPI_Name_Length", SortOrder.ASC).addSort("Subject_Name_Length", SortOrder.ASC).addSort("Report_Name_Length", SortOrder.ASC)
                    .setFrom(Integer.parseInt(from) - 1)
                    .setSize(Integer.parseInt(size))
                    .get();
            SearchHits hits = response.getHits();
            System.out.println("用户有输入关键词:"+searchWord);
            System.out.println("from--->" + from + ",size--->" + size + ",选择的是综合类型 2017-09-26");
            List<HashMap<String, Object>> resultList = new ArrayList<>();
            for (int i = 0; i < hits.getHits().length; i++) {
                SearchHit searchHit = hits.getHits()[i];
                HashMap<String, Object> tmpMap = DataForm.dataForm(searchHit.getType(), searchHit, ratingMap);
                resultList.add(tmpMap);
            }
            return resultList;
            //什么都不输入的
        }else{
            List<String> K_AUTHORITY = new ArrayList<>();
            List<String> T_AUTHORITY = new ArrayList<>();
            List<String> R_AUTHORITY = new ArrayList<>();
            if(null!=hashMapAuth.get("K_AUTHORITY")) {
                K_AUTHORITY = (List<String>) hashMapAuth.get("K_AUTHORITY");
            }
            if(null!=hashMapAuth.get("T_AUTHORITY")) {
                T_AUTHORITY = (List<String>) hashMapAuth.get("T_AUTHORITY");
            }
            if(null!=hashMapAuth.get("R_AUTHORITY")) {
                R_AUTHORITY = (List<String>) hashMapAuth.get("R_AUTHORITY");
            }
            QueryBuilder must_what = QueryBuilders.matchAllQuery();
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must().add(must_what);
            for (int i=0;i<K_AUTHORITY.size();i++){
                QueryBuilder mustNot_what = QueryBuilders.termQuery("KPI_Code",K_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }
            for (int i=0;i<T_AUTHORITY.size();i++){
                QueryBuilder mustNot_what = QueryBuilders.termQuery("Subject_Code",T_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }
            for (int i=0;i<R_AUTHORITY.size();i++){
                QueryBuilder mustNot_what = QueryBuilders.termQuery("Report_Code",R_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }

            //对于tabId影响的总数要考虑进去
            if(!CodeTable.getTabCodeValue(tabId).equals("全部")){
                QueryBuilder must_add = QueryBuilders.termQuery("Acct_Type",CodeTable.getTabCodeValue(tabId));
                boolQueryBuilder.must().add(must_add);
            }

            //综合的DFS的sql
            SearchResponse response = client.prepareSearch(esIndex)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder)
                    .setFrom(0)
                    .setSize(10000)
                    .get();
            SearchHits hits = response.getHits();
            System.out.println("用户没有有输入关键词");
            System.out.println("from--->" + from + ",size--->" + size + ",选择的是综合类型");
            List<HashMap<String, Object>> resultList = new ArrayList<>();
            for (int i = 0; i < hits.getHits().length; i++) {
                SearchHit searchHit = hits.getHits()[i];
                HashMap<String, Object> tmpMap = DataForm.dataForm(searchHit.getType(), searchHit, ratingMap);
                resultList.add(tmpMap);
            }
            return resultList;
        }
    }

    public static String getCount(TransportClient client, String searchWord,HashMap<String,Object> hashMapAuth,String tabId) {
        HashMap<String,String> propertiesMap = SetESConfig.getPropertiesES();
        esIndex = propertiesMap.get("esIndex");
        if (!searchWord.equals("")) {
            List<String> K_AUTHORITY = new ArrayList<>();
            List<String> T_AUTHORITY = new ArrayList<>();
            List<String> R_AUTHORITY = new ArrayList<>();
            if (null != hashMapAuth.get("K_AUTHORITY")) {
                K_AUTHORITY = (List<String>) hashMapAuth.get("K_AUTHORITY");
            }
            if (null != hashMapAuth.get("T_AUTHORITY")) {
                T_AUTHORITY = (List<String>) hashMapAuth.get("T_AUTHORITY");
            }
            if (null != hashMapAuth.get("R_AUTHORITY")) {
                R_AUTHORITY = (List<String>) hashMapAuth.get("R_AUTHORITY");
            }

            //设置权重
            QueryBuilder qbMatchKpiCode = QueryBuilders.matchQuery("KPI_Name", searchWord).boost(3);
            QueryBuilder qbMatchKpiCode2 = QueryBuilders.matchPhraseQuery("KPI_Name", searchWord).boost(100);
            QueryBuilder qbMatchTopic = QueryBuilders.matchQuery("Subject_Name", searchWord).boost(25);
            QueryBuilder qbMatchTopic2 = QueryBuilders.matchPhraseQuery("Subject_Name", searchWord).boost(50);
            QueryBuilder qbMatchReport = QueryBuilders.matchQuery("Report_Name", searchWord).boost(30);
            QueryBuilder qbMatchReport2 = QueryBuilders.matchPhraseQuery("Report_Name", searchWord).boost(30);
            QueryBuilder topicDesc = QueryBuilders.matchPhraseQuery("Desc", searchWord).boost(5);
            QueryBuilder kpiDesc = QueryBuilders.matchPhraseQuery("KPI_Desc", searchWord).boost(5);


            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            //添加搜索的词
            boolQueryBuilder.should().add(qbMatchTopic);
            boolQueryBuilder.should().add(qbMatchKpiCode);
            boolQueryBuilder.should().add(qbMatchReport);
            boolQueryBuilder.should().add(topicDesc);
            boolQueryBuilder.should().add(kpiDesc);
            boolQueryBuilder.should().add(qbMatchKpiCode2);
            boolQueryBuilder.should().add(qbMatchTopic2);
            boolQueryBuilder.should().add(qbMatchReport2);

            //存放筛选条件的,如果有才会add没有那么不会add
            for (int i = 0; i < K_AUTHORITY.size(); i++) {
                QueryBuilder mustNot_what = QueryBuilders.termQuery("KPI_Code", K_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }
            for (int i = 0; i < T_AUTHORITY.size(); i++) {
                QueryBuilder mustNot_what = QueryBuilders.termQuery("Subject_Code", T_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }
            for (int i = 0; i < R_AUTHORITY.size(); i++) {
                QueryBuilder mustNot_what = QueryBuilders.termQuery("Report_Code", R_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }

            //对于tabId影响的总数要考虑进去
            if(!CodeTable.getTabCodeValue(tabId).equals("全部")){
                QueryBuilder must_add = QueryBuilders.termQuery("Acct_Type",CodeTable.getTabCodeValue(tabId));
                boolQueryBuilder.must().add(must_add);
            }

            boolQueryBuilder.minimumShouldMatch(1);

            //综合的DFS的sql
            SearchResponse response = client.prepareSearch(esIndex)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder)
                    .get();
            SearchHits hits = response.getHits();
            return "" + hits.getTotalHits();
            //什么都不输入的
        } else {
            List<String> K_AUTHORITY = new ArrayList<>();
            List<String> T_AUTHORITY = new ArrayList<>();
            List<String> R_AUTHORITY = new ArrayList<>();
            if (null != hashMapAuth.get("K_AUTHORITY")) {
                K_AUTHORITY = (List<String>) hashMapAuth.get("K_AUTHORITY");
            }
            if (null != hashMapAuth.get("T_AUTHORITY")) {
                T_AUTHORITY = (List<String>) hashMapAuth.get("T_AUTHORITY");
            }
            if (null != hashMapAuth.get("R_AUTHORITY")) {
                R_AUTHORITY = (List<String>) hashMapAuth.get("R_AUTHORITY");
            }
            QueryBuilder must_what = QueryBuilders.matchAllQuery();
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.must().add(must_what);
            for (int i = 0; i < K_AUTHORITY.size(); i++) {
                QueryBuilder mustNot_what = QueryBuilders.termQuery("KPI_Code", K_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }
            for (int i = 0; i < T_AUTHORITY.size(); i++) {
                QueryBuilder mustNot_what = QueryBuilders.termQuery("Subject_Code", T_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }
            for (int i = 0; i < R_AUTHORITY.size(); i++) {
                QueryBuilder mustNot_what = QueryBuilders.termQuery("Report_Code", R_AUTHORITY.get(i));
                boolQueryBuilder.mustNot().add(mustNot_what);
            }

            //对于tab影响的总数要考虑进去
            if(!CodeTable.getTabCodeValue(tabId).equals("全部")){
                QueryBuilder must_add = QueryBuilders.termQuery("Acct_Type",CodeTable.getTabCodeValue(tabId));
                boolQueryBuilder.must().add(must_add);
            }

            //boolQueryBuilder.minimumShouldMatch(1);

            //综合的DFS的sql
            SearchResponse response = client.prepareSearch(esIndex)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(boolQueryBuilder)
                    .get();
            SearchHits hits = response.getHits();
            return "" + hits.getTotalHits();
        }
    }

}
