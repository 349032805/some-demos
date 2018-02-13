package kitt.site.controller;


import com.google.gson.Gson;
import kitt.core.domain.*;
import kitt.core.libs.LogisticsShipClient;
import kitt.core.libs.logistics.ShipQuery;
import kitt.core.libs.logistics.ShipQueryRet;
import kitt.core.persistence.*;
import kitt.core.util.AuthMethod;
import kitt.ext.WithLogger;
import kitt.site.basic.annotation.Client;
import kitt.site.service.BeanValidators;
import kitt.site.service.Session;
import kitt.site.service.ShipService;
import me.chanjar.weixin.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lich on 16/1/11.
 */
@Controller
public class ShipController implements WithLogger {
    @Autowired
    private Session session;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private ShipMapper shipMapper;
    @Autowired
    private ShipService shipService;
    @Autowired
    private LogisticsshipfeedbackMapper logisticsshipfeedbackMapper;
    @Autowired
    private LogisticsMapper logisticsMapper;
    @Autowired
    private shipanchorpointinfoMapper shipanchorpointinfoMapper;
    @Autowired
    private LogisticsDelegateMapper logisticsDelegateMapper;
    @Autowired
    private AuthMethod authMethod;

    @Autowired
    private ValidMapper validMapper;
    @Autowired
    private LogisticsShipClient logisticsShipClient;

      Logger logger=  LoggerFactory.getLogger(this.getClass());

    //保存委托单
    @RequestMapping("/ship/saveDelegate")
    @ResponseBody
    public Object saveDelegate(LogisticsDelegate delegate,@RequestParam(value = "smscode", required = false)String smscode){
        System.out.println("--------委托保存");
        MapObject map=new MapObject();
        Map map1=new HashMap<String,Object>();
        delegate.setStatus(LogisticsStatus.DELEGATE_TREATED_NOT.toString());
          BeanValidators.validateWithException(delegate);
            Phonevalidator phonevalidator = validMapper.findVerifyCode(delegate.getMobile(), smscode, ValidateType.logistics);
            map = authMethod.doCheckCodeNOCheckPhoneExist(delegate.getMobile(), phonevalidator);
            if (map.isSuccess()) {
                boolean f =  logisticsDelegateMapper.addLogisticsDelegate(delegate);
                if (f) {
                    map1.put("message", true);
                } else {
                    map1.put("message", false);
                    map1.put("errdata","委托询盘错误");
                }
            }else{
                map1.put("message", false);
                map1.put("errdata","验证码不正确或已过期");
            }

        return map1;

    }

    ////进入船运首页
    //@RequestMapping("/ship/toShipIndex")
    //public String toShip(Map<String, Object> model){
    //    //返回所有的海域
    //    model.put("waters",  shipMapper.findAllDistrict(null,1));
    //    model.put("ports",shipMapper.findAllPorts(2));
    //    model.put("severtime", LocalDate.now().plusDays(1));
    //    return "logistics/logisticIndex";
    //}

    //获取所有的海域
    @RequestMapping("/ship/getAllProvinces")
    @ResponseBody
    public Object getAllProvinces(){
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("waters",  shipMapper.findAllDistrict(null, 1));
        return map;
    }

    //根据海域code查找港口
    @RequestMapping("/ship/getAllShipPorts")
    @ResponseBody
    public Object getAllShipPorts(@RequestParam(value = "code", required = true)String code){
        Map<String,Object> map=new HashMap<String,Object>();
        List<District> portsList=shipMapper.findAllDistrict(code,2);
        map.put("portsList", portsList);
        return map;
    }

    ////进入船运意向单确认页面
    //@RequestMapping(value="/ship/toQueryShip")
    //public String toQueryShip(Conditions query,Map<String, Object> model){
    //    District district=new District();
    //    district= shipMapper.findParentByCode(query.getLoadPortName());
    //    model.put("query",query);
    //    BeanValidators.validateWithException(query);
    //    model.put("area", district);    //选中的海域
    //    model.put("areaList",shipMapper.findByLevel(1));  //所有的海域
    //    model.put("areaports",shipMapper.findAllDistrict(district.getCode(),2)); //选中的海域下的港口
    //    return "logistics/searchShip";
    //}


    @RequestMapping("/ship/checktimes")
    @ResponseBody
    public Object checktimes(@RequestParam(value = "mobile", required = true)String mobile,@Client ClientInfo clientInfo){
     Map<String,Object> map1=new HashMap<String,Object>();
        map1.put("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(mobile, clientInfo.getIP()));
        return map1;
    }

    //进入确认页面
    @RequestMapping(value="/logistics/toLogistics/toShip")
    public String toConfirmShip(Shipintention shipintention,Map<String, Object> model,@Client ClientInfo clientInfo){
        if(session!=null&&session.getUser()!=null) {
            shipintention.setMobile(session.getUser().getSecurephone());
        }
        //model.put("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(shipintention.getMobile(), clientInfo.getIP()));
        model.put("shipintention", shipintention);
        List<District> molds=shipMapper.findMolds();
        if(molds!=null&&molds.size()>0){
            for(District d:molds){
                d.setRegionList(shipMapper.findByMold(d.getMold()));
            }
        }
        model.put("ports",molds);
        return "logistics/searchShipOrder";
    }

    //保存意向单
    @RequestMapping("/ship/saveLogistics")
    @ResponseBody
    public Object saveLogistics(Shipintention shipintention,@RequestParam(value = "smscode", required = false)String smscode){
        MapObject map=new MapObject();
        BeanValidators.validateWithException(shipintention);
        Map map1=new HashMap<String,Object>();
        map1.put("message", false);
        if (!session.isLogined() || (session.isLogined() && !session.getUser().getSecurephone().equals(shipintention.getMobile()))) {
            Phonevalidator phonevalidator = validMapper.findVerifyCode(shipintention.getMobile(), smscode, ValidateType.logistics);
            map = authMethod.doCheckCodeNOCheckPhoneExist(shipintention.getMobile(), phonevalidator);
            if (map.isSuccess()) {
                boolean f = shipService.saveShipIntention(shipintention);
                if (f) {
                    map1.put("message", true);
                } else {
                    map1.put("message", false);
                    map1.put("errdata","提交意向单错误");
                }
            }else{
                map1.put("errdata","验证码不正确或已过期");
            }

        }else{
            boolean f = shipService.saveShipIntention(shipintention);
            if (f) {
                map1.put("message", true);
            } else {
                map1.put("message", false);
                map1.put("errdata","提交意向单错误");
            }
        }
        return map1;
    }

    //船运--查询船
    @RequestMapping(value="/logistics/toLogistics/queryShips",method = RequestMethod.GET)
    public Object queryShips(ShipQuery query,Map<String, Object> model) throws IOException {
        ShipQuery query1=new ShipQuery();
        query1=query;
        BeanValidators.validateWithException(query1);
        District district=new District();
        district= shipMapper.findParentByCode(query1.getLoadPortName());
        model.put("areaList", shipMapper.findByLevel(1));  //所有的海域
        if(district!=null) {
            model.put("area", district);    //选中的海域
            model.put("areaports", shipMapper.findAllDistrict(district.getCode(), 2)); //选中的海域下的港口
        }else{
            model.put("area", null);
        }
        query1.setPageSize(10);
        Gson gson=new Gson();
        String data=logisticsShipClient.shipquery(query1);
        ShipQueryRet shipQueryRet= gson.fromJson(data, ShipQueryRet.class);
        //model.put("params",query);    //查询条件
        model.put("query", query1);    //查询条件
        System.out.println("---------排序名称:" + query1.getSortNum());
        System.out.println("---------排序规则:" + query1.getSortIdent());
        model.put("portName", shipMapper.findByCode(query1.getLoadPortName()));
        model.put("data", shipQueryRet); //返回数据
        return "logistics/searchShip";
    }



    //船运--匹配完成接口
    @RequestMapping(value="/ship/pushMatchInfo",method = RequestMethod.POST,produces = "text/json;charset=utf-8")
    @ResponseBody
    public Object pushMatchInfo(@RequestParam(value = "data", required = true)String data) throws IOException {
        Gson gson=new Gson();
        ShipRequest request=new ShipRequest();
        PushStatusResponse response = new PushStatusResponse();
        LogisticsShipFeedback feedback =new LogisticsShipFeedback();
        logger.info("匹配完成接口---------data:" + data);
        System.out.println("匹配完成接口传入数据-------data:" + data);
        try {
            request = gson.fromJson(data, ShipRequest.class);
            if(request.getNoLoadDate()!=null&&!request.getNoLoadDate().equals("")){
                LocalDate.parse(request.getNoLoadDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        }catch (Exception e){
            response.setCode(-600002);
            response.setMessage("请求参数格式错误");
            return  gson.toJson(response);
        }

        boolean b=BeanValidators.validateWithMessage(request);
        if(!b){
            response.setCode(-600001);
            response.setMessage("非空字段有空值");
            return gson.toJson(response);
        }
        LogisticsShipFeedback logisticsShipFeedback= logisticsshipfeedbackMapper.findFeedBackBySouceId(request.getNo());
        Logisticsintention logisticsintention= logisticsMapper.findBySouceId(request.getNo());
        if(logisticsShipFeedback!=null){
            response.setCode(600002);
            response.setMessage("对应流水号的结果已经存在");
            return gson.toJson(response);
        }
        if(logisticsintention==null){
            response.setCode(-600004);
            response.setMessage("对应流水号的意向单不存在");
            return gson.toJson(response);
        }
        if(!logisticsintention.getStatus().equals(LogisticsStatus.MATCH_ING.toString())){
            response.setCode(-600008);
            response.setMessage("对应流水号的意向单还不能执行此操作");
            return gson.toJson(response);
        }

        //设置值
        feedback= setValues(request);
        feedback.setStatus(LogisticsStatus.MATCH_COMPLETED.toString());
        feedback.setVenderid(logisticsintention.getVenderid());
        feedback.setCreatetime(LocalDateTime.now());
        logisticsshipfeedbackMapper.addLogisticsShipfeedback(feedback);
        logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.MATCH_COMPLETED.toString(), request.getNo());
        response.setCode(600000);
        response.setMessage("接收成功");
        return  gson.toJson(response);
        }



    //船运--运送中接口
    @RequestMapping(value="/ship/pushTransportInfo",method = RequestMethod.POST,produces = "text/json;charset=utf-8")
    @ResponseBody
    public Object pushTransportInfo(@RequestParam(value = "data", required = true)String data) throws IOException {
        Gson gson=new Gson();
        logger.info("运送中接口--------data:" + data);
        System.out.println("运送中接口传入数据-------data:" + data);
        ShipInfoRequest request=new ShipInfoRequest();
        PushStatusResponse response=new PushStatusResponse();
        ShipAnchorPointInfo info=new ShipAnchorPointInfo();
        try {
            request = gson.fromJson(data, ShipInfoRequest.class);
            if(request.getProcessTime()!=null&&!request.getProcessTime().equals("")){
                LocalDate.parse(request.getProcessTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }catch (Exception e){
            response.setCode(-600002);
            response.setMessage("请求参数格式错误");
            return  gson.toJson(response);
        }
        //验证数据

        boolean b=BeanValidators.validateWithMessage(request);
        if(!b){
            response.setCode(-600001);
            response.setMessage("非空字段有空值");
            return gson.toJson(response);
        }
        Logisticsintention logisticsintention= logisticsMapper.findBySouceId(request.getNo());
        if(logisticsintention==null){
            response.setCode(-600004);
            response.setMessage("对应流水号的意向单不存在");
            return gson.toJson(response);
        }
        if(!logisticsintention.getStatus().equals(LogisticsStatus.MATCH_COMPLETED.toString())&&!logisticsintention.getStatus().equals(LogisticsStatus.TRANSPORT_ING.toString())){
            response.setCode(-600008);
            response.setMessage("对应流水号的意向单还不能执行此操作");
            return gson.toJson(response);
        }
        if(request.getSign().equals("1")){  // sign 0正常  1删除
            shipanchorpointinfoMapper.updateShipAnchorPointInfo(request.getCdId());
            int count=shipanchorpointinfoMapper.countsBySourceId(request.getNo());
            if(count==0){
                //更新成匹配中
                logisticsshipfeedbackMapper.updateShipfeedback(request.getNo(), "超级船东更新成匹配完成", LogisticsStatus.MATCH_COMPLETED.toString());
                logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.MATCH_COMPLETED.toString(),request.getNo());
            }else if(count>0){
                //更新成运送中
                logisticsshipfeedbackMapper.updateShipfeedback(request.getNo(), "超级船东更新成运送中状态", LogisticsStatus.TRANSPORT_ING.toString());
                logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.TRANSPORT_ING.toString(),request.getNo());
            }

        }else if(request.getSign().equals("0")){
            info.setSouceId(request.getNo());
            info.setInfo(request.getInfo());
            info.setInfoid(request.getInfoId());
            info.setProcesstime(request.getProcessTime());
            info.setSort(request.getSort());
            info.setSign(request.getSign());
            info.setCdId(request.getCdId());
            shipanchorpointinfoMapper.updateShipInfo(request.getCdId(),request.getNo());
            shipanchorpointinfoMapper.addAnchorpointinfo(info);
            logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.TRANSPORT_ING.toString(), request.getNo());
            logisticsshipfeedbackMapper.updateShipfeedback(request.getNo(), "超级船东更新成运送中状态", LogisticsStatus.TRANSPORT_ING.toString());
        }else{
            response.setCode(-600006);
            response.setMessage("传送的sign字段值存在错误");
            return gson.toJson(response);
        }

        response.setCode(600000);
        response.setMessage("接收成功");
        return  gson.toJson(response);

    }

    //取消订单接口
    @RequestMapping(value="/ship/pushCancelInfo",method = RequestMethod.POST,produces = "text/json;charset=utf-8")
    @ResponseBody
    public Object pushCancelInfo(@RequestParam(value = "data", required = true)String data) throws IOException {
        Gson gson = new Gson();
        logger.info("取消订单接口--------data:" + data);
        System.out.println("取消订单接口传入数据-------data:" + data);
       Map<String,Object> request=new HashMap<String,Object>();
        PushStatusResponse response=new PushStatusResponse();
        try {
            request = gson.fromJson(data, Map.class);
        }catch (Exception e){
            response.setCode(-600002);
            response.setMessage("请求参数格式错误");
            return  gson.toJson(response);
        }
        //验证数据
        if(StringUtils.isBlank((String) request.get("No"))||StringUtils.isBlank((String) request.get("Remark"))){
            response.setCode(-600001);
            response.setMessage("非空字段有空值");
            return  gson.toJson(response);
        }
        String No= (String) request.get("No");
        String Remark = (String) request.get("Remark");

        Logisticsintention logisticsintention= logisticsMapper.findBySouceId(No);
        if(logisticsintention==null){
            response.setCode(-600004);
            response.setMessage("对应流水号的意向单不存在");
            return gson.toJson(response);
        }
        if(logisticsintention.getStatus().equals(LogisticsStatus.MATCH_COMPLETED.toString())) {
            logisticsshipfeedbackMapper.updateShipfeedback(No, Remark, LogisticsStatus.NOT_TRADING.toString());
            logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.NOT_TRADING.toString(), No);
        }else{
            response.setCode(-600007);
            response.setMessage("该意向单不能被取消");
            return  gson.toJson(response);
        }
        response.setCode(600000);
        response.setMessage("接收成功");
        return  gson.toJson(response);

    }




    //船运--已完成接口
    @RequestMapping(value="/ship/pushCompleteInfo",method = RequestMethod.POST,produces = "text/json;charset=utf-8")
    @ResponseBody
    public Object pushCompleteInfo(@RequestParam(value = "data", required = true)String data) throws IOException {
        Gson gson=new Gson();
        ShipRequest request=new ShipRequest();
        PushStatusResponse response=new PushStatusResponse();
        LogisticsShipFeedback feedback = new LogisticsShipFeedback();
        logger.info("已完成接口-------data:" + data);
        System.out.println("已完成接口传入数据-------data:" + data);

        try {
            request = gson.fromJson(data, ShipRequest.class);
        }catch (Exception e){
            response.setCode(-600002);
            response.setMessage("请求参数格式错误");
            return  gson.toJson(response);
        }

        if(StringUtils.isBlank(request.getNo())||request.getPayTotalAmount()==null||request.getPayTotalWeight()==null){
            response.setCode(-600001);
            response.setMessage("非空字段有空值");
            return  gson.toJson(response);
        }

        Logisticsintention logisticsintention= logisticsMapper.findBySouceId(request.getNo());
        if (logisticsintention==null){
            response.setCode(-600004);
            response.setMessage("对应流水号的意向单不存在");
            return gson.toJson(response);
        }

        if(!logisticsintention.getStatus().equals(LogisticsStatus.TRANSPORT_ING.toString())){
            response.setCode(-600008);
            response.setMessage("对应流水号的意向单还不能执行此操作");
            return gson.toJson(response);
        }
        feedback.setStatus(LogisticsStatus.COMPLETED_ALREADY.toString());
        feedback.setSouceId(request.getNo());
        feedback.setContractattachurl(request.getContractAttachUrl());
        feedback.setPaytotalamount(request.getPayTotalAmount());
        feedback.setPaytotalweight(request.getPayTotalWeight());
        logisticsshipfeedbackMapper.updateLogisticsShipfeedback(feedback);
        logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.COMPLETED_ALREADY.toString(), request.getNo());
        response.setCode(600000);
        response.setMessage("接收成功");
        return  gson.toJson(response);

    }

    //订单更新接口
    @RequestMapping(value="/ship/pushUpdateInfo",method = RequestMethod.POST,produces = "text/json;charset=utf-8")
    @ResponseBody
    public Object pushUpdateInfo(@RequestParam(value = "data", required = true)String data) throws IOException {
        Gson gson=new Gson();
        logger.info("订单更新接口--------data:" + data);
        System.out.println("订单更新接口传入数据-------data:" + data);
        ShipUpdateInfo request=new ShipUpdateInfo();
        PushStatusResponse response=new PushStatusResponse();
        LogisticsShipFeedback info=new LogisticsShipFeedback();
        try {
            request = gson.fromJson(data, ShipUpdateInfo.class);
            if(request.getNoLoadDate()!=null&&!request.getNoLoadDate().equals("")){
                LocalDate.parse(request.getNoLoadDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        }catch (Exception e){
            response.setCode(-600002);
            response.setMessage("请求参数格式错误");
            return  gson.toJson(response);
        }

        //验证数据
        boolean b=BeanValidators.validateWithMessage(request);
        if(!b){
            response.setCode(-600001);
            response.setMessage("非空字段有空值");
            return gson.toJson(response);
        }

        LogisticsShipFeedback back=logisticsshipfeedbackMapper.findFeedBackBySouceId(request.getNo());
        if(back==null){
            response.setCode(-600004);
            response.setMessage("对应流水号的意向单不存在");
            return gson.toJson(response);
        }
        if(back.getStatus().equals(LogisticsStatus.TRANSPORT_ING.toString())||back.getStatus().equals(LogisticsStatus.MATCH_COMPLETED.toString())){
            info.setSouceId(request.getNo());
            info.setNeedbill(request.getNeedBill());
            info.setPricetype(request.getPriceType());
            info.setTransportprices(request.getPrice());
            info.setDWT(request.getCapacity());
            info.setEmptyport(request.getNoLoadPort());
            info.setEmptydate(request.getNoLoadDate());
            info.setTotalprice(request.getTotalAmount());
            info.setRemark(request.getRemark());
            info.setOtherprice(request.getOtherFees());
            info.setDetentioncharge(request.getDemurrageFees());
            info.setDetentionchargetype(request.getDemurrageFeesType());
            info.setDetentionday(request.getDemurrageDay());
            logisticsshipfeedbackMapper.updatefeedback(info);
        }else{
            response.setCode(-600005);
            response.setMessage("对应流水号的意向单不能更改");
            return gson.toJson(response);
        }
        response.setCode(600000);
        response.setMessage("接收成功");
        return  gson.toJson(response);

    }




    public LogisticsShipFeedback setValues(ShipRequest request){
        LogisticsShipFeedback back=new LogisticsShipFeedback();
        back.setSouceId(request.getNo());
        back.setTransportcompany(request.getTransportName());
        back.setCarrier(request.getTransportContacterName());
        back.setCarrierphone(request.getTransportContacterPhone());
        back.setCcempname(request.getCCEmpName());
        back.setCcempphone(request.getCCEmpPhone());
        back.setPaytype(request.getPaymentCondition());
        back.setSettlementtype(request.getSettlementKind());
        back.setNeedbill(request.getNeedBill());
        back.setTransportprices(request.getPrice());
        back.setPricetype(request.getPriceType());
        back.setOtherprice(request.getOtherFees());
        back.setDWT(request.getCapacity());
        back.setShipname(request.getShipName());
        back.setEmptyport(request.getNoLoadPort());
        back.setEmptydate(request.getNoLoadDate());
        back.setDetentioncharge(request.getDemurrageFees());
        back.setDetentionchargetype(request.getDemurrageFeesType());
        back.setTotalprice(request.getTotalAmount());
        back.setRemark(request.getRemark());
        back.setDetentionday(request.getDemurrageDay());
        return back;
    }

    @RequestMapping("/ship/shipSuccess")
    public String shipSuccess(){
        return "logistics/shipSuccess";
    }

}










