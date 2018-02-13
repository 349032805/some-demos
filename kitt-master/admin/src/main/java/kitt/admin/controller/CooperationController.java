package kitt.admin.controller;

import com.mysql.jdbc.StringUtils;
import kitt.admin.basic.JsonController;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.service.CooperationService;
import kitt.admin.service.Session;
import kitt.core.domain.*;
import kitt.core.persistence.CooperationMapper;
import kitt.core.persistence.DataBookMapper;
import kitt.core.persistence.StatusBookMapper;
import kitt.ext.mybatis.Where;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/9/29.
 */
@RestController
@RequestMapping("/user/cooperation")
public class CooperationController extends JsonController {
    @Autowired
    private CooperationMapper cooperationMapper;
    @Autowired
    private StatusBookMapper statusBookMapper;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private Session session;

    /**
     * 我要合作list
     * @param status                            状态
     * @param content                           搜索框内容
     * @param page                              页数
     * @param kind                              企业大类
     * @param type                              企业细分
     * @return
     */
    @RequestMapping("/list")
    public Object getCooperationList(@RequestParam(value = "status", required = false, defaultValue = "1")int status,
                                     @RequestParam(value = "content", required = false, defaultValue = "")String content,
                                     @RequestParam(value = "page", required = false, defaultValue = "1")int page,
                                     @RequestParam(value = "kind", required = false, defaultValue = "0")int kind,
                                     @RequestParam(value = "type", required = false, defaultValue = "0")int type){
        Map<String, Object> map = new HashMap<>();
        map.put("statusList", statusBookMapper.getStatusBookListByType("cooperationstatus"));
        map.put("status", status);
        map.put("content", content);
        map.put("kind", kind);
        map.put("type", type);
        map.put("typeList", getTypeListByKind(kind));
        map.put("CooperationList", cooperationMapper.pageCooperation(status, Where.$like$(content), kind, type, page, 10));
        int totalCount = cooperationMapper.getCooperationCount(status, Where.$like$(content), 0, 0);
        int supplierCount = cooperationMapper.getCooperationCount(status, Where.$like$(content), 1, 0);
        int downStreamCount = cooperationMapper.getCooperationCount(status, Where.$like$(content), 2, 0);
        int financeCount = cooperationMapper.getCooperationCount(status, Where.$like$(content), 3, 0);
        int storageCount = cooperationMapper.getCooperationCount(status, Where.$like$(content), 4, 0);
        map.put("totalCount", totalCount);
        map.put("supplierCount", supplierCount);
        map.put("supplierCountPercent", String.format("%.2f", supplierCount / (totalCount * 1.00) * 100) + "%");
        map.put("downStreamCount", downStreamCount);
        map.put("downStreamCountPercent", String.format("%.2f", downStreamCount / (totalCount * 1.00) * 100) + "%");
        map.put("financeCount", financeCount);
        map.put("financeCountPercent", String.format("%.2f", financeCount / (totalCount * 1.00) * 100) + "%");
        map.put("storageCount", storageCount);
        map.put("storageCountPercent", String.format("%.2f", storageCount / (totalCount * 1.00) * 100) + "%");
        return map;
    }

    /**
     * 根据企业大类获取企业类型细分list
     * @param kind                      企业大类
     * @return
     */
    public List<DataBook> getTypeListByKind(int kind){
        List<DataBook> dataBookList = new ArrayList<>();
        switch (kind){
            case 1:
                dataBookList.add(new DataBook(1, "煤炭企业"));
                dataBookList.add(new DataBook(2, "贸易商"));
                break;
            case 2:
                dataBookList.add(new DataBook(3, "电厂"));
                dataBookList.add(new DataBook(4, "钢厂"));
                dataBookList.add(new DataBook(5, "其它下游企业"));
                break;
            case 3:
                dataBookList.add(new DataBook(6, "银行"));
                dataBookList.add(new DataBook(7, "保险"));
                dataBookList.add(new DataBook(8, "其它金融企业"));
                break;
            case 4:
                dataBookList.add(new DataBook(9, "仓储"));
                dataBookList.add(new DataBook(10, "港口"));
                dataBookList.add(new DataBook(11, "物流企业"));
                break;
            default:
                dataBookList = dataBookMapper.getDataBookListByType("cooperationtype");
                break;
        }
        return dataBookList;
    }

    /**
     * 检查导出数据条数
     * @param status                          状态
     * @param content                         搜索框里的内容
     * @param kind                            企业大类
     * @param type                            企业细分
     */
    @RequestMapping("/checkExportCount")
    public Object doCheckExportCooperationCount(@RequestParam(value = "status", required = false, defaultValue = "1")int status,
                                                @RequestParam(value = "content", required = false, defaultValue = "")String content,
                                                @RequestParam(value = "kind", required = false, defaultValue = "0")int kind,
                                                @RequestParam(value = "type", required = false, defaultValue = "0")int type){
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        String error = "";
        int count = cooperationMapper.getCooperationCount(status, Where.$like$(content), kind, type);
        if(count == 0)  error = "当前数据为0条，请先改变筛选条件再导出！";
        else if(count > 2000) error = "当前数据太多，超过了2000条，请先改变筛选条件再导出！";
        else success = true;
        map.put("success", success);
        map.put("error", error);
        return map;
    }

    /**
     * 导出我要合作列表
     * @param status                           状态
     * @param content                          搜索框里的内容
     * @param kind                             企业大类
     * @param type                             企业细分
     */
    @RequestMapping("/exportExcel")
    public void doExportCooperationToExcel(@RequestParam(value = "status", required = false, defaultValue = "1")int status,
                                           @RequestParam(value = "content", required = false, defaultValue = "")String content,
                                           @RequestParam(value = "kind", required = false, defaultValue = "0")int kind,
                                           @RequestParam(value = "type", required = false, defaultValue = "0")int type,
                                           HttpServletResponse response,
                                           HttpServletRequest request) throws IOException {

        HSSFWorkbook wb = new HSSFWorkbook();
        String filename = "我要合作列表";
        HSSFSheet sheet = wb.createSheet(filename);
        HSSFRow row = sheet.createRow(0);
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sheet.setVerticallyCenter(true);
        sheet.setHorizontallyCenter(true);
        sheet.setColumnWidth(0, 1200);
        sheet.setColumnWidth(1, 3000);
        sheet.setColumnWidth(2, 10000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 3000);
        sheet.setColumnWidth(5, 4500);
        sheet.setColumnWidth(6, 4500);
        sheet.setColumnWidth(7, 8000);
        sheet.setColumnWidth(8, 2500);
        sheet.setColumnWidth(9, 2000);
        sheet.setColumnWidth(10, 20000);
        String[] excelHeader = {"序号", "手机号", "公司名称", "企业大类", "类型细分", "创建时间", "沟通时间", "客户需求", "沟通结果", "是否登录", "沟通备注信息"};
        for (int i=0; i <excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(style);
        }
        List<Cooperation> cooperationList = cooperationMapper.getCooperationList(status, Where.$like$(content), kind, type, 2000, 0);
        for (int i = 0; i < cooperationList.size(); i++) {
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(String.valueOf(i + 1));
            row.createCell(1).setCellValue(String.valueOf(cooperationList.get(i).getLinkmanphone()));
            row.createCell(2).setCellValue(String.valueOf(cooperationList.get(i).getCompanyname()));
            row.createCell(3).setCellValue(String.valueOf(cooperationList.get(i).getKindname()));
            row.createCell(4).setCellValue(String.valueOf(cooperationList.get(i).getTypename()));
            row.createCell(5).setCellValue(String.valueOf(cooperationList.get(i).getCreatetime()));
            row.createCell(6).setCellValue(String.valueOf(cooperationList.get(i).getSolvedtime()));
            row.createCell(7).setCellValue(String.valueOf(cooperationList.get(i).getRemarks()));
            row.createCell(8).setCellValue(String.valueOf(cooperationList.get(i).getStatusname()));
            row.createCell(9).setCellValue(String.valueOf("否"));
            //row.createCell(9).setCellValue(String.valueOf(cooperationList.get(i).isLogin() == true ? "是" : "否"));
            row.createCell(10).setCellValue(String.valueOf(cooperationList.get(i).getSolvedremarks()));
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        filename += LocalDate.now() + ".xls";
        if(request.getHeader("user-agent").toLowerCase().contains("firefox")) {
            filename =  new String(filename.getBytes("UTF-8"), "ISO-8859-1");
        } else {
            filename = URLEncoder.encode(filename, "UTF-8");
        }
        response.addHeader("Content-Disposition", "attachment;filename="+ filename);
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
    }


    /**
     * 根据id获取Cooperation对象
     * @param id                            id
     */
    @RequestMapping("/getById")
    public Object doGetCooperationById(@RequestParam(value = "id", required = false, defaultValue = "0")int id){
        return new Object() {
            public Cooperation cooperation = cooperationMapper.getCooperationById(id);
            public List<DataBook> kindList = dataBookMapper.getDataBookListByType("cooperationkind");
            public List<DataBook> typeList = getTypeListByKind(cooperation.getKind());
            public List<StatusBook> statusList = statusBookMapper.getStatusBookListByType("cooperationstatus");
        };
    }

    /**
     * 根据企业大类获取企业细分列表
     * @param kind                    企业大类
     * @return
     */
    @RequestMapping("/getTypeList")
    public List<DataBook> doGetTypeListByKind(@RequestParam(value = "kind", required = true)int kind){
        return getTypeListByKind(kind);
    }

    @RequestMapping("/update")
    public boolean doAddCooperation(Cooperation cooperation){
        if(StringUtils.isNullOrEmpty(cooperation.getLinkmanphone())) throw new BusinessException("联系电话不能为空！");
        cooperation.setKindname(dataBookMapper.getDataBookNameByTypeSequence("cooperationkind", cooperation.getKind()));
        cooperation.setTypename(dataBookMapper.getDataBookNameByTypeSequence("cooperationtype", cooperation.getType()));
        cooperation.setStatusname(statusBookMapper.getStatusBookNameByTypeSequence("cooperationstatus", cooperation.getStatus()));
        cooperation.setSolvedmanid(session.getAdmin().getId());
        cooperation.setSolvedmanusername(session.getAdmin().getUsername());
        return cooperationService.doUpdateCooperationMethod(cooperation);
    }


}
