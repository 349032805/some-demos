package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 15/12/21.
 * 企业表
 */
public class Enterprise implements Serializable {
    private int id;
    private String name;                   //企业名称
    private String logo;                   //企业logo
    private String remarks;                //备注
    private String usetype;                //使用类型
    private int parentid;                  //所属对象数据库表id
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;      //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;  //最后一次更新时间
    private boolean isdelete;              //是否删除
    private int lasteditmanid;             //最后一次更新人id
    private String lasteditmanusername;    //最后一次操作人登录名
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastedittime;    //最后一次操作时间

    public Enterprise() {
    }

    public Enterprise(String name, String logo, int parentid, String usetype, int lasteditmanid, String lasteditmanusername) {
        this.name = name;
        this.logo = logo;
        this.parentid = parentid;
        this.usetype = usetype;
        this.lasteditmanid = lasteditmanid;
        this.lasteditmanusername = lasteditmanusername;
    }

    public Enterprise(int id, String name, String logo, int parentid, String usetype, int lasteditmanid, String lasteditmanusername) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.parentid = parentid;
        this.usetype = usetype;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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
