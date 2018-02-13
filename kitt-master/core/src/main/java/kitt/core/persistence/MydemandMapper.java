package kitt.core.persistence;

import kitt.core.domain.Mydemand;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by fanjun on 14-12-24.
 */
public interface MydemandMapper {

    //添加我的需求
    @Insert("insert into mydemands(userid,demandcode,releasetime,quoteenddate,demandamount,status," +
            "deliverydate,deliverydatestart,deliverydateend) values(" +
            "#{userid},#{demandcode},#{releasetime},#{quoteenddate},#{demandamount},#{status}," +
            "#{deliverydate},#{deliverydatestart},#{deliverydateend})")
    public void addMydemand(Mydemand mydemand);

    //修改我的需求
    @Update("update mydemands set releasetime=#{releasetime},quoteenddate=#{quoteenddate},demandamount=#{demandamount},status=#{status}," +
            "deliverydate=#{deliverydate},deliverydatestart=#{deliverydatestart},deliverydateend=#{deliverydateend}" +
            " where demandcode=#{demandcode}")
    public void modifyMyDemand(Mydemand mydemand);

    //获取我的发布的需求
    @Select("select m.*, d.viewtimes as viewtimes,d.coaltype as coaltype from mydemands m left join demands d on m.demandcode=d.demandcode where m.userid=#{userid} order by m.releasetime desc limit #{startNum},#{pageSize}")
    public List<Mydemand> getTurnpageListWithUserid(@Param("userid") int userid,@Param("startNum") int startNum,@Param("pageSize") int pageSize);

    @Select("<script>select m.*, d.viewtimes as viewtimes,d.coaltype as coaltype from" +
            " mydemands m left join demands d on m.demandcode=d.demandcode" +
            "  where m.userid=#{userid} " +
            "<if test='demandcode!=null and demandcode!=\"\"'> and m.demandcode=#{demandcode}</if>" +
            "<if test='coaltype!=null and coaltype!=\"全部\"'> and d.coaltype=#{coaltype}</if>" +
            "<if test='status!=null and status!=\"全部\"'> and m.status=#{status}</if>" +
            "<if test='releasetimeStart!=null and releasetimeStart!=\"\"'>  and DATE_FORMAT(m.releasetime,'%Y-%m-%d') <![CDATA[ >=]]>  #{releasetimeStart} </if>" +
            "<if test='releasetimeEnd!=null and releasetimeEnd!=\"\"'>  and DATE_FORMAT(m.releasetime,'%Y-%m-%d')   <![CDATA[ <=]]>  #{releasetimeEnd} </if>" +
            "<if test='quoteenddateStart!=null and quoteenddateStart!=\"\"'>  and DATE_FORMAT(m.quoteenddate,'%Y-%m-%d')  <![CDATA[ >=]]>  #{quoteenddateStart} </if>" +
            "<if test='quoteenddateEnd!=null and quoteenddateEnd!=\"\"'>  and DATE_FORMAT(m.quoteenddate,'%Y-%m-%d') <![CDATA[ <=]]>  #{quoteenddateEnd} </if>" +
            " order by " +
            "<if test='ordercol!=null and ordercol!=\"\" and ordercol==\"releasetime\" '>   m.releasetime</if>" +
            "<if test='ordercol!=null and ordercol!=\"\" and ordercol==\"quoteenddate\" '>   m.quoteenddate</if>" +
            "<if test='ordercol!=null and ordercol!=\"\" and  ordercol==\"viewtimes\" '>   d.viewtimes</if>" +
            "<if test='ordercol!=null and ordercol!=\"\" and sortflag==0'>  desc</if>" +
            "<if test='ordercol!=null and ordercol!=\"\" and sortflag==1 '>  asc</if>" +
            " limit #{startNum},#{pageSize}</script>")
    public List<Mydemand> getTurnpageWithUserid(@Param("userid") int userid,
                                                    @Param("demandcode") String demandcode,
                                                    @Param("coaltype") String coaltype,
                                                    @Param("status") String status,
                                                    @Param("releasetimeStart") String releasetimeStart,
                                                    @Param("releasetimeEnd") String releasetimeEnd,
                                                    @Param("quoteenddateStart") String quoteenddateStart,
                                                    @Param("quoteenddateEnd") String quoteenddateEnd,
                                                    @Param("startNum") int startNum,
                                                    @Param("pageSize") int pageSize,
                                                    @Param("ordercol") String ordercol,
                                                    @Param("sortflag") int sortflag
                                                    );


    @Select("<script>select m.*, d.viewtimes as viewtimes,d.coaltype as coaltype from" +
            " mydemands m left join demands d on m.demandcode=d.demandcode" +
            "  where m.userid=#{userid} " +
            "<if test='coaltype!=null and coaltype!=\"全部\"'> and d.coaltype=#{coaltype}</if>" +
            "<if test='status!=null and status!=\"全部\"'> and m.status=#{status}</if>" +
            " order by" +
            "<if test='ordercol!=null and ordercol!=\"\" and ordercol==\"releasetime\" '>   m.releasetime</if>" +
            "<if test='ordercol!=null and ordercol!=\"\" and ordercol==\"quoteenddate\" '>   m.quoteenddate</if>" +
            "<if test='ordercol!=null and ordercol!=\"\" and  ordercol==\"viewtimes\" '>   d.viewtimes</if>" +
            "<if test='ordercol!=null and ordercol!=\"\" and sortflag==0'>  desc</if>" +
            "<if test='ordercol!=null and ordercol!=\"\" and sortflag==1 '>  asc</if>" +
            " limit #{startNum},#{pageSize}</script>")
    public List<Mydemand> getTurnpageWithUserid1(@Param("userid") int userid,
                                                @Param("coaltype") String coaltype,
                                                @Param("status") String status,
                                                @Param("startNum") int startNum,
                                                @Param("pageSize") int pageSize,
                                                @Param("ordercol") String ordercol,
                                                @Param("sortflag") int sortflag
    );
    //统计我的需求总数
    @Select("select count(*) from mydemands where userid=#{userid}")
    public int countAllMydemands(int userid);

    @Select("<script>select count(*) from  mydemands m left join demands d on m.demandcode=d.demandcode" +
            " where m.userid=#{userid}" +
            "<if test='demandcode!=null and demandcode!=\"\"'> and m.demandcode=#{demandcode}</if>" +
            "<if test='coaltype!=null and coaltype!=\"全部\"'> and d.coaltype=#{coaltype}</if>" +
            "<if test='status!=null and status!=\"全部\"'> and m.status=#{status}</if>" +
            "<if test='releasetimeStart!=null and releasetimeStart!=\"\"'>  and DATE_FORMAT(m.releasetime,'%Y-%m-%d') <![CDATA[ >=]]>  #{releasetimeStart} </if>" +
            "<if test='releasetimeEnd!=null and releasetimeEnd!=\"\"'>  and DATE_FORMAT(m.releasetime,'%Y-%m-%d')   <![CDATA[ <=]]>  #{releasetimeEnd} </if>" +
            "<if test='quoteenddateStart!=null and quoteenddateStart!=\"\"'>  and DATE_FORMAT(m.quoteenddate,'%Y-%m-%d')  <![CDATA[ >=]]>  #{quoteenddateStart} </if>" +
            "<if test='quoteenddateEnd!=null and quoteenddateEnd!=\"\"'>  and DATE_FORMAT(m.quoteenddate,'%Y-%m-%d') <![CDATA[ <=]]>  #{quoteenddateEnd} </if>" +
            "</script>")
    public int countAllMydemandsBy(@Param("userid") int userid,
                                   @Param("demandcode") String demandcode,
                                   @Param("coaltype") String coaltype,
                                   @Param("status") String status,
                                   @Param("releasetimeStart") String releasetimeStart,
                                   @Param("releasetimeEnd") String releasetimeEnd,
                                   @Param("quoteenddateStart") String quoteenddateStart,
                                   @Param("quoteenddateEnd") String quoteenddateEnd
                                   );


    @Select("<script>select count(*) from  mydemands m left join demands d on m.demandcode=d.demandcode" +
            " where m.userid=#{userid}" +
            "<if test='coaltype!=null and coaltype!=\"全部\"'> and d.coaltype=#{coaltype}</if>" +
            "<if test='status!=null and status!=\"全部\"'> and m.status=#{status}</if>" +
            "</script>")
    public int countAllMydemandsBy1(@Param("userid") int userid,
                                   @Param("coaltype") String coaltype,
                                   @Param("status") String status
    );
    //删除我的需求
    @Delete("delete from mydemands where demandcode=#{demandcode} and userid=#{userid}")
    public void deleteMyDemandByDemandcode(@Param("demandcode") String demandcode,@Param("userid") int userid);

    /*//修改我的需求状态-交易结束
    @Update("update mydemands set status='交易结束' where status='匹配中' and (date_add(deliverydate,interval 1 day)=date_format(now(),'%Y-%m-%d') or date_add(deliverydateend,interval 1 day)=date_format(now(),'%Y-%m-%d'))")
    public void modifyStatusToTradeOverTask();*/

    //将我的需求表的状态
    @Update("update mydemands set status=#{status} where demandcode=#{demandcode}")
    public int modifyStatusByDemandcode(@Param("status") String status,@Param("demandcode") String demandcode);

    //将我的需求表的状态
    @Update("update mydemands set status=#{status} where demandcode=#{demandcode} and userid=#{userid}")
    public void modifyStatusByDemandcodeAndUserid(@Param("status") String status,@Param("demandcode") String demandcode,@Param("userid") int userid);

    //报价成功需求表报价人数加1
    @Update("update mydemands set quotenum=quotenum+1 where demandcode=#{demandcode}")
    public void plusQuotenum(String demandcode);

    //删除报价,我的需求表的报价人数减1
    @Update("update mydemands set quotenum=quotenum-1 where demandcode=#{demandcode}")
    public void minusQuotenum(String demandcode);

    //根据需求编号获取我的需求
    @Select("select * from mydemands where demandcode=#{demandcode}")
    public Mydemand getMydemandByDemandcode(String demandcode);

    //修改已匹配吨数
    @Update("update mydemands set purchasednum=#{purchasednum}+purchasednum where demandcode=#{demandcode}")
    public void modifyPurchaseNumByDemandcode(@Param("purchasednum") int purchasednum,@Param("demandcode") String demandcode);

/*    //过了报价截止时间后,交易状态改为匹配中
    @Update("update mydemands set status='匹配中' where date_add(quoteenddate,interval 1 day) = date_format(now(),'%Y-%m-%d')")
    public void modifyStatusToMatchingTask();*/

    //
    @Update("update mydemands set status=#{status} where demandcode=#{demandcode}")
    public void modifyStatusByDemandCode(@Param("status") String status,@Param("demandcode") String demandcode);

}
