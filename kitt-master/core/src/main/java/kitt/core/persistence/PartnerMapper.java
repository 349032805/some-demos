package kitt.core.persistence;

import kitt.core.domain.Partner;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/9/19.
 * 合作伙伴
 */
public interface PartnerMapper {


    @Select("<script>" +
            "select count(1) from partners " +
            " <where>" +
            " isdelete=0 " +
            " <if test='content!=null and content!=\"\"'> and companyname like #{content}</if>" +
            " </where>" +
            "</script>")
    public int getPartnerCount(@Param("content")String content);

    @Select("<script>" +
            "select * from partners " +
            " <where>" +
            " isdelete=0 " +
            " <if test='content!=null and content!=\"\"'> and companyname like #{content}</if>" +
            " </where>" +
            " order by isshow desc, sequence limit #{limit} offset #{offset}" +
            "</script>")
    public List<Partner> getPartnerList(@Param("content")String content,
                                        @Param("limit") int limit,
                                        @Param("offset") int offset);

    public default Pager<Partner> pagePartner(String content, int page, int pagesize){
        return Pager.config(this.getPartnerCount(content), (int limit, int offset) -> this.getPartnerList(content, limit, offset))
                .page(page, pagesize);
    }


    //lich  查找所有的需要显示的合作伙伴
    @Select("select * from partners where isdelete=0 and isshow=1 order by sequence, lastedittime desc")
    public List<Partner> findPartnerList();

    /**
     * 添加合作伙伴
     * @param partner
     * @return
     */
    @Insert("insert into partners(companyname, picurl, linkurl, sequence, remarks, createtime, " +
            " lastedittime, lasteditman) values(#{companyname}, #{picurl}, #{linkurl}, #{sequence}, " +
            " #{remarks}, now(), now(), #{lasteditman})")
    @Options(useGeneratedKeys=true)
    int doAddPartnerMethod(Partner partner);

    /**
     * update 合作伙伴
     * @param partner
     * @return
     */
    @Update("update partners set companyname=#{companyname}, picurl=#{picurl}, linkurl=#{linkurl}, " +
            " sequence=#{sequence}, remarks=#{remarks}, lastedittime=#{lastedittime}, lasteditman=#{lasteditman} " +
            " where id=#{id}")
    boolean doUpdatePartnerMethod(Partner partner);

    /**
     * 根据id查找合作伙伴
     * @param id
     * @return
     */
    @Select("select * from partners where id=#{id} limit 1")
    Partner getPartnerById(int id);

    @Select("select * from partners where companyname=#{companyname} limit 1")
    Partner getPartnerByCompanyName(String companyname);

    /**
     * 更改合作伙伴顺序
     * @param sequence                顺序
     * @param id                      id
     * @return
     */
    @Update("update partners set sequence=#{sequence} where id=#{id}")
    boolean doChangePartnerSequenceMethod(@Param("sequence")int sequence,
                                          @Param("id")int id);

    /**
     * 设置或者取消合作伙伴在前台显示
     * @param id                      id
     * @return
     */
    @Update("update partners set isshow=(isshow+1)%2 where id=#{id}")
    int doSetCancelPartnerShowMethod(int id);

    /**
     * 删除合作伙伴
     * @param id                      id
     * @return
     */
    @Update("update partners set isdelete=1 where id=#{id} and isdelete=0")
    int doDeletePartnerMethod(int id);

}
