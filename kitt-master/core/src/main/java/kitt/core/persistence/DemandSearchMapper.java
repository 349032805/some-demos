package kitt.core.persistence;


import kitt.core.domain.Demand;
import kitt.core.domain.SellInfo;
import kitt.core.util.PageQueryParam;
import kitt.ext.mybatis.Where;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by liuxinjie on 15/9/14.
 */
public interface DemandSearchMapper {
    /**
     * 微信搜索
     */
    @Select("select count(1) from demands where demandcode like #{content} and isdelete=0 and releasestatus=1 and checkstatus='审核通过' ")
    int getWapSearchDemandCount(@Param("content")String content);

    @Select("<script>" +
            " select * from demands where demandcode like #{content} and isdelete=0 and releasestatus=1 and checkstatus='审核通过' " +
            " order by id desc " +
            " <choose><when test='anchor==null or anchor==\"\"'>limit #{pageQuery.pagesize} offset #{pageQuery.indexNum}</when><otherwise>limit ${pageQuery.rowNum}  offset 0</otherwise></choose>" +
            "</script>")
    List<Demand> getWapSearchDemandList(@Param("content")String content,
                                        @Param("pageQuery") PageQueryParam param,
                                        @Param("anchor") String scrtop);

    public default PageQueryParam getWapSearchDemandTotalList(String content, PageQueryParam param, String scrtop) {
        content = Where.$like$(content);
        int totalCount = getWapSearchDemandCount(content);
        List<Demand> demandList = getWapSearchDemandList(content, param, scrtop);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(demandList);
        //如果有锚点,加载完数据，告诉前台当前是第几页
        if (org.apache.commons.lang3.StringUtils.isNotBlank(scrtop)) {
            param.setPage(param.getIndexNum() / param.getPagesize());
        }
        return param;
    }

}
