package kitt.site.controller.mobile;

import kitt.core.domain.*;
import kitt.core.persistence.CompanyMapper;
import kitt.core.persistence.UserMapper;
import kitt.core.persistence.ValidMapper;
import kitt.core.service.MessageNotice;
import kitt.core.util.AuthMethod;
import kitt.core.util.check.CheckPictureInfo;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.Client;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.Auth;
import kitt.site.service.FileService;
import kitt.site.service.Session;
import kitt.site.service.mobile.UserService;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiangyang on 15-5-13.
 */
@Controller
@RequestMapping("/m")
public class UserController extends JsonController {
    private Logger log = Logger.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private Session session;
    @Autowired
    private WxMpService wxMpService;
    @Autowired
    private Auth auth;
    @Autowired
    private ValidMapper validMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private CheckPictureInfo checkPictureInfo;
    @Autowired
    private AuthMethod authMethod;

    /**
     * 如果手机号已经重复，把账号带到登陆页面
     * @param securephone
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam(value = "securephone", required = false) String securephone,
                        @RequestParam(value = "code", required = false) String code,
                        Map<String, Object> model) throws WxErrorException {
        String wxopenid = "";
        log.info("当前静默授权code:"+code);
        if (!StringUtils.isBlank(code)) {
            try {
                WxMpOAuth2AccessToken wxMpOAuth2AccessToken = wxMpService.oauth2getAccessToken(code);
                wxopenid = wxMpOAuth2AccessToken.getOpenId();
            } catch (WxErrorException e) {
                log.info("用code获取网页授权access_token出错",e);
                return "/m/login";
            }
            User u = userService.getUserByWxopenId(wxopenid);
            //已经绑定用户，直接登录
            if (u != null) {
                session.login(u);
                return "/m/account";
            } else if (session.isLogined()) {
                return "/m/account";
            }
        }
        model.put("securephone", securephone);
        model.put("wxopenid", wxopenid);
        return "/m/login";
    }

    //处理登录逻辑
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody Object login(@Client ClientInfo clientInfo,
                                      @RequestParam(value = "username", required = true) String username,
                                      @RequestParam(value = "password", required = true) String password,
                                      @RequestParam(value = "wxopenid", required = false) String wxopenid,
                                      BindResult result) {
        User user = userService.login(username);
        if (user != null && user.getPassword().equals(DigestUtils.md5Hex(password))) {
            if (false == (user.isIsactive())) {
                result.addError("status", "此账号被禁用!");
            } else {
                session.login(user);
                userService.addUserLog(user, clientInfo);
                if (!StringUtils.isBlank(wxopenid)) {
                    userService.updateWxopenid(wxopenid, user.getId());
                }
            }
        } else {
            result.addError("status", "用户名或密码错误,请重试!");
        }
        return json(result);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public @ResponseBody boolean logout() {
        session.logout();
        return true;
    }

    //隐私条款
    @RequestMapping(value = "/privatePolicy")
    public String showPolicy() {
        return "/m/privatePolicy";
    }

    //使用协议
    @RequestMapping(value = "/useAgreement")
    public String showAgreement() {
        return "/m/useAgreement";
    }

    //检查手机号唯一
    @RequestMapping(value = "/phoneExist/{phoneNum}", method = RequestMethod.POST)
    public @ResponseBody boolean phoneExist(@PathVariable("phoneNum") String phoneNum) {
        return userService.userExists(phoneNum) >= 1 ? false : true;
    }

    @RequestMapping(value = "/updateUserPassword", method = RequestMethod.POST)
    public @ResponseBody Object updatePassword(
            @RequestParam(value = "phone", required = true) String phone,
            @RequestParam(value = "userId", required = true) int userId) {
        userService.updateUserPassword(phone, userId);
        return true;
    }

    @RequestMapping(value = "/checkLogin", method = RequestMethod.GET)
    public @ResponseBody boolean userIsLogin() {
        if (session.getUser() == null) {
            return false;
        }
        return true;
    }

    /**
     * 手机号是否存在
     * @param user                session 当前用户
     * @param type                0: 手机号, 1 : 邮箱
     */
    @RequestMapping(value = "/center/checkphone", method = RequestMethod.POST)
    @ResponseBody
    public Object doCheckResetPasswordPhone(@CurrentUser User user, @Client ClientInfo clientInfo,
                                            @RequestParam(value = "type", required = true)int type,
                                            @RequestParam(value = "codetype", required = true)int codetype) {
        if (type != 0 && type != 1) throw new NotFoundException(EnumRemindInfo.Site_System_Error.value());
        if (type == 0) {
            return authMethod.doCheckUserPhone(user.getSecurephone(), codetype, true, clientInfo.getIP());
        } else {
            return authMethod.doCheckEmail(user.getSecurephone(), true);
        }
    }

    /*************************************************** 注册 start ****************************************************/

    /**
     * 跳转到注册页面
     */
    @RequestMapping(value = "/reg")
    public String reg() {
        return "/m/register";
    }

    /**
     * 检查手机号是否正确
     * @param securephone           手机号
     */
    @RequestMapping(value = "/register/checkphone", method = RequestMethod.POST)
    public @ResponseBody Object registerCheckPhone(@Client ClientInfo clientInfo,
                                                   @RequestParam(value = "securephone", required = true) String securephone,
                                                   @RequestParam(value = "codetype", required = true) int codetype) throws Exception {
        return authMethod.doCheckUserPhone(securephone, codetype, false, clientInfo.getIP());
    }

    /**
     * 给手机发送验证码
     * @param securephone           手机号
     * @param imagecode             图片验证码
     */
    @RequestMapping(value = "/register/sendSmsCode", method = RequestMethod.POST)
    @ResponseBody
    public Object doSendSmsCode(@Client ClientInfo clientInfo,
                                @RequestParam(value = "securephone", required = true) String securephone,
                                @RequestParam(value = "imagecode", required = false, defaultValue = "") String imagecode) throws Exception {
        if (!auth.doCheckImageCodeTimesAndRight(securephone, imagecode, clientInfo.getIP())) {
            return new Object(){
                public boolean success = false;
                public String error = "图片验证码错误";
                public String errorType = "imagecode";
            };
        }
        return authMethod.sendSMSCode(auth.getSessionUserId(), securephone, false, ValidateType.registerWeixin, clientInfo.getIP());
    }

    /**
     * 注册提交
     * @param securephone           手机号
     * @param password              密码
     * @param smscode               短信验证码
     * @param userFrom              用户来源
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public MapObject doRegister(@RequestParam(value = "securephone", required = true)String securephone,
                                @RequestParam(value = "password", required = true)String password,
                                @RequestParam(value = "smscode", required = true)String smscode,
                                @RequestParam(value = "userForm", required = false)String userFrom) throws Exception {
        Phonevalidator phonevalidator = validMapper.findVerifyCode(securephone, smscode, ValidateType.registerWeixin);
        MapObject map =  authMethod.doCheckCode(securephone, false, phonevalidator);
        if (map.isSuccess()) {
            if (!auth.addUser(securephone, password, userFrom, 3)) {
                map.setSuccess(false);
                map.setError("注册失败");
            }
            validMapper.modifyValidatedAndTime(securephone, smscode);
        }
        return map;
    }

    /*************************************************** 注册 end ******************************************************/


    /*************************************************** 修改手机号 start ***********************************************/

    /**
     * 修改密码,修改手机号,进入手机验证码页面
     */
    @LoginRequired
    @RequestMapping(value = "/account/gotoSendCode", method = RequestMethod.GET)
    public String gotoSendCode(String reqFrom, @CurrentUser User user, @Client ClientInfo clientInfo, Model model) {
        model.addAttribute("reqFrom", reqFrom);
        model.addAttribute("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(user.getSecurephone(), clientInfo.getIP()));
        model.addAttribute("phone", user.getSecurephone());
        return "/m/myzone/myZone_validcode";
    }

    /**
     * 进入修改手机页面
     */
    @LoginRequired
    @RequestMapping(value = "/account/gotoChangePhone", method = RequestMethod.GET)
    public String gotoChangePhone(@CurrentUser User user, @Client ClientInfo clientInfo, Model model) {
        model.addAttribute("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(user.getSecurephone(), clientInfo.getIP()));
        return "/m/myzone/myZone_changePhone";
    }

    /**
     * 修改手机号 - 发送短信验证码/邮箱验证码
     * @param imagecode            图片验证码
     * @param type                 0: 手机接收验证码, 1: 邮箱接收验证码
     */
    @RequestMapping(value = "/changephone/sendcode")
    @ResponseBody
    @LoginRequired
    public Object doSendChangePhoneSMSCode(@Client ClientInfo clientInfo,
                                           @RequestParam(value = "imagecode", required = false, defaultValue = "")String imagecode,
                                           @RequestParam(value = "type", required = true)int type) throws Exception {
        if(type != 0 && type != 1) throw new BusinessException("系统出错, 请刷新页面重试!");
        User user = auth.getLatestUserBySession(session.getUser().getId());
        if (!auth.doCheckImageCodeTimesAndRight(user.getSecurephone(), imagecode, clientInfo.getIP())) {
            return new Object(){
                public boolean success = false;
                public String error = "图片验证码错误";
                public String errorType = "imagecode";
            };
        }
        if (type == 0) {
            Map<String, Object> map = authMethod.sendSMSCode(user.getId(), user.getSecurephone(), true, ValidateType.updatePhoneNumWeixin, clientInfo.getIP());
            map.put("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(user.getSecurephone(), clientInfo.getIP()));
            return map;
        } else {
            return authMethod.sendEMailCode(user.getId(), user.getEmail(), true, ValidateType.updatePhoneNumWeixin, clientInfo.getIP());
        }
    }

    /**
     * 修改手机号 - 检查新手机号
     * @param securephone               手机号
     * @return
     */
    @RequestMapping(value = "/changephone/checknewphone", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object CheckNewSecurephone(@Client ClientInfo clientInfo,
                                      @RequestParam(value = "securephone", required = true) String securephone,
                                      @RequestParam(value = "codetype", required = true) int codetype) {
        return authMethod.doCheckUserPhone(securephone, codetype, false, clientInfo.getIP());
    }

    /**
     * 修改手机号 - 向新手机号发送短信验证码
     * @param imagecode                  图片验证码
     * @return
     */
    @RequestMapping(value = "/changephone/newsendcode")
    @ResponseBody
    @LoginRequired
    public Object doSendChangePhoneSMSCode(@Client ClientInfo clientInfo,
                                           @RequestParam(value = "securephone", required = true)String securephone,
                                           @RequestParam(value = "imagecode", required = false, defaultValue = "")String imagecode) throws Exception {
        if (!auth.doCheckImageCodeTimesAndRight(securephone, imagecode, clientInfo.getIP())) {
            return new Object(){
                public boolean success = false;
                public String error = "图片验证码错误";
                public String errorType = "imagecode";
            };
        }
        return authMethod.sendSMSCode(auth.getSessionUserId(), securephone, false, ValidateType.updatePhoneNumWeixin, clientInfo.getIP());
    }

    /**
     * 修改手机号 - 提交
     * @param newPhone                   新手机号
     * @param code                       验证码
     * @param user
     */
    @RequestMapping(value = "/account/changePhone", method = RequestMethod.POST)
    @LoginRequired
    @ResponseBody
    public Object changePhone(String newPhone, String code, @CurrentUser User user, BindResult bindResult) {
        Phonevalidator phonevalidator = validMapper.findVerifyCode(newPhone, code, ValidateType.updatePhoneNumWeixin);
        User newUser=userMapper.getUserByPhone(newPhone);
        if(newUser != null) {
            bindResult.addError("userIsExists", "该手机号已经存在!");
        } else if (StringUtils.isBlank(code)) {
            bindResult.addError("verifyCode", "验证码不能为空!");
        } else if (phonevalidator == null) {
            bindResult.addError("verifyCode", "验证码错误!");
        } else if (phonevalidator.getExpiretime().isBefore(LocalDateTime.now())) {
            bindResult.addError("verifyCode", "验证码已过期!");
        } else {
            validMapper.modifyValidatedAndTime(newPhone, code);
            userMapper.modifyPhone(newPhone, user.getId());
            user.setSecurephone(newPhone);
        }
        return json(bindResult);
    }

    /**************************************************** 修改手机号 end ************************************************/

    /**************************************************** 忘记密码 start ************************************************/

    /**
     * 进入忘记密码页面
     */
    @RequestMapping(value = "/account/gotoForgetPass", method = RequestMethod.GET)
    public String gotoForgetPass() {
        return "/m/myzone/myZone_forgetPass";
    }

    /**
     * 忘记密码-检查手机号是否存在
     * @param securephone            手机号
     */
    @RequestMapping(value = "/forgetpwd/checkphone", method = RequestMethod.POST)
    @ResponseBody
    public Object doCheckResetPasswordPhone(@Client ClientInfo clientInfo,
                                            @RequestParam(value = "securephone", required = true) String securephone,
                                            @RequestParam(value = "codetype", required = true) int codetype) {
        return authMethod.doCheckUserPhone(securephone, codetype, true, clientInfo.getIP());
    }

    /**
     * 忘记密码-发送短信验证码/邮箱验证码
     * @param securephone            手机号
     * @param imagecode              图片验证码
     * @return
     */
    @RequestMapping(value = "/forgetpwd/sendcode")
    @ResponseBody
    public Object doSendResetPasswordSMSCode(@Client ClientInfo clientInfo,
                                             @RequestParam(value = "securephone", required = true)String securephone,
                                             @RequestParam(value = "imagecode", required = false, defaultValue = "")String imagecode,
                                             @RequestParam(value = "type", required = true)int type) throws Exception {
        if (type != 0 && type != 1) throw new BusinessException("系统出错, 请刷新页面重试!");
        if (!auth.doCheckImageCodeTimesAndRight(securephone, imagecode, clientInfo.getIP())) {
            return new Object(){
                public boolean success = false;
                public String error = "图片验证码错误";
                public String errorType = "imagecode";
            };
        }
        if (type == 0) {
            return authMethod.sendSMSCode(auth.getSessionUserId(), securephone, true, ValidateType.forgetPasswordWeixin, clientInfo.getIP());
        } else {
            return authMethod.sendEMailCode(auth.getSessionUserId(), securephone, true, ValidateType.forgetPasswordWeixin, clientInfo.getIP());
        }
    }

    /**
     * 忘记密码-提交
     * @param code                   短信验证码
     * @param password               密码
     * @param phone                  手机号
     * @param bindResult
     */
    @ResponseBody
    @RequestMapping(value = "/account/resetForgetPassword", method = RequestMethod.POST)
    public Object resetForgetPassword(String code, String password, String phone, BindResult bindResult) {
        Phonevalidator phonevalidator = validMapper.findVerifyCode(phone, code, ValidateType.forgetPasswordWeixin);
        User user = userMapper.getUserByPhone(phone);
        if(user==null) {
            bindResult.addError("userIsNotExists", "该用户不存在!");
        } else if(!user.isIsactive()) {
            bindResult.addError("userorbidden", "该用户已被禁用!");
        } else if (StringUtils.isBlank(code)) {
            bindResult.addError("verifyCode", "验证码不能为空!");
        } else if (phonevalidator == null) {
            bindResult.addError("verifyCode", "验证码错误!");
        } else if (phonevalidator.getExpiretime().isBefore(LocalDateTime.now())) {
            bindResult.addError("verifyCode", "验证码已过期!");
        } else{
            validMapper.modifyValidatedAndTime(user.getSecurephone(), code);
            userMapper.modifyPasswdByPhone(DigestUtils.md5Hex(password), user.getSecurephone());
        }
        return json(bindResult);
    }

    /**************************************************** 忘记密码 end **************************************************/


    /**************************************************** 修改密码 start ************************************************/

    /**
     * 进入修改密码页面
     */
    @LoginRequired
    @RequestMapping(value = "/account/gotoResetPassword", method = RequestMethod.GET)
    public String gotoResetPassword(@CurrentUser User user, @Client ClientInfo clientInfo, Model model) {
        model.addAttribute("phone", user.getSecurephone());
        model.addAttribute("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(user.getSecurephone(), clientInfo.getIP()));
        return "/m/myzone/myZone_resetPasswd";
    }

    /**
     * 修改密码 - 发送短信验证码
     * @param imagecode  图片验证码
     * @param type       0: 手机号, 1: 邮箱
     */
    @RequestMapping(value = "/changepwd/sendcode")
    @ResponseBody
    @LoginRequired
    public Object doSendChangePWDSMSCode(@Client ClientInfo clientInfo,
                                         @RequestParam(value = "imagecode", required = false, defaultValue = "") String imagecode,
                                         @RequestParam(value = "type", required = true) int type) throws Exception {
        if (type != 0 && type != 1) throw new BusinessException("系统出错, 请刷新页面重试!");
        User user = auth.getLatestUserBySession(session.getUser().getId());
        if (!auth.doCheckImageCodeTimesAndRight(user.getSecurephone(), imagecode, clientInfo.getIP())) {
            return new Object(){
                public boolean success = false;
                public String error = "图片验证码错误";
                public String errorType = "imagecode";
            };
        }
        if (type == 0) {
            Map<String, Object> map = authMethod.sendSMSCode(user.getId(), user.getSecurephone(), true, ValidateType.resetpasswordWeixin, clientInfo.getIP());
            map.put("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(session.getUser().getSecurephone(), clientInfo.getIP()));
            return map;
        } else {
            return authMethod.sendEMailCode(user.getId(), user.getEmail(), true, ValidateType.resetpasswordWeixin, clientInfo.getIP());
        }
    }

    /**
     * 修改密码提交
     */
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/resetPassword", method = RequestMethod.POST)
    public Object resetPassword(String code, String password, @CurrentUser User user, BindResult bindResult) {
        Phonevalidator phonevalidator = validMapper.findVerifyCode(user.getSecurephone(), code, ValidateType.resetpasswordWeixin);
        //验证码
        if (StringUtils.isBlank(code)) {
            bindResult.addError("verifyCode", "验证码不能为空");
        } else if (phonevalidator == null) {
            bindResult.addError("verifyCode", "验证码错误");
        } else if (phonevalidator.getExpiretime().isBefore(LocalDateTime.now())) {
            bindResult.addError("verifyCode", "验证码已过期");
        } else {
            validMapper.modifyValidatedAndTime(user.getSecurephone(), code);
            userMapper.modifyPasswdByPhone(DigestUtils.md5Hex(password), user.getSecurephone());
        }
        return json(bindResult);
    }

    /**************************************************** 修改密码 end **************************************************/


    /**************************************************** 保存公司信息 start *********************************************/

    //上传公司信息和图片,因为页面批量上传MultipartFile数组无法区分图片名称,所以改成单个文件参数
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/account/saveCompanyInfo", method = RequestMethod.POST)
    public Object saveCompanyInfo(Company company, @RequestParam("license") MultipartFile license,
                                  @RequestParam("tax") MultipartFile tax, @RequestParam("organization") MultipartFile organization,
                                  @RequestParam("openAccount") MultipartFile openAccount, @RequestParam("bill") MultipartFile bill,
                                  @RequestParam("operate") MultipartFile operate,
                                  String isdeleteBill,String isdeleteOperate,
                                  @CurrentUser User user) throws Exception {
        user = userMapper.getUserById(user.getId());
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = true;
        String errorMsg = null;
        int compnayCount = companyMapper.countCompanyIsExist(company.getName().trim(), user.getId());
        if(compnayCount != 0) {
            success = false;
            errorMsg = "公司名称已存在";
        } else {
            if (!(checkPictureInfo.doCheckPictureTypeIncludeNullFile(license) && checkPictureInfo.doCheckPictureTypeIncludeNullFile(tax) && checkPictureInfo.doCheckPictureTypeIncludeNullFile(organization) && checkPictureInfo.doCheckPictureTypeIncludeNullFile(openAccount) && checkPictureInfo.doCheckPictureTypeIncludeNullFile(bill) && checkPictureInfo.doCheckPictureTypeIncludeNullFile(operate))) {
                success = false;
                errorMsg = "只能上传 .jpg, .bmp, .png, .jpeg 格式的图片";
            } else if (!(checkPictureInfo.doCheckPictureSize(license) && checkPictureInfo.doCheckPictureSize(tax) && checkPictureInfo.doCheckPictureSize(organization) && checkPictureInfo.doCheckPictureSize(openAccount) && checkPictureInfo.doCheckPictureSize(bill) && checkPictureInfo.doCheckPictureSize(operate))) {
                success = false;
                errorMsg = "上传图片大小不能超过10M";
            } else if (!"审核通过".equals(user.getVerifystatus())) {
                company.setUserid(user.getId());
                if (company.getId() == 0) {
                    String licensePath = fileService.uploadPictureToUploadDir(license);
                    String taxPath = fileService.uploadPictureToUploadDir(tax);
                    String organizationPath = fileService.uploadPictureToUploadDir(organization);
                    String openAccountPath = fileService.uploadPictureToUploadDir(openAccount);
                    String billPath = null;
                    String operatePath = null;
                    company.setBusinesslicense(licensePath);
                    company.setIdentificationnumber(taxPath);
                    company.setOrganizationcode(organizationPath);
                    company.setOpeninglicense(openAccountPath);
                    if (bill != null && bill.getSize() > 0) {
                        billPath = fileService.uploadPictureToUploadDir(bill);
                        company.setInvoicinginformation(billPath);
                    }
                    if (operate != null && operate.getSize() > 0) {
                        operatePath = fileService.uploadPictureToUploadDir(operate);
                        company.setOperatinglicense(operatePath);
                    }
                    companyMapper.addCompany(company);
                } else {
                    Company com = companyMapper.getCompanyById(company.getId());

                    String licensePath = null;
                    String taxPath = null;
                    String organizationPath = null;
                    String openAccountPath = null;
                    String billPath = null;
                    String operatePath = null;
                    if (license != null && license.getSize() > 0) {
                        licensePath = fileService.uploadPictureToUploadDir(license);
                    } else {
                        licensePath = com.getBusinesslicense();
                    }
                    if (tax != null && tax.getSize() > 0) {
                        taxPath = fileService.uploadPictureToUploadDir(tax);
                    } else {
                        taxPath = com.getIdentificationnumber();
                    }
                    if (organization != null && organization.getSize() > 0) {
                        organizationPath = fileService.uploadPictureToUploadDir(organization);
                    } else {
                        organizationPath = com.getOrganizationcode();
                    }
                    if (openAccount != null && openAccount.getSize() > 0) {
                        openAccountPath = fileService.uploadPictureToUploadDir(openAccount);
                    } else {
                        openAccountPath = com.getOpeninglicense();
                    }
                    if (bill != null && bill.getSize() > 0) {
                        billPath = fileService.uploadPictureToUploadDir(bill);
                    } else {
                        if (StringUtils.isBlank(isdeleteBill)) {
                            billPath = com.getInvoicinginformation();
                        } else {
                            billPath = null;
                        }
                    }
                    if (operate != null && operate.getSize() > 0) {
                        operatePath = fileService.uploadPictureToUploadDir(operate);
                    } else {
                        if (StringUtils.isBlank(isdeleteOperate)) {
                            operatePath = com.getOperatinglicense();
                        } else {
                            operatePath = null;
                        }
                    }
                    company.setBusinesslicense(licensePath);
                    company.setIdentificationnumber(taxPath);
                    company.setOrganizationcode(organizationPath);
                    company.setOpeninglicense(openAccountPath);
                    company.setInvoicinginformation(billPath);
                    company.setOperatinglicense(operatePath);
                    companyMapper.modifyCompany(company);
                }
                companyMapper.addCompVerify(new CompanyVerify("待审核", LocalDateTime.now(), companyMapper.getIdByUserid(user.getId()), user.getId()));
                companyMapper.setCompanyStatus("待审核", null, companyMapper.getIdByUserid(user.getId()));
                userMapper.setUserVerifyStatus("待审核", null, user.getId());
                MessageNotice.SubmitCompany.noticeUser(user.getSecurephone());
            }
        }
        map.put("success", success);
        map.put("errorMsg", errorMsg);
        return map;
    }

    /**************************************************** 保存公司信息 end ***********************************************/
}
