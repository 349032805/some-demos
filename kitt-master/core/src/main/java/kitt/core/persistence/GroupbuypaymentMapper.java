package kitt.core.persistence;

import kitt.core.domain.GroupBuyPayment;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhangbolun on 15/8/11.
 */
public interface GroupbuypaymentMapper {
    @Select("select * from groupbuypayment where id=#{id} and isdelete=false")
    public GroupBuyPayment getGroupBuyPaymentById(@Param("id")int id);

    @Select("select * from groupbuypayment where qualificationcode=#{qualificationcode} and userid=#{userid} and isdelete=false")
    public List<GroupBuyPayment> getgetGroupBuyPaymentByCodeUid(@Param("userid")int userid,@Param("qualificationcode")String qualificationcode);

    @Update("update groupbuypayment set status=#{status} where id =#{id} and isdelete=false")
    public  void updateStatus(@Param("id")int id,@Param("status")String status);

    @Update("update groupbuypayment set amount=#{amount} where id =#{id} and isdelete=false")
    public void updateAmout(@Param("id")int id,@Param("amount")BigDecimal amount);

    @Update("update groupbuypayment set isdelete=true where id =#{id}")
    public void deleteLogic(@Param("id")int id);

    @Delete("delete from groupbuypayment where isdelete=true")
    public void deleteGroupbuypayment();
}