package com.bonc.esexplore.dataform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.search.SearchHit;

import java.util.HashMap;

/**
 * Created by zhaoc on 2017/5/17.
 */
public class DataForm {

    public static HashMap<String,Object> dataForm(String type,SearchHit data,HashMap<String, Integer> ratingMap){
        //指标
    if(type.equals("K")){
        HashMap<String,Object> tmpMap = new HashMap<>();
        JSONObject jsonData = JSON.parseObject(data.getSourceAsString());
        tmpMap.put("id",jsonData.get("KPI_Code").toString());
        tmpMap.put("title",jsonData.get("KPI_Name").toString());
        tmpMap.put("dayOrMonth",jsonData.get("Acct_Type").toString());
        tmpMap.put("type","指标");
        tmpMap.put("typeId","1");
        tmpMap.put("isMinus",jsonData.get("IS_MINUS").toString());
        tmpMap.put("ratings",ratingMap.get(jsonData.get("KPI_Code").toString())==null?0:ratingMap.get(jsonData.get("KPI_Code").toString()));
        return tmpMap;
        //专题
    }else if(type.equals("T")){
        HashMap<String,Object> tmpMap = new HashMap<>();
        JSONObject jsonData = JSON.parseObject(data.getSourceAsString());
        tmpMap.put("id",jsonData.get("Subject_Code").toString());
        tmpMap.put("title",jsonData.get("Subject_Name").toString());
        tmpMap.put("content",jsonData.get("Desc").toString());
        tmpMap.put("tabName",jsonData.get("Acct_Type").toString());
        tmpMap.put("type","专题");
        tmpMap.put("typeId","2");
        tmpMap.put("ratings",ratingMap.get(jsonData.get("Subject_Code").toString())==null?0:ratingMap.get(jsonData.get("Subject_Code").toString()));
        return tmpMap;
        //报告
    }else if(type.equals("R")){
        HashMap<String,Object> tmpMap = new HashMap<>();
        JSONObject jsonData = JSON.parseObject(data.getSourceAsString());
        tmpMap.put("id",jsonData.get("Report_Code").toString());
        tmpMap.put("title",jsonData.get("Report_Name").toString());
        tmpMap.put("tabName",jsonData.get("Acct_Type").toString());
        tmpMap.put("type","报告");
        tmpMap.put("typeId","3");
        tmpMap.put("ratings",ratingMap.get(jsonData.get("Report_Code").toString())==null?0:ratingMap.get(jsonData.get("Report_Code").toString()));
        return tmpMap;
    }else{
        HashMap<String,Object> hm = new HashMap<>();
        return hm;
        }
    }

}
