package kitt.core.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by zhangbolun on 16/1/24.
 */
public class LogisticsDelegate {
    private int id;
    private String souceId;
    @NotBlank(message="运输类型不能为空")
    private String transportType;
    @NotBlank(message="货物类型不能为空")
    private String goodsType;
    @NotBlank(message="货物重量不能为空")
    @Length(max=18,message = "货物重量长度限制为18")
    private String goodsWeight;
    @NotNull(message="运送日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transportDate;
    @NotBlank(message="联系电话不能为空")
    @Length(max=11,message = "联系电话长度限制为11")
    private String mobile;
    private String status;
    @Length(max=200,message = "备注长度限制为200")
    private String remark;
    private String suggest;
    private LocalDateTime createtime;
    private LocalDateTime lastupdatetime;

    public LogisticsDelegate() {}

    public LogisticsDelegate(int id, String souceId, String transportType, String goodsType, String goodsWeight, LocalDate transportDate, String mobile, String status, String remark, String suggest, LocalDateTime createtime, LocalDateTime lastupdatetime) {
        this.id = id;
        this.souceId = souceId;
        this.transportType = transportType;
        this.goodsType = goodsType;
        this.goodsWeight = goodsWeight;
        this.transportDate = transportDate;
        this.mobile = mobile;
        this.status = status;
        this.remark = remark;
        this.suggest = suggest;
        this.createtime = createtime;
        this.lastupdatetime = lastupdatetime;
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

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(String goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public LocalDate getTransportDate() {
        return transportDate;
    }

    public void setTransportDate(LocalDate transportDate) {
        this.transportDate = transportDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSuggest() {
        return suggest;
    }

    public void setSuggest(String suggest) {
        this.suggest = suggest;
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

    @Override
    public String toString() {
        return "LogisticsDelegate{" +
                "id=" + id +
                ", souceId='" + souceId + '\'' +
                ", transportType='" + transportType + '\'' +
                ", goodsType='" + goodsType + '\'' +
                ", goodsWeight='" + goodsWeight + '\'' +
                ", transportDate=" + transportDate +
                ", mobile='" + mobile + '\'' +
                ", status='" + status + '\'' +
                ", remark='" + remark + '\'' +
                ", suggest='" + suggest + '\'' +
                ", createtime=" + createtime +
                ", lastupdatetime=" + lastupdatetime +
                '}';
    }
}
