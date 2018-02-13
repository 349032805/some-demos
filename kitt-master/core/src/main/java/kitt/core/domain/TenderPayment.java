package kitt.core.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangyang on 15/11/13.
 */
public class TenderPayment {
    private Integer id;

    private Integer mytenderid;

    private Boolean issuccess;

    private BigDecimal money;

    private String verifyman;

    private Integer verifymanid;

    private LocalDate createtime;

    private LocalDate verifytime;

    private LocalDate lastupdatetime;

    private Integer userid;

    private String username;

    private Boolean isverified;

    private Integer version;

    private String companyName;

    private String status;

    private int picCount;
    private int did;

    private BigDecimal margins;

    private int supplyamount;

    private LocalDate tenderenddate;
    private List<String> picUrlList=new ArrayList<String>();
    private LocalDate  paymentTime;
    private String tendercode;
    private BigDecimal price;
    private String pic1;
    private String pic2;
    private String pic3;


    public int getPicCount() {
        return picCount;
    }

    public void setPicCount(int picCount) {
        this.picCount = picCount;
    }

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMytenderid() {
        return mytenderid;
    }

    public void setMytenderid(Integer mytenderid) {
        this.mytenderid = mytenderid;
    }

    public Boolean getIssuccess() {
        return issuccess;
    }

    public void setIssuccess(Boolean issuccess) {
        this.issuccess = issuccess;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getVerifyman() {
        return verifyman;
    }

    public void setVerifyman(String verifyman) {
        this.verifyman = verifyman;
    }

    public Integer getVerifymanid() {
        return verifymanid;
    }

    public void setVerifymanid(Integer verifymanid) {
        this.verifymanid = verifymanid;
    }

    public LocalDate getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDate createtime) {
        this.createtime = createtime;
    }

    public LocalDate getVerifytime() {
        return verifytime;
    }

    public void setVerifytime(LocalDate verifytime) {
        this.verifytime = verifytime;
    }

    public LocalDate getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(LocalDate lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIsverified() {
        return isverified;
    }

    public void setIsverified(Boolean isverified) {
        this.isverified = isverified;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getTenderenddate() {
        return tenderenddate;
    }

    public void setTenderenddate(LocalDate tenderenddate) {
        this.tenderenddate = tenderenddate;
    }

    public List<String> getPicUrlList() {
        return picUrlList;
    }

    public void setPicUrlList(List<String> picUrlList) {
        this.picUrlList = picUrlList;
    }

    public LocalDate getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDate paymentTime) {
        this.paymentTime = paymentTime;

    }

    public String getTendercode() {
        return tendercode;
    }

    public void setTendercode(String tendercode) {
        this.tendercode = tendercode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDid() {
        return did;
    }

    public void setDid(int did) {
        this.did = did;
    }

    public BigDecimal getMargins() {
        return margins;
    }

    public void setMargins(BigDecimal margins) {
        this.margins = margins;
    }

    public int getSupplyamount() {
        return supplyamount;
    }

    public void setSupplyamount(int supplyamount) {
        this.supplyamount = supplyamount;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }
}
