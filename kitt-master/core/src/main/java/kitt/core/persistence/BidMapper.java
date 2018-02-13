package kitt.core.persistence;

import kitt.core.domain.Bid;
import kitt.core.domain.TenderStatus;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by lich on 15/11/19.
 */
public interface BidMapper {
    @Delete("delete from bid where id=#{bidid}")
    public int deleteBidById(@Param("bidid") int bidid);

    //查询当前用户的某一个公告的投标
    @Select("select b.* from bid b inner join tenderdeclaration td on td.id=b.tenderdeclarationid  where b.id=#{bidId} and td.userid=#{userId} ")
    public Bid findById(@Param("userId")int userId,@Param("bidId")int paymentId);

    @Select("select * from bid where tenderdeclarationid=#{tenderdeclarationid}")
    List<Bid> getBidListByTenderDeclarationid(int tenderdeclarationid);

    @Select("select * from bid where tenderdeclarationid=#{tenderdeclarationid} and userid=#{userid} ")
    List<Bid> findByTenderId(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("userid")int userid);

    @Select("select * from bid where tenderdeclarationid=#{tenderdeclarationid}")
    List<Bid> findBidByTenderId(@Param("tenderdeclarationid")int tenderdeclarationid);

    @Select("select * from bid where id=#{id}")
    Bid findBidById(@Param("id")int id);

    //审核通过支付凭证
    @Update("update bid set mytenderstatus=#{status} where id=#{id} and mytenderstatus='MYTENDER_TENDERED' ")
    public void updateTenderStatusConfirm(@Param("status")TenderStatus status,@Param("id")int id);

    @Update("update bid set mytenderstatus=#{status} where id=#{id}  ")
    public void updateBidStatus(@Param("status")TenderStatus status,@Param("id")int id);

    @Update("update bid set mytenderstatus=#{status} where id=#{id} and mytenderstatus='MYTENDER_WAITING_CHOOSE' ")
    public void updateTenderStatusChoose(@Param("status")TenderStatus status,@Param("id")int id);

    @Insert("insert into bid(id,tenderdeclarationid,userid,mytenderstatus,paymentstatus,supplyamount,attachmentpath,attachmentfilename,needpay,createtime) values(#{id},#{tenderdeclarationid},#{userid},#{mytenderstatus},#{paymentstatus},#{supplyamount},#{attachmentpath},#{attachmentfilename},#{needpay},#{createtime})")
    public void addBidWithId(Bid bid);

    @Update("update bid set mytenderstatus=#{mytenderstatus} where id=#{id}")
    public int updateBidStatusById(@Param("id")int id,@Param("mytenderstatus")String mytenderstatus);

    @Update("update bid set mytenderstatus=#{mytenderstatus},attachmentpath=#{attachmentpath} where userid=#{userid} and tenderdeclarationid=#{tenderdeclarationid}")
    public int updateBidStatusByUserId(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("userid")int userid,@Param("mytenderstatus")String mytenderstatus,@Param("attachmentpath")String attachmentpath);

//    @Update("update bid set mytenderstatus=#{mytenderstatus},attachmentpath=#{attachmentpath},attachmentfilename=#{attachmentfilename},supplyamount=#{supplyamount} where userid=#{userid} and id=#{id}")
//    public int updateBidStatusByBidId(@Param("id")int id,@Param("userid")int userid,@Param("mytenderstatus")String mytenderstatus,@Param("attachmentpath")String attachmentpath,@Param("attachmentfilename")String attachmentfilename,@Param("supplyamount")BigDecimal supplyamount);

    @Update("update bid set supplyamount=#{supplyamount} where id=#{id}")
    public int updateBidAmountById(@Param("id")int id,@Param("supplyamount")BigDecimal supplyamount);

    @Select("select * from bid where tenderdeclarationid=#{tenderdeclarationid} and userid=#{userid} and mytenderstatus=#{mytenderstatus}")
    public Bid getBidByDeclarIdUserid(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("userid")int userid,@Param("mytenderstatus")String mytenderstatus);

    //获取可以修改的正式的投标
    @Select("select * from bid where tenderdeclarationid=#{tenderdeclarationid} and userid=#{userid} and mytenderstatus in ('MYTENDER_TENDERED_CONFIRM','MYTENDER_TENDERED_FREE','MYTENDER_TENDERED')")
    public Bid getBidByStatus(@Param("tenderdeclarationid")int tenderdeclarationid, @Param("userid")int userid);

    @Select("select * from bid where tenderdeclarationid=#{tenderdeclarationid} and userid=#{userid} and mytenderstatus in ('MYTENDER_EDIT','MYTENDER_TENDERED_FREE','MYTENDER_TENDERED','MYTENDER_TENDERED_CONFIRM')")
    public  List<Bid> getBidByTenderidUserid(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("userid")int userid);

    @Select("select * from bid where tenderdeclarationid=#{tenderdeclarationid} and userid=#{userid} and mytenderstatus in {'MYTENDER_TENDERED_FREE', 'MYTENDER_TENDERED', 'MYTENDER_TENDERED_CONFIRM'}")
    public List<Bid> getActiveBidByIdUserid(@Param("id")int id,
                                            @Param("userid")int userid);

    @Select("select * from bid where id=#{id} and userid=#{userid}")
    public Bid getBidByIdUserid(@Param("id")int id, @Param("userid")int userid);

    @Select("select * from bid where id=#{id}")
    public Bid getBidById(@Param("id")int id);

    @Delete("delete from bid where id=#{id} and userid=#{userid}")
    public int deleteBid(@Param("id")int id,@Param("userid")int userid);

    @Insert("insert into bid(tenderdeclarationid,userid,mytenderstatus,paymentstatus,supplyamount,attachmentpath,attachmentfilename,needpay,createtime) values(#{tenderdeclarationid},#{userid},#{mytenderstatus},#{paymentstatus},#{supplyamount},#{attachmentpath},#{attachmentfilename},#{needpay},now())")
    @Options(useGeneratedKeys = true)
    public void addBid(Bid bid);

    @Update("update bid set mytenderstatus='MYTENDER_CANCEL' where mytenderstatus ='MYTENDER_WAITING_CHOOSE' and tenderdeclarationid=#{tenderdeclarationid} ")
    public int setBidCancel(@Param("tenderdeclarationid")int tenderdeclarationid);

    @Update("update bid set paymentstatus=#{status} where id=#{id}  ")
    public int updateStatusById(@Param("id")int id,@Param("status")String status);

    @Select("select count(*) from bid where tenderdeclarationid=#{tenderdeclarationid}  and (mytenderstatus!='MYTENDER_EDIT' and mytenderstatus !='MYTENDER_MISSING' and mytenderstatus!='MYTENDER_GIVEUP' and mytenderstatus!='MYTENDER_CANCEL') ")
    int findBidTbCount(@Param("tenderdeclarationid")int tenderdeclarationid);

    //根据招标公告id, 状态查询待选标人数
    @Select("select count(1) from bid where tenderdeclarationid=#{tenderdeclarationid} and mytenderstatus in (#{status1}, #{status2})")
    int findBidCountByStatusTenderDeclarationId(@Param("tenderdeclarationid")int tenderdeclaration,
                                                @Param("status1")String status1,
                                                @Param("status2")String status2);

    //根据招标公告id查询待审核支付凭证的投标人数
    @Select("select count(1) from bid where tenderdeclarationid=#{tenderdeclarationid} and paymentstatus=#{paymentstatus}")
    int findBidCountByPaymentStatusTenderDeclarationId(@Param("tenderdeclarationid")int tenderdeclaration,
                                                       @Param("paymentstatus")String paymentstatus);

    //查询暂存的投标
    @Select("select * from bid where userid=#{userId} and tenderdeclarationid=#{declareId} and mytenderstatus='MYTENDER_EDIT' limit 1 ")
    public Bid findEditBid(@Param("userId")int userId,@Param("declareId") int declareId);

    @Select("<script>update  bid <set > " +
            "<if test='userid!=null'>userid=#{userid},</if> " +
            "<if test='tenderdeclarationid!=null'>tenderdeclarationid=#{tenderdeclarationid},</if>" +
            "<if test='mytenderstatus!=null'>mytenderstatus=#{mytenderstatus},</if>" +
            "<if test='paymentstatus!=null'>paymentstatus=#{paymentstatus},</if>" +
            "<if test='supplyamount!=null'>supplyamount=#{supplyamount},</if>" +
            "<if test='attachmentpath!=null'>attachmentpath=#{attachmentpath},</if>" +
            "<if test='attachmentfilename!=null'>attachmentfilename=#{attachmentfilename},</if>" +
            " </set> where id=#{id} </script>")
    public void updateBid(Bid bid);

    //修改bid状态为取消
    @Update("update bid set mytenderstatus=#{status} where tenderdeclarationid=#{delcareId} and userid=#{userId}")
    public void updateBidStatusByDelcareId(@Param("status") TenderStatus tenderStatus,@Param("delcareId") int declareId,@Param("userId")int userId);

    //查询公告下的所有投标信息
    @Select("select id,paymentstatus from  bid where tenderdeclarationid=#{tenderDecId}")
    public  List<Bid> findTenderInTenderDec(@Param("tenderDecId") int tenderDecId);

    /**
     * @param userId   投标用户Id
     * @param bidId 标包Id
     */
    @Update("update bid set paymentstatus= #{status},verifytime=now() where id=#{bidId} and userid=#{userId}  ")
    public void updateBidStatusUseridBidid(@Param("status") TenderStatus status, @Param("userId") int userId, @Param("bidId") int bidId);

    @Update("update bid set mytenderstatus= #{status} where id=#{bidId} and userid=#{userId}  ")
    public void updateBidMytenderStatus(@Param("status") TenderStatus status, @Param("userId") int userId, @Param("bidId") int bidId);

    @Select("select count(1) from bid where tenderdeclarationid=#{tenderdeclarationid}")
    int getBidCountByDeclarationId(@Param("tenderdeclarationid") int tenderdeclarationid);

    @Select("select b.id, b.mytenderstatus as status, b.createtime, c.name as companyname from bid b, companies c where b.userid=c.userid and b.tenderdeclarationid=#{tenderdeclarationid} limit #{limit} offset #{offset}")
    List<Map<String, Object>> getBidAndCompanyListByTenderDeclarationId(@Param("tenderdeclarationid") int tenderdeclarationid,
                                                                        @Param("limit")int limit,
                                                                        @Param("offset")int offset);

    public default Pager<Map<String, Object>> pageBidAndCompanyListByTenderDeclarationId(int tenderDeclarationId, int page, int pagesize){
        return Pager.config(this.getBidCountByDeclarationId(tenderDeclarationId), (int limit, int offset) -> this.getBidAndCompanyListByTenderDeclarationId(tenderDeclarationId, limit, offset))
                .page(page, pagesize);
    }

}
