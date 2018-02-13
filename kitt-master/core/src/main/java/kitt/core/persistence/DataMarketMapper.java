package kitt.core.persistence;

import kitt.core.domain.Chart;
import kitt.core.domain.DataBook;
import kitt.core.domain.DataMarket;
import kitt.core.domain.DataMarketObject;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/9/21.
 * 行情数据
 */
public interface DataMarketMapper {

    /**
     * 获取所有指标name
     */
    @Select("select distinct(name) from data_market order by lastupdatetime desc")
    List<String> doGetDataMarketNameList();

    @Select("select * from data_market where name=#{name} order by date desc limit 2")
    List<DataMarket> doGetDataMarketListByName(String name);

    default List<Map<String, Object>> doGetDataMarketSiteList() {
        List<String> nameList = doGetDataMarketNameList();
        List<DataMarketObject> dataMarketObjectList = new ArrayList<>();
        if (nameList.size() > 0) {
            for (String name : nameList) {
                List<DataMarket> dataMarkets = doGetDataMarketListByName(name);
                if (dataMarkets.size() == 2) {
                    //dataMarketObjectList.add(dataMarkets.get(0).);
                }
            }
        }
        return null;
    }


    /**
     * lich--易煤行情--start
     */

    //查找数据类型
    @Select("select * from databook where  type='datamarkettype'")
    public List<DataBook> getType();

    //添加产地价格,港口价格数据
    @Insert("<script>" +
            " insert into data_market(type,name,data01,<if test='data02!=null'>data02,</if>date,createtime) values(" +
            "#{type},#{name},#{data01},<if test='data02!=null'>#{data02},</if>#{date}, now())" +
            "</script>")
    public boolean addData(DataMarket data);


    //修改产地价格,港口价格数据
    @Update("<script>" +
            " update data_market set type=#{type},name=#{name},data01=#{data01},<if test='data02!=null'>data02=#{data02},</if> date=#{date} where id=#{id}" +
            "</script>")
    public boolean updateData(DataMarket data);


    @Select("select count(*) from data_market where type=#{type} and isdelete=0")
    public int countAllDataMarketByType(int type);

    @Select("select * from data_market where type=#{type} and isdelete=0 order by sequence,lastupdatetime desc limit #{limit} offset #{offset}")
    public List<DataMarket> listAllDataMarketByType(@Param("type") int type, @Param("limit") int limit, @Param("offset") int offset);

    public default Pager<DataMarket> pageAllCHartsByType(int type, int page, int pagesize) {
        return Pager.config(this.countAllDataMarketByType(type), (int limit, int offset) -> this.listAllDataMarketByType(type, limit, offset))
                .page(page, pagesize);
    }

    @Update("update data_market set isdelete=1 where id=#{id}")
    boolean deleteData(int id);


    @Select("select date from data_market group by date order by date desc limit 2 ")
    public List<DataMarket> findDate();


    @Select("select t.name,t.dt011 as data011,t.dt022 as data012,t1.dt01 as data021,t1.dt02 as data022 ,((t1.dt02+t1.dt01)/2-(t.dt022+t.dt011)/2) as compare " +
            " from((select name ,type,isdelete,lastupdatetime, sequence,data01 as dt011 , data02 as dt022   from  data_market  t where date  = #{date1}) t" +
            " ,(select name ,type,isdelete,lastupdatetime,sequence,data01 as  dt01  , data02 as dt02  from  data_market   where date  = #{date2}) t1" +
            " ) where t.name=t1.name and t.type=t1.type  and t.isdelete=0 and t1.isdelete=0 and t.type=#{type}  order by t1.sequence,t1.lastupdatetime desc limit #{limit}")
    public List<DataMarket> findDataMarket(@Param("date1") String date1, @Param("date2") String date2, @Param("type") int type, @Param("limit") int limit);


    @Select("select * from data_market t where t.name=#{name} and t.date=#{date} and t.isdelete=0 and t.type=#{type} ")
    public List<DataMarket> findByNameAndDate(@Param("name") String name, @Param("date")String date,@Param("type")int type);


    @Select("SELECT a.name FROM (SELECT * FROM data_market  where name like #{name} and isdelete=0 and type=#{type} ORDER BY lastupdatetime DESC) a   GROUP BY a.name order by a.lastupdatetime desc limit 15")
    public List<String> findByName(@Param("name") String name,@Param("type")int type);

    @Update("update data_market set sequence=#{sequence} where id=#{id}")
    boolean doChangeAdverpicSequenceMethod(@Param("id")int id, @Param("sequence")int sequence);
}
