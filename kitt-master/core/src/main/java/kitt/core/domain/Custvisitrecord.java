package kitt.core.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by lich on 16/1/19.
 */
public class Custvisitrecord {
    private int id;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime visitetime;
    @Length(max=50,message = "公司名称长度限制为50")
    @NotBlank(message = "公司名称不能为空")
    private String companyname;
    @Length(max=15,message = "企业类型长度限制为15")
    //@NotBlank(message = "企业类型不能为空")
    private String enterpricetype;
    private String enterpriceprovince;
    private String enterpriceprovinceStr;
    private String enterpricecity;
    private String enterpricecityStr;
    @Length(max=70,message = "企业所在地详细地址长度限制为70")
    private String enterpricelocationdetail;
    @Length(max=15,message = "需求类型长度限制为15")
    //@NotBlank(message = "需求类型不能为空")
    private String needtype;
    @Length(max=100,message = "信息来源长度限制为100")
    private String infoorigin;
    @Length(max=500,message = "问题描述长度限制为500")
    private String problemdesc;
    @Length(max=500,message = "解答情况长度限制为500")
    private String answerinfo;
    @Length(max=50,message = "线上负责长度限制为50")
    private String onlinepeople;
    private String offlineteam;
    private String offlineteampeople;
    private String offlineteampeopleStr;
    @Length(max=500,message = "线下结果反馈长度限制为500")
    private String resultfeedback;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime returnvisittime;
    @Length(max=500,message = "回访结果长度限制为500")
    private String returnvisitresult;
    //@Length(max=11,message = "注册手机号长度限制为11")
    private String registerphone;
    private String hangtype;
    @Digits(integer=11, fraction=2,message = "挂单量长度限制为11")
    private BigDecimal hangnumber;
    @Digits(integer=11, fraction=2,message = "交易量长度限制为11")
    private BigDecimal tradenumber;
    @Digits(integer=11, fraction=2,message = "交易额长度限制为11")
    private BigDecimal tradeprice;
    private String coaltype;
    @Digits(integer=11, fraction=2,message = "需求供应量长度限制为11")
    private BigDecimal needamout;
    private String pickupgoodstype;
    private String paytype;
    private String pickupprovince;
    private String pickupcity;
    private String pickupcityStr;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pickuptime;
    private String ncv1;
    private String tm1;
    private String rs1;
    private String ncv2;
    private String tm2;
    private String rs2;
    private String adv1;
    private String adv2;
    @Length(max=100,message = "其他指标长度限制为100")
    private String otherindicators;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;
    @Length(max=200,message = "修改备注长度限制为200")
    //@NotBlank(message="修改备注不能为空")
    private String remark;
    @Length(max=15,message = "其他需求类型长度限制为15")
    private String otherneedtype;
    @Length(max=15,message = "其他企业类型长度限制为15")
    private  String otherenterpricetype;
    @Length(max=15,message = "其他煤种类型长度限制为15")
    private String  othercoaltype;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getVisitetime() {
        return visitetime;
    }

    public void setVisitetime(LocalDateTime visitetime) {
        this.visitetime = visitetime;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getEnterpricetype() {
        return enterpricetype;
    }

    public void setEnterpricetype(String enterpricetype) {
        this.enterpricetype = enterpricetype;
    }

    public String getEnterpriceprovince() {
        return enterpriceprovince;
    }

    public void setEnterpriceprovince(String enterpriceprovince) {
        this.enterpriceprovince = enterpriceprovince;
    }

    public String getEnterpriceprovinceStr() {
        return enterpriceprovinceStr;
    }

    public void setEnterpriceprovinceStr(String enterpriceprovinceStr) {
        this.enterpriceprovinceStr = enterpriceprovinceStr;
    }

    public String getEnterpricecity() {
        return enterpricecity;
    }

    public void setEnterpricecity(String enterpricecity) {
        this.enterpricecity = enterpricecity;
    }

    public String getEnterpricecityStr() {
        return enterpricecityStr;
    }

    public void setEnterpricecityStr(String enterpricecityStr) {
        this.enterpricecityStr = enterpricecityStr;
    }

    public String getEnterpricelocationdetail() {
        return enterpricelocationdetail;
    }

    public void setEnterpricelocationdetail(String enterpricelocationdetail) {
        this.enterpricelocationdetail = enterpricelocationdetail;
    }

    public String getNeedtype() {
        return needtype;
    }

    public void setNeedtype(String needtype) {
        this.needtype = needtype;
    }

    public String getInfoorigin() {
        return infoorigin;
    }

    public void setInfoorigin(String infoorigin) {
        this.infoorigin = infoorigin;
    }

    public String getProblemdesc() {
        return problemdesc;
    }

    public void setProblemdesc(String problemdesc) {
        this.problemdesc = problemdesc;
    }

    public String getAnswerinfo() {
        return answerinfo;
    }

    public void setAnswerinfo(String answerinfo) {
        this.answerinfo = answerinfo;
    }

    public String getOnlinepeople() {
        return onlinepeople;
    }

    public void setOnlinepeople(String onlinepeople) {
        this.onlinepeople = onlinepeople;
    }

    public String getOfflineteam() {
        return offlineteam;
    }

    public void setOfflineteam(String offlineteam) {
        this.offlineteam = offlineteam;
    }

    public String getOfflineteampeople() {
        return offlineteampeople;
    }

    public void setOfflineteampeople(String offlineteampeople) {
        this.offlineteampeople = offlineteampeople;
    }

    public String getOfflineteampeopleStr() {
        return offlineteampeopleStr;
    }

    public void setOfflineteampeopleStr(String offlineteampeopleStr) {
        this.offlineteampeopleStr = offlineteampeopleStr;
    }

    public String getResultfeedback() {
        return resultfeedback;
    }

    public void setResultfeedback(String resultfeedback) {
        this.resultfeedback = resultfeedback;
    }

    public LocalDateTime getReturnvisittime() {
        return returnvisittime;
    }

    public void setReturnvisittime(LocalDateTime returnvisittime) {
        this.returnvisittime = returnvisittime;
    }

    public String getReturnvisitresult() {
        return returnvisitresult;
    }

    public void setReturnvisitresult(String returnvisitresult) {
        this.returnvisitresult = returnvisitresult;
    }

    public String getRegisterphone() {
        return registerphone;
    }

    public void setRegisterphone(String registerphone) {
        this.registerphone = registerphone;
    }

    public String getHangtype() {
        return hangtype;
    }

    public void setHangtype(String hangtype) {
        this.hangtype = hangtype;
    }

    public String getCoaltype() {
        return coaltype;
    }

    public void setCoaltype(String coaltype) {
        this.coaltype = coaltype;
    }



    public String getPickupgoodstype() {
        return pickupgoodstype;
    }

    public void setPickupgoodstype(String pickupgoodstype) {
        this.pickupgoodstype = pickupgoodstype;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getPickupprovince() {
        return pickupprovince;
    }

    public void setPickupprovince(String pickupprovince) {
        this.pickupprovince = pickupprovince;
    }

    public String getPickupcity() {
        return pickupcity;
    }

    public void setPickupcity(String pickupcity) {
        this.pickupcity = pickupcity;
    }

    public String getPickupcityStr() {
        return pickupcityStr;
    }

    public void setPickupcityStr(String pickupcityStr) {
        this.pickupcityStr = pickupcityStr;
    }

    public LocalDateTime getPickuptime() {
        return pickuptime;
    }

    public void setPickuptime(LocalDateTime pickuptime) {
        this.pickuptime = pickuptime;
    }

    public String getNcv1() {
        return ncv1;
    }

    public void setNcv1(String ncv1) {
        this.ncv1 = ncv1;
    }

    public String getTm1() {
        return tm1;
    }

    public void setTm1(String tm1) {
        this.tm1 = tm1;
    }

    public String getRs1() {
        return rs1;
    }

    public void setRs1(String rs1) {
        this.rs1 = rs1;
    }

    public String getNcv2() {
        return ncv2;
    }

    public void setNcv2(String ncv2) {
        this.ncv2 = ncv2;
    }

    public String getTm2() {
        return tm2;
    }

    public void setTm2(String tm2) {
        this.tm2 = tm2;
    }

    public String getRs2() {
        return rs2;
    }

    public void setRs2(String rs2) {
        this.rs2 = rs2;
    }

    public String getAdv1() {
        return adv1;
    }

    public void setAdv1(String adv1) {
        this.adv1 = adv1;
    }

    public String getAdv2() {
        return adv2;
    }

    public void setAdv2(String adv2) {
        this.adv2 = adv2;
    }

    public String getOtherindicators() {
        return otherindicators;
    }

    public void setOtherindicators(String otherindicators) {
        this.otherindicators = otherindicators;
    }

    public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getHangnumber() {
        return hangnumber;
    }

    public void setHangnumber(BigDecimal hangnumber) {
        this.hangnumber = hangnumber;
    }

    public BigDecimal getTradenumber() {
        return tradenumber;
    }

    public void setTradenumber(BigDecimal tradenumber) {
        this.tradenumber = tradenumber;
    }

    public BigDecimal getTradeprice() {
        return tradeprice;
    }

    public void setTradeprice(BigDecimal tradeprice) {
        this.tradeprice = tradeprice;
    }

    public BigDecimal getNeedamout() {
        return needamout;
    }

    public void setNeedamout(BigDecimal needamout) {
        this.needamout = needamout;
    }

    public String getOtherneedtype() {
        return otherneedtype;
    }

    public void setOtherneedtype(String otherneedtype) {
        this.otherneedtype = otherneedtype;
    }

    public String getOtherenterpricetype() {
        return otherenterpricetype;
    }

    public void setOtherenterpricetype(String otherenterpricetype) {
        this.otherenterpricetype = otherenterpricetype;
    }

    public String getOthercoaltype() {
        return othercoaltype;
    }

    public void setOthercoaltype(String othercoaltype) {
        this.othercoaltype = othercoaltype;
    }
}
