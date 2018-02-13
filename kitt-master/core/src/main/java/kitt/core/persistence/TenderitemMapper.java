package kitt.core.persistence;

import kitt.core.domain.TenderItem;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 15/11/10.
 */
public interface TenderitemMapper {

    @Insert("<script>" +
            "insert into tenderitem(" +
            "tenderdeclarationid,purchasecontent,item,sequence,receiptunits," +
            "purchaseamount,arriveplace,userid,version,createtime) values(" +
            "#{tenderdeclarationid},#{purchasecontent},#{item},#{sequence},#{receiptunits}," +
            "#{purchaseamount},#{arriveplace},#{userid},0,now() )" +
            "</script>")
    @Options(useGeneratedKeys = true)
    public void addTenderItem(TenderItem tenderItem);

    @Delete("delete from tenderitem where tenderdeclarationid=#{tenderdeclarationid} and userid=#{userid} ")
    public int deleteItemByDeclarId(@Param("tenderdeclarationid")int tenderdeclarationid, @Param("userid")int userid);

    @Select("select * from tenderitem where tenderdeclarationid=#{tenderdeclarationid}")
    List<TenderItem> findTendItemByDecalarId(int tenderdeclarationid);

    /**
     *  导出公告下的所有投标
     * @param userId  用户id
     * @param declareId  公告id
     * @return
     * 
     */
    @Select("select mt.id,ti.receiptunits, ti.sequence projectId, tp.sequence packetId,c.name companyName,mt.coaltype," +
            "mt.NCV,mt.RS,mt.ADV,mt.ADV02,mt.TM,mt.supplyamount,mt.deliverymode,mt.price from bid b inner " +
            "join tenderdeclaration td on td.id=b.tenderdeclarationid inner join mytender mt on mt.bidid=b.id " +
            "inner join tenderitem ti on ti.id=mt.tenderitemid inner join tenderpacket tp on tp.id=mt.tenderpacketid " +
            "inner join companies c on c.id=mt.competecompanyid where td.userid=#{userId} and td.id=#{declareId} and (b.paymentstatus='paidUp' " +
            "or td.margins=0) group by  mt.id  order by projectid,packetid,mt.price ")
    public  List<Map<String,Object>> findTenderInDeclare(@Param("userId")int userId,@Param("declareId")int declareId);


    //导出公告下的所有中标
    @Select("select  mt.id, ti.receiptunits,mt.needamount,ti.sequence projectId, tp.sequence packetId,c.name companyName, " +
            "mt.supplyamount,mt.price,mt.deliverymode from bid b " +
            "inner join tenderdeclaration td on td.id=b.tenderdeclarationid " +
            "inner join mytender mt on mt.bidid=b.id " +
            " inner join tenderitem  ti on ti.id=mt.tenderitemid " +
            "inner join tenderpacket  tp on tp.id=mt.tenderpacketid " +
            "inner join companies c on c.userid=b.userid " +
            "where  td.userid=#{userId} and td.id=#{declareId} and mt.status='MYTENDER_SUCCEED' group by mt.id order by  ti.sequence,tp.sequence,mt.price")
    public  List<Map<String,Object>> findWinTenderItem(@Param("userId")int userId,@Param("declareId")int declareId);

}
