package kitt.site.service;


import kitt.core.domain.Phonevalidator;
import kitt.core.domain.User;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Session implements Serializable{
    public class CaptureData implements Serializable{
        public String phone;
        public String capturecode;

        public CaptureData(String phone, String capturecode) {
            this.phone = phone;
            this.capturecode = capturecode;
        }
    }
    protected User user;
    protected Phonevalidator phonevalidator;
    protected String picCode;
    protected Phonevalidator resetPasswdValidCode;
    protected String mailCode;  //邮箱验证码
    protected CaptureData captureData;

    public User getUser() {
        return user;
    }

    public boolean login(User user) {
        this.user = user;
        return true;
    }

    public boolean addPhonevalidator(Phonevalidator phonevalidator) {
        this.phonevalidator = phonevalidator;
        return true;
    }

    public boolean isLogined() {
        return this.user != null;
    }

    public void logout() {
        this.user = null;
    }

    public Phonevalidator getPhonevalidator() {
        return phonevalidator;
    }

    public String getPicCode() {
        return picCode;
    }

    public void setPicCode(String picCode) {
        this.picCode = picCode;
    }

    public Phonevalidator getResetPasswdValidCode() {
        return resetPasswdValidCode;
    }

    public void setResetPasswdValidCode(Phonevalidator resetPasswdValidCode) {
        this.resetPasswdValidCode = resetPasswdValidCode;
    }

    public String getMailCode() {
        return mailCode;
    }

    public void setMailCode(String mailCode) {
        this.mailCode = mailCode;
    }

    public CaptureData getCaptureData() {
        return captureData;
    }

    public void setCaptureData(CaptureData captureData) {
        this.captureData = captureData;
    }
}
