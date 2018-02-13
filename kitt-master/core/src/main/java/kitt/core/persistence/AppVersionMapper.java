package kitt.core.persistence;

import kitt.core.domain.AppVersion;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by fanjun on 15-5-13.
 */
public interface AppVersionMapper {

    //分页
    @Select("select count(*) from appversion")
    public int countAllAppVersion();

    @Select("select * from appversion  order by createtime desc limit #{limit} offset #{offset}")
    public List<AppVersion> listAllAppVersion(@Param("limit") int limit, @Param("offset") int offset);

    public default Pager<AppVersion> pageAllAppVersion(int page, int pagesize){
        return Pager.config(this.countAllAppVersion(), (int limit, int offset) -> this.listAllAppVersion(limit, offset))
                .page(page, pagesize);
    }

    //获取最高的版本号
    @Select("select * from appversion order by versionnum desc limit 1")
    public AppVersion getNewestVersionNum();

    //新增版本
    @Insert("insert into appversion(type,name,path,version,createtime,versionnum) values(" +
            "#{type},#{name},#{path},#{version},#{createtime},#{versionnum})")
    public void addAppVersion(AppVersion appVersion);

    //保存版本描述
    @Update("update appversion set comment=#{comment} where version=#{newVersion}")
    public void modifyCommentByVersion(@Param("newVersion") String newVersion,@Param("comment") String comment);

    //根据id获取对象
    @Select("select * from appversion where id=#{id}")
    public AppVersion getAppVersionById(int id);

    //修改版本描述
    @Update("update appversion set comment=#{comment} where id=#{id}")
    public void modifyCommentById(@Param("id") int id,@Param("comment") String comment);

    //根据id修改状态和启用时间
    @Update("update appversion set status=1, usetime=now() where id=#{id}")
    public void modifyStatusAndUsetimeById(int id);

    //更改版本为未启用
    @Update("update appversion set status=0 where status=1")
    public void modifyVersionToNoUse();

}
