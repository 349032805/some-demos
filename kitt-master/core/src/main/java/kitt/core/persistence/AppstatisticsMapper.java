package kitt.core.persistence;

import kitt.core.domain.Appstatistics;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by zhangbolun on 15/8/24.
 */
public interface AppstatisticsMapper {
    @Insert("insert into appstatistics(equipmentnumber,createtime,Logintimes) values(#{equipmentnumber},#{createtime},Logintimes+1);")
    public void addAppstatistics(@Param("equipmentnumber")String equipmentnumber,@Param("createtime")LocalDateTime createtime);

    @Select("select * from appstatistics where equipmentnumber=#{equipmentnumber} ")
    public Appstatistics getAppstatisticsb(@Param("equipmentnumber")String equipmentnumber);

    @Select("select * from appstatistics ")
    public List<Appstatistics> getAllAppstatisticsb();

    @Update("update appstatistics set Logintimes=Logintimes+1 where equipmentnumber=#{equipmentnumber}")
    public int updateAppstatistics(@Param("equipmentnumber")String equipmentnumber);


    //根据设备类型和日期统计下载量
    @Select("select count(*) from appstatistics where date_format(createtime,'%Y-%m-%d') between #{startDate} and #{endDate}")
    public int countDownloadNumByAndDate(@Param("startDate")LocalDate startDate,
                                         @Param("endDate")LocalDate endDate);

}
