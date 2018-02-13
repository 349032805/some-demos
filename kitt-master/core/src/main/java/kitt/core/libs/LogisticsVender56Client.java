package kitt.core.libs;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitt.core.domain.Logisticsintention;
import kitt.core.libs.logistics.LogisticsVender56Response;
import kitt.core.libs.logistics.PurposeResponse;
import kitt.core.persistence.DataBookMapper;
import kitt.ext.WithLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

/**
 * Created by zhangbolun on 15/12/14.
 */
@Service
public class LogisticsVender56Client implements WithLogger {

    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private ObjectMapper objectMapper;

    private  final String userName="ym_api_getmytoken_name";
//    private  final String url56="http://ym.api.56kuaiche.com"; //for product
//    private  final String pwd="Hj1bdTBXttb$0uQ%&$^*Ddn$xTOPhdkiuy98fG^KkSM3b7zlFwP224HZt2xb*"; //for product
    private  @Value("${Vender56.url}") String url56;//for test
    private  @Value("${Vender56.pwd}") String pwd; //for test

    private  final String version="v1.0";
    private  String salt;
    private  String token;
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取生成口令与数字签名
     * @throws IOException
     */
    public void initTokenSalt() throws IOException{

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String tokenValidDate = dataBookMapper.getDataBookNameByTypeSequence("logisticsVender56token", 2);
        String saltValidDate = dataBookMapper.getDataBookNameByTypeSequence("logisticsVender56salt", 2);

        //判断token 是否过期
        LocalDateTime tokenDateTime=LocalDateTime.parse(tokenValidDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if(LocalDateTime.now().isAfter(tokenDateTime)){
            getToken();
        }else {
            this.token =  dataBookMapper.getDataBookNameByTypeSequence("logisticsVender56token",1);
        }
        //判断salt 是否过期
        LocalDateTime saltDateTime=LocalDateTime.parse(saltValidDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if(LocalDateTime.now().isAfter(saltDateTime)){
            getSalt();
        }else {
            this.salt  =  dataBookMapper.getDataBookNameByTypeSequence("logisticsVender56salt",1);
        }
    }

    public void getToken() throws IOException{
        LogisticsVender56Response vender56Response;
        HashMap<String, String> remap = new HashMap<String, String>();
        remap.put("userName", userName);
        remap.put("pwd", pwd);
        String responseStr = HttpRequest.sendPostRequestHttpClient(url56 + "/security/getmytoken", remap, "utf8") ;
        vender56Response = objectMapper.readValue(responseStr, LogisticsVender56Response.class);

        if (vender56Response.getCode() != 600020 || StringUtils.isBlank(vender56Response.getDatabody().getToken())) {
            logger().info("物流供应商56口令获取失败");
        } else {
            this.token = vender56Response.getDatabody().getToken();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            long tokenValidDate = vender56Response.getDatabody().getExpire();
            LocalDateTime tokenDateTime=LocalDateTime.parse(sdf.format(tokenValidDate).replace(' ','T'), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            dataBookMapper.updateDataBook(1,this.token,"logisticsVender56token");
            dataBookMapper.updateDataBook(2,tokenDateTime.toString(),"logisticsVender56token");
        }
    }

    public void getSalt() throws IOException{
        LogisticsVender56Response vender56Response;
        String param = "token=" + token + "&version=" + version;

        String url = url56+"/security/getmysalt";
        String result = HttpRequest.sendPost(url, param,"56");

        vender56Response = objectMapper.readValue(result, LogisticsVender56Response.class);
        if (vender56Response.getCode() != 600000 || StringUtils.isBlank(vender56Response.getDatabody().getSalt())) {
            logger().info("物流供应商56数据签名获取失败");
        } else {
            this.salt = vender56Response.getDatabody().getSalt();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            long saltValidDate = vender56Response.getDatabody().getExpire();
            LocalDateTime saltDateTime=LocalDateTime.parse(sdf.format(saltValidDate).replace(' ','T'), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            dataBookMapper.updateDataBook(1,this.salt,"logisticsVender56salt");
            dataBookMapper.updateDataBook(2,saltDateTime.toString(),"logisticsVender56salt");
        }
    }

    /**
     * 提交意向单
     */
    public PurposeResponse purpose(Logisticsintention intention,boolean isfirst) throws IOException {
        PurposeResponse purposeResponse= submit(intention,isfirst);
        if(purposeResponse.getCode()==-600000){
            getToken();
            getSalt();
            purposeResponse= submit(intention,isfirst);
        }
        System.out.println("-------提交意向单--------");
        System.out.println(purposeResponse);
        return purposeResponse;
    }

    private PurposeResponse submit(Logisticsintention intention,boolean isfirst) throws IOException{
        String shipperName="易煤网用户";
        String mobile="00000000000";
        if(!isfirst){
            shipperName=intention.getContacts();
            mobile=intention.getMobile();
        }
        String object = "" +
                "{souceId:'"+intention.getSouceId()+"'," +
                "shipperName:'"+shipperName+"'," +
                "loadProvince:'"+intention.getLoadProvince()+"'," +
                "loadCity:'"+intention.getLoadCity()+"'," +
                "loadCountry:'"+intention.getLoadCountry()+"'," +
                "loadAddDetail:'"+intention.getLoadAddDetail()+"'," +
                "unLoadProvince:'"+intention.getUnLoadProvince()+"'," +
                "unLoadCity:'"+intention.getUnLoadCity()+"'," +
                "unLoadCountry:'"+intention.getUnloadCountry()+"'," +
                "unLoadAddDetail:'"+intention.getUnLoadAddDetail()+"'," +
                "goodsType:'"+intention.getGoodsType()+"'," +
                "goodsWeight:"+intention.getGoodsWeight()+"," +
                "expectedPrice:"+ (intention.getPriceUp() == null ? Integer.valueOf(0) : intention.getPriceUp().doubleValue()) + "," +
                "mobile:'"+mobile+"'," +
                "token:'"+token+"'," +
                "version:'"+version+"'}" ;
        System.out.println("-------提交意向单 请求数据------:"+object);
        String paramencode =  URLEncoder.encode(object,"utf-8");
        String sign = md5(object+salt) ;
        String param = "data="+ paramencode +"&sign="+sign;
        String url = url56+"/contract/purpose";
        String result =  HttpRequest.sendPost(url ,param,"56");
        PurposeResponse purposeResponse = objectMapper.readValue(result, PurposeResponse.class);
        return purposeResponse;
    }


    /**
     * 变更意向单
     * @throws UnsupportedEncodingException
     */
    public  void modifypurpose() throws UnsupportedEncodingException {
        String object = "{souceId:'sfsfsf',shipperName:'货主姓名',loadAddress:'陕西省西安市'," +
                "loadAddDetail:'莲湖区',unLoadAddress:'卸车地址2',unLoadAddDetail:'卸车详细地址',goodsType:'大块煤'," +
                "goodsWeight:9800,priceDown:100.9,priceUp:355.23,mobile:'1455555555',token:'"+token+"'," +
                "version:'"+version+"'}" ;
        String paramencode =  URLEncoder.encode(object,"utf-8");
        String sign = md5(object+salt) ;
        String param = "data="+ paramencode +"&sign="+sign;
        String url = url56+"/contract/modifypurpose";
        String result =  HttpRequest.sendPost(url ,param,"56");
        System.out.println("-------变更意向单--------");
        System.out.println(result);
    }

    /**
     * 查询意向单状态
     * @throws UnsupportedEncodingException
     */
    public  void querypurpose () throws UnsupportedEncodingException {
        String object = "{souceId:'sfsfsf',token:'"+token+"', version:'"+version+"'}" ;
        String paramencode =  URLEncoder.encode(object,"utf-8");
        String sign = md5(object+salt) ;
        String param = "data="+ paramencode +"&sign="+sign;
        String url = url56+"/contract/querypurpose";
        String result =  HttpRequest.sendPost(url ,param,"56");
        System.out.println("-------查询意向单状态--------");
        System.out.println(result);
    }

    /**
     * 取消意向单状态
     * @throws UnsupportedEncodingException
     */
    public PurposeResponse cancelpurpose (String souceId,String remark) throws IOException {
        String object = "{souceId:'"+souceId+"',token:'"+token+"', version:'"+version+"',remark:'"+remark+"'}" ;
        String paramencode =  URLEncoder.encode(object,"utf-8");
        String sign = md5(object+salt) ;
        String param = "data="+ paramencode +"&sign="+sign;
        String url = url56+"/contract/cancelpurpose";
        String result =  HttpRequest.sendPost(url ,param,"56");
        PurposeResponse purposeResponse = objectMapper.readValue(result, PurposeResponse.class);
        System.out.println("-------取消意向单状态--------");
        System.out.println(result);
        logger.info("-------取消意向单状态-----request---"+object);
        logger.info("-------取消意向单状态-----response---"+result);

        return purposeResponse;
    }

    /**
     * 查询-平台车辆
     * @throws UnsupportedEncodingException
     */
    public void querycars() throws UnsupportedEncodingException{
        String object = "{carType:'0',pageNum:'1', pageSize:'10',token:'"+token+"',version:'"+version+"'}" ;
        String paramencode =  URLEncoder.encode(object,"utf-8");
        String sign = md5(object+salt) ;
        String param = "data="+ paramencode +"&sign="+sign;
        String url=url56+"/platformcar/querycars";
        String result =  HttpRequest.sendPost(url ,param,"56");
        System.out.println("-------查询-平台车辆--------");
        System.out.println(result);
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
