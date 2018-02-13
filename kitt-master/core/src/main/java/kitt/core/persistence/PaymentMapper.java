package kitt.core.persistence;

import kitt.core.domain.Payment;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jack on 14/12/31.
 */
public interface PaymentMapper {

    //添加支付凭证
    @Insert("<script>" +
            "insert into payment(createtime, oid, orderid, userid, username, pictureurl <if test='version!=null'>, version</if>) values(now(), #{oid}, #{orderid}, #{userid}, #{username}, #{pictureurl} <if test='version!=null'>, #{version}</if>)" +
            "</script>")
    @Options(useGeneratedKeys=true)
    public int addPayment(Payment payment);

    //查询待审核支付凭证
    @Select("select * from payment where orderid=#{orderid} and version=#{version}")
    List<Payment> getPaymentList(@Param("orderid")int orderid,
                                 @Param("version")int version);

    //查询支付凭证-id
    @Select("select * from payment where id=#{id}")
    public Payment getPaymentById(int id);

    //删除支付凭证
    @Delete("delete from payment where id=#{id}")
    public int deletePaymentById(int id);

    //后台审核支付凭证
    @Update("update payment set money=#{money}, verifyman=#{verifyman}, verifymanid=#{verifymanid}, " +
            "verifytime=now(), version=2 where id=#{id} and version=1")
    int verifyPayment(@Param("id")int id,
                      @Param("money")BigDecimal money,
                      @Param("verifymanid")int verifymanid,
                      @Param("verifyman")String verifyman);

    //确认提交，确认支付凭证
    @Update("update payment set version=version+1 where id=#{id} and version=#{version}")
    int confirmPaymentById(@Param("id")int pid,
                           @Param("version")int version);

}
