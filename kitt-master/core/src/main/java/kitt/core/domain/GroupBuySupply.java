package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 团购供货信息
 * Created by zhangbolun on 15/1/19.
 */
public class GroupBuySupply extends CoalBaseData implements Serializable {
    private int id;                          //pk_id
    private int providerinfoid;              //团购供应商信息id
    private String coaltype;                 //煤种
    private String port;                     //港口
    private String groupbuysupplycode;       //团购供应编号
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime groupbuybegindate; //团购开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime groupbuyenddate;   //团购结束时间
    private BigDecimal marketprice;          //市场价格
    private BigDecimal groupbuyprice;        //团购价格
    private String deliveryplace;            //交货地点
    private LocalDate deliverytime;          //交货时间,提货时间
    private String deliverymode;             //交货方式
    private String storageplace;             //堆位
    private int supplyamount;                //供应数量
    private int selledamount;                //已销售量
    private String  inspectionagency;        //检验机构
    private int surplusamount;               //可销售库存
    private int minimumamount;               //起订量
    private LocalDateTime lastupdatetime;    //最后一次更新时间
    private LocalDateTime createtime;        //信息创建时间
    private String comment;                  //团购规则
    private String status;                   //状态
    private String deliverydistrict;         //提货省份片区
    private String deliveryprovince;         //提货省份
    private int groupbuyordercount;          //团购订单数量
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deliverydatestart;     //提货时间开始
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deliverydateend;       //提货时间截至
    private String otherorg;                 //其他机构
    private String photopath;                //图片路径

    private int viewtimes;                   //浏览量

    private String originplace;              //产地
    private int traderid;                    //交易员id
    private String tradername;               //交易员姓名
    private String traderphone;              //交易员电话
    private int regionId;                    //区域片区id
    private int provinceId;                  //省份id
    private int portId;                      //港口id
    private BigDecimal succeedpercentmin;    //成功团购条件最小百分比
    private BigDecimal succeedpercentmax;    //成功团购条件最大百分比


    public GroupBuySupply(){}

    public GroupBuySupply(BigDecimal succeedpercentmin,BigDecimal succeedpercentmax,String photopath,String otherorg,LocalDate deliverydateend,LocalDate deliverydatestart,int groupbuyordercount,String deliveryprovince,String deliverydistrict,String comment,String groupbuysupplycode,String status,int id, int providerinfoid, String coaltype, String port, LocalDateTime groupbuybegindate, LocalDateTime groupbuyenddate, BigDecimal marketprice, BigDecimal groupbuyprice, String deliveryplace, LocalDate deliverytime, String deliverymode, String storageplace, int supplyamount, int selledamount, String inspectionagency, int surplusamount, int minimumamount,LocalDateTime lastupdatetime,LocalDateTime createtime, int viewtimes, String originplace) {
        this.id = id;
        this.providerinfoid = providerinfoid;
        this.coaltype = coaltype;
        this.port = port;
        this.groupbuybegindate = groupbuybegindate;
        this.groupbuyenddate = groupbuyenddate;
        this.marketprice = marketprice;
        this.groupbuyprice = groupbuyprice;
        this.deliveryplace = deliveryplace;
        this.deliverytime = deliverytime;
        this.deliverymode = deliverymode;
        this.storageplace = storageplace;
        this.supplyamount = supplyamount;
        this.selledamount = selledamount;
        this.inspectionagency = inspectionagency;
        this.surplusamount = surplusamount;
        this.minimumamount = minimumamount;
        this.lastupdatetime=lastupdatetime;
        this.createtime=createtime;
        this.status=status;
        this.groupbuysupplycode=groupbuysupplycode;
        this.deliverydistrict=deliverydistrict;
        this.deliveryprovince=deliveryprovince;
        this.groupbuyordercount=groupbuyordercount;
        this.comment=comment;
        this.deliverydatestart=deliverydatestart;
        this.deliverydateend=deliverydateend;
        this.otherorg=otherorg;
        this.photopath=photopath;
        this.viewtimes = viewtimes;
        this.originplace = originplace;
        this.succeedpercentmin=succeedpercentmin;
        this.succeedpercentmax=succeedpercentmax;
    }

    public GroupBuySupply(BigDecimal succeedpercentmin,BigDecimal succeedpercentmax,String photopath,String otherorg,LocalDate deliverydateend,LocalDate deliverydatestart,int groupbuyordercount,String deliveryprovince,String deliverydistrict,String comment,String groupbuysupplycode,String status,int NCV, BigDecimal ADS, BigDecimal RS, BigDecimal TM, BigDecimal IM, BigDecimal ADV, BigDecimal RV, int AFT, BigDecimal ASH, int HGI, int id, int providerinfoid, String coaltype, String port, LocalDateTime groupbuybegindate, LocalDateTime groupbuyenddate, BigDecimal marketprice, BigDecimal groupbuyprice, String deliveryplace, LocalDate deliverytime, String deliverymode, String storageplace, int supplyamount, int selledamount, String inspectionagency, int surplusamount, int minimumamount,LocalDateTime lastupdatetime,LocalDateTime createtime,int viewtimes, String originplace) {
        super(NCV, ADS, RS, TM, IM, ADV, RV, AFT, ASH, HGI);
        this.id = id;
        this.providerinfoid = providerinfoid;
        this.coaltype = coaltype;
        this.port = port;
        this.groupbuybegindate = groupbuybegindate;
        this.groupbuyenddate = groupbuyenddate;
        this.marketprice = marketprice;
        this.groupbuyprice = groupbuyprice;
        this.deliveryplace = deliveryplace;
        this.deliverytime = deliverytime;
        this.deliverymode = deliverymode;
        this.storageplace = storageplace;
        this.supplyamount = supplyamount;
        this.selledamount = selledamount;
        this.inspectionagency = inspectionagency;
        this.surplusamount = surplusamount;
        this.minimumamount = minimumamount;
        this.createtime=createtime;
        this.lastupdatetime=lastupdatetime;
        this.status=status;
        this.groupbuysupplycode=groupbuysupplycode;
        this.deliverydistrict=deliverydistrict;
        this.deliveryprovince=deliveryprovince;
        this.groupbuyordercount=groupbuyordercount;
        this.comment=comment;
        this.deliverydatestart=deliverydatestart;
        this.deliverydateend=deliverydateend;
        this.otherorg=otherorg;
        this.photopath=photopath;
        this.viewtimes = viewtimes;
        this.originplace = originplace;
        this.succeedpercentmin=succeedpercentmin;
        this.succeedpercentmax=succeedpercentmax;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProviderinfoid() {
        return providerinfoid;
    }

    public void setProviderinfoid(int providerinfoid) {
        this.providerinfoid = providerinfoid;
    }

    public String getCoaltype() {
        return coaltype;
    }

    public void setCoaltype(String coaltype) {
        this.coaltype = coaltype;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public LocalDateTime getGroupbuybegindate() {
        return groupbuybegindate;
    }

    public void setGroupbuybegindate(LocalDateTime groupbuybegindate) {
        this.groupbuybegindate = groupbuybegindate;
    }

    public LocalDateTime getGroupbuyenddate() {
        return groupbuyenddate;
    }

    public void setGroupbuyenddate(LocalDateTime groupbuyenddate) {
        this.groupbuyenddate = groupbuyenddate;
    }

    public BigDecimal getMarketprice() {
        return marketprice;
    }

    public void setMarketprice(BigDecimal marketprice) {
        this.marketprice = marketprice;
    }

    public BigDecimal getGroupbuyprice() {
        return groupbuyprice;
    }

    public void setGroupbuyprice(BigDecimal groupbuyprice) {
        this.groupbuyprice = groupbuyprice;
    }

    public String getDeliveryplace() {
        return deliveryplace;
    }

    public void setDeliveryplace(String deliveryplace) {
        this.deliveryplace = deliveryplace;
    }

    public LocalDate getDeliverytime() {
        return deliverytime;
    }

    public void setDeliverytime(LocalDate deliverytime) {
        this.deliverytime = deliverytime;
    }

    public String getStorageplace() {
        return storageplace;
    }

    public void setStorageplace(String storageplace) {
        this.storageplace = storageplace;
    }

    public int getSupplyamount() {
        return supplyamount;
    }

    public void setSupplyamount(int supplyamount) {
        this.supplyamount = supplyamount;
    }

    public int getSelledamount() {
        return selledamount;
    }

    public void setSelledamount(int selledamount) {
        this.selledamount = selledamount;
    }

    public String getInspectionagency() {
        return inspectionagency;
    }

    public void setInspectionagency(String inspectionagency) {
        this.inspectionagency = inspectionagency;
    }

    public int getSurplusamount() {
        return surplusamount;
    }

    public void setSurplusamount(int surplusamount) {
        this.surplusamount = surplusamount;
    }

    public int getMinimumamount() {
        return minimumamount;
    }

    public void setMinimumamount(int minimumamount) {
        this.minimumamount = minimumamount;
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

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public String getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(String deliverymode) {
        this.deliverymode = deliverymode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGroupbuysupplycode() {
        return groupbuysupplycode;
    }

    public void setGroupbuysupplycode(String groupbuysupplycode) {
        this.groupbuysupplycode = groupbuysupplycode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDeliverydistrict() {
        return deliverydistrict;
    }

    public void setDeliverydistrict(String deliverydistrict) {
        this.deliverydistrict = deliverydistrict;
    }

    public String getDeliveryprovince() {
        return deliveryprovince;
    }

    public void setDeliveryprovince(String deliveryprovince) {
        this.deliveryprovince = deliveryprovince;
    }

    public int getGroupbuyordercount() {
        return groupbuyordercount;
    }

    public void setGroupbuyordercount(int groupbuyordercount) {
        this.groupbuyordercount = groupbuyordercount;
    }

    public LocalDate getDeliverydatestart() {
        return deliverydatestart;
    }

    public void setDeliverydatestart(LocalDate deliverydatestart) {
        this.deliverydatestart = deliverydatestart;
    }

    public LocalDate getDeliverydateend() {
        return deliverydateend;
    }

    public void setDeliverydateend(LocalDate deliverydateend) {
        this.deliverydateend = deliverydateend;
    }

    public String getOtherorg() {
        return otherorg;
    }

    public void setOtherorg(String otherorg) {
        this.otherorg = otherorg;
    }

    public String getPhotopath() {
        return photopath;
    }

    public void setPhotopath(String photopath) {
        this.photopath = photopath;
    }

    public int getViewtimes() {
        return viewtimes;
    }

    public void setViewtimes(int viewtimes) {
        this.viewtimes = viewtimes;
    }

    public String getOriginplace() {
        return originplace;
    }

    public void setOriginplace(String originplace) {
        this.originplace = originplace;
    }

    public int getTraderid() {
        return traderid;
    }

    public void setTraderid(int traderid) {
        this.traderid = traderid;
    }

    public String getTradername() {
        return tradername;
    }

    public void setTradername(String tradername) {
        this.tradername = tradername;
    }

    public String getTraderphone() {
        return traderphone;
    }

    public void setTraderphone(String traderphone) {
        this.traderphone = traderphone;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getPortId() {
        return portId;
    }

    public void setPortId(int portId) {
        this.portId = portId;
    }

    public BigDecimal getSucceedpercentmin() {
        return succeedpercentmin;
    }

    public void setSucceedpercentmin(BigDecimal succeedpercentmin) {
        this.succeedpercentmin = succeedpercentmin;
    }

    public BigDecimal getSucceedpercentmax() {
        return succeedpercentmax;
    }

    public void setSucceedpercentmax(BigDecimal succeedpercentmax) {
        this.succeedpercentmax = succeedpercentmax;
    }

    @Override
    public String toString() {
        return "GroupBuySupply{" +
                "id=" + id +
                ", providerinfoid=" + providerinfoid +
                ", coaltype='" + coaltype + '\'' +
                ", port='" + port + '\'' +
                ", groupbuysupplycode='" + groupbuysupplycode + '\'' +
                ", groupbuybegindate=" + groupbuybegindate +
                ", groupbuyenddate=" + groupbuyenddate +
                ", marketprice=" + marketprice +
                ", groupbuyprice=" + groupbuyprice +
                ", deliveryplace='" + deliveryplace + '\'' +
                ", deliverytime=" + deliverytime +
                ", deliverymode='" + deliverymode + '\'' +
                ", storageplace='" + storageplace + '\'' +
                ", supplyamount=" + supplyamount +
                ", selledamount=" + selledamount +
                ", inspectionagency='" + inspectionagency + '\'' +
                ", surplusamount=" + surplusamount +
                ", minimumamount=" + minimumamount +
                ", lastupdatetime=" + lastupdatetime +
                ", createtime=" + createtime +
                ", comment='" + comment + '\'' +
                ", status='" + status + '\'' +
                ", deliverydistrict='" + deliverydistrict + '\'' +
                ", deliveryprovince='" + deliveryprovince + '\'' +
                ", groupbuyordercount=" + groupbuyordercount +
                ", deliverydatestart=" + deliverydatestart +
                ", deliverydateend=" + deliverydateend +
                ", otherorg='" + otherorg + '\'' +
                ", photopath='" + photopath + '\'' +
                ", viewtimes=" + viewtimes +
                ", originplace='" + originplace + '\'' +
                ", traderid=" + traderid +
                ", tradername='" + tradername + '\'' +
                ", traderphone='" + traderphone + '\'' +
                ", regionId=" + regionId +
                ", provinceId=" + provinceId +
                ", portId=" + portId +
                ", succeedpercentmin=" + succeedpercentmin +
                ", succeedpercentmax=" + succeedpercentmax +
                '}';
    }
}
