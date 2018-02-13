package kitt.core.entity;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 16/2/29.
 */
public class Demand extends CoalBaseData implements Serializable {
    //需求表
    private int id;
    private int userid;                     //用户id
    private String demandcustomer;          //需求方
    private String demandcode;              //需求编号
    private String deliveryplace;           //提货地点
    @Min(value = 50,message = "需求吨数不能小于50")
    private int demandamount;               //需求数量
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private LocalDate deliverydate;         //提货时间
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private LocalDate deliverydatestart;    //自提提货时间开始
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private LocalDate deliverydateend;      //自提提货时间截至
    private LocalDateTime releasetime;      //发布时间
    /*private int floatdays;                  //浮动天数(已废弃)*/
    private String deliverymode;            //提货方式
    /*private int unitprice;                  //单价(已废弃)*/
    private String inspectionagency;        //检验机构
    private String checkstatus;             //审核状态
    private String tradestatus;             //交易状态
    private int quotenum;                   //报价人数
    private boolean isdelete;               //单据状态,是否删除 0未删除,1已删除
    private int purchasednum;               //已采购量
    private int residualdemand;             //剩余需求量
    private String deliverydistrict;        //提货省份片区
    private String deliveryprovince;        //提货省份
    /* private boolean isshowcompany;          //是否显示公司信息 1显示,0不显示(废弃)*/
    //用于需求发布的保存
    private boolean releasestatus;          //需求发布状态 0未发布,1已发布
    private String comment;                  //需求审核备注
    @Length(max = 20,message = "详细检验机构字段长度应该{min}-{max}之间")
    private String otherorg;                 //其它检验机构
    @Length(max = 20,message = "详细港口字段长度应该{min}-{max}之间")
    private String otherplace;               //其它提货地点
    @NotNull(message = "报价截止日不能为空")
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private LocalDate quoteenddate;          //报价截止时间
    private LocalDate noshowdate;            //报价截止7天后
    private String coaltype;                 //煤种
    private String tradercode;                 //交易员编号
    private LocalDateTime checktime;         //审核时间
    private String tradername;               //交易员姓名
    private String traderphone;              //交易员手机号
    private Integer regionId;
    private Integer provinceId;
    private Integer portId;
    private int traderid;
    private int viewtimes;                   //浏览人次
    @Length(max = 200,message = "备注字段应该在200位")
    private String releasecomment;           //发布备注
    private int clienttype;                  //客户端类型
    private String paymode;                  //付款方式
    private String transportmode;            //运输方式
    private boolean showYM;                  //是否显示在易煤网
    private int sourceId;                    //来源Id, 1:客户发布,
    private String infoSource;               //信息来源
    private boolean logistics;               //是否需要易煤网提供物流服务, 0:处理老数据, 1:需要, 2:不需要
    private boolean finance;                 //是否需要易煤网提供金融服务, 0:处理老数据, 1:需要, 2:不需要
    private boolean promotion;               //是否参加促销活动, 0: 处理老数据, 1:参加促销, 2:不参加促销

    public  Demand(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getDemandcustomer() {
        return demandcustomer;
    }

    public void setDemandcustomer(String demandcustomer) {
        this.demandcustomer = demandcustomer;
    }

    public String getDemandcode() {
        return demandcode;
    }

    public void setDemandcode(String demandcode) {
        this.demandcode = demandcode;
    }

    public String getDeliveryplace() {
        return deliveryplace;
    }

    public void setDeliveryplace(String deliveryplace) {
        this.deliveryplace = deliveryplace;
    }

    public int getDemandamount() {
        return demandamount;
    }

    public void setDemandamount(int demandamount) {
        this.demandamount = demandamount;
    }

    public String getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(String deliverymode) {
        this.deliverymode = deliverymode;
    }

    public String getInspectionagency() {
        return inspectionagency;
    }

    public void setInspectionagency(String inspectionagency) {
        this.inspectionagency = inspectionagency;
    }

    public String getCheckstatus() {
        return checkstatus;
    }

    public void setCheckstatus(String checkstatus) {
        this.checkstatus = checkstatus;
    }

    public String getTradestatus() {
        return tradestatus;
    }

    public void setTradestatus(String tradestatus) {
        this.tradestatus = tradestatus;
    }

    public int getQuotenum() {
        return quotenum;
    }

    public void setQuotenum(int quotenum) {
        this.quotenum = quotenum;
    }

    public boolean isIsdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public int getPurchasednum() {
        return purchasednum;
    }

    public void setPurchasednum(int purchasednum) {
        this.purchasednum = purchasednum;
    }

    public int getResidualdemand() {
        return residualdemand;
    }

    public void setResidualdemand(int residualdemand) {
        this.residualdemand = residualdemand;
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

    public boolean isReleasestatus() {
        return releasestatus;
    }

    public void setReleasestatus(boolean releasestatus) {
        this.releasestatus = releasestatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getDeliverydate() {
        return deliverydate;
    }

    public void setDeliverydate(LocalDate deliverydate) {
        this.deliverydate = deliverydate;
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

    public LocalDateTime getReleasetime() {
        return releasetime;
    }

    public void setReleasetime(LocalDateTime releasetime) {
        this.releasetime = releasetime;
    }

    public String getOtherorg() {
        return otherorg;
    }

    public void setOtherorg(String otherorg) {
        this.otherorg =otherorg;
    }

    public String getOtherplace() {
        return otherplace;
    }

    public void setOtherplace(String otherplace) {
        this.otherplace = otherplace;;
    }

    public LocalDate getQuoteenddate() {
        return quoteenddate;
    }

    public void setQuoteenddate(LocalDate quoteenddate) {
        this.quoteenddate = quoteenddate;
    }

    public LocalDate getNoshowdate() {
        return noshowdate;
    }

    public void setNoshowdate(LocalDate noshowdate) {
        this.noshowdate = noshowdate;
    }

    public String getCoaltype() {
        return coaltype;
    }

    public void setCoaltype(String coaltype) {
        this.coaltype = coaltype;
    }

    public String getTradercode() {
        return tradercode;
    }

    public void setTradercode(String tradercode) {
        this.tradercode = tradercode;
    }

    public LocalDateTime getChecktime() {
        return checktime;
    }

    public void setChecktime(LocalDateTime checktime) {
        this.checktime = checktime;
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

    public String getReleasecomment() {
        return releasecomment;
    }

    public void setReleasecomment(String releasecomment) {
        this.releasecomment = releasecomment;
    }

    public int getTraderid() {
        return traderid;
    }

    public void setTraderid(int traderid) {
        this.traderid = traderid;
    }

    public int getClienttype() {
        return clienttype;
    }

    public void setClienttype(int clienttype) {
        this.clienttype = clienttype;
    }

    public String getPaymode() {
        return paymode;
    }

    public void setPaymode(String paymode) {
        this.paymode = paymode;
    }

    public String getTransportmode() {
        return transportmode;
    }

    public void setTransportmode(String transportmode) {
        this.transportmode = transportmode;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public boolean isShowYM() {
        return showYM;
    }

    public void setShowYM(boolean showYM) {
        this.showYM = showYM;
    }

    public String getInfoSource() {
        return infoSource;
    }

    public void setInfoSource(String infoSource) {
        this.infoSource = infoSource;
    }

    public boolean isLogistics() {
        return logistics;
    }

    public void setLogistics(boolean logistics) {
        this.logistics = logistics;
    }

    public boolean isFinance() {
        return finance;
    }

    public void setFinance(boolean finance) {
        this.finance = finance;
    }

    public boolean isPromotion() {
        return promotion;
    }

    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }
}
