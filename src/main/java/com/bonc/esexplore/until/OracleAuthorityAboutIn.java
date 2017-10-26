package com.bonc.esexplore.until;

import com.bonc.esexplore.service.QueryAuthorityService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zhaoc on 2017/6/2.
 */
public class OracleAuthorityAboutIn {
    public static List<String> addKpiAuth(String userId, QueryAuthorityService queryAuthorityService){
        QueryAuthority qa = new QueryAuthority();
        HashMap<String,Object> hashMapAuth = qa.filterAuthority(userId,queryAuthorityService);
        //用户不再权限表
        if(hashMapAuth.get("flag").equals("-1")){
            return null;
        }else{
            //有权限限制
            if (null!=hashMapAuth.get("K_AUTHORITY")){
                List<String> li = (List<String>) hashMapAuth.get("K_AUTHORITY");
                return li;
                //没有权限限制
            }else{
                return new ArrayList<>();
            }
        }
    }

    public static List<String> addSubAuth(String userId, QueryAuthorityService queryAuthorityService){
        QueryAuthority qa = new QueryAuthority();
        HashMap<String,Object> hashMapAuth = qa.filterAuthority(userId,queryAuthorityService);
        //用户不再权限表
        if(hashMapAuth.get("flag").equals("-1")){
            return null;
        }else{
            //有权限限制
            if (null!=hashMapAuth.get("T_AUTHORITY")){
                List<String> li = (List<String>) hashMapAuth.get("T_AUTHORITY");
                return li;
                //没有权限限制
            }else{
                return new ArrayList<>();
            }
        }
    }
}
