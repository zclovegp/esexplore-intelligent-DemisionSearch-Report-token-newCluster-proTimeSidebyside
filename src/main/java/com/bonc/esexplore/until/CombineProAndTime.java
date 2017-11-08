package com.bonc.esexplore.until;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/11/3.
 */
public class CombineProAndTime {

    public static List<HashMap<String,String>> combineProAndTime(List<HashMap<String,String>> proAreaList,List<String> dateList){
        List<HashMap<String,String>> list = new ArrayList();
        list = proAreaList;
        if(list.size()==0){
            HashMap<String,String> tmpHm = new HashMap();
            tmpHm.put("provId","111");
            tmpHm.put("cityId","-1");
            list.add(tmpHm);
        }
        List<String> list2 = new ArrayList();
        list2 = dateList;
        if(list2.size()==0){
            list2.add("max");
        }
        List<List> list3 = new ArrayList();list3.add(list);list3.add(list2);
        int counterIndex = list3.size() - 1;
        int[] counter = {0, 0};

        List<HashMap<String,String>> re = new ArrayList();

        for (int i = 0; i < list.size() * list2.size(); i++) {
            HashMap<String, String> tmpProAreaGet = list.get(counter[0]);
            HashMap<String, String> tmpProAreaPut = new HashMap<>();
            String tmpDate = list2.get(counter[1]);
            tmpProAreaPut.put("date",tmpDate);
            tmpProAreaPut.put("provId",tmpProAreaGet.get("provId"));
            tmpProAreaPut.put("cityId",tmpProAreaGet.get("cityId"));
            re.add(tmpProAreaPut);
            handle(counter,counterIndex,list3);
        }
        //System.out.println("组合的维度是"+re);
        return re;
    }

    public static void handle(int [] counter,int counterIndex,List<List> list3) {
        counter[counterIndex]++;
        if (counter[counterIndex] >= list3.get(counterIndex).size()) {
            counter[counterIndex] = 0;
            counterIndex--;
            if (counterIndex >= 0) {
                handle(counter,counterIndex,list3);
            }
            counterIndex = list3.size() - 1;
        }
    }

}
