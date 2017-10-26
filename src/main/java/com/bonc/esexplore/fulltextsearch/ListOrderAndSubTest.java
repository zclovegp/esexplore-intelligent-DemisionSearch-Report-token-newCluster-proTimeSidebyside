/*
package com.bonc.esexplore.fulltextsearch;

import java.util.*;

*/
/**
 * Created by zhaoc on 2017/8/3.
 *//*


*/
/*
    目前测试10万条数据返回时间为50ms一下,
    *只有数据量在100万条的时候返回结果才有
    * 让人难以接受的13s
*//*

public class ListOrderAndSubTest {
    public static void main(String[] args){
        List<HashMap<String,Object>> list = new ArrayList<>();
        for(int i = 0;i<100000;i++){
            HashMap<String,Object> tmp = new HashMap<>();
            tmp.put("name",""+i);
            tmp.put("age",(i+5));
            list.add(tmp);
        }
        long start = System.currentTimeMillis();

        Collections.sort(list, new Comparator<HashMap<String, Object>>() {
            @Override
            public int compare(HashMap<String, Object> o1, HashMap<String,Object> o2) {
                Integer o1Time = (int)o1.get("age");
                Integer o2Time = (int)o2.get("age");
                return o2Time.compareTo(o1Time);
            }
        });
        List<HashMap<String,Object>> result = list.subList(1000,1020);
        System.out.println("消耗时长为:"+(System.currentTimeMillis()-start)+"ms");
        System.out.println(result);
    }
}
*/
