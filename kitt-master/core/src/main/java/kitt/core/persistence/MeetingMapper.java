package kitt.core.persistence;

import kitt.core.domain.AttendMeeting;
import kitt.core.domain.AttendMeetingType;
import kitt.core.domain.Meeting;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/12/21.
 */
public interface MeetingMapper {

    /**
     * admin模块 会议list
     */
    final String MeetingAdminStr1 =
            " <where>" +
            " isdelete=0 " +
            " <if test='content!=null and content!=\"\"'> and name like #{content}</if>" +
            " </where> ";
    @Select("<script>" +
            " select count(1) from meeting " + MeetingAdminStr1 +
            "</script>")
    public int getMeetingCount(@Param("content")String content);

    @Select("<script>" +
            " select * from meeting " + MeetingAdminStr1 +
            " order by isshow desc, sequence limit #{limit} offset #{offset}" +
            "</script>")
    public List<Meeting> getMeetingList(@Param("content")String content,
                                        @Param("limit") int limit,
                                        @Param("offset") int offset);

    public default Pager<Meeting> pageMeeting(String content, int page, int pagesize){
        return Pager.config(this.getMeetingCount(content), (int limit, int offset) -> this.getMeetingList(content, limit, offset))
                .page(page, pagesize);
    }

    //根据id查询会议
    @Select("select * from meeting where id=#{id} and isdelete=0")
    Meeting getMeetingById(int id);

    //添加会议
    @Insert("insert into meeting(name, createtime, lastedittime, lasteditmanid, lasteditmanusername) values(#{name}, " +
            "now(), now(), #{lasteditmanid}, #{lasteditmanusername})")
    @Options(useGeneratedKeys=true)
    int doAddMeetingMethod(Meeting meeting);

    //更新会议
    @Update("<script>" +
            " update meeting set " +
            " <if test='name!=null'> name=#{name}, </if>" +
            " <if test='banner1!=null'> banner1=#{banner1}, </if> " +
            " <if test='banner2!=null'> banner2=#{banner2}, </if> " +
            " <if test='banner3!=null'> banner3=#{banner3}, </if> " +
            " <if test='banner4!=null'> banner4=#{banner4}, </if> " +
            " <if test='banner5!=null'> banner5=#{banner5}, </if> " +
            " <if test='unitshowstyle!=null'> unitshowstyle=#{unitshowstyle}, </if>" +
            " <if test='qrcodename1!=null'> qrcodename1=#{qrcodename1}, </if>" +
            " <if test='qrcodeurl1!=null'> qrcodeurl1=#{qrcodeurl1}, </if>" +
            " <if test='qrcodename2!=null'> qrcodename2=#{qrcodename2}, </if>" +
            " <if test='qrcodeurl2!=null'> qrcodeurl2=#{qrcodeurl2}, </if>" +
            " <if test='introduction!=null'> introduction=#{introduction}, </if>" +
            " <if test='agenda!=null'> agenda=#{agenda}, </if>" +
            " <if test='starttime!=null'> starttime=#{starttime}, </if>" +
            " <if test='endtime!=null'> endtime=#{endtime}, </if>" +
            " <if test='place!=null'> place=#{place}, </if>" +
            " <if test='placeinfo!=null'> placeinfo=#{placeinfo}, </if>" +
            " <if test='placepic!=null'> placepic=#{placepic}, </if>" +
            " <if test='placemap!=null'> placemap=#{placemap}, </if>" +
            " <if test='placeway1!=null'> placeway1=#{placeway1}, </if>" +
            " <if test='placeway2!=null'> placeway2=#{placeway2}, </if>" +
            " <if test='placeway3!=null'> placeway3=#{placeway3}, </if>" +
            " <if test='placeway4!=null'> placeway4=#{placeway4}, </if>" +
            " <if test='placeway5!=null'> placeway5=#{placeway5}, </if>" +
            " <if test='linkmanname1!=null'> linkmanname1=#{linkmanname1}, </if>" +
            " <if test='linkmanphone1!=null'> linkmanphone1=#{linkmanphone1}, </if>" +
            " <if test='linkmanname2!=null'> linkmanname2=#{linkmanname2}, </if>" +
            " <if test='linkmanphone2!=null'> linkmanphone2=#{linkmanphone2}, </if>" +
            " <if test='linkmanname3!=null'> linkmanname3=#{linkmanname3}, </if>" +
            " <if test='linkmanphone3!=null'> linkmanphone3=#{linkmanphone3}, </if>" +
            " <if test='linkmanname4!=null'> linkmanname4=#{linkmanname4}, </if>" +
            " <if test='linkmanphone4!=null'> linkmanphone4=#{linkmanphone4}, </if>" +
            " <if test='linkmanname5!=null'> linkmanname5=#{linkmanname5}, </if>" +
            " <if test='linkmanphone5!=null'> linkmanphone5=#{linkmanphone5}, </if>" +
            " <if test='invitationcard!=null'> invitationcard=#{invitationcard}, </if>" +
            " <if test='receipt!=null'> receipt=#{receipt}, </if>" +
            " <if test='attendmeetingtheme!=null'> attendmeetingtheme=#{attendmeetingtheme}, </if>" +
            " lastedittime=now() where id=#{id} " +
            "</script>")
    int doUpdateMeetingMethod(Meeting meeting);

    //删除会议
    @Update("update meeting set isdelete=1 where id=#{id} and isdelete=0")
    int doDeleteMeetingMethod(int id);

    //设置或取消在前台显示
    @Update("update meeting set isshow=(isshow+1)%2 where id=#{id}")
    int doSetCancelShowMeetingMethod(int id);

    //更改会议顺序
    @Update("update meeting set sequence=#{sequence} where id=#{id}")
    boolean doChangeMeetingSequence(@Param("id") int id,
                                    @Param("sequence") int sequence);


    //根据会议id查询会议的报名方式
    @Select("select * from attendmeetingtype where mid=#{mid} and isdelete=0")
    List<AttendMeetingType> getAttendMeetingTypeListByMid(int mid);

    //添加会议报名
    @Insert("insert into attendmeeting(mid, companyname, ymwusername, isstay, createtime) values(#{mid}, #{companyname}, #{ymwusername}, #{isstay}, now())")
    @Options(useGeneratedKeys=true)
    int addAttendMeetingMethod(AttendMeeting attendMeeting);

    //添加会议报名方式
    @Insert(" insert into attendmeetingtype(mid, name, content, qrcodename1, qrcodeurl1, qrcodename2, qrcodeurl2, " +
            " example, createtime, lastedittime, lasteditmanid, lasteditmanusername) values(#{mid}, #{name}, " +
            " #{content}, #{qrcodename1}, #{qrcodeurl1}, #{qrcodename2}, #{qrcodeurl2}, #{example}, now(), now(), " +
            " #{lasteditmanid}, #{lasteditmanusername})")
    int addAttendMeetingTypeMethod(AttendMeetingType attendMeetingType);

    //更新报名方式
    @Update("<script>" +
            " update attendmeetingtype set " +
            " <if test='name!=null'> name=#{name}, </if>" +
            " <if test='content!=null'> content=#{content}, </if>" +
            " <if test='qrcodename1!=null'> qrcodename1=#{qrcodename1}, </if>" +
            " <if test='qrcodeurl1!=null'> qrcodeurl1=#{qrcodeurl1}, </if>" +
            " <if test='qrcodename2!=null'> qrcodename2=#{qrcodename2}, </if>" +
            " <if test='qrcodeurl2!=null'> qrcodeurl2=#{qrcodeurl2}, </if>" +
            " <if test='example!=null'> example=#{example}, </if>" +
            " lastedittime=now(), lasteditmanid=#{lasteditmanid}, lasteditmanusername=#{lasteditmanusername}" +
            " where id=#{id} " +
            "</script>")
    int updateAttendMeetingTypeMethod(AttendMeetingType attendMeetingType);

    //删除报名方式
    @Update("update attendmeetingtype set isdelete=1 where id=#{id} and isdelete=0")
    int deleteAttendMeetingTypeMethod(int id);

    //根据mid 和 name  查询报名方式
    @Select("select * from attendmeetingtype where mid=#{mid} and name=#{name}")
    AttendMeetingType getAttendMeetingTypeByMidAndName(@Param("mid") int mid,
                                                       @Param("name") String name);

    //根据会议id查询会议的报名 个数
    @Select("<script>" +
            " select count(*) from attendmeeting where mid=#{mid}" +
            " <if test='content!=null'> and companyname like #{content} </if>" +
            "</script>")
    int getAttendMeetingCount(@Param("mid") int mid,
                              @Param("content") String content);

    //根据会议id查询会议的报名列表
    @Select("<script>" +
            " select * from attendmeeting where mid=#{mid}  " +
            " <if test='content!=null'> and companyname like #{content}</if> " +
            " order by id desc limit #{limit} offset #{offset}" +
            "</script>")
    List<AttendMeeting> getAttendMeetingListByMid(@Param("mid")int mid,
                                                  @Param("content") String content,
                                                  @Param("limit")int limit,
                                                  @Param("offset")int offset);

    public default Pager<AttendMeeting> getAttendMeetingList(int id, String content, int page, int pagesize){
        return Pager.config(this.getAttendMeetingCount(id, content), (int limit, int offset) -> this.getAttendMeetingListByMid(id, content, limit, offset))
                .page(page, pagesize);
    }



}
