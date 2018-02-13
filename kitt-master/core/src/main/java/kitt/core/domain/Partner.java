package kitt.core.domain;


import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 15/9/19.
 * 合作伙伴
 */
public class Partner implements Serializable {
    private int id;
    private String companyname;                 //合作伙伴公司名称
    private String picurl;                      //合作伙伴图片URL
    private String linkurl;                     //合作伙伴超链接URL
    private int sequence;                       //排列顺序
    private String remarks;                     //描述信息
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;           //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;       //最后一次更改时间（MySQL自动更改）
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastedittime;         //最后一次更改时间
    private boolean isshow;                     //是否在前台显示
    private boolean isdelete;                   //是否删除
    private String lasteditman;                 //最后一次更新操作人

    public Partner() {
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

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
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

    public String getLasteditman() {
        return lasteditman;
    }

    public void setLasteditman(String lasteditman) {
        this.lasteditman = lasteditman;
    }
}
