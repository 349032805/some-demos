package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by jack on 14/11/26.
 */
public class MyInterest implements Serializable {
    private int id;
    private String pid;
    private int sid;
    private String pname;
    private String seller;

    private BigDecimal price;
    private boolean isdelete;
    private int userid;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createtime;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastupdatetime;
    private String type;
    private Integer amount;
    private String NCV;
    private int clienttype;              //客户端类型

    public MyInterest(){}


    public MyInterest(String pid, int sid, String pname, String seller, BigDecimal price, Integer amount, String NCV, int userid, String type, int clienttype) {
        this.pid = pid;
        this.sid = sid;
        this.pname = pname;
        this.seller = seller;
        this.price = price;
        this.amount = amount;
        this.NCV = NCV;
        this.userid = userid;
        this.type = type;
        this.clienttype = clienttype;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }


    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public boolean isIsdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNCV() {
        return NCV;
    }

    public void setNCV(String NCV) {
        this.NCV = NCV;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public int getClienttype() {
        return clienttype;
    }

    public void setClienttype(int clienttype) {
        this.clienttype = clienttype;
    }
}
