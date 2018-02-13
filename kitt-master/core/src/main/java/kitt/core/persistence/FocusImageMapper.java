package kitt.core.persistence;

import kitt.core.domain.Focusimage;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by lich on 16/2/16.
 */
public interface FocusImageMapper {

    @Select("select count(*) from focusimage ")
    public int countFocusImage();

    @Select("select * from focusimage where  pictitle=#{pictitle}  and isdelete=0 ")
    public List<Focusimage> findByTitle(@Param("pictitle")String pictitle);

    @Select("select * from focusimage " +
            " order by lastupdatetime desc limit #{limit} offset #{offset}")
    public List<Focusimage> getFocusImageList(@Param("limit") int limit, @Param("offset") int offset);

    public default Pager<Focusimage> getFocusImage(int page, int pagesize){
        return Pager.config(this.countFocusImage(), (int limit, int offset) -> this.getFocusImageList(limit, offset))
                .page(page, pagesize);
    }
    //删除焦点图片
    @Update("update focusimage set isdelete=#{ftype} where id=#{id}")
    boolean doDeleteFocusImage(@Param("id")int id,@Param("ftype")int ftype);

    //添加焦点图片
    @Insert("insert into focusimage(pictitle,articletitle,articlelink,picaddress,summary, lasteditman,createtime)  " +
            " values(#{pictitle},#{articletitle},#{articlelink},#{picaddress},#{summary},#{lasteditman},now())")
    @Options(useGeneratedKeys=true)
    public int addFocusPic(Focusimage focusimage);


    //修改焦点图片
    @Update("<script>" +
            "update focusimage set pictitle=#{pictitle}, articletitle=#{articletitle}, articlelink=#{articlelink}, lasteditman=#{lasteditman} " +
            "<if test='picaddress!=\"\" and picaddress!=null'> ,picaddress=#{picaddress}</if>" +
            " where id=#{id}"+
            "</script>")
    public int updateFocusPic(Focusimage focusimage);

    /***********************************************以下site模块************************************************/

    @Select("select * from focusimage where isdelete=0 and pictitle=#{pictitle} order by lastupdatetime desc limit 1")
    public Focusimage findFocusImageByName(@Param("pictitle") String pictitle);


}
