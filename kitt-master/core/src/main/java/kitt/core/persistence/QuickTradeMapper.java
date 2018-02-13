package kitt.core.persistence;

import kitt.core.domain.QuickTrade;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 15/9/24.
 */
public interface QuickTradeMapper {

  @Select("select * from quicktrade where id=#{value}")
  public QuickTrade findById(int id);


  final String dynamicSql = "<where>" +
    "<if test='map!=null'>" +
    "<if test='map.code!=null and map.code!=\"\"'> and (code like \"%\"#{map.code}\"%\" or content like \"%\"#{map.code}\"%\")</if>" +
    "<if test='map.status!=null and map.status!=0'> and status=#{map.status}</if>" +
    "<if test='map.clientType!=null and map.clientType!=0'> and clienttype=${map.clientType}</if>" +
    "<if test='map.deliveryStartDate!=\"\" and map.deliveryStartDate!=null and map.deliveryEndDate !=\"\" and map.deliveryEndDate!=null'> and ( DATE_FORMAT(createtime,'%Y-%m-%d') between #{map.deliveryStartDate} and #{map.deliveryEndDate})</if>" +
    "<if test='map.deliveryStartDate!=\"\" and map.deliveryStartDate!=null and (map.deliveryEndDate==null or map.deliveryEndDate==\"\")'> and (DATE_FORMAT(createtime,'%Y-%m-%d') between #{map.deliveryStartDate} and DATE_FORMAT(now(),'%Y-%m-%d'))</if>" +
    "<if test='map.deliveryEndDate !=\"\" and map.deliveryEndDate!=null and (map.deliveryStartDate==null or map.deliveryStartDate==\"\")'> and  DATE_FORMAT(createtime,'%Y-%m-%d') <![CDATA[<=]]> STR_TO_DATE(#{map.deliveryEndDate},'%Y-%m-%d') </if>" +
    "</if>" +
    "  and isdelete = 0 " +
    "</where>";

  @Select("<script>select count(1) from quicktrade"+ dynamicSql+"</script>")
  public int countAll(@Param("map") Map<String, Object> map);

  @Select("<script> select * from quicktrade" + dynamicSql + " order by createtime desc limit #{pageSize} offset #{indexNum} </script>")
  public List<QuickTrade> findAll(@Param("map") Map<String, Object> map,
                                  @Param("pageSize") int pageSize,
                                  @Param("indexNum") int indexNum);

  @Select("<script> select *  from quicktrade " + dynamicSql + " order by createtime desc </script>")
  public List<QuickTrade> exportData(@Param("map")Map<String, Object> map);

  @Update("update quicktrade set status=2,statusname='已处理',solvedremarks=#{solvedremarks},solvedtime=#{solvedtime},solvedmanusername=#{solvedmanusername},solvedmanid=#{solvedmanid} where id=#{id}")
  public int update(QuickTrade rapidTrade);



  public default Pager<QuickTrade> findAllPage(Map<String, Object> map, int page, int pagesize){
    return  Pager.config(this.countAll(map), (int limit, int offset) -> this.findAll(map,limit,offset)).page(page, pagesize);
  }


  //添加快速找货
  @Insert("insert into quicktrade(userphone,content,createtime,code,clientType) values(" +
          "#{userphone},#{content},#{createtime},#{code},#{clientType})")
  int addTrade(QuickTrade trade);

}
