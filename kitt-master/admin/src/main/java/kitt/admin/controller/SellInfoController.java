package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.BuyMethod;
import kitt.admin.service.Session;
import kitt.admin.service.UserService;
import kitt.core.bl.BuyService;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.MessageNotice;
import kitt.ext.mybatis.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 14/12/10.
 */
@RequestMapping("/supply")
@RestController
public class SellInfoController {
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private PriceLadderMapper priceLadderMapper;
    @Autowired
    private DealerMapper dealerMapper;
    @Autowired
    private Auth auth;
    @Autowired
    private BuyService buyService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BuyMethod buyMethod;
    @Autowired
    private Session session;

    @RequestMapping("/pass")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getPassList(@RequestParam(value = "productNo", required = false, defaultValue = "")String productNo,
                              @RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")int region,
                              @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")int province,
                              @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")int harbour,
                              @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
                              @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                              @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                              @RequestParam(value = "page", required = false, defaultValue = "1")int page) {
        return buyMethod.doGetSellInfoProductList(region, province, harbour, productNo, clienttype, startDate, endDate, EnumSellInfo.VerifyPass, null, 1, page, 10);
    }

    @RequestMapping("/fail")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getFailList(@RequestParam(value = "productNo", required = false, defaultValue = "")String productNo,
                              @RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")int region,
                              @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")int province,
                              @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")int harbour,
                              @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                              @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                              @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
                              @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return buyMethod.doGetSellInfoProductList(region, province, harbour, productNo, clienttype, startDate, endDate, EnumSellInfo.VerifyNotPass, null, 1, page, 10);
    }

    @RequestMapping("/wait")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getWaitList(@RequestParam(value = "productNo", required = false, defaultValue = "")String productNo,
                              @RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")Integer region,
                              @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")Integer province,
                              @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")Integer harbour,
                              @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                              @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                              @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
                              @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return buyMethod.doGetSellInfoProductList(region, province, harbour, productNo, clienttype, startDate, endDate, EnumSellInfo.WaitVerify, null, 1, page, 10);
    }

    @RequestMapping("/date")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getOutDateList(@RequestParam(value = "productNo", required = false, defaultValue = "")String productNo,
                                 @RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")Integer region,
                                 @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")Integer province,
                                 @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")Integer harbour,
                                 @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                                 @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                                 @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
                                 @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return buyMethod.doGetSellInfoProductList(region, province, harbour, productNo, clienttype, startDate, endDate, EnumSellInfo.OutOfDate, null, 1, page, 10);
    }

    @RequestMapping("/checkExportCount")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doCheckExportSellInfoCount(@RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")int region,
                                             @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")int province,
                                             @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")int harbour,
                                             @RequestParam(value = "productNo", required = false, defaultValue = "")String productNo,
                                             @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                                             @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                                             @RequestParam(value = "clienttype", required = false, defaultValue = "0") int clienttype,
                                             @RequestParam(value = "status", required = true)EnumSellInfo status){
        return buyMethod.doCheckExportSellInfoCountMethod(region, province, harbour, Where.$like$(productNo), clienttype, startDate, endDate, status, null, 1);
    }

    @RequestMapping("/exportExcel")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public void doExportSellList(@RequestParam(value = "productNo", required = false, defaultValue = "") String productNo,
                                 @RequestParam(value = "deliveryRegion", required = false, defaultValue = "0") int region,
                                 @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0") int province,
                                 @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0") int harbour,
                                 @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
                                 @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate,
                                 @RequestParam(value = "clienttype", required = false, defaultValue = "0") int clienttype,
                                 @RequestParam(value = "status", required = true)EnumSellInfo status,
                                 HttpServletResponse response, HttpServletRequest request) throws IOException {
        buyMethod.doExportSellInfoList(region, province, harbour, Where.$like$(productNo), clienttype, startDate, endDate, status, null, 1, response, request);
    }

    @RequestMapping("/detail")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showDetail(@RequestParam(value="id", required = true)int id,
                             @RequestParam(value="type", required = false)String type,
                             @RequestParam(value="showType", required = false)String showType){
        Map<String, Object> map = new HashMap<String, Object>();
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if (sellInfo == null) throw new NotFoundException();
        if(sellInfo.getSellerid() != 0) {
            User seller = userMapper.getUserById(sellInfo.getSellerid());
            if (seller == null) throw new NotFoundException();
            map.put("traderid", seller.getTraderid());
        }
        map.put("sellInfo", sellInfo);
        map.put("showType", showType);
        List<PriceLadder> priceLadders = priceLadderMapper.getPriceLadderListBySellinfoId(id);
        switch (priceLadders.size()){
            case 5:
                map.put("jtj05Obj", priceLadders.get(4));
            case 4:
                map.put("jtj04Obj", priceLadders.get(3));
            case 3:
                map.put("jtj03Obj", priceLadders.get(2));
            case 2:
                map.put("jtj01Obj", priceLadders.get(0));
                map.put("jtj02Obj", priceLadders.get(1));
                break;
            default:
                break;
        }
        //待审核供应信息显示交易员信息
        if(EnumSellInfo.WaitVerify.toString().equals(type)){
            map.put("dealerList", auth.getDealerList());
        }
        if(EnumSellInfo.VerifyPass.toString().equals(type)){
            //查找交易员
            map.put("dealer", dealerMapper.findDealerById(sellInfo.getTraderid()));
        }
        map.put("success", true);
        map.put("type", type);
        return map;
    }

    /**
     * 审核供应信息
     * @param id             供应信息id
     * @param version        供应信息version
     * @param checkResult    审核结果， 分为：审核通过， 审核未通过 两种
     * @param comment        审核备注
     * @param dealerId       审核分配的 交易员 id
     * @return
     */
    @RequestMapping("/checkInfo")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public boolean doVerifySupply(@RequestParam(value = "id", required = true)int id,
                                 @RequestParam(value = "version", required = true)int version,
                                 @RequestParam(value = "checkResult", required = true)int checkResult,
                                 @RequestParam(value = "comment", required = false)String comment,
                                 @RequestParam(value = "dealerId", required = false)Integer dealerId) {
        SellInfo sellinfo = buyMapper.getSellInfoById(id);
        SupplyVerify supplyVerify = buyMapper.getSupplyVerifyBySellinfoId(id);
        if (sellinfo == null || supplyVerify == null) throw new NotFoundException();
        if(sellinfo.getVersion() != version) throw new BusinessException("审核失败, 请刷新页面重试! ");
        User user = userMapper.getUserById(sellinfo.getSellerid());
        if(user == null) throw new NotFoundException();
        if(!user.getVerifystatus().equals("审核通过")) throw new BusinessException("该客户的公司信息尚未审核通过，不能审核通过！");
        if (!EnumSellInfo.WaitVerify.equals(sellinfo.getStatus())) throw new BusinessException("服务器出错，请联系技术人员！");
        sellinfo.setRemarks(comment);
        return verifySellinfo(checkResult, sellinfo, dealerId, supplyVerify);
    }

    @Transactional
    public boolean verifySellinfo(int checkResult, SellInfo sellInfo, Integer dealerId, SupplyVerify supplyVerify) {
        String phone=userMapper.findUserPhoneBySupplyId(sellInfo.getId());
        Assert.notNull("phone","phone must be not null");
        if (checkResult == 0) {
            Admin dealer = dealerMapper.findDealerById(dealerId);
            try {
                buyService.verifySellInfo(dealer, EnumSellInfo.VerifyPass, sellInfo, supplyVerify, session.getAdmin().getName());
            } catch (SQLExcutionErrorException e) {
                auth.doOutputErrorInfo("此供应信息(sellinfo)已被客户修改，供应信息id=" + sellInfo.getId() + ", version=" + sellInfo.getVersion());
                throw new BusinessException("此供应信息已被客户修改，请刷新页面后重试");
            }
            User seller = userMapper.getUserById(sellInfo.getSellerid());
            if(seller == null) throw new NotFoundException();
            if(seller.getTraderid() == null){
                if(!userService.doAddUpdateUserTraderMethod(seller.getId(), dealerId)) {
                    throw new BusinessException("服务器出错，请联系技术人员！");
                }
            }
            final String content = "您好,您的供应" + sellInfo.getPid() + "审核已通过!";
            MessageNotice.CommonMessage.noticeUser(phone, content);
        } else if (checkResult == 1) {
            try {
                buyService.verifySellInfo(null, EnumSellInfo.VerifyNotPass, sellInfo, supplyVerify, session.getAdmin().getName());
            } catch (SQLExcutionErrorException e) {
                auth.doOutputErrorInfo("此供应信息(sellinfo)已被客户修改，供应信息id=" + sellInfo.getId() + ", version=" + sellInfo.getVersion());
                throw new BusinessException("此供应信息已被客户修改，请刷新页面后重试");
            }
            final String content = "您好,您的供应" + sellInfo.getPid() + "审核未通过，审核反馈为:" + sellInfo.getRemarks() + ",如需帮助，请拨打客服热线400-960-1180。";
            MessageNotice.CommonMessage.noticeUser(phone, content);
        }
        return true;
    }

    /**
     * 取消供应信息
     * @param id      供应信息 id
     * @param version 供应信息 version
     * @return        true or false
     */
    @RequestMapping("/cancelSupply")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doCancelSupply (@RequestParam(value = "id", required = true) int id,
                                  @RequestParam(value = "version", required = true) int version){
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if (sellInfo == null) throw new NotFoundException();
        if (!buyService.changeSellInfoStatus(id, version, EnumSellInfo.Canceled, "管理员(" + session.getAdmin().getJobnum() + ")取消供应.")){
            auth.doOutputErrorInfo("取消供应信息(sellinfo)出错，供应信息id=" + id + ", version=" + version);
            throw new BusinessException("取消供应信息失败，请刷新页面后重试");
        }
        return true;
    }

    @RequestMapping("/checkSupplyTraderCorrect")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doCheckSupplyTraderCorrect(@RequestParam(value = "id", required = true)int id,
                                             @RequestParam(value = "dealerId", required = true)int dealerId){
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if(sellInfo == null) throw new NotFoundException();
        if(userMapper.getUserById(sellInfo.getSellerid()).getTraderid() == dealerId){
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
