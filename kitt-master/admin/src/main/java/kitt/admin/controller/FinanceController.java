package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.FinanceService;
import kitt.core.domain.AuthenticationRole;
import kitt.core.domain.Finance;
import kitt.core.persistence.FinanceMapper;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by fanjun on 15-1-24.
 */
@Controller
public class FinanceController {

    @Autowired
    private FinanceMapper financeMapper;
    @Autowired
    private FinanceService financeService;

    //金融客户列表
    @RequestMapping(value="/finance/list", method= RequestMethod.POST)
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Admin)
    public Object list(@RequestParam(value = "page", required = false, defaultValue = "1")int page,
                       @RequestParam(value = "status", required = false, defaultValue = "ToBeSolved")String status) {
        Map map = new HashMap();
        map.put("finance", financeMapper.pageAllFinanceCustomer(status, page, 10));
        return map;
    }

    //导出Excel文件
    @RequestMapping("/finance/exportExcel")
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Admin)
    public void exportExcel(String scope, HttpServletResponse response, HttpServletRequest request) throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("金融客户");
        HSSFRow row = sheet.createRow((int) 0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        String[] excelHeader = {"序号", "类型", "公司名称", "公司地址", "业务区域","融资金额", "联系人", "联系电话", "创建时间"};
        for (int i = 0; i < excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(i);
        }
        List<Finance> customersList = null;
        if(scope.equals("today")){
            customersList = financeMapper.getTodayFinaceCustomers();
        }
        if(scope.equals("all")){
            customersList = financeMapper.getAllFinanceCustomers();
        }
        for (int i = 0; i < customersList.size(); i++) {
            row = sheet.createRow(i + 1);
            Finance customer = customersList.get(i);
            row.createCell(0).setCellValue(customer.getId());
            row.createCell(1).setCellValue(customer.getType());
            row.createCell(2).setCellValue(customer.getCompanyname());
            row.createCell(3).setCellValue(customer.getAddress());
            row.createCell(4).setCellValue(customer.getBusinessarea());
            row.createCell(5).setCellValue(String.valueOf(customer.getAmountnum()));
            row.createCell(6).setCellValue(customer.getContact());
            row.createCell(7).setCellValue(customer.getPhone());
            row.createCell(8).setCellValue(customer.getCreatetime().toString());
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        String filename = "金融客户" + LocalDate.now() + ".xls";
        if(request.getHeader("user-agent").toLowerCase().contains("firefox")) {
            filename =  new String(filename.getBytes("UTF-8"), "ISO-8859-1");
        } else {
            filename = URLEncoder.encode(filename, "UTF-8");
        }
        response.addHeader("Content-Disposition", "attachment; filename="+ filename);
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
    }

    @RequestMapping("/finance/detail")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Admin)
    public Finance getFinanceById(@RequestParam(value = "id", required = true)int id){
        if(financeMapper.getFinanceById(id) == null){
            throw new NotFoundException();
        } else {
            return financeMapper.getFinanceById(id);
        }
    }

    @RequestMapping("/finance/solved")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doSolveFinance(@RequestParam(value = "id", required = true)int id,
                                  @RequestParam(value = "solvedremarks", required = true)String solvedremarks){
        Finance finance = financeMapper.getFinanceById(id);
        if(finance == null) throw new NotFoundException();
        return financeService.doSolveFinanceOrderMethod(id, solvedremarks);
    }

    @RequestMapping("/finance/delete")
    @ResponseBody
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Finance)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeleteFinancdOrder(@RequestParam(value = "id", required = true)int id){
        Finance finance = financeMapper.getFinanceById(id);
        if(finance == null) throw new NotFoundException();
        return financeService.doDeleteFinanceOrderMethod(id);
    }


}




