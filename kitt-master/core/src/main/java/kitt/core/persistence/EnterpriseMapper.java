package kitt.core.persistence;

import kitt.core.domain.Enterprise;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by liuxinjie on 15/12/25.
 */
public interface EnterpriseMapper {

    //通过type 和 parentid 查询 企业列表
    @Select("select * from enterprise where usetype=#{usetype} and parentid=#{parentid} and isdelete=0")
    List<Enterprise> getEnterpriseListByTypeAndParentId(@Param("usetype")String usetype,
                                                        @Param("parentid")int parentid);
    //添加企业方法
    @Insert("insert into enterprise(name, logo, remarks, usetype, parentid, createtime, lastedittime, lasteditmanid, lasteditmanusername) values(" +
            " #{name}, #{logo}, #{remarks}, #{usetype}, #{parentid}, now(), now(), #{lasteditmanid}, #{lasteditmanusername})")
    int addEnterprise(Enterprise enterprise);

    @Update("update enterprise set isdelete=1 where usetype=#{usetype} and parentid=#{parentid} and isdelete=0")
    int deleteEnterpriseByParentid(@Param("usetype")String usetype,
                                   @Param("parentid")int parentid);

    //根据id查询Enterprise
    @Select("select * from enterprise where id=#{id}")
    Enterprise getEnterpriseById(int id);

    //根据name和usetype 查询Enterprise
    @Select("select * from enterprise where parentid=#{parentid} and name=#{name} and usetype=#{usetype} and isdelete=0 limit 1")
    Enterprise getEnterpriseByParentIdAndNameAndType(@Param("parentid") int parentid,
                                                     @Param("name") String name,
                                                     @Param("usetype") String usetype);

    //根据name,usetype 查询Enterprise
    @Select("select * from enterprise where parentid=#{parentid} and logo=#{logo} and usetype=#{usetype} and isdelete=0 limit 1")
    Enterprise getEnterpriseByParentIdAndLogoAndType(@Param("parentid") int parentid,
                                                     @Param("logo")String logo,
                                                     @Param("usetype")String usetype);

    //根据id 删除企业
    @Update("update enterprise set isdelete=1 where id=#{id} and isdelete=0")
    int deleteEnterpriseById(int id);

    //更新企业
    @Update("<script>" +
            " update enterprise set isdelete=0, " +
            " <if test='name!=null'> name=#{name}, </if>" +
            " <if test='logo!=null'> logo=#{logo}, </if>" +
            " <if test='remarks!=null'> remarks=#{remarks}, </if>" +
            " <if test='usetype!=null'> usetype=#{usetype}, </if>" +
            " <if test='parentid!=null'> parentid=#{parentid}, </if>" +
            " <if test='lasteditmanid!=null'> lasteditmanid=#{lasteditmanid}, </if>" +
            " <if test='lasteditmanusername!=null'> lasteditmanusername=#{lasteditmanusername}, </if>" +
            " lastedittime=now() where id=#{id}" +
            "</script>")
    int updateEnterprise(Enterprise enterprise);




}
