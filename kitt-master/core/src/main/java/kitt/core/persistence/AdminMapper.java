package kitt.core.persistence;

import kitt.core.domain.Admin;
import kitt.core.domain.EnumAdmin;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by joe on 11/4/14.
 */
public interface AdminMapper {
    @Select("select * from admins where username=#{username}")
    public Admin getByUsername(String username);

    @Select("select * from admins where name=#{name}")
    public List<Admin> getByName(String name);


    @Select("select count(1) from admins where name=#{name}")
    public int nameExists(String name);

    @Select("select count(1) from admins where username=#{username}")
    public int usernameExists(String username);

    @Select("select username from admins WHERE username REGEXP #{username}  order by id desc limit 1")
    public String matchUsername(String name);

    @Select("select * from admins where phone=#{phone}")
    public Admin getByPhone(String phone);

    @Select("select count(1) from admins where phone=#{value} and isactive=1")
    public  int countByPhone(String phone);

    @Select("select name,phone,isactive,username from admins where phone=#{value} and isactive=1")
    public  Admin loadDealerIsactive(String phone);
    @Select("select * from admins where name=#{name} and phone=#{phone}")
    public Admin getAdminByNameAndPhone(@Param("name")String name,
                                        @Param("phone")String phone);

    //添加用户
    @Insert("insert into admins(username, password, isactive, phone, jobnum, name) " +
            "values(#{username}, #{password}, 1, #{phone}, #{jobnum}, #{name})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    public int addAdmin(Admin admin);

    //设置用户的JobNum
    @Update("update admins set jobNum=#{jobnum} where id=#{id}")
    public int updateJobNumByid(Admin admin);

    //根据角色，查询
    @Select("select * from admins where role=#{role} and isactive=1")
    List<Admin> getManListByRole(String role);


    //根据工号查询
    @Select("select * from admins where jobnum=#{jobnum}")
    public Admin getAdminByJobNum(String jobnum);

    //根据姓名，电话查询dealerid
    @Select("select jobnum from admins where name=#{name} and phone=#{phone}")
    public String getJobnumByNamePhone(@Param("name")String name,
                                       @Param("phone")String phone);

    //改变admin， 是否启用，禁用
    @Select("update admins set isactive=#{isactive} where jobnum=#{jobnum}")
    public void setIsActiveByJobnum(@Param("isactive")boolean isactive, @Param("jobnum")String jobnum);


    //page分页
    @Select("<script>select count(1) from (select a.id from admins a left join usersroles ur on a.id=ur.userid" +
            " <where><if test='params.name!=null and params.name!=\"\"'> and a.name like \"%\"#{params.name}\"%\"  </if>" +
            "<if test='params.phone!=null and params.phone!=\"\"'> and a.phone like \"%\"#{params.phone}\"%\"  </if>" +
            "<if test='params.jobnum!=null and params.jobnum!=\"\"'> and a.jobnum like \"%\"#{params.jobnum}\"%\"  </if> " +
            "<if test='params.roleId!=null and params.roleId!=0'> and ur.roleId = ${params.roleId} </if></where> group by a.id) t</script>")
    public int countAllAdmins(@Param("params")Map<String,Object> params);


    @Select("<script>select * from (select admin.id,admin.username,admin.isactive,admin.name,admin.phone,admin.jobnum from admins admin left join usersroles ur " +
            "on admin.id=ur.userid <where><if test='params.name!=null and params.name!=\"\"'>and admin.name like \"%\"#{params.name}\"%\" </if>" +
            "<if test='params.phone!=null and params.phone!=\"\"'>and admin.phone like \"%\"#{params.phone}\"%\"</if>" +
            "<if test='params.jobnum!=null and params.jobnum!=\"\"'>and admin.jobnum like \"%\"#{params.jobnum}\"%\" </if>" +
            " <if test='params.roleId!=null and params.roleId!=0'>and ur.roleId = ${params.roleId}</if></where> " +
            "group by admin.id order by admin.isactive desc,admin.id asc ) t limit #{limit} offset  #{offset}</script>")
    @Results(value= {@Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "name", column = "name"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "jobnum", column = "jobnum"),
            @Result(property = "isactive",column = "isactive"),
            @Result(property = "roles",column = "id",javaType = List.class,many = @Many(select = "kitt.core.persistence.RoleMapper.findRolesByUserId"))
            })
    public List<Admin> listAllAdmins(@Param("params")Map<String,Object>params,@Param("limit") int limit, @Param("offset") int offset);

    public default Pager<Admin> pageAllAdmins(Map<String,Object> params,int page, int pagesize){
        return Pager.config(this.countAllAdmins(params), (int limit, int offset) -> this.listAllAdmins(params,limit, offset))
                .page(page, pagesize);
    }

    //修改用户的激活状态
    @Update("update admins set isactive=#{isactive}, status=#{status} where id=#{id}")
    public void modifyIsactive(@Param("id") int id,
                               @Param("isactive") boolean isactive,
                               @Param("status")EnumAdmin status);

    //重置密码
    @Update("update admins set password=#{password} where id=#{id}")
    public void initPassword(@Param("id") int id,@Param("password") String password);

    @Update("update admins set phone=#{phone} where id=#{id}")
    public void updatePhoneById(@Param("phone")String phone,
                         @Param("id")int id);

    @Update("update admins set password=#{password} where id=#{id}")
    public void resetPasswordById(@Param("password")String password,
                                  @Param("id")int id);

    @Select("select * from admins where id=#{id}")
    public Admin getAdminById(int id);


    @Update("update admins set status=#{status},isactive=0 where id =#{id}")
    public  void updateStatus(@Param("status")String stauts,@Param("id")int id);

    @Select("select count(1) from  admins where  status=#{status} and isactive=0 and id =#{id}")
    public  int  loadDealerByStatus(@Param("status")String stauts,@Param("id")int id);


    @Select("select * from  admins where  teamId=#{teamId} and isactive=1")
    public  List<Admin>  findByTeamId(@Param("teamId")int teamId);





}
