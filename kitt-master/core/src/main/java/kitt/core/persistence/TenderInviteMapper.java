package kitt.core.persistence;

import kitt.core.domain.InviteTemp;
import kitt.core.domain.result.InviteRs;
import kitt.core.domain.result.TenderInviteRs;
import kitt.core.util.PageQueryParam;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 16/1/15.
 */
public interface TenderInviteMapper {

    //添加投标邀请
    @Insert("insert into tenderinvite (userid,supplyerid,tenderdeclarationid) values(#{userId}, #{supplyerId},#{declareId}) ")
    public void addTenderInvite(@Param("declareId") int declareId, @Param("userId") int userId, @Param("supplyerId") int supplyerId);

    //添加非易煤会员邀请
    @Insert("insert into invitetemp (userid,invitecompanyname,invitephone,status,tenderdeclarationid)" +
            "values (#{userId},#{companyName},#{phone},#{status},#{tenderDeclarationId})")
    @Options(useGeneratedKeys = true)
    public void addTenderInviteTemp(InviteRs tenderInviteRs);


    @Select("select count(1) from tenderinvite where  tenderdeclarationid=#{declareId} and userid=#{userId}  and supplyerid=#{supplyerId}")
    public  int isExistsInvite(@Param("declareId") int declareId, @Param("userId") int userId, @Param("supplyerId") int supplyerId);

    @Select("select * from (" +
            "select count(td.id) inviteCount from tenderdeclaration td   where  td.tenderenddate > now()   and td.tenderopen=0  and td.userid=#{userId} and td.status!='TENDER_EDIT' and td.status!='TENDER_GIVEUP') t1," +
            "(select count(td.id) inviteCompleteCount from tenderdeclaration td  where td.tenderenddate < now() and td.tenderopen=0  and td.userid=#{userId} and td.status!='TENDER_EDIT' and td.status!='TENDER_GIVEUP')t2")
    public Map<String,Integer> findInviteCount(@Param("userId") int userId);

    //公告邀请列表
    @Select("<script>select count(td.id) from tenderdeclaration td  where  td.userid=#{userId} and td.tenderopen=0 and td.status!='TENDER_EDIT' and td.status!='TENDER_GIVEUP' " +
            "<if test='type==\"inviteComplete\"'>and td.tenderenddate <![CDATA[ < ]]> now()</if>" +
            "<if test='type==\"inviteing\"'>and td.tenderenddate <![CDATA[ > ]]> now()</if></script>")
    public  int countInviteInMyDeclaretion(@Param("userId") int userId,@Param("type")String type);

    //公告邀请列表
    @Select("<script>select t1.id,t1.tendercode tenderCode,t1.createtime,t1.status,(t1.inviteCount+IFNULL(t2.inviteCount,0)) inviteCount,t1.inviteId ," +
            "concat_ws(',',t1.companyName,t2.companyName) companyName from(" +
            " select td.id,td.tendercode tenderCode,td.createtime,td.status,count(ti.id) inviteCount,group_concat(c.name) " +
            "companyName,group_concat(c.userid) inviteId from   tenderdeclaration td left join tenderinvite ti on td.id=ti.tenderdeclarationid " +
            " left join users u on u.id=ti.supplyerid   left join companies " +
            "c on c.userid=u.id where td.userid=#{userId} and  td.tenderopen=0  and td.status!='TENDER_EDIT'  and td.status!='TENDER_GIVEUP' " +
            " <if test='type==\"inviteComplete\"'>and td.tenderenddate <![CDATA[ < ]]> now()</if> "+
            "<if test='type==\"inviteing\"'>and td.tenderenddate <![CDATA[ > ]]> now()</if>"+
            " group by td.id ) t1 left join " +
            " (select tenderdeclarationid,group_concat(invitecompanyname) companyName,count(id) inviteCount from   invitetemp where userid=#{userId} and tenderdeclarationid!=0  group by tenderdeclarationid " +
            " ) t2   on t1.id=t2.tenderdeclarationid  " +
            " group by t1.id order by createtime desc limit #{page.pagesize} offset #{page.indexNum}</script>")
    public  List<TenderInviteRs> findInviteInMyDeclaretion(@Param("userId") int userId,@Param("type")String type, @Param("page") PageQueryParam pageQueryParam);

    @Select(
            "select ti.supplyerid inviteId, c.name companyName, u.securephone phone, 1 inviteType from tenderdeclaration td inner join tenderinvite " +
            "ti on ti.tenderdeclarationid=td.id inner join users u on u.id=ti.supplyerid inner join companies "+
            "c on c.userid=u.id where td.id=#{declareId} and td.userid=#{userId} " +
            "union all " +
            "select  id inviteId,invitecompanyname companyName,invitephone  phone,0 inviteType from invitetemp where  userid=#{userId} and tenderdeclarationid=#{declareId} ")
    public List<TenderInviteRs> loadInviteByDeclareId(@Param("declareId") int declareId,@Param("userId") int userId);


    @Select("select u.securephone from tenderinvite ti ,users u where ti.supplyerid=u.id " +
            " and ti.tenderdeclarationid=#{declareId}")
    public List<String> findTenderInvitePhoneByDeclareId(@Param("declareId")int declareId);

    //已邀请的供应商id
    @Select("select ti.supplyerid from tenderinvite ti " +
            "where ti.userid=#{userId} and ti.tenderdeclarationid=#{declareId}")
    public List<Integer> findSupplyerIdByDeclareId(@Param("userId")int userId,@Param("declareId")int declareId);

    //公告下的临时邀请记录id
    @Select("select id from invitetemp ti " +
            "where ti.userid=#{userId} and ti.tenderdeclarationid=#{declareId}")
    public List<Integer> findTempInviteIdByDeclareId(@Param("userId")int userId,@Param("declareId")int declareId);

    //删除公告下邀请
    @Select("<script>delete from tenderinvite " +
            "where userid=#{userId} and tenderdeclarationid=#{declareId}" +
            " and supplyerid in " +
            "<foreach collection='list' index='i' item='supplyerId' open='('  separator=',' close=')'> #{supplyerId} </foreach></script>")
    public List<Integer> deleteTenderInvite(@Param("userId")int userId,@Param("declareId")int declareId,@Param("list") List<Integer> list);

    //删除公告下的临时邀请记录
    @Select("<script>delete from invitetemp " +
            "where userid=#{userId} and tenderdeclarationid=#{declareId}" +
            " and id in " +
            "<foreach collection='list' index='i' item='supplyerId' open='('  separator=',' close=')'> #{supplyerId} </foreach></script>")
    public List<Integer> deleteTenderTempInvite(@Param("userId")int userId,@Param("declareId")int declareId,@Param("list") List<Integer> list);

    //判断当前用户是否有权限进行投标(在白名单&&有投标邀请)
    @Select("select count(1) from tenderinvite ti inner join mysupplyer m on m.supplyerid=ti.supplyerid   where ti.supplyerid=#{userId} and tenderdeclarationid=#{declareId} and m.status=1")
    public int isAllowTender(@Param("userId") int userId,@Param("declareId") int declareId);

    @Select("select * from invitetemp where invitephone=#{phone}")
    public List<InviteTemp> findInviteTempByPhone(@Param("phone") String phone);


    @Select("select invitecompanyname companyName,invitephone  phone,tenderdeclarationid tenderDeclarationId from invitetemp where id=#{id} and tenderdeclarationid=#{declareId}")
    public InviteRs findInviteTempByIdAndDeclareId(@Param("id") int id,@Param("declareId") int declareId);


    //加入黑名单之后,把对应的投标邀请去掉
    @Update("<script>delete from tenderinvite where tenderdeclarationid  in(" +
            "select id from  tenderdeclaration where  status !='TENDER_CHOOSE_CONFIRM' and status !='TENDER_RELEASE_RESULT' " +
            "and tenderopen=0 and userid=#{userId}" +
            ") and supplyerid in " +
            "<foreach collection='list' index='i' item='supplyerId' open='('  separator=',' close=')'> #{supplyerId} </foreach></script>")
    public void updateTenderInvite(@Param("userId") int userId,@Param("list")List<Integer> supplyerIds);

    //删除公告下的所有邀请
    @Delete("delete from tenderinvite where tenderdeclarationid =#{declareId} and userid=#{userId}")
    public void deleteAllTenderinvite(@Param("declareId") int declareId,@Param("userId") int userId);

    //删除公告下的所有邀请
    @Delete("delete from invitetemp where tenderdeclarationid =#{declareId} and userid=#{userId}")
    public void deleteAllTenderTempinvite(@Param("declareId") int declareId,@Param("userId") int userId);



}
