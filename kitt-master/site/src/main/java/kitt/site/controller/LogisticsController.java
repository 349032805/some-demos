package kitt.site.controller;


import com.google.gson.Gson;
import kitt.core.domain.*;
import kitt.core.libs.LogisticsVender56Client;
import kitt.core.persistence.*;
import kitt.core.service.MessageNotice;
import kitt.core.util.AuthMethod;
import kitt.core.util.EmojiFilter;
import kitt.core.util.ToolsMethod;
import kitt.site.basic.annotation.Client;
import kitt.site.service.Auth;
import kitt.site.service.BeanValidators;
import kitt.site.service.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lich on 15/12/17.
 */
@Controller
public class LogisticsController {
    @Autowired
    private RegionMapper regionMapper;
    @Autowired
    private LogisticsMapper logisticsMapper;
    @Autowired
    private LogisticsvenderMapper logisticsvenderMapper;
    @Autowired
    private LogisticsfeedbackMapper logisticsfeedbackMapper;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private LogisticsVender56Client vender56Client;
    @Autowired
    private Session session;
    @Autowired
    private Auth auth;
    @Autowired
    private AuthMethod authMethod;
    @Autowired
    private ToolsMethod toolsMethod;
    @Autowired
    private ValidMapper validMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ShipMapper shipMapper;
    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());



    //进入意向单确认页面
    @RequestMapping(value="/account/toConfirmLogistics", method = RequestMethod.POST)
    public String toConfirmLogistics(@Client ClientInfo clientInfo,
                                     Logisticsintention logisticsintention, Map<String, Object> model,HttpServletResponse response){
//        checkCompany();
        //确认页面加载数据
        List<District> provinceList=regionMapper.getAllProvinces();
//        model.put("goodstype",getGoodsType());  //货物类型
        model.put("provinceList", provinceList);  //省
        List<District> moldPList=regionMapper.getDistinctMold(1,null);
        if(moldPList!=null&&moldPList.size()>0){
            for(District d:moldPList){
                d.setRegionList(regionMapper.getRegionsByMold(d.getMold(),null, 1));
            }
        }
        model.put("provinceList",moldPList);
//        logisticsintention.setVenderid(1);     //待定
//        logisticsintention.setVendername("56快车");  //待定
        if (session != null && session.getUser() != null) {
            if (session.getUser().getSecurephone().equals(logisticsintention.getMobile().trim())) {
                model.put("mobileEquals", true);
            } else {
                model.put("mobileEquals", false);
            }
        }
        model.put("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(logisticsintention.getMobile(), clientInfo.getIP()));
        model.put("logisticsintention",logisticsintention);

        response.addHeader("Cache-Control", "no-store, must-revalidate");
        response.addHeader("Expires", "Thu, 01 Jan 1970 00:00:01 GMT");

        return "logistics/confirmLogistic";
    }

    /**
     * 发送短信验证码 ( 未登录的用户, 或者已登录的用户, 但是 填写的手机号不是注册手机号)
     * @param securephone                手机号
     * @param imagecode                  图片验证码
     */
    @RequestMapping("/logistics/sendSmsCode")
    @ResponseBody
    public Object doSendLogisticsSmsCode(@Client ClientInfo clientInfo,
                                         @RequestParam(value = "mobile", required = true) String securephone,
                                         @RequestParam(value = "imagecode", required = false, defaultValue = "") String imagecode) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        if (!authMethod.checkTodaySMSCodeTimesByPhoneAndIP(securephone, clientInfo.getIP()) && StringUtils.isBlank(imagecode)) {
            return toolsMethod.setMapMessage(map, false, "请输入图片验证码");
        }
        if (!auth.doCheckImageCodeTimesAndRight(securephone, imagecode, clientInfo.getIP())) {
            return toolsMethod.setMapMessage(map, false, "图片验证码错误");
        }
        return authMethod.sendSMSCodeNOCheckPhoneExist(auth.getSessionUserId(), securephone, ValidateType.logistics, clientInfo.getIP());
    }

    //保存意向单
    @RequestMapping("/account/saveLogistics")
    @ResponseBody
    public Object saveLogistics(Logisticsintention logisticsintention,
                                @RequestParam(value = "smscode", required = false)String smscode) {
        MapObject map = new MapObject();
        map.setSuccess(true);
        if (!session.isLogined() || (session.isLogined() && !session.getUser().getSecurephone().equals(logisticsintention.getMobile()))) {
            Phonevalidator phonevalidator = validMapper.findVerifyCode(logisticsintention.getMobile(), smscode, ValidateType.logistics);
            map = authMethod.doCheckCodeNOCheckPhoneExist(logisticsintention.getMobile(), phonevalidator);
        }
        if (map.isSuccess()) {
            //检查公司信息是否完整
            BeanValidators.validateWithException(logisticsintention);
            logisticsintention.setLoadAddDetail(EmojiFilter.filterEmoji(logisticsintention.getLoadAddDetail()));
            logisticsintention.setUnLoadAddDetail(EmojiFilter.filterEmoji(logisticsintention.getUnLoadAddDetail()));
            logisticsintention.setVenderid(1);     //待定
            logisticsintention.setVendername("56快车");  //待定
            if (session != null && session.getUser() != null) {
                logisticsintention.setUserid(session.getUser().getId());
            } else {
                User user = userMapper.getUserByPhone(logisticsintention.getMobile());
                if (user != null) logisticsintention.setUserid(user.getId());
            }
            logisticsintention.setType(0);
            logisticsintention.setCreatetime(LocalDateTime.now());
            logisticsintention.setStatus(LogisticsStatus.TREATED_NOT.toString());
            logisticsMapper.addLogisticsintentionC(logisticsintention);
            MessageNotice.LOGISTICSSUBMIT.noticeUser(logisticsintention.getMobile()); //发短信提醒用户
            map.setSuccess(true);
        }
        return map;
    }


    @RequestMapping("/account/sendLogisticsSuccess")
    public String sendLogisticsSuccess(Map<String, Object> model){
        return "logistics/logisticSuccess";
    }





    //进入物流首页
    @RequestMapping("/logistics/toLogistics")
    public String toLogistics(Map<String, Object> model){
        List<District> provinceList=regionMapper.getAllProvinces();
//        model.put("goodstype",getGoodsType());  //货物类型
        model.put("provinceList", provinceList);  //省
        List<District> moldPList=regionMapper.getDistinctMold(1,null);
        if(moldPList!=null&&moldPList.size()>0){
            for(District d:moldPList){
                d.setRegionList(regionMapper.getRegionsByMold(d.getMold(),null, 1));
            }
        }
        if(session!=null&&session.getUser()!=null){
            model.put("telephone",session.getUser().getSecurephone());
        }
        model.put("provinceList",moldPList);
        //供应商下拉
        //model.put("supplier",logisticsvenderMapper.findAllLogisticsvender()) ;
        //成交纪录
        //model.put("countSuc",logisticsMapper.countSuccessIntention());
        List<Logisticsintention> sucList= logisticsMapper.findSuccessIntention();
        if(sucList!=null&&sucList.size()>0){
            for(Logisticsintention logisticsintention:sucList){
                if(logisticsintention.getType()==2){
                    Shipintention shipintention= shipMapper.getShipIntenttionById(logisticsintention.getShipid());
                    logisticsintention.setLoadProvince(shipintention.getLoadport());
                    logisticsintention.setUnLoadProvince(shipintention.getUnloadport());
                }
            }
        }
        model.put("sucList",sucList);
        model.put("severtime", LocalDate.now().plusDays(1));

        List<District> molds=shipMapper.findMolds();
        if(molds!=null&&molds.size()>0){
            for(District d:molds){
                d.setRegionList(shipMapper.findByMold(d.getMold()));
            }
        }
        model.put("ports",molds);
        model.put("portsList",shipMapper.findAllPorts(2));
        return "logistics/logisticIndex";
    }

//    public Map getGoodsType(){
//        Map<String,Object> map=new HashMap<String,Object>();
//        map.put("原煤","原煤");
//        map.put("焦炭","焦炭");
//        map.put("块煤", "块煤");
//        map.put("煤泥", "煤泥");
//        map.put("沫煤", "沫煤");
//        map.put("混煤", "混煤");
//        return map;
//    }

    //获取所有的省
    @RequestMapping("/logistics/getAllProvinces")
    @ResponseBody
    public Object getAllProvinces(){
        Map<String,Object> map=new HashMap<String,Object>();
        List<District> moldPList=regionMapper.getDistinctMold(1,null);
        if(moldPList!=null&&moldPList.size()>0){
            for(District d:moldPList){
                d.setRegionList(regionMapper.getRegionsByMold(d.getMold(),null, 1));
            }
        }
        map.put("provinceList",moldPList);
        return map;
    }

    //根据code获取省下面的市
    @RequestMapping("/logistics/getCitysByParent")
    @ResponseBody
    public Object getCitysByParent(@RequestParam(value = "code", required = true)String code){
        Map<String,Object> map=new HashMap<String,Object>();
        List<District> moldPList=regionMapper.getDistinctMold(2,code);
        if(moldPList!=null&&moldPList.size()>0){
            for(District d:moldPList){
                d.setRegionList(regionMapper.getRegionsByMold(d.getMold(),code, 2));
            }
        }
        map.put("cityList",moldPList);
        return map;
    }


    //根据code获取市下面的区
    @RequestMapping("/logistics/getCountrysByParent")
    @ResponseBody
    public Object getCountrysByParent(@RequestParam(value = "code", required = true)String code){
        Map<String,Object> map=new HashMap<String,Object>();
        List<District> moldPList=regionMapper.getDistinctMold(3,code);
        if(moldPList!=null&&moldPList.size()>0){
            for(District d:moldPList){
                d.setRegionList(regionMapper.getRegionsByMold(d.getMold(),code,3));
            }
        }
        map.put("countryList",moldPList);
        return map;
    }


    @RequestMapping(value="/logistics/webServiceGetTest",method = RequestMethod.GET,produces = "text/json;charset=utf-8")
    @ResponseBody
    public Object webServiceGetTest(){
        Gson gson=new Gson();
        return gson.toJson("ok");
    }

    @RequestMapping(value="/logistics/webServicePostTest",method = RequestMethod.POST,produces = "text/json;charset=utf-8")
    @ResponseBody
    public Object webServicePostTest(){
        Gson gson=new Gson();
        return gson.toJson("ok");
    }

    //56推送取消订单接口
    @RequestMapping(value="/logistics/pushCancel",method = RequestMethod.POST,produces = "text/json;charset=utf-8")
    @ResponseBody
    public Object pushCancel(@RequestParam(value = "data", required = true)String data, @RequestParam(value = "sign", required = false, defaultValue = "")String sign) throws IOException{
        Gson gson=new Gson();
        PushCancelRequest request=new PushCancelRequest();
        logger.info("--56推送取消订单接口--data:"+data);
        PushStatusResponse response=new PushStatusResponse();
        try {
            request = gson.fromJson(data, PushCancelRequest.class);
        }catch (Exception e){
            response.setCode(-600002);
            response.setMessage("请求参数格式错误");
            logger.info("--56推送取消订单接口--请求参数格式错误");
            return  gson.toJson(response);
        }

        if (StringUtils.isBlank(request.getSouceId()) || StringUtils.isBlank(request.getToken())||StringUtils.isBlank(request.getVersion())) {
            response.setCode(-600001);
            response.setMessage("非空字段有空值");
            logger.info("--56推送取消订单接口--非空字段有空值");
            return  gson.toJson(response);
        }

//        vender56Client.initTokenSalt();
        String tk = dataBookMapper.getDataBookNameByTypeSequence("logisticsVender56token", 1);
        if (!request.getToken().equals(tk)) {
            response.setCode(-600000);
            response.setMessage("非法请求");
            logger.info("--56推送取消订单接口--非法请求--token"+request.getToken());
            return gson.toJson(response);
        }

        Logisticsintention logisticsintention = logisticsMapper.findBySouceId(request.getSouceId());
        if(logisticsintention==null){
            response.setCode(-600015);
            response.setMessage("对应流水号的意向单不存在");
            logger.info("--56推送取消订单接口--该意向单不存在--SouceId:"+request.getSouceId());
            return  gson.toJson(response);
        }

        Logisticsfeedback logisticsfeedback = logisticsfeedbackMapper.findDetailBySouceId(request.getSouceId());
        if(logisticsfeedback==null){
            response.setCode(-600008);
            response.setMessage("该意向单未经匹配不能取消");
            logger.info("--56推送取消订单接口--该意向单未经匹配不能取消--SouceId:"+request.getSouceId());
            return  gson.toJson(response);
        }

       if( LogisticsStatus.COMPLETED_ALREADY.name().equals(logisticsintention.getStatus())){
           response.setCode(-600008);
           response.setMessage("该意向单已完成不能取消");
           logger.info("--56推送取消订单接口--该意向单已完成不能取消--SouceId:"+request.getSouceId());
           return  gson.toJson(response);
       }

        logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.NOT_TRADING.name(),request.getSouceId());
        logisticsfeedbackMapper.updatecomment(request.getRemark(),LogisticsStatus.NOT_TRADING.name(),request.getSouceId());
        if(logisticsintention.getUserid()!=0) {
            User user = userMapper.getUserById(logisticsintention.getUserid());
            final String content = "尊敬的易煤网用户，您的意向单已经被取消，如需帮助，请拨打咨询热线：400-960-1180";
            MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
        }
        response.setCode(600000);
        response.setMessage("接收成功");
        return gson.toJson(response);
    }

    //56推送状态接口
    @RequestMapping(value="/logistics/pushState",method = RequestMethod.POST,produces = "text/json;charset=utf-8")
    @ResponseBody
    public Object pushState(
                            @RequestParam(value = "data", required = true)String data,
                            @RequestParam(value = "sign", required = false,defaultValue = "1")int sign
                            ) throws IOException {
        Gson gson=new Gson();
        PushStatusRequest request=new PushStatusRequest();
        PushStatusResponse response=new PushStatusResponse();
        logger.info("data:"+data);
        try {
            request = gson.fromJson(data, PushStatusRequest.class);
            logger.info("--56推送状态接口--request---:" + request.toString());
           String Vendercreatetime=request.getVendercreatetime();
           String transportstartdate=request.getTransportstartdate();
            String transportenddate=request.getTransportenddate();
            if(Vendercreatetime!=null&&!Vendercreatetime.equals("")){
                LocalDateTime.parse(Vendercreatetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
           }
            if(transportstartdate!=null&&!transportstartdate.equals("")){
                LocalDate.parse(transportstartdate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if(transportenddate!=null&&!transportenddate.equals("")){
                LocalDate.parse(transportenddate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }

        }catch (Exception e){
            response.setCode(-600002);
            response.setMessage("请求参数格式错误");
            return  gson.toJson(response);
        }
        if(StringUtils.isBlank(request.getStatus())||StringUtils.isBlank(request.getSouceId())||StringUtils.isBlank(request.getToken())||StringUtils.isBlank(request.getVersion())){
            response.setCode(-600001);
            response.setMessage("非空字段有空值");
            return  gson.toJson(response);
        }else{
            vender56Client.initTokenSalt();
            String tk = dataBookMapper.getDataBookNameByTypeSequence("logisticsVender56token", 1);
            if (request.getToken().equals(tk) && request.getVersion().equals("v1.0")) {
                Map<String, Object> map = new HashMap<String, Object>();
                Logisticsfeedback logistics = logisticsfeedbackMapper.findDetailBySouceId(request.getSouceId());
                Logisticsintention logisticsintention= logisticsMapper.findBySouceId(request.getSouceId());

                if(logisticsintention==null){
                    response.setCode(-600015);
                    response.setMessage("对应流水号的意向单不存在");
                    return gson.toJson(response);
                }

                if(sign==1){
                    if (logistics != null) {
                        //返回
                        response.setCode(600002);
                        response.setMessage("对应流水号的结果已经存在");
                        return gson.toJson(response);
                    } else {
                        if(!request.getStatus().equals("NOT_TRADING")){
                            //返回
                            response.setCode(600003);
                            response.setMessage("返回状态错误!");
                            return gson.toJson(response);

                        }

                        //插入数据
                        //PushStatusRequest req1=new PushStatusRequest();
                        //req1.setSouceId(request.getSouceId());
                        //req1.setStatus(request.getStatus());
                        //req1.setComment(request.getComment());
                        //req1.setCreatetime(LocalDateTime.now());
                        //req1.setVenderid(logisticsintention.getVenderid());
                        //req1.setIntentionid(logisticsintention.getId());
                        //req1.setTransportstartdate(request.getTransportstartdate());
                        //req1.setTransportenddate(request.getTransportenddate());
                          request.setCreatetime(LocalDateTime.now());
                            request.setVenderid(logisticsintention.getVenderid());
                            request.setIntentionid(logisticsintention.getId());
                        logisticsfeedbackMapper.addLogisticsfeedback(request);
//                        //更新意向单状态
//                        logisticsMapper.updatLogisIntentionStatusBySouceid(request.getStatus(), request.getSouceId());
                    }
                }else if(sign==2){
                    //意向单匹配字段判断
                    //if(StringUtils.isBlank(request.getOrdercode())||request.getVendercreatetime()==null){
                    //    response.setCode(-600001);
                    //    response.setMessage("非空字段有空值");
                    //    return  gson.toJson(response);
                    //}else{
                        if (logistics != null) {
                            //返回
                            response.setCode(600002);
                            response.setMessage("对应流水号的结果已经存在");
                            return gson.toJson(response);
                        }else{

                            if(!request.getStatus().equals("MATCH_COMPLETED")){
                                //返回
                                response.setCode(600003);
                                response.setMessage("返回状态错误!");
                                return gson.toJson(response);

                            }
                            //PushStatusRequest req2=new PushStatusRequest();
                            //req2.setSouceId(request.getSouceId());
                            //req2.setStatus(request.getStatus());
                            //req2.setComment(request.getComment());
                            request.setCreatetime(LocalDateTime.now());
                            //req2.setVendercreatetime(request.getVendercreatetime());
                            //req2.setOrdercode(request.getOrdercode());
                            //req2.setLogisticsphone(request.getLogisticsphone());
                            //req2.setTotalprice(request.getTotalprice());
                            //req2.setTransportprices(request.getTransportprices());
                            request.setVenderid(logisticsintention.getVenderid());
                            request.setIntentionid(logisticsintention.getId());
                            //更新数据
                            logisticsfeedbackMapper.addLogisticsfeedback(request);
//                            //更新意向单状态
//                            logisticsMapper.updatLogisIntentionStatusBySouceid(request.getStatus(), request.getSouceId());
                        }
                    //}
                }else if(sign==3){
                    //更新意向单字段判断
                    if(StringUtils.isBlank(request.getOrdercode())||request.getVendercreatetime()==null
                            ||request.getTransportprices()==null||request.getTotalprice()==null
                            ||StringUtils.isBlank(request.getLogisticsphone())||request.getTransportstartdate()==null||request.getTransportenddate()==null){
                        response.setCode(-600001);
                        response.setMessage("非空字段有空值");
                        return  gson.toJson(response);
                    }else{
                        if (logistics != null) {
                            if(!request.getStatus().equals("COMPLETED_ALREADY")&&!request.getStatus().equals("MATCH_COMPLETED")){
                                //返回
                                response.setCode(600003);
                                response.setMessage("返回状态错误!");
                                return gson.toJson(response);

                            }

                            if(request.getIsfinish()==true){
                                logisticsMapper.updateIsfinish(true, logisticsintention.getId());
                            }

                            //更新数据
                            logisticsfeedbackMapper.updateLogisticsfeedback(request);
                            //更新意向单状态
                            logisticsMapper.updatLogisIntentionStatusBySouceid(request.getStatus(), request.getSouceId());
                            if(request.getStatus().equals(LogisticsStatus.COMPLETED_ALREADY.toString())){
                                int userid=logisticsMapper.findUserBySouceId(request.getSouceId());
                                if(userid!=0) {
                                    MessageNotice.LOGISTICCOMPLETED.noticeUser(userid);
                                }
                            }
                        }
                    }
                }
                response.setCode(600000);
                response.setMessage("接收成功");
                return gson.toJson(response);
            } else {
                response.setCode(-600000);
                response.setMessage("非法请求");
                return gson.toJson(response);
            }

        }

    }

    //进入确认找船订单页面
    @RequestMapping("/testSearchShipOrder")
    public String testSearchShipOrder() {
        return "logistics/searchShipOrder";
    }

    //进入确认找船订单页面
    @RequestMapping("/testSearchShip")
    public String testSearchShip() {
        return "logistics/searchShip";
    }


}
