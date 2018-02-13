package kitt.core.persistence;

import kitt.core.domain.Admin;
import kitt.core.domain.Bid;
import kitt.core.domain.Company;
import kitt.core.domain.TenderDeclaration;
import kitt.core.util.PageQueryParam;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 15/11/10.
 */
public interface TenderdeclarMapper {

    @Insert("<script>" +
            "insert into tenderdeclaration(" +
            "<if test='userid!=null'>userid,</if>" +
            "<if test='attachmentfilename!=null'>attachmentfilename,</if>" +
            "<if test='tendertype!=null'>tendertype,</if>" +
            "<if test='requirements!=null'>requirements,</if>" +
            "<if test='comments!=null'>comments,</if>" +
            "<if test='status!=null'>status,</if>" +
            "<if test='companyid!=null'>companyid,</if>" +
            "<if test='tendercode!=null'>tendercode,</if>" +
            "<if test='tendername!=null'>tendername,</if>" +
            "<if test='tenderunits!=null'>tenderunits,</if>" +
            "<if test='receiptunits!=null'>receiptunits,</if>" +
            "<if test='capitalsettlement!=null'>capitalsettlement,</if>" +
            "<if test='margins!=null'>margins,</if>" +
            "<if test='contractconbegindate!=null'>contractconbegindate,</if>" +
            "<if test='contractconenddate!=null'>contractconenddate,</if>" +
            "<if test='tenderbegindate!=null'>tenderbegindate,</if>" +
            "<if test='tenderenddate!=null'>tenderenddate,</if>" +
            "<if test='starttenderdate!=null'>starttenderdate,</if>" +
            "<if test='rewardpenaltyclause!=null'>rewardpenaltyclause,</if>" +
            "<if test='attachmentpath!=null'>attachmentpath,</if>" +
            "<if test='purchaseamount!=null'>purchaseamount,</if>" +
            "version,createtime,tenderopen) values(" +
            "<if test='userid!=null'>#{userid},</if>" +
            "<if test='attachmentfilename!=null'>#{attachmentfilename},</if>" +
            "<if test='tendertype!=null'>#{tendertype},</if>" +
            "<if test='requirements!=null'>#{requirements},</if>" +
            "<if test='comments!=null'>#{comments},</if>" +
            "<if test='status!=null'>#{status},</if>" +
            "<if test='companyid!=null'>#{companyid},</if>" +
            "<if test='tendercode!=null'>#{tendercode},</if>" +
            "<if test='tendername!=null'>#{tendername},</if>" +
            "<if test='tenderunits!=null'>#{tenderunits},</if>" +
            "<if test='receiptunits!=null'>#{receiptunits},</if>" +
            "<if test='capitalsettlement!=null'>#{capitalsettlement},</if>" +
            "<if test='margins!=null'>#{margins},</if>" +
            "<if test='contractconbegindate!=null'>#{contractconbegindate},</if>" +
            "<if test='contractconenddate!=null'>#{contractconenddate},</if>" +
            "<if test='tenderbegindate!=null'>#{tenderbegindate},</if>" +
            "<if test='tenderenddate!=null'>#{tenderenddate},</if>" +
            "<if test='starttenderdate!=null'>#{starttenderdate},</if>" +
            "<if test='rewardpenaltyclause!=null'>#{rewardpenaltyclause},</if>" +
            "<if test='attachmentpath!=null'>#{attachmentpath},</if>" +
            "<if test='purchaseamount!=null'>#{purchaseamount},</if>" +
            "0,now(),#{tenderopen})" +
            "</script>")
    @Options(useGeneratedKeys = true)
    public void addTenderdeclar(TenderDeclaration tenderDeclaration);


    @Update("<script>" +
            "update tenderdeclaration set tenderopen=#{tenderopen}," +
            "<if test='tendertype!=null'> tendertype=#{tendertype},</if>" +
            "<if test='requirements!=null'> requirements=#{requirements},</if>" +
            "<if test='comments!=null'> comments=#{comments},</if>" +
            "<if test='status!=null'> status=#{status},</if>" +
            "attachmentfilename=#{attachmentfilename},tendercode=#{tendercode},tendername=#{tendername},tenderunits=#{tenderunits},receiptunits=#{receiptunits}," +
            "capitalsettlement=#{capitalsettlement},margins=#{margins},contractconbegindate=#{contractconbegindate},contractconenddate=#{contractconenddate},tenderbegindate=#{tenderbegindate},tenderenddate=#{tenderenddate}," +
            "starttenderdate=#{starttenderdate},rewardpenaltyclause=#{rewardpenaltyclause},purchaseamount=#{purchaseamount},attachmentpath=#{attachmentpath},version=version+1 " +
            "where id=#{id} and userid=#{userid} " +
            "</script>")
    public int updateTenderdeclar(TenderDeclaration declaration);

    @Delete("delete from tenderdeclaration where id=#{id} and userid=#{userid} ")
    public int deleteTenderdeclarationByid(@Param("id")int id,@Param("userid")int userid);

    @Update("update tenderdeclaration set purchaseamount=#{purchaseamount} where id=#{id} and userid=#{userid} ")
    public int updatePurchaseamount(@Param("id") int id, @Param("purchaseamount") BigDecimal purchaseamount, @Param("userid")int userid);

    @Update("update tenderdeclaration set status=#{status} where id=#{id} and userid=#{userid}")
    public int updateStatusById(@Param("id") int id, @Param("status") String status, @Param("userid")int userid);

    @Update("update tenderdeclaration set scantime=scantime+1 where id=#{id}")
    public int updateDeclarScantimes(@Param("id") int id);

    @Update("update tenderdeclaration set attachmentpath=#{attachmentpath} where id=#{id} and userid=#{userid} ")
    public int updatePathById(@Param("id") int id, @Param("attachmentpath") String attachmentpath, @Param("userid")int userid);

    @Select("select * from tenderdeclaration where id=#{id} and userid=#{userId}")
    public TenderDeclaration getTenderdeclarByIdUserId(@Param("id") int id,@Param("userId") int userId);

    @Select("select * from tenderdeclaration where id=#{id}")
    TenderDeclaration findTendDeclarById(int id);

    @Select("select * from tenderdeclaration where id=#{id} and userid=#{userId}")
    TenderDeclaration findTendDeclarByIdAndUserId(@Param("id")int id,@Param("userId") int userId);

    @Select("select c.name from tenderdeclaration t, companies c where t.companyid=c.id and t.id=#{id}")
    String findCompanyName(int tenderdeclarationid);

    @Select("select * from tenderdeclaration where companyid=#{companyid} and status=#{status} order by lastupdatetime desc")
    List<TenderDeclaration> findTendDeclarByCompanyId(@Param("companyid") int companyid, @Param("status") String status);


    @Select("<script>" +
            "select * from tenderdeclaration where companyid=#{companyid} " +
            "<if test='status!=null and status!=\"\"'> and status=#{status}</if>" +
            "and status in('TENDER_START','TENDER_VERIFY_PASS','TENDER_CHOOSE_CONFIRM','TENDER_RELEASE_RESULT') order by case status   when 'TENDER_START' then  #{i1} when 'TENDER_VERIFY_PASS' then  #{i2} when 'TENDER_CHOOSE_CONFIRM'  then #{i3} when 'TENDER_RELEASE_RESULT' then  #{i4}  end , createtime desc  limit 0,3</script>")
    List<TenderDeclaration> findTenderDecalrByCompanyId(@Param("companyid") int companyid, @Param("status") String status,@Param("i1") int i1, @Param("i2") int i2, @Param("i3") int i3, @Param("i4") int i4);

    @Select("<script>" +
            "select count(*) from tenderdeclaration where companyid=#{companyid} " +
            "<if test='status!=null and status!=\"\"'> and status=#{status}</if>" +
            "and status in('TENDER_START','TENDER_VERIFY_PASS','TENDER_CHOOSE_CONFIRM','TENDER_RELEASE_RESULT') </script>")
    int countTenderDecalrByCompanyId(@Param("companyid") int companyid, @Param("status") String status);



//    @Select(  "<script>select t.companyid as companyid,t.createtime as createtime,sum(t.purchaseamount) as purchaseamount,c.`name` as companyName,c.logopic as logopic from tenderdeclaration t,companies c"+
//            " where t.companyid=c.id and t.status in ('TENDER_VERIFY_PASS','TENDER_START','TENDER_CHOOSE_CONFIRM','TENDER_RELEASE_RESULT')"+
//            "<if test='status!=null and status!=\"\"'> and t.status=#{status}</if>" +
//            "<if test='name!=null and name!=\"\"'>and t.companyid in (select id  from companies where name like #{name} and istender=1) </if>"+
//            "  group by t.companyid"+
//            "  order by t.createtime ,t.companyid asc</script>")

    @Select("<script>select tt.companyid as companyid,tt.createtime as createtime,sum(tt.purchaseamount) as purchaseamount,tt.`companyName`"+
    "as companyName,tt.logopic as logopic  FROM("+
            "select t.companyid as companyid,t.id as id,t.createtime as createtime,t.purchaseamount as purchaseamount,c.`name`"+
            "as companyName,c.logopic as logopic from"+
            " tenderdeclaration t,companies c where t.companyid=c.id"+
            "<if test='status!=null and status!=\"\"'> and t.status=#{status}</if>" +
            "<if test='name!=null and name!=\"\"'>and t.companyid in (select id  from companies where name like #{name} and istender=1) </if>"+
            " and t.status in ('TENDER_VERIFY_PASS','TENDER_START','TENDER_CHOOSE_CONFIRM','TENDER_RELEASE_RESULT') order by t.createtime desc) tt"+
   " group by tt.companyid order by tt.createtime desc,tt.companyid asc</script>")
    List<TenderDeclaration> findTenderDecalrCount(@Param("status") String status, @Param("name") String name);


    @Select("select a.* from users u,tenderdeclaration t,companies c,admins a where u.id=c.userid and c.id=t.companyid and u.traderid=a.id  and t.id=#{id}")
    Admin findUserInfoByDecalrId(@Param("id") int id);

    @Select(" select a.* from companies c,users u,admins a where c.userid=u.id and u.traderid=a.id and c.id=#{id}")
    Admin findUserInfoByCompanyId(@Param("id") int id);

    @Select(" select  c.`name` as name,c.logopic as logopic,c.bannerpic as bannerpic from tenderdeclaration t ,companies c where t.`companyid`=c.id and t.id=#{id}")
    Company findCompanyByDeId(@Param("id") int id);

    @Select(" select  *  from companies where id=#{id}")
    Company findCompanyById(@Param("id") int id);


    @Select("<script> select count(*) from tenderdeclaration " +
            "<where><if test='i!=0'> id in <foreach collection='list' index='index' item='item'  open=\"(\" separator=\",\" close=\")\" >  #{item}</foreach></if></where> </script>")
    int countTenderDeclaration(@Param("i") int i, @Param("list") List<Integer> list);


    @Select("<script>select * from tenderdeclaration " +
            "<where><if test='i!=0'> id  in <foreach collection='list' index='index' item='item'  open=\"(\" separator=\",\" close=\")\" > #{item}</foreach></if> " +
            "</where> order by verifydate desc limit #{limit} offset #{offset} </script>")
    List<TenderDeclaration> findTenderDecalrByCompanyName(@Param("i") int i, @Param("list") List<Integer> list, @Param("limit") int limit,
                                                          @Param("offset") int offset);

//    default Pager<TenderDeclaration> pageTenderDeclaration(List<Integer> list, int page, int pagesize) {
//        return Pager.config(this.countTenderDeclaration(list), (int limit, int offset) -> this.findTenderDecalrByCompanyName(list, limit, offset))
//                .page(page, pagesize);
//    }

    //增加了时间判断, 2015-12-20,  因为之前有和电厂的测试数据
    @Select("select b.id as bid,b.lastupdatetime, u.securephone as securephone,t.contractconbegindate as contractconbegindate from bid b ,users u, tenderdeclaration t where b.userid=u.id and b.`tenderdeclarationid`=t.id and b.`mytenderstatus`='MYTENDER_SUCCEED' and b.createtime > '2015-12-20'")
    List<Map<String,Object>> findBidRecords();

    @Select("<script>select * from tenderdeclaration " +
            "<where> status in ('TENDER_VERIFY_PASS','TENDER_START','TENDER_CHOOSE_CONFIRM','TENDER_RELEASE_RESULT')" +
            " <if test='companyid!=0'>  and companyid =#{companyid}</if>" +
            "<if test='status!=null and status!=\"\"'> and status=#{status}</if>" +
            "<if test='createtime!=null and createtime!=\"\"'> and DATE_FORMAT(verifydate,'%Y-%m') like #{createtime}</if>" +
            "</where> </script>")
    List<TenderDeclaration> findTenders(@Param("status") String status, @Param("companyid") int companyid, @Param("createtime") String createtime);


    @Select("<script>select i.receiptunits as tendername,i.sequence as projectId, p.sequence as packetId,m.deliverymode as deliverymode ,m.competecompanyname as releasecompanyname,m.price as price,m.needamount as purchaseamount from mytender m,tenderpacket p,tenderdeclaration t,tenderitem i " +
            "<where> t.id=p.tenderdeclarationid and p.tenderitemid=i.id and m.`tenderpacketid`=p.id and m.status='MYTENDER_SUCCEED' and t.userid=#{userid} and t.id=#{id}" +
            " </where> order by m.price asc </script>")
    List<Map<String, Object>> findBidTender(@Param("id") int id,@Param("userid") int userid);

    @Select("<script>select i.receiptunits as tendername,i.sequence as projectId, p.sequence as packetId,m.competecompanyname as releasecompanyname,m.price as price,m.needamount as purchaseamount from mytender m,tenderpacket p,tenderdeclaration t,tenderitem i " +
            "<where> t.id=p.tenderdeclarationid and p.tenderitemid=i.id and m.`tenderpacketid`=p.id and m.status=#{status}  and m.`competeuserid`=#{userid} and t.id=#{id}" +
            " </where> order by m.price asc</script>")
    List<Map<String, Object>> findBidBy(@Param("id") int id,@Param("status") String status,@Param("userid") int userid);

    @Select("<script>select t.tendername as tendername,p.sequence as sequence,m.competecompanyname as releasecompanyname,m.price as price,p.purchaseamount as purchaseamount from mytender m,tenderpacket p,tenderdeclaration t " +
            "<where> t.id=p.tenderdeclarationid and m.`tenderdeclarationid`=t.id and m.status='MYTENDER_SUCCEED' and t.userid=#{userid} and t.id=#{id}" +
            " </where></script>")
    List<Map<String, Object>> findBidTenderP(@Param("id") int id,@Param("userid") int userid);

    @Select("<script>select IFNULL(sum(m.needamount), 0) from mytender m,tenderpacket p,tenderdeclaration t" +
            " <where> t.id=p.tenderdeclarationid and m.`tenderpacketid`=p.id and m.status='MYTENDER_SUCCEED' and t.userid=#{userid} and t.id=#{id} " +
            " </where></script>")
    Double countSupplyamount(@Param("id") int id,@Param("userid") int userid);

    @Select("<script>select IFNULL(sum(m.needamount), 0) from mytender m,tenderpacket p,tenderdeclaration t" +
            " <where> t.id=p.tenderdeclarationid and m.`tenderpacketid`=p.id and m.status=#{status} and m.`competeuserid`=#{userid} and t.id=#{id} " +
            " </where></script>")
    BigDecimal countSupply(@Param("id") int id,@Param("status") String status,@Param("userid") int userid);


    @Select("<script>select IFNULL(sum(m.needamount), 0) from mytender m,tenderpacket p,tenderdeclaration t" +
            " <where> t.id=p.tenderdeclarationid and m.`tenderdeclarationid`=t.id and m.status='MYTENDER_SUCCEED' and  t.userid=#{userid} and t.id=#{id} " +
            " </where></script>")
    Integer countSupplyamountP(@Param("id") int id,@Param("userid") int userid);

    // 个人中心我的招标
    @Select("<script>select  count(1) from tenderdeclaration td  where td.userid=#{userId}" +
            "<if test='status!=null'> and (td.status=#{status} <if test='status==\"TENDER_GIVEUP\"'> or td.status='TENDER_CANCEL' </if>)</if>" +
            "<if test='status==null'> and (td.status!='TENDER_EDIT' and td.status!='TENDER_CANCEL' and td.status!='TENDER_GIVEUP') </if>" +
            "<if test='tenderCode!=null and tenderCode!=\"\"'> and td.tendercode=#{tenderCode} </if>"+
            "<if test='year!=\"\" and month!=\"\"'> and DATE_FORMAT(td.createtime,'%Y-%c')=CONCAT(#{year},'-',#{month})</if>" +
            "<if test='year!=\"\" and month==\"\"'> and DATE_FORMAT(td.createtime,'%Y')=#{year}</if></script>")
    public int countMyTenderDeclaration(@Param("status") String status, @Param("userId") int userId, @Param("year") String year, @Param("month") String month,@Param("tenderCode")String tenderCode);


    @Select("<script>" +
            "select a.*,IFNULL(b.tdCount,0) tdCount,IFNULL(c.waitVerifyCount,0) waitVerifyCount,IFNULL(d.verifyCount,0) verifyCount from ( " +
            " select td.id,td.tenderenddate,td.createtime,td.tendercode,td.status,td.lastupdatetime,td.margins " +
            "from tenderdeclaration td  where td.userid=#{userId} " +
            ") a left join " +
            "(select td.id,count(distinct(b.userid)) tdCount from tenderdeclaration td  inner join bid b on b.tenderdeclarationid=td.id " +
            " where   b.mytenderstatus not in ('MYTENDER_EDIT','MYTENDER_MISSING','MYTENDER_GIVEUP') and td.userid=#{userId} group by td.id) " +
            "b  on a.id=b.id " +
            "left join (select td.id,count(distinct(b.userid)) waitVerifyCount  from tenderdeclaration td inner  join bid b on b.tenderdeclarationid=td.id  where " +
            " b.paymentstatus is null and b.mytenderstatus not  in('MYTENDER_GIVEUP','MYTENDER_MISSING')  and td.userid=#{userId} group by td.id) " +
            "c on a.id=c.id " +
            " left join (select td.id,count(b.id) verifyCount from tenderdeclaration td inner join bid b on td.id=b.tenderdeclarationid where b.paymentstatus='paidUp' group by td.id) d " +
            "on  a.id=d.id "+
             "<where> " +
            "<if test='status!=null'>  and (a.status=#{status} <if test='status==\"TENDER_GIVEUP\"'> or a.status='TENDER_CANCEL' </if>) </if>" +
            "<if test='status==null'> and (a.status!='TENDER_EDIT' and a.status!='TENDER_CANCEL' and a.status!='TENDER_GIVEUP')  </if>" +
            "<if test='tenderCode!=null and tenderCode!=\"\"'> and a.tendercode=#{tenderCode} </if>"+
            "<if test='year!=\"\" and month!=\"\"'>and DATE_FORMAT(a.createtime,'%Y-%c')=CONCAT(#{year},'-',#{month})</if>" +
            "<if test='year!=\"\" and month==\"\"'>and DATE_FORMAT(a.createtime,'%Y')=#{year}</if> " +
            "</where>" +
            "  order by a.lastupdatetime  desc limit #{page.pagesize} offset #{page.indexNum}" +
            "</script>")
    public List<TenderDeclaration> myTenderDeclarationList(@Param("page") PageQueryParam page, @Param("userId") int userId, @Param("status") String status, @Param("year") String year, @Param("month") String month,@Param("tenderCode")String tenderCode);

    //个人中心履约审核列表
    @Select("<script>" +
            " select count(distinct(td.id)) from tenderdeclaration td inner join bid b  on b.tenderdeclarationid=td.id " +
            " where td.userid=#{userId} and td.status in('TENDER_START','TENDER_CHOOSE_CONFIRM','TENDER_CANCEL','TENDER_RELEASE_RESULT') " +
            "<if test='tenderCode!=null and tenderCode!=\"\"'> and td.tendercode=#{tenderCode} </if>"+
            "<if test='year!=\"\" and month!=\"\"'>and DATE_FORMAT(td.createtime,'%Y-%c')=CONCAT(#{year},'-',#{month})</if>" +
            "<if test='year!=\"\" and month==\"\"'>and DATE_FORMAT(td.createtime,'%Y')=#{year}</if> " +
            "</script>")
    public int countVerifyPaymentList(@Param("userId") int userId,  @Param("year") String year, @Param("month") String month,@Param("tenderCode")String tenderCode);


    @Select("<script>" +
            "select a.*,IFNULL(c.waitVerifyCount,0) waitVerifyCount,IFNULL(d.verifyCount,0) verifyCount  from ( " +
            " select td.id,td.tenderenddate,td.createtime,td.tendercode,td.status,td.lastupdatetime " +
            "from tenderdeclaration td  inner join bid b  on b.tenderdeclarationid=td.id where td.userid=#{userId}" +
            " and td.status in('TENDER_START','TENDER_CHOOSE_CONFIRM','TENDER_CANCEL','TENDER_RELEASE_RESULT')  group by td.id " +
            ") a left join (select td.id,count(distinct(b.userid)) waitVerifyCount  from tenderdeclaration td inner  join bid b on b.tenderdeclarationid=td.id  where " +
            " b.paymentstatus is null and td.userid=#{userId} group by td.id) " +
            " c on a.id=c.id "+
            "left join (select td.id,count(b.id) verifyCount from tenderdeclaration td inner join bid b on td.id=b.tenderdeclarationid where b.paymentstatus='paidUp' group by td.id) d " +
            "on  a.id=d.id"+
            "<where> " +
            "<if test='tenderCode!=null and tenderCode!=\"\"'> and a.tendercode=#{tenderCode} </if>"+
            "<if test='year!=\"\" and month!=\"\"'>and DATE_FORMAT(a.createtime,'%Y-%c')=CONCAT(#{year},'-',#{month})</if>" +
            "<if test='year!=\"\" and month==\"\" '>and DATE_FORMAT(a.createtime,'%Y')=#{year}</if> " +
            "</where>" +
            "  order by a.lastupdatetime  desc limit #{page.pagesize} offset #{page.indexNum}" +
            "</script>")
    public List<TenderDeclaration> verifyPaymentList(@Param("page") PageQueryParam page, @Param("userId") int userId, @Param("year") String year, @Param("month") String month,@Param("tenderCode")String tenderCode);

    @Select("select status from tenderdeclaration t where userid=#{userId}")
    public List<TenderDeclaration> listAllStatus(@Param("userId") int userId);

    @Select("select * from tenderdeclaration where id=#{id} and userid=#{userId}")
    public  TenderDeclaration findTenderDeclarByIdAndUserId(@Param("id")int id,@Param("userId")int userId);

    @Select("select * from tenderdeclaration where id = (select tenderdeclarationId from mytender where id=#{value})")
    public  TenderDeclaration findTenderDeclarByTenderId(int id);

    //我的招标统计信息条数
    @Select("select count(1) from tenderdeclaration  td left join mytender mt  on  td.id=mt.tenderdeclarationid " +
            " where (mt.paymentstatus='waitVerify' or td.status in('TENDER_CHOOSE_CONFIRM','TENDER_VERIFY_PASS','TENDER_VERIFY_FAIL')) and td.userid=#{value}  ")
    public int countTenderDeclare(int userId);

    //我的投标统计信息条数
    @Select("select count(1) from mytender where  (status in ('MYTENDER_TENDERED','MYTENDER_WAITING_CHOOSE','MYTENDER_SUCCEED') or paymentstatus in('unPaidUp','paidUp')) and competeuserid=#{value}")
    public int countMytender(int userId);

    //---------------------后台查询-------------------------
    final String TenderDeclarationSelectSQl =
            "<where>" +
            "<if test='status!=null and status!=\"\"'> " +
                    "<if test='status==\"TENDER_VERIFY_PASS\"'> and status in ('TENDER_VERIFY_PASS', 'TENDER_START', 'TENDER_CHOOSE_CONFIRM', 'TENDER_RELEASE_RESULT')</if>" +
                    "<if test='status!=\"TENDER_VERIFY_PASS\"'> and status=#{status}</if>" +
            "</if>" +
            "<if test='content!=null and content!=\"\"'> and c.name like #{content}</if>" +
            "<if test='companyid!=0'> and c.id=#{companyid}</if>" +
            "<if test='startDate != null and startDate != \"\" and endDate != null and endDate != \"\"'> and (t.createtime between #{startDate} and #{endDate}) </if>" +
            "<if test='startDate != null and startDate != \"\" and (endDate == null or endDate == \"\")'> and (t.createtime between #{startDate} and now()) </if>" +
            "</where>";
    @Select("<script>" +
            "select count(*) from tenderdeclaration t left join companies c on t.companyid = c.id"
             + TenderDeclarationSelectSQl +
            "</script>")
    public int countAllTenderDeclarations(@Param("status")String status,
                                          @Param("content")String content,
                                          @Param("companyid")int companyId,
                                          @Param("startDate")String startDate,
                                          @Param("endDate")String endDate);

    @Select("<script>" +
            "select t.*, c.name as companyname from tenderdeclaration t left join companies c on t.companyid = c.id " +
            TenderDeclarationSelectSQl +
            " order by t.createtime desc limit #{limit} offset #{offset}" +
            "</script>")
    public List<Map<String, Object>> listAllTenderDeclarations(@Param("status")String status,
                                                               @Param("content")String content,
                                                               @Param("companyid")int companyId,
                                                               @Param("startDate")String startDate,
                                                               @Param("endDate")String endDate,
                                                               @Param("limit") int limit,
                                                               @Param("offset") int offset);

    public default Pager<Map<String, Object>> pageAllTenderDeclaration(String status, String content, int companyId, String startDate, String endDate, int page, int pagesize){
        return Pager.config(this.countAllTenderDeclarations(status, content, companyId, startDate, endDate), (int limit, int offset) -> this.listAllTenderDeclarations(status, content, companyId, startDate, endDate,limit, offset))
                .page(page, pagesize);
    }

    //审核TenderDeclaration
    @Update("<script>" +
            " update tenderdeclaration set status=#{status}, " +
            " <if test='traderid!=\"0\"'>traderid=#{traderid}, </if>" +
            " tradername=#{tradername}, traderphone=#{traderphone}, verifydate=now(), verifyremarks=#{verifyremarks}, " +
            " version=version+1 where id=#{id} and status!=#{status} and version=#{version}" +
            "</script>")
    int verifyTenderDeclarationMethod(@Param("id") int id,
                                      @Param("version")int version,
                                      @Param("traderid")int traderid,
                                      @Param("tradername")String tradername,
                                      @Param("traderphone")String traderphone,
                                      @Param("status") String status,
                                      @Param("verifyremarks")String verifyremarks);



    //下架, 放弃 招标公告
    @Update("update tenderdeclaration set status=#{status}, giveupremarks=#{giveupremarks}, version=version+1 where id=#{id} and version=#{version}")
    int giveUpDeclarationMethod(@Param("id") int id,
                                @Param("version")int version,
                                @Param("status") String status,
                                @Param("giveupremarks") String giveupremarks);

    //下架, 放弃 一个公司所有的 招标公告
    @Update("update tenderdeclaration set status=#{status}, giveupremarks=#{giveupremarks} where companyid=#{companyid} and status!=#{status}")
    int giveUpCompanyDeclarationMethod(@Param("companyid") int companyid,
                                       @Param("status") String status,
                                       @Param("giveupremarks") String giveupremarks);

    //根据交易员id更换交易员
    @Update("update tenderdeclaration set traderid=#{newDealer.id}, tradername=#{newDealer.name}, traderphone=#{newDealer.phone} where traderid=#{oldId}")
    void updateTenderDeclarationDealerByDealerId(@Param("newDealer") Admin newDealer,
                                                 @Param("oldId") int oldId);

    //获取昨天投标结束的 招标公告List
    @Select("select * from tenderdeclaration where date_add(tenderenddate,interval 1 day) = CURDATE() and status=#{status}")
    List<TenderDeclaration> getYesterdayTenderEndList(String status);

    @Select("select b.id,b.attachmentpath,b.attachmentfilename from tenderdeclaration td inner join bid b on b.tenderdeclarationid=td.id " +
             " where b.attachmentpath!='' and mytenderstatus in('MYTENDER_WAITING_CHOOSE','MYTENDER_CHOOSE','MYTENDER_CHOOSE_FREE') and b.tenderdeclarationid=#{declareId} and td.userid=#{userId}")
    public List<Bid> findByInDeclare(@Param("userId")int userId,@Param("declareId")int declareId);

    @Select("select  distinct(date_format(createtime,'%Y'))from tenderdeclaration order by createtime  desc")
    public List<Integer> findDeclarationYear();

    @Select("select  tenderopen from tenderdeclaration where id=#{value}")
    public  boolean  isTenderOpen(int tenderDeclareId);



    @Select("select  tendercode,userid,contractconbegindate,contractconenddate,tendername,tenderunits,tradername,traderphone,tenderbegindate,tenderenddate from tenderdeclaration where id=#{value}")
    public  TenderDeclaration  lodaDeclareById(int tenderDeclareId);


}
