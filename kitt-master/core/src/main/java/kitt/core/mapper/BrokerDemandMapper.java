package kitt.core.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import kitt.core.entity.Demand;

/**
 * Created by liuxinjie on 16/2/29.
 */
public interface BrokerDemandMapper {

    /**
     * 通过id查询demand
     */
    @Select("select * from demands where id=#{id}")
    Demand getDemandById(int id);

    /**
     * 通过demandcode查询demand
     */
    @Select("select * from demands where demandcode=#{demandcode}")
    Demand getDemandByDemandcode(String demandcode);

    /**
     * 添加需求
     */
    @Insert("<script>" +
            "insert into demands(userid,demandcode,demandcustomer,coaltype," +
            "NCV,NCV02, ADS,ADS02, RS,RS02, TM,TM02, IM,IM02, ADV,ADV02, RV, AFT, ASH,ASH02, HGI,GV,YV,FC,PS,PSName,RV02,FC02,GV02,YV02," +
            "deliverydistrict,deliveryprovince,deliveryplace,otherplace,demandamount," +
            "deliverydate,deliverydatestart,deliverydateend,deliverymode,residualdemand,inspectionagency," +
            "otherorg,quoteenddate,noshowdate,releasetime,checkstatus,tradestatus," +
            "releasestatus,regionId,provinceId,portId,releasecomment, clienttype, paymode,transportmode," +
            " showYM, sourceId, logistics, finance, promotion, infoSource) " +
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
            "#{regionId},#{provinceId},#{portId},#{releasecomment}, #{clienttype},#{paymode},#{transportmode}," +
            "#{showYM}, #{sourceId}, #{logistics}, #{finance}, #{promotion}, #{infoSource})" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addDemand(Demand demand);


    /**
     * 更改需求
     */
    @Update("<script>update demands set coaltype=#{coaltype}, demandcustomer=#{demandcustomer}," +
            "<if test='RS!=null'>RS=#{RS},</if> <if test='RS==null'>RS=0,</if>"+
            "<if test='RS02!=null'>RS02=#{RS02},</if> <if test='RS02==null'>RS02=0,</if>"+
            "<if test='TM!=null'>TM=#{TM},</if> <if test='TM==null'>TM=0,</if>"+
            "<if test='TM02!=null'>TM02=#{TM02},</if> <if test='TM02==null'>TM02=0,</if>"+
            "<if test='NCV!=null'>NCV=#{NCV},</if> <if test='NCV==null'>NCV=0,</if>"+
            "<if test='NCV02!=null'>NCV02=#{NCV02},</if> <if test='NCV02==null'>NCV02=0,</if>"+
            "<if test='ADS!=null'>ADS=#{ADS},</if> <if test='ADS==null'>ADS=0,</if>"+
            "<if test='ADS02!=null'>ADS02=#{ADS02},</if> <if test='ADS02==null'>ADS02=0,</if>"+
            "<if test='IM!=null'>IM=#{IM},</if> <if test='IM==null'>IM=0,</if>" +
            "<if test='IM02!=null'>IM02=#{IM02},</if> <if test='IM02==null'>IM02=0,</if>" +
            "<if test='RV!=null'>RV=#{RV},</if> <if test='RV==null'>RV=0,</if>" +
            "<if test='AFT!=null'>AFT=#{AFT},</if> <if test='AFT==null'>AFT=0,</if>" +
            "<if test='ASH!=null'>ASH=#{ASH},</if> <if test='ASH==null'>ASH=0,</if>" +
            "<if test='ASH02!=null'>ASH02=#{ASH02},</if> <if test='ASH02==null'>ASH02=0,</if>" +
            "<if test='HGI!=null'>HGI=#{HGI},</if> <if test='HGI==null'>HGI=0,</if>" +
            "<if test='GV!=null'>GV=#{GV},</if> <if test='GV==null'>GV=0,</if> " +
            "<if test='YV!=null'>YV=#{YV},</if> <if test='YV==null'>YV=0,</if>" +
            "<if test='FC!=null'>FC=#{FC},</if> <if test='FC==null'>FC=0,</if>" +
            "<if test='PS!=null'>PS=#{PS},</if> <if test='PS==null'>PS=0,</if> " +
            "<if test='ADV02!=null'>ADV02=#{ADV02},</if> <if test='ADV02==null'>ADV02=0,</if>" +
            "<if test='RV02!=null'>RV02=#{RV02},</if> <if test='RV02==null'>RV02=0,</if> " +
            "<if test='FC02!=null'>FC02=#{FC02},</if> <if test='FC02==null'>FC02=0,</if> " +
            "<if test='GV02!=null'>GV02=#{GV02},</if> <if test='GV02==null'>GV02=0,</if> " +
            "<if test='YV02!=null'>YV02=#{YV02},</if> <if test='YV02==null'>YV02=0,</if>" +
            "ADV=#{ADV}," +
            "<if test='checkstatus!=null'>checkstatus=#{checkstatus},</if>" +
            "deliverydistrict=#{deliverydistrict}," +
            "deliveryprovince=#{deliveryprovince},deliveryplace=#{deliveryplace},otherplace=#{otherplace}," +
            "demandamount=#{demandamount}, deliverydate=#{deliverydate},deliverydatestart=#{deliverydatestart}," +
            "deliverydateend=#{deliverydateend}, deliverymode=#{deliverymode},residualdemand=#{residualdemand}," +
            "inspectionagency=#{inspectionagency}, otherorg=#{otherorg},quoteenddate=#{quoteenddate},noshowdate=#{noshowdate}," +
            "releasetime=now(), regionId=#{regionId},provinceId=#{provinceId},portId=#{portId}," +
            "<if test='PSName!=null'>PSName=#{PSName},</if> " +
            "<if test='paymode!=null'>paymode=#{paymode},</if> " +
            "<if test='transportmode!=null'>transportmode=#{transportmode},</if>" +
            "<if test='comment!=null'>comment=#{comment},</if> " +
            "<if test='releasecomment!=null'>releasecomment=#{releasecomment},</if>" +
            " showYM=#{showYM}, sourceId=#{sourceId}, logistics=#{logistics}, finance=#{finance}, promotion=#{promotion}," +
            " infoSource=#{infoSource} " +
            " where demandcode=#{demandcode}</script>")
    public void modifyDemand(Demand demand);


    //更新发布状态
    @Update("update demands set releasestatus=1 where id=#{id}")
    public void modifyReleaseStatusByDemandId(int id);


}
