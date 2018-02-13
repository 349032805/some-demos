package kitt.site.controller;

import com.google.common.collect.Collections2;
import freemarker.template.TemplateException;
import kitt.core.bl.BuyService;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.libs.LogisticsShipClient;
import kitt.core.libs.logistics.CancelShip;
import kitt.core.libs.logistics.ShipRet;
import kitt.core.persistence.*;
import kitt.core.service.FileStore;
import kitt.core.service.LogisticsWebService;
import kitt.core.service.MessageNotice;
import kitt.core.util.AuthMethod;
import kitt.core.util.PageQueryParam;
import kitt.ext.mybatis.Where;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.Client;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


/**
 * Created by fanjun on 14-12-13.
 * 个人中心
 * 包括我的订单,账号管理,我的关注,我的发布,我的报价,五个部分
 */
@LoginRequired
@Controller
public class PersonalCenterController extends JsonController {
    @Autowired
    private Session session;
    @Autowired
    protected QuoteMapper quoteMapper;
    @Autowired
    protected CompanyMapper companyMapper;
    @Autowired
    protected BuyMapper buyMapper;
    @Autowired
    protected BuyService buyService;
    @Autowired
    protected MydemandMapper mydemandMapper;
    @Autowired
    protected DemandMapper demandMapper;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected OrderMapper orderMapper;
    @Autowired
    protected MyInterestMapper myInterestMapper;
    @Autowired
    protected PaymentMapper paymentMapper;
    @Autowired
    private Auth auth;
    @Autowired
    protected DataBookMapper dataBookMapper;
    @Autowired
    protected MyInterestMapper interestMapper;
    @Autowired
    private BuyMethod buyMethod;
    @Autowired
    private PriceLadderMapper priceLadderMapper;
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    protected FileService fileService;
    @Autowired
    protected FileStore fileStore;
    @Autowired
    private TenderApplyService tenderApplyService;
    @Autowired
    private MytenderMapper mytenderMapper;
    @Autowired
    private TenderPayMentMapper tenderPayMentMapper;
    @Autowired
    private TenderPaymentService tenderPaymentService;
    @Autowired
    private TenderDeclarationService tenderDeclarationService;
    @Autowired
    private BidMapper bidMapper;
    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private LogisticsMapper logisticsMapper;
    @Autowired
    private TenderitemMapper tenderitemMapper;
    @Autowired
    private LogisticsfeedbackMapper logisticsfeedbackMapper;
    @Autowired
    private RegionMapper regionMapper;
    @Autowired
    private LogisticsshipfeedbackMapper logisticsshipfeedbackMapper;
    @Autowired
    private RegionYMMapper regionYMMapper;
    @Autowired
    private AuthMethod authMethod;
    @Autowired
    private LogisticsWebService logisticsWebService;
    @Autowired
    private shipanchorpointinfoMapper shipfoMapper;
    @Autowired
    private LogisticsShipClient logisticsShipClient;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    @RequestMapping(value = "/account/individualCenter")
    public String individualCenter(Map<String, Object> model) {
        doGetAccountInfo(model);
        return "person/accountInfo";
    }


    //我的招标---选标测试路径
    @RequestMapping(value = "/account/testBid")
    public String testBid() {
        return "person/myStandardSelection";
    }

    //账号信息
    @RequestMapping(value = "/account/accountInfo")
    public String accountInfo(Map<String, Object> model) {
        doGetAccountInfo(model);
//        return "individualCenter";
        return "person/accountInfo";
    }

    public void doGetAccountInfo(Map<String, Object> model) {
        Company company = companyMapper.getCompanyByUserid(session.getUser().getId());
        User user = userMapper.getUserByPhone(session.getUser().getSecurephone());
        model.put("personPhone", session.getUser().getSecurephone());
        model.put("company", company);
        model.put("users", user);
    }

    //认证公司信息
    @RequestMapping(value = "/account/getMyAccount")
    public String Certification(Map<String, Object> model) {
        model.put("certification", "certification");
        //获取所有分类好的省
        List<District> moldPList=regionYMMapper.getDistinctMold(1,null);
        if(moldPList!=null&&moldPList.size()>0){
            for(District d:moldPList){
                d.setRegionList(regionYMMapper.getregionymsByMold(d.getMold(),null, 1));
            }
        }
        model.put("provinceList",moldPList);
//        return "individualCenter";
        return "person/companyInfo";
    }

    //账号安全
    @RequestMapping(value = "/account/accountSecurity")
    public String accountSecurity(@Client ClientInfo clientInfo, Map<String, Object> model, HttpServletResponse response){
        response.addHeader("Cache-Control", "no-store, must-revalidate");
        response.addHeader("Expires", "Thu, 01 Jan 1970 00:00:01 GMT");
        model.put("accountSecurity","accountSecurity");
        model.put("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(session.getUser().getSecurephone(), clientInfo.getIP()));
        return "/person/accountSecurity";
    }

    //个人中心,我的需求--lich
    @RequestMapping(value = "/account/getMyDemand")
    public String myRelease(
            @RequestParam(value = "demandcode", required = false,defaultValue = "") String demandcode,
            @RequestParam(value = "coaltype", required = false,defaultValue = "全部") String coaltype,
            @RequestParam(value = "status", required = false,defaultValue = "全部") String status,
            @RequestParam(value = "releasetimeStart", required = false,defaultValue = "") String releasetimeStart,
            @RequestParam(value = "releasetimeEnd", required = false,defaultValue = "") String releasetimeEnd,
            @RequestParam(value = "quoteenddateStart", required = false,defaultValue = "") String quoteenddateStart,
            @RequestParam(value = "quoteenddateEnd", required = false,defaultValue = "") String quoteenddateEnd,
            @RequestParam(value = "ordercol", required = false,defaultValue = "releasetime") String ordercol,
            @RequestParam(value = "sortflag", required = false,defaultValue = "0") int sortflag, //0降序,1升序
            PageQueryParam param, Map<String, Object> model) {
        int demandTotalCount = mydemandMapper.countAllMydemandsBy(session.getUser().getId(),
                demandcode, coaltype, status, releasetimeStart, releasetimeEnd, quoteenddateStart, quoteenddateEnd); //总数
        param.setCount(demandTotalCount);
        List<Mydemand> demandList = mydemandMapper.getTurnpageWithUserid(session.getUser().getId(),
                demandcode, coaltype, status, releasetimeStart, releasetimeEnd, quoteenddateStart, quoteenddateEnd,
                param.getIndexNum(), param.getPagesize(), ordercol, sortflag);
//        int totalPage = demandTotalCount / param.getPagesize();
//        totalPage = demandTotalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
//        param.setTotalPage(totalPage);
//        if(demandTotalCount % param.getPagesize() == 0&&param.getPage()!=1){
//            param.setPage(param.getPage()-1);
//        }

        model.put("demandList", demandList);
        model.put("demandTotalCount", demandTotalCount);
        model.put("demandPagesize", param.getPagesize());
        model.put("pageNumber", param.getPage());
        //
        model.put("demandcode", demandcode);
        model.put("coaltype", coaltype);
        model.put("status", status);
        model.put("releasetimeStart", releasetimeStart);
        model.put("releasetimeEnd", releasetimeEnd);
        model.put("quoteenddateStart", quoteenddateStart);
        model.put("quoteenddateEnd", quoteenddateEnd);
        model.put("statusList", getMyDemandStatusList());
        model.put("ordercol", ordercol);
        model.put("sortflag", sortflag);
//        return "individualCenter";
        return "person/myDemand";
    }



    //我的发布-需求查看
    /*
        resume: 审核未通过时的重新发布
        chooseQuote: 匹配中的选择报价
     */
    @RequestMapping(value = "/account/viewMyDemand")
    public String viewMyDemand(String demandcode, String reqsource, String resume, String chooseQuote, Map<String, Object> model) {
        Demand demand = null;
        if (demandcode != null) {
            if (resume != null && resume != "") {
                model.put("resume", resume);
            }
            if (chooseQuote != null && chooseQuote != "") {
                model.put("chooseQuote", chooseQuote);
            }
            demand = demandMapper.getDemandByDemandcodeAndUserid(demandcode, session.getUser().getId());
            if (demand == null) {
                throw new NotFoundException();
            }
            //所有对应此需求的报价列表
            if (reqsource != null && reqsource != "") {
                if (reqsource.equals("myrelease")) {
                    List<Quote> quoteList = quoteMapper.getQuoteByDemandcode(demandcode);
                    if (quoteList != null && quoteList.size() > 0) {
                        model.put("quoteList", quoteList);
                        model.put("myDemand_status", mydemandMapper.getMydemandByDemandcode(demandcode).getStatus());
                    }
                }
            }
        }
        model.put("demand", demand);
        model.put("reqsource", reqsource);
//        return "checkDemandInfo";
        return "checkDemand";
    }

    //我的发布-需求删除
    @RequestMapping(value = "/account/deleteMydemand")
    @ResponseBody
    public boolean deleteMydemand(String demandcode){
        if(demandcode != null){
            mydemandMapper.deleteMyDemandByDemandcode(demandcode,session.getUser().getId());
            demandMapper.modifyIsdeleteByDemandcodeAndUserid(demandcode, session.getUser().getId());
        }
        return true;
    }

    //我的需求-取消发布
    @RequestMapping(value = "/account/cancelMydemand")
    @ResponseBody
    public Object cancelMydemand(String demandcode){
        String message = "success";
        if (demandcode != null) {
            Demand demand = demandMapper.getDemandByDemandcodeAndUserid(demandcode, session.getUser().getId());
            if (demand.getCheckstatus().equals("待审核")) {
                //将主表的需求状态设置为已删除,保留信息
                demandMapper.modifyIsdeleteByDemandcodeAndUserid(demandcode, session.getUser().getId());
                //将我的需求表的状态改为交易结束
                mydemandMapper.modifyStatusByDemandcodeAndUserid("交易结束", demandcode, session.getUser().getId());
                //如果针对需求有报价,将报价状态改为未中标
                List<Quote> quoteList = quoteMapper.getQuoteByDemandcode(demandcode);
                if (quoteList != null && quoteList.size() > 0) {
                    for (Quote quote : quoteList) {
                        quoteMapper.modifyStatusByQuoteid("未中标", quote.getId());
                    }
                }
            } else {
                message = "hasChecked";
            }
        }
        Map map = new HashMap<>();
        map.put("message",message);
        return map;
    }

    public void showMyOrder(int pagesize, PageQueryParam param, Map<String, Object> model) {
        int countBuyGo = orderMapper.countSixStatusOrdersBuy(session.getUser().getId(),
                EnumOrder.WaitPayment, EnumOrder.WaitVerify, EnumOrder.VerifyPass,
                EnumOrder.VerifyNotPass, EnumOrder.WaitBalancePayment, EnumOrder.MakeMatch);
        param.setCount(countBuyGo);
        List<Order> orderBuyGoingList = orderMapper.getSixStatusOrdersBuy(session.getUser().getId(),
                EnumOrder.WaitPayment, EnumOrder.WaitVerify, EnumOrder.VerifyPass,
                EnumOrder.VerifyNotPass, EnumOrder.WaitBalancePayment, EnumOrder.MakeMatch,
                pagesize, param.getIndexNum());
        model.put("pageNumBuyGo", param.getPage());
        model.put("pagesizeBuyGo", pagesize);
        model.put("countBuyGo", countBuyGo);
        model.put("orderBuyGoList", orderBuyGoingList);
    }

    //我的订单-买货订单-进行中
    @RequestMapping(value = "/account/getMyOrders")
    public String myOrder(PageQueryParam param, Map<String, Object> model) {
        showMyOrder(param.getPagesize(), param, model);
//        return "individualCenter";
        return "person/buyOrder";
    }

    /**
     * 支付凭证提交方法
     *
     * @param id      订单id
     * @param version 订单version
     * @param pid01   支付凭证1 id
     * @param pid02   支付凭证2 id
     * @param pid03   支付凭证3 id
     * @return
     */
    @RequestMapping(value = "/account/payMyOrders")
    @ResponseBody
    @Transactional
    public Object payMyOrder(@RequestParam(value = "id", required = true) int id,
                             @RequestParam(value = "version", required = true) int version,
                             @RequestParam(value = "pid01", required = false, defaultValue = "0") int pid01,
                             @RequestParam(value = "pid02", required = false, defaultValue = "0") int pid02,
                             @RequestParam(value = "pid03", required = false, defaultValue = "0") int pid03) {
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException("提交失败！此订单状态已经被更改，请去个人中心查看！");
        if(pid01 == 0 && pid02 == 0 && pid03 == 0) throw new NotFoundException("提交失败！支付凭证提交出错！");
        if(order.getStatus().equals(EnumOrder.WaitPayment) || order.getStatus().equals(EnumOrder.WaitBalancePayment) || order.getStatus().equals(EnumOrder.VerifyNotPass)) {
            OrderVerify orderVerify = new OrderVerify(EnumOrder.WaitVerify, LocalDateTime.now(), order.getId(), session.getUser().getId());
            int[] pids = {pid01, pid02, pid03};
            try {
                buyService.payOrderCompleted(order, EnumOrder.WaitVerify, orderVerify, session.getUser().getId(), session.getUser().getNickname(), pids, "买家本人提交订单,待审核");
            } catch (SQLExcutionErrorException e) {
                auth.doOutputErrorInfo("确认提交出错，订单id=" + order.getId() + ", version=" + order.getVersion());
                throw new BusinessException("提交失败，请刷新网页，重新操作！");
            }
            return true;
        }
        return false;
    }

    //我的订单-卖货订单-进行中
    @RequestMapping(value = "/account/getMySellOrders")
    public String mySellOrder(PageQueryParam param, Map<String, Object> model) {
        int countSellGo = orderMapper.countOneStatusOrdersSell(session.getUser().getId(), EnumOrder.MakeMatch, EnumOrder.OtherOrder);
        param.setCount(countSellGo);
        List<Order> orderSellGoingList = orderMapper.getOneStatusOrdersSell(session.getUser().getId(), EnumOrder.MakeMatch, EnumOrder.OtherOrder, param.getPagesize(), param.getIndexNum());
        model.put("pageNumSellGo", param.getPage());
        model.put("pagesizeSellGo", param.getPagesize());
        model.put("countSellGo", countSellGo);
        model.put("orderSellGoList", orderSellGoingList);
//        return "individualCenter";
        return "/person/sellOrder";
    }

    //我的订单-买货订单-已完成
    @RequestMapping(value = "/account/getMyFinishedOrders")
    @LoginRequired
    public String getMyFinishedOrders(PageQueryParam param, Map<String, Object> model) {
        int countComp = orderMapper.countTwoStatusOrdersBuy(session.getUser().getId(), EnumOrder.Completed, EnumOrder.ReturnCompleted);
        param.setCount(countComp);
        List<Order> orderCompList = orderMapper.getTwoStatusOrdersBuy(session.getUser().getId(), EnumOrder.Completed, EnumOrder.ReturnCompleted, param.getPagesize(), param.getIndexNum());
        model.put("pageNumComp", param.getPage());
        model.put("pagesizeComp", param.getPagesize());
        model.put("countComp", countComp);
        model.put("orderCompList", orderCompList);
//        return "individualCenter";
        return "person/buyOrder";
    }

    //我的订单-卖货订单-已完成
    @RequestMapping(value = "/account/getMySellFinishedOrders")
    public String getMySellFinishedOrders(PageQueryParam param, Map<String, Object> model) {
        int countComp = orderMapper.countOneStatusOrdersSellIncludeDeleted(session.getUser().getId(), EnumOrder.Completed, EnumOrder.OtherOrder);
        param.setCount(countComp);
        List<Order> orderCompList = orderMapper.getOneStatusOrdersSellIncludeDeleted(session.getUser().getId(), EnumOrder.Completed, EnumOrder.OtherOrder, param.getPagesize(), param.getIndexNum());
        model.put("pageNumSellComp", param.getPage());
        model.put("pagesizeSellComp", param.getPagesize());
        model.put("countSellComp", countComp);
        model.put("orderSellCompList", orderCompList);
//        return "individualCenter";
        return "/person/sellOrder";
    }

    //我的订单-买货订单-已取消
    @RequestMapping(value = "/account/getMyCanceledOrders")
    public String getMyCancelOrders(PageQueryParam param, Map<String, Object> model) {
        int countCancel = orderMapper.countOneStatusOrdersBuy(session.getUser().getId(), EnumOrder.Canceled);
        param.setCount(countCancel);
        List<Order> orderCancelList = orderMapper.getOneStatusOrdersBuy(session.getUser().getId(), EnumOrder.Canceled, param.getPagesize(), param.getIndexNum());
        model.put("pageNumCancel", param.getPage());
        model.put("pagesizeCancel", param.getPagesize());
        model.put("countCancel", countCancel);
        model.put("orderCancelList", orderCancelList);
//        return "individualCenter";
        return "person/buyOrder";
    }

    //我的订单-卖货订单-已取消
    @RequestMapping(value = "/account/getMySellCanceledOrders")
    public String getMySellCancelOrders(PageQueryParam param, Map<String, Object> model) {
        int countCancel = orderMapper.countOneStatusOrdersSellIncludeDeleted(session.getUser().getId(), EnumOrder.Canceled, EnumOrder.OtherOrder);
        param.setCount(countCancel);
        List<Order> orderCancelList = orderMapper.getOneStatusOrdersSellIncludeDeleted(session.getUser().getId(), EnumOrder.Canceled, EnumOrder.OtherOrder, param.getPagesize(), param.getIndexNum());
        model.put("pageNumSellCancel", param.getPage());
        model.put("pagesizeSellCancel", param.getPagesize());
        model.put("countSellCancel", countCancel);
        model.put("orderSellCancelList", orderCancelList);
//        return "individualCenter";
        return "/person/sellOrder";
    }

    //我的订单-买货订单-退货中
    @RequestMapping(value = "/account/getMyReturnedOrders")
    public String getMyReturnOrders(PageQueryParam param, Map<String, Object> model) {
        int countReturn = orderMapper.countOneStatusOrdersBuy(session.getUser().getId(), EnumOrder.ReturnGoods);
        param.setCount(countReturn);
        List<Order> orderReturnList = orderMapper.getOneStatusOrdersBuy(session.getUser().getId(), EnumOrder.ReturnGoods, param.getPagesize(), param.getIndexNum());
        model.put("pageNumReturn", param.getPage());
        model.put("pagesizeReturn", param.getPagesize());
        model.put("countReturn", countReturn);
        model.put("orderReturnList", orderReturnList);
//        return "individualCenter";
        return "person/buyOrder";
    }

    //申请退货
    @RequestMapping("/account/applyReturnGoods")
    @ResponseBody
    @Transactional
    public Object doApplyReturnGoods(@RequestParam(value="id", required = true)int id,
                                     @RequestParam(value="version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException();
        auth.doCheckUserRight(order.getUserid());
        if(!order.getStatus().equals(EnumOrder.Completed) && !order.getStatus().equals(EnumOrder.ReturnCompleted)) {
            try {
                buyService.applyReturnGoodsOrder(order, EnumOrder.ReturnGoods, session.getUser().getId(), session.getUser().getNickname(), "买家本人申请退款");
            } catch (SQLExcutionErrorException e) {
                auth.doOutputErrorInfo("确认退货出错，订单id=" + order.getId() + ", version=" + order.getVersion());
                throw new BusinessException("退货失败，请刷新网页，重新操作！");
            }
        }
        return true;
    }

    //撤销退货
    @RequestMapping("/account/cancelReturnGoods")
    @ResponseBody
    @Transactional
    public Object doCancelReturnGoods(@RequestParam(value = "id", required = true) int id,
                                      @RequestParam(value = "version", required = true) int version) {
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException();
        auth.doCheckUserRight(order.getUserid());
        OrderReturn orderReturn = orderMapper.getOrderReturnByOrderId(order.getId());
        if(orderReturn == null) throw new NotFoundException();
        try {
            buyService.cancelReturnGoodsOrder(order, orderReturn, session.getUser().getId(), session.getUser().getNickname(), "买家撤销退货,将卖家状态置空");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("取消退货出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("取消退货失败，请刷新网页，重新操作！");
        }
        return true;
    }

    //取消订单
    @RequestMapping("/account/cancelOrder")
    @ResponseBody
    @Transactional
    public Object doCancelOrder(@RequestParam(value = "id", required = true) int id,
                                @RequestParam(value = "version", required = true) int version,
                                @RequestParam(value = "cancelType", required = false) String cancelType) {
        Map<String, Object> map = new HashMap<String, Object>();
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException();
        auth.doCheckUserRight(order.getUserid());
        if(buyMapper.getSellInfoById(order.getSellinfoid()) == null) throw new NotFoundException();
        boolean isPlusQuantity = order.getOrdertype().equals(EnumOrder.MallOrder) ? true : false;
        try {
            buyService.changeOrderStatusPlusSellInfoQuantity(order, isPlusQuantity, EnumOrder.Canceled, session.getUser().getId(), session.getUser().getNickname(), "买家本人取消订单");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("取消订单出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("取消订单失败，请刷新网页，重新操作！");
        }
        map.put("cancelType", cancelType);
        map.put("success", true);
        return map;
    }

    //删除订单-买货
    @RequestMapping("/account/deleteOrder")
    @ResponseBody
    @Transactional
    public Object doDeleteBuyOrder(@RequestParam(value = "id", required = true) int id,
                                   @RequestParam(value = "version", required = true) int version) {
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException();
        auth.doCheckUserRight(order.getUserid());
        try {
            buyService.changeOrderStatus(order, EnumOrder.Deleted, session.getUser().getId(), session.getUser().getNickname(), "买家本人删除订单");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("买家删除订单出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("删除订单失败，请刷新网页，重新操作！");
        }
        return true;
    }

    //删除订单-卖货
    @RequestMapping("/account/deleteSellOrder")
    @ResponseBody
    @Transactional
    public Object doDeleteSellOrder(@RequestParam(value = "id", required = true) int id,
                                    @RequestParam(value = "version", required = true) int version,
                                    @RequestParam(value = "delType", required = false, defaultValue = "") String delType) {
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException();
        auth.doCheckUserRight(order.getSellerid());
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            buyService.changeSellerOrderStatusPlusSellInfoQuantity(order, false, EnumOrder.Deleted, session.getUser().getId(), session.getUser().getNickname(), "卖家删除订单");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("卖家删除订单出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("删除订单失败，请刷新页面重新操作！");
        }
        map.put("delType", delType);
        map.put("success", true);
        return map;
    }

    /**
     * 我的关注列表-产品
     * @param model
     * @param param
     * @return
     */
    @RequestMapping("/account/myinterest/supply")
    @LoginRequired
    public String getMyInterest(@RequestParam(value = "coaltype", required = false, defaultValue = "")String coaltype,
                                @RequestParam(value = "pid", required = false, defaultValue = "")String pid,
                                @RequestParam(value = "NCV", required = false, defaultValue = "")String NCV,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "startDate", required = false, defaultValue = "")LocalDate startDate,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "endDate", required = false, defaultValue = "")LocalDate endDate,
                                @RequestParam(value = "sortType", required = false, defaultValue = "")String sortType,
                                @RequestParam(value = "sortOrder", required = false, defaultValue = "1")int sortOrder,
                                Map<String, Object> model, PageQueryParam param) {
        pid = pid.trim();
        String NCV01 = "0";
        String NCV02 = "7500";
        if(!StringUtils.isBlank(NCV)){
            if(NCV.equals("3000以下")){
                NCV02 = "3000";
            } else if(NCV.equals("6000以上")){
                NCV01 = "6000";
            } else if(NCV.length() == 9 && NCV.contains("-")) {
                String NCVS[] = NCV.split("-");
                NCV01 = NCVS[0];
                NCV02 = NCVS[1];
            }
        }
        if(sortOrder != 0) sortOrder = 1;
        List<String> sortTypeList = Arrays.asList(new String[]{"createdate", "viewtimes"});
        if(!sortTypeList.contains(sortType)){ sortType = "";}
        int supplyCount = myInterestMapper.getMyInterestSupplyCount(session.getUser().getId(), coaltype, Where.$like$(pid), NCV01, NCV02, startDate == null ? "" : String.valueOf(startDate), endDate == null ? "" : String.valueOf(endDate), sortType, sortOrder);
        param.setCount(supplyCount);
        List<Map<String, Object>> mySupplyList = myInterestMapper.getMyInterestSupplyList(session.getUser().getId(), coaltype, Where.$like$(pid), NCV01, NCV02, startDate == null ? "" : String.valueOf(startDate), endDate == null ? "" : String.valueOf(endDate), sortType, sortOrder, param.getPagesize(), param.getIndexNum());
        model.put("pageNumber", param.getPage());
        model.put("pagesizeSupply", param.getPagesize());
        model.put("supplyMap", mySupplyList);
        model.put("supplyCount", supplyCount);
        model.put("coalTypeList", dictionaryMapper.getAllCoalTypes());
        model.put("statusList", getInterestSupplyStatusList());
        model.put("coaltype", coaltype);
        model.put("pid", pid);
        model.put("NCV", NCV);
        model.put("startDate", startDate == null ? "" : startDate.toString());
        model.put("endDate", endDate == null ? "" : endDate.toString());
        model.put("sortType", sortType);
        model.put("sortOrder", sortOrder);
        return "person/myFavoriteSupply";
    }

    private Map getInterestSupplyStatusList(){
        Map<String, String> map = new HashMap<>();
        map.put("", "全部");
        map.put(EnumSellInfo.VerifyPass.name(), "审核通过");
        map.put(EnumSellInfo.OutOfStack.name(), "已下架");
        map.put(EnumSellInfo.OutOfDate.name(), "已过期");
        return map;
    }


    /**
     * 我的关注列表-需求
     * @param demandcode         需求编号
     * @param coaltype           煤炭种类
     * @param status             状态
     * @param createDateStart    发布日期 - 开始日期
     * @param createDateEnd      发布日期 - 结束日期
     * @param quoteDateStart     报价截止日期 - 开始日期
     * @param quoteDateEnd       报价截止日期 - 结束日期
     * @return
     */
    @RequestMapping("/account/myinterest/demand")
    public String getMyInterestDemand(@RequestParam(value = "demandcode", required = false, defaultValue = "")String demandcode,
                                      @RequestParam(value = "coaltype", required = false, defaultValue = "")String coaltype,
                                      @RequestParam(value = "status", required = false, defaultValue = "")String status,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "createDateStart", required = false, defaultValue = "")LocalDate createDateStart,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "createDateEnd", required = false, defaultValue = "")LocalDate createDateEnd,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "quoteDateStart", required = false, defaultValue = "")LocalDate quoteDateStart,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "quoteDateEnd", required = false, defaultValue = "")LocalDate quoteDateEnd,
                                      @RequestParam(value = "sortType", required = false, defaultValue = "")String sortType,
                                      @RequestParam(value = "sortOrder", required = false, defaultValue = "1")int sortOrder,
                                      Map<String, Object> model, PageQueryParam param) {
        demandcode = demandcode.trim();
        if(sortOrder != 0) sortOrder = 1;
        List<String> sortTypeList = Arrays.asList(new String[]{"releasetime", "viewtimes", "quoteenddate"});
        if(!sortTypeList.contains(sortType)){ sortType = "";}
        int demandCount = myInterestMapper.getMyInterestDemandCount(session.getUser().getId(), Where.$like$(demandcode), coaltype, status, createDateStart == null ? "" : String.valueOf(createDateStart), createDateEnd == null ? "" : String.valueOf(createDateEnd), quoteDateStart == null ? "" : String.valueOf(quoteDateStart), quoteDateEnd == null ? "" : String.valueOf(quoteDateEnd), sortType, sortOrder);
        param.setCount(demandCount);
        List<Map<String, Object>> demandList = myInterestMapper.getMyInterestDemandList(session.getUser().getId(), Where.$like$(demandcode), coaltype, status, createDateStart == null ? "" : String.valueOf(createDateStart), createDateEnd == null ? "" : String.valueOf(createDateEnd), quoteDateStart == null ? "" : String.valueOf(quoteDateStart), quoteDateEnd == null ? "" : String.valueOf(quoteDateEnd), sortType, sortOrder, param.getPagesize(), param.getIndexNum());
        model.put("pageNumber", param.getPage());
        model.put("pagesizeDemand", param.getPagesize());
        model.put("demandMap", demandList);
        model.put("demandCount", demandCount);
        model.put("coalTypeList", dictionaryMapper.getAllCoalTypes());
        model.put("statusList", getInterestDemandStatusList());
        model.put("demandcode", demandcode);
        model.put("coaltype", coaltype);
        model.put("status", status);
        model.put("createDateStart", createDateStart == null ? "" : String.valueOf(createDateStart));
        model.put("createDateEnd", createDateEnd == null ? "" : String.valueOf(createDateEnd));
        model.put("quoteDateStart", quoteDateStart == null ? "" : String.valueOf(quoteDateStart));
        model.put("quoteDateEnd", quoteDateEnd == null ? "" : String.valueOf(quoteDateEnd));
        model.put("sortType", sortType);
        model.put("sortOrder", sortOrder);
        return "person/interestDemand";
    }

    private Map getInterestDemandStatusList(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("", "全部");
        map.put("开始报价", "报价中");
        map.put("匹配中", "比价中");
        map.put("已中标", "已中标");
        map.put("交易结束", "交易结束");
        return map;
    }

    private Map getMyDemandStatusList(){
        Map<String, String> map = new LinkedHashMap<>();
        map.put("", "全部");
        map.put("审核中", "审核中");
        map.put("审核未通过", "审核未通过");
        map.put("报价中", "报价中");
        map.put("匹配中", "匹配中");
        map.put("匹配结束", "匹配结束");
        map.put("交易结束", "交易结束");
        return map;
    }

    /**
     *  我的关注列表-店铺
     * @param shopname  模糊搜索店铺名
     * @param sortfield 排序字段 myinterestcount:关注人数排序，否则按照sellinfocount排序
     * @param isdesc    是否降序 true:降序   false:升序
     * @param model
     * @param param
     * @return
     */
    @RequestMapping("/account/myinterest/store")
    public String getMyInterestStore(
            @RequestParam(value = "shopname", required = false,defaultValue = "")String shopname,
            @RequestParam(value = "sortfield", required = false,defaultValue = "sellinfocount")String sortfield,
            @RequestParam(value = "isdesc", required = false,defaultValue = "false")boolean isdesc,
            Map<String, Object> model, PageQueryParam param) {
        int myShopCount = myInterestMapper.getMyInterestCount("shop", session.getUser().getId());
        List<MyInterest> myShopList = myInterestMapper.getMyInterestList2("shop", session.getUser().getId());//, param.getPagesize(), param.getIndexNum()
        List<MyInterestShop> myInterestShopList=new ArrayList<MyInterestShop>();
        for(MyInterest myInterest:myShopList){
            Shop shop = shopMapper.getShopById(myInterest.getSid());
            if(shop==null)
                throw new NotFoundException();
            MyInterestShop myInterestShop=new MyInterestShop();
            myInterestShop.setMyinterestid(myInterest.getId());
            myInterestShop.setShopname(shop.getName());
            myInterestShop.setShopid(shop.getId());
            myInterestShop.setLocation(shop.getLocation());
            myInterestShop.setMyinterestcount(myInterestMapper.countMyInterestByShopId(myInterest.getSid()));
            myInterestShop.setSellinfocount(buyMapper.countSellInfoShop(myInterest.getSid()));
            myInterestShop.setSortfield(sortfield);
            myInterestShop.setIsdesc(isdesc);
            myInterestShopList.add(myInterestShop);
        }

        if(shopname!=null&&!shopname.equals("")){
            List<MyInterestShop> myInterestShopList2=new ArrayList<MyInterestShop>();
            for(MyInterestShop shop:myInterestShopList){
                if(shop.getShopname().contains(shopname)){
                    myInterestShopList2.add(shop);
                }
            }
            myInterestShopList=myInterestShopList2;
            myShopCount=myInterestShopList2.size();
        }
        param.setCount(myShopCount);
        Collections.sort(myInterestShopList);
        int a=0;
        if(param.getIndexNum()+param.getPagesize()>myInterestShopList.size()) {
            a = myInterestShopList.size();
        }else {
            a =param.getIndexNum()+param.getPagesize();
        }
        model.put("MyInterestShop", myInterestShopList.subList(param.getIndexNum(),a));
        model.put("myShopCount", myShopCount);
        model.put("pageNumber", param.getPage());
        model.put("pagesizeShop", param.getPagesize());
        model.put("shopname",shopname);
        model.put("sortfield", sortfield);
        model.put("isdesc", isdesc);
        return "person/myFavoriteStore";
    }

    /**
     * 我的关注列表-招标
     * @param model
     * @param param
     * @return
     */
    @RequestMapping("/account/myinterest/tender")
    public String getMyTenderInterest(@RequestParam(value = "tendercode", required = false, defaultValue = "")String tendercode,
                                      @RequestParam(value = "companyname", required = false, defaultValue = "")String companyname,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "startDate", required = false, defaultValue = "")LocalDate startDate,
                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "endDate", required = false, defaultValue = "")LocalDate endDate,
                                      @RequestParam(value = "coaltype", required = false, defaultValue = "") String coaltype,
                                      @RequestParam(value = "NCV", required = false, defaultValue = "")String NCV,
                                      @RequestParam(value = "status", required = false, defaultValue = "")String status,
                                      @RequestParam(value = "sortType", required = false, defaultValue = "") String sortType,
                                      @RequestParam(value = "sortOrder", required = false, defaultValue = "1")int sortOrder,
                                      Map<String, Object> model, PageQueryParam param) {
        companyname = companyname.trim();
        tendercode = tendercode.trim();
        String NCV01 = "0";
        String NCV02 = "7500";
        if(!StringUtils.isBlank(NCV)){
            if(NCV.contains("3000以下")){
                NCV02 = "3000";
            } else if(NCV.contains("6000以上")){
                NCV01 = "6000";
            } else if(NCV.length() == 9 && NCV.contains("-")) {
                String NCVS[] = NCV.split("-");
                NCV01 = NCVS[0];
                NCV02 = NCVS[1];
            }
        }
        if(sortOrder != 0) sortOrder = 1;
        List<String> sortTypeList = Arrays.asList(new String[]{"createtime"});
        if(!sortTypeList.contains(sortType)){ sortType = "";}
        int totalCount = myInterestMapper.getMyInterestTenderCount(session.getUser().getId(), Where.$like$(tendercode), Where.$like$(companyname), startDate == null ? "" : String.valueOf(startDate), endDate == null ? "" : String.valueOf(endDate), coaltype, NCV01, NCV02, status);
        param.setCount(totalCount);
        List<Map<String, Object>> pageList = myInterestMapper.getMyInterestTenderList(session.getUser().getId(), Where.$like$(tendercode), Where.$like$(companyname), startDate == null ? "" : String.valueOf(startDate), endDate == null ? "" : String.valueOf(endDate), coaltype, NCV01, NCV02, status, sortType, sortOrder, param.getPagesize(), param.getIndexNum());
        if (pageList != null && pageList.size() > 0) {
            for (int i = 0; i < pageList.size(); i++) {
                /*List<TenderPacket> packetList = tenderpacketMapper.findTendpackgeByDeclarId(Integer.parseInt(String.valueOf(pageList.get(i).get("tid"))));
                if (packetList != null && packetList.size() > 0) {
                    pageList.get(i).put("coaltype", packetList.get(0).getCoaltype());
                    pageList.get(i).put("NCV", packetList.get(0).getNCV());
                }*/
                if (pageList.get(i).get("status").equals("TENDER_START") || pageList.get(i).get("status").equals("TENDER_CHOOSE_CONFIRM")) {
                    pageList.get(i).put("xbz", mytenderMapper.findCount(Integer.parseInt(String.valueOf(pageList.get(i).get("tid")))));   //有几家公司投标
                } else if (pageList.get(i).get("status").equals("TENDER_RELEASE_RESULT")) {
                    List<Map<String,Object>> l=mytenderMapper.findDecalrBid(Integer.parseInt(String.valueOf(pageList.get(i).get("tid"))));
                    int yjs=0;
                    if(l!=null&&l.size()>0){
                        yjs=l.size();
                    }
                    pageList.get(i).put("yjs",yjs); //已几家客户中标
                }
                if(Integer.parseInt(String.valueOf(pageList.get(i).get("userid")))==session.getUser().getId()){
                    pageList.get(i).put("flag","0");
                } else{
                    pageList.get(i).put("flag","1");
                }

            }

        }
        model.put("count", totalCount);
        model.put("pagesize", param.getPagesize());
        model.put("page", param.getPage());
        model.put("pageList", pageList);
        model.put("statusList", getInterestTenderStatusList());
        model.put("tendercode", tendercode);
        model.put("companyname", companyname);
        model.put("startDate", startDate == null ? "" : String.valueOf(startDate));
        model.put("endDate", endDate == null ? "" : String.valueOf(endDate));
        model.put("coaltype", coaltype);
        model.put("NCV", NCV);
        model.put("status", status);
        model.put("sortType", sortType);
        model.put("sortOrder", sortOrder);
        return "person/myFavoriteTender";
    }

    private Map getInterestTenderStatusList(){
        Map<Object, String> map = new LinkedHashMap<>();
        map.put("", "全部");
        map.put(TenderStatus.TENDER_VERIFY_PASS.name(), "招标预告");
        map.put(TenderStatus.TENDER_START.name(), "投标中");
        map.put(TenderStatus.TENDER_CHOOSE_CONFIRM.name(), "开标中");
        map.put(TenderStatus.TENDER_RELEASE_RESULT.name(), "已结束");
        map.put(TenderStatus.TENDER_CANCEL.name(), "已作废");
        return map;
    }

    //取消我的关注
    @RequestMapping("/account/cancelMyInterest")
    @ResponseBody
    public Object doCancelMyInterest(int id) {
        MyInterest myInterest = myInterestMapper.getMyInterestById(id);
        if (myInterest == null) throw new NotFoundException();
        if (!myInterest.isIsdelete()) {
            myInterestMapper.cancelMyInterest(id);
        }
        return true;
    }

    //个人中心-我的报价-删除
    @RequestMapping(value = "/account/deleteMyquote", method = RequestMethod.POST)
    @ResponseBody
    public boolean deleteMyquote(@RequestParam("id") Integer id){
        quoteMapper.modifyIsdeleteById(id,session.getUser().getId());
        return true;

    }

    //个人中心,我的报价--lich
    @RequestMapping(value = "/account/getMyQuote")
    public String myQuoteUnderway(
            @RequestParam(value = "demandcode", required = false,defaultValue = "") String demandcode,
            @RequestParam(value = "status", required = false,defaultValue = "全部") String status,
            @RequestParam(value = "releaseDateStart", required = false,defaultValue = "") String releaseDateStart,
            @RequestParam(value = "releaseDateEnd", required = false,defaultValue = "") String releaseDateEnd,
            @RequestParam(value = "ordercol", required = false,defaultValue = "lastupdatetime") String ordercol,
            @RequestParam(value = "sortflag", required = false,defaultValue = "0") int sortflag, //0降序,1升序
            PageQueryParam param, Map<String, Object> model) {
        int totalCount = quoteMapper.countAllMyQuote(session.getUser().getId(), demandcode, status, releaseDateStart, releaseDateEnd); //总数
        param.setCount(totalCount);
        List<Quote> quoteList = quoteMapper.getTurnpageList(session.getUser().getId(),
                demandcode, status, releaseDateStart, releaseDateEnd,
                param.getIndexNum(), param.getPagesize(),ordercol,sortflag);
        model.put("quoteUnderwayList", quoteList);
        model.put("underwayTotalCount", totalCount);
        model.put("pagesizeQuote", param.getPagesize());
        model.put("pageNumber", param.getPage());
        //
        model.put("demandcode", demandcode);
        model.put("status", status);
        model.put("releaseDateStart", releaseDateStart);
        model.put("releaseDateEnd", releaseDateEnd);
        model.put("sortflag", sortflag);
        return "/person/myQuote";
    }


    //我的报价-查看
    @RequestMapping(value = "/account/viewMyQuote")
    public String viewMyQuote(@RequestParam("demandid") Integer demandid, String reqsource, Map<String, Object> model) {
        Demand demand = demandMapper.getDemandByIdIncludeDeleted(demandid);
        if (demand == null) {
            throw new NotFoundException();
        }
        Quote quote = quoteMapper.getQuoteByUserIdAndDemandid(session.getUser().getId(), demandid);
        if (reqsource != null && reqsource != "") {
            model.put("reqsource", reqsource);
        }
        model.put("demand", demand);
        model.put("quote", quote);
        model.put("modifyPrice", "modifyPrice");

        if (reqsource.equals("myquote") && (quote.getPS() != null)) {
            model.put("psName", dataBookMapper.getDataBookNameByTypeSequence("pstype", quote.getPS()));
        }

        model.put("pstypelist", dataBookMapper.getDataBookListByType("pstype"));
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//取服务器的当前时间
        model.put("serverTime", df.format(new Date()));

        // 关注
        model.put("favor", (session != null && session.getUser() != null &&
                interestMapper.getMyInterestBySid(demand.getId(), session.getUser().getId(), "demand") != null &&
                !interestMapper.getMyInterestBySid(demand.getId(), session.getUser().getId(), "demand").isIsdelete()));


        //报价成交记录展示
        List<DealDemand> dealList = demandMapper.getListDealDemand();
        if (dealList != null && dealList.size() > 0) {
            model.put("dealList", dealList);
        }

        // 供应商信息
        Company company = companyMapper.getCompanyByUserid(demand.getUserid());
        model.put("company", company);
        return "sellInfo";
    }

    //我的报价-已中标
    @RequestMapping(value = "/account/getMyQuoteBid")
    public String myQuoteBid(PageQueryParam param, Map<String, Object> model) {
        int totalCount = quoteMapper.countAllMyQuoteBid(session.getUser().getId()); //总数
        param.setCount(totalCount);
        List<Quote> quoteList = quoteMapper.getTurnpageBidList(session.getUser().getId(), param.getIndexNum(), param.getPagesize());
        model.put("quoteBidList", quoteList);
        model.put("bidTotalCount", totalCount);
        model.put("pagesizeBid", param.getPagesize());
        model.put("pageNumber", param.getPage());
        return "/person/myQuote";
    }

    //我的报价-未中标
    @RequestMapping(value = "/account/getMyQuoteNotBid")
    public String myQuoteNotBid(PageQueryParam param, Map<String, Object> model) {
        int totalCount = quoteMapper.countAllMyQuoteNotBid(session.getUser().getId()); //总数
        param.setCount(totalCount);
        List<Quote> quoteList = quoteMapper.getTurnpageNotBidList(session.getUser().getId(), param.getIndexNum(), param.getPagesize());
        model.put("quoteNotBidList", quoteList);
        model.put("notBidTotalCount", totalCount);
        model.put("pageNumber", param.getPage());
        model.put("pagesizeNotBid", param.getPagesize());
        return "/person/myQuote";
    }

    //查看供应信息详细
    @RequestMapping("/account/getSupplyDetail")
    public String doGetSellDetail(int id, String reqsource, Map<String, Object> model) {
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if (sellInfo == null) throw new NotFoundException("该供应信息不存在，或者已经被取消！");
        sellInfo = buyMapper.getAvailableSellInfoByPid(sellInfo.getPid());
        if (sellInfo == null) throw new NotFoundException("该供应信息不存在，或者已经被取消！");
        auth.doCheckUserRight(sellInfo.getSellerid());
        buyMethod.showJTJ(id, model);
        showEditHist(sellInfo.getId(),sellInfo.getParentid(),model);
        model.put("supplyInfo", sellInfo);
        model.put("reqsource", reqsource);
        return "checkSupplyInfo";
    }

    public void showEditHist(int id,int parentid, Map<String, Object> model) {
        List<SellInfo> list =  buyMapper.getSellInfoEditHist(id, parentid);
        for (SellInfo in : list){
            List<PriceLadder> priceLadders =  priceLadderMapper.getPriceLadderListBySellinfoId(in.getId()) ;
            if(priceLadders!=null && priceLadders.size()>0){
                in.setPricelist(priceLadders);
            }
        }
        if(list!=null && list.size()>0){
            model.put("editHistList",list);
        }
    }

    /**
     * 个人中心-我的供应列表
     * @param coaltype          煤炭种类
     * @param status            状态
     * @param NCV               热值区间字符串
     * @param startDate         开始日期
     * @param endDate           结束日期
     * @param sortType          排序类型 字符串
     * @param sortOrder         排序方式 int 0 代表升序, 1代表降序
     * @return
     */
    @RequestMapping(value = "/account/getMySupply")
    public String doGetMySupply(@RequestParam(value = "coaltype", required = false, defaultValue = "")String coaltype,
                                @RequestParam(value = "pid", required = false, defaultValue = "")String pid,
                                @RequestParam(value = "status", required = false, defaultValue = "")String status,
                                @RequestParam(value = "NCV", required = false, defaultValue = "")String NCV,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "startDate", required = false, defaultValue = "")LocalDate startDate,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "endDate", required = false, defaultValue = "")LocalDate endDate,
                                @RequestParam(value = "sortType", required = false, defaultValue = "createdate")String sortType,
                                @RequestParam(value = "sortOrder", required = false, defaultValue = "1")int sortOrder,
                                PageQueryParam param, Map<String, Object> model) {
        pid = pid.trim();
        String NCV01 = "0";
        String NCV02 = "7500";
        if(!StringUtils.isBlank(NCV)){
            if(NCV.contains("3000以下")){
                NCV02 = "3000";
            } else if(NCV.contains("6000以上")){
                NCV01 = "6000";
            } else if(NCV.length() == 9 && NCV.contains("-")) {
                String NCVS[] = NCV.split("-");
                NCV01 = NCVS[0];
                NCV02 = NCVS[1];
            }
        }
        if(sortOrder != 0) sortOrder = 1;
        List<String> sortTypeList = Arrays.asList(new String[]{"createdate", "viewtimes"});
        if(!sortTypeList.contains(sortType)){ sortType = "createdate";}
        int count = buyMapper.getSellinfoCenterCountByUserid(session.getUser().getId(), coaltype, Where.$like$(pid), status, NCV01, NCV02, startDate == null ? "" : String.valueOf(startDate), endDate == null ? "" : String.valueOf(endDate), sortType, sortOrder);
        param.setCount(count);
        List<SellInfo> sellInfoList = buyMapper.getSellinfoCenterListByUserid(session.getUser().getId(), coaltype, Where.$like$(pid), status, NCV01, NCV02, startDate == null ? "" : String.valueOf(startDate), endDate == null ? "" : String.valueOf(endDate), sortType, sortOrder, param.getPagesize(), param.getIndexNum());
        model.put("supplyList", sellInfoList);
        model.put("count", count);
        model.put("pagesizeMySupply", param.getPagesize());
        model.put("pageNumber", param.getPage());
        model.put("coalTypeList", dictionaryMapper.getAllCoalTypes());
        model.put("statusList", getCenterSupplyStatusList());
        model.put("coaltype", coaltype);
        model.put("pid", pid);
        model.put("status", status);
        model.put("NCV", NCV);
        model.put("startDate", startDate == null ? "" : startDate.toString());
        model.put("endDate", endDate == null ? "" : endDate.toString());
        model.put("sortType", sortType);
        model.put("sortOrder", sortOrder);
        return "person/mySupply";
    }

    private Map getCenterSupplyStatusList(){
        Map<Object, String> map = new LinkedHashMap<>();
        map.put("", "全部");
        map.put(EnumSellInfo.WaitVerify.name(), "审核中");
        map.put(EnumSellInfo.VerifyPass.name(), "审核通过");
        map.put(EnumSellInfo.VerifyNotPass.name(), "审核未通过");
        map.put(EnumSellInfo.Canceled.name(), "已取消");
        map.put(EnumSellInfo.WaitShopRun.name(), "待上架");
        return map;
    }

    //删除供应信息
    @RequestMapping("/account/deleteMySupply")
    @ResponseBody
    public Object doDeleteMySupply(@RequestParam(value = "id", required = true) int id,
                                   @RequestParam(value = "version", required = true)int version){
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if (sellInfo == null) throw new NotFoundException();
        auth.doCheckUserRight(sellInfo.getSellerid());
        if(!buyService.changeSellInfoStatus(id, version, EnumSellInfo.Deleted, "供应方本人刪除供应信息")){
            auth.doOutputErrorInfo("删除供应信息(sellinfo)出错, 供应信息id=" + id + ", 操作人userid=" + session.getUser().getId());
            throw new BusinessException("刪除失败, 请刷新页面重试! ");
        }
        return true;
    }

    //取消供应信息
    @RequestMapping("/account/cancelRelease")
    @ResponseBody
    public Object doCancelSellinfo(@RequestParam(value = "id", required = true)int id,
                                   @RequestParam(value = "version", required = true)int version){
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if (sellInfo == null) throw new NotFoundException();
        auth.doCheckUserRight(sellInfo.getSellerid());
        if(orderMapper.countSellInfoUnderWayOrders(sellInfo.getPid(), EnumOrder.ReturnGoods,
                EnumOrder.WaitPayment, EnumOrder.WaitVerify, EnumOrder.VerifyPass,
                EnumOrder.VerifyNotPass, EnumOrder.WaitBalancePayment, EnumOrder.MakeMatch) != 0){
            map.put("error", "order");
        } else {
            if(!buyService.changeSellInfoStatus(id, version, EnumSellInfo.Canceled, "供应方本人取消供应信息")){
                auth.doOutputErrorInfo("取消供应信息(sellinfo)出错, 供应信息id=" + id + ", 操作人userid=" + session.getUser().getId());
                throw new BusinessException("取消失败, 请刷新页面重试! ");
            }
            success = true;
        }
        map.put("success", success);
        return map;
    }

    //个人中心-账号信息,固定电话和qq
    @RequestMapping(value = "/account/saveMyaccount", method = RequestMethod.POST)
    @ResponseBody
    public boolean saveMyaccount(String nickname, String telephone, String qq){
        if(!checkNicknameIfUsed(nickname)){
            return false;
        } else {
            if (nickname != null || telephone != null || qq != null) {
                userMapper.modifyPhoneQQNickname(telephone, qq, nickname, session.getUser().getId());
            }
            return true;
        }
    }

    public boolean checkNicknameIfUsed(String nickname) {
        Integer userId = userMapper.getUserIdByNickname(nickname);
        if (userId != null && userId != session.getUser().getId()) {
            return false;
        } else {
            return true;
        }
    }

    //我的订单详情
    @RequestMapping("/account/OrderInfo")
    public String doGetMyOrders(@RequestParam(value = "id", required = true) int id,
                                @RequestParam(value = "version", required = true) int version,
                                String reqsource, HttpServletRequest request,
                                Map<String, Object> model) throws ServletException, TemplateException, IOException {
        orderInfos(id, version, reqsource, request, model);
        return "orderInfo";
    }

    public void orderInfos(int id, int version, String reqsource, final HttpServletRequest request, Map<String, Object> model) throws ServletException, TemplateException, IOException {
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException("该订单不存在，或者已经被更改！");
        auth.doCheckUserRight(order.getUserid(), order.getSellerid());
        SellInfo sellInfo = buyMapper.getSellInfoById(order.getSellinfoid());
        if (sellInfo == null) throw new NotFoundException();
        Object check7days = 0;
        if (order.getDeliverytime1().minusDays(7).isAfter(LocalDate.now())) {
            check7days = 1;
        }
        if(session.getUser().getId() == order.getUserid()){
            model.put("userType", 0);
        } else if(session.getUser().getId() == order.getSellerid()){
            model.put("userType", 1);
        }
        model.put("check7days", check7days);
        model.put("orderInfo", order);
        model.put("sellInfo", sellInfo);
        model.put("reqsource", reqsource);
        model.put("contract", buyMethod.contractContent(id, request));
    }

    @RequestMapping("/account/getOrderStatus")
    @ResponseBody
    public Object doGetSellinfoStatus(@RequestParam(value = "id", required = true) int id) {
        Order order = orderMapper.getOrderById(id);
        if (order == null) throw new NotFoundException();
        return order.getStatus();
    }

    @RequestMapping(value = "/account/delMyTenderInter", method = RequestMethod.POST)
    @ResponseBody
    public boolean delMyTenderInter(@RequestParam(value = "id", required = true) int id) {
        myInterestMapper.deltetenderquotes(id);
        return true;
    }

    //个人中心,我的投标- lich
    @LoginRequired
    @RequestMapping("/account/getMyTender")
    public String getMyTender(
            Map<String, Object> model,
            @RequestParam(value = "status", required = false, defaultValue = "ytb") String status,
            @RequestParam(value = "year", required = false, defaultValue = "") String year,
            @RequestParam(value = "month", required = false, defaultValue = "") String month,
            PageQueryParam param) {
        String time1 = "";
        String patten1 = "";
        if (!year.equals("") && !month.equals("")&&!month.equals("全部")) {
            time1 = year + "-" + month;
            patten1 = "%Y-%m";
        } else {
            time1 = year;
            patten1 = "%Y";
        }
        if (!status.equals("zfpz")) {
            List<Map<String, Object>> pageList = null;
            int totalCount = 0;
            String[] s=null;
            if (status.equals("ytb")) {
                s=new String[]{"MYTENDER_TENDERED","MYTENDER_TENDERED_CONFIRM","MYTENDER_WAITING_CHOOSE","MYTENDER_FAIL","MYTENDER_SUCCEED","MYTENDER_TENDERED_FREE","MYTENDER_CHOOSE_FREE","MYTENDER_CHOOSE"};
                //已投标
                pageList = myInterestMapper.findtenders(session.getUser().getId(),status,s,time1, patten1, param.getPagesize(), param.getIndexNum());
                totalCount = myInterestMapper.counttenders(session.getUser().getId(), status,s, time1, patten1);

            } else if (status.equals("zhb")) {
                s=new String[]{"MYTENDER_EDIT","MYTENDER_MISSING"};
                //暂缓的投标
                pageList = myInterestMapper.findtenders(session.getUser().getId(),status,s,time1, patten1, param.getPagesize(), param.getIndexNum());
                totalCount = myInterestMapper.counttenders(session.getUser().getId(), status,s, time1, patten1);
            } else if (status.equals("yfq")) {
                //已放弃
                s=new String[]{"MYTENDER_GIVEUP","MYTENDER_CANCEL"};
                pageList = myInterestMapper.findtenders(session.getUser().getId(),status,s,time1, patten1, param.getPagesize(), param.getIndexNum());
                totalCount = myInterestMapper.counttenders(session.getUser().getId(), status,s, time1, patten1);
            } else {
                //已中标,未中标
                pageList = myInterestMapper.findMytenders(session.getUser().getId(), status, time1, patten1, param.getPagesize(), param.getIndexNum());
                totalCount = myInterestMapper.countMytenders(session.getUser().getId(), status, time1, patten1);
            }

            if (pageList != null && pageList.size() > 0) {
                for (int i = 0; i < pageList.size(); i++) {
                    pageList.get(i).put("tb", bidMapper.findBidTbCount(Integer.parseInt(String.valueOf(pageList.get(i).get("did"))))); //有几家公司投标
                }
            }
            model.put("count", totalCount);
            model.put("pageList", pageList);
        } else {
            List<Map<String, Object>> resultList = myInterestMapper.findtenderPayment(session.getUser().getId(), param.getPagesize(), param.getIndexNum());
            model.put("count", myInterestMapper.getCountByuserid(session.getUser().getId()));
            model.put("pageList", resultList);

        }
        model.put("page", param.getPage());
        model.put("pagesize", param.getPagesize());
        model.put("status", status);
        model.put("year", year);
        model.put("month", month);
        model.put("st1", myInterestMapper.getYtbCount(session.getUser().getId()));//已投的标的数量
        model.put("st2", myInterestMapper.getZhCount(session.getUser().getId()));//投标暂缓
        model.put("st3", myInterestMapper.getCountByStatus(session.getUser().getId(), "MYTENDER_SUCCEED"));//已中的标的数量
        model.put("st4", myInterestMapper.getCountByStatus(session.getUser().getId(), "MYTENDER_FAIL"));//未中的标数量
        model.put("st5", myInterestMapper.getYfqCount(session.getUser().getId())); //已放弃
//        model.put("st8", myInterestMapper.getCountByuserid(session.getUser().getId())); //支付凭证数量
        model.put("yearInterval", Collections2.transform(tenderDeclarationService.findYear(), t -> String.valueOf(t)));
        return "person/mzTenderTBList";
    }


    //个人中心,我的投标删除
    @LoginRequired
    @RequestMapping("/account/delMyTender")
    @ResponseBody
    public Object delMyTender(@RequestParam(value = "id", required = true) int id) {
       Bid bid= bidMapper.getBidByIdUserid(id,session.getUser().getId());
        if(bid==null){
           throw new NotFoundException();
        }else {
            Map<String, Object> map = new HashMap<String, Object>();
            myInterestMapper.deleteMytender(id);
//        tenderPayMentMapper.deleteTpayment(id);
            bidMapper.deleteBidById(id);
            map.put("success", true);
            return map;
        }
    }


//    //个人中心,我的投标支付凭证删除
//    @LoginRequired
//    @RequestMapping("/account/delPayment")
//    @ResponseBody
//    public boolean delPayment(@RequestParam(value = "id", required = true) int id,@RequestParam(value = "paymentstatus", required = true) String status) {
//        if(status.equals("unPaidUp")){
//            tenderPayMentMapper.updateTenderpmt(id);
//        }else {
//            tenderPayMentMapper.deleteTenderpayment(id);
//        }
//        bidMapper.updateStatusById(id, null);
//        return true;
//    }


//    //支付凭证上传保存到数据库
//    @RequestMapping(value = "/account/savePic", method = RequestMethod.POST)
//    @ResponseBody
//    @LoginRequired
//    public Object addPaymentPic(@RequestParam("myPics") String myPics, Map<String, Object> model,
//                                @RequestParam(value = "bid", required = true) int bid,
//                                @RequestParam(value = "type", required = true) String type
//                               ) throws IOException, FileStore.UnsupportedContentType {
//
//        Map map = new HashMap<String, Object>();
//        String[] str = myPics.split(",");
//        String pic1 = null;
//        String pic2 = null;
//        String pic3 = null;
//        if (str != null && str.length > 0&&myPics!=null&&!myPics.equals("")) {
//            if (str.length == 1) {
//                pic1 = str[0];
//            } else if (str.length == 2) {
//                pic1 = str[0];
//                pic2 = str[1];
//            } else if (str.length == 3) {
//                pic1 = str[0];
//                pic2 = str[1];
//                pic3 = str[2];
//            }
//            if(type.equals("add")) {
//                tenderPayMentMapper.addTenderpayment(bid, session.getUser().getId(), session.getUser().getNickname(), LocalDateTime.now(), pic1, pic2, pic3);
//            }else if(type.equals("update")){
//                tenderPayMentMapper.updatePayment(pic1, pic2, pic3, bid);
//            }else if(type.equals("readd")){
//                //审核失败再次上传支付凭证
//                tenderPayMentMapper.updateTpaymentdBy("fail",bid);
//                tenderPayMentMapper.addTenderpayment(bid, session.getUser().getId(), session.getUser().getNickname(), LocalDateTime.now(), pic1, pic2, pic3);
//            }
//            bidMapper.updateStatusById(bid, "waitVerify");
//           MessageNotice.ALREADYPAYTENDERPACKET.noticeUser(session.getUser().getId());
//            map.put("flag", true);
//            return map;
//        } else {
//            throw new BusinessException("上传的支付凭证不能为空");
//        }
//
//
//    }



//    //支付凭证上传
//    @RequestMapping(value = "/account/uploadPic", method = RequestMethod.POST)
//    @ResponseBody
//    @LoginRequired
//    public Object addPaymentPic(@RequestParam("file") MultipartFile mypic,HttpServletResponse response,Map<String, Object> model) throws IOException, FileStore.UnsupportedContentType {
//        Map map = new HashMap<String, Object>();
//        if (mypic.isEmpty()) throw new BusinessException("请选择图片上传");
//        String fileType = fileStore.getFileType(mypic);
//        List<String> picTypeList = Arrays.asList(new String[]{"jpg", "bmp", "png", "jpeg"});
//        response.setContentType("text/html");
//        if (picTypeList.contains(fileType)) {
//            if (mypic.getSize() / 1000 / 1000 <= 10) {
//                String filePath = fileService.uploadTenderPicture(mypic);
//                map.put("filePath", filePath);
//                return map;
//            }
////            throw new BusinessException("上传的图片不能超过10M！");
//            map.put("error","上传的图片不能超过10M!");
//            return map;
//        }
//        map.put("error","请选择 jpg, bmp, png, jpeg 格式的图片上传！");
//        return map;
////        throw new BusinessException("请选择 jpg, bmp, png, jpeg 格式的图片上传！");
//
//    }

    //支付凭证上传
    @RequestMapping(value = "/account/uploadPic", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object addPaymentPic(@RequestParam("file") MultipartFile mypic, HttpServletResponse response, Map<String, Object> model) throws IOException, FileStore.UnsupportedContentType {
        Map map = new HashMap<String, Object>();
        if (mypic.isEmpty()) throw new BusinessException("请选择图片上传");
        String fileType = fileStore.getFileTypeByFileOriginName(mypic);
        List<String> picTypeList = Arrays.asList(new String[]{"jpg", "bmp", "png", "jpeg"});
        response.setContentType("text/html");
        if (picTypeList.contains(fileType)) {
            if (mypic.getSize() / 1000 / 1000 <= 10) {
                String filePath = fileService.uploadTenderPicture(mypic);
                map.put("filePath", filePath);
                return map;
            }
//            throw new BusinessException("上传的图片不能超过10M！");
            map.put("error","上传的图片不能超过10M!");
            return map;
        }
        map.put("error","请选择 jpg, bmp, png, jpeg 格式的图片上传！");
        return map;
//        throw new BusinessException("请选择 jpg, bmp, png, jpeg 格式的图片上传！");

    }


    //支付凭证删除
//    @RequestMapping(value = "/account/deletePic", method = RequestMethod.POST)
//    @ResponseBody
//    @LoginRequired
//    public Object deletePaymentPic(@RequestParam("picPath") String picPath, Map<String, Object> model) throws IOException, FileStore.UnsupportedContentType {
//        Map map = new HashMap<String, Object>();
//        File file = new File(picPath);
//        if (file.exists()) {
//            file.delete();
//            map.put("picPath", "");
//            return map;
//        } else {
//            throw new BusinessException("支付凭证图片路径有误!");
//        }
//    }


    //测试个人中心模板
    @RequestMapping("/account/testTemplate")
    public String testTemplate() {
        return "person/pLayout";
    }

    /**
     * 审核履约保证金列表
     *
     * @param queryParam
     * @param declareId  公告id
     * @param status     状态
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value = "/account/tenderNotice/getMyBond", method = RequestMethod.GET)
    public String getMyBond(PageQueryParam queryParam,
                            @RequestParam(value = "tenderId", required = true) Integer declareId,
                            @RequestParam(value = "status", defaultValue = "waitVerify") TenderStatus status,
                            @CurrentUser User user,
                            Model model) {

        TenderDeclaration tenderDeclaration=tenderdeclarMapper.getTenderdeclarByIdUserId(declareId, user.getId());
        if(tenderDeclaration==null){
            throw  new NotFoundException();
        }
        tenderPaymentService.listPaymentByDeclareId(queryParam, user, declareId, status);
        List<Bid> paymentList =  tenderPayMentMapper.findBidByDeclare(user.getId(),declareId);
        long paidUpCount=paymentList.stream().filter(t->TenderStatus.paidUp.toString().equals(t.getPaymentstatus())).count();
        long unpaidUpCount=paymentList.stream().filter(t->TenderStatus.unPaidUp.toString().equals(t.getPaymentstatus())).count();
        //老客户不需要上传支付凭证，也算未审核
        long paymentStatusisNullCount=paymentList.stream().filter(t -> t.getPaymentstatus() == null).count();
        model.addAttribute("pageParam", queryParam);
        model.addAttribute("status", status);
        model.addAttribute("tenderId", declareId);
        model.addAttribute("waitVerifyCount",paymentStatusisNullCount);
        model.addAttribute("paidUpCount",paidUpCount);
        model.addAttribute("unpaidUpCount",unpaidUpCount);
        model.addAttribute("tenderDeclaration",tenderDeclaration);

        return "person/mybond";
    }

    //查看支付凭证图片
    @RequestMapping(value = "/account/paymentPic", method = RequestMethod.POST)
    @ResponseBody
    public Object loadPicPath(@RequestParam("paymentId") int paymentId, @CurrentUser User user, BindResult result) {
        Map<String, String> picList = tenderPayMentMapper.findPathByPaymentId(paymentId);
        if (picList == null) {
            throw new NotFoundException("没有找到当前竞标的支付凭证");
        }
        result.addAttribute("pathList", picList.values());
        return json(result);

    }

    /**
     * 修改支付凭证状态--已缴纳
     * @param bidId   投标Id

     * @param result
     * @return
     */
    @RequestMapping(value = "/account/updatepaymentPaidUp", method = RequestMethod.POST)
    @ResponseBody
    public Object updatePaymentStatusPaidUp(@RequestParam("bidId") int bidId,
                                            @CurrentUser User user,
                                         BindResult result) {
        tenderPaymentService.updateTenderPaymentStatus(TenderStatus.paidUp, user, bidId);
        return json(result);
    }
    /**
     * 修改支付凭证状态--未缴纳
     * @param bidId  投标Id
     * @param result
     * @return
     */
    @RequestMapping(value = "/account/updatepaymentUnPaidUp", method = RequestMethod.POST)
    @ResponseBody
    public Object updatePaymentStatusUnPaid(
                                      @RequestParam("bidId") int bidId,
                                      @CurrentUser User user,
                                      BindResult result) {
        tenderPaymentService.updateTenderPaymentStatus(TenderStatus.unPaidUp, user, bidId);
        return json(result);
    }


    //修改公告已放弃
    @RequestMapping(value = "/account/giveUpDeclare", method = RequestMethod.POST)
    @ResponseBody
    public Object updateTenderStatus(@RequestParam("declareId") int declareId,
                                     @CurrentUser User user,
                                     BindResult result) {
        tenderDeclarationService.updateDeclarationGiveUp(declareId, user.getId());
        return json(result);
    }

    //招标中标结果查看
    @RequestMapping(value = "/account/tenderBidResult")
    @LoginRequired
    public String tenderBidResult(@RequestParam(value = "id", required = true) int id,
                                 Map<String, Object> model) {
        //公告表里面的userid必须是当前登录用户
        TenderDeclaration tenderdeclar = tenderApplyService.findDeclarById(id);
        if (tenderdeclar == null)
            throw new NotFoundException();
        if(tenderdeclar.getUserid()==session.getUser().getId()){
            //招标中标结果
            model.put("totalSupplyamount", tenderdeclarMapper.countSupplyamount(id, session.getUser().getId()));
            model.put("result", tenderdeclarMapper.findBidTender(id, session.getUser().getId()));
            model.put("tenderdeclar", tenderdeclar);
            model.put("flag", "tenderSuccess");
            return "/person/tenderResult";

        }else {
                throw new NotFoundException();
            }
    }

    //我的投标,中标查看
    @RequestMapping(value = "/account/checkBidSucResult")
    @LoginRequired
    public String checkBidSucResult(@RequestParam(value = "id", required = true) int id,
                                    Map<String, Object> model) {
        TenderDeclaration tenderdeclar = tenderApplyService.findDeclarById(id);
        if (tenderdeclar == null)
            throw new NotFoundException();
        List<Bid> lt= bidMapper.findByTenderId(tenderdeclar.getId(),session.getUser().getId());
        if(lt!=null&&lt.size()>0){
            //中标未中标
            model.put("totalSupplyamount", tenderdeclarMapper.countSupply(id, "MYTENDER_SUCCEED", session.getUser().getId()));
            model.put("result", tenderdeclarMapper.findBidBy(id, "MYTENDER_SUCCEED", session.getUser().getId()));
            model.put("tenderdeclar", tenderdeclar);
            model.put("sl", tenderdeclarMapper.countSupply(id, "MYTENDER_SUCCEED", session.getUser().getId()));
            model.put("flag", "mytenderSuccess");
            return "/person/tenderResult";
        }else{
            throw new NotFoundException();
        }
    }


    //我的投标,未中标查看
    @RequestMapping(value = "/account/checkBidFailResult")
    @LoginRequired
    public String checkBidFailResult(@RequestParam(value = "id", required = true) int id,
                                        Map<String, Object> model) {
        TenderDeclaration tenderdeclar = tenderApplyService.findDeclarById(id);
        if(tenderdeclar==null)
            throw new NotFoundException();
        List<Bid> lt= bidMapper.findByTenderId(tenderdeclar.getId(), session.getUser().getId());
        if(lt!=null&&lt.size()>0){
            model.put("totalSupplyamount", tenderdeclarMapper.countSupply(id, "MYTENDER_FAIL", session.getUser().getId()));
            model.put("result",tenderdeclarMapper.findBidBy(id, "MYTENDER_FAIL", session.getUser().getId()));
            model.put("tenderdeclar", tenderdeclar);
            model.put("sl", 0);
            model.put("flag", "mytenderFail");
            return "/person/tenderResult";
        }else{
            throw new NotFoundException();
        }
    }



    @RequestMapping(value = "/account/toTenderResult")
    @LoginRequired
    public String toTenderResult(@RequestParam(value = "id", required = true) int id,
                                 @RequestParam(value = "flag", required = true) String flag,
                                 @CurrentUser User user,
                                 Map<String, Object> model) {
        //公告表里面的userid必须是当前登录用户
        TenderDeclaration tenderdeclar = tenderApplyService.findDeclarById(id);
//        if (flag.equals("tbzb")) {
//          List<Bid> list= bidMapper.findByTenderId(tenderdeclar.getId(),session.getUser().getId());
//            if(list!=null&&list.size()>0){
//                //招标中标结果
//                model.put("totalSupplyamount", tenderdeclarMapper.countSupplyamount(id));
//                model.put("result", tenderdeclarMapper.findBidTender(id));
//                model.put("tenderdeclar", tenderdeclar);
//                model.put("flag", flag);
//                return "/person/tenderResult";
//            }else{
//                throw new NotFoundException();
//            }
//
//        } else if (flag.equals("zbwzb")) {
//            List<Bid> lt= bidMapper.findByTenderId(tenderdeclar.getId(),session.getUser().getId());
//            if(lt!=null&&lt.size()>0){
//                //中标未中标
//                List<Map<String, Object>> list = tenderdeclarMapper.findBidTenderP(id, session.getUser().getId());
//                if (list != null && list.size() > 0) {
//                    model.put("sfzb", true);
//                    model.put("sl", tenderdeclarMapper.countSupplyamountP(id, session.getUser().getId()));
//                } else {
//                    model.put("sfzb", false);
//                    model.put("sl", 0);
//                }
//                model.put("totalSupplyamount", tenderdeclarMapper.countSupplyamount(id));
//                model.put("result", tenderdeclarMapper.findBidTender(id));
//                model.put("tenderdeclar", tenderdeclar);
//                model.put("flag", flag);
//                return "/person/tenderResult";
//            }else{
//                throw new NotFoundException();
//            }
//
//        } else if (flag.equals("dxb")) {
//            List<Bid> lt= bidMapper.findByTenderId(tenderdeclar.getId(),session.getUser().getId());
//            if(lt!=null&&lt.size()>0) {
//                //投标查看
//                List<TenderItem> itemList = tenderdeclar.getItemList();
//                if (itemList != null && itemList.size() > 0) {
//                    for (int i = 0; i < itemList.size(); i++) {
//                        List<TenderPacket> packetList = itemList.get(i).getPacketList();
//                        if (packetList != null && packetList.size() > 0) {
//                            for (int j = 0; j < packetList.size(); j++) {
//                                int packgeId = packetList.get(j).getId();
//                                List<Mytender> tenderlist = mytenderMapper.findMytenderList(packgeId, session.getUser().getId());
//                                packetList.get(j).setMytenderList(tenderlist);
//                            }
//                        }
//                    }
//                }
//
//                model.put("attachmentpath", lt.get(0).getAttachmentpath());
//                model.put("attachmentfilename", lt.get(0).getAttachmentfilename());
//                model.put("tenderdeclar", tenderdeclar);
//                return "/person/tenderWaitResult";
//            }else{
//                throw new NotFoundException();
//            }
//        } else

        if (flag.equals("xb")) {
            if(tenderdeclar!=null && tenderdeclar.getUserid()==session.getUser().getId()) {
                    //选标
                    List<TenderItem> itemList = tenderdeclar.getItemList();
                    if (itemList != null && itemList.size() > 0) {
                        for (int i = 0; i < itemList.size(); i++) {
                            List<TenderPacket> packetList = itemList.get(i).getPacketList();
                            if (packetList != null && packetList.size() > 0) {
                                for (int j = 0; j < packetList.size(); j++) {
                                    int packgeId = packetList.get(j).getId();
                                    List<Mytender> tenderlist = mytenderMapper.getByPacketidAndUserid(packgeId,session.getUser().getId());
                                    packetList.get(j).setMytenderList(tenderlist);
                                }
                            }
                        }
                    }
                    model.put("tenderdeclar", tenderdeclar);
                    List<Map<String, Object>> xlist = mytenderMapper.findByPacketid(tenderdeclar.getId());
                    if (xlist != null && xlist.size() > 0) {
                        for (int i = 0; i < xlist.size(); i++) {
                            int pid = Integer.parseInt(String.valueOf(xlist.get(i).get("pid")));
                            xlist.get(i).put("gyssl", mytenderMapper.countSupplyNum(pid));
                        }
                    }
                    model.put("xlist", xlist);
                    model.put("ybzl", mytenderMapper.countSupplyAmount(tenderdeclar.getId()));
                    model.put("hasAttachment", tenderdeclarMapper.findByInDeclare(user.getId(),id).size() > 0 ? true : false);
                    return "/person/myStandardSelection";
            } else {
                throw new NotFoundException();
            }
        }
        return "";
    }

    //投标查看
    @RequestMapping(value = "/account/toCheckTenderDetail")
    @LoginRequired
    public String toCheckTenderDetail(@RequestParam(value = "id", required = true) int id,Map<String, Object> model) {
        Bid bid = bidMapper.getBidByIdUserid(id, session.getUser().getId());
        if (bid == null)
            throw new NotFoundException();
        TenderDeclaration tenderDeclaration = tenderApplyService.findDeclarcDetailByDidId(bid.getTenderdeclarationid(), id);
        if (tenderDeclaration == null)
            throw new NotFoundException();

        //公告附件
        model.put("attachmentpath", tenderDeclaration.getAttachmentpath());
        model.put("attachmentfilename", tenderDeclaration.getAttachmentfilename());
        model.put("tenderdeclar", tenderDeclaration);
        //投标附件
        model.put("tenderAttachmentPath",bid.getAttachmentpath());
        model.put("tenderAttachmentfileName",bid.getAttachmentfilename());
        return "/person/tenderWaitResult";

    }




    //投标中标结果导出
    @RequestMapping("/account/exportBidTender")
    public void exportBidTender(@RequestParam(value = "id", required = true) int id,HttpServletResponse response) throws Exception {
        TenderDeclaration tenderDeclaration = tenderdeclarMapper.findTenderDeclarByIdAndUserId(id, session.getUser().getId());
        if(tenderDeclaration==null){
            throw new NotFoundException();
        }
        double count=tenderdeclarMapper.countSupplyamount(id, session.getUser().getId());
        List<Map<String, Object>> list= tenderdeclarMapper.findBidTender(id, session.getUser().getId());
        if(list!=null&&list.size()>0) {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("招标中标结果导出");
            // 第一行样式
            HSSFFont headfont = wb.createFont();
            headfont.setFontName("宋体");
            headfont.setFontHeightInPoints((short) 18);// 字体大小
            HSSFCellStyle headstyle = wb.createCellStyle();
            headstyle.setFont(headfont);
            headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
            headstyle.setWrapText(true);// 自动换行

            //
            HSSFCellStyle style1 = wb.createCellStyle();
            style1.setAlignment(HSSFCellStyle.ALIGN_RIGHT);// 左右居中
            style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
            style1.setLocked(true);
            style1.setWrapText(true);// 自动换行
            HSSFFont font1 = wb.createFont();
            font1.setFontHeightInPoints((short) 11);// 字体大小
            font1.setFontName("宋体");
            style1.setFont(font1);

            //表头样式
            HSSFCellStyle style2 = wb.createCellStyle();
            style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
            HSSFFont font2 = wb.createFont();
            font2.setFontName("宋体");
            font2.setFontHeightInPoints((short) 12);// 字体大小
            style2.setFont(font2);
            style2.setWrapText(true);// 自动换行

            //列表样式
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
            HSSFFont font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 12);// 字体大小
            style.setFont(font);
            style.setWrapText(true);// 自动换行

            HSSFRow row0 = sheet.createRow(0);
            // 设置行高
            row0.setHeight((short) 1200);
            // 创建第一列
            HSSFCell cell0 = row0.createCell(0);
            cell0.setCellValue(new HSSFRichTextString(tenderDeclaration.getTenderunits()+"\n"+tenderDeclaration.getContractconbegindate().getMonthValue()+"月招标采购各供应商中标结果"));
            cell0.setCellStyle(headstyle);
            CellRangeAddress range = new CellRangeAddress(0, 0, 0, 7);
            sheet.addMergedRegion(range);

            HSSFRow row1 = sheet.createRow(1);
            row1.setHeight((short) 300);
            HSSFCell cell10 = row1.createCell(0);
            cell10.setCellValue(new HSSFRichTextString("来源:易煤网采购平台  导出时间:"+LocalDate.now()));
            cell10.setCellStyle(style1);
            CellRangeAddress range1 = new CellRangeAddress(1,1, 0,7);
            sheet.addMergedRegion(range1);

            String[] excelHeader = {"序号", "收货单位", "投标项目", "标包段", "供货商", "供应数量(万吨)","价格(元/百大卡.吨)","运输方式" };
            HSSFRow row = sheet.createRow((int) 2);
            for (int i = 0; i < excelHeader.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(excelHeader[i]);
                cell.setCellStyle(style2);
                sheet.autoSizeColumn(i);
            }

            // 设置列宽
            sheet.setColumnWidth(0, 3500);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 3500);
            sheet.setColumnWidth(3, 3500);
            sheet.setColumnWidth(4, 5000);
            sheet.setColumnWidth(5, 4500);
            sheet.setColumnWidth(6, 4500);
            sheet.setColumnWidth(7, 4500);


            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 3);
                HSSFCell cell01= row.createCell(0);
                cell01.setCellValue(i + 1);
                cell01.setCellStyle(style);

                HSSFCell cell02= row.createCell(1);
                cell02.setCellValue((String) list.get(i).get("tendername"));
                cell02.setCellStyle(style);

//                HSSFCell cell03= row.createCell(2);
//                cell03.setCellValue("项目" + list.get(i).get("projectId"));
//                cell03.setCellStyle(style);

                String  projectId=  changeNumToStr((Integer) list.get(i).get("projectId"));
                HSSFCell cell03= row.createCell(2);
                cell03.setCellValue("项目" + projectId);
                cell03.setCellStyle(style);

                HSSFCell cell04= row.createCell(3);
                cell04.setCellValue("标包" + list.get(i).get("packetId"));
                cell04.setCellStyle(style);

                HSSFCell cell05= row.createCell(4);
                cell05.setCellValue((String) list.get(i).get("releasecompanyname"));
                cell05.setCellStyle(style);

                HSSFCell cell06= row.createCell(5);
                cell06.setCellValue(Double.parseDouble(String.valueOf(list.get(i).get("purchaseamount"))));
                cell06.setCellStyle(style);

                HSSFCell cell07= row.createCell(6);
                cell07.setCellValue(Double.parseDouble(String.valueOf(list.get(i).get("price"))));
                cell07.setCellStyle(style);

                HSSFCell cell08= row.createCell(7);
                cell08.setCellValue((String)list.get(i).get("deliverymode"));
                cell08.setCellStyle(style);

            }
            row = sheet.createRow(list.size() + 3);
            HSSFCell cell09=row.createCell(0);
            cell09.setCellValue("合计");
            cell09.setCellStyle(style);
            HSSFCell cell11=row.createCell(1);
            cell11.setCellValue("本次应标总量为:" + count + "万吨");
            cell11.setCellStyle(style);
            CellRangeAddress range2 = new CellRangeAddress(list.size() + 3, list.size() + 4, 0, 0);
            sheet.addMergedRegion(range2);
            CellRangeAddress range3 = new CellRangeAddress(list.size() + 3, list.size() + 4, 1, 7);
            sheet.addMergedRegion(range3);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            String filename =tenderDeclaration.getTendercode();
            filename = URLEncoder.encode(filename, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + filename + ".xls");
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
        }else{
            throw new NotFoundException("暂无数据");
        }
    }



    //选标页面--我的投标导出excel
    @RequestMapping("/account/exportAllTender")
    public void exportAllTender(@RequestParam(value = "id", required = true) int id,HttpServletResponse response) throws Exception {
        TenderDeclaration tenderDeclaration = tenderdeclarMapper.findTenderDeclarByIdAndUserId(id, session.getUser().getId());
        if(tenderDeclaration==null){
            throw new NotFoundException();
        }
        double count=tenderdeclarMapper.countSupplyamount(id, session.getUser().getId());
        List<Map<String, Object>> list = tenderitemMapper.findTenderInDeclare(session.getUser().getId(), id);
        if(list!=null&&list.size()>0) {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("投标结果导出");
            // 第一行样式
            HSSFFont headfont = wb.createFont();
            headfont.setFontName("宋体");
            headfont.setFontHeightInPoints((short) 18);// 字体大小
            HSSFCellStyle headstyle = wb.createCellStyle();
            headstyle.setFont(headfont);
            headstyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            headstyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
            headstyle.setWrapText(true);// 自动换行

            //
            HSSFCellStyle style1 = wb.createCellStyle();
            style1.setAlignment(HSSFCellStyle.ALIGN_RIGHT);// 左右居中
            style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
            style1.setLocked(true);
            style1.setWrapText(true);// 自动换行
            HSSFFont font1 = wb.createFont();
            font1.setFontHeightInPoints((short) 11);// 字体大小
            font1.setFontName("宋体");
            style1.setFont(font1);

            //表头样式
            HSSFCellStyle style2 = wb.createCellStyle();
            style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
            HSSFFont font2 = wb.createFont();
            font2.setFontName("宋体");
            font2.setFontHeightInPoints((short) 12);// 字体大小
            style2.setFont(font2);
            style2.setWrapText(true);// 自动换行

            //列表样式
            HSSFCellStyle style = wb.createCellStyle();
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 左右居中
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 上下居中
            HSSFFont font = wb.createFont();
            font.setFontName("宋体");
            font.setFontHeightInPoints((short) 12);// 字体大小
            style.setFont(font);
            style.setWrapText(true);// 自动换行

            HSSFRow row0 = sheet.createRow(0);
            // 设置行高
            row0.setHeight((short) 1200);
            // 创建第一列
            HSSFCell cell0 = row0.createCell(0);
            cell0.setCellValue(new HSSFRichTextString(tenderDeclaration.getTenderunits()+"\n"+tenderDeclaration.getContractconenddate().getMonthValue()+"月招标采购各供应商投标结果"));
            cell0.setCellStyle(headstyle);
            CellRangeAddress range = new CellRangeAddress(0, 0, 0, 12);
            sheet.addMergedRegion(range);

            HSSFRow row1 = sheet.createRow(1);
            row1.setHeight((short) 300);
            HSSFCell cell10 = row1.createCell(0);
            cell10.setCellValue(new HSSFRichTextString("来源:易煤网采购平台  导出时间:"+LocalDate.now()));
            cell10.setCellStyle(style1);
            CellRangeAddress range1 = new CellRangeAddress(1,1, 0,12);
            sheet.addMergedRegion(range1);

            String[] excelHeader = {"序号", "收货单位", "投标项目", "标包段", "供应商名称", "品种","热值(Qnet,ar)","硫分(Std)","挥发分范围(Vdaf)","全水(TM)","投标供应量(万吨)","运输方式","价格(元/百大卡.吨)" };
            HSSFRow row = sheet.createRow((int) 2);
            for (int i = 0; i < excelHeader.length; i++) {
                HSSFCell cell = row.createCell(i);
                cell.setCellValue(excelHeader[i]);
                cell.setCellStyle(style2);
                sheet.autoSizeColumn(i);
            }

            // 设置列宽
            sheet.setColumnWidth(0, 3500);
            sheet.setColumnWidth(1, 5000);
            sheet.setColumnWidth(2, 3500);
            sheet.setColumnWidth(3, 3500);
            sheet.setColumnWidth(4, 5000);
            sheet.setColumnWidth(5, 3500);
            sheet.setColumnWidth(6, 3500);
            sheet.setColumnWidth(7, 3500);
            sheet.setColumnWidth(8, 4500);
            sheet.setColumnWidth(9, 3500);
            sheet.setColumnWidth(10, 3500);
            sheet.setColumnWidth(11, 3500);
            sheet.setColumnWidth(12, 3500);

            for (int i = 0; i < list.size(); i++) {
                row = sheet.createRow(i + 3);
                HSSFCell cell01= row.createCell(0);
                cell01.setCellValue(i +1);
                cell01.setCellStyle(style);

                HSSFCell cell02= row.createCell(1);
                cell02.setCellValue((String) list.get(i).get("receiptunits"));
                cell02.setCellStyle(style);
                String  projectId=  changeNumToStr((Integer) list.get(i).get("projectId"));
                HSSFCell cell03= row.createCell(2);
                cell03.setCellValue("项目" + projectId);
                cell03.setCellStyle(style);

                HSSFCell cell04= row.createCell(3);
                cell04.setCellValue("标包" + list.get(i).get("packetId"));
                cell04.setCellStyle(style);

                HSSFCell cell05= row.createCell(4);
                cell05.setCellValue((String) list.get(i).get("companyName"));
                cell05.setCellStyle(style);

                HSSFCell cell06= row.createCell(5);
                cell06.setCellValue((String) list.get(i).get("coaltype"));
                cell06.setCellStyle(style);

                HSSFCell cell07= row.createCell(6);
                cell07.setCellValue(Double.parseDouble(String.valueOf(list.get(i).get("NCV"))));
                cell07.setCellStyle(style);

                HSSFCell cell08= row.createCell(7);
                cell08.setCellValue(list.get(i).get("RS")+"%");
                cell08.setCellStyle(style);


                HSSFCell cell11= row.createCell(8);
                cell11.setCellValue(list.get(i).get("ADV")+"-"+list.get(i).get("ADV02")+"%");
                cell11.setCellStyle(style);

                HSSFCell cell12= row.createCell(9);
                cell12.setCellValue(list.get(i).get("TM")+"%");
                cell12.setCellStyle(style);

                HSSFCell cell13= row.createCell(10);
                cell13.setCellValue(Double.parseDouble(String.valueOf(list.get(i).get("supplyamount"))));
                cell13.setCellStyle(style);

                HSSFCell cell14= row.createCell(11);
                cell14.setCellValue((String) list.get(i).get("deliverymode"));
                cell14.setCellStyle(style);

                HSSFCell cell15= row.createCell(12);
                cell15.setCellValue(Double.parseDouble(String.valueOf(list.get(i).get("price"))));
                cell15.setCellStyle(style);

            }
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/x-download");
            String filename =tenderDeclaration.getTendercode();
            filename = URLEncoder.encode(filename, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + filename + ".xls");
            OutputStream out = response.getOutputStream();
            wb.write(out);
            out.close();
        }else{
            throw new NotFoundException("暂无数据");
        }
    }


    public String changeNumToStr(int num){
        String str="";
        switch(num)
        {
            case 1:
                str="一";
                break;
            case 2:
                str="二";
                break;
            case 3:
                str="三";
                break;
            case 4:
                str="四";
                break;
            case 5:
                str="五";
                break;
            case 6:
                str="六";
                break;
            case 7:
                str="七";
                break;
            case 8:
                str="八";
                break;
            case 9:
                str="九";
                break;
            case 10:
                str="十";
                break;
            default:
               str="";
        }
        return str;

    }

    /**
     * 招标专区
     */
    @RequestMapping(value = "/account/mytender", method = RequestMethod.GET)
    public String getMyTender(PageQueryParam queryParam,
                              @RequestParam(value = "status", required = false) TenderStatus tenderStatus,
                              @RequestParam(value = "year", required = false,defaultValue = "") String year,
                              @RequestParam(value = "month", required = false,defaultValue = "") String month,
                              @CurrentUser User user,
                              Model model) {
        String status = tenderStatus == null ? null : tenderStatus.toString();
        List<TenderDeclaration> allTender = tenderdeclarMapper.listAllStatus(user.getId());
        //暂缓发布招标个数
        long editCount = allTender.stream().filter(td -> TenderStatus.TENDER_EDIT.toString().equals(td.getStatus())).count();
        //投标中个数
        long inProcessCount = allTender.stream().filter(td -> TenderStatus.TENDER_START.toString().equals(td.getStatus())).count();
        //待选标
        long waitChooseCount = allTender.stream().filter(td -> TenderStatus.TENDER_CHOOSE_CONFIRM.toString().equals(td.getStatus())).count();
        //已放弃+已作废
        long cancelCount = allTender.stream().filter(td -> TenderStatus.TENDER_CANCEL.toString().equals(td.getStatus())).count();
        long giveUpCount = allTender.stream().filter(td -> TenderStatus.TENDER_GIVEUP.toString().equals(td.getStatus())).count()+cancelCount;
        int totalCount = tenderdeclarMapper.countMyTenderDeclaration(status, user.getId(), year, month, null);
        List<TenderDeclaration> tenderDeclarations = tenderdeclarMapper.myTenderDeclarationList(queryParam, user.getId(), status, year, month, null);
        int totalPage = totalCount / queryParam.getPagesize();
        totalPage = totalCount % queryParam.getPagesize() == 0 ? totalPage : totalPage + 1;
        queryParam.setTotalCount(totalCount);
        queryParam.setTotalPage(totalPage);
        queryParam.setList(tenderDeclarations);
        model.addAttribute("pageParam", queryParam);
        model.addAttribute("status", status);
        model.addAttribute("editCount", editCount);
        model.addAttribute("inProcessCount", inProcessCount);
        model.addAttribute("waitChooseCount", waitChooseCount);
        model.addAttribute("giveUpCount", giveUpCount);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("yearInterval", Collections2.transform(tenderDeclarationService.findYear(), t -> String.valueOf(t)));
        return "person/mytender";

    }

    /**
     * @param queryParam
     * @param year
     * @param month
     * @param tenderCode
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value = "/account/tenderNotice", method = RequestMethod.GET)
    public String tenderNotice(PageQueryParam queryParam,
                               @RequestParam(value = "year", required = false,defaultValue = "") String year,
                               @RequestParam(value = "month", required = false,defaultValue = "") String month,
                               @RequestParam(value = "tenderCode", required = false) String tenderCode,
                               @CurrentUser User user, Model model) {
        int totalCount = tenderdeclarMapper.countVerifyPaymentList(user.getId(), year, month, tenderCode);
        List<TenderDeclaration> tenderDeclarations = tenderdeclarMapper.verifyPaymentList(queryParam, user.getId(), year, month, tenderCode);
        int totalPage = totalCount / queryParam.getPagesize();
        totalPage = totalCount % queryParam.getPagesize() == 0 ? totalPage : totalPage + 1;
        queryParam.setTotalCount(totalCount);
        queryParam.setTotalPage(totalPage);
        queryParam.setList(tenderDeclarations);
        model.addAttribute("pageParam", queryParam);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("tenderCode", tenderCode);
        //页面年份查询区间
        model.addAttribute("yearInterval", Collections2.transform(tenderDeclarationService.findYear(), t -> String.valueOf(t)));
        return "person/tenderNotice";
    }

    //放弃投标
    @RequestMapping(value = "/account/giveupTender", method = RequestMethod.POST)
    @ResponseBody
    public Object giveupTender(@RequestParam(value = "id", required = true) int id) {
        Map map=new HashMap<String,Object>();
        Bid bid= bidMapper.getBidByIdUserid(id, session.getUser().getId());
        if(bid==null)
            throw new NotFoundException();
        if (mytenderMapper.updateMytenderStatusByDidId(id,session.getUser().getId(),TenderStatus.MYTENDER_GIVEUP.toString())==1&&bidMapper.updateBidStatusById(id, TenderStatus.MYTENDER_GIVEUP.toString()) == 1 || tenderApplyService.deleteByBidid(id) == 1) {
            map.put("success",true);
        } else {
            map.put("success",false);
        }
        return map;
    }



    //测试跳转到我所关注的需求页面
    @RequestMapping("/account/interest/demand")
    public String interestDemand() {
        return "person/interestDemand";
    }


    /////////////////////////////个人中心--物流专区--start/////////////////////////////

    //个人中心-物流专区列表
    @RequestMapping(value = "/account/getMyLogistics")
    public String getMyLogistics(
            @RequestParam(value = "status", required = false,defaultValue = "全部") String status,
            @RequestParam(value = "souceId", required = false,defaultValue = "") String souceId,
            @RequestParam(value = "type", required = false,defaultValue = "-1") int type,   //0:物流  2:船运
            PageQueryParam param, Map<String, Object> model) {
        int totalCount = logisticsMapper.countAllLogisticsintention(session.getUser().getId(), status, souceId, type);
        param.setCount(totalCount);
        List<Map<String,Object>> intendtionList =logisticsMapper.findAllLogisticsintention(session.getUser().getId(), status, param.getIndexNum(), param.getPagesize(),souceId,type);
        model.put("intendtionList", intendtionList);
        model.put("totalCount", totalCount);
        model.put("Pagesize", param.getPagesize());
        model.put("pageNumber", param.getPage());
        model.put("status", status);
        model.put("ppz", logisticsMapper.countLogisticsintention(session.getUser().getId(), souceId, type)); //匹配中数量(包括匹配中和已提交和未处理)
        model.put("ppwc", logisticsMapper.countAllLogisticsintention(session.getUser().getId(), LogisticsStatus.MATCH_COMPLETED.toString(), souceId, type)); //匹配完成数量
        model.put("ysz", logisticsMapper.countAllLogisticsintention(session.getUser().getId(), LogisticsStatus.TRANSPORT_ING.toString(), souceId, type)); //运送中数量
        model.put("ywc", logisticsMapper.countAllLogisticsintention(session.getUser().getId(), LogisticsStatus.COMPLETED_ALREADY.toString(), souceId, type)); //已完成数量
        model.put("wjy", logisticsMapper.countAllLogisticsintention(session.getUser().getId(), LogisticsStatus.NOT_TRADING.toString(), souceId, type)); //未交易数量
        model.put("souceId", souceId);
        model.put("type", type);
        return "person/myLogistics";
    }


    //个人中心-物流专区删除
    @LoginRequired
    @RequestMapping("/account/delMyLogisIntention")
    @ResponseBody
    public Object delMyLogisIntention(@RequestParam(value = "id", required = true) int id) {
        Logisticsintention logisticsintention= logisticsMapper.findByIdAndUserid(session.getUser().getId(), id);
        if(logisticsintention==null)
            throw new NotFoundException();
        Map<String, Object> map = new HashMap<String, Object>();
        logisticsMapper.delLogisIntention(id);
        map.put("success", true);
        return map;
    }

    //个人中心-物流专区取消
    @LoginRequired
    @RequestMapping("/account/cancelMyLogisIntention")
    @ResponseBody
    public Object cancelMyLogisIntention(@RequestParam(value = "id", required = true) int id) throws IOException{
        logger.info("------------/account/cancelMyLogisIntention--------------"+id);
        Logisticsintention logisticsintention= logisticsMapper.findByIdAndUserid(session.getUser().getId(), id);
        if(logisticsintention==null)
            throw new NotFoundException();
        Map<String, Object> map = new HashMap<String, Object>();
        String message="";
        boolean issuccess = false;
        if(logisticsintention.getIsfinish()==false) {
            logger.info("------------cancelPurpose--------------");
            if(logisticsintention.getStatus().equals(LogisticsStatus.TREATED_NOT.name())) {
                logisticsMapper.cancelLogisIntention(LogisticsStatus.NOT_TRADING.name(),logisticsintention.getId());
                logisticsMapper.updatCustomerremarkBySouceid("易煤网客户取消56汽运订单",logisticsintention.getSouceId());
                logisticsfeedbackMapper.updatecomment("易煤网客户取消56汽运订单",LogisticsStatus.NOT_TRADING.name(),logisticsintention.getSouceId());
                if(logisticsintention.getUserid()!=0) {
                    User user = userMapper.getUserById(logisticsintention.getUserid());
                    final String content = "尊敬的易煤网用户，您的意向单已经被取消，如需帮助，请拨打咨询热线：400-960-1180";
                    MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
                }
                message ="接收成功";
            }else {
                message = logisticsWebService.cancelPurpose(logisticsintention, "");
            }
            if (message.equals("接收成功")) {
                issuccess = true;
            }
        }else {
            if(logisticsintention.getStatus().equals(LogisticsStatus.MATCH_COMPLETED.name())) {
                message="此订单匹配完成，不能取消";
            }else {
                message="此订单已完成，不能取消";
            }

        }
        map.put("success", issuccess);
        map.put("message",message);
        return map;
    }

    @LoginRequired
    @RequestMapping("cancelshippurposedetailedit")
    @ResponseBody
    public Object cancelshippurposedetailedit(@RequestParam(value = "id", required = true)int id) {
        Logisticsintention logistisintention = logisticsMapper.findById(id);
        if (logistisintention == null)
            throw new NotFoundException();
        if (logistisintention.getStatus().equals(LogisticsStatus.TREATED_NOT.toString())) {
            logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.NOT_TRADING.name(), logistisintention.getSouceId());
            logisticsMapper.updatCustomerremarkBySouceid("易煤网客户取消船运订单", logistisintention.getSouceId());
            if(logistisintention.getUserid()!=0) {
                User user = userMapper.getUserById(logistisintention.getUserid());
                final String content = "尊敬的易煤网用户，您的意向单已经被取消，如需帮助，请拨打咨询热线：400-960-1180";
                MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
            }
            return new Object() {
                public String message = "物流订单取消成功";
                public boolean success=true;
            };
        } else if (logistisintention.getStatus().equals(LogisticsStatus.MATCH_COMPLETED.toString())||logistisintention.getStatus().equals(LogisticsStatus.MATCH_ING.toString())) {
            CancelShip cancelShip = new CancelShip();
            cancelShip.setNo(logistisintention.getSouceId());
            cancelShip.setIP("http://www.yimei180.com/");
            cancelShip.setRemark("易煤网客户取消订单");
            cancelShip.setEditDate(LocalDateTime.now().toString());
            try {
                ShipRet shipRet = logisticsShipClient.cancelInfo(cancelShip);
                if (shipRet.getRetMsg().equals("成功！")) {
                    logisticsshipfeedbackMapper.updateShipfeedback(logistisintention.getSouceId(), "易煤网客户取消船运订单", LogisticsStatus.NOT_TRADING.toString());
                    logisticsMapper.updatLogisIntentionStatusBySouceid(LogisticsStatus.NOT_TRADING.name(), logistisintention.getSouceId());
                    logisticsMapper.updatCustomerremarkBySouceid("易煤网客户取消船运订单", logistisintention.getSouceId());
                    if(logistisintention.getUserid()!=0) {
                        User user = userMapper.getUserById(logistisintention.getUserid());
                        final String content = "尊敬的易煤网用户，您的意向单已经被取消，如需帮助，请拨打咨询热线：400-960-1180";
                        MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
                    }
                    return new Object() {
                        public String message = "接收成功!";
                        public boolean success=true;
                    };
                } else {
                    return new Object() {
                        public String message = "订单取消失败";
                        public boolean success=false;
                    };
                }
            } catch (IOException exception) {
                return new Object() {
                    public String message = "物流供应商服务延时，请稍后再试";
                    public boolean success=false;
                };
            }

        }
        return new Object() {
            public String message = "此状态无法取消订单";
            public boolean success=false;
        };
    }




        //个人中心-物流专区评价
    @LoginRequired
    @RequestMapping("/account/evaluateMyLogisIntention")
    @ResponseBody
    public Object evaluateMyLogisIntention(@RequestParam(value = "id", required = true) int id,
                                           @RequestParam(value = "serviceevaluation", required = false,defaultValue = "") String serviceevaluation,
                                           @RequestParam(value = "servicescore", required = true) int servicescore,
                                           @RequestParam(value = "logisticsevaluation", required = false,defaultValue = "") String logisticsevaluation,
                                           @RequestParam(value = "logisticsscore", required = true) int logisticsscore
                                           ) {
        Logisticsintention logisticsintention= logisticsMapper.findByIdAndUserid(session.getUser().getId(), id);
        if(logisticsintention==null)
            throw new NotFoundException();
        Map<String, Object> map = new HashMap<String, Object>();
        logisticsMapper.evaluateLogisIntention(serviceevaluation, servicescore, logisticsevaluation, logisticsscore, id);
        map.put("success", true);
        return map;
    }

    //个人中心-物流专区查看详情
    @LoginRequired
    @RequestMapping("/account/detailLogisIntention")
    public String detailLogisIntention(@RequestParam(value = "id", required = true)int id,
                               @RequestParam(value = "detailtype", required = true) int detailtype,  //type:0 车运   2:船运
            Map<String, Object> model) {
        Logisticsintention logisticsintention= logisticsMapper.findByIdAndUserid(session.getUser().getId(), id);
        if(logisticsintention==null)
            throw new NotFoundException();
        Map<String,Object> itention= logisticsMapper.findDetailById(id);
        model.put("detailintention", itention) ;
        String path="";
        if(detailtype==0){
            //物流详情
            model.put("detailfeedback", logisticsfeedbackMapper.findDetailBySouceId((String) itention.get("souceId"))) ;
            path="logistics/logisticsCarDetail";
        }else if(detailtype==2){
            //船运详情
            model.put("detailfeedback", logisticsshipfeedbackMapper.findFeedBackBySouceId((String) itention.get("souceId"))) ;
            model.put("shipinfo",shipfoMapper.findShipAnchorPointInfosBySourceId((String) itention.get("souceId")));
            path="logistics/logisticsShipDetail";
        }
        model.put("detailtype", detailtype);
        return path;
    }

    //个人中心-查看评价
    @LoginRequired
    @ResponseBody
    @RequestMapping("/account/getLogisticsDetail")
    public Object getLogisticsDetail(@RequestParam(value = "id", required = true)int id,
                                       Map<String, Object> model) {
        Logisticsintention logisticsintention= logisticsMapper.findByIdAndUserid(session.getUser().getId(), id);
        if(logisticsintention==null)
            throw new NotFoundException();
        Map<String,Object> itention= logisticsMapper.findDetailById(id);
        Map map=new HashMap<>();
        map.put("logisticsintention",itention);
        return map;
    }


    public void checkCompany(){
        User user = userMapper.getUserByPhone(session.getUser().getSecurephone());
        if(user == null) throw new NotFoundException();
        Company company = companyMapper.getCompanyByUserid(user.getId());
        if(company != null) {
            if (user.getVerifystatus().equals("待完善信息")) {
                throw new BusinessException("请完善公司信息");
            } else if (user.getVerifystatus().equals("待审核") || company.getVerifystatus().equals("待审核")) {
                throw new BusinessException("公司信息审核中");
            } else if (user.getVerifystatus().equals("审核未通过") || company.getVerifystatus().equals("审核未通过")) {
                throw new BusinessException("公司信息审核未通过");
            }
        } else{
            throw new BusinessException("公司信息未找到");
        }

    }


}

