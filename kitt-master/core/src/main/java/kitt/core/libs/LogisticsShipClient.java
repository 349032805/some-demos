package kitt.core.libs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import kitt.core.libs.logistics.CancelShip;
import kitt.core.libs.logistics.ShipQuery;
import kitt.core.libs.logistics.ShipRet;
import kitt.core.libs.logistics.SubmitShipInfo;
import kitt.core.util.JsonMapper;
import kitt.ext.WithLogger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by zhangbolun on 16/1/9.
 */
@Service
public class LogisticsShipClient implements WithLogger {
    private final static String sign="69d4bb2dbeb439dcb5f0d94f815ba212";
    private final static String partner="ymwpartner";
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ObjectMapper objectMapper;
    private @Value("${VenderShip.url}")String shipurl;//for test


    public void testGet(){
        String url=shipurl+"/api/test/get";
        String param = "partner=" + partner + "&sign=" + sign;
        String testGetresult = HttpRequest.sendGet(url, param);
        System.out.println("--------testGet--------");
        System.out.println("result:"+testGetresult);
    }

    public void testPost(){
        String url=shipurl+"/api/test/post?partner="+partner+"&sign="+sign;
        String testPostresult = HttpRequest.sendPost(url,"","ship");
        System.out.println("--------testPost--------");
        System.out.println("result:"+testPostresult);
    }


    public String shipquery(ShipQuery shipQuery) throws IOException{
         Gson gson=new Gson();
        System.out.println("-------外部接口数据，船东查询接口--------:");
        String url = shipurl+"/api/YMW_QueryShipListInfo/Post?partner="+partner+"&sign="+sign;
        logger().info("-------外部接口数据，船东查询接口--------:");
        logger().info(url);
        //String data1="{'Page':" +shipQuery.getPage()+
        //        ",'PageSize':" +shipQuery.getPageSize()+
        //        ",'LoadPortName':" +shipQuery.getLoadPortName()+
        //        ",'LowerTon':" +(shipQuery.getLowerTon()==0?null:shipQuery.getLowerTon())+
        //        ",'UpperTon':" +(shipQuery.getUpperTon()==0?null:shipQuery.getUpperTon())+
        //        ",'SortNum':" +shipQuery.getSortNum()+
        //        ",'SortIdent':" +shipQuery.getSortIdent()+
        //        ",'BeginDate':" +shipQuery.getBeginDate()+
        //        ",'EndDate':" +shipQuery.getEndDate()+
        //        "}";
        String data =gson.toJson(shipQuery);
        //System.out.println("request body json-----1:"+data1);
        System.out.println("request body json-----2:"+data);
        logger().info("request body json-----2:"+data);
        String result =  HttpRequest.sendPost(url, data, "ship");
        System.out.println("result json:"+result);
        logger().info("result json:"+result);
        return result;
    }

    public ShipRet submitInfo(SubmitShipInfo submitShipInfo) throws IOException{
        System.out.println("-------外部接口数据，发送货盘信息接口--------:");
        logger().info("-------外部接口数据，发送货盘信息接口--------:");
        String url = shipurl+"/api/YMW_GoodsListInfoAdd/Post?partner="+partner+"&sign="+sign;
        logger().info(url);
        String data = JsonMapper.nonDefaultMapper().toJson(submitShipInfo);
        System.out.println("request body json:"+data);
        logger().info("request body json:"+data);
        String result =  HttpRequest.sendPost(url, data, "ship");
        System.out.println("result json:"+result);
        logger().info("result json:"+result);
        ShipRet shipRet = objectMapper.readValue(result, ShipRet.class);
        System.out.println("result to class:"+shipRet.toString());
        logger().info("result to class:"+shipRet.toString());
        return shipRet;
    }

    public ShipRet cancelInfo(CancelShip cancelShip) throws IOException{
        System.out.println("-------外部接口数据，取消货盘信息接口--------:");
        logger().info("-------外部接口数据，取消货盘信息接口--------:");
        String url = shipurl+"/api/YMW_GoodsListCancel/Post?partner="+partner+"&sign="+sign;
        logger().info(url);
        String data = JsonMapper.nonDefaultMapper().toJson(cancelShip);
        System.out.println("request body json:"+data);
        logger().info("request body json:"+data);
        String result =  HttpRequest.sendPost(url ,data,"ship");
        System.out.println("result json:"+result);
        logger().info("result json:"+result);
        ShipRet shipRet = objectMapper.readValue(result, ShipRet.class);
        System.out.println("result to class:"+shipRet.toString());
        logger().info("result to class:"+shipRet.toString());
        return shipRet;
    }


    /**
     * md5加密
     * @param text
     * @return
     */
    static String md5(String text) {
        byte[] bts;
        try {
            bts = text.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bts_hash = md.digest(bts);
            StringBuffer buf = new StringBuffer();
            for (byte b : bts_hash) {
                buf.append(String.format("%02X", b & 0xff));
            }
            return buf.toString();
        } catch (java.io.UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }


}
