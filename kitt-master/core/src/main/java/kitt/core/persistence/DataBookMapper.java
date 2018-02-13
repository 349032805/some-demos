package kitt.core.persistence;

import kitt.core.domain.DataBook;
import kitt.core.domain.SellInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by jack on 6/10/15.
 */
public interface DataBookMapper {
  //根据类型查询数据
  @Select("select * from databook where type=#{type} order by sequence")
  List<DataBook> getDataBookListByType(String type);

  //根据类型，顺序获取 名称
  @Select("select name from databook where type=#{type} and sequence=#{sequence}")
  String getDataBookNameByTypeSequence(@Param("type") String type,
                                       @Param("sequence") int sequence);

  //通过名称查颗粒度值
  @Select("select sequence from databook where name=#{value} ")
  public int findPsByName(String  name);

  //修改数据
  @Update("update databook set name=#{name} where type=#{type} and sequence=#{sequence}")
  public void updateDataBook(@Param("sequence")int sequence,@Param("name")String name,@Param("type")String type);

}
