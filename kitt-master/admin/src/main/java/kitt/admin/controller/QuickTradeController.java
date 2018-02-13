package kitt.admin.controller;

import com.itextpdf.text.DocumentException;
import kitt.admin.annotation.Authority;
import kitt.admin.service.Session;
import kitt.core.domain.AuthenticationRole;
import kitt.core.domain.QuickTrade;
import kitt.core.persistence.DataBookMapper;
import kitt.core.persistence.QuickTradeMapper;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 15/9/24.
 */
@RestController
public class QuickTradeController {

  @Autowired
  private QuickTradeMapper quickTradeMapper;
  @Autowired
  private DataBookMapper dataBookMapper;
  @Autowired
  private Session session;

  @RequestMapping(value = "/loadAllTradeList", method = RequestMethod.GET)
  public Object findTradeList(@RequestParam(value = "page", required = false, defaultValue = "1") int page,
                              @RequestParam Map<String, Object> params) {
    Map<String, Object> maps = new HashMap<>();
    maps.put("clientTypeList", dataBookMapper.getDataBookListByType("clienttype"));
    maps.put("tradeList", quickTradeMapper.findAllPage(params, page, 10));
    maps.put("clientType", params.get("clientType"));
    maps.put("status", params.get("status"));
    return maps;
  }

  @RequestMapping(value = "/count/quickTrade",method = RequestMethod.POST)
  public Integer countquickTrade(@RequestParam Map<String, Object> map){
    return quickTradeMapper.countAll(map);
  }

  @RequestMapping(value = "/quicktrade/{id}",method = RequestMethod.GET)
  public QuickTrade loadById(@PathVariable("id")int id){
        return quickTradeMapper.findById(id);
  }


  @RequestMapping(value = "/demand/handleQuickTrade",method = RequestMethod.POST)
  public Object handleDemand(@RequestParam("id")int id,
                             @RequestParam(value = "solvedremarks",required = false)String releaseMarks){
    QuickTrade rapidTrade =new QuickTrade();
    rapidTrade.setSolvedtime(LocalDateTime.now());
    rapidTrade.setSolvedmanusername(session.getAdmin().getName());
    rapidTrade.setLastupdatetime(LocalDateTime.now());
    rapidTrade.setId(id);
    rapidTrade.setSolvedremarks(releaseMarks);
    rapidTrade.setSolvedmanid(session.getAdmin().getId());
    return quickTradeMapper.update(rapidTrade);

  }


  //下载快速找货工excel
  @RequestMapping(value = "/demand/download_quickTrade", method = RequestMethod.GET)
  @Authority(role = AuthenticationRole.TraderAssistant)
  @Authority(role = AuthenticationRole.Operation)
  @Authority(role = AuthenticationRole.Admin)
  public void downloadQuickTrade(@RequestParam Map<String, Object> map,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws IOException, DocumentException {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    //导出
    List<QuickTrade> rapidTrades = quickTradeMapper.exportData(map);
    String filename = Integer.valueOf(String.valueOf(map.get("status"))) == 1 ? "快速找货-待处理" : "快速找货-已处理";
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet = wb.createSheet(filename);
    HSSFRow row = sheet.createRow(0);
    CellStyle cellStyle = wb.createCellStyle();
    cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
    cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
    sheet.setVerticallyCenter(true);
    sheet.setHorizontallyCenter(true);
    String[] excelHeader = {"序号", "客户联系电话", "创建时间", "状态", "处理时间", "处理人", "反馈备注"};

    for (int i = 0; i < excelHeader.length; i++) {
      HSSFCell cell = row.createCell(i);
      cell.setCellValue(excelHeader[i]);
      cell.setCellStyle(cellStyle);
      sheet.autoSizeColumn(i, true);
    }
    for (int i = 0; i < rapidTrades.size(); i++) {
      QuickTrade rapidTrade = rapidTrades.get(i);
      sheet.autoSizeColumn(i, true);
      row = sheet.createRow(i + 1);
      row.setRowStyle(cellStyle);
      row.createCell(0).setCellValue(i + 1);
      row.createCell(1).setCellValue(rapidTrade.getUserphone());
      row.createCell(2).setCellValue(rapidTrade.getCreatetime().format(formatter));
      row.createCell(3).setCellValue(rapidTrade.getStatus() == 1 ? "待处理" : "已处理");
      row.createCell(4).setCellValue(rapidTrade.getSolvedtime() != null ? rapidTrade.getSolvedtime().format(formatter) : "");
      row.createCell(5).setCellValue(rapidTrade.getSolvedmanusername());
      row.createCell(6).setCellValue(rapidTrade.getSolvedremarks());
    }
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/x-download");
    filename += LocalDate.now() + ".xls";
    if (request.getHeader("user-agent").toLowerCase().contains("firefox")) {
      filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");
    } else {
      filename = URLEncoder.encode(filename, "UTF-8");
    }
    response.addHeader("Content-Disposition", "attachment; filename=" + filename);
    OutputStream out = response.getOutputStream();
    wb.write(out);
    out.close();
  }


}
