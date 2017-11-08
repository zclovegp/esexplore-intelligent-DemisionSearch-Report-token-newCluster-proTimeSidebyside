package com.bonc.esexplore.fulltextsearch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bonc.esexplore.until.CombineProAndTime;
import com.bonc.esexplore.until.DimensionInit;
import com.bonc.esexplore.until.SetESConfig;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhaoc on 2017/9/8.
 */
public class DimensionSearch {

    public static String esDimensionIndex;
    public static String searchWordExceptDimension="";

    public static List<HashMap<String,Object>> dimensionSearch(String searchWord ,TransportClient client) throws IOException, ParseException {
        //初始化搜索词
        searchWordExceptDimension = searchWord.replace("省","").replace("市","");

        HashMap<String,String> propertiesMap = SetESConfig.getPropertiesES();
        esDimensionIndex = propertiesMap.get("esDimensionIndex");
        SearchRequestBuilder searchRequestBuilderTopic = client.prepareSearch(esDimensionIndex)
                .setTypes("dimension")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

        QueryBuilder qb = QueryBuilders.matchQuery("searchword",searchWord);
        boolQueryBuilder.must().add(qb);

        SearchResponse response = searchRequestBuilderTopic.setQuery(boolQueryBuilder)
                .setFrom(0)
                .setSize(500)
                .get();

        //对结果遍历放到list中
        SearchHits hits = response.getHits();
        long count = response.getHits().getTotalHits();

        System.out.println("匹配到维度的个数有" + count);
        //结构初始化
        HashMap<String,Object> resHm = DimensionInit.dimensionInit();
        List<HashMap<String,Object>> resList = (List<HashMap<String,Object>>)resHm.get("selectType");

        HashMap<Integer,HashMap<String,String>> offsetProCityMap = new HashMap<>();

        List<HashMap<String,String>> proAreaList = new ArrayList<>();

        for (int i = 0; i < hits.getHits().length; i++) {
            SearchHit searchHit = hits.getHits()[i];
            JSONObject jsonData = JSON.parseObject(searchHit.getSourceAsString());
            //渠道
            if(jsonData.get("dimensiontype").toString().equals("channel")){
                searchWordExceptDimension = searchWordExceptDimension.replace(jsonData.get("searchword").toString(),"");
                HashMap<String,Object> hmTmp = new HashMap<>();
                List<String> value = new ArrayList<>();
                //先添加旧的
                value.addAll((List<String>)resList.get(0).get("1"));
                //再添加新的
                value.add(jsonData.get("code").toString());
                hmTmp.put("1",value);
                resList.set(0,hmTmp);
            }
            //产品
            if(jsonData.get("dimensiontype").toString().equals("product")){
                searchWordExceptDimension = searchWordExceptDimension.replace(jsonData.get("searchword").toString(),"");
                HashMap<String,Object> hmTmp = new HashMap<>();
                List<String> value = new ArrayList<>();
                //先添加旧的
                value.addAll((List<String>)resList.get(1).get("2"));
                //再添加新的
                value.add(jsonData.get("code").toString());
                hmTmp.put("2",value);
                resList.set(1,hmTmp);
            }
            //业务
            if(jsonData.get("dimensiontype").toString().equals("service")){
                searchWordExceptDimension = searchWordExceptDimension.replace(jsonData.get("searchword").toString(),"");
                HashMap<String,Object> hmTmp = new HashMap<>();
                List<String> value = new ArrayList<>();
                //先添加旧的
                value.addAll((List<String>)resList.get(2).get("3"));
                //再添加新的
                value.add(jsonData.get("code").toString());
                hmTmp.put("3",value);
                resList.set(2,hmTmp);
            }
            //省份地市
            if(jsonData.get("dimensiontype").toString().equals("pro")){
                searchWordExceptDimension = searchWordExceptDimension.replace(jsonData.get("searchword").toString(),"");
                //该词在搜索词的下标索引
                int offset = searchWord.indexOf(jsonData.get("searchword").toString());
                HashMap<String,String> proCityMap = new HashMap<>();
                proCityMap.put("provId",jsonData.get("code").toString());
                proCityMap.put("cityId","-1");
                offsetProCityMap.put(offset,proCityMap);

            }
            if(jsonData.get("dimensiontype").toString().equals("area")){
                searchWordExceptDimension = searchWordExceptDimension.replace(jsonData.get("searchword").toString(),"");
                //该词在搜索词的下标索引
                int offset = searchWord.indexOf(jsonData.get("searchword").toString());
                HashMap<String,String> proCityMap = new HashMap<>();
                proCityMap.put("provId",jsonData.get("relationproid").toString());
                proCityMap.put("cityId",jsonData.get("code").toString());
                offsetProCityMap.put(offset,proCityMap);
            }
        }

        //还原省份和地市在原字符串中的顺序
        List<Map.Entry<Integer,HashMap<String,String>>> list = new ArrayList<Map.Entry<Integer,HashMap<String,String>>>(offsetProCityMap.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<Integer,HashMap<String,String>>>() {
            //升序排序
            public int compare(Map.Entry<Integer, HashMap<String,String>> o1,Map.Entry<Integer, HashMap<String,String>> o2) {
                return o1.getKey()-o2.getKey();
            }

        });
        System.out.println("排序后的省份地市是--->"+list);
        for(Map.Entry<Integer,HashMap<String,String>> mapping:list){
            proAreaList.add(mapping.getValue());
        }

        //时间正则（包含其它连接符号和光有年份的）
        //String eL = "((((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._年])(10|12|0?[13578])([-\\/\\._月])(3[01]|[12][0-9]|0?[1-9])([日 ]))|(((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._年])(11|0?[469])([-\\/\\._月])(30|[12][0-9]|0?[1-9])([日 ]))|(((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._年])(0?2)([-\\/\\._月])(2[0-8]|1[0-9]|0?[1-9])([日 ]))|(([2468][048]00)([-\\/\\._年])(0?2)([-\\/\\._月])(29)([日 ]))|(([3579][26]00)([-\\/\\._年])(0?2)([-\\/\\._月])(29)([日 ]))|(([1][89][0][48])([-\\/\\._年])(0?2)([-\\/\\._月])(29)([日 ]))|(([2-9][0-9][0][48])([-\\/\\._年])(0?2)([-\\/\\._月])(29)([日 ]))|(([1][89][2468][048])([-\\/\\._年])(0?2)([-\\/\\._月])(29)([日 ]))|(([2-9][0-9][2468][048])([-\\/\\._年])(0?2)([-\\/\\._月])(29)([日 ]))|(([1][89][13579][26])([-\\/\\._年])(0?2)([-\\/\\._月])(29)([日 ]))|(([2-9][0-9][13579][26])([-\\/\\._年])(0?2)([-\\/\\._月])(29)([日 ]))|([0-9]{3}[1-9]([年])(0[1-9]([月])|1[0-2]([月])|[1-9]([月]))|[0-9]{2}[1-9][0-9]{1}([年])(0[1-9]([月])|1[0-2]([月])|[1-9]([月]))|[0-9]{1}[1-9][0-9]{2}([年])(0[1-9]([月])|1[0-2]([月])|[1-9]([月]))|[1-9][0-9]{3}([年])(0[1-9]([月])|1[0-2]([月])|[1-9]([月])))|([0-9]{3}[1-9]([年])|[0-9]{2}[1-9][0-9]{1}([年])|[0-9]{1}[1-9][0-9]{2}([年])|[1-9][0-9]{3}([年])))";
        //只有年月日连接符号并且光有年份的去掉了
        String eL = "((((1[8-9]\\d{2})|([2-9]\\d{3}))([年])(10|12|0?[13578])([月])(3[01]|[12][0-9]|0?[1-9])([日]))|(((1[8-9]\\d{2})|([2-9]\\d{3}))([年])(11|0?[469])([月])(30|[12][0-9]|0?[1-9])([日]))|(((1[8-9]\\d{2})|([2-9]\\d{3}))([年])(0?2)([月])(2[0-8]|1[0-9]|0?[1-9])([日]))|(([2468][048]00)([年])(0?2)([月])(29)([日]))|(([3579][26]00)([年])(0?2)([月])(29)([日]))|(([1][89][0][48])([年])(0?2)([月])(29)([日]))|(([2-9][0-9][0][48])([年])(0?2)([月])(29)([日]))|(([1][89][2468][048])([年])(0?2)([月])(29)([日]))|(([2-9][0-9][2468][048])([年])(0?2)([月])(29)([日]))|(([1][89][13579][26])([年])(0?2)([月])(29)([日]))|(([2-9][0-9][13579][26])([年])(0?2)([月])(29)([日]))|([0-9]{3}[1-9]([年])(0[1-9]([月])|1[0-2]([月])|[1-9]([月]))|[0-9]{2}[1-9][0-9]{1}([年])(0[1-9]([月])|1[0-2]([月])|[1-9]([月]))|[0-9]{1}[1-9][0-9]{2}([年])(0[1-9]([月])|1[0-2]([月])|[1-9]([月]))|[1-9][0-9]{3}([年])(0[1-9]([月])|1[0-2]([月])|[1-9]([月]))))";
        Pattern p = Pattern.compile(eL);
        Matcher matcher = p.matcher(searchWord);
        List<String> dateList = new ArrayList();
        while(matcher.find()){
            String tmpDate = matcher.group(0).trim();
            boolean flag = tmpDate.contains("日");
            Date date=null;
            if (flag) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");//小写的mm表示的是分钟
                SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
                //先转换成日期
                date = sdf.parse(tmpDate);
                //在转换成字符串
                String str=sdf2.format(date);
                dateList.add(str);
                searchWordExceptDimension = searchWordExceptDimension.replace(matcher.group(0).toString(),"");
            }else{
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");//小写的mm表示的是分钟
                SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM");
                //先转换成日期
                date = sdf.parse(tmpDate);
                //在转换成字符串
                String str=sdf2.format(date);
                dateList.add(str);
                searchWordExceptDimension = searchWordExceptDimension.replace(matcher.group(0).toString(),"");
            }
            //dateList.add(matcher.group(0).trim());
            //去除原先搜索词中的
            //searchWordExceptDimension = searchWordExceptDimension.replace(matcher.group(0).toString(),"");
        }

        System.out.println("抛去所有维度后搜索词是--->"+searchWordExceptDimension);

        //将时间和省份地市维度进行组合
        List<HashMap<String, String>> splitDoc = CombineProAndTime.combineProAndTime(proAreaList, dateList);
        List<HashMap<String, Object>> resultList = new ArrayList<>();
        for (int i=0;i<splitDoc.size();i++){
            HashMap<String, String> tmpGetHm = splitDoc.get(i);
            HashMap<String, Object> tmpPutHm = new HashMap<>();
            tmpPutHm.put("provId",tmpGetHm.get("provId"));
            tmpPutHm.put("cityId",tmpGetHm.get("cityId"));
            tmpPutHm.put("date",tmpGetHm.get("date"));
            tmpPutHm.put("selectType",resList);
            resultList.add(tmpPutHm);
        }
        return resultList;
    }

}
