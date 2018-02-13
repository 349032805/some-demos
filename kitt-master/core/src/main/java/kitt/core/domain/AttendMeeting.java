package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by liuxinjie on 15/12/21.
 * 前台报名记录
 */
public class AttendMeeting implements Serializable{
    private int id;
    private int mid;                         //会议id
    private String companyname;              //公司名称
    private String ymwusername;              //易煤网账号
    private boolean isstay;                  //是否住宿, false:否, true:是
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;        //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;    //最后一次更新时间
    private List<Person> personList;         //参会人员list

    public AttendMeeting() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getYmwusername() {
        return ymwusername;
    }

    public void setYmwusername(String ymwusername) {
        this.ymwusername = ymwusername;
    }

    public boolean isstay() {
        return isstay;
    }

    public void setIsstay(boolean isstay) {
        this.isstay = isstay;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }
}
