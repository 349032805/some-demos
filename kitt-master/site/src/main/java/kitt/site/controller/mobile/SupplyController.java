package kitt.site.controller.mobile;

import kitt.core.domain.*;
import kitt.core.domain.SellInfo.OrderType;
import kitt.core.persistence.DataBookMapper;
import kitt.core.persistence.MyInterestMapper;
import kitt.core.persistence.UserMapper;
import kitt.core.service.ConfigConsts;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.BeanValidators;
import kitt.site.service.FileService;
import kitt.site.service.mobile.CompanyService;
import kitt.site.service.mobile.DemandService;
import kitt.site.service.mobile.SupplyService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 15-5-19.
 */
@Controller
@RequestMapping("/m")
public class SupplyController extends JsonController {
    @Autowired
    private SupplyService supplyService;
    @Autowired
    private DemandService demandService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private MyInterestMapper myInterestMapper;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    protected FileService fileService;

    @RequestMapping(value = "/supply/{id}", method = RequestMethod.GET)
    public String loadSellInfo(@PathVariable("id") int id, Model model) {
        model.addAttribute("supply", supplyService.loadSellDeatil(id));
        if (session.getUser() != null) {
            MyInterest interest = myInterestMapper.getMyInterestBySid(id, session.getUser().getId(), "supply");
            if (null != interest && !interest.isIsdelete()) {
                model.addAttribute("isWatch", true);
            }
        }
        return "/m/supply/supplyDetail";
    }

    /**
     * 商城列表
     *
     * @param param
     * @param orderType
     * @param provinceId
     * @param portId
     * @param lowNCV
     * @param highNCV
     * @param lowRS
     * @param highRS
     * @param coalType
     * @param model
     * @return
     */
    @RequestMapping(value = "/mall", method = RequestMethod.GET)
    public String mallSupply(PageQueryParam param,
                             @RequestParam(value = "orderType", defaultValue = "lastupdatetimeDesc") OrderType orderType,
                             @RequestParam(value = "provinceId", required = false) Integer provinceId,
                             @RequestParam(value = "portId", required = false) Integer portId,
                             @RequestParam(value = "lowNCV", required = false) Integer lowNCV,
                             @RequestParam(value = "highNCV", required = false) Integer highNCV,
                             @RequestParam(value = "lowRS", required = false) BigDecimal lowRS,
                             @RequestParam(value = "highRS", required = false) BigDecimal highRS,
                             @RequestParam(value = "coalType", required = false) String coalType,
                             @RequestParam(value = "scrtop", required = false) String anchor, Model model) {
        final boolean type = true;
        this.showSupplyList(param, orderType, provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType, type, anchor, model);
        model.addAttribute("isMall", type);
        return "/m/supply/mall";
    }

    //现货搜索
    @RequestMapping(value = "/supply", method = RequestMethod.GET)
    public String supplySearch(PageQueryParam param,
                               @RequestParam(value = "orderType", defaultValue = "lastupdatetimeDesc") OrderType orderType,
                               @RequestParam(value = "provinceId", required = false) Integer provinceId,
                               @RequestParam(value = "portId", required = false) Integer portId,
                               @RequestParam(value = "lowNCV", required = false) Integer lowNCV,
                               @RequestParam(value = "highNCV", required = false) Integer highNCV,
                               @RequestParam(value = "lowRS", required = false) BigDecimal lowRS,
                               @RequestParam(value = "highRS", required = false) BigDecimal highRS,
                               @RequestParam(value = "coalType", required = false) String coalType,
                               @RequestParam(value = "scrtop", required = false) String anchor, Model model) {
        //type为false
        final boolean type = false;
        this.showSupplyList(param, orderType, provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType, type, anchor, model);
        model.addAttribute("isMall", type);
        return "/m/supply/mall";
    }

    //商城产品加载更多
    @RequestMapping(value = "/mall/list", method = RequestMethod.GET)
    public String mallSupplyPage(PageQueryParam param,
                                 @RequestParam(value = "orderType", defaultValue = "lastupdatetimeDesc") OrderType orderType,
                                 @RequestParam(value = "provinceId", required = false) Integer provinceId,
                                 @RequestParam(value = "portId", required = false) Integer portId,
                                 @RequestParam(value = "lowNCV", required = false) Integer lowNCV,
                                 @RequestParam(value = "highNCV", required = false) Integer highNCV,
                                 @RequestParam(value = "lowRS", required = false) BigDecimal lowRS,
                                 @RequestParam(value = "highRS", required = false) BigDecimal highRS,
                                 @RequestParam(value = "coalType", required = false) String coalType, Model model) {
        final boolean type = true;
        this.showSupplyList(param, orderType, provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType, type, null, model);
        model.addAttribute("isMall", type);
        return "/m/supply/mallList";
    }

    //现货搜索产品加载更多
    @RequestMapping(value = "/supply/list", method = RequestMethod.GET)
    public String supplyPage(PageQueryParam param,
                             @RequestParam(value = "orderType", defaultValue = "lastupdatetimeDesc") OrderType orderType,
                             @RequestParam(value = "provinceId", required = false) Integer provinceId,
                             @RequestParam(value = "portId", required = false) Integer portId,
                             @RequestParam(value = "lowNCV", required = false) Integer lowNCV,
                             @RequestParam(value = "highNCV", required = false) Integer highNCV,
                             @RequestParam(value = "lowRS", required = false) BigDecimal lowRS,
                             @RequestParam(value = "highRS", required = false) BigDecimal highRS,
                             @RequestParam(value = "coalType", required = false) String coalType, Model model) {
        final boolean type = false;
        this.showSupplyList(param, orderType, provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType, type, null, model);
        model.addAttribute("isMall", type);
        return "/m/supply/mallList";
    }

    private void showSupplyList(PageQueryParam param,
                                OrderType orderType,
                                Integer provinceId,
                                Integer portId,
                                Integer lowNCV,
                                Integer highNCV,
                                BigDecimal lowRS,
                                BigDecimal highRS,
                                String coalType,
                                boolean type,
                                String anchor,
                                Model model) {
        PageQueryParam pageQueryParam = supplyService.loadMallData(param, orderType, type, provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType, anchor);

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
        model.addAttribute("mallSupply", pageQueryParam);
        model.addAttribute("orderType", orderType);
    }


    //关注供应
    @RequestMapping(value = "supply/watchSupply/{supplyId}", method = RequestMethod.GET)
    @LoginRequired
    public
    @ResponseBody
    Object watchSupply(@PathVariable("supplyId") int supplyId, @CurrentUser User user, BindResult bindResult) {
        final String type = "supply";
        //逻辑一样，调用demandService方法
        supplyService.addMyWatch(supplyId, user.getId(), type);
        return json(bindResult);
    }

    //打开发布供应页面
    @RequestMapping(value = "/releaseSupply", method = RequestMethod.GET)
    @LoginRequired
    public String releaseSupply(Model model) {
        storeModelBaseData(model);
        model.addAttribute("flag", "save");
        return "/m/supply/supplyRelease";
    }

    //发布供应
    @RequestMapping(value = "/releaseSupply", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object saveSupply(SellInfo sellInfo, @CurrentUser User user, BindResult result) throws Exception{
        //进行验证，如果有错误抛异常
        BeanValidators.validateWithException(sellInfo);
        Company company = companyService.loadByUserId(user.getId());
        user = userMapper.getUserById(user.getId());
        if (company == null) {
            result.addError(ConfigConsts.companyWaitComplete, "您的公司信息不完整,请完善!");
        } else if (company.getVerifystatus().equals("审核未通过")|| user.getVerifystatus().equals("审核未通过")) {
            result.addError(ConfigConsts.companynoPass, "您的公司信息审核未通过!");
        } else if (!company.getVerifystatus().equals("审核通过")|| !user.getVerifystatus().equals("审核通过")) {
            result.addError(ConfigConsts.companyChecking, "您的公司信息正在审核中,请您耐心等待!");
        } else {
            supplyService.saveSupply(sellInfo, user);
            result.addAttribute("supplyId", sellInfo.getId());
        }
        return json(result);
    }

    //保存化验报告
    @RequestMapping(value = "/saveChemicalExamPic", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object saveChemicalExamPic(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        BigDecimal filesize = new BigDecimal(file.getSize());
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if(returnValue <= 10) {
            response.setContentType("text/html");
            String picSavePath = fileService.uploadPicture(file);
            if (StringUtils.isEmpty(picSavePath))
                throw new NotFoundException();
            map.put("picSavePath", picSavePath);
        }else {
            map.put("error","上传图片过大,请选择小于10M图片上传");
        }
        return map;
    }

    //确认发布供应
    @RequestMapping(value = "/confirmSupply/{supplyId}", method = RequestMethod.GET)
    @LoginRequired
    public
    @ResponseBody
    Object confirmSupply(@PathVariable("supplyId") int supplyId, @CurrentUser User user, BindResult result) {
        supplyService.confirmSupply(supplyId, user.getId());
        return json(result);
    }

    //打开修改供应页面
    @RequestMapping(value = "/updateSupply/{supplyId}", method = RequestMethod.GET)
    @LoginRequired
    public String updateSupply(@PathVariable("supplyId") int supplyId, @CurrentUser User user, Model model) {
        SellInfo supply = supplyService.loadSellDeatil(supplyId, user.getId());
        model.addAttribute("supply", supply);
        model.addAttribute("flag", "update");
        model.addAttribute("provinceList", supplyService.loadProvince());
        model.addAttribute("portList", supplyService.loadAreaById(supply.getProvinceId()));
        model.addAttribute("coalTypeList", demandService.loadCoalTypes());
        model.addAttribute("deliverymodeList", dataBookMapper.getDataBookListByType("deliverymode"));
        model.addAttribute("inspectionagencyList", supplyService.loadInspectionagencys());
        model.addAttribute("pslist", dataBookMapper.getDataBookListByType("pstype"));
        return "/m/supply/supplyRelease";
    }

    //修改供应
    @RequestMapping(value = "/updateSupply", method = RequestMethod.POST)
    @LoginRequired
    public
    @ResponseBody
    Object updateSupply(SellInfo sellInfo, @CurrentUser User user, BindResult result) throws Exception{
        //进行验证，如果有错误抛异常
        BeanValidators.validateWithException(sellInfo);
        Company company = companyService.loadByUserId(user.getId());
        user = userMapper.getUserById(user.getId());
        if (company == null) {
            result.addError(ConfigConsts.companyWaitComplete, "您的公司信息不完整,请完善!");
        } else if (company.getVerifystatus().equals("审核未通过") || user.getVerifystatus().equals("审核未通过")) {
            result.addError(ConfigConsts.companynoPass, "您的公司信息审核未通过!");
        } else if (!company.getVerifystatus().equals("审核通过")|| !user.getVerifystatus().equals("审核通过")) {
            result.addError(ConfigConsts.companyChecking, "您的公司信息正在审核中,请您耐心等待!");
        } else {
            supplyService.updateSupply(sellInfo);
            result.addAttribute("supplyId", sellInfo.getId());
        }
        return json(result);
    }

    //供应确认页面
    @RequestMapping(value = "/supplyCheck/{supplyId}", method = RequestMethod.GET)
    @LoginRequired
    public String supplyCheck(@PathVariable("supplyId") int supplyId, @CurrentUser User user, Model model) {
        model.addAttribute("supply", supplyService.loadSellDeatil(supplyId, user.getId()));
        return "/m/supply/supplyConfirm";
    }


    //存放模型基础数据
    private void storeModelBaseData(Model model) {
        List<Areaport> provinces = supplyService.loadProvince();
        List<Areaport> ports = supplyService.loadAreaById(provinces.get(0).getId());
        model.addAttribute("provinceList", provinces);
        model.addAttribute("portList", ports);
        model.addAttribute("coalTypeList", demandService.loadCoalTypes());
        model.addAttribute("deliverymodeList", dataBookMapper.getDataBookListByType("deliverymode"));
        model.addAttribute("inspectionagencyList", supplyService.loadInspectionagencys());
        model.addAttribute("pslist", dataBookMapper.getDataBookListByType("pstype"));
    }

    // 发布供应成功页面
    @RequestMapping(value = "/supply/releaseSuccess", method = RequestMethod.GET)
    public String releaseSuccess() {
        return "/m/releaseSuccessSupply";
    }

}
