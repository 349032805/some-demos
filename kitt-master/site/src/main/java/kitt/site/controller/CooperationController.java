package kitt.site.controller;


import kitt.core.domain.*;
import kitt.core.persistence.CompanyMapper;
import kitt.core.persistence.DataBookMapper;
import kitt.core.persistence.ValidMapper;
import kitt.core.util.AuthMethod;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.Client;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.Auth;
import kitt.site.service.CooperationService;
import kitt.site.service.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by liuxinjie on 15/9/28.
 */
@Controller
public class CooperationController extends JsonController {
    @Autowired
    private Auth auth;
    @Autowired
    private CooperationService cooperationService;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private Session session;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private ValidMapper validMapper;
    @Autowired
    private AuthMethod authMethod;

    /**
     * 从首页调到我要合作页面
     * @param type                             类型，我要合作分为4种类型
     * @return
     */
    @RequestMapping("/cooperation")
    public String doGoCooperationPage(@RequestParam(value = "type", required = false, defaultValue = "1")int type,
                                      Map<String, Object> map){
        map.put("type", type);
        if(session != null && session.getUser() != null){
            User user = auth.getLatestUserBySession(session.getUser().getId());
            map.put("linkmanphone", user.getSecurephone());
            Company company = companyMapper.getCompanyByUserid(user.getId());
            if(company != null) {
                map.put("companyname", company.getName());
            }
        }
        return "cooperation/add";
    }

    /**
     * 检查手机号是否正确
     */
    @RequestMapping("/cooperation/checkphone")
    @ResponseBody
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
    @RequestMapping("/cooperation/sendcode")
    @ResponseBody
    public Object doSendCooperationSMSCode(@Client ClientInfo clientInfo,
                                           @RequestParam(value = "securephone", required = true)String securephone,
                                           @RequestParam(value = "imagecode", required = false, defaultValue = "")String imagecode) throws Exception {
        if (!auth.doCheckImageCodeTimesAndRight(securephone, imagecode, clientInfo.getIP())) {
            return new Object() {
                public boolean success = false;
                public String error = "图片验证码错误";
                public String errortype = "imagecode";
            };
        }
        return authMethod.sendSMSCodeNOCheckPhoneExist(auth.getSessionUserId(), securephone, ValidateType.cooperation, clientInfo.getIP());
    }

    /**
     * 添加我要合作
     * @param cooperation                      Cooperation 对象
     */
    @RequestMapping("/cooperation/add")
    @ResponseBody
    public Object doAddCooperation(Cooperation cooperation,
                                   @RequestParam(value = "smscode", required = false)String smscode){
        MapObject map = new MapObject();
        //用户没有登录，需要验证码
        if(session.getUser()==null) {
            Phonevalidator phonevalidator = validMapper.findVerifyCode(cooperation.getLinkmanphone(), smscode, ValidateType.cooperation);
            map =  authMethod.doCheckCodeNOCheckPhoneExist(cooperation.getLinkmanphone(), phonevalidator);
            if (!map.isSuccess()) {
                return map;
            } else {
                validMapper.modifyValidatedAndTime(cooperation.getLinkmanphone(), smscode);
            }
        }
        //验证通过
        cooperation.setClienttype(1);
        cooperation.setKindname(dataBookMapper.getDataBookNameByTypeSequence("cooperationkind", cooperation.getKind()));
        cooperation.setTypename(dataBookMapper.getDataBookNameByTypeSequence("cooperationtype", cooperation.getType()));
        cooperationService.doAddCooperationMethod(cooperation);
        map.setSuccess(true);
        return map;
    }



}
