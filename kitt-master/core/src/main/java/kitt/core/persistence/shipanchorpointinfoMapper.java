package kitt.core.persistence;

import kitt.core.domain.ShipAnchorPointInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by lich on 16/1/13.
 */
public interface shipanchorpointinfoMapper {
    @Insert("insert into shipanchorpointinfo(info,processtime,sign,souceId,infoid,sort,createtime,cdId) values(#{info},#{processtime},#{sign},#{souceId},#{infoid},#{sort},now(),#{cdId})")
    public void addAnchorpointinfo(ShipAnchorPointInfo info);

    @Select("select * from shipanchorpointinfo where souceId=#{souceId} order by createtime desc")
    public List<ShipAnchorPointInfo> getShipAnchorBySourceId(@Param("souceId")String souceId);

    @Select("select * from shipanchorpointinfo where souceId=#{souceId} and isdelete=0 order by createtime desc")
    public List<ShipAnchorPointInfo> getShipAnchorPointInfosBySourceId(@Param("souceId")String souceId);

    @Select("select * from shipanchorpointinfo where souceId=#{souceId} and isdelete=0 order by createtime desc limit 1")
    public ShipAnchorPointInfo findShipAnchorPointInfosBySourceId(@Param("souceId")String souceId);

    @Update(" update shipanchorpointinfo set isdelete=1 where cdId=#{cdId} and olddata=0 and isdelete=0 ")
    public void updateShipAnchorPointInfo(@Param("cdId")int cdId);

    @Select("select count(*) from shipanchorpointinfo where souceId=#{souceId} and isdelete=0 and olddata=0 ")
    public int countsBySourceId(@Param("souceId")String souceId);

    @Update(" update shipanchorpointinfo set olddata=1 where cdId=#{cdId} and souceId=#{souceId} and isdelete=0 ")
    public void updateShipInfo(@Param("cdId")int cdId,@Param("souceId")String souceId);





}
