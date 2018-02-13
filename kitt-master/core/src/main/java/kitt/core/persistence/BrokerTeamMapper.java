package kitt.core.persistence;

import kitt.core.domain.BrokerTeam;
import kitt.core.util.PageQueryParam;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by xiangyang on 16/1/7.
 */
public interface BrokerTeamMapper {

    @Insert("insert into brokerteam (teamName,teamLeader,status) values(#{teamName},#{teamLeader},#{status})")
    public void add(BrokerTeam brokerTeam);

    @Select("select id,teamName,teamLeader from brokerteam where id=#{value}")
    public BrokerTeam loadTeamById(int id);

    @Update("<script>update brokerteam    <set> " +
            "<if test='teamName != null'>teamName =#{teamName},</if>" +
            " <if test='teamLeader != null'>teamLeader = #{teamLeader},</if>  " +
            "<if test='status != null'>status=#{status},</if> </set> where id=#{id}</script>")
    public void update(BrokerTeam brokerTeam);

    @Select("<script> select count(1) from brokerteam <where><if test='teamName!=null'>teamName  like '%' #{teamName} '%'</if></where></script>")
    public int count(BrokerTeam brokerTeam);

    @Select("<script> select t.id,t.teamName,t.teamLeader,group_concat(a.name) dealerName  from brokerteam t left join admins  a on a.teamid=t.id   <where><if test='team.teamName!=null'> t.teamName like '%' #{team.teamName} '%'</if></where> group by t.id order by id limit 10 offset #{page.indexNum} </script>")
    public List<BrokerTeam> list(@Param("page") PageQueryParam pageQueryParam,@Param("team") BrokerTeam brokerTeam);
    //是否存在该条数据
    @Select("select count(1) from brokerteam where teamName=#{teamName}")
    public int isTeamNameExists(@Param("teamName") String name);

    //是否有与该条数据一样的teamName
    @Select("select count(1) from brokerteam where id!=#{id} and teamName=#{teamName}")
    public int isEqualsTeamNameExists(BrokerTeam brokerTeam);

    @Select("select id,teamName,teamLeader from brokerteam ")
    public List<BrokerTeam> listAll();




}
