package kitt.core.entity;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by zhangbolun on 16/2/26.
 */
public class BPotentialCompany extends BaseEntity implements Serializable {
    private int id;
    @NotBlank(message = "公司名称不能为空")
    @Length(max = 20, message = "公司名称不能超过20个字")
    private String companyName;           //公司名称
    @NotBlank(message = "公司简称不能为空")
    @Length(max = 20, message = "公司简称不能超过20个字")
    private String companySimpleName;     //公司简称
    @NotBlank(message = "请填写业务负责人")
    @Length(max = 10, message = "业务负责人不能超过10个字")
    private String businessOwner;         //业务负责人
    @NotBlank(message = "请填写手机号")
    @Length(max = 11, message = "请输入正确的手机号")
    private String userPhone;             //用户手机
    @NotNull(message = "请选择交易员")
    private int traderId;                 //交易员编号
    private String traderName;            //交易员姓名
    private String traderPhone;           //交易员手机号
    @NotNull(message = "请选择办公地点省份")
    private String officeProvinceCode;    //办公地点省code
    private String officeProvince;        //办公地点省
    @NotNull(message = "请选择办公地点市")
    private String officeCityCode;        //办公地点市code
    private String officeCity;            //办公地点市
    private String officeCountryCode;     //办公地点区code
    private String officeCountry;         //办公地点区
    @Length(message = "最多输入20个字")
    private String officeDetailAddress;   //办公地点详细地址
    private String registerProvinceCode;  //注册地点省code
    private String registerProvince;      //注册地点省
    private String registerCityCode;      //注册地点市code
    private String registerCity;          //注册地点市
    private String registerCountryCode;   //注册地点区code
    private String registerCountry;       //注册地点区
    @Length(message = "最多输入20个字")
    private String registerDetailAddress; //注册地点详细地址
    @Length(message = "最多输入20个字")
    private String officePhone;           //办公电话
    @NotBlank(message = "请输入开户银行")
    @Length(max = 50, message = "最多输入50个字")
    private String openingBank;           //开户银行
    @Length(message = "最多输入20个字")
    private String legalPersonName;       //法人姓名
    @Length(max = 30, message = "最多输入30个数字")
    private String account;               //银行账号
    private String identificationNumword; //纳税人识别号
    private String fax;                   //传真
    private String zipCode;               //邮编
    @NotNull(message = "请选择企业类型")
    private int enterpriseTypeNumber;     //企业类型id
    private String enterpriseTypeName;    //企业类型名称
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal planOutput;        //设计产能
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal actualOutput;      //实际产能
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal annualSales;       //年销售量
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal annualCoalConsum;  //年耗煤量
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal annualPurchase;    //年采购量
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal installCapacity;   //装机容量
    private String annual;                //年度
    @NotNull(message = "请选择企业性质")
    private int enterpriseNatureNumber;   //企业性质id
    private String enterpriseNatureName;  //企业性质名称
    @Length(max = 20, message = "不能超过20个字")
    private String industry;              //具体行业
    @Length(max = 20, message = "不能超过20个字")
    private String nickName;              //负责人绰号
    @Length(max = 20, message = "不能超过20个字")
    private String wechat;                //微信号
    @Length(max = 20, message = "不能超过20个字")
    private String qq;                    //qq号
    @Length(max = 20, message = "不能超过20个字")
    @Email(message = "请输入正确的邮箱")
    private String mail;                  //邮箱
    @Length(max = 20, message = "不能超过20个字")
    private String remark;                //备注

    public BPotentialCompany() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanySimpleName() {
        return companySimpleName;
    }

    public void setCompanySimpleName(String companySimpleName) {
        this.companySimpleName = companySimpleName;
    }

    public String getBusinessOwner() {
        return businessOwner;
    }

    public void setBusinessOwner(String businessOwner) {
        this.businessOwner = businessOwner;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getTraderId() {
        return traderId;
    }

    public void setTraderId(int traderId) {
        this.traderId = traderId;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public String getTraderPhone() {
        return traderPhone;
    }

    public void setTraderPhone(String traderPhone) {
        this.traderPhone = traderPhone;
    }

    public String getOfficeProvinceCode() {
        return officeProvinceCode;
    }

    public void setOfficeProvinceCode(String officeProvinceCode) {
        this.officeProvinceCode = officeProvinceCode;
    }

    public String getOfficeProvince() {
        return officeProvince;
    }

    public void setOfficeProvince(String officeProvince) {
        this.officeProvince = officeProvince;
    }

    public String getOfficeCityCode() {
        return officeCityCode;
    }

    public void setOfficeCityCode(String officeCityCode) {
        this.officeCityCode = officeCityCode;
    }

    public String getOfficeCity() {
        return officeCity;
    }

    public void setOfficeCity(String officeCity) {
        this.officeCity = officeCity;
    }

    public String getOfficeCountryCode() {
        return officeCountryCode;
    }

    public void setOfficeCountryCode(String officeCountryCode) {
        this.officeCountryCode = officeCountryCode;
    }

    public String getOfficeCountry() {
        return officeCountry;
    }

    public void setOfficeCountry(String officeCountry) {
        this.officeCountry = officeCountry;
    }

    public String getOfficeDetailAddress() {
        return officeDetailAddress;
    }

    public void setOfficeDetailAddress(String officeDetailAddress) {
        this.officeDetailAddress = officeDetailAddress;
    }

    public String getRegisterProvinceCode() {
        return registerProvinceCode;
    }

    public void setRegisterProvinceCode(String registerProvinceCode) {
        this.registerProvinceCode = registerProvinceCode;
    }

    public String getRegisterProvince() {
        return registerProvince;
    }

    public void setRegisterProvince(String registerProvince) {
        this.registerProvince = registerProvince;
    }

    public String getRegisterCityCode() {
        return registerCityCode;
    }

    public void setRegisterCityCode(String registerCityCode) {
        this.registerCityCode = registerCityCode;
    }

    public String getRegisterCity() {
        return registerCity;
    }

    public void setRegisterCity(String registerCity) {
        this.registerCity = registerCity;
    }

    public String getRegisterCountryCode() {
        return registerCountryCode;
    }

    public void setRegisterCountryCode(String registerCountryCode) {
        this.registerCountryCode = registerCountryCode;
    }

    public String getRegisterCountry() {
        return registerCountry;
    }

    public void setRegisterCountry(String registerCountry) {
        this.registerCountry = registerCountry;
    }

    public String getRegisterDetailAddress() {
        return registerDetailAddress;
    }

    public void setRegisterDetailAddress(String registerDetailAddress) {
        this.registerDetailAddress = registerDetailAddress;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getOpeningBank() {
        return openingBank;
    }

    public void setOpeningBank(String openingBank) {
        this.openingBank = openingBank;
    }

    public String getLegalPersonName() {
        return legalPersonName;
    }

    public void setLegalPersonName(String legalPersonName) {
        this.legalPersonName = legalPersonName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getIdentificationNumword() {
        return identificationNumword;
    }

    public void setIdentificationNumword(String identificationNumword) {
        this.identificationNumword = identificationNumword;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public int getEnterpriseTypeNumber() {
        return enterpriseTypeNumber;
    }

    public void setEnterpriseTypeNumber(int enterpriseTypeNumber) {
        this.enterpriseTypeNumber = enterpriseTypeNumber;
    }

    public String getEnterpriseTypeName() {
        return enterpriseTypeName;
    }

    public void setEnterpriseTypeName(String enterpriseTypeName) {
        this.enterpriseTypeName = enterpriseTypeName;
    }

    public BigDecimal getPlanOutput() {
        return planOutput;
    }

    public void setPlanOutput(BigDecimal planOutput) {
        this.planOutput = planOutput;
    }

    public BigDecimal getActualOutput() {
        return actualOutput;
    }

    public void setActualOutput(BigDecimal actualOutput) {
        this.actualOutput = actualOutput;
    }

    public BigDecimal getAnnualSales() {
        return annualSales;
    }

    public void setAnnualSales(BigDecimal annualSales) {
        this.annualSales = annualSales;
    }

    public BigDecimal getAnnualCoalConsum() {
        return annualCoalConsum;
    }

    public void setAnnualCoalConsum(BigDecimal annualCoalConsum) {
        this.annualCoalConsum = annualCoalConsum;
    }

    public BigDecimal getAnnualPurchase() {
        return annualPurchase;
    }

    public void setAnnualPurchase(BigDecimal annualPurchase) {
        this.annualPurchase = annualPurchase;
    }

    public BigDecimal getInstallCapacity() {
        return installCapacity;
    }

    public void setInstallCapacity(BigDecimal installCapacity) {
        this.installCapacity = installCapacity;
    }

    public String getAnnual() {
        return annual;
    }

    public void setAnnual(String annual) {
        this.annual = annual;
    }

    public int getEnterpriseNatureNumber() {
        return enterpriseNatureNumber;
    }

    public void setEnterpriseNatureNumber(int enterpriseNatureNumber) {
        this.enterpriseNatureNumber = enterpriseNatureNumber;
    }

    public String getEnterpriseNatureName() {
        return enterpriseNatureName;
    }

    public void setEnterpriseNatureName(String enterpriseNatureName) {
        this.enterpriseNatureName = enterpriseNatureName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "BPotentialCompany{" +
                "id=" + id +
                ", companyName='" + companyName + '\'' +
                ", companySimpleName='" + companySimpleName + '\'' +
                ", businessOwner='" + businessOwner + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", traderId=" + traderId +
                ", traderName='" + traderName + '\'' +
                ", traderPhone='" + traderPhone + '\'' +
                ", officeProvinceCode='" + officeProvinceCode + '\'' +
                ", officeProvince='" + officeProvince + '\'' +
                ", officeCityCode='" + officeCityCode + '\'' +
                ", officeCity='" + officeCity + '\'' +
                ", officeCountryCode='" + officeCountryCode + '\'' +
                ", officeCountry='" + officeCountry + '\'' +
                ", officeDetailAddress='" + officeDetailAddress + '\'' +
                ", registerProvinceCode='" + registerProvinceCode + '\'' +
                ", registerProvince='" + registerProvince + '\'' +
                ", registerCityCode='" + registerCityCode + '\'' +
                ", registerCity='" + registerCity + '\'' +
                ", registerCountryCode='" + registerCountryCode + '\'' +
                ", registerCountry='" + registerCountry + '\'' +
                ", registerDetailAddress='" + registerDetailAddress + '\'' +
                ", officePhone='" + officePhone + '\'' +
                ", openingBank='" + openingBank + '\'' +
                ", legalPersonName='" + legalPersonName + '\'' +
                ", account='" + account + '\'' +
                ", identificationNumword='" + identificationNumword + '\'' +
                ", fax='" + fax + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", enterpriseTypeNumber=" + enterpriseTypeNumber +
                ", enterpriseTypeName='" + enterpriseTypeName + '\'' +
                ", planOutput=" + planOutput +
                ", actualOutput=" + actualOutput +
                ", annualSales=" + annualSales +
                ", annualCoalConsum=" + annualCoalConsum +
                ", annualPurchase=" + annualPurchase +
                ", installCapacity=" + installCapacity +
                ", annual='" + annual + '\'' +
                ", enterpriseNatureNumber=" + enterpriseNatureNumber +
                ", enterpriseNatureName='" + enterpriseNatureName + '\'' +
                ", industry='" + industry + '\'' +
                ", nickName='" + nickName + '\'' +
                ", wechat='" + wechat + '\'' +
                ", qq='" + qq + '\'' +
                ", mail='" + mail + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
