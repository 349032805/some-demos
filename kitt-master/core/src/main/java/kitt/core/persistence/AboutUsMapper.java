package kitt.core.persistence;

import kitt.core.domain.AboutUs;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by fanjun on 15-4-26.
 */
public interface AboutUsMapper {

    final String whereSql = "<where>" +
            "<if test='type!=null'> type=#{type} </if>" +
            "<if test='status!=null'> and status=#{status} </if>" +
            " and isdelete=0 "+
            "</where>";

    @Select("<script>select count(*) from aboutus" + whereSql+ "</script>")
    public int countAllInfoByType(@Param("type") String type,@Param("status") String status);

    @Select("<script>select * from aboutus" + whereSql + " order by sequence limit #{limit} offset #{offset}</script>")
    public List<AboutUs> listAllInfoByType(@Param("type") String type,@Param("status") String status,@Param("limit") int limit, @Param("offset") int offset);

    public default Pager<AboutUs> pageAllInfoByType(String type,String status,int page, int pagesize){
        return Pager.config(this.countAllInfoByType(type,status), (int limit, int offset) -> this.listAllInfoByType(type,status,limit, offset))
                .page(page, pagesize);
    }

    //增加记录
    @Insert("insert into aboutus(title, content, type, ishot, workplace, contact, status," +
            "updatetime, updateuser, picurl) values(" +
            "#{title}, #{content}, #{type}, #{ishot}, #{workplace}, #{contact}, #{status}, " +
            "#{updatetime}, #{updateuser}, #{picurl})")
    public int addAboutUs(AboutUs aboutUs);

    //修改记录
    @Update("update aboutus set title=#{title}, content=#{content}, ishot=#{ishot}, " +
            "workplace=#{workplace}, contact=#{contact}, updatetime=#{updatetime}, " +
            "updateuser=#{updateuser}, picurl=#{picurl} where id=#{id}")
    public boolean modifyAboutUs(AboutUs aboutUs);

    //获取单个记录
    @Select("select * from aboutus where id=#{id} and isdelete=0")
    public AboutUs getAboutUsById(int id);

    //删除(更改删除状态)
    @Update("update aboutus set isdelete=1 where id=#{id}")
    public void modifyIsdelete(int id);

    //更改建议反馈沟通状态
    @Update("update aboutus set status='hasCommunicated' where id=#{id}")
    public void modifyStatus(int id);

    /*//根据状态获取建议对象
    @Select("<script>select * from aboutus <where>" +
            "<if test='status!=null'> status=#{status} </if>"+
            "</where></script>")
    public List<AboutUs> getAdviceByStatus(@Param("status")String status);*/

    //根据类型获取对象分页
    @Select("select * from aboutus where type=#{type} and isdelete=0 order by sequence limit #{limit} offset #{offset}")
    public List<AboutUs> getAboutUsByTypeWithPaging(@Param("type") String type,@Param("limit") int limit,@Param("offset") int offset);

    //根据类型统计记录数
    @Select("select count(*) from aboutus where type=#{type} and isdelete=0")
    public int countAboutUsByType(String type);

    //根据类型获取全部对象集合
    @Select("select * from aboutus where type=#{type} and isdelete=0")
    public List<AboutUs> getAboutUsByType(String type);

    //根据类型获取对象集合
    @Select("select * from aboutus where type=#{type} and isdelete=0 limit #{limit}")
    public List<AboutUs> getSomeAboutUsByType(@Param("type")String type,
                                              @Param("limit")int limit);

    //根据状态获取建议对象
    @Select("select * from aboutus where status=#{status}")
    public List<AboutUs> getAdviceByStatus(String status);

    //更改顺序
    @Update("update aboutus set sequence=#{sequence} where id=#{id}")
    public void modifySequence(@Param("id") int id,@Param("sequence") int sequence);

    //累加浏览次数
    @Update("update aboutus set viewtimes=viewtimes+1 where id=#{id}")
    public void addViewtimes(int id);

}
