package kitt.core.persistence;

import kitt.core.domain.Bid;
import kitt.core.domain.Mytender;
import kitt.core.domain.TenderPayment;
import kitt.core.domain.TenderStatus;
import kitt.core.util.PageQueryParam;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 15/11/13.
 */
public interface TenderPayMentMapper {

    /**
     *
     * @param declareId 公告id
     * @param userId   发布招标用户Id
     * @param status    状态
     * @return
     */
    //旧版本需要上传支付凭证
//    @Select("<script>SELECT count(p.id)  FROM bid b  inner  join  tenderpayment  p  on p.bidid=b.id inner join tenderdeclaration td on td.id=b.tenderdeclarationid  " +
//            "where  b.tenderdeclarationid=#{declareId}  and td.userid=#{userId} " +
//            "<choose>" +
//            "<when test='paymentstatus==\"unPaidUp\"'>  and(b.paymentstatus='unPaidUp' or p.auditstatus is not null)</when>" +
//            "<otherwise> and b.paymentstatus=#{paymentstatus} and p.auditstatus is null</otherwise>"+
//            "</choose>"+
//            "</script>")
        @Select("<script>SELECT count(b.id)  FROM bid b   inner join tenderdeclaration td on td.id=b.tenderdeclarationid  " +
            "where  b.tenderdeclarationid=#{declareId}  and td.userid=#{userId} and b.mytenderstatus not in('MYTENDER_EDIT','MYTENDER_MISSING','MYTENDER_GIVEUP') "+
                "<choose>" +
                "<when test='paymentstatus==\"waitVerify\"'>  and(b.paymentstatus='waitVerify' or b.paymentstatus is null)</when>" +
                "<otherwise> and b.paymentstatus=#{paymentstatus} </otherwise>" +
                "</choose>"+
            "</script>")
    public int countByUserId(@Param("declareId") Integer declareId, @Param("userId") int userId, @Param("paymentstatus") String status);


//    @Select("<script>select * from (select  p.id,b.paymentstatus, c.name companyName,p.createtime,p.money,p.verifytime,td.margins,p.pic1,p.pic2,p.pic3 from tenderpayment p " +
//            "inner join bid b on b.id=p.bidid inner join companies c  on c.userid=b.userid " +
//            "inner join tenderdeclaration td on td.id=b.tenderdeclarationid "+
//            "where b.tenderdeclarationid=#{declareId}   and td.userid=#{userId} " +
//            "<choose>" +
//            "<when test='paymentstatus==\"waitVerify\"'> and (b.paymentstatus='waitVerify' or b.paymentstatus is null) </when>"+
//            "<otherwise> and b.paymentstatus=#{paymentstatus}<otherwise>"+
//            "</choose>"+
//            " limit #{page.pagesize} offset #{page.indexNum} ) t </script>")
    @Select("<script>select b.id,b.paymentstatus,b.needpay,c.name companyName,b.createtime,b.verifytime,td.margins from  bid b" +
            "  inner join companies c  on c.userid=b.userid " +
            "inner join tenderdeclaration td on td.id=b.tenderdeclarationid "+
            "where b.tenderdeclarationid=#{declareId}   and td.userid=#{userId} and b.mytenderstatus not in('MYTENDER_EDIT','MYTENDER_MISSING','MYTENDER_GIVEUP') " +
            "<choose>" +
            "<when test='paymentstatus==\"waitVerify\"'> and (b.paymentstatus='waitVerify' or b.paymentstatus is null) </when>"+
            "<otherwise> and b.paymentstatus=#{paymentstatus}</otherwise>"+
            "</choose>"+
            " limit #{page.pagesize} offset #{page.indexNum} </script>")
    public List<Bid> findAllByUserId(@Param("page") PageQueryParam pageQueryParam, @Param("declareId") Integer declareId,@Param("userId") int userId, @Param("paymentstatus") String status);


    @Select("select b.paymentstatus,b.id from bid b inner join tenderdeclaration td on td.id=b.tenderdeclarationid where td.userid=#{userId} and td.id=#{declareId} and  b.mytenderstatus not in('MYTENDER_EDIT','MYTENDER_MISSING','MYTENDER_GIVEUP')  ")
    public List<Bid> findBidByDeclare(@Param("userId")int userId,@Param("declareId")int declareId);

    @Select("select count(p.id) from bid b inner join tenderdeclaration td on td.id=b.tenderdeclarationid inner join tenderpayment p on p.bidid=b.id where td.userid=#{userId} and td.id=#{declareId} and p.auditstatus is not null  ")
    public int countfailPayment(@Param("userId")int userId,@Param("declareId")int declareId);


    @Select("select  pic1,pic2,pic3 from tenderpayment where  id=#{paymentId} ")
    public Map<String,String> findPathByPaymentId(@Param("paymentId") int tenderId);


    @Insert("<script>insert into tenderpayment(" +
            "bidid,userid,username," +
            "<if test='pic1!=null and pic1!=\"\"'>pic1,</if>" +
            "<if test='pic2!=null and pic2!=\"\"'>pic2,</if>" +
            "<if test='pic3!=null and pic3!=\"\"'>pic3,</if>" +
            "createtime) values(" +
            "#{bidid},#{userid},#{username}," +
            "<if test='pic1!=null and pic1!=\"\"'>#{pic1},</if>" +
            "<if test='pic2!=null and pic2!=\"\"'>#{pic2},</if>" +
            "<if test='pic3!=null and pic3!=\"\"'>#{pic3},</if>" +
            "#{createtime})</script>")
    public void addTenderpayment(@Param("bidid") int bidid,@Param("userid") int userid,@Param("username") String username,@Param("createtime") LocalDateTime createtime,@Param("pic1") String pic1,@Param("pic2") String pic2,@Param("pic3") String pic3);

    @Delete("delete from tenderpayment where bidid=#{bidid} and (IFNULL(auditstatus, '')!='fail')")
    public int deleteTenderpayment(@Param("bidid") int bidid);

    @Update("update  tenderpayment set auditstatus='fail' where  bidid=#{bidid} and (IFNULL(auditstatus, '')!='fail')")
    public int updateTenderpmt(@Param("bidid") int bidid);

    @Delete("delete from tenderpayment where bidid=#{bidid}")
    public int deleteTpayment(@Param("bidid") int bidid);


    @Update("update tenderpayment set auditstatus=#{auditstatus} where bidid=#{bidid} and (IFNULL(auditstatus, '')!='fail') ")
    public void updateTpaymentdBy(@Param("auditstatus")String auditstatus,@Param("bidid")int bidid);

    @Update("update tenderpayment set pictureurl=#{pictureurl},userid=#{userid},username=#{username} where id=#{id}")
    public int updateTenderpayment(@Param("pictureurl") String pictureurl, @Param("userid") int userid, @Param("username") String username, @Param("id") int id);

    @Update("update tenderpayment set pic1=#{pic1},pic2=#{pic2},pic3=#{pic3} where bidid=#{bidid}")
    public int updatePayment(@Param("pic1") String pic1, @Param("pic2") String pic2, @Param("pic3") String pic3, @Param("bidid") int bidid);



    @Update("update tenderpayment set verifytime=now() where id=#{value}")
    public void updatePaymentVerifydate(int id);

    @Select("select * from mytender where id=#{value}")
    public Mytender findById(int id);

}
