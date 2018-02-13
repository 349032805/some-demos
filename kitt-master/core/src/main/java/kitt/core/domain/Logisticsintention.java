package kitt.core.domain;


import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Created by lich on 15/12/14.
 */
public class Logisticsintention {
    private  int  id;
    @NotNull(message = "供应商id不能为空")
    private int venderid;                       //供应商id
    private String souceId;                     //流水编号
    private int type;                //type:0:车运  2:船运
    private int shipid;                         //关联船id
    private String companyname;                //公司名字
    @Length(max=20,message = "装车'省份'长度限制为20")
    @NotBlank(message = "装车地址'省份'不能为空")
    private String loadProvince;                //装车地址-省
    @Length(max=20,message = "卸车'省份'长度限制为20")
    @NotBlank(message = "卸车地址'省份'不能为空")
    private String unLoadProvince;              //卸车地址-省
    @Length(max=20,message = "装车'市'长度限制为20")
    @NotBlank(message = "装车地址'市'不能为空")
    private String loadCity;                    //装车地址-市
    @Length(max=20,message = "卸车'市'长度限制为20")
    @NotBlank(message = "卸车地址'市'不能为空")
    private String unLoadCity;                  //卸车地址-市
    @Length(max=20,message = "装车'区'长度限制为20")
    private String loadCountry;                    //装车地址-区
    @Length(max=20,message = "卸车'区'长度限制为20")
    private String unloadCountry;                  //卸车地址-区
    @Length(max=70,message = "装车详细地址长度限制为70")
//    @NotBlank(message = "装车地址详细地址不能为空")
    //@Pattern(regexp="[a-zA-Z0-9_\u4e00-\u9fa5':;',!.！‘；：”“’。，、]*" ,message = "装车详细地址不能有非法字符")
    private String loadAddDetail;               //装车地址-详细地址
    @Length(max=70,message = "卸车详细地址长度限制为70")
//    @NotBlank(message = "卸车地址详细地址不能为空")
    //@Pattern(regexp="[a-zA-Z0-9_\u4e00-\u9fa5':;',!.！‘；：”“’。，、]*" ,message = "卸车详细地址不能有非法字符")
    private String unLoadAddDetail;             //卸车地址-详细地址
    @Length(max=20,message = "装车省编码长度限制为20")
    @NotBlank(message = "装车省编码不能为空")
    private String provinceCode;
    @Length(max=20,message = "装车市编码长度限制为20")
    @NotBlank(message = "装车市编码不能为空")
    private String cityCode;
    @Length(max=20,message = "卸车省编码长度限制为20")
    @NotBlank(message = "卸车省编码不能为空")
    private String unprovinceCode;
    @Length(max=20,message = "卸车市编码长度限制为20")
    @NotBlank(message = "卸车市编码不能为空")
    private String uncityCode;
    @Length(max=20,message = "装车区编码长度限制为20")
    private String countryCode;
    @Length(max=20,message = "卸车区编码长度限制为20")
    private String uncountryCode;
    @NotNull(message = "收件开始时间不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate receivebegintime;     //收件开始时间
    @NotNull(message = "收件结束时间不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate receiveendtime;       //收件结束时间
    @Length(max=20,message = "货物类型长度限制为20")
    @NotBlank(message = "货物类型不能为空")
    private String goodsType;                   //货物类型
    @Max(value=99999999,message = "货物重量长度最大为8位数")
    @NotNull(message = "货物重量不能为空")
    private BigDecimal goodsWeight;                 //货物重量
    @Length(max=15,message = "联系人长度限制为15")
    @NotBlank(message = "联系人不能为空")
    private String contacts;                    //联系人
    @Length(max=15,message = "联系电话长度限制为15")
    @NotBlank(message = "联系电话不能为空")
    private String mobile;                      //联系电话
    @Max(value=99999999,message = "期望运费长度最大为8位数")
    private BigDecimal priceUp;               //期望运费最大值
    @Length(max=200,message = "备注长度限制为200")
    private String remark;                      //备注
    private String vendername;                  //供应商名
    private String status;                      //状态
    @Length(max=200,message = "服务评价长度限制为200")
    private String serviceevaluation;           //服务评价
    private int servicescore;                   //服务评分
    @Length(max=200,message = "物流评价长度限制为200")
    private String logisticsevaluation;         //物流评价
    private int logisticsscore;                 //物流评分
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;           //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;       //最后一次更新时间
    private int isdelete;                       //是否删除 0:未删除  1:删除
    private int userid;                         //用户id
    private int isevaluated;                    //是否评价过 0:未评价 1:已评价
    private String customerremark;              //客服备注
    private boolean isfinish;                   //此订单是否完成 0:未完成  1:已完成

    public Logisticsintention(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVenderid() {
        return venderid;
    }

    public void setVenderid(int venderid) {
        this.venderid = venderid;
    }

    public String getSouceId() {
        return souceId;
    }

    public void setSouceId(String souceId) {
        this.souceId = souceId;
    }

    public String getLoadProvince() {
        return loadProvince;
    }

    public void setLoadProvince(String loadProvince) {
        this.loadProvince = loadProvince;
    }

    public String getUnLoadProvince() {
        return unLoadProvince;
    }

    public void setUnLoadProvince(String unLoadProvince) {
        this.unLoadProvince = unLoadProvince;
    }

    public String getLoadCity() {
        return loadCity;
    }

    public void setLoadCity(String loadCity) {
        this.loadCity = loadCity;
    }

    public String getUnLoadCity() {
        return unLoadCity;
    }

    public void setUnLoadCity(String unLoadCity) {
        this.unLoadCity = unLoadCity;
    }

    public String getLoadAddDetail() {
        return loadAddDetail;
    }

    public void setLoadAddDetail(String loadAddDetail) {
        this.loadAddDetail = loadAddDetail;
    }

    public String getUnLoadAddDetail() {
        return unLoadAddDetail;
    }

    public void setUnLoadAddDetail(String unLoadAddDetail) {
        this.unLoadAddDetail = unLoadAddDetail;
    }

    public LocalDate getReceivebegintime() {
        return receivebegintime;
    }

    public void setReceivebegintime(LocalDate receivebegintime) {
        this.receivebegintime = receivebegintime;
    }

    public LocalDate getReceiveendtime() {
        return receiveendtime;
    }

    public void setReceiveendtime(LocalDate receiveendtime) {
        this.receiveendtime = receiveendtime;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public BigDecimal getGoodsWeight() {
        return goodsWeight;
    }

    public void setGoodsWeight(BigDecimal goodsWeight) {
        this.goodsWeight = goodsWeight;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public BigDecimal getPriceUp() {
        return priceUp;
    }

    public void setPriceUp(BigDecimal priceUp) {
        this.priceUp = priceUp;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVendername() {
        return vendername;
    }

    public void setVendername(String vendername) {
        this.vendername = vendername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceevaluation() {
        return serviceevaluation;
    }

    public void setServiceevaluation(String serviceevaluation) {
        this.serviceevaluation = serviceevaluation;
    }

    public int getServicescore() {
        return servicescore;
    }

    public void setServicescore(int servicescore) {
        this.servicescore = servicescore;
    }

    public String getLogisticsevaluation() {
        return logisticsevaluation;
    }

    public void setLogisticsevaluation(String logisticsevaluation) {
        this.logisticsevaluation = logisticsevaluation;
    }

    public int getLogisticsscore() {
        return logisticsscore;
    }

    public void setLogisticsscore(int logisticsscore) {
        this.logisticsscore = logisticsscore;
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

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getIsevaluated() {
        return isevaluated;
    }

    public void setIsevaluated(int isevaluated) {
        this.isevaluated = isevaluated;
    }

    public String getLoadCountry() {
        return loadCountry;
    }

    public void setLoadCountry(String loadCountry) {
        this.loadCountry = loadCountry;
    }

    public String getUnloadCountry() {
        return unloadCountry;
    }

    public void setUnloadCountry(String unloadCountry) {
        this.unloadCountry = unloadCountry;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getUnprovinceCode() {
        return unprovinceCode;
    }

    public void setUnprovinceCode(String unprovinceCode) {
        this.unprovinceCode = unprovinceCode;
    }

    public String getUncityCode() {
        return uncityCode;
    }

    public void setUncityCode(String uncityCode) {
        this.uncityCode = uncityCode;
    }

    public String getCustomerremark() {
        return customerremark;
    }

    public void setCustomerremark(String customerremark) {
        this.customerremark = customerremark;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getUncountryCode() {
        return uncountryCode;
    }

    public void setUncountryCode(String uncountryCode) {
        this.uncountryCode = uncountryCode;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getShipid() {
        return shipid;
    }

    public void setShipid(int shipid) {
        this.shipid = shipid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }
    public boolean getIsfinish() {
        return isfinish;
    }

    public void setIsfinish(boolean isfinish) {
        this.isfinish = isfinish;

    }

    @Override
    public String toString() {
        return "Logisticsintention{" +
                "id=" + id +
                ", venderid=" + venderid +
                ", souceId='" + souceId + '\'' +
                ", loadProvince='" + loadProvince + '\'' +
                ", unLoadProvince='" + unLoadProvince + '\'' +
                ", loadCity='" + loadCity + '\'' +
                ", unLoadCity='" + unLoadCity + '\'' +
                ", loadCountry='" + loadCountry + '\'' +
                ", unloadCountry='" + unloadCountry + '\'' +
                ", loadAddDetail='" + loadAddDetail + '\'' +
                ", unLoadAddDetail='" + unLoadAddDetail + '\'' +
                ", provinceCode='" + provinceCode + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", unprovinceCode='" + unprovinceCode + '\'' +
                ", uncityCode='" + uncityCode + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", uncountryCode='" + uncountryCode + '\'' +
                ", receivebegintime=" + receivebegintime +
                ", receiveendtime=" + receiveendtime +
                ", goodsType='" + goodsType + '\'' +
                ", goodsWeight=" + goodsWeight +
                ", contacts='" + contacts + '\'' +
                ", mobile='" + mobile + '\'' +
                ", priceUp=" + priceUp +
                ", remark='" + remark + '\'' +
                ", vendername='" + vendername + '\'' +
                ", status='" + status + '\'' +
                ", serviceevaluation='" + serviceevaluation + '\'' +
                ", servicescore=" + servicescore +
                ", logisticsevaluation='" + logisticsevaluation + '\'' +
                ", logisticsscore=" + logisticsscore +
                ", createtime=" + createtime +
                ", lastupdatetime=" + lastupdatetime +
                ", isdelete=" + isdelete +
                ", userid=" + userid +
                ", isevaluated=" + isevaluated +
                ", isfinish=" + isfinish +
                ", customerremark='" + customerremark + '\'' +
                '}';
    }

    public boolean isfinish() {
        return isfinish;
    }
}
