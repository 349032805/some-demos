package kitt.core.persistence;

import kitt.core.domain.Quote;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;



import java.util.List;

/**
 * Created by fanjun on 14-11-26.
 */
public interface QuoteMapper {

    //增加报价记录
//    @Insert("<script> insert into quotes (userid, demandid, \n" +
//            "      demandcode,  supplyton, \n" +
//            "       quote, lastupdatetime, \n" +
//            "      isdelete, status, deliverymode, \n" +
//            "      deliverydate, deliverydatestart, deliverydateend, \n" +
//            "      quoteenddate, companyname, traderphone, \n" +
//            "      clienttype, NCV, RS, \n" +
//            "      TM, IM, ADV, RV, \n" +
//            "      AFT, ASH, HGI, GV, \n" +
//            "      YV, FC, PS, PSName, \n" +
//            "      ADV02, RV02, FC02, \n" +
//            "      GV02, YV02, ADS, originplace, \n" +
//            "      startport, endport, deliverytime1, \n" +
//            "      deliverytime2, remarks)\n" +
//            "    values (#{userid,jdbcType=INTEGER}, #{demandid,jdbcType=INTEGER}, \n" +
//            "      #{demandcode,jdbcType=VARCHAR},  #{supplyton,jdbcType=INTEGER}, \n" +
//            "       #{quote,jdbcType=INTEGER}, #{lastupdatetime,jdbcType=TIMESTAMP}, \n" +
//            "      #{isdelete,jdbcType=BIT}, #{status,jdbcType=VARCHAR}, #{deliverymode,jdbcType=VARCHAR}, \n" +
//            "      #{deliverydate,jdbcType=DATE}, #{deliverydatestart,jdbcType=DATE}, #{deliverydateend,jdbcType=DATE}, \n" +
//            "      #{quoteenddate,jdbcType=DATE}, #{companyname,jdbcType=VARCHAR}, #{traderphone,jdbcType=VARCHAR}, \n" +
//            "      #{clienttype,jdbcType=INTEGER}, #{NCV,jdbcType=INTEGER}, #{RS,jdbcType=DECIMAL}, \n" +
//            "      #{TM,jdbcType=DECIMAL}, #{IM,jdbcType=DECIMAL}, #{ADV,jdbcType=DECIMAL}, #{RV,jdbcType=DECIMAL}, \n" +
//            "      #{AFT,jdbcType=INTEGER}, #{ASH,jdbcType=DECIMAL}, #{HGI,jdbcType=INTEGER}, #{GV,jdbcType=INTEGER}, \n" +
//            "      #{YV,jdbcType=INTEGER}, #{FC,jdbcType=INTEGER}, #{PS,jdbcType=INTEGER}, #{PSName,jdbcType=VARCHAR}, \n" +
//            "      #{ADV02,jdbcType=DECIMAL}, #{RV02,jdbcType=DECIMAL}, #{FC02,jdbcType=INTEGER}, \n" +
//            "      #{GV02,jdbcType=INTEGER}, #{YV02,jdbcType=INTEGER}, #{ADS,jdbcType=DECIMAL}, #{originplace,jdbcType=VARCHAR}, \n" +
//            "      #{startport,jdbcType=VARCHAR}, #{endport,jdbcType=VARCHAR}, #{deliverytime1,jdbcType=TIMESTAMP}, \n" +
//            "      #{deliverytime2,jdbcType=TIMESTAMP}, #{remarks,jdbcType=VARCHAR})</script>")
//    @Options(useGeneratedKeys = true, keyProperty = "id")
//    public void addQuote(Quote quote);

    @Insert("<script> insert into quotes (userid, demandid, " +
            " demandcode,  supplyton, quote, lastupdatetime, isdelete, status, deliverymode, deliverydate, " +
            " deliverydatestart, deliverydateend, quoteenddate, companyname, traderphone, clienttype, " +
            " NCV, NCV02, ADS, ADS02, RS, RS02, TM, TM02, IM, IM02, ADV, ADV02, RV, RV02, AFT, ASH, " +
            " ASH02, HGI, GV, GV02, YV, YV02,FC,FC02, PS, PSName, " +
            " originplace,remarks, startport, endport, deliverytime1,  deliverytime2)" +
            " values (#{userid,jdbcType=INTEGER}, #{demandid,jdbcType=INTEGER}, #{demandcode,jdbcType=VARCHAR}, " +
            " #{supplyton,jdbcType=INTEGER}, #{quote,jdbcType=INTEGER}, #{lastupdatetime,jdbcType=TIMESTAMP}, " +
            " #{isdelete,jdbcType=BIT}, #{status,jdbcType=VARCHAR}, #{deliverymode,jdbcType=VARCHAR}, " +
            " #{deliverydate,jdbcType=DATE}, #{deliverydatestart,jdbcType=DATE}, #{deliverydateend,jdbcType=DATE}, \n" +
            " #{quoteenddate,jdbcType=DATE}, #{companyname,jdbcType=VARCHAR}, #{traderphone,jdbcType=VARCHAR}, \n" +
            " #{clienttype,jdbcType=INTEGER}," +
            " <if test='NCV != null'>#{NCV,jdbcType=INTEGER},</if> <if test='NCV == null'>0,</if>" +
            " <if test='NCV02 != null'>#{NCV02,jdbcType=INTEGER},</if> <if test='NCV02 == null'>0,</if>" +
            " <if test='ADS != null'>#{ADS,jdbcType=DECIMAL},</if> <if test='ADS == null'>0.00,</if>" +
            " <if test='ADS02 != null'>#{ADS02,jdbcType=DECIMAL},</if> <if test='ADS02 == null'>0.00,</if>" +
            " #{RS,jdbcType=DECIMAL}, #{RS02,jdbcType=DECIMAL}, #{TM,jdbcType=DECIMAL}, #{TM02,jdbcType=DECIMAL}," +
            " <if test='IM != null'>#{IM,jdbcType=DECIMAL}, </if> <if test='IM == null'>0.00,</if>" +
            " <if test='IM02 != null'>#{IM02,jdbcType=DECIMAL}, </if> <if test='IM02 == null'>0.00,</if>" +
            " #{ADV,jdbcType=DECIMAL},#{ADV02,jdbcType=DECIMAL}, " +
            " <if test='RV != null'>#{RV,jdbcType=DECIMAL}, </if> <if test='RV == null'>0.00,</if>" +
            " <if test='RV02 != null'>#{RV02,jdbcType=DECIMAL}, </if> <if test='RV02 == null'>0.00,</if>" +
            " <if test='AFT != null'>#{AFT,jdbcType=INTEGER}, </if> <if test='AFT == null'>0,</if>" +
            " <if test='ASH != null'>#{ASH,jdbcType=DECIMAL}, </if> <if test='ASH == null'>0.0,</if>" +
            " <if test='ASH02 != null'>#{ASH02,jdbcType=DECIMAL}, </if> <if test='ASH02 == null'>0.0,</if>" +
            " <if test='HGI != null'>#{HGI,jdbcType=INTEGER}, </if> <if test='HGI == null'>0,</if>" +
            " <if test='GV != null'>#{GV,jdbcType=INTEGER}, </if> <if test='GV == null'>0,</if>" +
            " <if test='GV02 != null'>#{GV02,jdbcType=INTEGER}, </if> <if test='GV02 == null'>0,</if>" +
            " <if test='YV != null'>#{YV,jdbcType=INTEGER}, </if> <if test='YV == null'>0,</if>" +
            " <if test='YV02 != null'>#{YV02,jdbcType=INTEGER}, </if> <if test='YV02 == null'>0,</if>" +
            " <if test='FC != null'>#{FC,jdbcType=INTEGER}, </if> <if test='FC == null'>0,</if>" +
            " <if test='FC02 != null'>#{FC02,jdbcType=INTEGER}, </if> <if test='FC02 == null'>0,</if>" +
            " <if test='PS != null'>#{PS,jdbcType=INTEGER}, </if> <if test='PS == null'>0,</if>" +
            " #{PSName,jdbcType=VARCHAR}, " +
            " #{originplace,jdbcType=VARCHAR}, #{remarks,jdbcType=VARCHAR}, \n" +
            " #{startport,jdbcType=VARCHAR}, #{endport,jdbcType=VARCHAR}, #{deliverytime1,jdbcType=TIMESTAMP}, \n" +
            " #{deliverytime2,jdbcType=TIMESTAMP})</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public void addQuote(Quote quote);


//    public Quote(int userid, int demandid,String demandcode, int supplyton, BigDecimal quote, LocalDateTime lastupdatetime,
//                 String status, String deliverymode, LocalDate deliverydate, LocalDate deliverydatestart,
//                 LocalDate deliverydateend, LocalDate quoteenddate, String companyname, String traderphone,int clienttype,
//
//                 Integer NCV,Integer NCV02, BigDecimal ADS,BigDecimal ADS02, BigDecimal RS, BigDecimal RS02,BigDecimal TM,
//                 BigDecimal TM02,BigDecimal IM, BigDecimal IM02,BigDecimal ADV, BigDecimal ADV02,BigDecimal RV,BigDecimal RV02,
//                 Integer AFT, BigDecimal ASH, BigDecimal ASH02,Integer HGI, Integer GV, Integer GV02,Integer YV,Integer YV02,
//
//                 Integer FC, Integer FC02,Integer PS, String PSName,String originplace,
//                 String remarks, String startport, String endport, LocalDate deliverytime1, LocalDate deliverytime2)


//    @Insert("insert into quotes(userid,demandid,demandcode,supplyton,quote,lastupdatetime,status," +
//            "deliverymode,deliverydate,deliverydatestart,deliverydateend,quoteenddate,companyname," +
//            "traderphone, clienttype,NCV,ADS,RS,TM,IM,ADV,RV,AFT,ASH,HGI,GV,YV,FC,PS,PSName,ADV02,RV02,FC02,originplace,GV02,YV02,remarks,startport,endport,deliverytime1,deliverytime2) values(" +
//            "#{userid},#{demandid},#{demandcode},#{supplyton},#{quote},#{lastupdatetime},#{status}," +
//            "#{deliverymode},#{deliverydate},#{deliverydatestart},#{deliverydateend},#{quoteenddate}," +
//            "#{companyname},#{traderphone}, #{clienttype},#{NCV},#{ADS},#{RS},#{TM},#{IM},#{ADV},#{RV},#{AFT},#{ASH},#{HGI},#{GV},#{YV},#{FC},#{PS},#{PSName},#{ADV02},#{RV02},#{FC02},#{originplace},#{GV02},#{YV02},#{remarks},#{startport},#{endport},#{deliverytime1},#{deliverytime2})")




   /* //删除我的报价记录
    @Delete("delete from quotes where id=#{id}")
    public void deleteQuoteById(int id);*/

    //统计所有报价记录-进行中
    @Select("select count(*) from quotes where isdelete=0 and userid=#{userid} and (status='报价中' or status='比价中')")
    public int countAllMyQuoteUnderway(int userid);

    //个人中心,我的报价统计
    @Select("<script>select count(*) from quotes " +
            "where isdelete=0 and userid=#{userid} and (status='报价中' or status='比价中' or status='已中标' or status='未中标')"+
            "<if test='demandcode!=null and demandcode!=\"\"'> and demandcode=#{demandcode}</if>" +
            "<if test='status!=null and status!=\"全部\"'> and status=#{status}</if>" +
            "<if test='releaseDateStart!=null and releaseDateStart!=\"\"'>  and DATE_FORMAT(lastupdatetime,'%Y-%m-%d')  <![CDATA[ >=]]>  #{releaseDateStart} </if>" +
            "<if test='releaseDateEnd!=null and releaseDateEnd!=\"\"'>  and DATE_FORMAT(lastupdatetime,'%Y-%m-%d')  <![CDATA[ <=]]>  #{releaseDateEnd} </if></script>")
    public int countAllMyQuote(@Param("userid") int userid,
                               @Param("demandcode") String demandcode,
                               @Param("status") String status,
                               @Param("releaseDateStart") String releaseDateStart,
                               @Param("releaseDateEnd") String releaseDateEnd);

    @Select("<script>select count(*) from quotes " +
            "where isdelete=0 and userid=#{userid} and (status='报价中' or status='比价中' or status='已中标' or status='未中标')"+
            "<if test='status!=null and status!=\"全部\"'> and status=#{status}</if></script>")
    public int countAllMyQuoteBy(@Param("userid") int userid,
                               @Param("status") String status);

    //我的报价-进行中
    @Select("select * from quotes where userid=#{userid} and isdelete=0 and (status='报价中' or status='比价中') order by status,lastupdatetime desc limit #{startNum},#{pageSize}")
    public List<Quote> getTurnpageUnderwayList(@Param("userid") int userid,@Param("startNum") int startNum,@Param("pageSize")int pageSize);

    //个人中心,我的报价
    @Select("<script>select * from quotes" +
            " where userid=#{userid} and isdelete=0 and (status='报价中' or status='比价中' or status='已中标' or status='未中标') " +
            "<if test='demandcode!=null and demandcode!=\"\"'> and demandcode=#{demandcode}</if>" +
            "<if test='status!=null and status!=\"全部\"'> and status=#{status}</if>" +
            "<if test='releaseDateStart!=null and releaseDateStart!=\"\"'>  and DATE_FORMAT(lastupdatetime,'%Y-%m-%d')  <![CDATA[ >=]]>  #{releaseDateStart} </if>" +
            "<if test='releaseDateEnd!=null and releaseDateEnd!=\"\"'>  and DATE_FORMAT(lastupdatetime,'%Y-%m-%d')  <![CDATA[ <=]]>  #{releaseDateEnd} </if>" +
            " order by " +
            "<if test='ordercol!=null and ordercol!=\"\" and ordercol==\"lastupdatetime\" '>  lastupdatetime</if>" +
            "<if test='sortflag==0'>  desc</if>" +
            "<if test='sortflag==1'>  asc</if>" +
            " limit #{startNum},#{pageSize}</script>")
    public List<Quote> getTurnpageList(@Param("userid") int userid,
                                       @Param("demandcode") String demandcode,
                                       @Param("status") String status,
                                       @Param("releaseDateStart") String releaseDateStart,
                                       @Param("releaseDateEnd") String releaseDateEnd,
                                       @Param("startNum") int startNum,
                                       @Param("pageSize")int pageSize,
                                       @Param("ordercol") String ordercol,
                                       @Param("sortflag") int sortflag);

    @Select("<script>select * from quotes" +
            " where userid=#{userid} and isdelete=0 and (status='报价中' or status='比价中' or status='已中标' or status='未中标') " +
            "<if test='status!=null and status!=\"全部\"'> and status=#{status}</if>" +
            " order by lastupdatetime desc " +
            " limit #{startNum},#{pageSize}</script>")
    public List<Quote> getTurnpageListBy(@Param("userid") int userid,
                                       @Param("status") String status,
                                       @Param("startNum") int startNum,
                                       @Param("pageSize")int pageSize);

    //统计所有报价记录-已中标
    @Select("select count(*) from quotes where isdelete=0 and userid=#{userid} and status='已中标'")
    public int countAllMyQuoteBid(int userid);

    //我的报价-已中标
    @Select("select * from quotes where userid=#{userid} and isdelete=0 and status='已中标' order by lastupdatetime desc limit #{startNum},#{pageSize}")
    public List<Quote> getTurnpageBidList(@Param("userid") int userid,@Param("startNum") int startNum,@Param("pageSize")int pageSize);

    //统计所有报价记录-未中标
    @Select("select count(*) from quotes where isdelete=0 and userid=#{userid} and status='未中标'")
    public int countAllMyQuoteNotBid(int userid);

    //我的报价-未中标
    @Select("select * from quotes where userid=#{userid} and isdelete=0 and status='未中标' order by lastupdatetime desc limit #{startNum},#{pageSize}")
    public List<Quote> getTurnpageNotBidList(@Param("userid") int userid,@Param("startNum") int startNum,@Param("pageSize")int pageSize);

    //判断是否对此需求报价过,返回对象判断,利用对象
    @Select("select * from quotes where userid=#{userid} and demandid=#{demandid} limit 1")
    public Quote getQuoteByUserIdAndDemandid(@Param("userid") int userid,@Param("demandid")int demandid);

    @Select("select * from quotes where userid=#{userid} and demandcode=#{demandcode}")
    public Quote getQuoteByUserIdAndDemandcode(@Param("userid") int userid,@Param("demandcode")String demandid);

    //修改申供吨数和报价

    @Update("<script>" +
            " update quotes \n" +

//            "    set userid = #{userid,jdbcType=INTEGER},\n" +
//            "      demandid = #{demandid,jdbcType=INTEGER},\n" +
//            "      demandcode = #{demandcode,jdbcType=VARCHAR},\n" +

            "     set quote = #{quote,jdbcType=INTEGER},"+
            "      supplyton = #{supplyton,jdbcType=INTEGER}," +
            "      lastupdatetime = now()," +

//            "      isdelete = #{isdelete,jdbcType=BIT},\n" +
//            "      status = #{status,jdbcType=VARCHAR},\n" +
//            "      deliverymode = #{deliverymode,jdbcType=VARCHAR},\n" +
//            "      deliverydate = #{deliverydate,jdbcType=DATE},\n" +
//            "      deliverydatestart = #{deliverydatestart,jdbcType=DATE},\n" +
//            "      deliverydateend = #{deliverydateend,jdbcType=DATE},\n" +
//            "      quoteenddate = #{quoteenddate,jdbcType=DATE},\n" +
//            "      companyname = #{companyname,jdbcType=VARCHAR},\n" +
//            "      traderphone = #{traderphone,jdbcType=VARCHAR},\n" +
//            "      clienttype = #{clienttype,jdbcType=INTEGER},\n" +


            " <if test='NCV != null'>NCV=#{NCV,jdbcType=INTEGER},</if> <if test='NCV == null'>NCV=0,</if>" +
            " <if test='NCV02 != null'>NCV02=#{NCV02,jdbcType=INTEGER},</if> <if test='NCV02 == null'>NCV02=0,</if>" +
            " RS=#{RS,jdbcType=DECIMAL},RS02 = #{RS02,jdbcType=DECIMAL},TM = #{TM,jdbcType=DECIMAL},TM02 = #{TM02,jdbcType=DECIMAL}," +
            " <if test='IM != null'>IM=#{IM,jdbcType=DECIMAL},</if> <if test='IM == null'>IM=0.00,</if>" +
            " <if test='IM02 != null'>IM02=#{IM02,jdbcType=DECIMAL},</if> <if test='IM02 == null'>IM02=0.00,</if>" +
            " <if test='ADV02 != null'>ADV02=#{ADV02,jdbcType=DECIMAL},</if> <if test='ADV02 == null'>ADV02=0.00,</if>" +
            " <if test='RV != null'>RV=#{RV,jdbcType=DECIMAL},</if> <if test='RV == null'>RV=0.00,</if>" +
            " <if test='RV02 != null'>RV02=#{RV02,jdbcType=DECIMAL},</if> <if test='RV02 == null'>RV02=0.00,</if>" +
            " <if test='AFT != null'>AFT=#{AFT,jdbcType=INTEGER},</if> <if test='AFT == null'>AFT=0,</if>" +
            " <if test='ASH != null'>ASH=#{ASH,jdbcType=DECIMAL},</if> <if test='ASH == null'>ASH=0.0,</if>" +
            " <if test='ASH02 != null'>ASH02=#{ASH02,jdbcType=DECIMAL},</if> <if test='ASH02 == null'>ASH02=0.0,</if>" +
            " <if test='HGI != null'>HGI=#{HGI,jdbcType=INTEGER},</if> <if test='HGI == null'>HGI=0,</if>" +
            " <if test='GV != null'>GV=#{GV,jdbcType=INTEGER},</if> <if test='GV == null'>GV=0,</if>" +
            " <if test='YV != null'>YV=#{YV,jdbcType=INTEGER},</if> <if test='YV == null'>YV=0,</if>" +
            " <if test='FC != null'>FC=#{FC,jdbcType=INTEGER},</if> <if test='FC == null'>FC=0,</if>" +
            " <if test='PS != null'>PS=#{PS,jdbcType=INTEGER},</if> <if test='PS == null'>PS=0,</if>" +
            " PSName = #{PSName,jdbcType=VARCHAR}, ADV=#{ADV,jdbcType=DECIMAL}, ADV02 = #{ADV02,jdbcType=DECIMAL}," +
            " <if test='RV != null'>RV02=#{RV02,jdbcType=DECIMAL},</if> <if test='RV == null'>RV=0.00,</if>" +
            " <if test='FC02 != null'>FC02=#{FC02,jdbcType=INTEGER},</if> <if test='FC02 == null'>FC02=0.00,</if>" +
            " <if test='GV02 != null'>GV02=#{GV02,jdbcType=INTEGER},</if> <if test='GV02 == null'>GV02=0.00,</if>" +
            " <if test='YV02 != null'>YV02=#{YV02,jdbcType=INTEGER},</if> <if test='YV02 == null'>YV02=0.00,</if>" +
            " <if test='ADS != null'>ADS=#{ADS,jdbcType=DECIMAL},</if> <if test='ADS == null'>ADS=0.00,</if>" +
            " <if test='ADS02 != null'>ADS02=#{ADS02,jdbcType=DECIMAL},</if> <if test='ADS02 == null'>ADS02=0.00,</if>" +
            " originplace = #{originplace,jdbcType=VARCHAR}, startport = #{startport,jdbcType=VARCHAR}, " +
            " endport = #{endport,jdbcType=VARCHAR}, deliverytime1 = #{deliverytime1,jdbcType=TIMESTAMP}," +
            " deliverytime2 = #{deliverytime2,jdbcType=TIMESTAMP}, remarks = #{remarks,jdbcType=VARCHAR} " +
            " where id = #{id,jdbcType=INTEGER}" +
            "</script>")
    public void modifyQuoteByQuoteid(Quote quote);



    //如果对需求多次报价
//    @Update("update quotes set originplace=#{originplace},NCV=#{NCV},ADS=#{ADS},RS=#{RS}," +
//            "TM=#{TM},IM=#{IM},ADV=#{ADV},RV=#{RV},AFT=#{AFT},ASH=#{ASH}," +
//            "HGI=#{HGI},GV=#{GV},YV=#{YV},FC=#{FC},PS=#{PS},PSName=#{PSName}," +
//            "ADV02=#{ADV02},RV02=#{RV02},FC02=#{FC02},GV02=#{GV02},YV02=#{YV02},remarks=#{remarks}," +
//            "startport=#{startport},endport=#{endport},deliverytime1=#{deliverytime1},deliverytime2=#{deliverytime2}," +
//            " supplyton=#{supplyton},quote=#{quote},lastupdatetime=now() where userid=#{userid} and demandid=#{demandid}" )


    @Update("<script>" +
            " update quotes \n" +

//            "    set userid = #{userid,jdbcType=INTEGER},\n" +
//            "      demandid = #{demandid,jdbcType=INTEGER},\n" +
//            "      demandcode = #{demandcode,jdbcType=VARCHAR},\n" +

            "     set quote = #{quote,jdbcType=INTEGER},"+
            "      supplyton = #{supplyton,jdbcType=INTEGER}," +
            "      lastupdatetime = now()," +

//            "      isdelete = #{isdelete,jdbcType=BIT},\n" +
//            "      status = #{status,jdbcType=VARCHAR},\n" +
//            "      deliverymode = #{deliverymode,jdbcType=VARCHAR},\n" +
//            "      deliverydate = #{deliverydate,jdbcType=DATE},\n" +
//            "      deliverydatestart = #{deliverydatestart,jdbcType=DATE},\n" +
//            "      deliverydateend = #{deliverydateend,jdbcType=DATE},\n" +
//            "      quoteenddate = #{quoteenddate,jdbcType=DATE},\n" +
//            "      companyname = #{companyname,jdbcType=VARCHAR},\n" +
//            "      traderphone = #{traderphone,jdbcType=VARCHAR},\n" +
//            "      clienttype = #{clienttype,jdbcType=INTEGER},\n" +


            " <if test='NCV != null'>NCV=#{NCV,jdbcType=INTEGER},</if> <if test='NCV == null'>NCV=0,</if>" +
            " <if test='NCV02 != null'>NCV02=#{NCV02,jdbcType=INTEGER},</if> <if test='NCV02 == null'>NCV02=0,</if>" +
            " RS=#{RS,jdbcType=DECIMAL},RS02 = #{RS02,jdbcType=DECIMAL},TM = #{TM,jdbcType=DECIMAL},TM02 = #{TM02,jdbcType=DECIMAL}," +
            " <if test='IM != null'>IM=#{IM,jdbcType=DECIMAL},</if> <if test='IM == null'>IM=0.00,</if>" +
            " <if test='IM02 != null'>IM02=#{IM02,jdbcType=DECIMAL},</if> <if test='IM02 == null'>IM02=0.00,</if>" +
            " <if test='ADV02 != null'>ADV02=#{ADV02,jdbcType=DECIMAL},</if> <if test='ADV02 == null'>ADV02=0.00,</if>" +
            " <if test='RV != null'>RV=#{RV,jdbcType=DECIMAL},</if> <if test='RV == null'>RV=0.00,</if>" +
            " <if test='RV02 != null'>RV02=#{RV02,jdbcType=DECIMAL},</if> <if test='RV02 == null'>RV02=0.00,</if>" +
            " <if test='AFT != null'>AFT=#{AFT,jdbcType=INTEGER},</if> <if test='AFT == null'>AFT=0,</if>" +
            " <if test='ASH != null'>ASH=#{ASH,jdbcType=DECIMAL},</if> <if test='ASH == null'>ASH=0.0,</if>" +
            " <if test='ASH02 != null'>ASH02=#{ASH02,jdbcType=DECIMAL},</if> <if test='ASH02 == null'>ASH02=0.0,</if>" +
            " <if test='HGI != null'>HGI=#{HGI,jdbcType=INTEGER},</if> <if test='HGI == null'>HGI=0,</if>" +
            " <if test='GV != null'>GV=#{GV,jdbcType=INTEGER},</if> <if test='GV == null'>GV=0,</if>" +
            " <if test='YV != null'>YV=#{YV,jdbcType=INTEGER},</if> <if test='YV == null'>YV=0,</if>" +
            " <if test='FC != null'>FC=#{FC,jdbcType=INTEGER},</if> <if test='FC == null'>FC=0,</if>" +
            " <if test='PS != null'>PS=#{PS,jdbcType=INTEGER},</if> <if test='PS == null'>PS=0,</if>" +
            " PSName = #{PSName,jdbcType=VARCHAR}, ADV=#{ADV,jdbcType=DECIMAL}, ADV02 = #{ADV02,jdbcType=DECIMAL}," +
            " <if test='RV != null'>RV02=#{RV02,jdbcType=DECIMAL},</if> <if test='RV == null'>RV=0.00,</if>" +
            " <if test='FC02 != null'>FC02=#{FC02,jdbcType=INTEGER},</if> <if test='FC02 == null'>FC02=0.00,</if>" +
            " <if test='GV02 != null'>GV02=#{GV02,jdbcType=INTEGER},</if> <if test='GV02 == null'>GV02=0.00,</if>" +
            " <if test='YV02 != null'>YV02=#{YV02,jdbcType=INTEGER},</if> <if test='YV02 == null'>YV02=0.00,</if>" +
            " <if test='ADS != null'>ADS=#{ADS,jdbcType=DECIMAL},</if> <if test='ADS == null'>ADS=0.00,</if>" +
            " <if test='ADS02 != null'>ADS02=#{ADS02,jdbcType=DECIMAL},</if> <if test='ADS02 == null'>ADS02=0.00,</if>" +
            " originplace = #{originplace,jdbcType=VARCHAR}, startport = #{startport,jdbcType=VARCHAR}, " +
            " endport = #{endport,jdbcType=VARCHAR}, deliverytime1 = #{deliverytime1,jdbcType=TIMESTAMP}," +
            " deliverytime2 = #{deliverytime2,jdbcType=TIMESTAMP}, remarks = #{remarks,jdbcType=VARCHAR} " +
            "where userid=#{userid} and demandid=#{demandid}" +
            "</script>")

    public void saveMoreQuote(Quote quote);



    //通过id查询报价记录
    @Select("select * from quotes where id=#{id}")
    public Quote getQuoteById(int id);

    //判断某客户是否对某需求信息报价过
    @Select("select count(*) from quotes where userid=#{userid} and demandid=#{demandid}")
    public int countQuotedByUserIdAndDemandid(@Param("userid") int userid,@Param("demandid")int demandid);

    //取消我的报价,设置状态为删除
    @Update("update quotes set isdelete=1 where id=#{id} and userid=#{userid}")
    public void modifyIsdeleteById(@Param("id") int id,@Param("userid") int userid);

    //取消我的报价,设置状态为删除
    @Update("update quotes set isdelete=1 where demandcode=#{demandcode}")
    public void modifyIsdeleteByDemandcode(String demandcode);

    //根据需求编号获取所有对应的报价
    @Select("select * from quotes where demandcode=#{demandcode} and isdelete=0 group by userid")
    public List<Quote> getQuoteByDemandcode(String demandcode);

    //根据报价id修改状态-已中标
    @Update("update quotes set status=#{status} where id=#{id}")
    public void modifyStatusByQuoteid(@Param("status") String status,@Param("id") int id);

    //我的客户---报价
    final String listAllSql="<where><if test='status!=null'> and q.status=#{status} </if>" +
            //"<if test='currentUser.isAdmin!=true'> and q.traderphone=#{currentUser.phone} </if>
            "</where>";

    @Select("<script>select distinct(q.id), q.demandcode, q.companyname, q.lastupdatetime, q.supplyton, q.quote, q.status, a.name tradername from quotes q left join admins a on q.traderphone=a.phone where q.status=#{status} order by q.lastupdatetime desc limit #{pageSize} offset #{offset}</script>")
    public List<Quote> showAllList(@Param("status") String status,
                                   @Param("pageSize")int pageSize,
                                   @Param("offset")int offset);

    @Select("<script>select count(distinct(id)) from quotes where status=#{status} </script>")
    public int showAllListCount(@Param("status") String status);

    public default Pager<Quote> getAllQuotesList(String status, int page, int pagesize){
        return Pager.config(this.showAllListCount(status), (int limit, int offset) -> this.showAllList(status,limit,offset))
                .page(page, pagesize);
    }

    @Select("<script> select q.id,u.securephone username,q.lastupdatetime,q.supplyton, " +
            "q.companyname, q.quote,q.status, q.clienttype from quotes q left join  " +
            "users u on q.userid=u.id where q.demandcode=#{demandcode} " +
            "<if test='clienttype!=0'> and q.clienttype=#{clienttype}</if> " +
            "<if test='status!=null'> and q.status=#{status}</if>  " +
            "order by q.id limit #{pageSize} offset #{offset}</script>")
    public List<Quote> findByDemandCode(@Param("demandcode") String demandcode,
                                        @Param("clienttype") int clienttype,
                                        @Param("status") String status,
                                        @Param("pageSize") int pageSize,
                                        @Param("offset") int offset);

    @Select("<script> select count(1) from quotes q where q.demandcode = #{demandcode}" +
            "<if test='clienttype!=0'> and q.clienttype=#{clienttype}</if> " +
            "<if test='status!=null'> and q.status = #{status} </if> </script>")
    public int countByDemandCode(@Param("demandcode") String demandcode,
                                 @Param("clienttype") int clienttype,
                                 @Param("status") String status);

    public default Pager<Quote> getQuoteByDemandCode(String demandcode, int clienttype, String status, int page, int pagesize){
        return Pager.config(this.countByDemandCode(demandcode, clienttype, status), (int limit, int offset) -> this.findByDemandCode(demandcode, clienttype, status,limit, offset))
                .page(page, pagesize);
    }

    @Select("select * from quotes where status='已中标'")
    List<Quote> getAllCompQuotes();

    //同一需求下还没有中标的报价数目
    @Select("select count(1) from  quotes  where  demandcode = #{demandcode}  and  status !='已中标' and isdelete=0")
    public int getCountNotBidByDemandcode(@Param("demandcode") String demandcode);

    //导入数据
    @Select("select * from demands  where  demandcode = #{demandcode}")
    public int getDemandByDemandcode(@Param("demandcode") String demandcode);
}
