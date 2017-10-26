package com.bonc.esexplore.until;

import com.bonc.esexplore.fulltextsearch.ESSearch;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by zhaoc on 2017/10/16.
 */
//读取properties文件
public class SetESConfig {
    public static HashMap<String,String> getPropertiesES(){
        HashMap<String,String> re = new HashMap();
            // 生成输入流(如果是class.getResourceAsStream，那么直接用classes的根目录)
            InputStream ins=ESSearch.class.getResourceAsStream("/conf/esconfig.properties");
            // 生成properties对象
            Properties p = new Properties();
            try {
                p.load(ins);
            } catch (Exception e) {
                e.printStackTrace();
            }  finally{
                try {
                    ins.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        re.put("esIndex",(String) p.get("es.index"));
        re.put("esLogIndex",(String) p.get("es.log.index"));
        re.put("esDimensionIndex",(String) p.get("es.dimension.index"));
        re.put("esClusterName",(String) p.get("es.cluster.name"));
        re.put("esClusterip1",(String) p.get("es.cluster.ip1"));
        re.put("esClusterip2",(String) p.get("es.cluster.ip2"));
        re.put("esClusterip3",(String) p.get("es.cluster.ip3"));
        return re;
    }
}
