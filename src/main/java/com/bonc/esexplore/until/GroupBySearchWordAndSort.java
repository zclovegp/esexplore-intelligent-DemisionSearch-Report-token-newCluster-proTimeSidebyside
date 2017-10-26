package com.bonc.esexplore.until;

import java.util.*;

/**
 * Created by zhaoc on 2017/8/17.
 */
public class GroupBySearchWordAndSort {
    public static List<HashMap<String, Object>> groupBySearchWordAndSort(List<HashMap<String, Object>> list,String searchWord){
        HashMap<Integer,List<HashMap<String,Object>>> result = new HashMap<>();
        List<HashMap<String,Object>> resultF = new ArrayList<>();
        //搜索字变HashMap
        char[] searWordArr = searchWord.toUpperCase().toCharArray();
        HashMap<String,String> searchHm = new HashMap<>();
        for (int i = 0;i<searWordArr.length;i++){
            searchHm.put(searWordArr[i]+"","1");
        }
        //分组
        for(int i = 0;i<list.size();i++){
            //每条记录的name去重找字
            char[] name1 = list.get(i).get("title").toString().toCharArray();
            HashMap<String,String> tmpCharHm = new HashMap<>();
            for (int m=0;m<name1.length;m++){
                tmpCharHm.put(name1[m]+"","");
            }

            //匹配词个数
            int count1 = 0;
            Iterator iter = tmpCharHm.entrySet().iterator();
            while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = entry.getKey().toString();
                if (null!=searchHm.get(key)&&searchHm.get(key).equals("1")){
                    count1 = count1 + 1;
                }
            }
            /*for (int j = 0;j<name1.length;j++){
                if (null!=searchHm.get(name1[j]+"")&&searchHm.get(name1[j]+"").equals("1")){
                    count1 = count1 + 1;
                }
            }*/

            if (null != result.get(count1)){
                List<HashMap<String,Object>> tmpList = result.get(count1);
                tmpList.add(list.get(i));
                result.put(count1,tmpList);
            }else{
                List<HashMap<String,Object>> tmpList = new ArrayList<>();
                tmpList.add(list.get(i));
                result.put(count1,tmpList);
            }

        }
        //System.out.println("分组后的结果"+result);
        //外层排序
        List<Map.Entry<Integer,List<HashMap<String,Object>>>> groupList = new ArrayList<Map.Entry<Integer,List<HashMap<String,Object>>>>(result.entrySet());
        Collections.sort(groupList, new Comparator<Map.Entry<Integer,List<HashMap<String,Object>>>>() {
            public int compare(Map.Entry<Integer,List<HashMap<String,Object>>> o1, Map.Entry<Integer,List<HashMap<String,Object>>> o2) {
                //匹配搜索字多的在前面
                return (o2.getKey() - o1.getKey());
            }
        });
        //System.out.println("外层排序的结果"+groupList);
        //内层排序
        for(int i=0;i<groupList.size();i++){
            List<HashMap<String, Object>> tmp = groupList.get(i).getValue();
            Collections.sort(tmp, new Comparator<HashMap<String, Object>>() {
                @Override
                public int compare(HashMap<String, Object> o1, HashMap<String,Object> o2) {
                    Integer o1Time = (int)o1.get("ratings");
                    Integer o2Time = (int)o2.get("ratings");
                    //相同匹配搜索字的历史点击高的在前
                    return o2Time.compareTo(o1Time);
                }
            });
            resultF.addAll(tmp);
        }
        //System.out.println("内层排序的结果"+resultF);

        return resultF;
    }
}
