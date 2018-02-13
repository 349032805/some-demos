package kitt.core.service;

import kitt.core.domain.EnumRemindInfo;
import kitt.core.domain.Phonevalidator;
import kitt.core.domain.ValidateType;
import kitt.core.persistence.ValidMapper;
import kitt.core.util.ToolsMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiangyang on 15/10/20.
 */
@Service
@Transactional(readOnly = false)
public class VerifyCodeService {
  @Autowired
  protected ValidMapper validMapper;
  @Autowired
  private CODE code;
  @Autowired
  private ToolsMethod toolsMethod;
  private Logger logger = LoggerFactory.getLogger(VerifyCodeService.class);
  private final String HELLOWORDS_STR = "您的验证码是：";
  private final String PHONE_CANNOT_NULL = "手机号不能为空";
  private final String CODE_SEND_ERROR = "验证码发送失败";
  private final String ADD_CODE_MYDQL_ERROR = "将验证码添加到数据库出错";

  /**发送验证码
   * @param phoneNum         手机号
   * @param validateType     发送验证码的类型 register, resetpassword, manualsell,forgetPassword, quickTrade
   */
  @Transactional(readOnly = false)
  public Map<String, Object> generateVerifyCode(int userId, String phoneNum, ValidateType validateType, String IP)  {
    Map<String, Object> map = new HashMap<String, Object>();
    if (StringUtils.isBlank(phoneNum)) {
      logger.error(phoneNum, PHONE_CANNOT_NULL);
      return toolsMethod.setMapMessage(map, false, PHONE_CANNOT_NULL);
    }
    //检查10分钟以内是否有没有验证的验证码(validated 为 0)
    Phonevalidator phoneValidator = validMapper.findCodeWithin10Minute(phoneNum, validateType);
    if (phoneValidator == null) {
      //创建验证码, 过期时间位10分钟
      phoneValidator = new Phonevalidator(userId, phoneNum, code.CreateCode(), LocalDateTime.now().plusMinutes(10), validateType, false, IP);
      //添加验证码信息到数据库记录表
      if (validMapper.addValid(phoneValidator) != 1) {
        logger.error(phoneNum, ADD_CODE_MYDQL_ERROR);
        return toolsMethod.setMapMessage(map, false, EnumRemindInfo.Site_System_Error.value());
      }
    } else {
      if(validMapper.modifyValidatedAndTime(phoneNum, phoneValidator.getCode()) > 0) {
        if (validMapper.addValid(phoneValidator) != 1) {
          logger.error(phoneNum, ADD_CODE_MYDQL_ERROR);
          return toolsMethod.setMapMessage(map, false, EnumRemindInfo.Site_System_Error.value());
        }
      } else {
        logger.error(phoneNum, CODE_SEND_ERROR);
        return toolsMethod.setMapMessage(map, false, CODE_SEND_ERROR);
      }
    }
    try {
      MessageNotice.CommonMessage.noticeUser(phoneNum, HELLOWORDS_STR + phoneValidator.getCode());
    } catch (Exception e) {
      logger.error(phoneNum, CODE_SEND_ERROR);
      return toolsMethod.setMapMessage(map, false, CODE_SEND_ERROR);
    }
    return toolsMethod.setMapMessage(map, true, "");
  }


}
