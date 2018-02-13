package kitt.core.persistence;

import kitt.core.domain.Admin;
import kitt.core.domain.DealDemand;
import kitt.core.domain.Demand;
import kitt.core.util.PageQueryParam;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by fanjun on 14-11-25.
 */
public interface DemandMapper {

    //根据省份,港口,热值,硫份组合查询的结果集. 条件:已审核通过,未删除.顺序:发布时间倒序
    final String checkWhereSql =
            "<where> "+
                    "<if test='coalWord!=null'> coaltype = #{coalWord}</if>" +
                    "<if test='area!=0'> and regionId = #{area}</if>" +
                    "<if test='province!=0'> and provinceId = #{province}</if>" +
                    "<if test='port!=0 and port != 999'> and portId = #{port}</if>" +
                    "<if test='otherDeliveryPlace ==999'> and otherplace is not null </if>" +
                    "<if test='lowHotValue!=null'> and NCV &gt;= #{lowHotValue}</if>" +
                    "<if test='highHotValue!=null'> and NCV &lt;= #{highHotValue}</if>" +
                    "<if test='lowSulfurContent!=null'> and RS &gt;= #{lowSulfurContent}</if>" +
                    "<if test='highSulfurContent!=null'> and RS &lt;= #{highSulfurContent}</if>" +
                    " and checkstatus='审核通过' "+
                    " and isdelete=0 "+       //报价结束后超过7天的记录状态改为删除
                    "</where> ";

    @Select("<script>" +
            "select * from demands " +
            checkWhereSql+
            " order by case tradestatus  when '未开始报价' then  1  when '开始报价'  then 2 when '匹配中' then  3  when  '已中标' then 4  when  '报价结束' then 5 when '已过期' then 6   end , releasetime desc " +
            " limit #{limit} offset #{offset}"+
            "</script>")
    public List<Demand> getDemandsByCheck(@Param("coalWord") String coalWord,@Param("area") int area,@Param("province") int province,@Param("port") int port,@Param("otherDeliveryPlace") int otherDeliveryPlace,
                                          @Param("lowHotValue") Integer lowHotValue,@Param("highHotValue") Integer highHotValue,
                                          @Param("lowSulfurContent") BigDecimal lowSulfurContent,@Param("highSulfurContent") BigDecimal highSulfurContent,
                                          @Param("limit") int limit,@Param("offset") int offset);

    //组合查询的结果总数
    @Select("<script>" +
            "select count(*) from demands " +
            checkWhereSql+
            "</script>")
    public int countDemandsByCheck(@Param("coalWord") String coalWord,@Param("area") int area,@Param("province") int province,@Param("port") int port,@Param("otherDeliveryPlace") int otherDeliveryPlace,
                                   @Param("lowHotValue") Integer lowHotValue,@Param("highHotValue") Integer highHotValue,
                                   @Param("lowSulfurContent") BigDecimal lowSulfurContent,@Param("highSulfurContent") BigDecimal highSulfurContent);

    //根据需求编号查询需求对象
    @Select("select * from demands where demandcode=#{demandcode}")
    public Demand getDemandByDemandcode(@Param("demandcode")String demandcode);


    @Select("select * from demands where id=#{demandId}")
    public Demand getDemandByDemandId(@Param("demandId")int demandId);


    //根据需求编号查询需求对象
    @Select("select * from demands where demandcode=#{demandcode} and userid=#{userid}")
    public Demand getDemandByDemandcodeAndUserid(@Param("demandcode") String demandcode,@Param("userid") int userid);

    //根据需求id查询需求对象
    @Select("select * from demands where id=#{id} and isdelete=0")
    public Demand getDemandById(int id);

    @Select("select * from demands where id=#{id}")
    public Demand getDemandByIddelete(int id);

    //根据需求id查询需求对象 包括已删除的
    @Select("select * from demands where id=#{id}")
    public Demand getDemandByIdIncludeDeleted(int id);

    //报价成功需求表报价人数加1
    @Update("update demands set quotenum=quotenum+1 where demandcode=#{demandcode}")
    public void plusQuotenum(String demandcode);

    //统计总需求数
    @Select("select count(*) from demands where releasestatus=1 and isdelete=0")
    public int countAllDemands();

    //个人中心-我的报价删除,报价人数减1
    @Update("update demands set quotenum=quotenum-1 where demandcode=#{demandcode}")
    public void minusQuotenum(String demandcode);

    //添加需求
//    @Insert("<script>insert into demands(userid,demandcode,demandcustomer,coaltype,NCV," +
//            "<if test='ADS!=null'>ADS,</if>" +
//            "RS,TM,ADV," +
//            "<if test='IM!=null'>IM,</if>" +
//            "<if test='RV!=null'>RV,</if>" +
//            "<if test='AFT!=null'>AFT,</if>" +
//            "<if test='ASH!=null'>ASH,</if>" +
//            "<if test='HGI!=null'>HGI,</if>" +
//            "deliverydistrict,deliveryprovince,deliveryplace,otherplace,demandamount," +
//            "deliverydate,deliverydatestart,deliverydateend,deliverymode,residualdemand,inspectionagency," +
//            "otherorg,quoteenddate,noshowdate,releasetime,checkstatus,tradestatus," +
//            "releasestatus,regionId,provinceId,portId,releasecomment, clienttype) values(" +
//            "#{userid},dateseq_next_value('XQ'),#{demandcustomer},#{coaltype},#{NCV}," +
//            "<if test='ADS!=null'>#{ADS},</if>" +
//            "#{RS},#{TM},#{ADV}," +
//            "<if test='IM!=null'>#{IM},</if>" +
//            "<if test='RV!=null'>#{RV},</if>" +
//            "<if test='AFT!=null'>#{AFT},</if>" +
//            "<if test='ASH!=null'>#{ASH},</if>" +
//            "<if test='HGI!=null'>#{HGI},</if>" +
//            "#{deliverydistrict},#{deliveryprovince},#{deliveryplace},#{otherplace},#{demandamount}," +
//            "#{deliverydate},#{deliverydatestart},#{deliverydateend},#{deliverymode},#{residualdemand},#{inspectionagency},#{otherorg}," +
//            "#{quoteenddate},#{noshowdate},#{releasetime},#{checkstatus},#{tradestatus},#{releasestatus}," +
//            "#{regionId},#{provinceId},#{portId},#{releasecomment}, #{clienttype})</script>")
//    @Options(useGeneratedKeys=true, keyProperty="id")
//    public int addDemand(Demand demand);


    //添加需求
    @Insert("<script>" +
            "insert into demands(userid,demandcode,demandcustomer,coaltype," +
            "NCV,NCV02, ADS,ADS02, RS,RS02, TM,TM02, IM,IM02, ADV,ADV02, RV, AFT, ASH,ASH02, HGI,GV,YV,FC,PS,PSName,RV02,FC02,GV02,YV02," +
            "deliverydistrict,deliveryprovince,deliveryplace,otherplace,demandamount," +
            "deliverydate,deliverydatestart,deliverydateend,deliverymode,residualdemand,inspectionagency," +
            "otherorg,quoteenddate,noshowdate,releasetime,checkstatus,tradestatus," +
            "releasestatus,regionId,provinceId,portId,releasecomment, clienttype, paymode,transportmode) " +
            "values(" +
            "#{userid},dateseq_next_value('XQ'),#{demandcustomer},#{coaltype}," +
            "<if test='NCV!=null'>#{NCV},</if> <if test='NCV==null'>0,</if>" +
            "<if test='NCV02!=null'>#{NCV02},</if> <if test='NCV02==null'>0,</if>" +
            "<if test='ADS!=null'>#{ADS},</if> <if test='ADS==null'>0.00,</if>" +
            "<if test='ADS02!=null'>#{ADS02},</if> <if test='ADS02==null'>0.00,</if>" +
            "<if test='RS!=null'>#{RS},</if> <if test='RS==null'>0,</if>" +
            "<if test='RS02!=null'>#{RS02},</if> <if test='RS02==null'>0,</if>" +
            "<if test='TM!=null'>#{TM},</if> <if test='TM==null'>0,</if>" +
            "<if test='TM02!=null'>#{TM02},</if> <if test='TM02==null'>0,</if>" +
            "<if test='IM!=null'>#{IM},</if> <if test='IM==null'>0.00,</if>" +
            "<if test='IM02!=null'>#{IM02},</if> <if test='IM02==null'>0.00,</if>" +
            "<if test='ADV!=null'>#{ADV},</if> <if test='ADV==null'>0.00,</if>" +
            "<if test='ADV02!=null'>#{ADV02},</if> <if test='ADV02==null'>0.00,</if>" +
            "<if test='RV!=null'>#{RV},</if> <if test='RV==null'>0.00,</if>" +
            "<if test='AFT!=null'>#{AFT},</if> <if test='AFT==null'>0,</if>" +
            "<if test='ASH!=null'>#{ASH},</if> <if test='ASH==null'>0.0,</if>" +
            "<if test='ASH02!=null'>#{ASH02},</if> <if test='ASH02==null'>0.0,</if>" +
            "<if test='HGI!=null'>#{HGI},</if> <if test='HGI==null'>0,</if>" +
            "<if test='GV!=null'>#{GV},</if> <if test='GV==null'>0,</if>" +
            "<if test='YV!=null'>#{YV},</if> <if test='YV==null'>0,</if>" +
            "<if test='FC!=null'>#{FC},</if> <if test='FC==null'>0,</if>" +
            "<if test='PS!=null'>#{PS},</if> <if test='PS==null'>0,</if>" +
            "#{PSName}," +
            "<if test='RV02!=null'>#{RV02},</if> <if test='RV02==null'>0.00,</if>" +
            "<if test='FC02!=null'>#{FC02},</if> <if test='FC02==null'>0,</if>" +
            "<if test='GV02!=null'>#{GV02},</if> <if test='GV02==null'>0,</if>" +
            "<if test='YV02!=null'>#{YV02},</if> <if test='YV02==null'>0,</if>" +
            "#{deliverydistrict},#{deliveryprovince},#{deliveryplace},#{otherplace},#{demandamount}," +
            "#{deliverydate},#{deliverydatestart},#{deliverydateend},#{deliverymode},#{residualdemand},#{inspectionagency},#{otherorg}," +
            "#{quoteenddate},#{noshowdate}, now(),#{checkstatus},#{tradestatus},#{releasestatus}," +
            "#{regionId},#{provinceId},#{portId},#{releasecomment}, #{clienttype},#{paymode},#{transportmode})" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int addDemand(Demand demand);

    //更新需求编号
    @Update("update demands set demandcode=#{code} where id=#{id}")
    public void modifyDemandcodeWithCode(@Param("code") String code,@Param("id") int id);

    //更新发布状态
    @Update("update demands set releasestatus=1 where demandcode=#{demandcode}")
    public void modifyReleaseStatusByDemandcode(String demandcode);


    //更改需求
    @Update("<script>update demands set coaltype=#{coaltype}," +
            "<if test='RS!=null'>RS=#{RS},</if>"+
            "<if test='RS==null'>RS=0,</if>"+
            "<if test='RS02!=null'>RS02=#{RS02},</if>"+
            "<if test='RS02==null'>RS02=0,</if>"+
            "<if test='TM!=null'>TM=#{TM},</if>"+
            "<if test='TM==null'>TM=0,</if>"+
            "<if test='TM02!=null'>TM02=#{TM02},</if>"+
            "<if test='TM02==null'>TM02=0,</if>"+
            "<if test='NCV!=null'>NCV=#{NCV},</if>"+
            "<if test='NCV==null'>NCV=0,</if>"+
            "<if test='NCV02!=null'>NCV02=#{NCV02},</if>"+
            "<if test='NCV02==null'>NCV02=0,</if>"+
            "<if test='ADS!=null'>ADS=#{ADS},</if>"+
            "<if test='ADS==null'>ADS=0,</if>"+
            "<if test='ADS02!=null'>ADS02=#{ADS02},</if>"+
            "<if test='ADS02==null'>ADS02=0,</if>"+
            "<if test='IM!=null'>IM=#{IM},</if>" +
            "<if test='IM==null'>IM=0,</if>" +
            "<if test='IM02!=null'>IM02=#{IM02},</if>" +
            "<if test='IM02==null'>IM02=0,</if>" +
            "<if test='RV!=null'>RV=#{RV},</if>" +
            "<if test='RV==null'>RV=0,</if>" +
            "<if test='AFT!=null'>AFT=#{AFT},</if>" +
            "<if test='AFT==null'>AFT=0,</if>" +
            "<if test='ASH!=null'>ASH=#{ASH},</if>" +
            "<if test='ASH==null'>ASH=0,</if>" +
            "<if test='ASH02!=null'>ASH02=#{ASH02},</if>" +
            "<if test='ASH02==null'>ASH02=0,</if>" +
            "<if test='HGI!=null'>HGI=#{HGI},</if>" +
            "<if test='HGI==null'>HGI=0,</if>" +
            "<if test='GV!=null'>GV=#{GV},</if> " +
            "<if test='GV==null'>GV=0,</if> " +
            "<if test='YV!=null'>YV=#{YV},</if>" +
            "<if test='YV==null'>YV=0,</if>" +
            " <if test='FC!=null'>FC=#{FC},</if>" +
            " <if test='FC==null'>FC=0,</if>" +
            "<if test='PS!=null'>PS=#{PS},</if> " +
            "<if test='PS==null'>PS=0,</if> " +
            "<if test='ADV02!=null'>ADV02=#{ADV02},</if>" +
            "<if test='ADV02==null'>ADV02=0,</if>" +
            "<if test='RV02!=null'>RV02=#{RV02},</if> " +
            "<if test='RV02==null'>RV02=0,</if> " +
            "<if test='FC02!=null'>FC02=#{FC02},</if> " +
            "<if test='FC02==null'>FC02=0,</if> " +
            "<if test='GV02!=null'>GV02=#{GV02},</if> " +
            "<if test='GV02==null'>GV02=0,</if> " +
            "<if test='YV02!=null'>YV02=#{YV02},</if>" +
            "<if test='YV02==null'>YV02=0,</if>" +

            "ADV=#{ADV},<if test='checkstatus!=null'>checkstatus=#{checkstatus},</if>deliverydistrict=#{deliverydistrict}," +
            "deliveryprovince=#{deliveryprovince},deliveryplace=#{deliveryplace},otherplace=#{otherplace},demandamount=#{demandamount}," +
            "deliverydate=#{deliverydate},deliverydatestart=#{deliverydatestart},deliverydateend=#{deliverydateend}," +
            "deliverymode=#{deliverymode},residualdemand=#{residualdemand},inspectionagency=#{inspectionagency}," +
            "otherorg=#{otherorg},quoteenddate=#{quoteenddate},noshowdate=#{noshowdate},releasetime=now()," +
            "regionId=#{regionId},provinceId=#{provinceId},portId=#{portId} " +
            "<if test='PSName!=null'>,PSName=#{PSName}</if> " +
            "<if test='paymode!=null'>,paymode=#{paymode}</if> <if test='transportmode!=null'>,transportmode=#{transportmode}</if>" +
            "<if test='comment!=null'>,comment=#{comment}</if> <if test='releasecomment!=null'>,releasecomment=#{releasecomment}</if>" +
            "where demandcode=#{demandcode}</script>")
    public void modifyDemand(Demand demand);

    //更改需求审核的审核状态和备注
    @Update("update demands set checkstatus=#{checkstatus},comment=#{comment},checktime=now() where demandcode=#{demandcode} and checkstatus='待审核'")
    public int modifyCheckstatusAndComment(@Param("checkstatus") String checkstatus,@Param("comment") String comment,@Param("demandcode") String demandcode);

    //获取我的发布的需求
    @Select("select * from demands where releasestatus=1 and userid=#{userid} order by releasetime desc limit #{startNum},#{pageSize}")
    public List<Demand> getTurnpageListWithUserid(@Param("userid") int userid,@Param("startNum") int startNum,@Param("pageSize") int pageSize);

/*    //我的发布-删除需求
    @Delete("delete from demands where demandcode=#{demandcode}")
    public void deleteMyDemandByDemandcode(String demandcode);*/

    //更改交易状态
    @Update("update demands set tradestatus=#{tradestatus},tradername=#{dealername},traderphone=#{dealerphone},traderId=#{traderId} where demandcode=#{demandcode}")
    public int modifyTradestatusByDemandcode(@Param("dealername")String tradername,@Param("dealerphone")String traderphone,@Param("tradestatus") String tradestatus,@Param("demandcode") String demandcode,@Param("traderId") int traderId);

    //导航栏上方模糊搜索

    //后台需求列表模糊搜索
    @Select("select d.* from(select * from demands where " +
            " demandcustomer like concat('%',#{content},'%') or "+
            " demandcode like concat('%',#{content},'%') or "+
            " deliverydistrict like concat('%',#{content},'%') or "+
            " deliveryprovince like concat('%',#{content},'%') or "+
            " deliveryplace like concat('%',#{content},'%') or "+
            " where d.releasestatus=1")
    public List<Demand> getDemandByContent(String content);

   /* //每天0点检查修改今天报价截止的需求记录改为报价结束
    @Update("update demands set tradestatus='报价结束' where date_add(quoteenddate,interval 1 day) = date_format(now(),'%Y-%m-%d')")
    public void modifyTradestatusTask();*/

/*    //将超过7天的记录状态改为删除
    @Update("update demands set isdelete=1 where noshowdate = date_format(now(),'%Y-%m-%d')")
    public void modifyIsdeleteTask();*/

    //设置需求状态为已删除
    @Update("update demands set isdelete=1 where demandcode=#{demandcode}")
    public void modifyIsdeleteByDemandcode(String demandcode);

    //设置需求状态为已删除
    @Update("update demands set isdelete=1 where demandcode=#{demandcode} and userid=#{userid}")
    public void modifyIsdeleteByDemandcodeAndUserid(@Param("demandcode") String demandcode,@Param("userid") int userid);

    //根据需求id和用户id查询,不能对自己发布的需求报价
    @Select("select count(*) from demands where id=#{demandid} and userid=#{userid}")
    public int countMydemandByDemandidAndUserid(@Param("demandid") int demandid,@Param("userid") int userid);


    final String searchCondition =
            "<if test='deliveryregion!=0'> and regionId=#{deliveryregion}</if>" +
            "<if test='deliveryprovince!=0'> and provinceId=#{deliveryprovince}</if>" +
            "<if test='deliveryplace!=0'> and portId=#{deliveryplace}</if>"+
            "<if test='content!= null'> and (demandcode like #{content} or tradername like #{content})</if>" +
            "<if test='clienttype!=0'> and d.clienttype=#{clienttype}</if>" +
            "<if test='startDate != null and startDate != \"\" and endDate != null and endDate != \"\"'> and (releasetime between #{startDate} and #{endDate}) </if>" +
            "<if test='startDate != null and startDate != \"\" and (endDate == null or endDate == \"\")'> and (releasetime between #{startDate} and now()) </if>";
    final String demandStatus =
            "<if test='checkstatus!=null'> and checkstatus=#{checkstatus}</if>";
     //后台page分页
    //待审核
    @Select("<script>select count(*) from demands d where releasestatus=1 and isdelete=0"
            + searchCondition + demandStatus +"</script>")
    public int countAllDemandsBySelect(@Param("deliveryregion")int deliveryregion,
                                       @Param("deliveryprovince")int deliveryprovince,
                                       @Param("deliveryplace")int deliveryplace,
                                       @Param("content")String content,
                                       @Param("clienttype")int clienttype,
                                       @Param("startDate")String startDate,
                                       @Param("endDate")String endDate,
                                       @Param("checkstatus")String checkstatus);

    @Select("<script>" +
            " select d.*, a.name as tradername_temp, a.id as traderid_temp, a.phone as traderphone_temp from demands d" +
            " left join users u on u.id = d.userid" +
            " left join admins a on u.traderid = a.id" +
            " where releasestatus=1 and isdelete=0 "
            + searchCondition + demandStatus + " order by releasetime desc " +
            "limit #{limit} offset #{offset}" +
            "</script>")
    public List<Map<String, Object>> listAllDemandsBySelect(@Param("deliveryregion")int deliveryregion,
                                                            @Param("deliveryprovince")int deliveryprovince,
                                                            @Param("deliveryplace")int deliveryplace,
                                                            @Param("content")String content,
                                                            @Param("clienttype")int clienttype,
                                                            @Param("startDate")String startDate,
                                                            @Param("endDate")String endDate,
                                                            @Param("checkstatus")String checkstatus,
                                                            @Param("limit") int limit,
                                                            @Param("offset") int offset);

    public default Pager<Map<String, Object>> pageAllDemandsBySelect(int deliveryregion, int deliveryprovince, int deliveryplace, String content, int clienttype, String startDate, String endDate, String checkstatus, int page, int pagesize){
        return Pager.config(this.countAllDemandsBySelect(deliveryregion, deliveryprovince, deliveryplace, content, clienttype, startDate, endDate, checkstatus),
                (int limit, int offset) -> this.listAllDemandsBySelect(deliveryregion, deliveryprovince, deliveryplace, content, clienttype, startDate, endDate, checkstatus, limit, offset))
                .page(page, pagesize);
    }


    @Select("<script>" +
            " select d.*, u.securephone from demands d inner join users u on d.userid = u.id where releasestatus=1 and isdelete=0 "
            + searchCondition + demandStatus + " order by releasetime desc " +
            "</script>")
    public List<Map<String, Object>> getExportDemandList(@Param("deliveryregion")int deliveryregion,
                                                         @Param("deliveryprovince")int deliveryprovince,
                                                         @Param("deliveryplace")int deliveryplace,
                                                         @Param("content")String content,
                                                         @Param("clienttype")int clienttype,
                                                         @Param("startDate")String startDate,
                                                         @Param("endDate")String endDate,
                                                         @Param("checkstatus")String checkstatus);

    //后台--我的客户--需求列表显示
    static final  String showAllDynamicSql =
            "<where>"+
                    "<if test='demand!=null'>" +
                    "<if test='demand.checkstatus!=null and demand.checkstatus!=\"\"'> checkstatus=#{demand.checkstatus}</if>" +
                    "<if test='demand.tradestatus!=null and demand.tradestatus!=\"\"'> and tradestatus=#{demand.tradestatus}</if>"+
                    "<if test='demand.demandcode!=null and demand.demandcode!=\"\"'> and demandcode=#{demand.demandcode}</if>"+
                    "<if test='demand.coaltype!=null and demand.coaltype!=\"\"'> and coaltype=#{demand.coaltype}</if>" +
                    "<if test='demand.deliverymode!=null and demand.deliverymode!=\"\"'> and deliverymode=#{demand.deliverymode}</if>" +
                    "<if test='demand.deliverydistrict!=null and demand.deliverydistrict!=\"\"'> and deliverydistrict=#{demand.deliverydistrict}</if>" +
                    "<if test='demand.deliveryprovince!=null and demand.deliveryprovince!=\"\" ' > and deliveryprovince=#{demand.deliveryprovince}</if>" +
                    "<if test='demand.deliveryplace!=null and demand.deliveryplace!=\"\"'> and deliveryplace=#{demand.deliveryplace}</if>" +
                    "<if test='demand.NCV!=null'> and NCV=#{demand.NCV}</if>" +
                    "<if test='demand.RS!=null'> and RS=#{demand.RS}</if>" +
                    "<if test='demand.ADV!=null'> and ADV=#{demand.ADV}</if>" +
                    "<if test='demand.TM!=null'> and TM=#{demand.TM}</if>" +
                    "<if test='demand.isdelete!=null'> and isdelete=#{demand.isdelete}</if>" +
                    "<if test='demand.isdelete==null'> and isdelete=0</if>"+
                    "</if>" +
                    " and releasestatus=1 " +
                    //"<if test='currentUser.isAdmin!=true'> and d.tradercode=#{currentUser.jobnum}</if>" +
                    "</where>";
    String demandOrderBy =
            "<if test='sortType==\"demandcode\"'>" +
                    "<if test='!sortOrder'>order by demandcode desc</if>" +
                    "<if test='sortOrder'>order by demandcode asc</if>" +
                    "</if>" +
                    "<if test='sortType==\"releasetime\"'>" +
                    "<if test='!sortOrder'>order by releasetime desc</if>" +
                    "<if test='sortOrder'>order by releasetime asc</if>" +
                    "</if>" +
                    "<if test='sortType==\"checktime\"'>" +
                    "<if test='!sortOrder'>order by checktime desc</if>" +
                    "<if test='sortOrder'>order by checktime asc</if>" +
                    "</if>";

    @Select("<script>select * from demands " + showAllDynamicSql + demandOrderBy + " limit #{limit} offset #{offset} </script>")
    public List<Demand> showAllByDealerId(@Param("demand") Demand demand,
                                          @Param("sortType")String sortType,
                                          @Param("sortOrder")boolean sortOrder,
                                          @Param("limit")int limit,
                                          @Param("offset")int offset);


    @Select("<script>select count(id) from demands " + showAllDynamicSql + "</script>")
    public int countAllByDealerId(@Param("demand") Demand demand);

    public default Pager<Demand> getAllDemandByStatus(Demand demand, String sortType, boolean sortOrder, int page, int pagesize){
        return Pager.config(this.countAllByDealerId(demand), (int limit, int offset) -> this.showAllByDealerId(demand, sortType, sortOrder, limit, offset))
                .page(page, pagesize);
    }

//    @Select("<script>select * from demands d "+showAllDynamicSql+" order by demandcode limit #{limit} offset #{offset} </script>")
//    public List<Demand> showAllList(@Param("demand") Demand demand, @Param("limit")int limit,@Param("offset")int offset);
//
//
//    @Select("<script>select count(1) from demands d "+showAllDynamicSql+"</script>")
//    public int showAllListCount(@Param("demand") Demand demand);
//
//    public default Pager<Demand> getAllDemandList(Demand demand,int page, int pagesize){
//        return Pager.config(this.showAllListCount(demand), (int limit, int offset) -> this.showAllList( demand,limit,offset))
//                .page(page, pagesize);
//    }

    //需求表匹配量累加
    @Update("update demands set purchasednum=#{supplyton}+purchasednum where demandcode=#{demandcode}")
    public void modifyPurchaseNumByDemandcode(@Param("supplyton") int supplyton,@Param("demandcode") String demandcode);

    //首页数据统计，查询所有审核通过的需求
    @Select("select * from demands where checkstatus='审核通过' and isdelete=0")
    public List<Demand> getAllPassDemandList();

    //统计审核通过的需求 -- 动力煤
    @Select("select IFNULL(SUM(demandamount),0) from demands where checkstatus='审核通过' and isdelete=0 and coaltype='动力煤' and tradestatus='开始报价' ")
    public long getSumPowerPassDemand();

    //统计审核通过的需求 -- 无烟煤
    @Select("select IFNULL(SUM(demandamount),0) from demands where checkstatus='审核通过' and isdelete=0 and coaltype='无烟煤' and tradestatus='开始报价' ")
    public long getSumAnthracitePassDemand();

    //统计审核通过的需求 -- 喷吹煤
    @Select("select IFNULL(SUM(demandamount),0) from demands where checkstatus='审核通过' and isdelete=0 and coaltype='喷吹煤' and tradestatus='开始报价' ")
    public long getSumCoalInjectionPassDemand();

    //统计审核通过的需求 -- 焦煤
    @Select("select IFNULL(SUM(demandamount),0) from demands where checkstatus='审核通过' and isdelete=0 and coaltype='焦煤' and tradestatus='开始报价' ")
    public long getSumCokingCoalPassDemand();

    //通过dealerid查询需求信息
    @Select("select * from demands where tradercode=#{tradercode}")
    public List<Demand> getDemandListByDealerid(String tradercode);

    //通过region， province, harbour
    @Select("select * from demands where deliverydistrict=#{deliverydistrict} " +
            "and deliveryprovince=#{deliveryprovince} and deliveryplace=#{deliveryplace}")
    public List<Demand> getDemandListByRegionProvinceHarbour(@Param("deliverydistrict")String deliverydistrict,
                                                             @Param("deliveryprovince")String deliveryprovince,
                                                             @Param("deliveryplace")String deliveryplace);

    @Update("update demands set tradercode=#{tradercode}, tradername=#{tradername}, traderphone=#{traderphone} where id=#{id}")
    public void updateDemandDealer(@Param("tradercode")String tradercode,
                                   @Param("tradername")String tradername,
                                   @Param("traderphone")String traderphone,
                                   @Param("id")int id);

    @Update("update demands set traderphone=#{traderphone} where id=#{id}")
    public void updateDemandDealerPhone(@Param("traderphone")String traderphone,
                                        @Param("id")int id);

    @Update("update demands set viewtimes=viewtimes+1 where id=#{id}")
    public void setPageViewTimesById(int id);

    @Update("update demands set traderphone=#{traderphone} where traderid=#{traderid}")
    public void updateDealerPhone(@Param("traderphone")String traderphone,
                                        @Param("traderid")int dealerid);

    //通过交易员id修改交易员信息
    @Update("update demands set  tradername = #{newDealer.name}, traderphone=#{newDealer.phone},traderid=#{newDealer.id} where checkstatus='审核通过' and traderid=#{oldId}")
    public void updateDemandDealerByDealerId(@Param("newDealer")Admin admin,
                                             @Param("oldId")int oldId);


    //根据需求id查询需求对象
    @Select("select * from demands where id=#{demandId} and userid=#{userId} and isdelete=0")
    public Demand getDemandByIdAndUserId(@Param("demandId")int demandId,@Param("userId")int userId);

    final String dynamicWeixin="<where>" +
              "<if test='provinceId!=null'> and provinceId=#{provinceId}</if>" +
              "<if test='portId!=null'> and portId=#{portId}</if>" +
              "<if test='coalType!=null and coalType!=\"\"'> and coaltype=#{coalType}</if>" +
              "<if test='lowNCV!=null and highNCV!=null'> and ncv between #{lowNCV} and #{highNCV}</if>" +
              "<if test='lowRS!=null  and highRS!=null'> and rs between #{lowRS} and #{highRS}</if>"+
                " and checkstatus='审核通过' and isdelete=0 "+
            "</where>";
    @Select("<script>select count(1) from demands "+dynamicWeixin+ "</script>")
    public  int demandCount(@Param("provinceId") Integer provinceId,
                            @Param("portId") Integer portId,
                            @Param("lowNCV") Integer lowNCV,
                            @Param("highNCV") Integer highNCV,
                            @Param("lowRS") BigDecimal lowRS,
                            @Param("highRS") BigDecimal highRS,
                            @Param("coalType") String coalType);

    //微信端查询需求列表
    @Select("<script>select * from demands "+dynamicWeixin+ " order by case tradestatus  when '未开始报价' then  1  when '开始报价'  then 2 when '匹配中' then  3  when  '已中标' then 4  when  '报价结束' then 5 when '已过期' then 6   end , releasetime desc  <choose><when test='anchor==null or anchor==\"\"'>limit #{pageQuery.pagesize} offset #{pageQuery.indexNum}</when><otherwise>limit ${pageQuery.rowNum}  offset 0</otherwise></choose> </script>")
    public List<Demand> demandList(@Param("pageQuery") PageQueryParam param,
                                   @Param("provinceId") Integer provinceId,
                                   @Param("portId") Integer portId,
                                   @Param("lowNCV") Integer lowNCV,
                                   @Param("highNCV") Integer highNCV,
                                   @Param("lowRS")  BigDecimal lowRS,
                                   @Param("highRS") BigDecimal highRS,
                                   @Param("coalType") String coalType,
                                   @Param("anchor")String anchor);

    //修改需求状态
    @Update("update demands set releasestatus=1,checkstatus='待审核' where demandcode=#{demandcode}")
    public void updateDemandAvailable(@Param("demandcode")String demandcode);







    //
    @Update("update demands set tradestatus=#{status} where demandcode=#{demandcode}")
    public void modifyStatusByDemandCode(@Param("status") String status,@Param("demandcode") String demandcode);


    /**
     * 需求专区的显示数据
     */
    @Select("select * from demands where checkstatus='审核通过' and isdelete = 0  order by releasetime desc limit 5")
    public List<Demand> getRecommendDemandList();

    @Select("select count(*) from  demands where checkstatus='审核通过' ")
    public long getTotalDemandRecords();

    @Select("select count(*) from quotes where status ='已中标' ")
    public long getSaleRecords();

    @Select("select IFNULL(sum(supplyton),0) from quotes where status ='已中标' ")
    public long getTotalTrading();

    @Select("select b.securephone,c.deliveryplace,c.otherplace,c.coaltype,a.supplyton," +
            "c.NCV,a.quote,a.lastupdatetime " +
            "from quotes as a, users as b , demands as c " +
            "where b.id = a.`userid` and a.status='已中标' and c.demandcode = a.demandcode " +
            "order by a.lastupdatetime desc")
    public List<DealDemand> getListDealDemand();


    /**
     * 按照需求编号，店铺id 查询需求信息
     * @param pid
     * @param shopid
     * @return
     */
    @Select("<script>" +
            "select count(1) from demands where demandcode like #{demandcode}" +
            "<if test='shopid != 0'> and shopid=#{shopid} </if>" +
            "and checkstatus='审核通过' and isdelete=0 " +
            "</script>")
    int getDemandCountByPid(@Param("demandcode")String pid,
                            @Param("shopid")int shopid);


    @Select("<script>" +
            "select * from demands where demandcode like #{demandcode}" +
            "<if test='shopid != 0'> and shopid=#{shopid} </if>" +
            "and checkstatus='审核通过' and isdelete=0 order by releasetime desc " +
            "limit #{pagesize} offset #{offset}" +
            "</script>")
    List<Demand> getDemandListByPid(@Param("demandcode")String pid,
                                    @Param("shopid")int shopid,
                                    @Param("pagesize")int pagesize,
                                    @Param("offset")int offset);

    /**
     * 首页我的需求_新进展
     */
    @Select("select count(1) from quotes q left join mydemands m on q.demandcode = m.demandcode where m.status in ('报价中', '匹配中') and m.userid = #{userId}")
    int getPersonalLatestChangeDemand(@Param("userId") int userId);

    /**
     * 今日凌晨过期的需求信息
     */
    @Select("select * from demands where tradestatus='已过期' and IFNULL(deliverydate, deliverydateend) = date_sub(CURDATE(), interval 1 day)")
    List<Demand> getDemandYestdayOutOfDateList();

}




