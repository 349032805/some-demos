package kitt.core.persistence;

import kitt.core.domain.Logisticsintention;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * Created by lich on 15/12/14.
 */
public interface LogisticsMapper {
    //后台--物流专区列表
    @Select("select count(*) from logisticsintention where status=#{status}")
    public int countLogisIntention(@Param("status") String status);
    @Select("select * from logisticsintention where status=#{status}" +
            " order by createtime desc limit #{limit} offset #{offset}")
    public List<Logisticsintention> getPageIntention(@Param("status") String status,@Param("limit") int limit, @Param("offset") int offset);

    public default Pager<Logisticsintention> getIntentionList(String status,int page, int pagesize){
        return Pager.config(this.countLogisIntention(status), (int limit, int offset) -> this.getPageIntention(status,limit, offset))
                .page(page, pagesize);
    }
    @Insert("<script>" +
            "insert into logisticsintention(venderid,loadProvince,unLoadProvince,loadCity,unLoadCity,loadCountry,unloadCountry,loadAddDetail,unLoadAddDetail,provinceCode,cityCode,unprovinceCode,uncityCode,countryCode,uncountryCode,receivebegintime,receiveendtime," +
            "goodsType,goodsWeight,contacts,mobile,priceUp,vendername,status,createtime,userid,souceId,type"+
            "<if test='customerremark!=null'>,customerremark</if>" +
            "<if test='remark!=null'>,remark</if>" +
            "<if test='shipid!=null and shipid!=0'>,shipid</if>" +
            ") values(#{venderid},#{loadProvince},#{unLoadProvince},#{loadCity},#{unLoadCity},#{loadCountry},#{unloadCountry},#{loadAddDetail},#{unLoadAddDetail},#{provinceCode},#{cityCode},#{unprovinceCode},#{uncityCode},#{countryCode},#{uncountryCode},#{receivebegintime},#{receiveendtime}," +
              "#{goodsType},#{goodsWeight},#{contacts},#{mobile},#{priceUp},#{vendername},#{status},now(),#{userid},dateseq_next_value('WLC'),#{type}"+
            "<if test='customerremark!=null'>,#{customerremark}</if>" +
            "<if test='remark!=null'>,#{remark}</if>" +
            "<if test='shipid!=null and shipid!=0'>,#{shipid}</if>" +
            ")" +
            "</script>")
    @Options(useGeneratedKeys = true,keyProperty="id")
    public void addLogisticsintentionC(Logisticsintention logisticsintention);

    @Insert("<script>" +
            "insert into logisticsintention(venderid,loadProvince,unLoadProvince,loadCity,unLoadCity,loadCountry,unloadCountry,loadAddDetail,unLoadAddDetail,provinceCode,cityCode,unprovinceCode,uncityCode,countryCode,uncountryCode,receivebegintime,receiveendtime," +
            "goodsType,goodsWeight,contacts,mobile,priceUp,vendername,status,createtime,userid,souceId,type"+
            "<if test='customerremark!=null'>,customerremark</if>" +
            "<if test='remark!=null'>,remark</if>" +
            "<if test='shipid!=null and shipid!=0'>,shipid</if>" +
            ") values(#{venderid},#{loadProvince},#{unLoadProvince},#{loadCity},#{unLoadCity},#{loadCountry},#{unloadCountry},#{loadAddDetail},#{unLoadAddDetail},#{provinceCode},#{cityCode},#{unprovinceCode},#{uncityCode},#{countryCode},#{uncountryCode},#{receivebegintime},#{receiveendtime}," +
            "#{goodsType},#{goodsWeight},#{contacts},#{mobile},#{priceUp},#{vendername},#{status},now(),#{userid},dateseq_next_value('WLS'),#{type}"+
            "<if test='customerremark!=null'>,#{customerremark}</if>" +
            "<if test='remark!=null'>,#{remark}</if>" +
            "<if test='shipid!=null and shipid!=0'>,#{shipid}</if>" +
            ")" +
            "</script>")
    @Options(useGeneratedKeys = true,keyProperty="id")
    public void addLogisticsintentionS(Logisticsintention logisticsintention);

    @Select("<script>" +
            "select l.*,s.loaddock as loaddock,s.loadport as loadport,s.unloaddock as unloaddock,s.unloadport as unloadport,s.shipcode as shipcode,s.receiptdate as receiptdate from logisticsintention l left join shipintention s on l.shipid=s.id " +
            "<where> l. userid=#{userid} and l.isdelete=0 "+
            "<if test='status!=null and status!=\"全部\" and status!=\"MATCH_ING\" '> and l.status=#{status}</if>" +
            "<if test='status!=null and status!=\"全部\" and status==\"MATCH_ING\" '> and (l.status='MATCH_ING' or l.status='SUBMITTED_ALREADY' or l.status='TREATED_NOT')</if>" +
            "<if test='souceId!=null and souceId!=\"\" '> and l.souceId=#{souceId}</if>" +
            "<if test='type!=null and type!=-1'> and l.type=#{type}</if>" +
            "</where> order by createtime desc  limit #{startNum},#{pageSize}</script>")
    List<Map<String,Object>> findAllLogisticsintention(@Param("userid") int userid,@Param("status") String status, @Param("startNum") int startNum,
                                                       @Param("pageSize") int pageSize,@Param("souceId") String souceId,@Param("type") int type);

    @Select("<script>" +
            "select count(*) from logisticsintention l left join shipintention s on l.shipid=s.id " +
            "<where> l. userid=#{userid} and l.isdelete=0 "+
            "<if test='status!=null and status!=\"全部\" and status!=\"MATCH_ING\" '> and l.status=#{status}</if>" +
            "<if test='status!=null and status!=\"全部\" and status==\"MATCH_ING\" '> and (l.status='MATCH_ING' or l.status='SUBMITTED_ALREADY' or l.status='TREATED_NOT')</if>" +
            "<if test='souceId!=null and souceId!=\"\" '> and l.souceId=#{souceId}</if>" +
            "<if test='type!=null and type!=-1'> and l.type=#{type}</if>" +
            "</where></script>")
    int countAllLogisticsintention(@Param("userid") int userid,@Param("status") String status,@Param("souceId") String souceId,@Param("type") int type);

    @Select("<script>select count(*) from logisticsintention where  userid=#{userid} and isdelete=0 and (status='SUBMITTED_ALREADY' or status='MATCH_ING' or status='TREATED_NOT')" +
            "<if test='souceId!=null and souceId!=\"\" '> and souceId=#{souceId}</if>" +
            "<if test='type!=null and type!=-1'> and type=#{type}</if>" +
            "</script>")
    int countLogisticsintention(@Param("userid") int userid,@Param("souceId") String souceId,@Param("type") int type);


    @Select("select * from logisticsintention where userid=#{userid} and id=#{id}")
    Logisticsintention findByIdAndUserid(@Param("userid") int userid,@Param("id") int id);

    @Select("select * from logisticsintention where id=#{id} and isdelete=0")
    Logisticsintention findById(@Param("id") int id);

    @Select("select * from logisticsintention where souceId=#{souceId} and isdelete=0")
    Logisticsintention findBySouceId(@Param("souceId")String souceId);


    @Select("select * from logisticsintention where isdelete=0 and  status='COMPLETED_ALREADY' order by lastupdatetime desc limit 10")
     List<Logisticsintention>  findSuccessIntention();

    //@Select("select count(*) from logisticsintention where isdelete=0 and (status='SUBMITTED_ALREADY' or status='MATCH_ING' or status='MATCH_COMPLETED') ")
    //int  countSuccessIntention();
    //个人中心--物流专区删除
    @Update("update logisticsintention set isdelete=1 where id=#{id}")
    public int delLogisIntention(@Param("id") int id);

    //个人中心--物流专区取消
    @Update("update logisticsintention set status=#{status} where id=#{id}")
    public int cancelLogisIntention(@Param("status")String status,@Param("id") int id);

    @Update("update logisticsintention set isfinish=#{isfinish} where id=#{id}")
    public int updateIsfinish(@Param("isfinish")boolean isfinish,@Param("id") int id);


    //个人中心--物流专区评价
    @Update("<script>update logisticsintention " +
            "set servicescore=#{servicescore},logisticsscore=#{logisticsscore},isevaluated=1" +
            "<if test='serviceevaluation!=null and serviceevaluation!=\"\"'>,serviceevaluation=#{serviceevaluation}</if>" +
            "<if test='logisticsevaluation!=null and logisticsevaluation!=\"\"'>,logisticsevaluation=#{logisticsevaluation}</if>" +
            " where id=#{id}</script>")
    public int evaluateLogisIntention(@Param("serviceevaluation")String serviceevaluation,
                                      @Param("servicescore") int servicescore,
                                      @Param("logisticsevaluation")String logisticsevaluation,
                                      @Param("logisticsscore") int logisticsscore,
                                      @Param("id") int id);

    //个人中心--意向单查看详情
    @Select("select l.*,s.loaddock as loaddock,s.loadport as loadport,s.unloaddock as unloaddock,s.unloadport as unloadport,s.shipcode as shipcode,s.receiptdate as receiptdate,l.status as status from logisticsintention l left join shipintention s on l.shipid=s.id  where l.id=#{id}")
    Map<String,Object> findDetailById(@Param("id") int id);


    @Select("select  userid from  logisticsintention  where souceId=#{souceId}")
    int findUserBySouceId(@Param("souceId")String souceId);

    //---------------------add by zbl-------------------------

    @Select("select  * from  logisticsintention  where shipid=#{shipid}")
    Logisticsintention getLogisIntentionByshipid(@Param("shipid")int shipid);

    @Update("update logisticsintention set status=#{status} where souceId=#{souceId} and isdelete=0")
    public int updatLogisIntentionStatusBySouceid(@Param("status")String status,@Param("souceId")String souceId);

    @Update("update logisticsintention set customerremark=#{customerremark} where souceId=#{souceId}")
    public int updatCustomerremarkBySouceid(@Param("customerremark")String customerremark,@Param("souceId")String souceId);

    @Update("update logisticsintention set " +
            "loadProvince=#{loadProvince},provinceCode=#{provinceCode},loadCity=#{loadCity},cityCode=#{cityCode},loadCountry=#{loadCountry},loadAddDetail=#{loadAddDetail},uncountryCode=#{uncountryCode},countryCode=#{countryCode}," +
            "unLoadProvince=#{unLoadProvince},unprovinceCode=#{unprovinceCode},unLoadCity=#{unLoadCity},uncityCode=#{uncityCode},unloadCountry=#{unloadCountry},unLoadAddDetail=#{unLoadAddDetail}," +
            "goodsType=#{goodsType},goodsWeight=#{goodsWeight},priceUp=#{priceUp},contacts=#{contacts},mobile=#{mobile},vendername=#{vendername},venderid=#{venderid},customerremark=#{customerremark} where souceId=#{souceId}"
    )
    public int updateLogisIntention(Logisticsintention logisticsintention);



}
