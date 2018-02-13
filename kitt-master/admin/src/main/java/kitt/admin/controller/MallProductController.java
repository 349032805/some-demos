package kitt.admin.controller;

import com.mysql.jdbc.StringUtils;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.*;
import kitt.core.bl.BuyService;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.FileStore;
import kitt.core.util.ToolsMethod;
import kitt.ext.mybatis.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 15/1/7.
 */
@RestController
public class MallProductController {
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private Session session;
    @Autowired
    private DealerMapper dealerMapper;
    @Autowired
    private Tools tools;
    @Autowired
    private Auth auth;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private PriceLadderMapper priceLadderMapper;
    @Autowired
    protected FileService fileService;
    @Autowired
    protected FileStore fileStore;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BuyMethod buyMethod;
    @Autowired
    private BuyService buyService;
    @Autowired
    private ToolsMethod toolsMethod;

    @RequestMapping("/mall/sales")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getSellSellInfoList(
            @RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")int region,
            @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")int province,
            @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")int harbour,
            @RequestParam(value = "productNo", required = false, defaultValue = "")String productNo,
            @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
            @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
            int page){
        return buyMethod.doGetSellInfoProductList(region, province, harbour, productNo, 0, startDate, endDate, EnumSellInfo.VerifyPass, null, 0, page, 10);
    }

    @RequestMapping("/mall/pulls")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getOffSellinfoList(
            @RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")int region,
            @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")int province,
            @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")int harbour,
            @RequestParam(value = "productNo", required = false, defaultValue = "")String productNo,
            @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
            @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
            @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return buyMethod.doGetSellInfoProductList(region, province, harbour, productNo, 0, startDate, endDate, EnumSellInfo.OutOfStack, EnumSellInfo.OutOfDate, 0, page, 10);
    }

    /**
     * 检查需要导出的Excel条数
     */
    @RequestMapping("/mall/checkExportCount")
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
                                             @RequestParam(value = "status1", required = false)EnumSellInfo status1,
                                             @RequestParam(value = "status2", required = false)EnumSellInfo status2){
        return buyMethod.doCheckExportSellInfoCountMethod(region, province, harbour, Where.$like$(productNo), 0, startDate, endDate, status1, status2, 0);
    }

    /**
     * 商城产品导出到Excel
     * @param region                     地区id
     * @param province                   省份id
     * @param harbour                    港口id
     * @param productNo                  产品编号，交易员搜索，搜索框里的内容
     * @param startDate                  开始日期（搜索）
     * @param endDate                    结束日期（搜索）
     * @param status1                    status1
     * @param status2                    status2
     */
    @RequestMapping("/mall/exportExcel")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public void doExportMallProductList(
            @RequestParam(value = "deliveryRegion", required = false, defaultValue = "0")int region,
            @RequestParam(value = "deliveryProvince", required = false, defaultValue = "0")int province,
            @RequestParam(value = "deliveryHarbour", required = false, defaultValue = "0")int harbour,
            @RequestParam(value = "productNo", required = false, defaultValue = "")String productNo,
            @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
            @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
            @RequestParam(value = "status1", required = true)EnumSellInfo status1,
            @RequestParam(value = "status2", required = false)EnumSellInfo status2,
            HttpServletResponse response,
            HttpServletRequest request) throws IOException {
        buyMethod.doExportSellInfoList(region, province, harbour, Where.$like$(productNo), 0, startDate, endDate, status1, status2, 0, response, request);
    }

    /**
     * 发布商城产品，初始化页面
     * @return   返回 省份，检验机构等 下拉列表
     */
    @RequestMapping("/supplyInfo")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getSupplyInfo(@RequestParam(value = "sellerid", required = false, defaultValue = "0")int sellerid) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Dictionary> deliverymodelistTemp = new ArrayList<>();
        List<Dictionary> inspectorgs = dictionaryMapper.getAllInspectionagencys();
        inspectorgs.add(new Dictionary(100, "inspectionagency", "其它"));
        map.put("deliveryproviences", areaportMapper.getAllProvince());
        map.put("inspectorgs", inspectorgs);
        map.put("dealerList", auth.getDealerList());
        map.put("paymodelist", dataBookMapper.getDataBookListByType("paymode"));
        map.put("pstypelist", dataBookMapper.getDataBookListByType("pstype"));
        map.put("deliverymodelist", dataBookMapper.getDataBookListByType("deliverymode"));
        map.put("accountsmethodList", dataBookMapper.getDataBookListByType("accountsmethod"));
        deliverymodelistTemp.add(new Dictionary("deliverymode", "港口平仓"));
        deliverymodelistTemp.add(new Dictionary("deliverymode", "到岸舱底"));
        deliverymodelistTemp.add(new Dictionary("deliverymode", "场地自提"));
        map.put("deliverymodelistTemp", deliverymodelistTemp);
        map.put("coalTypeList", dictionaryMapper.getAllCoalTypes());
        if(sellerid != 0){
            map.put("traderId", userMapper.getUserById(sellerid).getTraderid());
        }
        return map;
    }

    /**
     * 发布商城产品
     * @param sid              原sellinfo id， 如果是新增 sid 为0， 更新 sid 不为0
     * @param pname            产品名字
     * @param NCV              低位热值
     * @param RS               收到基全硫
     * @param ADS              空气干燥剂泉流
     * @param TM               全水
     * @param IM               内水
     * @param ADV              空气干燥剂挥发分
     * @param RV               收到基挥发分
     * @param ASH              收到基灰分
     * @param AFT              灰熔点
     * @param HGI              哈氏可磨
     * @param ykj              一口价 单价
     * @param jtj01            阶梯价01
     * @param amount12         阶梯就01 右区间吨数
     * @param jtj02            阶梯价02
     * @param amount21         阶梯价02 左区间吨数
     * @param amount22         阶梯价02 右区间吨数
     * @param jtj03            阶梯价03
     * @param amount31         阶梯价03 左区间吨数
     * @param amount32         阶梯价03 右区间吨数
     * @param jtj04            阶梯价04
     * @param amount41         阶梯价04 左区间吨数
     * @param amount42         阶梯价04 右区间吨数
     * @param jtj05            阶梯价05
     * @param amount51         阶梯价05 左区间吨数
     * @param amount52         阶梯价05 右区间吨数
     * @param provinceId       省份id
     * @param portId           港口id
     * @param deliverymode     提货方式
     * @param deliverytime1    提货时间1
     * @param deliverytime2    提货时间2
     * @param supplyquantity   供应数据
     * @param inspectorg       检验机构
     * @param otherinspectorg  其他检验机构（手输入的）
     * @param otherharbour     其他港口（手输入的）
     * @param producttype      产品类型（推荐，普通等）
     * @param originplace      产地
     * @param releaseremarks   发布产品备注
     * @param dealerId         交易员id
     * @return                 id: 产品id， success： true or false
     */
    @RequestMapping("/addSellinfo")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doAddSellinfo(@RequestParam(value = "id",required = false, defaultValue = "0")int sid,
                                @RequestParam(value = "version",required = false, defaultValue = "0")int version,
                                @RequestParam(value = "pname", required = true) String pname,
                                @RequestParam(value = "NCV", required = false, defaultValue = "0")int NCV,
                                @RequestParam(value = "NCV02", required = false, defaultValue = "0")int NCV02,
                                @RequestParam(value = "RS", required = true)BigDecimal RS,
                                @RequestParam(value = "RS02", required = true)BigDecimal RS02,
                                @RequestParam(value = "ADS", required = false, defaultValue = "0")BigDecimal ADS,
                                @RequestParam(value = "ADS02", required = false, defaultValue = "0")BigDecimal ADS02,
                                @RequestParam(value = "TM", required=true)BigDecimal TM,
                                @RequestParam(value = "TM02", required=true)BigDecimal TM02,
                                @RequestParam(value = "IM", required = false, defaultValue = "0")BigDecimal IM,
                                @RequestParam(value = "IM02", required = false, defaultValue = "0")BigDecimal IM02,
                                @RequestParam(value = "ADV", required = true)BigDecimal ADV,
                                @RequestParam(value = "ADV02", required = true)BigDecimal ADV02,
                                @RequestParam(value = "RV", required = false, defaultValue = "0")BigDecimal RV,
                                @RequestParam(value = "RV02", required = false, defaultValue = "0")BigDecimal RV02,
                                @RequestParam(value = "ASH", required = false, defaultValue = "0") BigDecimal ASH,
                                @RequestParam(value = "ASH02", required = false, defaultValue = "0") BigDecimal ASH02,
                                @RequestParam(value = "AFT", required = false, defaultValue = "0")int AFT,
                                @RequestParam(value = "HGI", required = false, defaultValue = "0")int HGI,
                                @RequestParam(value = "GV", required = false, defaultValue = "0")int GV,
                                @RequestParam(value = "GV02", required = false, defaultValue = "0")int GV02,
                                @RequestParam(value = "YV", required = false, defaultValue = "0")int YV,
                                @RequestParam(value = "YV02", required = false, defaultValue = "0")int YV02,
                                @RequestParam(value = "FC", required = false, defaultValue = "0")int FC,
                                @RequestParam(value = "FC02", required = false, defaultValue = "0")int FC02,
                                @RequestParam(value = "PS", required = false, defaultValue = "0")int PS,
                                @RequestParam(value = "CRC", required = false, defaultValue = "0")int CRC,
                                @RequestParam(value = "CRC02", required = false, defaultValue = "0")int CRC02,
                                @RequestParam(value = "ykj", required = false, defaultValue = "0")BigDecimal ykj,
                                @RequestParam(value = "jtj01", required = false, defaultValue = "0")BigDecimal jtj01,
                                @RequestParam(value = "amount12", required = false, defaultValue = "0")int amount12,
                                @RequestParam(value = "jtj02", required = false, defaultValue = "0")BigDecimal jtj02,
                                @RequestParam(value = "amount21", required = false, defaultValue = "0")int amount21,
                                @RequestParam(value = "amount22", required = false, defaultValue = "0")int amount22,
                                @RequestParam(value = "jtj03", required = false, defaultValue = "0")BigDecimal jtj03,
                                @RequestParam(value = "amount31", required = false, defaultValue = "0")int amount31,
                                @RequestParam(value = "amount32", required = false, defaultValue = "0")int amount32,
                                @RequestParam(value = "jtj04", required = false, defaultValue = "0")BigDecimal jtj04,
                                @RequestParam(value = "amount41", required = false, defaultValue = "0")int amount41,
                                @RequestParam(value = "amount42", required = false, defaultValue = "0")int amount42,
                                @RequestParam(value = "jtj05", required = false, defaultValue = "0")BigDecimal jtj05,
                                @RequestParam(value = "amount51", required = false, defaultValue = "0")int amount51,
                                @RequestParam(value = "amount52", required = false, defaultValue = "0")int amount52,
                                @RequestParam(value = "provinceId", required = true)Integer provinceId,
                                @RequestParam(value = "portId", required = true)Integer portId,
                                @RequestParam(value = "deliverymode", required = true)String deliverymode,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "deliverytime1", required = true)LocalDate deliverytime1,
                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "deliverytime2", required = true)LocalDate deliverytime2,
                                @RequestParam(value = "supplyquantity", required = true)int supplyquantity,
                                @RequestParam(value = "inspectorg", required = false)String inspectorg,
                                @RequestParam(value = "otherinspectorg", required = false, defaultValue = "")String otherinspectorg,
                                @RequestParam(value = "otherharbour", required = false, defaultValue = "")String otherharbour,
                                @RequestParam(value = "producttype", required = false, defaultValue = "Common")String producttype,
                                @RequestParam(value = "originplace", required = true)String originplace,
                                @RequestParam(value = "paymode", required = true)int paymode,
                                @RequestParam(value = "payperiod", required = false, defaultValue = "0")BigDecimal payperiod,
                                @RequestParam(value = "releaseremarks", required = true)String releaseremarks,
                                @RequestParam(value = "traderId", required = true)int dealerId,
                                @RequestParam(value = "shopid", required = false, defaultValue = "0")int shopid,
                                @RequestParam(value = "sellerid", required = false, defaultValue = "0")int sellerid,
                                @RequestParam(value = "shoppname", required = false)String shoppname,
                                @RequestParam(value = "brandname", required = false)String brandname,
                                @RequestParam(value = "tax", required = false, defaultValue = "2")int tax,
                                @RequestParam(value = "accountsmethod", required = false, defaultValue = "0")int accountsmethod,
                                @RequestParam(value = "logistics", required = false, defaultValue = "0")int logistics,
                                @RequestParam(value = "finance", required = false, defaultValue = "0")int finance,
                                @RequestParam(value = "promotion", required = false, defaultValue = "0")int promotion,
                                @RequestParam(value = "promotionremarks", required = false)String promotionremarks,
                                @RequestParam(value = "chemicalexam1", required = false, defaultValue = "")String chemicalexam1,
                                @RequestParam(value = "chemicalexam2", required = false, defaultValue = "")String chemicalexam2,
                                @RequestParam(value = "chemicalexam3", required = false, defaultValue = "")String chemicalexam3,
                                @RequestParam(value = "coalpic1", required = false, defaultValue = "")String coalpic1,
                                @RequestParam(value = "coalpic2", required = false, defaultValue = "")String coalpic2,
                                @RequestParam(value = "coalpic3", required = false, defaultValue = "")String coalpic3,
                                @RequestParam(value = "coalpic4", required = false, defaultValue = "")String coalpic4,
                                @RequestParam(value = "coalpic5", required = false, defaultValue = "")String coalpic5,
                                @RequestParam(value = "coalpic6", required = false, defaultValue = "")String coalpic6) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        //根据煤炭种类不同,判断必须的指标是否有值
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
        //将阶梯价封装成PriceLadderList
        List<PriceLadder> priceLadderList = new ArrayList<PriceLadder>();
        BigDecimal[] priceList = new BigDecimal[]{jtj01, jtj02, jtj03, jtj04, jtj05};
        Integer[] amount1List = new Integer[]{0, amount21, amount31, amount41, amount51};
        Integer[] amount2List = new Integer[]{amount12, amount22, amount32, amount42, amount52};
        int i = 0;
        BigDecimal jtjLast = jtj01;
        while(i < priceList.length && priceList[i].doubleValue() > 0){
            priceLadderList.add(new PriceLadder(i+1, priceList[i], amount1List[i], amount2List[i]));
            jtjLast = jtjLast.doubleValue() > priceList[i].doubleValue() ? priceList[i] : jtjLast;
            i++;
        }
        //设置交易员
        Admin dealer=dealerMapper.findDealerById(dealerId);
        //处理 区域id  区域 省份 港口 等字段
        int regionId=areaportMapper.getParentidById(provinceId);
        String regionName=areaportMapper.getNameById(regionId);
        String provinceName=areaportMapper.getNameById(provinceId);
        String portName=areaportMapper.getNameById(portId);
        //如果没有港口信息，比如港口为其它的情况，把portId设置为-1
        portId = portId==null?-1:portId;
        portName = portId == -1 ? "其它" : portName;
        String PSName = dataBookMapper.getDataBookNameByTypeSequence("pstype", PS);
        inspectorg = (StringUtils.isNullOrEmpty(inspectorg) == true ? "无" : inspectorg);
        shoppname = StringUtils.isEmptyOrWhitespaceOnly(shoppname) ? null : shoppname;
        String accountsmethodname = "";
        if (tax == 1) {
            accountsmethodname = accountsmethod == 0 ? "" : dataBookMapper.getDataBookNameByTypeSequence("accountsmethod", accountsmethod);
        } else {
            accountsmethod = 0;
        }
        //如果是店铺产品,判断公司信息是否审核通过, 并为之配置交易员
        if(sellerid != 0) {
            User seller = userMapper.getUserById(sellerid);
            if(seller == null) throw new NotFoundException();
            if(!"审核通过".equals(seller.getVerifystatus())) {
                throw new BusinessException("该客户的公司信息尚未审核通过，不能发布产品！");
            }
            if (seller.getTraderid() == null) {
                if(!userMapper.doAddUpdateUserTraderMethod(sellerid, dealerId)) {
                    throw new BusinessException("服务器出错，请联系技术人员！");
                }
            }
        }
        //店铺产品一些字段处理
        Shop shop = shopMapper.getShopById(shopid);
        String sellerCompanyName = "自营";
        String sellerphone = "";
        int type = 0;
        EnumSellInfo status = EnumSellInfo.VerifyPass;
        if(shop != null) {
            sellerCompanyName = shop.getCompanyname();
            sellerphone = userMapper.getUserById(sellerid).getSecurephone();
            type = shop.getLevel();
            status = shop.getStatus() == 1 ? EnumSellInfo.VerifyPass : EnumSellInfo.WaitShopRun;
        }
        int id = 0;
        if (deliverytime1.isBefore(LocalDate.now())) {
            map.put("error", "before");
        } else if(ykj.doubleValue() == 0 && (jtj01.doubleValue() == 0 || jtj02.doubleValue() == 0)){
            throw new NotFoundException();
        } else {
            if(!StringUtils.isNullOrEmpty(chemicalexam1)&&chemicalexam1.contains("temp")) {
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
            SellInfo sellInfo = new SellInfo(NCV, NCV02, ADS, ADS02, RS, RS02, TM, TM02, IM, IM02, ADV, ADV02, RV, RV02, ASH,
                    ASH02, AFT, HGI, GV, GV02, YV, YV02, FC, FC02, PS, PSName, CRC, CRC02, status, pname, ykj, sellerCompanyName,
                    sellerid, regionId, provinceId, portId, regionName, provinceName, portName, otherharbour, deliverymode,
                    deliverytime1, deliverytime2, supplyquantity, supplyquantity, inspectorg, otherinspectorg, originplace,
                    paymode, payperiod, releaseremarks, 0, type, brandname, tax, accountsmethod, accountsmethodname, logistics,
                    finance, promotion, promotionremarks, chemicalexam1, chemicalexam2, chemicalexam3, coalpic1, coalpic2,
                    coalpic3, coalpic4, coalpic5, coalpic6, LocalDateTime.now(), dealerId, dealer.getName(), dealer.getPhone(),
                    producttype, shopid, shoppname, session.getAdmin().getUsername() + "--" + session.getAdmin().getJobnum());
            //如果左边值大于右边,自动交换值
            toolsMethod.doCheckAndChangeIndex(sellInfo);
            //添加
            if(sid==0) {
                id = buyService.addSellInfo(sellInfo, priceLadderList, jtjLast, sellerid, sellerphone);
                if (id == 0) throw new NotFoundException();
            } else{
                id = sid;
                sellInfo.setId(sid);
                sellInfo.setVersion(version);
                sellInfo.setJtjlast(jtjLast);
                buyService.updateSellInfo(sellInfo, priceLadderList, jtjLast, sellerid, sellerphone);
            }
            success = true;
        }
        map.put("id", id);
        map.put("success", success);
        return map;
    }

    /**
     * 删除，修改化验报告
     * @param sellinfoid
     * @param type 1:删除第一个化验报告图片；  2:删除第二个化验报告图片;  3:删除第三个化验报告图片
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/deleteChemicalExamPic", method = RequestMethod.POST)
    @ResponseBody
    public Object updateChemicalExamPic(@RequestParam(value = "sellinfoid", required = true) int sellinfoid, @RequestParam(value = "type", required = true) int type) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        SellInfo sellInfo = buyMapper.getSellInfoById(sellinfoid);
        if(sellInfo == null) throw new NotFoundException();
        if(type==3) {
            success = buyService.deleteSellInfoChemicalExam(sellinfoid, type);
        } else if(type == 2) {
            success =  buyService.changeSellInfoChemicalExam(sellinfoid, 2, sellInfo.getChemicalexam3())
                    && buyService.deleteSellInfoChemicalExam(sellinfoid, 3);
        } else if(type ==1 ) {
            success = buyService.changeSellInfoChemicalExam(sellinfoid, 1, sellInfo.getChemicalexam2())
                    && buyService.changeSellInfoChemicalExam(sellinfoid, 2, sellInfo.getChemicalexam3())
                    && buyService.deleteSellInfoChemicalExam(sellinfoid, 3);
        }
        if(success) {
            map.put("message", "succeed");
        } else {
            map.put("message", "fail bad request param");
        }
        return map;
    }

    @RequestMapping("/mall/getPorts")
    public List<Areaport> getMallPorts(Integer id){
        return tools.getMallPorts(id);
    }

    @RequestMapping("/mall/getProvinces")
    public List<Areaport> getMallProvinces(Integer id){
        return tools.getMallProvinces(id);
    }

    @RequestMapping("/getDealerPorts")
    public List<Areaport> getDealerPorts(Integer id){
        List<Areaport> deliveryplaceList = new ArrayList<Areaport>();
        if(id != null && id != 0) {
            deliveryplaceList.addAll(areaportMapper.getProcvincesOrPortsByParentid(id));
            deliveryplaceList.add(new Areaport(-1, "其它"));
        }
        return deliveryplaceList;
    }

    @RequestMapping("/getProviences")
    public List<Areaport> getBuyProviences(String name){
        if(StringUtils.isNullOrEmpty(name)) throw new NotFoundException();
        return areaportMapper.getProcvincesOrPortsByParentname(name);
    }

    @RequestMapping("/getBuyPorts")
    public Object getBuyPorts(String name){
        Map<String, Object> map = new HashMap<String, Object>();
        if(!StringUtils.isNullOrEmpty(name)) {
            List<Areaport> deliveryplaces = areaportMapper.getProcvincesOrPortsByParentname(name);
            deliveryplaces.add(new Areaport(-1, "其它"));
            map.put("deliveryplaces", deliveryplaces);
        } else{
            map.put("deliveryplaces", "null");
        }
        return map;
    }

    @RequestMapping("/mall/putOffShelves")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doPutOffShelves(@RequestParam(value = "id", required = true)int id,
                                  @RequestParam(value = "version", required = true)int version){
        if (buyMapper.getSellInfoById(id) == null) throw new NotFoundException();
        if (!buyService.changeSellInfoStatus(id, version, EnumSellInfo.OutOfStack, "下架商城产品,管理员工号:" + session.getAdmin().getJobnum())){
            auth.doOutputErrorInfo("下架商城产品出错, 供应信息id=" + id + ", 操作人工号:" + session.getAdmin().getJobnum());
            throw new BusinessException("下架商城产品出错, 请联系技术人员! ");
        }
        return true;
    }

    @RequestMapping("/mall/changeRecommend")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doRecommendProduct(@RequestParam(value = "id", required = true)int id,
                                     @RequestParam(value = "version", required = true)int version,
                                     @RequestParam(value = "recommend", required = true)String recommendType){
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if(sellInfo == null) throw new NotFoundException();
        if (!buyService.changeProductRecommendType(id, version, recommendType)){
            auth.doOutputErrorInfo("改变产品推荐类型出错, 供应信息id=" + id + ", 操作人工号:" + session.getAdmin().getJobnum());
            throw new BusinessException("系统出错, 请联系技术人员! ");
        }
        return new Object(){
            public boolean success = true;
            public String recommend = recommendType;
        };
    }

    @RequestMapping("/mall/findSupplyById")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object doRecommendProduct(@RequestParam(value = "id", required = true)int id){
        Map map=new HashMap<String,Object>();
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        List<Dictionary> inspectorgs = dictionaryMapper.getAllInspectionagencys();
        inspectorgs.add(new Dictionary(100, "inspectionagency", "其它"));
        List<Areaport> portList=tools.getMallPorts1(sellInfo.getProvinceId(), sellInfo.getPortId());
        if(sellInfo == null) throw new NotFoundException();
        map.put("sellInfo", sellInfo);
        map.put("paymodelist", dataBookMapper.getDataBookListByType("paymode"));
        map.put("djfs","ykj");
        map.put("dealer", dealerMapper.findDealerById(sellInfo.getTraderid()));
        map.put("pstypelist", dataBookMapper.getDataBookListByType("pstype"));
        map.put("deliverymodelist", dataBookMapper.getDataBookListByType("deliverymode"));
        map.put("deliveryproviences", areaportMapper.getAllProvince());
        map.put("accountsmethodList", dataBookMapper.getDataBookListByType("accountsmethod"));
        map.put("inspectorgs", inspectorgs);
        map.put("dealerList", auth.getDealerList());
        map.put("portList",portList);
        List<PriceLadder> priceLadderlist= new ArrayList<PriceLadder>();
        priceLadderlist=  priceLadderMapper.getPriceLadderListBySellinfoId(id);
        if(sellInfo.getYkj().doubleValue()==0){
            map.put("djfs","jtj");
        }
        switch (priceLadderlist.size()){
            case 5:
                map.put("jtj05Obj", priceLadderlist.get(4));
            case 4:
                map.put("jtj04Obj", priceLadderlist.get(3));
            case 3:
                map.put("jtj03Obj", priceLadderlist.get(2));
            case 2:
                map.put("jtj01Obj", priceLadderlist.get(0));
                map.put("jtj02Obj", priceLadderlist.get(1));
                break;
            default:
                break;
        }
        return map;
    }

    //保存化验报告图片
    @RequestMapping(value = "/mall/saveChemicalExamPic")
    public Object saveChemicalExamPic(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception{
        BigDecimal filesize = new BigDecimal(file.getSize());
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        Map<String, Object> map = new HashMap<String, Object>();
        if (returnValue <= 10) {
            response.setContentType("text/html");
            String picSavePath = fileService.uploadPicture(file);
            if (StringUtils.isNullOrEmpty(picSavePath)) throw new NotFoundException();
            map.put("picSavePath", picSavePath);
        } else {
            map.put("error","上传图片过大,请选择小于10M图片上传");
        }
        return map;
    }
}
