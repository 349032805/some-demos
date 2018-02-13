package kitt.core.domain;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by zhangbolun on 15/11/10.
 */
public class TenderItem implements Serializable {
    private int id;
    private int userid;                    //招标方用户id
    private Integer tenderdeclarationid;   //招标公告id
    @Length(max=100,message = "采购内容长度最大为100个字符")
    //@NotNull(message = "采购内容不能为空")
    private String purchasecontent;         //采购内容
    @Length(max=30,message = "项目长度最大为30个字符")
    private String item;                    //项目
    private int sequence;                   //顺序
    @Length(max=50,message = "收货单位最大为50个字符")
    @NotNull(message = "收货单位不能为空")
    private String receiptunits;            //收货单位
    @NotNull(message = "采购量不能为空")
    private BigDecimal purchaseamount;             //采购量
    @Length(max=50,message = "到货地长度最大为50个字符")
    @NotNull(message = "到货地不能为空")
    private String arriveplace;             //到货地
//    @NotNull(message = "版本控制不能为空")
    private Integer version;                   //数据版本号，控制并发

    private List<TenderPacket> packetList;   //多个标包

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getTenderdeclarationid() {
        return tenderdeclarationid;
    }

    public void setTenderdeclarationid(Integer tenderdeclarationid) {
        this.tenderdeclarationid = tenderdeclarationid;
    }

    public String getPurchasecontent() {
        return purchasecontent;
    }

    public void setPurchasecontent(String purchasecontent) {
        this.purchasecontent = purchasecontent;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getReceiptunits() {
        return receiptunits;
    }

    public void setReceiptunits(String receiptunits) {
        this.receiptunits = receiptunits;
    }

    public BigDecimal getPurchaseamount() {
        return purchaseamount;
    }

    public void setPurchaseamount(BigDecimal purchaseamount) {
        this.purchaseamount = purchaseamount;
    }

    public String getArriveplace() {
        return arriveplace;
    }

    public void setArriveplace(String arriveplace) {
        this.arriveplace = arriveplace;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Override
    public String toString() {
        return "TenderItem{" +
                "id=" + id +
                ", tenderdeclarationid=" + tenderdeclarationid +
                ", purchasecontent='" + purchasecontent + '\'' +
                ", item='" + item + '\'' +
                ", sequence=" + sequence +
                ", receiptunits='" + receiptunits + '\'' +
                ", purchaseamount=" + purchaseamount +
                ", arriveplace='" + arriveplace + '\'' +
                ", version=" + version +
                "packetList="+packetList+
                '}';
    }

    public TenderItem(Integer tenderdeclarationid, String purchasecontent, String item, int sequence, String receiptunits, BigDecimal purchaseamount, String arriveplace, Integer version) {
        this.tenderdeclarationid = tenderdeclarationid;
        this.purchasecontent = purchasecontent;
        this.item = item;
        this.sequence = sequence;
        this.receiptunits = receiptunits;
        this.purchaseamount = purchaseamount;
        this.arriveplace = arriveplace;
        this.version = version;
    }

    public TenderItem() {
    }

    public List<TenderPacket> getPacketList() {
        return packetList;
    }

    public void setPacketList(List<TenderPacket> packetList) {
        this.packetList = packetList;
    }
}
