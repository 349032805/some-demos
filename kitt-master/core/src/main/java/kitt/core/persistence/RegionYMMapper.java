package kitt.core.persistence;

import kitt.core.domain.District;
import kitt.core.entity.RegionYM;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 16/1/17.
 */
public interface RegionYMMapper {
    /**
     * 根据code获取name
     */
    @Select("select name from regionym where code=#{code} and isdelete=0")
    public String getRegionNameByCode(@Param("code") String code);

    //获取省下面的市
    @Select("select * from regionym where parent=#{parent} and isdelete=0 order by mold ")
    public List<RegionYM> getAllCitysByParentCode(@Param("parent")String parent);

    @Select("select * from regionym where level=1  and isdelete=0 order by mold")
    public List<RegionYM> getAllProvincesRegionYM();

    @Select("select * from regionym where level=1  and isdelete=0 order by mold")
    public List<District> getAllProvinces();

    @Select("select * from regionym where isdelete=0 and name=#{name} and level=#{level} order by sequence is null,sequence")
    public District getProvincesByName(@Param("name") String name,@Param("level") int level);

    @Select("select * from regionym where parent=#{parent} and level=#{level} and isdelete=0 order by sequence is null,sequence")
    public List<District> getDistrictByParent(@Param("parent") String parent,@Param("level") int level);

    @Select("select * from regionym where code=#{code} and isdelete=0")
    public District getDistrictByCode(@Param("code") String code);

    @Select("<script>select distinct(mold) from regionym where level=#{level} and isdelete=0 " +
            "<if test='parent!=null'>and parent=#{parent}</if>" +
            "order by mold</script>")
    public List<District> getDistinctMold(@Param("level") int level,@Param("parent") String parent);


    @Select("<script>select * from regionym where level=#{level} and isdelete=0 and mold=#{mold}" +
            "<if test='parent!=null'>and parent=#{parent}</if>" +
            "</script>")
    public List<Map<String,Object>> getregionymsByMold(@Param("mold")char mold,@Param("parent") String parent,@Param("level")int level);


    @Select("select * from regionym where parent=#{parent} and  isdelete=0 order by sequence is null,sequence")
    public List<District> loadDistrictByParent(@Param("parent") String parent);

    @Select("select code from regionym where parent=#{parent}  order by code desc limit 1")
    public String loadMaxCodeInParentId(@Param("parent") String parent);

    @Select("select code from regionym where level=1 order by code desc limit 1")
    public String loadMaxCode();

    @Insert("insert into regionym (name,mold,code,level,parent,sequence) values(#{name},#{mold},#{code},#{level},#{parent},#{sequence})")
    public void addRegion(District district);

    @Select("<script>select count(code)  from regionym where level=#{level}  and name=#{name} and isdelete=0 <if test='code!=null'>and code!=#{code}</if></script>")
    public int findNameExists(District district);

    @Update("update regionym set name=#{name},sequence=#{sequence},mold=#{mold} where code=#{code}")
    public void updateRegion(District district);

    @Update("update  regionym set isdelete=1 where code=#{code}")
    public void deleteRegion(String code);

    //获取省下面的市
    @Select("select * from regionym where level=2  and isdelete=0  and parent=#{parent} order by mold")
    public List<District> getAllCitys(@Param("parent")String parent);


}
