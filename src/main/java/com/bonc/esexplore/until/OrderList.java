package com.bonc.esexplore.until;

import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/5/26.
 */
public class OrderList {
    public static List<HashMap<String,Object>> putOrder(List<HashMap<String,Object>> resultListFF,String startWhere){
        for(int i = 0;i<resultListFF.size();i++){
            resultListFF.get(i).put("ord",(i+Integer.parseInt(startWhere))+"");
        }
        return resultListFF;
    }
}
