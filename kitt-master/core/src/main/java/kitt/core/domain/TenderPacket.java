package kitt.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 15/11/10.
 */
public class TenderPacket implements Serializable {
    private int id;
    private int userid;                                  //招标方用户id
    private Integer tenderitemid;                        //招标项目id
    private int tenderdeclarationid;                     //招标公告id
    private int sequence;                                //顺序
    @Length(max=10,message = "标包段长度最大为10个字符")
    //@NotNull(message = "标包段不能为空")
    private String tenderpacket;                          //标包段
    @Length(max=5,message = "煤炭种类长度最大为5个字符")
    @NotNull(message = "煤炭种类不能为空")
    private String coaltype;                              //煤炭种类
    @NotNull(message = "低位热值不能为空")
    @Range(min=1, max=7500)
    @JsonProperty("NCV")
    private Integer NCV;                                  //低位热值
    @NotNull(message = "全水分不能为空")
    @Range(min=0, max=50)
    @JsonProperty("TM")
    private BigDecimal TM;                                //全水分
    @NotNull(message = "收到基全硫不能为空")
    @Range(min=0, max=10)
    @JsonProperty("RS")
    private BigDecimal RS;                                //收到基全硫
    @NotNull(message = "空气干燥基挥发份不能为空")
    @Range(min=0, max=50)
    @JsonProperty("ADV")
    private BigDecimal ADV;                               //空气干燥基挥发份
    @Range(min=0, max=50)
    @JsonProperty("ADV02")
    private BigDecimal ADV02;                             //空气干燥基挥发份
    @NotNull(message = "采购量不能为空")
    private BigDecimal purchaseamount;                    //采购量
//    @NotNull(message = "最高投标限价不能为空")
    private BigDecimal highestprice;                      //最高投标限价
//    @NotNull(message = "最低投标量不能为空")
    private BigDecimal minamount;                         //最低投标量
    @Length(max=30,message = "运输方式长度最大为30个字符")
    @NotNull(message = "运输方式不能为空")
    private String deliverymode;                          //运输方式
    private Integer version;                              //数据版本号，控制并发
    List<Mytender> mytenderList;                          //投标信息列表
    List<Map<String,Object>>  tbList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTenderitemid() {
        return tenderitemid;
    }

    public void setTenderitemid(Integer tenderitemid) {
        this.tenderitemid = tenderitemid;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getTenderpacket() {
        return tenderpacket;
    }

    public void setTenderpacket(String tenderpacket) {
        this.tenderpacket = tenderpacket;
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

    public BigDecimal getPurchaseamount() {
        return purchaseamount;
    }

    public void setPurchaseamount(BigDecimal purchaseamount) {
        this.purchaseamount = purchaseamount;
    }

    public BigDecimal getHighestprice() {
        return highestprice;
    }

    public void setHighestprice(BigDecimal highestprice) {
        this.highestprice = highestprice;
    }

    public BigDecimal getMinamount() {
        return minamount;
    }

    public void setMinamount(BigDecimal minamount) {
        this.minamount = minamount;
    }

    public String getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(String deliverymode) {
        this.deliverymode = deliverymode;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<Mytender> getMytenderList() {
        return mytenderList;
    }

    public void setMytenderList(List<Mytender> mytenderList) {
        this.mytenderList = mytenderList;
    }

    public int getTenderdeclarationid() {
        return tenderdeclarationid;
    }

    public void setTenderdeclarationid(int tenderdeclarationid) {
        this.tenderdeclarationid = tenderdeclarationid;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public BigDecimal getADV02() {
        return ADV02;
    }

    public void setADV02(BigDecimal ADV02) {
        this.ADV02 = ADV02;
    }

    public TenderPacket() {}

    public TenderPacket(Integer tenderitemid, int tenderdeclarationid, int sequence, String tenderpacket, String coaltype, Integer NCV, BigDecimal TM, BigDecimal RS, BigDecimal ADV,BigDecimal ADV02, BigDecimal purchaseamount, BigDecimal highestprice, BigDecimal minamount, String deliverymode, String status, Integer version, LocalDateTime createtime, LocalDateTime lastupdatetime) {
        this.tenderitemid = tenderitemid;
        this.tenderdeclarationid = tenderdeclarationid;
        this.sequence = sequence;
        this.tenderpacket = tenderpacket;
        this.coaltype = coaltype;
        this.NCV = NCV;
        this.TM = TM;
        this.RS = RS;
        this.ADV = ADV;
        this.ADV02=ADV02;
        this.purchaseamount = purchaseamount;
        this.highestprice = highestprice;
        this.minamount = minamount;
        this.deliverymode = deliverymode;
    }

    @Override
    public String toString() {
        return "TenderPacket{" +
                "id=" + id +
                ", userid=" + userid +
                ", tenderitemid=" + tenderitemid +
                ", tenderdeclarationid=" + tenderdeclarationid +
                ", sequence=" + sequence +
                ", tenderpacket='" + tenderpacket + '\'' +
                ", coaltype='" + coaltype + '\'' +
                ", NCV=" + NCV +
                ", TM=" + TM +
                ", RS=" + RS +
                ", ADV=" + ADV +
                ", ADV02=" + ADV02 +
                ", purchaseamount=" + purchaseamount +
                ", highestprice=" + highestprice +
                ", minamount=" + minamount +
                ", deliverymode='" + deliverymode + '\'' +
                ", version=" + version +
                ", mytenderList=" + mytenderList +
                ", tbList=" + tbList +
                '}';
    }

    public List<Map<String, Object>> getTbList() {
        return tbList;
    }

    public void setTbList(List<Map<String, Object>> tbList) {
        this.tbList = tbList;
    }
}
