package kitt.core.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.mysql.jdbc.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by fanjun on 14-11-12.
 *
 */
public class Company implements Serializable {
    @NotNull
    private int userid;
    @NotNull
    private String name;
    @NotNull
    private String address;
    @NotBlank
    @Length(max=20,message = "省长度限制为20")
    private String province;
    @NotBlank
    @Length(max=20,message = "市长度限制为20")
    private String city;
    @Length(max=20,message = "区长度限制为20")
    private String country;
    @NotBlank
    @Length(max=70,message = "详细地址长度限制为70")
    private String detailaddress;
    @NotBlank
    @Length(max=50,message = "省编码长度限制为50")
    private String provinceCode;
    @NotBlank
    @Length(max=50,message = "市编码长度限制为50")
    private String cityCode;
    @Length(max=50,message = "区编码长度限制为50")
    private String countryCode;
    private String phone;
    private String legalperson;           //公司法人
    @NotNull
    private String businesslicense;       //营业执照
    @NotNull
    private String identificationnumber;  //纳税人识别号
    @NotNull
    private String organizationcode;      //机构代码
    @NotNull
    private String openinglicense;        //开户许可证
    @NotNull
    private String legalpersonname;       //公司法人姓名
    @NotNull
    private String account;               //银行账号
    @NotNull
    private String openingbank;           //开户银行
    @NotNull
    private String identificationnumword; //纳税人识别号(文字)
    @NotNull
    private String zipcode;               //邮编

    private String operatinglicense;      //经营许可证
    private String invoicinginformation;  //企业开票信息
    private String verifystatus;          //公司信息审核状态
    private String remarks;               //审核未通过理由
    private String traderphone;           //交易员手机
    private String fax;
    private int id;
    private String logopic;               //公司LOGO图片url
    private String bannerpic;             //公司宣传图片url
    private String shortername;           //公司简称
    private boolean istender;             //公司是否是招标状态

    public Company(){

    }

    public Company(String name, String address, String fax, String legalperson, String businesslicense,
                   String identificationnumber, String organizationcode, String operatinglicense, int userid,
                   String legalpersonname, String account, String openingbank, String traderphone){
        this.setName(name);
        this.setAddress(address);
        this.setFax(fax);
        this.setLegalperson(legalperson);
        this.setBusinesslicense(businesslicense);
        this.setIdentificationnumber(identificationnumber);
        this.setOrganizationcode(organizationcode);
        this.setOperatinglicense(operatinglicense);
        this.userid = userid;
        this.setLegalpersonname(legalpersonname);
        this.setAccount(account);
        this.setOpeningbank(openingbank);
        this.setTraderphone(traderphone);
    }

    public Company(String name, String address, String phone, String fax, String legalperson, String businesslicense,
                   String identificationnumber, String organizationcode, String operatinglicense, int userid,
                   String legalpersonname, String account, String openingbank){
        this.setName(name);
        this.setAddress(address);
        this.setPhone(phone);
        this.setFax(fax);
        this.setLegalperson(legalperson);
        this.setBusinesslicense(businesslicense);
        this.setIdentificationnumber(identificationnumber);
        this.setOrganizationcode(organizationcode);
        this.setOperatinglicense(operatinglicense);
        this.userid = userid;
        this.setLegalpersonname(legalpersonname);
        this.setAccount(account);
        this.setOpeningbank(openingbank);
    }

    public Company(String name, String address, String legalperson, String businesslicense, String identificationnumber,
                   String organizationcode, String operatinglicense, int userid, String legalpersonname, String account,
                   String openingbank, String traderphone, String identificationnumword, String zipcode){
        this.setName(name);
        this.setAddress(address);
        this.setLegalperson(legalperson);
        this.setBusinesslicense(businesslicense);
        this.setIdentificationnumber(identificationnumber);
        this.setOrganizationcode(organizationcode);
        this.setOperatinglicense(operatinglicense);
        this.userid = userid;
        this.setLegalpersonname(legalpersonname);
        this.setAccount(account);
        this.setOpeningbank(openingbank);
        this.setTraderphone(traderphone);
        this.setIdentificationnumword(identificationnumword);
        this.setZipcode(zipcode);
    }

    public Company(String name, String province, String city, String country, String detailaddress, String provinceCode, String cityCode, String countryCode, String phone, String fax, String invoicinginformation,
                   String businesslicense, String identificationnumber, String organizationcode, String operatinglicense,
                   String openinglicense, String legalpersonname, String account, String openingbank, int userid,
                   String identificationnumword, String zipcode) {
        this.setName(name);
        this.setProvince(province);
        this.setCity(city);
        this.setCountry(country);
        this.setDetailaddress(detailaddress);
        this.setProvinceCode(provinceCode);
        this.setCityCode(cityCode);
        this.setCountryCode(countryCode);
        this.setPhone(phone);
        this.setFax(fax);
        this.setInvoicinginformation(invoicinginformation);
        this.setBusinesslicense(businesslicense);
        this.setIdentificationnumber(identificationnumber);
        this.setOrganizationcode(organizationcode);
        this.setOperatinglicense(operatinglicense);
        this.setOpeninglicense(openinglicense);
        this.setLegalpersonname(legalpersonname);
        this.setAccount(account);
        this.setOpeningbank(openingbank);
        this.userid = userid;
        this.setIdentificationnumword(identificationnumword);
        this.setZipcode(zipcode);
    }

    public Company(String name, String address, String account, String businesslicense, String fax,
                   String identificationnumber, String legalperson, String legalpersonname, String openingbank,
                   String operatinglicense, String organizationcode, String phone, String remarks, String traderphone,
                   int userid, String verifystatus) {
        this.setName(name);
        this.setAddress(address);
        this.setAccount(account);
        this.setBusinesslicense(businesslicense);
        this.setFax(fax);
        this.setIdentificationnumber(identificationnumber);
        this.setLegalperson(legalperson);
        this.setLegalpersonname(legalpersonname);
        this.setOpeningbank(openingbank);
        this.setOperatinglicense(operatinglicense);
        this.setOrganizationcode(organizationcode);
        this.setPhone(phone);
        this.setRemarks(remarks);
        this.setTraderphone(traderphone);
        this.userid = userid;
        this.setVerifystatus(verifystatus);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (StringUtils.isNullOrEmpty(name)) {
            this.name = name;
        } else {
            this.name = name.trim();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (StringUtils.isNullOrEmpty(address)) {
            this.address = address;
        } else {
            this.address = address.trim();
        }
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        if (StringUtils.isNullOrEmpty(phone)) {
            this.phone = phone;
        } else {
            this.phone = phone.trim();
        }
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        if (StringUtils.isNullOrEmpty(fax)) {
            this.fax = fax;
        } else {
            this.fax = fax.trim();
        }
    }

    public String getLegalperson() {
        return legalperson;
    }

    public void setLegalperson(String legalperson) {
        if (StringUtils.isNullOrEmpty(legalperson)) {
            this.legalperson = legalperson;
        } else {
            this.legalperson = legalperson.trim();
        }
    }

    public String getBusinesslicense() {
        return businesslicense;
    }

    public void setBusinesslicense(String businesslicense) {
        if (StringUtils.isNullOrEmpty(businesslicense)) {
            this.businesslicense = businesslicense;
        } else {
            this.businesslicense = businesslicense.trim();
        }
    }

    public String getIdentificationnumber() {
        return identificationnumber;
    }

    public void setIdentificationnumber(String identificationnumber) {
        if (StringUtils.isNullOrEmpty(identificationnumber)) {
            this.identificationnumber = identificationnumber;
        } else {
            this.identificationnumber = identificationnumber.trim();
        }
    }

    public String getOrganizationcode() {
        return organizationcode;
    }

    public void setOrganizationcode(String organizationcode) {
        if (StringUtils.isNullOrEmpty(organizationcode)) {
            this.organizationcode = organizationcode;
        } else {
            this.organizationcode = organizationcode.trim();
        }
    }

    public String getOperatinglicense() {
        return operatinglicense;
    }

    public void setOperatinglicense(String operatinglicense) {
        if (StringUtils.isNullOrEmpty(operatinglicense)) {
            this.operatinglicense = operatinglicense;
        } else {
            this.operatinglicense = operatinglicense.trim();
        }
    }

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

    public String getVerifystatus() {
        return verifystatus;
    }

    public void setVerifystatus(String verifystatus) {
        if (StringUtils.isNullOrEmpty(verifystatus)) {
            this.verifystatus = verifystatus;
        } else {
            this.verifystatus = verifystatus.trim();
        }
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        if (StringUtils.isNullOrEmpty(remarks)) {
            this.remarks = remarks;
        } else {
            this.remarks = remarks.trim();
        }
    }

    public String getTraderphone() {
        return traderphone;
    }

    public void setTraderphone(String traderphone) {
        if (StringUtils.isNullOrEmpty(traderphone)) {
            this.traderphone = traderphone;
        } else {
            this.traderphone = traderphone.trim();
        }
    }

    public String getLegalpersonname() {
        return legalpersonname;
    }

    public void setLegalpersonname(String legalpersonname) {
        if (StringUtils.isNullOrEmpty(legalpersonname)) {
            this.legalpersonname = legalpersonname;
        } else {
            this.legalpersonname = legalpersonname.trim();
        }
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        if (StringUtils.isNullOrEmpty(account)) {
            this.account = account;
        } else {
            this.account = account.trim();
        }
    }

    public String getOpeningbank() {
        return openingbank;
    }

    public void setOpeningbank(String openingbank) {
        if (StringUtils.isNullOrEmpty(openingbank)) {
            this.openingbank = openingbank;
        } else {
            this.openingbank = openingbank.trim();
        }
    }

    public String getIdentificationnumword() {
        return identificationnumword;
    }

    public void setIdentificationnumword(String identificationnumword) {
        if (StringUtils.isNullOrEmpty(identificationnumword)) {
            this.identificationnumword = identificationnumword;
        } else {
            this.identificationnumword = identificationnumword.trim();
        }
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        if (StringUtils.isNullOrEmpty(zipcode)) {
            this.zipcode = zipcode;
        } else {
            this.zipcode = zipcode.trim();
        }
    }

    public String getOpeninglicense() {
        return openinglicense;
    }

    public void setOpeninglicense(String openinglicense) {
        if (StringUtils.isNullOrEmpty(openinglicense)) {
            this.operatinglicense = openinglicense;
        } else {
            this.openinglicense = openinglicense.trim();
        }
    }

    public String getInvoicinginformation() {
        return invoicinginformation;
    }

    public void setInvoicinginformation(String invoicinginformation) {
        if (StringUtils.isNullOrEmpty(invoicinginformation)) {
            this.invoicinginformation = invoicinginformation;
        } else {
            this.invoicinginformation = invoicinginformation.trim();
        }
    }

    public String getLogopic() {
        return logopic;
    }

    public void setLogopic(String logopic) {
        if (StringUtils.isNullOrEmpty(logopic)) {
            this.logopic = logopic;
        } else {
            this.logopic = logopic.trim();
        }
    }

    public String getBannerpic() {
        return bannerpic;
    }

    public void setBannerpic(String bannerpic) {
        if (StringUtils.isNullOrEmpty(bannerpic)) {
            this.bannerpic = bannerpic;
        } else {
            this.bannerpic = bannerpic.trim();
        }
    }

    public String getShortername() {
        return shortername;
    }

    public void setShortername(String shortername) {
        if (StringUtils.isNullOrEmpty(shortername)) {
            this.shortername = shortername;
        } else {
            this.shortername = shortername.trim();
        }
    }

    public boolean istender() {
        return istender;
    }

    public void setIstender(boolean istender) {
        this.istender = istender;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDetailaddress() {
        return detailaddress;
    }

    public void setDetailaddress(String detailaddress) {
        this.detailaddress = detailaddress;
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

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }


}
