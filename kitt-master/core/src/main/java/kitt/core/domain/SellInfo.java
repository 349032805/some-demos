package kitt.core.domain;

import kitt.core.service.validate.constraints.DeliveryDateMatcher;
import kitt.core.util.EmojiFilter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;


import javax.validation.constraints.Min;



import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by jack on 14/11/25.
 */
@DeliveryDateMatcher(startDate = "deliverytime1",endDate = "deliverytime2")
public class SellInfo extends CoalBaseData implements Serializable {
    private int id;
    private String pid;
    private EnumSellInfo status;
    @NotBlank(message = "煤种不能为空")
    @Length(max = 30,message = "煤种字段不能超过30位")
    private String pname;
    private BigDecimal ykj;                        //一口价
    private String seller;                  //自营还是其他卖家
    private String deliveryregion;          //提货地区，华东地区，西南地区等
    private String deliveryprovince;        //提货省份
    private String deliveryplace;           //提货地
    @NotBlank(message = "提货方式不能为空")
    @Length(max =30,message = "提货方式应该在{min}-{max}")
    private String deliverymode;            //提货方式
    @NotNull(message = "交货开始时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliverytime1;        //提货时间1
    @NotNull(message = "交货截止时间不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliverytime2;        //提货时间2
    @Min(value = 50,message = "不能小于50吨")
    private int supplyquantity;             //供应数量
    private int soldquantity;               //已售数量
    private int availquantity;              //可售数量
    private String inspectorg;              //检验机构
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;       //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;   //最后一次更新时间
    private int sellerid;                   //卖家id
    private String remarks;                 //审核信息备注
    @Length(max = 20,message = "详细港口字段长度应该{min}-{max}之间")
    private String otherharbour;            //其它港口
    @Length(max = 20,message = "详细检验机构字段长度应该{min}-{max}之间")
    private String otherinspectorg;         //其它检验机构
    private BigDecimal jtjlast;             //阶梯价，最低价
    private LocalDate createdate;           //createdate
    private String dealerid;                //交易员编号
    private String dealername;              //交易员姓名
    private String dealerphone;             //交易员电话
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verifytime;       //审核时间
    private String producttype;             //产品类型，例如：推荐等
    private List<PriceLadder> pricelist;    //阶梯价列表
    private Integer regionId;
    private Integer provinceId;
    private Integer portId;
    private int traderid;
    private int viewtimes;                  //浏览次数
    @NotBlank(message = "产地信息不能为空")
    @Length(max = 80, message = "产地字段应该在{min}-{max}位之间")
    private String originplace;             //产地
    private int paymode;                    //付款方式
    private BigDecimal payperiod;           //到账周期
    @Length(max = 200,message = "备注字段应该在200位")
    private String releaseremarks;          //发布备注

    private int parentid;                   //原产品id
    private int editnum;                    //修改次数
    private int version;                    //版本号
    private int clienttype;                 //客户端类型
    private int type;                       //产品类型，现在0是商城产品，1是现货
    private String atype;                   //安卓端用来标识操作类型的
    private boolean linktype;               //联系方式
    @Length(max = 30,message = "联系人姓名应该在{min}-{max}之间")
    private String linkmanname;             //客户联系人

    @Length(max=11,message = "手机号长度限制为11位")
    private String linkmanphone;            //客户联系人手机号
    private Integer shopid;                 //店铺id
    private String shoppname;               //店铺产品品牌名称

    @Length(max=255,message = "化验报告图片路径限制为255")
    private String chemicalexam1;           //化验报告图片路径1
    @Length(max=255,message = "化验报告图片路径限制为255")
    private String chemicalexam2;           //化验报告图片路径2
    @Length(max=255,message = "化验报告图片路径限制为255")
    private String chemicalexam3;           //化验报告图片路径3
    private String brandname;               //产品品牌名称
    private int tax;                        //价格是否含税 0:处理老数据, 1:含税, 2:不含税
    private int accountsmethod;             //结算方式ID
    @Length(max=30,message = "结算方式不能超过30个字符")
    private String accountsmethodname;      //结算方式name
    private int logistics;                  //是否需要易煤网提供物流服务, 0:处理老数据, 1:需要, 2:不需要
    private int finance;                    //是否需要易煤网提供金融服务, 0:处理老数据, 1:需要, 2:不需要
    private int promotion;                  //是否参加促销活动, 0: 处理老数据, 1:参加促销, 2:不参加促销
    @Length(max=255, message = "煤炭图片路径不能超过255个字符")
    private String coalpic1;                //煤炭图片1
    @Length(max=255, message = "煤炭图片路径不能超过255个字符")
    private String coalpic2;                //煤炭图片2
    @Length(max=255, message = "煤炭图片路径不能超过255个字符")
    private String coalpic3;                //煤炭图片3
    @Length(max=255, message = "煤炭图片路径不能超过255个字符")
    private String coalpic4;                //煤炭图片4
    @Length(max=255, message = "煤炭图片路径不能超过255个字符")
    private String coalpic5;                //煤炭图片5
    @Length(max=255, message = "煤炭图片路径不能超过255个字符")
    private String coalpic6;                //煤炭图片6
    @Length(max=200, message = "促销活动备注不能超过200个字符")
    private String promotionremarks;

    public boolean isLinktype() {
        return linktype;
    }

    public void setLinktype(boolean linktype) {
        this.linktype = linktype;
    }

    public String getLinkmanname() {
        return linkmanname;
    }

    public void setLinkmanname(String linkmanname) {
        this.linkmanname = EmojiFilter.filterEmoji(linkmanname);;
    }

    public String getLinkmanphone() {
        return linkmanphone;
    }

    public void setLinkmanphone(String linkmanphone) {
        this.linkmanphone = linkmanphone;
    }

    public SellInfo() {
    }

    public  SellInfo(Integer NCV, Integer NCV02, BigDecimal ADS, BigDecimal ADS02, BigDecimal RS, BigDecimal RS02,
                    BigDecimal TM, BigDecimal TM02, BigDecimal IM, BigDecimal IM02, BigDecimal ADV, BigDecimal ADV02,
                    BigDecimal RV, BigDecimal RV02, BigDecimal ASH, BigDecimal ASH02, Integer AFT, Integer HGI,
                    Integer GV, Integer GV02, Integer YV, Integer YV02, Integer FC, Integer FC02, Integer PS, String PSName,
                    Integer CRC, Integer CRC02, EnumSellInfo status, String pname, BigDecimal ykj, String seller, int sellerid,
                    Integer regionId,Integer provinceId,Integer portId, String deliveryregion, String deliveryprovince,
                    String deliveryplace, String otherharbour, String deliverymode, LocalDate deliverytime1, LocalDate deliverytime2,
                    int supplyquantity, int availquantity, String inspectorg, String otherinspectorg,
                    String originplace, int paymode, BigDecimal payperiod, String releaseremarks,
                    int clienttype, int type, String brandname, int tax, int accountsmethod, String accountsmethodname,
                    int logistics, int finance, int promotion, String promotionremarks, String chemicalexam1, String chemicalexam2,
                    String chemicalexam3, String coalpic1, String coalpic2, String coalpic3, String coalpic4, String coalpic5, String coalpic6) {
        super(NCV, NCV02, ADS, ADS02, RS, RS02, TM, TM02, IM, IM02, ADV, ADV02, RV, RV02, AFT, ASH, ASH02, HGI, GV, GV02, YV, YV02, FC, FC02, PS, PSName, CRC, CRC02);
        this.status = status;
        this.pname = pname;
        this.ykj = ykj;
        this.seller = seller;
        this.sellerid = sellerid;
        this.regionId = regionId;
        this.provinceId = provinceId;
        this.portId = portId;
        this.deliveryregion = deliveryregion;
        this.deliveryprovince = deliveryprovince;
        this.deliveryplace = deliveryplace;
        this.otherharbour = otherharbour;
        this.deliverymode = deliverymode;
        this.deliverytime1 = deliverytime1;
        this.deliverytime2 = deliverytime2;
        this.supplyquantity = supplyquantity;
        this.availquantity = availquantity;
        this.inspectorg = inspectorg;
        this.otherinspectorg = otherinspectorg;
        this.originplace = originplace;
        this.paymode = paymode;
        this.payperiod = payperiod;
        this.releaseremarks = releaseremarks;
        this.clienttype = clienttype;
        this.type = type;
        this.brandname = brandname;
        this.tax = tax;
        this.accountsmethod = accountsmethod;
        this.accountsmethodname = accountsmethodname;
        this.logistics = logistics;
        this.finance = finance;
        this.promotion = promotion;
        this.promotionremarks = promotionremarks;
        this.chemicalexam1 = chemicalexam1;
        this.chemicalexam2 = chemicalexam2;
        this.chemicalexam3 = chemicalexam3;
        this.coalpic1 = coalpic1;
        this.coalpic2 = coalpic2;
        this.coalpic3 = coalpic3;
        this.coalpic4 = coalpic4;
        this.coalpic5 = coalpic5;
        this.coalpic6 = coalpic6;
    }

    /**
     * admin 商城产品 模块使用
     */
    public SellInfo(Integer NCV, Integer NCV02, BigDecimal ADS, BigDecimal ADS02, BigDecimal RS, BigDecimal RS02, BigDecimal TM,
                    BigDecimal TM02, BigDecimal IM, BigDecimal IM02, BigDecimal ADV, BigDecimal ADV02, BigDecimal RV, BigDecimal RV02,
                    BigDecimal ASH, BigDecimal ASH02, Integer AFT, Integer HGI, Integer GV, Integer GV02, Integer YV, Integer YV02,
                    Integer FC, Integer FC02, Integer PS, String PSName, Integer CRC, Integer CRC02, EnumSellInfo status, String pname,
                    BigDecimal ykj, String seller, int sellerid, int regionId, int provinceId, int portId, String deliveryregion,
                    String deliveryprovince, String deliveryplace, String otherharbour, String deliverymode, LocalDate deliverytime1,
                    LocalDate deliverytime2, int supplyquantity, int availquantity, String inspectorg, String otherinspectorg,
                    String originplace, int paymode, BigDecimal payperiod, String releaseremarks,
                    int clienttype, int type, String brandname, int tax, int accountsmethod, String accountsmethodname,
                    int logistics, int finance, int promotion, String promotionremarks, String chemicalexam1, String chemicalexam2,
                    String chemicalexam3, String coalpic1, String coalpic2, String coalpic3,
                    String coalpic4, String coalpic5, String coalpic6, LocalDateTime verifytime, int traderid, String dealername,
                    String dealerphone, String producttype, Integer shopid, String shoppname, String remarks)  {
        this(NCV, NCV02, ADS, ADS02, RS, RS02, TM, TM02, IM, IM02, ADV, ADV02, RV, RV02, ASH, ASH02, AFT, HGI, GV, GV02, YV, YV02,
                FC, FC02, PS, PSName, CRC, CRC02, status, pname, ykj, seller, sellerid, regionId, provinceId, portId,  deliveryregion,
                deliveryprovince, deliveryplace, otherharbour, deliverymode, deliverytime1, deliverytime2, supplyquantity, availquantity,
                inspectorg, otherinspectorg, originplace, paymode, payperiod, releaseremarks, clienttype, type, brandname,
                tax, accountsmethod, accountsmethodname, logistics, finance, promotion, promotionremarks, chemicalexam1, chemicalexam2,
                chemicalexam3, coalpic1, coalpic2, coalpic3, coalpic4, coalpic5, coalpic6);
        this.verifytime = verifytime;
        this.traderid = traderid;
        this.dealername = dealername;
        this.dealerphone = dealerphone;
        this.producttype = producttype;
        this.shopid = shopid;
        this.shoppname = shoppname;
        this.remarks = remarks;
    }

    /**
     * 网站 发布,修改供应使用
     */
    public SellInfo(Integer NCV, Integer NCV02, BigDecimal ADS, BigDecimal ADS02, BigDecimal RS, BigDecimal RS02, BigDecimal TM,
                    BigDecimal TM02, BigDecimal IM, BigDecimal IM02, BigDecimal ADV, BigDecimal ADV02, BigDecimal RV, BigDecimal RV02,
                    BigDecimal ASH, BigDecimal ASH02, Integer AFT, Integer HGI, Integer GV, Integer GV02, Integer YV, Integer YV02,
                    Integer FC, Integer FC02, Integer PS, String PSName, Integer CRC, Integer CRC02, EnumSellInfo status,  String pname,
                    BigDecimal ykj, String seller, int sellerid, Integer regionId, Integer provinceId, Integer portId, String deliveryregion,
                    String deliveryprovince, String deliveryplace, String otherharbour, String deliverymode, LocalDate deliverytime1,
                    LocalDate deliverytime2, int supplyquantity, int availquantity, String inspectorg, String otherinspectorg,
                    String originplace, int paymode, BigDecimal payperiod, String releaseremarks, int clienttype, int type,
                    String brandname, int tax, int accountsmethod, String accountsmethodname, int logistics, int finance,
                    int promotion, String promotionremarks, String chemicalexam1, String chemicalexam2, String chemicalexam3,
                    String coalpic1, String coalpic2, String coalpic3, String coalpic4, String coalpic5, String coalpic6,
                    int editnum, int parentid,  boolean linktype, String linkmanname, String linkmanphone, int id, int version) {
        this(NCV, NCV02, ADS, ADS02, RS, RS02, TM, TM02, IM, IM02, ADV, ADV02, RV, RV02, ASH, ASH02, AFT, HGI, GV, GV02, YV, YV02,
                FC, FC02, PS, PSName, CRC, CRC02, status, pname, ykj, seller, sellerid, regionId, provinceId, portId, deliveryregion,
                deliveryprovince, deliveryplace, otherharbour, deliverymode, deliverytime1, deliverytime2, supplyquantity, availquantity,
                inspectorg, otherinspectorg, originplace, paymode, payperiod, releaseremarks, clienttype,
                type, brandname, tax, accountsmethod, accountsmethodname, logistics, finance, promotion, promotionremarks, chemicalexam1,
                chemicalexam2, chemicalexam3, coalpic1, coalpic2, coalpic3, coalpic4, coalpic5, coalpic6);
        this.editnum = editnum;
        this.parentid = parentid;
        this.linktype = linktype;
        this.linkmanname = linkmanname;
        this.linkmanphone = linkmanphone;
        this.id = id;
        this.version = version;
    }

    public List<PriceLadder> getPricelist() {
        return pricelist;
    }

    public void setPricelist(List<PriceLadder> pricelist) {
        this.pricelist = pricelist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public EnumSellInfo getStatus() {
        return status;
    }

    public void setStatus(EnumSellInfo status) {
        this.status = status;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public BigDecimal getYkj() {
        return ykj;
    }

    public void setYkj(BigDecimal ykj) {
        this.ykj = ykj;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getDeliveryplace() {
        return deliveryplace;
    }

    public String getDeliveryregion() {
        return deliveryregion;
    }

    public void setDeliveryregion(String deliveryregion) {
        this.deliveryregion = deliveryregion;
    }

    public String getDeliveryprovince() {
        return deliveryprovince;
    }

    public void setDeliveryprovince(String deliveryprovince) {
        this.deliveryprovince = deliveryprovince;
    }

    public void setDeliveryplace(String deliveryplace) {
        this.deliveryplace = deliveryplace;
    }

    public String getOtherharbour() {
        return otherharbour;
    }

    public void setOtherharbour(String otherharbour) {
        this.otherharbour = EmojiFilter.filterEmoji(otherharbour);;
    }

    public String getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(String deliverymode) {
        this.deliverymode = deliverymode;
    }

    public LocalDate getDeliverytime1() {
        return deliverytime1;
    }

    public void setDeliverytime1(LocalDate deliverytime1) {
        this.deliverytime1 = deliverytime1;
    }

    public LocalDate getDeliverytime2() {
        return deliverytime2;
    }

    public void setDeliverytime2(LocalDate deliverytime2) {
        this.deliverytime2 = deliverytime2;
    }

    public int getSupplyquantity() {
        return supplyquantity;
    }

    public void setSupplyquantity(int supplyquantity) {
        this.supplyquantity = supplyquantity;
    }

    public int getSoldquantity() {
        return soldquantity;
    }

    public void setSoldquantity(int soldquantity) {
        this.soldquantity = soldquantity;
    }

    public int getAvailquantity() {
        return availquantity;
    }

    public void setAvailquantity(int availquantity) {
        this.availquantity = availquantity;
    }

    public String getInspectorg() {
        return inspectorg;
    }

    public void setInspectorg(String inspectorg) {
        this.inspectorg = inspectorg;
    }

    public String getOtherinspectorg() {
        return otherinspectorg;
    }

    public void setOtherinspectorg(String otherinspectorg) {
        this.otherinspectorg = EmojiFilter.filterEmoji(otherinspectorg);;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public int getSellerid() {
        return sellerid;
    }

    public void setSellerid(int sellerid) {
        this.sellerid = sellerid;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public BigDecimal getJtjlast() {
        return jtjlast;
    }

    public void setJtjlast(BigDecimal jtjlast) {
        this.jtjlast = jtjlast;
    }

    public LocalDate getCreatedate() {
        return createdate;
    }

    public void setCreatedate(LocalDate createdate) {
        this.createdate = createdate;
    }

    public String getDealerid() {
        return dealerid;
    }

    public void setDealerid(String dealerid) {
        this.dealerid = dealerid;
    }

    public String getDealername() {
        return dealername;
    }

    public void setDealername(String dealername) {
        this.dealername = dealername;
    }

    public String getDealerphone() {
        return dealerphone;
    }

    public void setDealerphone(String dealerphone) {
        this.dealerphone = dealerphone;
    }

    public LocalDateTime getVerifytime() {
        return verifytime;
    }

    public void setVerifytime(LocalDateTime verifytime) {
        this.verifytime = verifytime;
    }

    public String getProducttype() {
        return producttype;
    }

    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getPortId() {
        return portId;
    }

    public void setPortId(Integer portId) {
        this.portId = portId;
    }

    public int getViewtimes() {
        return viewtimes;
    }

    public void setViewtimes(int viewtimes) {
        this.viewtimes = viewtimes;

    }

    public String getReleaseremarks() {
        return releaseremarks;
    }

    public void setReleaseremarks(String releaseremarks) {
        this.releaseremarks = EmojiFilter.filterEmoji(releaseremarks);
    }

    public int getPaymode() {
        return paymode;
    }

    public void setPaymode(int paymode) {
        this.paymode = paymode;
    }

    public BigDecimal getPayperiod() {
        return payperiod;
    }

    public void setPayperiod(BigDecimal payperiod) {
        this.payperiod = payperiod;
    }

    public String getOriginplace() {
        return originplace;
    }

    public void setOriginplace(String originplace) {
        this.originplace = EmojiFilter.filterEmoji(originplace);;
    }


    public int getTraderid() {
        return traderid;
    }

    public void setTraderid(int traderid) {
        this.traderid = traderid;
    }

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public int getEditnum() {
        return editnum;
    }

    public void setEditnum(int editnum) {
        this.editnum = editnum;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;

    }

    public String getShoppname() {
        return shoppname;
    }

    public void setShoppname(String shoppname) {
        this.shoppname = shoppname;
    }

    public String getChemicalexam1() {
        return chemicalexam1;
    }

    public void setChemicalexam1(String chemicalexam1) {
        this.chemicalexam1 = chemicalexam1;
    }

    public String getChemicalexam2() {
        return chemicalexam2;
    }

    public void setChemicalexam2(String chemicalexam2) {
        this.chemicalexam2 = chemicalexam2;
    }

    public String getChemicalexam3() {
        return chemicalexam3;
    }

    public void setChemicalexam3(String chemicalexam3) {
        this.chemicalexam3 = chemicalexam3;
    }

    public String getBrandname() {
        return brandname;
    }

    public void setBrandname(String brandname) {
        this.brandname = brandname;
    }

    public int getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public int getAccountsmethod() {
        return accountsmethod;
    }

    public void setAccountsmethod(int accountsmethod) {
        this.accountsmethod = accountsmethod;
    }

    public String getAccountsmethodname() {
        return accountsmethodname;
    }

    public void setAccountsmethodname(String accountsmethodname) {
        this.accountsmethodname = accountsmethodname;
    }

    public int getLogistics() {
        return logistics;
    }

    public void setLogistics(int logistics) {
        this.logistics = logistics;
    }

    public int getFinance() {
        return finance;
    }

    public void setFinance(int finance) {
        this.finance = finance;
    }

    public int getPromotion() {
        return promotion;
    }

    public void setPromotion(int promotion) {
        this.promotion = promotion;
    }

    public String getCoalpic1() {
        return coalpic1;
    }

    public void setCoalpic1(String coalpic1) {
        this.coalpic1 = coalpic1;
    }

    public String getCoalpic2() {
        return coalpic2;
    }

    public void setCoalpic2(String coalpic2) {
        this.coalpic2 = coalpic2;
    }

    public String getCoalpic3() {
        return coalpic3;
    }

    public void setCoalpic3(String coalpic3) {
        this.coalpic3 = coalpic3;
    }

    public String getCoalpic4() {
        return coalpic4;
    }

    public void setCoalpic4(String coalpic4) {
        this.coalpic4 = coalpic4;
    }

    public String getCoalpic5() {
        return coalpic5;
    }

    public void setCoalpic5(String coalpic5) {
        this.coalpic5 = coalpic5;
    }

    public String getCoalpic6() {
        return coalpic6;
    }

    public void setCoalpic6(String coalpic6) {
        this.coalpic6 = coalpic6;
    }

    public String getPromotionremarks() {
        return promotionremarks;
    }

    public void setPromotionremarks(String promotionremarks) {
        this.promotionremarks = promotionremarks;
    }

    /**
     * 排序类型 *
     */
    public enum OrderType {


        /**
         * 价格升序 *
         */
        priceAsc,

        /**
         * 价格降序 *
         */
        priceDesc,

        /**
         * 创建时间升序 *
         */
        lastupdatetimeAsc,

        /**
         * 创建时间降序 *
         */
        lastupdatetimeDesc,

        /**
         * 销量升序
         */
        salesAsc,

        /**
         * 销量降序
         */
        salesDesc,

        /**
         * 店铺名称
         */
        shopNameAsc,

    }

    public int getClienttype() {
        return clienttype;
    }

    public void setClienttype(int clienttype) {
        this.clienttype = clienttype;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Integer getShopid() {
        return shopid;
    }

    public void setShopid(Integer shopid) {
        this.shopid = shopid;
    }

    public String getAtype() {
        return atype;
    }

    public void setAtype(String atype) {
        this.atype = atype;
    }
}







