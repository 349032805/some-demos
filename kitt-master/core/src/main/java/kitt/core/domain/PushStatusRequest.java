package kitt.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by lich on 15/12/24.
 */
public class PushStatusRequest {
    private String ordercode;

    private String vendercreatetime;
    private String transportstartdate;
    private String transportenddate;
    private BigDecimal transportprices;
    private BigDecimal totalprice;
    private String logisticsphone;
    private String comment;
    private String status;
    private LocalDateTime createtime;
    private String souceId;
    private String token;
    private String version;
    private int venderid;
    private int intentionid;
    private boolean isfinish;

    public String getOrdercode() {
        return ordercode;
    }

    public void setOrdercode(String ordercode) {
        this.ordercode = ordercode;
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

    public int getVenderid() {
        return venderid;
    }

    public void setVenderid(int venderid) {
        this.venderid = venderid;
    }

    public int getIntentionid() {
        return intentionid;
    }

    public void setIntentionid(int intentionid) {
        this.intentionid = intentionid;
    }

    public String getVendercreatetime() {
        return vendercreatetime;
    }

    public void setVendercreatetime(String vendercreatetime) {
        this.vendercreatetime = vendercreatetime;
    }

    public String getTransportstartdate() {
        return transportstartdate;
    }

    public void setTransportstartdate(String transportstartdate) {
        this.transportstartdate = transportstartdate;
    }

    public String getTransportenddate() {
        return transportenddate;
    }

    public void setTransportenddate(String transportenddate) {
        this.transportenddate = transportenddate;
    }

    public boolean getIsfinish() {
        return isfinish;
    }

    public void setIsfinish(boolean isfinish) {
        this.isfinish = isfinish;
    }

    @Override
    public String toString() {
        return "PushStatusRequest{" +
                "ordercode='" + ordercode + '\'' +
                ", vendercreatetime='" + vendercreatetime + '\'' +
                ", transportstartdate='" + transportstartdate + '\'' +
                ", transportenddate='" + transportenddate + '\'' +
                ", transportprices=" + transportprices +
                ", totalprice=" + totalprice +
                ", logisticsphone='" + logisticsphone + '\'' +
                ", comment='" + comment + '\'' +
                ", status='" + status + '\'' +
                ", createtime=" + createtime +
                ", souceId='" + souceId + '\'' +
                ", token='" + token + '\'' +
                ", version='" + version + '\'' +
                ", venderid=" + venderid +
                ", intentionid=" + intentionid +
                ", isfinish=" + isfinish +
                '}';
    }
}
