package com.bonc.esexplore.until;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/11/3.
 */
public class OneBecomeMore {
    public static List<HashMap<String, Object>> oneBecomeMore(List<HashMap<String, Object>> dataList,List<HashMap<String, Object>> dimensionList){
        List<HashMap<String, Object>> resultList = new ArrayList<>();
        System.out.println("dimensionList--->是"+dimensionList);
        //现在是指标在外层循环，因为是展示相同指标的不同维度
        for(int i=0;i<dataList.size();i++){
            HashMap<String, Object> tmpDataHm = (HashMap<String, Object>) dataList.get(i).clone();
            if(tmpDataHm.get("typeId").equals("1")) {
                for (int j = 0; j < dimensionList.size(); j++) {
                    HashMap<String, Object> tmpDimHm = (HashMap<String, Object>) dimensionList.get(j).clone();
                    //注意地址问题
                    HashMap<String,Object> tTmpDataHm = (HashMap<String,Object>)tmpDataHm.clone();
                    tTmpDataHm.put("dimension", tmpDimHm);
                    resultList.add(tTmpDataHm);
                }
            }else{
                resultList.add(tmpDataHm);
            }
        }
        //System.out.println("拼接后的结果是-----"+resultList);
        return resultList;
    }
}
