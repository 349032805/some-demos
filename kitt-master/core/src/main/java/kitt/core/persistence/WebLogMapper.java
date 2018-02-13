package kitt.core.persistence;

import kitt.core.domain.WebLog;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/9/15.
 */
public interface WebLogMapper {

    //添加日志信息
    @Insert("insert into weblog(url, parameter, method, userid, userphone, useragent, userip, requesttype, createtime)" +
            " values(#{url}, #{parameter}, #{method}, #{userid}, #{userphone}, #{useragent}, #{userip}, #{requesttype}, now())")
    public int addWebLog(WebLog webLog);

    @Select("select count(distinct(method)) from weblog")
    int getWebLogStatisticCount();

    @Select("select method, url, count(method) as totaltimes, count(if(createtime between #{month} and now(),true, null)) as monthtimes," +
            " count(if(createtime between #{week} and now(), true, null)) as weektimes, " +
            " count(if(createtime between #{today} and now(), true, null)) as todaytimes from weblog " +
            " group by method order by totaltimes desc limit #{limit} offset #{offset}")
    List<Map<String, Object>> getWebLogStatisticList(@Param("month") LocalDate month,
                                                     @Param("week") LocalDate week,
                                                     @Param("today") LocalDate today,
                                                     @Param("limit")int limit,
                                                     @Param("offset")int offset);

    default Pager<Map<String, Object>> pageAllWebLog(LocalDate month, LocalDate week, LocalDate today, int page, int pagesize){
        return Pager.config(this.getWebLogStatisticCount(), (int limit, int offset) -> this.getWebLogStatisticList(month, week, today, limit, offset))
                .page(page, pagesize);
    }

}
