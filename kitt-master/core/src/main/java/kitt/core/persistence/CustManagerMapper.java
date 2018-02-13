package kitt.core.persistence;

import kitt.core.domain.Custvisitrecord;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by lich on 16/1/19.
 */
public interface CustManagerMapper {
    //获取客服访问总数
    @Select("<script>select count(*) from custvisitrecord" +
            "<where> 1=1"+
            "<if test='needtype!=null and needtype!=\"\"'> and needtype=#{needtype}</if>" +
            "<if test='enterpricetype!=null and enterpricetype!=\"\"'> and enterpricetype=#{enterpricetype}</if>" +
            "<if test='enterpriceprovince!=null and enterpriceprovince!=0'> and enterpriceprovince=#{enterpriceprovince}</if>" +
            "<if test='enterpricecity!=null and enterpricecity!=0'> and enterpricecity=#{enterpricecity}</if>" +
            "<if test='companyname!=null and companyname!=\"\"'> and companyname like #{companyname}</if>" +
            "<if test='offlineteam!=null and offlineteam!=0'> and offlineteam=#{offlineteam}</if>" +
            "<if test='offlineteampeople!=null and offlineteampeople!=0'> and offlineteampeople=#{offlineteampeople}</if>" +
            "</where>"+
            "</script> ")
    public int countCust(@Param("needtype")String needtype,@Param("enterpricetype")String enterpricetype,@Param("enterpriceprovince")String enterpriceprovince,@Param("enterpricecity")String enterpricecity,@Param("companyname")String companyname,@Param("offlineteam")String offlineteam,@Param("offlineteampeople")String offlineteampeople);
    //获取客服访问列表

    @Select("<script>select * from custvisitrecord " +
            "<where> 1=1"+
            "<if test='needtype!=null and needtype!=\"\"'> and needtype=#{needtype}</if>" +
            "<if test='enterpricetype!=null and enterpricetype!=\"\"'> and enterpricetype=#{enterpricetype}</if>" +
            "<if test='enterpriceprovince!=null and enterpriceprovince!=0'> and enterpriceprovince=#{enterpriceprovince}</if>" +
            "<if test='enterpricecity!=null and enterpricecity!=0'> and enterpricecity=#{enterpricecity}</if>" +
            "<if test='companyname!=null and companyname!=\"\"'> and companyname like #{companyname}</if>" +
            "<if test='offlineteam!=null and offlineteam!=0'> and offlineteam=#{offlineteam}</if>" +
            "<if test='offlineteampeople!=null and offlineteampeople!=0'> and offlineteampeople=#{offlineteampeople}</if>" +
            "</where>"+
            "order by lastupdatetime desc limit #{limit} offset #{offset}</script>")
    public List<Custvisitrecord> findCustList(@Param("limit") int limit, @Param("offset") int offset,@Param("needtype")String needtype,@Param("enterpricetype")String enterpricetype,@Param("enterpriceprovince")String enterpriceprovince,@Param("enterpricecity")String enterpricecity,@Param("companyname")String companyname,@Param("offlineteam")String offlineteam,@Param("offlineteampeople")String offlineteampeople);

    @Select("<script>select * from custvisitrecord " +
            "<where> 1=1"+
            "<if test='needtype!=null and needtype!=\"\"'> and needtype=#{needtype}</if>" +
            "<if test='enterpricetype!=null and enterpricetype!=\"\"'> and enterpricetype=#{enterpricetype}</if>" +
            "<if test='enterpriceprovince!=null and enterpriceprovince!=0'> and enterpriceprovince=#{enterpriceprovince}</if>" +
            "<if test='enterpricecity!=null and enterpricecity!=0'> and enterpricecity=#{enterpricecity}</if>" +
            "<if test='companyname!=null and companyname!=\"\"'> and companyname like #{companyname}</if>" +
            "<if test='offlineteam!=null and offlineteam!=0'> and offlineteam=#{offlineteam}</if>" +
            "<if test='offlineteampeople!=null and offlineteampeople!=0'> and offlineteampeople=#{offlineteampeople}</if>" +
            "</where>"+
            " order by lastupdatetime desc "+
            "</script>")
    public List<Custvisitrecord> findCustDatas(@Param("needtype")String needtype,@Param("enterpricetype")String enterpricetype,@Param("enterpriceprovince")String enterpriceprovince,@Param("enterpricecity")String enterpricecity,@Param("companyname")String companyname,@Param("offlineteam")String offlineteam,@Param("offlineteampeople")String offlineteampeople);



    public default Pager<Custvisitrecord> getCustList(int page, int pagesize,String needtype,String enterpricetype,String enterpriceprovince,String enterpricecity,String companyname,String offlineteam,String offlineteampeople){
        return Pager.config(this.countCust(needtype,enterpricetype,enterpriceprovince,enterpricecity,companyname,offlineteam,offlineteampeople), (int limit, int offset) -> this.findCustList(limit, offset,needtype,enterpricetype,enterpriceprovince,enterpricecity,companyname,offlineteam,offlineteampeople))
                .page(page, pagesize);
    }


    @Insert("<script>insert into custvisitrecord(visitetime,companyname,enterpricetype,enterpriceprovince,enterpricecity,enterpricelocationdetail,needtype,infoorigin," +
            "problemdesc,answerinfo,onlinepeople,offlineteam,offlineteampeople,resultfeedback, " +
            "<if test='returnvisittime!=null '> returnvisittime,</if>" +
            "<if test='hangnumber!=null and hangnumber!=\"\"'> hangnumber,</if>" +
            "<if test='tradenumber!=null and tradenumber!=\"\" '> tradenumber,</if>" +
            "<if test='tradeprice!=null and tradeprice!=\"\"  '> tradeprice,</if>" +
            "<if test='needamout!=null and needamout!=\"\" '> needamout,</if>" +
            "<if test='pickuptime!=null '> pickuptime,</if>" +
            "<if test='ncv1!=null and ncv1!=\"\" '> ncv1,</if>" +
            "<if test='tm1!=null and tm1!=\"\" '> tm1,</if>" +
            "<if test='rs1!=null and rs1!=\"\" '> rs1,</if>" +
            "<if test='adv1!=null and adv1!=\"\" '> adv1,</if>" +
            "<if test='adv2!=null and adv2!=\"\" '> adv2,</if>" +
            "<if test='ncv2!=null and ncv2!=\"\" '> ncv2,</if>" +
            "<if test='tm2!=null and tm2!=\"\" '> tm2,</if>" +
            "<if test='rs2!=null and rs2!=\"\" '> rs2,</if>" +
            //"<if test='otherneedtype!=null and otherneedtype!=\"\" '> otherneedtype,</if>" +
            //"<if test='otherenterpricetype!=null and otherenterpricetype!=\"\" '> otherenterpricetype,</if>" +
            //"<if test='othercoaltype!=null and othercoaltype!=\"\" '> othercoaltype,</if>" +
            "otherneedtype,otherenterpricetype,othercoaltype,returnvisitresult,registerphone,hangtype," +
            "coaltype,pickupgoodstype,paytype,pickupprovince,pickupcity,otherindicators,remark) " +
            "values(#{visitetime},#{companyname},#{enterpricetype},#{enterpriceprovince},#{enterpricecity},#{enterpricelocationdetail},#{needtype},#{infoorigin}," +
            "#{problemdesc},#{answerinfo},#{onlinepeople},#{offlineteam},#{offlineteampeople},#{resultfeedback}," +
            "<if test='returnvisittime!=null'> #{returnvisittime},</if>" +
            "<if test='hangnumber!=null and hangnumber!=\"\" '> #{hangnumber},</if>" +
            "<if test='tradenumber!=null and tradenumber!=\"\" '> #{tradenumber},</if>" +
            "<if test='tradeprice!=null and tradeprice!=\"\" '> #{tradeprice},</if>" +
            "<if test='needamout!=null and needamout!=\"\"'> #{needamout},</if>" +
            "<if test='pickuptime!=null '> #{pickuptime},</if>" +
            "<if test='ncv1!=null and ncv1!=\"\"'> #{ncv1},</if>" +
            "<if test='tm1!=null and tm1!=\"\"'> #{tm1},</if>" +
            "<if test='rs1!=null and rs1!=\"\"'> #{rs1},</if>" +
            "<if test='adv1!=null and adv1!=\"\"'> #{adv1},</if>" +
            "<if test='adv2!=null and adv2!=\"\"'> #{adv2},</if>" +
            "<if test='ncv2!=null and ncv2!=\"\"'> #{ncv2},</if>" +
            "<if test='tm2!=null and tm2!=\"\"'> #{tm2},</if>" +
            "<if test='rs2!=null and rs2!=\"\"'> #{rs2},</if>" +
            //"<if test='otherneedtype!=null and otherneedtype!=\"\"'> #{otherneedtype},</if>" +
            //"<if test='otherenterpricetype!=null and otherenterpricetype!=\"\"'> #{otherenterpricetype},</if>" +
            //"<if test='othercoaltype!=null and othercoaltype!=\"\"'> #{othercoaltype},</if>" +
            "#{otherneedtype},#{otherenterpricetype},#{othercoaltype},#{returnvisitresult},#{registerphone},#{hangtype}," +
            "#{coaltype},#{pickupgoodstype},#{paytype},#{pickupprovince},#{pickupcity},#{otherindicators},#{remark})</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public void addCustvisitrecord(Custvisitrecord custvisitrecord);


    @Update("<script>update custvisitrecord set remark=#{remark},enterpriceprovince=#{enterpriceprovince},enterpricecity=#{enterpricecity}," +
            "enterpricelocationdetail=#{enterpricelocationdetail},infoorigin=#{infoorigin}," +
            "problemdesc=#{problemdesc},answerinfo=#{answerinfo},onlinepeople=#{onlinepeople},offlineteam=#{offlineteam},offlineteampeople=#{offlineteampeople},resultfeedback=#{resultfeedback},returnvisitresult=#{returnvisitresult}," +
            "registerphone=#{registerphone},hangtype=#{hangtype}," +
            "<if test='hangnumber!=null and hangnumber!=\"\"'>  hangnumber=#{hangnumber},</if>" +
            "<if test='tradenumber!=null and tradenumber!=\"\"'>  tradenumber=#{tradenumber},</if>" +
            "<if test='tradeprice!=null and tradeprice!=\"\"'>  tradeprice=#{tradeprice},</if>" +
            "<if test='needamout!=null and needamout!=\"\"'>  needamout=#{needamout},</if>" +
            "<if test='pickuptime!=null '>  pickuptime=#{pickuptime},</if>" +
            "<if test='returnvisittime!=null '>  returnvisittime=#{returnvisittime},</if>" +
            "<if test='ncv1!=null and ncv1!=\"\"'>ncv1=#{ncv1},</if>" +
            "<if test='tm1!=null and tm1!=\"\"'> tm1=#{tm1},</if>" +
            "<if test='rs1!=null and rs1!=\"\"'> rs1=#{rs1},</if>" +
            "<if test='adv1!=null and adv1!=\"\"'>adv1=#{adv1},</if>" +
            "<if test='adv2!=null and adv2!=\"\"'> adv2=#{adv2},</if>" +
            "<if test='ncv2!=null and ncv2!=\"\"'>   ncv2=#{ncv2},</if>" +
            "<if test='tm2!=null and tm2!=\"\"'> tm2=#{tm2},</if>" +
            "<if test='rs2!=null and rs2!=\"\"'>  rs2=#{rs2},</if>" +
            "coaltype=#{coaltype},othercoaltype=#{othercoaltype}, " +
            "pickupgoodstype=#{pickupgoodstype}, paytype=#{paytype},pickupprovince=#{pickupprovince}, pickupcity=#{pickupcity}, otherindicators=#{otherindicators}" +
            " where id=#{id}</script>")
    public int alterCustvisitrecord(Custvisitrecord custvisitrecord);


    //根据id查询
    @Select("select * from custvisitrecord where id=#{id}")
    public Custvisitrecord findByCustById(@Param("id") int id);






















}
