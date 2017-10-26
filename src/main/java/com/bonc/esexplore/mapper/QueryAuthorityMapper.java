package com.bonc.esexplore.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface QueryAuthorityMapper {
    List<HashMap<String,String>> getAuthority(HashMap<String, String> param);

    List<HashMap<String,String>> getChild(HashMap<String, Object> param);

    List<HashMap<String,String>> getChildById(HashMap<String, Object> param);

    List<HashMap<String,String>> getChildSubject(HashMap<String, Object> param);

    List<HashMap<String,String>> getChildSubjectById(HashMap<String, Object> param);

    List<HashMap<String,Object>> getKpi(HashMap<String, Object> param);

    List<HashMap<String,Object>> getSub(HashMap<String, Object> param);

    List<HashMap<String,Object>> getKpiCount(HashMap<String, Object> param);

    List<HashMap<String,Object>> getSubCount(HashMap<String, Object> param);
}
