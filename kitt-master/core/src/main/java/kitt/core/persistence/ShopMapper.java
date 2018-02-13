package kitt.core.persistence;

import kitt.core.domain.*;

import kitt.core.domain.rs.CoalZoneRs;
import kitt.core.util.PageQueryParam;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.util.*;

/**
 * Created by liuxinjie on 15/9/9.
 */
public interface ShopMapper {

    //所有店铺列表
    @Select("<script>" +
            "select * from shop order by status, level, createtime desc limit #{limit} offset #{offset}" +
            "</script>")
    List<Shop> getShopList(@Param("limit") int page,
                           @Param("offset") int offset);

    //店铺个数
    @Select("select count(1) from shop")
    int getShopCount();

    //店铺Pager
    default Pager<Shop> getShopListPager(int page, int pagesize){
        return Pager.config(this.getShopCount(), (int limit, int offset) -> this.getShopList(limit, offset)).page(page, pagesize);
    }

    //根据id查询店铺
    @Select("select * from shop where id=#{id}")
    Shop getShopById(int id);

    //根据userid查询店铺
    @Select("select * from shop where userid=#{userid}")
    Shop getShopByUserid(int userid);

    //根据id查询已上架的店铺
    @Select("select * from shop where id=#{id} and status=1")
    Shop getActiveShopById(int id);

    //保存店铺（保存草稿）
    @Insert("insert into shop(userid, companyname, name, level, createtime, adverpic, logo, featuretext, " +
            " featurepic001, featurepic002, featurepic003, featurepic004, partnertext, partnerpic001, " +
            " partnerpic002, location,provinceId,cityId,slogan) values(#{userid}, " +
            "#{companyname}, #{name}, #{level}, #{createtime}, #{adverpic}, #{logo},  #{featuretext}, #{featurepic001}, #{featurepic002}," +
            "#{featurepic003}, #{featurepic004}, #{partnertext}, #{partnerpic001}, #{partnerpic002}, #{location},#{provinceId},#{cityId},#{slogan})")
    int doSaveShop(Shop shop);

    //更新店铺（更新草稿）
    @Update("update shop set name=#{name},companyname=#{companyname},level=#{level},adverpic=#{adverpic},logo=#{logo}," +
            " featuretext=#{featuretext}, featurepic001=#{featurepic001}, featurepic002=#{featurepic002}," +
            " featurepic003=#{featurepic003}, featurepic004=#{featurepic004}," +
            " partnertext=#{partnertext}, partnerpic001=#{partnerpic001},partnerpic002=#{partnerpic002},provinceId=#{provinceId},cityId=#{cityId},slogan=#{slogan} " +
            " location=#{location}" +
            " where id=#{id} ")
    int doUpdateSaveShop(Shop shop);

    //添加店铺（新增）
    @Insert("insert into shop(userid, companyname, shopid, name, level, createtime, status, adverpic, logo, " +
            "featuretext, featurepic001, featurepic002, featurepic003, featurepic004, partnertext, " +
            "partnerpic001, partnerpic002, location,provinceId,cityId,slogan) values(#{userid}, #{companyname}, code_six('MK'), " +
            "#{name}, #{level}, #{createtime}, #{status}, #{adverpic}, #{logo}, #{featuretext}, #{featurepic001}, " +
            "#{featurepic002}, #{featurepic003}, #{featurepic004}, #{partnertext}, #{partnerpic001}, " +
            "#{partnerpic002}, #{location},#{provinceId},#{cityId},#{slogan})")
    int doAddShop(Shop shop);

    //更新店铺(提交）
    @Update("<script>" +
            " update shop set shopid=code_six('MK'),status=#{status},name=#{name},companyname=#{companyname},level=#{level}," +
            " <if test='adverpic!=null and adverpic != \"\"'>adverpic=#{adverpic},</if>" +
            " <if test='logo!=null and logo != \"\"'>logo=#{logo},</if>" +
            " featuretext=#{featuretext}, featurepic001=#{featurepic001}, featurepic002=#{featurepic002}," +
            " featurepic003=#{featurepic003}, featurepic004=#{featurepic004}," +
            " partnertext=#{partnertext}, partnerpic001=#{partnerpic001},partnerpic002=#{partnerpic002}, " +
            " location=#{location},provinceId=#{provinceId},cityId=#{cityId},slogan=#{slogan} where id=#{id} " +
            "</script>")
    int doUpdateAddShop(Shop shop);

    //上架，下架店铺
    @Update("update shop set status=#{status} where id=#{id}")
    int doUpDownShopMethod(@Param("id")Integer id,
                           @Param("status")int status);

    //更改店铺顺序
    @Update("update shop set sequence=#{sequence} where id=#{id}")
    boolean doChangeShopSequenceMethod(@Param("id")int id,
                                       @Param("sequence")int sequence);

    //前台宣传区域店铺列表
    @Select("select * from shop where status=1 order by sequence asc")
    List<Shop> getAllShopListBySequence();

    //


    /************************************** 旗舰店产品 **************************************/
    /************************************** 旗舰店产品 **************************************/
    /************************************** 旗舰店产品 **************************************/

    String WhereShopStr =
            "<where>" +
                    "shopid=#{shopid} and status='VerifyPass'" +
                    "<if test='price01 != null and price01 != 0'> and (ykj+IFNULL(jtjlast, 0)) &gt;= #{price01}</if>" +
                    "<if test='price02 != null and price02 != 0'> and (ykj+IFNULL(jtjlast, 0)) &lt;= #{price02}</if>" +
                    "<if test='pname != null and pname != \"\"'> and pname=#{pname}</if>" +
                    "<if test='portId != null and portId != 0 and portId != \"\"'> and portId=#{portId}</if>" +
                    "</where>";
    String OrderByShopStr =
            "<if test='sortType==\"0\"'>" +
                    "<if test='sortOrder==1'> order by createtime desc</if>" +
                    "<if test='sortOrder==0'> order by createtime asc</if>" +
            "</if>" +
            "<if test='sortType==\"1\"'>" +
                    "<if test='sortOrder==1'> order by soldquantity desc</if>" +
                    "<if test='sortOrder==0'> order by soldquantity asc</if>" +
            "</if>" +
            "<if test='sortType==\"2\"'>" +
                    "<if test='sortOrder==1'> order by price desc</if>" +
                    "<if test='sortOrder==0'> order by price asc</if>" +
            "</if>";

    @Select("<script>" +
            "select *, (ykj+IFNULL(jtjlast, 0)) as price from sellinfo" + WhereShopStr + OrderByShopStr +
            " limit #{limit} offset #{offset} " +
            "</script>")
    List<SellInfo> getSelectSellInfoListByShopId(@Param("shopid")int shopid,
                                                 @Param("price01")Integer price01,
                                                 @Param("price02")Integer price02,
                                                 @Param("pname")String pname,
                                                 @Param("portId")Integer portId,
                                                 @Param("sortType")Integer sortType,
                                                 @Param("sortOrder")Integer sortOrder,
                                                 @Param("limit")int limit,
                                                 @Param("offset")int offset);

    @Select("<script>" +
            "select count(1) price from sellinfo" + WhereShopStr +
            "</script>")
    int getSelectSellInfoCountByShopId(@Param("shopid")int shopid,
                                       @Param("price01")Integer price01,
                                       @Param("price02")Integer price02,
                                       @Param("pname")String pname,
                                       @Param("portId")Integer portId,
                                       @Param("sortType")Integer sortType,
                                       @Param("sortOrder")Integer sortOrder);


    //查询一个店铺的所有产品
    @Select("select * from sellinfo where status='VerifyPass' and shopid=#{id}")
    List<SellInfo> getAllSellInfoListByShopId(int id);


    @Select("select distinct(portId) from sellinfo where shopid=#{shopid} and status='VerifyPass'")
    List<Integer> getSellInfoAllHarbourIdByShopId(int shopid);

    @Select("select * from areaports where id=#{id}")
    Areaport getAreaportById(int id);

    //查询一个店铺的所有港口（有产品在线的港口）
    default List<Areaport> getSellInfoHarbourListByShopId(int id){
        List<Integer> portIdList = getSellInfoAllHarbourIdByShopId(id);
        List<Areaport> areaportList = new ArrayList<>();
        for(Integer integer : portIdList){
            Areaport areaport = getAreaportById(integer);
            if(areaport != null) {
                areaportList.add(areaport);
            }
        }
        areaportList.add(new Areaport(-1,  "其它"));
        return areaportList;
    }


    //删除店铺
    @Delete("delete from shop where id=#{id}")
    public int deleteShopById(int id);

    //统计店铺名称是否重名
    @Select("select * from shop where name=#{name} limit 1")
    public Shop countShopByName(String name);

    //已经关联的公司名称集合
    @Select("select companyname from shop limit 1")
    public List<String> getUsedCompanyname();

    //单独更新特色文字描述
    @Update("update shop set featuretext=#{featuretext} where id=#{id}")
    public void modifyFeatureWordById(@Param("id")int id,@Param("featuretext")String featuretext);

    //单独更新特色文字描述
    @Update("update shop set partnertext=#{partnertext} where id=#{id}")
    public void modifyPartnerWordById(@Param("id")int id,@Param("partnertext")String partnertext);

    //查询顺序是否存在
    @Select("select count(*) from shop where sequence=#{sequence}")
    public int countSequence(int sequence);

    //判断店铺是否已经使用过某公司名称
    @Select("select * from shop where companyname=#{companyname} limit 1")
    public Shop countShopCompanyname(String companyname);

    //设置店铺发布的供应信息上架或者下架
    @Update("update sellinfo set status=#{status1} where shopid=#{shopid} and status=#{status2} and deliverytime2 >= now()")
    int updateShopSellInfoStatus(@Param("status1")EnumSellInfo status1,
                                 @Param("status2")EnumSellInfo status2,
                                 @Param("shopid")int shopid);

    //设置待上架的，但是已过期的shop 供应信息 状态位 已过期
    @Update("update sellinfo set status='OutOfDate' where shopid=#{shopid} and status='WaitShopRun' and deliverytime2 < now()")
    int setShopSellInfoOutOfDate(int shopid);

    //查询一个店铺包含的煤炭种类
    @Select("select distinct(pname) from sellinfo where status='VerifyPass' and shopid=#{shopid}")
    List<String> getCoalTypeListByShopId(int shopid);

    //查询一个店铺包含的煤炭种类, 最低价
    @Select("select distinct(pname) as coaltype, min(ykj + IFNULL(jtjlast,0)) as price, ykj from sellinfo where status='VerifyPass' and shopid=#{shopid} group by pname order by price")
    List<Map<String, Object>> getCoalTypePriceListByShopId(int shopid);

    default List<ShopObject> getAllShopObjectShowList(){
        List<ShopObject> shopObjectList = new ArrayList<>();
        List<Shop> shopList = getAllShopListBySequence();
        List<String> allCoalTypeList = Arrays.asList(new String[]{"动力煤", "无烟煤", "喷吹煤", "焦煤"});
        for(Shop shop : shopList){
            List<Map<String, Object>> shopCoalTypeMap = getCoalTypePriceListByShopId(shop.getId());
            for(String coaltype : allCoalTypeList){
                if(!getCoalTypeListByShopId(shop.getId()).contains(coaltype)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("coaltype", coaltype);
                    map.put("price", 0);
                    shopCoalTypeMap.add(map);
                }
            }
            shopObjectList.add(new ShopObject(shop, shopCoalTypeMap));
        }
        return shopObjectList;
    }


    /***********************首页煤矿专区展示*********************/
    //煤矿企业个数
    @Select("select count(1) from shop where status=1")
    public int countCoalEnterprise();

    //煤矿专区产品个数
    @Select("select count(se.id) from sellinfo se ,shop s where se.shopid=s.id and  s.status=1 " +
            "and se.status='VerifyPass'  ")
    public  int countSupplyWithInCoalZone();

    //按产品:按照所在地&煤矿企业个数 e.x:内蒙古(20)
    @Select("select a.code provinceId,replace(replace(replace(a.name,'省',''),'市','') ,'自治区','') location,count(s.id) supplyCount from shop s ,regionym a " +
            "where s.provinceId=a.code   and s.status=1 group by a.code order by convert(a.name using gbk)")
    public List<CoalZoneRs> findCoalEnterprise();

    //按煤矿企业统计:按煤矿企业统计 内蒙古: 蒙泰集团 远兴能源
    @Select("select a.code provinceId,replace(replace(replace(a.name,'省',''),'市','') ,'自治区','') location from shop s,regionym a where s.provinceId=a.code  and  s.status=1  group by a.code order by convert(a.name using gbk) ")
    @Results(value= {
            @Result(property = "id", column = "id"),
            @Result(property = "companyname", column = "companyname"),
            @Result(property = "shops", column = "provinceId",
                    javaType = List.class,
                    many = @Many(select = "kitt.core.persistence.ShopMapper.findCoalZoneShop"))
    })
    public List<CoalZoneRs> findAllCoalEnterprise();

    @Select("select s.id,REPLACE(s.name, '官方旗舰店','')name  from shop s,companies c  " +
            " where s.provinceId=#{value} and  c.userid=s.userid and  s.status=1 order by convert(s.name using gbk)  ")
    public List<Shop> findCoalZoneShop(int provinceId);

    //煤矿专区商品煤种统计
    @Select("<script> select s.pname coaltype,count(s.id)  supplyCount from sellinfo s,shop  sh " +
            " where  s.shopid = sh.id  and sh.status=1  and s.status='VerifyPass'   group by s.pname </script>")
    public  List<CoalZoneRs> coalTypeWithInCoalZone();

    String coalZoneSqlByShop=" <if test='rs.provinceId!=null'> and s.provinceId=#{rs.provinceId}</if>" +
            "<if test='rs.coaltype!=null'> and se.pname=#{rs.coaltype} </if>" +
            "<if test='rs.deliverymode!=null'> and se.deliverymode=#{rs.deliverymode} </if>" +
            "<if test='rs.NCV01!=null and rs.NCV02!=null'> and (se.NCV between #{rs.NCV01} and  #{rs.NCV02}  or se.NCV02 between #{rs.NCV01} and #{rs.NCV02}) </if>" +
            "<if test='rs.TM01!=null and rs.TM02!=null'> and (se.TM between #{rs.TM01} and  #{rs.TM02}  or se.TM02 between #{rs.TM01} and #{rs.TM02}) </if>" +
            "<if test='rs.RS01!=null and rs.RS02!=null'> and (se.RS between #{rs.RS01} and  #{rs.RS02}  or se.RS02 between #{rs.RS01} and #{rs.RS02}) </if>" +
            "<if test='rs.ADV01!=null and rs.ADV02!=null'> and (se.ADV between #{rs.ADV01} and  #{rs.ADV02}  or se.ADV02 between #{rs.ADV01} and #{rs.ADV02}) </if>" +
            "<if test='rs.price01!=null and rs.price02==null'> and case ykj when 0 then jtjlast else ykj end  <![CDATA[>=]]>  #{rs.price01} </if>" +
            "<if test='rs.price01==null and rs.price02!=null'> and case ykj when 0 then jtjlast else ykj end <![CDATA[<=]]> #{rs.price02} </if>" +
            "<if test='rs.price01!=null and rs.price02!=null'> and case ykj when 0 then jtjlast else ykj end between  #{rs.price01} and #{rs.price02} </if> "+
            "<if test='rs.shopId!=null'> and s.id=#{rs.shopId}</if>";

    @Select("<script>select count(1) from (" +
            "select count(s.id) from shop s left join sellinfo se  on se.shopid=s.id where  s.status=1  and (se.status='VerifyPass'  or se.status is null )" +
            coalZoneSqlByShop + "group by s.id ) t </script>")
    public int findCoalZoneShopCount(@Param("rs") CoalZoneRs rs);


    @Select("<script>" +
            "select shopId,provinceName,location,logo,slogan,price,sum(soldquantity) soldquantity from (" +
            "select s.id shopId,a.name provinceName,case  when a.name =(select name from regionym where code=s.cityid)  then REPLACE(a.name,'市','') " +
            "else " +
            "           CONCAT (REPLACE(REPLACE(a.name,'省',''),'自治区','') ,REPLACE((select name from regionym where code=s.cityid),'市',''))  end location,s.logo,s.slogan," +
            "IFNULL(ykj,0)+IFNULL(jtjlast,0) price,IFNULL(soldquantity,0) soldquantity " +
            "from shop s left join sellinfo se on se.shopid=s.id left join regionym a on a.code=s.provinceId " +
            "where s.status=1 " +coalZoneSqlByShop+
            "  <if test='orderType.field==\"price\"'>order by IFNULL(ykj,0)+IFNULL(jtjlast,0) </if>" +
            ")  t group by shopId  order by ${orderType.field} ${orderType.direction}" +
            " limit #{page.pagesize} offset #{page.indexNum}</script>")
    public List<CoalZoneRs> findCoalZoneShopList(@Param("rs") CoalZoneRs rs, @Param("page") CoalZoneRs.CoalZoneRsPage pageQueryParam, @Param("orderType")OrderByType orderType);


    @Select("<script>select count(se.id) " +
            "  from sellinfo se ,shop s where se.shopid=s.id " +
            "  and se.id and s.status=1 and se.status='VerifyPass'  "+coalZoneSqlByShop +" </script>")
    public int findCoalZoneSupplyCount(@Param("rs") CoalZoneRs rs);


    @Select("<script>" +
            "select * from ("+
            "select se.shopid,s.name,se.id supplyId,s.logo,se.soldquantity,se.supplyquantity supplyQuantity ,se.pname coaltype, " +
            "(select name from regionym where code=s.provinceid) provincename, "+
            "case se.ykj when 0 then se.jtjlast else ykj end price,case se.deliveryplace when '其它' then se.otherharbour " +
            "else se.deliveryplace end deliveryplace,se.NCV NCV01,se.NCV02 ,se.TM TM01,se.TM02,se.RS RS01,se.RS02,se.deliverymode,se.availquantity " +
            "availQuantity,case se.paymode when 1 then '现汇' when 2 then '账期' when 3 then '银行承兑汇票' when 4 " +
            "then '现汇+银行承兑汇票' end paymode from sellinfo se ,shop s where se.shopid=s.id  and s.status=1 and  se.availquantity <![CDATA[ > ]]> 49  " +coalZoneSqlByShop +
            " and se.status='VerifyPass' " +
            " order by <choose><when test='orderType.field==\"shopName\"'> convert(provincename using gbk) asc,  convert(s.name using gbk) asc</when><otherwise>${orderType.field} ${orderType.direction}</otherwise></choose> ,se.createtime desc) t1 " +
            "union all " +
            "select se.shopid,s.name,se.id supplyId,s.logo,se.soldquantity,se.supplyquantity supplyQuantity,se.pname coaltype," +
            "(select name from regionym where code=s.provinceid) provincename, "+
            "case se.ykj when 0 then se.jtjlast else ykj end price,case se.deliveryplace when '其它' then se.otherharbour " +
            "else se.deliveryplace end deliveryplace,se.NCV NCV01,se.NCV02 ,se.TM TM01,se.TM02,se.RS RS01,se.RS02,se.deliverymode,se.availquantity " +
            "availQuantity,case se.paymode when 1 then '现汇' when 2 then '账期' when 3 then '银行承兑汇票' when 4 " +
            "then '现汇+银行承兑汇票' end paymode "+
            " from sellinfo se ,shop s where se.shopid=s.id  and s.status=1  and  se.availquantity <![CDATA[ <= ]]> 49  "+coalZoneSqlByShop +

            "limit #{page.pagesize} offset #{page.indexNum} </script>")
    public List<CoalZoneRs> findCoalZoneSupply(@Param("rs") CoalZoneRs rs,@Param("page") CoalZoneRs.CoalZoneRsPage pageQueryParam, @Param("orderType")OrderByType orderType);


    //店铺主要供应
    @Select(" select coaltype," +
            "case when sum(supplyquantity)/10000 >1 then ROUND(sum(supplyquantity) /10000,0)" +
            "else  ROUND(sum(supplyquantity) /10000,2) end " +
            " supplyQuantity,price,pnameCount from (" +
            "  select pname coaltype, sum(supplyquantity) supplyquantity ,min(case ykj when 0 then jtjlast else ykj end) price, " +
            "            count(id) pnameCount from sellinfo where shopid=#{value} and status ='VerifyPass' group by pname " +
            "            order by supplyquantity desc " +
            "            ) t   ")
    public CoalZoneRs findMajorProduct(int shopId);


    //首页-煤炭专区,卡片列表
    @Select("select s.id shopId," +
            "case  when a.name =(select name from regionym where code=s.cityid)  then REPLACE(a.name,'市','')" +
            "else " +
            "           CONCAT (REPLACE(REPLACE(a.name,'省',''),'自治区','') ,REPLACE((select name from regionym where code=s.cityid),'市',''))  end location,s.logo,s.slogan " +
            " from shop s left join sellinfo se on se.shopid=s.id left  join regionym a on a.code=s.provinceId   " +
            "where   s.status=1  group by s.id  order by s.sequence")
    public List<CoalZoneRs> getIndexShopList();
}
