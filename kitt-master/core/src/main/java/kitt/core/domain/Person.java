package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 15/12/21.
 * 人员表
 */
public class Person implements Serializable {
    private int id;
    private String name;                          //姓名
    private boolean sex;                          //性别,0:女,1:男
    private String phone;                         //联系电话
    private String position;                      //职位
    private String remarks;                       //备注
    private String usetype;                       //使用类型
    private int parentid;                         //所属对象数据库表id
    private String icon;                          //头像
    private String iconoriginal;                  //上传,没有经过优化的图片
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;             //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;         //最后一次更新时间
    private boolean isdelete;                     //是否删除
    private int lasteditmanid;                    //最后一次更新人id
    private String lasteditmanusername;           //最后一次操作人登录名
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastedittime;           //最后一次操作时间

    public Person() {
    }

    public Person(String name,String position,String phone){
        this.name = name;
        this.position = position;
        this.phone = phone;
    }

    public Person(String name, String position, String usetype, int parentid, String icon, String iconoriginal, int lasteditmanid, String lasteditmanusername) {
        this.name = name;
        this.position = position;
        this.usetype = usetype;
        this.parentid = parentid;
        this.icon = icon;
        this.iconoriginal = iconoriginal;
        this.lasteditmanid = lasteditmanid;
        this.lasteditmanusername = lasteditmanusername;
    }

    public Person(int id, String name, String position, String usetype, int parentid, String icon, String iconoriginal, int lasteditmanid, String lasteditmanusername) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.usetype = usetype;
        this.parentid = parentid;
        this.icon = icon;
        this.iconoriginal = iconoriginal;
        this.lasteditmanid = lasteditmanid;
        this.lasteditmanusername = lasteditmanusername;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getUsetype() {
        return usetype;
    }

    public void setUsetype(String usetype) {
        this.usetype = usetype;
    }

    public int getParentid() {
        return parentid;
    }

    public void setParentid(int parentid) {
        this.parentid = parentid;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIconoriginal() {
        return iconoriginal;
    }

    public void setIconoriginal(String iconoriginal) {
        this.iconoriginal = iconoriginal;
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

    public boolean isdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public int getLasteditmanid() {
        return lasteditmanid;
    }

    public void setLasteditmanid(int lasteditmanid) {
        this.lasteditmanid = lasteditmanid;
    }

    public String getLasteditmanusername() {
        return lasteditmanusername;
    }

    public void setLasteditmanusername(String lasteditmanusername) {
        this.lasteditmanusername = lasteditmanusername;
    }

    public LocalDateTime getLastedittime() {
        return lastedittime;
    }

    public void setLastedittime(LocalDateTime lastedittime) {
        this.lastedittime = lastedittime;
    }
}
