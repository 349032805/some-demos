package kitt.site.service;

import kitt.core.domain.*;
import kitt.core.persistence.UserMapper;
import kitt.core.service.MessageNotice;
import kitt.core.util.AuthMethod;
import kitt.core.util.ToolsMethod;
import kitt.ext.WithLogger;
import kitt.site.basic.exception.NotFoundException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;

@Service
public class Auth implements WithLogger {
    @Autowired
    private Session session;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AuthMethod authMethod;

    public boolean login(String securephone, String password) {
        if (securephone != null) {
            User user = userMapper.getUserByPhone(securephone);
            if (user != null && user.getPassword().equals(password)) {
                session.login(user);
                return true;
            }
        }
        return false;
    }

    public void login(User user) {
        if(user != null){
            session.login(user);
        }
    }

    /**
     * 注册，向数据库添加用户
     * @param securephone       手机号
     * @param password          密码
     * @param userFrom          来源
     * @param clienttype        客户来源 1-网站, 2-安卓, 3-微信, 4-IOS
     */
    public boolean addUser(String securephone, String password, String userFrom, int clienttype) throws Exception {
        if (!StringUtils.isBlank(securephone) && !StringUtils.isBlank(password)) {
            if(!StringUtils.isBlank(userFrom) && userFrom.length() > 50) userFrom = userFrom.substring(0, 50);
            userMapper.addUser(new User(securephone, DigestUtils.md5Hex(password), LocalDateTime.now(), true, clienttype, userFrom));
            session.login(userMapper.getUserByPhone(securephone));
            String content = "感谢您成为易煤网会员！温馨提示：请您完善企业信息，以便享受易煤网更加快捷、安全的交易功能与服务。完善信息需上传最新有效的企业资质并加盖公章：1、开 票资料 2、企业法人营业执照副本 3、组织机构代码证副本 4、税务登记证副本 5、开户许可证 6、煤炭经营许可证（仅限山西、内蒙古地区）。感谢您的支持!如需帮助，请拨打客服热线400-960-1180。";
            MessageNotice.CommonMessage.noticeUser(securephone, content);
            return true;    //注册成功
        }
        return false;       //注册失败
    }

    //检查是否有查看权限
    public void doCheckUserRight(int userid){
        if(session.getUser().getId() != userid){
            throw new NotFoundException();
        }
    }

    public void doCheckUserRight(int userid, int sellerid){
        if((session.getUser().getId() != userid && session.getUser().getId() != sellerid) ||
                (session.getUser().getId() == userid && session.getUser().getId() == sellerid )){
            throw new NotFoundException();
        }
    }

    //输出错误日志
    public void doOutputErrorInfo(String info){
        logger().info("REMINDINFO -- REMINDINFO");
        for(int i=0; i<5; i++) {
            logger().info(info);
        }
    }

    /**
     * 去掉Cookie
     * @param cookieName
     */
    public void removeCookie(String cookieName, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 设置Cookie
     * @param cookieName
     * @param securephone
     */
    public void setCookie(String cookieName, String securephone, HttpServletResponse response) {
        String values = null;
        try {
            values = URLEncoder.encode(securephone, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Cookie cookie = new Cookie(cookieName, values);
        cookie.setMaxAge(30 * 24 * 60 * 60);//设置cookie的有效期
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    /**
     * 通过session里面的userid获取最新的User数据
     * @param id               users id
     * @return
     */
    public User getLatestUserBySession(int id) {
        return userMapper.getUserById(id);
    }

    /**
     * 获取session里面的userId
     * @return
     */
    public int getSessionUserId(){
        if (session != null && session.getUser() != null) return session.getUser().getId();
        else return 0;
    }

    /**
     * 检查图片验证码  当该手机号或者该ip地址发送验证码超过5次, 并且验证码错误或者为空, 结果false , 其它为true
     * @param phone                手机号
     * @param imagecode            图片验证码
     * @param ip                   IP地址
     */
    public boolean doCheckImageCodeTimesAndRight(String phone, String imagecode, String ip) {
        if (authMethod.checkTodaySMSCodeTimesByPhoneAndIP(phone, ip)) {
            session.setPicCode(null);
            return true;
        } else if (!StringUtils.isBlank(session.getPicCode()) && !StringUtils.isBlank(imagecode) && session.getPicCode().equalsIgnoreCase(imagecode)) {
            session.setPicCode(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查图片验证码是否正确
     * @param imagecode            图片验证码
     */
    public MapObject doCheckImageCode(String imagecode) {
        MapObject map = new MapObject();
        map.setSuccess(false);
        if (StringUtils.isBlank(imagecode)) {
            map.setError("请输入图片验证码");
        } else if (!StringUtils.isBlank(session.getPicCode()) && !StringUtils.isBlank(imagecode) && session.getPicCode().equalsIgnoreCase(imagecode)) {
            map.setSuccess(true);
        } else {
            map.setError("图片验证码错误");
        }
        return map;
    }
}
