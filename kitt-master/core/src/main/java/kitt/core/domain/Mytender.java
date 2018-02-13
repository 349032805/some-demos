package kitt.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Created by lich on 15/11/12.
 */
public class Mytender implements Serializable {
    private int id;
    private int releaseuserid;          //发布公告用户id
    private int competeuserid;          //竞标用户id
    private int competecompanyid;       //竞标公司id
    private int tenderdeclarationid;    //竞标公告id
    private int tenderitemid;           //公告所属项目id
    private int tenderpacketid;         //项目所属标包id
    private String releasecompanyname;  //发布公告公司名称
    private String competecompanyname;  //竞标公告公司名称
    private LocalDate tenderdate;       //投标日期
    private LocalDate verifydate;       //审核日期
    private String tendercode;          //招标编号
    private String status;              //状态
    private String paymentstatus;       //支付状态
    private int releasecompanyid;
    private int bidid;


    @NotNull(message="煤炭种类不能为空")
    private String coaltype;

    @NotNull(message="NCV不能为空")
    @JsonProperty("NCV")
    private Integer NCV;

    @NotNull(message="TM不能为空")
    @JsonProperty("TM")
    private BigDecimal TM;

    @NotNull(message="RS不能为空")
    @JsonProperty("RS")
    private BigDecimal RS;

    @NotNull(message="ADV不能为空")
    @JsonProperty("ADV")
    private BigDecimal ADV;

    @NotNull(message="ADV02不能为空")
    @JsonProperty("ADV02")
    private BigDecimal ADV02;

    @NotNull(message="供应吨数不能为空")
    private BigDecimal supplyamount;

    @Length(max=30,message = "运输方式限制为30")
    @NotBlank(message="运输方式不能为空")
    private String deliverymode;

    @Length(max=30,message = "发货地限制为30")
    @NotBlank(message="发货地不能为空")
    private String departurepoint;

    @NotNull(message="价格不能为空")
    @Max(value = 10000,message = "投标价格不能超过10000")
    private BigDecimal price;

    private int iscommit;    //判断是否提交

    private BigDecimal needamount;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getBidid() {
        return bidid;
    }

    public void setBidid(int bidid) {
        this.bidid = bidid;
    }

    public int getCompetecompanyid() {
        return competecompanyid;
    }

    public void setCompetecompanyid(int competecompanyid) {
        this.competecompanyid = competecompanyid;
    }

    public int getTenderdeclarationid() {
        return tenderdeclarationid;
    }

    public void setTenderdeclarationid(int tenderdeclarationid) {
        this.tenderdeclarationid = tenderdeclarationid;
    }

    public int getTenderitemid() {
        return tenderitemid;
    }

    public void setTenderitemid(int tenderitemid) {
        this.tenderitemid = tenderitemid;
    }

    public int getTenderpacketid() {
        return tenderpacketid;
    }

    public void setTenderpacketid(int tenderpacketid) {
        this.tenderpacketid = tenderpacketid;
    }

    public String getReleasecompanyname() {
        return releasecompanyname;
    }

    public void setReleasecompanyname(String releasecompanyname) {
        this.releasecompanyname = releasecompanyname;
    }

    public String getCompetecompanyname() {
        return competecompanyname;
    }

    public void setCompetecompanyname(String competecompanyname) {
        this.competecompanyname = competecompanyname;
    }

    public LocalDate getTenderdate() {
        return tenderdate;
    }

    public void setTenderdate(LocalDate tenderdate) {
        this.tenderdate = tenderdate;
    }

    public LocalDate getVerifydate() {
        return verifydate;
    }

    public void setVerifydate(LocalDate verifydate) {
        this.verifydate = verifydate;
    }

    public String getTendercode() {
        return tendercode;
    }

    public void setTendercode(String tendercode) {
        this.tendercode = tendercode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCoaltype() {
        return coaltype;
    }

    public void setCoaltype(String coaltype) {
        this.coaltype = coaltype;
    }

    public Integer getNCV() {
        return NCV;
    }

    public void setNCV(Integer NCV) {
        this.NCV = NCV;
    }

    public BigDecimal getTM() {
        return TM;
    }

    public void setTM(BigDecimal TM) {
        this.TM = TM;
    }

    public BigDecimal getRS() {
        return RS;
    }

    public void setRS(BigDecimal RS) {
        this.RS = RS;
    }

    public BigDecimal getADV() {
        return ADV;
    }

    public void setADV(BigDecimal ADV) {
        this.ADV = ADV;
    }

    public BigDecimal getADV02() {
        return ADV02;
    }

    public void setADV02(BigDecimal ADV02) {
        this.ADV02 = ADV02;
    }

    public BigDecimal getSupplyamount() {
        return supplyamount;
    }

    public void setSupplyamount(BigDecimal supplyamount) {
        this.supplyamount = supplyamount;
    }

    public String getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(String deliverymode) {
        this.deliverymode = deliverymode;
    }

    public String getDeparturepoint() {
        return departurepoint;
    }

    public void setDeparturepoint(String departurepoint) {
        this.departurepoint = departurepoint;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getReleaseuserid() {
        return releaseuserid;
    }

    public void setReleaseuserid(int releaseuserid) {
        this.releaseuserid = releaseuserid;
    }

    public int getCompeteuserid() {
        return competeuserid;
    }

    public void setCompeteuserid(int competeuserid) {
        this.competeuserid = competeuserid;
    }

    public int getReleasecompanyid() {
        return releasecompanyid;
    }

    public void setReleasecompanyid(int releasecompanyid) {
        this.releasecompanyid = releasecompanyid;
    }

    public int getIscommit() {
        return iscommit;
    }

    public void setIscommit(int iscommit) {
        this.iscommit = iscommit;
    }

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }

    public BigDecimal getNeedamount() {
        return needamount;
    }

    public void setNeedamount(BigDecimal needamount) {
        this.needamount = needamount;
    }

    @Override
    public String toString() {
        return "Mytender{" +
                "id=" + id +
                ", releaseuserid=" + releaseuserid +
                ", competeuserid=" + competeuserid +
                ", competecompanyid=" + competecompanyid +
                ", tenderdeclarationid=" + tenderdeclarationid +
                ", tenderitemid=" + tenderitemid +
                ", tenderpacketid=" + tenderpacketid +
                ", releasecompanyname='" + releasecompanyname + '\'' +
                ", competecompanyname='" + competecompanyname + '\'' +
                ", tenderdate=" + tenderdate +
                ", verifydate=" + verifydate +
                ", tendercode='" + tendercode + '\'' +
                ", status='" + status + '\'' +
                ", paymentstatus='" + paymentstatus + '\'' +
                ", releasecompanyid=" + releasecompanyid +
                ", coaltype='" + coaltype + '\'' +
                ", NCV=" + NCV +
                ", TM=" + TM +
                ", RS=" + RS +
                ", ADV=" + ADV +
                ", ADV02=" + ADV02 +
                ", supplyamount=" + supplyamount +
                ", deliverymode='" + deliverymode + '\'' +
                ", departurepoint='" + departurepoint + '\'' +
                ", price=" + price +
                ", iscommit=" + iscommit +
                '}';
    }
}
