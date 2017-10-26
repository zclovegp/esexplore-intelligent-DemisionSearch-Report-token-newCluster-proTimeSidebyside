package com.bonc.esexplore.until;

import com.bonc.esexplore.service.QueryAuthorityService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/5/26.
 */
public class QueryAuthority {

    public HashMap<String,Object> filterAuthority(String userId,QueryAuthorityService queryAuthorityService) {
        List<HashMap<String, String>> resultList = new ArrayList<>();
        HashMap<String, String> paramT = new HashMap<>();
        paramT.put("userId",userId);
        try {
            resultList = queryAuthorityService.getAuthority(paramT);
        }catch (Exception e){
            System.out.println(e);
        }

        HashMap<String,Object> HmF = new HashMap<>();

        //用户不在权限表
        if(resultList.size()==0){
            System.out.println(userId+"用户不在权限表中");
            HmF.put("flag","-1");
        }

        //拥有最高权限
        if(resultList.size()==1&&resultList.get(0)==null){
            resultList = new ArrayList<>();
            HmF.put("flag","0");
            System.out.println("用户"+userId+"限制的权限是"+resultList);
        }

        //有权限限制
        if(resultList.size()==1){
            String[] K_AUTHORITY;
            String[] T_AUTHORITY;
            String[] R_AUTHORITY;
            if (null!=resultList.get(0).get("K_AUTHORITY")&&!(resultList.get(0).get("K_AUTHORITY").equals(""))){
                K_AUTHORITY = resultList.get(0).get("K_AUTHORITY").split(",");
                List<String> li = new ArrayList();
                for(int i=0;i<K_AUTHORITY.length;i++){
                    li.add(K_AUTHORITY[i]);
                }
                //只要get("K_AUTHORITY")不是null,那么里面就是有东西的
                HmF.put("K_AUTHORITY",li);
            }
            if (null!=resultList.get(0).get("T_AUTHORITY")&&!(resultList.get(0).get("T_AUTHORITY").equals(""))){
                T_AUTHORITY = resultList.get(0).get("T_AUTHORITY").split(",");
                List<String> li = new ArrayList();
                for(int i=0;i<T_AUTHORITY.length;i++){
                    li.add(T_AUTHORITY[i]);
                }
                HmF.put("T_AUTHORITY",li);
            }
            if (null!=resultList.get(0).get("R_AUTHORITY")&&!(resultList.get(0).get("R_AUTHORITY").equals(""))){
                R_AUTHORITY = resultList.get(0).get("R_AUTHORITY").split(",");
                List<String> li = new ArrayList();
                for(int i=0;i<R_AUTHORITY.length;i++){
                    li.add(R_AUTHORITY[i]);
                }
                HmF.put("R_AUTHORITY",li);
            }
            //有权限限制
            HmF.put("flag","1");
            System.out.println("用户"+userId+"限制的权限是"+resultList);
        }

        return HmF;
    }

}
