package kitt.core.mapper;

import org.apache.ibatis.annotations.*;
import kitt.core.entity.Mydemand;

/**
 * Created by liuxinjie on 16/2/29.
 */
public interface MyDemandMapper {

    /**
     * 删除我的需求
     */
    @Delete("delete from mydemands where demandcode=#{demandcode} and userid=#{userid}")
    int deleteMyDemandByDemandcode(@Param("demandcode") String demandcode);

    //根据需求编号获取我的需求
    @Select("select * from mydemands where demandcode=#{demandcode}")
    public Mydemand getMydemandByDemandcode(String demandcode);

    //添加我的需求
    @Insert("insert into mydemands(userid,demandcode,releasetime,quoteenddate,demandamount,status," +
            "deliverydate,deliverydatestart,deliverydateend) values(" +
            "#{userid},#{demandcode},#{releasetime},#{quoteenddate},#{demandamount},#{status}," +
            "#{deliverydate},#{deliverydatestart},#{deliverydateend})")
    public void addMydemand(Mydemand mydemand);

    //修改我的需求
    @Update("update mydemands set releasetime=#{releasetime},quoteenddate=#{quoteenddate},demandamount=#{demandamount},status=#{status}," +
            "deliverydate=#{deliverydate},deliverydatestart=#{deliverydatestart},deliverydateend=#{deliverydateend}" +
            " where demandcode=#{demandcode}")
    public void modifyMyDemand(Mydemand mydemand);
}
