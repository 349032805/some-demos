package kitt.admin.service;

import com.mysql.jdbc.StringUtils;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.MessageNotice;
import kitt.ext.mybatis.Where;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yimei on 15/7/21.
 */
@Service
public class DemandService {
    @Autowired
    private MydemandMapper mydemandMapper;
    @Autowired
    private DemandMapper demandMapper;
    @Autowired
    private QuoteMapper quoteMapper;
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private Tools tools;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DealerMapper dealerMapper;

    /**
     * admin 管理员 取消需求信息
     * @param demand  需求信息对象
     * @return  true or false
     */
    @Transactional
    public boolean doCancelDemandByAdmin(Demand demand){
        //将主表的需求状态设置为已删除,保留信息
        demandMapper.modifyIsdeleteByDemandcode(demand.getDemandcode());
        //将我的需求表的状态改为交易结束
        mydemandMapper.modifyStatusByDemandcode("交易结束", demand.getDemandcode());
        //如果针对需求有报价, 将报价状态改为未中标
        List<Quote> quoteList = quoteMapper.getQuoteByDemandcode(demand.getDemandcode());
        if (quoteList != null && quoteList.size() > 0) {
            for (Quote quote : quoteList) {
                quoteMapper.modifyStatusByQuoteid("未中标", quote.getId());
            }
        }
        return true;
    }

    /**
     * 获取需求信息列表
     * @param region      区域 id
     * @param province    省份 id
     * @param harbour     港口 id
     * @param content     搜索内容
     * @param clienttype  客户端
     * @param status      状态
     * @param page        第几页
     * @param pagesize    每页多少条数据
     * @return
     */
    public Map<String, Object> doGetDemandListMethod(Integer region, Integer province, Integer harbour, String content, int clienttype, String startDate, String endDate, String status, int page, int pagesize) {
        Map<String, Object> map = new HashMap<>();
        map.put("deliveryRegion", region);
        map.put("deliveryProvince", province);
        map.put("deliveryHarbour", harbour);
        map.put("clienttype", clienttype);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        map.put("regionList", areaportMapper.getAllArea());
        map.put("content", content);
        map.put("provinceList", tools.getMallProvinces(region));
        map.put("harbourList", tools.getMallPorts(province));
        map.put("clientTypeList", dataBookMapper.getDataBookListByType("clienttype"));
        map.put("demand", demandMapper.pageAllDemandsBySelect(region, province, harbour, Where.$like$(content), clienttype, startDate, endDate, status, page, pagesize));
        return map;
    }

    /**
     * 导出需求信息
     * @param region
     * @param province
     * @param harbour
     * @param content
     * @param clienttype
     * @param startDate
     * @param endDate
     * @param checkstatus
     * @param response
     * @param request
     */
    @Transactional
    public void doExportDemandList(int region, int province, int harbour, String content, int clienttype, String startDate, String endDate, String checkstatus, HttpServletResponse response, HttpServletRequest request) throws IOException {
        List<Map<String, Object>> demandList = demandMapper.getExportDemandList(region, province, harbour, content, clienttype, startDate, endDate, checkstatus);
        String fileName = "需求信息";
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(fileName);
        HSSFRow row = sheet.createRow(0);
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sheet.setVerticallyCenter(true);
        sheet.setHorizontallyCenter(true);
        sheet.setColumnWidth(0, 1200);
        sheet.setColumnWidth(1, 8000);
        sheet.setColumnWidth(2, 5000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 4000);
        sheet.setColumnWidth(6, 3600);
        sheet.setColumnWidth(7, 3600);
        sheet.setColumnWidth(8, 3000);
        sheet.setColumnWidth(9, 10000);
        sheet.setColumnWidth(10, 3600);
        sheet.setColumnWidth(11, 3600);
        sheet.setColumnWidth(12, 6000);
        sheet.setColumnWidth(13, 5000);
        sheet.setColumnWidth(14, 7000);
        sheet.setColumnWidth(15, 5000);
        sheet.setColumnWidth(16, 3500);
        sheet.setColumnWidth(17, 10000);
        sheet.setColumnWidth(18, 3000);
        sheet.setColumnWidth(19, 3500);
        String[] excelHeader = {"序号", "公司名称", "需求编号", "煤炭种类", "数量（吨）", "低位热值（kcal/kg）", "收到基硫分（%）", "空干基挥发分", "全水", "其它指标", "提货地点", "提货方式", "提货时间", "付款方式", "检验机构", "发布时间", "注册手机号", "备注", "所属交易员", "已匹配吨数"};
        for (int i=0; i <excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(style);
        }
        for (int i = 0; i < demandList.size(); i++) {
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(String.valueOf(i + 1));
            row.createCell(1).setCellValue(String.valueOf(demandList.get(i).get("demandcustomer")));
            row.createCell(2).setCellValue(String.valueOf(demandList.get(i).get("demandcode")));
            row.createCell(3).setCellValue(String.valueOf(demandList.get(i).get("coaltype")));
            row.createCell(4).setCellValue(String.valueOf(demandList.get(i).get("demandamount")));
            row.createCell(5).setCellValue(String.valueOf(demandList.get(i).get("NCV") + "-" + demandList.get(i).get("NCV02")));
            row.createCell(6).setCellValue(String.valueOf(demandList.get(i).get("RS") + "-" + demandList.get(i).get("RS02")));
            row.createCell(7).setCellValue(String.valueOf(demandList.get(i).get("ADV") + "-" + demandList.get(i).get("ADV02")));
            row.createCell(8).setCellValue(String.valueOf(demandList.get(i).get("TM") + "-" + demandList.get(i).get("TM02")));
            row.createCell(9).setCellValue(String.valueOf((BigDecimal.valueOf(Double.valueOf(StringUtils.isEmptyOrWhitespaceOnly(String.valueOf(demandList.get(i).get("ADS")).replace("null", "")) ? "0" : String.valueOf(demandList.get(i).get("ADS")).replace("null", ""))).compareTo(BigDecimal.valueOf(0)) == 0 ? "" : "[空干基硫分(%)：" + demandList.get(i).get("ADS") + "-" + demandList.get(i).get("ADS02") + "], ") + (BigDecimal.valueOf(Double.valueOf(StringUtils.isEmptyOrWhitespaceOnly(String.valueOf(demandList.get(i).get("IM")).replace("null", "")) ? "0" : String.valueOf(demandList.get(i).get("IM")).replace("null", ""))).compareTo(BigDecimal.valueOf(0)) == 0 ? "" : "[内水(%)：" + demandList.get(i).get("IM") + "-" + demandList.get(i).get("IM02") + "], ") + (BigDecimal.valueOf(Double.valueOf(StringUtils.isEmptyOrWhitespaceOnly(String.valueOf(demandList.get(i).get("RV")).replace("null", "")) ? "null" : String.valueOf(demandList.get(i).get("RV")).replace("null", ""))).compareTo(BigDecimal.valueOf(0)) == 0 ? "" : "[收到基挥发分(%)：" + demandList.get(i).get("RV") + "-" + demandList.get(i).get("RV02") + "], ") + (BigDecimal.valueOf(Double.valueOf(StringUtils.isEmptyOrWhitespaceOnly(String.valueOf(demandList.get(i).get("ASH")).replace("null", "")) ? "0" : String.valueOf(demandList.get(i).get("ASH")).replace("null", ""))).compareTo(BigDecimal.valueOf(0)) ==0 ? "" : "[收到基灰分(%)：" + demandList.get(i).get("ASH") + "-" + demandList.get(i).get("ASH02") + "], ") + (Integer.valueOf(String.valueOf(demandList.get(i).get("GV"))).equals(0) ? "" : "[G值：" + demandList.get(i).get("GV") + "-" + demandList.get(i).get("GV02") + "], ") + (Integer.valueOf(String.valueOf(demandList.get(i).get("YV"))).equals(0) ? "" : "[Y值(mm)：" + demandList.get(i).get("YV") + "-" + demandList.get(i).get("YV02") + "], ") + (Integer.valueOf(String.valueOf(demandList.get(i).get("FC"))).equals(0) ? "" : "[固定碳(%)：" + demandList.get(i).get("FC") + "-" + demandList.get(i).get("FC02") + "], ") + (Integer.valueOf(String.valueOf(demandList.get(i).get("AFT"))).equals(0) ? "" : "[灰熔点(℃)：" + demandList.get(i).get("AFT") + "], ") + (Integer.valueOf(String.valueOf(demandList.get(i).get("HGI"))).equals(0) ? "" : "[哈氏可磨：" + demandList.get(i).get("HGI") + "], ") + (StringUtils.isNullOrEmpty(String.valueOf(demandList.get(i).get("PSName")).replace("null", "")) ? "" : "[颗粒度：" + demandList.get(i).get("PSName") + "], ")));
            row.createCell(10).setCellValue(String.valueOf(demandList.get(i).get("deliveryprovince")) + (String.valueOf(demandList.get(i).get("deliveryplace")).equals("其它") ? String.valueOf(demandList.get(i).get("otherharbour")) : String.valueOf(demandList.get(i).get("deliveryplace"))));
            row.createCell(11).setCellValue(String.valueOf(demandList.get(i).get("deliverymode")));
            row.createCell(12).setCellValue(String.valueOf(StringUtils.isNullOrEmpty(String.valueOf(demandList.get(i).get("deliverydate"))) ? String.valueOf(demandList.get(i).get("deliverydatestart")) + "-" + String.valueOf(demandList.get(i).get("deliverydateend")) : String.valueOf(demandList.get(i).get("deliverydate"))));
            row.createCell(13).setCellValue(String.valueOf(demandList.get(i).get("paymode")));
            row.createCell(14).setCellValue(String.valueOf(demandList.get(i).get("inspectionagency")).equals("其它") ? String.valueOf(demandList.get(i).get("otherorg")) : String.valueOf(demandList.get(i).get("inspectionagency")));
            row.createCell(15).setCellValue(String.valueOf(demandList.get(i).get("releasetime")));
            row.createCell(16).setCellValue(String.valueOf(demandList.get(i).get("securephone")));
            row.createCell(17).setCellValue(String.valueOf(demandList.get(i).get("releasecomment")).replace("null", ""));
            row.createCell(18).setCellValue(String.valueOf(demandList.get(i).get("tradername")));
            row.createCell(19).setCellValue(String.valueOf(demandList.get(i).get("purchasednum")));
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        fileName += LocalDate.now() + ".xls";
        if(request.getHeader("user-agent").toLowerCase().contains("firefox")) {
            fileName =  new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        } else {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        }
        response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
    }

    /**
     * 查看导出需求条数
     * @param region
     * @param province
     * @param harbour
     * @param content
     * @param clienttype
     * @param startDate
     * @param endDate
     * @param checkstatus
     * @return
     */
    public Object doCheckExportDemandCountMethod(Integer region, Integer province, Integer harbour, String content, int clienttype, String startDate, String endDate, String checkstatus) {
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        String error = "";
        int count = demandMapper.countAllDemandsBySelect(region, province, harbour, Where.$like$(content), clienttype, startDate, endDate, checkstatus);
        if(count == 0)  error = "当前数据为0条，请先改变筛选条件再导出！";
        else if(count > 2000) error = "当前数据太多，超过了2000条，请先改变筛选条件再导出！";
        else success = true;
        map.put("success", success);
        map.put("error", error);
        return map;
    }

    @Transactional
    public boolean doVerifyDemandMethod(User user, String demandcode, String checkstatus, Integer dealerId, String comment) {
        if(checkstatus.equals("0")) {
            checkstatus = EnumCommonString.VerifyPass.value();
            //审核通过后修改交易状态,开始报价
            Admin dealer = dealerMapper.findDealerById(dealerId);
            if (dealer == null) throw new NotFoundException();
            if (user.getTraderid() == null) {
                if(!userMapper.doAddUpdateUserTraderMethod(user.getId(), dealerId)) {
                    throw new BusinessException("服务器出错，请联系技术人员！");
                }
            }
            if (demandMapper.modifyTradestatusByDemandcode(dealer.getName(), dealer.getPhone(), "开始报价", demandcode, dealerId) == 1 &&
                    //修改我的需求表状态为匹配中
                    mydemandMapper.modifyStatusByDemandcode("报价中", demandcode) == 1 &&
                    demandMapper.modifyCheckstatusAndComment(checkstatus, comment, demandcode) == 1) {
                final String content = "您好,您的需求" + demandcode + "审核已通过!";
                MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
                return true;
            } else{
                throw new BusinessException("服务器出错，请联系技术人员！");
            }
        } else{
            checkstatus = EnumCommonString.VerifyNotPass.value();
            if(mydemandMapper.modifyStatusByDemandcode("审核未通过", demandcode) == 1 &&
                    demandMapper.modifyCheckstatusAndComment(checkstatus, comment, demandcode) == 1){
                final String content = "您好,您的需求demandId审核未通过，审核反馈为:" + comment + ",如需帮助，请拨打客服热线400-960-1180。";
                MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
                return true;
            } else{
                throw new BusinessException("服务器出错，请联系技术人员！");
            }
        }

    }
}
