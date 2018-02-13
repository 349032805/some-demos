package kitt.core.persistence;

import kitt.core.domain.UserInfoLogin;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/8/19.
 * 此Mapper专门用来查询，统计 用户登陆 等信息
 */
public interface UserInfoMapper {

    String loginInfoStr1 = "select u.id as id, u.securephone as userphone, substring(u.registertime, 1, 11) as createtime, if(length(c.name) > 40, concat(substring(c.name, 1, 40), ' ...'), c.name) as companyname, if(length(c.legalpersonname) > 20, concat(substring(c.legalpersonname, 1, 20), ' ...'), c.legalpersonname) as username, count(if(ul.logintime between #{lastSelectDate} and now(),1,0)) as logintimes " +
           // "sum(if(s.status = 'VerifyPass' or (s.status='OutOfDate' and s.remarks='已过期，易煤网将该产品下架') , s.supplyquantity, 0)) as totalSellinfoAmount," +
           // "sum(if(m.status in ('交易结束', '报价中', '匹配中'), m.demandamount, 0)) as totalDemandAmount " +
            " from users u " +
            " inner join userlogins ul on u.id=ul.userid " +
            " inner join companies c on u.id=c.userid " +
           // " left join sellinfo s on s.sellerid=u.id " +
           // " left join mydemands m on m.userid=u.id " +
            "<where> c.verifystatus='审核通过' " +
            "<if test='content!=null and content!=\"\"'> and (c.name like #{content} or c.legalpersonname like #{content} or u.securephone like #{content})</if>" +
            "<if test='startDate!=null and startDate!=\"\" and endDate!=null and endDate!=\"\"'> and u.registertime between #{startDate} and #{endDate}</if>" +
            "</where>";
    String loginInfoStr2 = "select count(u.id) from users u " +
            " inner join userlogins ul on u.id=ul.userid " +
            " inner join companies c on u.id=c.userid " +
            "<where> c.verifystatus='审核通过' " +
            "<if test='content!=null and content!= \"\"'>and (c.name like #{content} or c.legalpersonname like #{content} or u.securephone like #{content})</if>" +
            "<if test='startDate!=null and startDate!=\"\" and endDate!=null and endDate!=\"\"'>and u.registertime between #{startDate} and #{endDate}</if>" +
            "</where>";
    String loginInfoOrderBy =
            "<if test='sortType==\"login\"'>" +
                    "<if test='!sortOrder'>order by logintimes desc,</if>" +
                    "<if test='sortOrder'>order by logintimes asc,</if>" +
            "</if>" +
            /*"<if test='sortType==\"sellinfo\"'>" +
                    "<if test='!sortOrder'>order by totalSellinfoAmount desc,</if>" +
                    "<if test='sortOrder'>order by totalSellinfoAmount asc,</if>" +
            "</if>" +
            "<if test='sortType==\"demand\"'>" +
                    "<if test='!sortOrder'>order by totalDemandAmount desc,</if>" +
                    "<if test='sortOrder'>order by totalDemandAmount asc,</if>" +
            "</if>" + */
            "createtime asc";
    @Select("<script>" + loginInfoStr2 + "group by u.id" + "</script>")
    List<UserInfoLogin> countAllUserLoginInfoNums(@Param("content")String content,
                                                  @Param("startDate")String startDate,
                                                  @Param("endDate")String endDate);

    @Select("<script>" +
            loginInfoStr1 + " group by u.id " + loginInfoOrderBy +
            "<if test='limit != -1 and offset != -1'> limit #{limit} offset #{offset}</if>" +
            "</script>")
    List<UserInfoLogin> getAllUserLoginInfoList(@Param("lastSelectDate")LocalDate lastSelectDate,
                                                @Param("content")String content,
                                                @Param("startDate")String startDate,
                                                @Param("endDate")String endDate,
                                                @Param("sortType")String sortType,
                                                @Param("sortOrder")boolean sortOrder,
                                                @Param("limit")int limit,
                                                @Param("offset")int offset);

    default Pager<UserInfoLogin> pageAllUserInfo(LocalDate lastmonth, String content, String startDate, String endDate, String sortType, boolean sortOrder, int page, int pagesize){
        return Pager.config(this.countAllUserLoginInfoNums(content, startDate, endDate).size(), (int limit, int offset) -> this.getAllUserLoginInfoList(lastmonth, content, startDate, endDate, sortType, sortOrder, limit, offset))
                .page(page, pagesize);
    }

    /**
     * 根据userid 查询 客户的 公司，电话，法人等
     * @param id               users表id
     * @return                 用户信息
     */
    @Select("select u.id as id, u.securephone as userphone, u.registertime as createtime, c.name as companyname, c.legalpersonname as username from users u inner join companies c on u.id=c.userid where u.id=#{id}")
    Map<String, Object> getUserInfoDetail(int id);

    /**
     * 根据userid 查询 客户登陆详细情况信息
     * @param id               users表id
     * @param onemonth         日期 一个月前
     * @param threemonth       日期 三个月前
     * @param sixmonth         日期 六个月前
     * @param oneyear          日期 一年前
     * @param threeyear        日期 三年前
     * @return                 用户登录详细信息
     */
    @Select("select u.id as id, count(ul.id) as totaltimes, count(if(logintime between #{onemonth} and now(),1,0)) as onemonthtimes, count(if(logintime between #{threemonth} and now(),1,0)) as threemonthtimes, count(if(logintime between #{sixmonth} and now(),1,0)) as sixmonthtimes, count(if(logintime between #{oneyear} and now(),1,0)) as oneyeartimes, count(if(logintime between #{threeyear} and now(),1,0)) as threeyeartimes from users u inner join userlogins ul on u.id=ul.userid where u.id=#{id}")
    Map<String, Object> getUserInfoLoginDetail(@Param("id")int id,
                                               @Param("onemonth")LocalDate onemonth,
                                               @Param("threemonth")LocalDate threemonth,
                                               @Param("sixmonth")LocalDate sixmonth,
                                               @Param("oneyear")LocalDate oneyear,
                                               @Param("threeyear")LocalDate threeyear);

    /**
     * 获取客户发布的总供应数量：（1）现在是状态是审核通过的供应信息
     * （2）状态是已过期的，只包括 提货时间在今天之前系统定时任务自动过期的，不包括客户
     * 修改审核通过的供应信息时，系统把老的供应信息过期那种情况。
     * （3）（2）中查询条件是，状态是已过期，但是不存在pid和它的一样并且id比它的id大的供应信息
     * （4）（3）中查询已过期条件较复杂，下面用的是简单方法：状态是已过期，并且备注是‘已过期，易煤网将该产品下架’，
     * 这是系统定时任务自动过期时的备注。
     * @param id        users表id
     * @return          客户发布的总供应数量
     */
    @Select("select sum(xx) from" +
            "(" +
            "select sum(supplyquantity) xx from sellinfo where sellerid=#{id} and status='VerifyPass'" +
            "union all" +
            " select sum(supplyquantity) xx from sellinfo where sellerid=#{id} and status='OutOfDate' and remarks='已过期，易煤网将该产品下架'" +
            " ) aa")
    Long getUserTotalSellInfoAmount(int id);

    /**
     * 获取客户已经销售的总供应数量
     * @param id        users表id
     * @return          客户已销售的总供应数量
     */
    @Select("select sum(amount) from orders where sellerid=#{id} and status='Completed'")
    Long getUserTotalOrderCompleteSoldAmount(int id);

    /**
     * 获取客户已经购买的总供应数量
     * @param id        users表id
     * @return          客户已购买的总供应数量
     */
    @Select("select sum(amount) from orders where userid=#{id} and status='Completed'")
    Long getUserTotalOrderCompleteBuyAmount(int id);

    /**
     * 获取客户发布的需求信息（审核通过）
     * @param id       users表id
     * @return         客户发布的需求总量（审核通过）
     */
    @Select("select sum(demandamount) from mydemands where userid=#{id} and status in ('交易结束', '报价中', '匹配中')")
    Long getUserTotalDemandAmount(int id);

    /**
     * 获取客户接受别人报价（购买） 总需求数量
     * @param id       users表id
     * @return         客户接受别人报价的总需求数量
     */
    @Select("select sum(q.supplyton) from quotes q inner join mydemands m on q.demandcode=m.demandcode where m.userid=#{id} and q.status='已中标'")
    Long getUserTotalQuoteBuyAmount(int id);

    /**
     * 获取客户报价成功（已中标） 总需求数量
     * @param id       users表id
     * @return         客户报价成功（已中标）总需求数量
     */
    @Select("select sum(supplyton) from quotes where userid=#{id} and status='已中标'")
    Long getUserTotalQuoteSoldAmount(int id);
}
