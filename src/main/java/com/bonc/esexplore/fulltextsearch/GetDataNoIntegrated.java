package com.bonc.esexplore.fulltextsearch;

import com.bonc.esexplore.dataform.DataForm;
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
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/5/16.
 */
public class GetDataNoIntegrated {

    public static String esIndex;

    public static List<HashMap<String,Object>> getData(TransportClient client,String field,String searchWord,String from,String size,HashMap<String,Object> hashMapAuth,String tabId,HashMap<String, Integer> ratingMap,List<HashMap<String, Object>> dimensionList){
        HashMap<String,String> propertiesMap = SetESConfig.getPropertiesES();
        esIndex = propertiesMap.get("esIndex");

        List<HashMap<String, Object>> liF;

        //最高权限用户，和普通可以查看
            if(hashMapAuth.get("flag").equals("0")||hashMapAuth.get("flag").equals("1")) {
                //专题类型的
                if (field.equals("2")) {
                    //当搜索词不为空的时候
                    if (!searchWord.equals("")) {
                        liF = NormalOrNothingGet.normalGet(client,"Subject_Name",searchWord,from,size,hashMapAuth,esIndex,"T","T_AUTHORITY","Subject_Code",tabId,ratingMap,dimensionList);
                        return liF;
                    } else {
                        liF = NormalOrNothingGet.nothingGet(client,from,size,hashMapAuth,esIndex,"T","T_AUTHORITY","Subject_Code",tabId,ratingMap,dimensionList);
                        return liF;
                    }

                    //指标类型的
                } else if (field.equals("1")) {
                    if (!searchWord.equals("")) {
                    liF = NormalOrNothingGet.normalGet(client,"KPI_Name",searchWord,from,size,hashMapAuth,esIndex,"K","K_AUTHORITY","KPI_Code",tabId,ratingMap,dimensionList);
                    return liF;
                    } else {
                    liF = NormalOrNothingGet.nothingGet(client,from,size,hashMapAuth,esIndex,"K","K_AUTHORITY","KPI_Code",tabId,ratingMap,dimensionList);
                    return  liF;
                    }

                    //报告类型的
                } else if (field.equals("3")) {
                    if (!searchWord.equals("")) {
                        liF = NormalOrNothingGet.normalGet(client,"Report_Name",searchWord,from,size,hashMapAuth,esIndex,"R","R_AUTHORITY","Report_Code",tabId,ratingMap,dimensionList);
                        return liF;
                    } else {
                        liF = NormalOrNothingGet.nothingGet(client,from,size,hashMapAuth,esIndex,"R","R_AUTHORITY","Report_Code",tabId,ratingMap,dimensionList);
                        return liF;
                    }

                } else {
                    List<HashMap<String, Object>> resultList = new ArrayList<>();
                    return resultList;
                }
                //在用户表中不存在的用户
            }else{
                List<HashMap<String, Object>> resultList = new ArrayList<>();
                return resultList;
            }
    }

    public static String getDataCount(TransportClient client,String field,String searchWord,HashMap<String,Object> hashMapAuth,String tabId){
        HashMap<String,String> propertiesMap = SetESConfig.getPropertiesES();
        esIndex = propertiesMap.get("esIndex");

        String count;

        //最高权限用户，均可以查看
        if(hashMapAuth.get("flag").equals("0")||hashMapAuth.get("flag").equals("1")) {
            //专题类型的
            if (field.equals("2")) {
                //当搜索词不为空的时候
                if (!searchWord.equals("")) {
                    count = NormalOrNothingGet.normalGetCount(client,"Subject_Name",searchWord,hashMapAuth,esIndex,"T","T_AUTHORITY","Subject_Code",tabId);
                    return count;
                } else {
                    count = NormalOrNothingGet.nothingGetCount(client,hashMapAuth,esIndex,"T","T_AUTHORITY","Subject_Code",tabId);
                    return count;
                }

                //指标类型的
            } else if (field.equals("1")) {
                if (!searchWord.equals("")) {
                    count = NormalOrNothingGet.normalGetCount(client,"KPI_Name",searchWord,hashMapAuth,esIndex,"K","K_AUTHORITY","KPI_Code",tabId);
                    return count;
                } else {
                    count = NormalOrNothingGet.nothingGetCount(client,hashMapAuth,esIndex,"K","K_AUTHORITY","KPI_Code",tabId);
                    return  count;
                }

                //报告类型的
            } else if (field.equals("3")) {
                if (!searchWord.equals("")) {
                    count = NormalOrNothingGet.normalGetCount(client,"Report_Name",searchWord,hashMapAuth,esIndex,"R","R_AUTHORITY","Report_Code",tabId);
                    return count;
                } else {
                    count = NormalOrNothingGet.nothingGetCount(client,hashMapAuth,esIndex,"R","R_AUTHORITY","Report_Code",tabId);
                    return count;
                }

            } else {
                return "0";
            }
            //在用户表中不存在的用户
        }else{
            return "0";
        }
    }

}
