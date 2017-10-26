package com.bonc.esexplore.until;

import java.util.*;

/**
 * Created by zhaoc on 2017/8/2.
 */
//这里的from要-1
//flag:0在es做，不需要分页，1在oracle做，需要分页
public class SecondOrder {
    public static List<HashMap<String, Object>> secondOrder(List<HashMap<String, Object>> firstOrderList,int from,int size,String searchWord){
        List<HashMap<String, Object>> resultList = new ArrayList<>();

        //搜索词空，全部重排序，直接按照历史点击量(直接走oracle)
        if (searchWord.equals("")){
            Collections.sort(firstOrderList, new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> o1, HashMap<String,Object> o2) {
                    Integer o1Time = (int)o1.get("ratings");
                    Integer o2Time = (int)o2.get("ratings");
                    return o2Time.compareTo(o1Time);
                }
            });
            //分页
        if (from>firstOrderList.size()){
            return resultList;
        }else if((from+size)>firstOrderList.size()){
            resultList = firstOrderList.subList(from,firstOrderList.size());
            return resultList;
        }else{
            resultList = firstOrderList.subList(from,from+size);
            return resultList;
        }

        //搜索词不空的时候先将匹配的搜索字个数相同的分组，然后按照历史点击进行排序(旧)
        //只用es的排序(目前)
        }else{
            //if(firstOrderList.size()!=0){
            //    firstOrderList.get(0).put("ratings",999999999);
            //}
            //resultList = GroupBySearchWordAndSort.groupBySearchWordAndSort(firstOrderList,searchWord);
            return firstOrderList;
        }
    }
}
