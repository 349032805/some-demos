package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.service.Auth;
import kitt.admin.service.FileService;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.MessageNotice;
import kitt.core.util.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 15/1/20.
 */
@RestController
public class GroupBuyController {
    @Autowired
    private ProviderInfoMapper providerInfoMapper;
    @Autowired
    private GroupBuyQualificationMapper groupBuyQualifyMapper;
    @Autowired
    private GroupBuySupplyMapper groupBuySupplyMapper;
    @Autowired
    private GroupBuyOrderMapper groupBuyOrderMapper;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected CompanyMapper companyMapper;
    @Autowired
    protected FileService fileService;
    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private Auth auth;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private AdminMapper adminMapper;


    //查询团购供应商列表
    @RequestMapping("/groupBuy/supplierslist")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getProviderInfoList(int page) {
        Map map = new HashMap();
        map.put("supplier", providerInfoMapper.pageAllSuppliers(page, 10));
        return map;
    }

    //新增供应商
    @RequestMapping("/groupBuy/addProviderInfo")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object addProviderInfo(ProviderInfo providerInfo){
        Map map = new HashMap();
        String message="succeed";
        providerInfo.setCreatetime(LocalDateTime.now());
        //供应商暂时不提供删除功能
        providerInfo.setStatus(ProviderStatus.PROVIDER_FROZEN.toString());
        providerInfoMapper.addProviderInfo(providerInfo);
        ProviderInfo tempProviderInfo= providerInfoMapper.getProviderInfoById(providerInfo.getId());
        if(tempProviderInfo==null)
            message="新增供应商失败";
        map.put("message",message);
        return map;
    }

    //删除供应商
    @RequestMapping("/groupBuy/deleteProviderInfo")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object deleteProviderInfo(@RequestParam(value="providerinfoid", required = true)int providerinfoid){
        Map map = new HashMap();
        String message="succeed";
        ProviderInfo providerInfo=providerInfoMapper.getProviderInfoById(providerinfoid);
        if(providerInfo==null)
            message="未找到供应商";
        if(providerInfo.getStatus().equals(ProviderStatus.PROVIDER_FROZEN.toString())){
            message="此供应商团购正在进行中，不能删除";
        } else {
            providerInfoMapper.deleteProviderInfoByid(providerInfo.getId());
        }
        map.put("message",message);
        return map;
    }

    //查看供应商发布的团购信息列表--进行中
    @RequestMapping("/groupBuy/getGroupBuyInProcess")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getGroupBuyInProcess(int page,int providerinfoid){
        Map map = new HashMap();
        map.put("groupbuy", groupBuySupplyMapper.pageInProcessGroupBuy(providerinfoid, page, 10));
        return map;
    }

    //查看供应商发布的团购信息列表--已结束
    @RequestMapping("/groupBuy/getGroupBuyFinish")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getGroupBuyFinish(int page,int providerinfoid){
        Map map = new HashMap();
        map.put("groupbuy", groupBuySupplyMapper.pageFinishGroupBuy(providerinfoid, page, 10));
        return map;
    }

    //团购信息下架
    @RequestMapping("/groupBuy/cancelGroupBuySupply")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object cancelGroupBuySupply(@RequestParam(value="groupbuysupplyid", required = true)int groupbuysupplyid){
        Map map=new HashMap();
        String message="succeed";
        GroupBuySupply groupBuySupply= groupBuySupplyMapper.getGroupBuySupplyById(groupbuysupplyid);
        if(groupBuySupply==null)
            message="团购发布信息未找到";
        if(groupBuySupply.getStatus().equals(GroupBuySupplyStatus.GROUP_BUY_SUPPLY_INPROGRESS.toString())) {
            message = "团购进行中，不能下架";
        }else {
            groupBuySupplyMapper.updateStatusById(groupBuySupply.getId(),GroupBuySupplyStatus.GROUP_BUY_SUPPLY_GIVEUP.toString());
            groupBuySupplyMapper.deleteGroupBuySupplyById(groupBuySupply.getId());
        }
        map.put("message",message);
        return map;
    }

    //查看团购发布详细信息与相应的团购订单列表
    @RequestMapping("/groupBuy/getGroupBuySupplyDetail")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getGroupBuySupplyDetail(@RequestParam(value="groupbuyid", required = true)int groupbuyid,int page) {
        Map map = new HashMap();
        GroupBuySupply groupBuySupply = groupBuySupplyMapper.getGroupBuySupplyById(groupbuyid);
        Pager<GroupBuyOrder> pager= groupBuyOrderMapper.pageAllGroupBuyOrder(groupbuyid, page, 5);
        List<GroupBuyOrder> orderPagerList= pager.getList();
        ProviderInfo providerInfo=null;
        if(orderPagerList!=null) {
            for (GroupBuyOrder gbo : orderPagerList) {
                providerInfo = providerInfoMapper.getProviderInfoById(groupBuySupply.getProviderinfoid());
                gbo.setCompanyname(providerInfo.getProvidername());
                gbo.setDeliverydatestart(groupBuySupply.getDeliverydatestart());
                gbo.setDeliverydateend(groupBuySupply.getDeliverydateend());
            }
        }
        pager.setList(orderPagerList);
        map.put("groupbuy", groupBuySupply);
        map.put("groupbuyorder", pager);
        map.put("surplusamount",groupBuySupply.getSurplusamount());
        map.put("totalamount",groupBuySupply.getSelledamount());
        return map;
    }

    //解除资质与订单的绑定
    @RequestMapping("/groupBuy/removeGroupBuyQualify")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object removeGroupBuyQualify(@RequestParam(value = "qualificationcode", required = true)String qualificationcode,@RequestParam(value="issucceed", required = true)int issucceed){
        Map map = new HashMap();
        String message = "succeed";
        GroupBuyQualification groupBuyQualification= groupBuyQualifyMapper.getGroupBuyQualifyByCode(qualificationcode);
        if(groupBuyQualification==null)
            message="用户资质未找到";
        if(issucceed==1) {
            groupBuyQualifyMapper.updateStatusByCode(qualificationcode, QualifyStatus.QUALIFY_ACTIVE.toString());
        } else {
            groupBuyQualifyMapper.updateStatusByCode(qualificationcode, QualifyStatus.QUALIFY_CANCEL.toString());
        }
        map.put("message", message);
        return map;
    }

    /**
     * 发布商城产品，初始化页面
     * @return   返回 省份，检验机构等 下拉列表
     */
    @RequestMapping("/groupBuy/releaseGroupBuyInfo")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getSupplyInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Dictionary> inspectorgs = dictionaryMapper.getAllInspectionagencys();
        inspectorgs.add(new Dictionary(100, "inspectionagency", "其它"));
        List<DataBook> pslist= dataBookMapper.getDataBookListByType("pstype");
        map.put("deliveryregionlist", areaportMapper.getAllArea());
        map.put("inspectorgs", inspectorgs);
        map.put("coaltypelist", dictionaryMapper.getAllCoalTypes());
        map.put("pslist",pslist);
        map.put("deliverymodelist", dataBookMapper.getDataBookListByType("deliverymode"));
        map.put("traderlist", auth.getDealerList());
        return map;
    }

    //发布团购信息
    @RequestMapping("/groupBuy/releaseGroupBuySupply")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public boolean releaseGroupBuySupply(GroupBuySupply groupBuySupply){

        if(groupBuySupply.getNCV()!=null&&groupBuySupply.getNCV02()!=null){
            int NCV=groupBuySupply.getNCV().intValue();
            int NCV02=groupBuySupply.getNCV02().intValue();
            if(NCV > NCV02) { groupBuySupply.setNCV(new Integer(NCV02));  groupBuySupply.setNCV02(new Integer(NCV)) ; }
        }

        if(groupBuySupply.getGV()!=null&&groupBuySupply.getGV02()!=null){
            int GV=groupBuySupply.getGV().intValue();
            int GV02=groupBuySupply.getGV02().intValue();
            if(GV > GV02) { groupBuySupply.setGV(new Integer(GV02)); groupBuySupply.setGV02(new Integer(GV));}
        }

        if(groupBuySupply.getYV()!=null&&groupBuySupply.getYV02()!=null){
            int YV=groupBuySupply.getYV().intValue();
            int YV02=groupBuySupply.getYV02().intValue();
            if(YV > YV02) { groupBuySupply.setYV(new Integer(YV02)); groupBuySupply.setYV02(new Integer(YV));}
        }

        if(groupBuySupply.getFC()!=null&&groupBuySupply.getFC02()!=null) {
            int FC=groupBuySupply.getFC().intValue();
            int FC02=groupBuySupply.getFC02().intValue();
            if(FC > FC02) { groupBuySupply.setFC(new Integer(FC02)); groupBuySupply.setFC02(new Integer(FC)); }
        }

        if(groupBuySupply.getRS()!=null&&groupBuySupply.getRS()!=null){
            double RS=groupBuySupply.getRS().doubleValue();
            double RS02=groupBuySupply.getRS02().doubleValue();
            if(RS>RS02){groupBuySupply.setRS(new BigDecimal(RS02)); groupBuySupply.setRS02(new BigDecimal(RS)); }
        }

        if(groupBuySupply.getADS()!=null&&groupBuySupply.getADS02()!=null){
            double ADS=groupBuySupply.getADS().doubleValue();
            double ADS02=groupBuySupply.getADS02().doubleValue();
            if(ADS>ADS02){groupBuySupply.setADS(new BigDecimal(ADS02)); groupBuySupply.setADS02(new BigDecimal(ADS)); }
        }

        if(groupBuySupply.getTM()!=null&&groupBuySupply.getTM02()!=null){
            double TM=groupBuySupply.getTM().doubleValue();
            double TM02=groupBuySupply.getTM02().doubleValue();
            if(TM>TM02){groupBuySupply.setTM(new BigDecimal(TM02)); groupBuySupply.setTM02(new BigDecimal(TM)); }
        }

        if(groupBuySupply.getIM()!=null&&groupBuySupply.getIM02()!=null){
            double IM=groupBuySupply.getIM().doubleValue();
            double IM02=groupBuySupply.getIM02().doubleValue();
            if(IM>IM02){groupBuySupply.setIM(new BigDecimal(IM02)); groupBuySupply.setIM02(new BigDecimal(IM)); }
        }

        if(groupBuySupply.getADV()!=null&&groupBuySupply.getADV02()!=null){
            double ADV=groupBuySupply.getADV().doubleValue();
            double ADV02=groupBuySupply.getADV02().doubleValue();
            if(ADV>ADV02){groupBuySupply.setADV(new BigDecimal(ADV02)); groupBuySupply.setADV02(new BigDecimal(ADV)); }
        }

        if(groupBuySupply.getRV()!=null&&groupBuySupply.getRV02()!=null){
            double RV=groupBuySupply.getRV().doubleValue();
            double RV02=groupBuySupply.getRV02().doubleValue();
            if(RV>RV02){groupBuySupply.setRV(new BigDecimal(RV02)); groupBuySupply.setRV02(new BigDecimal(RV)); }
        }

        if(groupBuySupply.getASH()!= null&&groupBuySupply.getASH02()!=null){
            double ASH=groupBuySupply.getASH().doubleValue();
            double ASH02=groupBuySupply.getASH02().doubleValue();
            if(ASH>ASH02){groupBuySupply.setASH(new BigDecimal(ASH02)); groupBuySupply.setASH02(new BigDecimal(ASH)); }
        }

        groupBuySupply.setDeliverydistrict(areaportMapper.getNameById(groupBuySupply.getRegionId()));
        groupBuySupply.setDeliveryprovince(areaportMapper.getNameById(groupBuySupply.getProvinceId()));
        String portname=areaportMapper.getNameById(groupBuySupply.getPortId());
        if(portname!=null) {
            groupBuySupply.setPort(portname);
        }else {
            groupBuySupply.setPort("其它");
        }
        groupBuySupply.setTradername(adminMapper.getAdminById(groupBuySupply.getTraderid()).getName());
        groupBuySupply.setTraderphone(adminMapper.getAdminById(groupBuySupply.getTraderid()).getPhone());
        if(groupBuySupply.getPS()!=null)
        groupBuySupply.setPSName(dataBookMapper.getDataBookNameByTypeSequence("pstype",groupBuySupply.getPS()));
        if(groupBuySupply.getGroupbuysupplycode()==null){
            groupBuySupply.setStatus(GroupBuySupplyStatus.GROUP_BUY_SUPPLY_RELEASE.toString());
            groupBuySupply.setCreatetime(LocalDateTime.now());
            groupBuySupply.setGroupbuyordercount(0);
            groupBuySupply.setSurplusamount(groupBuySupply.getSupplyamount());
            groupBuySupply.setSelledamount(0);
            groupBuySupplyMapper.addGroupBuySupply(groupBuySupply);
        } else {
            groupBuySupplyMapper.updateGroupBuySupply(groupBuySupply);
        }
        return true;
    }

    //团购发布校验页面
    @RequestMapping("/groupBuy/getGroupBuyForCheck")
    public Object getGroupBuyForCheck(String groupbuysupplycode){
        GroupBuySupply groupbuy = groupBuySupplyMapper.getGroupBuySupplyByCode(groupbuysupplycode);
        Map map = new HashMap();
        map.put("groupbuy", groupbuy);
        return map;
    }

    //可用不可用
    @RequestMapping("/groupBuy/resetQualifyStatus")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public boolean resetQualifyStatus(String isOk,String qualificationcode,String ordercode){
        if(isOk.equals("true")){
            groupBuyQualifyMapper.updateStatusByCode(qualificationcode,QualifyStatus.QUALIFY_ACTIVE.toString());
        }else {
            groupBuyQualifyMapper.updateStatusByCode(qualificationcode, QualifyStatus.QUALIFY_CANCEL.toString());
        }
        return true;
    }

    //查询团购资质列表
    @RequestMapping("/groupBuy/getGroupBuyQualifyList")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.LegalPersonnel)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Admin)
    public Object getGroupBuyQualifyList(String status,int page){
        Map map = new HashMap();
        Pager<GroupBuyQualification> pager=null;
        if(status.equals("wait")){
            pager = groupBuyQualifyMapper.pageGroupBuyQualifyByStatus(QualifyStatus.QUALIFY_APPLY.toString(), page, 10);
        }else if(status.equals("pass")){
            pager = groupBuyQualifyMapper.pageGroupBuyQualifyPass(page, 10);
        }else {
            pager = groupBuyQualifyMapper.pageGroupBuyQualifyByStatus(QualifyStatus.QUALIFY_NOT_ENOUGH.toString(), page, 10);
        }

        List<GroupBuyQualification> groupBuyQualifications= pager.getList();
        if(groupBuyQualifications!=null) {
            for (GroupBuyQualification gbq : groupBuyQualifications) {
                User user = userMapper.getUserById(gbq.getUserid());
                if(user!=null) {
                    if (user.getNickname() != null)
                        gbq.setUsername(user.getNickname());
                    gbq.setUserphone(user.getSecurephone());
                }
            }
            pager.setList(groupBuyQualifications);
        }
        map.put("groupBuyQualifications",pager);
        return map;
    }

    //查看团购资质详细信息
    @RequestMapping("/groupBuy/getGroupBuyQualifyDetail")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getGroupBuyQualifyDetail(String code){
        Map map = new HashMap();
        GroupBuyQualification groupBuyQualification= groupBuyQualifyMapper.getGroupBuyQualifyByCode(code);
        if(groupBuyQualification!=null){
            User user = userMapper.getUserById(groupBuyQualification.getUserid());
            if(user!=null){
                groupBuyQualification.setUserphone(user.getSecurephone());
                Company company= companyMapper.getCompanyByUserid(user.getId());
                if(company!=null){
                    groupBuyQualification.setCompanyname(company.getName());
                }
            }
        }
        map.put("groupBuyQualification",groupBuyQualification);
        return map;
    }

    //提交团购资质审核
    @RequestMapping("/groupBuy/confirmGroupBuyQualifyDetail")
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Admin)
    public Object confirmGroupBuyQualifyDetail(String qualifyCode, String comment, BigDecimal margins){
        Map map = new HashMap();
        String message="";
        GroupBuyQualification groupBuyQualification= groupBuyQualifyMapper.getGroupBuyQualifyByCode(qualifyCode);
        if (groupBuyQualification==null && (groupBuyQualification.getStatus().equals("QUALIFY_APPLY") || groupBuyQualification.getStatus().equals("QUALIFY_NOT_ENOUGH"))) {
            message="fail";
        } else {
             double value= margins.doubleValue();
            if (value>=100000) {
                groupBuyQualification.setStatus(QualifyStatus.QUALIFY_ACTIVE.toString());
                String phone = userMapper.getUserById(groupBuyQualification.getUserid()).getSecurephone();
                final String content = "您好，您的编号为" + qualifyCode + "的团购资格申请审核已通过，可以正常参与易煤网团购活动，注意：一个团购资格每次只能参加一次团购活动。";
                MessageNotice.CommonMessage.noticeUser(phone, content);
            } else {
                groupBuyQualification.setStatus(QualifyStatus.QUALIFY_NOT_ENOUGH.toString());
                String phone = userMapper.getUserById(groupBuyQualification.getUserid()).getSecurephone();
                final String content = "您好，您的编号为" + qualifyCode + "的团购资格审核未通过，审核反馈为：" + comment + ",如需帮助，请拨打客服热线400-960-1180。";
                MessageNotice.CommonMessage.noticeUser(phone, content);
            }
            groupBuyQualification.setComment(comment);
            groupBuyQualification.setMargins(margins);
            groupBuyQualifyMapper.updateGroupBuyQualifyById(groupBuyQualification);
            message="succeed";
        }
        map.put("message",message);
        return map;
    }

    //查询申请放弃团购资质列表
    @RequestMapping("/groupBuy/getGiveupQualifyList")
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.LegalPersonnel)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getGiveupQualifyList(int page,int userid){
        Map map = new HashMap();
        Pager<GroupBuyQualification> pagerResult = groupBuyQualifyMapper.pageGroupBuyQualifyByStatusId( QualifyStatus.QUALIFY_GIVEUP.toString(),userid,page,10);
        map.put("pagerResult",pagerResult);
        return map;
    }

    //查询申请放弃团购资质列表
    @RequestMapping("/groupBuy/getinanceGiveupQualifyList")
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.LegalPersonnel)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getFinanceGiveupQualifyList(int page){
        Map map = new HashMap();
        Pager<GroupBuyQualification> pagerResult = groupBuyQualifyMapper.pageAllGroupBuyQualifyByStatusId(QualifyStatus.QUALIFY_GIVEUP.toString(), page, 10);
        map.put("pagerResult",pagerResult);
        return map;
    }

    //审批放弃团购资质
    @RequestMapping("/groupBuy/confirmGiveupQualify")
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.Finance)
    public boolean confirmGiveupQualify(String qualifyCode){
        boolean success=false;
        GroupBuyQualification groupBuyQualification= groupBuyQualifyMapper.getGroupBuyQualifyByCode(qualifyCode);
        if(groupBuyQualification.getStatus().equals(QualifyStatus.QUALIFY_GIVEUP.toString())) {
            groupBuyQualifyMapper.updateStatusByCode(qualifyCode, QualifyStatus.QUALIFY_GIVEUPED.toString());
            success = true;
        }
        return success;
    }

    //保存团购发布图片
    @RequestMapping("/groupBuy/saveGroupBuyPic")
    public Object saveGroupBuyPic(@RequestParam("file") MultipartFile file) throws Exception{
        String filePath = fileService.uploadPicture(file);
        Map map = new HashMap<>();
        map.put("filePath",filePath);
        return map;
    }

    //查询申请退款团购资质列表
    @RequestMapping("/groupBuy/getFinanceRefundQualifyList")
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.LegalPersonnel)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getFinanceRefundQualifyList(int page){
        Map map = new HashMap();
        Pager<GroupBuyQualification> pagerResult = groupBuyQualifyMapper.pageAllGroupBuyQualifyByStatusId(QualifyStatus.QUALIFY_APPLY_REFUND.toString(), page, 10);
        map.put("pagerResult",pagerResult);
        return map;
    }

    //审批退款团购资质
    @RequestMapping("/groupBuy/confirmRefundQualify")
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.Finance)
    public Object confirmRefundQualify(String qualifyCode){
        boolean success=false;
        GroupBuyQualification groupBuyQualification= groupBuyQualifyMapper.getGroupBuyQualifyByCode(qualifyCode);
        if(groupBuyQualification.getStatus().equals(QualifyStatus.QUALIFY_APPLY_REFUND.toString())) {
            groupBuyQualifyMapper.updateStatusByCode(qualifyCode, QualifyStatus.QUALIFY_REFUNDED.toString());
            success = true;
        }
        Map map = new HashMap<>();
        map.put("success",success);
        return map;
    }

    //查询已经结束团购资质列表-已放弃
    @RequestMapping("/groupBuy/getFinanceGiveupedQualifyList")
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.LegalPersonnel)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getFinanceGiveupedQualifyList(int page){
        Map map = new HashMap();
        Pager<GroupBuyQualification> pagerResult = groupBuyQualifyMapper.pageAllGroupBuyQualifyByStatus(QualifyStatus.QUALIFY_GIVEUPED.toString(), page, 10);
        map.put("pagerResult",pagerResult);
        return map;
    }

    //查询已经结束团购资质列表-已退款
    @RequestMapping("/groupBuy/getFinanceRefundedQualifyList")
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.LegalPersonnel)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getFinanceRefundedQualifyList(int page){
        Map map = new HashMap();
        Pager<GroupBuyQualification> pagerResult = groupBuyQualifyMapper.pageAllGroupBuyQualifyByStatus(QualifyStatus.QUALIFY_REFUNDED.toString(), page, 10);
        map.put("pagerResult",pagerResult);
        return map;
    }
}
