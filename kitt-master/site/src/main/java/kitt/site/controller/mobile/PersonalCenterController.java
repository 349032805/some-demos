package kitt.site.controller.mobile;

import com.google.common.collect.Maps;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.CODE;
import kitt.core.service.SMS;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.FileService;
import kitt.site.service.Session;
import kitt.site.service.mobile.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 15/6/18.
 */
@Controller("mobilePersonalCenterController")
@RequestMapping("/m")
public class PersonalCenterController extends JsonController {
    @Autowired
    private DemandService demandService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SupplyService supplyService;
    @Autowired
    private ManualsellService manualsellService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    protected Session session;
    @Autowired
    protected SMS sms;
    @Autowired
    protected ValidMapper validMapper;
    @Autowired
    protected CODE code;
    @Autowired
    protected FileService fileService;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private BuyMapper buyMapper;
    private String[] selectedSellTab = {"inProgress", "canceled", "completed"};
    private String[] selectedBuyTab = {"inProgress", "return", "canceled", "completed"};

    /**
     * 个人中心--买货订单/卖货订单
     * 初始化买货订单
     * @param user
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/account/buyOrder", method = RequestMethod.GET)
    public String initBuyOrders(@CurrentUser User user, @RequestParam(value = "select", required = false, defaultValue = "inProgress") String select, Model model) {
        PageQueryParam inProgressParam = new PageQueryParam();
        inProgressParam = orderService.loadBuyOrderInProgress(inProgressParam, user.getId());
        model.addAttribute("inProgressOrders", inProgressParam);

        PageQueryParam returnGoodsParam = new PageQueryParam();
        returnGoodsParam = orderService.loadBuyOrderReturnGoods(returnGoodsParam, user.getId());
        model.addAttribute("returnGoodsOrders", returnGoodsParam);

        PageQueryParam canceledParam = new PageQueryParam();
        canceledParam = orderService.loadBuyOrderCanceled(canceledParam, user.getId());
        model.addAttribute("canceledOrders", canceledParam);

        PageQueryParam completedParam = new PageQueryParam();
        completedParam = orderService.loadBuyOrderCompleted(completedParam, user.getId());
        model.addAttribute("completedOrders", completedParam);

        // in order to prevent XSS
        boolean flag = false;
        for (String tmp : selectedBuyTab) {
            flag = true;
        }
        if (!flag) {
            select = "inProgress";
        }

        model.addAttribute("select", select);
        return "/m/myzone/myZone_buyOrder";
    }

    /**
     * 查询买货订单
     * @param type  InProgress/ReturnGoods/Canceled/Completed
     * @param user
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/account/buyOrder/more", method = RequestMethod.GET)
    public String getBuyOrders(PageQueryParam param,
                               @RequestParam(value = "type", required = true, defaultValue = "") String type,
                               @RequestParam(value = "select", required = false, defaultValue = "") String select,
                               @CurrentUser User user, Model model) {
        if (type.equals("InProgress")) {
            param = orderService.loadBuyOrderInProgress(param, user.getId());
            model.addAttribute("orderInfo", param);
            model.addAttribute("select", select);
        } else if (type.equals("ReturnGoods")) {
            param = orderService.loadBuyOrderReturnGoods(param, user.getId());
            model.addAttribute("select", select);
            model.addAttribute("orderInfo", param);
        } else if (type.equals("Canceled")) {
            param = orderService.loadBuyOrderCanceled(param, user.getId());
            model.addAttribute("select", select);
            model.addAttribute("orderInfo", param);
        } else if (type.equals("Completed")) {
            param = orderService.loadBuyOrderCompleted(param, user.getId());
            model.addAttribute("select", select);
            model.addAttribute("orderInfo", param);
        }
        return "/m/myzone/myZone_buyOrderList";
    }

    /**
     * 初始化卖货订单
     * @param user
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/account/sellOrder", method = RequestMethod.GET)
    public String initSellOrders(@CurrentUser User user, @RequestParam(value = "select", required = false, defaultValue = "inProgress") String select, Model model) {

        PageQueryParam inProgressParam = new PageQueryParam();
        inProgressParam = orderService.loadSellOrderInProgress(inProgressParam, user.getId());
        model.addAttribute("inProgressOrders", inProgressParam);

        PageQueryParam completedParam = new PageQueryParam();
        completedParam = orderService.loadSellOrderCompleted(completedParam, user.getId());
        model.addAttribute("completedOrders", completedParam);

        PageQueryParam canceledParam = new PageQueryParam();
        canceledParam = orderService.loadSellOrderCanceled(canceledParam, user.getId());
        model.addAttribute("canceledOrders", canceledParam);

        // in order to prevent XSS
        boolean flag = false;
        for (String tmp : selectedSellTab) {
            flag = true;
        }
        if (!flag) {
            select = "inProgress";
        }

        model.addAttribute("select", select);
        return "/m/myzone/myZone_sellOrder";
    }

    /**
     * 查询卖货订单
     * @param param
     * @param type  InProgress/Completed/Canceled
     * @param user
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/account/sellOrder/more", method = RequestMethod.GET)
    public String getSellOrders(PageQueryParam param,
                                @RequestParam(value = "type", required = true, defaultValue = "") String type,
                                @RequestParam(value = "select", required = false, defaultValue = "") String select,
                                @CurrentUser User user, Model model) {
        if (type.equals("InProgress")) {
            param = orderService.loadSellOrderInProgress(param, user.getId());
            model.addAttribute("select", select);
            model.addAttribute("orderInfo", param);
        } else if (type.equals("Completed")) {
            param = orderService.loadSellOrderCompleted(param, user.getId());
            model.addAttribute("select", select);
            model.addAttribute("orderInfo", param);
        } else if (type.equals("Canceled")) {
            param = orderService.loadSellOrderCanceled(param, user.getId());
            model.addAttribute("select", select);
            model.addAttribute("orderInfo", param);
        }

        return "/m/myzone/myZone_sellOrderList";
    }

    /**
     * 卖货订单 卖货订单详情信息
     * @param id
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping("/account/orderInfo/detail")
    public String doGetMyOrders(@RequestParam(value = "id", required = true) int id,
                                @RequestParam(value = "flag", required = false) String flag,
                                Map<String, Object> model) {
        orderService.orderInfos(id, model);
        model.put("flag", flag);
        return "/m/orderDetail";
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/cancelOrder", method = RequestMethod.GET)
    public Object cancelOrder(@RequestParam(value = "id", required = true) int id,
                              @RequestParam(value = "version", required = true) int version,
                              @CurrentUser User user) {
        Map<String, Object> maps = Maps.newHashMap();
        orderService.cancelOrder(id, version, user);
        maps.put("success", true);
        return maps;

    }

    /**
     * 删除买货订单
     * @param id
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/buy/deleteOrder", method = RequestMethod.GET)
    public Object deleteOrder(@RequestParam(value = "id", required = true) int id,
                              @RequestParam(value = "version", required = true) int version,
                              @CurrentUser User user) {
        Map<String, Object> maps = Maps.newHashMap();
        orderService.deleteOrder(id, version, user);
        maps.put("success", true);
        return maps;
    }


    /**
     * 删除卖货订单
     * @param id
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/sell/deleteSellOrder", method = RequestMethod.GET)
    public Object deleteSellOrder(@RequestParam(value = "id", required = true) int id,
                                  @RequestParam(value = "version", required = true) int version,
                                  @CurrentUser User user) {
        Map<String, Object> maps = Maps.newHashMap();
        orderService.deleteSellOrder(id, version, user);
        maps.put("success", true);
        return maps;
    }

    /**
     * 申请退货
     * @param id
     * @param user
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/applyReturnGoods", method = RequestMethod.GET)
    public Object applyReturnGoods(@RequestParam(value = "id", required = true) int id,
                                   @RequestParam(value = "version", required = true) int version,
                                   @CurrentUser User user) {
        Map<String, Object> maps = Maps.newHashMap();
        orderService.applyReturnGoods(id, version, user);
        maps.put("success", true);
        return maps;
    }


    /**
     * 撤销退货
     * @param id
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/cancelReturnGoods", method = RequestMethod.GET)
    public Object cancelReturnGoods(@RequestParam(value = "id", required = true) int id,
                                    @RequestParam(value = "version", required = true) int version,
                                    @CurrentUser User user) {
        Map<String, Object> maps = Maps.newHashMap();
        orderService.cancelReturnGoods(id, version, user);
        maps.put("success", true);
        return maps;
    }

    @ResponseBody
    @RequestMapping(value = "/account/confirmUploadDeposit")
    @LoginRequired
    public Object payMyOrder(@RequestParam(value = "id", required = true) int id,
                             @RequestParam(value = "version", required = true) int version,
                             @RequestParam(value = "pid01", required = false, defaultValue = "0") int pid01,
                             @RequestParam(value = "pid02", required = false, defaultValue = "0") int pid02,
                             @RequestParam(value = "pid03", required = false, defaultValue = "0") int pid03,
                             @CurrentUser User user) {
        Order order = orderService.getOrderByIdAndVersion(id, version);
        if (order == null || (pid01 == 0 && pid02 == 0 && pid03 == 0)) throw new NotFoundException();
        if (order.getStatus().equals(EnumOrder.WaitPayment) || order.getStatus().equals(EnumOrder.WaitBalancePayment) || order.getStatus().equals(EnumOrder.VerifyNotPass)) {
            orderService.payOrderCompleted(order, pid01, pid02, pid03, user);
            return true;
        }
        return false;
    }

    /**
     * 个人中心--我的关注
     * 查找我的关注
     * @param param type:demand/supply
     * @return param
     */
    @LoginRequired
    @RequestMapping(value = "/account/MyInterest/watchedList", method = RequestMethod.GET)
    public String accountMyInterestWatched(PageQueryParam param,
                                           @RequestParam(value = "type", required = true, defaultValue = "supply") String type,
                                           @CurrentUser User user,
                                           Model model) {
        param = demandService.loadMyInterestList(param, type, user);
        model.addAttribute("selectType", type);
        model.addAttribute("orderInfo", param);
        if (param.getPage() > 1) {
            return "/m/myzone/myZone_attendList";
        } else {
            return "/m/myzone/myZone_attend";
        }
    }

    @LoginRequired
    @RequestMapping(value = "/account/MyInterest/watchedList/more", method = RequestMethod.GET)
    public Object accountMyInterestWatchedMore(PageQueryParam param,
                                               @RequestParam(value = "type", required = true, defaultValue = "supply") String type,
                                               @CurrentUser User user, Model model) {
        param = demandService.loadMyInterestList(param, type, user);
        model.addAttribute("orderInfo", param);
        return "/m/attendPageData";
    }

    //未使用
    @LoginRequired
    @RequestMapping(value = "/account/MyInterest/SupplyDetail/{id}", method = RequestMethod.GET)
    public String myInterestSupplyDetail(@PathVariable("id") int id, Model model) {
        model.addAttribute("supply", supplyService.loadSellDeatilByInterestId(id));
        return "/m/supply/supplyDetail";
    }

    //检查我关注的产品状态
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/MyInterest/CheckSupplyDemandStatus/{sid}/{flag}", method = RequestMethod.GET)
    public Object CheckSupplyDemandStatus(@PathVariable("sid") int sid, @PathVariable("flag") String flag) {
        Map<String, Object> maps = Maps.newHashMap();
        String message = "";
        if (flag.equals("supply")) {
            message = supplyService.checkSellStatus(sid);
            if (message == null) {
                maps.put("success", true);
            } else {
                maps.put("success", false);
            }
        } else if (flag.equals("demand")) {
            Demand demand = demandService.getDemandByDemandId(sid);
            if (demand == null)
                throw new NotFoundException();
            if (demand.isIsdelete() == true) {
                maps.put("success", false);
                message = "该需求已经过期!";
            } else {
                maps.put("success", true);
            }
        }
        maps.put("message", message);
        maps.put("flag", flag);
        return maps;
    }

    @LoginRequired
    @RequestMapping(value = "/account/MyInterest/DemandDetail/{flag}/{id}", method = RequestMethod.GET)
    public String myInterestDemandDetail(@PathVariable("id") int id, @PathVariable("flag") String flag, Model model) {
        Demand demand = demandService.getDemandByDemandId(id);
        if (demand == null)
            throw new NotFoundException();
        LocalDate now = LocalDate.now();
        if (demand.getQuoteenddate().isBefore(now)) {
            model.addAttribute("canquote", "false");
        } else {
            model.addAttribute("canquote", "true");
        }
        model.addAttribute("demand", demand);
        model.addAttribute("status", flag);
        model.addAttribute("coalTypes", demandService.loadCoalTypes());
        model.addAttribute("pslist", dataBookMapper.getDataBookListByType("pstype"));
        return "/m/demandDetail";
    }

    /**
     * 删除我的关注
     * @param id myinterestId
     * @return maps
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/MyInterest/cancelMyInterest", method = RequestMethod.GET)
    public Object cancelMyInterest(@RequestParam(value = "id", required = true) int id) {
        Map<String, Object> maps = Maps.newHashMap();
        demandService.mCancelMyInterest(id);
        maps.put("success", true);
        return maps;
    }

    /**
     * 个人中心--我的报价
     * 我的报价--查询
     * @param param
     * @param type  GetMyQuote:我的报价--进行中  GetMyQuoteBid:我的报价--已中标  GetMyQuoteNotBid:我的报价--未中标 more:更多
     * @param user
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/account/MyQuote/getMyQuote", method = RequestMethod.GET)
    public String getMyQuote(PageQueryParam param,
                             @RequestParam(value = "type", required = true, defaultValue = "GetMyQuote") String type,
                             @CurrentUser User user,
                             Model model) {
        if (type.equals("GetMyQuote")) {
            param = quoteService.loadTurnpageUnderwayList(param, user);
            model.addAttribute("myQuoteInfo", param);
            model.addAttribute("quoteType", type);
            if (param.getPage() > 1) {
                return "/m/myzone/myZone_quoteList";
            } else {
                return "/m/myzone/myZone_quote";
            }
        } else if (type.equals("GetMyQuoteBid")) {
            param = quoteService.loadMyQuoteBid(param, user);
            model.addAttribute("myQuoteInfo", param);
            model.addAttribute("quoteType", type);
            if (param.getPage() > 1) {
                return "/m/myzone/myZone_quoteList";
            } else {
                return "/m/myzone/myZone_quote";
            }
        } else if (type.equals("GetMyQuoteNotBid")) {
            param = quoteService.loadGetMyQuoteNotBid(param, user);
            model.addAttribute("myQuoteInfo", param);
            model.addAttribute("quoteType", type);
            if (param.getPage() > 1) {
                return "/m/myzone/myZone_quoteList";
            } else {
                return "/m/myzone/myZone_quote";
            }
        }
        return "";
    }

    //判断是否下架
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/MyQuote/checkDemand", method = RequestMethod.GET)
    public Object checkDemand(@RequestParam(value = "quoteId", required = true) int quoteId, @CurrentUser User user) {
        Quote quote = demandService.getQuote(quoteId);
        Map<String, Object> maps = Maps.newHashMap();
        if (quote == null)
            throw new NotFoundException();
        boolean isdelete = false;
        Demand demand = demandService.getDemand(quote.getDemandcode());
        if (demand != null)
            isdelete = demand.isIsdelete();
        maps.put("isdelete", isdelete);
        return maps;
    }

    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/MyQuote/checkByDemandId", method = RequestMethod.GET)
    public Object IsValidateDemand(@RequestParam(value = "demandId", required = true) int demandId, @CurrentUser User user) {
        Demand demand = demandService.getDemandByDemandId(demandId);
        Map<String, Object> maps = Maps.newHashMap();
        String message = "";
        boolean success = true;
        if (demand == null) {
            throw new NotFoundException();
        } else if ((demand.getTradestatus().equals("报价结束")) && (demand.isIsdelete() == false)) {
            message = "该需求已经过期";
        } else if ((demand.getTradestatus().equals("报价结束")) && (demand.isIsdelete() == true)) {
            message = "该需求已删除!";
            success = false;
        }
        maps.put("message", message);
        maps.put("success", success);
        return maps;
    }

    /**
     * 我的报价-删除
     * @param id
     * @param user
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/MyQuote/deleteMyQuote", method = RequestMethod.GET)
    public Object deleteMyquote(@RequestParam(value = "id", required = true) int id,
                                @CurrentUser User user) {
        Map<String, Object> maps = Maps.newHashMap();
        quoteService.deleteMyquote(id, user);
        maps.put("success", true);
        return maps;
    }

    /**
     * 个人中心--我的需求
     * 个人中心-查询我的需求列表
     */
    @LoginRequired
    @RequestMapping(value = "/account/MyDemand/getMyDemand", method = RequestMethod.GET)
    public String getMyQuote(PageQueryParam param, @CurrentUser User user, Model model) {
        param = demandService.getMyDemand(param, user);
        model.addAttribute("myDemandInfo", param);
        if (param.getPage() > 1) {
            return "/m/myzone/myZone_demandList";
        } else {
            return "/m/myzone/myZone_demand";
        }
    }

    /**
     * 个人中心-我的需求详细信息
     * @param demandCode
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/account/MyDemand/viewMyDemand", method = RequestMethod.GET)
    public String viewMyDemand(@RequestParam(value = "demandCode", required = true) String demandCode, Model model) {
        Demand demand = demandService.getDemand(demandCode);
        List<Quote> quoteList = demandService.getQuoteList(demandCode);
        model.addAttribute("demand", demand);
        model.addAttribute("quoteList", quoteList);
        return "/m/myzone/myZone_demandDetail";
//        return "/m/demandDetail";
    }

    /**
     * 个人中心-我的需求报价页面
     * @param demandCode
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/account/MyDemand/ChooseQuote", method = RequestMethod.GET)
    public String ChooseQuote(@RequestParam(value = "demandCode", required = true) String demandCode, Model model) {
        Demand demand = demandService.getDemand(demandCode);
        List<Quote> quoteList = demandService.getQuoteList(demandCode);
        model.addAttribute("demand", demand);
        if (0 < quoteList.size()) {
            model.addAttribute("quoteList", quoteList);
            model.addAttribute("myDemand_status", demandService.loadMyDemand(demandCode).getStatus());
        }
        return "/m/myzone/myZone_viewDemand";
    }

    /**
     * 个人中心-我的需求报价详细页面
     * @param id
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/account/MyDemand/ChooseTotalQuote", method = RequestMethod.GET)
    public String ChooseTotalQuote(@RequestParam(value = "id", required = true) int id,
                                   @RequestParam(value = "index", required = true) int index, Model model) {
        Quote quote = demandService.getQuote(id);
        if (null == quote) {
            throw new NotFoundException();
        } else {
            model.addAttribute("quote", quote);
            model.addAttribute("quote_index", index);
            model.addAttribute("demandCode", (demandService.getDemandByDemandId(quote.getDemandid())).getCoaltype());
            model.addAttribute("myDemand_status", demandService.loadMyDemand(quote.getDemandcode()).getStatus());
        }
        return "/m/myzone/myZone_viewTotalDemand";
    }


    /**
     * 个人中心-我的需求删除
     * @param demandCode
     * @param user
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/MyDemand/deleteMydemand", method = RequestMethod.POST)
    public Object deleteMydemand(@RequestParam(value = "demandCode", required = true) String demandCode, @CurrentUser User user) {
        Map<String, Object> maps = Maps.newHashMap();
        demandService.deleteMydemand(user, demandCode);
        maps.put("success", true);
        return maps;
    }

    /**
     * 我的需求-取消发布
     * @param demandCode
     * @param user
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/MyDemand/cancelMydemand", method = RequestMethod.POST)
    public Object cancelMydemand(@RequestParam(value = "demandCode", required = true) String demandCode, @CurrentUser User user, BindResult bindResult) {
        Demand demand = demandService.loadDemandDetailForDemandCode(demandCode, user.getId());
        //防止出现由于状态不一致，造成的丢失更新
        if (demand.getCheckstatus().equals("审核通过") || demand.getCheckstatus().equals("审核未通过")) {
            bindResult.addError("status", "状态异常");
        } else {
            demandService.cancelMydemand(demand);
        }
        return json(bindResult);
    }

    /**
     * 报价-中标
     * @param quoteid
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/MyDemand/quoteBid", method = RequestMethod.POST)
    public Object quoteBid(@RequestParam(value = "quoteid", required = true) int quoteid) {
        Map<String, Object> maps = Maps.newHashMap();
        demandService.quoteBid(quoteid);
        maps.put("success", true);
        return maps;
    }

    /**
     * 个人中心--我的供应
     * 个人中心-我的供应-列表查询
     */
    @LoginRequired
    @RequestMapping(value = "/account/MySupply/getMySupply", method = RequestMethod.GET)
    public String getMySupply(PageQueryParam param, @CurrentUser User user, Model model) {
        param = supplyService.getMySupply(param, user);
        model.addAttribute("mySupplyInfo", param);
        if (param.getPage() > 1) {
            return "/m/myzone/myZone_supplyList";
        } else {
            return "/m/myzone/myZone_supply";
        }
    }

    /**
     * 个人中心-我的供应-查看信息详细
     * @param pid
     * @param model
     * @return
     */
    @LoginRequired
    @RequestMapping(value = "/account/MySupply/viewSupplyDetail", method = RequestMethod.GET)
    public String viewSupplyDetail(@RequestParam(value = "pid", required = true) String pid, Model model) {
        SellInfo sellInfo = supplyService.getActiveSellInfoById(pid);
        model.addAttribute("supply", sellInfo);
        model.addAttribute("supplyEditHist", buyMapper.getSellInfoEditHist(sellInfo.getId(), sellInfo.getParentid()));
        return "/m/myzone/myZone_supplyDetail";
    }

    @LoginRequired
    @RequestMapping("/account/MySupply/updateMySupply")
    public String updateMySupply(@RequestParam(value = "pid", required = true) String pid, Model model) {
        SellInfo sellInfo = supplyService.getActiveSellInfoById(pid);
        List<Dictionary> coalTypeList = supplyService.loadCoalTypes();
        List<Dictionary> deliverymodeList = supplyService.getDeliverymodes();
        List<Dictionary> inspectionagencyList = supplyService.loadInspectionagencys();
        List<Areaport> ProvinceList = supplyService.loadProvince();
        List<Areaport> portList = supplyService.loadPort(sellInfo.getDeliveryprovince());
        model.addAttribute("supply", sellInfo);
        model.addAttribute("coalTypeList", coalTypeList);
        model.addAttribute("deliverymodeList", deliverymodeList);
        model.addAttribute("inspectionagencyList", inspectionagencyList);
        model.addAttribute("provinceList", ProvinceList);
        model.addAttribute("portList", portList);
        model.addAttribute("pslist", dataBookMapper.getDataBookListByType("pstype"));
        // 根据editnum 和 VerifyPass 判断是否曾经通过审核
        if ((EnumSellInfo.VerifyPass).equals(sellInfo.getStatus()) || sellInfo.getEditnum() > 0) {
            model.addAttribute("verifiedOnce", "true");
            return "/m/myzone/myZone_supplyUpdatePart";
        } else {
            model.addAttribute("verifiedOnce", "false");
            return "m/myzone/myZone_supplyUpdate";
        }
    }

    //修改全部供应信息
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/AddSellInfo", method = RequestMethod.POST)
    public Object addSellInfo(SellInfo sellInfo,
                              @RequestParam(value = "chemicalexam1", required = false, defaultValue = "") String chemicalexam1,
                              @RequestParam(value = "chemicalexam2", required = false, defaultValue = "") String chemicalexam2,
                              @RequestParam(value = "chemicalexam3", required = false, defaultValue = "") String chemicalexam3,
                              @CurrentUser User user) throws Exception {
        Map<String, Object> maps = Maps.newHashMap();
        sellInfo = supplyService.prepareAllSellInfo(sellInfo, user.getId());
        if (sellInfo.getDeliverytime1() == null)
            throw new BusinessException("提货时间错误");
        if (sellInfo.getDeliverytime1().isBefore(LocalDate.now().minusDays(1))) {
            throw new BusinessException("时间验证错误");
        } else {
            if (!EnumSellInfo.VerifyPass.equals(sellInfo.getStatus())) {
                supplyService.updateMySupply(sellInfo, chemicalexam1, chemicalexam2, chemicalexam3);
                maps.put("success", true);
                return maps;
            } else {
                throw new BusinessException("此供应已审核通过，无法修改全部信息");
            }
        }
    }

    //修改部分供应信息
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/editSellinfo", method = RequestMethod.POST)
    public Object editSellinfo(SellInfo sellInfoInput,
                               @RequestParam(value = "chemicalexam1", required = false, defaultValue = "") String chemicalexam1,
                               @RequestParam(value = "chemicalexam2", required = false, defaultValue = "") String chemicalexam2,
                               @RequestParam(value = "chemicalexam3", required = false, defaultValue = "") String chemicalexam3,
                               @CurrentUser User user) throws Exception {
        Map<String, Object> maps = Maps.newHashMap();

        SellInfo sellInfo = supplyService.preparePartSellInfo(sellInfoInput);
        if (EnumSellInfo.VerifyPass.equals(sellInfo.getStatus())) {
            if (0 == sellInfo.getParentid())
                sellInfo.setParentid(sellInfo.getId());
            sellInfo.setStatus(EnumSellInfo.WaitVerify);
            sellInfo.setEditnum(sellInfo.getEditnum() + 1);
            supplyService.addSellinfoForUpdate(sellInfo, chemicalexam1, chemicalexam2, chemicalexam3, user);
        } else {
            supplyService.updateMySupply(sellInfo, chemicalexam1, chemicalexam2, chemicalexam3);
        }
        maps.put("success", true);
        return maps;
    }

    /**
     * 个人中心-我的供应-取消供应信息
     * @param id
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/MySupply/cancelMySupply", method = RequestMethod.GET)
    public Object cancelMySupply(@RequestParam(value = "id", required = true) int id) {
        Map<String, Object> maps = Maps.newHashMap();
        supplyService.cancelMySupply(id);
        maps.put("success", true);
        return maps;
    }

    /**
     * 个人中心-我的供应-删除供应信息
     * @param id
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/MySupply/deleteMySupply", method = RequestMethod.GET)
    public Object deleteMySupply(@RequestParam(value = "id", required = true) int id) {
        Map<String, Object> maps = Maps.newHashMap();
        supplyService.deleteMySupply(id);
        maps.put("success", true);
        return maps;
    }

    /***********************************个人中心--人工销售--人工找货**********************************/

    /**
     * 人工销售
     * @param param
     * @param dateRange : thirty_day--30天以内/three_month--3个月以内/half_year--半年以内/all--全部
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value = "/account/ManualSell/showSellList")
    @LoginRequired
    public String showSellList(PageQueryParam param, @RequestParam(value = "dateRange", required = false, defaultValue = "thirty_day") String dateRange, @CurrentUser User user, Model model) {
        param = manualsellService.show(param, false, dateRange, user);
        model.addAttribute("showSellList", param);
        return "/m/myzone/myZone_showSellList";
    }

    /**
     * 人工找货
     * @param param
     * @param dateRange : thirty_day--30天以内/three_month--3个月以内/half_year--半年以内/all--全部
     * @param user
     * @param model
     * @return
     */
    @RequestMapping(value = "/account/ManualSell/manualFindList")
    @LoginRequired
    public String showlookupList(PageQueryParam param, @RequestParam(value = "dateRange", required = false, defaultValue = "thirty_day") String dateRange, @CurrentUser User user, Model model) {
        param = manualsellService.show(param, true, dateRange, user);
        model.addAttribute("manualFindList", param);
        model.addAttribute("dateRange", dateRange);
        return "/m/myzone/myZone_manualFind";
    }

    /**
     * 删除人工信息
     * @param manualsellId
     * @param user
     * @return
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/ManualSell/deleteManualSell", method = RequestMethod.POST)
    public Object deleteManualSell(@RequestParam(value = "manualsellId", required = true) String manualsellId, @CurrentUser User user) {
        Map<String, Object> maps = Maps.newHashMap();
        manualsellService.DeleteManualSell(manualsellId, user);
        maps.put("success", true);
        return maps;
    }

    //进入我的账户
    @LoginRequired
    @RequestMapping(value = "/account/myAccount", method = RequestMethod.GET)
    public String myAccount(Model model, @CurrentUser User user) {
        User u = userMapper.getUserById(user.getId());
        Company company = companyMapper.getCompanyByUserid(user.getId());
        model.addAttribute("user", u);
        model.addAttribute("company", company);
        return "/m/myzone/myZone_myAccount";
    }

    //查看我的公司信息
    @LoginRequired
    @RequestMapping(value = "/account/viewMyCompanyInfo", method = RequestMethod.GET)
    public String viewMyCompanyInfo(@CurrentUser User user, Model model) {
        Company company = companyMapper.getCompanyByUserid(user.getId());
        model.addAttribute("company", company);
        int companyIsExist = 0;
        if (company != null) {
            companyIsExist = 1;
        }
        model.addAttribute("companyIsExist", companyIsExist);
        return "/m/myzone/myZone_myCompany";
    }

    //保存客户的固话和QQ
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/savePhoneAndQQ", method = RequestMethod.POST)
    public Object savePhoneAndQQ(String telephone, String qq, @CurrentUser User user) {
        userMapper.modifyPhoneAndQQ(telephone, qq, user.getId());
        user.setTelephone(telephone);
        user.setQq(qq);
        Map map = new HashMap<>();
        map.put("success", true);
        return map;
    }

    //登出
    @LoginRequired
    @RequestMapping(value = "/account/loginOut", method = RequestMethod.GET)
    public String loginOut() {
        session.logout();
        return "redirect:/m/account";
    }

    //检查手机号是否已存在
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/checkPhoneIsExist", method = RequestMethod.POST)
    public Object checkPhoneIsExist(String newPhone) {
        boolean success = true;
        User u = userMapper.getUserByPhone(newPhone);
        if (u != null) {
            success = false;
        }
        Map map = new HashMap<>();
        map.put("success", success);
        return map;
    }

    //检查客户输入的验证码
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/checkValidCode", method = RequestMethod.POST)
    public Object checkValidCode(String code, @CurrentUser User user, BindResult bindResult) {
        Phonevalidator phonevalidator = validMapper.findVerifyCode(user.getSecurephone(), code, ValidateType.updatePhoneNumWeixin);
        if (StringUtils.isBlank(code)) {
            bindResult.addError("verifyCode", "验证码不能为空!");
        } else if (phonevalidator == null) {
            bindResult.addError("verifyCode", "验证码错误!");
        } else if (phonevalidator.getExpiretime().isBefore(LocalDateTime.now())) {
            bindResult.addError("verifyCode", "验证码已过期!");
        } else {
            validMapper.modifyValidatedAndTime(user.getSecurephone(), code);
        }
        return json(bindResult);
    }

    //忘记密码发送验证码
    @ResponseBody
    @RequestMapping(value = "/account/sendValidForgetCode", method = RequestMethod.POST)
    public Object sendValidCode(String phone) {
        boolean success = true;
        String errorMsg = null;
        User user = userMapper.getUserByPhone(phone);
        if (user != null) {
            if (!user.isIsactive()) {
                success = false;
                errorMsg = "此用户已禁用";
            }
        } else {
            success = false;
            errorMsg = "此用户不存在";
        }
        Map map = new HashMap<>();
        map.put("success", success);
        map.put("errorMsg", errorMsg);
        return map;
    }


}
