package kitt.core.persistence;

import kitt.core.domain.Logisticsintention;
import kitt.core.domain.Returnvisit;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by zhangbolun on 15/12/25.
 */
public interface ReturnvisitMapper {

    @Insert("insert into returnvisit(souceid,status,comment,createtime) values(#{souceid},#{status},#{comment},now())")
    @Options(useGeneratedKeys = true)
    public void addReturnvisit(Returnvisit returnvisit);

//    @Select("select * from returnvisit where souceid=#{souceid}")
//    public List<Returnvisit> getReturnvisitBySouceId(@Param("souceid") String souceid);

    @Select("select count(*) from returnvisit where souceid=#{souceid}")
    public int countLogisIntention(@Param("souceid") String souceid);

    @Select("select * from returnvisit where souceid=#{souceid}" +
            " order by createtime desc limit #{limit} offset #{offset}")
    public List<Returnvisit> getPageIntention(@Param("souceid") String status,@Param("limit") int limit, @Param("offset") int offset);

    public default Pager<Returnvisit> getReturnvisitBySouceId(@Param("souceid") String souceid,int page, int pagesize){
        return Pager.config(this.countLogisIntention(souceid), (int limit, int offset) -> this.getPageIntention(souceid,limit, offset))
                .page(page, pagesize);
    }

}
