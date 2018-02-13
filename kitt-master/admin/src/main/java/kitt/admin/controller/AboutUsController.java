package kitt.admin.controller;

import kitt.admin.service.AboutUsService;
import kitt.admin.service.Session;
import kitt.core.domain.AboutUs;
import kitt.core.persistence.AboutUsMapper;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanjun on 15-4-26.
 */
@Controller
public class AboutUsController {
    @Autowired
    private AboutUsMapper aboutUsMapper;
    @Autowired
    private Session session;
    @Autowired
    private AboutUsService aboutUsService;

    @RequestMapping("/aboutUs/list")
    @ResponseBody
    public Object list(String type,
                       @RequestParam(value="status", required = false) String status,
                       int page) {
        Map map = new HashMap();
        map.put("aboutUs", aboutUsMapper.pageAllInfoByType(type, status,page, 10));
        map.put("type",type);
        return map;
    }

    //保存或更新信息
    @RequestMapping("/aboutUs/saveOrUpdateInfo")
    @ResponseBody
    public boolean saveOrUpdateInfo(AboutUs aboutUs) {
        aboutUs.setUpdatetime(LocalDateTime.now());
        aboutUs.setUpdateuser(session.getAdmin().getUsername());
        if(aboutUs.getId() == 0) {
            return aboutUsService.doAddAboutUsMethod(aboutUs);
        } else{
            return aboutUsService.doUpdateAboutUsMethod(aboutUs);
        }
    }

    //获取单个编辑信息
    @RequestMapping("/aboutUs/editAboutUs")
    @ResponseBody
    public Object editAboutUs(int id) {
        AboutUs aboutUs = aboutUsMapper.getAboutUsById(id);
        Map map = new HashMap();
        map.put("aboutUs",aboutUs);
        return map;
    }

    //删除单个记录(更改删除状态)
    @RequestMapping("/aboutUs/deleteAboutUs")
    @ResponseBody
    public Object deleteAboutUs(int id) {
        aboutUsMapper.modifyIsdelete(id);
        Map map = new HashMap();
        boolean success = true;
        map.put("success",success);
        return map;
    }

    //更改建议反馈沟通状态
    @RequestMapping("/aboutUs/modifyStatus")
    @ResponseBody
    public Object modifyStatus(int id) {
        aboutUsMapper.modifyStatus(id);
        Map map = new HashMap();
        boolean success = true;
        map.put("success",success);
        return map;
    }

    //建议反馈导出
    @RequestMapping("/aboutUs/exportExcel")
    public void exportExcel(String status,HttpServletResponse response) throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("建议反馈");
        HSSFRow row = sheet.createRow((int) 0);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        String[] excelHeader = {"序号", "联系方式", "状态", "创建时间", "内容"};
        for (int i = 0; i < excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(style);
            sheet.autoSizeColumn(i);
        }
        if(status.equals("null") || status.equals("")){
            status = null;
        }
        List<AboutUs> adviceList = null;
        if(status == null){
            adviceList = aboutUsMapper.getAboutUsByType("advice");
        } else{
            adviceList = aboutUsMapper.getAdviceByStatus(status);
        }

        for (int i = 0; i < adviceList.size(); i++) {
            row = sheet.createRow(i + 1);
            AboutUs advice = adviceList.get(i);
            String aboutUsStatus = advice.getStatus();
            if(aboutUsStatus.equals("noCommunicate")){
                advice.setStatus("未沟通");
            } else{
                advice.setStatus("已沟通");
            }
            row.createCell(0).setCellValue(advice.getId());
            row.createCell(1).setCellValue(advice.getContact());
            row.createCell(2).setCellValue(advice.getStatus());
            row.createCell(3).setCellValue(advice.getUpdatetime().toString());
            row.createCell(4).setCellValue(advice.getContent());
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        String filename = "建议反馈"+ LocalDate.now();
        filename = URLEncoder.encode(filename, "UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename="+ filename+".xls");
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
    }

    //最新动态,网站公告更改顺序
    @RequestMapping("/aboutUs/modifySequence")
    @ResponseBody
    public Object modifySequence(int id,int sequence) {
        aboutUsMapper.modifySequence(id,sequence);
        Map map = new HashMap();
        boolean success = true;
        map.put("success",success);
        return map;
    }
}
