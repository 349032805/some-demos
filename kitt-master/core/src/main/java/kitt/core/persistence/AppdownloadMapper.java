package kitt.core.persistence;

import kitt.core.domain.Appdownload;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

/**
 * Created by fanjun on 15-5-28.
 */
public interface AppdownloadMapper {

    @Insert("insert into appdownload(type,version,useragent,ip,createtime) values(" +
            "#{type},#{version},#{useragent},#{ip},#{createtime})")
    public void addAppdownload(Appdownload appdownload);

    //根据设备类型统计下载量
    @Select("select count(*) from appdownload where type=#{type}")
    public int countDownloadNumByType(String type);

    //根据设备类型和日期统计下载量
    @Select("select count(*) from appdownload where type=#{type} and date_format(createtime,'%Y-%m-%d') between #{startDate} and #{endDate}")
    public int countDownloadNumByTypeAndDate(@Param("type")String type,
                                             @Param("startDate")LocalDate startDate,
                                             @Param("endDate")LocalDate endDate);
}
