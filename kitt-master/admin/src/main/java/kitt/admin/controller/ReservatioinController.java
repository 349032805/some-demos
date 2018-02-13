package kitt.admin.controller;


import com.itextpdf.text.DocumentException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Session;
import kitt.core.domain.ReservationResult;
import kitt.core.domain.result.ReservationRs;
import kitt.core.persistence.BrokerTeamMapper;
import kitt.core.persistence.DealerMapper;
import kitt.core.persistence.ReservationMapper;
import kitt.core.util.PageQueryParam;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 16/1/7.
 */
@Controller
public class ReservatioinController {
    @Autowired
    private ReservationMapper reservationMapper;
    @Autowired
    private BrokerTeamMapper brokerTeamMapper;
    @Autowired
    private DealerMapper dealerMapper;
    @Autowired
    private Session session;


    @RequestMapping(value = "/customer/reservation", method = RequestMethod.POST)
    @ResponseBody
    public Object reservation(PageQueryParam pageParam, ReservationRs reservationRs) {
        Map<String, Object> maps = new HashMap<>();
        int totalCount = reservationMapper.count(reservationRs);
        List<ReservationRs> reservationRses = reservationMapper.list(pageParam, reservationRs);
        pageParam.setTotalCount(totalCount);
        pageParam.setList(reservationRses);
        int totalPage = totalCount / pageParam.getPagesize();
        totalPage = totalCount % pageParam.getPagesize() == 0 ? totalPage : totalPage + 1;
        pageParam.setTotalCount(totalCount);
        pageParam.setTotalPage(totalPage);
        maps.put("pageParam", pageParam);
        maps.put("teamList", brokerTeamMapper.listAll());
        if (reservationRs.getBrokerGroupId() != null) {
            maps.put("dealerList", dealerMapper.findAdminByTeamId(reservationRs.getBrokerGroupId()));
        }
        maps.put("reservationRs", reservationRs);
        return maps;
    }


    @RequestMapping(value = "/customer/addReservation_record", method = RequestMethod.POST)
    @ResponseBody
    public Object addReservationAcord(ReservationResult reservationResult) {
        reservationResult.setAdminId(session.getAdmin().getId());
        reservationResult.setCreatetime(LocalDateTime.now());
        reservationMapper.addReservationRs(reservationResult);
        return true;
    }

    @RequestMapping(value = "/customer/reservation/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object loadReservationResult(@PathVariable("id") int id) {
        if (reservationMapper.load(id) == null) {
            throw new NotFoundException();
        }
        Map<String, Object> maps = new HashMap<>();
        maps.put("reservation", reservationMapper.loadReservationDetail(id));
        maps.put("reservationResult", reservationMapper.loadResultHistoryById(id));
        maps.put("teamList", brokerTeamMapper.listAll());
        return maps;
    }

    @RequestMapping(value = "/reservation/download", method = RequestMethod.GET)
    public void downloadQuickTrade(ReservationRs reservationRs, HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //导出
        List<ReservationRs> reservationRses = reservationMapper.list(null,reservationRs);
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("客户预约");
        HSSFRow row = sheet.createRow(0);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sheet.setVerticallyCenter(true);
        sheet.setHorizontallyCenter(true);
        String[] excelHeader = {"序号", "公司名称", "联系电话", "煤种", "产品编号", "线上负责", "线下团队", " 最后回访时间", "预约采购量(吨)", "预约采购时间", "预约时间", "处理状态"};
        for (int i = 0; i < excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(i, true);
        }
        for (int i = 0; i < reservationRses.size(); i++) {
            ReservationRs rs = reservationRses.get(i);
            sheet.autoSizeColumn(i, true);
            row = sheet.createRow(i + 1);
            row.setRowStyle(cellStyle);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(rs.getCompanyName());
            row.createCell(2).setCellValue(rs.getPhone());
            row.createCell(3).setCellValue(rs.getCoalType());
            row.createCell(4).setCellValue(rs.getSupplyId());
            row.createCell(5).setCellValue(rs.getOnlineBroker());
            row.createCell(6).setCellValue(rs.getBrokerName());
            row.createCell(7).setCellValue(rs.getRevisitDate()==null?"":rs.getRevisitDate().format(formatter));
            row.createCell(8).setCellValue(rs.getAmount());
            row.createCell(9).setCellValue(rs.getDeliveryDate().format(formatter2));
            row.createCell(10).setCellValue(rs.getReservationDate().format(formatter));
            row.createCell(11).setCellValue(replaceStatusById(rs.getStatus()));
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        String filename = "客户预约采购.xls";
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

    @RequestMapping(value = "/reservation/countdownload", method = RequestMethod.POST)
    @ResponseBody
    private Object countdownload(ReservationRs rs){
      return  reservationMapper.count(rs);
    }
    private String replaceStatusById(Integer status) {
        if (status == 1) {
            return "未沟通";
        } else if (status == 2) {
            return "沟通中未完成采购";
        } else if (status == 3) {
            return "沟通结果不进行采购";
        } else if (status == 4) {
            return "沟通结束,采取线下跟进";
        } else if (status == 5) {
            return "采购已确定";
        }
        return null;
    }


}
