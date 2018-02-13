package kitt.core.mapper;

import org.apache.ibatis.annotations.Select;
import kitt.core.entity.Admin;

import java.util.List;

/**
 * Created by liuxinjie on 16/2/29.
 */
public interface BrokerAdminMapper {

    /**
     * 查找系统内所有的交易员
     */
    @Select(" select a.id,a.username,a.isactive,a.phone,a.jobnum,a.name,a.status from admins a, roles r, usersroles u " +
            " where a.id=u.userid and r.id=u.roleid and r.rolecode='Trader' and a.isactive=1 " +
            " group by a.id order by a.id")
    public List<Admin> getAllTraders();

    /**
     * 根据id查询Admin
     */
    @Select("select * from admins where id=#{id}")
    Admin getAdminById(int id);

    @Select("select * from admins where phone=#{phone}")
    Admin getAdminByPhone(String phone);
}
