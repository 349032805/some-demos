package kitt.core.persistence;

import kitt.core.domain.MyInterest;
import kitt.core.domain.Mytender;
import kitt.core.domain.TenderPayment;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 15/1/4.
 */
public interface MyInterestMapper {

    //我的关注
    @Select("select * from myinterest where type=#{type} and userid=#{userid} and  isdelete=0 order by lastupdatetime desc limit #{limit} offset #{offset}")
    public List<MyInterest> getMyInterestList(@Param("type") String type,
                                              @Param("userid") int userid,
                                              @Param("limit") int limit,
                                              @Param("offset") int offset);

    @Select("select * from myinterest where type=#{type} and userid=#{userid} and  isdelete=0 ")
    public List<MyInterest> getMyInterestList2(@Param("type") String type, @Param("userid") int userid);

    @Select("select count(id) from myinterest where type=#{type} and userid=#{userid} and isdelete=0")
    public int getMyInterestCount(@Param("type") String type, @Param("userid") int userid);

    @Select("select count(*) from myinterest where type='shop' and sid=#{shopid} and isdelete=0")
    public int countMyInterestByShopId(@Param("shopid") int shopid);

    //添加我的关注
    @Insert("insert into myinterest(pid, sid, pname, seller, price, amount, NCV, userid, type, createtime, clienttype) values(#{pid}, #{sid}, #{pname}, #{seller}, #{price}, #{amount}, #{NCV}, #{userid}, #{type}, now(), #{clienttype})")
    public int addMyInterest(MyInterest myInterest);

    //查询是否已经关注sid
    @Select("select * from myinterest where sid=#{sid} and userid=#{userid} and type=#{type}")
    public MyInterest getMyInterestBySid(@Param("sid") int sid, @Param("userid") int userid, @Param("type") String type);

    //取消关注
    @Update("update myinterest set isdelete=1 where id=#{id}")
    public void cancelMyInterest(int id);

    //我的关注-已关注，修改状态
    @Update("update myinterest set isdelete=(isdelete+1)%2 where sid=#{sid} and userid=#{userid} and type=#{type}")
    public int setMyInterestStatusBySid(@Param("sid") int sid,
                                        @Param("userid") int userid,
                                        @Param("type") String type);

    //我的关注-已关注，需求，修改状态
    @Update("update myinterest set isdelete=1 where sid=#{sid} and isdelete=0 and userid=#{userid} and type=#{type}")
    public void setMyInterestStatusBySidDemand(@Param("sid") int sid,
                                               @Param("userid") int userid,
                                               @Param("type") String type);

    //根据id获取MyInterest
    @Select("select * from myinterest where id=#{id}")
    public MyInterest getMyInterestById(int id);



    @Update("update tenderquotes set isdelete=1 where id=#{id}")
    public void deltetenderquotes(@Param("id") int id);

    //个人中心--我的投标
    @Select("<script>" +
            "    select b.id as id,b.paymentstatus as paymentstatus, t.id as did, b.createtime as createtime,t.tendercode as tendercode,t.tenderunits as tenderunits ,b.`mytenderstatus` as status ,IFNULL(t.margins , 0) as margins,t.tenderenddate as tenderenddate,t.purchaseamount as purchaseamount,IFNULL(b.supplyamount , 0) as supplyamount from bid b,tenderdeclaration t " +
            " <where>  b.userid=#{userid} and b.`tenderdeclarationid`=t.id "+
            "<if test='status!=null and status!=\"\"'> and  b.mytenderstatus=#{status}</if>"+
            " <if test='createtime!=null and createtime!=\"\"'>  and DATE_FORMAT(b.createtime,#{pattern1}) like #{createtime}</if>"+
            "</where> order by b.createtime desc limit #{limit} offset #{offset}</script>")
    List<Map<String,Object>> findMytenders(@Param("userid") int userid,@Param("status") String status,@Param("createtime") String createtime,@Param("pattern1") String pattern1,@Param("limit")int limit,
                                                          @Param("offset")int offset);



    @Select("<script>" +
            "    select b.id as id,b.paymentstatus as paymentstatus, t.id as did, b.createtime as createtime,t.tendercode as tendercode,t.tenderunits as tenderunits ,b.`mytenderstatus` as status ,IFNULL(t.margins , 0) as margins,t.tenderenddate as tenderenddate,t.purchaseamount as purchaseamount,IFNULL(b.supplyamount, 0) as supplyamount from bid b,tenderdeclaration t " +
            " <where>  b.userid=#{userid} and b.`tenderdeclarationid`=t.id "+
            "<if test='status!=null and status!=\"\"'> and  b.mytenderstatus in <foreach collection='str' index='index' item='item'  open=\"(\" separator=\",\" close=\")\" > #{item}</foreach></if>"+
            " <if test='createtime!=null and createtime!=\"\"'>  and DATE_FORMAT(b.createtime,#{pattern1}) like #{createtime}</if>"+
            "</where> order by b.createtime desc limit #{limit} offset #{offset}</script>")
    List<Map<String,Object>> findtenders(@Param("userid") int userid,@Param("status") String status,@Param("str")String[] str,@Param("createtime") String createtime,@Param("pattern1") String pattern1,@Param("limit")int limit,
                                         @Param("offset")int offset);


    @Select("<script>" +
            "select count(*) from bid b ,`tenderdeclaration` t " +
            "<where> b.tenderdeclarationid=t.id and b.userid=#{userid} "+
            "<if test='status!=null and status!=\"\"'>  and  b.mytenderstatus=#{status}</if>"+
            " <if test='createtime!=null and createtime!=\"\"'>  and DATE_FORMAT(b.createtime,#{pattern1}) like #{createtime}</if>"+
            "</where> </script>")
    int countMytenders(@Param("userid") int userid,@Param("status") String status,@Param("createtime") String createtime,@Param("pattern1") String pattern1
                      );


    @Select("<script>" +
            "select count(*) from bid b ,`tenderdeclaration` t " +
            "<where> b.tenderdeclarationid=t.id and b.userid=#{userid}  "+
            "<if test='status!=null and status!=\"\"'>  and  b.mytenderstatus in <foreach collection='str' index='index' item='item'  open=\"(\" separator=\",\" close=\")\" > #{item}</foreach></if>"+
            " <if test='createtime!=null and createtime!=\"\"'>  and DATE_FORMAT(b.createtime,#{pattern1}) like #{createtime}</if>"+
            "</where> </script>")
    int counttenders(@Param("userid") int userid,@Param("status") String status,@Param("str") String[] str,@Param("createtime") String createtime,@Param("pattern1") String pattern1
    );

    @Delete("delete from mytender where bidid=#{id}")
    public int deleteMytender(int id);


    @Select("select  count(*) from bid b, tenderdeclaration t   where b.`tenderdeclarationid`=t.id and b.userid=#{userid} and b.mytenderstatus=#{status}")
     int getCountByStatus(@Param("userid")int userid,@Param("status")String status);

    //暂缓的标
    @Select("select  count(*) from bid b,tenderdeclaration t   where b.`tenderdeclarationid`=t.id  and b.userid=#{userid} and b.mytenderstatus in ('MYTENDER_EDIT','MYTENDER_MISSING')")
    int getZhCount(@Param("userid")int userid);

    //已放弃
    @Select("select  count(*) from bid b,tenderdeclaration t   where b.`tenderdeclarationid`=t.id and b.userid=#{userid} and b.mytenderstatus in ('MYTENDER_GIVEUP','MYTENDER_CANCEL')")
    int getYfqCount(@Param("userid")int userid);


    @Select("select  count(*) from bid b,tenderdeclaration t where b.`tenderdeclarationid`=t.id and b.userid=#{userid} and b.mytenderstatus in('MYTENDER_TENDERED','MYTENDER_TENDERED_CONFIRM','MYTENDER_WAITING_CHOOSE','MYTENDER_FAIL','MYTENDER_SUCCEED','MYTENDER_TENDERED_FREE','MYTENDER_CHOOSE_FREE','MYTENDER_CHOOSE')")
    int getYtbCount(@Param("userid")int userid);



    @Select("select count(*) from tenderpayment t,bid b,tenderdeclaration d  where b.`tenderdeclarationid`=d.id and  (IFNULL(t.auditstatus, '')!='fail')  and b.id=t.bidid and t.userid=#{userid} " +
            "and (t.pic1!=\"\" or t.pic2!=\"\" or t.pic3!=\"\" )")
    int getCountByuserid(@Param("userid")int userid);



    //支付凭证查看
    @Select("<script> select p.createtime as createtime,b.paymentstatus as paymentstatus,b.mytenderstatus as status,b.id as bid,t.id as did,p.id as pid,t.tenderunits as tenderunits,t.tendercode as tendercode,IFNULL(t.margins , 0) as price,IFNULL(b.supplyamount , 0) as supplyamount,p.pic1 as pic1,p.pic2 as pic2,p.pic3 as pic3 from bid b,tenderdeclaration t ,tenderpayment p where b.`tenderdeclarationid`=t.id  and b.id=p.bidid  and (IFNULL(p.auditstatus, '')!='fail') and b.userid=#{userid}  and (p.pic1 !=\"\" or p.pic2 !=\"\" or p.pic3 !=\"\" )" +
            " order by p.createtime desc limit #{limit} offset #{offset}</script>" )
    List<Map<String,Object>> findtenderPayment(@Param("userid")int userid,@Param("limit")int limit,
                                  @Param("offset")int offset);

    //根据mytenderid查找
    @Select("select  competecompanyname,tendercode,price, tenderdeclarationid from mytender where id=#{mytenderid} ")
    Mytender findPartTenderById(@Param("mytenderid")int mytenderid);


    @Select("select IFNULL(sum(supplyamount), 0)  from mytender where id=#{mytenderid} and competeuserid=#{userid} ")
    int findCountAmount(@Param("mytenderid")int mytenderid,@Param("userid")int userid);




    @Select("select  t.margins as margins from mytender m,tenderdeclaration t where m.tenderdeclarationid=t.id and m.id=#{mytenderid} and m.`competeuserid`=#{userid}")
    BigDecimal findMarginsByIdAndUserid(@Param("mytenderid")int mytenderid,@Param("userid")int userid);

    //根据mytenderid查找picurl和时间
    @Select("select  * from tenderpayment where mytenderid=#{mytenderid} ")
    List<TenderPayment> findPayment(@Param("mytenderid")int mytenderid);


    /**
     * 个人中心我的关注 - 需求
     */
    final String DemandMyInterestSelect1 = "" +
            "<if test='demandcode != \"\" and demandcode != null'> and d.demandcode like #{demandcode} </if>" +
            "<if test='coaltype != \"\" and coaltype != null'> and d.coaltype=#{coaltype} </if>" +
            "<if test='status != \"\" and status != null'> and d.tradestatus=#{status} </if>" +
            "<if test='createDateStart != \"\" and createDateStart != null and createDateEnd != \"\" and createDateEnd != null'> and (date(d.releasetime) between #{createDateStart} and #{createDateEnd}) </if> " +
            "<if test='quoteDateStart != \"\" and quoteDateStart != null and quoteDateEnd != \"\" and quoteDateEnd != null'> and (date(d.quoteenddate) between #{quoteDateStart} and #{quoteDateEnd}) </if>";
    final String DemandMyInterestOrder1 = "" +
            " order by " +
            "<if test='sortType != \"\" and sortType != null'>" +
            " ${sortType} " +
            "<if test='sortOrder == 0'>  asc, </if>" +
            "<if test='sortOrder == 1'>  desc, </if>" +
            "</if>" +
            " m.lastupdatetime desc ";
    @Select("<script>" +
            "select m.id as mid, d.* from myinterest m inner join demands d " +
            " on m.sid = d.id " +
            "<where> m.type='demand' and m.userid=#{userid} and m.isdelete=0 " + DemandMyInterestSelect1 +
            "</where>" + DemandMyInterestOrder1 + "  limit #{limit} offset #{offset} " +
            "</script>")
    public List<Map<String, Object>> getMyInterestDemandList(@Param("userid") int userid,
                                                             @Param("demandcode") String demandcode,
                                                             @Param("coaltype") String coaltype,
                                                             @Param("status") String status,
                                                             @Param("createDateStart") String createDateStart,
                                                             @Param("createDateEnd") String createDateEnd,
                                                             @Param("quoteDateStart") String quoteDateStart,
                                                             @Param("quoteDateEnd") String quoteDateEnd,
                                                             @Param("sortType") String sortType,
                                                             @Param("sortOrder") int sortOrder,
                                                             @Param("limit") int limit,
                                                             @Param("offset") int offset);
    @Select("<script>" +
            "select count(m.id) from myinterest m inner join demands d " +
            " on m.sid = d.id" +
            "<where> m.type='demand' and m.userid=#{userid} and m.isdelete=0 " + DemandMyInterestSelect1 +
            "</where>"  +
            "</script>")
    public int getMyInterestDemandCount(@Param("userid") int userid,
                                        @Param("demandcode") String demandcode,
                                        @Param("coaltype") String coaltype,
                                        @Param("status") String status,
                                        @Param("createDateStart") String createDateStart,
                                        @Param("createDateEnd") String createDateEnd,
                                        @Param("quoteDateStart") String quoteDateStart,
                                        @Param("quoteDateEnd") String quoteDateEnd,
                                        @Param("sortType") String sortType,
                                        @Param("sortOrder") int sortOrder);


    /**
     * 个人中心我的关注 - 招标
     */
    final String TenderMyInterestSelect1 = "" +
            "<if test='tendercode != \"\" and tendercode != null'> and t.tendercode like #{tendercode} </if>" +
            "<if test='companyname != \"\" and companyname != null'> and c.name like #{companyname} </if>" +
            "<if test='coaltype != \"\" and coaltype != null'> and p.coaltype=#{coaltype} </if>" +
            "<if test='NCV01 != 0 or NCV02 != 7500'> and ( p.NCV between ${NCV01} and ${NCV02} ) </if>" +
            "<if test='startDate != \"\" and startDate != null and endDate != \"\" and endDate != null'> and ( date(t.createtime) between #{startDate} and #{endDate} ) </if>" +
            "<if test='startDate != \"\" and startDate != null and (endDate == \"\" or endDate == null)'> and ( date(t.createtime) &gt;= #{startDate} ) </if>" +
            "<if test='(startDate == \"\" or startDate == null) and endDate != \"\" and endDate != null'> and ( date(t.createtime) %lt;= #{endDate} ) </if>" +
            "<if test='status != \"\" and status != null'> " +
            "<if test='status == \"TENDER_CANCEL\"'> and t.status in ('TENDER_CANCEL', 'TENDER_GIVEUP') </if>" +
            "<if test='status != \"TENDER_CANCEL\"'> and t.status=#{status} </if> " +
            "</if>";
    final String TenderMyInterestOrder1 = "" +
            " order by " +
            "<if test='sortType != \"\" and sortType != null'>" +
            " ${sortType} " +
            "<if test='sortOrder == 0'> asc, </if>" +
            "<if test='sortOrder == 1'> desc, </if>" +
            "</if>" +
            " m.lastupdatetime desc ";
    @Select("<script>" +
            " select m.id as mid, t.id as tid, t.userid as userid, t.createtime as createtime, c.name as companyname, " +
            " t.tendercode as tendercode, t.purchaseamount as purchaseamount, t.status as status, p.coaltype as coaltype, " +
            " p.NCV as NCV from myinterest m, tenderdeclaration t, tenderpacket p, companies c  " +
            "<where> " +
            " m.type='tender' and m.sid=t.id and p.tenderdeclarationid=t.id and c.id=t.companyid and m.isdelete=0 and m.userid=#{userid}  " +
            " and t.status in ('TENDER_START','TENDER_CHOOSE_CONFIRM','TENDER_RELEASE_RESULT','TENDER_VERIFY_PASS','TENDER_CANCEL','TENDER_GIVEUP') " +
            TenderMyInterestSelect1 +
            " </where>" + " group by m.id " + TenderMyInterestOrder1 + "  limit #{limit} offset #{offset} " +
            "</script>")
    List<Map<String, Object>> getMyInterestTenderList(@Param("userid") int userid,
                                                      @Param("tendercode") String tendercode,
                                                      @Param("companyname") String companyname,
                                                      @Param("startDate") String startDate,
                                                      @Param("endDate") String endDate,
                                                      @Param("coaltype") String coaltype,
                                                      @Param("NCV01") String NCV01,
                                                      @Param("NCV02") String NCV02,
                                                      @Param("status") String status,
                                                      @Param("sortType") String sortType,
                                                      @Param("sortOrder") int sortOrder,
                                                      @Param("limit")int limit,
                                                      @Param("offset")int offset);
    @Select("<script>" +
            "select count(distinct(m.id)) from myinterest m, tenderdeclaration t, tenderpacket p, companies c " +
            " <where> m.type='tender' and m.sid=t.id and p.tenderdeclarationid=t.id and c.id=t.companyid and m.isdelete=0 " +
            " and m.userid=#{userid}  and t.status in ('TENDER_START','TENDER_CHOOSE_CONFIRM','TENDER_RELEASE_RESULT','TENDER_VERIFY_PASS', 'TENDER_CANCEL', 'TENDER_GIVEUP') " +
            TenderMyInterestSelect1 +
            " </where>" +
            "</script>")
    int getMyInterestTenderCount(@Param("userid") int userid,
                                 @Param("tendercode") String tendercode,
                                 @Param("companyname") String companyname,
                                 @Param("startDate") String startDate,
                                 @Param("endDate") String endDate,
                                 @Param("coaltype") String coaltype,
                                 @Param("NCV01") String NCV01,
                                 @Param("NCV02") String NCV02,
                                 @Param("status") String status);

    /**
     * 个人中心我的关注 - 产品
     */
    final String SupplyMyInterestSelect1 = "" +
            "<if test='coaltype != \"\" and coaltype != null'> and s.pname=#{coaltype} </if>" +
            "<if test='pid != \"\" and pid != null'> and s.pid like #{pid} </if>" +
            "<if test='NCV01 != 0 or NCV02 != 7500'> and ((s.NCV between ${NCV01} and ${NCV02}) or (s.NCV02 between ${NCV01} and ${NCV02})) </if>" +
            "<if test='startDate != \"\" and startDate != null and endDate != \"\" and endDate != null'> and (s.createdate between #{startDate} and #{endDate}) </if>" +
            "<if test='startDate != \"\" and startDate != null and (endDate == \"\" or endDate == null)'> and (s.createdate &gt;= #{startDate}) </if>" +
            "<if test='(startDate == \"\" or startDate == null) and endDate != \"\" and endDate != null'> and (s.createdate &lt;= #{endDate}) </if>";
    final String SupplyMyInterestOrder1 = "" +
            " order by " +
            "<if test='sortType!=null and sortType!=\"\"'>" +
            "<if test='sortType == \"price\"'> (ykj + IFNULL(jtjlast, 0)) " +
            "<if test='sortOrder == 0'> asc, </if>" +
            "<if test='sortOrder == 1'> desc, </if>" +
            "</if>" +
            "<if test='sortType != \"price\"'> ${sortType}" +
            "<if test='sortOrder == 0'>  asc, </if>" +
            "<if test='sortOrder == 1'>  desc, </if>" +
            "</if>" +
            "</if>" +
            " m.lastupdatetime desc " ;
    @Select("<script>" +
            " select m.id as mid, s.* from myinterest m, sellinfo s " +
            " <where> m.type='supply' and m.sid=s.id and m.userid=#{userid} and m.isdelete=0" +
            SupplyMyInterestSelect1 +
            " </where> " + SupplyMyInterestOrder1 + " limit #{limit} offset #{offset}" +
            "</script>")
    List<Map<String, Object>> getMyInterestSupplyList(@Param("userid")int userid,
                                                      @Param("coaltype") String coaltype,
                                                      @Param("pid") String pid,
                                                      @Param("NCV01") String NCV01,
                                                      @Param("NCV02") String NCV02,
                                                      @Param("startDate") String startDate,
                                                      @Param("endDate") String endDate,
                                                      @Param("sortType") String sortType,
                                                      @Param("sortOrder") int sortOrder,
                                                      @Param("limit")int limit,
                                                      @Param("offset")int offset);

    @Select("<script>" +
            " select count(m.id) from myinterest m, sellinfo s" +
            " <where> m.type='supply' and m.sid=s.id and m.userid=#{userid} and m.isdelete=0" +
            SupplyMyInterestSelect1 + " </where> " +
            "</script>")
    int getMyInterestSupplyCount(@Param("userid")int userid,
                                 @Param("coaltype") String coaltype,
                                 @Param("pid") String pid,
                                 @Param("NCV01") String NCV01,
                                 @Param("NCV02") String NCV02,
                                 @Param("startDate") String startDate,
                                 @Param("endDate") String endDate,
                                 @Param("sortType") String sortType,
                                 @Param("sortOrder") int sortOrder);
}
