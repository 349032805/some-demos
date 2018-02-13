package kitt.core.persistence;

import kitt.core.domain.InviteTemp;
import kitt.core.domain.MySupplyer;
import kitt.core.domain.result.InviteRs;
import kitt.core.util.PageQueryParam;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 16/1/17.
 */
public interface MySupplyerMapper {


    //添加易煤会员邀请
    @Insert("insert into mysupplyer (userid,supplyerid,status) values(#{userId},#{supplyerId},#{status})")
    public void addInvite(MySupplyer mySupplyer);

    //添加非易煤会员邀请
    @Insert("insert into invitetemp (userid,invitecompanyname,invitephone,status)" +
            "values (#{userId},#{inviteTemp.companyName},#{inviteTemp.phone},#{inviteTemp.status})")
    public void addInviteTemp(@Param("userId") int userId, @Param("inviteTemp") InviteRs tenderInviteRs);


    //修改我的供应商状态
    @Update("<script>update  mysupplyer  set status=#{status} where  userid=#{userId} and supplyerid in " +
            "<foreach collection='list' index='i' item='supplyerId' open='('  separator=',' close=')'> #{supplyerId} </foreach></script>")
    public void updateMySupplyerStatus(@Param("userId") int userId,@Param("status") int status, @Param("list") List<Integer> supplyerIds);

    //删除备选供应商
    @Delete("<script>delete from invitetemp where userid=#{userId} and id in " +
            "<foreach collection='list' index='i' item='tempId' open='(' separator=',' close=')'>#{tempId}</foreach>  " +
            "</script>")
    public  void deleteAlternativeSupplyer(@Param("userId") int userId,@Param("list") List<Integer> ids);

    //删除我的的供应商
    @Delete("<script>delete from mysupplyer where userid=#{userId}  and supplyerid in " +
            "<foreach collection='list' index='i' item='tempId' open='(' separator=',' close=')'>#{tempId}</foreach>  " +
            "</script>")
    public  void deleteMySupplyer(@Param("userId") int userId,@Param("list") List<Integer> ids);


    //根据电话查找是否存在临时邀请的供应商
    @Select("select * from invitetemp where invitephone=#{phone}")
    public  List<InviteTemp> isExistsInviteSupplyerByPhone( @Param("phone")String phone);

    //根据电话查找是否有邀请的供应商
    @Select("select * from invitetemp where invitephone=#{phone} and userid=#{userId}")
    public  List<InviteTemp> isExistsTempInviteByPhone(@Param("userId") int userId, @Param("phone")String phone);

    //我的供应商列表是否存在
    @Select("select s.status from mysupplyer s,users u where s.supplyerid=u.id  and u.securephone=#{phone} and s.userid=#{userId} ")
    public MySupplyer isExistsMySupplyer(@Param("userId")int userId,@Param("phone")String phone);

    //我的备选供应商列表是否存在
    @Select("select count(1) from invitetemp where invitephone=#{phone} and userid=#{userId} ")
    public int isExistsAlternativeSupplyer(@Param("userId")int userId,@Param("phone")String phone);

    //供应商是否存在
    @Select("select c.name companyName,u.id userId,u.securephone phone  from users u,companies c where u.id=c.userid and c.verifystatus='审核通过' and u.securephone=#{phone} ")
    public  InviteRs isExistsSupplyer(@Param("phone") String phone);

    @Select("select c.name companyName,u.id userId,u.securephone phone  from users u,companies c where u.id=c.userid and c.verifystatus='审核通过' and u.id=#{supplyerId} ")
    public  InviteRs findSupplyerById(@Param("supplyerId") int supplyerId);

    //修改临时邀请的供应商状态
    @Update("update invitetemp  set status=#{status} where  invitephone=#{invitephone}")
    public void updateInviteTempByPhone(@Param("invitephone") String phone,@Param("status")int status);

    @Delete("delete from invitetemp where id =#{value}")
    public  void deleteInviteTemp(int id);

    //是否是我的供应商
    @Select("select count(id) from mysupplyer where  userid=#{userId} and supplyerid=#{supplyerId}")
    public int isMySupplyer(@Param("userId") int userId,@Param("supplyerId")int supplyerId);


    //加载我的供应商
    @Select("<script>" +
            "select count(1) from (select count(1) from mysupplyer m,companies c where m.supplyerid=c.userid and m.userid=#{userId}  " +
            "<if test='inviteRs.companyName!=null'> and c.name like '%' #{inviteRs.companyName} '%'</if>" +
            "<if test='inviteRs.status!=null'> and  m.status =#{inviteRs.status}</if>" +
            "group by m.supplyerid ) t1 </script>")
    public int countAllSupplyer(@Param("userId") int userId, @Param("inviteRs") InviteRs inviteRs);


    @Select("select count(1) from mysupplyer where userid=#{userId} and status=1")
    public int countMySupplyerTotalCount(@Param("userId")int userId);


    //加载我的供应商
    @Select("<script>select c.userid userId,c.name companyName,u.registertime registerDate,u.securephone phone, " +
            "case u.verifystatus when '待完善信息' then 2 when '待审核' then 2 when '审核未通过' then 2 when '审核通过' then 3 else 1 end status from mysupplyer m,companies c,users u " +
            "where m.supplyerid=c.userid and u.id=c.userid and m.userid=#{userId}  " +
            "<if test='inviteRs.companyName!=null'> and c.name like '%' #{inviteRs.companyName} '%'</if>" +
            "<if test='inviteRs.status!=null'> and  m.status =#{inviteRs.status}</if>" +
            "group by m.supplyerid limit #{page.pagesize} offset #{page.indexNum}</script>")
    public List<InviteRs> findAllSupplyer(@Param("userId") int userId, @Param("inviteRs") InviteRs inviteRs, @Param("page") PageQueryParam pageQueryParam);



    //加载我的备选供应商
    @Select("<script> select count(1) from invitetemp where userid=#{userId} " +
            "<if test='inviteRs.companyName!=null'> and invitecompanyname like '%' #{inviteRs.companyName} '%'</if>" +
            "</script>")
    public int countAlternativeSupplyer(@Param("userId") int userId, @Param("inviteRs") InviteRs inviteRs);

    //加载备选供应商
    @Select("<script> select temp.id userId,temp.invitecompanyname companyName,temp.invitephone phone, u.registertime registerDate," +
            " case u.verifystatus when '待完善信息' then 2 when '待审核' then 2 when '审核未通过' then 2 when '审核通过' then 3 else 1 end status  from invitetemp temp left join users u  on temp.invitephone=u.securephone where temp.userid=#{userId} " +
            "<if test='inviteRs.companyName!=null'> and temp.invitecompanyname like '%' #{inviteRs.companyName} '%'</if>" +
            " limit #{page.pagesize} offset #{page.indexNum}</script>")
    public List<InviteRs> findAlternativeSupplyer(@Param("userId") int userId, @Param("inviteRs") InviteRs inviteRs,@Param("page") PageQueryParam pageQueryParam);

    @Select("select * from (select count(1) blackListCount from mysupplyer where status=0 and userid=#{userId} ) t1," +
            "         (select count(1) whiteListCount from mysupplyer where status=1 and userid=#{userId}) t2," +
            "         (select count(1) alternativeCount from invitetemp where userid=#{userId}) t3")
    public Map<String,Integer> countMyInvite(@Param("userId") int userId);

    //根据手机号查找未审核通过的供应商
    @Select("select c.name companyName,u.id userId,u.securephone phone  from users u,companies c where u.id=c.userid  and u.securephone=#{phone} ")
    public  String isUnVerifySupplyer(@Param("phone") String phone);

    @Select("select userid from  mysupplyer where supplyerid=#{supplyerId}")
    public List<Integer>  findUserbySupplyerId(@Param("supplyerId") int id);

    @Delete("delete from  mysupplyer where supplyerid=#{supplyerId}")
    public void  deleteSupplyer(@Param("supplyerId") int id);



}
