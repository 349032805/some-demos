package kitt.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by zhangbolun on 15/8/11.
 */
public class GroupBuyPayment {
    private int id;
    private int userid;                    //用户id
    private String qualificationcode;      //团购资质编号
    private String status;                 //状态
    private BigDecimal amount;             //金额
    private String photopath;              //保证金图片路径
    private boolean isdelete;              //是否删除
    private int clienttype;                //客户端类型
    private LocalDateTime createtime;      //信息创建时间

    public GroupBuyPayment(){}

    public GroupBuyPayment(int id, int userid, String qualificationcode, String status, BigDecimal amount, String photopath, boolean isdelete, int clienttype, LocalDateTime createtime) {
        this.id = id;
        this.userid = userid;
        this.qualificationcode = qualificationcode;
        this.status = status;
        this.amount = amount;
        this.photopath = photopath;
        this.isdelete = isdelete;
        this.clienttype = clienttype;
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        return "Groupbuypayment{" +
                "id=" + id +
                ", userid=" + userid +
                ", qualificationcode='" + qualificationcode + '\'' +
                ", status='" + status + '\'' +
                ", amount=" + amount +
                ", photopath='" + photopath + '\'' +
                ", isdelete=" + isdelete +
                ", clienttype=" + clienttype +
                ", createtime=" + createtime +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getQualificationcode() {
        return qualificationcode;
    }

    public void setQualificationcode(String qualificationcode) {
        this.qualificationcode = qualificationcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPhotopath() {
        return photopath;
    }

    public void setPhotopath(String photopath) {
        this.photopath = photopath;
    }

    public boolean isIsdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public int getClienttype() {
        return clienttype;
    }

    public void setClienttype(int clienttype) {
        this.clienttype = clienttype;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }
}