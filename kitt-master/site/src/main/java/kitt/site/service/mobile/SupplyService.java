package kitt.site.service.mobile;

import kitt.core.domain.*;
import kitt.core.domain.SellInfo.OrderType;
import kitt.core.persistence.*;
import kitt.core.service.ConfigConsts;
import kitt.core.service.FileStore;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.Auth;
import kitt.site.service.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by xiangyang on 15-5-18.
 */
@Service
@Transactional(readOnly = true)
public class SupplyService {

  @Autowired
  private BuyMapper buyMapper;
  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private PriceLadderMapper priceLadderMapper;
  @Autowired
  protected MyInterestMapper myInterestMapper;
  @Autowired
  private AreaportMapper areaportMapper;
  @Autowired
  private MyInterestMapper watchMapper;
  @Autowired
  private DictionaryMapper dictionaryMapper;
  @Autowired
  private DataBookMapper dataBookMapper;
  @Autowired
  private Auth auth;
  @Autowired
  private CompanyMapper companyMapper;
  @Autowired
  protected Session session;
  @Autowired
  protected FileStore fileStore;

  /**
   * @param queryParam
   * @param orderType
   * @param type       true 商城的数据，false 非自营的数据
   * @param provinceId
   * @param portId
   * @param lowNCV
   * @param highNCV
   * @param lowRS
   * @param highRS
   * @param coalType
   * @param anchor     锚点Id
   * @return
   */
  public PageQueryParam loadMallData(PageQueryParam queryParam, OrderType orderType, boolean type, Integer provinceId, Integer portId, Integer lowNCV, Integer highNCV, BigDecimal lowRS, BigDecimal highRS, String coalType, String anchor) {
    //封装排序参数
    OrderByType orderByType = new OrderByType();
    if (orderType == OrderType.priceAsc) {
      orderByType.asc("(ykj+IFNULL(jtjlast,0))");
    } else if (orderType == OrderType.priceDesc) {
      orderByType.desc("(ykj+IFNULL(jtjlast,0))");
    } else if (orderType == OrderType.lastupdatetimeAsc) {
      orderByType.asc("createdate");
    } else if (orderType == OrderType.salesAsc) {
      orderByType.asc("soldquantity");
    } else if (orderType == OrderType.salesDesc) {
      orderByType.desc("soldquantity");
    } else {
      orderByType.desc("createdate");
    }
    //转换查询参数
    /*int totalCount = buyMapper.countSell(type, provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType);
    List<SellInfo> sellInfos = buyMapper.listSellInfo(queryParam, orderByType, type, provinceId, portId, lowNCV, highNCV, lowRS, highRS, coalType, anchor);
    int totalPage = totalCount / queryParam.getPagesize();
    totalPage = totalCount % queryParam.getPagesize() == 0 ? totalPage : totalPage + 1;
    queryParam.setTotalCount(totalCount);
    queryParam.setTotalPage(totalPage);
    queryParam.setList(sellInfos);
    //如果有锚点,加载完数据，告诉前台当前是第几页
    if (StringUtils.isNotBlank(anchor)) {
      queryParam.setPage(queryParam.getIndexNum() / queryParam.getPagesize());
    }*/
    return queryParam;
  }


  /**
   * 获取所有省份划区
   *
   * @return
   */
  public List<Areaport> loadRegion() {
    return areaportMapper.getAllArea();
  }

  /**
   * 获取所有省份
   *
   * @return
   */
  public List<Areaport> loadProvince() {
    return areaportMapper.getAllProvince();
  }

  /**
   * 获取煤种
   *
   * @return
   */
  public List<Dictionary> loadCoalTypes() {
    return dictionaryMapper.getAllCoalTypes();
  }

  /**
   * 获取所有提货方式
   *
   * @return
   */
  public List<Dictionary> getDeliverymodes() {
    return dictionaryMapper.getDeliverymodes();
  }

  /**
   * 根据省份获取港口
   *
   * @param deliveryprovince
   * @return
   */
  public List<Areaport> loadPort(String deliveryprovince) {
    List<Areaport> harbourlist = areaportMapper.getProcvincesOrPortsByParentname(deliveryprovince);
    harbourlist.add(new Areaport(-1, "其它"));
    return harbourlist;
  }

  /**
   * 通过id得到区域名
   *
   * @param id
   * @return
   */
  public String loadAreaNameById(int id) {
    return areaportMapper.getNameById(id);
  }

  /**
   * 获取所有检验机构
   *
   * @return
   */
  public List<Dictionary> loadInspectionagencys() {
    List<Dictionary> inspectorgs = new ArrayList<Dictionary>();
    inspectorgs.add(new Dictionary(0, "inspectionagency", "无"));
    inspectorgs.addAll(dictionaryMapper.getAllInspectionagencys());
    inspectorgs.add(new Dictionary(100, "inspectionagency", "其它"));
    return inspectorgs;
  }


  public List<Areaport> loadAreaById(int parentId) {
    List<Areaport> harbourlist = areaportMapper.getProcvincesOrPortsByParentid(parentId);
    harbourlist.add(new Areaport(-1, "其它"));
    return harbourlist;
  }

  public SellInfo loadSellDeatilByInterestId(int id) {
    MyInterest myInterest = myInterestMapper.getMyInterestById(id);
    if (myInterest == null)
      throw new NotFoundException();
    SellInfo sellInfo = buyMapper.getSellInfoById(myInterest.getSid());
    if (sellInfo == null) {
      throw new NotFoundException();
    } else {
      if (sellInfo.getYkj().doubleValue() == 0) {
        List<PriceLadder> priceList = priceLadderMapper.getPriceLadderListBySellinfoId(id);
        sellInfo.setPricelist(priceList);
      }
    }
    return sellInfo;
  }

  public String checkSellStatus(int sid) {
    SellInfo sellInfo = buyMapper.getSellInfoById(sid);
    if (sellInfo == null) {
      throw new NotFoundException();
    } else if (sellInfo.getStatus().equals(EnumSellInfo.OutOfDate) ||
      sellInfo.getStatus().equals(EnumSellInfo.OutOfStack) ||
      sellInfo.getStatus().equals(EnumSellInfo.Canceled) ||
      sellInfo.getStatus().equals(EnumSellInfo.Deleted)) {
      return "该产品已经下架!";
    } else {
      return null;
    }
  }


  public SellInfo loadSellDeatil(int id) {
    SellInfo sellInfo = buyMapper.getSellInfoById(id);
    if (sellInfo == null) {
      throw new NotFoundException();
    }
    if (null == sellInfo.getYkj() || sellInfo.getYkj().doubleValue() == 0) {
      List<PriceLadder> priceList = priceLadderMapper.getPriceLadderListBySellinfoId(id);
      sellInfo.setPricelist(priceList);
    }
    return sellInfo;
  }


  public SellInfo loadSellDeatil(int supplyId, int userId) {
    SellInfo sellInfo = buyMapper.loadSellInfo(supplyId, userId);
    if (sellInfo == null) {
      throw new NotFoundException();
    }
    if (sellInfo.getYkj().doubleValue() == 0) {
      List<PriceLadder> priceList = priceLadderMapper.getPriceLadderListBySellinfoId(supplyId);
      sellInfo.setPricelist(priceList);
    }
    return sellInfo;
  }

  /**
   * 替换区间值，第一个值比第二个值大的情况
   *
   * @param coal
   */
  public static void replaceValue(CoalBaseData coal) {
    if(coal==null){
      throw  new  BusinessException("coalData is  not allow null");
    }
    if (coal.getNCV() != null && coal.getNCV02() != null && coal.getNCV().compareTo(coal.getNCV02())==1) {
      Integer temp = coal.getNCV();
      coal.setNCV(coal.getNCV02());
      coal.setNCV02(temp);
    }
    //空干基挥发分 ADV
    if (coal.getADV() != null && coal.getADV02() != null && coal.getADV().compareTo(coal.getADV02()) == 1) {
      BigDecimal temp = coal.getADV();
      coal.setADV(coal.getADV02());
      coal.setADV02(temp);

    }
    //全水 TM
    if (coal.getTM() != null && coal.getTM02() != null && coal.getTM().compareTo(coal.getTM02()) == 1) {
      BigDecimal temp = coal.getTM();
      coal.setTM(coal.getTM02());
      coal.setTM02(temp);
    }

    //收到基硫分 RS
    if (coal.getRS() != null && coal.getRS02() != null && coal.getRS().compareTo(coal.getRS02()) == 1) {
      BigDecimal temp = coal.getRS();
      coal.setRS(coal.getRS02());
      coal.setRS02(temp);
    }
//    收到基挥发分 RV
    if (coal.getRV() != null && coal.getRV02() != null && coal.getRV().compareTo(coal.getRV02()) == 1) {
      BigDecimal temp = coal.getRV();
      coal.setRV(coal.getRV02());
      coal.setRV02(temp);
    }
//    空干基硫分 ADS
    if (coal.getADS() != null && coal.getADS02() != null && coal.getADS().compareTo(coal.getADS02()) == 1) {
      BigDecimal temp = coal.getADS();
      coal.setADS(coal.getADS02());
      coal.setADS02(temp);
    }
//    内水 IM
    if (coal.getIM() != null && coal.getIM02() != null && coal.getIM().compareTo(coal.getIM02()) == 1) {
      BigDecimal temp = coal.getIM();
      coal.setIM(coal.getIM02());
      coal.setIM02(temp);
    }
//    灰分 ASH
    if (coal.getASH() != null && coal.getASH02() != null && coal.getASH().compareTo(coal.getASH02()) == 1) {
      BigDecimal temp = coal.getASH();
      coal.setASH(coal.getASH02());
      coal.setASH02(temp);
    }
//固定碳 FC
    if (coal.getFC() != null && coal.getFC02() != null && coal.getFC().compareTo(coal.getFC02()) == 1) {
      Integer temp = coal.getFC();
      coal.setFC(coal.getFC02());
      coal.setFC02(temp);
    }
    // G值 GV
    if (coal.getGV() != null && coal.getGV02() != null && coal.getGV().compareTo(coal.getGV02()) == 1) {
      Integer temp = coal.getGV();
      coal.setGV(coal.getGV02());
      coal.setGV02(temp);
    }
    // Y值 YV
    if (coal.getYV() != null && coal.getYV02() != null && coal.getYV().compareTo(coal.getYV02()) == 1) {
      Integer temp = coal.getYV();
      coal.setYV(coal.getYV02());
      coal.setYV02(temp);
    }
  }

  @Transactional(readOnly = false)
  public void saveSupply(SellInfo supply, User user) throws Exception{
    //设置供应所属区域、省份、港口
    String regionName = areaportMapper.getAreaByProvinceId(supply.getProvinceId()).getName();
    String provinceName = areaportMapper.getNameById(supply.getProvinceId());
    String portName = supply.getPortId() == -1 ? "其它" : areaportMapper.getNameById(supply.getPortId());
    //如果有颗粒度名称，取当前的颗粒度值,没有颗粒度名称，颗粒度值为0
    Integer ps = StringUtils.isNotEmpty(supply.getPSName()) ? dataBookMapper.findPsByName(supply.getPSName()) : 0;
    supply.setPS(ps);
    //前台发布的都是非商城的

    String chemicalexam1=supply.getChemicalexam1();
    String chemicalexam2=supply.getChemicalexam2();
    String chemicalexam3=supply.getChemicalexam3();
    System.out.println("---saveSupply---chemicalexam1------"+chemicalexam1);
    System.out.println("---saveSupply---chemicalexam2------"+chemicalexam2);
    System.out.println("---saveSupply--chemicalexam3-------"+chemicalexam3);


    if(!StringUtils.isEmpty(supply.getChemicalexam1())&&supply.getChemicalexam1().contains("temp")) {
      fileStore.copyFileToUploadDir(supply.getChemicalexam1());
      supply.setChemicalexam1(supply.getChemicalexam1().replace("temp", "upload"));
    }

    if(!StringUtils.isEmpty(supply.getChemicalexam2())&&supply.getChemicalexam2().contains("temp")) {
      fileStore.copyFileToUploadDir(supply.getChemicalexam2());
      supply.setChemicalexam2(supply.getChemicalexam2().replace("temp", "upload"));
    }

    if(!StringUtils.isEmpty(supply.getChemicalexam3())&&supply.getChemicalexam3().contains("temp")) {
      fileStore.copyFileToUploadDir(supply.getChemicalexam3());
      supply.setChemicalexam3(supply.getChemicalexam3().replace("temp", "upload"));
    }


    supply.setType(1);
    supply.setDeliveryregion(regionName);
    supply.setDeliveryprovince(provinceName);
    supply.setDeliveryplace(portName);
    supply.setRegionId(areaportMapper.getParentidById(supply.getProvinceId()));
    supply.setSeller(companyMapper.getCompanyByUserid(user.getId()).getName());
    supply.setStatus(EnumSellInfo.WaitConfirmed);
    supply.setCreatetime(LocalDateTime.now());
    supply.setLastupdatetime(LocalDateTime.now());
    supply.setSellerid(user.getId());
    supply.setAvailquantity(supply.getSupplyquantity());
    supply.setClienttype(ConfigConsts.Weixin);

    //替换指标区间值，第一个值比第二个值大的情况
    replaceValue(supply);
    //数据库没有默认值，为了和pc端保持一致、页面取值不出错。设置为0
    if (supply.getPayperiod() == null) {
      supply.setPayperiod(BigDecimal.ZERO);
    }
    //如果一口价是0，就是阶梯价
    if (supply.getYkj() == null || supply.getYkj().doubleValue() == 0) {
      if (supply.getPricelist() == null) {
        throw new BusinessException("产品价格形式有误");
      }
      supply.setJtjlast(loadjtjLast(supply.getPricelist()));
      buyMapper.addSellinfo(supply);
      priceLadderMapper.savePriceLadder(supply.getPricelist(), supply.getId(), user.getId());
    } else {
      buyMapper.addSellinfo(supply);
    }
    //设置当前供应parentId和id相同
    buyMapper.setParentIdById(supply.getId(), supply.getId());
  }

  @Transactional(readOnly = false)
  public void saveMySupply(SellInfo supply, User user) throws Exception{
    //设置供应所属区域、省份、港口
    String regionName = areaportMapper.getAreaByProvinceId(supply.getProvinceId()).getName();
    String provinceName = areaportMapper.getNameById(supply.getProvinceId());
    String portName = supply.getPortId() == -1 ? "其它" : areaportMapper.getNameById(supply.getPortId());
    Integer ps = StringUtils.isNotEmpty(supply.getPSName()) ? dataBookMapper.findPsByName(supply.getPSName()) : 0;

    String chemicalexam1=supply.getChemicalexam1();
    String chemicalexam2=supply.getChemicalexam2();
    String chemicalexam3=supply.getChemicalexam3();

    if(!StringUtils.isEmpty(supply.getChemicalexam1())&&supply.getChemicalexam1().contains("temp")) {
      fileStore.copyFileToUploadDir(supply.getChemicalexam1());
      supply.setChemicalexam1(supply.getChemicalexam1().replace("temp", "upload"));
    }

    if(!StringUtils.isEmpty(supply.getChemicalexam2())&&supply.getChemicalexam2().contains("temp")) {
      fileStore.copyFileToUploadDir(supply.getChemicalexam2());
      supply.setChemicalexam2(supply.getChemicalexam2().replace("temp", "upload"));
    }

    if(!StringUtils.isEmpty(supply.getChemicalexam3())&&supply.getChemicalexam3().contains("temp")) {
      fileStore.copyFileToUploadDir(supply.getChemicalexam3());
      supply.setChemicalexam3(supply.getChemicalexam3().replace("temp", "upload"));
    }

    supply.setPS(ps);
    supply.setType(1);
    supply.setDeliveryregion(regionName);
    supply.setDeliveryprovince(provinceName);
    supply.setDeliveryplace(portName);
    supply.setRegionId(areaportMapper.getParentidById(supply.getProvinceId()));
    supply.setSeller(companyMapper.getCompanyByUserid(user.getId()).getName());
    supply.setStatus(EnumSellInfo.WaitVerify);
    supply.setCreatetime(LocalDateTime.now());
    supply.setLastupdatetime(LocalDateTime.now());
    supply.setSellerid(user.getId());
    supply.setClienttype(ConfigConsts.Weixin);
    //数据库没有默认值，为了和pc端保持一致、页面取值不出错。设置为0
    if (supply.getPayperiod() == null) {
      supply.setPayperiod(BigDecimal.ZERO);
    }
    //如果一口价是0，就是阶梯价
    if (supply.getYkj() == null || supply.getYkj().doubleValue() == 0) {
      if (supply.getPricelist() == null) {
        throw new BusinessException("产品价格形式有误");
      }
      supply.setJtjlast(loadjtjLast(supply.getPricelist()));
      buyMapper.addSellinfoForUpdate(supply);
      priceLadderMapper.savePriceLadder(supply.getPricelist(), supply.getId(), user.getId());
    } else {
      buyMapper.addSellinfoForUpdate(supply);
    }
  }


  @Transactional(readOnly = false)
  public void confirmSupply(int supplyId, int userId) {
    if (null == loadSellDeatil(supplyId, userId)) {
      throw new NotFoundException();
    }
    buyMapper.updateSellInfoStatus(EnumSellInfo.WaitVerify, null, supplyId);
    //增加供应supplyverify，供后台使用
    SupplyVerify supplyVerify = new SupplyVerify();
    supplyVerify.setUserid(userId);
    supplyVerify.setSellinfoid(supplyId);
    supplyVerify.setStatus(EnumSellInfo.WaitVerify);
    supplyVerify.setApplytime(LocalDateTime.now());
    buyMapper.addSupplyVerify(supplyVerify);
  }


  @Transactional(readOnly = false)
  public void updateSupply(SellInfo supply) throws Exception{
    //设置供应所属区域、省份、港口
    String regionName = areaportMapper.getAreaByProvinceId(supply.getProvinceId()).getName();
    String provinceName = areaportMapper.getNameById(supply.getProvinceId());
    String portName = supply.getPortId() == -1 ? "其它" : areaportMapper.getNameById(supply.getPortId());
    Integer ps = StringUtils.isNotEmpty(supply.getPSName()) ? dataBookMapper.findPsByName(supply.getPSName()) : 0;

    String chemicalexam1=supply.getChemicalexam1();
    String chemicalexam2=supply.getChemicalexam2();
    String chemicalexam3=supply.getChemicalexam3();
    System.out.println("---updateSupply---chemicalexam1------" + chemicalexam1);
    System.out.println("---updateSupply---chemicalexam2------" + chemicalexam2);
    System.out.println("---updateSupply--chemicalexam3-------" + chemicalexam3);

    if(!StringUtils.isEmpty(chemicalexam1)&&chemicalexam1.contains("temp")) {
      fileStore.copyFileToUploadDir(chemicalexam1);
      supply.setChemicalexam1(chemicalexam1.replace("temp", "upload"));
    }else if(!StringUtils.isEmpty(chemicalexam1)&&chemicalexam1.contains("upload")){
      supply.setChemicalexam1(chemicalexam1);
    }else if(StringUtils.isEmpty(chemicalexam1)){
      supply.setChemicalexam1(null);
    }

    if(!StringUtils.isEmpty(chemicalexam2)&&chemicalexam2.contains("temp")) {
      fileStore.copyFileToUploadDir(chemicalexam2);
      supply.setChemicalexam2(chemicalexam2.replace("temp", "upload"));
    }else if(!StringUtils.isEmpty(chemicalexam2)&&chemicalexam2.contains("upload")){
      supply.setChemicalexam2(chemicalexam2);
    }else if(StringUtils.isEmpty(chemicalexam2)){
      supply.setChemicalexam2(null);
    }

    if(!StringUtils.isEmpty(chemicalexam3)&&chemicalexam3.contains("temp")) {
      fileStore.copyFileToUploadDir(chemicalexam3);
      supply.setChemicalexam3(chemicalexam3.replace("temp", "upload"));
    }else if(!StringUtils.isEmpty(chemicalexam3)&&chemicalexam3.contains("upload")){
      supply.setChemicalexam3(chemicalexam3);
    }else if(StringUtils.isEmpty(chemicalexam3)){
      supply.setChemicalexam3(null);
    }

    supply.setType(1);
    supply.setPS(ps);
    supply.setDeliveryregion(regionName);
    supply.setDeliveryprovince(provinceName);
    supply.setStatus(EnumSellInfo.WaitVerify);
    supply.setSeller(companyMapper.getCompanyByUserid(session.getUser().getId()).getName());
    supply.setDeliveryplace(portName);
    supply.setRegionId(areaportMapper.getParentidById(supply.getProvinceId()));
    supply.setAvailquantity(supply.getSupplyquantity());
    supply.setCreatetime(LocalDateTime.now());

    //数据库没有默认值，为了和pc端保持一致、页面取值不出错。设置为0
    if (supply.getPayperiod() == null) {
      supply.setPayperiod(BigDecimal.ZERO);
    }
    //删除阶梯价
    priceLadderMapper.deletePriceBySellinfoId(supply.getId());
    //如果一口价是0，就是阶梯价
    if (supply.getYkj() == null || supply.getYkj().doubleValue() == 0) {
      if (supply.getPricelist() == null) {
        throw new BusinessException("产品价格形式有误");
      }
      supply.setJtjlast(loadjtjLast(supply.getPricelist()));
      buyMapper.updateSellinfo(supply);
      priceLadderMapper.savePriceLadder(supply.getPricelist(), supply.getId(), session.getUser().getId());
    } else {
      buyMapper.updateSellinfo(supply);
    }
    buyMapper.addSupplyVerify(new SupplyVerify(EnumSellInfo.WaitVerify, LocalDateTime.now(), supply.getId(), session.getUser().getId()));
  }

  @Transactional(readOnly = false)
  public void updateMySupply(SellInfo supply,String chemicalexam1,String chemicalexam2,String chemicalexam3) throws Exception{
    //重新加载供应数据，取shopId
    SellInfo data = buyMapper.getSellInfoById(supply.getId());
    //设置供应所属区域、省份、港口
    String regionName = areaportMapper.getAreaByProvinceId(supply.getProvinceId()).getName();
    String provinceName = areaportMapper.getNameById(supply.getProvinceId());
    String portName = supply.getPortId() == -1 ? "其它" : areaportMapper.getNameById(supply.getPortId());
    Integer ps = StringUtils.isNotEmpty(supply.getPSName()) ? dataBookMapper.findPsByName(supply.getPSName()) : 0;

    System.out.println("---updateMySupply---chemicalexam1------"+chemicalexam1);
    System.out.println("---updateMySupply---chemicalexam2------"+chemicalexam2);
    System.out.println("---updateMySupply--chemicalexam3-------"+chemicalexam3);

    if(!StringUtils.isEmpty(chemicalexam1)&&chemicalexam1.contains("temp")) {
      fileStore.copyFileToUploadDir(chemicalexam1);
      supply.setChemicalexam1(chemicalexam1.replace("temp", "upload"));
    }else if(!StringUtils.isEmpty(chemicalexam1)&&chemicalexam1.contains("upload")){
      supply.setChemicalexam1(chemicalexam1);
    }else if(StringUtils.isEmpty(chemicalexam1)) {
      supply.setChemicalexam1(null);
    }

    if(!StringUtils.isEmpty(chemicalexam2)&&chemicalexam2.contains("temp")) {
      fileStore.copyFileToUploadDir(chemicalexam2);
      supply.setChemicalexam2(chemicalexam2.replace("temp", "upload"));
    }else if(!StringUtils.isEmpty(chemicalexam2)&&chemicalexam2.contains("upload")){
      supply.setChemicalexam2(chemicalexam2);
    }else if(StringUtils.isEmpty(chemicalexam2)) {
      supply.setChemicalexam2(null);
    }

    if(!StringUtils.isEmpty(chemicalexam3)&&chemicalexam3.contains("temp")) {
      fileStore.copyFileToUploadDir(chemicalexam3);
      supply.setChemicalexam3(chemicalexam3.replace("temp", "upload"));
    }else if(!StringUtils.isEmpty(chemicalexam3)&&chemicalexam3.contains("upload")){
      supply.setChemicalexam3(chemicalexam3);
    }else if(StringUtils.isEmpty(chemicalexam3)) {
      supply.setChemicalexam3(null);
    }

    supply.setType(1);
    supply.setPS(ps);
    supply.setDeliveryregion(regionName);
    supply.setShopid(data.getShopid());
    supply.setDeliveryprovince(provinceName);
    supply.setStatus(EnumSellInfo.WaitVerify);
    supply.setSeller(companyMapper.getCompanyByUserid(session.getUser().getId()).getName());
    supply.setDeliveryplace(portName);
    supply.setRegionId(areaportMapper.getParentidById(supply.getProvinceId()));
    supply.setCreatetime(LocalDateTime.now());
    //数据库没有默认值，为了和pc端保持一致、页面取值不出错。设置为0
    if (supply.getPayperiod() == null) {
      supply.setPayperiod(BigDecimal.ZERO);
    }
    //删除阶梯价
    priceLadderMapper.deletePriceBySellinfoId(supply.getId());
    //如果一口价是0，就是阶梯价
    if (supply.getYkj() == null || supply.getYkj().doubleValue() == 0) {
      if (supply.getPricelist() == null) {
        throw new BusinessException("产品价格形式有误");
      }
      supply.setJtjlast(loadjtjLast(supply.getPricelist()));
      buyMapper.updateSellinfo(supply);
      priceLadderMapper.savePriceLadder(supply.getPricelist(), supply.getId(), session.getUser().getId());
    } else {
      buyMapper.updateSellinfo(supply);
    }
    buyMapper.addSupplyVerify(new SupplyVerify(EnumSellInfo.WaitVerify, LocalDateTime.now(), supply.getId(), session.getUser().getId()));
  }

  /**
   * @param userId   用户Id
   * @param supplyId 供应Id
   * @return
   */
  @Transactional(readOnly = false)
  public boolean watchSupply(int supplyId, int userId) {
    final String type = "supply";
    SellInfo sellInfo = buyMapper.getSellInfoById(supplyId);
    //如果一口价为0，取最低的阶梯价
    BigDecimal price = sellInfo.getYkj().doubleValue() == 0 ? sellInfo.getJtjlast() : sellInfo.getYkj();
    MyInterest myInterest = new MyInterest();
    myInterest.setSeller(sellInfo.getSeller());
    myInterest.setPrice(price);
    myInterest.setUserid(userId);
    myInterest.setType(type);
    //可供吨数
    myInterest.setAmount(sellInfo.getSupplyquantity());
    //低位热值
    myInterest.setNCV(sellInfo.getNCV().toString());
    //sellinfoId
    myInterest.setSid(sellInfo.getId());
    //pid
    myInterest.setPid(sellInfo.getPid());
    //煤种
    myInterest.setPname(sellInfo.getPname());
    if(sellInfo.getNCV()!=0&&sellInfo.getNCV02() != 0) {
      myInterest.setNCV(sellInfo.getNCV() + "-"+sellInfo.getNCV02());
    }
    watchMapper.addMyInterest(myInterest);
    return true;
  }

  @Transactional(readOnly = false)
  public void addMyWatch(int supplyId, int userId, String type) {
    //如果没有关注 insert
    MyInterest myInterest = watchMapper.getMyInterestBySid(supplyId, userId, type);
    if (null == myInterest) {
      watchSupply(supplyId, userId);
    } else {
      //状态改为取消关注
      watchMapper.setMyInterestStatusBySid(supplyId, userId, type);
    }
  }

  /**
   * 判断单价
   *
   * @param sellInfo
   * @return
   */
  public BigDecimal matchUnitPrice(SellInfo sellInfo, int amount) {
    //一口价为0，说明就是阶梯价
    if (sellInfo.getYkj().doubleValue() == 0) {
      List<PriceLadder> priceList = priceLadderMapper.getPriceLadderListBySellinfoId(sellInfo.getId());
      for (PriceLadder priceObj : priceList) {
        if (amount >= priceObj.getAmount1() && amount <= priceObj.getAmount2()) {
          return priceObj.getPrice();

        }
      }
    }
    return sellInfo.getYkj();
  }

  /**
   * 个人中心-我的供应-列表查询
   *
   * @param param
   * @param user
   * @return
   */
  public PageQueryParam getMySupply(PageQueryParam param, User user) {
    List<SellInfo> sellInfoList = buyMapper.getSellinfoByUserid(user.getId(), param.getPagesize(), param.getIndexNum());
    for (int i = 0; i < sellInfoList.size(); i++) {
      SellInfo sellInfo = sellInfoList.get(i);
      sellInfoList.get(i).setPricelist(showJTJ(sellInfo.getId()));
    }
    int totalCount = buyMapper.getSellinfoCountByUserid(user.getId());
    int totalPage = totalCount / param.getPagesize();
    totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
    param.setTotalCount(totalCount);
    param.setTotalPage(totalPage);
    param.setList(sellInfoList);
    return param;
  }

  /**
   * 个人中心-我的供应-查看信息详细
   *
   * @param pid
   */
  //获取当前有效供应信息
  public SellInfo getActiveSellInfoById(String pid) {
    List<SellInfo> sellInfoList = buyMapper.getSellInfoByPid(pid);
    SellInfo result = null;
    if (sellInfoList == null)
      throw new NotFoundException();
    for (SellInfo sellInfo : sellInfoList) {
      if (sellInfo.getStatus().equals(EnumSellInfo.VerifyPass) ||
        sellInfo.getStatus().equals(EnumSellInfo.WaitVerify) ||
        sellInfo.getStatus().equals(EnumSellInfo.VerifyNotPass) ||
        sellInfo.getStatus().equals(EnumSellInfo.Canceled)) {
        result = sellInfo;
      }
    }
    if (result.getYkj().doubleValue() == 0) {
      List<PriceLadder> priceList = priceLadderMapper.getPriceLadderListBySellinfoId(result.getId());
      result.setPricelist(priceList);
    }
    return result;
  }

  //显示阶梯价
  public List<PriceLadder> showJTJ(int SellinfoId) {
    List<PriceLadder> priceLadders = priceLadderMapper.getPriceLadderListBySellinfoId(SellinfoId);
    return priceLadders;
  }

  /**
   * 个人中心-我的供应-删除供应信息
   *
   * @param id
   */
  @Transactional(readOnly = false)
  public void deleteMySupply(int id) {
    SellInfo sellInfo = buyMapper.getSellInfoById(id);
    if (sellInfo == null)
      throw new NotFoundException();
    buyMapper.updateSellInfoStatus(EnumSellInfo.Deleted, null, id);
  }

  /**
   * 个人中心-我的供应-取消供应信息
   *
   * @param id
   */
  @Transactional(readOnly = false)
  public void cancelMySupply(int id) {
    SellInfo sellInfo = buyMapper.getSellInfoById(id);
    if (sellInfo == null)
      throw new NotFoundException();
    auth.doCheckUserRight(sellInfo.getSellerid());
    int ordersCount = orderMapper.countSellInfoUnderWayOrders(sellInfo.getPid(), EnumOrder.ReturnGoods,
            EnumOrder.WaitPayment, EnumOrder.WaitVerify, EnumOrder.VerifyPass,
            EnumOrder.VerifyNotPass, EnumOrder.WaitBalancePayment, EnumOrder.MakeMatch);
    if (ordersCount != 0) {
      throw new BusinessException("该条供应信息下还存在订单,不能取消!");
    } else {
      buyMapper.cancelSellInfoById(id);
    }
  }

  //修改全部供应数据
  public SellInfo prepareAllSellInfo(SellInfo sellInfo, int userid) {
    SellInfo sellInfotemp = buyMapper.getSellInfoById(sellInfo.getId());
    if (sellInfotemp == null)
      throw new BusinessException("修改供应失败");
    int availquantity = sellInfo.getSupplyquantity() - sellInfotemp.getSoldquantity();
    if (availquantity < 0)
      throw new BusinessException("供应量必须大于已销量");
    sellInfo.setCreatetime(sellInfotemp.getCreatetime());
    sellInfo.setLastupdatetime(LocalDateTime.now());
    sellInfo.setVerifytime(sellInfotemp.getVerifytime());
    sellInfo.setVersion(sellInfotemp.getVersion());
    String seller = companyMapper.getCompanyByUserid(userid).getName();
    sellInfo.setSeller(seller);
    sellInfo.setSellerid(userid);
    sellInfo.setClienttype(ConfigConsts.Weixin);
    sellInfo.setParentid(sellInfo.getId());
    sellInfo.setAvailquantity(availquantity);
    return sellInfo;
  }

  //修改部分供应数据
  public SellInfo preparePartSellInfo(SellInfo sellInfoInput) {
    SellInfo sellInfo = buyMapper.getSellInfoById(sellInfoInput.getId());
    if (sellInfo == null)
      throw new BusinessException("修改供应失败");
    int availquantity = sellInfoInput.getSupplyquantity() - sellInfo.getSoldquantity();
    if (availquantity < 0)
      throw new BusinessException("供应量必须大于已销量");
    sellInfo.setAvailquantity(sellInfoInput.getAvailquantity());
    sellInfo.setYkj(sellInfoInput.getYkj());
    sellInfo.setDeliverytime1(sellInfoInput.getDeliverytime1());
    sellInfo.setDeliverytime2(sellInfoInput.getDeliverytime2());
    sellInfo.setPaymode(sellInfoInput.getPaymode());
    sellInfo.setSupplyquantity(sellInfoInput.getSupplyquantity());
    sellInfo.setPayperiod(sellInfoInput.getPayperiod());
    sellInfo.setReleaseremarks(sellInfoInput.getReleaseremarks());
    sellInfo.setLinktype(sellInfoInput.isLinktype());
    sellInfo.setLinkmanname(sellInfoInput.getLinkmanname());
    sellInfo.setLinkmanphone(sellInfoInput.getLinkmanphone());
    sellInfo.setPricelist(sellInfoInput.getPricelist());
    sellInfo.setVersion(sellInfoInput.getVersion());
    sellInfo.setLastupdatetime(LocalDateTime.now());
    sellInfo.setClienttype(ConfigConsts.Weixin);
    sellInfo.setPricelist(sellInfoInput.getPricelist());
    sellInfo.setCreatetime(LocalDateTime.now());
    sellInfo.setParentid(sellInfoInput.getParentid());
    sellInfo.setPid(sellInfoInput.getPid());
    sellInfo.setAvailquantity(availquantity);
    return sellInfo;
  }


  public String getCompanyByUserid(int id) {
    return companyMapper.getCompanyByUserid(id).getName();
  }


  //部分
  @Transactional(readOnly = false)
  public int addSellinfoForUpdate(SellInfo sellInfo,String chemicalexam1,String chemicalexam2,String chemicalexam3, User user) throws Exception{
    if (sellInfo == null) throw new NotFoundException();
    System.out.println("---addSellinfoForUpdate---chemicalexam1------"+chemicalexam1);
    System.out.println("---addSellinfoForUpdate---chemicalexam2------"+chemicalexam2);
    System.out.println("---addSellinfoForUpdate---chemicalexam3------"+chemicalexam3);

    if(!StringUtils.isEmpty(chemicalexam1)&&chemicalexam1.contains("temp")) {
      fileStore.copyFileToUploadDir(chemicalexam1);
      sellInfo.setChemicalexam1(chemicalexam1.replace("temp", "upload"));
    }else if(!StringUtils.isEmpty(chemicalexam1)&&chemicalexam1.contains("upload")){
      sellInfo.setChemicalexam1(chemicalexam1);
    }else if(StringUtils.isEmpty(chemicalexam1)) {
      sellInfo.setChemicalexam1(null);
    }

    if(!StringUtils.isEmpty(chemicalexam2)&&chemicalexam2.contains("temp")) {
      fileStore.copyFileToUploadDir(chemicalexam2);
      sellInfo.setChemicalexam2(chemicalexam2.replace("temp", "upload"));
    }else if(!StringUtils.isEmpty(chemicalexam2)&&chemicalexam2.contains("upload")){
      sellInfo.setChemicalexam2(chemicalexam2);
    }else if(StringUtils.isEmpty(chemicalexam2)) {
      sellInfo.setChemicalexam2(null);
    }

    if(!StringUtils.isEmpty(chemicalexam3)&&chemicalexam3.contains("temp")) {
      fileStore.copyFileToUploadDir(chemicalexam3);
      sellInfo.setChemicalexam3(chemicalexam3.replace("temp", "upload"));
    }else if(!StringUtils.isEmpty(chemicalexam3)&&chemicalexam3.contains("upload")){
      sellInfo.setChemicalexam3(chemicalexam3);
    }else if(StringUtils.isEmpty(chemicalexam3)) {
      sellInfo.setChemicalexam3(null);
    }

    if (buyMapper.getSellInfoById(sellInfo.getId()) == null || buyMapper.setSellinfoStatus(EnumSellInfo.OutOfStack, sellInfo.getId(), sellInfo.getVersion()) != 1) {
      auth.doOutputErrorInfo("更改供应信息失败，供应信息id=" + sellInfo.getId() + "，version=" + sellInfo.getVersion());
      throw new BusinessException("更改供应信息失败，请刷新网页，重新操作！");
    }
    saveMySupply(sellInfo, user);
    //buyMapper.addSupplyVerify(new SupplyVerify(EnumSellInfo.WaitVerify, LocalDateTime.now(), sellInfo.getId(), session.getUser().getId()));
    return sellInfo.getId();
  }

  //删除化验报告
  @Transactional(readOnly = false)
  public void deleteChemicalExam(int type,int sellinfoid){
    buyMapper.deleteChemicalExam(sellinfoid, type);
  }

  //修改化验报告
  @Transactional(readOnly = false)
  public void updateChemicalExam(int type,int sellinfoid,String chemicalexam) throws Exception{
    if(!StringUtils.isEmpty(chemicalexam)) {
      fileStore.copyFileToUploadDir(chemicalexam);
    }
    buyMapper.updateChemicalExam(type, sellinfoid,chemicalexam.replace("temp", "upload"));
  }


  private BigDecimal loadjtjLast(List<PriceLadder> prices) {
    List<BigDecimal> list = new ArrayList<BigDecimal>();
    for (PriceLadder p : prices) {
      list.add(p.getPrice());
    }
    Collections.sort(list);
    return list.get(0);
  }
}
