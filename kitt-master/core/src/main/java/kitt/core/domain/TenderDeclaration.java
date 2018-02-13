package kitt.core.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 15/11/10.
 */
public class TenderDeclaration {

    private int id;

    //@NotNull(message = "公司id不能为空")
    private Integer companyid;        //公司id
    //@NotNull(message = "用户id不能为空")
    private Integer userid;           //用户id
    @Length(max=30,message = "招标编号限制为30")
    @NotBlank(message = "招标编号不能为空")
    private String tendercode;       //招标编号
    @Length(max=50,message = "招标名称限制为50")
    @NotBlank(message = "招标名称不能为空")
    private String tendername;       //招标名称
    @Length(max=30,message = "招标方式限制为30")
    //@NotBlank(message = "招标方式不能为空")
    private String tendertype;       //招标方式
    @Length(max=50,message = "招标单位限制为50")
    @NotBlank(message = "招标单位不能为空")
    private String tenderunits;      //招标单位
//    @Length(max=200,message = "收货单位限制为200")
//    @NotBlank(message = "收货单位不能为空")
    private String receiptunits;     //收货单位 多个单位
    @Length(max=30,message = "结算方式限制为30")
    @NotBlank(message = "结算方式不能为空")
    private String capitalsettlement; //结算方式
    @NotNull(message = "投标保证金不能为空")
    private BigDecimal margins;       //履约保证金
//    @Max(value=10000000,message = "采购量长度最大为8位数")
    private BigDecimal purchaseamount;             //采购量
//    @NotNull(message = "合同执行期开始不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate contractconbegindate; //合同执行期开始
//    @NotNull(message = "合同执行期结束不能为空")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate contractconenddate; //合同执行期结束
//    @NotNull(message = "投标起始日不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime tenderbegindate;     //投标起始日
//    @NotNull(message = "投标截止日不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime tenderenddate;       //投标截止日
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDateTime verifydate;          //公告审核时间
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate starttenderdate;     //开标日期
    @Length(max=400,message = "投标单位资质要求限制为400")
    private String requirements;           //投标单位资质要求
    @Length(max=500,message = "奖扣标准条款限制为500")
    @NotBlank(message = "奖扣标准条款不能为空")
    private String rewardpenaltyclause;    //奖扣标准条款
    @Length(max=500,message = "其它要约限制为500")
    private String comments;               //其它要约
    @Length(max=100,message = "招标附件路径限制为100")
    @NotBlank(message = "招标附件路径不能为空")
    private String attachmentpath;         //招标附件路径
    @Length(max=100,message = "招标附件路径限制为100")
    private String attachmentfilename;     //招标附件文件名
    private int focus;                     //是否被关注. 0:未被关注  1:被关注
    private String status;                 //状态
    private int version;                   //版本控制
    private int scantime;             //浏览次数
    private LocalDateTime createtime;      //信息创建时间
    private LocalDateTime lastupdatetime;  //最后一次更新时间
    private List<TenderItem> itemList;     //多个项目
    private boolean flag=false;
    private String coaltype;
    @JsonProperty("NCV")
    private Integer NCV;
    private String userName;
    private String userPhone;
    private String companyName;
    private String logopic;    //公司logo图片
    private int declarNum;
    private List<TenderDeclaration> dList;
    private int tdCount;    //已有多少家公司投标
    private List<Map<String,Object>> decalarBids;   //已有多少家公司中标以及中标吨数
    private int zbCount;
    private int iscommit;    //判断是否提交
    private int waitVerifyCount;//待审核个数
    private int verifyCount;//审核通过个数
    @Length(max=100,message = "审核反馈信息不能超过100个字符")
    private String verifyremarks;         //审核反馈备注信息
    @Length(max=100,message = "下架招标公告备注不能超过100个字符")
    private String giveupremarks;         //后台下架审核通过的招标公告填写的备注
    private int traderid;                 //交易员id
    private String tradername;            //交易员姓名
    private String traderphone;           //交易员手机号
    private boolean  tenderopen;            //投标是否公开
    public TenderDeclaration(){}

    public TenderDeclaration(Integer userid,String attachmentfilename,int id, int companyid, String tendercode, String tendername, String tendertype, String tenderunits, String receiptunits, String capitalsettlement, BigDecimal margins, BigDecimal purchaseamount, LocalDate contractconbegindate, LocalDate contractconenddate, LocalDateTime tenderbegindate, LocalDateTime tenderenddate, LocalDate starttenderdate, String requirements, String rewardpenaltyclause, String comments, String attachmentpath, String status, int version, int scantime, LocalDateTime createtime, LocalDateTime lastupdatetime, List<TenderItem> itemList, boolean flag) {
        this.id = id;
        this.companyid = companyid;
        this.tendercode = tendercode;
        this.tendername = tendername;
        this.tendertype = tendertype;
        this.tenderunits = tenderunits;
        this.receiptunits = receiptunits;
        this.capitalsettlement = capitalsettlement;
        this.margins = margins;
        this.purchaseamount = purchaseamount;
        this.contractconbegindate = contractconbegindate;
        this.contractconenddate = contractconenddate;
        this.tenderbegindate = tenderbegindate;
        this.tenderenddate = tenderenddate;
        this.starttenderdate = starttenderdate;
        this.requirements = requirements;
        this.rewardpenaltyclause = rewardpenaltyclause;
        this.comments = comments;
        this.attachmentpath = attachmentpath;
        this.status = status;
        this.version = version;
        this.scantime = scantime;
        this.createtime = createtime;
        this.lastupdatetime = lastupdatetime;
        this.itemList = itemList;
        this.flag = flag;
        this.userid=userid;
        this.attachmentfilename=attachmentfilename;
    }

    @Override
    public String toString() {
        return "TenderDeclaration{" +
                "id=" + id +
                ", companyid=" + companyid +
                ", tendercode='" + tendercode + '\'' +
                ", tendername='" + tendername + '\'' +
                ", tendertype='" + tendertype + '\'' +
                ", tenderunits='" + tenderunits + '\'' +
                ", receiptunits='" + receiptunits + '\'' +
                ", capitalsettlement='" + capitalsettlement + '\'' +
                ", margins=" + margins +
                ", purchaseamount=" + purchaseamount +
                ", contractconbegindate=" + contractconbegindate +
                ", contractconenddate=" + contractconenddate +
                ", tenderbegindate=" + tenderbegindate +
                ", tenderenddate=" + tenderenddate +
                ", starttenderdate=" + starttenderdate +
                ", requirements='" + requirements + '\'' +
                ", rewardpenaltyclause='" + rewardpenaltyclause + '\'' +
                ", comments='" + comments + '\'' +
                ", attachmentpath='" + attachmentpath + '\'' +
                ", status='" + status + '\'' +
                ", version=" + version +
                ", scantime=" + scantime +
                ", createtime=" + createtime +
                ", lastupdatetime=" + lastupdatetime +
                ", itemList=" + itemList +
                ", flag=" + flag +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getCompanyid() {
        return companyid;
    }

    public void setCompanyid(Integer companyid) {
        this.companyid = companyid;
    }

    public String getTendercode() {
        return tendercode;
    }

    public void setTendercode(String tendercode) {
        this.tendercode = tendercode;
    }

    public String getTendername() {
        return tendername;
    }

    public void setTendername(String tendername) {
        this.tendername = tendername;
    }

    public String getTendertype() {
        return tendertype;
    }

    public void setTendertype(String tendertype) {
        this.tendertype = tendertype;
    }

    public String getTenderunits() {
        return tenderunits;
    }

    public void setTenderunits(String tenderunits) {
        this.tenderunits = tenderunits;
    }

    public String getReceiptunits() {
        return receiptunits;
    }

    public void setReceiptunits(String receiptunits) {
        this.receiptunits = receiptunits;
    }

    public String getCapitalsettlement() {
        return capitalsettlement;
    }

    public void setCapitalsettlement(String capitalsettlement) {
        this.capitalsettlement = capitalsettlement;
    }

    public BigDecimal getMargins() {
        return margins;
    }

    public void setMargins(BigDecimal margins) {
        this.margins = margins;
    }

    public BigDecimal getPurchaseamount() {
        return purchaseamount;
    }

    public void setPurchaseamount(BigDecimal purchaseamount) {
        this.purchaseamount = purchaseamount;
    }

    public LocalDate getContractconbegindate() {
        return contractconbegindate;
    }

    public void setContractconbegindate(LocalDate contractconbegindate) {
        this.contractconbegindate = contractconbegindate;
    }

    public LocalDate getContractconenddate() {
        return contractconenddate;
    }

    public void setContractconenddate(LocalDate contractconenddate) {
        this.contractconenddate = contractconenddate;
    }

    public LocalDateTime getTenderbegindate() {
        return tenderbegindate;
    }

    public void setTenderbegindate(LocalDateTime tenderbegindate) {
        this.tenderbegindate = tenderbegindate;
    }

    public LocalDateTime getTenderenddate() {
        return tenderenddate;
    }

    public void setTenderenddate(LocalDateTime tenderenddate) {
        this.tenderenddate = tenderenddate;
    }

    public LocalDate getStarttenderdate() {
        return starttenderdate;
    }

    public void setStarttenderdate(LocalDate starttenderdate) {
        this.starttenderdate = starttenderdate;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getRewardpenaltyclause() {
        return rewardpenaltyclause;
    }

    public void setRewardpenaltyclause(String rewardpenaltyclause) {
        this.rewardpenaltyclause = rewardpenaltyclause;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAttachmentpath() {
        return attachmentpath;
    }

    public void setAttachmentpath(String attachmentpath) {
        this.attachmentpath = attachmentpath;
    }

    public int getFocus() {
        return focus;
    }

    public void setFocus(int focus) {
        this.focus = focus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getScantime() {
        return scantime;
    }

    public void setScantime(int scantime) {
        this.scantime = scantime;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
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

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public List<TenderItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<TenderItem> itemList) {
        this.itemList = itemList;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getDeclarNum() {
        return declarNum;
    }

    public void setDeclarNum(int declarNum) {
        this.declarNum = declarNum;
    }

    public List<TenderDeclaration> getdList() {
        return dList;
    }

    public void setdList(List<TenderDeclaration> dList) {
        this.dList = dList;
    }

    public int getTdCount() {
        return tdCount;
    }

    public void setTdCount(int tdCount) {
        this.tdCount = tdCount;
    }

    public String getAttachmentfilename() {
        return attachmentfilename;
    }

    public LocalDateTime getVerifydate() {
        return verifydate;
    }

    public void setVerifydate(LocalDateTime verifydate) {
        this.verifydate = verifydate;
    }

    public int getIscommit() {
        return iscommit;
    }

    public void setIscommit(int iscommit) {
        this.iscommit = iscommit;
    }

    public void setAttachmentfilename(String attachmentfilename) {
        this.attachmentfilename = attachmentfilename;
    }

    public List<Map<String, Object>> getDecalarBids() {
        return decalarBids;
    }

    public void setDecalarBids(List<Map<String, Object>> decalarBids) {
        this.decalarBids = decalarBids;
    }

    public int getWaitVerifyCount() {
        return waitVerifyCount;
    }

    public void setWaitVerifyCount(int waitVerifyCount) {
        this.waitVerifyCount = waitVerifyCount;
    }

    public String getVerifyremarks() {
        return verifyremarks;
    }

    public void setVerifyremarks(String verifyremarks) {
        this.verifyremarks = verifyremarks;
    }

    public int getZbCount() {
        return zbCount;
    }

    public void setZbCount(int zbCount) {
        this.zbCount = zbCount;
    }

    public String getGiveupremarks() {
        return giveupremarks;
    }

    public void setGiveupremarks(String giveupremarks) {
        this.giveupremarks = giveupremarks;
    }

    public int getTraderid() {
        return traderid;
    }

    public void setTraderid(int traderid) {
        this.traderid = traderid;
    }

    public String getTradername() {
        return tradername;
    }

    public void setTradername(String tradername) {
        this.tradername = tradername;
    }

    public String getTraderphone() {
        return traderphone;
    }

    public void setTraderphone(String traderphone) {
        this.traderphone = traderphone;
    }

    public String getLogopic() {
        return logopic;
    }

    public void setLogopic(String logopic) {
        this.logopic = logopic;
    }

    public int getVerifyCount() {
        return verifyCount;
    }

    public void setVerifyCount(int verifyCount) {
        this.verifyCount = verifyCount;
    }

    public boolean getTenderopen() {
        return tenderopen;
    }

    public void setTenderopen(boolean tenderopen) {
        this.tenderopen = tenderopen;
    }
}
