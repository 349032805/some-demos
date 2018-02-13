package kitt.core.mapper;

import com.github.pagehelper.Page;
import kitt.core.entity.BUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by xiangyang on 15/8/20.
 */
public interface BUserMapper {
    @Select("select * from busers where id=#{value}")
    public BUser findById(long id);

    @Select("select * from busers")
    public Page<BUser> findUser();

    @Select("select * from busers where loginname=#{loginname}")
    public BUser findByLoginName(String loginname);

    @Select("select * from busers where loginname=#{loginname} and password=#{password}")
    public BUser findByLoginNameAndPass(@Param("loginname") String loginname, @Param("password") String password);

    public void  addUser(BUser user);

    //更新密码
    @Update("update busers t set t.password = #{password},salt=#{salt},last_modified_date=now() where  loginname = #{loginname}")
    public int updateUserPassword(BUser u);

}
