package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by liuxinjie on 15/12/21.
 * 后台发布会议时,添加的报名方式
 */
public class AttendMeetingType implements Serializable {
    private int id;
    private int mid;                     //会议id
    private String name;                 //报名方式名称
    private String content;              //内容
    private String qrcodename1;          //二维码1名称
    private String qrcodeurl1;           //二维码1路径
    private String qrcodename2;          //二维码2名称
    private String qrcodeurl2;           //二维码2路径
    private String example;              //例子
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;    //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;//最后一次更新时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastedittime;  //最后一次操作时间
    private int lasteditmanid;           //最后一次操作人id
    private String lasteditmanusername;  //最后一次操作人登录名
    private boolean isdelete;            //是否删除

    public AttendMeetingType() {
    }

    public AttendMeetingType(int mid, String name, String content, String qrcodename1, String qrcodeurl1, String qrcodename2, String qrcodeurl2, String example, int lasteditmanid, String lasteditmanusername) {
        this.mid = mid;
        this.name = name;
        this.content = content;
        this.qrcodename1 = qrcodename1;
        this.qrcodeurl1 = qrcodeurl1;
        this.qrcodename2 = qrcodename2;
        this.qrcodeurl2 = qrcodeurl2;
        this.example = example;
        this.lasteditmanid = lasteditmanid;
        this.lasteditmanusername = lasteditmanusername;
    }

    public AttendMeetingType(int id, int mid, String name, String content, String qrcodename1, String qrcodeurl1, String qrcodename2, String qrcodeurl2, String example, int lasteditmanid, String lasteditmanusername) {
        this.id = id;
        this.mid = mid;
        this.name = name;
        this.content = content;
        this.qrcodename1 = qrcodename1;
        this.qrcodeurl1 = qrcodeurl1;
        this.qrcodename2 = qrcodename2;
        this.qrcodeurl2 = qrcodeurl2;
        this.example = example;
        this.lasteditmanid = lasteditmanid;
        this.lasteditmanusername = lasteditmanusername;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQrcodename1() {
        return qrcodename1;
    }

    public void setQrcodename1(String qrcodename1) {
        this.qrcodename1 = qrcodename1;
    }

    public String getQrcodeurl1() {
        return qrcodeurl1;
    }

    public void setQrcodeurl1(String qrcodeurl1) {
        this.qrcodeurl1 = qrcodeurl1;
    }

    public String getQrcodename2() {
        return qrcodename2;
    }

    public void setQrcodename2(String qrcodename2) {
        this.qrcodename2 = qrcodename2;
    }

    public String getQrcodeurl2() {
        return qrcodeurl2;
    }

    public void setQrcodeurl2(String qrcodeurl2) {
        this.qrcodeurl2 = qrcodeurl2;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
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

    public boolean isdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean isdelete) {
        this.isdelete = isdelete;
    }
}
