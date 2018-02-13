package kitt.core.persistence;

import kitt.core.domain.Custvisitlog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by lich on 16/1/20.
 */
public interface CustvisitlogMapper {
    //新增客服修改记录
    @Insert("insert into custvisitlog(operator, content, adminid,createtime,recordid) values(#{operator}," +
            "#{content}, #{adminid}, now(),#{recordid})")
    public int addCustLog(Custvisitlog log);

    //获取客服访问列表
    @Select("select * from custvisitlog  where recordid=#{recordid} order by lastupdatetime desc ")
    public List<Custvisitlog> findCustListLog(@Param("recordid") int recordid);


}
