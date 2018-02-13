package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 15/12/16.
 */
public class FriendlyLink implements Serializable {
    private int id;                          //id
    private String companyname;              //公司名称
    private String url;                      //公司网站url
    private int sequence;                    //顺序
    private String remarks;                  //备注
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;        //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;    //最后一次更新时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastedittime;      //最后一次修改时间
    private boolean isshow;                  //是否在前台显示
    private boolean isdelete;                //是否删除
    private int lasteditmanid;               //最后一次操作人id
    private String lasteditmanusername;      //最后一次操作人username

    public FriendlyLink() {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public LocalDateTime getLastedittime() {
        return lastedittime;
    }

    public void setLastedittime(LocalDateTime lastedittime) {
        this.lastedittime = lastedittime;
    }

    public boolean isshow() {
        return isshow;
    }

    public void setIsshow(boolean isshow) {
        this.isshow = isshow;
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


}
