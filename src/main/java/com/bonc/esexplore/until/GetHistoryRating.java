package com.bonc.esexplore.until;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.HashMap;

/**
 * Created by zhaoc on 2017/8/2.
 */
//某一个用户对各个商品点击聚合
public class GetHistoryRating {
    public static HashMap<String,Integer> getHistoryRating(TransportClient client,String userId,String esLogIndex){
        SearchRequestBuilder searchRequestBuilderHistory = client.prepareSearch(esLogIndex)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //使用userid
        QueryBuilder must_what = QueryBuilders.matchQuery("UserID",userId);
        boolQueryBuilder.must().add(must_what);
        //对商品聚合
        AggregationBuilder aggregationSpecificMark =
                AggregationBuilders
                        .terms("SpecificMarkAgg").field("SpecificMark.keyword").order(Terms.Order.count(false)).size(1000);

        //boolQueryBuilder.must().add(must_what);

        SearchResponse resultFromEs = searchRequestBuilderHistory.setQuery(boolQueryBuilder)
                .addAggregation(aggregationSpecificMark)
                .setFrom(0)
                .setSize(0)
                .get();

        Terms hits = resultFromEs.getAggregations().get("SpecificMarkAgg");
        System.out.println("用户"+userId+"含有的历史商品个数是"+hits.getBuckets().size());
        HashMap<String,Integer> result = new HashMap<>();
        for (int i = 0; i < hits.getBuckets().size(); i++) {
            Terms.Bucket a = hits.getBuckets().get(i);
            //System.out.println("商品"+a.getKeyAsString()+",点击量"+a.getDocCount());
            result.put(a.getKeyAsString(), (int) a.getDocCount());
        }
        return result;
    }
}
