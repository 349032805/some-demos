package kitt.core.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 15/9/15.
 * 记录客户访问日志
 */
public class WebLog implements Serializable {
    private String url;                          //访问的url
    private String method;                       //访问方法名
    private int userid;                          //已登陆的客户，users表对应的id
    private String userphone;                    //已登录的客户，登陆手机号，users表对应securephone
    private String useragent;                    //客户访问时使用的客户端
    private String userip;                       //客户访问使用的ip
    private String requesttype;                  //request 方式，分为 GET 和 POST
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;            //访问时间
    private String parameter;                    //url参数

    public WebLog() {
    }

    public WebLog(String url, String parameter, String method, String useragent, String userip, String requesttype) {
        this.setUrl(url);
        this.setParameter(parameter);
        this.setMethod(method);
        this.setUseragent(useragent);
        this.setUserip(userip);
        this.requesttype = requesttype;
    }

    public WebLog(String url, String parameter, String method, int userid, String userphone, String useragent, String userip, String requesttype) {
        this.setUrl(url);
        this.setParameter(parameter);
        this.setMethod(method);
        this.userid = userid;
        this.userphone = userphone;
        this.setUseragent(useragent);
        this.setUserip(userip);
        this.requesttype = requesttype;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        if (!StringUtils.isBlank(url)) {
            url = url.trim();
        }
        if (!StringUtils.isBlank(url) && url.length() > 250) {
            this.url = url.substring(0, 250);
        } else {
            this.url = url;
        }
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        if (!StringUtils.isBlank(method) && method.length() > 250) {
            this.method = method.substring(0, 250);
        } else {
            this.method = method;
        }
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        if (!StringUtils.isBlank(useragent) && useragent.length() > 990) {
            this.useragent = useragent.substring(0, 990);
        } else {
            this.useragent = useragent;
        }
    }

    public String getUserip() {
        return userip;
    }

    public void setUserip(String userip) {
        if (!StringUtils.isBlank(userip) && userip.length() > 95) {
            this.userip = userip.substring(0, 95);
        } else {
            this.userip = userip;
        }
    }

    public String getRequesttype() {
        return requesttype;
    }

    public void setRequesttype(String requesttype) {
        this.requesttype = requesttype;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        if (!StringUtils.isBlank(parameter)) {
            parameter = parameter.trim();
        }
        if (!StringUtils.isBlank(parameter) && parameter.length() > 250) {
            this.parameter = parameter.substring(0, 250);
        } else {
            this.parameter = parameter;
        }
    }
}
