package kitt.core.domain;

import kitt.core.service.validate.constraints.DeliveryDateMatcher;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by xiangyang on 14-12-17.
 */
@DeliveryDateMatcher(startDate = "deliveryStartDate",endDate = "deliveryEndDate")
public class ManualSell  implements Serializable {
    private int id;
    private String manualsellId;
    private int userId;
    //类型 1.人工找货  0.人工销售
    private boolean type=true;
    //低位热值
    @NotNull(message = "低位热值不能为空")
    @Range(min = 1,max = 7500,message = "低位热值必须是{min}-{max}之间的整数")
    private Integer lowcalorificvalue;
    //空干基硫分
    private BigDecimal airdrybasissulfur;
    //收到基硫分
    @NotNull(message = "收到基硫分不能为空")
    @Digits(integer = 2,fraction =2,message = "收到基硫分不能>{fraction}位小数")
    @DecimalMin(value = "0",inclusive = false,message = "收到基硫分不包括{value}")
    @DecimalMax(value = "10",inclusive = false,message = "收到基硫分不能大于{value}")
    private BigDecimal receivebasissulfur;
    //空干基挥发份
    @NotNull(message = "空干基挥发分不能为空")
    @Digits(integer = 2,fraction =2,message = "空干基挥发分不能>{fraction}位小数")
    @DecimalMax(value = "50",message = "空干基挥发分不能大于{value}")
    @DecimalMin(value = "0",inclusive = false,message = "空干基挥发分不包括{value}")
    private BigDecimal airdrybasisvolatile;
    //省份片区
    @NotBlank(message = "省份片区不能为空")
    private String deliveryDistrict;
    //省份
    @NotBlank(message = "省份不能为空")
    private String deliveryProvince;
    //提货详细地址
    @NotBlank(message = "港口不能为空")
    private String deliveryAddr;
    private String deliveryOtherPlace;
    //提货方式
    @NotBlank(message = "提货方式不能为空")
    private String deliveryMode;
    //需求数量
    @NotNull(message = "需求数量不能为空")
    private Integer demandAmount;
    //供货开始时间
    @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deliveryStartDate;
    @NotNull(message = "结束时间不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deliveryEndDate;
    //联系人
    @NotBlank(message = "联系人不能为空")
    private String contactName;
    //联系电话
    @NotBlank(message = "电话号码不能为空")
    private String phone;
    //公司名称
    @NotBlank(message = "公司名称不能为空")
    private String companyName;
    //煤种
    @NotBlank(message = "煤种不能为空")
    private String coalType;
    private LocalDate createDatetime;
    //验证码，数据库没有这个字段
    private String verifyCode;
    private String dateCondition;
    private Integer regionId;
    private Integer provinceId;
    private Integer portId;
    private int clienttype;                  //客户端类型
    private boolean isdelete;                //是否删除
    private String status;                   //状态
    private int solvedmanid;                 //处理单子人id
    private String solvedmanusername;        //处理单子人登录名
    private String solvedremarks;            //处理单子人填写的备注

    public boolean getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getLowcalorificvalue() {
        return lowcalorificvalue;
    }

    public void setLowcalorificvalue(Integer lowcalorificvalue) {
        this.lowcalorificvalue = lowcalorificvalue;
    }

    public BigDecimal getAirdrybasissulfur() {
        return airdrybasissulfur;
    }

    public void setAirdrybasissulfur(BigDecimal airdrybasissulfur) {
        this.airdrybasissulfur = airdrybasissulfur;
    }

    public String getDeliveryDistrict() {
        return deliveryDistrict;
    }

    public void setDeliveryDistrict(String deliveryDistrict) {
        this.deliveryDistrict = deliveryDistrict;
    }

    public String getDeliveryProvince() {
        return deliveryProvince;
    }

    public void setDeliveryProvince(String deliveryProvince) {
        this.deliveryProvince = deliveryProvince;
    }

    public String getDeliveryAddr() {
        return deliveryAddr;
    }

    public void setDeliveryAddr(String deliveryAddr) {
        this.deliveryAddr = deliveryAddr;
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    public void setDeliveryMode(String deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public Integer getDemandAmount() {
        return demandAmount;
    }

    public void setDemandAmount(Integer demandAmount) {
        this.demandAmount = demandAmount;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public void setType(boolean type) {
        this.type = type;
    }

    public String getDeliveryOtherPlace() {
        return deliveryOtherPlace;
    }

    public void setDeliveryOtherPlace(String deliveryOtherPlace) {
        this.deliveryOtherPlace = deliveryOtherPlace;
    }

    public boolean getType() {
        return type;
    }

    public LocalDate getDeliveryStartDate() {
        return deliveryStartDate;
    }

    public void setDeliveryStartDate(LocalDate deliveryStartDate) {
        this.deliveryStartDate = deliveryStartDate;
    }

    public LocalDate getDeliveryEndDate() {
        return deliveryEndDate;
    }

    public void setDeliveryEndDate(LocalDate deliveryEndDate) {
        this.deliveryEndDate = deliveryEndDate;
    }

    public BigDecimal getAirdrybasisvolatile() {
        return airdrybasisvolatile;
    }

    public void setAirdrybasisvolatile(BigDecimal airdrybasisvolatile) {
        this.airdrybasisvolatile = airdrybasisvolatile;
    }

    public BigDecimal getReceivebasissulfur() {
        return receivebasissulfur;
    }

    public void setReceivebasissulfur(BigDecimal receivebasissulfur) {
        this.receivebasissulfur = receivebasissulfur;
    }

    public String getCoalType() {
        return coalType;
    }

    public void setCoalType(String coalType) {
        this.coalType = coalType;
    }

    public LocalDate getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(LocalDate createDatetime) {
        this.createDatetime = createDatetime;
    }
    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public String getManualsellId() {
        return manualsellId;
    }

    public void setManualsellId(String manualsellId) {
        this.manualsellId = manualsellId;
    }

    public String getDateCondition() {
        return dateCondition;
    }

    public void setDateCondition(String dateCondition) {
        this.dateCondition = dateCondition;
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

    public int getClienttype() {
        return clienttype;
    }

    public void setClienttype(int clienttype) {
        this.clienttype = clienttype;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSolvedmanid() {
        return solvedmanid;
    }

    public void setSolvedmanid(int solvedmanid) {
        this.solvedmanid = solvedmanid;
    }

    public String getSolvedmanusername() {
        return solvedmanusername;
    }

    public void setSolvedmanusername(String solvedmanusername) {
        this.solvedmanusername = solvedmanusername;
    }

    public String getSolvedremarks() {
        return solvedremarks;
    }

    public void setSolvedremarks(String solvedremarks) {
        this.solvedremarks = solvedremarks;
    }
}
