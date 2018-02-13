package kitt.core.util;

import kitt.core.domain.*;
import kitt.core.persistence.UserMapper;
import kitt.core.persistence.ValidMapper;
import kitt.core.service.CODE;
import kitt.core.service.MailService;
import kitt.core.service.VerifyCodeService;
import kitt.core.util.check.CheckPhoneEmail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuxinjie on 16/1/16.
 */
@Service
public class AuthMethod {
    @Autowired
    private ValidMapper validMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CheckPhoneEmail checkPhoneEmail;
    @Autowired
    private CODE code;
    @Autowired
    private MailService mailService;
    @Autowired
    private VerifyCodeService verifyCodeService;
    @Autowired
    private ToolsMethod toolsMethod;

    /**
     * 检查邮箱是否正确
     */
    public Object doCheckEmail(String email, boolean exist){
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        String error = "";
        String errortype = "username";
        if (!checkPhoneEmail.doCheckEmailMethod(email)) {
            errortype = "email";
            error = "请输入正确的邮箱";
        } else if(exist && userMapper.getUserByEmail(email) == null) {
            errortype = "email";
            error = "此邮箱尚未注册";
        } else if(!exist && userMapper.getUserByEmail(email) != null) {
            error = "此邮箱已经绑定其它账号";
        } else {
            errortype = "";
            success = true;
        }
        map.put("success", success);
        map.put("error", error);
        map.put("errortype", errortype);
        return map;
    }

    /**
     * 检查手机号是否正确, 并检查手机号是否注册
     * @param phone             手机号
     * @param codetype          类型, 最后提交时值为1, 其它地方值为0
     * @param IP                IP地址
     */
    public Object doCheckUserPhone(String phone, int codetype, boolean exist, String IP){
        Map<String, Object> map = new HashMap<String, Object>();
        if (codetype != 0 && codetype != 1) {
            return toolsMethod.setMapMessage(map, false, "username", EnumRemindInfo.Site_System_Error.value());
        }
        boolean success = false;
        String error = "";
        String errortype = "username";
        if (!checkPhoneEmail.doCheckMobilePhoneNumMethod(phone)) {
            error = "请输入正确的手机号";
        } else if (!exist && !checkUser(phone)) {
            error = "此手机号已经被注册";
        } else if (exist && checkUser(phone)){
            error = "此手机号尚未注册";
        } else if ((countTodaySMSCodeTimesByPhone(phone) - codetype) >= 30) {
            errortype = "username";
            error = "此手机号今天短信发送已达到最大条数,请明天再尝试!";
        } else if ((countTodaySMSCodeTimesByIP(IP) - codetype) >= 30) {
            errortype = "username";
            error = "该IP地址今天短信发送已达到最大条数,请明天再尝试!";
        } else {
            success = true;
            errortype = "";
            map.put("times", checkTodaySMSCodeTimesByPhoneAndIP(phone, IP));
        }
        map.put("success", success);
        map.put("error", error);
        map.put("errortype", errortype);
        return map;
    }

    /**
     * 检查手机号是否已经注册
     * @param securephone        手机号
     * @return                   true: 没有被注册, false: 手机号已经被注册
     */
    public boolean checkUser(String securephone) {
        if (!StringUtils.isBlank(securephone)) {
            User user = userMapper.getUserByPhone(securephone);
            return user == null ? true : false;
        }
        return false;           //手机号已经被注册
    }

    /**
     * 检查手机号是否正确 不检查手机号是否注册
     * @param phone             手机号
     * @param codetype          类型, 最后提交时值为1, 其它地方值为0
     * @param IP                IP地址
     */
    public Object doCheckUserPhoneNoCheckPhoneExist(String phone, int codetype, String IP){
        Map<String, Object> map = new HashMap<String, Object>();
        if (codetype != 0 && codetype != 1) {
            return toolsMethod.setMapMessage(map, false, "username", EnumRemindInfo.Site_System_Error.value());
        }
        boolean success = false;
        String error = "";
        String errortype = "username";
        if (!checkPhoneEmail.doCheckMobilePhoneNumMethod(phone)) {
            error = "请输入正确的手机号";
        } else if ((countTodaySMSCodeTimesByPhone(phone) - codetype) >= 30) {
            errortype = "username";
            error = "此手机号今天短信发送已达到最大条数,请明天再尝试!";
        } else if ((countTodaySMSCodeTimesByIP(IP) - codetype) >= 30) {
            errortype = "username";
            error = "该IP地址今天短信发送已达到最大条数,请明天再尝试!";
        } else {
            success = true;
            errortype = "";
            map.put("times", checkTodaySMSCodeTimesByPhoneAndIP(phone, IP));
        }
        map.put("success", success);
        map.put("error", error);
        map.put("errortype", errortype);
        return map;
    }

    /**
     * 发送邮箱验证码
     * @param userId                  用户Id
     * @param email                   邮箱
     * @param exist                   是否已经绑定用户
     * @param validateType            验证码类型
     * @param IP                      IP地址
     */
    public Map<String, Object> sendEMailCode(int userId, String email, boolean exist, ValidateType validateType, String IP){
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        String error = "";
        String errortype = "username";
        if(!StringUtils.isBlank(email)) {
            if (!checkPhoneEmail.doCheckEmailMethod(email)) {
                error = "请输入正确的邮箱";
            } else if (exist && userMapper.getUserByEmail(email) == null) {
                error = "该邮箱尚未注册";
            } else if(!exist && userMapper.getUserByEmail(email) != null) {
                error = "此邮箱已经绑定其它账号";
            } else {
                errortype = "";
                success = doSendEmailCode(userId, email, validateType, IP);
            }
        }
        map.put("success", success);
        map.put("error", error);
        map.put("errortype", errortype);
        return map;
    }

    /**
     * 发送手机验证码, 并检查手机号是否已经注册
     * @param userId                  用户Id
     * @param phone                   手机号码
     * @param exist                   手机号是否已经注册
     * @param validateType            验证码类型
     * @param IP                      IP地址
     */
    public Map<String, Object> sendSMSCode(int userId, String phone, boolean exist, ValidateType validateType, String IP) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        String error = "";
        String errortype = "";
        if (!StringUtils.isBlank(phone)) {
            if (!checkPhoneEmail.doCheckMobilePhoneNumMethod(phone)) {
                errortype = "username";
                error = "请输入正确的手机号";
            } else if (countTodaySMSCodeTimesByPhone(phone) >= 30) {
                errortype = "username";
                error = "此手机号今天短信发送已达到最大条数,请明天再尝试!";
            } else if (countTodaySMSCodeTimesByIP(IP) >= 30) {
                errortype = "username";
                error = "该IP地址今天短信发送已达到最大条数,请明天再尝试!";
            } else if (exist && checkUser(phone)) {
                errortype = "username";
                error = "此手机号尚未注册";
            } else if (!exist && !checkUser(phone)) {
                errortype = "username";
                error = "此手机号已经被注册";
            } else {
                success = true;
                errortype = "";
                Map<String, Object> mapCode = verifyCodeService.generateVerifyCode(userId, phone, validateType, IP);
                if (!Boolean.valueOf(String.valueOf(mapCode.get("success")))) {
                    success = false;
                    error = String.valueOf(mapCode.get("error"));
                    errortype = "username";
                }
            }
        }
        map.put("success", success);
        map.put("error", error);
        map.put("errortype", errortype);
        return map;
    }

    /**
     * 发送手机验证码 - 不检查手机号是否已经注册
     * @param userId                  用户Id
     * @param phone                   手机号码
     * @param validateType            验证码类型
     * @param IP                      IP地址
     */
    public Map<String, Object> sendSMSCodeNOCheckPhoneExist(int userId, String phone, ValidateType validateType, String IP) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        String error = "";
        String errortype = "";
        if (phone != null) {
            if (!checkPhoneEmail.doCheckMobilePhoneNumMethod(phone)) {
                errortype = "username";
                error = "请输入正确的手机号";
            } else if (countTodaySMSCodeTimesByPhone(phone) >= 30) {
                errortype = "username";
                error = "此手机号今天短信发送已达到最大条数,请明天再尝试!";
            } else if (countTodaySMSCodeTimesByIP(IP) >= 30) {
                errortype = "username";
                error = "该IP地址今天短信发送已达到最大条数,请明天再尝试!";
            } else {
                success = true;
                errortype = "";
                Map<String, Object> mapCode = verifyCodeService.generateVerifyCode(userId, phone, validateType, IP);
                if (!Boolean.valueOf(String.valueOf(mapCode.get("success")))) {
                    success = false;
                    error = String.valueOf(mapCode.get("error"));
                    errortype = "username";
                }
            }
        }
        map.put("success", success);
        map.put("error", error);
        map.put("errortype", errortype);
        return map;
    }

    /**
     * 检查验证码是否正确
     * @param phone                 手机号
     * @param exist                 手机号是否已经注册
     * @param phonevalidator        手机验证码对象
     */
    public MapObject doCheckCode(String phone, boolean exist, Phonevalidator phonevalidator){
        MapObject map = new MapObject();
        boolean success = false;
        String error = "";
        String errortype = "";
        if (!checkPhoneEmail.doCheckMobilePhoneNumMethod(phone)) {
            error = "请输入正确的手机号";
            errortype = "username";
        } else if (!exist && userMapper.getUserByPhone(phone) != null) {
            error = "手机号已经被注册";
            errortype = "username";
        } else if (exist && userMapper.getUserByPhone(phone) == null) {
            error = "手机号尚未注册";
            errortype = "username";
        } else if (phonevalidator == null) {
            error = "短信验证码输入错误";
            errortype = "code";
        } else if (phonevalidator.getExpiretime().isBefore(LocalDateTime.now())) {
            error = "短信验证码已过期";
            errortype = "code";
        } else {
            validMapper.modifyValidatedAndTime(phone, phonevalidator.getCode());
            success = true;
            errortype = "";
        }
        map.setSuccess(success);
        map.setError(error);
        map.setErrortype(errortype);
        return map;
    }

    /**
     * 检查验证码是否正确
     * @param phone                 手机号
     * @param phonevalidator        手机验证码对象
     */
    public MapObject doCheckCodeNOCheckPhoneExist(String phone, Phonevalidator phonevalidator){
        MapObject map = new MapObject();
        boolean success = false;
        String error = "";
        String errortype = "";
        if (!checkPhoneEmail.doCheckMobilePhoneNumMethod(phone)) {
            error = "请输入正确的手机号";
            errortype = "username";
        } else if (phonevalidator == null) {
            error = "短信验证码输入错误";
            errortype = "code";
        } else if (phonevalidator.getExpiretime().isBefore(LocalDateTime.now())) {
            error = "短信验证码已过期";
            errortype = "code";
        } else {
            validMapper.modifyValidatedAndTime(phone, phonevalidator.getCode());
            success = true;
            errortype = "";
        }
        map.setSuccess(success);
        map.setError(error);
        map.setErrortype(errortype);
        return map;
    }

    /**
     * 发送邮件验证码
     */
    public boolean doSendEmailCode(int userId, String email, ValidateType validateType, String IP){
        String mailCode = code.CreateCode();
        mailService.sendSimpleMail(email, "来自易煤网的验证码", "您好,您的验证码为:" + mailCode + "(2个小时内有效). 【易煤网】");
        Phonevalidator phonevalidator = new Phonevalidator(userId, email, mailCode, LocalDateTime.now().plusHours(2), validateType, false, IP);
        return validMapper.addValid(phonevalidator) == 1;
    }

    /**
     * 检查邮箱验证码是否正确
     * @param email               邮箱
     * @param exist               此邮箱是否已经绑定用户
     * @param phonevalidator      验证码对象
     */
    public MapObject doCheckEMailCode(String email, boolean exist, Phonevalidator phonevalidator){
        MapObject map = new MapObject();
        boolean success = false;
        String error = "";
        String errortype = "";
        if (exist && userMapper.getUserByEmail(email) == null) {
            error = "该邮箱尚未注册";
            errortype = "username";
        } else if (!exist && userMapper.getUserByEmail(email) != null) {
            error = "该邮箱已经绑定其它用户";
            errortype = "username";
        } else if (phonevalidator == null) {
            error = "邮箱验证码输入错误";
            errortype = "code";
        } else if (phonevalidator.getExpiretime().isBefore(LocalDateTime.now())) {
            error = "邮箱验证码已过期";
            errortype = "code";
        } else {
            validMapper.modifyValidatedAndTime(email, phonevalidator.getCode());
            success = true;
            errortype = "";
        }
        map.setSuccess(success);
        map.setError(error);
        map.setErrortype(errortype);
        return map;
    }

    /**
     * 查询本手机号和本IP地址今天发送短信验证码的次数是否超过了5次
     * @param phone         手机号
     */
    public boolean checkTodaySMSCodeTimesByPhoneAndIP(String phone, String ip) {
        return validMapper.countTodaySMSCodeTimesByPhone(phone) < 5 && validMapper.countTodaySMSCodeTimesByIP(ip) < 5;
    }

    /**
     * 查询本IP地址今天发送短信验证码的次数
     * @param ip           ip地址
     */
    public int countTodaySMSCodeTimesByIP(String ip){
        return validMapper.countTodaySMSCodeTimesByIP(ip);
    }

    /**
     * 查询本手机号今天发送短信验证码的次数
     * @param phone        手机号
     */
    public int countTodaySMSCodeTimesByPhone(String phone){
        return validMapper.countTodaySMSCodeTimesByPhone(phone);
    }


}
