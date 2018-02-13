package kitt.core.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by fanjun on 15-4-26.
 */
public class AboutUs implements Serializable {

    private int id;
    private String title;               //标题//职位
    private String content;
    private String type;                //归类
    private int sequence;
    private boolean ishot;              //是否热门
    private String workplace;           //工作地点
    private String contact;             //联系方式
    private int viewtimes;              //浏览次数
    /**
     * 关于我们 四个模块设计在了一张表里存放数据
     * 沟通状态 已沟通,未沟通.原本设计为boolean类型,但是因为type其它三模块没有status,
     * 建议反馈模块需要区别开全部,已沟通,未沟通列表,status为非必须字段,但是得给默认值0
     * 造成全部选项的列表数据不对
     */
    private String status;  //沟通状态noCommunicate,hasCommunicated
    private boolean isdelete;
    private LocalDateTime updatetime;
    private String updateuser;
    private String picurl;

    public AboutUs(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isIshot() {
        return ishot;
    }

    public void setIshot(boolean ishot) {
        this.ishot = ishot;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isIsdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }

    public LocalDateTime getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(LocalDateTime updatetime) {
        this.updatetime = updatetime;
    }

    public String getUpdateuser() {
        return updateuser;
    }

    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getViewtimes() {
        return viewtimes;
    }

    public void setViewtimes(int viewtimes) {
        this.viewtimes = viewtimes;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }
}
