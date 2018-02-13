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
public class PotentialCompany extends BaseEntity implements Serializable {
    private int id;
    @NotBlank(message = "公司名称不能为空")
    @Length(max = 20, message = "公司名称不能超过20个字")
    private String companyname;           //公司名称
    @NotBlank(message = "公司简称不能为空")
    @Length(max = 20, message = "公司简称不能超过20个字")
    private String companysimplename;     //公司简称
    @NotBlank(message = "请填写业务负责人")
    @Length(max = 10, message = "业务负责人不能超过10个字")
    private String businessowner;         //业务负责人
    @NotBlank(message = "请填写手机号")
    @Length(max = 11, message = "请输入正确的手机号")
    private String userphone;             //用户手机
    @NotNull(message = "请选择交易员")
    private int tradercode;               //交易员编号
    private String tradername;            //交易员姓名
    private String traderphone;           //交易员手机号
    @NotNull(message = "请选择办公地点省份")
    private String officeprovincecode;    //办公地点省code
    private String officeprovince;        //办公地点省
    @NotNull(message = "请选择办公地点市")
    private String officecitycode;        //办公地点市code
    private String officecity;            //办公地点市
    private String officecountrycode;     //办公地点区code
    private String officecountry;         //办公地点区
    @Length(message = "最多输入20个字")
    private String officedetailaddress;   //办公地点详细地址
    private String registerprovincecode;  //注册地点省code
    private String registerprovince;      //注册地点省
    private String registercitycode;      //注册地点市code
    private String registercity;          //注册地点市
    private String registercountrycode;   //注册地点区code
    private String registercountry;       //注册地点区
    @Length(message = "最多输入20个字")
    private String registerdetailaddress; //注册地点详细地址
    @Length(message = "最多输入20个字")
    private String officephone;           //办公电话
    @NotBlank(message = "请输入开户银行")
    @Length(max = 50, message = "最多输入50个字")
    private String openingbank;           //开户银行
    @Length(message = "最多输入20个字")
    private String legalpersonname;       //法人姓名
    @Length(max = 30, message = "最多输入30个数字")
    private String account;               //银行账号
    private String identificationnumword; //纳税人识别号
    private String fax;                   //传真
    private String zipcode;               //邮编
    @NotNull(message = "请选择企业类型")
    private int enterprisetypenumber;     //企业类型id
    private String enterprisetypename;    //企业类型名称
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal planoutput;        //设计产能
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal actualoutput;      //实际产能
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal annualsales;       //年销售量
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal annualcoalconsum;  //年耗煤量
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal annualpurchase;    //年采购量
    @Length(max = 6, message = "最多只能输入6位")
    @DecimalMin(value = "0.01", message = "数值不能小于0.01")
    @DecimalMax(value = "9999", message = "数值不能超过9999")
    private BigDecimal installcapacity;   //装机容量
    private String annual;                //年度
    @NotNull(message = "请选择企业性质")
    private int enterprisenaturenumber;   //企业性质id
    private String enterprisenaturename;  //企业性质名称
    @Length(max = 20, message = "不能超过20个字")
    private String industry;              //具体行业
    @Length(max = 20, message = "不能超过20个字")
    private String nickname;              //负责人绰号
    @Length(max = 20, message = "不能超过20个字")
    private String wechat;                //微信号
    @Length(max = 20, message = "不能超过20个字")
    private String qq;                    //qq号
    @Length(max = 20, message = "不能超过20个字")
    @Email(message = "请输入正确的邮箱")
    private String mail;                  //邮箱
    @Length(max = 20, message = "不能超过20个字")
    private String remark;                //备注

    public PotentialCompany() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getCompanysimplename() {
        return companysimplename;
    }

    public void setCompanysimplename(String companysimplename) {
        this.companysimplename = companysimplename;
    }

    public String getBusinessowner() {
        return businessowner;
    }

    public String getAnnual() {
        return annual;
    }

    public void setAnnual(String annual) {
        this.annual = annual;
    }

    public void setBusinessowner(String businessowner) {
        this.businessowner = businessowner;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public int getTradercode() {
        return tradercode;
    }

    public void setTradercode(int tradercode) {
        this.tradercode = tradercode;
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

    public String getOfficeprovincecode() {
        return officeprovincecode;
    }

    public void setOfficeprovincecode(String officeprovincecode) {
        this.officeprovincecode = officeprovincecode;
    }

    public String getOfficeprovince() {
        return officeprovince;
    }

    public void setOfficeprovince(String officeprovince) {
        this.officeprovince = officeprovince;
    }

    public String getOfficecitycode() {
        return officecitycode;
    }

    public void setOfficecitycode(String officecitycode) {
        this.officecitycode = officecitycode;
    }

    public String getOfficecity() {
        return officecity;
    }

    public void setOfficecity(String officecity) {
        this.officecity = officecity;
    }

    public String getOfficecountrycode() {
        return officecountrycode;
    }

    public void setOfficecountrycode(String officecountrycode) {
        this.officecountrycode = officecountrycode;
    }

    public String getOfficecountry() {
        return officecountry;
    }

    public void setOfficecountry(String officecountry) {
        this.officecountry = officecountry;
    }

    public String getOfficedetailaddress() {
        return officedetailaddress;
    }

    public void setOfficedetailaddress(String officedetailaddress) {
        this.officedetailaddress = officedetailaddress;
    }

    public String getRegisterprovincecode() {
        return registerprovincecode;
    }

    public void setRegisterprovincecode(String registerprovincecode) {
        this.registerprovincecode = registerprovincecode;
    }

    public String getRegisterprovince() {
        return registerprovince;
    }

    public void setRegisterprovince(String registerprovince) {
        this.registerprovince = registerprovince;
    }

    public String getRegistercitycode() {
        return registercitycode;
    }

    public void setRegistercitycode(String registercitycode) {
        this.registercitycode = registercitycode;
    }

    public String getRegistercity() {
        return registercity;
    }

    public void setRegistercity(String registercity) {
        this.registercity = registercity;
    }

    public String getRegistercountrycode() {
        return registercountrycode;
    }

    public void setRegistercountrycode(String registercountrycode) {
        this.registercountrycode = registercountrycode;
    }

    public String getRegistercountry() {
        return registercountry;
    }

    public void setRegistercountry(String registercountry) {
        this.registercountry = registercountry;
    }

    public String getRegisterdetailaddress() {
        return registerdetailaddress;
    }

    public void setRegisterdetailaddress(String registerdetailaddress) {
        this.registerdetailaddress = registerdetailaddress;
    }

    public String getOfficephone() {
        return officephone;
    }

    public void setOfficephone(String officephone) {
        this.officephone = officephone;
    }

    public String getOpeningbank() {
        return openingbank;
    }

    public void setOpeningbank(String openingbank) {
        this.openingbank = openingbank;
    }

    public String getLegalpersonname() {
        return legalpersonname;
    }

    public void setLegalpersonname(String legalpersonname) {
        this.legalpersonname = legalpersonname;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getIdentificationnumword() {
        return identificationnumword;
    }

    public void setIdentificationnumword(String identificationnumword) {
        this.identificationnumword = identificationnumword;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public int getEnterprisetypenumber() {
        return enterprisetypenumber;
    }

    public void setEnterprisetypenumber(int enterprisetypenumber) {
        this.enterprisetypenumber = enterprisetypenumber;
    }

    public String getEnterprisetypename() {
        return enterprisetypename;
    }

    public void setEnterprisetypename(String enterprisetypename) {
        this.enterprisetypename = enterprisetypename;
    }

    public BigDecimal getPlanoutput() {
        return planoutput;
    }

    public void setPlanoutput(BigDecimal planoutput) {
        this.planoutput = planoutput;
    }

    public BigDecimal getActualoutput() {
        return actualoutput;
    }

    public void setActualoutput(BigDecimal actualoutput) {
        this.actualoutput = actualoutput;
    }

    public BigDecimal getAnnualsales() {
        return annualsales;
    }

    public void setAnnualsales(BigDecimal annualsales) {
        this.annualsales = annualsales;
    }

    public BigDecimal getAnnualcoalconsum() {
        return annualcoalconsum;
    }

    public void setAnnualcoalconsum(BigDecimal annualcoalconsum) {
        this.annualcoalconsum = annualcoalconsum;
    }

    public BigDecimal getAnnualpurchase() {
        return annualpurchase;
    }

    public void setAnnualpurchase(BigDecimal annualpurchase) {
        this.annualpurchase = annualpurchase;
    }

    public BigDecimal getInstallcapacity() {
        return installcapacity;
    }

    public void setInstallcapacity(BigDecimal installcapacity) {
        this.installcapacity = installcapacity;
    }

    public int getEnterprisenaturenumber() {
        return enterprisenaturenumber;
    }

    public void setEnterprisenaturenumber(int enterprisenaturenumber) {
        this.enterprisenaturenumber = enterprisenaturenumber;
    }

    public String getEnterprisenaturename() {
        return enterprisenaturename;
    }

    public void setEnterprisenaturename(String enterprisenaturename) {
        this.enterprisenaturename = enterprisenaturename;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
        return "PotentialCompany{" +
                "id=" + id +
                ", companyname='" + companyname + '\'' +
                ", companysimplename='" + companysimplename + '\'' +
                ", businessowner='" + businessowner + '\'' +
                ", userphone='" + userphone + '\'' +
                ", tradercode=" + tradercode +
                ", tradername='" + tradername + '\'' +
                ", traderphone='" + traderphone + '\'' +
                ", officeprovincecode='" + officeprovincecode + '\'' +
                ", officeprovince='" + officeprovince + '\'' +
                ", officecitycode='" + officecitycode + '\'' +
                ", officecity='" + officecity + '\'' +
                ", officecountrycode='" + officecountrycode + '\'' +
                ", officecountry='" + officecountry + '\'' +
                ", officedetailaddress='" + officedetailaddress + '\'' +
                ", registerprovincecode='" + registerprovincecode + '\'' +
                ", registerprovince='" + registerprovince + '\'' +
                ", registercitycode='" + registercitycode + '\'' +
                ", registercity='" + registercity + '\'' +
                ", registercountrycode='" + registercountrycode + '\'' +
                ", registercountry='" + registercountry + '\'' +
                ", registerdetailaddress='" + registerdetailaddress + '\'' +
                ", officephone='" + officephone + '\'' +
                ", openingbank='" + openingbank + '\'' +
                ", legalpersonname='" + legalpersonname + '\'' +
                ", account='" + account + '\'' +
                ", identificationnumword='" + identificationnumword + '\'' +
                ", fax='" + fax + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", enterprisetypenumber=" + enterprisetypenumber +
                ", enterprisetypename='" + enterprisetypename + '\'' +
                ", planoutput=" + planoutput +
                ", actualoutput=" + actualoutput +
                ", annual=" + annual +
                ", enterprisenaturenumber=" + enterprisenaturenumber +
                ", enterprisenaturename='" + enterprisenaturename + '\'' +
                ", industry='" + industry + '\'' +
                ", nickname='" + nickname + '\'' +
                ", wechat='" + wechat + '\'' +
                ", qq='" + qq + '\'' +
                ", mail='" + mail + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
