package kitt.admin.controller;

import com.itextpdf.text.DocumentException;
import com.mysql.jdbc.StringUtils;
import freemarker.template.TemplateException;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.*;
import kitt.core.bl.BuyService;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.Freemarker;
import kitt.core.service.PDF;
import kitt.core.util.Pager;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 15-1-14.
 */

@Controller
public class CustomerController {
    @Autowired
    private MyCustomerMapper myCustomerMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private DemandMapper demandMapper;
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private QuoteMapper quoteMapper;
    @Autowired
    private ManualSellMapper manualSellMapper;
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private GroupBuyQualificationMapper groupBuyQualificationMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private GroupBuyOrderMapper groupBuyOrderMapper;
    @Autowired
    private GroupBuySupplyMapper groupBuySupplyMapper;
    @Autowired
    private Freemarker freemarker;
    @Autowired
    private DemandService demandService;
    @Autowired
    private ManualSellService manualSellService;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private Tools tools;
    @Autowired
    private BuyService buyService;
    @Autowired
    private Auth auth;
    @Autowired
    private Session session;

    private String contractContent;
    private String constractNo;
    private static final int pageSize= 10;

    /**
     * 我的客户列表
     * @param page  第几页
     * @return      客户对象List
     */
    @RequestMapping("/customer/list")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object listCustomer(@RequestParam(value = "page",defaultValue = "1")Integer page,
                               @RequestParam(value = "keyWord",required = false)String keyWord ) {
        return  myCustomerMapper.getAllCustomerList(keyWord, page, pageSize);
    }

    /**
     * 我的客户详细
     * @param id   客户id
     * @return     该客户的详细资料
     */
    @RequestMapping("/customer/showCustomerDetail")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Customer showCustomerDetail(@RequestParam("id") Integer id) {
        return myCustomerMapper.load(id);
    }

    @RequestMapping("/customer/searchCustomer")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showCustomerList(@RequestParam("keyword") String keyword) {
        Customer customer = myCustomerMapper.customerSearch(keyword);
        return customer!=null?customer:Boolean.FALSE;
    }

    /**
     * 订单列表
     * @param status     订单状态
     * @param orderType  订单类型， 分为 MallOrder OtherOrder
     * @param page       第几页
     * @return           订单List
     */
    @RequestMapping("/customer/orderlist")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showCustomerOrders(@RequestParam(value = "status", required = false, defaultValue = "WaitSignContract") EnumOrder status,
                                     @RequestParam(value = "ordertype", required = true)EnumOrder orderType,
                                     @RequestParam(value = "page", defaultValue = "1")Integer page) {
        return orderMapper.getAllOrderList(page, pageSize, status, orderType);
    }

    /**
     * 订单列表， 取消订单操作
     * @param id         订单id
     * @param version    订单version
     * @return
     */
    @RequestMapping("/customer/order/cancel")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public boolean doCancelOrder(@RequestParam(value = "id", required = true)int id,
                                 @RequestParam(value = "version", required = true)int version){
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if(order == null) throw new NotFoundException();
        if(buyMapper.getSellInfoById(order.getSellinfoid()) == null) throw new NotFoundException();
        try {
            buyService.changeOrderStatusPlusSellInfoQuantity(order, true, EnumOrder.Canceled, session.getAdmin().getId(), session.getAdmin().getUsername(), "管理员取消订单");
        } catch (SQLExcutionErrorException e) {
            auth.doOutputErrorInfo("订单取消出错, 订单id=" + order.getId() + ", 管理员工号:" + session.getAdmin().getJobnum());
            throw new BusinessException("订单取消失败！请联系技术人员！");
        }
        return true;
    }

    /**
     * 需求列表
     * @param demand    需求对象
     * @param page      第几页
     * @return          需求List
     */
    @RequestMapping("/customer/demandlist")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showCustomerDemands(Demand demand,
                                      @RequestParam(value = "sorttype", required = false, defaultValue = "demandcode")String sortType,
                                      @RequestParam(value = "sortorder", required = false, defaultValue = "false")boolean sortOrder,
                                      @RequestParam(value = "currentId", required = false, defaultValue = "CUnCheck")String CurrentId,
                                      @RequestParam(value = "page", required = false, defaultValue = "1")int page) {
        if(!demand.isIsdelete() && StringUtils.isNullOrEmpty(demand.getCheckstatus())){
            demand.setCheckstatus("待审核");
        }
        if(demand.getCheckstatus().equals("待审核") && sortType.equals("checktime")){
            sortType = "demandcode";
        }
        final String sortTypeTemp = sortType;
        return new Object(){
            public Pager<Demand> demandList = demandMapper.getAllDemandByStatus(demand, sortTypeTemp, sortOrder, page, pageSize);
            public String checkstatus = demand.getCheckstatus();
            public String tradestatus = demand.getTradestatus();
            public boolean isdelete = demand.isIsdelete();
            public String sorttype = sortTypeTemp;
            public boolean sortorder = sortOrder;
            public String currentId = CurrentId;
        };
    }

    /**
     * 取消需求操作
     * @param id   需求id
     * @return     true or false
     */
    @RequestMapping("/customer/demand/cancel")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doCancelDemand(@RequestParam(value = "id", required = true)int id) {
        Demand demand = demandMapper.getDemandById(id);
        if (demand == null) throw new NotFoundException();
        return demandService.doCancelDemandByAdmin(demand);
    }

    /**
     * 供应列表
     * @param sellInfo   供应对象
     * @param page       第几页
     * @return           true or false
     */
    @RequestMapping("/customer/supplylist")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showCustomerSupply(SellInfo sellInfo,
                                     @RequestParam(value = "sorttype", required = false, defaultValue = "pid")String sortType,
                                     @RequestParam(value = "sortorder", required = false, defaultValue = "false")boolean sortOrder,
                                     @RequestParam(value = "currentId", required = false, defaultValue = "CWaitVerify")String CurrentId,
                                     @RequestParam(value = "page", defaultValue = "1")Integer page) {
        if(sellInfo.getStatus() == null){
            sellInfo.setStatus(EnumSellInfo.WaitVerify);
        }
        if(sellInfo.getStatus().equals(EnumSellInfo.WaitVerify) && sortType.equals("verifytime")){
            sortType = "pid";
        }
        final String sortTypeTemp = sortType;
        return new Object() {
            public Pager<SellInfo> supplyList = buyMapper.getAllSupplyList(sellInfo,sortTypeTemp,sortOrder,page,pageSize);
            public String status = sellInfo.getStatus().toString();
            public String sorttype = sortTypeTemp;
            public boolean sortorder = sortOrder;
            public String currentId = CurrentId;
        };
    }

    /**
     * 取消供应操作
     * @param id       供应id
     * @param version  供应version
     * @return         true or false
     */
    @RequestMapping("/customer/supply/cancel")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doCancelSupply(@RequestParam(value = "id", required = true)int id,
                                  @RequestParam(value = "version",required = true)int version){
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if(sellInfo == null) throw new NotFoundException();
        if(!buyService.changeSellInfoStatus(id, version, EnumSellInfo.Canceled, "管理员(" + session.getAdmin().getJobnum() + ")取消供应.")){
            auth.doOutputErrorInfo("取消供应信息(sellinfo)出错，供应信息id=" + id + ", version=" + version);
            throw new BusinessException("取消供应信息失败，请刷新页面后重试");
        }
        return true;
    }

    /**
     * 报价列表
     * @param status  报价状态
     * @param page    第几页
     * @return        报价List
     */
    @RequestMapping("/customer/quotelist")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showCustomerQuote(@RequestParam(value = "status", required = false, defaultValue = "报价中") String status,
                                    @RequestParam(value = "page", required = false) Integer page) {
        return quoteMapper.getAllQuotesList(status, page, pageSize);
    }

    /**
     *  委托人工
     * @param map   委托人工参数map
     * @return
     */
    @RequestMapping("/customer/manualsell")
    @ResponseBody
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showCustomerManualLookup(@RequestParam Map<String, Object> map){
        Map<String, Object> model = new HashMap<>();
        if(StringUtils.isNullOrEmpty(String.valueOf(map.get("status")))){
            map.put("status", "ToBeSolved");
        }
        Integer portId = Integer.valueOf(String.valueOf(map.get("deliveryAddr")));
        Integer provinceId = Integer.valueOf(String.valueOf(map.get("deliveryProvince")));
        Integer regionId = Integer.valueOf(String.valueOf(map.get("deliveryDistrict")));
        model.put("regionList", areaportMapper.getAllArea());
        model.put("provinceList", tools.getMallProvinces(regionId));
        model.put("harbourList", tools.getMallPorts(provinceId));
        map.put("deliveryDistrict", areaportMapper.getNameById(regionId));
        map.put("deliveryProvince", areaportMapper.getNameById(provinceId));
        map.put("deliveryAddr", areaportMapper.getNameById(portId));
        model.put("deliveryDistrict", regionId);
        model.put("deliveryProvince", provinceId);
        model.put("deliveryAddr", portId);
        model.put("clientTypeList", dataBookMapper.getDataBookListByType("clienttype"));
        model.put("clienttype", String.valueOf(map.get("clienttype")));
        model.put("manualselllist", manualSellMapper.getAllManualSellList(map, Integer.valueOf(map.get("page").toString()), pageSize));
        return model;
    }

    /**
     * 查询委托人工导出所选择数据条数
     * @param map
     * @return
     */
    @RequestMapping(value = "/customer/countmanualsell",method = RequestMethod.POST)
    @ResponseBody
    public Integer countManualsell(@RequestParam Map<String, Object> map){
        if(StringUtils.isNullOrEmpty(String.valueOf(map.get("status")))){
            map.put("status", "ToBeSolved");
        }
        Integer portId = map.get("deliveryAddr") == null ? 0 : Integer.valueOf(String.valueOf(map.get("deliveryAddr")));
        Integer provinceId = map.get("deliveryProvince") == null ? 0 : Integer.valueOf(String.valueOf(map.get("deliveryProvince")));
        Integer regionId = map.get("deliveryDistrict") == null ? 0 : Integer.valueOf(String.valueOf(map.get("deliveryDistrict")));
        map.put("deliveryDistrict", areaportMapper.getNameById(regionId));
        map.put("deliveryProvince", areaportMapper.getNameById(provinceId));
        map.put("deliveryAddr", areaportMapper.getNameById(portId));
        return manualSellMapper.countAllManualsell(map);
    }

    //下载委托人工excel
    @RequestMapping(value = "/customer/downloadmanualsell")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public void downloadManualsell(@RequestParam Map<String, Object> map,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException, DocumentException {
        if(StringUtils.isNullOrEmpty(String.valueOf(map.get("status")))){
            map.put("status", "ToBeSolved");
        }
        Integer portId = map.get("deliveryAddr") == null ? 0 : Integer.valueOf(String.valueOf(map.get("deliveryAddr")));
        Integer provinceId = map.get("deliveryProvince") == null ? 0 : Integer.valueOf(String.valueOf(map.get("deliveryProvince")));
        Integer regionId = map.get("deliveryDistrict") == null ? 0 : Integer.valueOf(String.valueOf(map.get("deliveryDistrict")));
        map.put("deliveryDistrict", areaportMapper.getNameById(regionId));
        map.put("deliveryProvince", areaportMapper.getNameById(provinceId));
        map.put("deliveryAddr", areaportMapper.getNameById(portId));
        List<ManualSell> manualSells= manualSellMapper.listAllManualsell(map, 1000, 0);
        String filename = Integer.valueOf(String.valueOf(map.get("type")))==1?"委托人工找货":"委托人工销售";

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(filename);
        HSSFRow row = sheet.createRow(0);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sheet.setVerticallyCenter(true);
        sheet.setHorizontallyCenter(true);
        String[] excelHeader = {"序号","煤种","低位热值","收到基硫份","空干基挥发","提货地点","需求数量","提货方式","联系人","联系方式","公司名称"};
        for (int i = 0; i < excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(i,true);
        }
        for(int i=0;i<manualSells.size();i++){
            ManualSell manual =  manualSells.get(i);
            sheet.autoSizeColumn(i,true);
            row = sheet.createRow(i+1);
            row.setRowStyle(cellStyle);
            row.createCell(0).setCellValue(i+1);
            row.createCell(1).setCellValue(manual.getCoalType());
            row.createCell(2).setCellValue(manual.getLowcalorificvalue());
            row.createCell(3).setCellValue(String.valueOf(manual.getReceivebasissulfur()));
            row.createCell(4).setCellValue(String.valueOf(manual.getAirdrybasisvolatile()));
            row.createCell(5).setCellValue(manual.getDeliveryProvince() + manual.getDeliveryAddr());
            row.createCell(6).setCellValue(manual.getDemandAmount());
            row.createCell(7).setCellValue(manual.getDeliveryMode());
            row.createCell(8).setCellValue(manual.getContactName());
            row.createCell(9).setCellValue(manual.getPhone());
            row.createCell(10).setCellValue(manual.getCompanyName());
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        filename += LocalDate.now() + ".xls";
        if(request.getHeader("user-agent").toLowerCase().contains("firefox")) {
            filename =  new String(filename.getBytes("UTF-8"), "ISO-8859-1");
        } else {
            filename = URLEncoder.encode(filename, "UTF-8");
        }
        response.addHeader("Content-Disposition", "attachment; filename=" + filename);
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
    }

    /**
     * 处理委托人共表
     * @param id              id
     * @param solvedremarks   处理备注
     * @return
     */
    @RequestMapping("/customer/manual/solved")
    @ResponseBody
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    private boolean doSolveManual(@RequestParam(value = "id", required = true)int id,
                                  @RequestParam(value = "solvedremarks", required = true)String solvedremarks){
        ManualSell manualSell = manualSellMapper.getManualSellById(id);
        if(manualSell == null) throw new NotFoundException();
        manualSell.setStatus("Solved");
        manualSell.setSolvedremarks(solvedremarks.replaceAll(" ", ""));
        return manualSellService.doSolveManualMethod(manualSell);
    }

    /**
     * 需求所有报价列表
     * @param demandCode      需求编号
     * @param clienttypeTemp  客户端类型
     * @param page
     * @return
     */
    @RequestMapping("/customer/quoteresult")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showQuoteByDemandCode(@RequestParam("demandcode") String demandCode,
                                        @RequestParam(value = "clienttype", required = false, defaultValue = "0") int clienttypeTemp,
                                        @RequestParam(value = "page", required = false) Integer page){
        Pager<Quote> quoteList = quoteMapper.getQuoteByDemandCode(demandCode, clienttypeTemp, null, page, pageSize);
        return new Object() {
            public List<Quote> list = quoteList.getList();
            public int page = quoteList.getPage();
            public int count = quoteList.getCount();
            public int clienttype = clienttypeTemp;
            public List<DataBook> clientTypeList = dataBookMapper.getDataBookListByType("clienttype");
        };
    }

    //已中标的报价
    @RequestMapping("/customer/inbidquote")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showBidQuoteByDemandCode(@RequestParam("demandcode") String demandCode,
                                           @RequestParam(value = "page", required = false) Integer page){
        final String status = "已中标";
        return quoteMapper.getQuoteByDemandCode(demandCode, 0,status, page, pageSize);
    }

    /**
     * 委托人工详细
     * @param id   委托人工id
     * @return     委托人工对象
     */
    @RequestMapping(value = "/customer/manualselldetail/{id}")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object showManualsellDetail(@PathVariable String id) {
        ManualSell manualSell=manualSellMapper.loadByManualId(id);
        if(manualSell==null){
            throw new NotFoundException();
        }
       return  manualSell;
    }

    //撮合交易--供应信息
    @RequestMapping("/customer/bringdealsellinfo")
    @ResponseBody
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object bringDealSellInfo(SellInfo sellInfo,
                                    @RequestParam(value = "sorttype", required = false, defaultValue = "createtime")String sortType,
                                    @RequestParam(value = "sortorder", required = false, defaultValue = "false")boolean sortOrder,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) Integer page){
        Map<String,Object> model = new HashMap<String,Object>();
        sellInfo.setStatus(EnumSellInfo.VerifyPass);
        List<Areaport> areas = areaportMapper.getAllArea();
        List<Dictionary> deliverymodeList = dictionaryMapper.getDeliverymodes();
        List<Dictionary> cocaltypeList = dictionaryMapper.getAllCoalTypes();
        model.put("area",areas);
        model.put("deliverymodeList",deliverymodeList);
        model.put("cocaltypeList", cocaltypeList);
        model.put("sellinfolist", buyMapper.getAllSupplyList(sellInfo, sortType, sortOrder, page, pageSize));
       return model;
    }

    //撮合交易--需求信息
    @RequestMapping("/customer/bringdealdemandinfo")
    @ResponseBody
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object bringDealDemandInfo(Demand demand,
                                      @RequestParam(value = "sorttype", required = false, defaultValue = "demandcode")String sortType,
                                      @RequestParam(value = "sortorder", required = false, defaultValue = "false")boolean sortOrder,
                                      @RequestParam(value = "page", defaultValue = "1", required = false) Integer page){
        Map<String,Object> model = new HashMap<String,Object>();
        demand.setCheckstatus("审核通过");
        List<Areaport> areas = areaportMapper.getAllArea();
        List<Dictionary> deliverymodeList = dictionaryMapper.getDeliverymodes();
        List<Dictionary> cocaltypeList = dictionaryMapper.getAllCoalTypes();
        model.put("area", areas);
        model.put("deliverymodeList",deliverymodeList);
        model.put("cocaltypeList", cocaltypeList);
        model.put("demandlist", demandMapper.getAllDemandByStatus(demand, sortType, sortOrder, page, pageSize));
        return model;
    }

    //团购资质
    @RequestMapping("/customer/teamorderQualification")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object lookteamOrderQualification(GroupBuyQualification groupBuyQualification,@RequestParam(value = "page", defaultValue = "1", required = false)Integer page){
        return groupBuyQualificationMapper.listAllQualification(groupBuyQualification, page, pageSize);
    }

    //团购订单
    @RequestMapping("/customer/teamorder")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object lookteamOrder(GroupBuyOrder groupBuyOrder,@RequestParam(value = "page", defaultValue = "1", required = false)Integer page){
        return groupBuyOrderMapper.pageAllGroupBuyOrder(groupBuyOrder, page, pageSize);
    }

    //团购信息详细
    @RequestMapping("/customer/teamorderDetail")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object lookteamOrderDetail(@RequestParam(value="supplyCode")String supplyCode){
        return groupBuySupplyMapper.loadBygroupBycode(supplyCode);
    }

    @RequestMapping("/customer/depositContract")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public String lookDepositContract(HttpServletRequest request,GroupBuyQualification groupBuyQualification,Model model) throws IOException, TemplateException {
        final Company company = companyMapper.getCompanyByUserid(groupBuyQualification.getUserid());
        constractNo= groupBuyQualification.getQualificationcode();
        //合同内容
        contractContent= freemarker.render("/contract/depositTemplate",new HashMap<String,Object>(){{
            put("qualificationNo",constractNo);
            put("phone",company.getPhone());
            put("fax",company.getFax());
            put("createtime", LocalDate.now());
            put("companyname",company.getName());
            put("companyaddress",company.getAddress());
            put("legalpersonname",company.getLegalpersonname());
            put("companyaccount",company.getAccount());
            put("companyzipcode", company.getZipcode());
            put("companyopeningbank", company.getOpeningbank());
            put("companyidentificationnumword", company.getIdentificationnumword());
            put("year",LocalDate.now().getYear());
            put("month",LocalDate.now().getMonthValue());
            put("day",LocalDate.now().getDayOfMonth());
            put("ctx", request.getScheme() + "://"+ request.getServerName()+":"+request.getServerPort());
        }});
        model.addAttribute("contract", contractContent);
        return "depositContract";
    }

    @RequestMapping("/customer/lookagreementContract")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public String lookagreementContract(HttpServletRequest request,GroupBuyOrder order,Model model) throws IOException, TemplateException {
        Company company = companyMapper.getCompanyByUserid(order.getUserid());
        constractNo= order.getPerformancecode();
        //合同内容
        contractContent= freemarker.render("/contract/agreementTemplate",new HashMap<String,Object>(){{
            put("qualificationNo",constractNo);
            put("phone",company.getPhone());
            put("fax",company.getFax());
            put("createtime", LocalDate.now());
            put("companyname",company.getName());
            put("companyaddress",company.getAddress());
            put("legalpersonname",company.getLegalpersonname());
            put("companyaccount",company.getAccount());
            put("companyzipcode", company.getZipcode());
            put("companyopeningbank", company.getOpeningbank());
            put("companyidentificationnumword", company.getIdentificationnumword());
            put("year",LocalDate.now().getYear());
            put("month",LocalDate.now().getMonthValue());
            put("day",LocalDate.now().getDayOfMonth());
            put("ctx", request.getScheme() + "://"+ request.getServerName()+":"+request.getServerPort());

        }});
        model.addAttribute("contract", contractContent);
        return "depositContract";
    }

    //下载电子合同
    @RequestMapping("/customer/download/constract")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
     public HttpEntity<byte[]> downloadContract() throws IOException, DocumentException {
        //controller是单例的，constract定义为全局变量，方便拿markdown的字符串.
        File file = PDF.create(contractContent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", constractNo+".pdf");
        return new HttpEntity<byte[]>(FileUtils.readFileToByteArray(file), headers);
     }

    /**
     * 设置文字在单元格里面的 对齐方式
     * @param cellStyle
     * @return
     */
    public static CellStyle alignmentDecorate(CellStyle cellStyle){
        //设置上下
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        //设置左右
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        return cellStyle;
    }
}
