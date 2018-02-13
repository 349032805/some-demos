package kitt.core.domain;

import java.math.BigDecimal;

/**
 * Created by zhangbolun on 15/11/18.
 */
public class SelectTender {

    private int id;
    private String competecompanyname;
    private String releasecompanyname;
    private int itemsequence;
    private int packetsequence;
    private BigDecimal price;
    private int competecompanyid;
    private BigDecimal needamount;

    public SelectTender(){}

    public SelectTender(BigDecimal needamount,int id, String competecompanyname, String releasecompanyname, int itemsequence, int packetsequence, BigDecimal price, int competecompanyid) {
        this.id = id;
        this.competecompanyname = competecompanyname;
        this.releasecompanyname = releasecompanyname;
        this.itemsequence = itemsequence;
        this.packetsequence = packetsequence;
        this.price = price;
        this.competecompanyid = competecompanyid;
        this.needamount=needamount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompetecompanyname() {
        return competecompanyname;
    }

    public void setCompetecompanyname(String competecompanyname) {
        this.competecompanyname = competecompanyname;
    }

    public String getReleasecompanyname() {
        return releasecompanyname;
    }

    public void setReleasecompanyname(String releasecompanyname) {
        this.releasecompanyname = releasecompanyname;
    }

    public int getItemsequence() {
        return itemsequence;
    }

    public void setItemsequence(int itemsequence) {
        this.itemsequence = itemsequence;
    }

    public int getPacketsequence() {
        return packetsequence;
    }

    public void setPacketsequence(int packetsequence) {
        this.packetsequence = packetsequence;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getCompetecompanyid() {
        return competecompanyid;
    }

    public void setCompetecompanyid(int competecompanyid) {
        this.competecompanyid = competecompanyid;
    }


    public BigDecimal getNeedamount() {
        return needamount;
    }

    public void setNeedamount(BigDecimal needamount) {
        this.needamount = needamount;
    }

    @Override
    public String toString() {
        return "SelectTender{" +
                "id=" + id +
                ", competecompanyname='" + competecompanyname + '\'' +
                ", releasecompanyname='" + releasecompanyname + '\'' +
                ", itemsequence=" + itemsequence +
                ", packetsequence=" + packetsequence +
                ", price=" + price +
                ", competecompanyid=" + competecompanyid +
                '}';
    }
}
