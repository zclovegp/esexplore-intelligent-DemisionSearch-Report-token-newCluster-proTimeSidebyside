package com.bonc.esexplore.fulltextsearch;

import com.bonc.esexplore.searchfromoracle.Oracle4Model;
import com.bonc.esexplore.service.QueryAuthorityService;
import com.bonc.esexplore.until.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.xpack.client.PreBuiltXPackTransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@Api(value="ES",description="ES搜索测试")
@RequestMapping("/es")
public class ESSearch {

    public static String esClusterName=null;

    public static String esLogIndex=null;

    public static String esClusterip1=null;

    public static String esClusterip2=null;

    public static String esClusterip3=null;

    //连接对象
    private static TransportClient client = null;
    /**
     * 建立连接
     **/
    public static void getESClient() throws UnknownHostException {
        setESConf();
        Settings settings = Settings.builder()
                .put("cluster.name",esClusterName)
                .put("client.transport.sniff", true)
                .put("xpack.security.user", "yz_dw3_pro:INS8_evdj_55BV")
                .build();
        if(client == null) {
            client = new PreBuiltXPackTransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esClusterip1), 9300))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esClusterip2), 9300))
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(esClusterip3), 9300));
        }
    }

    @Autowired
    QueryAuthorityService queryAuthorityService;
    /*
     * 默认是返回结果返回些什么（match_all）
     * 还有就是如果传过来的是一个指标类或者专题类的id直接去oracle拿数据
     * */
    @ApiOperation("调用搜索参数")
    @PostMapping("/explore")
    public Object search(@RequestBody @ApiParam("参数") String param) throws IOException {
        param = URLDecoder.decode(param, "UTF-8");
        System.out.println("接收到的参数有:"+param);
        String[] paramArray = param.split(",");
        if(paramArray.length==6){
            //参数解析(用户id，域名，搜索词，日月全部筛选条件，分页起始，分页大小)
            String userId = paramArray[0].trim();
            String field = paramArray[1].trim();
            String searchWord = paramArray[2].trim();
            String tabId = paramArray[3].trim();
            String from = paramArray[4].trim();
            String size = paramArray[5].trim();
            HashMap<String,Object> pid = new HashMap<>();
            pid.put("pid",searchWord);
            List<HashMap<String, String>> child=new ArrayList<>();
            //有没有可能是指标类
            if(field.equals("1")) {
                //如果用户输入的是汉字，那么child会有结果,但是不一定是一个
                child = queryAuthorityService.getChild(pid);
                //如果用户输入的是id,那么child会有结果,一定是一个
                if(child.size()==0){
                    child = queryAuthorityService.getChildById(pid);
                }
            }
            //有没有可能是专题类
            if(field.equals("2")) {
                //如果用户输入的是汉字，那么child会有结果,但是不一定是一个
                child = queryAuthorityService.getChildSubject(pid);
                //如果用户输入的是id,那么child会有结果,一定是一个
                if(child.size()==0){
                    child = queryAuthorityService.getChildSubjectById(pid);
                }
            }
            //在维度索引中进行查询
            HashMap<String, Object> dimensionMap = DimensionSearch.dimensionSearch(searchWord, client);

            //这里要把用户历史的点击量也考虑进来
            HashMap<String, Integer> ratingMap = GetHistoryRating.getHistoryRating(client, userId, esLogIndex);

            //这里不考虑权限，判断是在oracle做的事情后进去再考虑权限
            if (!((child.size()!=0&&field.equals("1"))||(child.size()!=0&&field.equals("2")))) {
                List<HashMap<String, Object>> resultList;
                List<HashMap<String, Object>> resultListF;
                HashMap<String,Object> hashMapAuth;
                List<HashMap<String, Object>> resultListFFF;
                QueryAuthority qa = new QueryAuthority();
                HashMap<String, Object> hmF = new HashMap<>();
                //1 指标,2 专题,3 报告查询用这个
                if (field.equals("1") || field.equals("2") || field.equals("3")) {
                    //判断用户是否在表中，如果在表中有什么东西不能看
                    hashMapAuth = qa.filterAuthority(userId,queryAuthorityService);
                    //拿数据
                    resultList = GetDataNoIntegrated.getData(client, field, searchWord, from, size, hashMapAuth, tabId, ratingMap);
                    //根据历史点击量重排序+分页
                    resultListF = SecondOrder.secondOrder(resultList,Integer.parseInt(from)-1,Integer.parseInt(size),searchWord);
                    //putOrder
                    resultListFFF = OrderList.putOrder(resultListF,from);
                    String count = GetDataNoIntegrated.getDataCount(client, field, searchWord, hashMapAuth,tabId);
                    hmF.put("data", resultListFFF);
                    hmF.put("count", count);
                    hmF.put("dimension",dimensionMap);
                    return hmF;
                    //综合查询用这个
                } else {
                    //判断用户是否在表中，如果在表中有什么东西不能看
                    hashMapAuth = qa.filterAuthority(userId,queryAuthorityService);
                    String count = "0";
                    //用户是在表中的
                    if (!hashMapAuth.get("flag").equals("-1")) {
                        //拿数据
                        resultList = GetDataIntegrated.getData(client, searchWord, from, size, hashMapAuth, tabId, ratingMap);
                        //根据历史点击量重排序+分页
                        resultListF = SecondOrder.secondOrder(resultList,Integer.parseInt(from)-1,Integer.parseInt(size),searchWord);
                        //putOrder
                        resultListFFF = OrderList.putOrder(resultListF,from);
                        count = GetDataIntegrated.getCount(client, searchWord, hashMapAuth, tabId);
                    }else{
                        resultListFFF=new ArrayList<>();
                    }
                    hmF.put("data", resultListFFF);
                    hmF.put("count", count);
                    hmF.put("dimension",dimensionMap);
                    return hmF;
                }
                //直接从oracle进行提数
            }else {
                //指标类从oracle直接拿
                if (field.equals("1")){
                    System.out.println("这条数据是从oracle直接读取的----指标");
                    HashMap<String, Object> hmF = Oracle4Model.oracle4KPI(pid,child,tabId,userId,queryAuthorityService,from,size,ratingMap);
                    hmF.put("dimension",dimensionMap);
                return hmF;

                //专题类直接从oracle拿
              }else if (field.equals("2")){
                    System.out.println("这条数据是从oracle直接读取的----专题");
                    HashMap<String, Object> hmF = Oracle4Model.oracle4Subject(pid,child,tabId,userId,queryAuthorityService,from,size,ratingMap);
                    hmF.put("dimension",dimensionMap);
                    return hmF;
                }
                //不会走到这里...
                return null;
            }
        }else {
            return "参数个数不正确...";
        }
    }

    public static void setESConf(){
        HashMap<String,String> propertiesMap = SetESConfig.getPropertiesES();
        esLogIndex = propertiesMap.get("esLogIndex");
        esClusterName = propertiesMap.get("esClusterName");
        esClusterip1 = propertiesMap.get("esClusterip1");
        esClusterip2 = propertiesMap.get("esClusterip2");
        esClusterip3 = propertiesMap.get("esClusterip3");
    }

}