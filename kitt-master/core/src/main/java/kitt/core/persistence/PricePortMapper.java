package kitt.core.persistence;

import kitt.core.domain.MapObject;
import kitt.core.domain.PricePort;
import kitt.core.domain.PricePortValue;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 16/1/21.
 */
public interface PricePortMapper {

    /**
     * 港口价格List
     */
    @Select("select count(1) from priceport where isdelete=0 ")
    public int getPricePortCount();

    @Select("select * from priceport where isdelete=0 order by isshow desc, sequence, id desc limit #{limit} offset #{offset}")
    public List<PricePort> getPricePortList(@Param("limit")int limit,
                                            @Param("offset")int offset);

    public default Pager<PricePort> pagePricePort(int page, int pagesize){
        return Pager.config(this.getPricePortCount(), (int limit, int offset) -> this.getPricePortList(limit, offset))
                .page(page, pagesize);
    }

    /**
     * 添加港口价格
     */
    @Insert("<script>" +
            " insert into priceport(region, name, " +
            " <if test='ASH!=null and ASH!=\"\"'>ASH,</if>" +
            " <if test='ASH02!=null and ASH02!=\"\"'>ASH02,</if>" +
            " <if test='RV!=null and RV!=\"\"'>RV,</if>" +
            " <if test='RV02!=null and RV02!=\"\"'>RV02,</if>" +
            " <if test='RS!=null and RS!=\"\"'>RS,</if>" +
            " <if test='RS02!=null and RS02!=\"\"'>RS02,</if>" +
            " <if test='TM!=null and TM!=\"\"'>TM,</if>" +
            " <if test='TM02!=null and TM02!=\"\"'>TM02,</if>" +
            " <if test='bondindex!=null and bondindex!=\"\"'>bondindex,</if>" +
            " <if test='bondindex02!=null and bondindex02!=\"\"'>bondindex02,</if>" +
            " <if test='NCV!=null and NCV!=\"\"'>NCV,</if>" +
            " <if test='NCV02!=null and NCV02!=\"\"'>NCV02,</if>" +
            " pricetype, team, placedispatch, placereceipt, createtime, lastupdatetime," +
            " lastupdatemanid, lastupdatemanusername, lastupdatemanname, unit) values(#{region}, #{name}, " +
            " <if test='ASH!=null and ASH!=\"\"'>#{ASH},</if>" +
            " <if test='ASH02!=null and ASH02!=\"\"'>#{ASH02},</if>" +
            " <if test='RV!=null and RV!=\"\"'>#{RV},</if>" +
            " <if test='RV02!=null and RV02!=\"\"'>#{RV02},</if>" +
            " <if test='RS!=null and RS02!=\"\"'>#{RS},</if>" +
            " <if test='RS02!=null and RS02!=\"\"'>#{RS02},</if>" +
            " <if test='TM!=null and TM!=\"\"'>#{TM},</if>" +
            " <if test='TM02!=null and TM02!=\"\"'>#{TM02},</if>" +
            " <if test='bondindex!=null and bondindex!=\"\"'>#{bondindex},</if>" +
            " <if test='bondindex02!=null and bondindex02!=\"\"'>#{bondindex02},</if>" +
            " <if test='NCV!=null and NCV!=\"\"'>#{NCV},</if>" +
            " <if test='NCV02!=null and NCV02!=\"\"'>#{NCV02},</if>" +
            " #{pricetype}, #{team}, #{placedispatch}, #{placereceipt}, now(), now(), #{lastupdatemanid}, " +
            " #{lastupdatemanusername}, #{lastupdatemanname}, #{unit})" +
            "</script>")
    @Options(useGeneratedKeys = true)
    int addPricePort(PricePort pricePort);

    /**
     * 添加港口价格数值
     */
    @Insert("<script>" +
            " insert into priceportvalue(priceportid, " +
            " <if test='value!=null and value!=\"\"'>value, </if>" +
            " <if test='value02!=null and value02!=\"\"'>value02, </if>" +
            " date, createtime, lastupdatetime, lastupdatemanid, lastupdatemanusername, lastupdatemanname) " +
            " values(#{priceportid}, " +
            " <if test='value!=null and value!=\"\"'>#{value}, </if>" +
            " <if test='value02!=null and value02!=\"\"'>#{value02}, </if>" +
            " #{date}, now(), now(), #{lastupdatemanid}, #{lastupdatemanusername}, #{lastupdatemanname}) " +
            "</script>")
    @Options(useGeneratedKeys = true)
    int addPricePortValue(PricePortValue pricePortValue);

    /**
     * 添加港口价格数值 List 方式
     */
    @Insert("<script>" +
            " insert into priceportvalue(priceportid, " +
            " value, value02, " +
            " date, createtime, lastupdatetime, lastupdatemanid, lastupdatemanusername, lastupdatemanname) values " +
            " <foreach collection=\"pricePortValueList\" index=\"index\" item=\"item\" separator=\",\" >" +
            " (#{item.priceportid}, " +
            " <if test='item.value!=null and item.value02!=\"\"'>#{item.value}, </if>" +
            " <if test='item.value==null or item.value02==\"\"'> '', </if>" +
            " <if test='item.value02!=null and item.value02!=\"\"'>#{item.value02}, </if>" +
            " <if test='item.value02==null or item.value02==\"\"'>'', </if>" +
            " #{item.date}, now(), now(), #{item.lastupdatemanid}, " +
            " #{item.lastupdatemanusername}, #{item.lastupdatemanname} )" +
            " </foreach> " +
            "</script>")
    int addPricePortValueList(@Param("pricePortValueList") List<PricePortValue> pricePortValueList);

    /**
     * 查询港口价格是否存在
     */
    @Select("<script>" +
            " select * from priceport where region=#{region} and name=#{name} and pricetype=#{pricetype} " +
            " <if test='NCV==null'> and NCV is NULL </if>" +
            " <if test='NCV!=null'> and NCV=#{NCV} </if>" +
            " <if test='NCV02==null'> and NCV02 is NULL </if>" +
            " <if test='NCV02!=null'> and NCV02=#{NCV02} </if>" +
            " <if test='RS==null'> and RS is NULL </if>" +
            " <if test='RS!=null'> and RS=#{RS} </if>" +
            " <if test='RS02==null'> and RS02 is NULL </if>" +
            " <if test='RS02!=null'> and RS02=#{RS02} </if>" +
            " <if test='TM==null'> and TM is NULL </if>" +
            " <if test='TM!=null'> and TM=#{TM} </if>" +
            " <if test='TM02==null'> and TM02 is NULL </if>" +
            " <if test='TM02!=null'> and TM02=#{TM02} </if>" +
            " <if test='RV==null'> and RV is NULL </if>" +
            " <if test='RV!=null'> and RV=#{RV} </if>" +
            " <if test='RV02==null'> and RV02 is NULL </if>" +
            " <if test='RV02!=null'> and RV02=#{RV02} </if>" +
            " <if test='ASH==null'> and ASH is NULL </if>" +
            " <if test='ASH!=null'> and ASH=#{ASH} </if>" +
            " <if test='ASH02==null'> and ASH02 is NULL </if>" +
            " <if test='ASH02!=null'> and ASH02=#{ASH02} </if>" +
            " <if test='BondIndex==null'> and BondIndex is NULL </if>" +
            " <if test='BondIndex!=null'> and BondIndex=#{BondIndex} </if>" +
            " <if test='BondIndex02==null'> and BondIndex02 is NULL </if>" +
            " <if test='BondIndex02!=null'> and BondIndex02=#{BondIndex02} </if>" +
            " and isdelete=0 limit 1" +
            "</script>")
    PricePort getPricePortBySelect(@Param("region") String region,
                                   @Param("name") String name,
                                   @Param("pricetype") String pricetype,
                                   @Param("NCV") Integer NCV,
                                   @Param("NCV02") Integer NCV02,
                                   @Param("RS") BigDecimal RS,
                                   @Param("RS02") BigDecimal RS02,
                                   @Param("TM") BigDecimal TM,
                                   @Param("TM02") BigDecimal TM02,
                                   @Param("RV") BigDecimal RV,
                                   @Param("RV02") BigDecimal RV02,
                                   @Param("ASH") BigDecimal ASH,
                                   @Param("ASH02") BigDecimal ASH02,
                                   @Param("BondIndex") Integer BondIndex,
                                   @Param("BondIndex02") Integer BondIndex02);

    /**
     * 设置 / 取消 港口价格在前台显示
     */
    @Update("update priceport set isshow=(isshow+1)%2 where id=#{id} and isdelete=0")
    int doSetCancelShowPricePortMethod(int id);

    /**
     * 改变港口价格在前台显示顺序
     */
    @Update("update priceport set sequence=#{sequence} where id=#{id} and isdelete=0")
    boolean doChangePricePortSequenceMethod(@Param("id") int id, @Param("sequence") int sequence);

    /**
     * 根据 priceportid 查询已经存在数据dateList
     */
    @Select("select date from priceportvalue where priceportid=#{priceportid} and isdelete=0")
    List<String> getDateListByPriceportId(@Param("priceportid")int priceportid);

    /**
     * 检查数据库是否已经存 Excel中日期内 该该港口价格 数据
     */
    default MapObject getValueByPricePortIdAndDateList(int priceportid, List<String> dateList, String info) {
        MapObject map = new MapObject();
        List<String> dateSQLList = getDateListByPriceportId(priceportid);
        for (String date : dateList) {
            if (dateSQLList.contains(date)) {
                map.setSuccess(false);
                map.setError(info + date + " 的数据");
                return map;
            }
        }
        map.setSuccess(true);
        return map;
    }

    /**
     * 根据id查看详细
     */
    @Select("select * from priceport where id=#{id} and isdelete=0")
    PricePort getPricePortById(int id);

    /**
     * 删除港口价格
     */
    @Update("update priceport set isdelete=1 where id=#{id} and isdelete=0")
    int doDeletePricePortById(@Param("id")int id);

    /**
     * 根据 PricePortId 获取 PricePortValue list
     * @param priceportid
     */
    @Select("select count(1) from priceportvalue where priceportid=#{priceportid} and isdelete=0")
    int getPricePortValueCountByPortId(@Param("priceportid") int priceportid);

    @Select("select * from priceportvalue where priceportid=#{priceportid} and isdelete=0 order by date desc limit #{limit} offset #{offset}")
    List<PricePortValue> getPricePortValueListByPricePortId(@Param("priceportid") int priceportid,
                                                            @Param("limit")int limit,
                                                            @Param("offset")int offset);
    public default Pager<PricePortValue> pagePricePortValueListByPricePortId(int priceportid, int page, int pagesize){
        return Pager.config(this.getPricePortValueCountByPortId(priceportid), (int limit, int offset) -> this.getPricePortValueListByPricePortId(priceportid, limit, offset))
                .page(page, pagesize);
    }

    /**
     * 删除港口价格数值
     */
    @Update("update priceportvalue set isdelete=1 where id=#{id} and isdelete=0")
    int doDeletePricePortValueById(@Param("id")int id);

    /**
     * 通过id查询PricePortValue
     */
    @Select("select * from priceportvalue where id=#{id} and isdelete=0")
    PricePortValue getPricePortValueById(int id);

    /**
     * 手机站显示的6条港口价格
     */
    @Select(" select id, region, name, NCV, NCV02, RS, RS02 from priceport where isdelete=0 and isshow=1 order by sequence ")
    List<PricePort> getWapShowPricePortList();

    /**
     * 更具PricePortId获取最新的数据
     */
    @Select(" select value, value02, date from priceportvalue where isdelete=0 and priceportid=#{priceportid} order by date desc limit 1 ")
    PricePortValue getLatestPricePortValueByPricePortId(int priceportid);


}
