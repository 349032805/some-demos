package kitt.core.persistence;

import kitt.core.domain.District;
import kitt.core.domain.Shipintention;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by lich on 16/1/11.
 */
public interface ShipMapper {
   //查询所有海域
    @Select("select * from shipport where isdelete=0 and level=#{level} and parent=#{parent}")
    public List<District> findAllDistrict(@Param("parent")String parent,@Param("level")int level);


    @Select("select * from shipport where isdelete=0 and level=#{level} order by mold ")
    public List<District> findByLevel(@Param("level")int level);

    @Select("select s.* from shipport c,shipport s where c.parent=s.code and c.name=#{name}")
    public District findParentByCode(@Param("name")String name);

    @Select("select distinct(mold) from shipport where level=2 and isdelete=0 order by mold")
    public List<District> findMolds();


    @Select("select * from shipport where level=2 and isdelete=0 and mold=#{mold}")
    public List<Map<String,Object>> findByMold(@Param("mold")char mold);


    @Select("select * from shipport  where  code=#{code}")
    public District findByCode(@Param("code")String code);

    @Select("select * from shipport where name=#{name}")
    public District findByName(@Param("name")String name);

    //查询所有港口
    @Select("select * from shipport where isdelete=0 and level=#{level}")
    public List<District> findAllPorts(@Param("level")int level);

    @Select("select * from shipintention where id=#{id}")
    public Shipintention getShipIntenttionById(@Param("id")int id);


    @Insert("<script>" +
            "insert into shipintention(loadport,unloadport"+
            "<if test='shipcode!=null'>,shipcode</if>" +
            "<if test='receiptdate!=null'>,receiptdate</if>" +
            "<if test='loaddock!=null'>,loaddock</if>" +
            "<if test='unloaddock!=null'>,unloaddock</if>" +
            ") values(#{loadport},#{unloadport}"+
            "<if test='shipcode!=null'>,#{shipcode}</if>" +
            "<if test='receiptdate!=null'>,#{receiptdate}</if>" +
            "<if test='loaddock!=null'>,#{loaddock}</if>" +
            "<if test='unloaddock!=null'>,#{unloaddock}</if>" +
            ")" +
            "</script>")
    @Options(useGeneratedKeys = true,keyProperty="id")
    public void addShipIntenttion(Shipintention shipintention);

    @Update(" update shipintention set receiptdate=#{receiptdate},loadport=#{loadport},unloadport=#{unloadport} where id=#{id} ")
    public void updateShipIntenttion(Shipintention shipintention);


}
