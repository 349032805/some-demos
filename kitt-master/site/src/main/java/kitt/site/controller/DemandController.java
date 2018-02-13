package kitt.site.controller;

import com.mysql.jdbc.StringUtils;
import kitt.core.domain.*;
import kitt.core.domain.Dictionary;
import kitt.core.persistence.*;
import kitt.core.util.DropdownListUtil;
import kitt.core.util.PageQueryParam;
import kitt.core.util.ToolsMethod;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by fanjun on 14-11-25.
 */
@Controller
public class DemandController extends JsonController {
    @Autowired
    protected DictionaryMapper dictionaryMapper;
    @Autowired
    protected DemandMapper demandMapper;
    @Autowired
    private Session session;
    @Autowired
    protected QuoteMapper quoteMapper;
    @Autowired
    protected CompanyMapper companyMapper;
    @Autowired
    protected AreaportMapper areaportMapper;
    @Autowired
    protected MydemandMapper mydemandMapper;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected DataBookMapper dataBookMapper;
    @Autowired
    protected MyInterestMapper interestMapper;
    @Autowired
    protected ValidMapper validMapper;
    @Autowired
    private ToolsMethod toolsMethod;

    //没用实体类来接受参数,只取需要的参数Form接受
    public static class DemandForm {
        private String coaltype;                   //煤炭种类
        private String demandcode;                 //需求编号
        private int NCV;                        //低位热值
        @Null
        private BigDecimal ADS;                     //空干基硫分
        private BigDecimal RS;                     //收到基硫分
        private BigDecimal TM;                      //全水分
        @Null
        private BigDecimal IM;                      //内水分
        private BigDecimal ADV;                     //空干基挥发分
        @Null
        private BigDecimal RV;                      //收到基挥发分
        @Null
        private int AFT;                            //灰熔点
        @Null
        private BigDecimal ASH;                     //灰分
        @Null
        private int HGI;                            //哈氏可磨
        private String deliveryplace;              //提货地点
        private String demandamount;               //需求数量
        @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
        private LocalDate deliverydate;               //提货时间
        @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
        private LocalDate deliverydatestart;          //提货时间开始
        @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
        private LocalDate deliverydateend;            //提货时间截至
        private String deliverymode;                    //提货方式
        private String inspectionagency;            //检验机构
        private String deliveryprovince;            //提货省分
        private String otherorg;                    //其它检验机构
        private String otherplace;                  //其它提货地点
        @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
        private LocalDate quoteenddate;             //报价截至日期
        private String releasecomment;              //发布备注

        private Integer GV;                         //G值
        private Integer YV;                         //Y值
        private Integer FC;                         //固定碳

        private Integer PS;                         //颗粒度
        private String PSName;                      //颗粒度名字

        private Integer GV02;                       //G值2
        private Integer YV02;                       //Y值2
        private Integer FC02;                       //固定碳2

        private BigDecimal ADV02;                   //空干基挥发分
        private BigDecimal RV02;                    //收到基挥发分

        private int NCV02;                          //低位热值2
        private BigDecimal ADS02;                   //空干基硫分2
        private BigDecimal RS02;                    //收到基硫分2
        private BigDecimal TM02;                    //全水分2
        private BigDecimal IM02;                    //内水分2
        private BigDecimal ASH02;                   //灰分2

        private String paymode;                     //付款方式
        private String transportmode;               //运输方式

        public int getNCV02() {
            return NCV02;
        }

        public void setNCV02(int NCV02) {
            this.NCV02 = NCV02;
        }

        public BigDecimal getADS02() {
            return ADS02;
        }

        public void setADS02(BigDecimal ADS02) {
            this.ADS02 = ADS02;
        }

        public BigDecimal getRS02() {
            return RS02;
        }

        public void setRS02(BigDecimal RS02) {
            this.RS02 = RS02;
        }

        public BigDecimal getTM02() {
            return TM02;
        }

        public void setTM02(BigDecimal TM02) {
            this.TM02 = TM02;
        }

        public BigDecimal getIM02() {
            return IM02;
        }

        public void setIM02(BigDecimal IM02) {
            this.IM02 = IM02;
        }

        public BigDecimal getASH02() {
            return ASH02;
        }

        public void setASH02(BigDecimal ASH02) {
            this.ASH02 = ASH02;
        }

        public String getPaymode() {
            return paymode;
        }

        public void setPaymode(String paymode) {
            this.paymode = paymode;
        }

        public String getTransportmode() {
            return transportmode;
        }

        public void setTransportmode(String transportmode) {
            this.transportmode = transportmode;
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

        public Integer getFC02() {
            return FC02;
        }

        public void setFC02(Integer FC02) {
            this.FC02 = FC02;
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

        public Integer getGV() {
            return GV;
        }

        public void setGV(Integer GV) {
            this.GV = GV;
        }

        public Integer getYV() {
            return YV;
        }

        public void setYV(Integer YV) {
            this.YV = YV;
        }

        public Integer getFC() {
            return FC;
        }

        public void setFC(Integer FC) {
            this.FC = FC;
        }

        public Integer getPS() {
            return PS;
        }

        public void setPS(Integer PS) {
            this.PS = PS;
        }

        public String getPSName() {
            return PSName;
        }

        public void setPSName(String PSName) {
            this.PSName = PSName;
        }

        public String getDemandcode() {
            return demandcode;
        }

        public void setDemandcode(String demandcode) {
            this.demandcode = demandcode;
        }

        public String getDeliveryplace() {
            return deliveryplace;
        }

        public void setDeliveryplace(String deliveryplace) {
            this.deliveryplace = deliveryplace;
        }

        public String getDemandamount() {
            return demandamount;
        }

        public void setDemandamount(String demandamount) {
            this.demandamount = demandamount;
        }

        public String getDeliverymode() {
            return deliverymode;
        }

        public void setDeliverymode(String deliverymode) {
            this.deliverymode = deliverymode;
        }

        public String getInspectionagency() {
            return inspectionagency;
        }

        public void setInspectionagency(String inspectionagency) {
            this.inspectionagency = inspectionagency;
        }

        public String getDeliveryprovince() {
            return deliveryprovince;
        }

        public void setDeliveryprovince(String deliveryprovince) {
            this.deliveryprovince = deliveryprovince;
        }

        public String getOtherorg() {
            return otherorg;
        }

        public void setOtherorg(String otherorg) {
            this.otherorg = otherorg;
        }

        public String getOtherplace() {
            return otherplace;
        }

        public void setOtherplace(String otherplace) {
            this.otherplace = otherplace;
        }

        public String getCoaltype() {
            return coaltype;
        }

        public void setCoaltype(String coaltype) {
            this.coaltype = coaltype;
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

        public int getNCV() {
            return NCV;
        }

        public void setNCV(int NCV) {
            this.NCV = NCV;
        }

        public BigDecimal getADS() {
            return ADS;
        }

        public void setADS(BigDecimal ADS) {
            this.ADS = ADS;
        }

        public BigDecimal getRS() {
            return RS;
        }

        public void setRS(BigDecimal RS) {
            this.RS = RS;
        }

        public BigDecimal getTM() {
            return TM;
        }

        public void setTM(BigDecimal TM) {
            this.TM = TM;
        }

        public BigDecimal getIM() {
            return IM;
        }

        public void setIM(BigDecimal IM) {
            this.IM = IM;
        }

        public BigDecimal getADV() {
            return ADV;
        }

        public void setADV(BigDecimal ADV) {
            this.ADV = ADV;
        }

        public BigDecimal getRV() {
            return RV;
        }

        public void setRV(BigDecimal RV) {
            this.RV = RV;
        }

        public int getAFT() {
            return AFT;
        }

        public void setAFT(int AFT) {
            this.AFT = AFT;
        }

        public BigDecimal getASH() {
            return ASH;
        }

        public void setASH(BigDecimal ASH) {
            this.ASH = ASH;
        }

        public int getHGI() {
            return HGI;
        }

        public void setHGI(int HGI) {
            this.HGI = HGI;
        }

        public String getReleasecomment() {
            return releasecomment;
        }

        public void setReleasecomment(String releasecomment) {
            this.releasecomment = releasecomment;
        }
    }

    @RequestMapping(value = "/sell", method = RequestMethod.GET)
    public String sell(Integer coaltype,
                       @RequestParam(value="area", required = false, defaultValue = "0") Integer area,
                       @RequestParam(value="province", required = false, defaultValue = "0")Integer province,
                       @RequestParam(value="port", required = false, defaultValue = "0")Integer port,
                       @RequestParam(value="pid", required = false, defaultValue = "")String pid,
                       Integer lowHotValue,Integer highHotValue, BigDecimal lowSulfurContent,BigDecimal highSulfurContent,
                      // @RequestParam(value="orderByColumn", required = false, defaultValue = "releasetime")String orderByColumn,
                       PageQueryParam param, Map<String, Object> model){

        //煤种暂时没有id和编号之类的.
        String coalWord = null;
        if (coaltype != null) {
            if (coaltype != 0) {
                coalWord = dictionaryMapper.getCaolNameByCoalId(coaltype);
            }
        }

        /* 因为查询可能会选择其它,数字转化字符串会带逗号,取最大的三位数999
           实现页面selected选择找到对比值*/
        int otherDeliveryPlace = 0;
        if (port != null) {
            if (port != 0) {
                if(port == 999){
                    otherDeliveryPlace = 999;
                }
            }
        }

        /*//releasetime创建时间.用了数字代替参数值,因为文字传入存在单双引号的问题.
        int orderByInt = 0;
        if(orderByColumn.equals("NCV")){
            orderByInt = 1;
        }
        if(orderByColumn.equals("RS")){
            orderByInt = 2;
        }*/
        int totalCount = 0;
        List<Demand> demandList = new ArrayList<>();
        if(StringUtils.isNullOrEmpty(pid)) {
            totalCount = demandMapper.countDemandsByCheck(coalWord, area, province, port, otherDeliveryPlace, lowHotValue, highHotValue, lowSulfurContent, highSulfurContent);
            param.setCount(totalCount);
            //if(param.getPage() > (totalCount / param.getPagesize() + 1)) param.setPage(totalCount / param.getPagesize() + 1);
            demandList = demandMapper.getDemandsByCheck(coalWord, area, province, port, otherDeliveryPlace, lowHotValue, highHotValue,
                    lowSulfurContent, highSulfurContent, param.getPagesize(), param.getIndexNum());
        } else{
            totalCount = demandMapper.getDemandCountByPid(pid, 0);
            param.setCount(totalCount);
            demandList = demandMapper.getDemandListByPid(pid, 0, param.getPagesize(), param.getIndexNum());
        }
        if (demandList != null && demandList.size() > 0) {
            model.put("demandList", demandList);
        }

        List<Dictionary> coalList = dictionaryMapper.getAllCoalTypes();
        List<Areaport> areatList = areaportMapper.getAllArea();
        List<Areaport> provincesList = null;

        if (area != null && area != 0) {
            provincesList = areaportMapper.getProvincesOrPortsByParentid(area);
        } else{
            provincesList = areaportMapper.getAllProvince();
        }
        model.put("coalList", coalList);
        model.put("areatList", areatList);
        model.put("provincesList", provincesList);

        if (province != null && province != 0) {
            List<Areaport> portsList = areaportMapper.getProvincesOrPortsByParentid(province);
            Areaport areaport = new Areaport();
            areaport.setId(999);
            areaport.setName("其它");
            portsList.add(areaport);
            model.put("portsList", portsList);
        }
        model.put("totalCount", totalCount);
        model.put("pageNumber", param.getPage());
        model.put("coaltype", String.valueOf(coaltype == null ? "0" : coaltype));
        model.put("area", String.valueOf(area));
        model.put("province", String.valueOf(province));
        model.put("port", String.valueOf(port));
        model.put("lowHotValue", String.valueOf(lowHotValue == null ? "0":lowHotValue));
        model.put("highHotValue", String.valueOf(highHotValue == null ? "7500" :highHotValue));
        model.put("lowSulfurContent", String.valueOf(lowSulfurContent == null ? "0" : lowSulfurContent));
        model.put("highSulfurContent", String.valueOf(highSulfurContent == null ? "10" : highSulfurContent));
       // model.put("orderByColumn",orderByColumn);
        model.put("pagesize", param.getPagesize());

        /**
         * sell页面改版需要的逻辑
         * add by xj
         */
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//取服务器的当前时间
        model.put("serverTime",df.format(new Date()));
        model.put("totalDemands", demandMapper.getTotalDemandRecords());//累计发布需求总数
        model.put("saleSuccess", demandMapper.getSaleRecords());//累计求购成功总数
        model.put("totalTrading",demandMapper.getTotalTrading());//累计成交总量
        model.put("hotValue",(String.valueOf(lowHotValue == null ? "0":lowHotValue)+'-'+String.valueOf(highHotValue == null ? "7500" :highHotValue)));//低热位值范围
        //报价成交展示
        List<DealDemand> dealList = demandMapper.getListDealDemand();
        if (dealList != null && dealList.size() > 0) {
            model.put("dealList", dealList);
        }

        model.put("title",Seoconfig.offerArea_title);
        model.put("keywords",Seoconfig.offerArea_keywords);
        model.put("description",Seoconfig.offerArea_description);
        return "sell";
    }

    //检查公司信息是否完整
    @RequestMapping(value = "/checkCompanyInfo", method = RequestMethod.POST)
    @ResponseBody
    public Object CheckCompanyInfo() {
        String message = null;
        if (session.getUser() != null) {
            User u = userMapper.getUserById(session.getUser().getId());
            if(u == null) throw new NotFoundException();
            Company company = companyMapper.getCompanyByUserid(u.getId());

            if(company != null) {
                if (u.getVerifystatus().equals("待完善信息") || u.getVerifystatus().equals("")) {
                    message = "improve";
                } else if (u.getVerifystatus().equals("待审核") || company.getVerifystatus().equals("待审核")) {
                    message = "verifying";
                } else if (u.getVerifystatus().equals("审核未通过") || company.getVerifystatus().equals("审核未通过")) {
                    message = "notPass";
                } else {
                    message = "success";
                }
            } else {
                message = "improve";
            }

        } else {
                message = "notLogin";
        }
        Map map = new HashMap<>();
        map.put("message",message);
        return map;
    }

    //检查客户是否对某条需求报价过,检查是否是客户自己发布的信息.
    @RequestMapping(value = "/checkIsQuoted", method = RequestMethod.POST)
    //@LoginRequired
    @ResponseBody
    public Object checkIsQuoted(@RequestParam("demandid") Integer demandid) {
        String message = null;
        int countQuoted = quoteMapper.countQuotedByUserIdAndDemandid(session.getUser().getId(),demandid);
        if (countQuoted == 1) {
            message = "quoted";
        } else {
            message = "gotoquote";
        }

        int countMydemand = demandMapper.countMydemandByDemandidAndUserid(demandid,session.getUser().getId());
        if(countMydemand == 1){
            message = "mydemand";
        }
        Map map = new HashMap<>();
        map.put("message",message);
        return map;
    }

    //跳转到报价或查看页面
    @RequestMapping("/sell/gotoQuote")
//    @LoginRequired
    public String gotoQuote(@RequestParam(value = "id",required = true)int id,
                            String reqsource, Map<String, Object> model) throws MissingServletRequestParameterException {
        Demand demand = demandMapper.getDemandById(id);
        if(demand == null){
//            throw new NotFoundException("该需求不存在或者已经下架！");
            throw new NotFoundException("该需求已结束！");
        }
        demandMapper.setPageViewTimesById(id);

        // 获取服务器时间，倒计时
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//取服务器的当前时间
        model.put("serverTime", df.format(new Date()));

        // 关注
        model.put("favor", (session != null && session.getUser() != null &&
                interestMapper.getMyInterestBySid(demand.getId(), session.getUser().getId(), "demand") != null &&
                !interestMapper.getMyInterestBySid(demand.getId(), session.getUser().getId(), "demand").isIsdelete()));

        //报价成交记录展示
        List<DealDemand> dealList = demandMapper.getListDealDemand();
        if (dealList != null && dealList.size() > 0) {
            model.put("dealList", dealList);
        }

        // 供应商信息
        Company company = companyMapper.getCompanyByUserid(demand.getUserid());
        model.put("company", company);

        model.put("demand", demandMapper.getDemandById(id));
        model.put("reqsource", reqsource);
        model.put("pstypelist", dataBookMapper.getDataBookListByType("pstype"));
        return "sellInfo";
    }

    //需求详情页面报价
    @RequestMapping(value = "/quote", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object demandQuote(Quote QuoteForm, BindingResult result) {
        Quote q = quoteMapper.getQuoteByUserIdAndDemandid(session.getUser().getId(),QuoteForm.getDemandid());
        String message = "success";
        if(q == null) {
            Demand demand = demandMapper.getDemandById(QuoteForm.getDemandid());
            if(demand != null) {
                if (demand.getTradestatus().equals("开始报价") && demand.isIsdelete() != true) {
                    Company company = companyMapper.getCompanyByUserid(session.getUser().getId());

                    if (null != QuoteForm.getPS()) {
                        QuoteForm.setPSName(dataBookMapper.getDataBookNameByTypeSequence("pstype", QuoteForm.getPS()));
                    }

                    Quote newQuote = new Quote(
                            session.getUser().getId(), demand.getId(), demand.getDemandcode(), QuoteForm.getSupplyton(), QuoteForm.getQuote(), LocalDateTime.now(), "报价中", demand.getDeliverymode(),
                            demand.getDeliverydate(), demand.getDeliverydatestart(), demand.getDeliverydateend(), demand.getQuoteenddate(), company.getName(), demand.getTraderphone(), 1,
                            QuoteForm.getNCV(),QuoteForm.getNCV02(),QuoteForm.getADS(),QuoteForm.getADS02(),QuoteForm.getRS(),QuoteForm.getRS02(),QuoteForm.getTM(),QuoteForm.getTM02(),
                            QuoteForm.getIM(),QuoteForm.getIM02(),QuoteForm.getADV(),QuoteForm.getADV02(),QuoteForm.getRV(),QuoteForm.getRV02(),QuoteForm.getAFT(),QuoteForm.getASH(),
                            QuoteForm.getASH02(),QuoteForm.getHGI(),QuoteForm.getGV(),QuoteForm.getGV02(),QuoteForm.getYV(),QuoteForm.getYV02(),QuoteForm.getFC(),QuoteForm.getFC02(),
                            QuoteForm.getPS(),QuoteForm.getPSName(),QuoteForm.getOriginplace(),QuoteForm.getRemarks(),QuoteForm.getStartport(),QuoteForm.getEndport(),QuoteForm.getDeliverytime1(),
                            QuoteForm.getDeliverytime2());
//                            QuoteForm.getNCV(), QuoteForm.getADS(), QuoteForm.getRS(), QuoteForm.getTM(), QuoteForm.getIM(), QuoteForm.getADV(),
//                            QuoteForm.getRV(), QuoteForm.getAFT(), QuoteForm.getASH(), QuoteForm.getHGI(), QuoteForm.getGV(), QuoteForm.getYV(), QuoteForm.getFC(),
//                            QuoteForm.getPS(), QuoteForm.getPSName(),
//                            QuoteForm.getADV02(), QuoteForm.getRV02(), QuoteForm.getFC02(), QuoteForm.getOriginplace(), QuoteForm.getGV02(), QuoteForm.getYV02(), QuoteForm.getRemarks(),
//                            QuoteForm.getStartport(), QuoteForm.getEndport(), QuoteForm.getDeliverytime1(), QuoteForm.getDeliverytime2()
//                    );

                    quoteMapper.addQuote(newQuote);
                    //主表的报价人数加1
                    demandMapper.plusQuotenum(demand.getDemandcode());
                    //我的需求表报价人数加1
                    mydemandMapper.plusQuotenum(demand.getDemandcode());
                }else {
                    message="该需求已取消!";
                }
            }else{
                message = "notExist";
            }
        }else{
            Quote newQuote = getQuote(QuoteForm, q);
            quoteMapper.modifyQuoteByQuoteid(newQuote);
        }
        Map map = new HashMap<>();
        map.put("message",message);
        return map;
    }

    private Quote getQuote(Quote QuoteForm,Quote q) {
        Quote newQuote = new Quote();
        newQuote.setOriginplace(QuoteForm.getOriginplace());
        newQuote.setNCV(QuoteForm.getNCV());
        newQuote.setNCV02(QuoteForm.getNCV02());
        newQuote.setADS(QuoteForm.getADS());
        newQuote.setADS02(QuoteForm.getADS02());
        newQuote.setRS(QuoteForm.getRS());
        newQuote.setRS02(QuoteForm.getRS02());
        newQuote.setTM(QuoteForm.getTM());
        newQuote.setTM02(QuoteForm.getTM02());
        newQuote.setIM(QuoteForm.getIM());
        newQuote.setIM02(QuoteForm.getIM02());
        newQuote.setADV(QuoteForm.getADV());
        newQuote.setADV02(QuoteForm.getADV02());
        newQuote.setRV(QuoteForm.getRV());
        newQuote.setRV02(QuoteForm.getRV02());
        newQuote.setAFT(QuoteForm.getAFT());
        newQuote.setASH(QuoteForm.getASH());
        newQuote.setASH02(QuoteForm.getASH02());
        newQuote.setHGI(QuoteForm.getHGI());
        newQuote.setGV(QuoteForm.getGV());
        newQuote.setGV02(QuoteForm.getGV02());
        newQuote.setYV(QuoteForm.getYV());
        newQuote.setYV02(QuoteForm.getYV02());
        newQuote.setFC(QuoteForm.getFC());
        newQuote.setFC02(QuoteForm.getFC02());
        newQuote.setPS(QuoteForm.getPS());
        if( null != QuoteForm.getPS()){
            newQuote.setPSName(dataBookMapper.getDataBookNameByTypeSequence("pstype", QuoteForm.getPS()));
        }
        newQuote.setRemarks(QuoteForm.getRemarks());
        newQuote.setStartport(QuoteForm.getStartport());
        newQuote.setEndport(QuoteForm.getEndport());
        newQuote.setDeliverytime1(QuoteForm.getDeliverytime1());
        newQuote.setDeliverytime2(QuoteForm.getDeliverytime2());
        newQuote.setSupplyton(QuoteForm.getSupplyton());
        newQuote.setQuote(QuoteForm.getQuote());
        newQuote.setLastupdatetime(LocalDateTime.now());
        newQuote.setId(q.getId());

        return newQuote;
    }


    //查询热值和硫分区间返回过滤的结果集做查询区间
    //返回高热值结果集
    @RequestMapping(value = "/getHighHeatValue", method = RequestMethod.POST)
    @ResponseBody
    public List getHighHeatValue(@RequestParam("lowHotValue") Integer lowHotValue) {
        if(lowHotValue == 1){
            lowHotValue = 3500;
        }
        List list = DropdownListUtil.genMapIntList(lowHotValue);
        return list;
    }

    //返回高硫分结果集
    @RequestMapping(value = "/getHighSulfurContent", method = RequestMethod.POST)
    @ResponseBody
    public List getHighSulfurContent(@RequestParam("lowSulfurContent") String lowSulfurContent) {
        if(lowSulfurContent.equals("1")){
            lowSulfurContent = "0.1";
        }
        List decilmalList = DropdownListUtil.genMapDoubleList(Double.parseDouble(lowSulfurContent));
        return decilmalList;
    }

    //发布需求
    @RequestMapping("/buy/releaseDemand")
    @LoginRequired
    public String releaseDemand(Map<String, Object> model, String demandcode) {
        Demand demand = demandMapper.getDemandByDemandcodeAndUserid(demandcode, session.getUser().getId());
        List<Areaport> portsList = null;
        if (demand != null) {
            portsList = areaportMapper.getProcvincesOrPortsByParentname(demand.getDeliveryprovince());
            model.put("demand", demand);
        } else{
            portsList = areaportMapper.getProcvincesOrPortsByParentid(areaportMapper.getAllProvince().get(0).getId());
        }
        List<Dictionary> cocaltypeList = dictionaryMapper.getAllCoalTypes();
        List<Areaport> provincesList = areaportMapper.getAllProvince();

        Areaport areaport = new Areaport("其它", "other");
        portsList.add(areaport);

       // List<Areaport> areaList = areaportMapper.getAllArea();
        //List<Dictionary> deliverymodeList = dictionaryMapper.getDeliverymodes();
        List<Dictionary> orgList = dictionaryMapper.getAllInspectionagencys();
        List<Dictionary> inspectorgs = new ArrayList<>();
        Dictionary d1 = new Dictionary("nothing", "无");
        Dictionary d2 = new Dictionary("ohter", "其它");
        inspectorgs.add(d1);
        for (int i = 0; i < orgList.size(); i++) {
            inspectorgs.add(orgList.get(i));
        }
        inspectorgs.add(d2);

        model.put("cocaltypeList", cocaltypeList);
      //  model.put("areaList", areaList);
        model.put("provincelist", provincesList);
        model.put("harbourlist", portsList);
        model.put("deliverymodeList", dataBookMapper.getDataBookListByType("deliverymode"));
        model.put("inspectorgs", inspectorgs);
        model.put("pstypelist", dataBookMapper.getDataBookListByType("pstype"));
        model.put("paymodeList", dataBookMapper.getDataBookListByType("demandpaymode"));
        model.put("transportationModeList", dataBookMapper.getDataBookListByType("transportmode"));
        return "releaseDemand";
    }

    //根据父id获取子集-省分
    @RequestMapping(value = "/getProvincesByParentid", method = RequestMethod.POST)
    @ResponseBody
    public List<Areaport> getProcvincesByParentid(@RequestParam("id") Integer id) {
        List<Areaport> provincesList = null;
        if(id != 0) {
            provincesList = areaportMapper.getProvincesOrPortsByParentid(id);
        }else{
            provincesList = areaportMapper.getAllProvince();
        }
        return provincesList;
    }

   /* //根据父id获取子集-港口
    @RequestMapping(value = "/getPortsByParentid", method = RequestMethod.POST)
    @ResponseBody
    public List<Areaport> getPortsByParentid(@RequestParam("id") Integer id) {
        List<Areaport> portsList = areaportMapper.getProvincesOrPortsByParentid(id);
        Areaport areaport = new Areaport("其它", "other");
        portsList.add(areaport);
        return portsList;
    }*/

    //根据省分id获取子集-港口和地区
    @RequestMapping(value = "/getPortsByParentid", method = RequestMethod.POST)
    @ResponseBody
    public Object getPortsByParentid(@RequestParam("id") Integer id) {
        Map map = new HashMap<>();
        List<Areaport> portsList = areaportMapper.getProvincesOrPortsByParentid(id);
        Areaport areaport = new Areaport("其它", "other");
        portsList.add(areaport);
        map.put("portsList",portsList);
        Areaport  area = areaportMapper.getAreaByProvinceId(id);
        map.put("area",area);
        return map;
    }

    //根据父名称获取子集-省分
    @RequestMapping(value = "/getProvincesByParentname", method = RequestMethod.POST)
    @ResponseBody
    public List<Areaport> getProcvincesByParentname(@RequestParam("name") String name) {
        List<Areaport> provincesList = null;
        if(!name.equals("全部")) {
           provincesList = areaportMapper.getProcvincesOrPortsByParentname(name);
        }else{
            provincesList = areaportMapper.getAllProvince();
        }
        return provincesList;
    }

    //根据父名称获取子集-港口
    @RequestMapping(value = "/getPortsByParentname", method = RequestMethod.POST)
    @ResponseBody
    public List<Areaport> getPortsByParentname(@RequestParam("name") String name) {
        List<Areaport> portsList = areaportMapper.getProcvincesOrPortsByParentname(name);
        Areaport areaport = new Areaport("其它", "other");
        portsList.add(areaport);
        return portsList;
    }

    //通过省分id获取地区对象
    @RequestMapping(value = "/getAreaByProvinceId", method = RequestMethod.POST)
    @ResponseBody
    public Object getAreaByProvinceId(@RequestParam("provinceid") int provinceid) {
        Areaport  area = areaportMapper.getAreaByProvinceId(provinceid);
        List<Areaport> areaList = areaportMapper.getAllArea();
        Map map = new HashMap<>();
        map.put("area",area);
        return map;
    }

    //通过省分名称获取地区对象和集合
    @RequestMapping(value = "/getAreaByProvinceName", method = RequestMethod.POST)
    @ResponseBody
    public Object getAreaByProvinceName(@RequestParam("provincename") String  provincename) {
        Areaport  area = areaportMapper.getAreaByProvincename(provincename);
        List<Areaport> areaList = areaportMapper.getAllArea();
        Map map = new HashMap<>();
        map.put("area",area);
        return map;
    }


    //需求发布校验
    @RequestMapping(value = "/checkDemand", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object checkDemand(@Valid DemandForm demandForm, BindingResult result) {
        Demand demand = new Demand();
        demand.setUserid(session.getUser().getId());
        Company company = companyMapper.getCompanyByUserid(session.getUser().getId());
        demand.setDemandcustomer(company.getName());
        demand.setCoaltype(demandForm.coaltype);

        //TODO:   default value?
        demand.setNCV(demandForm.NCV);
        demand.setNCV02(demandForm.NCV02);
        demand.setADS(demandForm.ADS);
        demand.setADS02(demandForm.ADS02);
        demand.setRS(demandForm.RS);
        demand.setRS02(demandForm.RS02);
        demand.setTM(demandForm.TM);
        demand.setTM02(demandForm.TM02);
        demand.setIM(demandForm.IM);
        demand.setIM02(demandForm.IM02);
        demand.setADV(demandForm.ADV);
        demand.setRV(demandForm.RV);
        demand.setAFT(demandForm.AFT);
        demand.setASH(demandForm.ASH);
        demand.setASH02(demandForm.ASH02);
        demand.setHGI(demandForm.HGI);
        // add GV, YV, FC, PS, PSName, ADV02, RV02, FC02, GV02, YV02
        demand.setGV(demandForm.GV);
        demand.setGV02(demandForm.GV02);
        demand.setYV(demandForm.YV);
        demand.setYV02(demandForm.YV02);
        demand.setFC(demandForm.FC);
        demand.setFC02(demandForm.FC02);

        if (demandForm.PS != null) {
            demand.setPS(demandForm.PS);
            String psName = dataBookMapper.getDataBookNameByTypeSequence("pstype", demandForm.PS);
            demand.setPSName(psName);
        }

        demand.setADV02(demandForm.ADV02);
        demand.setRV02(demandForm.RV02);
        demand.setPaymode(demandForm.paymode);
        demand.setTransportmode(demandForm.transportmode);
        Areaport areaport = areaportMapper.getAreaByProvincename(demandForm.deliveryprovince);
        demand.setDeliverydistrict(areaport.getName());
        demand.setDeliveryprovince(demandForm.deliveryprovince);
        demand.setDeliveryplace(demandForm.deliveryplace);  //提货地点可能是"其它"
        if (demandForm.otherplace != null && demandForm.otherplace != "" && !demandForm.otherplace.equals(",")) {
            String otherplaceArr[] = demandForm.otherplace.split(",");
            demand.setOtherplace(otherplaceArr[0]);
        } else {
            demand.setOtherplace(null);
        }
        demand.setDemandamount(Integer.parseInt(demandForm.demandamount));
        demand.setDeliverymode(demandForm.deliverymode);
//        if (demandForm.deliverymode.equals("场地自提") || demandForm.deliverymode.equals("车板交货") ) {   //提货时间可以不用判断提货方式来set,如果不用,就要判断是否为空.
//            demand.setDeliverydatestart(demandForm.deliverydatestart);
//            demand.setDeliverydateend(demandForm.deliverydateend);
//        } else {
//            demand.setDeliverydate(demandForm.deliverydate);
//        }
        if (demandForm.deliverymode.equals("港口平仓") || demandForm.deliverymode.equals("到岸舱底") ) {
            demand.setDeliverydate(demandForm.deliverydate);
        } else {
            demand.setDeliverydatestart(demandForm.deliverydatestart);
            demand.setDeliverydateend(demandForm.deliverydateend);
        }
        demand.setResidualdemand(Integer.parseInt(demandForm.demandamount));
        demand.setInspectionagency(demandForm.inspectionagency);
        if (demandForm.otherorg != null && demandForm.otherorg != "" && !demandForm.otherorg.equals(",")) {
            String otherorgArr[] = demandForm.otherorg.split(",");
            demand.setOtherorg(otherorgArr[0]);
        } else {
            demand.setOtherorg(null);
        }
        //报价截止日期
        demand.setQuoteenddate(demandForm.quoteenddate);
        demand.setNoshowdate(demandForm.quoteenddate.plusDays(7));  //7天后
        demand.setCheckstatus("待审核");
        demand.setTradestatus("未开始报价");
        demand.setComment(null);
        demand.setReleasecomment(demandForm.releasecomment);
        //设置需求区域id、省分id、港口id
        demand.setRegionId(areaportMapper.getParentIdByName(demandForm.getDeliveryprovince()));
        demand.setProvinceId(areaportMapper.getIdByName(demandForm.getDeliveryprovince()));
        Integer portId=areaportMapper.getIdByName(demandForm.getDeliveryplace());
        portId=portId==null?-1:portId;
        demand.setPortId(portId);
        demand.setClienttype(1);
        toolsMethod.doCheckAndChangeIndex(demand);

        Demand d1 = demandMapper.getDemandByDemandcodeAndUserid(demandForm.demandcode, session.getUser().getId());
        Map map = new HashMap();
        if (d1 == null) {
            demandMapper.addDemand(demand);
            Demand d2 = demandMapper.getDemandById(demand.getId());
            //需求编号改为数据库生成;这里传需求编号为修改做准备
            map.put("demandcode", d2.getDemandcode());

        } else {
            if(d1.getCheckstatus().equals("审核未通过")){
                demandMapper.addDemand(demand);
                Demand d2 = demandMapper.getDemandById(demand.getId());
                map.put("demandcode", d2.getDemandcode());
                //删除我的需求审核未通过的
                mydemandMapper.deleteMyDemandByDemandcode(d1.getDemandcode(),session.getUser().getId());

            }else if(d1.getTradestatus().equals("报价结束")){
                demandMapper.addDemand(demand);
                Demand d2 = demandMapper.getDemandById(demand.getId());
                map.put("demandcode", d2.getDemandcode());
            }else{
                demand.setId(d1.getId());
                demand.setDemandcode(d1.getDemandcode());
                demandMapper.modifyDemand(demand);
                map.put("demandcode", demandForm.demandcode);
            }
        }
        map.put("success", true);
        return map;
    }

    //跳转到需求发布校验页面
    @RequestMapping(value = "/buy/gotoCheck", method = RequestMethod.GET)
    @LoginRequired
    public String gotoCheck(@RequestParam(value="demandcode", required = true) String demandcode, Map<String, Object> model) {
        Demand demand = demandMapper.getDemandByDemandcodeAndUserid(demandcode,session.getUser().getId());
        model.put("demand", demand);
//        return "checkDemandQuota";
        return "checkDemand";
    }

    //保存需求(更改发布状态)
    @RequestMapping(value = "/saveDemand", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public boolean saveDemand(@RequestParam("demandcode") String demandcode) {
        demandMapper.modifyReleaseStatusByDemandcode(demandcode);
        Demand d = demandMapper.getDemandByDemandcode(demandcode);
        Mydemand md = mydemandMapper.getMydemandByDemandcode(demandcode);
        Mydemand mydemand = new Mydemand(session.getUser().getId(), d.getDemandcode(),
                d.getReleasetime(), d.getQuoteenddate(), d.getDemandamount(), "审核中",
                d.getDeliverydate(), d.getDeliverydatestart(), d.getDeliverydateend());
        if(md == null) {
            //把记录放入我的需求表
            mydemandMapper.addMydemand(mydemand);
        }else{
            mydemandMapper.modifyMyDemand(mydemand);
        }
        return true;
    }

    //跳转到需求发布校验页面
    @RequestMapping(value = "/buy/relSuccess", method = RequestMethod.GET)
    @LoginRequired
    public String relDemandSucc() {
        return "relDemandSuccess";
    }

    //报价状态-中标
    @RequestMapping(value = "/quoteBid", method = RequestMethod.POST)
    @ResponseBody
    public boolean quoteBid(@RequestParam("quoteid") Integer quoteid) {
        quoteMapper.modifyStatusByQuoteid("已中标",quoteid);
        //我的需求表已匹配数量增加,累加
        Quote quote = quoteMapper.getQuoteById(quoteid);
        demandMapper.modifyPurchaseNumByDemandcode(quote.getSupplyton(), quote.getDemandcode());
        demandMapper.modifyStatusByDemandCode("已中标", quote.getDemandcode());
        mydemandMapper.modifyPurchaseNumByDemandcode(quote.getSupplyton(), quote.getDemandcode());

        if(quoteMapper.getCountNotBidByDemandcode(quote.getDemandcode())==0){//全部都报价了，则将我的需求状态改为匹配结束
            mydemandMapper.modifyStatusByDemandCode("匹配结束",quote.getDemandcode());
        }
        return true;
    }

    //导入数据
    @RequestMapping(value = "/getDemandByDemandId", method = RequestMethod.POST)
    @ResponseBody
    public Demand GetDemandByDemandId(@RequestParam(value="id", required = true)Integer id) {
        Demand demand = demandMapper.getDemandById(id);
        if(demand == null){
            throw new NotFoundException("该需求不存在！");
        }else{
            return demand;
        }
    }
}
