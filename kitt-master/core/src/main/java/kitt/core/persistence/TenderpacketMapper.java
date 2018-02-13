package kitt.core.persistence;

import kitt.core.domain.Company;
import kitt.core.domain.TenderPacket;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 15/11/10.
 */
public interface TenderpacketMapper {

    @Insert("insert into tenderpacket(" +
            "tenderdeclarationid,tenderitemid,sequence,tenderpacket,coaltype,NCV,ADV02," +
            "TM,RS,ADV,purchaseamount,highestprice," +
            "minamount,deliverymode,userid,version,createtime) values(" +
            "#{tenderdeclarationid},#{tenderitemid},#{sequence},#{tenderpacket},#{coaltype},#{NCV},#{ADV02}," +
            "#{TM},#{RS},#{ADV},#{purchaseamount},#{highestprice}," +
            "#{minamount},#{deliverymode},#{userid},0,now() )")
    @Options(useGeneratedKeys = true)
    public void addTenderPacket(TenderPacket packet);

    @Update("update tenderpacket set " +
            "tenderitemid=#{tenderitemid},sequence=#{sequence},tenderpacket=#{tenderpacket},coaltype=#{coaltype},NCV=#{NCV},ADV02=#{ADV02}," +
            "TM=#{TM},RS=#{RS},ADV=#{ADV},purchaseamount=#{purchaseamount},highestprice=#{highestprice}," +
            "minamount=#{minamount},deliverymode=#{deliverymode},version=version+1,createtime=now() " +
            " where id=#{id} and version=#{version} and userid=#{userid} ")
    public int updateTenderPacket(TenderPacket packet);

    @Update("update tenderpacket set  where tenderdeclarationid=#{tenderdeclarationid} and userid=#{userid} ")
    public int updateStatusByDeclarId(@Param("tenderdeclarationid")int tenderdeclarationid);

    @Delete("delete from tenderpacket where tenderdeclarationid=#{tenderdeclarationid} and userid=#{userid} ")
    public int deletePacketbydeclarid(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("userid")int userid);

    @Delete("delete from tenderpacket where id=#{id}")
    public int deletePacketbyId(@Param("id")int id);

    @Select("select * from companies where id=#{id}")
    Company findCNameById(@Param("id")int id);

    @Select("select * from tenderpacket where tenderitemid=#{tenderitemid}")
    List<TenderPacket> findTendpackgeByItemId(int tenderitemid);

    @Select("select * from tenderpacket where tenderdeclarationid=#{tenderdeclarationid}")
    List<TenderPacket> findTendpackgeByDecalId(int tenderdeclarationid);

    @Select("select * from tenderpacket where tenderdeclarationid=#{tenderdeclarationid} order by sequence")
    TenderPacket findTendpackgeById(@Param("id")int id);

    @Select("select * from tenderpacket where id=#{id}")
    TenderPacket getTendpackgeById(@Param("id")int id);

    @Select("select * from tenderpacket where tenderdeclarationid=#{tenderdeclarationid}")
    List<TenderPacket> findTendpackgeByDeclarId(@Param("tenderdeclarationid")int tenderdeclarationid);


    @Select("<script>select * from tenderpacket where tenderdeclarationid=#{tenderdeclarationid} " +
            "<if test='NCV1!=0 and NCV2!=0'> and NCV between #{NCV1} and #{NCV2}</if> " +
            "<if test='lit!=0'> and NCV <![CDATA[ <=]]>  #{lit}</if>" +
            "<if test='large!=0'> and NCV  <![CDATA[ >=]]>  #{large}</if>" +
            "order by sequence</script>")
    List<TenderPacket> findTendpackgeByDeclarId1(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("NCV1")int NCV1,@Param("NCV2")int NCV2,@Param("lit")int lit,@Param("large")int large);

    @Select("<script>select * from tenderpacket where tenderitemid=#{tenderitemid} " +
            "<if test='coaltype!=null and coaltype!=\"\"'> and coaltype=#{coaltype}</if> " +
            "<if test='NCV1!=0 and NCV2!=0'> and NCV between #{NCV1} and #{NCV2}</if>" +
            "<if test='lit!=0'> and NCV <![CDATA[ <=]]>   #{lit}</if>" +
            "<if test='large!=0'> and NCV  <![CDATA[ >=]]>  #{large}</if>" +
            "</script>")
    List<TenderPacket> findTendpackgeByConditions(@Param("tenderitemid")int tenderitemid,@Param("coaltype")String coaltype,@Param("NCV1")int NCV1,@Param("NCV2")int NCV2,@Param("lit")int lit,@Param("large")int large);
}
