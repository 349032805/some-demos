package kitt.site.controller;

import kitt.core.domain.*;
import kitt.core.persistence.QuickTradeMapper;
import kitt.core.persistence.ValidMapper;
import kitt.core.util.AuthMethod;
import kitt.site.basic.annotation.Client;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by liuxinjie on 15/11/21.
 * 快速找货
 */
@RestController
public class QuickTradeController {
    @Autowired
    private ValidMapper validMapper;
    @Autowired
    private QuickTradeMapper quickTradeMapper;
    @Autowired
    private Auth auth;
    @Autowired
    private AuthMethod authMethod;

    /**
     * 检查手机号是否正确
     * @param securephone           手机号
     */
    @RequestMapping("/quicktrade/checkphone")
    public Object doCheckCooperationPhone(@Client ClientInfo clientInfo,
                                          @RequestParam(value = "securephone", required = true)String securephone,
                                          @RequestParam(value = "codetype", required = true)int codetype) {
        return authMethod.doCheckUserPhoneNoCheckPhoneExist(securephone, codetype, clientInfo.getIP());
    }

    /**
     * 给客户手机发送验证码
     * @param securephone                     客户手机号
     * @param imagecode                       图片验证码
     */
    @RequestMapping("/quicktrade/sendcode")
    public Object doSendSecurityCode(@Client ClientInfo clientInfo,
                                     @RequestParam(value = "securephone", required = true)String securephone,
                                     @RequestParam(value = "imagecode", required = false, defaultValue = "")String imagecode) throws Exception {
        if (!auth.doCheckImageCodeTimesAndRight(securephone, imagecode, clientInfo.getIP())) {
            return new Object(){
                public boolean success = false;
                public String error = "图片验证码错误";
                public String errortype = "imagecode";
            };
        }
        Map<String, Object> map = authMethod.sendSMSCodeNOCheckPhoneExist(auth.getSessionUserId(), securephone, ValidateType.quickTrade, clientInfo.getIP());
        map.put("times", authMethod.checkTodaySMSCodeTimesByPhoneAndIP(securephone, clientInfo.getIP()));
        return map;
    }

    /**
     * 提交快速找货
     * @param securephone           客户手机号
     * @param tradeInfo             客户填写的信息
     * @param smscode               手机验证码
     */
    @RequestMapping(value = "/quicktrade/add", method = RequestMethod.POST)
    public Object SubmitTradeInfo(@RequestParam(value = "securephone", required = true)String securephone,
                                  @RequestParam(value = "smscode", required = true)String smscode,
                                  @RequestParam(value = "tradeInfo", required = true) String tradeInfo){
        Phonevalidator phonevalidator = validMapper.findVerifyCode(securephone, smscode, ValidateType.quickTrade);
        MapObject map =  authMethod.doCheckCodeNOCheckPhoneExist(securephone, phonevalidator);
        if(map.isSuccess()){
            if(quickTradeMapper.addTrade(new QuickTrade(securephone, tradeInfo, LocalDateTime.now(), smscode, 1)) != 1){
                map.setSuccess(false);
                map.setError("提交失败,请刷新页面重试");
            }
            validMapper.modifyValidatedAndTime(securephone, smscode);
        }
        return map;
    }


}
