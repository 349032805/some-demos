package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by jack on 14/11/25.
 */
public class Order implements Serializable{
    private int id;                                     //id
    private String pid;                                 //产品id，sellinfo表中的pid
    private int sellinfoid;                             //sellinfo表对应的id
    private EnumOrder status;                           //订单状态
    private EnumOrder ostatus;                          //订单备份表里对应的状态 ordersinfo表
    private BigDecimal price;                           //价格
    private int amount;                                 //数量
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;                   //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;               //最后一次更新时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliverytime1;                    //提货时间1
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliverytime2;                    //提货时间2
    private String seller;                              //卖家公司名
    private String deliverymode;                        //提货方式
    private String transportmode;                       //运输方式
    private BigDecimal totalmoney;                      //总货款
    private String linkman;                             //联系人
    private String linkmanphone;                        //联系人电话
    private EnumOrder ordertype;                        //订单类型
    private int userid;                                 //买家id， userid
    private String orderid;                             //orderid
    private String remarks;                             //备注
    private EnumOrder paytype;                          //付款类型
    private boolean isfutures;                          //是否是期货
    private String contractno;                          //合同编号
    private BigDecimal paidmoney;                       //已付货款
    private BigDecimal waitmoney;                       //待付货款
    private BigDecimal savemoney;                       //结余款
    private int sellerid;                               //卖家id
    private EnumOrder sellerstatus;                     //卖家状态
    private String deliveryregion;                      //区域
    private String deliveryprovince;                    //省市
    private String deliveryplace;                       //港口
    private String otherharbour;                        //其它港口
    private String dealerid;                            //交易员id
    private String dealername;                          //交易员name
    private String dealerphone;                         //交易员
    private int version;                                //数据库版本，控制并发
    private Integer regionId;
    private Integer provinceId;
    private Integer portId;
    private Integer traderid;
    private int clienttype;                             //客户端类型
    private String paymode;                             //付款方式
    public Order(){}

    public Order(String pid, int sellinfoid, EnumOrder status, BigDecimal price, int amount, LocalDateTime createtime, LocalDate deliverytime1, LocalDate deliverytime2, String seller, String deliveryregion, String deliveryprovince, String deliveryplace, String otherharbour, String deliverymode, BigDecimal totalmoney, String linkman, String linkmanphone, int userid, EnumOrder ordertype, int sellerid, int traderid, String dealername, String dealerphone, int regionId, int provinceId, int portId, int clienttype, String paymode) {
        this.pid = pid;
        this.sellinfoid = sellinfoid;
        this.price = price;
        this.status = status;
        this.amount = amount;
        this.createtime = createtime;
        this.deliverytime1 = deliverytime1;
        this.deliverytime2 = deliverytime2;
        this.seller = seller;
        this.deliveryregion = deliveryregion;
        this.deliveryprovince = deliveryprovince;
        this.deliveryplace = deliveryplace;
        this.otherharbour = otherharbour;
        this.deliverymode = deliverymode;
        this.totalmoney = totalmoney;
        this.linkman = linkman;
        this.linkmanphone = linkmanphone;
        this.userid = userid;
        this.ordertype = ordertype;
        this.sellerid = sellerid;
        this.traderid = traderid;
        this.dealername = dealername;
        this.dealerphone = dealerphone;
        this.regionId = regionId;
        this.provinceId = provinceId;
        this.portId = portId;
        this.clienttype = clienttype;
        this.paymode = paymode;
    }

    public Order(String pid, int sellinfoid, EnumOrder status, BigDecimal price, int amount, LocalDateTime createtime, LocalDate deliverytime1, LocalDate deliverytime2, String seller, String deliveryregion, String deliveryprovince, String deliveryplace, String otherharbour, String deliverymode, BigDecimal totalmoney, BigDecimal waitmoney, String linkman, String linkmanphone, int userid, EnumOrder ordertype, EnumOrder paytype, boolean isfutures, int sellerid, Integer traderid, String dealername, String dealerphone, int regionId, int provinceId, int portId, int clienttype, String paymode) {
        this.pid = pid;
        this.sellinfoid = sellinfoid;
        this.price = price;
        this.status = status;
        this.amount = amount;
        this.createtime = createtime;
        this.deliverytime1 = deliverytime1;
        this.deliverytime2 = deliverytime2;
        this.seller = seller;
        this.deliveryregion = deliveryregion;
        this.deliveryprovince = deliveryprovince;
        this.deliveryplace = deliveryplace;
        this.otherharbour = otherharbour;
        this.deliverymode = deliverymode;
        this.totalmoney = totalmoney;
        this.waitmoney = waitmoney;
        this.linkman = linkman;
        this.linkmanphone = linkmanphone;
        this.userid = userid;
        this.ordertype = ordertype;
        this.paytype = paytype;
        this.isfutures = isfutures;
        this.sellerid = sellerid;
        this.traderid = traderid;
        this.dealername = dealername;
        this.dealerphone = dealerphone;
        this.regionId = regionId;
        this.provinceId = provinceId;
        this.portId = portId;
        this.clienttype = clienttype;
        this.paymode = paymode;
    }

    public Order(int id, String pid, int sellinfoid, EnumOrder status, BigDecimal price, int amount, LocalDateTime createtime, LocalDate deliverytime1, LocalDate deliverytime2, String seller, String deliveryregion, String deliveryprovince, String deliveryplace, String otherharbour, String deliverymode, BigDecimal totalmoney, BigDecimal waitmoney, String linkman, String linkmanphone, int userid, EnumOrder ordertype, EnumOrder paytype, boolean isfutures, int sellerid, Integer traderid, String dealername, String dealerphone, int regionId, int provinceId, int portId, int version) {
        this.id = id;
        this.pid = pid;
        this.sellinfoid = sellinfoid;
        this.price = price;
        this.status = status;
        this.amount = amount;
        this.createtime = createtime;
        this.deliverytime1 = deliverytime1;
        this.deliverytime2 = deliverytime2;
        this.seller = seller;
        this.deliveryregion = deliveryregion;
        this.deliveryprovince = deliveryprovince;
        this.deliveryplace = deliveryplace;
        this.otherharbour = otherharbour;
        this.deliverymode = deliverymode;
        this.totalmoney = totalmoney;
        this.waitmoney = waitmoney;
        this.linkman = linkman;
        this.linkmanphone = linkmanphone;
        this.userid = userid;
        this.ordertype = ordertype;
        this.paytype = paytype;
        this.isfutures = isfutures;
        this.sellerid = sellerid;
        this.traderid = traderid;
        this.dealername = dealername;
        this.dealerphone = dealerphone;
        this.regionId = regionId;
        this.provinceId = provinceId;
        this.portId = portId;
        this.version = version;
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

    public int getSellinfoid() {
        return sellinfoid;
    }

    public void setSellinfoid(int sellinfoid) {
        this.sellinfoid = sellinfoid;
    }

    public EnumOrder getStatus() {
        return status;
    }

    public void setStatus(EnumOrder status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
    return price;
  }

    public void setPrice(BigDecimal price) {
    this.price = price;
  }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getDeliveryprovince() {
        return deliveryprovince;
    }

    public void setDeliveryprovince(String deliveryprovince) {
        this.deliveryprovince = deliveryprovince;
    }

    public String getDeliveryregion() {
        return deliveryregion;
    }

    public void setDeliveryregion(String deliveryregion) {
        this.deliveryregion = deliveryregion;
    }

    public String getDeliveryplace() {
        return deliveryplace;
    }

    public void setDeliveryplace(String deliveryplace) {
        this.deliveryplace = deliveryplace;
    }

    public String getOtherharbour() {
        return otherharbour;
    }

    public void setOtherharbour(String otherharbour) {
        this.otherharbour = otherharbour;
    }

    public String getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(String deliverymode) {
        this.deliverymode = deliverymode;
    }

    public String getTransportmode() {
        return transportmode;
    }

    public void setTransportmode(String transportmode) {
        this.transportmode = transportmode;
    }

    public BigDecimal getTotalmoney() {
        return totalmoney;
    }

    public void setTotalmoney(BigDecimal totalmoney) {
        this.totalmoney = totalmoney;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getLinkmanphone() {
        return linkmanphone;
    }

    public void setLinkmanphone(String linkmanphone) {
        this.linkmanphone = linkmanphone;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public EnumOrder getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(EnumOrder ordertype) {
        this.ordertype = ordertype;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public EnumOrder getPaytype() {
        return paytype;
    }

    public void setPaytype(EnumOrder paytype) {
        this.paytype = paytype;
    }

    public boolean isFutures() {
        return isfutures;
    }

    public void setIsfutures(boolean isfutures) {
        this.isfutures = isfutures;
    }

    public String getContractno() {
        return contractno;
    }

    public void setContractno(String contractno) {
        this.contractno = contractno;
    }

    public BigDecimal getPaidmoney() {
        return paidmoney;
    }

    public void setPaidmoney(BigDecimal paidmoney) {
        this.paidmoney = paidmoney;
    }

    public BigDecimal getSavemoney() {
        return savemoney;
    }

    public void setSavemoney(BigDecimal savemoney) {
        this.savemoney = savemoney;
    }

    public BigDecimal getWaitmoney() {
        return waitmoney;
    }

    public void setWaitmoney(BigDecimal waitmoney) {
        this.waitmoney = waitmoney;
    }

    public int getSellerid() {
        return sellerid;
    }

    public void setSellerid(int sellerid) {
        this.sellerid = sellerid;
    }

    public EnumOrder getSellerstatus() {
        return sellerstatus;
    }

    public void setSellerstatus(EnumOrder sellerstatus) {
        this.sellerstatus = sellerstatus;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

    public Integer getTraderid() {
        return traderid;
    }

    public void setTraderid(Integer traderid) {
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

    public EnumOrder getOstatus() {
        return ostatus;
    }

    public void setOstatus(EnumOrder ostatus) {
        this.ostatus = ostatus;
    }
}
