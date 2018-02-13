package kitt.core.domain;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by lich on 16/1/8.
 */
public class Shipintention {
    private int id;
    @Length(max=30,message = "装车码头长度限制为30")
    //@NotBlank(message = "装车码头不能为空")
    private String loaddock;
    @Length(max=30,message = "装车港长度限制为30")
    @NotBlank(message = "装车港不能为空")
    private String loadport;
    private String otherloadport;
    @Length(max=30,message = "卸车码头长度限制为30")
    //@NotBlank(message = "卸车码头不能为空")
    private String unloaddock;
    @Length(max=30,message = "卸车港长度限制为30")
    @NotBlank(message = "卸车港不能为空")
    private String unloadport;
    private String otherunloadport;
    @Length(max=30,message = "船编号长度限制为30")
//    @NotBlank(message = "船编号不能为空")
    private String shipcode;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate receiptdate;
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
    private String mobile;
    @Length(max=50,message = "公司名字长度限制为50")
//    @NotBlank(message = "公司名字不能为空")
    private String companyname;
    private String remark;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLoaddock() {
        return loaddock;
    }

    public void setLoaddock(String loaddock) {
        this.loaddock = loaddock;
    }

    public String getLoadport() {
        return loadport;
    }

    public void setLoadport(String loadport) {
        this.loadport = loadport;
    }

    public String getUnloaddock() {
        return unloaddock;
    }

    public void setUnloaddock(String unloaddock) {
        this.unloaddock = unloaddock;
    }

    public String getUnloadport() {
        return unloadport;
    }

    public void setUnloadport(String unloadport) {
        this.unloadport = unloadport;
    }

    public String getShipcode() {
        return shipcode;
    }

    public void setShipcode(String shipcode) {
        this.shipcode = shipcode;
    }

    public LocalDate getReceiptdate() {
        return receiptdate;
    }

    public void setReceiptdate(LocalDate receiptdate) {
        this.receiptdate = receiptdate;
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

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOtherloadport() {
        return otherloadport;
    }

    public void setOtherloadport(String otherloadport) {
        this.otherloadport = otherloadport;
    }

    public String getOtherunloadport() {
        return otherunloadport;
    }

    public void setOtherunloadport(String otherunloadport) {
        this.otherunloadport = otherunloadport;
    }


    @Override
    public String toString() {
        return "Shipintention{" +
                "id=" + id +
                ", loaddock='" + loaddock + '\'' +
                ", loadport='" + loadport + '\'' +
                ", otherloadport='" + otherloadport + '\'' +
                ", unloaddock='" + unloaddock + '\'' +
                ", unloadport='" + unloadport + '\'' +
                ", otherunloadport='" + otherunloadport + '\'' +
                ", shipcode='" + shipcode + '\'' +
                ", receiptdate=" + receiptdate +
                ", goodsType='" + goodsType + '\'' +
                ", goodsWeight=" + goodsWeight +
                ", contacts='" + contacts + '\'' +
                ", mobile='" + mobile + '\'' +
                ", companyname='" + companyname + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
