package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by fanjun on 14-11-26.
 */
public class Quote extends CoalBaseData implements Serializable {
    //报价表
    private int id;
    private int userid;
    private int demandid;                   //需求id
    private String demandcode;              //需求编号  (需求id和需求编号原本一个就好,为了方便页面处理字符和数字类型,都保留)
    /*  private int unitprice;                  //单价 (需求表的单价)*/
    private int supplyton;                  //申供吨数
    /* private int lowcalorificvalue;          //低位热值 (需求更改后未使用,保留)*/
    private BigDecimal quote;                      //报价
    private LocalDateTime lastupdatetime;   //最后更新时间 (因为报价表暂时不需要更新,所以此字段只作为第一次报价时记录时间)
    private boolean isdelete;                //是否删除
    private String status;                  //状态

    //增加提货时间和报价截止时间,方便定时任务修改报价信息的状态
    private String deliverymode;             //提货方式
    private LocalDate deliverydate;         //提货时间
    private LocalDate deliverydatestart;    //自提开始时间
    private LocalDate deliverydateend;      //自提截止时间
    private LocalDate quoteenddate;         //报价截止时间
    private String companyname;             //公司名称
    private String traderphone;             //交易员手机
    private String tradername;                //交易员姓名
    private String username;
    private int clienttype;              //客户端类型
    private String originplace;        // 产地
    private String remarks;         //备注信息

    private String startport;         //发运站/港

    private String endport;         //交货站/港

    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private LocalDate deliverytime1;         //提货时间1
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    private LocalDate deliverytime2;         //提货时间2



    public Quote(){

    }


    public Quote(int userid, int demandid, String demandcode, int supplyton,BigDecimal quote, LocalDateTime lastupdatetime, String status, String deliverymode, LocalDate deliverydate, LocalDate deliverydatestart, LocalDate deliverydateend, LocalDate quoteenddate,String companyname,String traderphone, int clienttype) {
        this.userid = userid;
        this.demandid = demandid;
        this.demandcode = demandcode;
        this.supplyton = supplyton;
        this.quote = quote;
        this.lastupdatetime = lastupdatetime;
        this.status = status;
        this.deliverymode = deliverymode;
        this.deliverydate = deliverydate;
        this.deliverydatestart = deliverydatestart;
        this.deliverydateend = deliverydateend;
        this.quoteenddate = quoteenddate;
        this.companyname = companyname;
        this.traderphone = traderphone;
        this.clienttype = clienttype;
    }

    /*public CoalBaseData(Integer NCV, BigDecimal ADS, BigDecimal RS, BigDecimal TM, BigDecimal IM, BigDecimal ADV, BigDecimal RV, Integer AFT, BigDecimal ASH,
                        Integer HGI, Integer GV, Integer YV, Integer FC, Integer PS, String PSName)

    public CoalBaseData(Integer NCV, Integer NCV02, BigDecimal ADS, BigDecimal ADS02, BigDecimal RS, BigDecimal RS02, BigDecimal TM, BigDecimal TM02,
                        BigDecimal IM, BigDecimal IM02, BigDecimal ADV, BigDecimal ADV02, BigDecimal RV, BigDecimal RV02, Integer AFT, BigDecimal ASH, BigDecimal ASH02, Integer HGI,
                        Integer GV, Integer GV02, Integer YV, Integer YV02, Integer FC, Integer FC02, Integer PS, String PSName)*/

    public Quote(int userid, int demandid,String demandcode, int supplyton, BigDecimal quote, LocalDateTime lastupdatetime,
                 String status, String deliverymode, LocalDate deliverydate, LocalDate deliverydatestart,
                 LocalDate deliverydateend, LocalDate quoteenddate, String companyname, String traderphone,int clienttype,

                 Integer NCV,Integer NCV02, BigDecimal ADS,BigDecimal ADS02, BigDecimal RS, BigDecimal RS02,BigDecimal TM,
                 BigDecimal TM02,BigDecimal IM, BigDecimal IM02,BigDecimal ADV, BigDecimal ADV02,BigDecimal RV,BigDecimal RV02,
                 Integer AFT, BigDecimal ASH, BigDecimal ASH02,Integer HGI, Integer GV, Integer GV02,Integer YV,Integer YV02,

                 Integer FC, Integer FC02,Integer PS, String PSName,String originplace,
                 String remarks, String startport, String endport, LocalDate deliverytime1, LocalDate deliverytime2) {
        super(NCV,NCV02, ADS, ADS02,RS, RS02,TM,TM02, IM,IM02, ADV,ADV02, RV,RV02, AFT, ASH, ASH02, HGI, GV, GV02,YV,YV02, FC,FC02, PS, PSName);
        this.userid = userid;
        this.demandid = demandid;
        this.demandcode = demandcode;
        this.supplyton = supplyton;
        this.quote = quote;
        this.lastupdatetime = lastupdatetime;
        this.status = status;
        this.deliverymode = deliverymode;
        this.deliverydate = deliverydate;
        this.deliverydatestart = deliverydatestart;
        this.deliverydateend = deliverydateend;
        this.quoteenddate = quoteenddate;
        this.companyname = companyname;
        this.traderphone = traderphone;
        this.clienttype = clienttype;
        this.ADV02 = ADV02;
        this.RV02 = RV02;
        this.FC02 = FC02;
        this.originplace = originplace;
        this.GV02 = GV02;
        this.YV02 = YV02;
        this.remarks = remarks;
        this.startport = startport;
        this.endport = endport;
        this.deliverytime1 = deliverytime1;
        this.deliverytime2 = deliverytime2;
    }

    public Quote(BigDecimal ADV02, int clienttype, String companyname, LocalDate deliverydate, LocalDate deliverydateend, LocalDate deliverydatestart, String deliverymode, LocalDate deliverytime1, LocalDate deliverytime2, String demandcode, int demandid, String endport, Integer FC02, Integer GV02, int id, boolean isdelete, LocalDateTime lastupdatetime, String originplace, BigDecimal quote, LocalDate quoteenddate, String remarks, BigDecimal RV02, String startport, String status, int supplyton, String tradername, String traderphone, int userid, String username, Integer YV02) {
        this.ADV02 = ADV02;
        this.clienttype = clienttype;
        this.companyname = companyname;
        this.deliverydate = deliverydate;
        this.deliverydateend = deliverydateend;
        this.deliverydatestart = deliverydatestart;
        this.deliverymode = deliverymode;
        this.deliverytime1 = deliverytime1;
        this.deliverytime2 = deliverytime2;
        this.demandcode = demandcode;
        this.demandid = demandid;
        this.endport = endport;
        this.FC02 = FC02;
        this.GV02 = GV02;
        this.id = id;
        this.isdelete = isdelete;
        this.lastupdatetime = lastupdatetime;
        this.originplace = originplace;
        this.quote = quote;
        this.quoteenddate = quoteenddate;
        this.remarks = remarks;
        this.RV02 = RV02;
        this.startport = startport;
        this.status = status;
        this.supplyton = supplyton;
        this.tradername = tradername;
        this.traderphone = traderphone;
        this.userid = userid;
        this.username = username;
        this.YV02 = YV02;
    }




    public Quote(int userid, int demandid, String demandcode, int supplyton,BigDecimal quote, LocalDateTime lastupdatetime,
                 String status, String deliverymode, LocalDate deliverydate, LocalDate deliverydatestart, LocalDate deliverydateend,
                 LocalDate quoteenddate,String companyname,String traderphone, int clienttype,BigDecimal ADV02,BigDecimal RV02,Integer FC02,Integer GV02,
                 Integer YV02,String originplace,String remarks,String startport,String endport,LocalDate deliverytime1,LocalDate deliverytime2)  {
        this.userid = userid;
        this.demandid = demandid;
        this.demandcode = demandcode;
        this.supplyton = supplyton;
        this.quote = quote;
        this.lastupdatetime = lastupdatetime;
        this.status = status;
        this.deliverymode = deliverymode;
        this.deliverydate = deliverydate;
        this.deliverydatestart = deliverydatestart;
        this.deliverydateend = deliverydateend;
        this.quoteenddate = quoteenddate;
        this.companyname = companyname;
        this.traderphone = traderphone;
        this.clienttype = clienttype;
        this.ADV02 = ADV02;
        this.RV02 = RV02;
        this.FC02 = FC02;
        this.GV02 = GV02;
        this.YV02 = YV02;
        this.originplace = originplace;
        this.remarks = remarks;
        this.startport = startport;
        this.endport = endport;
        this.deliverytime1 = deliverytime1;
        this.deliverytime2 = deliverytime2;
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

    public int getDemandid() {
        return demandid;
    }

    public void setDemandid(int demandid) {
        this.demandid = demandid;
    }

    public String getDemandcode() {
        return demandcode;
    }

    public void setDemandcode(String demandcode) {
        this.demandcode = demandcode;
    }

    public int getSupplyton() {
        return supplyton;
    }

    public void setSupplyton(int supplyton) {
        this.supplyton = supplyton;
    }

  public BigDecimal getQuote() {
    return quote;
  }

  public void setQuote(BigDecimal quote) {
    this.quote = quote;
  }

  public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public boolean isIsdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDeliverydate() {
        return deliverydate;
    }

    public void setDeliverydate(LocalDate deliverydate) {
        this.deliverydate = deliverydate;
    }

    public LocalDate getDeliverydatestart() {
        return deliverydatestart;
    }

    public void setDeliverydatestart(LocalDate deliverydatestart) {
        this.deliverydatestart = deliverydatestart;
    }

    public LocalDate getDeliverydateend() {
        return deliverydateend;
    }

    public void setDeliverydateend(LocalDate deliverydateend) {
        this.deliverydateend = deliverydateend;
    }

    public LocalDate getQuoteenddate() {
        return quoteenddate;
    }

    public void setQuoteenddate(LocalDate quoteenddate) {
        this.quoteenddate = quoteenddate;
    }

    public String getDeliverymode() {
        return deliverymode;
    }

    public void setDeliverymode(String deliverymode) {
        this.deliverymode = deliverymode;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getTraderphone() {
        return traderphone;
    }

    public void setTraderphone(String traderphone) {
        this.traderphone = traderphone;
    }

    public String getTradername() {
        return tradername;
    }

    public void setTradername(String tradername) {
        this.tradername = tradername;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getClienttype() {
        return clienttype;
    }

    public void setClienttype(int clienttype) {
        this.clienttype = clienttype;
    }

    public BigDecimal getADV02() {
        return ADV02;
    }

    public void setADV02(BigDecimal ADV02) {
        this.ADV02 = ADV02;
    }

    public BigDecimal getRV02() {
        return RV02;
    }

    public void setRV02(BigDecimal RV02) {
        this.RV02 = RV02;
    }

    public Integer getFC02() {
        return FC02;
    }

    public void setFC02(Integer FC02) {
        this.FC02 = FC02;
    }

    public String getOriginplace() {
        return originplace;
    }

    public void setOriginplace(String originplace) {
        this.originplace = originplace;
    }

    public Integer getGV02() {
        return GV02;
    }

    public void setGV02(Integer GV02) {
        this.GV02 = GV02;
    }

    public Integer getYV02() {
        return YV02;
    }

    public void setYV02(Integer YV02) {
        this.YV02 = YV02;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStartport() {
        return startport;
    }

    public void setStartport(String startport) {
        this.startport = startport;
    }

    public String getEndport() {
        return endport;
    }

    public void setEndport(String endport) {
        this.endport = endport;
    }

    public LocalDate getDeliverytime1() {
        return deliverytime1;
    }

    public void setDeliverytime1(LocalDate deliverytime1) {
        this.deliverytime1 = deliverytime1;
    }

    public LocalDate getDeliverytime2() {
        return deliverytime2;
    }

    public void setDeliverytime2(LocalDate deliverytime2) {
        this.deliverytime2 = deliverytime2;
    }

    @Override
    public String toString() {
        return "Quote{" +
                "id=" + id +
                ", userid=" + userid +
                ", demandid=" + demandid +
                ", demandcode='" + demandcode + '\'' +
                ", supplyton=" + supplyton +
                ", quote=" + quote +
                ", lastupdatetime=" + lastupdatetime +
                ", isdelete=" + isdelete +
                ", status='" + status + '\'' +
                ", deliverymode='" + deliverymode + '\'' +
                ", deliverydate=" + deliverydate +
                ", deliverydatestart=" + deliverydatestart +
                ", deliverydateend=" + deliverydateend +
                ", quoteenddate=" + quoteenddate +
                ", companyname='" + companyname + '\'' +
                ", traderphone='" + traderphone + '\'' +
                ", tradername='" + tradername + '\'' +
                ", username='" + username + '\'' +
                ", clienttype=" + clienttype +
                ", ADV02=" + ADV02 +
                ", RV02=" + RV02 +
                ", FC02=" + FC02 +
                ", originplace='" + originplace + '\'' +
                ", GV02=" + GV02 +
                ", YV02=" + YV02 +
                ", remarks='" + remarks + '\'' +
                ", startport='" + startport + '\'' +
                ", endport='" + endport + '\'' +
                ", deliverytime1=" + deliverytime1 +
                ", deliverytime2=" + deliverytime2 +
                '}';
    }
}
