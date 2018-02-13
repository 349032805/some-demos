package kitt.core.persistence;

import kitt.core.domain.UploadFileByUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by joe on 2/8/15.
 */
public interface UploadFileByUserMapper {
    @Insert("insert into uploadfilebyuser (userid, filepath, createtime) values (#{userid}, #{filepath}, now())")
    @Options(useGeneratedKeys = true)
    public void insertUpload(UploadFileByUser upload);

    //查询路径包含temp并且上传时间大于参数时间段之外的记录
    @Select("select * from uploadfilebyuser where filepath like '%temp%' and createtime < now() - interval #{hour} hour")
    public List<UploadFileByUser> getOutofTimeList(int hour);

}
