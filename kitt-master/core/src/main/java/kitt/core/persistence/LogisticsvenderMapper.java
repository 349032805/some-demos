package kitt.core.persistence;


import kitt.core.domain.District;
import kitt.core.domain.Logistics56responselog;
import kitt.core.domain.Logisticsvender;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by lich on 15/12/16.
 */
public interface LogisticsvenderMapper {
    @Select("select * from logisticsvender where isdelete=0 and type=#{type}")
    List<Logisticsvender> findLogisticsvenderByType(@Param("type")int type);

    @Insert("<script>" +
            "insert into region(name,parentid,type,mold) values(#{name},#{parentid},#{type},#{mold}"+
            ")" +
            "</script>")
    @Options(useGeneratedKeys = true,keyProperty="id")
    public void addLogisticsintention(District region);


    @Insert("<script>" +
            "insert into logistics56responselog(souceid,code,databodysize,message,databody) values(#{souceid},#{code},#{databodysize},#{message},#{databody}"+
            ")" +
            "</script>")
    @Options(useGeneratedKeys = true,keyProperty="id")
    public void addLogistics56responselog(Logistics56responselog region);


}
