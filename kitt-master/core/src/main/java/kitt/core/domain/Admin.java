package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by joe on 11/4/14.
 */
public class Admin implements Serializable {
    private int id;                         //id
    private String username;                //账号
    private String password;                //密码
    private boolean isactive;               //是否激活，可用
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;       //创建时间
    private String phone;                   //电话号码
    private String jobnum;                  //工号
    private String name;                    //姓名
    private String status;                  //状态
    private List<Role> roles;
    //private boolean isAdmin;
    private List<Areaport> ports;
    private Integer teamId;

    public Admin(){}

    public Admin(String name, String phone){
        this.name = name;
        this.phone = phone;
    }

    public Admin(String status, String name, String phone, String jobnum){
        this.status = status;
        this.name = name;
        this.phone = phone;
        this.jobnum = jobnum;
    }

    public Admin(String status,  String password, String name, String phone, boolean isactive){
        this.status = status;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.isactive = isactive;
    }

    public Admin(String status, String username, String password, String name, String phone, boolean isactive){
        this.status = status;
        this.username = username;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.isactive = isactive;
    }

    public Admin(String username, String password, boolean isactive, String phone, String jobnum, String name) {
        this.username = username;
        this.isactive = isactive;
        this.jobnum = jobnum;
        this.name = name;
        this.password = password;
        this.phone = phone;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isIsactive() {
        return isactive;
    }

    public void setIsactive(boolean isactive) {
        this.isactive = isactive;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJobnum() {
        return jobnum;
    }

    public void setJobnum(String jobnum) {
        this.jobnum = jobnum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }


    /*public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
    */

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Areaport> getPorts() {
        return ports;
    }

    public void setPorts(List<Areaport> ports) {
        this.ports = ports;
    }


    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "username='" + username + '\'' +
                ", isactive=" + isactive +
                ", createtime=" + createtime +
                ", jobnum='" + jobnum + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
