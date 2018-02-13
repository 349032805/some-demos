package kitt.site.service.mobile;


import kitt.core.domain.*;
import kitt.core.persistence.UserMapper;
import kitt.core.persistence.ValidMapper;
import kitt.site.basic.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by xiangyang on 15-5-13.
 */
@Service
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    protected ValidMapper validMapper;

    // 用户登陆
    public User login(String loginName) {
        if (StringUtils.isBlank(loginName)) {
            throw new BusinessException("参数传递错误");
        }
        return userMapper.loadUserByPhoneOrEmail(loginName);
    }

    // 用户登陆
    public User getUserByWxopenId(String wxopenid) {
        return userMapper.getUserByWxopenid(wxopenid);
    }

    //绑定用户和微信号
    @Transactional(readOnly = false)
    public void updateWxopenid(String wxopenid, int userid) {
        userMapper.updateWxopenid(wxopenid, userid);
    }

    //验证手机号是否存在
    public int userExists(String phone) {
        if (StringUtils.isBlank(phone)) {
            throw new BusinessException("参数传递错误");
        }
        return userMapper.getUserByNewSecurephone(phone);
    }

    @Transactional(readOnly = false)
    public void updateUserPassword(String phone, int userId) {
        if (StringUtils.isBlank(phone)) {
            throw new BusinessException("参数传递错误");
        }
        userMapper.modifyAccountPasswd(phone, userId);
    }

    @Transactional(readOnly = false)
    public  void addUserLog(User user, ClientInfo clientInfo){
        Userlogin ul = new Userlogin();
        ul.setUserid(user.getId());
        ul.setLogintime(LocalDateTime.now());
        ul.setLoginip(clientInfo.getIP());
        ul.setAcceptlanguage(clientInfo.getAcceptLanguage());
        ul.setUseragent(clientInfo.getUserAgent());
        userMapper.addUserlogin(ul);
    }

}
