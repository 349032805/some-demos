package kitt.core.persistence;

import kitt.core.domain.Cooperation;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by liuxinjie on 15/9/28.
 */
public interface CooperationMapper {

    /**
     * 添加我要合作
     * @param cooperation
     * @return
     */
    @Insert("insert into cooperation(code, companyname, linkmanname, linkmanphone, remarks, kind, kindname, " +
            " type, typename, createtime, clienttype, login, userid, userphone) " +
            " values(dateseq_next_value('HZ'), #{companyname}, #{linkmanname}, #{linkmanphone}, " +
            " #{remarks}, #{kind}, #{kindname}, #{type}, #{typename},  now(), #{clienttype}, #{login}, " +
            " #{userid}, #{userphone})")
    int doAddCooperationMethod(Cooperation cooperation);


    /**
     * admin模块处理Cooperation
     * @param cooperation
     * @return
     */
    @Update("update cooperation set companyname=#{companyname}, linkmanname=#{linkmanname}, linkmanphone=#{linkmanphone}, " +
            " kind=#{kind}, kindname=#{kindname}, type=#{type}, typename=#{typename}, status=#{status}, statusname=#{statusname}, " +
            " solvedtime=now(), solvedmanid=#{solvedmanid}, solvedmanusername=#{solvedmanusername}, solvedremarks=#{solvedremarks} " +
            " where id=#{id} ")
    boolean doUpdateCooperationMethod(Cooperation cooperation);



    @Select("<script>" +
            "select count(1) from cooperation " +
            " <where>" +
            " isdelete=0 and status=#{status} " +
            " <if test='content!=null'> and companyname like #{content}</if>" +
            " <if test='kind!=0'> and kind=#{kind}</if>" +
            " <if test='type!=0'> and type=#{type}</if>" +
            " </where>" +
            "</script>")
    public int getCooperationCount(@Param("status") int status,
                                   @Param("content") String content,
                                   @Param("kind") int kind,
                                   @Param("type") int type);

    @Select("<script>" +
            "select * from cooperation " +
            " <where>" +
            " isdelete=0 and status=#{status} " +
            " <if test='content!=null'> and companyname like #{content}</if>" +
            " <if test='kind!=0'> and kind=#{kind}</if>" +
            " <if test='type!=0'> and type=#{type}</if>" +
            " </where>" +
            " order by createtime desc limit #{limit} offset #{offset}" +
            "</script>")
    public List<Cooperation> getCooperationList(@Param("status") int status,
                                                @Param("content") String content,
                                                @Param("kind") int kind,
                                                @Param("type") int type,
                                                @Param("limit") int limit,
                                                @Param("offset") int offset);

    public default Pager<Cooperation> pageCooperation(int status, String content, int kind, int type, int page, int pagesize){
        return Pager.config(this.getCooperationCount(status, content, kind, type), (int limit, int offset) -> this.getCooperationList(status, content, kind, type, limit, offset))
                .page(page, pagesize);
    }

    /**
     * 根据id查询Cooperation对象
     * @param id
     * @return
     */
    @Select("select * from cooperation where id=#{id}")
    Cooperation getCooperationById(int id);



}
