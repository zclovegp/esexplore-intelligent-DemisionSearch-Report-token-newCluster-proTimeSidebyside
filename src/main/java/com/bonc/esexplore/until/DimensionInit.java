package com.bonc.esexplore.until;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/9/11.
 */
//维度查询的结构初始化
public class DimensionInit {
    public static HashMap<String,Object> dimensionInit(){
        HashMap<String,Object> resHm = new HashMap<>();
        List<HashMap<String,Object>> resList = new ArrayList<>();
        HashMap<String,Object> hmTmp = new HashMap<>();
        HashMap<String,Object> hmTmp2 = new HashMap<>();
        HashMap<String,Object> hmTmp3 = new HashMap<>();
        List<String> value = new ArrayList<>();
        hmTmp.put("1",value);
        resList.add(hmTmp);
        hmTmp2.put("2",value);
        resList.add(hmTmp2);
        hmTmp3.put("3",value);
        resList.add(hmTmp3);
        resHm.put("selectType",resList);
        resHm.put("provId","111");
        resHm.put("cityId","-1");
        return resHm;
    }
}
