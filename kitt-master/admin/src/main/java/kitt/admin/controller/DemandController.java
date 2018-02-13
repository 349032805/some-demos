package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.DemandService;
import kitt.core.domain.AuthenticationRole;
import kitt.core.domain.Demand;

import kitt.core.persistence.DealerMapper;
import kitt.core.persistence.DemandMapper;
import kitt.core.persistence.MydemandMapper;
import kitt.core.persistence.QuoteMapper;

import kitt.core.domain.User;
import kitt.core.persistence.*;
import kitt.ext.mybatis.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fanjun on 14-12-10.
 */
@RestController
public class DemandController {
    @Autowired
    protected DemandMapper demandMapper;
    @Autowired
    protected MydemandMapper mydemandMapper;
    @Autowired
    protected QuoteMapper quoteMapper;
    @Autowired
    private DealerMapper dealerMapper;
    @Autowired
    private DemandService demandService;
    @Autowired
    private Auth auth;
    @Autowired
    private UserMapper userMapper;

    private final String waitStatus="待审核";
    private final String verifyPass="审核通过";

    //需求列表-待审核
    @RequestMapping("/demand/wait")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object waitList(@RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")Integer region,
                           @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")Integer province,
                           @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")Integer harbour,
                           @RequestParam(value = "content", required = false, defaultValue = "")String content,
                           @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
                           @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                           @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                           @RequestParam(value = "page", required = false, defaultValue = "1")int page) {
        return demandService.doGetDemandListMethod(region, province, harbour, content, clienttype,  startDate, endDate, "待审核", page, 10);
    }

    //需求列表-审核通过
    @RequestMapping("/demand/pass")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object passList(@RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")Integer region,
                           @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")Integer province,
                           @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")Integer harbour,
                           @RequestParam(value = "content", required = false, defaultValue = "")String content,
                           @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
                           @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                           @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                           @RequestParam(value = "page", required = false, defaultValue = "1")int page) {
        return demandService.doGetDemandListMethod(region, province, harbour, content, clienttype, startDate, endDate, "审核通过", page, 10);
    }

    //需求列表-审核未通过
    @RequestMapping("/demand/fail")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object failList(@RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")Integer region,
                           @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")Integer province,
                           @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")Integer harbour,
                           @RequestParam(value = "content", required = false, defaultValue = "")String content,
                           @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
                           @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                           @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                           @RequestParam(value = "page", required = false, defaultValue = "1")int page) {
        return demandService.doGetDemandListMethod(region, province, harbour, content, clienttype, startDate, endDate, "审核未通过", page, 10);
    }

    @RequestMapping("/demand/checkExportCount")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doCheckExportDemandCount(@RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")Integer region,
                                           @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")Integer province,
                                           @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")Integer harbour,
                                           @RequestParam(value = "content", required = false, defaultValue = "")String content,
                                           @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
                                           @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                                           @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                                           @RequestParam(value = "checkstatus", required = false, defaultValue = "审核未通过")String checkstatus){
        return demandService.doCheckExportDemandCountMethod(region, province, harbour, content, clienttype, startDate, endDate, checkstatus);
    }

    @RequestMapping("/demand/exportExcel")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public void doExportDemandList(@RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")Integer region,
                                   @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")Integer province,
                                   @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")Integer harbour,
                                   @RequestParam(value = "content", required = false, defaultValue = "")String content,
                                   @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
                                   @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                                   @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                                   @RequestParam(value = "checkstatus", required = false, defaultValue = "审核未通过")String checkstatus,
                                   HttpServletResponse response,
                                   HttpServletRequest request) throws IOException {
        demandService.doExportDemandList(region, province, harbour, Where.$like$(content), clienttype, startDate, endDate, checkstatus, response, request);
    }

    //需求列表的查看
    @RequestMapping("/demand/view")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object demandView(@RequestParam(value = "demandcode", required = true)String demandcode,
                             @RequestParam(value = "showType", required = false)String showType) {
        Map<String,Object> map = new HashMap<String,Object>();
        Demand demand = demandMapper.getDemandByDemandcode(demandcode);
        if(demand == null) throw new NotFoundException();
        User user = userMapper.getUserById(demand.getUserid());
        if(user == null) throw new NotFoundException();
        map.put("traderid", user.getTraderid());
        map.put("demand",demand);
        map.put("showType", showType);
        //待审核时， 查找交易员列表
        if(waitStatus.equals(demand.getCheckstatus())){
            map.put("dealerList", auth.getDealerList());
        }
        //审核通过时， 显示交易员， 查找交易员
        if(verifyPass.equals(demand.getCheckstatus())){
            map.put("dealer", dealerMapper.findDealerById(demand.getTraderid()));
        }
        return map;
    }

    //保存审核状态和备注
    @RequestMapping("/demand/modifyCheckstatusAndComment")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Admin)
    public boolean modifyCheckstatusAndComment(@RequestParam(value = "demandcode", required = true)String demandcode,
                                               @RequestParam(value = "checkstatus", required = true)String checkstatus,
                                               @RequestParam(value = "comment", required = false, defaultValue = "")String comment,
                                               @RequestParam(value = "dealerId",required = false, defaultValue = "")Integer dealerId){
        Demand demand = demandMapper.getDemandByDemandcode(demandcode);
        if(demand == null) throw new NotFoundException();
        User user = userMapper.getUserById(demand.getUserid());
        if(user == null) throw new NotFoundException();
        if(!user.getVerifystatus().equals("审核通过")){ throw new BusinessException("该客户的公司信息尚未审核通过，不能审核通过！");}
        if(!"待审核".equals(demand.getCheckstatus())) throw new BusinessException("服务器出错，请联系技术人员！");
        return demandService.doVerifyDemandMethod(user, demandcode, checkstatus, dealerId, comment);

    }

    //取消审核通过的需求
    @RequestMapping("/demand/cancelDemand")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public boolean cancelDemand(String demandcode) {
        Demand demand = demandMapper.getDemandByDemandcode(demandcode);
        if(demand == null) throw new NotFoundException();
        return demandService.doCancelDemandByAdmin(demand);
    }


    @RequestMapping("/demand/checkDemandTraderCorrect")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doCheckDemandTraderCorrect(@RequestParam(value = "demandcode", required = true)String demandcode,
                                             @RequestParam(value = "dealerId", required = true)int dealerid){
        Demand demand = demandMapper.getDemandByDemandcode(demandcode);
        if(demand == null) throw new NotFoundException();
        if(userMapper.getUserById(demand.getUserid()).getTraderid() == dealerid){
            return new Object(){
                public boolean success = true;
            };
        } else{
            return new Object(){
                public boolean success = false;
                public String error = "该客户的交易员已经更改，请刷新页面后重新审核！";
            };
        }
    }

}

