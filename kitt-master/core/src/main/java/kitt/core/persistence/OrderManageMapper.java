package kitt.core.persistence;

import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 15/1/22.
 */
public interface OrderManageMapper {

    String OrderSelectElementSQL =
            "<if test='params.orderid!=null and params.orderid!=\"\"'> and (o.orderid like #{params.orderid} or o.pid like #{params.orderid})</if>" +
            "<if test='params.deliveryRegion!=0'> and o.regionId=${params.deliveryRegion}</if>" +
            "<if test='params.deliveryProvince!=0'> and o.provinceId=${params.deliveryProvince}</if>" +
            "<if test='params.deliveryHarbour!=0'> and o.portId=${params.deliveryHarbour}</if>" +
            "<if test='params.clienttype!=0'> and o.clienttype=#{params.clienttype}</if>" +
            "<if test='params.startDate!=null and params.endDate!=null'> and DATE_FORMAT(o.createtime, '%Y-%m-%d') between  #{params.startDate} and  #{params.endDate}</if> " +
            "<if test='params.startDate!=null and params.endDate==null'> and DATE_FORMAT(o.createtime, '%Y-%m-%d') between  #{params.startDate} and  DATE_FORMAT(now(), '%Y-%m-%d')</if>"+
            "<if test='params.orderType!=null'> and o.ordertype=#{params.orderType}</if>" +
            "<if test='params.status==\"cancelway\"'> and (o.status='Canceled' or (o.status='Deleted' and os.status='Canceled' ))</if>" +
            "<if test='params.status==\"matchunderway\"'> and o.status='MakeMatch' and o.sellerstatus not in ('Completed', 'Canceled') </if>" +
            "<if test='params.status==\"matchcompleteway\"'> and ( o.status='Completed' or (o.status='Deleted' and os.status='Completed' and os.remarks='确认完成') ) </if>" +
            "<if test='params.status==\"matchrecheckway\"'> and o.status='MakeMatch' and o.sellerstatus in ('Completed', 'Canceled') </if>" +
            "<if test='params.status==\"underway\"'> and o.status in ('WaitPayment', 'WaitVerify', 'VerifyPass', 'VerifyNotPass', 'WaitBalancePayment') and o.sellerstatus not in ('Completed', 'Canceled') </if>" +
            "<if test='params.status==\"returnway\"'> and o.status='ReturnGoods' and o.sellerstatus!='ReturnCompleted' </if>" +
            "<if test='params.status==\"completeway\"'> and (o.status in ('Completed', 'ReturnCompleted') or (o.status='Deleted' and os.status in ('Completed', 'ReturnCompleted')) ) </if>" +
            "<if test='params.status==\"recheckway\"'> and o.status in ('WaitPayment', 'WaitVerify', 'VerifyPass', 'VerifyNotPass', 'WaitBalancePayment', 'ReturnGoods') and o.sellerstatus in ('Completed', 'Canceled', 'ReturnCompleted') </if>" +
            //"<if test='params.orderStatus!=null'> and o.status in <foreach collection='params.orderStatus' index='index' item='item' open='(' separator=',' close=')'>#{item}</foreach></if>"+
            //"<if test='params.sellerStatus!=null'><foreach collection='params.sellerStatus' index='i' item='item'> and o.sellerstatus!=#{item} </foreach></if>" +
            "";

    @Select("<script>" +
            " select IFNULL(SUM(`totalmoney`), 0) from orders o " +
            " inner join (select * from ordersinfo order by id desc ) os on o.id=os.oid " +
            Where.where + OrderSelectElementSQL + Where.$where +
            "</script>")
    public BigDecimal countTotalMoneyByStatus(@Param("params")Map<String, Object> params);

    @Select("<script>" +
            " select count(distinct(o.id)) from orders o " +
            " inner join ordersinfo os on o.id=os.oid " +
            Where.where + OrderSelectElementSQL + Where.$where +
            "</script>")
    int countOrderByStatus(@Param("params")Map<String, Object> params);


    @Select("<script>" +
            " select o.*, os.createtime as completetime, os.status as ostatus from  orders o " +
            " inner join ordersinfo os on o.id=os.oid " +
            Where.where + OrderSelectElementSQL +
            " and os.id=(select max(id) as id from ordersinfo where oid=o.id and status!='Deleted') " + Where.$where +
            " order by o.createtime desc limit #{limit} offset #{offset}" +
            "</script>")
    List<Map<String, Object>> findOrder(@Param("params")Map<String, Object> params,
                                        @Param("limit") int page,
                                        @Param("offset") int offset);

    @Select("<script>" +
            "select o.orderid, o.pid, o.userid,o.seller,s.pname,o.deliveryplace, os.createtime as completetime, o.createtime,s.NCV,s.RS,s.ADV,s.RV,s.ASH,s.TM,s.IM,o.amount,o.price,o.totalmoney,o.dealername,o.clienttype from orders o " +
            " inner join sellinfo s on o.sellinfoid=s.id " +
            " inner join ordersinfo os on os.id=(select max(id) as id from ordersinfo where status!='Deleted' and oid=o.id) " +
            Where.where + OrderSelectElementSQL + Where.$where +
            " order by o.createtime desc" +
            "</script>")
    List<Map<String, Object>> ExportOrder(@Param("params") Map<String, Object> parmas);

    default List<Map<String, Object>> ExportOrderMethod(@Param("params") Map<String, Object> params){
        params.put("orderid", Where.$like$((String)params.get("orderid")));
        return ExportOrder(params);
    }

    public default Pager<Map<String, Object>> findOrderByStatus(Map<String,Object> params, int page, int pagesize){
        params.put("orderid", Where.$like$((String)params.get("orderid")));
        return Pager.config(this.countOrderByStatus(params), (int limit, int offset) -> this.findOrder(params, limit, offset)).page(page, pagesize);
    }


}
