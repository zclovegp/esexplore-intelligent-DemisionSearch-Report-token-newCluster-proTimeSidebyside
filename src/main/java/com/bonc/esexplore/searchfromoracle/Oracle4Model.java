package com.bonc.esexplore.searchfromoracle;

import com.bonc.esexplore.service.QueryAuthorityService;
import com.bonc.esexplore.until.OracleAuthorityAboutIn;
import com.bonc.esexplore.until.OrderList;
import com.bonc.esexplore.until.SecondOrder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/6/23.
 */
public class Oracle4Model {

    //指标类走oracle
    public static HashMap<String, Object> oracle4KPI(HashMap<String,Object> pid,List<HashMap<String, String>> child,String tabId,String userId,QueryAuthorityService queryAuthorityService,String from,String size,HashMap<String, Integer> ratingMap){
        List<HashMap<String, Object>> resultListOracle = new ArrayList<>();
        HashMap<String, Object> hmF = new HashMap<>();
        if (child.size()==1){
            System.out.println("搜索词输入的是一个id"+child);
        }else{
            System.out.println("搜索词输入的是一个汉字"+child);
        }
        List list = new ArrayList();
        for (int i=0;i<child.size();i++){
            list.add(child.get(i).get("LEVEL_ID").toString());
        }
        pid.put("pid_code",list);

        //tabId条件
        if (tabId.equals("-1")) {
        } else {
            //只是现在的数据库是这样写的日月标识
            pid.put("label_type", tabId.equals("1") ? "D" : "M");
        }

        //权限添加条件(in)
        List<String> li = OracleAuthorityAboutIn.addKpiAuth(userId, queryAuthorityService);
        //没有这个用户，直接空返回
        if (null == li) {
            hmF.put("data", new ArrayList<>());
            hmF.put("count", "0");
            return hmF;
        }
        //最高权限
        if (li.size() == 0) {
            li = null;
        }
        pid.put("kpiAuth", li);

        //用pid从oracle取数据，过滤权限，过滤日月标识，做分页
        List<HashMap<String, Object>> getKpiWhere = queryAuthorityService.getKpi(pid);
        List<HashMap<String, Object>> countLi = queryAuthorityService.getKpiCount(pid);
        String countOracle = countLi.get(0).get("count").toString();

        for (int i = 0; i < getKpiWhere.size(); i++) {
            HashMap<String, Object> tmpHm = getKpiWhere.get(i);
            HashMap<String, Object> re = new HashMap<>();
            re.put("id", tmpHm.get("KPI_CODE"));
            re.put("title", tmpHm.get("KPI_FULL_NAME"));
            re.put("dayOrMonth", tmpHm.get("LABEL_TYPE") == null ? "未知" : tmpHm.get("LABEL_TYPE").equals("D") ? "日报" : "月报");
            re.put("isMinus",tmpHm.get("IS_MINUS") == null ? "未知" : tmpHm.get("IS_MINUS"));
            re.put("type", "指标");
            re.put("typeId", "1");
            re.put("ratings",ratingMap.get(tmpHm.get("KPI_CODE"))==null?0:ratingMap.get(tmpHm.get("KPI_CODE")));
            resultListOracle.add(re);
        }
        List<HashMap<String, Object>> resultListOracleResult;
        List<HashMap<String, Object>> tmpList = SecondOrder.secondOrder(resultListOracle,Integer.parseInt(from)-1,Integer.parseInt(size),"");
        resultListOracleResult = OrderList.putOrder(tmpList,from);
        hmF.put("data", resultListOracleResult);
        hmF.put("count", countOracle);
        return hmF;
    }

    //专题类走oracle
    public static HashMap<String, Object> oracle4Subject(HashMap<String,Object> pid,List<HashMap<String, String>> child,String tabId,String userId,QueryAuthorityService queryAuthorityService,String from,String size,HashMap<String, Integer> ratingMap){
        List<HashMap<String, Object>> resultListOracle = new ArrayList<>();
        HashMap<String, Object> hmF = new HashMap<>();
        if (child.size()==1){
            System.out.println("搜索词输入的是一个id"+child);
        }else{
            System.out.println("搜索词输入的是一个汉字"+child);
        }
        List list = new ArrayList();
        for (int i=0;i<child.size();i++){
            list.add(child.get(i).get("LEVEL_ID").toString());
        }
        pid.put("pid_code",list);

        //tabId条件
        if (tabId.equals("-1")) {
        } else {
            //只是现在的数据库是这样写的日月标识
            pid.put("label_type", tabId);
        }

        //权限添加条件(in)
        List<String> li = OracleAuthorityAboutIn.addSubAuth(userId, queryAuthorityService);
        //没有这个用户，直接空返回
        if (null == li) {
            hmF.put("data", new ArrayList<>());
            hmF.put("count", "0");
            return hmF;
        }
        //最高权限
        if (li.size() == 0) {
            li = null;
        }
        pid.put("subAuth", li);

        //分页参数
        pid.put("from", from);
        int toStr = Integer.parseInt(from) + Integer.parseInt(size) - 1;
        pid.put("to", String.valueOf(toStr));

        //用pid从oracle取数据，过滤权限，过滤日月标识，做分页
        List<HashMap<String, Object>> getSubWhere = queryAuthorityService.getSub(pid);
        List<HashMap<String, Object>> countLi = queryAuthorityService.getSubCount(pid);
        String count = countLi.get(0).get("count").toString();

        for (int i = 0; i < getSubWhere.size(); i++) {
            HashMap<String, Object> tmpHm = getSubWhere.get(i);
            HashMap<String, Object> re = new HashMap<>();
            re.put("id", tmpHm.get("SUBJECT_CODE"));
            re.put("title", tmpHm.get("SUBJECT_NAME"));
            re.put("content", tmpHm.get("SUBJECT_DESC"));
            re.put("tabName", tmpHm.get("LABEL_TYPE") == null ? "未知" : tmpHm.get("LABEL_TYPE").equals("1") ? "日报" : "月报");
            re.put("type", "专题");
            re.put("typeId", "2");
            re.put("ratings",ratingMap.get(tmpHm.get("SUBJECT_CODE"))==null?0:ratingMap.get(tmpHm.get("SUBJECT_CODE")));
            resultListOracle.add(re);
        }

        List<HashMap<String, Object>> resultListOracleResult;
        List<HashMap<String, Object>> tmpList = SecondOrder.secondOrder(resultListOracle,Integer.parseInt(from)-1,Integer.parseInt(size),"");
        resultListOracleResult = OrderList.putOrder(tmpList,from);
        hmF.put("data", resultListOracleResult);
        hmF.put("count", count);
        return hmF;
    }

}
