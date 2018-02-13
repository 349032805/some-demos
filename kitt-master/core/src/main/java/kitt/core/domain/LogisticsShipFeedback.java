package kitt.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by zhangbolun on 16/1/8.
 */
public class LogisticsShipFeedback {
    private int  id;
    private String souceId;             //易煤网流水
    //private String ordercode;           //运单号
    private String transportcompany;    //运输公司/船东
    private String carrier;             //承运人
    private int DWT;                    //载重
    private String paytype;             //付款方式
    private String settlementtype;      //结算方式
    private String needbill;           //是否需要发票 0:否 1:是
    private BigDecimal transportprices; //单价
    private BigDecimal totalprice;      //总价
    private BigDecimal otherprice;      //其他费用
    private BigDecimal detentioncharge; //滞留费用
    private int detentionday;           //滞留天数
    private String shipname;            //船名
    private String emptyport;           //空载港
    private String emptydate;        //空载日期
    private int venderid;               //物流供应商id
    private String status;              //状态
    private LocalDateTime createtime;   //创建日期
    private LocalDateTime lastupdatetime;//数据更新日期
    private boolean isdelete;            //是否删除
    private String carrierphone;         //承运人联系方式
    private String ccempname;            //超级船东交易员
    private String ccempphone;           //超级船东交易员联系方式
    private String pricetype;            //价格类型
    private String detentionchargetype;  //滞留费用类型
    private String remark;               //备注
    private String contractattachurl;    //合同凭证地址，多个地址
    private BigDecimal paytotalamount;   //付款总价（总付款合计）
    private BigDecimal paytotalweight;   //付款总重 (结算吨数)

    public LogisticsShipFeedback(){}

    public LogisticsShipFeedback(int id, String souceId, String transportcompany, String carrier, int DWT, String paytype, String settlementtype, String needbill, BigDecimal transportprices, BigDecimal totalprice, BigDecimal otherprice, BigDecimal detentioncharge, String shipname, String emptyport, String emptydate, int venderid, String status, LocalDateTime createtime, LocalDateTime lastupdatetime, boolean isdelete, String carrierphone, String ccempname, String ccempphone, String pricetype, String detentionchargetype, String remark, String contractattachurl, BigDecimal paytotalamount, BigDecimal paytotalweight) {
        this.id = id;
        this.souceId = souceId;
        //this.ordercode = ordercode;
        this.transportcompany = transportcompany;
        this.carrier = carrier;
        this.DWT = DWT;
        this.paytype = paytype;
        this.settlementtype = settlementtype;
        this.needbill = needbill;
        this.transportprices = transportprices;
        this.totalprice = totalprice;
        this.otherprice = otherprice;
        this.detentioncharge = detentioncharge;
        this.shipname = shipname;
        this.emptyport = emptyport;
        this.emptydate = emptydate;
        this.venderid = venderid;
        this.status = status;
        this.createtime = createtime;
        this.lastupdatetime = lastupdatetime;
        this.isdelete = isdelete;
        this.carrierphone = carrierphone;
        this.ccempname = ccempname;
        this.ccempphone = ccempphone;
        this.pricetype = pricetype;
        this.detentionchargetype = detentionchargetype;
        this.remark = remark;
        this.contractattachurl = contractattachurl;
        this.paytotalamount = paytotalamount;
        this.paytotalweight = paytotalweight;
    }

    public String getCarrierphone() {
        return carrierphone;
    }

    public void setCarrierphone(String carrierphone) {
        this.carrierphone = carrierphone;
    }

    public String getCcempname() {
        return ccempname;
    }

    public void setCcempname(String ccempname) {
        this.ccempname = ccempname;
    }

    public String getCcempphone() {
        return ccempphone;
    }

    public void setCcempphone(String ccempphone) {
        this.ccempphone = ccempphone;
    }

    public String getPricetype() {
        return pricetype;
    }

    public void setPricetype(String pricetype) {
        this.pricetype = pricetype;
    }

    public String getDetentionchargetype() {
        return detentionchargetype;
    }

    public void setDetentionchargetype(String detentionchargetype) {
        this.detentionchargetype = detentionchargetype;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getContractattachurl() {
        return contractattachurl;
    }

    public void setContractattachurl(String contractattachurl) {
        this.contractattachurl = contractattachurl;
    }

    public BigDecimal getPaytotalamount() {
        return paytotalamount;
    }

    public void setPaytotalamount(BigDecimal paytotalamount) {
        this.paytotalamount = paytotalamount;
    }

    public BigDecimal getPaytotalweight() {
        return paytotalweight;
    }

    public void setPaytotalweight(BigDecimal paytotalweight) {
        this.paytotalweight = paytotalweight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSouceId() {
        return souceId;
    }

    public void setSouceId(String souceId) {
        this.souceId = souceId;
    }

    //public String getOrdercode() {
    //    return ordercode;
    //}
    //
    //public void setOrdercode(String ordercode) {
    //    this.ordercode = ordercode;
    //}

    public String getTransportcompany() {
        return transportcompany;
    }

    public void setTransportcompany(String transportcompany) {
        this.transportcompany = transportcompany;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public int getDWT() {
        return DWT;
    }

    public void setDWT(int DWT) {
        this.DWT = DWT;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getSettlementtype() {
        return settlementtype;
    }

    public void setSettlementtype(String settlementtype) {
        this.settlementtype = settlementtype;
    }

    public String getNeedbill() {
        return needbill;
    }

    public void setNeedbill(String needbill) {
        this.needbill = needbill;
    }

    public boolean isdelete() {
        return isdelete;
    }

    public BigDecimal getTransportprices() {
        return transportprices;
    }

    public void setTransportprices(BigDecimal transportprices) {
        this.transportprices = transportprices;
    }

    public BigDecimal getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(BigDecimal totalprice) {
        this.totalprice = totalprice;
    }

    public BigDecimal getOtherprice() {
        return otherprice;
    }

    public void setOtherprice(BigDecimal otherprice) {
        this.otherprice = otherprice;
    }

    public BigDecimal getDetentioncharge() {
        return detentioncharge;
    }

    public void setDetentioncharge(BigDecimal detentioncharge) {
        this.detentioncharge = detentioncharge;
    }

    public String getShipname() {
        return shipname;
    }

    public void setShipname(String shipname) {
        this.shipname = shipname;
    }

    public String getEmptyport() {
        return emptyport;
    }

    public void setEmptyport(String emptyport) {
        this.emptyport = emptyport;
    }

    public String getEmptydate() {
        return emptydate;
    }

    public void setEmptydate(String emptydate) {
        this.emptydate = emptydate;
    }

    public int getVenderid() {
        return venderid;
    }

    public void setVenderid(int venderid) {
        this.venderid = venderid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public boolean isIsdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public int getDetentionday() {
        return detentionday;
    }

    public void setDetentionday(int detentionday) {
        this.detentionday = detentionday;
    }

    @Override
    public String toString() {
        return "LogisticsShipFeedback{" +
                "id=" + id +
                ", souceId='" + souceId + '\'' +
                ", transportcompany='" + transportcompany + '\'' +
                ", carrier='" + carrier + '\'' +
                ", DWT=" + DWT +
                ", paytype='" + paytype + '\'' +
                ", settlementtype='" + settlementtype + '\'' +
                ", needbill=" + needbill +
                ", transportprices=" + transportprices +
                ", totalprice=" + totalprice +
                ", otherprice=" + otherprice +
                ", detentioncharge=" + detentioncharge +
                ", shipname='" + shipname + '\'' +
                ", emptyport='" + emptyport + '\'' +
                ", emptydate=" + emptydate +
                ", venderid=" + venderid +
                ", status='" + status + '\'' +
                ", createtime=" + createtime +
                ", lastupdatetime=" + lastupdatetime +
                ", isdelete=" + isdelete +
                ", carrierphone='" + carrierphone + '\'' +
                ", ccempname='" + ccempname + '\'' +
                ", ccempphone='" + ccempphone + '\'' +
                ", pricetype='" + pricetype + '\'' +
                ", detentionchargetype='" + detentionchargetype + '\'' +
                ", remark='" + remark + '\'' +
                ", contractattachurl='" + contractattachurl + '\'' +
                ", paytotalamount=" + paytotalamount +
                ", paytotalweight=" + paytotalweight +
                '}';
    }
}
