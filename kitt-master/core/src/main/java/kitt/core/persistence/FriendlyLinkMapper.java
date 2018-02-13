package kitt.core.persistence;

import kitt.core.domain.FriendlyLink;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by liuxinjie on 15/12/16.
 */
public interface FriendlyLinkMapper {

    /**
     * 查询友情链接个数
     */
    @Select("select count(1) from friendlylink where isdelete=0 and isshow=1")
    int countFriendlyList();

    /**
     * 获取友情链接list
     */
    @Select("select * from friendlylink where isdelete=0 and isshow=1 order by sequence limit #{limit} offset #{offset}")
    List<FriendlyLink> getFriendlyListBySequence(@Param("limit")int limit,
                                                 @Param("offset")int offset);


    /**
     * 获取所有友情链接list
     */
    @Select("select * from friendlylink where isdelete=0 and isshow=1 order by sequence")
    List<FriendlyLink> getAllFriendlyList();


    final String FriendlyLinkAdminStr1 =
            " <where>" +
            " isdelete=0 " +
            " <if test='content!=null and content!=\"\"'> and companyname like #{content}</if>" +
            " </where>";
    @Select("<script>" +
            " select count(1) from friendlylink " + FriendlyLinkAdminStr1 +
            "</script>")
    public int getFriendlyLinkCount(@Param("content")String content);

    @Select("<script>" +
            " select * from friendlylink " + FriendlyLinkAdminStr1 +
            " order by isshow desc, sequence limit #{limit} offset #{offset}" +
            "</script>")
    public List<FriendlyLink> getFriendlyLinkList(@Param("content")String content,
                                             @Param("limit") int limit,
                                             @Param("offset") int offset);

    public default Pager<FriendlyLink> pageFriendlyLink(String content, int page, int pagesize){
        return Pager.config(this.getFriendlyLinkCount(content), (int limit, int offset) -> this.getFriendlyLinkList(content, limit, offset))
                .page(page, pagesize);
    }

    /**
     * 通过id查询友情链接
     */
    @Select("select * from friendlylink where id=#{id}")
    FriendlyLink getFriendlyLinkById(int id);

    /**
     * 通过公司名查询友情链接
     */
    @Select("select * from friendlylink where companyname=#{companyname} and isdelete=0")
    FriendlyLink getFriendlyLinkByCompanyName(String companyname);

    /**
     * 添加友情链接方法
     */
    @Insert("insert into friendlylink(companyname, url, sequence, remarks, createtime, lastedittime, lasteditmanid," +
            " lasteditmanusername) values(#{companyname}, #{url}, #{sequence}, #{remarks}, now(), now(), #{lasteditmanid}," +
            " #{lasteditmanusername})")
    @Options(useGeneratedKeys=true)
    int addFriendlyLinkMethod(FriendlyLink friendlyLink);

    /**
     * update 合作伙伴
     * @param friendlyLink
     * @return
     */
    @Update("update friendlylink set companyname=#{companyname}, url=#{url}, sequence=#{sequence}, remarks=#{remarks}," +
            " lastedittime=now(), lasteditmanid=#{lasteditmanid}, lasteditmanusername=#{lasteditmanusername} " +
            " where id=#{id}")
    int doUpdateFriendlyLinkMethod(FriendlyLink friendlyLink);

    @Update("update friendlylink set sequence=#{sequence} where id=#{id}")
    boolean doChangeFriendlyLinkSequenceMethod(@Param("sequence") int sequence,
                                               @Param("id") int id);

    @Update("update friendlylink set isshow=(isshow+1)%2 where id=#{id}")
    int doSetCancelFriendlyLinkShowMethod(int id);

    @Update("update friendlylink set isdelete=(isdelete+1)%2 where id=#{id}")
    int doDeleteFriendlyLinkMethod(int id);

}
