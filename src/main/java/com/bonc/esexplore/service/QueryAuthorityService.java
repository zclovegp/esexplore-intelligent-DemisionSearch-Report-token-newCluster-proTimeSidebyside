package com.bonc.esexplore.service;

import com.bonc.esexplore.mapper.QueryAuthorityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class QueryAuthorityService {
    @Autowired
    QueryAuthorityMapper queryAuthorityMapper;

    public List<HashMap<String,String>> getAuthority(HashMap<String,String> param){
        List<HashMap<String,String>> resultLit = new ArrayList<>();
        resultLit = queryAuthorityMapper.getAuthority(param);
        return resultLit;
    }

    public List<HashMap<String,String>> getChild(HashMap<String,Object> param){
        List<HashMap<String,String>> resultLit = new ArrayList<>();
        resultLit = queryAuthorityMapper.getChild(param);
        return resultLit;
    }

    public List<HashMap<String,String>> getChildById(HashMap<String,Object> param){
        List<HashMap<String,String>> resultLit = new ArrayList<>();
        resultLit = queryAuthorityMapper.getChildById(param);
        return resultLit;
    }

    public List<HashMap<String,String>> getChildSubject(HashMap<String,Object> param){
        List<HashMap<String,String>> resultLit = new ArrayList<>();
        resultLit = queryAuthorityMapper.getChildSubject(param);
        return resultLit;
    }

    public List<HashMap<String,String>> getChildSubjectById(HashMap<String,Object> param){
        List<HashMap<String,String>> resultLit = new ArrayList<>();
        resultLit = queryAuthorityMapper.getChildSubjectById(param);
        return resultLit;
    }

    public List<HashMap<String,Object>> getKpi(HashMap<String,Object> param){
        List<HashMap<String,Object>> resultLit = new ArrayList<>();
        resultLit = queryAuthorityMapper.getKpi(param);
        return resultLit;
    }

    public List<HashMap<String,Object>> getSub(HashMap<String,Object> param){
        List<HashMap<String,Object>> resultLit = new ArrayList<>();
        resultLit = queryAuthorityMapper.getSub(param);
        return resultLit;
    }

    public List<HashMap<String,Object>> getKpiCount(HashMap<String,Object> param){
        List<HashMap<String,Object>> resultLit = new ArrayList<>();
        resultLit = queryAuthorityMapper.getKpiCount(param);
        return resultLit;
    }

    public List<HashMap<String,Object>> getSubCount(HashMap<String,Object> param){
        List<HashMap<String,Object>> resultLit = new ArrayList<>();
        resultLit = queryAuthorityMapper.getSubCount(param);
        return resultLit;
    }

}
