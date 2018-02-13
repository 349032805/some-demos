package kitt.admin.controller;

import com.mysql.jdbc.StringUtils;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.NotFoundException;
import kitt.core.domain.AuthenticationRole;
import kitt.core.domain.User;
import kitt.core.domain.UserInfoLogin;
import kitt.core.persistence.DataBookMapper;
import kitt.core.persistence.UserInfoMapper;
import kitt.core.persistence.UserMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/8/19.
 * 此Controller 专门用来 统计和分析用户登陆情况等数据
 */
@RestController
@RequestMapping("/userinfo")
public class UserInfoController {
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 用户登陆信息统计一览表
     * @param content          搜索内容
     * @param monthnums        近几个月，1个月monthnums=1, 1年，monthnums=12
     * @param sortOrder        排序方式
     * @param currentId        当前排序id
     * @param startDate        搜索开始时间
     * @param endDate          搜索结束时间
     * @param page             当前页数
     * @return                 用户登陆信息list
     */
    @RequestMapping("/loginlist")
    @Authority(role = AuthenticationRole.Service)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    public Object getUserLoginInfoList(@RequestParam(value = "content", required = false, defaultValue = "")String content,
                                       @RequestParam(value = "monthnums", required = false, defaultValue = "0")int monthnums,
                                       @RequestParam(value = "sorttype", required = false, defaultValue = "login")String sortType,
                                       @RequestParam(value = "sortorder", required = false, defaultValue = "false")boolean sortOrder,
                                       @RequestParam(value = "currentId", required = false, defaultValue = "totalTimesDown")String currentId,
                                       @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                                       @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                                       @RequestParam(value = "page", required = false, defaultValue = "0")int page){
        Map<String, Object> map = new HashMap<>();
        String startDateTemp = startDate;
        String endDateTemp = endDate;
        if(StringUtils.isNullOrEmpty(startDate)){
            startDate = "2015-01-01";
        }
        if(StringUtils.isNullOrEmpty(endDate)){
            endDate = LocalDate.now().plusDays(1).toString();
        }
        LocalDate lastSelectDate = LocalDate.parse("2015-01-01");
        if(monthnums != 0){
            lastSelectDate = LocalDate.now().minusMonths(monthnums);
        }
        map.put("userLoginList", userInfoMapper.pageAllUserInfo(lastSelectDate, Where.$like$(content), startDate, endDate, sortType, sortOrder, page, 10));
        map.put("sorttype", sortType);
        map.put("sortorder", sortOrder);
        map.put("content", content);
        map.put("currentId", currentId);
        map.put("startDate", startDateTemp);
        map.put("endDate", endDateTemp);
        map.put("monthnums", monthnums);
        map.put("logintimezone", dataBookMapper.getDataBookListByType("logintimezone"));
        return map;
    }

    /**
     * 导出用户登陆信息方法
     * @param content           搜索内容
     * @param monthnums         近几个月，1个月monthnums=1, 1年，monthnums=12
     * @param sortOrder         排序方式， 升序还是倒序
     * @param startDate         搜索 开始时间
     * @param endDate           搜索 结束时间
     * @param response
     * @param request
     * @throws IOException
     */
    @RequestMapping("/exportUserLogin")
    public void doExportUserLoginInfo(@RequestParam(value = "content", required = false, defaultValue = "")String content,
                                      @RequestParam(value = "monthnums", required = false, defaultValue = "0")int monthnums,
                                      @RequestParam(value = "sorttype", required = false, defaultValue = "login")String sortType,
                                      @RequestParam(value = "sortorder", required = false, defaultValue = "false")boolean sortOrder,
                                      @RequestParam(value = "startDate", required = false, defaultValue = "")String startDate,
                                      @RequestParam(value = "endDate", required = false, defaultValue = "")String endDate,
                                      HttpServletResponse response,
                                      HttpServletRequest request) throws IOException {
        if(StringUtils.isNullOrEmpty(startDate)){
            startDate = "2015-01-01";
        }
        if(StringUtils.isNullOrEmpty(endDate)){
            endDate = LocalDate.now().plusDays(1).toString();
        }
        LocalDate lastSelectDate = LocalDate.parse("2015-01-01");
        if(monthnums != 0){
            lastSelectDate = LocalDate.now().minusMonths(monthnums);
        }
        String loginTimesName = "";
        switch (monthnums){
            case 0:
                loginTimesName = "总登录次数";
                break;
            case 1:
                loginTimesName = "近1个月登陆次数";
                break;
            case 3:
                loginTimesName = "近3个月登陆次数";
                break;
            case 6:
                loginTimesName = "近6个月登陆次数";
                break;
            case 12:
                loginTimesName = "近1年登陆次数";
                break;
            case 36:
                loginTimesName = "近3年登陆次数";
                break;
            default:
                break;
        }
        HSSFWorkbook wb = new HSSFWorkbook();
        String filename = "用户登陆信息一览表";
        HSSFSheet sheet = wb.createSheet(filename);
        HSSFRow row = sheet.createRow(0);
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sheet.setVerticallyCenter(true);
        sheet.setHorizontallyCenter(true);
        sheet.setColumnWidth(0, 1200);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 3600);
        sheet.setColumnWidth(3, 3800);
        sheet.setColumnWidth(4, 3600);
        sheet.setColumnWidth(5, 3600);
        sheet.setColumnWidth(6, 5000);
        sheet.setColumnWidth(7, 5000);
        String[] excelHeader = {"序号", "公司名称", "法人", "联系电话", "注册时间", loginTimesName, "总供应量", "总需求量"};
        for (int i=0; i <excelHeader.length; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(style);
        }
        List<UserInfoLogin> userLoginList = userInfoMapper.getAllUserLoginInfoList(lastSelectDate, Where.$like$(content), startDate, endDate, sortType, sortOrder, -1, -1);
        for (int i = 0; i < userLoginList.size(); i++) {
            UserInfoLogin userLogin = userLoginList.get(i);
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(String.valueOf(i + 1));
            row.createCell(1).setCellValue(String.valueOf(userLogin.getCompanyname()));
            row.createCell(2).setCellValue(String.valueOf(userLogin.getUsername()));
            row.createCell(3).setCellValue(String.valueOf(userLogin.getUserphone()));
            row.createCell(4).setCellValue(String.valueOf(String.valueOf(userLogin.getCreatetime())));
            row.createCell(5).setCellValue(String.valueOf(userLogin.getLogintimes()));
            row.createCell(6).setCellValue(String.valueOf(userLogin.getTotalSellinfoAmount()));
            row.createCell(7).setCellValue(String.valueOf(userLogin.getTotalDemandAmount()));
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
     * 用户登陆详细信息
     * @param id         users表对应的id
     * @return           用户登陆详细信息对象
     */
    @RequestMapping("/login/detail")
    public Object doGetUserInfoLoginDetail(@RequestParam(value = "id", required = true)int id){
        if(userMapper.getUserById(id) == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<>();
        map.put("userInfoDetail", userInfoMapper.getUserInfoDetail(id));
        map.put("userInfoLogin", userInfoMapper.getUserInfoLoginDetail(id, LocalDate.now().minusMonths(1), LocalDate.now().minusMonths(3), LocalDate.now().minusMonths(6), LocalDate.now().minusYears(1), LocalDate.now().minusYears(3)));
        map.put("totalSellInfoAmount", userInfoMapper.getUserTotalSellInfoAmount(id));
        map.put("totalOrderCompleteSoldAmount", userInfoMapper.getUserTotalOrderCompleteSoldAmount(id));
        map.put("totalOrderCompleteBuyAmount", userInfoMapper.getUserTotalOrderCompleteBuyAmount(id));
        map.put("totalDemandAmount", userInfoMapper.getUserTotalDemandAmount(id));
        map.put("totalQuoteSucceedBuyAmount", userInfoMapper.getUserTotalQuoteBuyAmount(id));
        map.put("totalQuoteSucceedSoldAmount", userInfoMapper.getUserTotalQuoteSoldAmount(id));
        return map;
    }

}
