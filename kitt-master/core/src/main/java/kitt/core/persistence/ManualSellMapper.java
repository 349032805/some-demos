package kitt.core.persistence;

import kitt.core.domain.ManualSell;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 14-12-17.
 */
public interface ManualSellMapper {

    @Insert("<script>insert into manualsell (<if test='userId!=0'>userId,</if>lowcalorificvalue,deliverystartdate,deliveryenddate,demandamount,deliverydistrict,deliveryprovince,deliveryaddr,deliverymode,contactName,phone,companyName,type,deliveryotherplace,receivebasissulfur,airdrybasisvolatile,coalType,createdatetime, clienttype,manualsellid)" +
            "values (<if test='userId!=0'>#{userId},</if>#{lowcalorificvalue},#{deliveryStartDate},#{deliveryEndDate},#{demandAmount},#{deliveryDistrict},#{deliveryProvince},#{deliveryAddr},#{deliveryMode},#{contactName},#{phone},#{companyName},#{type},#{deliveryOtherPlace},#{receivebasissulfur},#{airdrybasisvolatile},#{coalType},#{createDatetime}, #{clienttype}, case when #{type} = 1 then dateseq_next_value('ZH') else dateseq_next_value('XS')end)</script>")
    @Options(useGeneratedKeys=true, keyProperty="id")
    public int save(ManualSell sell);

    final String sql = "<where>" +
            "<if test='userId!=null'> userId = #{userId}</if>" +
            "<if test='manualsellType!=null'> and type = #{manualsellType}</if>" +
            "<if test='port!=null'> and deliveryaddr = #{port}</if>"+
            "<if test='dateCondition!=null'> and createdatetime between  #{dateCondition} and  DATE_FORMAT(now(),'%Y-%m-%d')</if>" +
            "</where>";

    @Select("<script> select * from manualsell " + sql + "and isdelete=false order by id limit #{pageSize} offset #{indexNum} </script>")
    public List<ManualSell> list(@Param("userId") Integer userId, @Param("manualsellType") Boolean manualsellType, @Param("dateCondition") LocalDate dateCondition, @Param("pageSize") int pageSize, @Param("indexNum") int indexNum, @Param("port") String port);

    @Select("<script>select count(1) from manualsell"+sql+"and isdelete=false </script>")
    public int count(@Param("userId") Integer userId, @Param("manualsellType") boolean type, @Param("dateCondition") LocalDate dateCondition, @Param("port") String port);



    @Update("update manualsell set isdelete=true where manualsellid=#{manualsellId} and userid=#{userId}")
    public void deleteManualSellById(@Param("manualsellId")String manualsellId,@Param("userId")int userid);

    @Select("select * from manualsell where  userid=#{userId} and manualsellid =#{manualsellId} and isdelete=false ")
    public ManualSell loadByUserIdandManualId(@Param("userId") int userId, @Param("manualsellId") String manualsellId);
    @Select("select * from manualsell where  manualsellid =#{manualsellId} and isdelete=false")
    public ManualSell loadByManualId(@Param("manualsellId") String manualsellId);
    @Select("select * from manualsell where id=#{id}")
    public ManualSell getManualSellById(int id);

    /*********************admin后台*****************************/

    final String dynamicSql = "<where>" +
            "<if test='map!=null'>" +
            "<if test='map.userId!=null and map.userId!=0'> userId=#{map.userId}</if>" +
            "<if test='map.type!=null'> and type=#{map.type}</if>" +
            "<if test='map.status!=null'> and status=#{map.status}</if>" +
            "<if test='map.clienttype!=0 and map.clienttype!=null'> and clienttype=#{map.clienttype}</if>" +
            "<if test='map.deliveryDistrict!=null'> and deliverydistrict=#{map.deliveryDistrict}</if>"+
            "<if test='map.deliveryProvince!=null'> and deliveryprovince=#{map.deliveryProvince}</if>"+
            "<if test='map.deliveryAddr!=null'> and deliveryaddr=#{map.deliveryAddr}</if>"+
            "<if test='map.deliveryStartDate!=\"\" and map.deliveryStartDate!=null and map.deliveryEndDate !=\"\" and map.deliveryEndDate!=null'> and (createdatetime between #{map.deliveryStartDate} and #{map.deliveryEndDate})</if>" +
            "<if test='map.deliveryStartDate!=\"\" and map.deliveryStartDate!=null and (map.deliveryEndDate==null or map.deliveryEndDate==\"\")'> and (createdatetime between #{map.deliveryStartDate} and DATE_FORMAT(now(),'%Y-%m-%d'))</if>" +
            "</if>" +
            "and isdelete=0 " +
            "</where>";
    @Select("<script> select * from manualsell " + dynamicSql + " order by createdatetime desc limit #{pageSize} offset #{indexNum} </script>")
    public List<ManualSell> listAllManualsell(@Param("map") Map<String, Object> map,
                                              @Param("pageSize") int pageSize,
                                              @Param("indexNum") int indexNum);

    @Select("<script>select count(1) from manualsell " + dynamicSql + "</script>")
    public int countAllManualsell(@Param("map") Map<String, Object> map);

    public default Pager<ManualSell> getAllManualSellList(Map<String, Object> map, int page, int pagesize){
        return Pager.config(this.countAllManualsell(map), (int limit, int offset) -> this.listAllManualsell(map,limit, offset))
                .page(page, pagesize);
    }

    //处理 manualsell 单子
    @Update("update manualsell set status='Solved' where status='ToBeSolved' and id=#{id}")
    int solveManualSellById(int id);

    //添加 处理 manualsell 单子的信息
    @Update("update manualsell set solvedmanid=#{solvedmanid}, solvedmanusername=#{solvedmanusername}, solvedremarks=#{solvedremarks} where id=#{id} and status='Solved'")
    public int updateManualAddSolvedInfo(@Param("solvedmanid")int solvedmanid,
                                         @Param("solvedmanusername")String solvedmanusername,
                                         @Param("solvedremarks")String solvedremarks,
                                         @Param("id")int id);
}
