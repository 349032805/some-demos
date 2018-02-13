package kitt.site.controller;

import kitt.core.domain.*;
import kitt.core.persistence.UserMapper;
import kitt.core.persistence.ValidMapper;
import kitt.core.service.CODE;

import kitt.core.service.MySupplyerService;

import kitt.core.service.CreateImageCode;

import kitt.core.service.SMS;
import kitt.core.util.AuthMethod;
import kitt.core.util.check.CheckPhoneEmail;
import kitt.ext.WithLogger;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.Client;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.Auth;
import kitt.core.service.MailService;
import kitt.site.basic.exception.BusinessException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by joe on 10/26/14.
 */
@Controller
public class AuthController extends JsonController implements WithLogger {
    @Autowired
    protected Auth auth;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected SMS sms;
    @Autowired
    protected ValidMapper validMapper;
    @Autowired
    protected CODE code;
    @Autowired
    protected MailService mailService;
    @Autowired
    private CheckPhoneEmail checkPhoneEmail;
    @Autowired
    private MySupplyerService mySupplyerService;
    @Autowired
    private AuthMethod authMethod;


    public static class PhoneForm {
        @Size(min = 11, max = 11, message = "手机号长度11位")
        @NotNull
        protected String securephone;

        public String getSecurephone() {
            return securephone;
        }

        public void setSecurephone(String securephone) {
            this.securephone = securephone;
        }
    }

    public static class LoginForm {
        @Size(min = 11, max = 11, message = "手机号长度11位")
        @NotEmpty
        protected String securephone;

        public String getSecurephone() {
            return securephone;
        }

        public void setSecurephone(String securephone) {
            this.securephone = securephone;
        }

        @Size(min = 6, max = 16, message = "密码长度为6-16位")
        @NotEmpty
        protected String password;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class ValidCodeForm extends PhoneForm {
        @Size(min = 6, max = 6, message = "验证码长度为6位")
        @NotNull(message = "验证码不能为空")
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }
    }

    public static class ResetAccountPasswordForm {
        private String password;
        @Size(min = 6, max = 16, message = "密码长度为6-16位")
        @NotNull(message = "新密码不能为空")
        private String newpassword;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getNewpassword() {
            return newpassword;
        }

        public void setNewpassword(String newpassword) {
            this.newpassword = newpassword;
        }
    }

    public static class ResetNicknameForm extends PhoneForm {
        @Size(min = 1, max = 20, message = "昵称长度不能超过20位")
        @NotNull(message = "昵称不能为空")
        private String nickname;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    //发送激活邮件
    @RequestMapping(value = "/sendMail", method = RequestMethod.POST)
    @ResponseBody
    public Object doSendEmail(@RequestParam("email") String email) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = true;
        String errorMsg = null;
        if (checkPhoneEmail.doCheckEmailMethod(email)) {
            User user = userMapper.getUserByEmail(email);
            if (user == null) {
                String uuid = UUID.randomUUID().toString();
                //记录邮箱地址,激活码uuid和发送邮件的时间,以确认是否超过失效时间
                userMapper.modifyEmailAndSendMailTime(email, uuid, session.getUser().getId());
                //发送html邮件
                mailService.sendHtmlMail(email, uuid);
            } else {
                errorMsg = "邮箱地址已存在";
                success = false;
            }
        } else {
            errorMsg = "邮箱地址错误";
            success = false;
        }
        map.put("success", success);
        map.put("errorMsg", errorMsg);
        return map;
    }

    //测试激活邮件模板
    @RequestMapping("/testEmail")
    public String testEmail() {
        return "email/email";
    }

    //完成激活
    @RequestMapping("/validateMail")
    public Object validateMail(@RequestParam("uuid") String uuid, Map<String, Object> model) {
        String flag = "success";
        User user = userMapper.getUserByVerifyuuid(uuid);
        if (user != null) {
            if (user.getVerifymailtime() == null) {
                //检查是否超过有效期
                if (user.getSendmailtime().plusHours(2).isAfter(LocalDateTime.now())) {
                    if (session.getUser() != null) {
                        session.getUser().setEmail(user.getEmail());
                    }
                    //保存邮箱,记录激活时间
                    userMapper.modifyVerifyMailTime(user.getEmail());
                } else {
                    flag = "overtime";
                }
            } else {
                //已激活过
                flag = "validated";
            }
        } else {
            //无效链接
            flag = "invalid";
        }
        model.put("flag", flag);
        return "validateMail";
    }

    /**
     * 验证邮箱是否存在并且发送邮箱验证码
     * @param email           邮箱对象
     */
    @RequestMapping("/validateEmailAndSendCode")
    @ResponseBody
    public Object validateEmailAndPicCode(@Client ClientInfo clientInfo,
                                          @RequestParam(value = "email", required = true)String email) {
        User user = userMapper.getUserByEmail(email);
        if (user == null) {
            return new Object(){
                public boolean success = false;
                public String error = "邮箱不存在";
            };
        } else {
            return authMethod.doSendEmailCode(auth.getSessionUserId(), email, null, clientInfo.getIP());
        }
    }

    //更换邮箱,判断是否是用户最新的邮箱
    @RequestMapping(value = "/exchangeEmail", method = RequestMethod.POST)
    @ResponseBody
    public Object exchangeEmail(@RequestParam("email") String email) {
        boolean success = true;
        User user = userMapper.getUserById(session.getUser().getId());
        if (!email.equals(user.getEmail())) {
            success = false;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", success);
        return map;
    }

    //更换手机号,判断是否是用户最新的手机号
    @RequestMapping(value = "/exchangePhone", method = RequestMethod.POST)
    @ResponseBody
    public Object exchangePhone(@RequestParam("securephone") String securephone) {
        boolean success = true;
        User user = userMapper.getUserById(session.getUser().getId());
        if (!securephone.equals(user.getSecurephone())) {
            success = false;
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("success", success);
        return map;
    }

    //验证邮箱验证码
    @RequestMapping(value = "/validateMailCode", method = RequestMethod.POST)
    @ResponseBody
    public Object validateMailCode(@RequestParam(value = "email", required = true)String email,
                                   @RequestParam(value = "mailCode", required = true) String mailCode) {
        Phonevalidator phonevalidator = validMapper.findVerifyCode(email, mailCode, null);
        return authMethod.doCheckEMailCode(email, false, phonevalidator);
    }

    @RequestMapping("/logout")
    public String logout() {
        session.logout();
        return "redirect:/login";
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET})
    // @SecureToken
    public String login(String securephone, String t, Map<String, Object> model, HttpServletRequest request, String f) {
        Cookie cookies[] = request.getCookies();
        String cookieValue = null;
        if (null != cookies) {
            for (int i = 0; i < cookies.length; i++) {
                if ("cookieName".equals(cookies[i].getName())) {
                    cookieValue = cookies[i].getValue();
                    model.put("cookieName", cookieValue);
                    break;
                }
            }
        }
        if (!StringUtils.isBlank(securephone))
            model.put("securephone", securephone);
        if (!StringUtils.isBlank(t))
            model.put("t", t);
        if (!StringUtils.isBlank(f)) {
            model.put("urlFlag", f);
        }
        return "login";
    }

    @RequestMapping(value = "/login", method = {RequestMethod.POST})
    @ResponseBody
    //  @SecureToken
    public Object doLogin(@Valid LoginForm loginForm,
                          @Client ClientInfo clientInfo,
                          BindingResult bindingResult,
                          HttpServletResponse response,
                          boolean flag) {
        if (bindingResult.hasErrors()) {
            return json(bindingResult);
        }
        String securephone = loginForm.securephone;
        String password = DigestUtils.md5Hex(loginForm.password);
        User user = userMapper.getUserByPhone(securephone);
        if (user == null) {
            user = userMapper.getUserByEmail(securephone);
        }
        if (user != null) {
            if (!user.isIsactive()) {
                bindingResult.rejectValue("securephone", "loginerror", "此账号被禁用");
            } else {
                if (user.getPassword().equals(password)) {
                    auth.login(user);
                    Userlogin userlogin = new Userlogin(user.getId(), LocalDateTime.now(), clientInfo.getIP(), null, clientInfo.getUserAgent(), clientInfo.getAcceptLanguage());
                    userMapper.addUserlogin(userlogin); //新增用户登陆记录
                    if (flag) {
                        auth.setCookie("cookieName", securephone, response);
                    } else {
                        auth.removeCookie("cookieName", response);
                    }
                } else {
                    bindingResult.rejectValue("securephone", "loginerror", "用户名或密码错误");
                }
            }
        } else {
            bindingResult.rejectValue("securephone", "loginerror", "用户名或密码错误");
        }
        mySupplyerService.registerValidateSupplyer(securephone);
        return json(bindingResult);
    }

    //发送邮箱验证码
    @RequestMapping(value = "/sendMailValidateCode", method = RequestMethod.POST)
    @ResponseBody
    public Object sendMailValidateCode(@Client ClientInfo clientInfo,
                                       @RequestParam("email") String email) throws Exception {
        return authMethod.doSendEmailCode(auth.getSessionUserId(), email, null, clientInfo.getIP());
    }

    //跳转填写验证码页面
    @RequestMapping("/verifyAccount")
    public String verifyAccount(Map<String, Object> model,@RequestParam("securephone") String securephone){
        model.put("securephone", securephone);
        return "verifyAccount";
    }

    //填写验证码
    @RequestMapping(value = "/validcode", method = RequestMethod.POST)
    @ResponseBody
    public Object validCode(@Valid ValidCodeForm validCodeForm,BindingResult bindingResult){
        Phonevalidator phonevalidator = validMapper.findVerifyCode(validCodeForm.securephone, validCodeForm.code, ValidateType.forgetPassword);
      if (!checkPhoneEmail.doCheckMobilePhoneNumMethod(validCodeForm.securephone)) {
          bindingResult.rejectValue("code", "codeerror", "请输入正确的手机号");
      } else if (phonevalidator == null) {
          bindingResult.rejectValue("code", "codeerror", "验证码输入错误");
      } else if (phonevalidator.getExpiretime().isBefore(LocalDateTime.now())) {
          bindingResult.rejectValue("code", "codeerror", "验证码已过期");
      } else  {
          validMapper.modifyValidatedAndTime(validCodeForm.securephone, validCodeForm.code);
      }
      return json(bindingResult);
    }

    //跳转修改密码页面
    @RequestMapping("/modifypassword")
    public String modifyPassword(Map<String, Object> model,@RequestParam("securephone") String securephone){
        model.put("securephone", securephone);
        return "modifyPassword";
    }

    //修改密码
    @RequestMapping(value = "/modifyPasswd", method = RequestMethod.POST)
    @ResponseBody
    public Object modifyPasswd(@RequestParam(value = "securephone", required = false) String securephone,
                               @RequestParam(value = "email", required = false) String email,
                               @RequestParam(value = "password", required = true) String password){
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        if((!StringUtils.isBlank(securephone) && DigestUtils.md5Hex(password).equals(userMapper.getUserByPhone(securephone).getPassword())) || (!StringUtils.isBlank(email) && DigestUtils.md5Hex(password).equals(userMapper.getUserByEmail(email).getPassword()))){
            map.put("error", "新密码和原密码一样");
            map.put("errortype", "newpassword");
        } else if(securephone != null) {
            success = userMapper.modifyPasswdByPhone(DigestUtils.md5Hex(password), securephone) == 1;
        } else if(email != null) {
            success = userMapper.modifyPasswdByEmail(DigestUtils.md5Hex(password), email) == 1;
        }
        map.put("success", success);
        return map;
    }

    //修改昵称
    @RequestMapping(value = "/modifynickname", method = RequestMethod.POST)
    @ResponseBody
    public Object modifyNickname(@Valid ResetNicknameForm resetNicknameForm,BindingResult bindingResult){
        if (!bindingResult.hasErrors()) {
            userMapper.modifyNickname(resetNicknameForm.nickname, resetNicknameForm.securephone);
            session.getUser().setNickname(resetNicknameForm.nickname);
        } else {
            bindingResult.rejectValue("nickname", "nicknameerror");
        }
        return json(bindingResult);
    }

    //修改账户密码
    @RequestMapping(value = "/modifyAccountPasswd", method = RequestMethod.POST)
    @ResponseBody
    public Object modifyAccountPasswd(@Valid ResetAccountPasswordForm resetAccountPasswordForm,BindingResult bindingResult){
       //从数据库取密码,不从session中取,避免浏览器重复登录修改密码通过.
        User u = userMapper.getUserById(session.getUser().getId());
        if (!DigestUtils.md5Hex(resetAccountPasswordForm.password).equals(u.getPassword())) {
            bindingResult.rejectValue("password", "passworderror", "原密码错误");
        } else {
            userMapper.modifyAccountPasswd(DigestUtils.md5Hex(resetAccountPasswordForm.newpassword), session.getUser().getId());
            session.getUser().setPassword(DigestUtils.md5Hex(resetAccountPasswordForm.newpassword));
        }
        return json(bindingResult);
    }

    //email修改账户手机号
    @RequestMapping(value = "/modifyPhoneByEmail", method = RequestMethod.POST)
    @ResponseBody
    public Object modifyPhoneByEmail(@RequestParam(value = "newSecurePhone", required = true) String newSecurePhone){
        if (userMapper.modifyPhone(newSecurePhone,session.getUser().getId()) == 1) {
            session.getUser().setSecurephone(newSecurePhone);
            return true;
        } else {
            return false;
        }
    }

    //修改email
    @RequestMapping(value = "/modifyEmail", method = RequestMethod.POST)
    @ResponseBody
    public Object modifyEmail(@RequestParam(value = "newEmail", required = true) String newEmail){
        if(userMapper.modifyEmail(newEmail,session.getUser().getId()) == 1){
            session.getUser().setEmail(newEmail);
            return true;
        } else {
            return false;
        }
    }

    /************************************************** 注册 start *****************************************************/

    /**
     * 跳转到注册界面
     * @param userFrom             来源
     */
    @RequestMapping("/register")
    public String register(Map<String, Object> model, @RequestParam(value = "f", required = false) String userFrom,HttpServletResponse response) {
        response.addHeader("Cache-Control", "no-store, must-revalidate");
        response.addHeader("Expires", "Thu, 01 Jan 1970 00:00:01 GMT");
        if (userFrom != null) {
            model.put("urlFlag", userFrom);
        }
        return "register";
    }

    /**
     * 检查手机号是否存在
     * @param securephone          手机号
     */
    @RequestMapping(value = "/userCheck", method = RequestMethod.POST)
    @ResponseBody
    public Object doUserCheck(@Client ClientInfo clientInfo,
                              @RequestParam(value = "securephone", required = true)String securephone,
                              @RequestParam(value = "codetype", required = true)int codetype) {
        return authMethod.doCheckUserPhone(securephone, codetype, false, clientInfo.getIP());
    }

    /**
     * 给手机发送验证码
     * @param securephone          手机号
     * @param imagecode            图片验证码
     */
    @RequestMapping(value = "/register/sendSmsCode", method = RequestMethod.POST)
    @ResponseBody
    public Object doSendRegisterSmsCode(@Client ClientInfo clientInfo,
                                        @RequestParam(value = "securephone", required = true) String securephone,
                                        @RequestParam(value = "imagecode", required = false, defaultValue = "") String imagecode) throws Exception {
        if (!auth.doCheckImageCodeTimesAndRight(securephone, imagecode, clientInfo.getIP())) {
            return new Object(){
                public boolean success = false;
                public String error = "图片验证码错误";
                public String errortype = "imagecode";
            };
        }
        return authMethod.sendSMSCode(auth.getSessionUserId(), securephone, false, ValidateType.register, clientInfo.getIP());
    }

    /**
     * 注册提交
     * @param securephone          手机号
     * @param password             密码
     * @param smscode              短信验证码
     * @param userFrom             用户来源
     */
    @RequestMapping(value = "/register", method = {RequestMethod.POST})
    @ResponseBody
    public MapObject doRegister(@RequestParam(value = "securephone", required = true)String securephone,
                                @RequestParam(value = "password", required = true)String password,
                                @RequestParam(value = "smscode", required = true)String smscode,
                                @RequestParam(value = "userForm", required = false)String userFrom) throws Exception {
        Phonevalidator phonevalidator = validMapper.findVerifyCode(securephone, smscode, ValidateType.register);
        MapObject map =  authMethod.doCheckCode(securephone, false, phonevalidator);
        if(map.isSuccess()){
            if(!auth.addUser(securephone, password, userFrom, 1)){
                map.setSuccess(false);
                map.setError("注册失败");
            }
            validMapper.modifyValidatedAndTime(securephone, smscode);
        }
        return map;
    }

    /**
     * 注册成功页面
     */
    @LoginRequired
    @RequestMapping("/registeredSuccess")
    public String doWelcome(Map<String, Object> model) {
        if (session != null && session.getUser() != null) {
            if (!StringUtils.isBlank(session.getUser().getSecurephone())) {
                model.put("securephone", session.getUser().getSecurephone());
            }
            if (!StringUtils.isBlank(session.getUser().getEmail())) {
                model.put("email", session.getUser().getEmail());
            }
            return "registeredSuccess";
        } else {
            return "/";
        }
    }

    /*************************************************** 注册 end *****************************************************/


    /*************************************************** 忘记密码 start ***********************************************/

    /**
     * 忘记密码-检查手机号 / 邮箱 是否存在
     * @param securephone            手机号 / 邮箱
     * @param type                   0: 手机号, 1: 邮箱
     */
    @RequestMapping(value = "/forgetpwd/checkphone", method = RequestMethod.POST)
    @ResponseBody
    public Object doCheckResetPasswordPhone(@Client ClientInfo clientInfo,
                                            @RequestParam(value = "securephone", required = true) String securephone,
                                            @RequestParam(value = "type", required = true)int type,
                                            @RequestParam(value = "codetype", required = true)int codetype) {
        if (type != 0 && type != 1) throw new NotFoundException(EnumRemindInfo.Site_System_Error.value());
        if (type == 0) {
            return authMethod.doCheckUserPhone(securephone, codetype, true, clientInfo.getIP());
        } else {
            return authMethod.doCheckEmail(securephone, true);
        }
    }

    /**
     * 忘记密码-发送短信验证码 / 邮箱验证码
     * @param securephone            手机号 / 邮箱
     * @param imagecode              图片验证码
     * @param type                   0: 手机号, 1: 邮箱
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
                public String errortype = "imagecode";
            };
        }
        if (type == 0) {
            return authMethod.sendSMSCode(auth.getSessionUserId(), securephone, true, ValidateType.forgetPassword, clientInfo.getIP());
        } else {
            return authMethod.sendEMailCode(auth.getSessionUserId(), securephone, true, ValidateType.forgetPassword, clientInfo.getIP());
        }
    }

    /**
     * 忘记密码 - 检查验证码
     * @param securephone           手机号 / 邮箱
     * @param smscode               短信验证码
     * @param type                  0: 手机号, 1: 邮箱
     */
    @RequestMapping("/forgetpwd/checksmscode")
    @ResponseBody
    public Object doCheckResetPasswordSmsCode(@RequestParam(value = "securephone", required = true)String securephone,
                                              @RequestParam(value = "smscode", required = true)String smscode,
                                              @RequestParam(value = "type", required = true)int type){
        if (type != 0 && type != 1) throw new BusinessException("系统出错, 请刷新页面重试!");
        Phonevalidator phonevalidator = validMapper.findVerifyCode(securephone, smscode, ValidateType.forgetPassword);
        if (type == 0) {
            return authMethod.doCheckCode(securephone, true, phonevalidator);
        } else {
            return authMethod.doCheckEMailCode(securephone, true, phonevalidator);
        }
    }


    /**
     * 忘记密码-下一步 跳转页面
     */
    @RequestMapping("/reset-password")
    public String resetPassword(HttpServletResponse response) {
        response.addHeader("Cache-Control", "no-store, must-revalidate");
        response.addHeader("Expires", "Thu, 01 Jan 1970 00:00:01 GMT");
        return "reset-password";
    }

    /*************************************************** 忘记密码 end **************************************************/


    /*************************************************** 修改手机号 start **********************************************/

    /**
     * 修改手机号 - 发送验证码
     * @param imagecode                图片验证码
     * @param type                     0: 手机号, 1: 邮箱
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
                public String errortype = "imagecode";
            };
        }
        if (type == 0) {
            Map<String, Object> map = authMethod.sendSMSCode(user.getId(), user.getSecurephone(), true, ValidateType.updatePhoneNum, clientInfo.getIP());
            map.put("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(user.getSecurephone(), clientInfo.getIP()));
            return map;
        } else {
            return authMethod.sendEMailCode(auth.getSessionUserId(), user.getEmail(), true, ValidateType.updatePhoneNum, clientInfo.getIP());
        }
    }

    /**
     * 修改手机号-检查验证码
     * @param smscode                   短信验证码
     * @param type                      0: 手机号, 1: 邮箱
     */
    @RequestMapping(value = "/changephone/checksmscode")
    @ResponseBody
    @LoginRequired
    public Object doCheckChangePhoneSMSCode(@RequestParam(value = "smscode", required = true)String smscode,
                                            @RequestParam(value = "type", required = true)int type) {
        if(type != 0 && type != 1) throw new BusinessException("系统出错, 请刷新页面重试!");
        User user = auth.getLatestUserBySession(session.getUser().getId());
        if (type == 0) {
            Phonevalidator phonevalidator = validMapper.findVerifyCode(user.getSecurephone(), smscode, ValidateType.updatePhoneNum);
            return authMethod.doCheckCode(user.getSecurephone(), true, phonevalidator);
        } else {
            Phonevalidator phonevalidator = validMapper.findVerifyCode(user.getEmail(), smscode, ValidateType.updatePhoneNum);
            return authMethod.doCheckEMailCode(user.getEmail(), true, phonevalidator);
        }
    }

    /**
     * 修改手机号 - 检查新手机号
     * @param securephone               手机号
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
     * @param securephone                手机号
     * @param imagecode                  图片验证码
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
                public String errortype = "imagecode";
            };
        }
        return authMethod.sendSMSCode(auth.getSessionUserId(), securephone, false, ValidateType.updatePhoneNum, clientInfo.getIP());
    }

    /**
     * 确认修改手机号 - 提交
     * @param securephone                手机号
     * @param smscode                    短信验证码
     */
    @RequestMapping(value = "/changephone/submitchange")
    @ResponseBody
    @LoginRequired
    public Object doChangePhone(@RequestParam(value = "securephone", required = true)String securephone,
                                @RequestParam(value = "smscode", required = true)String smscode){
        Phonevalidator phonevalidator =  validMapper.findVerifyCode(securephone, smscode,ValidateType.updatePhoneNum);
        MapObject map = authMethod.doCheckCode(securephone, false, phonevalidator);
        if(map.isSuccess()) {
            if (userMapper.modifyPhone(securephone, session.getUser().getId()) != 1) {
                map.setSuccess(false);
                map.setError("手机号修改失败,请刷新页面重试!");
            } else {
                session.getUser().setSecurephone(securephone);
            }
            validMapper.modifyValidatedAndTime(securephone, smscode);
        }
        return  map;
    }

    /*************************************************** 修改手机号 end ************************************************/


    /*************************************************** 修改密码 start ************************************************/

    /**
     * 修改密码 - 发送验证码
     * @param imagecode                图片验证码
     * @param type                     0: 手机号, 1: 邮箱
     */
    @RequestMapping(value = "/changepwd/sendcode")
    @ResponseBody
    @LoginRequired
    public Object doSendChangePWDSMSCode(@Client ClientInfo clientInfo,
                                         @RequestParam(value = "imagecode", required = false, defaultValue = "")String imagecode,
                                         @RequestParam(value = "type", required = true)int type) throws Exception {
        if (type != 0 && type != 1) throw new BusinessException("系统出错, 请刷新页面重试!");
        User user = auth.getLatestUserBySession(session.getUser().getId());
        if (!auth.doCheckImageCodeTimesAndRight(user.getSecurephone(), imagecode, clientInfo.getIP())) {
            return new Object(){
                public boolean success = false;
                public String error = "图片验证码错误";
                public String errortype = "imagecode";
            };
        }
        if (type == 0) {
            Map<String, Object> map = authMethod.sendSMSCode(user.getId(), user.getSecurephone(), true, ValidateType.resetpassword, clientInfo.getIP());
            map.put("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(session.getUser().getSecurephone(), clientInfo.getIP()));
            return map;
        } else {
            return authMethod.sendEMailCode(user.getId(), user.getEmail(), true, ValidateType.resetpassword, clientInfo.getIP());
        }
    }

    /**
     * 修改密码 - 检查验证码, 提交
     * @param password                  原密码
     * @param newpassword               新密码
     * @param smscode                   短信验证码
     * @param type                      0: 手机号修改, 1:邮箱修改
     */
    @RequestMapping(value = "/changepwd/checkcodesubmit")
    @ResponseBody
    @LoginRequired
    public Object doCheckChangePWDSMSCode(@RequestParam(value = "password", required = true)String password,
                                          @RequestParam(value = "newpassword", required = true)String newpassword,
                                          @RequestParam(value = "smscode", required = true)String smscode,
                                          @RequestParam(value = "type", required = true)int type){
        User user = auth.getLatestUserBySession(session.getUser().getId());
        MapObject map = new MapObject();
        if (type != 0 && type != 1) throw new BusinessException("系统出错, 请刷新页面重试!");
        if (!user.getPassword().equals(DigestUtils.md5Hex(password))){
            map.setSuccess(false);
            map.setError("原密码输入错误");
            map.setErrortype("oldpassword");
        } else if(password.equals(newpassword)){
            map.setSuccess(false);
            map.setError("新密码和原密码一样");
            map.setErrortype("newpassword");
        } else {
            if(type == 0) {
                Phonevalidator phonevalidator = validMapper.findVerifyCode(user.getSecurephone(), smscode, ValidateType.resetpassword);
                map = authMethod.doCheckCode(user.getSecurephone(), true, phonevalidator);
            } else if (type == 1) {
                Phonevalidator phonevalidator = validMapper.findVerifyCode(user.getEmail(), smscode, ValidateType.resetpassword);
                map =  authMethod.doCheckEMailCode(user.getEmail(), true, phonevalidator);
            }
            if(map.isSuccess() && userMapper.modifyPasswdById(user.getId(), DigestUtils.md5Hex(newpassword)) != 1) {
                map.setSuccess(false);
                map.setError("修改失败,请刷新页面重试");
            }
        }
        return map;
    }


    /*************************************************** 修改密码 end **************************************************/


    /*************************************************** 更换邮箱 start*************************************************/

    /**
     * 更换邮箱 - 发送验证码
     * @param imagecode                图片验证码
     * @param type                     0: 手机号接收验证码, 1:邮箱接收验证码
     */
    @RequestMapping(value = "/changeemail/sendcode")
    @ResponseBody
    @LoginRequired
    public Object doSendChangeEmailSMSCode(@Client ClientInfo clientInfo,
                                           @RequestParam(value = "imagecode", required = false, defaultValue = "")String imagecode,
                                           @RequestParam(value = "type", required = true)int type) throws Exception {
        if (type != 0 && type != 1) throw new NotFoundException("系统出错, 请刷新页面重试!");
        User user = auth.getLatestUserBySession(session.getUser().getId());
        if (!auth.doCheckImageCodeTimesAndRight(user.getSecurephone(), imagecode, clientInfo.getIP())) {
            return new Object(){
                public boolean success = false;
                public String error = "图片验证码错误";
                public String errortype = "imagecode";
            };
        }
        if(type == 0) {
            Map<String, Object> map = authMethod.sendSMSCode(user.getId(), user.getSecurephone(), true, ValidateType.changeemail, clientInfo.getIP());
            map.put("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(user.getSecurephone(), clientInfo.getIP()));
            return map;
        } else {
            return authMethod.sendEMailCode(user.getId(), user.getEmail(), true, ValidateType.changeemail, clientInfo.getIP());
        }
    }

    /**
     * 更换邮箱 - 检查验证码
     * @param smscode                 短信验证码/邮箱验证码
     * @param type                    0: 手机号验证, 1:邮箱验证
     */
    @RequestMapping(value = "/changeemail/checkcode")
    @ResponseBody
    @LoginRequired
    public Object doChangeEmailByPhone(@RequestParam(value = "smscode", required = true)String smscode,
                                       @RequestParam(value = "type", required = true)int type){
        User user = auth.getLatestUserBySession(session.getUser().getId());
        if (type == 0) {
            Phonevalidator phonevalidator = validMapper.findVerifyCode(user.getSecurephone(), smscode, ValidateType.changeemail);
            return authMethod.doCheckCode(user.getSecurephone(), true, phonevalidator);
        } else {
            Phonevalidator phonevalidator = validMapper.findVerifyCode(user.getEmail(), smscode, ValidateType.changeemail);
            return authMethod.doCheckEMailCode(user.getEmail(), true, phonevalidator);
        }
    }

    /**
     * 检查新邮箱格式是否正确及是否已经被绑定
     * @param email                   邮箱
     */
    @RequestMapping(value = "/changeemail/checknewemail")
    @ResponseBody
    @LoginRequired
    public Object doChangeEmailCheckNewEmail(@RequestParam(value = "email", required = true)String email){
        return authMethod.doCheckEmail(email, false);
    }

    /**
     * 更换邮箱 - 发送新邮箱验证码
     * @param email                   邮箱
     */
    @RequestMapping(value = "/changeemail/sendnewmailcode")
    @ResponseBody
    @LoginRequired
    public Object doSendEmailCodeNewEmail(@Client ClientInfo clientInfo,
                                          @RequestParam(value = "email", required = true)String email){
        return authMethod.sendEMailCode(auth.getSessionUserId(), email, false, ValidateType.changeemail, clientInfo.getIP());
    }

    /**
     * 更换邮箱 - 提交更换操作
     * @param mailcode                 邮箱验证码
     * @param email                    邮箱
     */
    @RequestMapping(value = "/changeemail/change")
    @ResponseBody
    @LoginRequired
    public Object doChangeEmailByPhone(@RequestParam(value = "mailcode", required = true)String mailcode,
                                       @RequestParam(value = "email", required = true)String email){
        Phonevalidator phonevalidator = validMapper.findVerifyCode(email, mailcode, ValidateType.changeemail);
        MapObject map = authMethod.doCheckEMailCode(email, false, phonevalidator);
        if(map.isSuccess()){
            if (userMapper.modifyEmail(email, session.getUser().getId()) == 1) {
                session.getUser().setEmail(email);
                return map;
            } else {
                map.setSuccess(false);
                map.setError("更改失败,请刷新页面重试");
                return map;
            }
        } else {
            return map;
        }

    }


    /*************************************************** 更换邮箱 end***************************************************/


    /*************************************************** 生成图片验证码  start ******************************************/

    /**
     * 生成图片验证码
     * @param date              时间戳 - 时间转成Long型
     * @param type              类型: 1,2,3,4 整形
     */
    @RequestMapping(value="/verifyCode",method= RequestMethod.GET)
    public void service(@RequestParam(value = "date", required = false)String date,
                        @RequestParam(value = "type", required = false, defaultValue = "1")int type,
                        HttpServletResponse resp) throws ServletException, java.io.IOException {
        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);
        resp.setContentType("image/jpeg");
        StringBuffer randomCode = CreateImageCode.getRandomImageCode();           //生成4位随机字符
        session.setPicCode(randomCode.toString());
        // 将图像输出到Servlet输出流中。
        ServletOutputStream sos = resp.getOutputStream();
        Long dateNum = 0L;
        if (!StringUtils.isBlank(date)) {
            try {
                dateNum = Long.valueOf(date);
            } catch (NumberFormatException e) {
                throw new BusinessException(EnumRemindInfo.Site_System_Error.value());
            }
        }
        ImageIO.write(CreateImageCode.createImageCode(dateNum, type, randomCode.toString()), "jpeg", sos);
        sos.close();
    }

    /*************************************************** 生成图片验证码  end ********************************************/

}
