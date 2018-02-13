package kitt.core.persistence;

import kitt.core.domain.Customer;
import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by xiangyang on 15-1-14.
 */

public interface MyCustomerMapper {
    //查询客户列表
    @Select("<script>select u.id id,u.securephone nickname,c.name companyName,c.legalpersonname contact ,c.phone phone from users u left join companies c on u.id = c.userid " +
            /*"where u.id in (select  userid from (" +
            "select  userid from demands <if test=\'admin.isAdmin!=true\'>where tradercode = #{admin.jobnum}</if>" +
            " union " +
            "select  sellerid from sellinfo <if test='admin.isAdmin!=true'>where dealerid =#{admin.jobnum}</if>" +
            ") t) */
            "where u.verifystatus='审核通过' <if test='keyWord!=null and keyWord!=\"\"'> and (u.nickname like #{keyWord}  or u.securephone like #{keyWord} or c.name like #{keyWord})</if>" +
            "limit #{limit} offset #{offset}</script>")
    public List<Customer> showAllCustomer(@Param("keyWord")String keyWord,@Param("limit")int limit,@Param("offset")int offset);


    @Select("<script>select count(1) from users u left join companies c on u.id = c.userid " +
            /*"where u.id in(select sellerid from (select  sellerid from sellinfo <if test='admin.isAdmin!=true'>where dealerid=#{admin.jobnum}</if>" +
            "   union  " +
            "select  userid from demands <if test='admin.isAdmin!=true'>where tradercode = #{admin.jobnum}</if>" +
            ") t) */
            "where u.verifystatus='审核通过' <if test='keyWord!=null and keyWord!=\"\"'> and (u.nickname like #{keyWord}  or u.securephone like #{keyWord} or c.name like #{keyWord})</if>" +
            "</script>")
    public int countAllCustomer(@Param("keyWord")String keyWord);

    @Select(" select * from ( (select sum(amount) shopOrderCount from orders where userid=#{id} and status in('Completed','ReturnCompleted','Deleted') \n" +
            "and sellerstatus !='Deleted' and seller='自营') a, (select sum(amount) unShopOrderCount from \n" +
            "orders where userId=#{id} and status = 'Completed' and sellerstatus !='Deleted' and seller!='自营')b, \n" +
            "(select count(1) releaseDemandCount,sum(purchasednum) demandMatchCount from demands where userId=#{id} and (checkstatus ='审核通过' or tradestatus='报价结束')) demand, (select count(1) quoteCount, count( case when status = '已中标' \n" +
            "then 1 else 0 end) validQuoteCount from quotes where userId = #{id}) quote, (select count(1) \n" +
            "supplyCount,sum(soldquantity) matchSupplyCount from sellinfo where sellerid = #{id} and status in('VerifyPass','OutOfDate')) \n" +
            "sellinfo, (select c.name companyName,c.phone from users u,companies c where u.id=c.userid and u.id=#{id}) uc ) \n")
    public Customer load(int id);

    @Select("select u.id from users u left join companies c  on u.id = c.userid  where  u.nickname=#{keyword}  or u.securephone=#{keyword} or c.name = #{keyword}")
    public Integer searchCustomerIdByCondition(@Param("keyword")String keyword);

    public default Pager<Customer> getAllCustomerList(String keyWord,int page, int pagesize){

        return  Pager.config(this.countAllCustomer(Where.$like$(keyWord)), (int limit, int offset) -> this.showAllCustomer(Where.$like$(keyWord),limit, offset))
                .page(page, pagesize);
    }

    public default  Customer customerSearch(String keyword){
        if(null != searchCustomerIdByCondition(keyword)){
            return this.load(searchCustomerIdByCondition(keyword));
        }
        return null;
    }


}
