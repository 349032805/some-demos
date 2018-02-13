package kitt.core.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/12/18.
 * 会议表
 */
public class Meeting implements Serializable {
    private int id;
    private String name;
    private String banner1;
    private String banner2;
    private String banner3;
    private String banner4;
    private String banner5;
    private String unitshowstyle;
    private String qrcodename1;
    private String qrcodeurl1;
    private String qrcodename2;
    private String qrcodeurl2;
    private String introduction;               //会议简介图片路径
    private String agenda;                     //会议议程图片路径
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime starttime;           //会议开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endtime;             //会议结束时间
    private String place;                      //会议地点
    private String placeinfo;                  //会议地点介绍
    private String placepic;                   //会议地点图片
    private String placemap;                   //会议地图图片
    private String placeway1;                  //会议地点路径1
    private String placeway2;                  //会议地点路径2
    private String placeway3;                  //会议地点路径3
    private String placeway4;                  //会议地点路径4
    private String placeway5;                  //会议地点路径5
    private String linkmanname1;               //会议联系人1姓名
    private String linkmanphone1;              //会议联系人1电话
    private String linkmanname2;               //会议联系人2姓名
    private String linkmanphone2;              //会议联系人2电话
    private String linkmanname3;               //会议联系人3姓名
    private String linkmanphone3;              //会议联系人3电话
    private String linkmanname4;               //会议联系人4姓名
    private String linkmanphone4;              //会议联系人4电话
    private String linkmanname5;               //会议联系人5姓名
    private String linkmanphone5;              //会议联系人5电话
    private String invitationcard;             //邀请函文件路径
    private String receipt;                    //回执文件路径
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createtime;          //创建时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastupdatetime;      //最后一次更新时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastedittime;        //最后一次更新时间
    private int lasteditmanid;                 //最后一次更新人id
    private String lasteditmanusername;        //最后一次操作人登录名
    private int sequence;                      //顺序
    private boolean isshow;                    //是否显示
    private String attendmeetingtheme;         //会议报名主题
    private List<Map<String, Object>> bannerUrlList;         //获取banner图片url list
    private List<Map<String, Object>> hostUnitList;          //主办单位list
    private List<Map<String, Object>> assistUnitList;        //协办单位list
    private List<Map<String, Object>> guestList;             //嘉宾列表
    private List<Map<String, Object>> enterpriseList;        //参会企业
    private List<Map<String, Object>> mediaList;             //媒体支持
    private List<Map<String, Object>> placewayList;          //路线介绍list
    private List<Map<String, Object>> linkmanList;           //会议组联系人list
    private List<Map<String, Object>> attendTypeList;        //报名方式list

    public Meeting() {
    }

    public Meeting(String name, int lasteditmanid, String lasteditmanusername){
        this.name = name;
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

    public String getBanner1() {
        return banner1;
    }

    public void setBanner1(String banner1) {
        this.banner1 = banner1;
    }

    public String getBanner2() {
        return banner2;
    }

    public void setBanner2(String banner2) {
        this.banner2 = banner2;
    }

    public String getBanner3() {
        return banner3;
    }

    public void setBanner3(String banner3) {
        this.banner3 = banner3;
    }

    public String getBanner4() {
        return banner4;
    }

    public void setBanner4(String banner4) {
        this.banner4 = banner4;
    }

    public String getBanner5() {
        return banner5;
    }

    public void setBanner5(String banner5) {
        this.banner5 = banner5;
    }

    public String getUnitshowstyle() {
        return unitshowstyle;
    }

    public void setUnitshowstyle(String unitshowstyle) {
        this.unitshowstyle = unitshowstyle;
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

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public LocalDateTime getStarttime() {
        return starttime;
    }

    public void setStarttime(LocalDateTime starttime) {
        this.starttime = starttime;
    }

    public LocalDateTime getEndtime() {
        return endtime;
    }

    public void setEndtime(LocalDateTime endtime) {
        this.endtime = endtime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getPlaceinfo() {
        return placeinfo;
    }

    public void setPlaceinfo(String placeinfo) {
        this.placeinfo = placeinfo;
    }

    public String getPlacepic() {
        return placepic;
    }

    public void setPlacepic(String placepic) {
        this.placepic = placepic;
    }

    public String getPlacemap() {
        return placemap;
    }

    public void setPlacemap(String placemap) {
        this.placemap = placemap;
    }

    public String getPlaceway1() {
        return placeway1;
    }

    public void setPlaceway1(String placeway1) {
        this.placeway1 = placeway1;
    }

    public String getPlaceway2() {
        return placeway2;
    }

    public void setPlaceway2(String placeway2) {
        this.placeway2 = placeway2;
    }

    public String getPlaceway3() {
        return placeway3;
    }

    public void setPlaceway3(String placeway3) {
        this.placeway3 = placeway3;
    }

    public String getPlaceway4() {
        return placeway4;
    }

    public void setPlaceway4(String placeway4) {
        this.placeway4 = placeway4;
    }

    public String getPlaceway5() {
        return placeway5;
    }

    public void setPlaceway5(String placeway5) {
        this.placeway5 = placeway5;
    }

    public String getLinkmanname1() {
        return linkmanname1;
    }

    public void setLinkmanname1(String linkmanname1) {
        this.linkmanname1 = linkmanname1;
    }

    public String getLinkmanphone1() {
        return linkmanphone1;
    }

    public void setLinkmanphone1(String linkmanphone1) {
        this.linkmanphone1 = linkmanphone1;
    }

    public String getLinkmanname2() {
        return linkmanname2;
    }

    public void setLinkmanname2(String linkmanname2) {
        this.linkmanname2 = linkmanname2;
    }

    public String getLinkmanphone2() {
        return linkmanphone2;
    }

    public void setLinkmanphone2(String linkmanphone2) {
        this.linkmanphone2 = linkmanphone2;
    }

    public String getLinkmanname3() {
        return linkmanname3;
    }

    public void setLinkmanname3(String linkmanname3) {
        this.linkmanname3 = linkmanname3;
    }

    public String getLinkmanphone3() {
        return linkmanphone3;
    }

    public void setLinkmanphone3(String linkmanphone3) {
        this.linkmanphone3 = linkmanphone3;
    }

    public String getLinkmanname4() {
        return linkmanname4;
    }

    public void setLinkmanname4(String linkmanname4) {
        this.linkmanname4 = linkmanname4;
    }

    public String getLinkmanphone4() {
        return linkmanphone4;
    }

    public void setLinkmanphone4(String linkmanphone4) {
        this.linkmanphone4 = linkmanphone4;
    }

    public String getLinkmanname5() {
        return linkmanname5;
    }

    public void setLinkmanname5(String linkmanname5) {
        this.linkmanname5 = linkmanname5;
    }

    public String getLinkmanphone5() {
        return linkmanphone5;
    }

    public void setLinkmanphone5(String linkmanphone5) {
        this.linkmanphone5 = linkmanphone5;
    }

    public String getInvitationcard() {
        return invitationcard;
    }

    public void setInvitationcard(String invitationcard) {
        this.invitationcard = invitationcard;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isshow() {
        return isshow;
    }

    public void setIsshow(boolean isshow) {
        this.isshow = isshow;
    }

    public String getAttendmeetingtheme() {
        return attendmeetingtheme;
    }

    public void setAttendmeetingtheme(String attendmeetingtheme) {
        this.attendmeetingtheme = attendmeetingtheme;
    }

    public List<Map<String, Object>> getBannerUrlList() {
        return bannerUrlList;
    }

    public void setBannerUrlList(List<Map<String, Object>> bannerUrlList) {
        this.bannerUrlList = bannerUrlList;
    }

    public List<Map<String, Object>> getHostUnitList() {
        return hostUnitList;
    }

    public void setHostUnitList(List<Map<String, Object>> hostUnitList) {
        this.hostUnitList = hostUnitList;
    }

    public List<Map<String, Object>> getAssistUnitList() {
        return assistUnitList;
    }

    public void setAssistUnitList(List<Map<String, Object>> assistUnitList) {
        this.assistUnitList = assistUnitList;
    }

    public List<Map<String, Object>> getGuestList() {
        return guestList;
    }

    public void setGuestList(List<Map<String, Object>> guestList) {
        this.guestList = guestList;
    }

    public List<Map<String, Object>> getEnterpriseList() {
        return enterpriseList;
    }

    public void setEnterpriseList(List<Map<String, Object>> enterpriseList) {
        this.enterpriseList = enterpriseList;
    }

    public List<Map<String, Object>> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Map<String, Object>> mediaList) {
        this.mediaList = mediaList;
    }

    public List<Map<String, Object>> getPlacewayList() {
        return placewayList;
    }

    public void setPlacewayList(List<Map<String, Object>> placewayList) {
        this.placewayList = placewayList;
    }

    public List<Map<String, Object>> getLinkmanList() {
        return linkmanList;
    }

    public void setLinkmanList(List<Map<String, Object>> linkmanList) {
        this.linkmanList = linkmanList;
    }

    public List<Map<String, Object>> getAttendTypeList() {
        return attendTypeList;
    }

    public void setAttendTypeList(List<Map<String, Object>> attendTypeList) {
        this.attendTypeList = attendTypeList;
    }
}
