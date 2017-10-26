package com.bonc.esexplore.until;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/5/19.
 */
public class TabFilter {

    public static List<HashMap<String,Object>> tabFilter(String tabId,List<HashMap<String,Object>> list){
        System.out.println("日月标识:"+CodeTable.getTabCodeValue(tabId));
        List<HashMap<String,Object>> resultList = new ArrayList<>();
        //如果是全部，不要过滤，直接返回
        if(CodeTable.getTabCodeValue(tabId).equals("全部")){
            return list;
        }

        for(int i=0;i<list.size();i++){
            String tmp;
            String tabIdValue;
            if(list.get(i).get("typeId").toString().equals("1")){
                tmp = list.get(i).get("dayOrMonth").toString();
                tabIdValue = CodeTable.getTabCodeValue(tabId);
            }else {
                 tmp = list.get(i).get("tabName").toString();
                 tabIdValue = CodeTable.getTabCodeValue(tabId);
            }
            if(!(null==tmp)) {
                 if (tmp.equals(tabIdValue)) {
                    resultList.add(list.get(i));
                }
            }
        }
        return resultList;
    }

}
