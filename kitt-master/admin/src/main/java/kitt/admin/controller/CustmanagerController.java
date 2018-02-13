package kitt.admin.controller;

import com.itextpdf.text.DocumentException;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.service.Session;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import me.chanjar.weixin.common.util.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lich on 16/1/19.
 */

@RestController
public class CustmanagerController {
    @Autowired
    private CustManagerMapper custManagerMapper;
    @Autowired
    private BrokerTeamMapper brokerTeamMapper;
    @Autowired
    private RegionYMMapper regionYMMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private CustvisitlogMapper custvisitlogMapper;
    @Autowired
    private Session session;

    /**
     * 获取客服访问列表
     *
     * @param page page
     * @return
     */
    @RequestMapping("/cust/listcustmanager")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getCustList(
            @RequestParam(value = "needtype", required = false, defaultValue = "") String needtype,
            @RequestParam(value = "enterpricetype", required = false, defaultValue = "") String enterpricetype,
            @RequestParam(value = "enterpriceprovince", required = false, defaultValue = "") String enterpriceprovince,
            @RequestParam(value = "enterpricecity", required = false, defaultValue = "") String enterpricecity,
            @RequestParam(value = "companyname", required = false, defaultValue = "") String companyname,
            @RequestParam(value = "offlineteam", required = false, defaultValue = "") String offlineteam,
            @RequestParam(value = "offlineteampeople", required = false, defaultValue = "") String offlineteampeople,
            @RequestParam(value = "page", required = true) int page) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> map = new HashMap<String, Object>();
        Pager<Custvisitrecord> pager = custManagerMapper.getCustList(page, 10, needtype, enterpricetype, enterpriceprovince, enterpricecity, Where.$like$(companyname), offlineteam, offlineteampeople);
        map.put("custlist", pager);
        List<Custvisitrecord> list = pager.getList();
        if (list != null && list.size() > 0) {
            for (Custvisitrecord c : list) {
                District dis1 = regionYMMapper.getDistrictByCode(c.getEnterpricecity());
                if (dis1 != null) {
                    c.setEnterpricecityStr(dis1.getName());
                }
                District dis2 = regionYMMapper.getDistrictByCode(c.getEnterpriceprovince());
                if (dis2 != null) {
                    c.setEnterpriceprovinceStr(dis2.getName() + c.getEnterpricecityStr());
                }

                if (!StringUtils.isBlank(c.getOfflineteam())) {
                    BrokerTeam team = brokerTeamMapper.loadTeamById(Integer.parseInt(c.getOfflineteam()));
                    c.setOfflineteam(team.getTeamName());
                }

            }
        }
        pager.setList(list);


        if (!StringUtils.isBlank(enterpricecity) && !enterpricecity.equals("0")) {
            map.put("szdcStr", regionYMMapper.getDistrictByCode(enterpricecity).getName());
            map.put("szdc", enterpricecity);
        } else {
            map.put("szdc", 0);
            map.put("szdcStr", "全部");
        }
        map.put("gsmz", companyname);

        if (!StringUtils.isBlank(offlineteampeople) && !offlineteampeople.equals("0")) {
            map.put("xxtdcStr", adminMapper.getAdminById(Integer.parseInt(offlineteampeople)).getName());
            map.put("xxtdc", offlineteampeople);
        } else {
            map.put("xxtdc", 0);
            map.put("xxtdcStr", "全部");
        }
        map.put("teamList", brokerTeamMapper.listAll());
        map.put("provinces", regionYMMapper.getAllProvinces());
        map.put("xqlx", needtype);
        map.put("qylx", enterpricetype);

        map.put("szdp", enterpriceprovince);
        //map.put("szdc",enterpricecity);
        map.put("gsmz", companyname);

        map.put("xxtdt", offlineteam);
        //map.put("xxtdc", offlineteampeople);
        return map;
    }


    @RequestMapping("  /cust/exportExcel")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public void getCustList(
            @RequestParam(value = "needtype", required = false, defaultValue = "") String needtype,
            @RequestParam(value = "enterpricetype", required = false, defaultValue = "") String enterpricetype,
            @RequestParam(value = "enterpriceprovince", required = false, defaultValue = "") String enterpriceprovince,
            @RequestParam(value = "enterpricecity", required = false, defaultValue = "") String enterpricecity,
            @RequestParam(value = "companyname", required = false, defaultValue = "") String companyname,
            @RequestParam(value = "offlineteam", required = false, defaultValue = "") String offlineteam,
            @RequestParam(value = "offlineteampeople", required = false, defaultValue = "") String offlineteampeople, HttpServletRequest request, HttpServletResponse response) throws IOException, DocumentException {
        Map<String, Object> map = new HashMap<String, Object>();
        List<Custvisitrecord> list = custManagerMapper.findCustDatas(needtype, enterpricetype, enterpriceprovince, enterpricecity, Where.$like$(companyname), offlineteam, offlineteampeople);
        list = changeColName(list);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("客户预约");
        HSSFRow row = sheet.createRow(0);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sheet.setVerticallyCenter(true);
        sheet.setHorizontallyCenter(true);
        String[] excelHeader = {"来访时间", "公司名称", "企业类型", "企业所在地", "需求类型", "信息来源",
                "问题描述", "解答情况", "煤种", "需求/供应量(吨)", "交/提货地", "交/提货时间",
                "低位热值(kcal/kg)", "全水分(%)", "空干基挥发分(%)", "收到基硫分(%)", "其他指标", "线上负责", "线下团队", "线下结果反馈",
                "客服回访时间", "回访结果", "联系方式", "挂单量(万吨)", "交易量(万吨)"};
        for (int i = 0; i < excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(cellStyle);
            sheet.autoSizeColumn(i, true);
        }
        for (int i = 0; i < list.size(); i++) {
            Custvisitrecord c = list.get(i);
            sheet.autoSizeColumn(i, true);
            row = sheet.createRow(i + 1);
            row.setRowStyle(cellStyle);
            row.createCell(0).setCellValue(c.getVisitetime() == null ? "" : c.getVisitetime().format(formatter));
            row.createCell(1).setCellValue(c.getCompanyname());
            row.createCell(2).setCellValue(c.getEnterpricetype());
            row.createCell(3).setCellValue((c.getEnterpriceprovinceStr() == null ? "" : c.getEnterpriceprovinceStr()) + " " + (c.getEnterpricecityStr() == null ? "" : c.getEnterpricecityStr()) + " " + (c.getEnterpricelocationdetail() == null ? "" : c.getEnterpricelocationdetail()));
            row.createCell(4).setCellValue(c.getNeedtype() == null ? "" : c.getNeedtype());
            row.createCell(5).setCellValue(c.getInfoorigin() == null ? "" : c.getInfoorigin());
            row.createCell(6).setCellValue(c.getProblemdesc() == null ? "" : c.getProblemdesc());
            row.createCell(7).setCellValue(c.getAnswerinfo() == null ? "" : c.getAnswerinfo());
            row.createCell(8).setCellValue(c.getCoaltype() == null ? "" : c.getCoaltype());
            row.createCell(9).setCellValue(c.getNeedamout() == null ? "" : String.valueOf(c.getNeedamout()));
            row.createCell(10).setCellValue((c.getPickupprovince() == null ? "" : c.getPickupprovince()) + " " + (c.getPickupcity() == null ? "" : c.getPickupcity()));
            row.createCell(11).setCellValue((c.getPickuptime() == null ? "" : c.getPickuptime().format(formatter)));
            row.createCell(12).setCellValue((c.getNcv1() == null ? "" : c.getNcv1()) + (c.getNcv2() == null ? "" : "~" + c.getNcv2()));
            row.createCell(13).setCellValue((c.getTm1() == null ? "" : c.getTm1()) + (c.getTm2() == null ? "" : "~" + c.getTm2()));
            row.createCell(14).setCellValue((c.getAdv1() == null ? "" : c.getAdv1()) + (c.getAdv2() == null ? "" : "~" + c.getAdv2()));
            row.createCell(15).setCellValue((c.getRs1() == null ? "" : c.getRs1()) + (c.getRs2() == null ? "" : "~" + c.getRs2()));
            row.createCell(16).setCellValue((c.getOtherindicators() == null ? "" : c.getOtherindicators()));
            row.createCell(17).setCellValue((c.getOnlinepeople() == null ? "" : c.getOnlinepeople()));
            row.createCell(18).setCellValue((c.getOfflineteam() == null ? "" : c.getOfflineteam()));
            row.createCell(19).setCellValue((c.getResultfeedback() == null ? "" : c.getResultfeedback()));
            row.createCell(20).setCellValue((c.getReturnvisittime() == null ? "" : c.getReturnvisittime().format(formatter)));
            row.createCell(21).setCellValue((c.getReturnvisitresult() == null ? "" : c.getReturnvisitresult()));
            row.createCell(22).setCellValue((c.getRegisterphone() == null ? "" : c.getRegisterphone()));
            row.createCell(23).setCellValue((c.getHangnumber() == null ? "" : String.valueOf(c.getHangnumber())));
            row.createCell(24).setCellValue((c.getTradenumber() == null ? "" : String.valueOf(c.getTradenumber())));
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        String filename = "客服访问信息.xls";
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


    //根据线下团队获取成员
    @RequestMapping("/cust/getMembers")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getMembers(
            @RequestParam(value = "id", required = true) String id) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (!StringUtils.isBlank(id)) {
            map.put("members", adminMapper.findByTeamId(Integer.parseInt(id)));
        } else {
            map.put("members", "");
        }
        return map;

    }


    //获取省下面的市
    @RequestMapping("/cust/getCitys")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getCitys(
            @RequestParam(value = "code", required = true) String code) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("citys", regionYMMapper.getAllCitys(code));
        return map;

    }


    @RequestMapping("/cust/addcustmanager")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object addcustmanager(@Valid Custvisitrecord custvisitrecord, BindingResult result) {
        if (result.hasErrors()) {
            throw new BusinessException(result.getFieldErrors().get(0).getDefaultMessage());
        }
        Map<String, Object> map = new HashMap<String, Object>();
        custvisitrecord.setRemark("首次新增录入");
        custvisitrecord = assignment(custvisitrecord);
        custManagerMapper.addCustvisitrecord(custvisitrecord);
        Custvisitlog log = new Custvisitlog();
        log.setAdminid(session.getAdmin().getId());
        log.setOperator(session.getAdmin().getUsername());
        log.setContent("首次新增录入");
        log.setRecordid(custvisitrecord.getId());
        custvisitlogMapper.addCustLog(log);
        map.put("message", true);
        return map;

    }


    public Custvisitrecord assignment(Custvisitrecord custvisitrecord) {
        Custvisitrecord record = custvisitrecord;
        if (record != null) {
            if (StringUtils.isBlank(record.getNcv1()) && !StringUtils.isBlank(record.getNcv2())) {
                record.setNcv1(record.getNcv2());
            }
            if (!StringUtils.isBlank(record.getNcv1()) && StringUtils.isBlank(record.getNcv2())) {
                record.setNcv2(record.getNcv1());
            }

            if (StringUtils.isBlank(record.getAdv1()) && !StringUtils.isBlank(record.getAdv2())) {
                record.setAdv1(record.getAdv2());
            }
            if (!StringUtils.isBlank(record.getAdv1()) && StringUtils.isBlank(record.getAdv2())) {
                record.setAdv2(record.getAdv1());
            }
            if (StringUtils.isBlank(record.getRs1()) && !StringUtils.isBlank(record.getRs2())) {
                record.setRs1(record.getRs2());
            }
            if (!StringUtils.isBlank(record.getRs1()) && StringUtils.isBlank(record.getRs2())) {
                record.setRs2(record.getRs1());
            }
            if (StringUtils.isBlank(record.getTm1()) && !StringUtils.isBlank(record.getTm2())) {
                record.setTm1(record.getTm2());
            }
            if (!StringUtils.isBlank(record.getTm1()) && StringUtils.isBlank(record.getTm2())) {
                record.setTm2(record.getTm1());
            }
            return record;
        }
        return null;
    }


    @RequestMapping(value = "/cust/findByCustById/{id}", method = RequestMethod.GET)
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object findByCustById(@PathVariable("id") int id) {
        Map<String, Object> map = new HashMap<String, Object>();
        Custvisitrecord custvisitrecord = custManagerMapper.findByCustById(id);
        District dis1 = regionYMMapper.getDistrictByCode(custvisitrecord.getEnterpricecity());
        if (dis1 != null) {
            custvisitrecord.setEnterpricecityStr(regionYMMapper.getDistrictByCode(custvisitrecord.getEnterpricecity()).getName());
        } else {
            custvisitrecord.setEnterpricecity("");
            custvisitrecord.setEnterpricecityStr("--请选择--");
        }
        District dis2 = regionYMMapper.getDistrictByCode(custvisitrecord.getPickupcity());
        if (dis2 != null) {
            custvisitrecord.setPickupcityStr(regionYMMapper.getDistrictByCode(custvisitrecord.getPickupcity()).getName());
        } else {
            custvisitrecord.setPickupcity("");
            custvisitrecord.setPickupcityStr("--请选择--");
        }
        if (!StringUtils.isBlank(custvisitrecord.getOfflineteampeople())) {
            Admin admin = adminMapper.getAdminById(Integer.parseInt(custvisitrecord.getOfflineteampeople()));
            if (admin != null) {
                custvisitrecord.setOfflineteampeopleStr(adminMapper.getAdminById(Integer.parseInt(custvisitrecord.getOfflineteampeople())).getName());
            }
        } else {
            custvisitrecord.setOfflineteampeopleStr("--请选择--");
            custvisitrecord.setOfflineteampeople("");
        }
        map.put("custvisitrecord", custvisitrecord);
        map.put("teamList", brokerTeamMapper.listAll());
        map.put("provinces", regionYMMapper.getAllProvinces());
        map.put("cid", id);
        map.put("custlogs", custvisitlogMapper.findCustListLog(id));
        return map;

    }


    @RequestMapping("/cust/altercustmanager")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object altercustmanager(@Valid Custvisitrecord custvisitrecord, BindingResult result) {
        if (result.hasErrors()) {
            throw new BusinessException(result.getFieldErrors().get(0).getDefaultMessage());
        }
        Map<String, Object> map = new HashMap<String, Object>();
        custvisitrecord = assignment(custvisitrecord);
        custManagerMapper.alterCustvisitrecord(custvisitrecord);
        Custvisitlog log = new Custvisitlog();
        log.setAdminid(session.getAdmin().getId());
        log.setOperator(session.getAdmin().getUsername());
        log.setContent(custvisitrecord.getRemark());
        log.setRecordid(custvisitrecord.getId());
        custvisitlogMapper.addCustLog(log);
        map.put("message", true);
        return map;

    }


    public List<Custvisitrecord> changeColName(List<Custvisitrecord> list) {
        List<Custvisitrecord> list1 = new ArrayList<Custvisitrecord>();
        list1 = list;
        if (list1 != null && list1.size() > 0) {
            for (Custvisitrecord c : list1) {
                District dis1 = regionYMMapper.getDistrictByCode(c.getEnterpricecity());
                if (dis1 != null) {
                    //企业所在地市
                    c.setEnterpricecityStr(dis1.getName());
                }
                District dis2 = regionYMMapper.getDistrictByCode(c.getEnterpriceprovince());
                if (dis2 != null) {
                    //企业所在地省
                    c.setEnterpriceprovinceStr(dis2.getName());
                }
                Admin admin = null;
                if (!StringUtils.isBlank(c.getOfflineteampeople())) {
                    admin = adminMapper.getAdminById(Integer.parseInt(c.getOfflineteampeople()));
                }
                if (admin != null) {
                    //线下团队负责人
                    c.setOfflineteampeopleStr(admin.getUsername());
                }
                //线下团队名
                if (!StringUtils.isBlank(c.getOfflineteam())) {
                    BrokerTeam team = brokerTeamMapper.loadTeamById(Integer.parseInt(c.getOfflineteam()));
                    c.setOfflineteam(team.getTeamName());
                }
                if (c.getEnterpricetype() != null) {
                    if (c.getEnterpricetype().equals("其他")) {
                        c.setEnterpricetype(c.getOtherenterpricetype());
                    }
                }
                if (c.getNeedtype() != null) {
                    if (c.getNeedtype().equals("其他")) {
                        c.setNeedtype(c.getOtherneedtype());
                    }
                }
                if (c.getCoaltype() != null) {
                    if (c.getCoaltype().equals("其他")) {
                        c.setCoaltype(c.getOthercoaltype());
                    }
                }
                District dis3 = regionYMMapper.getDistrictByCode(c.getPickupcity());
                if (dis3 != null) {

                    c.setPickupcity(dis3.getName());
                }
                District dis4 = regionYMMapper.getDistrictByCode(c.getPickupprovince());
                if (dis4 != null) {

                    c.setPickupprovince(dis4.getName());
                }
            }
        }
        return list1;
    }


}
