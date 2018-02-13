package kitt.site.controller;

import com.mysql.jdbc.StringUtils;
import kitt.core.bl.ManageService;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.persistence.EnterpriseMapper;
import kitt.core.persistence.MeetingMapper;
import kitt.core.persistence.PersonMapper;
import kitt.site.basic.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by liuxinjie on 15/12/21.
 */
@Controller
public class MeetingController {
    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private EnterpriseMapper enterpriseMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private ManageService manageService;

    @RequestMapping("/meeting/index")
    public String indexTemplate(@RequestParam(value = "id", required = true)int id, Map<String, Object> model) {
        Meeting meeting = meetingMapper.getMeetingById(id);
        if(meeting == null || !meeting.isshow()) throw new NotFoundException("该会议不存在或已经过期!");
        model.put("meeting", meeting);
        model.put("bannerList", getMeetingBannerList(meeting));
        model.put("hostUnitList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_HostUnit.toString(), meeting.getId()));
        model.put("assistUnitList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AssistUnit.toString(), meeting.getId()));
        model.put("guestList", personMapper.getPersonListByTypeAndParentid(EnumPerson.Meeting_HonoredGuest.toString(), meeting.getId()));
        model.put("attendEnterpriseList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AttendEnterprise.toString(), meeting.getId()));
        model.put("attendMediaList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AttendMedia.toString(), meeting.getId()));
        model.put("attendMeetingTypeList", meetingMapper.getAttendMeetingTypeListByMid(meeting.getId()));
        String meetingTime = "";
        if (meeting.getStarttime().toLocalTime().toString().equals(meeting.getEndtime().toLocalTime().toString())) {
            meetingTime = meeting.getStarttime().getYear() + "年" + meeting.getStarttime().getMonthValue() + "月" + meeting.getStarttime().getDayOfMonth() + "日" + " " + meeting.getStarttime().getHour() + "点" + meeting.getStarttime().getMinute() + "分" + " - " + meeting.getEndtime().getHour() + "点" + meeting.getEndtime().getMinute() + "分";
        } else {
            meetingTime = meeting.getStarttime().getYear() + "年" + meeting.getStarttime().getMonthValue() + "月" + meeting.getStarttime().getDayOfMonth() + "日" + " - " + meeting.getEndtime().getYear() + "年" + meeting.getEndtime().getMonthValue() + "月" + meeting.getEndtime().getDayOfMonth() + "日";
        }
        model.put("meetingTime", meetingTime);
        return "meeting/indexTemplate";
    }

    /**
     * 会议详细
     * @param id                       会议id
     */
    @RequestMapping("/meeting/detail")
    public Object doShowMeetingDetail(@RequestParam(value = "id", required = true)int id, Map<String, Object> model) {
        Meeting meeting = meetingMapper.getMeetingById(id);
        if(meeting == null || !meeting.isshow()) throw new NotFoundException("该会议不存在或已经过期!");
        model.put("meeting", meeting);
        model.put("bannerList", getMeetingBannerList(meeting));
        model.put("hostUnitList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_HostUnit.toString(), meeting.getId()));
        model.put("assistUnitList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AssistUnit.toString(), meeting.getId()));
        model.put("guestList", personMapper.getPersonListByTypeAndParentid(EnumPerson.Meeting_HonoredGuest.toString(), meeting.getId()));
        model.put("attendEnterpriseList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AttendEnterprise.toString(), meeting.getId()));
        model.put("attendMediaList", enterpriseMapper.getEnterpriseListByTypeAndParentId(EnumEnterprise.Meeting_AttendMedia.toString(), meeting.getId()));
        model.put("attendMeetingTypeList", meetingMapper.getAttendMeetingTypeListByMid(meeting.getId()));
        String meetingTime = "";
        if (meeting.getStarttime().toLocalDate().toString().equals(meeting.getEndtime().toLocalDate().toString())) {
            meetingTime = meeting.getStarttime().getYear() + "年" + meeting.getStarttime().getMonthValue() + "月" + meeting.getStarttime().getDayOfMonth() + "日" + " " + meeting.getStarttime().getHour() + "点" + meeting.getStarttime().getMinute() + "分" + " - " + meeting.getEndtime().getHour() + "点" + meeting.getEndtime().getMinute() + "分";
        } else {
            meetingTime = meeting.getStarttime().getYear() + "年" + meeting.getStarttime().getMonthValue() + "月" + meeting.getStarttime().getDayOfMonth() + "日" + " - " + meeting.getEndtime().getYear() + "年" + meeting.getEndtime().getMonthValue() + "月" + meeting.getEndtime().getDayOfMonth() + "日";
        }
        model.put("meetingTime", meetingTime);
        return "meeting/index";
    }

    List<String> getMeetingBannerList(Meeting meeting){
        List<String> bannerList = new ArrayList<String>();
        String[] banners = {meeting.getBanner1(), meeting.getBanner2(), meeting.getBanner3(), meeting.getBanner4(), meeting.getBanner5()};
        for(int i=0; i<banners.length; i++){
            if(!StringUtils.isNullOrEmpty(banners[i])) bannerList.add(banners[i]);
        }
        return bannerList;
    }

    //保存参会人员
    @RequestMapping("/meeting/attendMeeting")
    @ResponseBody
    @Transactional
    public boolean doAttendMeeting(AttendMeeting attendMeeting, @RequestParam(value = "meetingid", required = true)int mid) throws SQLExcutionErrorException {
        return manageService.addAttendMeeting(attendMeeting, mid, EnumPerson.Meeting_AttendGuest.toString());
    }

    //判断是否还可以报名(还没有到报名截止时间)
    @RequestMapping("/meeting/checkIfAttend")
    @ResponseBody
    public boolean doCheckIfAttend(@RequestParam(value = "id", required = true)int id) {
        Meeting meeting = meetingMapper.getMeetingById(id);
        if(meeting == null) throw new NotFoundException("该会议不存在,或者已经过期!");
        return LocalDate.now().isBefore(meeting.getStarttime().toLocalDate()) ? true : false;
    }
}
