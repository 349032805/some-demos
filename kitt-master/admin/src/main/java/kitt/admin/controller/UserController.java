package kitt.admin.controller;

import com.itextpdf.text.DocumentException;
import com.mysql.jdbc.StringUtils;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.JsonController;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.UserService;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.MySupplyerService;
import kitt.ext.mybatis.Where;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
 * Created by joe on 11/9/14.
 */
@RestController
@RequestMapping("/user/user")
public class UserController extends JsonController{
    @Autowired
    private UserMapper userMapper;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private UserService userService;
	@Autowired
	private DataBookMapper dataBookMapper;
	@Autowired
	private ShopMapper shopMapper;
	@Autowired
	private AdminMapper adminMapper;
	@Autowired
	private Auth auth;
	@Autowired
	private RegionYMMapper regionMapper;

	//用户列表
	@RequestMapping("/userlist")
	@Authority(role = AuthenticationRole.Service)
	@Authority(role = AuthenticationRole.TraderAssistant)
	@Authority(role = AuthenticationRole.LegalPersonnel)
	@Authority(role = AuthenticationRole.Operation)
	@Authority(role = AuthenticationRole.Admin)
	public Object passList(int page, String status,
						   @RequestParam(value = "securephone", required = false, defaultValue = "")String securephone,
						   @RequestParam(value = "startDate", required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
						   @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate,
						   @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("clientTypeList", dataBookMapper.getDataBookListByType("clienttype"));
		map.put("clienttype", clienttype);
		map.put("userList", userMapper.pageAllUser(status, securephone, clienttype, startDate, endDate, page, 10));
		map.put("securephone",securephone);
		map.put("status",status);
		map.put("startDate",startDate);
		map.put("endDate",endDate);
		map.put("dealerList", auth.getDealerList());
		return map;
	}

	@RequestMapping("/count")
	public Integer countData(String status,
							 @RequestParam(value = "securephone", required = false, defaultValue = "")String securephone,
							 @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
							 @RequestParam(value = "startDate",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
							 @RequestParam(value = "endDate",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate){
		return userMapper.countAllUser(status, Where.$like$(securephone), clienttype, startDate, endDate);
	}

	@RequestMapping(value = "/downloadData")
	@Authority(role = AuthenticationRole.Service)
	@Authority(role = AuthenticationRole.TraderAssistant)
	@Authority(role = AuthenticationRole.LegalPersonnel)
	@Authority(role = AuthenticationRole.Admin)
	@Authority(role = AuthenticationRole.Operation)
	public void downloadUserData(String status,
								 @RequestParam(value = "securephone", required = false, defaultValue = "")String securephone,
								 @RequestParam(value = "clienttype", required = false, defaultValue = "0")int clienttype,
								 @RequestParam(value = "startDate",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
								 @RequestParam(value = "endDate",required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate,
								 HttpServletRequest request,
								 HttpServletResponse response) throws IOException, DocumentException {
		List<Map<String,Object>> users = userMapper.userExport(status, Where.$like$(securephone), clienttype, startDate, endDate);
		String filename = status + "用户数据";
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet(filename);
		HSSFRow row = sheet.createRow(0);
		CellStyle cellStyle = wb.createCellStyle();
		cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
		sheet.setVerticallyCenter(true);
		sheet.setHorizontallyCenter(true);
		sheet.setColumnWidth(0, 1200);
		sheet.setColumnWidth(1, 3600);
		sheet.setColumnWidth(2, 8000);
		sheet.setColumnWidth(3, 4500);
		sheet.setColumnWidth(4, 4500);
		String[] excelHeader = {"序号","负责人","客户名称","客户联系方式","审核通过时间"};
		for (int i = 0; i < excelHeader.length; i++) {
			sheet.autoSizeColumn(i, true);
			HSSFCell cell = row.createCell(i);
			cell.setCellValue(excelHeader[i]);
			cell.setCellStyle(cellStyle);
		}
		for(int i=0; i<users.size(); i++){
			Map<String,Object> resultSet =  users.get(i);
			sheet.autoSizeColumn(i, true);
			row = sheet.createRow(i+1);
			row.setRowStyle(cellStyle);
			row.createCell(0).setCellValue(i+1);
			row.createCell(1).setCellValue(String.valueOf(resultSet.get("tradername")).equals("null") ? "" : String.valueOf(resultSet.get("tradername")));
			row.createCell(2).setCellValue(String.valueOf(resultSet.get("companyname")));
			row.createCell(3).setCellValue(String.valueOf(resultSet.get("securephone")));
			row.createCell(4).setCellValue(String.valueOf(resultSet.get("verifytime")));
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
	 * 重置客户密码
	 * @param securephone  手机号码
	 * @return             true or false
	 * @throws Exception
	 */
	@RequestMapping("/edit")
	@Authority(role = AuthenticationRole.Admin)
	public boolean doResetPassword(String securephone) throws Exception {
        User user = userMapper.getUserByPhone(securephone);
        if (user == null) throw new NotFoundException();
        return userService.doResetPasswordMethod(securephone);
	}

	/**
	 * 禁用，启用 客户
	 */
	@RequestMapping("/account")
	@Authority(role = AuthenticationRole.Admin)
	public Object doDisableEnableAccount(String securephone){
        User user = userMapper.getUserByPhone(securephone);
        if (user == null) throw new NotFoundException();
        return userService.doDisableEnableAccountMethod(user);
	}

	/**
	 * 查看用户详情
	 * @param securephone            用户注册手机号
	 * @return
	 */
	@RequestMapping("/view")
	public Object getUserInfo(String securephone){
		User userTemp = userMapper.getUserByPhone(securephone);
        if(userTemp == null) throw new NotFoundException();
        final Company companyTemp = companyMapper.getCompanyByUserid(userTemp.getId());
        return new Object() {
			public boolean success = true;
            public User user = userTemp;
            public Company company = companyTemp == null ? null : companyTemp;
            public List<CompanyVerify> compverifyList = companyTemp == null ? null : companyMapper.getVerifyList(companyTemp.getId());
			public Shop shop = shopMapper.getShopByUserid(user.getId());
			public Admin trader = userTemp.getTraderid() == null ? null : adminMapper.getAdminById(userTemp.getTraderid());
			public List<Admin> dealerList = auth.getDealerList();
			public List<District> provinceslist = regionMapper.getAllProvinces();
			public List<District> loadcitylist = (companyTemp == null||companyTemp.getProvinceCode()==null) ? null : regionMapper.getDistrictByParent(companyTemp.getProvinceCode(), 2);
			public List<District> loadarealist = (companyTemp == null||companyTemp.getCityCode()==null) ? null : regionMapper.getDistrictByParent(companyTemp.getCityCode(), 3);
		};
	}

	/**
	 * 审核通过
	 * @param companyId  公司Id
	 * @param remarks    备注
	 * @return           true or false
	 */
	@RequestMapping("/verifypass")
	@Authority(role = AuthenticationRole.LegalPersonnel)
	@Authority(role = AuthenticationRole.Admin)
	public boolean doCompanyVerifyPass(int companyId, String remarks){
        Company company = companyMapper.getCompanyById(companyId);
        if (company == null) throw new NotFoundException();
		if (companyMapper.countCompanyIsExist(company.getName(), company.getUserid()) != 0) throw new BusinessException("已经存在相同名称并且审核通过的公司!");

        return userService.doCompanyVerifyPassMethod(company, remarks);
	}

	/**
	 * 审核不通过
	 * @param companyId  公司Id
	 * @param remarks    备注
	 * @return           true or false
	 */
	@RequestMapping("/verifyreject")
	@Authority(role = AuthenticationRole.LegalPersonnel)
	@Authority(role = AuthenticationRole.Admin)
	public boolean doCompanyVerifyNotPass(int companyId, String remarks){
        Company company = companyMapper.getCompanyById(companyId);
        if (company == null) throw new NotFoundException();
        return userService.doCompanyVerifyNotPassMethod(company, remarks);
	}


	/**
	 * 帮助客户完善，保存公司信息
	 * @param userid                   公司userid
	 * @param name                     公司name
	 * @param detailaddress                  公司地址
	 * @param invoicinginformation     企业开票信息（图片）
	 * @param businesslicense          营业执照（图片）
	 * @param identificationnumber     纳税人识别号（图片）
	 * @param organizationcode         组织机构编码（图片）
	 * @param operatinglicense         煤炭经营许可证（图片）
	 * @param openinglicense           开户许可证（图片）
	 * @param legalpersonname          法人姓名
	 * @param account                  银行账号
	 * @param openingbank              开户银行
	 * @param identificationnumword    纳税人识别号（文字）
	 * @param zipcode
	 * @return
	 */
	@RequestMapping(value = "/saveCompanyInfo", method = RequestMethod.POST)
	@Authority(role = AuthenticationRole.Service)
	@Authority(role = AuthenticationRole.Admin)
	public boolean saveCompany(@RequestParam("userid")int userid,
							   @RequestParam("name") String name,
							   @RequestParam("provinceCode") String provinceCode,
							   @RequestParam("cityCode") String cityCode,
							   @RequestParam("countryCode") String countryCode,
							   @RequestParam("detailaddress") String detailaddress,
							   @RequestParam("invoicinginformation") String invoicinginformation,
							   @RequestParam("businesslicense") String businesslicense,
							   @RequestParam("identificationnumber") String identificationnumber,
							   @RequestParam("organizationcode") String organizationcode,
							   @RequestParam("operatinglicense") String operatinglicense,
							   @RequestParam("openinglicense") String openinglicense,
							   @RequestParam("legalpersonname") String legalpersonname,
							   @RequestParam("account") String account,
							   @RequestParam("openingbank") String openingbank,
							   @RequestParam("identificationnumword")String identificationnumword,
							   @RequestParam("zipcode")String zipcode){
		if(companyMapper.countCompanyIsExist(name.trim(), userid) != 0) throw new BusinessException("该公司名已经存在，不能保存！");
		String province="";
		String city="";
		String country="";
		District provinceobj= regionMapper.getDistrictByCode(provinceCode);
		if(provinceobj!=null) province=provinceobj.getName();
		District cityobj= regionMapper.getDistrictByCode(cityCode);
		if(cityobj!=null) city=cityobj.getName();
		District countryobj= regionMapper.getDistrictByCode(countryCode);
		if(countryobj!=null) country=countryobj.getName();
		Company companyc = companyMapper.getCompanyByUserid(userid);
		Company company;
		User user = userMapper.getUserById(userid);
		if(companyc == null) {
			company = new Company(name,province,city,country,detailaddress,provinceCode,cityCode,countryCode,user.getSecurephone(), null, invoicinginformation, businesslicense, identificationnumber, organizationcode, operatinglicense, openinglicense, legalpersonname, account, openingbank, userid, identificationnumword, zipcode);
		} else {
			String cname = (StringUtils.isNullOrEmpty(name) == true ? companyc.getName() : name);
			String cinvoicinginformation = (StringUtils.isNullOrEmpty(invoicinginformation) == true ? companyc.getInvoicinginformation() : invoicinginformation);
			String cbusinesslicense = (StringUtils.isNullOrEmpty(businesslicense) == true ? companyc.getBusinesslicense() : businesslicense);
			String cidentificationnumber = (StringUtils.isNullOrEmpty(identificationnumber) == true ? companyc.getIdentificationnumber() : identificationnumber);
			String corganizationcode = (StringUtils.isNullOrEmpty(organizationcode) == true ? companyc.getOrganizationcode() : organizationcode);
			String coperatinglicense = (StringUtils.isNullOrEmpty(operatinglicense) == true ? companyc.getOperatinglicense() : operatinglicense);
            String copeninglicense = (StringUtils.isNullOrEmpty(openinglicense) == true ? companyc.getOpeninglicense() : openinglicense);
			String clegalpersonname = (StringUtils.isNullOrEmpty(legalpersonname) == true ? companyc.getLegalpersonname() : legalpersonname);
			String caccount = (StringUtils.isNullOrEmpty(account) == true ? companyc.getAccount() : account);
			String copeningbank = (StringUtils.isNullOrEmpty(openingbank) == true ? companyc.getOpeningbank() : openingbank);
			String cidentificationnumword = (StringUtils.isNullOrEmpty(identificationnumword) == true ? companyc.getIdentificationnumword() : identificationnumword);
			String czipcode = (StringUtils.isNullOrEmpty(zipcode) == true ? companyc.getZipcode() : zipcode);
			company = new Company(cname,province,city,country,detailaddress , provinceCode , cityCode , countryCode, companyc.getPhone(), companyc.getFax(), cinvoicinginformation, cbusinesslicense, cidentificationnumber, corganizationcode, coperatinglicense, copeninglicense, clegalpersonname, caccount, copeningbank, userid, cidentificationnumword, czipcode);
		}
		return userService.doSaveCompanyInfoMethod(companyc, company, userid);
	}

	//保存公司图片
	@RequestMapping(value = "/saveCompanyPic", method = RequestMethod.POST)
	@Authority(role = AuthenticationRole.Service)
	@Authority(role = AuthenticationRole.Admin)
	public Object saveCompanyPic(@RequestParam("file") MultipartFile file, HttpServletResponse response) throws Exception{
        return auth.uploadPicMethod(EnumFileType.File_UserCompany.toString(), EnumFileType.IMG.toString(), file, null, null);
	}

	@RequestMapping("/addTrader")
	public boolean doAddUserTrader(@RequestParam(value = "id", required = true)int id,
								   @RequestParam(value = "traderid", required = true)int traderid){
		if(userMapper.getUserById(id) == null || adminMapper.getAdminById(traderid) == null) throw new NotFoundException();
		return userService.doAddUpdateUserTraderMethod(id, traderid);
	}



	//选择省份联动查询下属城市
	@RequestMapping("/reloadcity")
	public Object reloadcity(@RequestParam(value = "provincecode", required = false)String provincecode){
		Map<String,Object> map=new HashMap<String,Object>();
		List<District> loadcitylist = regionMapper.getDistrictByParent(provincecode, 2);
		map.put("loadcitylist", loadcitylist);
		map.put("loadarealist", null);
		return map;
	}

	//选择省份联动查询下属城市
	@RequestMapping("/reloadarea")
	public Object reloadarea(@RequestParam(value = "citycode", required = false)String citycode){
		Map<String,Object> map=new HashMap<String,Object>();
		List<District> loadarealist = regionMapper.getDistrictByParent(citycode, 3);
		map.put("loadarealist", loadarealist);
		return map;
	}

}
