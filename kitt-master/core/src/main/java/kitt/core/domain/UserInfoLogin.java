package kitt.core.domain;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Created by liuxinjie on 15/8/30.
 * admin模块统计用户登陆信息时使用
 */
public class UserInfoLogin implements Serializable {
    private int id;                        //users表对应id
    private String companyname;            //公司名字
    private String username;               //companies表legalpersonname
    private String userphone;              //users表securephone
    private LocalDate createtime;          //users表registertime
    private Long logintimes;               //从userlogins表统计出来的登陆次数
    private Long totalSellinfoAmount;      //客户发布的总供应量（不包含审核未通过的，取消的等无效的）
    private Long totalDemandAmount;        //客户发布的总需求量

    public UserInfoLogin() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public LocalDate getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDate createtime) {
        this.createtime = createtime;
    }

    public Long getLogintimes() {
        return logintimes;
    }

    public void setLogintimes(Long logintimes) {
        this.logintimes = logintimes;
    }

    public Long getTotalSellinfoAmount() {
        return totalSellinfoAmount;
    }

    public void setTotalSellinfoAmount(Long totalSellinfoAmount) {
        this.totalSellinfoAmount = totalSellinfoAmount;
    }

    public Long getTotalDemandAmount() {
        return totalDemandAmount;
    }

    public void setTotalDemandAmount(Long totalDemandAmount) {
        this.totalDemandAmount = totalDemandAmount;
    }
}
