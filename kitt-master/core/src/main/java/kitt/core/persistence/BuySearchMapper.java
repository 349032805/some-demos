package kitt.core.persistence;

import kitt.core.domain.SellInfo;
import kitt.core.util.PageQueryParam;
import kitt.ext.mybatis.Where;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jack on 6/2/15.
 */
public interface BuySearchMapper {
    String SellInfoSearch =
                    "<if test='coaltype!=null'> pname=#{coaltype}</if>" +
                    "<if test='regionId!=0'> and regionId=#{regionId}</if>" +
                    "<if test='provinceId!=0'> and provinceId=#{provinceId}</if>" +
                    "<if test='portId &gt; 0'> and portId=#{portId}</if>" +
                    "<if test='portId &lt; 0'> and otherharbour is not null and otherharbour!=''</if>" +
                    "<if test='NCV01!=0'> and (NCV &gt;= #{NCV01} or NCV02 &gt;= #{NCV01})</if>" +
                    "<if test='NCV02!=7500'> and (NCV &lt;= #{NCV02} or NCV02 &lt;= #{NCV02})</if>" +
                    "<if test='ADV01!=0'> and (ADV &gt;= #{ADV01} or ADV02 &gt;= #{ADV01})</if>" +
                    "<if test='ADV02!=50'> and (ADV &lt;= #{ADV02} or ADV02 &lt;= #{ADV02})</if>" +
                    "<if test='RV01!=0'> and (RV &gt;= #{RV01} or RV02 &gt;= #{RV01})</if>" +
                    "<if test='RV02!=50'> and (RV &lt;= #{RV02} or RV02 &lt;= #{RV02})</if>" +
                    "<if test='RS01!=0'> and (RS &gt;= #{RS01} or RS02 &gt;= #{RS01})</if>" +
                    "<if test='RS02!=10'> and (RS &lt;= #{RS02} or RS02 &lt;= #{RS02})</if>" +
                    "<if test='ADS01!=0'> and (ADS &gt;= #{ADS01} or ADS02 &gt;= #{ADS01})</if>" +
                    "<if test='ADS02!=10'> and (ADS &lt;= #{ADS02} or ADS02 &lt;= #{ADS02})</if>" +
                    "<if test='TM01!=0'> and (TM &gt;= #{TM01} or TM02 &gt;= #{TM01})</if>" +
                    "<if test='TM02!=50'> and (TM &lt;= #{TM02} or TM02 &lt;= #{TM02})</if>" +
                    "<if test='IM01!=0'> and (IM &gt;= #{IM01} or IM02 &gt;= #{IM01})</if>" +
                    "<if test='IM02!=50'> and (IM &lt;= #{IM02} or IM02 &lt;= #{IM02})</if>" +
                    "<if test='ASH01!=0'> and (ASH &gt;= #{ASH01} or ASH02 &gt;= #{ASH01})</if>" +
                    "<if test='ASH02!=50'> and (ASH &lt;= #{ASH02} or ASH02 &lt;= #{ASH02})</if>" +
                    "<if test='G01!=0'> and (GV &gt;= #{G01} or GV02 &gt;= #{G01})</if>" +
                    "<if test='G02!=100'> and (GV &lt;= #{G02} or GV02 &lt;= #{G02})</if>" +
                    "<if test='Y01!=0'> and (YV &gt;= #{Y01} or YV02 &gt;= #{Y01})</if>" +
                    "<if test='Y02!=100'> and (YV &lt;= #{Y02} or YV02 &lt;= #{Y02})</if>" +
                    "<if test='FC01!=0'> and (FC &gt;= #{FC01} or FC02 &gt;= #{FC01})</if>" +
                    "<if test='FC02!=100'> and (FC &lt;= #{FC02} or FC02 &lt;= #{FC02})</if>" +
                    "<if test='AFT01!=900'> and AFT &gt;= #{AFT01}</if>" +
                    "<if test='AFT02!=1600'> and AFT &lt;= #{AFT02}</if>" +
                    "<if test='HGI01!=0'> and HGI &gt;= #{HGI01}</if>" +
                    "<if test='HGI02!=1000'> and HGI &lt;= #{HGI02}</if>" +
                    "<if test='PS!=0'> and PS=#{PS}</if>" +
                    "<if test='type==0'> and type=#{type}</if>" +
                    "<if test='setPicTrueOnly==0'> and ((chemicalexam1 is not null and chemicalexam1!='') " +
                            " or (chemicalexam2 is not null and chemicalexam2!='') or (chemicalexam3 is not null and chemicalexam3!='') " +
                            " or (coalpic1 is not null and coalpic1!='') or (coalpic2 is not null and coalpic2!='') or (coalpic3 is not null and coalpic3!='')" +
                            " or (coalpic4 is not null and coalpic4!='') or (coalpic5 is not null and coalpic5!='') or (coalpic6 is not null and coalpic6!='')) </if>";
    final String StatusVerifyPass = " and status='VerifyPass'";
    final String SellInfoAvailQuantity = " and availquantity > 0";

    final String OrderBy = "order by " +
            "<if test='sorttype==0'> createdate desc ,</if>" +
            "<if test='sorttype==2'> " +
            "<if test='sequence==0'>ykj+IFNULL(jtjlast,0) desc ,</if>" +
            "<if test='sequence==1'>ykj+IFNULL(jtjlast,0) asc ,</if>" +
            "</if>" +
            "<if test='sorttype==1'>" +
            "<if test='sequence==0'> soldquantity desc ,</if>" +
            "<if test='sequence==1'> soldquantity asc ,</if>" +
            "</if>" +
            "seller desc, createtime desc ";
    final String limitOffset = " limit #{limit} offset #{offset}";

    //查询供应信息列表，新的搜索方法
    @Select("<script> select * from sellinfo " +
            Where.where +
            SellInfoSearch /*+ SellInfoAvailQuantity*/ + StatusVerifyPass +
            Where.$where + OrderBy + limitOffset +
            "</script>")
    List<SellInfo> getSellInfoSearchList(@Param("coaltype")String coaltype,
                                         @Param("regionId") int regionId,
                                         @Param("provinceId") int provinceId,
                                         @Param("portId") int portId,
                                         @Param("NCV01") Integer NCV01,
                                         @Param("NCV02") Integer NCV02,
                                         @Param("ADV01") BigDecimal ADV01,
                                         @Param("ADV02") BigDecimal ADV02,
                                         @Param("RV01") BigDecimal RV01,
                                         @Param("RV02") BigDecimal RV02,
                                         @Param("RS01") BigDecimal RS01,
                                         @Param("RS02") BigDecimal RS02,
                                         @Param("ADS01") BigDecimal ADS01,
                                         @Param("ADS02") BigDecimal ADS02,
                                         @Param("TM01") BigDecimal TM01,
                                         @Param("TM02") BigDecimal TM02,
                                         @Param("IM01") BigDecimal IM01,
                                         @Param("IM02") BigDecimal IM02,
                                         @Param("ASH01") BigDecimal ASH01,
                                         @Param("ASH02") BigDecimal ASH02,
                                         @Param("G01") Integer G01,
                                         @Param("G02") Integer G02,
                                         @Param("Y01") BigDecimal Y01,
                                         @Param("Y02") BigDecimal Y02,
                                         @Param("FC01") Integer FC01,
                                         @Param("FC02") Integer FC02,
                                         @Param("AFT01") Integer AFT01,
                                         @Param("AFT02") Integer AFT02,
                                         @Param("HGI01") Integer HGI01,
                                         @Param("HGI02") Integer HGI02,
                                         @Param("PS") Integer PS,
                                         @Param("type") Integer type,
                                         @Param("setPicTrueOnly") Integer setPicTrueOnly,
                                         @Param("sorttype")int sorttype,
                                         @Param("sequence")int sequence,
                                         @Param("limit")int limit,
                                         @Param("offset")int offset);

    @Select("<script>select count(*) from sellinfo " +
            Where.where +
            SellInfoSearch /*+ SellInfoAvailQuantity*/ + StatusVerifyPass +
            Where.$where + " </script>")
    int getSellInfoSearchCount(@Param("coaltype")String coaltype,
                               @Param("regionId") int regionId,
                               @Param("provinceId") int provinceId,
                               @Param("portId") int portId,
                               @Param("NCV01") Integer NCV01,
                               @Param("NCV02") Integer NCV02,
                               @Param("ADV01") BigDecimal ADV01,
                               @Param("ADV02") BigDecimal ADV02,
                               @Param("RV01") BigDecimal RV01,
                               @Param("RV02") BigDecimal RV02,
                               @Param("RS01") BigDecimal RS01,
                               @Param("RS02") BigDecimal RS02,
                               @Param("ADS01") BigDecimal ADS01,
                               @Param("ADS02") BigDecimal ADS02,
                               @Param("TM01") BigDecimal TM01,
                               @Param("TM02") BigDecimal TM02,
                               @Param("IM01") BigDecimal IM01,
                               @Param("IM02") BigDecimal IM02,
                               @Param("ASH01") BigDecimal ASH01,
                               @Param("ASH02") BigDecimal ASH02,
                               @Param("G01") Integer G01,
                               @Param("G02") Integer G02,
                               @Param("Y01") BigDecimal Y01,
                               @Param("Y02") BigDecimal Y02,
                               @Param("FC01") Integer FC01,
                               @Param("FC02") Integer FC02,
                               @Param("AFT01") Integer AFT01,
                               @Param("AFT02") Integer AFT02,
                               @Param("HGI01") Integer HGI01,
                               @Param("HGI02") Integer HGI02,
                               @Param("PS") Integer PS,
                               @Param("type")Integer type,
                               @Param("setPicTrueOnly") Integer setPicTrueOnly);

    @Select("<script>" +
            "select * from sellinfo where status='VerifyPass'" +
            "<if test='pid != null and pid !=\"\"'> and pid like #{pid}</if>" +
            "<if test='shopid != 0'> and shopid=#{shopid} </if>" +
            "<if test='shopid == null or shopid == \"\"'> and availquantity>0 </if>" +
            " order by createtime desc " +
            " limit #{pagesize} offset #{offset}" +
            "</script>")
    List<SellInfo> getSellInfoListByPid(@Param("pid")String pid,
                                        @Param("shopid")int shopid,
                                        @Param("pagesize")int pagesize,
                                        @Param("offset")int offset);

    @Select("<script>" +
            "select count(1) from sellinfo where status='VerifyPass' " +
            "<if test='pid != null and pid != \"\"'> and pid like #{pid}</if>" +
            "<if test='shopid != 0'> and shopid=#{shopid} </if>" +
            "<if test='shopid == null or shopid == \"\"'> and availquantity>0 </if>" +
            "</script>")
    int getSellInfoCountByPid(@Param("pid")String pid,
                              @Param("shopid")int shopid);


    @Select("<script>" +
            "select count(id) from sellinfo where pname=#{pname} and status='VerifyPass' and availquantity>0" +
            "<if test='type == 0'> and type=#{type}</if>" +
            "</script>")
    int getCoalCountByTypeSeller(@Param("pname")String pname,
                                 @Param("type")Integer type);

    /**
     * 供应信息详细页面, 同类产品
     */
    @Select("<script> select * from sellinfo " +
            Where.where +
            " ${SQLStr} " + StatusVerifyPass +
            Where.$where + " order by id desc limit 5" +
            "</script>")
    List<SellInfo> getSimilarSellInfoList(@Param("SQLStr") String SQLStr);

    default List<SellInfo> getSimilarSellInfoList(SellInfo sellInfo) {
        Integer NCV = (int)(sellInfo.getNCV() * 0.7) < 1 ? 1 : (int)(sellInfo.getNCV() * 0.7);
        Integer NCV02 = (int)(sellInfo.getNCV02() * 1.3) > 7500 ? 7500 : (int)(sellInfo.getNCV02() * 1.3);
        Double RS = sellInfo.getRS().doubleValue() - 0.1 < 0.1 ? 0.1 : sellInfo.getRS().doubleValue() - 0.1;
        Double RS02 = sellInfo.getRS02().doubleValue() + 0.1 > 10.0 ? 10.0 : sellInfo.getRS02().doubleValue() + 0.1;
        String SimilarSQL1 = " and id!=" + sellInfo.getId() + " and pname='" + sellInfo.getPname() + "'" + " and NCV >=" + NCV + " and NCV02 <=" + NCV02 +
                " and RS >=" + RS + " and RS02 <=" + RS02;
        String SimilarSQL2 = SimilarSQL1 + " and regionId=" + sellInfo.getRegionId() + " and provinceId=" + sellInfo.getProvinceId() + " and deliveryplace='" + sellInfo.getDeliveryplace() + "'";
        List<SellInfo> sellInfoList2 = getSimilarSellInfoList(SimilarSQL2);
        if (sellInfoList2.size() >= 3) return sellInfoList2;
        else return getSimilarSellInfoList(SimilarSQL1);
    }

    /**
     * 最新产品
     */
    @Select("select * from sellinfo where status='VerifyPass' and id!=#{id} order by id desc limit 5")
    List<SellInfo> getLatestSellInfoList(int id);

    /**
     * 热销产品
     */
    @Select("select * from sellinfo where soldquantity>0 and status='VerifyPass' and id!=#{id} order by id desc limit 5")
    List<SellInfo> getHotestSellInfoList(int id);

    /**
    * 微信搜索
    */
    @Select("select count(1) from sellinfo where pid like #{content} and status='VerifyPass'")
    int getWapSearchSellInfoCount(@Param("content")String content);

    @Select("<script>" +
            " select * from sellinfo where pid like #{content} and status='VerifyPass' " +
            " order by " +
            " <if test='sortType==\"price\"'> (ykj+IFNULL(jtjlast, 0)) </if>" +
            " <if test='sortType!=\"price\"'> ${sortType} </if>" +
            " ${sortOrder} " +
            " <choose><when test='anchor==null or anchor==\"\"'>limit #{pageQuery.pagesize} offset #{pageQuery.indexNum}</when><otherwise>limit ${pageQuery.rowNum}  offset 0</otherwise></choose>" +
            "</script>")
    List<SellInfo> getWapSearchSellInfoList(@Param("content")String content,
                                            @Param("pageQuery") PageQueryParam param,
                                            @Param("sortType") String sortType,
                                            @Param("sortOrder") String sortOrder,
                                            @Param("anchor") String scrtop);

    public default PageQueryParam getWapSearchSellInfoTotalList(String content, PageQueryParam param, String sortType, String sortOrder, String scrtop) {
        content = Where.$like$(content);
        int totalCount = getWapSearchSellInfoCount(content);
        List<SellInfo> sellInfoList = getWapSearchSellInfoList(content, param, sortType, sortOrder, scrtop);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(sellInfoList);
        //如果有锚点,加载完数据，告诉前台当前是第几页
        if (org.apache.commons.lang3.StringUtils.isNotBlank(scrtop)) {
            param.setPage(param.getIndexNum() / param.getPagesize());
        }
        return param;
    }
}
