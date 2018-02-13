package kitt.site.controller;

import com.mysql.jdbc.StringUtils;
import freemarker.template.TemplateException;
import kitt.core.bl.BuyService;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.FileStore;
import kitt.core.service.MessageNotice;
import kitt.core.util.PageQueryParam;
import kitt.core.util.ToolsMethod;
import kitt.ext.mybatis.Where;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 14/11/25.
 */
@Controller
public class BuyController extends JsonController {
    @Autowired
    private Session session;
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private BuySearchMapper buySearchMapper;
    @Autowired
    private BuyService buyService;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private DemandMapper demandMapper;
    @Autowired
    private MyInterestMapper myInterestMapper;
    @Autowired
    private KittHandlerExceptionResolver kittHandlerExceptionResolver;
    @Autowired
    private Auth auth;
    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    protected FileService fileService;
    @Autowired
    protected FileStore fileStore;
    @Autowired
    protected PriceLadderMapper priceLadderMapper;
    @Autowired
    private CheckController checkController;
    @Autowired
    private BuyMethod buyMethod;
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private ReservationMapper reservationMapper;
    @Autowired
    private ToolsMethod toolsMethod;

    /**
     * 现货搜索产品详细
     * @param id            sellinfo id
     * @param type          类型,前台标识使用
     */
    @RequestMapping("/buy/info")
    public String getBuyDetailInfo(Integer id, String type, Map<String, Object> model, HttpServletRequest request) throws ServletException, TemplateException, IOException {
        showProductDetail(id, type, model, request);
        return "fixedInfo";
    }

    /**
     * 商城产品详细
     * @param id            sellinfo id
     * @param type          类型,前台标识使用
     */
    @RequestMapping("/mall/info")
    public String getMallDetailInfo(Integer id, String type, Map<String, Object> model, HttpServletRequest request) throws ServletException, TemplateException, IOException {
        showProductDetail(id, type, model, request);
        return "fixedInfo";
    }

    public void showProductDetail(Integer id, String type, Map<String, Object> model, HttpServletRequest request) throws ServletException, TemplateException, IOException {
        if(id == null) throw new NotFoundException("该产品不存在，或者已经下架！");
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if (sellInfo == null || !sellInfo.getStatus().equals(EnumSellInfo.VerifyPass)) throw new NotFoundException("该产品不存在，或者已经下架！");
        getSellInfoDetail(sellInfo, model);
        model.put("contract", sellInfo.getType() == 0 ? buyMethod.standardContractContent(sellInfo, request) : "");
        if(!StringUtils.isNullOrEmpty(type)){
            model.put("type",type);
        }
    }

    public void getSellInfoDetail(SellInfo sellInfo, Map<String, Object> model) {
        buyMapper.setPageViewTimesById(sellInfo.getId());
        buyMethod.showJTJ(sellInfo.getId(), model);
        model.put("sellInfo", buyMapper.getSellInfoById(sellInfo.getId()));
        model.put("interest", (session != null && session.getUser() != null && myInterestMapper.getMyInterestBySid(sellInfo.getId(), session.getUser().getId(), "supply") != null && !myInterestMapper.getMyInterestBySid(sellInfo.getId(), session.getUser().getId(), "supply").isIsdelete()));
        model.put("similarList", buySearchMapper.getSimilarSellInfoList(sellInfo));          //同类产品
        model.put("latestList", buySearchMapper.getLatestSellInfoList(sellInfo.getId()));                    //最新产品
        model.put("hotestList", buySearchMapper.getHotestSellInfoList(sellInfo.getId()));                    //热销产品
    }

    /**
     * 添加我的关注
     * @param sid   产品id 或者 需求id 或者 店铺 id
     * @param type  类型， 产品 还是 需求
     */
    @RequestMapping("/buy/addInterest")
    @ResponseBody
    @LoginRequired
    public boolean addMyInterest(@RequestParam("id") int sid,
                                 @RequestParam("type") String type) {
        SellInfo sellInfo = null;
        Demand demand = null;
        Shop shop = null;
        TenderDeclaration tenderDeclaration = null;
        if (type.equals("supply")) {
            sellInfo = buyMapper.getSellInfoById(sid);
        } else if (type.equals("demand")) {
            demand = demandMapper.getDemandById(sid);
        } else if(type.equals("shop")){
            shop = shopMapper.getShopById(sid);
        } else if(type.equals("tender")){
            tenderDeclaration = tenderdeclarMapper.findTendDeclarById(sid);
        }
        if (sellInfo == null && demand == null && shop == null && tenderDeclaration == null) throw new NotFoundException();
        String NCV = null;
        if (myInterestMapper.getMyInterestBySid(sid, session.getUser().getId(), type) == null) {
            MyInterest myInterest = null;
            if (type.equals("supply")) {
                if(sellInfo.getNCV() != 0 && sellInfo.getNCV02() != 0){
                    if(sellInfo.getNCV() == sellInfo.getNCV02()){
                        NCV = sellInfo.getNCV().toString();
                    } else {
                        NCV = sellInfo.getNCV() + "-" + sellInfo.getNCV02();
                    }
                }
                BigDecimal price = sellInfo.getYkj().doubleValue() == 0 ? sellInfo.getJtjlast() : sellInfo.getYkj();
                myInterest = new MyInterest(sellInfo.getPid(), sid, sellInfo.getPname(), sellInfo.getSeller(), price, sellInfo.getSupplyquantity(), NCV, session.getUser().getId(), type, 1);
            } else if (type.equals("demand")) {
                if(demand.getNCV() != 0 && demand.getNCV02() != 0){
                    if(demand.getNCV() == demand.getNCV02()){
                        NCV = demand.getNCV().toString();
                    } else {
                        NCV = demand.getNCV() + "-" + demand.getNCV02();
                    }
                }
                myInterest = new MyInterest(demand.getDemandcode(), sid, demand.getCoaltype(), demand.getDemandcustomer(), BigDecimal.valueOf(0), demand.getDemandamount(), NCV, session.getUser().getId(), type, 1);
            } else if (type.equals("shop")){
                myInterest = new MyInterest(shop.getShopid(), sid, null, shop.getName(), null, null, null, session.getUser().getId(), type, 1);
            } else if (type.equals("tender")){
                myInterest = new MyInterest(tenderDeclaration.getTendercode(), sid, null, tenderDeclaration.getCompanyName(), null, null, null, session.getUser().getId(), type, 1);
            }
            if(myInterest != null) {
                if(!buyService.addMyInterest(myInterest)){
                    auth.doOutputErrorInfo("关注出错! 类型: " + type + ", sid=" + sid);
                    throw new BusinessException("关注失败,请刷新页面重试!");
                }
            }
        } else {
            if(!buyService.updateMyInterest(sid, session.getUser().getId(), type)){
                auth.doOutputErrorInfo("关注出错! 类型: " + type + ", sid=" + sid);
                throw new BusinessException("关注失败,请刷新页面重试!");
            }
        }
        return true;
    }

    /**
     * 已经发布供应信息修改，增加一条新的待审核的供应信息，原供应信息下架
     */
    @RequestMapping(value = "/editSellinfo", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    @Transactional
    public Object editSellInfo (
            @RequestParam(value = "id", required = true, defaultValue = "0") int sid,
            @RequestParam(value = "ykj", required = false, defaultValue = "0") BigDecimal ykj,
            @RequestParam(value = "jtj01", required = false, defaultValue = "0") BigDecimal jtj01,
            @RequestParam(value = "amount12", required = false, defaultValue = "0") int amount12,
            @RequestParam(value = "jtj02", required = false, defaultValue = "0") BigDecimal jtj02,
            @RequestParam(value = "amount21", required = false, defaultValue = "0") int amount21,
            @RequestParam(value = "amount22", required = false, defaultValue = "0") int amount22,
            @RequestParam(value = "jtj03", required = false, defaultValue = "0") BigDecimal jtj03,
            @RequestParam(value = "amount31", required = false, defaultValue = "0") int amount31,
            @RequestParam(value = "amount32", required = false, defaultValue = "0") int amount32,
            @RequestParam(value = "jtj04", required = false, defaultValue = "0") BigDecimal jtj04,
            @RequestParam(value = "amount41", required = false, defaultValue = "0") int amount41,
            @RequestParam(value = "amount42", required = false, defaultValue = "0") int amount42,
            @RequestParam(value = "jtj05", required = false, defaultValue = "0") BigDecimal jtj05,
            @RequestParam(value = "amount51", required = false, defaultValue = "0") int amount51,
            @RequestParam(value = "amount52", required = false, defaultValue = "0") int amount52,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "deliverytime1", required = true) LocalDate deliverytime1,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "deliverytime2", required = true) LocalDate deliverytime2,
            @RequestParam(value = "paymode", required = true) int paymode,
            @RequestParam(value = "supplyquantity", required = true) int supplyquantity,
            @RequestParam(value = "payperiod", required = false) BigDecimal payperiod,
            @RequestParam(value = "releaseremarks", required = false) String releaseremarks,
            @RequestParam(value = "linktype", required = true)boolean linktype,
            @RequestParam(value = "linkmanname", required = false, defaultValue = "")String linkmanname,
            @RequestParam(value = "linkmanphone", required = false, defaultValue = "")String linkmanphone,
            @RequestParam(value = "version", required = false, defaultValue = "0")int version,
            @RequestParam(value = "brandname", required = false, defaultValue = "") String brandname,
            @RequestParam(value = "tax", required = false, defaultValue = "2")int tax,
            @RequestParam(value = "accountsmethod", required = false, defaultValue = "0")int accountsmethod,
            @RequestParam(value = "logistics", required = false, defaultValue = "0")int logistics,
            @RequestParam(value = "finance", required = false, defaultValue = "0")int finance,
            @RequestParam(value = "promotion", required = false, defaultValue = "0")int promotion,
            @RequestParam(value = "promotionremarks", required = false, defaultValue = "")String promotionremarks,
            @RequestParam(value = "chemicalexam1", required = false, defaultValue = "")String chemicalexam1,
            @RequestParam(value = "chemicalexam2", required = false, defaultValue = "")String chemicalexam2,
            @RequestParam(value = "chemicalexam3", required = false, defaultValue = "")String chemicalexam3,
            @RequestParam(value = "coalpic1", required = false, defaultValue = "")String coalpic1,
            @RequestParam(value = "coalpic2", required = false, defaultValue = "")String coalpic2,
            @RequestParam(value = "coalpic3", required = false, defaultValue = "")String coalpic3,
            @RequestParam(value = "coalpic4", required = false, defaultValue = "")String coalpic4,
            @RequestParam(value = "coalpic5", required = false, defaultValue = "")String coalpic5,
            @RequestParam(value = "coalpic6", required = false, defaultValue = "")String coalpic6) throws Exception{
        checkController.doCheckSessionUser();
        SellInfo sellInfoToAdd = buyMapper.getSellInfoById(sid);
        if(sellInfoToAdd == null) throw new NotFoundException();
        int id = sid;
        boolean success = false;
        Map<String, Object> map = new HashMap<String, Object>();
        List<PriceLadder> priceLadderList = new ArrayList<PriceLadder>();
        BigDecimal[] priceList = new BigDecimal[]{jtj01, jtj02, jtj03, jtj04, jtj05};
        Integer[] amount1List = new Integer[]{0, amount21, amount31, amount41, amount51};
        Integer[] amount2List = new Integer[]{amount12, amount22, amount32, amount42, amount52};
        if(paymode != 2){//账期
            payperiod = new BigDecimal(0);
        }
        String error = "";
        if (deliverytime1.isBefore(LocalDate.now().minusDays(1))) {
            error = "before";
            map.put("id", id);
            map.put("success", false);
            map.put("error", error);
            return map;
        }
        // 供货数量<已售数量，返回错误
        int availableQuantity = supplyquantity - sellInfoToAdd.getSoldquantity() ;
        if (availableQuantity < 0) {
            error = "wrongQuantity";
            map.put("id", id);
            map.put("success", false);
            map.put("error", error);
            return map;
        }
        if(!linktype){
            linkmanname = "";
            linkmanphone = "";
        }
        int i = 0;
        BigDecimal jtjLast = jtj01;
        while(i < priceList.length && priceList[i].doubleValue() > 0){
            priceLadderList.add(new PriceLadder(i+1, priceList[i], amount1List[i], amount2List[i]));
            jtjLast = jtjLast.doubleValue() > priceList[i].doubleValue() ? priceList[i] : jtjLast;
            i++;
        }
        sellInfoToAdd.setAvailquantity(availableQuantity);
        sellInfoToAdd.setYkj(ykj);
        sellInfoToAdd.setDeliverytime1(deliverytime1);
        sellInfoToAdd.setDeliverytime2(deliverytime2);
        sellInfoToAdd.setPaymode(paymode);
        sellInfoToAdd.setSupplyquantity(supplyquantity);
        sellInfoToAdd.setPayperiod(payperiod);
        sellInfoToAdd.setReleaseremarks(releaseremarks);
        sellInfoToAdd.setLinktype(linktype);
        sellInfoToAdd.setLinkmanname(linkmanname);
        sellInfoToAdd.setLinkmanphone(linkmanphone);
        sellInfoToAdd.setVersion(version);
        sellInfoToAdd.setBrandname(brandname);
        sellInfoToAdd.setTax(tax);
        if (tax == 1) {
            sellInfoToAdd.setAccountsmethod(accountsmethod);
            sellInfoToAdd.setAccountsmethodname(dataBookMapper.getDataBookNameByTypeSequence("accountsmethod", accountsmethod));
        } else {
            sellInfoToAdd.setAccountsmethod(0);
            sellInfoToAdd.setAccountsmethodname("");
        }
        sellInfoToAdd.setLogistics(logistics);
        sellInfoToAdd.setFinance(finance);
        sellInfoToAdd.setPromotion(promotion);
        sellInfoToAdd.setPromotionremarks(promotionremarks);
        sellInfoToAdd.setChemicalexam1(null);
        sellInfoToAdd.setChemicalexam2(null);
        sellInfoToAdd.setChemicalexam3(null);
        sellInfoToAdd.setCoalpic1(null);
        sellInfoToAdd.setCoalpic2(null);
        sellInfoToAdd.setCoalpic3(null);
        sellInfoToAdd.setCoalpic4(null);
        sellInfoToAdd.setCoalpic5(null);
        sellInfoToAdd.setCoalpic6(null);
        if(!StringUtils.isNullOrEmpty(chemicalexam1)) {
            fileStore.copyFileToUploadDir(chemicalexam1);
            sellInfoToAdd.setChemicalexam1(chemicalexam1.replace("temp", "upload"));
        }
        if(!StringUtils.isNullOrEmpty(chemicalexam2)) {
            fileStore.copyFileToUploadDir(chemicalexam2);
            sellInfoToAdd.setChemicalexam2(chemicalexam2.replace("temp", "upload"));
        }
        if(!StringUtils.isNullOrEmpty(chemicalexam3)) {
            fileStore.copyFileToUploadDir(chemicalexam3);
            sellInfoToAdd.setChemicalexam3(chemicalexam3.replace("temp", "upload"));
        }
        if(!StringUtils.isNullOrEmpty(coalpic1)) {
            fileStore.copyFileToUploadDir(coalpic1);
            sellInfoToAdd.setCoalpic1(coalpic1.replace("temp", "upload"));
        }
        if(!StringUtils.isNullOrEmpty(coalpic2)) {
            fileStore.copyFileToUploadDir(coalpic2);
            sellInfoToAdd.setCoalpic2(coalpic2.replace("temp", "upload"));
        }
        if(!StringUtils.isNullOrEmpty(coalpic3)) {
            fileStore.copyFileToUploadDir(coalpic3);
            sellInfoToAdd.setCoalpic3(coalpic3.replace("temp", "upload"));
        }
        if(!StringUtils.isNullOrEmpty(coalpic4)) {
            fileStore.copyFileToUploadDir(coalpic4);
            sellInfoToAdd.setCoalpic4(coalpic4.replace("temp", "upload"));
        }
        if(!StringUtils.isNullOrEmpty(coalpic5)) {
            fileStore.copyFileToUploadDir(coalpic5);
            sellInfoToAdd.setCoalpic5(coalpic5.replace("temp", "upload"));
        }
        if(!StringUtils.isNullOrEmpty(coalpic6)) {
            fileStore.copyFileToUploadDir(coalpic6);
            sellInfoToAdd.setCoalpic6(coalpic6.replace("temp", "upload"));
        }
        // 更改审核通过的记录VerifyPass：插入新记录，editnum+1，新加阶梯价，新id
        if(ykj.doubleValue() ==0 && (jtj01.doubleValue() == 0 || jtj02.doubleValue() == 0)){
            throw new NotFoundException();
        } else if (EnumSellInfo.VerifyPass.equals(sellInfoToAdd.getStatus())) {
            if(0 == sellInfoToAdd.getParentid()){
                sellInfoToAdd.setParentid(sid);
            }
            sellInfoToAdd.setStatus(EnumSellInfo.WaitVerify);
            sellInfoToAdd.setEditnum(sellInfoToAdd.getEditnum() + 1);
            sellInfoToAdd.setClienttype(1);
            // 新id
            try {
                id = buyService.addSellinfoForUpdate(sellInfoToAdd, sid, version, priceLadderList, jtjLast, session.getUser().getId(), session.getUser().getSecurephone());
            } catch (SQLExcutionErrorException se){
                auth.doOutputErrorInfo("更改供应信息(sellinfo出错, 老供应信息id(sid)=" + sid + ", 发布人userid=" + session.getUser().getId());
                throw new BusinessException(EnumRemindInfo.Site_System_Error.value());
            }
            success = true;
        } else {
            sellInfoToAdd.setStatus(EnumSellInfo.WaitVerify);
            // 更改其他状态的记录：更新原记录，原editnum，更新阶梯价，原id
            try{
                buyService.updateSellInfo(sellInfoToAdd, priceLadderList, jtjLast, session.getUser().getId(), session.getUser().getSecurephone());
            } catch (SQLExcutionErrorException se){
                auth.doOutputErrorInfo("更改供应信息(sellinfo出错, 老供应信息id(sid)=" + sid + ", 发布人userid=" + session.getUser().getId());
                throw new BusinessException(EnumRemindInfo.Site_System_Error.value());
            }
            success = true;
            map.put("id", sellInfoToAdd.getId());
        }
        map.put("id", id);
        map.put("success", success);
        map.put("error", error);
        return map ;
    }

    //保存化验报告
    @RequestMapping(value = "/saveChemicalExamPic", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object saveChemicalExamPic(@RequestParam("file") MultipartFile file,HttpServletResponse response) throws Exception{
        BigDecimal filesize = new BigDecimal(file.getSize());
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        Map<String, Object> map = new HashMap<String, Object>();
        if(returnValue<= 10) {
            response.setContentType("text/html");
            String picSavePath = fileService.uploadPicture(file);
            if (StringUtils.isNullOrEmpty(picSavePath)) throw new NotFoundException();
            map.put("picSavePath", picSavePath);
        } else {
            map.put("error","上传图片过大,请选择小于10M图片上传");
        }
        return map;
    }

    @RequestMapping(value = "/addSellinfo", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    @Transactional
    public Object doAddSellinfo(@CurrentUser User user,
                                @RequestParam(value = "id", required = false, defaultValue = "0") int sid,
                                @RequestParam(value = "pname", required = false) String pname,
                                @RequestParam(value = "NCV", required = false, defaultValue = "0") int NCV,
                                @RequestParam(value = "NCV02", required = false, defaultValue = "0") int NCV02,
                                @RequestParam(value = "RS", required = true) BigDecimal RS,
                                @RequestParam(value = "RS02", required = true) BigDecimal RS02,
                                @RequestParam(value = "ADS", required = false, defaultValue = "0") BigDecimal ADS,
                                @RequestParam(value = "ADS02", required = false, defaultValue = "0") BigDecimal ADS02,
                                @RequestParam(value = "TM", required = true) BigDecimal TM,
                                @RequestParam(value = "TM02", required = true) BigDecimal TM02,
                                @RequestParam(value = "IM", required = false, defaultValue = "0") BigDecimal IM,
                                @RequestParam(value = "IM02", required = false, defaultValue = "0") BigDecimal IM02,
                                @RequestParam(value = "ADV", required = true) BigDecimal ADV,
                                @RequestParam(value = "ADV02", required = true) BigDecimal ADV02,
                                @RequestParam(value = "RV", required = false, defaultValue = "0") BigDecimal RV,
                                @RequestParam(value = "RV02", required = false, defaultValue = "0") BigDecimal RV02,
                                @RequestParam(value = "ASH", required = false, defaultValue = "0.0") BigDecimal ASH,
                                @RequestParam(value = "ASH02", required = false, defaultValue = "0.0") BigDecimal ASH02,
                                @RequestParam(value = "AFT", required = false, defaultValue = "0") int AFT,
                                @RequestParam(value = "HGI", required = false, defaultValue = "0") int HGI,
                                @RequestParam(value = "GV", required = false, defaultValue = "0") int GV,
                                @RequestParam(value = "GV02", required = false, defaultValue = "0") int GV02,
                                @RequestParam(value = "YV", required = false, defaultValue = "0") int YV,
                                @RequestParam(value = "YV02", required = false, defaultValue = "0") int YV02,
                                @RequestParam(value = "FC", required = false, defaultValue = "0") int FC,
                                @RequestParam(value = "FC02", required = false, defaultValue = "0") int FC02,
                                @RequestParam(value = "CRC", required = false, defaultValue = "0") int CRC,
                                @RequestParam(value = "CRC02", required = false, defaultValue = "0") int CRC02,
                                @RequestParam(value = "PS", required = false, defaultValue = "0") int PS,
                                @RequestParam(value = "ykj", required = false, defaultValue = "0") BigDecimal ykj,
                                @RequestParam(value = "jtj01", required = false, defaultValue = "0") BigDecimal jtj01,
                                @RequestParam(value = "amount12", required = false, defaultValue = "0") int amount12,
                                @RequestParam(value = "jtj02", required = false, defaultValue = "0") BigDecimal jtj02,
                                @RequestParam(value = "amount21", required = false, defaultValue = "0") int amount21,
                                @RequestParam(value = "amount22", required = false, defaultValue = "0") int amount22,
                                @RequestParam(value = "jtj03", required = false, defaultValue = "0") BigDecimal jtj03,
                                @RequestParam(value = "amount31", required = false, defaultValue = "0") int amount31,
                                @RequestParam(value = "amount32", required = false, defaultValue = "0") int amount32,
                                @RequestParam(value = "jtj04", required = false, defaultValue = "0") BigDecimal jtj04,
                                @RequestParam(value = "amount41", required = false, defaultValue = "0") int amount41,
                                @RequestParam(value = "amount42", required = false, defaultValue = "0") int amount42,
                                @RequestParam(value = "jtj05", required = false, defaultValue = "0") BigDecimal jtj05,
                                @RequestParam(value = "amount51", required = false, defaultValue = "0") int amount51,
                                @RequestParam(value = "amount52", required = false, defaultValue = "0") int amount52,
                                @RequestParam(value = "deliveryprovince", required = true) int deliveryprovince,
                                @RequestParam(value = "deliveryplace", required = true) String deliveryplace,
                                @RequestParam(value = "deliverymode", required = true) String deliverymode,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "deliverytime1", required = true) LocalDate deliverytime1,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "deliverytime2", required = true) LocalDate deliverytime2,
                                @RequestParam(value = "supplyquantity", required = true) int supplyquantity,
                                @RequestParam(value = "inspectorg", required = true) String inspectorg,
                                @RequestParam(value = "otherinspectorg", required = false, defaultValue = "") String otherinspectorg,
                                @RequestParam(value = "otherinspectorg1", required = false, defaultValue = "") String otherinspectorg1,
                                @RequestParam(value = "otherharbour", required = false, defaultValue = "") String otherharbour,
                                @RequestParam(value = "otherharbour1", required = false, defaultValue = "") String otherharbour1,
                                @RequestParam(value = "type", required = false, defaultValue = "NULL") String type,
                                @RequestParam(value = "originplace", required = true) String originplace,
                                @RequestParam(value = "paymode", required = true) int paymode,
                                @RequestParam(value = "payperiod", required = false) BigDecimal payperiod,
                                @RequestParam(value = "releaseremarks", required = false) String releaseremarks,
                                @RequestParam(value = "linktype", required = true)boolean linktype,
                                @RequestParam(value = "linkmanname", required = false, defaultValue = "")String linkmanname,
                                @RequestParam(value = "linkmanphone", required = false, defaultValue = "")String linkmanphone,
                                @RequestParam(value = "version", required = false, defaultValue = "0")int version,
                                @RequestParam(value = "brandname", required = false, defaultValue = "") String brandname,
                                @RequestParam(value = "tax", required = false, defaultValue = "2")int tax,
                                @RequestParam(value = "accountsmethod", required = false, defaultValue = "0")int accountsmethod,
                                @RequestParam(value = "logistics", required = false, defaultValue = "0")int logistics,
                                @RequestParam(value = "finance", required = false, defaultValue = "0")int finance,
                                @RequestParam(value = "promotion", required = false, defaultValue = "0")int promotion,
                                @RequestParam(value = "promotionremarks", required = false, defaultValue = "")String promotionremarks,
                                @RequestParam(value = "chemicalexam1", required = false, defaultValue = "")String chemicalexam1,
                                @RequestParam(value = "chemicalexam2", required = false, defaultValue = "")String chemicalexam2,
                                @RequestParam(value = "chemicalexam3", required = false, defaultValue = "")String chemicalexam3,
                                @RequestParam(value = "coalpic1", required = false, defaultValue = "")String coalpic1,
                                @RequestParam(value = "coalpic2", required = false, defaultValue = "")String coalpic2,
                                @RequestParam(value = "coalpic3", required = false, defaultValue = "")String coalpic3,
                                @RequestParam(value = "coalpic4", required = false, defaultValue = "")String coalpic4,
                                @RequestParam(value = "coalpic5", required = false, defaultValue = "")String coalpic5,
                                @RequestParam(value = "coalpic6", required = false, defaultValue = "")String coalpic6)  throws Exception {
        checkController.doCheckSessionUser();
        if(releaseremarks.length() > 200) throw new BusinessException("备注字段太长，请修改后再提交！");
        switch (pname){
            case "动力煤":
            case "无烟煤":
                if(NCV == 0 || NCV02 == 0) throw new NotFoundException();
                break;
            case "喷吹煤":
                if(NCV == 0 || NCV02 == 0 || ASH.compareTo(BigDecimal.valueOf(0)) == 0 || ASH02.compareTo(BigDecimal.valueOf(0)) == 0) throw new NotFoundException();
                break;
            case "焦煤":
                if(ASH.compareTo(BigDecimal.valueOf(0)) == 0 || ASH02.compareTo(BigDecimal.valueOf(0)) == 0 || GV == 0 || GV02 == 0 || YV == 0 || YV02 == 0) throw new NotFoundException();
                break;
            default:
                throw new NotFoundException();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        String error = "";
        List<PriceLadder> priceLadderList = new ArrayList<PriceLadder>();
        BigDecimal[] priceList = new BigDecimal[]{jtj01, jtj02, jtj03, jtj04, jtj05};
        Integer[] amount1List = new Integer[]{0, amount21, amount31, amount41, amount51};
        Integer[] amount2List = new Integer[]{amount12, amount22, amount32, amount42, amount52};
        int id = 0;
        String seller = companyMapper.getCompanyByUserid(user.getId()).getName();
        if (!StringUtils.isNullOrEmpty(otherharbour1)) { otherharbour = otherharbour1; }
        if (!StringUtils.isNullOrEmpty(otherinspectorg1)) { otherinspectorg = otherinspectorg1; }
        String province = areaportMapper.getNameById(deliveryprovince);
        String region = areaportMapper.getNameBySonName(province);
        String harbour = areaportMapper.getNameById(Integer.valueOf(deliveryplace.replace(",", "")));
        if (harbour == null) {
            harbour = "其它";
        }
        if(!linktype){
            linkmanname = "";
            linkmanphone = "";
        }
        //获取区域id
        Integer regionId = areaportMapper.getIdByName(region);
        //获取港口id
        Integer portId=areaportMapper.getIdByName(harbour);
        //如果没有港口信息，比如港口为其它的情况，把portId设置为-1
        portId = portId==null?-1:portId;
        //付款方式
        if(paymode != 2){
            payperiod = new BigDecimal(0);
        } else {
            if(payperiod == null) throw new NotFoundException();
        }
        int i = 0;
        BigDecimal jtjLast = jtj01;
        while(i < priceList.length && priceList[i].doubleValue() > 0){
            priceLadderList.add(new PriceLadder(i+1, priceList[i], amount1List[i], amount2List[i]));
            jtjLast = jtjLast.doubleValue() > priceList[i].doubleValue() ? priceList[i] : jtjLast;
            i++;
        }
        String PSName = dataBookMapper.getDataBookNameByTypeSequence("pstype", PS);
        String accountsmethodname = "";
        if (tax == 1) {
            accountsmethodname = dataBookMapper.getDataBookNameByTypeSequence("accountsmethod", accountsmethod);
        } else {
            accountsmethod = 0;
        }
        SellInfo sellInfocompare = buyMapper.getSellInfoById(sid) ;
        //if(sellInfocompare == null) throw new NotFoundException();
        if(!StringUtils.isNullOrEmpty(chemicalexam1) && chemicalexam1.contains("temp")) {
            fileStore.copyFileToUploadDir(chemicalexam1);
            chemicalexam1 = chemicalexam1.replace("temp", "upload");
        }
        if(!StringUtils.isNullOrEmpty(chemicalexam2)&&chemicalexam2.contains("temp")) {
            fileStore.copyFileToUploadDir(chemicalexam2);
            chemicalexam2 = chemicalexam2.replace("temp", "upload");
        }
        if(!StringUtils.isNullOrEmpty(chemicalexam3)&&chemicalexam3.contains("temp")) {
            fileStore.copyFileToUploadDir(chemicalexam3);
            chemicalexam3 = chemicalexam3.replace("temp", "upload");
        }
        if(!StringUtils.isNullOrEmpty(coalpic1)) {
            fileStore.copyFileToUploadDir(coalpic1);
            coalpic1 = coalpic1.replace("temp", "upload");
        }
        if(!StringUtils.isNullOrEmpty(coalpic2)) {
            fileStore.copyFileToUploadDir(coalpic2);
            coalpic2 = coalpic2.replace("temp", "upload");
        }
        if(!StringUtils.isNullOrEmpty(coalpic3)) {
            fileStore.copyFileToUploadDir(coalpic3);
            coalpic3 = coalpic3.replace("temp", "upload");
        }
        if(!StringUtils.isNullOrEmpty(coalpic4)) {
            fileStore.copyFileToUploadDir(coalpic4);
            coalpic4 = coalpic4.replace("temp", "upload");
        }
        if(!StringUtils.isNullOrEmpty(coalpic5)) {
            fileStore.copyFileToUploadDir(coalpic5);
            coalpic5 = coalpic5.replace("temp", "upload");
        }
        if(!StringUtils.isNullOrEmpty(coalpic6)) {
            fileStore.copyFileToUploadDir(coalpic6);
            coalpic6 = coalpic6.replace("temp", "upload");
        }
        SellInfo sellInfo = new SellInfo(NCV, NCV02, ADS, ADS02, RS, RS02, TM, TM02, IM, IM02, ADV, ADV02, RV, RV02,
                ASH, ASH02, AFT, HGI, GV, GV02, YV, YV02, FC, FC02, PS, PSName, CRC, CRC02, EnumSellInfo.WaitConfirmed,
                pname, ykj, seller, user.getId(), regionId, deliveryprovince, portId, region, province, harbour,
                otherharbour, deliverymode, deliverytime1, deliverytime2, supplyquantity, supplyquantity, inspectorg,
                otherinspectorg, originplace, paymode, payperiod, releaseremarks, 1, 1, brandname, tax, accountsmethod,
                accountsmethodname, logistics, finance, promotion, promotionremarks, chemicalexam1, chemicalexam2,
                chemicalexam3, coalpic1, coalpic2, coalpic3, coalpic4, coalpic5, coalpic6, 0, 0,
                linktype, linkmanname, linkmanphone, sid, version);
        toolsMethod.doCheckAndChangeIndex(sellInfo);
        if (deliverytime1.isBefore(LocalDate.now().minusDays(1))) {
            error = "before";
        } else if(ykj.doubleValue() ==0 && (jtj01.doubleValue() == 0 || jtj02.doubleValue() == 0)){
            throw new NotFoundException();
        } else if(sid == 0 || (sid != 0 && !type.equals("update"))) {
            if(sellInfo == null || (sid != 0 && buyMapper.getSellInfoById(sid) == null)) throw new NotFoundException();
            try{
                id = buyService.addSellInfo(sellInfo, priceLadderList, jtjLast, user.getId(), user.getSecurephone());
            } catch (SQLExcutionErrorException se){
                auth.doOutputErrorInfo("发布供应信息(sellinfo)失败! 发布人userid=" + user.getId());
                throw new BusinessException(EnumRemindInfo.Site_System_Error.value());
            }
            success = true;
        } else {
            try {
                buyService.updateSellInfo(sellInfo, priceLadderList, jtjLast, user.getId(), user.getSecurephone());
            } catch (SQLExcutionErrorException se){
                auth.doOutputErrorInfo("更改供应信息(sellinfo)出错, 供应信息id=" + sid + ", 发布人userid=" + user.getId());
                throw new BusinessException(EnumRemindInfo.Site_System_Error.value());
            }
            id = sid;
            success = true;
        }
        map.put("id", id);
        map.put("success", success);
        map.put("error", error);
        return map;
    }

    /**
     * 跳转到发布供应信息页面
     * @param model   向发布信息页面上 put 初始化元素
     * @return        跳转到发布信息页面
     */
    @RequestMapping("/sell/createSupply")
    @LoginRequired
    public String getSupplyInfo(Map<String, Object> model) {
        initCreateSupply(model);
        return "releaseSupply";
    }

    /**
     * 更改供应信息
     * 未曾通过审核的修改供应跳到 "/releaseSupply"
     * 曾通过审核的修改供应跳到 "/releaseSupplyUpdate"
     * @param id
     * @param type
     * @param model
     * @return
     */
    @RequestMapping("/account/updateSupply")
    @LoginRequired
    public String goUpdateSupply(int id, String type, Map<String, Object> model) {
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if (sellInfo == null) throw new NotFoundException();
        initUpdateSupply(sellInfo.getDeliveryprovince(), model);
        buyMethod.showJTJ(id, model);
        model.put("sellInfo", sellInfo);
        model.put("type", type);
        String url = "/releaseSupply";
        // 是否曾经通过审核
        String verifiedOnce = "false";
        // 根据editnum 和 VerifyPass 判断是否曾经通过审核
        if ((EnumSellInfo.VerifyPass).equals(sellInfo.getStatus()) || sellInfo.getEditnum()>0) {
            verifiedOnce = "true";
            url = "/releaseSupplyUpdate";
        }
        model.put("verifiedOnce", verifiedOnce);
        return url;
    }

    public void initCreateSupply(Map<String, Object> model) {
        List<Areaport> harbourlist = areaportMapper.getProcvincesOrPortsByParentid(areaportMapper.getAllProvince().get(0).getId());
        harbourlist.add(new Areaport(-1, "其它"));
        model.put("harbourlist", harbourlist);
        initCreateUpdateSupply(model);
    }

    public void initUpdateSupply(String deliveryprovince, Map<String, Object> model) {
        List<Areaport> harbourlist = areaportMapper.getProcvincesOrPortsByParentname(deliveryprovince);
        harbourlist.add(new Areaport(-1, "其它"));
        model.put("harbourlist", harbourlist);
        initCreateUpdateSupply(model);
    }

    public void initCreateUpdateSupply(Map<String, Object> model){
        List<Dictionary> inspectorgs = new ArrayList<Dictionary>();
        inspectorgs.add(new Dictionary(0, "inspectionagency", "无"));
        inspectorgs.addAll(dictionaryMapper.getAllInspectionagencys());
        inspectorgs.add(new Dictionary(100, "inspectionagency", "其它"));
        model.put("provincelist", areaportMapper.getAllProvince());
        model.put("deliverymodes", dataBookMapper.getDataBookListByType("deliverymode"));
        model.put("inspectorgs", inspectorgs);
        model.put("pnames", dictionaryMapper.getAllCoalTypes());
        model.put("pstypelist", dataBookMapper.getDataBookListByType("pstype"));
    }

    /**
     * 确认发布供应信息
     * @param id        供应信息id
     * @param version   供应信息version
     * @return          true or false
     */
    @RequestMapping("/confirmSellinfo")
    @ResponseBody
    @LoginRequired
    @Transactional
    public Object doConfirmSellinfo(@RequestParam(value = "id", required = true)int id,
                                    @RequestParam(value = "version", required = true)int version) {
        checkController.doCheckSessionUser();
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if (sellInfo == null) throw new NotFoundException();
        auth.doCheckUserRight(sellInfo.getSellerid());
        try {
            buyService.confirmSellInfo(id, version, EnumSellInfo.WaitVerify, null, session.getUser().getId());
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("确认发布供应信息(sellinfo) 出错, 供应信息id=" + id + ", version=" + version + ", 发布人userid=" + session.getUser().getId());
            throw new BusinessException("确认发布出错, 请刷新页面重试! ");
        }
        return true;
    }

    /**
     * 商城，现货搜索 查询列表
     * @param coaltype        煤炭种类
     * @param province        省份id
     * @param harbour         港口id
     * @param NCV01           低位热值区间-左边值
     * @param NCV02           低位热值区间-右边值
     * @param ADV01           空气干燥剂挥发分01
     * @param ADV02           空气干燥剂挥发分02
     * @param RV01            收到基挥发分01
     * @param RV02            收到基挥发分02
     * @param RS01            收到基硫分01
     * @param RS02            收到基硫分02
     * @param ADS01           空气干燥剂全硫01
     * @param ADS02           空气干燥剂全硫02
     * @param TM01            全水01
     * @param TM02            全水02
     * @param IM01            内水01
     * @param IM02            内水02
     * @param ASH01           收到基灰分01
     * @param ASH02           收到基灰分02
     * @param G01             G值01
     * @param G02             G值02
     * @param Y01             Y值01
     * @param Y02             Y值02
     * @param FC01            固定碳01
     * @param FC02            固定碳02
     * @param AFT01           灰熔点01
     * @param AFT02           灰熔点02
     * @param HGI01           哈氏可磨01
     * @param HGI02           哈氏可磨02
     * @param PS              颗粒度
     * @param sorttype        排序类型
     * @param sequence        增序或者倒叙
     * @param sellerType      卖家类型
     * @param pid             产品id
     * @param sellerType      查询方式 0  是搜索排序， 1是按照产品编号排序
     * @param sellerType      卖家类型， 0是商城产品，1是撮合产品
     * @param model           map
     * @param param           分页封装类
     * @return
     */
    @RequestMapping(value = "/buy", method = RequestMethod.GET)
    public String buyList(
            @RequestParam(value = "coaltype", required = false, defaultValue = "0")int coaltype,
            @RequestParam(value = "province", required = false, defaultValue = "0") int province,
            @RequestParam(value = "harbour", required = false, defaultValue = "0") int harbour,
            @RequestParam(value = "NCV01", required = false, defaultValue = "0") int NCV01,
            @RequestParam(value = "NCV02", required = false, defaultValue = "7500") int NCV02,
            @RequestParam(value = "ADV01", required = false, defaultValue = "0") BigDecimal ADV01,
            @RequestParam(value = "ADV02", required = false, defaultValue = "50") BigDecimal ADV02,
            @RequestParam(value = "RV01", required = false, defaultValue = "0") BigDecimal RV01,
            @RequestParam(value = "RV02", required = false, defaultValue = "50") BigDecimal RV02,
            @RequestParam(value = "RS01", required = false, defaultValue = "0") BigDecimal RS01,
            @RequestParam(value = "RS02", required = false, defaultValue = "10") BigDecimal RS02,
            @RequestParam(value = "ADS01", required = false, defaultValue = "0") BigDecimal ADS01,
            @RequestParam(value = "ADS02", required = false, defaultValue = "10") BigDecimal ADS02,
            @RequestParam(value = "TM01", required = false, defaultValue = "0") BigDecimal TM01,
            @RequestParam(value = "TM02", required = false, defaultValue = "50") BigDecimal TM02,
            @RequestParam(value = "IM01", required = false, defaultValue = "0") BigDecimal IM01,
            @RequestParam(value = "IM02", required = false, defaultValue = "50") BigDecimal IM02,
            @RequestParam(value = "ASH01", required = false, defaultValue = "0") BigDecimal ASH01,
            @RequestParam(value = "ASH02", required = false, defaultValue = "50") BigDecimal ASH02,
            @RequestParam(value = "G01", required = false, defaultValue = "0") int G01,
            @RequestParam(value = "G02", required = false, defaultValue = "100") int G02,
            @RequestParam(value = "Y01", required = false, defaultValue = "0") BigDecimal Y01,
            @RequestParam(value = "Y02", required = false, defaultValue = "100") BigDecimal Y02,
            @RequestParam(value = "FC01", required = false, defaultValue = "0") int FC01,
            @RequestParam(value = "FC02", required = false, defaultValue = "100") int FC02,
            @RequestParam(value = "AFT01", required = false, defaultValue = "900") int AFT01,
            @RequestParam(value = "AFT02", required = false, defaultValue = "1600") int AFT02,
            @RequestParam(value = "HGI01", required = false, defaultValue = "0") int HGI01,
            @RequestParam(value = "HGI02", required = false, defaultValue = "1000") int HGI02,
            @RequestParam(value = "PS", required = false, defaultValue = "0")int PS,
            @RequestParam(value = "sorttype", required = false, defaultValue = "0")int sorttype,
            @RequestParam(value = "sequence", required = false, defaultValue = "0")int sequence,
            @RequestParam(value = "pid", required = false, defaultValue = "")String pid,
            @RequestParam(value = "sellerType", required = false, defaultValue = "1")Integer sellerType,
            @RequestParam(value = "setPicTrueOnly", required = false, defaultValue = "1") Integer setPicTrueOnly,
            Map<String, Object> model, PageQueryParam param) {
        sellInfoList(pid, coaltype, 0, province, harbour, NCV01, NCV02, ADV01, ADV02, RV01, RV02, RS01, RS02, ADS01, ADS02, TM01, TM02, IM01, IM02, ASH01, ASH02, G01, G02, Y01, Y02, FC01, FC02, AFT01, AFT02, HGI01, HGI02, PS, sellerType, setPicTrueOnly, sorttype, sequence, model, param);
        model.put("title",Seoconfig.xhSearch_title);
        model.put("keywords",Seoconfig.xhSearch_keywords);
        model.put("description", Seoconfig.xhSearch_description);
        return "buy";
    }

    @RequestMapping(value = "/mall", method = RequestMethod.GET)
    public String mallList(
            @RequestParam(value = "coaltype", required = false, defaultValue = "0")int coaltype,
            @RequestParam(value = "province", required = false, defaultValue = "0") int province,
            @RequestParam(value = "harbour", required = false, defaultValue = "0") int harbour,
            @RequestParam(value = "NCV01", required = false, defaultValue = "0") int NCV01,
            @RequestParam(value = "NCV02", required = false, defaultValue = "7500") int NCV02,
            @RequestParam(value = "ADV01", required = false, defaultValue = "0") BigDecimal ADV01,
            @RequestParam(value = "ADV02", required = false, defaultValue = "50") BigDecimal ADV02,
            @RequestParam(value = "RV01", required = false, defaultValue = "0") BigDecimal RV01,
            @RequestParam(value = "RV02", required = false, defaultValue = "50") BigDecimal RV02,
            @RequestParam(value = "RS01", required = false, defaultValue = "0") BigDecimal RS01,
            @RequestParam(value = "RS02", required = false, defaultValue = "10") BigDecimal RS02,
            @RequestParam(value = "ADS01", required = false, defaultValue = "0") BigDecimal ADS01,
            @RequestParam(value = "ADS02", required = false, defaultValue = "10") BigDecimal ADS02,
            @RequestParam(value = "TM01", required = false, defaultValue = "0") BigDecimal TM01,
            @RequestParam(value = "TM02", required = false, defaultValue = "50") BigDecimal TM02,
            @RequestParam(value = "IM01", required = false, defaultValue = "0") BigDecimal IM01,
            @RequestParam(value = "IM02", required = false, defaultValue = "50") BigDecimal IM02,
            @RequestParam(value = "ASH01", required = false, defaultValue = "0") BigDecimal ASH01,
            @RequestParam(value = "ASH02", required = false, defaultValue = "50") BigDecimal ASH02,
            @RequestParam(value = "G01", required = false, defaultValue = "0") int G01,
            @RequestParam(value = "G02", required = false, defaultValue = "100") int G02,
            @RequestParam(value = "Y01", required = false, defaultValue = "0") BigDecimal Y01,
            @RequestParam(value = "Y02", required = false, defaultValue = "100") BigDecimal Y02,
            @RequestParam(value = "FC01", required = false, defaultValue = "0") int FC01,
            @RequestParam(value = "FC02", required = false, defaultValue = "100") int FC02,
            @RequestParam(value = "AFT01", required = false, defaultValue = "900") int AFT01,
            @RequestParam(value = "AFT02", required = false, defaultValue = "1600") int AFT02,
            @RequestParam(value = "HGI01", required = false, defaultValue = "0") int HGI01,
            @RequestParam(value = "HGI02", required = false, defaultValue = "1000") int HGI02,
            @RequestParam(value = "PS", required = false, defaultValue = "0")int PS,
            @RequestParam(value = "sorttype", required = false, defaultValue = "0")int sorttype,
            @RequestParam(value = "sequence", required = false, defaultValue = "0")int sequence,
            @RequestParam(value = "pid", required = false, defaultValue = "")String pid,
            @RequestParam(value = "setPicTrueOnly", required = false, defaultValue = "1") Integer setPicTrueOnly,
            Map<String, Object> model, PageQueryParam param) {
        sellInfoList(pid, coaltype, 0, province, harbour, NCV01, NCV02, ADV01, ADV02, RV01, RV02, RS01, RS02, ADS01, ADS02, TM01, TM02, IM01, IM02, ASH01, ASH02, G01, G02, Y01, Y02, FC01, FC02, AFT01, AFT02, HGI01, HGI02, PS, 0, setPicTrueOnly, sorttype, sequence, model, param);
        model.put("mallType", "mall");
        model.put("title",Seoconfig.ymMall_title);
        model.put("keywords", Seoconfig.ymMall_keywords);
        model.put("description",Seoconfig.ymMall_description);
        return "mall";
    }

    private void sellInfoList(String pid, int coaltype, int region, int province, int harbour, Integer NCV01, Integer NCV02, BigDecimal ADV01, BigDecimal ADV02, BigDecimal RV01, BigDecimal RV02,
                              BigDecimal RS01, BigDecimal RS02, BigDecimal ADS01, BigDecimal ADS02, BigDecimal TM01, BigDecimal TM02, BigDecimal IM01, BigDecimal IM02, BigDecimal ASH01, BigDecimal ASH02,
                              Integer G01, Integer G02, BigDecimal Y01, BigDecimal Y02, Integer FC01, Integer FC02, Integer AFT01, Integer AFT02, Integer HGI01, Integer HGI02, Integer PS, Integer sellerType,
                              Integer setPicTrueOnly, int sorttype, int sequence, Map<String, Object> model, PageQueryParam param) {
        String coalname = dictionaryMapper.getNameById(coaltype);
        int count = 0;
        List<SellInfo> productList = new ArrayList<>();
        if(StringUtils.isNullOrEmpty(pid)) {
            count = buySearchMapper.getSellInfoSearchCount(coalname, region, province, harbour, NCV01, NCV02, ADV01, ADV02, RV01, RV02, RS01, RS02, ADS01, ADS02, TM01, TM02, IM01, IM02, ASH01, ASH02, G01, G02, Y01, Y02, FC01, FC02, AFT01, AFT02, HGI01, HGI02, PS, sellerType, setPicTrueOnly);
            param.setCount(count);
            productList = buySearchMapper.getSellInfoSearchList(coalname, region, province, harbour, NCV01, NCV02, ADV01, ADV02, RV01, RV02, RS01, RS02, ADS01, ADS02, TM01, TM02, IM01, IM02, ASH01, ASH02, G01, G02, Y01, Y02, FC01, FC02, AFT01, AFT02, HGI01, HGI02, PS, sellerType, setPicTrueOnly, sorttype, sequence, param.getPagesize(), param.getIndexNum());
        } else{
            count = buySearchMapper.getSellInfoCountByPid(Where.$like$(pid), 0);
            param.setCount(count);
            productList = buySearchMapper.getSellInfoListByPid(Where.$like$(pid), 0, param.getPagesize(), param.getIndexNum());
        }
        model.put("count", count);
        model.put("page", param.getPage());
        model.put("pagesize", param.getPagesize());
        model.put("parameterMap", productList);
        sellInfoElement(pid, coalname, region, province, harbour, NCV01, NCV02, ADV01, ADV02, RV01, RV02, RS01, RS02, ADS01, ADS02, TM01, TM02, IM01, IM02, ASH01, ASH02, G01, G02, Y01, Y02, FC01, FC02, AFT01, AFT02, HGI01, HGI02, PS, sellerType, setPicTrueOnly, sorttype, sequence, model);
    }

    private void sellInfoElement(String pid, String coalname, int region, int province, int harbour, Integer NCV01, Integer NCV02, BigDecimal ADV01, BigDecimal ADV02, BigDecimal RV01, BigDecimal RV02,
                                 BigDecimal RS01, BigDecimal RS02, BigDecimal ADS01, BigDecimal ADS02, BigDecimal TM01, BigDecimal TM02, BigDecimal IM01, BigDecimal IM02, BigDecimal ASH01, BigDecimal ASH02,
                                 Integer G01, Integer G02, BigDecimal Y01, BigDecimal Y02, Integer FC01, Integer FC02, Integer AFT01, Integer AFT02, Integer HGI01, Integer HGI02, Integer PS,
                                 Integer sellerType, Integer setPicTrueOnly, int sorttype, int sequence, Map<String, Object> model) {
        List<Areaport> provinceList = getMallProvinces(region);
        List<Areaport> harbourList = getMallPorts(province);
        List<Dictionary> coalList = new ArrayList<Dictionary>();
        coalList.add(new Dictionary(0, "coaltype", "全部"));
        coalList.addAll(dictionaryMapper.getAllCoalTypes());
        model.put("totalStreamCoalNums", buySearchMapper.getCoalCountByTypeSeller("动力煤", sellerType));
        model.put("totalAnthraciteCoalNums", buySearchMapper.getCoalCountByTypeSeller("无烟煤", sellerType));
        model.put("totalnjectionCoalNums", buySearchMapper.getCoalCountByTypeSeller("喷吹煤", sellerType));
        model.put("totalCokingCoalNums", buySearchMapper.getCoalCountByTypeSeller("焦煤", sellerType));
        model.put("sellerType", sellerType);
        model.put("setPicTrueOnly", setPicTrueOnly);
        model.put("searchPid", pid);
        model.put("coalList", coalList);
        model.put("provincelist", provinceList);
        model.put("harbourlist", harbourList);
        model.put("pslist", dataBookMapper.getDataBookListByType("pstype"));
        model.put("sorttype", sorttype);
        model.put("sequence", sequence);
        int coaltype = StringUtils.isNullOrEmpty(coalname) == true ? 0 : dictionaryMapper.getIdByName(coalname);
        model.put("coaltype", coaltype);
        model.put("province", String.valueOf(province));
        model.put("harbour", String.valueOf(harbour));
        model.put("NCV01", String.valueOf(NCV01));
        model.put("NCV02", String.valueOf(NCV02));
        model.put("ADV01", String.valueOf(ADV01));
        model.put("ADV02", String.valueOf(ADV02));
        model.put("RV01", String.valueOf(RV01));
        model.put("RV02", String.valueOf(RV02));
        model.put("RS01", String.valueOf(RS01));
        model.put("RS02", String.valueOf(RS02));
        model.put("ADS01", String.valueOf(ADS01));
        model.put("ADS02", String.valueOf(ADS02));
        model.put("TM01", String.valueOf(TM01));
        model.put("TM02", String.valueOf(TM02));
        model.put("IM01", String.valueOf(IM01));
        model.put("IM02", String.valueOf(IM02));
        model.put("ASH01", String.valueOf(ASH01));
        model.put("ASH02", String.valueOf(ASH02));
        model.put("G01", String.valueOf(G01));
        model.put("G02", String.valueOf(G02));
        model.put("Y01", String.valueOf(Y01));
        model.put("Y02", String.valueOf(Y02));
        model.put("FC01", String.valueOf(FC01));
        model.put("FC02", String.valueOf(FC02));
        model.put("AFT01", String.valueOf(AFT01));
        model.put("AFT02", String.valueOf(AFT02));
        model.put("HGI01", String.valueOf(HGI01));
        model.put("HGI02", String.valueOf(HGI02));
        model.put("PS", String.valueOf(PS));
    }

    @RequestMapping("/mall/getProvinces")
    @ResponseBody
    public List<Areaport> getMallProvinces(Integer id) {
        List<Areaport> provinceList = new ArrayList<Areaport>();
        provinceList.add(new Areaport(0, "全部"));
        if (id != null && id != 0) {
            provinceList.addAll(doGetProvinces(id));
        } else{
            provinceList.addAll(areaportMapper.getAllProvince());
        }
        return provinceList;
    }

    @RequestMapping("/mall/getPorts")
    @ResponseBody
    public List<Areaport> getMallPorts(@RequestParam(value = "id", required = true) Integer id) {
        List<Areaport> harbourList = new ArrayList<Areaport>();
        harbourList.add(new Areaport(0, "全部"));
        if (id != null && id != 0) {
            harbourList.addAll(getPorts(id));
            harbourList.add(new Areaport(-1, "其它"));
        }
        return harbourList;
    }

    @RequestMapping("/buy/getProvinces")
    @ResponseBody
    public List<Areaport> doGetProvinces(Integer id) {
        if(id == null || areaportMapper.getProcvincesOrPortsByParentid(id) == null) throw new NotFoundException();
        return areaportMapper.getProcvincesOrPortsByParentid(id);
    }

    @RequestMapping("/buy/getPorts")
    @ResponseBody
    public List<Areaport> doGetPorts(Integer id) {
        List<Areaport> harbourList = new ArrayList<Areaport>();
        if(id != null && id != 0) {
            harbourList = getPorts(id);
        }
        harbourList.add(new Areaport(-1, "其它"));
        return harbourList;
    }

    private List<Areaport> getPorts(int id) {
        return areaportMapper.getProcvincesOrPortsByParentid(id);
    }

    @RequestMapping("/getSellinfoStatus")
    @ResponseBody
    public Object doGetSellinfoStatus(@RequestParam(value = "id", required = true) int id) {
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if (sellInfo == null) throw new NotFoundException();
        return sellInfo.getStatus();
    }

    /**
     * 发布供应成功跳转到发布成功页面
     * @return
     */
    @RequestMapping("/account/releaseSupplySuccess")
    public String doReleaseSupplySuccess(){
        return "releaseSupplySuccess";
    }

    //测试邮件
    @RequestMapping("/test/email")
    public void doTestEmail(){
        kittHandlerExceptionResolver.resolveException(null, null, null, new SQLDataException());
    }

    @RequestMapping(value = "/reservationSupply",method = RequestMethod.POST)
    @ResponseBody
    public Object reservationSupply(Reservation reservation, @CurrentUser User user,BindResult result) throws Exception {
        SellInfo sellInfo=buyMapper.getSellInfoById(reservation.getSupplyId());
        if(sellInfo==null){
            throw  new NotFoundException();
        }
        //验证数据有效性
        if(sellInfo.getAvailquantity()>49||reservation.getAmount()<50 || reservation.getDeliveryDate()==null){
                throw new BusinessException("data illegal!");
        }
        if(sellInfo.getSellerid()==user.getId()){
            result.addError("fail","不能预约您自己发布的产品!");
            return json(result);
        }
        int userReservationCount=reservationMapper.reservationCount(user.getId());
        //当天用户预约次数是否达到上限
        if(userReservationCount>=5){
            result.addError("fail","您预约提交太频繁,明天再来吧.");
            return json(result);
        }
        //当天是否预约过此商品
        Reservation res=reservationMapper.countByUserId(user.getId(),reservation.getSupplyId());
        reservation.setCustomerId(user.getId());
        reservation.setType(EnumReservation.Customer_Reservation.value());
        //没有预约,添加记录
        if(res==null){
            reservationMapper.addReservation(reservation);
            //发送短信通知客服
            if(userReservationCount==0){
                String msg="您好!手机号为"+user.getSecurephone()+"的客户预约采购了产品编号为"+sellInfo.getPid()+"的产品"+reservation.getAmount()+"吨。【易煤网】";
                MessageNotice.CommonMessage.noticeUser("18103869966", msg);
                MessageNotice.CommonMessage.noticeUser("18670140689", msg);
            }
        } else{
            //如果有修改
            reservation.setId(res.getId());
            reservationMapper.updateReservation(reservation);
        }
        return json(result);
    }

}
