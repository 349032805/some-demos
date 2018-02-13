package kitt.core.persistence;

import kitt.core.domain.Person;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by liuxinjie on 15/12/25.
 */
public interface PersonMapper {

    //根据type 和 parentid 获取人员列表
    @Select("select * from person where usetype=#{usetype} and parentid=#{parentid} and isdelete=0")
    List<Person> getPersonListByTypeAndParentid(@Param("usetype")String usetype,
                                                @Param("parentid")int parentid);

    //添加人员
    @Insert("<script>" +
            " insert into person(name, " +
            "<if test='sex!=null'> sex, </if> " +
            "<if test='phone!=null'> phone, </if>" +
            "<if test='position!=null'> position, </if>" +
            "<if test='remarks!=null'> remarks, </if>" +
            "<if test='usetype!=null'> usetype, </if>" +
            " parentid, " +
            "<if test='icon!=null'> icon, </if>" +
            "<if test='iconoriginal!=null'> iconoriginal, </if>" +
            "<if test='lasteditmanid!=null'> lasteditmanid, </if>" +
            "<if test='lasteditmanusername!=null'> lasteditmanusername, </if>" +
            "createtime, lastedittime)" +
            "values(#{name}, " +
            "<if test='sex!=null'> #{sex}, </if>" +
            "<if test='phone!=null'> #{phone}, </if>" +
            "<if test='position!=null'> #{position}, </if>" +
            "<if test='remarks!=null'> #{remarks}, </if>" +
            "<if test='usetype!=null'> #{usetype}, </if>" +
            "#{parentid}, " +
            "<if test='icon!=null'> #{icon}, </if>" +
            "<if test='iconoriginal!=null'> #{iconoriginal}, </if>" +
            "<if test='lasteditmanid!=null'> #{lasteditmanid}, </if>" +
            "<if test='lasteditmanusername!=null'> #{lasteditmanusername}, </if>" +
            " now(), now() )" +
            "</script>")
    @Options(useGeneratedKeys=true)
    int addPerson(Person person);

    @Update("<script>" +
            " update person set name=#{name}, parentid=#{parentid}, " +
            " <if test='sex!=null'> sex=#{sex}, </if>" +
            " <if test='phone!=null'> phone=#{phone}, </if>" +
            " <if test='position!=null'> position=#{position}, </if>" +
            " <if test='remarks!=null'> remarks=#{remarks}, </if>" +
            " <if test='usetype!=null'> usetype=#{usetype}, </if>" +
            " <if test='icon!=null'> icon=#{icon}, </if>" +
            " <if test='iconoriginal!=null'> iconoriginal=#{iconoriginal}, </if>" +
            " <if test='lasteditmanid!=null'> lasteditmanid=#{lasteditmanid}, </if>" +
            " <if test='lasteditmanusername!=null'> lasteditmanusername=#{lasteditmanusername}, </if>" +
            " lastedittime=now() where id=#{id}" +
            "</script>")
    int updatePerson(Person person);

    //删除人员
    @Update("update person set isdelete=1 where id=#{id} and isdelete=0")
    int doDeletePersonById(int id);

    @Select("select * from person where id=#{id}")
    Person getPersonById(int id);

    @Select("select * from person where id=#{id} and isdelete=0")
    Person getPersonByIdNoDelete(int id);

    //根据姓名,职位,头像,类型查询人员
    @Select("select * from person where name=#{name} and position=#{position} and usetype=#{usetype}")
    Person getPersonByNameAndPositionAndType(@Param("name") String name,
                                             @Param("position") String position,
                                             @Param("usetype") String usetype);

}
