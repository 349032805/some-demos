package kitt.core.persistence;

import kitt.core.domain.*;
import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import org.apache.ibatis.annotations.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 14/12/31.
 */
public interface OrderMapper {
    //订单
    final String limitOffset = " limit #{limit} offset #{offset}";
    final String OrderStatusSix = " and status in ( #{status1}, #{status2}, #{status3}, #{status4}, #{status5}, #{status6} ) ";
    final String OrderStatusSeven = " and status in ( #{status1}, #{status2}, #{status3}, #{status4}, #{status5}, #{status6}, #{status7} ) ";
    final String OrderStatusOne = " and status=#{status}";
    final String OrderStatusOneIncludeDeleted = " and (o.status=#{status} or (o.status='Deleted' and os.status=#{status})) ";
    final String OrderStatusTwo = " and status in ( #{status1}, #{status2}) ";
    final String SellerStatusDelete = "and sellerstatus!='Deleted' ";
    final String Userid = " and userid=#{userid} ";
    final String Sellerid = " and sellerid=#{sellerid}";
    final String OrderType = " and ordertype=#{ordertype}";
    final String OrderNumberSQL = "<if test='content!=null'> and orderid like #{content} </if>";
    final String OrderNumberByPidSQL = "<if test='pid!=null'> and pid like #{pid} </if>";


    /**
     * 个人中心 - 我的订单列表 - 买家 - 两种状态
     */
    final String BuyerTwoStatusOrderStr1 = Where.where + Userid  + OrderStatusTwo + Where.$where;
    @Select("<script>" +
            " select * from orders" + BuyerTwoStatusOrderStr1 +
            " order by createtime desc limit #{limit} offset #{offset}" +
            "</script>")
    public List<Order> getTwoStatusOrdersBuy(@Param("userid") int userid,
                                             @Param("status1")EnumOrder status1,
                                             @Param("status2")EnumOrder status2,
                                             @Param("limit") int limit,
                                             @Param("offset") int offset);
    @Select("<script>" +
            " select count(id) from orders " + BuyerTwoStatusOrderStr1 +
            "</script>")
    public int countTwoStatusOrdersBuy(@Param("userid")int userid,
                                       @Param("status1")EnumOrder status1,
                                       @Param("status2")EnumOrder status2);


    /**
     * 个人中心 - 我的订单列表 - 买家 - 一种状态
     */
    final String BuyerOneStatusOrderStr1 = Where.where + Userid + OrderStatusOne + Where.$where;
    @Select("<script>" +
            " select * from orders" + BuyerOneStatusOrderStr1 +
            " order by createtime desc limit #{limit} offset #{offset}" +
            "</script>")
    public List<Order> getOneStatusOrdersBuy(@Param("userid") int userid,
                                             @Param("status")EnumOrder status,
                                             @Param("limit") int limit,
                                             @Param("offset") int offset);
    @Select("<script>" +
            " select count(id) from orders" + BuyerOneStatusOrderStr1 +
            "</script>")
    public int countOneStatusOrdersBuy(@Param("userid") int userid,
                                       @Param("status")EnumOrder status);


    /**
     * 个人中心 - 我的订单列表 - 买家 - 六种状态
     */
    final String BuyerSixStatusOrderStr1 = Where.where + Userid + OrderStatusSix + Where.$where;
    @Select("<script>" +
            " select * from orders" + BuyerSixStatusOrderStr1 +
            " order by createtime desc limit #{limit} offset #{offset} " +
            "</script>")
    public List<Order> getSixStatusOrdersBuy(@Param("userid") int userid,
                                             @Param("status1")EnumOrder status1,
                                             @Param("status2")EnumOrder status2,
                                             @Param("status3")EnumOrder status3,
                                             @Param("status4")EnumOrder status4,
                                             @Param("status5")EnumOrder status5,
                                             @Param("status6")EnumOrder status6,
                                             @Param("limit") int limit,
                                             @Param("offset") int offset);
    @Select("<script>" +
            " select count(id) from orders " + BuyerSixStatusOrderStr1 +
            "</script>")
    public int countSixStatusOrdersBuy(@Param("userid") int userid,
                                       @Param("status1")EnumOrder status1,
                                       @Param("status2")EnumOrder status2,
                                       @Param("status3")EnumOrder status3,
                                       @Param("status4")EnumOrder status4,
                                       @Param("status5")EnumOrder status5,
                                       @Param("status6")EnumOrder status6);


    /**
     * 查询一个供应信息是否有进行中的订单
     */
    @Select("<script>" +
            "select count(id) from orders" +
            Where.where +  "pid=#{pid} " + OrderStatusSeven + Where.$where +
            "</script>")
    public int countSellInfoUnderWayOrders(@Param("pid") String pid,
                                           @Param("status1")EnumOrder status1,
                                           @Param("status2")EnumOrder status2,
                                           @Param("status3")EnumOrder status3,
                                           @Param("status4")EnumOrder status4,
                                           @Param("status5")EnumOrder status5,
                                           @Param("status6")EnumOrder status6,
                                           @Param("status7")EnumOrder status7);

    /**
     * 个人中心 - 我的订单列表 - 卖家订单 - 一种状态
     */
    final String SellerOneStatusOrderStr1 = Where.where + Sellerid + OrderStatusOne + SellerStatusDelete + OrderType + Where.$where;
    @Select("<script>" +
            " select * from orders" + SellerOneStatusOrderStr1 +
            " order by createtime desc limit #{limit} offset #{offset}" +
            "</script>")
    public List<Order> getOneStatusOrdersSell(@Param("sellerid") int sellerid,
                                              @Param("status") EnumOrder status,
                                              @Param("ordertype") EnumOrder ordertype,
                                              @Param("limit") int limit,
                                              @Param("offset") int offset);
    @Select("<script>" +
            " select count(id) from orders" + SellerOneStatusOrderStr1 +
            "</script>")
    public int countOneStatusOrdersSell(@Param("sellerid")int sellerid,
                                        @Param("status")EnumOrder status,
                                        @Param("ordertype")EnumOrder ordertype);


    /**
     * 个人中心 - 我的订单列表 - 卖家订单 - 一种状态(包括原来是这种状态,但是现在已被买家删除)
     */
    final String SellerOneStatusIncludeBuyerDeletedStr1 = Where.where + Sellerid + OrderStatusOneIncludeDeleted + SellerStatusDelete + OrderType + Where.$where;
    @Select("<script>" +
            " select * from orders o inner join ordersinfo os on o.id=os.oid " + SellerOneStatusIncludeBuyerDeletedStr1 +
            " group by o.id order by o.createtime desc limit #{limit} offset #{offset}" +
            "</script>")
    public List<Order> getOneStatusOrdersSellIncludeDeleted(@Param("sellerid") int sellerid,
                                                            @Param("status") EnumOrder status,
                                                            @Param("ordertype") EnumOrder ordertype,
                                                            @Param("limit") int limit,
                                                            @Param("offset") int offset);
    @Select("<script>" +
            " select count(distinct(o.id)) from orders o inner join ordersinfo os on o.id=os.oid " + SellerOneStatusIncludeBuyerDeletedStr1 +
            "</script>")
    public int countOneStatusOrdersSellIncludeDeleted(@Param("sellerid")int sellerid,
                                                      @Param("status")EnumOrder status,
                                                      @Param("ordertype")EnumOrder ordertype);

    //添加订单-自营
    @Insert("insert into orders(orderid, pid, sellinfoid, status, price, amount, createtime, deliverytime1, " +
            "deliverytime2, seller, deliveryregion, deliveryprovince, deliveryplace, otherharbour, deliverymode," +
            "totalmoney, waitmoney, linkman, linkmanphone, userid, ordertype, paytype, isfutures, contractno, " +
            "sellerid, traderid, dealername, dealerphone, regionId, provinceId, portId, clienttype)  " +
            "values(dateseq_next_value('ZY'), #{pid}, #{sellinfoid}, #{status}, #{price}, " +
            "#{amount}, #{createtime}, #{deliverytime1}, #{deliverytime2}, #{seller}, #{deliveryregion}, " +
            "#{deliveryprovince}, #{deliveryplace}, #{otherharbour}, #{deliverymode}, #{totalmoney}, " +
            "#{waitmoney}, #{linkman}, #{linkmanphone}, #{userid}, #{ordertype}, #{paytype}, #{isfutures}, " +
            "dateseq_next_value('HT'), #{sellerid}, #{traderid}, #{dealername}, #{dealerphone}, " +
            "#{regionId}, #{provinceId}, #{portId}, #{clienttype})")
    @Options(useGeneratedKeys=true)
    int addZYOrder(Order order);

    //添加订单-委托
    @Insert("insert into orders(orderid, pid, sellinfoid, status, price, amount, createtime, deliverytime1, " +
            "deliverytime2, seller, deliveryregion, deliveryprovince, deliveryplace, otherharbour, " +
            "deliverymode, totalmoney, linkman, linkmanphone, userid, ordertype, sellerid, traderid," +
            "dealername, dealerphone,regionId,provinceId,portId, clienttype, paymode) " +
            "values(dateseq_next_value('WT'), #{pid}, #{sellinfoid}, #{status}, #{price}, #{amount}, " +
            "#{createtime}, #{deliverytime1}, #{deliverytime2}, #{seller}, #{deliveryregion}, " +
            "#{deliveryprovince}, #{deliveryplace}, #{otherharbour}, #{deliverymode}, #{totalmoney}, " +
            "#{linkman}, #{linkmanphone}, #{userid}, #{ordertype}, #{sellerid}, #{traderid}, " +
            "#{dealername}, #{dealerphone},#{regionId},#{provinceId},#{portId}, #{clienttype}, #{paymode})")
    @Options(useGeneratedKeys=true)
    int addWTOrder(Order order);

    //修改订单-自营
    @Insert("update orders set status=#{status}, price=#{price}, amount=#{amount}, createtime=#{createtime}, " +
            "deliverytime1=#{deliverytime1}, deliverytime2=#{deliverytime2}, deliveryregion=#{deliveryregion}," +
            "deliveryprovince=#{deliveryprovince}, deliveryplace=#{deliveryplace}, otherharbour=#{otherharbour}," +
            "deliverymode=#{deliverymode}, totalmoney=#{totalmoney}, waitmoney=#{waitmoney}, " +
            "linkman=#{linkman}, linkmanphone=#{linkmanphone}, userid=#{userid}, ordertype=#{ordertype}," +
            "paytype=#{paytype}, isfutures=#{isfutures}, traderid=#{traderid}, dealername=#{dealername}, " +
            "dealerphone=#{dealerphone}, regionId=#{regionId}, provinceId=#{provinceId}," +
            "portId=#{portId}, version=version+1 where id=#{id} and version=#{version}")
    int updateZYOrder(Order order);

    //设置Order状态
    @Update("update orders set status=#{status}, version=version+1 where id=#{id}")
    int setOrderStatus(@Param("status")EnumOrder status,
                       @Param("id")int id);

    @Update("update orders set status=#{status}, version=version+1 where id=#{id} and version=#{version}")
    int setOrderStatusByIdVersion(@Param("status")EnumOrder status,
                                  @Param("id")int id,
                                  @Param("version")int version);

    @Update("update orders set status=#{status}, version=version+1, remarks=#{remarks} where id=#{id} and version=#{version} and status!=#{status}")
    int changeOrderStatusByIdVersion(@Param("id")int id,
                                     @Param("version")int version,
                                     @Param("status")EnumOrder status,
                                     @Param("remarks")String remarks);

    @Update("update orders set status=#{status}, sellerstatus=#{sellerstatus}, version=version+1 where id=#{id} and version=#{version}")
    int cancelOrderByIdVersion(@Param("status")EnumOrder status,
                               @Param("sellerstatus")EnumOrder sellerstatus,
                               @Param("id")int id,
                               @Param("version")int version);

    //卖家删除订单
    @Update("update orders set sellerstatus=#{sellerstatus}, version=version+1 where id=#{id} and version=#{version}")
    int setOrderSellerStatusByIdVersion(@Param("sellerstatus")EnumOrder sellerstatus,
                                        @Param("id")int id,
                                        @Param("version")int version);

    //卖家删除订单
    @Update("update orders set sellerstatus=#{sellerstatus}, version=version+1, remarks=#{remarks} where id=#{id} and version=#{version} and sellerstatus!=#{sellerstatus} ")
    int changeOrderSellerStatusByIdVersion(@Param("id")int id,
                                           @Param("version")int version,
                                           @Param("sellerstatus")EnumOrder sellerstatus,
                                           @Param("remarks")String remarks);

    //根据id查询Order
    @Select("select * from orders where id=#{id}")
    public Order getOrderById(int id);

    @Select("select * from orders where id=#{id} and version=#{version}")
    Order getOrderByIdAndVersion(@Param("id")int id,
                                 @Param("version")int version);

    @Select("select os.status as ostatus, o.* from orders o " +
            " inner join ordersinfo os on o.id=os.oid" +
            " where o.id=#{id} and o.version=#{version} and os.status!='Deleted' order by os.id desc limit 1")
    Order getOrderByIdAndVersionAdmin(@Param("id")int id,
                                      @Param("version")int version);

    @Select("<script>" +
            " select count(*) from orders where status=#{status} " +
            OrderNumberSQL + OrderNumberByPidSQL +
            "</script>")
    public int countOrderBySelect(@Param("status")EnumOrder status,
                                  @Param("content")String content,
                                  @Param("pid")String pid);
    @Select("<script>" +
            " select * from orders where status=#{status} " +
            OrderNumberSQL + OrderNumberByPidSQL +
            " order by createtime desc " + limitOffset +
            "</script>")
    public List<Order> listOrderBySelect(@Param("status") EnumOrder status,
                                         @Param("content") String content,
                                         @Param("pid") String pid,
                                         @Param("limit") int limit,
                                         @Param("offset") int offset);
    public default Pager<Order> pageAllOrderBySelect(EnumOrder status, String content, String pid, int page, int pagesize){
        return Pager.config(this.countOrderBySelect(status, Where.$like$(content), Where.$like$(pid)), (int limit, int offset) ->
                this.listOrderBySelect(status, Where.$like$(content), Where.$like$(pid), limit, offset)).page(page, pagesize);
    }

    //添加审核信息
    @Insert("insert into orderverify(status, applytime, orderid, userid) values(#{status}, #{applytime}, #{orderid}, #{userid})")
    public int addOrderVerify(OrderVerify orderVerify);

    @Update("update orderverify set status=#{status}, verifyman=#{verifyman}, verifytime=#{verifytime}, remarks=#{remarks}, isverified=1 where orderid=#{orderid} and isverified=0 and status='WaitVerify'")
    int verifyOrderVerifyPaidCredit(@Param("status") EnumOrder status,
                                    @Param("verifyman") String verifyman,
                                    @Param("verifytime") LocalDateTime verifytime,
                                    @Param("remarks") String remarks,
                                    @Param("orderid") int orderid);

    @Select("select * from orders where status=#{status}")
    public List<Order> getOrderListByStatus(EnumOrder status);

    @Insert("insert into ordersinfo(status, operateman, operatemanid, createtime, oid, orderid, remarks) values(#{status}, #{operateman}, #{operatemanid}, now(), #{oid}, #{orderid}, #{remarks})")
    public int addOrdersInfo(OrdersInfo ordersInfo);

    @Update("update orders set paidmoney=#{paidmoney}, waitmoney=#{waitmoney}, savemoney=#{savemoney} where id=#{id} and status='WaitVerify'")
    public int doVerifyOrderSetMoney(@Param("id") int id,
                                     @Param("paidmoney") BigDecimal paidmoney,
                                     @Param("waitmoney") BigDecimal waitmoney,
                                     @Param("savemoney") BigDecimal savemoney);

    //admin审核订单支付凭证
    @Update("update orders set status=#{status}, remarks=#{remarks}, version=version+1 where id=#{id} and version=#{version}")
    int verifyOrderPaidCredit(@Param("status") EnumOrder status,
                              @Param("remarks") String remarks,
                              @Param("id") int id,
                              @Param("version") int version);

    //退货单--处理
    @Insert("insert into orderreturn(laststatus, orderid, oid, userid, username, iscanceled, createtime) value(#{laststatus}, #{orderid}, #{oid}, #{userid}, #{username}, #{iscanceled}, now())")
    public int addOrderReturn(OrderReturn orderReturn);

    //查询OrderReturn
    @Select("select * from orderreturn where orderid=#{orderid} and iscanceled=0 order by id desc limit 1")
    public OrderReturn getOrderReturnByOrderId(int orderid);

    //设置OrderReturn 是否已经撤销退货
    @Update("update orderreturn set iscanceled=1 where id=#{id} and iscanceled='0'")
    public int setOrderReturnIsCanceled(int id);

    //我的客户---订单
    @Select("<script>select count(1) from orders where status=#{status} and ordertype=#{ordertype} </script>")
    public int countAllOrders(@Param("status")EnumOrder status,
                                  @Param("ordertype")EnumOrder orderType);

    @Select("<script> select * from orders where status=#{status} and ordertype=#{ordertype} order by createtime desc limit #{limit} offset #{offset} </script>")
    public List<Order> showAllOrders(@Param("limit")int limit,
                                     @Param("offset")int offset,
                                     @Param("status")EnumOrder status,
                                     @Param("ordertype")EnumOrder orderType);

    public default Pager<Order> getAllOrderList(int page, int pagesize, EnumOrder status, EnumOrder orderType){
      return Pager.config(this.countAllOrders(status, orderType), (int limit, int offset) -> this.showAllOrders(limit, offset, status, orderType))
              .page(page, pagesize);
    }

    //最近成交记录 30条

    @Select("select o.amount as amount, o.status as status, s.pname as pname, s.seller as seller, if(o.deliveryplace='其它', o.otherharbour, o.deliveryplace) as harbour, DATE_FORMAT(os.lastupdatetime,'%m-%d') as time, os.lastupdatetime as lastupdatetime from orders o left join sellinfo s on o.sellinfoid = s.id left join ordersinfo os on o.id = os.oid where os.status='Completed' and os.remarks='确认完成' " +
            " union all " +
            " select q.supplyton as amount, q.status as status, d.coaltype as pname, d.demandcustomer as seller, if(d.deliveryplace='其它', d.otherplace, d.deliveryplace) as harbour, DATE_FORMAT(q.lastupdatetime,'%m-%d') as time, q.lastupdatetime as lastupdatetime from quotes q left join demands d on q.demandcode = d.demandcode where q.status='已中标' " +
            " order by lastupdatetime desc limit 30")
    public List<OrderTransaction> listRecentTransactionOrders();

    //更改订单的version
    @Update("update orders set version=version+1 where id=#{id} and version=#{version}")
    int updateOrderVersion(@Param("version")int version,
                           @Param("id")int id);

    /****************************author zxy****************************/

    //根据订单Id和用户Id查订单
    @Select("select * from orders where id=#{orderId} and userid=#{userId}")
    public Order loadOrder(@Param("orderId")int orderId,@Param("userId")int userId);

    //修改订单
    @Update("update orders set price=#{price},paytype=#{paytype},deliverytime1=#{deliverytime1},deliverytime2=#{deliverytime2},amount=#{amount},totalmoney=#{totalmoney},waitmoney=#{waitmoney} where id=#{id} and userid=#{userid}")
    public void updateOrder(Order order);


    /**
     * 查询获取成交 总数量
     * @return
     */
    @Select("select ROUND(IFNULL(sum(xx), 0)/10000,0) from" +
            "(" +
            " select IFNULL(sum(amount), 0) xx from orders where status='Completed'" +
            " union all" +
            " select IFNULL(sum(amount), 0) xx from orders o left join ordersinfo os on o.id = os.oid where o.status='Deleted' and os.status='Completed' and os.remarks='确认完成'" +
            " union all" +
            " select IFNULL(sum(supplyton), 0) xx from quotes where status='已中标'" +
            " ) aa")
    public Long getOrdersTotalAmount();

    /**
     * 查询获取成交 总金额
     * @return
     */
    @Select("select ROUND(IFNULL(sum(yy), 0)/10000,0) from" +
            "(" +
            " select IFNULL(sum(totalmoney), 0) yy from orders where status='Completed'" +
            " union all" +
            " select IFNULL(sum(totalmoney), 0) yy from orders o left join ordersinfo os on o.id = os.oid where o.status='Deleted' and os.status='Completed' and os.remarks='确认完成'" +
            "union all" +
            " select IFNULL(sum(supplyton*quote), 0) yy from quotes where status='已中标'" +
            " ) aa")
    public Long getOrdersTotalMoney();


    /**
     * 查询昨日新增
     */
    @Select("select IFNULL(sum(trading), 0) from" +
            "(" +
            " select IFNULL(sum(amount), 0) trading from orders where status='Completed' and date(lastupdatetime) >= date_sub(CURDATE(), interval 1 day) and date(lastupdatetime) < CURDATE() " +
            " union all" +
            " select IFNULL(sum(amount), 0) trading from orders o left join ordersinfo os on o.id = os.oid where o.status='Deleted' and os.status='Completed' and os.remarks='确认完成' and date(os.lastupdatetime) >= date_sub(CURDATE(), interval 1 day) and date(os.lastupdatetime) < CURDATE() " +
            " union all" +
            " select IFNULL(sum(supplyton), 0) trading from quotes where status='已中标' and date(lastupdatetime) >= date_sub(CURDATE(), interval 1 day) and date(lastupdatetime) < CURDATE() " +
            " ) total")
    public Long getTotalAmountYesterday();

}
