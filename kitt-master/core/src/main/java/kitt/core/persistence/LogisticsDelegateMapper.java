package kitt.core.persistence;

import kitt.core.domain.LogisticsDelegate;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by zhangbolun on 16/1/24.
 */
public interface LogisticsDelegateMapper {

    @Select("select * from logisticsdelegate where id=#{id}")
    public LogisticsDelegate getDelegateBuyId(@Param("id") int id);

    @Select("<script> " +
            "select count(*) from logisticsdelegate " +
            "<where>" +
            "<if test='transportType!=null and transportType!=\"\" '> transportType=#{transportType} </if>" +
            "<if test='transportbeginDate!=null and transportbeginDate!=\"\" '> and transportDate &gt;= #{transportbeginDate} </if>" +
            "<if test='transportendDate!=null and transportendDate!=\"\" '> and transportDate &lt;= #{transportendDate} </if>" +
            "<if test='mobile!=null and mobile!=\"\" '> and mobile=#{mobile} </if>" +
            "<if test='status!=null and status!=\"\" '> and status=#{status} </if>" +
            "</where> " +
            "</script>")
    public int countLogisIntention(@Param("status") String status,
                                   @Param("transportType") String transportType,
                                   @Param("transportbeginDate") String transportbeginDate,
                                   @Param("transportendDate") String transportendDate,
                                   @Param("mobile") String mobile);


    @Select("<script>select * from logisticsdelegate" +
            "<where>" +
            "<if test='transportType!=null and transportType!=\"\" '> transportType=#{transportType} </if>" +
            "<if test='transportbeginDate!=null and transportbeginDate!=\"\" '> and transportDate &gt;= #{transportbeginDate} </if>" +
            "<if test='transportendDate!=null and transportendDate!=\"\" '> and transportDate &lt;= #{transportendDate} </if>" +
            "<if test='mobile!=null and mobile!=\"\" '> and mobile=#{mobile} </if>" +
            "<if test='status!=null and status!=\"\" '> and status=#{status} </if>" +
            "</where>" +
            " order by createtime desc limit #{limit} offset #{offset} " +
            "</script>")
    public List<LogisticsDelegate> getPageIntention(@Param("status") String status,
                                                    @Param("transportType") String transportType,
                                                    @Param("transportbeginDate") String transportbeginDate,
                                                    @Param("transportendDate") String transportendDate,
                                                    @Param("mobile") String mobile,
                                                    @Param("limit") int limit,
                                                    @Param("offset") int offset);

    public default Pager<LogisticsDelegate> getLogisticsDelegateList(String status,String transportType,String transportbeginDate,String transportendDate,String mobile,int page, int pagesize){
        return Pager.config(this.countLogisIntention(status,transportType,transportbeginDate,transportendDate,mobile), (int limit, int offset) -> this.getPageIntention(status,transportType,transportbeginDate,transportendDate,mobile,limit, offset))
                .page(page, pagesize);
    }


    @Insert("<script>" +
            "insert into logisticsdelegate(transportType,goodsType,goodsWeight,transportDate,mobile,status"+
            "<if test='remark!=null'>,remark</if>" +
            ",createtime) values(#{transportType},#{goodsType},#{goodsWeight},#{transportDate},#{mobile},#{status}"+
            "<if test='remark!=null'>,#{remark}</if>" +
            ",now())" +
            "</script>")
    @Options(useGeneratedKeys = true,keyProperty="id")
    public boolean addLogisticsDelegate(LogisticsDelegate delegate);

    @Update("update logisticsdelegate set suggest=#{suggest}, status=#{status} where id=#{id} ")
    public int updatedelegateStatus(@Param("id")int id,@Param("suggest")String suggest,@Param("status")String status);





}
