package kitt.core.persistence;

import kitt.core.domain.DataBook;
import kitt.core.domain.StatusBook;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by liuxinjie on 15/10/8.
 */
public interface StatusBookMapper {

    //根据类型查询数据
    @Select("select * from statusbook where type=#{type} order by sequence")
    List<StatusBook> getStatusBookListByType(String type);

    //根据类型，顺序获取 名称
    @Select("select name from statusbook where type=#{type} and sequence=#{sequence}")
    String getStatusBookNameByTypeSequence(@Param("type") String type,
                                           @Param("sequence") int sequence);


}
