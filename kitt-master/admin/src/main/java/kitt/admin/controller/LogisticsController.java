package kitt.admin.controller;

import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.core.domain.*;
import kitt.core.libs.LogisticsShipClient;
import kitt.core.libs.logistics.CancelShip;
import kitt.core.libs.logistics.ShipRet;
import kitt.core.libs.logistics.SubmitShipInfo;
import kitt.core.persistence.*;
import kitt.core.service.LogisticsWebService;
import kitt.core.service.MessageNotice;
import kitt.core.util.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lich on 15/12/17.
 */
@RestController
@RequestMapping("/logiszone")
public class LogisticsController {
    @Autowired
    private LogisticsMapper logisticsMapper;
    @Autowired
    private RegionMapper regionMapper;
    @Autowired
    private LogisticsvenderMapper logisticsvenderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private LogisticsfeedbackMapper logisticsfeedbackMapper;
    @Autowired
    private ReturnvisitMapper returnvisitMapper;
    @Autowired
    private LogisticsDelegateMapper delegateMapper;
    @Autowired
    private LogisticsWebService logisticsWebService;
    @Autowired
    private ShipMapper shipMapper;
    @Autowired
    private LogisticsShipClient shipClient;
    @Autowired
    private LogisticsshipfeedbackMapper shipfeedbackMapper;
    @Autowired
    private shipanchorpointinfoMapper shipanchorpointinfoMapper;
    @Autowired
    private LogisticsShipClient logisticsShipClient;

    private final String IP="http://www.yimei180.com/";


    private List<DataBook> goodstypelist;

    private List<DataBook> visittypelist;

    public LogisticsController(){
        this.goodstypelist=new ArrayList<DataBook>();
        goodstypelist.add(new DataBook(1, "原煤"));
        goodstypelist.add(new DataBook(2,"焦炭"));
        goodstypelist.add(new DataBook(3, "块煤"));
        goodstypelist.add(new DataBook(4, "煤泥"));
        goodstypelist.add(new DataBook(5,"沫煤"));
        goodstypelist.add(new DataBook(6,"混煤"));

        this.visittypelist=new ArrayList<DataBook>();
        visittypelist.add(new DataBook(1,"沟通完成"));
        visittypelist.add(new DataBook(2,"待商榷"));
        visittypelist.add(new DataBook(3,"转线下处理"));
    }

    //物流专区 查看未处理详情页面
    @RequestMapping("/purposedetail")
    public Object purposedetail(@RequestParam(value = "souceid", required = false)String souceid){
        Map<String,Object> map=new HashMap<String,Object>();

        Logisticsintention logisticsintention= logisticsMapper.findBySouceId(souceid);
        List<District> provinceslist = regionMapper.getAllProvinces();
        List<Logisticsvender> logisticsvenders= logisticsvenderMapper.findLogisticsvenderByType(1);
        Logisticsfeedback  logisticsfeedback =logisticsfeedbackMapper.findDetailBySouceId(logisticsintention.getSouceId());
        if(logisticsfeedback!=null&&logisticsfeedback.getStatus()!=null) {
            if ("MATCH_COMPLETED".equals(logisticsfeedback.getStatus())) {
                logisticsfeedback.setStatus("匹配完成");
            } else if ("COMPLETED_ALREADY".equals(logisticsfeedback.getStatus())) {
                logisticsfeedback.setStatus("已完成");
            } else if ("NOT_TRADING".equals(logisticsfeedback.getStatus())) {
                logisticsfeedback.setStatus("未交易,已取消");
            }
        }
        Pager<Returnvisit> returnvisitList= returnvisitMapper.getReturnvisitBySouceId(souceid,1,10);
        List<District> loadcitylist = regionMapper.getDistrictByParent(logisticsintention.getProvinceCode(), 2);
        List<District> loadarealist = regionMapper.getDistrictByParent(logisticsintention.getCityCode(), 3);
        List<District> unloadcitylist = regionMapper.getDistrictByParent(logisticsintention.getUnprovinceCode(), 2);
        List<District> unloadarealist = regionMapper.getDistrictByParent(logisticsintention.getUncityCode(), 3);

        map.put("visittypelist",visittypelist);
        map.put("returnvisitList",returnvisitList);
        map.put("logisticsfeedback",logisticsfeedback);
        map.put("logisticsintention", logisticsintention);
        map.put("provinceslist", provinceslist);
        map.put("goodstypelist", goodstypelist);
        map.put("logisticsvenders", logisticsvenders);
        map.put("loadcitylist", loadcitylist);
        map.put("loadarealist", loadarealist);
        map.put("unloadcitylist", unloadcitylist);
        map.put("unloadarealist", unloadarealist);
        return map;
    }

    //物流专区进入客服添加意向单
    @RequestMapping("/addpurpose")
    public Object addpurpose(){
        Map<String,Object> map=new HashMap<String,Object>();
        List<District> provinceslist = regionMapper.getAllProvinces();
        List<Logisticsvender> logisticsvenders= logisticsvenderMapper.findLogisticsvenderByType(1);
        map.put("goodstypelist", goodstypelist);
        map.put("logisticsvenders", logisticsvenders);
        map.put("provinceslist", provinceslist);
        map.put("logistisintention", new Logisticsintention());
        return map;
    }

    //装车点选择省份联动查询下属城市
    @RequestMapping("/reloadcity")
    public Object reloadcity(@RequestParam(value = "provincecode", required = false)String provincecode){
        Map<String,Object> map=new HashMap<String,Object>();
        System.out.println("-----reloadcity-------" + provincecode);
        List<District> loadcitylist = regionMapper.getDistrictByParent(provincecode, 2);
        map.put("loadcitylist", loadcitylist);
        map.put("loadarealist", null);
        return map;
    }

    //装车点选择城市联动查询下属区域
    @RequestMapping("/unreloadcity")
    public Object unreloadcity(@RequestParam(value = "provincecode", required = false)String provincecode){
        Map<String,Object> map=new HashMap<String,Object>();
        System.out.println("-----unreloadcity-------"+provincecode);
        List<District> unloadcitylist = regionMapper.getDistrictByParent(provincecode, 2);
        map.put("unloadcitylist", unloadcitylist);
        map.put("unloadarealist", null);
        return map;
    }

    //卸车点选择省份联动查询下属城市
    @RequestMapping("/reloadarea")
    public Object reloadarea(@RequestParam(value = "citycode", required = false)String citycode){
        Map<String,Object> map=new HashMap<String,Object>();
        System.out.println("-----reloadarea-------"+citycode);
        List<District> loadarealist = regionMapper.getDistrictByParent(citycode, 3);
        map.put("loadarealist", loadarealist);
        return map;
    }

    //卸车点选择城市联动查询下属区域
    @RequestMapping("/unreloadarea")
    public Object unreloadarea(@RequestParam(value = "citycode", required = false)String citycode){
        Map<String,Object> map=new HashMap<String,Object>();
        System.out.println("-----unreloadarea-------"+citycode);
        List<District> unloadarealist = regionMapper.getDistrictByParent(citycode, 3);
        map.put("unloadarealist", unloadarealist);
        return map;
    }

    //未处理详细信息 提交意向单
    @RequestMapping("/saveLogistics")
    public Object saveLogistics(@Valid Logisticsintention logisticsintention, BindingResult result){

            System.out.println("--------saveLogistics-------"+logisticsintention.toString());
            Map<String, Object> map = new HashMap<String, Object>();
            if (result.hasErrors()) {
                throw new BusinessException(result.getFieldError().getDefaultMessage());
            } else {
                Logisticsintention oldlogistics =logisticsMapper.findBySouceId(logisticsintention.getSouceId());
                if(oldlogistics==null)
                    throw new NotFoundException();
                logisticsMapper.updateLogisIntention(logisticsintention);
                Logisticsintention newlogistics =logisticsMapper.findBySouceId(logisticsintention.getSouceId());
                String message = logisticsWebService.commitPurpose(newlogistics,true);
                map.put("message", message);
                return map;
            }
    }

    //已提交数据 取消意向单
    @RequestMapping("/cancelLogistics")
    public Object cancelLogistics(@RequestParam(value = "souceid", required = false)String souceid,
                                  @RequestParam(value = "customerremark", required = false)String customerremark,
                                  @RequestParam(value = "isreadonly", required = false)boolean isreadonly) {
        System.out.println("-----------cancelLogistics----------");
        Map<String, Object> map = new HashMap<String, Object>();
        Logisticsintention logisticsintention= logisticsMapper.findBySouceId(souceid);
        if(logisticsintention==null) {
            map.put("message","此意向单不存在");
            map.put("cancelsucceed","false");
            return map;
        }
        if((customerremark==null||customerremark.equals(""))&&isreadonly==false) {
            map.put("message","请输入备注,取消原因");
            map.put("cancelsucceed","false");
            return map;
        }
        if(logisticsintention.getIsfinish()==true){
            if(logisticsintention.getStatus().equals(LogisticsStatus.MATCH_COMPLETED.name())) {
                map.put("message","此订单匹配完成，不能取消");
                map.put("cancelsucceed","false");
                return map;
            }else {
                map.put("message","此订单已完成，不能取消");
                map.put("cancelsucceed","false");
                return map;
            }
        }

        String message="";
        if(logisticsintention.getStatus().equals(LogisticsStatus.TREATED_NOT.name())||logisticsintention.getStatus().equals(LogisticsStatus.MATCH_ING.toString())) {
            logisticsMapper.cancelLogisIntention(LogisticsStatus.NOT_TRADING.name(),logisticsintention.getId());
            shipfeedbackMapper.updateShipfeedback(logisticsintention.getSouceId(), "易煤网客户取消船运订单", LogisticsStatus.NOT_TRADING.toString());
            logisticsMapper.updatCustomerremarkBySouceid(customerremark,logisticsintention.getSouceId());
            logisticsfeedbackMapper.updatecomment("易煤网客户取消56汽运订单",LogisticsStatus.NOT_TRADING.name(),logisticsintention.getSouceId());
            if(logisticsintention.getUserid()!=0) {
                User user = userMapper.getUserById(logisticsintention.getUserid());
                final String content = "尊敬的易煤网用户，您的意向单已经被取消，如需帮助，请拨打咨询热线：400-960-1180";
                MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
            }
            map.put("cancelsucceed","true");
            map.put("message","订单取消成功");
            return map;
        }else{
            try {
                message = logisticsWebService.cancelPurpose(logisticsintention, customerremark);
                if (message.equals("接收成功")) {
                    map.put("cancelsucceed", "true");
                    map.put("message", "订单取消成功");
                    return map;
                } else {
                    map.put("cancelsucceed", "false");
                    map.put("message", message);
                    return map;
                }
            }catch (IOException exc){
                map.put("cancelsucceed", "false");
                map.put("message", "物流供应商服务器访问失败，请稍后在试！");
                return map;
            }
        }
    }

    //56快车返回匹配信息后，我方再次发送确认订单（第一次不发送用户名联系方式，第二次我方发送客户信息）
    @RequestMapping("/confirmfeedback")
    public Object confirmfeedback(@RequestParam(value = "souceid", required = false)String souceid) {
        System.out.println("-----------confirmfeedback----------");
        Map<String, Object> map = new HashMap<String, Object>();
        String message="";
        Logisticsintention logisticsintention= logisticsMapper.findBySouceId(souceid);
        if(logisticsintention==null)
            throw new BusinessException("意向单不存在");
        User user = userMapper.getUserById(logisticsintention.getUserid());
        Logisticsfeedback logisticsfeedback=logisticsfeedbackMapper.findDetailBySouceId(souceid);
        message = logisticsWebService.commitPurpose(logisticsintention, false);
        if(logisticsfeedback!=null && message.equals("接收成功")){
            if(logisticsfeedback.getStatus().equals("MATCH_COMPLETED")){//匹配完成
                if(user != null&&user.getSecurephone()!=null) {
                    final String content = "尊敬的易煤网会员，系统已经为您匹配到报价，稍后将由物流客服专员与您联系，请保持手机畅通。如需帮助请拨打咨询热线：400-960-1180。";
                    MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
                }
                logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.MATCH_COMPLETED.name(), souceid);
            }else if(logisticsfeedback.getStatus().equals("NOT_TRADING")){//未交易
                if(user != null&&user.getSecurephone()!=null) {
                    final String content = "尊敬的易煤网会员，暂时无法为您匹配物流服务，您的意向单已经被取消。如需帮助请拨打咨询热线：400-960-1180。";
                    MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
                }
                logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.NOT_TRADING.name(), souceid);
            }
        }else {
            if(logisticsfeedback==null) {
                message = "56未返回匹配单，不能点击确定";
            }
        }
        map.put("message",message);
        return map;
    }

    //添加随访记录
    @RequestMapping("/addreturnvisit")
    public Object addreturnvisit(@RequestParam(value = "returnvisitstatus", required = false)String returnvisitstatus,
                                 @RequestParam(value = "addcomment", required = false)String addcomment,
                                 @RequestParam(value = "souceid", required = false)String souceid){
        Map<String, Object> map = new HashMap<String, Object>();
        System.out.println("---------addreturnvisit--------"+returnvisitstatus+"   "+addcomment+"  "+souceid);
        returnvisitMapper.addReturnvisit(new Returnvisit(souceid, returnvisitstatus, addcomment));
        Pager<Returnvisit> returnvisitList= returnvisitMapper.getReturnvisitBySouceId(souceid,1,10);
        map.put("returnvisitList",returnvisitList);
        return map;
    }

    //获取回访记录
    @RequestMapping("/getreturnvisit")
    public Object getreturnvisit(@RequestParam(value = "souceid", required = true)String souceid,
                                 @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        Map<String, Object> map = new HashMap<String, Object>();
        System.out.println("--------getreturnvisit-------"+page+" "+souceid);
        Pager<Returnvisit> returnvisitList= returnvisitMapper.getReturnvisitBySouceId(souceid,page,10);
        map.put("returnvisitList",returnvisitList);
        return map;
    }

    //客服人员添加意向单提交
    @RequestMapping("/addLogistics")
    public Object addLogistics(@Valid Logisticsintention logisticsintention, BindingResult result){
        System.out.println("--------addLogistics-------"+logisticsintention.toString());
        Map<String, Object> map = new HashMap<String, Object>();
        String message = "发送56服务端失败";
        logisticsintention.setCreatetime(LocalDateTime.now());
        logisticsintention.setStatus(LogisticsStatus.TREATED_NOT.name());
        if (result.hasErrors()) {
            throw new BusinessException(result.getFieldError().getDefaultMessage());
        } else {
            User user = userMapper.getUserByPhone(logisticsintention.getMobile());
            if(user!=null){
                logisticsintention.setUserid(user.getId());
            }
            logisticsMapper.addLogisticsintentionC(logisticsintention);
            Logisticsintention logistics = logisticsMapper.findById(logisticsintention.getId());
            if(logistics!=null) {
                message = logisticsWebService.commitPurpose(logistics, true);
            }
        }
        map.put("message", message);
        return map;
    }

    //物流专区不同状态结果查询列表
    @RequestMapping("/listIntention")
    public Object getLogisticsList(
            @RequestParam(value = "status", required = true)String status,
            @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        final String returstatus=status;
        final int returpage=page;
        Pager<Logisticsintention> tempintentionList = logisticsMapper.getIntentionList(returstatus,returpage, 10);
        if(tempintentionList.getList()!=null&&tempintentionList.getList().size()>0) {
            for (Logisticsintention logisticsintention : tempintentionList.getList()) {
                if (logisticsintention.getType() == 2) {
                    Shipintention shipintention = shipMapper.getShipIntenttionById(logisticsintention.getShipid());
                    String LoadAddDetail="";
                    String UnloadAddDetail="";
                    if(shipintention.getLoaddock()!=null){
                        LoadAddDetail=shipintention.getLoadport()+shipintention.getLoaddock();
                    }else {
                        LoadAddDetail=shipintention.getLoadport();
                    }

                    if(shipintention.getUnloaddock()!=null){
                        UnloadAddDetail=shipintention.getUnloadport() + shipintention.getUnloaddock();
                    }else {
                        UnloadAddDetail=shipintention.getUnloadport();
                    }
                    logisticsintention.setLoadAddDetail(LoadAddDetail);
                    logisticsintention.setUnLoadAddDetail(UnloadAddDetail);
                }
            }
        }

        return new Object(){
            public String statust=returstatus;
            public Pager<Logisticsintention> intentionList = tempintentionList;
        };
    }

    //-----------------------------船运---------------------------------------------------------------------------------

    @RequestMapping("/getshipdelegatelist")
    public Object getshipdelegatelist(
            @RequestParam(value = "status", required = false)String status,
            @RequestParam(value = "transportType", required = false)String transportType,
            @RequestParam(value = "transportbeginDate", required = false)String transportbeginDate,
            @RequestParam(value = "transportendDate", required = false)String transportendDate,
            @RequestParam(value = "mobile", required = false)String mobile,
            @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        DataBook d1=new DataBook(1,"海运");
        DataBook d2=new DataBook(2,"汽运");
        DataBook d3=new DataBook(3,"江运");
        List<DataBook> tempdataBookList=new ArrayList<DataBook>();
        tempdataBookList.add(d1);
        tempdataBookList.add(d2);
        tempdataBookList.add(d3);
        DataBook d4=new DataBook(1,"未处理",LogisticsStatus.DELEGATE_TREATED_NOT.name());
        DataBook d5=new DataBook(2,"已处理",LogisticsStatus.DELEGATE_TREATED.name());
        List<DataBook> tempstatusList=new ArrayList<DataBook>();
        tempstatusList.add(d4);
        tempstatusList.add(d5);
        return new Object(){
            public Pager<LogisticsDelegate> delegateList = delegateMapper.getLogisticsDelegateList(status,transportType,transportbeginDate,transportendDate,mobile,page, 10);
            public  List<DataBook> transportTypeList=tempdataBookList;
            public  List<DataBook> statusList=tempstatusList;
        };
    }

    @RequestMapping("/getshipdelegatedetail")
    public Object getshipdelegatelist(@RequestParam(value = "id", required = false)int id){
        return new Object(){
            public LogisticsDelegate delegate= delegateMapper.getDelegateBuyId(id);
        };
    }


    @RequestMapping("/finishdelegatedetail")
    public Object finishdelegatedetail(@RequestParam(value = "id", required = true)int id,@RequestParam(value = "suggest", required = false)String suggest){
        LogisticsDelegate delegate= delegateMapper.getDelegateBuyId(id);
        if(delegate==null)
            throw new BusinessException("委托单不存在");
        Map<String, Object> map = new HashMap<String, Object>();
       if(delegateMapper.updatedelegateStatus(id,suggest,LogisticsStatus.DELEGATE_TREATED.name())==1){

           map.put("message","保存成功");
       }else {
           map.put("message","保存失败");
       }
        return map;
    }


    @RequestMapping("/loadshipaddpurpose")
    public Object loadshipaddpurpose(){

        List<District> shipports= shipMapper.findByLevel(2);
        for(District shipport:shipports){
            shipport.setName(shipport.getMold()+"-"+shipport.getName());
        }

        shipports.add(new District("其它","0") );

        return new Object(){
            public Shipintention shipintention= new Shipintention();
            public List<DataBook> shipgoodstypelist=goodstypelist;
            public List<District> shipportlist=shipports;
        };
    }

    @RequestMapping("/shipaddpurpose")
    public Object shipaddpurpose(@Valid Shipintention shipintention,BindingResult result){
        System.out.println("------shipaddpurpose------"+shipintention.toString());
        Map<String, Object> map = new HashMap<String, Object>();
        String loadport= shipintention.getLoadport().split("-")[1];
        String unloadport=shipintention.getUnloadport().split("-")[1];
        shipintention.setLoadport(loadport);
        shipintention.setUnloadport(unloadport);
        shipMapper.addShipIntenttion(shipintention);
        Logisticsintention logisticsintention = new Logisticsintention();
        logisticsintention.setShipid(shipintention.getId());
        logisticsintention.setGoodsType(shipintention.getGoodsType());
        logisticsintention.setGoodsWeight(shipintention.getGoodsWeight());
        logisticsintention.setContacts(shipintention.getContacts());
        logisticsintention.setMobile(shipintention.getMobile());
        logisticsintention.setCompanyname(shipintention.getCompanyname());
        logisticsintention.setRemark(shipintention.getRemark());
        logisticsintention.setType(2);
        logisticsintention.setVenderid(2);
        logisticsintention.setVendername("超级船东");
        logisticsintention.setStatus(LogisticsStatus.TREATED_NOT.name());
        User user= userMapper.getUserByPhone(shipintention.getMobile());
        if(user!=null) {
            logisticsintention.setUserid(user.getId());
        }
        logisticsMapper.addLogisticsintentionS(logisticsintention);

        logisticsintention=logisticsMapper.findById(logisticsintention.getId());
        SubmitShipInfo submitShipInfo=new SubmitShipInfo();
        submitShipInfo.setAddTime(LocalDateTime.now().toString());
        submitShipInfo.setArrivalAddress(unloadport + shipintention.getUnloaddock());
        submitShipInfo.setCapacity(shipintention.getGoodsWeight().intValue());
        submitShipInfo.setNo(logisticsintention.getSouceId());
        submitShipInfo.setStartAddress(loadport + shipintention.getLoaddock());
        submitShipInfo.setCargoName(shipintention.getGoodsType());
        submitShipInfo.setStartDate(shipintention.getReceiptdate().toString());
        submitShipInfo.setRemark(shipintention.getRemark());
        submitShipInfo.setIP(IP);
        submitShipInfo.setMemberMobile(shipintention.getMobile());
        submitShipInfo.setMemberName(shipintention.getContacts());
        submitShipInfo.setCompanyName(shipintention.getCompanyname());
        submitShipInfo.setIsTrade(1);
        try {
            ShipRet shipRet = shipClient.submitInfo(submitShipInfo);
            if (shipRet.getRetMsg().equals("成功！")) {
                logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.MATCH_ING.name(), logisticsintention.getSouceId());
                map.put("message", "船运意向单提交成功");
            } else {
                map.put("message", "船运意向单提交失败");
            }
        }catch (IOException exc){
            map.put("message", "物流供应商服务器访问失败，请稍后再试！");
        }
        return map;
    }

    @RequestMapping("/loadshippurposedetail")
    public Object loadshippurposedetail(@RequestParam(value = "souceid", required = true)String souceid){
        List<ShipAnchorPointInfo> anchorPointInfoList = shipanchorpointinfoMapper.getShipAnchorPointInfosBySourceId(souceid);
        List<DataBook> tempvisittypelist=visittypelist;
        return new Object(){
            public List<DataBook> visittypelist=tempvisittypelist;
            public Logisticsintention logistisintention= logisticsMapper.findBySouceId(souceid);
            public LogisticsShipFeedback shipFeedback=shipfeedbackMapper.findFeedBackBySouceId(souceid);
            public Shipintention shipintention=shipMapper.getShipIntenttionById(logistisintention.getShipid());
            public ShipAnchorPointInfo shipAnchorPointInfo=(anchorPointInfoList!=null&&anchorPointInfoList.size()>0)?anchorPointInfoList.get(0):null;
            public List<ShipAnchorPointInfo> showanchorPointInfoList=shipanchorpointinfoMapper.getShipAnchorBySourceId(souceid);
            public Pager<Returnvisit> returnvisitList= returnvisitMapper.getReturnvisitBySouceId(souceid,1,10);
        };
    }


    @RequestMapping("/loadshippurposedetailedit")
    public Object loadshippurposedetailedit(@RequestParam(value = "souceid", required = true)String souceid) {
        System.out.println("-----------loadshippurposedetailedit--------------");
        List<DataBook> tempvisittypelist=visittypelist;
        List<DataBook> tempgoodstypelist=goodstypelist;
        List<Logisticsvender> templogisticsvenders= logisticsvenderMapper.findLogisticsvenderByType(2);

        List<District> shipports= shipMapper.findByLevel(2);
        for(District shipport:shipports){
            shipport.setName(shipport.getMold()+"-"+shipport.getName());
        }
        shipports.add(new District("其它","0") );

        Logisticsintention templogistisintention= logisticsMapper.findBySouceId(souceid);
        Shipintention tempshipintention= shipMapper.getShipIntenttionById(templogistisintention.getShipid());
        District loadport= shipMapper.findByName(tempshipintention.getLoadport());
        if(loadport!=null){
            tempshipintention.setLoadport(loadport.getMold()+"-"+loadport.getName());
        }else {
            tempshipintention.setOtherloadport(tempshipintention.getLoadport());
            tempshipintention.setLoadport("其它");
        }

        District unloadport= shipMapper.findByName(tempshipintention.getUnloadport());
        if(unloadport!=null){
            tempshipintention.setUnloadport(unloadport.getMold()+"-"+unloadport.getName());
        }else {
            tempshipintention.setOtherunloadport(tempshipintention.getUnloadport());
            tempshipintention.setUnloadport("其它");
        }

        return new Object(){
            public List<DataBook> visittypelist=tempvisittypelist;
            public Logisticsintention logistisintention= templogistisintention;
            public Shipintention shipintention=tempshipintention;
            public Pager<Returnvisit> returnvisitList= returnvisitMapper.getReturnvisitBySouceId(souceid,1,10);
            public List<DataBook> goodstypelist= tempgoodstypelist;
            public List<Logisticsvender> logisticsvenders= templogisticsvenders;
            public List<District> shipportlist=shipports;
        };
    }

    @RequestMapping("submitshippurposedetailedit")
    public Object submitshippurposedetailedit(@Valid Shipintention shipintention,BindingResult result){
        System.out.println("--------submitshippurposedetailedit-------------:"+shipintention.toString());
        Map<String, Object> map = new HashMap<String, Object>();
        String loadport="";
        String unloadport="";
        if(shipintention.getLoadport().equals("其它")){
            loadport=shipintention.getOtherloadport();
        }else {
            loadport = shipintention.getLoadport().split("-")[1];
        }

        if(shipintention.getUnloadport().equals("其它")){
            unloadport = shipintention.getOtherunloadport();
        }else {
            unloadport = shipintention.getUnloadport().split("-")[1];
        }

        shipintention.setLoadport(loadport);
        shipintention.setUnloadport(unloadport);
        shipMapper.updateShipIntenttion(shipintention);

        Logisticsintention logisticsintention = logisticsMapper.getLogisIntentionByshipid(shipintention.getId());
        if(logisticsintention==null)
            throw new BusinessException("订单未找到");

        logisticsintention.setGoodsType(shipintention.getGoodsType());
        logisticsintention.setGoodsWeight(shipintention.getGoodsWeight());
        logisticsintention.setContacts(shipintention.getContacts());
        logisticsintention.setMobile(shipintention.getMobile());
        logisticsintention.setRemark(shipintention.getRemark());
        logisticsintention.setType(2);
        logisticsintention.setStatus(LogisticsStatus.TREATED_NOT.name());
        logisticsMapper.updateLogisIntention(logisticsintention);

        SubmitShipInfo submitShipInfo=new SubmitShipInfo();
        submitShipInfo.setAddTime(LocalDateTime.now().toString());
        submitShipInfo.setStartAddress(loadport + shipintention.getLoaddock());
        submitShipInfo.setArrivalAddress(unloadport + shipintention.getUnloaddock());
        submitShipInfo.setCapacity(shipintention.getGoodsWeight().intValue());
        submitShipInfo.setNo(logisticsintention.getSouceId());
        submitShipInfo.setCargoName(shipintention.getGoodsType());
        submitShipInfo.setStartDate(shipintention.getReceiptdate().toString());
        submitShipInfo.setRemark(shipintention.getRemark());
        submitShipInfo.setIP(IP);
        submitShipInfo.setMemberMobile(shipintention.getMobile());
        submitShipInfo.setMemberName(shipintention.getContacts());
        submitShipInfo.setCompanyName(shipintention.getCompanyname());
        submitShipInfo.setIsTrade(1);

        try {
            ShipRet shipRet = shipClient.submitInfo(submitShipInfo);
            if (shipRet.getRetMsg().equals("成功！")) {
                logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.MATCH_ING.name(), logisticsintention.getSouceId());
                map.put("message", "船运意向单提交成功");
            } else {
                map.put("message", "船运意向单提交失败");
            }
        }catch (IOException exc){
            map.put("message", "物流供应商服务器访问失败，请稍后再试！");
        }
        return map;
    }

    @RequestMapping("cancelshippurposedetailedit")
    public Object cancelshippurposedetailedit(@RequestParam(value = "souceid", required = true)String souceid,
                                              @RequestParam(value = "remark", required = false)String remark){
        Logisticsintention logistisintention= logisticsMapper.findBySouceId(souceid);
        if(remark==null||remark.trim().equals(""))
            throw new BusinessException("请填写备注");
        if(logistisintention==null)
            throw new NotFoundException();
        logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.NOT_TRADING.name(),souceid);
        logisticsMapper.updatCustomerremarkBySouceid(remark,souceid);
        return new Object(){
           public String message="物流订单取消成功";
        };
    }

    @RequestMapping("cancelshippurposedetail")
    public Object cancelshippurposedetail(@RequestParam(value = "souceid", required = true)String souceid,
                                              @RequestParam(value = "remark", required = false)String remark){
        Logisticsintention logistisintention= logisticsMapper.findBySouceId(souceid);
        if(remark==null||remark.trim().equals(""))
            throw new BusinessException("请填写备注");
        if(logistisintention==null)
            throw new NotFoundException();

        CancelShip cancelShip=new CancelShip();
        cancelShip.setNo(souceid);
        cancelShip.setIP(IP);
        cancelShip.setRemark(remark);
        cancelShip.setEditDate(LocalDateTime.now().toString());
        try {
            ShipRet shipRet= logisticsShipClient.cancelInfo(cancelShip);
            if(shipRet.getRetMsg().equals("成功！")) {
                logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.NOT_TRADING.name(), souceid);
                logisticsMapper.updatCustomerremarkBySouceid(remark, souceid);
            }else {
                return new Object(){
                    public String message="订单取消失败";
                };
            }
        }catch (IOException exception){
            return new Object(){
                public String message="物流供应商服务延时，请稍后再试";
            };
        }


        return new Object(){
            public String message="物流订单取消成功";
        };
    }

}
