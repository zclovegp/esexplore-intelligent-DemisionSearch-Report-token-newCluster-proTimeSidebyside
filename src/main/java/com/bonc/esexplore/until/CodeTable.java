package com.bonc.esexplore.until;

import java.util.HashMap;

/**
 * Created by zhaoc on 2017/5/19.
 */
public class CodeTable {
    public static String getTabCodeValue(String code){
        HashMap<String,String> hm = new HashMap<>();
        hm.put("-1","全部");
        hm.put("1","日报");
        hm.put("2","月报");
        return hm.get(code).toString();
    }

    public static String getSearchTypeCodeValue(String code){
        HashMap<String,String> hm1 = new HashMap<>();
        hm1.put("1","指标");
        hm1.put("2","专题");
        hm1.put("3","报告");
        hm1.put("999","综合");
        return hm1.get(code).toString();
    }
}
