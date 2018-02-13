package kitt.core.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Created by zhangbolun on 15/11/19.
 */
public class Bid {
    private int id;
    private int tenderdeclarationid;
    private int userid;
    private String mytenderstatus ;
    private String paymentstatus;
    private LocalDateTime createtime;
    private BigDecimal supplyamount;
    private String attachmentpath;
    private String attachmentfilename;
    private boolean needpay;
    private String companyName;
    private LocalDateTime verifytime;

    public Bid(){}

    public Bid(int id, int tenderdeclarationid, int userid, String mytenderstatus, String paymentstatus, LocalDateTime createtime, BigDecimal supplyamount) {
        this.id = id;
        this.tenderdeclarationid = tenderdeclarationid;
        this.userid = userid;
        this.mytenderstatus = mytenderstatus;
        this.paymentstatus = paymentstatus;
        this.createtime = createtime;
        this.supplyamount = supplyamount;
    }

    public String getAttachmentfilename() {
        return attachmentfilename;
    }

    public void setAttachmentfilename(String attachmentfilename) {
        this.attachmentfilename = attachmentfilename;
    }

    public String getAttachmentpath() {
        return attachmentpath;
    }

    public void setAttachmentpath(String attachmentpath) {
        this.attachmentpath = attachmentpath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getMytenderstatus() {
        return mytenderstatus;
    }

    public void setMytenderstatus(String mytenderstatus) {
        this.mytenderstatus = mytenderstatus;
    }

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public BigDecimal getSupplyamount() {
        return supplyamount;
    }

    public void setSupplyamount(BigDecimal supplyamount) {
        this.supplyamount = supplyamount;
    }

    public boolean getNeedpay() {
        return needpay;
    }

    public void setNeedpay(boolean needpay) {
        this.needpay = needpay;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public LocalDateTime getVerifytime() {
        return verifytime;
    }

    public void setVerifytime(LocalDateTime verifytime) {
        this.verifytime = verifytime;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "id=" + id +
                ", tenderdeclarationid=" + tenderdeclarationid +
                ", userid=" + userid +
                ", mytenderstatus='" + mytenderstatus + '\'' +
                ", paymentstatus='" + paymentstatus + '\'' +
                ", createtime=" + createtime +
                ", supplyamount=" + supplyamount +
                '}';
    }
}
