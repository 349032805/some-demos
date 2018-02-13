package kitt.core.persistence;

import kitt.core.domain.LogisticsShipFeedback;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by lich on 16/1/12.
 */
public interface LogisticsshipfeedbackMapper {
    @Select("select * from logisticsshipfeedback where souceId=#{souceId}")
    LogisticsShipFeedback findFeedBackBySouceId(@Param("souceId") String souceId);

    @Insert("<script>" +
            "insert into logisticsshipfeedback(" +
            "<if test='transportcompany !=null and transportcompany!=\"\"'>transportcompany,</if>" +
            "<if test='carrier!=null and carrier!=\"\"'>carrier,</if>" +
            "<if test='DWT!=0'>DWT,</if>" +
            "<if test='detentionday!=0'>detentionday,</if>" +
            "<if test='paytype!=null and paytype!=\"\"'>paytype,</if>" +
            "<if test='settlementtype!=null and settlementtype!=\"\"'>settlementtype,</if>" +
            "needbill," +
            "<if test='transportprices!=null and transportprices!=0'>transportprices,</if>" +
            "<if test='totalprice!=0'>totalprice,</if>" +
            "<if test='otherprice!=0'>otherprice,</if>" +
            "<if test='detentioncharge!=0'>detentioncharge,</if>" +
            "<if test='shipname!=null and shipname!=\"\"'>shipname,</if>" +
            "<if test='emptyport!=null and emptyport!=\"\"'>emptyport,</if>" +
            "<if test='emptydate!=null and emptydate!=\"\"'>emptydate,</if>" +
            "<if test='carrierphone!=null and carrierphone!=\"\"'>carrierphone,</if>" +
            "<if test='ccempname!=null and ccempname!=\"\"'>ccempname,</if>" +
            "<if test='ccempphone!=null and ccempphone!=\"\"'>ccempphone,</if>" +
            "<if test='pricetype!=null and pricetype!=\"\"'>pricetype,</if>" +
            "<if test='detentionchargetype!=null and detentionchargetype!=\"\"'>detentionchargetype,</if>" +
            "<if test='remark!=null and remark!=\"\"'>remark,</if>" +
            "<if test='venderid!=0'>venderid,</if>" +
            "souceId,status,createtime) values(" +
            "<if test='transportcompany !=null and transportcompany!=\"\"'>#{transportcompany},</if>" +
            "<if test='carrier!=null and carrier!=\"\"'>#{carrier},</if>" +
            "<if test='DWT!=0'>#{DWT},</if>" +
            "<if test='detentionday!=0'>#{detentionday},</if>" +
            "<if test='paytype!=null and paytype!=\"\"'>#{paytype},</if>" +
            "<if test='settlementtype!=null and settlementtype!=\"\"'>#{settlementtype},</if>" +
            "#{needbill}," +
            "<if test='transportprices!=0'>#{transportprices},</if>" +
            "<if test='totalprice!=0'>#{totalprice},</if>" +
            "<if test='otherprice!=0'>#{otherprice},</if>" +
            "<if test='detentioncharge!=0'>#{detentioncharge},</if>" +
            "<if test='shipname!=null and shipname!=\"\"'>#{shipname},</if>" +
            "<if test='emptyport!=null and emptyport!=\"\"'>#{emptyport},</if>" +
            "<if test='emptydate!=null and emptydate!=\"\"'>#{emptydate},</if>" +
            "<if test='carrierphone!=null and carrierphone!=\"\"'>#{carrierphone},</if>" +
            "<if test='ccempname!=null and ccempname!=\"\"'>#{ccempname},</if>" +
            "<if test='ccempphone!=null and ccempphone!=\"\"'>#{ccempphone},</if>" +
            "<if test='pricetype!=null and pricetype!=\"\"'>#{pricetype},</if>" +
            "<if test='detentionchargetype!=null and detentionchargetype!=\"\"'>#{detentionchargetype},</if>" +
            "<if test='remark!=null and remark!=\"\"'>#{remark},</if>" +
            "<if test='venderid!=0'>#{venderid},</if>" +
            "#{souceId},#{status},#{createtime})" +
            "</script>")
    public void addLogisticsShipfeedback(LogisticsShipFeedback back);


        @Update("<script>update logisticsshipfeedback set status=#{status},paytotalamount=#{paytotalamount},paytotalweight=#{paytotalweight}" +
                "<if test='contractattachurl!=null and contractattachurl!=\"\"'>,contractattachurl=#{contractattachurl}</if>" +
                "  where souceId=#{souceId}</script>")
        public int updateLogisticsShipfeedback(LogisticsShipFeedback back);


        @Update("update logisticsshipfeedback set status=#{status},remark=#{remark} where souceId=#{souceId}")
        public int updateShipfeedback(@Param("souceId")String No,@Param("remark")String remark,@Param("status")String status);
        //info.setSouceId(request.getNo());
        //info.setDetentioncharge(request.getDemurrageFees());
        //info.setDetentionchargetype(request.getDemurrageFeesType());


        @Update("<script>" +
                "update logisticsshipfeedback " +
                " <set>" +
                "needbill=#{needbill}" +
                "<if test='pricetype!=null and pricetype!=\"\"'>,pricetype=#{pricetype}</if>" +
                "<if test='transportprices!=null and transportprices!=0'>,transportprices=#{transportprices}</if>" +
                "<if test='DWT!=0'>,DWT=#{DWT}</if>" +
                "<if test='detentionday!=0'>,detentionday=#{detentionday}</if>" +
                "<if test='emptyport!=null and emptyport!=\"\"'>,emptyport=#{emptyport}</if>" +
                "<if test='emptydate!=null and emptydate!=\"\"'>,emptydate=#{emptydate}</if>" +
                "<if test='totalprice!=null and totalprice!=0'>,totalprice=#{totalprice}</if>" +
                "<if test='remark!=null and remark!=\"\"'>,remark=#{remark}</if>" +
                "<if test='otherprice!=null and otherprice!=0'>,otherprice=#{otherprice}</if>" +
                "<if test='detentioncharge!=null and detentioncharge!=0'>,detentioncharge=#{detentioncharge}</if>" +
                "<if test='detentionchargetype!=null and detentionchargetype!=\"\"'>,detentionchargetype=#{detentionchargetype}</if>" +
                " </set> " +
                " where souceId=#{souceId}" +
                "</script>")
        public int updatefeedback(LogisticsShipFeedback back);

}
