package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 15/9/2.
 */
public class Video implements Serializable {
    private int id;                              //id
    private String name;                         //视频主题
    private String url;                          //视频url
    private String picurl;                       //视频广告图片url
    private boolean isshow;                      //是否在前台显示
    private boolean isdelete;                    //是否删除
    private String remarks;                      //备注
    private int viewtimes;                       //浏览量
    private int sequence;                        //排列顺序
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;            //上传时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;        //最后一次变化时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastedittime;          //最后一次更改时间
    private String lasteditman;                  //最后一次更新，操作人
    private String length;                       //视频长度

    public Video() {
    }

    public Video(String url, String remarks, String lasteditman) {
        this.url = url;
        this.remarks = remarks;
        this.lasteditman = lasteditman;
    }

    public Video(int id, String url, String remarks, String lasteditman) {
        this.id = id;
        this.url = url;
        this.remarks = remarks;
        this.lasteditman = lasteditman;
    }

    public Video(String url, boolean isshow, boolean isdelete, String remarks, int viewtimes) {
        this.url = url;
        this.isshow = isshow;
        this.isdelete = isdelete;
        this.remarks = remarks;
        this.viewtimes = viewtimes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getViewtimes() {
        return viewtimes;
    }

    public void setViewtimes(int viewtimes) {
        this.viewtimes = viewtimes;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
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

    public String getLasteditman() {
        return lasteditman;
    }

    public void setLasteditman(String lasteditman) {
        this.lasteditman = lasteditman;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
