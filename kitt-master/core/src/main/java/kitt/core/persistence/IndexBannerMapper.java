package kitt.core.persistence;

import kitt.core.domain.IndexBanner;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by fanjun on 15-3-23.
 */
public interface IndexBannerMapper {
    /**
     * 前台使用图片
     */
    @Select("select * from indexbanners where type=#{type} and isshow=1 order by sequence limit #{limitnum}")
    public List<IndexBanner> getIndexBannnersWithLimit(@Param("type") String type,
                                                       @Param("limitnum") int limitnum);

    /**
     * 后台图片列表
     */
    @Select("select * from indexbanners where type=#{type} order by isshow desc, sequence limit #{limitnum}")
    public List<IndexBanner> getAdminIndexBannnersWithLimit(@Param("type") String type,
                                                            @Param("limitnum") int limitnum);

    //根据id查找IndexBanner图片
    @Select("select * from indexbanners where id=#{id}")
    IndexBanner getIndexBannerById(int id);

    /**
     * 更改图片path
     */
    @Update("<script>" +
            " update indexbanners set path=#{path}, " +
            " <if test='path==null or path==\"\"'> isshow=0, linkurl='', sequence=10, </if>" +
            " lastupdatemanid=#{lastupdatemanid}, lastupdatemanusername=#{lastupdatemanusername}," +
            " lastupdatemanname=#{lastupdatemanname} " +
            " where id=#{id}" +
            "</script>")
    int changeIndexBannersPathById(IndexBanner indexBanner);

    /**
     * 设置图片是否在前台显示
     */
    @Update("update indexbanners set isshow=(isshow+1)%2, lastupdatemanid=#{lastupdatemanid}, " +
            "lastupdatemanusername=#{lastupdatemanusername}, lastupdatemanname=#{lastupdatemanname} where id=#{id}")
    int setCancelShowBannerPic(IndexBanner indexBanner);

    /**
     * 设置图片的顺序
     */
    @Update("update indexbanners set sequence=#{sequence}, lastupdatemanid=#{lastupdatemanid}, " +
            "lastupdatemanusername=#{lastupdatemanusername}, lastupdatemanname=#{lastupdatemanname}  where id=#{id}")
    int changeSequence(IndexBanner indexBanner);

    /**
     * 更改图片的超链接
     */
    @Update("update indexbanners set linkurl=#{linkurl}, lastupdatemanid=#{lastupdatemanid}, " +
            "lastupdatemanusername=#{lastupdatemanusername}, lastupdatemanname=#{lastupdatemanname}  where id=#{id}")
    int changeBannerLinkURL(IndexBanner indexBanner);
}
