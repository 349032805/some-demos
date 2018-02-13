package kitt.core.persistence;



import kitt.core.domain.Logisticsfeedback;
import kitt.core.domain.PushStatusRequest;
import org.apache.ibatis.annotations.*;


/**
 * Created by lich on 15/12/22.
 */
public interface LogisticsfeedbackMapper {
    @Select("select * from logisticsfeedback where souceId=#{souceId}")
    Logisticsfeedback findDetailBySouceId(@Param("souceId") String souceId);

    @Insert("<script>" +
            "insert into logisticsfeedback(" +
            "<if test='ordercode !=null and ordercode!=\"\"'>ordercode,</if>" +
            "<if test='vendercreatetime!=null and vendercreatetime!=\"\"'>vendercreatetime,</if>" +
            "<if test='transportstartdate!=null and transportstartdate!=\"\"'>transportstartdate,</if>" +
            "<if test='transportenddate!=null and transportenddate!=\"\"'>transportenddate,</if>" +
            "<if test='transportprices!=0'>transportprices,</if>" +
            "<if test='totalprice!=0'>totalprice,</if>" +
            "<if test='logisticsphone!=null and logisticsphone!=\"\"'>logisticsphone,</if>" +
            "<if test='comment!=null and comment!=\"\"'>comment,</if>" +
            "<if test='venderid!=0'>venderid,</if>" +
            "<if test='intentionid!=0'>sintentionid,</if>" +
            "souceId,status,createtime) values(" +
            "<if test='ordercode!=null and ordercode!=\"\"'>#{ordercode},</if>" +
            "<if test='vendercreatetime!=null and vendercreatetime!=\"\"'>#{vendercreatetime},</if>" +
            "<if test='transportstartdate!=null and transportstartdate!=\"\"'>#{transportstartdate},</if>" +
            "<if test='transportenddate!=null and transportenddate!=\"\"'>#{transportenddate},</if>" +
            "<if test='transportprices!=0'>#{transportprices},</if>" +
            "<if test='totalprice!=0'>#{totalprice},</if>" +
            "<if test='logisticsphone!=null and logisticsphone!=\"\"'>#{logisticsphone},</if>" +
            "<if test='comment!=null and comment!=\"\"'>#{comment},</if>" +
            "<if test='venderid!=0'>#{venderid},</if>" +
            "<if test='intentionid!=0'>#{intentionid},</if>" +
            "#{souceId},#{status},#{createtime})" +
            "</script>")
    public void addLogisticsfeedback(PushStatusRequest request);


    @Update("<script>" +
            "update logisticsfeedback set " +
            "<if test='ordercode!=null and ordercode!=\"\"'> ordercode=#{ordercode},</if>" +
            "<if test='vendercreatetime!=null and vendercreatetime!=\"\"'> vendercreatetime=#{vendercreatetime},</if>" +
            "<if test='transportstartdate!=null and transportstartdate!=\"\"'> transportstartdate=#{transportstartdate},</if>" +
            "<if test='transportenddate!=null and transportenddate!=\"\"'> transportenddate=#{transportenddate},</if>" +
            "<if test='transportprices!=0'> transportprices=#{transportprices},</if>" +
            "<if test='totalprice!=0'> totalprice=#{totalprice},</if>" +
            "<if test='logisticsphone!=null and logisticsphone!=\"\"'> logisticsphone=#{logisticsphone},</if>" +
            "<if test='comment!=null and comment!=\"\"'> comment=#{comment},</if>" +
            "status=#{status}" +
            "where souceId=#{souceId}" +
            "</script>")
    public int updateLogisticsfeedback(PushStatusRequest request);


        @Update("update logisticsfeedback set comment=#{comment}, status=#{status} where souceId=#{souceId}")
        public int updatecomment(@Param("comment") String comment,@Param("status") String status,@Param("souceId") String souceId );




}
