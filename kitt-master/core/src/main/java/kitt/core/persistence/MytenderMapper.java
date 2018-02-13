package kitt.core.persistence;

import kitt.core.domain.Mytender;
import kitt.core.domain.SelectTender;
import kitt.core.domain.TenderStatus;
import org.apache.ibatis.annotations.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by lich on 15/11/13.
 */
public interface MytenderMapper {

    @Insert("<script>" +
            "insert into mytender(" +
            "<if test='releasecompanyid!=null'>releasecompanyid,</if>" +
            "<if test='competecompanyid!=null'>competecompanyid,</if>" +
            "<if test='tenderdeclarationid!=null'>tenderdeclarationid,</if>" +
            "<if test='tenderitemid!=null'>tenderitemid,</if>" +
            "<if test='tenderpacketid!=null'>tenderpacketid,</if>" +
            "<if test='releasecompanyname!=null'>releasecompanyname,</if>" +
            "<if test='competecompanyname!=null'>competecompanyname,</if>" +
            "<if test='tenderdate!=null'>tenderdate,</if>" +
            "<if test='verifydate!=null'>verifydate,</if>" +
            "<if test='tendercode!=null'>tendercode,</if>" +
            "<if test='releaseuserid!=null'>releaseuserid,</if>" +
            "<if test='competeuserid!=null'>competeuserid,</if>" +
            "<if test='coaltype!=null'>coaltype,</if>" +
            "<if test='NCV!=null'>NCV,</if>" +
            "<if test='TM!=null'>TM,</if>" +
            "<if test='RS!=null'>RS,</if>" +
            "<if test='ADV!=null'>ADV,</if>" +
            "<if test='ADV02!=null'>ADV02,</if>" +
            "<if test='supplyamount!=null'>supplyamount,</if>" +
            "<if test='deliverymode!=null'>deliverymode,</if>" +
            "<if test='departurepoint!=null'>departurepoint,</if>" +
            "<if test='price!=null'>price,</if>" +
            "<if test='status!=null'>status,</if>" +
            "<if test='bidid!=null'>bidid,</if>" +
            "createtime) values(" +
            "<if test='releasecompanyid!=null'>#{releasecompanyid},</if>" +
            "<if test='competecompanyid!=null'>#{competecompanyid},</if>" +
            "<if test='tenderdeclarationid!=null'>#{tenderdeclarationid},</if>" +
            "<if test='tenderitemid!=null'>#{tenderitemid},</if>" +
            "<if test='tenderpacketid!=null'>#{tenderpacketid},</if>" +
            "<if test='releasecompanyname!=null'>#{releasecompanyname},</if>" +
            "<if test='competecompanyname!=null'>#{competecompanyname},</if>" +
            "<if test='tenderdate!=null'>#{tenderdate},</if>" +
            "<if test='verifydate!=null'>#{verifydate},</if>" +
            "<if test='tendercode!=null'>#{tendercode},</if>" +
            "<if test='releaseuserid!=null'>#{releaseuserid},</if>" +
            "<if test='competeuserid!=null'>#{competeuserid},</if>" +
            "<if test='coaltype!=null'>#{coaltype},</if>" +
            "<if test='NCV!=null'>#{NCV},</if>" +
            "<if test='TM!=null'>#{TM},</if>" +
            "<if test='RS!=null'>#{RS},</if>" +
            "<if test='ADV!=null'>#{ADV},</if>" +
            "<if test='ADV02!=null'>#{ADV02},</if>" +
            "<if test='supplyamount!=null'>#{supplyamount},</if>" +
            "<if test='deliverymode!=null'>#{deliverymode},</if>" +
            "<if test='departurepoint!=null'>#{departurepoint},</if>" +
            "<if test='price!=null'>#{price},</if>" +
            "<if test='status!=null'>#{status},</if>" +
            "<if test='bidid!=null'>#{bidid},</if>" +
            " now() )" +
            "</script>")
    @Options(useGeneratedKeys = true)
    public void addMyTender(Mytender mytender);


//    @Update("<script> update mytender set " +
//            "<if test='tenderdate!=null'> tenderdate=#{tenderdate}</if>" +
//            "<if test='verifydate!=null'> ,verifydate=#{verifydate}</if>" +
//            "<if test='status!=null'> ,status=#{status}</if>" +
//            "<if test='coaltype!=null'> ,coaltype=#{coaltype}</if>" +
//            "<if test='NCV!=null'>,NCV=#{NCV}</if>" +
//            "<if test='TM!=null'>,TM=#{TM}</if>" +
//            "<if test='RS!=null'>,RS=#{RS}</if>" +
//            "<if test='ADV!=null'> ,ADV=#{ADV}</if>" +
//            "<if test='ADV02!=null'> ,ADV02=#{ADV02}</if>" +
//            "<if test='supplyamount!=null'> ,supplyamount=#{supplyamount}</if>" +
//            "<if test='deliverymode!=null'> ,deliverymode=#{deliverymode}</if>" +
//            "<if test='departurepoint!=null'> ,departurepoint=#{departurepoint}</if>" +
//            "<if test='price!=null'>,price=#{price} </if>" +
//            "where id=#{id} and competeuserid=#{competeuserid} " +
//            "</script>")
//    public int updateMyTender(Mytender mytender);

    @Select("select * from mytender where id=#{id} and competeuserid=#{userid}")
    public Mytender getMyTenderByIdUserid(@Param("id")int id,@Param("userid")int userid);

    @Update("update mytender set status=#{status} where bidid=#{bidid} and competeuserid=#{competeuserid}")
    public int updateMytenderStatusByDidId(@Param("bidid")int bidid,@Param("competeuserid")int competeuserid,@Param("status")String status);

    @Update("update mytender set status=#{status} where id=#{id} and releaseuserid=#{releaseuserid} ")
    public int updateMytenderStatusById(@Param("id")int id,@Param("releaseuserid")int releaseuserid,@Param("status")String status);

    @Select("select * from mytender where id=#{id} ")
    public Mytender getMyTenderById(@Param("id")int id);

    @Select(" select * from mytender where bidid=#{bidid} and tenderpacketid=#{tenderpacketid}")
    public List<Mytender> getMyTenderByBidIdPacket(@Param("bidid")int bidid,@Param("tenderpacketid")int tenderpacketid);

    @Select(" select * from mytender where bidid=#{bidid}")
    public List<Mytender> getMyTenderByBidId(@Param("bidid")int bidid);

    @Delete("delete from mytender where id=#{id} and competeuserid=#{userid}")
    public int deleteMyTender(@Param("id")int id, @Param("userid")int userid);

    @Delete("delete from mytender where bidid=#{bidid} and competeuserid =#{userid} ")
    public int deleteTender(@Param("bidid")int bidid ,@Param("userid")int userid);

    @Deprecated
    @Update("update mytender set status=#{status},needamount=#{needamount} where id=#{id} and releaseuserid=#{releaseuserid} ")
    public int commitSelectMytender(@Param("id")int id,@Param("releaseuserid")int releaseuserid,@Param("status")String status,@Param("needamount")BigDecimal needamount);

    @Update("update mytender set status=#{status},needamount=#{needamount} where id=#{id} and tenderdeclarationid=#{tenderdeclarationid} and releaseuserid=#{releaseuserid}")
    public int commitSelectMytender1(@Param("id")int id,@Param("releaseuserid")int releaseuserid,@Param("tenderdeclarationid")int tenderdeclarationid, @Param("status")String status,@Param("needamount")BigDecimal needamount);

    @Update("update mytender set status='MYTENDER_FAIL' where tenderdeclarationid=#{tenderdeclarationid} and status in('MYTENDER_CHOOSE_FREE','MYTENDER_CHOOSE', 'MYTENDER_WAITING_CHOOSE') and releaseuserid=#{releaseuserid} ")
    public int commitMytenderSetFail(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("releaseuserid")int releaseuserid);

    @Update("update mytender set status='MYTENDER_CANCEL' where tenderdeclarationid=#{tenderdeclarationid} and status ='MYTENDER_WAITING_CHOOSE' and releaseuserid=#{releaseuserid} ")
    public int commitMytenderSetCancel(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("releaseuserid")int releaseuserid);

    @Update("update mytender set status=#{status} where bidid=#{bidid} and competeuserid=#{competeuserid} ")
    public int updateMytenderStatus(@Param("bidid")int bidid,@Param("competeuserid")int competeuserid,@Param("status")String status);

    @Update("update mytender set status=#{status} where tenderdeclarationid=#{tenderdeclarationid} and competeuserid=#{competeuserid}")
    public int updateStatusPathBytenderid(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("status")String status,@Param("competeuserid")int competeuserid);

    @Select("select count(distinct(competeuserid)) from mytender where tenderdeclarationid=#{tenderdeclarationid}  and (status!='MYTENDER_EDIT' and status!='MYTENDER_MISSING' and status!='MYTENDER_GIVEUP' and status!='MYTENDER_CANCEL') ")
    int findCount(@Param("tenderdeclarationid")int tenderdeclarationid);


    @Select("select  c.name as name,m.`needamount` as purchaseamount  from mytender m,companies c, tenderpacket p where c.id=m.`competecompanyid` and p.id=m.tenderpacketid  and m.status='MYTENDER_SUCCEED' and  m.tenderdeclarationid=#{tenderdeclarationid}")
    List<Map<String,Object>> findDecalrBidCount(@Param("tenderdeclarationid")int tenderdeclarationid);


    @Select("select c.name as name,sum(m.`needamount`) as purchaseamount from mytender m,companies c ,tenderpacket p where c.id=m.`competecompanyid` and p.id=m.tenderpacketid and m.status='MYTENDER_SUCCEED' and m.tenderdeclarationid=#{tenderdeclarationid}  group by m.competecompanyid")
    List<Map<String,Object>> findDecalrBid(@Param("tenderdeclarationid")int tenderdeclarationid);
    //根据bid和状态查找mytender
    @Select("select * from mytender where bidid=#{bid} and status='MYTENDER_SUCCEED' order by createtime desc")
    List<Mytender> findByBidAndStatus(@Param("bid")int bid);


    @Select("select count(distinct(competecompanyid)) from mytender m,companies c, tenderpacket p where c.id=m.`competecompanyid` and p.id=m.tenderpacketid  and m.status='MYTENDER_SUCCEED' and  m.tenderdeclarationid=#{tenderdeclarationid}")
    int findBidCount(@Param("tenderdeclarationid")int tenderdeclarationid);

    @Select(" select m.* from mytender m,bid b  where m.bidid=b.id and m.`releaseuserid`=#{userid} and m.tenderpacketid=#{tenderpacketid}  and b.`paymentstatus`!='unPaidUp' and (m.status='MYTENDER_CHOOSE_FREE' or m.status='MYTENDER_CHOOSE' or m.status='MYTENDER_WAITING_CHOOSE') order by m.price")
    List<Mytender> getByPacketidAndUserid(@Param("tenderpacketid")int tenderpacketid,@Param("userid")int userid);


    @Select("select * from mytender where tenderpacketid=#{tenderpacketid} and competeuserid=#{userid}")
    List<Mytender> findMytenderList(@Param("tenderpacketid")int tenderpacketid,
                                    @Param("userid")int userid);

    @Select(" select p.id as pid,m.releasecompanyname as releasecompanyname, i.sequence as isequence,p.sequence as psequnce,m.competecompanyname as competecompanyname,m.price as price from mytender m,tenderpacket p,tenderdeclaration t,tenderitem i where " +
            "    t.id=p.tenderdeclarationid and t.id=i.`tenderdeclarationid` and m.`tenderdeclarationid`=t.id and m.status='MYTENDER_SUCCEED'  and t.id=#{tenderdeclarationid} order by i.sequence,p.sequence,m.createtime desc ")
    List<Map<String,Object>> findByPacketid(@Param("tenderdeclarationid")int tenderdeclarationid);



    @Select(" select  count(*) from mytender where  status='MYTENDER_SUCCEED'  and tenderpacketid=#{tenderpacketid} ")
    int countSupplyNum(@Param("tenderpacketid")int tenderpacketid);


    @Select("select IFNULL(sum(supplyamount), 0)  from mytender where  status='MYTENDER_SUCCEED'  and tenderdeclarationid=#{tenderdeclarationid}")
    int countSupplyAmount(@Param("tenderdeclarationid")int tenderdeclarationid);


    //查找我对同一个标包的投标
    @Select("select t3.competecompanyname ,t3.releasecompanyname ,t1.sequence as 'itemsequence' ,t2.sequence as 'packetsequence',t3.price,t3.`competecompanyid`,t3.id " +
            "from tenderitem as t1 " +
            "right join tenderpacket as t2 on t2.tenderdeclarationid=t1.tenderdeclarationid " +
            "right join mytender as t3 on t2.id=t3.tenderpacketid where t3.`id`=#{tenderid} and t1.sequence=#{itemsequence}")
    public SelectTender selectMytender(@Param("tenderid")int packetid,@Param("itemsequence")int itemsequence);

    @Update("update mytender set status=#{status} where bidid=#{bidId} and status='MYTENDER_TENDERED'")
    public void updateTenderStatusConfirm(@Param("status")TenderStatus status,@Param("bidId") int id);

    @Update("update mytender set status=#{status} where bidid=#{bidId} and status='MYTENDER_WAITING_CHOOSE'")
    public void updateTenderStatusChoose(@Param("status")TenderStatus status,@Param("bidId") int id);

    /***************************author zxy************************/

//    @Select("select * from mytender where competeuserid=#{userId} and tenderdeclarationid=#{declareId} and bidid=#{bidId} ")
//    public List<Mytender> findEditMyTender(@Param("userId")int userId,@Param("declareId") int declareId,@Param("bidId") int bidId);


    //查询标包下暂存的投标
    @Select(" select * from mytender where  tenderpacketid=#{tenderpacketid} and competeuserid=#{userId} and status=#{status}")
    public List<Mytender> findEditMyTender(@Param("userId")int userId,@Param("tenderpacketid")int tenderpacketid,@Param("status")String status);

    @Select(" select m.*, i.sequence as itemsequence, p.sequence as packetsequence from mytender m, tenderitem i, tenderpacket p where i.id=m.tenderitemid and p.id=tenderpacketid and bidid=#{bidid}")
    public List<Map<String, Object>> getMyTenderItemPackerSequenceByBidId(@Param("bidid")int bidid);

}
