package kitt.core.persistence;

import kitt.core.domain.Reservation;
import kitt.core.domain.ReservationResult;
import kitt.core.domain.result.ReservationRs;
import kitt.core.util.PageQueryParam;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by xiangyang on 16/1/6.
 */
public interface ReservationMapper {

    @Insert("insert into reservation (customerId,supplyId,amount,deliveryDate,createtime, type) " +
            "values(#{customerId},#{supplyId},#{amount},#{deliveryDate},now(),#{type})")
    @Options(useGeneratedKeys = true)
    public int addReservation(Reservation reservation);

    @Select("<script>" +
            "select count(distinct(r.id)) from reservation r left join ReservationResult  rs on r.id=rs.reservationid left join sellinfo s on s.id=r.supplyId left join  companies c on c.userid=r.customerId "+
            "<where>" +
            "<if test='companyName!=\"\"'>and c.name like '%' #{companyName} '%'</if>" +
            "<if test='supplyId!=\"\"'>and s.pid like '%' #{supplyId} '%' </if>" +
            "<if test='brokerGroupId!=0'> and rs.brokerGroupId=#{brokerGroupId} and  rs.id in(select max(id) from ReservationResult  group by reservationid)</if>" +
            "<if test='brokerId!=0'> and rs.brokerId=#{brokerId} and  rs.id in(select max(id) from ReservationResult  group by reservationid)</if>" +
            "<if test='startDate!=null and endDate!=null'> and date(r.createtime) between #{startDate} and #{endDate}</if>" +
            "<if test='startDate!=null'> and date(r.createtime) between #{startDate} and CURDATE()</if>"+
            "</where></script>")
    public int count(ReservationRs reservationRs);

    @Select("<script>" +
            "select  t1.*, t2.brokerName,IFNULL(t2.status,1)  status ," +
            "t2.onlineBroker,t2.brokerGroupId,t2.brokerId,t2.brokerSuggest,t2.customerServiceSuggest,t2.revisitDate " +
            "from (select r.id,r.createtime reservationDate, r.type, s.id sellinfoId, s.logistics, s.finance, c.name companyName,c.legalpersonname linkman,s.pid supplyId,c.address compayAddress,u.securephone phone,s.pname coalType,r.amount,r.deliveryDate from reservation r left join companies c on r.customerid=c.userid  left join sellinfo s on s.id=r.supplyId left join users u on u.id=r.customerid order by r.id ) t1 " +
            "left join (select   reservationid id,concat(br.teamName,'-',a.name) brokerName , " +
            "rs.status,rs.onlineBroker,rs.brokerGroupId,rs.brokerId,rs.brokerSuggest,rs.createtime revisitDate,rs.customerServiceSuggest from  ReservationResult  rs left join admins a on a.id=rs.brokerid left join brokerteam br on br.id=rs.brokerGroupId " +
            "where rs.id in(select max(id) from ReservationResult  group by reservationid)) t2 on t1.id=t2.id "+
            "<where>"+
            "<if test='reservation.supplyId!=\"\"'> and t1.supplyId like '%' #{reservation.supplyId} '%'</if>" +
            "<if test='reservation.companyName!=\"\"'> and t1.companyName like '%' #{reservation.companyName} '%'</if>" +
            "<if test='reservation.startDate!=null and reservation.endDate!=null'> and date(t1.reservationDate) between #{reservation.startDate} and #{reservation.endDate}</if>" +
            "<if test='reservation.startDate!=null'> and date(t1.reservationDate) between #{reservation.startDate} and CURDATE()</if>"+
            "<if test='reservation.brokerId!=0'> and t2.brokerId=#{reservation.brokerId}</if>" +
            "<if test='reservation.brokerGroupId!=0'> and t2.brokerGroupId=#{reservation.brokerGroupId}</if>"+
            "</where>"+
          " order by t1.id desc<if test='page!=null'>limit 10 offset ${page.indexNum}</if></script>")
    public List<ReservationRs> list(@Param("page") PageQueryParam pageParam, @Param("reservation") ReservationRs reservationRs);


    @Insert("insert into ReservationResult (reservationId,brokerGroupId,brokerId,adminId,onlineBroker,brokerSuggest,customerServiceSuggest,createtime,status) values(#{reservationId},#{brokerGroupId},#{brokerId},#{adminId},#{onlineBroker},#{brokerSuggest},#{customerServiceSuggest},#{createtime},#{status}) ")
    public void addReservationRs(ReservationResult reservationResult);

    @Select("select * from reservation where id=#{value}")
    public Reservation load(int id);

    @Select("select  t1.*, t2.brokerName,IFNULL(t2.status,1)  status ," +
            "t2.onlineBroker,t2.brokerGroupId,t2.brokerId,t2.brokerSuggest,t2.customerServiceSuggest,t2.revisitDate " +
            " from (select r.id,r.createtime reservationDate, r.type,s.id sellinfoId, s.logistics, s.finance, c.name companyName,c.legalpersonname linkman,s.pid supplyId,c.address compayAddress,u.securephone phone,s.pname coalType,r.amount,r.deliveryDate from reservation r left join companies c on r.customerid=c.userid  left join sellinfo s on s.id=r.supplyId left join users u on u.id=r.customerid  order by r.id ) t1 left join (select   reservationid id,concat(br.teamName,'-',a.name) brokerName , " +
            "  rs.status,rs.onlineBroker,rs.brokerGroupId,rs.brokerId,rs.brokerSuggest,rs.createtime revisitDate,rs.customerServiceSuggest from  ReservationResult  rs left join admins a on a.id=rs.brokerid left join brokerteam br on br.id=rs.brokerGroupId order by createtime desc limit 1) t2 on t1.id=t2.id where t1.id=#{value}")
    public  ReservationRs loadReservationDetail(int id);

    @Select("select r.id,r.brokerSuggest,concat(br.teamName,'-',a.name)  brokerName,r.onlineBroker,r.status,r.customerServiceSuggest,r.createtime,a.name adminName from ReservationResult r ,admins a,brokerteam br where r.brokerId=a.id  and  br.id=r.brokerGroupId and reservationId=#{value} order by r.createtime")
    public  List<ReservationRs> loadResultHistoryById(int reservationId);


    @Update("update reservation set deliveryDate=#{deliveryDate}, amount=#{amount}, type=#{type} where id=#{id}")
    public  int updateReservation(Reservation reservation);


    @Select("select count(id) from reservation where customerId=#{userId} and date(createtime)=CURDATE() ")
    public int reservationCount(@Param("userId") int userId);

    @Select("select id,amount,deliveryDate from reservation where customerId=#{userId} and supplyId=#{supplyId} and date(createtime)=CURDATE() ")
    public Reservation countByUserId(@Param("userId") int userId,@Param("supplyId") int supplyId);


}
