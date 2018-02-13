package kitt.core.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by lich on 15/12/22.
 */
public class Logisticsfeedback {
    private int id;
    private int sintentionid;                     //物流意向单id
    private int venderid;                         //物流供应商id
    private String ordercode;                     //运单号
    private LocalDateTime vendercreatetime;       //成单时间
    private LocalDate transportstartdate;         //起运日期
    private LocalDate transportenddate;           //完成日期
    private BigDecimal transportprices;           //运费单价
    private BigDecimal totalprice;                //运费总价
    private String logisticsphone;                //物流电话
    private String comment;                       //备注
    private String status;                        //状态
    private LocalDateTime createtime;             //创建时间
    private LocalDateTime lastupdatetime;         //最后一次更新时间
    private int isdelete;                         //是否删除  0:未删除  1:已删除
    private String souceId;                       //流水编号
    private String token;
    private String version;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSintentionid() {
        return sintentionid;
    }

    public void setSintentionid(int sintentionid) {
        this.sintentionid = sintentionid;
    }

    public int getVenderid() {
        return venderid;
    }

    public void setVenderid(int venderid) {
        this.venderid = venderid;
    }

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
    }

    public LocalDateTime getVendercreatetime() {
        return vendercreatetime;
    }

    public void setVendercreatetime(LocalDateTime vendercreatetime) {
        this.vendercreatetime = vendercreatetime;
    }

    public LocalDate getTransportstartdate() {
        return transportstartdate;
    }

    public void setTransportstartdate(LocalDate transportstartdate) {
        this.transportstartdate = transportstartdate;
    }

    public LocalDate getTransportenddate() {
        return transportenddate;
    }

    public void setTransportenddate(LocalDate transportenddate) {
        this.transportenddate = transportenddate;
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

    public String getLogisticsphone() {
        return logisticsphone;
    }

    public void setLogisticsphone(String logisticsphone) {
        this.logisticsphone = logisticsphone;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }

    public String getSouceId() {
        return souceId;
    }

    public void setSouceId(String souceId) {
        this.souceId = souceId;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

