package kitt.core.persistence;

import kitt.core.domain.PublicPic;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by fanjun on 15-5-7.
 */
public interface PublicPicMapper {

    //查询全部图片
    @Select("select * from publicpic order by createtime desc")
    public List<PublicPic> getAllPublicPic();

    //新增记录
    @Insert("insert into publicpic(name,path,createtime) values(#{name},#{path},#{createtime})")
    public void addPublicPic(PublicPic p);

    //根据图片名称统计是否存在
    @Select("select count(*) from publicpic where name=#{name}")
    public int countPicByName(String name);

    //更新图片上传时间
    @Update("update publicpic set createtime=now() where name=#{name}")
    public void modifyCreatetimeByName(String name);

    //更新图片备注
    @Update("update publicpic set comment=#{comment} where id=#{id}")
    public void modifyCommentById(@Param("id") int id,@Param("comment") String comment);

    //删除
    @Delete("delete from publicpic where id=#{id}")
    public void deleteOneById(int id);

    //根据id查询记录
    @Select("select * from publicpic where id=#{id}")
    public PublicPic getPicById(int id);

}
