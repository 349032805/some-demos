package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * Created by fanjun on 15-3-23.
 * 图片
 */
public class IndexBanner {
    private int id;
    private String name;                                //图片名称
    private String path;                                //图片路径
    private int sequence;                               //顺序
    private int limitnum;                               //显示数量
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;                   //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;               //最后一次更新时间
    private String type;                                //类型,web,wechat,advert...
    private boolean isshow;                             //是否显示
    private String linkurl;                             //图片链接
    private int lastupdatemanid;                        //最后一次操作人id
    private String lastupdatemanusername;               //最后一次操作人账号
    private String lastupdatemanname;                   //最后一次操作人姓名

    public IndexBanner(){

    }

    public IndexBanner(int id, int lastupdatemanid, String lastupdatemanusername, String lastupdatemanname) {
        this.id = id;
        this.lastupdatemanid = lastupdatemanid;
        this.lastupdatemanusername = lastupdatemanusername;
        this.lastupdatemanname = lastupdatemanname;
    }

    public IndexBanner(int id, String path, int lastupdatemanid, String lastupdatemanusername, String lastupdatemanname) {
        this(id, lastupdatemanid, lastupdatemanusername, lastupdatemanname);
        this.path = path;
    }

    public IndexBanner(int id, int lastupdatemanid, String lastupdatemanusername, String lastupdatemanname, String linkurl) {
        this(id, lastupdatemanid, lastupdatemanusername, lastupdatemanname);
        this.linkurl = linkurl;
    }

    public IndexBanner(int id, int sequence, int lastupdatemanid, String lastupdatemanusername, String lastupdatemanname) {
        this(id, lastupdatemanid, lastupdatemanusername, lastupdatemanname);
        this.sequence = sequence;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public int getLimitnum() {
        return limitnum;
    }

    public void setLimitnum(int limitnum) {
        this.limitnum = limitnum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isIsshow() {
        return isshow;
    }

    public void setIsshow(boolean isshow) {
        this.isshow = isshow;
    }

    public String getLinkurl() {
        return linkurl;
    }

    public void setLinkurl(String linkurl) {
        this.linkurl = linkurl;
    }

    public LocalDateTime getLastupdatetime() {
        return lastupdatetime;
    }

    public void setLastupdatetime(LocalDateTime lastupdatetime) {
        this.lastupdatetime = lastupdatetime;
    }

    public int getLastupdatemanid() {
        return lastupdatemanid;
    }

    public void setLastupdatemanid(int lastupdatemanid) {
        this.lastupdatemanid = lastupdatemanid;
    }

    public String getLastupdatemanusername() {
        return lastupdatemanusername;
    }

    public void setLastupdatemanusername(String lastupdatemanusername) {
        this.lastupdatemanusername = lastupdatemanusername;
    }

    public String getLastupdatemanname() {
        return lastupdatemanname;
    }

    public void setLastupdatemanname(String lastupdatemanname) {
        this.lastupdatemanname = lastupdatemanname;
    }
}
