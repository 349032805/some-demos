package kitt.admin.service;

import com.mysql.jdbc.StringUtils;
import kitt.core.domain.EnumSellInfo;
import kitt.core.persistence.AreaportMapper;
import kitt.core.persistence.BuyMapper;
import kitt.core.persistence.DataBookMapper;
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
 * Created by liuxinjie on 15/11/14.
 */
@Service
public class BuyMethod {
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private Tools tools;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private BuyMapper buyMapper;


    /**---------------------------------------第一部分 供应信息------------------------------------**/
    /**---------------------------------------第一部分 供应信息------------------------------------**/
    /**---------------------------------------第一部分 供应信息------------------------------------**/
    /**
     * 获取商城商品列表
     * @param region       区域id
     * @param province     省份id
     * @param harbour      港口id
     * @param productNo    产品编号
     * @param clienttype   客户端类型
     * @param status1      sellinfo 状态1
     * @param status2      sellinfo 状态2
     * @param sellertype   卖家类型， 0  代表自营（商城）， 非0 是其他卖家
     * @param page         第几页
     * @param pagesize     每页几条数据
     */
    public Object doGetSellInfoProductList(int region, int province, int harbour, String productNo, int clienttype, String startDate, String endDate, EnumSellInfo status1, EnumSellInfo status2, int sellertype, int page, int pagesize) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("deliveryRegion", region);
        map.put("deliveryProvince", province);
        map.put("deliveryHarbour", harbour);
        map.put("regionList", areaportMapper.getAllArea());
        map.put("provinceList", tools.getMallProvinces(region));
        map.put("harbourList", tools.getMallPorts(province));
        map.put("clientTypeList", dataBookMapper.getDataBookListByType("clienttype"));
        map.put("clienttype", clienttype);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        if(!StringUtils.isNullOrEmpty(productNo)) {
            map.put("productNo", productNo);
        }
        map.put("supplyList", buyMapper.getSellInfoBySelect(region, province, harbour, productNo, clienttype, startDate, endDate, status1, status2, sellertype, page, pagesize));
        return map;
    }

    /**
     * 导出供应信息list
     * @param region
     * @param province
     * @param harbour
     * @param productNo
     * @param clienttype
     * @param startDate
     * @param endDate
     * @param status1
     * @param status2
     * @param type
     */
    @Transactional
    public void doExportSellInfoList(int region, int province, int harbour, String productNo, int clienttype, String startDate, String endDate, EnumSellInfo status1, EnumSellInfo status2, int type,
                                     HttpServletResponse response, HttpServletRequest request) throws IOException {
        List<Map<String, Object>> sellInfoList = buyMapper.getExportSellInfoList(region, province, harbour, productNo, clienttype, startDate, endDate, status1, status2, type);
        String fileName = "";
        switch (type){
            case 0:
                fileName = "商城产品";
                break;
            case 1:
                fileName = "供应信息";
                break;
            default:
                fileName = "供应信息";
                break;
        }
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
        sheet.setColumnWidth(4, 5000);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 4000);
        sheet.setColumnWidth(8, 3600);
        sheet.setColumnWidth(9, 3600);
        sheet.setColumnWidth(10, 3000);
        sheet.setColumnWidth(11, 10000);
        sheet.setColumnWidth(12, 5000);
        sheet.setColumnWidth(13, 3000);
        sheet.setColumnWidth(14, 6000);
        sheet.setColumnWidth(15, 5000);
        sheet.setColumnWidth(16, 7000);
        sheet.setColumnWidth(17, 5000);
        sheet.setColumnWidth(18, 5000);
        sheet.setColumnWidth(19, 10000);
        sheet.setColumnWidth(20, 3500);
        sheet.setColumnWidth(21, 3500);
        String[] excelHeader = {"序号", "公司名称", "产品编号", "煤炭种类", "产地", "数量（吨）", "价格（元）", "低位热值（kcal/kg）", "收到基硫分（%）", "空干基挥发分", "全水", "其它指标", "提货地点", "提货方式", "提货时间", "付款方式", "检验机构", "发布时间", "注册手机号", "备注", "所属交易员", "已售吨数"};
        for (int i=0; i <excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(style);
        }
        for (int i = 0; i < sellInfoList.size(); i++) {
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(String.valueOf(i + 1));
            row.createCell(1).setCellValue(String.valueOf(sellInfoList.get(i).get("seller")));
            row.createCell(2).setCellValue(String.valueOf(sellInfoList.get(i).get("pid")));
            row.createCell(3).setCellValue(String.valueOf(sellInfoList.get(i).get("pname")));
            row.createCell(4).setCellValue(String.valueOf(sellInfoList.get(i).get("originplace")));
            row.createCell(5).setCellValue(String.valueOf(sellInfoList.get(i).get("supplyquantity")));
            row.createCell(6).setCellValue(String.valueOf(BigDecimal.valueOf(Double.valueOf(String.valueOf(sellInfoList.get(i).get("ykj")).replace("null", ""))).compareTo(BigDecimal.valueOf(0)) == 0 ? sellInfoList.get(i).get("jtjlast") : sellInfoList.get(i).get("ykj")));
            row.createCell(7).setCellValue(String.valueOf(sellInfoList.get(i).get("NCV") + "-" + sellInfoList.get(i).get("NCV02")));
            row.createCell(8).setCellValue(String.valueOf(sellInfoList.get(i).get("RS") + "-" + sellInfoList.get(i).get("RS02")));
            row.createCell(9).setCellValue(String.valueOf(sellInfoList.get(i).get("ADV") + "-" + sellInfoList.get(i).get("ADV02")));
            row.createCell(10).setCellValue(String.valueOf(sellInfoList.get(i).get("TM") + "-" + sellInfoList.get(i).get("TM02")));
            row.createCell(11).setCellValue(String.valueOf((BigDecimal.valueOf(Double.valueOf(StringUtils.isEmptyOrWhitespaceOnly(String.valueOf(sellInfoList.get(i).get("ADS")).replace("null", "")) ? "0" : String.valueOf(sellInfoList.get(i).get("ADS")).replace("null", "")))).compareTo(BigDecimal.valueOf(0)) == 0 ? "" : "[空干基硫分(%)：" + sellInfoList.get(i).get("ADS") + "-" + sellInfoList.get(i).get("ADS02") + "], ") + (BigDecimal.valueOf(Double.valueOf(StringUtils.isEmptyOrWhitespaceOnly(String.valueOf(sellInfoList.get(i).get("IM")).replace("null", "")) ? "0" : String.valueOf(sellInfoList.get(i).get("IM")).replace("null", ""))).compareTo(BigDecimal.valueOf(0)) == 0 ? "" : "[内水(%)：" + sellInfoList.get(i).get("IM") + "-" + sellInfoList.get(i).get("IM02") + "], ") + (BigDecimal.valueOf(Double.valueOf(StringUtils.isEmptyOrWhitespaceOnly(String.valueOf(sellInfoList.get(i).get("RV")).replace("null", "")) ? "0" : String.valueOf(sellInfoList.get(i).get("RV")))).compareTo(BigDecimal.valueOf(0)) == 0 ? "" : "[收到基挥发分(%)：" + sellInfoList.get(i).get("RV") + "-" + sellInfoList.get(i).get("RV02") + "], ") + (BigDecimal.valueOf(Double.valueOf(StringUtils.isEmptyOrWhitespaceOnly(String.valueOf(sellInfoList.get(i).get("ASH")).replace("null", "")) ? "0" : String.valueOf(sellInfoList.get(i).get("ASH")).replace("null", ""))).compareTo(BigDecimal.valueOf(0)) ==0 ? "" : "[收到基灰分(%)：" + sellInfoList.get(i).get("ASH") + "-" + sellInfoList.get(i).get("ASH02") + "], ") + (Integer.valueOf(String.valueOf(sellInfoList.get(i).get("GV"))).equals(0) ? "" : "[G值：" + sellInfoList.get(i).get("GV") + "-" + sellInfoList.get(i).get("GV02") + "], ") + (Integer.valueOf(String.valueOf(sellInfoList.get(i).get("YV"))).equals(0) ? "" : "[Y值(mm)：" + sellInfoList.get(i).get("YV") + "-" + sellInfoList.get(i).get("YV02") + "], ") + (Integer.valueOf(String.valueOf(sellInfoList.get(i).get("FC"))).equals(0) ? "" : "[固定碳(%)：" + sellInfoList.get(i).get("FC") + "-" + sellInfoList.get(i).get("FC02") + "], ") + (Integer.valueOf(String.valueOf(sellInfoList.get(i).get("AFT"))).equals(0) ? "" : "[灰熔点(℃)：" + sellInfoList.get(i).get("AFT") + "], ") + (Integer.valueOf(String.valueOf(sellInfoList.get(i).get("HGI"))).equals(0) ? "" : "[哈氏可磨：" + sellInfoList.get(i).get("HGI") + "], ") + (StringUtils.isNullOrEmpty(String.valueOf(sellInfoList.get(i).get("PSName")).replace("null", "")) == true ? "" : "[颗粒度：" + sellInfoList.get(i).get("PSName") + "], "));
            row.createCell(12).setCellValue(String.valueOf(sellInfoList.get(i).get("deliveryprovince")) + (String.valueOf(sellInfoList.get(i).get("deliveryplace")).equals("其它") ? String.valueOf(sellInfoList.get(i).get("otherharbour")) : String.valueOf(sellInfoList.get(i).get("deliveryplace"))));
            row.createCell(13).setCellValue(String.valueOf(sellInfoList.get(i).get("deliverymode")));
            row.createCell(14).setCellValue(String.valueOf(sellInfoList.get(i).get("deliverytime1")).substring(0, 10) + "-" + String.valueOf(sellInfoList.get(i).get("deliverytime2")).substring(0, 10));
            row.createCell(15).setCellValue(dataBookMapper.getDataBookNameByTypeSequence("paymode", Integer.valueOf(String.valueOf(sellInfoList.get(i).get("paymode")))) + (Integer.valueOf(String.valueOf(sellInfoList.get(i).get("paymode"))) == 2 ? "个月" : ""));
            row.createCell(16).setCellValue(String.valueOf(sellInfoList.get(i).get("inspectorg")).equals("其它") ? String.valueOf(sellInfoList.get(i).get("otherinspectorg")) : String.valueOf(sellInfoList.get(i).get("inspectorg")));
            row.createCell(17).setCellValue(String.valueOf(sellInfoList.get(i).get("createtime")));
            row.createCell(18).setCellValue(String.valueOf(sellInfoList.get(i).get("securephone")));
            row.createCell(19).setCellValue(String.valueOf(sellInfoList.get(i).get("releaseremarks")).replace("null", ""));
            row.createCell(20).setCellValue(String.valueOf(sellInfoList.get(i).get("dealername")));
            row.createCell(21).setCellValue(String.valueOf(sellInfoList.get(i).get("soldquantity")));
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
     *
     * @param region
     * @param province
     * @param harbour
     * @param productNo
     * @param clienttype
     * @param startDate
     * @param endDate
     * @param status1
     * @param status2
     * @param type
     * @return
     */
    public Object doCheckExportSellInfoCountMethod(int region, int province, int harbour, String productNo, int clienttype, String startDate, String endDate, EnumSellInfo status1, EnumSellInfo status2, int type) {
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        String error = "";
        int count = buyMapper.countSellInfoSelect(region, province, harbour, productNo, clienttype, startDate, endDate, status1, status2, type);
        if(count == 0)  error = "当前数据为0条，请先改变筛选条件再导出！";
        else if(count > 2000) error = "当前数据太多，超过了2000条，请先改变筛选条件再导出！";
        else success = true;
        map.put("success", success);
        map.put("error", error);
        return map;
    }

    /**---------------------------------------第二部分 订单------------------------------------**/
    /**---------------------------------------第二部分 订单------------------------------------**/
    /**---------------------------------------第二部分 订单------------------------------------**/

}
