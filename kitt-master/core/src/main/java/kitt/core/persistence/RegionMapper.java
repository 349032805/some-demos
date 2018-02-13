package kitt.core.persistence;

import kitt.core.domain.District;
import kitt.core.domain.Role;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Created by lich on 15/12/18.
 */
public interface RegionMapper {

    @Select("select * from region where level=1  and isdelete=0 order by mold")
    public List<District> getAllProvinces();

    @Select("select * from region where isdelete=0 and name=#{name} and level=#{level}")
    public District getProvincesByName(@Param("name") String name,@Param("level") int level);

    @Select("select * from region where parent=#{parent} and level=#{level} and isdelete=0")
    public List<District> getDistrictByParent(@Param("parent") String parent,@Param("level") int level);

    @Select("select * from region where parent=#{parent} and isdelete=0")
    public List<District> getDistrictByParentCode(@Param("parent") String parent);


    @Select("select * from region where code=#{code} and isdelete=0")
    public District getDistrictByCode(@Param("code") String code);

    @Select("<script>select distinct(mold) from region where level=#{level} and isdelete=0 " +
            "<if test='parent!=null'>and parent=#{parent}</if>" +
            "order by mold</script>")
    public List<District> getDistinctMold(@Param("level") int level,@Param("parent") String parent);


    @Select("<script>select * from region where level=#{level} and isdelete=0 and mold=#{mold}" +
            "<if test='parent!=null'>and parent=#{parent}</if>" +
            "</script>")
    public List<Map<String,Object>> getRegionsByMold(@Param("mold")char mold,@Param("parent") String parent,@Param("level")int level);









}
