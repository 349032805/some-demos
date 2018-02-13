package kitt.site.controller.mobile;

import kitt.core.domain.*;
import kitt.core.persistence.DataBookMapper;
import kitt.core.persistence.MyInterestMapper;
import kitt.core.persistence.UserMapper;
import kitt.core.service.ConfigConsts;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.BeanValidators;
import kitt.site.service.mobile.CompanyService;
import kitt.site.service.mobile.DemandService;
import kitt.site.service.mobile.QuoteService;
import kitt.site.service.mobile.SupplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by xiangyang on 15-5-19.
 */
@Controller("mobileDemandController")
@RequestMapping("/m")
public class DemandController extends JsonController {

    @Autowired
    private DemandService demandService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private SupplyService supplyService;
    @Autowired
    private MyInterestMapper myInterestMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DataBookMapper dataBookMapper;

    //需求列表展示
    @RequestMapping("/sell")
    public String demandList(PageQueryParam param,
                             @RequestParam(value = "provinceId", required = false) Integer provinceId,
                             @RequestParam(value = "portId", required = false) Integer portId,
                             @RequestParam(value = "lowNCV", required = false) Integer lowNCV,
                             @RequestParam(value = "highNCV", required = false) Integer highNCV,
                             @RequestParam(value = "lowRS", required = false) BigDecimal lowRS,
                             @RequestParam(value = "highRS", required = false) BigDecimal highRS,
                             @RequestParam(value = "coalType", required = false) String coalType,
                             @RequestParam(value = "scrtop", required = false) String anchor, Model model) {
        showDemand(param, provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType, anchor, model);
        return "/m/offerArea/sell";
    }

    //需求加载更多
    @RequestMapping("/sell/list")
    public String demandListMore(PageQueryParam param,
                                 @RequestParam(value = "provinceId", required = false) Integer provinceId,
                                 @RequestParam(value = "portId", required = false) Integer portId,
                                 @RequestParam(value = "lowNCV", required = false) Integer lowNCV,
                                 @RequestParam(value = "highNCV", required = false) Integer highNCV,
                                 @RequestParam(value = "lowRS", required = false) BigDecimal lowRS,
                                 @RequestParam(value = "highRS", required = false) BigDecimal highRS,
                                 @RequestParam(value = "coalType", required = false) String coalType, Model model) {
        showDemand(param, provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType, null, model);
        return "/m/offerArea/sellList";
    }

    public void showDemand(PageQueryParam param,
                           Integer provinceId,
                           Integer portId,
                           Integer lowNCV,
                           Integer highNCV,
                           BigDecimal lowRS,
                           BigDecimal highRS,
                           String coalType,
                           String anchor, Model model) {
        PageQueryParam pageQueryParam = demandService.loadDemandData(param, provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType, anchor);

        if (lowNCV != null && highNCV != null) {
            model.addAttribute("NCV", lowNCV + "-" + highNCV);
            model.addAttribute("lowNCV", lowNCV);
            model.addAttribute("highNCV", highNCV);
        }
        if (lowRS != null && highRS != null) {
            model.addAttribute("RS", String.valueOf(lowRS) + "%" + "-" + String.valueOf(highRS) + "%");
            model.addAttribute("lowRS", lowRS);
            model.addAttribute("highRS", highRS);
        }
        if (provinceId != null) {
            model.addAttribute("provinceName", supplyService.loadAreaNameById(provinceId));
            model.addAttribute("provinceId", provinceId);
        }
        if (portId != null) {
            model.addAttribute("portName", supplyService.loadAreaNameById(portId));
            model.addAttribute("portId", portId);
        }
        if (StringUtils.isNotBlank(coalType)) {
            model.addAttribute("coalType", coalType);
        }
        model.addAttribute("demandData", pageQueryParam);
    }


    //个人中心我的报价需求详情
    @RequestMapping(value = {"/demand/{status}/{id}"}, method = RequestMethod.GET)
    @LoginRequired
    public String loadMyDemandInfo(@PathVariable("id") int id, @PathVariable("status") String status, @CurrentUser User user, Model model) {
        Demand demand = demandService.loadDemandDeatil(id);
        model.addAttribute("demand", demand);
        if (!status.equals("demand")) {
            Quote quote = demandService.getQuote(user.getId(), id);
            model.addAttribute("quote", quote);
        }

        LocalDate now = LocalDate.now();
        if (demand.getQuoteenddate().isBefore(now)) {
            model.addAttribute("canquote", "false");
        } else {
            model.addAttribute("canquote", "true");
        }
        model.addAttribute("status", status);
        return "/m/showDemandInfo";
    }

    //打开到需求详情页 -- 报价区
    @RequestMapping(value = {"/demand/{id}"}, method = RequestMethod.GET)
    public String loadDemandInfo(@PathVariable("id") int id, Model model) {
        storeModelBaseData(model);
        Demand demand = demandService.loadDemandDeatil(id);
        model.addAttribute("demand", demand);
        if (session.getUser() != null) {
            MyInterest interest = myInterestMapper.getMyInterestBySid(id, session.getUser().getId(), "demand");
            if (null != interest && !interest.isIsdelete()) {
                model.addAttribute("isWatch", true);
            }
        }
        return "/m/showDemandInfo";
    }

    @RequestMapping(value = {"/demand/{status}/{id}/{flag}"}, method = RequestMethod.GET)
    @LoginRequired
    public String loadNessaryQuoteInfo(@PathVariable("id") int id, @PathVariable("status") String status,@PathVariable("flag") String flag, @CurrentUser User user, Model model) {
        Demand demand = demandService.loadDemandDeatil(id);
        model.addAttribute("demand", demand);
        if (!status.equals("demand")) {
            Quote quote = demandService.getQuote(user.getId(), id);
            model.addAttribute("quote", quote);
        }

        LocalDate now = LocalDate.now();
        if (demand.getQuoteenddate().isBefore(now)) {
            model.addAttribute("canquote", "false");
        } else {
            model.addAttribute("canquote", "true");
        }
        model.addAttribute("status", status);
        model.addAttribute("flag",flag);
        return "/m/showDemandInfo";
    }


    //从需求详情页面跳转到报价页面
    @RequestMapping(value = {"/gotoQuote/{status}/{demandId}"}, method = RequestMethod.GET)
    public String gotoQuote(@PathVariable("demandId") int demandId,@PathVariable("status") String status,@CurrentUser User user, Model model) {
        storeModelBaseData(model);
        Demand demand = demandService.loadDemandDeatil(demandId);
        model.addAttribute("demand", demand);
        if (!status.equals("demand")) {
            Quote quote = demandService.getQuote(user.getId(), demandId);
            model.addAttribute("quote", quote);
        }
        model.addAttribute("demandId",demandId);
        model.addAttribute("status",status);
        return "/m/demandDetail";
    }


    @RequestMapping(value = {"/gotoQuote/{demandId}"}, method = RequestMethod.GET)
    public String jumpToQuotePage(@PathVariable("demandId") int demandId, Model model) {
        storeModelBaseData(model);
        Demand demand = demandService.loadDemandDeatil(demandId);
        model.addAttribute("demand",demand);
        return "/m/demandDetail";
    }


    //对需求进行报价
    @RequestMapping(value = "/demand/demandQuote", method = RequestMethod.POST)
    @LoginRequired
    public @ResponseBody Object demandQuote(Quote quote,  @CurrentUser User user, BindResult bindResult) {
        Demand demand = demandService.loadDemandDeatil(quote.getDemandid());
        if (demand == null) {
          throw new NotFoundException();
        }
        if (demand.getTradestatus().equals("开始报价") && demand.isIsdelete() != true) {
            Company company = companyService.loadByUserId(user.getId());
            user = userMapper.getUserById(user.getId());
            // 检查公司信息
            if (company == null) {
                bindResult.addError(ConfigConsts.companyWaitComplete, "您的公司信息不完整,请完善!");
            } else if (company.getVerifystatus().equals("审核未通过") || user.getVerifystatus().equals("审核未通过")) {
                bindResult.addError(ConfigConsts.companynoPass, "您的公司信息审核未通过!");
            } else if (!company.getVerifystatus().equals("审核通过")|| !user.getVerifystatus().equals("审核通过")) {
                bindResult.addError(ConfigConsts.companyChecking, "您的公司信息正在审核中,请您耐心等待!");
            } else if (!quoteService.checkSelfQuote(user.getId(), demand.getDemandcode())) {
                bindResult.addError("QuoteError", "不能对自己发布的需求进行报价!");
            } else {
                demandService.demandQuote(user, demand, company, quote);
            }
        } else {
            throw new BusinessException("该需求已取消!");
        }
        return json(bindResult);
    }

    //对需求进行报价
    @RequestMapping(value = "/quote/checkSelfQuote", method = RequestMethod.POST)
    @LoginRequired
    public @ResponseBody Object checkSelfQuote(@RequestParam("demandId") int demandId,  @CurrentUser User user, BindResult bindResult) {
        Demand demand = demandService.loadDemandDeatil(demandId);
        if (!quoteService.checkSelfQuote(user.getId(), demand.getDemandcode()))
            bindResult.addError("QuoteError", "不能对自己发布的需求进行报价!");
        return json(bindResult);
    }


    //关注需求
    @RequestMapping(value = "/demand/watchDemand/{demandId}", method = RequestMethod.GET)
    @LoginRequired
    public @ResponseBody Object watchDemand(@PathVariable("demandId") int demandId, @CurrentUser User user, BindResult bindResult) {
        final String type = "demand";
        final String status = "开始报价";
        Demand demand = demandService.loadDemandDeatil(demandId);
        if (demand != null && status.equals(demand.getCheckstatus())) {
            bindResult.addError("status", "该需求已经" +demand.getCheckstatus()+",不能关注!");
        }else {
            demandService.addMyWatch(demandId, user.getId(), type);
        }
        return json(bindResult);
    }

    //打开发布需求页面
    @RequestMapping(value = "/releaseDemand", method = RequestMethod.GET)
    @LoginRequired
    public String releaseDemand(Model model) {
        storeModelBaseData(model);
        return "/m/releaseDemand";
    }

    //发布需求
    @RequestMapping(value = "/demand/releaseDemand", method = RequestMethod.POST)
    @LoginRequired
    public
    @ResponseBody
    Object saveDemand(Demand demand, @CurrentUser User user, BindResult result) {
        BeanValidators.validateWithException(demand);
        Company company = companyService.loadByUserId(user.getId());
        user = userMapper.getUserById(user.getId());
        // 检查公司信息
        if (company == null) {
            result.addError(ConfigConsts.companyWaitComplete, "您的公司信息不完整,请完善!");
        } else if (company.getVerifystatus().equals("审核未通过")|| user.getVerifystatus().equals("审核未通过")) {
            result.addError(ConfigConsts.companynoPass, "您的公司信息审核未通过!");
        } else if (!company.getVerifystatus().equals("审核通过") || !user.getVerifystatus().equals("审核通过")) {
            result.addError(ConfigConsts.companyChecking, "您的公司信息正在审核中,请您耐心等待!");
        } else {
            demandService.addDemand(demand);
            result.addAttribute("demand", demand);
        }
        return json(result);
    }

    //跳转到需求确认页面
    @RequestMapping(value = "/demandCheck/{demandId}", method = RequestMethod.GET)
    @LoginRequired
    public String checkDemand(@PathVariable("demandId") int demandId, @CurrentUser User user, Model model) {
        model.addAttribute("demand", demandService.loadDemandDetail(demandId, user.getId()));
        return "/m/checkDemandInfo";
    }

    //确认发布
    @RequestMapping(value = "/confirmReleaseDemand/{demandCode}", method = RequestMethod.GET)
    @LoginRequired
    public
    @ResponseBody
    Object configDemand(@PathVariable("demandCode") String demandCode, @CurrentUser User user, BindResult result) {
        Demand demand = demandService.loadDemandDetailForDemandCode(demandCode, user.getId());
        demandService.saveMyDemand(demand);
        return json(result);
    }


    //打开修改需求页面
    @RequestMapping(value = "/updateDemand/{demandCode}", method = RequestMethod.GET)
    @LoginRequired
    public String updateDemand(HttpServletRequest request, @PathVariable("demandCode") String demandCode, @CurrentUser User user, Model model) {
        Demand demand = demandService.loadDemandDetailForDemandCode(demandCode, user.getId());
        //判断请求来自个人中心还是来自发布需求，如果是个人中心的链接，防止出现由于状态不一致，造成的丢失更新
        if (request.getHeader("Referer") != null && request.getHeader("Referer").contains("/m/account/") && demandService.loadMyDemand(demandCode) == null && demand.getCheckstatus().equals("审核未通过")) {
            return "redirect:/m/account/MyDemand/getMyDemand";
        } else {
            model.addAttribute("demand", demand);
            //初始化模型数据
            List<Areaport> provinces = supplyService.loadProvince();
            List<Areaport> ports = supplyService.loadAreaById(demand.getProvinceId());
            model.addAttribute("provinces", provinces);
            model.addAttribute("ports", ports);
            model.addAttribute("coalTypes", demandService.loadCoalTypes());
            model.addAttribute("deliveryModes", dataBookMapper.getDataBookListByType("deliverymode"));
            model.addAttribute("pslist", dataBookMapper.getDataBookListByType("pstype"));
            model.addAttribute("inspectionAgencys", supplyService.loadInspectionagencys());
            model.addAttribute("transportmode", dataBookMapper.getDataBookListByType("transportmode"));
            model.addAttribute("paymode", dataBookMapper.getDataBookListByType("demandpaymode"));
        }
        return "/m/releaseDemand";
    }


    //修改需求
    @RequestMapping(value = "/updateDemand", method = RequestMethod.POST)
    @LoginRequired
    public
    @ResponseBody
    Object updateDemand(Demand demand, @CurrentUser User user, BindResult result) {
        BeanValidators.validateWithException(demand);
        Demand oldDemand = demandService.loadDemandDetailForDemandCode(demand.getDemandcode(), user.getId());
        //判断状态，如果后台管理员设置为审核未通过，重新发布,新增一条数据
        if (oldDemand.getCheckstatus().equals("审核未通过")) {
            //删除mydemand数据
            demandService.deleteMydemand(user, demand.getDemandcode());
            demandService.addDemand(demand);
        } else {
            demandService.updateDemand(demand);
        }
        result.addAttribute("demand", demand);
        return json(result);
    }


    @RequestMapping(value = "/releaseSuccess", method = RequestMethod.GET)
    public String releaseSuccess() {
        return "/m/releaseSuccess";
    }


    //存放模型基础数据
    private void storeModelBaseData(Model model) {
        List<Areaport> provinces = supplyService.loadProvince();
        List<Areaport> ports = supplyService.loadAreaById(provinces.get(0).getId());
        model.addAttribute("provinces", provinces);
        model.addAttribute("ports", ports);
        model.addAttribute("coalTypes", demandService.loadCoalTypes());
        model.addAttribute("deliveryModes", dataBookMapper.getDataBookListByType("deliverymode"));
        model.addAttribute("pslist", dataBookMapper.getDataBookListByType("pstype"));
        model.addAttribute("inspectionAgencys", supplyService.loadInspectionagencys());
        model.addAttribute("transportmode", dataBookMapper.getDataBookListByType("transportmode"));
        model.addAttribute("paymode", dataBookMapper.getDataBookListByType("demandpaymode"));

    }

}
