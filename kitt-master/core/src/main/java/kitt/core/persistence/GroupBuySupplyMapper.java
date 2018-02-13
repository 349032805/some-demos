package kitt.core.persistence;

import kitt.core.domain.GroupBuySupply;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by zhangbolun on 15/1/23.
 */
public interface GroupBuySupplyMapper {
    @Insert("<script> insert into groupbuysupply(" +
            "<if test='succeedpercentmin!=null'>succeedpercentmin,</if>" +
            "<if test='succeedpercentmax!=null'>succeedpercentmax,</if>" +
            "<if test='GV!=null'>GV,</if>" +
            "<if test='YV!=null'>YV,</if>" +
            "<if test='FC!=null'>FC,</if>" +
            "<if test='PS!=null'>PS,</if>" +
            "<if test='PSName!=null'>PSName,</if>" +
            "<if test='NCV!=null'>NCV,</if>" +
            "<if test='NCV02!=null'>NCV02,</if>" +
            "<if test='ADS02!=null'>ADS02,</if>" +
            "<if test='RS02!=null'>RS02,</if>" +
            "<if test='TM02!=null'>TM02,</if>" +
            "<if test='IM02!=null'>IM02,</if>" +
            "<if test='ADV02!=null'>ADV02,</if>" +
            "<if test='RV02!=null'>RV02,</if>" +
            "<if test='ASH02!=null'>ASH02,</if>" +
            "<if test='GV02!=null'>GV02,</if>" +
            "<if test='YV02!=null'>YV02,</if>" +
            "<if test='FC02!=null'>FC02,</if>" +
            "<if test='ADS!=null'>ADS,</if>" +
            "<if test='IM!=null'>IM,</if>" +
            "<if test='RV!=null'>RV,</if>" +
            "<if test='ASH!=null'>ASH,</if>" +
            "<if test='AFT!=null'>AFT,</if>" +
            "<if test='HGI!=null'>HGI,</if>" +
            "photopath,groupbuyenddate, groupbuysupplycode, createtime," +
            " RS, TM, ADV, providerinfoid, coaltype, port, groupbuybegindate, " +
            "marketprice, groupbuyprice, deliveryplace, storageplace, deliverytime, deliverymode, supplyamount," +
            " selledamount, inspectionagency, surplusamount, minimumamount, isdelete, status, comment, " +
            "deliverydistrict, deliveryprovince, groupbuyordercount, deliverydatestart, deliverydateend, " +
            "otherorg, originplace, traderid, tradername, traderphone, regionId, provinceId, portId) values(" +
            "<if test='succeedpercentmin!=null'>#{succeedpercentmin},</if>" +
            "<if test='succeedpercentmax!=null'>#{succeedpercentmax},</if>" +
            "<if test='GV!=null'>#{GV},</if>" +
            "<if test='YV!=null'>#{YV},</if>" +
            "<if test='FC!=null'>#{FC},</if>" +
            "<if test='PS!=null'>#{PS},</if>" +
            "<if test='PSName!=null'>#{PSName},</if>" +
            "<if test='NCV!=null'>#{NCV},</if>" +
            "<if test='NCV02!=null'>#{NCV02},</if>" +
            "<if test='ADS02!=null'>#{ADS02},</if>" +
            "<if test='RS02!=null'>#{RS02},</if>" +
            "<if test='TM02!=null'>#{TM02},</if>" +
            "<if test='IM02!=null'>#{IM02},</if>" +
            "<if test='ADV02!=null'>#{ADV02},</if>" +
            "<if test='RV02!=null'>#{RV02},</if>" +
            "<if test='ASH02!=null'>#{ASH02},</if>" +
            "<if test='GV02!=null'>#{GV02},</if>" +
            "<if test='YV02!=null'>#{YV02},</if>" +
            "<if test='FC02!=null'>#{FC02},</if>" +
            "<if test='ADS!=null'>#{ADS},</if>" +
            "<if test='IM!=null'>#{IM},</if>" +
            "<if test='RV!=null'>#{RV},</if>" +
            "<if test='ASH!=null'>#{ASH},</if>" +
            "<if test='AFT!=null'>#{AFT},</if>" +
            "<if test='HGI!=null'>#{HGI},</if>" +
            "#{photopath},#{groupbuyenddate}, dateseq_next_value('TG'), #{createtime}," +
            "#{RS},#{TM},#{ADV},#{providerinfoid},#{coaltype},#{port}," +
            "#{groupbuybegindate},#{marketprice},#{groupbuyprice},#{deliveryplace},#{storageplace}," +
            "#{deliverytime},#{deliverymode},#{supplyamount},#{selledamount},#{inspectionagency}," +
            "#{surplusamount},#{minimumamount}, false,  #{status},#{comment},#{deliverydistrict}," +
            "#{deliveryprovince},#{groupbuyordercount},#{deliverydatestart},#{deliverydateend}," +
            "#{otherorg}, #{originplace}, #{traderid}, #{tradername}, #{traderphone}, #{regionId}, #{provinceId}," +
            "#{portId} ) </script>")
    @Options(useGeneratedKeys=true)
    public void addGroupBuySupply(GroupBuySupply groupBuySupply);

    @Update("<script>update groupbuysupply set <if test='succeedpercentmin!=null'> succeedpercentmin=#{succeedpercentmin},</if> <if test='succeedpercentmax!=null'>succeedpercentmax=#{succeedpercentmax},</if>photopath=#{photopath}, groupbuyenddate=#{groupbuyenddate}, " +
            "deliverydistrict=#{deliverydistrict}, deliveryprovince=#{deliveryprovince}, " +
            "groupbuyordercount=#{groupbuyordercount}, comment=#{comment}, status=#{status}, createtime=#{createtime}," +
            " NCV=#{NCV},ADS=#{ADS},RS=#{RS},TM=#{TM},IM=#{IM},ADV=#{ADV},RV=#{RV},ASH=#{ASH},AFT=#{AFT},HGI=#{HGI},"+
            "deliverydatestart=#{deliverydatestart}, deliverydateend=#{deliverydateend},otherorg=#{otherorg}," +
            " providerinfoid=#{providerinfoid},coaltype=#{coaltype},port=#{port},groupbuybegindate=#{groupbuybegindate}," +
            "marketprice=#{marketprice}, groupbuyprice=#{groupbuyprice},deliveryplace=#{deliveryplace},storageplace=#{storageplace}," +
            "deliverytime=#{deliverytime},deliverymode=#{deliverymode},supplyamount=#{supplyamount},"+
            "selledamount=#{selledamount},inspectionagency=#{inspectionagency},surplusamount=#{surplusamount}," +
            "minimumamount=#{minimumamount}, originplace=#{originplace}, traderid=#{traderid}, tradername=#{tradername}," +
            "traderphone=#{traderphone}, regionId=#{regionId}, provinceId=#{provinceId}, portId=#{portId} " +
            " where id=#{id} and isdelete=false </script>")
    public void updateGroupBuySupply(GroupBuySupply groupBuySupply);

    @Update("update groupbuysupply set status=#{status} where id=#{id} and isdelete=false")
    public void updateStatusById(@Param("id")int id, @Param("status")String status);

    @Update("update groupbuysupply set groupbuyordercount=#{groupbuyordercount} where id=#{id} and isdelete=false")
    public void updateOrderCount(@Param("groupbuyordercount")int groupbuyordercount, @Param("id")int id);

    @Update("update groupbuysupply set isdelete=true where id=#{id} and isdelete=false ")
    public void deleteGroupBuySupplyById(@Param("id")int id);

    @Select("select * from groupbuysupply where id=#{groupbuyid} and isdelete=0")
    public GroupBuySupply getGroupBuySupplyById(@Param("groupbuyid")int groupbuyid);

    @Update("update groupbuysupply set viewtimes = viewtimes+1 where id=#{groupbuyid}")
    public void setPageViewTimesById(@Param("groupbuyid")int groupbuyid);

    @Select("select * from groupbuysupply where groupbuysupplycode=#{groupbuysupplycode} and isdelete=false")
    public GroupBuySupply getGroupBuySupplyByCode(String groupbuysupplycode);

    @Select("select * from groupbuysupply where providerinfoid=#{providerinfoid} and isdelete=false")
    public List<GroupBuySupply> getGroupBuySuppliesByProviderinfoId(@Param("providerinfoid")int providerinfoid);

    @Select("select * from groupbuysupply where isdelete=false")
    public List<GroupBuySupply> getAllGroupBuySupplies();

    @Select("select * from groupbuysupply where status=#{status} and isdelete=false")
    public List<GroupBuySupply> getGroupBuySuppliesByStatus(@Param("status")String status);

    @Select("select count(1) from groupbuysupply where isdelete=false and status='GROUP_BUY_SUPPLY_RELEASE' or status ='GROUP_BUY_SUPPLY_INPROGRESS' or status='GROUP_BUY_SUPPLY_FINISH'")
    public int getGroupBuySupplyCount();

    @Select("select * from groupbuysupply where isdelete=false and status='GROUP_BUY_SUPPLY_RELEASE' or status ='GROUP_BUY_SUPPLY_INPROGRESS' or status='GROUP_BUY_SUPPLY_FINISH' ORDER BY createtime DESC limit #{limit} offset #{offset}")
    public List<GroupBuySupply> getRemindGroupBuySupplyList(@Param("limit")int limit,
                                                            @Param("offset")int offset);

    @Select("select * from groupbuysupply where isdelete=false and status='GROUP_BUY_SUPPLY_RELEASE' or status ='GROUP_BUY_SUPPLY_INPROGRESS' or status='GROUP_BUY_SUPPLY_FINISH' ORDER BY createtime DESC")
    public List<GroupBuySupply> getRemindGroupBuySupplies();

    //分页查询
    @Select("select count(*) from groupbuysupply where providerinfoid=#{providerinfoid} and isdelete=0")
    public int countAllGroupBuy(int providerinfoid);
    @Select("select count(*) from groupbuysupply where groupbuyenddate > now() and providerinfoid=#{providerinfoid} and isdelete=0")
    public int countInProcessGroupBuy(int providerinfoid);
    @Select("select count(*) from groupbuysupply where groupbuyenddate < now() and providerinfoid=#{providerinfoid} and isdelete=0")
    public int countFinishGroupBuy(int providerinfoid);

    @Select("select * from groupbuysupply where providerinfoid=#{providerinfoid} and isdelete=0  limit #{limit} offset #{offset}")
    public List<GroupBuySupply> listAllGroupBuy(@Param("providerinfoid") int providerinfoid,@Param("limit") int limit, @Param("offset") int offset);
    @Select("select * from groupbuysupply where groupbuyenddate > now() and providerinfoid=#{providerinfoid} and isdelete=0  limit #{limit} offset #{offset}")
    public List<GroupBuySupply> listInProcessGroupBuy(@Param("providerinfoid") int providerinfoid,@Param("limit") int limit, @Param("offset") int offset);
    @Select("select * from groupbuysupply where groupbuyenddate < now() and providerinfoid=#{providerinfoid} and isdelete=0  limit #{limit} offset #{offset}")
    public List<GroupBuySupply> listFinishGroupBuy(@Param("providerinfoid") int providerinfoid,@Param("limit") int limit, @Param("offset") int offset);

    public default Pager<GroupBuySupply> pageAllGroupBuy(int providerinfoid,int page, int pagesize){
        return Pager.config(this.countAllGroupBuy(providerinfoid), (int limit, int offset) -> this.listAllGroupBuy(providerinfoid,limit, offset))
                .page(page, pagesize);
    }
    public default Pager<GroupBuySupply> pageInProcessGroupBuy(int providerinfoid,int page, int pagesize){
        return Pager.config(this.countInProcessGroupBuy(providerinfoid), (int limit, int offset) -> this.listInProcessGroupBuy(providerinfoid, limit, offset))
                .page(page, pagesize);
    }
    public default Pager<GroupBuySupply> pageFinishGroupBuy(int providerinfoid,int page, int pagesize){
        return Pager.config(this.countFinishGroupBuy(providerinfoid), (int limit, int offset) -> this.listFinishGroupBuy(providerinfoid,limit, offset))
                .page(page, pagesize);
    }

    @Select("select * from groupbuysupply where groupbuysupplycode=#{supplyCode}")
    public GroupBuySupply loadBygroupBycode(@Param("supplyCode")String supplyCode);

}
