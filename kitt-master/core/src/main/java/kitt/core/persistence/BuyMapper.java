package kitt.core.persistence;

import kitt.core.domain.*;
import kitt.core.util.PageQueryParam;
import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 14/11/25.
 */
public interface BuyMapper {
    final String SellInfo1=
            "<if test='NCV01!=0'> (NCV &gt;= #{NCV01} or NCV02 &gt;= #{NCV01})</if>" +
            "<if test='NCV02!=7500'> and (NCV &lt;= #{NCV02} or NCV02 &lt;= #{NCV02})</if>" +
            "<if test='RS01!=0'> and (RS &gt;= #{RS01} or RS02 &gt;= #{RS01})</if>" +
            "<if test='RS02!=10'> and (RS &lt;= #{RS02} or RS02 &lt;= #{RS02})</if>" +
            "<if test='regionId!=0'> and regionId=#{regionId}</if>" +
            "<if test='provinceId!=0'> and provinceId=#{provinceId}</if>" +
            "<if test='portId &gt; 0'> and portId=#{portId}</if>" +
            "<if test='portId &lt; 0'> and otherharbour is not null and otherharbour!=''</if>" +
            "<if test='seller!=null'> and seller=#{seller}</if>" +
            "<if test='coaltype!=null'> and pname=#{coaltype}</if>";
    final String SellInfoOneStatus = "<if test='status!=null'> and status=#{status}</if>";
    final String SellInfoAvailQuantity = " and availquantity > 0";
    final String SellInfoTwoStatus = "<if test='status1!=null'> and (s.status=#{status1}</if>" +
            "<if test='status2!=null'> or s.status=#{status2}</if>)";
    final String limitOffset = " limit #{limit} offset #{offset}";
    final String startWhere = "<where>";
    final String endWhere = "</where>";
    final String OrderBy = "order by " +
            "<if test='sorttype==0'> createdate desc ,</if>" +
            "<if test='sorttype==2'> " +
            "<if test='sequence==0'>ykj+IFNULL(jtjlast,0)  desc ,</if>" +
            "<if test='sequence==1'>ykj+IFNULL(jtjlast,0) asc ,</if>" +
            "</if>" +
            "<if test='sorttype==1'>" +
            "<if test='sequence==0'> soldquantity desc ,</if>" +
            "<if test='sequence==1'> soldquantity asc ,</if>" +
            "</if>" +
            "seller desc, createtime desc ";
    //查询供应信息
    String SellInfoSelectSQL1 =
            "<if test='regionId!=0'> regionId=#{regionId} </if>" +
            "<if test='provinceId!=0'> and provinceId=#{provinceId} </if>" +
            "<if test='portId!=0'>  and portId=#{portId} </if>";
    String fuzzyQuerySQL = "<if test='productNo!=null'> and (pid like #{productNo} or dealername like #{productNo})</if>";
    String clienttypeSQL = "<if test='clienttype!=0'> and s.clienttype=#{clienttype} </if>";
    String dateZoneQureySQL =
            "<if test='startDate != null and startDate != \"\" and endDate != null and endDate != \"\"'> and (createtime between #{startDate} and #{endDate}) </if>" +
            "<if test='startDate != null and startDate != \"\" and (endDate == null or endDate == \"\")'> and (createtime between #{startDate} and now()) </if>";
    String SellInfoStatus =
            "<if test='status!=null'>  and status=#{status}</if>";
    String SellerControl = "<if test='sellertype == 0'> and seller='自营'</if>" +
            "<if test='sellertype != 0'> and seller!='自营'</if>";

    //1，供应信息,我要买列表
    @Select("<script> select * from sellinfo " +
            startWhere + SellInfo1 + SellInfoAvailQuantity + SellInfoOneStatus +" and status='VerifyPass' "+ endWhere +
            OrderBy +
            limitOffset +
            "</script>")
    public List<SellInfo> getSellInfoList(@Param("NCV01") Integer NCV01,
                                          @Param("NCV02") Integer NCV02,
                                          @Param("RS01") BigDecimal RS01,
                                          @Param("RS02") BigDecimal RS02,
                                          @Param("regionId") int regionId,
                                          @Param("provinceId") int provinceId,
                                          @Param("portId") int portId,
                                          @Param("seller") String seller,
                                          @Param("coaltype")String coaltype,
                                          @Param("status") EnumSellInfo status,
                                          @Param("sorttype")int sorttype,
                                          @Param("sequence")int sequence,
                                          @Param("limit")int limit,
                                          @Param("offset")int offset);

    @Select("<script>select count(*) from sellinfo " + startWhere + SellInfo1 + SellInfoAvailQuantity + SellInfoOneStatus + endWhere + " </script>")
    public int getSellInfoCount(@Param("NCV01") Integer NCV01,
                                @Param("NCV02") Integer NCV02,
                                @Param("RS01") BigDecimal RS01,
                                @Param("RS02") BigDecimal RS02,
                                @Param("regionId") int regionId,
                                @Param("provinceId") int provinceId,
                                @Param("portId") int portId,
                                @Param("seller") String seller,
                                @Param("coaltype")String coaltype,
                                @Param("status") EnumSellInfo status);



    //查询所有状态审核通过的供求信息
    @Select("select * from sellinfo where status=#{status} order by seller desc")
    public List<SellInfo> getAllSellInfo(EnumSellInfo status);

    //查询供应信息 userid
    @Select("select * from sellinfo where sellerid=#{sellerid} and status in ('WaitVerify', 'VerifyPass', 'WaitShopRun', 'VerifyNotPass', 'Canceled')" +
            " order by case status  when 'WaitVerify' then 1  when 'VerifyPass' then 2  when 'WaitShopRun' then  3  when 'VerifyNotPass' then 4  when 'Canceled' then 5 end, createtime desc " +
            limitOffset)
    public List<SellInfo> getSellinfoByUserid(@Param("sellerid")int sellerid,
                                              @Param("limit")int limit,
                                              @Param("offset")int offset);

    //查询供应信息的总条数 userid
    @Select("select count(*) from sellinfo where sellerid=#{sellerid} and (status='VerifyPass' or status='WaitVerify' or status='VerifyNotPass' or status='Canceled') and seller!='自营'")
    public int getSellinfoCountByUserid(int sellerid);

    /**
     * 个人中心 - 我的供应列表
     */
    final String CenterSellInfoSELECT1 = "" +
            "<if test='coaltype != \"\" and coaltype != null'> and pname=#{coaltype} </if>" +
            "<if test='pid != \"\" and pid != null'> and pid like #{pid} </if>" +
            "<if test='status != \"\" and status != null'> and status=#{status} </if>" +
            "<if test='NCV01 != 0 or NCV02 != 7500'> and ((NCV between ${NCV01} and ${NCV02}) or (NCV02 between ${NCV01} and ${NCV02})) </if>" +
            "<if test='startDate != \"\" and startDate != null and endDate != \"\" and endDate != null'> and (createdate between #{startDate} and #{endDate}) </if>" +
            "<if test='startDate != \"\" and startDate != null and (endDate == \"\" or endDate == null)'> and (createdate &gt;= #{startDate}) </if>" +
            "<if test='(startDate == \"\" or startDate == null) and endDate != \"\" and endDate != null'> and (createdate &lt;= #{endDate}) </if>";
    final String CenterSellInfoOrder1 =
            "order by " +
            "<if test='sortType!=null and sortType!=\"\"'>" +
                    "<if test='sortType == \"price\"'> (ykj + IFNULL(jtjlast, 0)) " +
                    "<if test='sortOrder == 0'> asc, </if>" +
                    "<if test='sortOrder == 1'> desc, </if>" +
                    "</if>" +
                    "<if test='sortType != \"price\"'> ${sortType}" +
                    "<if test='sortOrder == 0'> asc, </if>" +
                    "<if test='sortOrder == 1'> desc, </if>" +
                    "</if>" +
            "</if>" +
            " createtime desc " ;
    @Select("<script>" +
            " select * from sellinfo " +
            startWhere + " sellerid=#{sellerid}  and status in ('WaitVerify', 'VerifyPass', 'WaitShopRun', 'VerifyNotPass', 'Canceled')" +
            CenterSellInfoSELECT1 + endWhere + CenterSellInfoOrder1 + limitOffset +
            "</script>")
    public List<SellInfo> getSellinfoCenterListByUserid(@Param("sellerid")int sellerid,
                                                        @Param("coaltype") String coaltype,
                                                        @Param("pid") String pid,
                                                        @Param("status") String status,
                                                        @Param("NCV01") String NCV01,
                                                        @Param("NCV02") String NCV02,
                                                        @Param("startDate") String startDate,
                                                        @Param("endDate") String endDate,
                                                        @Param("sortType") String sortType,
                                                        @Param("sortOrder") int sortOrder,
                                                        @Param("limit")int limit,
                                                        @Param("offset")int offset);
    @Select("<script>" +
            " select count(1) from sellinfo " +
            startWhere + " sellerid=#{sellerid} and status in ('VerifyPass', 'WaitVerify', 'WaitShopRun', 'VerifyNotPass', 'Canceled') " +
            CenterSellInfoSELECT1 + endWhere +
            "</script>")
    public int getSellinfoCenterCountByUserid(@Param("sellerid") int sellerid,
                                              @Param("coaltype") String coaltype,
                                              @Param("pid") String pid,
                                              @Param("status") String status,
                                              @Param("NCV01") String NCV01,
                                              @Param("NCV02") String NCV02,
                                              @Param("startDate") String startDate,
                                              @Param("endDate") String endDate,
                                              @Param("sortType") String sortType,
                                              @Param("sortOrder") int sortOrder);

    //通过id查询供需信息
    @Select("select * from sellinfo where id=#{id}")
    public SellInfo getSellInfoById(int id);

    //统计店铺正在销售产品数量
    @Select("select count(*) from sellinfo where shopid=#{shopid} and status='VerifyPass'  ")
    public int countSellInfoShop(@Param("shopid") int shopid);

    //根据id查询供应信息 审核通过，可售库存大于零
    @Select("select * from sellinfo where id=#{id} and status='VerifyPass' and availquantity>0")
    public SellInfo getSelInfoByIdPassAvail(int id);

    @Select("select * from sellinfo where pid=#{pid} and status in('WaitConfirmed', 'WaitShopRun', 'WaitVerify', 'VerifyPass', 'VerifyNotPass', 'Canceled') order by id desc limit 1")
    public SellInfo getAvailableSellInfoByPid(String pid);

    //通过pid查询供需信息
    @Select("select * from sellinfo where pid=#{pid}")
    public List<SellInfo> getSellInfoByPid(String pid);


    @Select("select * from sellinfo where id=#{id} and version=#{version}")
    public SellInfo getSellInfoByIdAndVersion(@Param("id") int id,@Param("version") int version);

    //供求信息  修改可用吨数
    @Update("update sellinfo set availquantity=#{availquantity}, soldquantity=#{soldquantity} where id=#{id}")
    void setAvailSoldAmount(@Param("availquantity") int availquantity,
                            @Param("soldquantity") int soldquantity,
                            @Param("id") int id);

    @Update("update sellinfo set availquantity=availquantity-#{amount}, soldquantity=soldquantity+#{amount} where id=#{id} and availquantity-#{amount} >= 0 and soldquantity+#{amount} >= 0")
    int setSellInfoAvailSoldAmountById(@Param("amount")int amount,
                                       @Param("id")int id);

    @Select("select * from sellinfo where pid=#{pid} order by id desc limit 1")
    SellInfo getTheLatestSellInfo(String pid);

    @Update("update sellinfo set availquantity=availquantity+#{amount}, soldquantity=soldquantity-#{amount} where id=#{id} and availquantity+#{amount} >= 0 and soldquantity-#{amount} >= 0")
    int changeSellInfoAvailSoldAmountById(@Param("id")int id,
                                          @Param("amount")int amount);

    default int changeSellInfoAvailSoldAmount(int id, int amount){
        return changeSellInfoAvailSoldAmountById(getTheLatestSellInfo(getSellInfoById(id).getPid()).getId(), amount);
    }

    //改变供应信息状态
    @Update("update sellinfo set status=#{status}, remarks=#{remarks}, version=version+1 where id=#{id} and version=#{version} and status!=#{status}")
    int changeSellInfoStatus(@Param("id")int id,
                             @Param("version")int version,
                             @Param("status")EnumSellInfo status,
                             @Param("remarks")String remarks);

    //改变供应信息状态
    @Update("update sellinfo set status=#{status}, remarks=#{remarks} where id=#{id}")
    int updateSellInfoStatus(@Param("status")EnumSellInfo status,
                             @Param("remarks")String remarks,
                             @Param("id")int id);

    //设置sellinfo表中的阶梯价，最低价
    @Update("update sellinfo set jtjlast=#{jtjlast} where id=#{id}")
    public boolean setJtjLastById(@Param("jtjlast")BigDecimal jtjlast, @Param("id")int id);

    //取消供应信息
    @Update("update sellinfo set status='Canceled' where id=#{id} and status !='SellOut'")
    public void cancelSellInfoById(int id);

    //发布供应信息
    @Insert("<script>" +
            " insert into sellinfo(pid,status, pname, " +
            " NCV, NCV02, ADS, ADS02, RS, RS02, TM, TM02, IM, IM02, ADV, ADV02, RV, RV02, ASH, ASH02, AFT, HGI, GV, GV02, " +
            " YV, YV02, FC, FC02, PS, PSName, CRC, CRC02," +
            " ykj, seller, deliveryregion, deliveryprovince, deliveryplace, " +
            " otherharbour, deliverymode, deliverytime1, deliverytime2, supplyquantity, availquantity, " +
            " inspectorg, otherinspectorg, createtime, " +
            " <if test='verifytime!=null'>verifytime,</if>" +
            " sellerid, producttype, createdate,regionId,provinceId,portId,originplace,paymode," +
            " payperiod,releaseremarks,parentid,editnum,traderid,dealername,dealerphone, " +
            " linktype, linkmanname, linkmanphone, clienttype, jtjlast, shopid, type,shoppname,chemicalexam1,chemicalexam2," +
            " chemicalexam3, brandname, tax, accountsmethod, accountsmethodname, logistics, finance, promotion, promotionremarks," +
            " coalpic1, coalpic2, coalpic3, coalpic4, coalpic5, coalpic6 )" +
            " values(dateseq_next_value('CP'), " +
            " #{status}, #{pname}, " +
            " <if test='NCV!=null'>#{NCV},</if> <if test='NCV==null'>0,</if> " +
            " <if test='NCV02!=null'>#{NCV02},</if> <if test='NCV02==null'>0,</if> " +
            " <if test='ADS!=null'>#{ADS},</if> <if test='ADS==null'>0.00,</if>" +
            " <if test='ADS02!=null'>#{ADS02},</if> <if test='ADS02==null'>0.00,</if>" +
            " #{RS}, #{RS02}, #{TM}, #{TM02}," +
            " <if test='IM!=null'>#{IM},</if> <if test='IM==null'>0.00,</if>" +
            " <if test='IM02!=null'>#{IM02},</if> <if test='IM02==null'>0.00,</if>" +
            " #{ADV}, #{ADV02}," +
            " <if test='RV!=null'>#{RV},</if> <if test='RV==null'>0.00,</if>" +
            " <if test='RV02!=null'>#{RV02},</if> <if test='RV02==null'>0.00,</if>" +
            " <if test='ASH!=null'>#{ASH},</if>  <if test='ASH==null'>0.0,</if>" +
            " <if test='ASH02!=null'>#{ASH02},</if>  <if test='ASH02==null'>0.0,</if>" +
            " <if test='AFT!=null'>#{AFT},</if> <if test='AFT==null'>0,</if>" +
            " <if test='HGI!=null'>#{HGI},</if> <if test='HGI==null'>0,</if>" +
            " <if test='GV!=null'>#{GV},</if> <if test='GV==null'>0,</if>" +
            " <if test='GV02!=null'>#{GV02},</if> <if test='GV02==null'>0,</if>" +
            " <if test='YV!=null'>#{YV},</if> <if test='YV==null'>0,</if>" +
            " <if test='YV02!=null'>#{YV02},</if> <if test='YV02==null'>0,</if>" +
            " <if test='FC!=null'>#{FC},</if> <if test='FC==null'>0,</if>" +
            " <if test='FC02!=null'>#{FC02},</if> <if test='FC02==null'>0,</if>" +
            " <if test='PS!=null'>#{PS},</if> <if test='PS==null'>0,</if>" +
            " #{PSName}," +
            " <if test='CRC!=null'>#{CRC},</if> <if test='CRC==null'>0,</if>" +
            " <if test='CRC02!=null'>#{CRC02},</if> <if test='CRC02==null'>0,</if>" +
            " #{ykj}, #{seller}, #{deliveryregion}, #{deliveryprovince}," +
            " #{deliveryplace}, #{otherharbour}, #{deliverymode}, #{deliverytime1}, #{deliverytime2}, " +
            " #{supplyquantity}, #{availquantity}, #{inspectorg}, #{otherinspectorg}, now(), " +
            " <if test='verifytime!=null'>#{verifytime},</if>" +
            " #{sellerid}, #{producttype}, curdate(),#{regionId},#{provinceId},#{portId}," +
            " #{originplace},#{paymode},#{payperiod},#{releaseremarks},#{parentid}," +
            " #{editnum},#{traderid},#{dealername},#{dealerphone}," +
            " #{linktype}, #{linkmanname}, #{linkmanphone}, #{clienttype},#{jtjlast},#{shopid},#{type}, #{shoppname}, " +
            " #{chemicalexam1}, #{chemicalexam2},#{chemicalexam3}, #{brandname}, #{tax}, #{accountsmethod}, " +
            " #{accountsmethodname}, #{logistics},  #{finance}, #{promotion}, #{promotionremarks}, #{coalpic1}, " +
            " #{coalpic2}, #{coalpic3}, #{coalpic4}, #{coalpic5}, #{coalpic6})" +
            "</script>")
    @Options(useGeneratedKeys = true)
    public int addSellinfo(SellInfo sellInfo);


    //修改增加供应信息，产品编号不变
    @Insert(" insert into sellinfo(pid,status, pname, NCV, NCV02, ADS, ADS02, RS, RS02, TM, TM02, IM, IM02, " +
            " ADV, ADV02, RV, RV02, ASH, ASH02, AFT, HGI, GV, GV02, YV, YV02, FC, FC02, PS, PSName," +
            " ykj, seller, deliveryregion, deliveryprovince, deliveryplace, " +
            " otherharbour, deliverymode, deliverytime1, deliverytime2, supplyquantity, " +
            " availquantity, soldquantity, inspectorg, otherinspectorg, createtime, sellerid, " +
            " producttype, createdate,regionId,provinceId,portId,originplace,paymode," +
            " payperiod,releaseremarks,parentid,editnum, linktype," +
            " linkmanname, linkmanphone, remarks, traderid, dealername, dealerphone," +
            " viewtimes, clienttype, jtjlast, type, shopid, shoppname, " +
            " chemicalexam1, chemicalexam2, chemicalexam3, brandname, tax, accountsmethod, accountsmethodname, logistics," +
            " finance, promotion, promotionremarks, coalpic1, coalpic2, coalpic3, coalpic4, coalpic5, coalpic6) " +
            " values(#{pid}, #{status}, #{pname},  #{NCV}, #{NCV02}, #{ADS}, #{ADS02}, #{RS}, #{RS02}, #{TM}, #{TM02}, " +
            " #{IM}, #{IM02}, #{ADV}, #{ADV02},  #{RV}, #{RV02}, #{ASH}, #{ASH02}, #{AFT}, #{HGI}, #{GV}, #{GV02}," +
            " #{YV}, #{YV02}, #{FC}, #{FC02}, #{PS}, #{PSName}, #{ykj}, #{seller}, #{deliveryregion}, #{deliveryprovince}," +
            " #{deliveryplace}, #{otherharbour},  #{deliverymode}, #{deliverytime1}, #{deliverytime2}, #{supplyquantity}, " +
            " #{availquantity}, #{soldquantity}, #{inspectorg}, #{otherinspectorg}, #{createtime}, " +
            " #{sellerid}, #{producttype}, curdate(),#{regionId},#{provinceId},#{portId}," +
            " #{originplace},#{paymode},#{payperiod},#{releaseremarks},#{parentid},#{editnum}, " +
            " #{linktype}, #{linkmanname}, #{linkmanphone}, #{remarks}, #{traderid}," +
            " #{dealername}, #{dealerphone}, #{viewtimes}, #{clienttype},#{jtjlast}, #{type}, #{shopid}, #{shoppname}, " +
            " #{chemicalexam1},#{chemicalexam2},#{chemicalexam3}, #{brandname}, #{tax}, #{accountsmethod}, #{accountsmethodname}, " +
            " #{logistics},  #{finance}, #{promotion}, #{promotionremarks}, #{coalpic1}, #{coalpic2}, #{coalpic3}, " +
            "#{coalpic4}, #{coalpic5}, #{coalpic6})")
    @Options(useGeneratedKeys=true)
    public int addSellinfoForUpdate(SellInfo sellInfo);

    //更新供应信息
    @Update("<script>update sellinfo set pname=#{pname}, status=#{status}, " +
            "<if test='NCV!=null'>NCV=#{NCV},</if> <if test='NCV==null'>NCV=0,</if>" +
            "<if test='NCV02!=null'>NCV02=#{NCV02},</if> <if test='NCV02==null'>NCV02=0,</if>" +
            " RS=#{RS}, RS02=#{RS02}, TM=#{TM}, TM02=#{TM02}, " +
            "<if test='ADS!=null'>ADS=#{ADS},</if> <if test='ADS==null'>ADS=0,</if> " +
            "<if test='ADS02!=null'>ADS02=#{ADS02},</if> <if test='ADS02==null'>ADS02=0,</if> " +
            "<if test='IM!=null'>IM=#{IM},</if> <if test='IM==null'>IM=0,</if> " +
            "<if test='IM02!=null'>IM02=#{IM02},</if> <if test='IM02==null'>IM02=0,</if> " +
            " ADV=#{ADV}, ADV02=#{ADV02}, " +
            "<if test='RV!=null'> RV=#{RV},</if> <if test='RV==null'>RV=0,</if>"+
            "<if test='RV02!=null'> RV02=#{RV02},</if> <if test='RV02==null'>RV02=0,</if>"+
            "<if test='ASH!=null'>ASH=#{ASH},</if> <if test='ASH==null'>ASH=0,</if> " +
            "<if test='ASH02!=null'>ASH02=#{ASH02},</if> <if test='ASH02==null'>ASH02=0,</if> " +
            "<if test='AFT!=null'>AFT=#{AFT},</if> <if test='AFT==null'>AFT=0,</if> " +
            "<if test='HGI!=null'>HGI=#{HGI},</if> <if test='HGI==null'>HGI=0,</if>" +
            "<if test='GV!=null'>GV=#{GV},</if> <if test='GV==null'>GV=0,</if>" +
            "<if test='GV02!=null'>GV02=#{GV02},</if> <if test='GV02==null'>GV02=0,</if>" +
            "<if test='YV!=null'>YV=#{YV},</if> <if test='YV==null'>YV=0,</if>" +
            "<if test='YV02!=null'>YV02=#{YV02},</if> <if test='YV02==null'>YV02=0,</if>" +
            "<if test='FC!=null'>FC=#{FC},</if> <if test='FC==null'>FC=0,</if>" +
            "<if test='FC02!=null'>FC02=#{FC02},</if> <if test='FC02==null'>FC02=0,</if>" +
            "<if test='PS!=null'>PS=#{PS},</if> <if test='PS==null'>PS=0,</if>" +
            " PSName=#{PSName}," +
            "<if test='CRC!=null'>CRC=#{CRC},</if> <if test='CRC==null'>CRC=0,</if>" +
            "<if test='CRC02!=null'>CRC02=#{CRC02},</if> <if test='CRC02==null'>CRC02=0,</if>" +
            " ykj=#{ykj}, seller=#{seller}, " +
            " deliveryregion=#{deliveryregion}, deliveryprovince=#{deliveryprovince}," +
            " deliveryplace=#{deliveryplace}, otherharbour=#{otherharbour}, deliverymode=#{deliverymode}," +
            " deliverytime1=#{deliverytime1}, deliverytime2=#{deliverytime2}, supplyquantity=#{supplyquantity}, " +
            " availquantity=#{availquantity}, inspectorg=#{inspectorg}, otherinspectorg=#{otherinspectorg}," +
            " producttype=#{producttype},  originplace=#{originplace}, " +
            " paymode=#{paymode}, payperiod=#{payperiod}, releaseremarks=#{releaseremarks}," +
            " regionId=#{regionId}, provinceId=#{provinceId}, portId=#{portId}, chemicalexam1=#{chemicalexam1}," +
            " chemicalexam2=#{chemicalexam2}, chemicalexam3=#{chemicalexam3}, coalpic1=#{coalpic1}, coalpic2=#{coalpic2}," +
            " coalpic3=#{coalpic3}, coalpic4=#{coalpic4}, coalpic5=#{coalpic5}, coalpic6=#{coalpic6}, " +
            " linktype=#{linktype}, linkmanname=#{linkmanname}, linkmanphone=#{linkmanphone}, jtjlast=#{jtjlast}, " +
            " traderid=#{traderid}, dealername=#{dealername}, dealerphone=#{dealerphone}, shoppname=#{shoppname}," +
            " brandname=#{brandname}, tax=#{tax}, accountsmethod=#{accountsmethod}, accountsmethodname=#{accountsmethodname}," +
            " logistics=#{logistics}, finance=#{finance}, promotion=#{promotion}, promotionremarks=#{promotionremarks}, " +
            " version=version+1 where id=#{id} and version=#{version}</script>")
    public int updateSellinfo(SellInfo sellInfo);

    @Update("update sellinfo set status=#{status}, version=version+1 where id=#{id} and version=#{version}")
    public int setSellinfoStatus(@Param("status")EnumSellInfo status,
                                 @Param("id")int id,
                                 @Param("version")int version);

    @Update("update sellinfo set status=#{status} where id=#{id} and status!='OutOfStack'")
    public int putOffMallProduct(@Param("status")EnumSellInfo status, @Param("id")int id);

    @Update("update sellinfo set producttype=#{producttype} where id=#{id} and version=#{version} and producttype!=#{producttype}")
    public int changeRecommentStatus(@Param("id")int id,
                                  @Param("version")int version,
                                  @Param("producttype")String producttype);

    @Update("update sellinfo set status=#{status}, remarks=#{remarks}, " +
            " dealerphone=#{dealerphone},dealername=#{dealername}, traderid=#{traderId}, " +
            " verifytime=now(),version=version+1 " +
            " where id=#{id} and version=#{version}")
    public int verifySellinfoStatus(@Param("dealerphone") String phone,
                                    @Param("dealername")String dealername,
                                    @Param("traderId")Integer traderId,
                                    @Param("status")EnumSellInfo status,
                                    @Param("remarks")String remarks,
                                    @Param("id")int id,
                                    @Param("version")int version);

    //删除供应信息
    @Delete("delete from sellinfo where id=#{id}")
    public void deleteSellinfo(int id);

    //admin供应信息审核列表
    @Select("select count(*) from sellinfo where status=#{status} and seller != '自营'")
    public int countAllSellInfo(@Param("status")EnumSellInfo status);

    @Select("select * from sellinfo where status=#{status} and seller != '自营'"+ limitOffset)
    public List<SellInfo> listAllSellInfo(@Param("status") EnumSellInfo status, @Param("limit") int limit, @Param("offset") int offset);

    public default Pager<SellInfo> pageAllSellinfo(EnumSellInfo status, int page, int pagesize){
        return Pager.config(this.countAllSellInfo(status), (int limit, int offset) -> this.listAllSellInfo(status, limit, offset))
                .page(page, pagesize);
    }

    //产品-查询列表
    @Select("<script>" +
            "select count(*) from sellinfo s " +
            startWhere + SellInfoSelectSQL1 + fuzzyQuerySQL + clienttypeSQL + dateZoneQureySQL + SellInfoTwoStatus + " and type=#{type} " + endWhere +
            "</script>")
    public int countSellInfoSelect(@Param("regionId")int regionId,
                                   @Param("provinceId")int provinceId,
                                   @Param("portId")int portId,
                                   @Param("productNo")String productNo,
                                   @Param("clienttype")int clienttype,
                                   @Param("startDate")String startDate,
                                   @Param("endDate")String endDate,
                                   @Param("status1")EnumSellInfo status1,
                                   @Param("status2")EnumSellInfo status2,
                                   @Param("type")int type);
    @Select("<script>" +
            " select s.*, a.name as tradername_temp, a.id as traderid_temp, a.phone as traderphone_temp from sellinfo s " +
            " left join users u on u.id = s.sellerid " +
            " left join admins a on u.traderid = a.id " +
            startWhere + SellInfoSelectSQL1 + fuzzyQuerySQL + clienttypeSQL + dateZoneQureySQL + SellInfoTwoStatus + " and type=#{type} " + endWhere + "order by createtime desc" + limitOffset +
            "</script>")
    public List<Map<String, Object>> listSellInfoSelect(@Param("regionId")int regionId,
                                                        @Param("provinceId")int provinceId,
                                                        @Param("portId")int portId,
                                                        @Param("productNo")String productNo,
                                                        @Param("clienttype")int clienttype,
                                                        @Param("startDate")String startDate,
                                                        @Param("endDate")String endDate,
                                                        @Param("status1")EnumSellInfo status1,
                                                        @Param("status2")EnumSellInfo status2,
                                                        @Param("type")int type,
                                                        @Param("limit")int limit,
                                                        @Param("offset")int offset);

    default Pager<Map<String, Object>> getSellInfoBySelect(int regionId, int provinceId, int portId, String procuctNo, int clienttype, String startDate, String endDate, EnumSellInfo status1, EnumSellInfo status2, int type, int page, int pagesize){
        return Pager.config(this.countSellInfoSelect(regionId, provinceId, portId, Where.$like$(procuctNo), clienttype, startDate, endDate, status1, status2, type),
                (int limit, int offset) -> this.listSellInfoSelect(regionId, provinceId, portId, Where.$like$(procuctNo), clienttype, startDate, endDate, status1, status2, type, limit, offset))
                .page(page, pagesize);
    }

    @Select("<script>" +
            "select s.*, u.securephone from sellinfo s left join users u on s.sellerid = u.id" +
            startWhere + SellInfoSelectSQL1 + fuzzyQuerySQL + clienttypeSQL + dateZoneQureySQL + SellInfoTwoStatus + " and type=#{type} " + endWhere + "order by createtime desc " +
            "</script>")
    List<Map<String, Object>> getExportSellInfoList(@Param("regionId")int regionId,
                                                    @Param("provinceId")int provinceId,
                                                    @Param("portId")int portId,
                                                    @Param("productNo")String productNo,
                                                    @Param("clienttype")int clienttype,
                                                    @Param("startDate")String startDate,
                                                    @Param("endDate")String endDate,
                                                    @Param("status1")EnumSellInfo status1,
                                                    @Param("status2")EnumSellInfo status2,
                                                    @Param("type")int type);

    //添加审核信息
    @Insert("insert into supplyverify(status, applytime, sellinfoid, userid) values(#{status}, #{applytime}, #{sellinfoid}, #{userid})")
    public int addSupplyVerify(SupplyVerify supplyVerify);

    //查询该供应最新id by pid
    @Select("select max(id) from sellinfo where pid = #{pid}")
    public int getSupplyLatestId(String pid);

    //查询该供应最新editNum by pid
    @Select("select max(editnum) from sellinfo where pid = #{pid}")
    public int getSupplyLatestEditNum(String pid);

    //查询供应审核信息 by id
    @Select("select * from supplyverify where sellinfoid=#{sellinfoid} order by id desc limit 1")
    public SupplyVerify getSupplyVerifyBySellinfoId(int sellinfoid);


    //审核供应信息通过
    @Update("update supplyverify set status=#{status}, verifyman=#{verifyman}, remarks=#{remarks}, verifytime=#{verifytime} where status='WaitVerify' and id=#{id}")
    public int verifySupplyInfo(@Param("status")EnumSellInfo status, @Param("verifyman")String verifyman, @Param("remarks")String remarks, @Param("verifytime")LocalDateTime verifytime, @Param("id")int id);

    static final  String dynamicSql =
            "<where>" +
                    "<if test='sell!=null'>" +
                    "status=#{sell.status}" +
                    "<if test='sell.pid!=null and sell.pid!=\"\"'>  and pid=#{sell.pid}</if>" +
                    "<if test='sell.seller!=null and sell.seller!=\"自营\"'> and seller!=\"自营\"</if>" +
                    "<if test='sell.seller!=null and sell.seller==\"自营\"'> and seller=\"自营\"</if>" +
                    "<if test='sell.ykj!=null'> and (ykj=#{sell.ykj}  or jtjlast=#{sell.ykj})</if>" +
                    "<if test='sell.pname!=null and sell.pname!=\"\"'> and pname=#{sell.pname}</if>" +
                    "<if test='sell.deliverymode!=null and sell.deliverymode!=\"\"'> and deliverymode=#{sell.deliverymode}</if>" +
                    "<if test='sell.deliveryregion!=null and sell.deliveryregion!=\"\"'> and deliveryregion=#{sell.deliveryregion}</if>" +
                    "<if test='sell.deliveryprovince!=null and sell.deliveryprovince!=\"\" ' > and deliveryprovince=#{sell.deliveryprovince}</if>" +
                    "<if test='sell.deliveryplace!=null and sell.deliveryplace!=\"\"'>  and deliveryplace=#{sell.deliveryplace}</if>" +
                    "<if test='sell.NCV!=null'> and NCV=#{sell.NCV}</if>" +
                    "<if test='sell.RS!=null'> and RS=#{sell.RS}</if>" +
                    "<if test='sell.ADV!=null'> and ADV=#{sell.ADV}</if>" +
                    "<if test='sell.TM!=null'> and TM=#{sell.TM}</if>"+
                    "</if>" +
                    //"<if test='currentUser.isAdmin!=true'> and dealerid=#{currentUser.jobnum}</if>" +
                    "</where>";
    String sellInfoOrderBy =
            "<if test='sortType==\"createtime\"'>" +
            "<if test='!sortOrder'>order by createtime desc</if>" +
            "<if test='sortOrder'>order by createtime asc</if>" +
            "</if>" +
            "<if test='sortType==\"pid\"'>" +
            "<if test='!sortOrder'>order by pid desc</if>" +
            "<if test='sortOrder'>order by pid asc</if>" +
            "</if>" +
            "<if test='sortType==\"verifytime\"'>" +
            "<if test='!sortOrder'>order by verifytime desc</if>" +
            "<if test='sortOrder'>order by verifytime asc</if>" +
            "</if>";

    @Select("<script> select * from sellinfo" + dynamicSql + sellInfoOrderBy + " limit #{limit} offset #{offset} </script>")
    public List<SellInfo> showAllList(@Param("sell") SellInfo sellInfo,
                                      @Param("sortType") String sorttype,
                                      @Param("sortOrder") boolean sortOrder,
                                      @Param("limit") int limit,
                                      @Param("offset") int offset);

    @Select("<script> select count(1) from sellinfo" + dynamicSql + " </script>")
    public int showAllListCount(@Param("sell") SellInfo sellInfo);

    public default Pager<SellInfo> getAllSupplyList(SellInfo sellInfo, String sortType, boolean sortOrder, int page, int pagesize) {
        return Pager.config(this.showAllListCount(sellInfo), (int limit, int offset) -> this.showAllList(sellInfo, sortType, sortOrder, limit, offset))
                .page(page, pagesize);
    }

    @Update("update sellinfo set createdate=#{createdate} where id=#{id}")
    public void setCreateDate(@Param("createdate")LocalDate createdate, @Param("id")int id);

    //查询所有待确认供应信息
    @Select("select * from sellinfo where status=#{status}")
    public List<SellInfo> getAllWaitSellInfo(EnumSellInfo status);

    //查询推荐产品列表
    @Select("select * from sellinfo where status='VerifyPass' and availquantity > 0 order by type, verifytime desc limit 5")
    public List<SellInfo> getRecommendSellinfoList();

    //商城产品历史记录
    @Select("select * from sellinfo where status='VerifyPass' and availquantity =0 order by lastupdatetime desc limit #{limit}")
    public List<SellInfo> getRecommendHistoryList(@Param("limit")int limit);




    //查询所有审核通过sellinfo
    @Select("select * from sellinfo where status='VerifyPass'")
    public List<SellInfo> getAllPassSellinfoList();

    //统计审核通过的供应信息 -- 动力煤
    @Select("select IFNULL(SUM(`availquantity`), 0) from sellinfo where status='VerifyPass' and pname='动力煤'")
    public long getSumPowerPassSellInfo();

    //统计审核通过的供应信息 -- 无烟煤
    @Select("select IFNULL(SUM(`availquantity`), 0) from sellinfo where status='VerifyPass' and pname='无烟煤'")
    public long getSumAnthracitePassSellInfo();

    //统计审核通过的供应信息 -- 喷吹煤
    @Select("select IFNULL(SUM(`availquantity`), 0) from sellinfo where status='VerifyPass' and pname='喷吹煤'")
    public long getSumCoalInjectionPassSellInfo();

    //统计审核通过的供应信息 -- 焦煤
    @Select("select IFNULL(SUM(`availquantity`), 0) from sellinfo where status='VerifyPass' and pname='焦煤'")
    public long getSumCokingCoalPassSellInfo();

    //通过DealerId查询供应信息
    @Select("select * from sellinfo where dealerid=#{dealerid}")
    public List<SellInfo> getSellinfoByDealerId(@Param("dealerid")String dealerid);

    //更改sellinfo交易员信息
    @Update("update sellinfo set dealerid=#{dealerid}, dealername=#{dealername}, " +
            "dealerphone=#{dealerphone} where id=#{id}")
    public void updateSellinfoDealer(@Param("dealerid")String dealerid,
                                     @Param("dealername")String dealername,
                                     @Param("dealerphone")String dealerphone,
                                     @Param("id")int id);

    //易煤直通车
    @Select("select * from sellinfo where status='VerifyPass' and pname=#{value} and seller='自营'  and availquantity>0")
    List<SellInfo> findByCoalType(String coalType);


    @Select("SELECT * from `sellinfo`  where status='VerifyPass' and availquantity>0 and pname=#{coal} and seller='自营'")
    List<SellInfo> getByDeliveryregion(@Param("coal")String coal);


    @Select("select * from sellinfo where deliveryregion=#{deliveryregion} and " +
            "deliveryprovince=#{deliveryprovince} and deliveryplace=#{deliveryplace}")
    List<SellInfo> getSellInfoByRegionProvinceHarbour(@Param("deliveryregion")String deliveryregion,
                                                      @Param("deliveryprovince")String deliveryprovince,
                                                      @Param("deliveryplace")String deliveryplace);

    @Update("update sellinfo set dealerphone=#{dealerphone} where id=#{id}")
    public void updateSellinfoDealerPhoneById(@Param("dealerphone")String dealerphone,
                                              @Param("id")int id);

    @Update("update sellinfo set viewtimes=viewtimes+1 where id=#{id}")
    public void setPageViewTimesById(@Param("id")int id);


    @Update("update sellinfo set parentid=#{parentid} where id=#{id}")
    public int setParentIdById(@Param("parentid")int parentid,
                               @Param("id")int id);

    @Select("SELECT * from sellinfo  where (parentid = #{parentid}  or  id = #{parentid} ) and  (id!=#{id}) order by editnum desc limit 10 ")
    List<SellInfo> getSellInfoEditHist(@Param("id")int id,@Param("parentid")int parentid);

    @Update("update sellinfo set dealerphone=#{dealerphone} where traderid=#{dealerid}")
    public void updateDealerPhone(@Param("dealerphone")String dealerphone,
                                  @Param("dealerid")int dealerid);

    /*******************author by zxy********************/

    //通过交易员id修改交易员信息
    @Update("update sellinfo set  dealername = #{newDealer.name}, dealerphone=#{newDealer.phone}, traderid=#{newDealer.id} where status='VerifyPass' and traderid=#{oldId}")
    public void updateSellInfoDealerByDealerId(@Param("newDealer") Admin admin, @Param("oldId") int oldId);

    @Select("select * from sellinfo where id=#{value} for update")
    public SellInfo lockSupplyById(int supplyId);

    @Select("select * from sellinfo where id=#{supplyId} and sellerid=#{userId}")
    public  SellInfo loadSellInfo(@Param("supplyId")int supplyId,@Param("userId")int userId);


    //修改库存&已售吨数
    @Update("update sellinfo set availquantity=availquantity-#{amount},soldquantity=soldquantity+#{amount} where id=#{supplyId} ")
    public void updateStockAndsolded(@Param("supplyId") int supplyId, @Param("amount") int amount);


    public final String WeiXinSupplydynamicSql = "<where>" +
            "<if test='coalType!=null and coalType!=\"\"'> and pname=#{coalType}</if>" +
            "<if test='provinceId!=null'> and provinceId=#{provinceId}</if>" +
            "<if test='portId!=null'> and portId=#{portId}</if>" +
            "<if test='lowNCV!=null and highNCV!=null'> and (NCV between #{lowNCV} and  #{highNCV}  or NCV02 between #{lowNCV} and #{highNCV})</if>" +
            "<if test='lowRS!=null and highRS!=null'> and (RS between #{lowRS} and  #{highRS}  or RS02 between #{lowRS} and  #{highRS})</if>" +
            "<if test='coalShop==\"shop\"'> and shopid is not null and shopid!='0' </if>" +
            "<if test='type==0'> and type=#{type} </if>" +
            " and availquantity > 0 and status='VerifyPass' " +
            "</where> ";
    @Select("<script>select count(1) from sellinfo" + WeiXinSupplydynamicSql + "</script>")
    public int countSell(@Param("type") int type,
                         @Param("provinceId")Integer provinceId,
                         @Param("portId")Integer portId,
                         @Param("lowNCV") Integer lowNCV,
                         @Param("highNCV") Integer highNCV,
                         @Param("lowRS") BigDecimal lowRS,
                         @Param("highRS") BigDecimal highRS,
                         @Param("coalType")String coalType,
                         @Param("coalShop")String coalShop);

    @Select("<script>select * from sellinfo" + WeiXinSupplydynamicSql + "<if test='null!=orderType'> order by ${orderType.field} ${orderType.direction}, seller desc,createtime desc</if> <choose><when test='anchor==null or anchor==\"\"'>limit #{pageQuery.pagesize} offset #{pageQuery.indexNum}</when><otherwise>limit ${pageQuery.rowNum}  offset 0</otherwise></choose> </script>")
    public List<SellInfo> listSellInfo(@Param("pageQuery") PageQueryParam param,
                                       @Param("orderType") OrderByType orderByType,
                                       @Param("type") int type,
                                       @Param("provinceId")Integer provinceId,
                                       @Param("portId")Integer portId,
                                       @Param("lowNCV") Integer lowNCV,
                                       @Param("highNCV") Integer highNCV,
                                       @Param("lowRS") BigDecimal lowRS,
                                       @Param("highRS") BigDecimal highRS,
                                       @Param("coalType")String coalType,
                                       @Param("coalShop")String coalShop,
                                       @Param("anchor")String anchor);

    /**
     * 首页我的供应_新进展
     * @param sellerid
     * @return
     */
    @Select("select count(1) from orders o left join sellinfo s on o.sellinfoid = s.id where s.status='VerifyPass' and o.status in ('WaitPayment', 'WaitVerify', 'MakeMatch', 'VerifyPass', 'WaitBalancePayment', 'VerifyNotPass', 'ReturnGoods') and s.sellerid=#{sellerid}")
    int getPersonalLatestChangeSellInfo(int sellerid);



    @Update("<script> update sellinfo set " +
            "<choose> " +
                "<when test='type==1'> chemicalexam1=NULL </when>" +
                "<when test='type==2'> chemicalexam2=NULL </when>" +
                "<when test='type==3'> chemicalexam3=NULL </when>" +
                "<otherwise> chemicalexam1=chemicalexam1 </otherwise>" +
            "</choose>" +
            "where id=#{id} " +
            "</script>")
    public int deleteChemicalExam(@Param("id")int id,
                                   @Param("type") int type);

    @Update("<script> update sellinfo set " +
            "<choose> " +
            "<when test='type==1'> chemicalexam1=#{chemicalexam} </when>" +
            "<when test='type==2'> chemicalexam2=#{chemicalexam} </when>" +
            "<when test='type==3'> chemicalexam3=#{chemicalexam} </when>" +
            "<otherwise> chemicalexam1=chemicalexam1 </otherwise>" +
            "</choose>" +
            "where id=#{id} " +
            "</script>")
    public int updateChemicalExam(@Param("id")int id,
                                  @Param("type") int type,
                                  @Param("chemicalexam")String chemicalexam);

//    //商城产品修改
//    @Update("update sellinfo set supplyquantity=#{supplyquantity},paymode=#{paymode},payperiod=#{payperiod},deliverytime1=#{deliverytime1},deliverytime2=#{deliverytime2},releaseremarks=#{releaseremarks},ykj=#{ykj} where id=#{id} ")
//    public void updateSupplySellinfo(SellInfo sellInfo);

    //判断是否自营 0 自营 !=0 非自营
    @Select("select type,pid,sellerid from  sellinfo where id=(select sellinfoid from orders where id=#{value}) ")
    public SellInfo findSupplyWithOrder(int orderId);

    //今天过期的供应信息列表
    @Select("select * from sellinfo where date(deliverytime2)=CURDATE()")
    List<SellInfo> getSellInfoTodayOutOfDateList();



}
