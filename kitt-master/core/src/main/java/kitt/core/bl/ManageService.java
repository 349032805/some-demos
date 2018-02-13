package kitt.core.bl;

import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.persistence.EnterpriseMapper;
import kitt.core.persistence.IndexBannerMapper;
import kitt.core.persistence.MeetingMapper;
import kitt.core.persistence.PersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mysql.jdbc.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/12/21.
 */
@Service
public class ManageService {
    @Autowired
    private MeetingMapper meetingMapper;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private EnterpriseMapper enterpriseMapper;
    @Autowired
    private IndexBannerMapper indexBannerMapper;

    /*********************************************** 第一部分,会议 start ***********************************************/
    /*********************************************** 第一部分,会议 start ***********************************************/

    /**
     * 添加会议方法,返回会议id
     * @param meeting                      会议对象
     * @return                             会议id
     */
    @Transactional
    public int doAddMeeting(Meeting meeting) throws SQLExcutionErrorException {
        if(meetingMapper.doAddMeetingMethod(meeting) != 1) throw new SQLExcutionErrorException();
        return meeting.getId();
    }

    /**
     * 更新会议方法
     * @param meeting
     */
    @Transactional
    public boolean doUpdateMeeting(Meeting meeting) {
        return meetingMapper.doUpdateMeetingMethod(meeting) == 1;
    }

    /**
     * 添加企业方法
     * @param mapList
     */
    @Transactional
    public Map<String, Object> doAddEnterprise(List<Map<String, Object>> mapList, int parentid, String usetype, int lasteditmanid, String lasteditmanusername) throws SQLExcutionErrorException {
        Map<String, Object> module = new HashMap<>();
        boolean success = true;
        String error = "";
        if(mapList != null) {
            for (Map map : mapList) {
                if((map.get("name")!=null && String.valueOf(map.get("name"))!="" )|| (map.get("logo") != null && String.valueOf(map.get("logo"))!="")) {
                    if (!(usetype.equals(EnumEnterprise.Meeting_AssistUnit.toString()) || usetype.equals(EnumEnterprise.Meeting_HostUnit.toString())) || (!StringUtils.isNullOrEmpty(String.valueOf(map.get("logo"))) && !String.valueOf(map.get("logo")).equals("null"))) {
                        if (map.get("id")!=null && String.valueOf(map.get("id"))!="") {
                            if (enterpriseMapper.updateEnterprise(new Enterprise(Integer.valueOf(String.valueOf(map.get("id"))), String.valueOf(map.get("name")), String.valueOf(map.get("logo")), parentid, usetype, lasteditmanid, lasteditmanusername)) != 1)
                                throw new SQLExcutionErrorException();
                        } else {
                            if (!usetype.equals(EnumEnterprise.Meeting_AttendMedia.toString()) && enterpriseMapper.getEnterpriseByParentIdAndNameAndType(parentid, String.valueOf(map.get("name")), usetype) != null) {
                                if (usetype.equals(EnumEnterprise.Meeting_HostUnit.toString())) {
                                    error = "第 " + (mapList.indexOf(map) + 1) + " 个主办单位已经存在";
                                } else if (usetype.equals(EnumEnterprise.Meeting_AssistUnit.toString())) {
                                    error = "第 " + (mapList.indexOf(map) + 1) + " 个协办单位已经存在";
                                } else if (usetype.equals(EnumEnterprise.Meeting_AttendEnterprise.toString())) {
                                    error = "第 " + (mapList.indexOf(map) + 1) + " 个参会企业已经存在";
                                }
                                success = false;
                                break;
                            } else if (usetype.equals(EnumEnterprise.Meeting_AttendMedia.toString()) && enterpriseMapper.getEnterpriseByParentIdAndLogoAndType(parentid, String.valueOf(map.get("logo")), usetype) != null) {
                                if (usetype.equals(EnumEnterprise.Meeting_AttendMedia.toString())) {
                                    error = "第 " + (mapList.indexOf(map) + 1) + " 个媒体已经存在";
                                    success = false;
                                    break;
                                }
                            }
                            if (enterpriseMapper.addEnterprise(new Enterprise(String.valueOf(map.get("name")), String.valueOf(map.get("logo")), parentid, usetype, lasteditmanid, lasteditmanusername)) != 1)
                                throw new SQLExcutionErrorException();
                        }
                    }
                } else if(map.get("id")!=null && String.valueOf(map.get("id"))!=""){
                    if (!doDeleteEnterpriseById(Integer.valueOf(String.valueOf(map.get("id")))))
                        throw new SQLExcutionErrorException();
                }
            }
        }
        module.put("success", success);
        module.put("error", error);
        return module;
    }

    /**
     * 根据id删除Enterprise
     */
    @Transactional
    public boolean doDeleteEnterpriseById(int id){
        return enterpriseMapper.deleteEnterpriseById(id) == 1;
    }

    /**
     * 删除会议方法
     * @param id                          会议id
     * @return                            true ? false
     */
    public boolean doDeleteMeeting(int id) {
        return meetingMapper.doDeleteMeetingMethod(id) == 1;
    }

    /**
     * 设置,取消在前台显示
     * @param id                          会议id
     * @return                            true ? false
     */
    public boolean doSetCancelShowMeeting(int id) {
        return meetingMapper.doSetCancelShowMeetingMethod(id) == 1;
    }

    /**
     * 更改会议顺序
     * @param id                          会议id
     * @param sequence                    会议顺序
     */
    public boolean doChangeMeetingSequence(int id, int sequence) {
        return meetingMapper.doChangeMeetingSequence(id, sequence);
    }

    /**
     * 报名参加会议
     * @param attendMeeting
     */
    @Transactional
    public boolean addAttendMeeting(AttendMeeting attendMeeting, int parentid, String usertype) throws SQLExcutionErrorException {
        attendMeeting.setMid(parentid);
        if(meetingMapper.addAttendMeetingMethod(attendMeeting) != 1) throw new SQLExcutionErrorException();
        List<Person> personList = attendMeeting.getPersonList();
        for(Person person : personList) {
            if (person.getName() != null && person.getName() != "") {
                person.setParentid(attendMeeting.getId());
                person.setUsetype(usertype);
                if (personMapper.addPerson(person) != 1) throw new SQLExcutionErrorException();
            }
        }
        return true;
    }

    /**
     * 添加嘉宾列表
     * @param guestList
     */
    @Transactional
    public Map<String, Object> doAddGuestMethod(List<Map<String, Object>> guestList, int parentid, String usetype, int lasteditmanid, String lasteditmanusername) throws SQLExcutionErrorException {
        Map<String, Object> model = new HashMap<>();
        boolean success = true;
        if(guestList != null) {
            for (Map map : guestList){
                if (map.get("name")!=null && String.valueOf(map.get("name"))!="" && map.get("position")!=null && String.valueOf(map.get("position"))!="" && map.get("icon")!=null && String.valueOf(map.get("icon"))!="") {
                    if(map.get("id")!=null && String.valueOf(map.get("id"))!=""){
                        if (personMapper.updatePerson(new Person(Integer.valueOf(String.valueOf(map.get("id"))), map.get("name").toString(), map.get("position").toString(), usetype, parentid, map.get("icon").toString(), map.get("icon").toString(), lasteditmanid, lasteditmanusername)) != 1) throw new SQLExcutionErrorException();
                    } else {
                        if (StringUtils.isNullOrEmpty(String.valueOf(map.get("id"))) && personMapper.getPersonByNameAndPositionAndType(String.valueOf(map.get("name")), String.valueOf(map.get("position")), usetype) != null){
                            success = false;
                            map.put("error", "第 " + (guestList.indexOf(map) + 1) + " 个嘉宾已经存在");
                            break;
                        }
                        if (personMapper.addPerson(new Person(map.get("name").toString(), map.get("position").toString(), usetype, parentid, map.get("icon").toString(), map.get("icon").toString(), lasteditmanid, lasteditmanusername)) != 1) throw new SQLExcutionErrorException();
                    }
                } else if (map.get("id")!=null && String.valueOf(map.get("id"))!="") {
                    if (!doDeletePersonMethod(Integer.valueOf(String.valueOf(map.get("id"))))) throw new SQLExcutionErrorException();
                }
            }
        }
        model.put("success", success);
        return model;
    }

    /**
     * 添加报名方式
     * @param attendTypeList
     */
    @Transactional
    public Map<String, Object> doAddAttendMeetingType(List<Map<String, Object>> attendTypeList, int mid, int lasteditmanid, String lasteditmanusername) throws SQLExcutionErrorException {
        Map<String, Object> module = new HashMap<>();
        boolean success = true;
        if(attendTypeList != null){
            for(Map map : attendTypeList){
                if(map.get("name")!=null && String.valueOf(map.get("name"))!="" && map.get("content")!=null && String.valueOf(map.get("content"))!="" && map.get("example")!=null && String.valueOf(map.get("example"))!=""){
                    if (map.get("id")!=null && String.valueOf(map.get("id"))!="") {
                        if (meetingMapper.updateAttendMeetingTypeMethod(new AttendMeetingType(Integer.valueOf(String.valueOf(map.get("id"))), mid, String.valueOf(map.get("name")), String.valueOf(map.get("content")), map.get("qrcodename1") != null ? String.valueOf(map.get("qrcodename1")) : "", map.get("qrcodeurl1") != null ? String.valueOf(map.get("qrcodeurl1")) : "", map.get("qrcodename2") != null ? String.valueOf(map.get("qrcodename2")) : "", map.get("qrcodeurl2") != null ? String.valueOf(map.get("qrcodeurl2")) : "", String.valueOf(map.get("example")), lasteditmanid, lasteditmanusername)) != 1) throw new SQLExcutionErrorException();
                    } else {
                        if (StringUtils.isNullOrEmpty(String.valueOf(map.get("name"))) && meetingMapper.getAttendMeetingTypeByMidAndName(mid, String.valueOf(map.get("name"))) != null) {
                            success = false;
                            module.put("error", "已经存在和第 " + (attendTypeList.indexOf(map) + 1) + " 个名称相同的报名方式!");
                            break;
                        }
                        if (meetingMapper.addAttendMeetingTypeMethod(new AttendMeetingType(mid, String.valueOf(map.get("name")), String.valueOf(map.get("content")), map.get("qrcodename1") != null ? String.valueOf(map.get("qrcodename1")) : "", map.get("qrcodeurl1") != null ? String.valueOf(map.get("qrcodeurl1")) : "", map.get("qrcodename2") != null ? String.valueOf(map.get("qrcodename2")) : "", map.get("qrcodeurl2") != null ? String.valueOf(map.get("qrcodeurl2")) : "", String.valueOf(map.get("example")), lasteditmanid, lasteditmanusername)) != 1) throw new SQLExcutionErrorException();
                    }
                } else if (map.get("id")!=null && String.valueOf(map.get("id"))!=""){
                    if (meetingMapper.deleteAttendMeetingTypeMethod(Integer.valueOf(String.valueOf(map.get("id")))) != 1)
                        throw new SQLExcutionErrorException();
                }
            }
        }
        module.put("success", success);
        return module;
    }

    /**
     * 删除人员
     * @param id               person id
     */
    public boolean doDeletePersonMethod(int id) {
        return personMapper.doDeletePersonById(id) == 1;
    }

    @Transactional
    public boolean doDeleteAttendMeetingType(int id) {
        return meetingMapper.deleteAttendMeetingTypeMethod(id) == 1;
    }


    /*********************************************** 第一部分,会议 end *************************************************/
    /*********************************************** 第一部分,会议 end *************************************************/

    /*********************************************** 第二部分,Banner图片 start *****************************************/
    /*********************************************** 第二部分,Banner图片 start *****************************************/

    /**
     * 改变招标公司图片
     * @param id               公司company id
     * @param path             图片 path
     */
    public boolean changeTenderCompanyPicture(int id, String path, Admin admin) {
        return indexBannerMapper.changeIndexBannersPathById(new IndexBanner(id, path, admin.getId(), admin.getUsername(), admin.getName())) == 1;
    }

    /**
     * 设置或者取消Banner图片在前台显示
     * @param id               公司company id
     */
    public boolean setCancelShowBannerPic(int id, Admin admin) {
        return indexBannerMapper.setCancelShowBannerPic(new IndexBanner(id, admin.getId(), admin.getUsername(), admin.getName())) == 1;
    }

    /**
     * 设置图片的顺序
     * @param id               indexBanner id
     * @param sequence         顺序
     */
    public boolean changeBannerSequence(int id, int sequence, Admin admin) {
        return indexBannerMapper.changeSequence(new IndexBanner(id, sequence, admin.getId(), admin.getUsername(), admin.getName())) == 1;
    }

    public boolean changeBannerURL(int id, String linkurl, Admin admin) {
        return indexBannerMapper.changeBannerLinkURL(new IndexBanner(id, admin.getId(), admin.getUsername(), admin.getName(), linkurl)) == 1;
    }


    /*********************************************** 第二部分,Banner图片 end *******************************************/
    /*********************************************** 第二部分,Banner图片 end *******************************************/


}
