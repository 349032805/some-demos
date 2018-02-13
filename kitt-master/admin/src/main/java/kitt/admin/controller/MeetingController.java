package kitt.admin.controller;

import com.mysql.jdbc.StringUtils;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.Session;
import kitt.core.bl.ManageService;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.persistence.EnterpriseMapper;
import kitt.core.persistence.MeetingMapper;
import kitt.core.persistence.PersonMapper;
import kitt.core.service.FileStore;
import kitt.core.util.text.TextCheck;
import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by liuxinjie on 15/12/21.
 */
@RestController
@RequestMapping("/meeting")
public class MeetingController {
    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private ManageService manageService;
    @Autowired
    private Session session;
    @Autowired
    private Auth auth;
    @Autowired
    private EnterpriseMapper enterpriseMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private TextCheck textCheck;

    /**
     * 会议list
     * @param content               //搜索框里输入的内容
     * @param page                  //页数
     */
    @RequestMapping("/list")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object getMeetingList(@RequestParam(value = "searchcontent", required = false, defaultValue = "") final String content,
                                 @RequestParam(value = "page", required = false, defaultValue = "1") final int page){
        return new Object(){
            public Pager<Meeting> meetingList = meetingMapper.pageMeeting(Where.$like$(content), page, 10);
            public String searchcontent = content;
        };
    }

    /**
     * 会议详细页面
     * @param id                    //id
     */
    @RequestMapping("/detail")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doGetMeetingDetail(@RequestParam(value = "id", required = false, defaultValue = "0")int id,
                                     @RequestParam(value = "searchcontent", required = false, defaultValue = "")String content,
                                     @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        Map<String, Object> map = new HashMap<String, Object>();
        Meeting meeting = meetingMapper.getMeetingById(id);
        map.put("meeting", meeting);
        map.put("bannerUrlList", getMeetingBannerList(meeting));
        map.put("hostUnitList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_HostUnit.toString(), meeting.getId()));
        map.put("assistUnitList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AssistUnit.toString(), meeting.getId()));
        map.put("guestList", personMapper.getPersonListByTypeAndParentid(EnumPerson.Meeting_HonoredGuest.toString(), meeting.getId()));
        map.put("attendEnterpriseList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AttendEnterprise.toString(), meeting.getId()));
        map.put("attendMediaList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AttendMedia.toString(), meeting.getId()));
        map.put("linkmanList", getLinkmanList(meeting));
        map.put("placeWayList", getPlaceWayList(meeting));
        map.put("attendMeetingTypeList", meetingMapper.getAttendMeetingTypeListByMid(meeting.getId()));
        map.put("attendMeetingList", getAttendMeetingList(meeting.getId(), Where.$like$(content), page, 10));
        map.put("searchcontent", content);
        return map;
    }

    List<Map<String, Object>> getMeetingBannerList(Meeting meeting){
        List<Map<String, Object>> bannerList = new ArrayList<Map<String, Object>>();
        String[] banners = {meeting.getBanner1(), meeting.getBanner2(), meeting.getBanner3(), meeting.getBanner4(), meeting.getBanner5()};
        for(int i=0; i<banners.length; i++){
            if(!StringUtils.isNullOrEmpty(banners[i])){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("url", banners[i]);
                bannerList.add(map);
            }
        }
        return bannerList;
    }

    private List<Map<String, Object>> getLinkmanList(Meeting meeting){
        List<Map<String, Object>> linkmanList = new ArrayList<>();
        String[] linkmans = {meeting.getLinkmanname1(), meeting.getLinkmanname2(), meeting.getLinkmanname3(), meeting.getLinkmanname4(), meeting.getLinkmanname5()};
        String[] phones = {meeting.getLinkmanphone1(), meeting.getLinkmanphone2(), meeting.getLinkmanphone3(), meeting.getLinkmanphone4(), meeting.getLinkmanphone5()};
        for(int i=0; i<linkmans.length; i++){
            Map<String, Object> map = new HashMap<>();
            if(!StringUtils.isNullOrEmpty(linkmans[i])) {
                map.put("name", linkmans[i]);
                map.put("phone", phones[i]);
                linkmanList.add(map);
            }
        }
        return linkmanList;
    }

    private List<Map<String, Object>> getPlaceWayList(Meeting meeting){
        List<Map<String, Object>> placeWayList = new ArrayList<>();
        String[] placeways = {meeting.getPlaceway1(), meeting.getPlaceway2(), meeting.getPlaceway3(), meeting.getPlaceway4(), meeting.getPlaceway5()};
        for(int i=0; i<placeways.length; i++) {
            Map<String, Object> map = new HashMap<>();
            if (!StringUtils.isNullOrEmpty(placeways[i])) {
                map.put("name", placeways[i]);
                placeWayList.add(map);
            }
        }
        return placeWayList;
    }

    public Pager<AttendMeeting> getAttendMeetingList(int id, String content, int page, int pagesize){
        Pager<AttendMeeting> attendMeetingPager = meetingMapper.getAttendMeetingList(id, content, page, pagesize);
        if (attendMeetingPager.getCount() > 0) {
            for (int i=0; i<attendMeetingPager.getList().size(); i++) {
                attendMeetingPager.getList().get(i).setPersonList(personMapper.getPersonListByTypeAndParentid(EnumPerson.Meeting_AttendGuest.toString(), attendMeetingPager.getList().get(i).getId()));
            }
        }
        return attendMeetingPager;
    }

    /**
     * 添加会议
     * @param name                  //会议主题
     */
    @RequestMapping("/add")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doAddMeeting(@RequestParam(value = "name", required = true)String name) {
        if (!textCheck.doTextCheckThree(name)) {
            return new Object() {
                public boolean success = false;
                public String error = "主题中包含有非法字符!";
            };
        }
        try {
            return new Object(){
                public int id = manageService.doAddMeeting(new Meeting(name, session.getAdmin().getId(), session.getAdmin().getUsername()));
                public boolean success = true;
            };
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(EnumRemindInfo.Admin_System_Error.value());
        }
    }

    /**
     * 删除会议
     * @param id                    id
     */
    @RequestMapping("/delete")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeleteMeeting(@RequestParam(value = "id", required = true)int id){
        return manageService.doDeleteMeeting(id);
    }

    /**
     * 设置或者取消会议在前台显示
     * @param id                    id
     */
    @RequestMapping("/setCancelShow")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doSetCancelShowMeeting(@RequestParam(value = "id", required = true)int id){
        Meeting meeting = meetingMapper.getMeetingById(id);
        if (meeting == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        String error = "";
        if (!meeting.isshow()) {
            if (StringUtils.isNullOrEmpty(meeting.getName()) || (StringUtils.isNullOrEmpty(meeting.getBanner1()) && StringUtils.isNullOrEmpty(meeting.getBanner2())
                    && StringUtils.isNullOrEmpty(meeting.getBanner3()) && StringUtils.isNullOrEmpty(meeting.getBanner4()) && StringUtils.isNullOrEmpty(meeting.getBanner5()))) {
                error = "会议首页模块信息不完整";
            } else if (StringUtils.isNullOrEmpty(meeting.getIntroduction())) {
                error = "会议简介模块信息不完整";
            } else if (StringUtils.isNullOrEmpty(meeting.getAgenda())) {
                error = "会议议程模块信息不完整";
            } else if (StringUtils.isNullOrEmpty(String.valueOf(meeting.getStarttime())) || StringUtils.isNullOrEmpty(meeting.getPlace()) || StringUtils.isNullOrEmpty(meeting.getPlaceinfo()) || StringUtils.isNullOrEmpty(meeting.getPlacepic()) ||
                    StringUtils.isNullOrEmpty(meeting.getPlacemap()) || StringUtils.isNullOrEmpty(meeting.getInvitationcard()) || StringUtils.isNullOrEmpty(meeting.getReceipt()) ||
                    (StringUtils.isNullOrEmpty(meeting.getLinkmanname1()) && StringUtils.isNullOrEmpty(meeting.getLinkmanname2()) && StringUtils.isNullOrEmpty(meeting.getLinkmanname3()) &&
                            StringUtils.isNullOrEmpty(meeting.getLinkmanname4()) && StringUtils.isNullOrEmpty(meeting.getLinkmanname5())) || (StringUtils.isNullOrEmpty(meeting.getPlaceway1()) &&
                    StringUtils.isNullOrEmpty(meeting.getPlaceway2()) && StringUtils.isNullOrEmpty(meeting.getPlaceway3()) && StringUtils.isNullOrEmpty(meeting.getPlaceway4()) &&
                    StringUtils.isNullOrEmpty(meeting.getPlaceway5()))) {
                error = "参会须知模块信息不完整";
            } else if (personMapper.getPersonListByTypeAndParentid(EnumPerson.Meeting_HonoredGuest.toString(), meeting.getId()).size() == 0) {
                error = "至少要有一位参会嘉宾";
            } else if (enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AttendEnterprise.toString(), meeting.getId()).size() == 0) {
                error = "参会企业模块不能为空";
            } else if (enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AttendMedia.toString(), meeting.getId()).size() == 0) {
                error = "媒体支持模块不能为空";
            } else {
                success = manageService.doSetCancelShowMeeting(id);
            }
        } else {
            success = manageService.doSetCancelShowMeeting(id);
        }
        map.put("success", success);
        map.put("error", error);
        return map;
    }

    /**
     * 改变会议顺序
     */
    @RequestMapping("/changeSequence")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doChangeMeetingSequence(@RequestParam(value = "id", required = true)int id,
                                           @RequestParam(value = "sequence", required = true)int sequence) {
        if(meetingMapper.getMeetingById(id) == null) throw new NotFoundException();
        return manageService.doChangeMeetingSequence(id, sequence);
    }

    /**
     * 上传会议图片
     * @param file                  图片对象
     */
    @RequestMapping("/uploadFile")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doUploadMeetingImage(@RequestParam("file") MultipartFile file,
                                       @RequestParam(value = "fileType", required = true)String fileType) throws IOException, FileStore.UnsupportedContentType {
        return auth.uploadPicMethod(EnumFileType.File_Meeting.toString(), fileType, file, null, null);
    }

    /**
     * 添加会议首页内容
     * @param meeting               会议对象
     * @return
     */
    @RequestMapping("/addBannerHomePage")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doAddBannerImages(Meeting meeting) throws SQLExcutionErrorException {
        if(meetingMapper.getMeetingById(meeting.getId()) == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        if (!textCheck.doTextCheckThree(meeting.getName())) {
            map.put("error", "主题中包含有非法字符!");
        } else if(meeting.getBannerUrlList() == null || meeting.getBannerUrlList().size() == 0){
            map.put("error", "至少上传一张Banner图!");
        } else {
            meeting.setBanner1(""); meeting.setBanner2(""); meeting.setBanner3("");
            meeting.setBanner4(""); meeting.setBanner5("");
            switch (meeting.getBannerUrlList().size()) {
                case 5:
                    meeting.setBanner5(String.valueOf(meeting.getBannerUrlList().get(4).get("url")));
                case 4:
                    meeting.setBanner4(String.valueOf(meeting.getBannerUrlList().get(3).get("url")));
                case 3:
                    meeting.setBanner3(String.valueOf(meeting.getBannerUrlList().get(2).get("url")));
                case 2:
                    meeting.setBanner2(String.valueOf(meeting.getBannerUrlList().get(1).get("url")));
                case 1:
                    meeting.setBanner1(String.valueOf(meeting.getBannerUrlList().get(0).get("url")));
                    break;
                default:
                    break;
            }
            meeting.setLasteditmanid(session.getAdmin().getId());
            meeting.setLasteditmanusername(session.getAdmin().getUsername());
            success = manageService.doUpdateMeeting(meeting);
        }
        map.put("success", success);
        return map;
    }

    /**
     * 添加会议简介
     * @param meeting                    会议对象
     */
    @RequestMapping("/addIntroduction")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doAddIntroduction(@RequestParam("deleteUnits[]")int[] deleteUnits, Meeting meeting) throws SQLExcutionErrorException {
        if(meetingMapper.getMeetingById(meeting.getId()) == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        String error = "";
        if (meeting.getHostUnitList() != null && meeting.getHostUnitList().size() > 0) {
            for (int i = 0; i < meeting.getHostUnitList().size(); i++) {
                if (!textCheck.doTextCheckThree(String.valueOf(meeting.getHostUnitList().get(i).get("name")))) {
                    error = "第 " + ++i + " 个主办单位名称中包含特殊字符";
                    break;
                }
            }
        }
        if (StringUtils.isNullOrEmpty(error) && meeting.getAssistUnitList() != null && meeting.getAssistUnitList().size() > 0) {
            for (int i = 0; i < meeting.getAssistUnitList().size(); i++) {
                if (!textCheck.doTextCheckThree(String.valueOf(meeting.getAssistUnitList().get(i).get("name")))) {
                    error = "第 " + ++i + " 个协办单位名称中包含特殊字符";
                    break;
                }
            }
        }
        if (StringUtils.isNullOrEmpty(error)) {
            if (!textCheck.doTextCheckThree(meeting.getQrcodename1())) {
                error = "二维码(一) 名称中包含非法字符";
            } else if (!textCheck.doTextCheckThree(meeting.getQrcodename2())) {
                error = "二维码(二) 名称中包含非法字符";
            } else {
                if (deleteUnits != null && deleteUnits.length > 1) {
                    for (int i = 1; i < deleteUnits.length; i++) {
                        if (enterpriseMapper.getEnterpriseById(deleteUnits[i]) == null) throw new NotFoundException();
                        if (!manageService.doDeleteEnterpriseById(deleteUnits[i]))
                            throw new BusinessException(EnumRemindInfo.Admin_Refresh_Again.value());
                    }
                }
                meeting.setLasteditmanid(session.getAdmin().getId());
                meeting.setLasteditmanusername(session.getAdmin().getUsername());
                Map map1 = manageService.doAddEnterprise(meeting.getHostUnitList(), meeting.getId(), EnumEnterprise.Meeting_HostUnit.toString(), session.getAdmin().getId(), session.getAdmin().getUsername());
                Map map2 = manageService.doAddEnterprise(meeting.getAssistUnitList(), meeting.getId(), EnumEnterprise.Meeting_AssistUnit.toString(), session.getAdmin().getId(), session.getAdmin().getUsername());
                if (!Boolean.valueOf(String.valueOf(map1.get("success")))) {
                    throw new BusinessException(String.valueOf(map1.get("error")));
                } else if(!Boolean.valueOf(String.valueOf(map2.get("success")))) {
                    throw new BusinessException(String.valueOf(map2.get("error")));
                } else if(!manageService.doUpdateMeeting(meeting)) {
                    throw new BusinessException(EnumRemindInfo.Admin_System_Error.value());
                } else {
                    success = true;
                }
            }
        }
        map.put("success", success);
        map.put("error", error);
        return map;
    }

    /**
     * 添加会议议程
     * @param meeting                     会议议程
     */
    @RequestMapping("/addAgenda")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doAddAgenda(Meeting meeting) {
        if(meetingMapper.getMeetingById(meeting.getId()) == null) throw new NotFoundException();
        return new Object() {
            public boolean success = manageService.doUpdateMeeting(meeting);
        };
    }

    /**
     * 添加更改嘉宾
     * @param meeting
     */
    @RequestMapping("/addGuestList")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doAddGuestList(@RequestParam("deleteGuests[]")int[] deleteGuests, Meeting meeting) throws SQLExcutionErrorException {
        if (meetingMapper.getMeetingById(meeting.getId()) == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        String error = "";
        if (meeting.getGuestList() != null && meeting.getGuestList().size() > 0) {
            for (int i = 0; i < meeting.getGuestList().size(); i++) {
                if (!textCheck.doTextCheckThree(String.valueOf(meeting.getGuestList().get(i).get("name")))) {
                    error = "第 " + ++i + " 个嘉宾姓名中包含非法字符";
                    break;
                }
                if (!textCheck.doTextCheckThree(String.valueOf(meeting.getGuestList().get(i).get("position")))) {
                    error = "第 " + ++i + " 个嘉宾职位中包含非法字符";
                    break;
                }
            }
        }
        if (StringUtils.isNullOrEmpty(error)) {
            if (deleteGuests != null && deleteGuests.length > 1) {
                for (int i = 1; i < deleteGuests.length; i++) {
                    if (personMapper.getPersonById(deleteGuests[i]) == null) throw new NotFoundException();
                    if (!manageService.doDeletePersonMethod(deleteGuests[i]))
                        throw new BusinessException(EnumRemindInfo.Admin_Refresh_Again.value());
                }
            }
            Map map1 = manageService.doAddGuestMethod(meeting.getGuestList(), meeting.getId(), EnumPerson.Meeting_HonoredGuest.toString(), session.getAdmin().getId(), session.getAdmin().getUsername());
            if (!Boolean.valueOf(String.valueOf(map1.get("success")))) {
                throw new BusinessException(String.valueOf(map1.get("error")));
            }
            success = true;
        }
        map.put("success", success);
        map.put("error", error);
        return map;
    }

    @RequestMapping("/addEnterprise")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doAddEnterprise(@RequestParam("deleteEnterprise[]")int[] deleteEnterprise, Meeting meeting) throws SQLExcutionErrorException {
        if (meetingMapper.getMeetingById(meeting.getId()) == null) throw new NotFoundException();
        if (meeting.getEnterpriseList() != null && meeting.getEnterpriseList().size() > 0) {
            for (Map map : meeting.getEnterpriseList()) {
                if (!textCheck.doTextCheckThree(String.valueOf(map.get("name")))) {
                    return new Object() {
                        public boolean success = false;
                        public String error = "第 " + (meeting.getAttendTypeList().indexOf(map) + 1) + " 个参会企业名称中包含特殊字符";
                    };
                }
            }
        }
        if (deleteEnterprise != null && deleteEnterprise.length > 1) {
            for (int i = 1; i < deleteEnterprise.length; i++) {
                if (enterpriseMapper.getEnterpriseById(deleteEnterprise[i]) == null) throw new NotFoundException();
                if (!manageService.doDeleteEnterpriseById(deleteEnterprise[i])) throw new BusinessException(EnumRemindInfo.Admin_Refresh_Again.value());
            }
        }
        Map map = manageService.doAddEnterprise(meeting.getEnterpriseList(), meeting.getId(), EnumEnterprise.Meeting_AttendEnterprise.toString(), session.getAdmin().getId(), session.getAdmin().getUsername());
        if (!Boolean.valueOf(String.valueOf(map.get("success")))) {
            throw new BusinessException(String.valueOf(map.get("error")));
        }
        return new Object(){
            public boolean success = true;
        };
    }

    @RequestMapping("/addMedia")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doAddMedia(@RequestParam("deleteMedia[]")int[] deleteMedia, Meeting meeting) throws SQLExcutionErrorException {
        if (meetingMapper.getMeetingById(meeting.getId()) == null) throw new NotFoundException();
        Map<String, Object> model = new HashMap<>();
        boolean success = false;
        String error = "";
        if (StringUtils.isNullOrEmpty(error)) {
            if (deleteMedia != null && deleteMedia.length > 1) {
                for (int i = 1; i < deleteMedia.length; i++) {
                    if (enterpriseMapper.getEnterpriseById(deleteMedia[i]) == null) throw new NotFoundException();
                    if (!manageService.doDeleteEnterpriseById(deleteMedia[i])) throw new BusinessException(EnumRemindInfo.Admin_Refresh_Again.value());
                }
            }
            Map map1 = manageService.doAddEnterprise(meeting.getMediaList(), meeting.getId(), EnumEnterprise.Meeting_AttendMedia.toString(), session.getAdmin().getId(), session.getAdmin().getUsername());
            if (!Boolean.valueOf(String.valueOf(map1.get("success")))) {
                throw new BusinessException(String.valueOf(map1.get("error")));
            } else {
                success = true;
            }
        }
        model.put("success", success);
        model.put("error", error);
        return model;
    }

    @RequestMapping("/addNoticeContent")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doAddNoticeContent(Meeting meeting) throws SQLExcutionErrorException {
        if (meetingMapper.getMeetingById(meeting.getId()) == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<>();
        String error = "";
        if (!textCheck.doTextCheckThree(meeting.getPlace())) {
            error = "地点中包含有非法字符";
        } else if (!textCheck.doTextCheckThree(meeting.getPlaceinfo())) {
            error = "地点介绍中包含有非法字符";
        } else {
            if (meeting.getPlacewayList() != null && meeting.getPlacewayList().size() > 0) {
                for (int i = 0; i < meeting.getPlacewayList().size(); i++) {
                    if (!textCheck.doTextCheckThree(String.valueOf(meeting.getPlacewayList().get(i).get("name")))) {
                        error = "第 " + ++i + " 条路线中包含有非法字符";
                        break;
                    }
                }
            }
            if (StringUtils.isNullOrEmpty(error) && meeting.getLinkmanList() != null && meeting.getLinkmanList().size() > 0){
                for (int i=0; i<meeting.getLinkmanList().size(); i++) {
                    if(!textCheck.doTextCheckThree(String.valueOf(meeting.getLinkmanList().get(i).get("name")))) {
                        error = "第 " + ++i + " 个联系人姓名中包含有非法字符";
                        break;
                    }
                    if(!textCheck.doTextCheckThree(String.valueOf(meeting.getLinkmanList().get(i).get("phone")))) {
                        error = "第 " + ++i + " 个联系人电话中包含有非法字符";
                        break;
                    }
                }
            }
        }
        if (StringUtils.isNullOrEmpty(error)) {
            meeting.setPlaceway1("");
            meeting.setPlaceway2("");
            meeting.setPlaceway3("");
            meeting.setPlaceway4("");
            meeting.setPlaceway5("");
            switch (meeting.getPlacewayList().size()) {
                case 5:
                    meeting.setPlaceway5(meeting.getPlacewayList().get(4).get("name").toString());
                case 4:
                    meeting.setPlaceway4(meeting.getPlacewayList().get(3).get("name").toString());
                case 3:
                    meeting.setPlaceway3(meeting.getPlacewayList().get(2).get("name").toString());
                case 2:
                    meeting.setPlaceway2(meeting.getPlacewayList().get(1).get("name").toString());
                case 1:
                    meeting.setPlaceway1(meeting.getPlacewayList().get(0).get("name").toString());
                    break;
                default:
                    break;
            }
            meeting.setLinkmanname1("");
            meeting.setLinkmanphone1("");
            meeting.setLinkmanname2("");
            meeting.setLinkmanphone2("");
            meeting.setLinkmanname3("");
            meeting.setLinkmanphone3("");
            meeting.setLinkmanname4("");
            meeting.setLinkmanphone4("");
            meeting.setLinkmanname5("");
            meeting.setLinkmanphone5("");
            switch (meeting.getLinkmanList().size()) {
                case 5:
                    meeting.setLinkmanname5(meeting.getLinkmanList().get(4).get("name").toString());
                    meeting.setLinkmanphone5(meeting.getLinkmanList().get(4).get("phone").toString());
                case 4:
                    meeting.setLinkmanname4(meeting.getLinkmanList().get(3).get("name").toString());
                    meeting.setLinkmanphone4(meeting.getLinkmanList().get(3).get("phone").toString());
                case 3:
                    meeting.setLinkmanname3(meeting.getLinkmanList().get(2).get("name").toString());
                    meeting.setLinkmanphone3(meeting.getLinkmanList().get(2).get("phone").toString());
                case 2:
                    meeting.setLinkmanname2(meeting.getLinkmanList().get(1).get("name").toString());
                    meeting.setLinkmanphone2(meeting.getLinkmanList().get(1).get("phone").toString());
                case 1:
                    meeting.setLinkmanname1(meeting.getLinkmanList().get(0).get("name").toString());
                    meeting.setLinkmanphone1(meeting.getLinkmanList().get(0).get("phone").toString());
                    break;
                default:
                    break;
            }
            map.put("success", manageService.doUpdateMeeting(meeting));
        } else {
            map.put("success", false);
            map.put("error", error);
        }
        return map;
    }

    @RequestMapping("/addAttendType")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    @Transactional
    public Object doAddAttendMeetingType(@RequestParam("deleteAttendType[]")int[] deleteAttendType, Meeting meeting) throws SQLExcutionErrorException {
        if (meetingMapper.getMeetingById(meeting.getId()) == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<>();
        String error = "";
        if (StringUtils.isNullOrEmpty(String.valueOf(meeting.getAttendmeetingtheme()))){
            error = "报名方式主题不能为空!";
        } else if (!textCheck.doTextCheckThree(String.valueOf(meeting.getAttendmeetingtheme()))) {
            error = "报名方式主题中包含非法字符!";
        } else {
            if (meeting.getAttendTypeList() != null && meeting.getAttendTypeList().size() > 0) {
                for (int i = 0; i < meeting.getAttendTypeList().size(); i++) {
                    if (!textCheck.doTextCheckThree(String.valueOf(meeting.getAttendTypeList().get(i).get("name")))) {
                        error = "第 " + ++i + " 个报名方式, 名称中包含非法字符!";
                        break;
                    }
                    if (!textCheck.doTextCheckThree(String.valueOf(meeting.getAttendTypeList().get(i).get("content")))) {
                        error = "第 " + ++i + " 个报名方式, 内容中包含非法字符!";
                        break;
                    }
                    if (!textCheck.doTextCheckThree(String.valueOf(meeting.getAttendTypeList().get(i).get("example")))) {
                        error = "第 " + ++i + " 个报名方式, 范例中包含非法字符!";
                        break;
                    }
                    if (!textCheck.doTextCheckThree(String.valueOf(meeting.getAttendTypeList().get(i).get("qrcodename1")))) {
                        error = "第 " + ++i + " 个报名方式, 二维码(一)名称中包含非法字符!";
                        break;
                    }
                    if (!textCheck.doTextCheckThree(String.valueOf(meeting.getAttendTypeList().get(i).get("qrcodename2")))) {
                        error = "第 " + ++i + " 个报名方式, 二维码(二)名称中包含非法字符!";
                        break;
                    }
                }
            }
        }
        if (StringUtils.isNullOrEmpty(error)) {
            if (deleteAttendType != null && deleteAttendType.length > 1) {
                for (int i=1; i < deleteAttendType.length; i++) {
                    if (!manageService.doDeleteAttendMeetingType(deleteAttendType[i])) throw new BusinessException(EnumRemindInfo.Admin_Refresh_Again.value());
                }
            }
            Map map1 = manageService.doAddAttendMeetingType(meeting.getAttendTypeList(), meeting.getId(), session.getAdmin().getId(), session.getAdmin().getUsername());
            if (!Boolean.valueOf(String.valueOf(map1.get("success")))) throw new BusinessException(String.valueOf(map.get("error")));
            if (!manageService.doUpdateMeeting(meeting)) throw new BusinessException(EnumRemindInfo.Admin_System_Error.value());
            map.put("success", true);
        } else {
            map.put("success", false);
            map.put("error", error);
        }
        return map;
    }

    @RequestMapping("/exportAttendMeetingList")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public void exportAttendMeetingListExcel(@RequestParam(value = "mid", required = true)int mid, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Meeting meeting = meetingMapper.getMeetingById(mid);
        if (meeting == null) throw new NotFoundException();
        List<AttendMeeting> attendMeetingList = getAttendMeetingList(mid, null, 1, 10000000).getList();
        if (attendMeetingList == null || attendMeetingList.size() == 0) throw new BusinessException("暂时没有报名记录!");
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet(meeting.getName());
        HSSFRow row = sheet.createRow((int) 0);
        HSSFCell cell = row.createCell(0);
        // merge row and column from row 0 to row 0, column 0 to column 7
        sheet.addMergedRegion(new CellRangeAddress(0,0,0,4));
        cell.setCellValue("会议主题: " + meeting.getName());
        row = sheet.createRow((int) 1);
        cell = row.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(1,1,0,4));
        cell.setCellValue("会议时间: " + meeting.getStarttime().toLocalDate() + " " + meeting.getStarttime().toLocalTime() + " - " + meeting.getEndtime().toLocalDate() + " " + meeting.getEndtime().toLocalTime());
        row = sheet.createRow((int) 2);
        cell = row.createCell(0);
        sheet.addMergedRegion(new CellRangeAddress(2,2,0,4));
        cell.setCellValue("发布人: " + meeting.getLasteditmanusername());
        row = sheet.createRow((int) 3);
        CellStyle style = wb.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        sheet.setVerticallyCenter(true);
        sheet.setHorizontallyCenter(true);
        sheet.setColumnWidth(0, 1200);
        sheet.setColumnWidth(1, 10000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 7000);
        sheet.setColumnWidth(4, 3600);
        String[] excelHeader = {"序号", "公司名称", "报名时间", "参会人员", "是否安排住宿"};
        for (int i = 0; i < excelHeader.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(excelHeader[i]);
            cell.setCellStyle(style);
        }
        int i=4;
        int num=1;
        for (AttendMeeting attendMeeting : attendMeetingList) {
            row = sheet.createRow(i);
            row.createCell(0).setCellValue(String.valueOf(num));
            row.createCell(1).setCellValue(attendMeeting.getCompanyname());
            row.createCell(2).setCellValue(attendMeeting.getCreatetime().toLocalDate().toString() + " " + attendMeeting.getCreatetime().toLocalTime().toString());
            if (StringUtils.isNullOrEmpty(attendMeeting.getPersonList().get(0).getPhone())) {
                row.createCell(3).setCellValue(attendMeeting.getPersonList().get(0).getName());
            } else {
                row.createCell(3).setCellValue(attendMeeting.getPersonList().get(0).getName() + " -- " + attendMeeting.getPersonList().get(0).getPhone());
            }
            row.createCell(4).setCellValue(attendMeeting.isstay() ? "是" : "否");
            if (attendMeeting.getPersonList().size() > 1) {
                sheet.addMergedRegion(new CellRangeAddress(i+1, i+attendMeeting.getPersonList().size()-1,0,2));
                sheet.addMergedRegion(new CellRangeAddress(i+1, i+attendMeeting.getPersonList().size()-1,4,4));
                for (int j = 1; j < attendMeeting.getPersonList().size(); j++) {
                    row = sheet.createRow(i + j);
                    if (StringUtils.isNullOrEmpty(attendMeeting.getPersonList().get(j).getPhone())) {
                        row.createCell(3).setCellValue(attendMeeting.getPersonList().get(j).getName());
                    } else {
                        row.createCell(3).setCellValue(attendMeeting.getPersonList().get(j).getName() + " -- " + attendMeeting.getPersonList().get(j).getPhone());
                    }
                }
                i += attendMeeting.getPersonList().size() - 1;
            }
            i++;
            num++;
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");
        String filename = "报名统计_" + meeting.getName() + "_"  + LocalDate.now() + ".xls";
        if(request.getHeader("user-agent").toLowerCase().contains("firefox")) {
            filename =  new String(filename.getBytes("UTF-8"), "ISO-8859-1");
        } else {
            filename = URLEncoder.encode(filename, "UTF-8");
        }
        response.addHeader("Content-Disposition", "attachment; filename="+ filename);
        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
    }

}
