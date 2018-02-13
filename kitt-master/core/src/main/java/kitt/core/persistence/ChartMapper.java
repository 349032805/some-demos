package kitt.core.persistence;

import kitt.core.domain.Chart;
import kitt.core.domain.DataBook;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by fanjun on 15-2-7.
 */
public interface ChartMapper {

    @Select("select count(*) from charts where type like concat('%',#{type},'%')")
    public int countAllChartsByType(String type);

    @Select("select * from charts where type=#{type} order by tradedate desc limit #{limit} offset #{offset}")
    public List<Chart> listAllChartsByType(@Param("type") String type,
                                           @Param("limit") int limit,
                                           @Param("offset") int offset);

    public default Pager<Chart> pageAllCHartsByType(String type,int page, int pagesize){
        return Pager.config(this.countAllChartsByType(type), (int limit, int offset) -> this.listAllChartsByType(type, limit, offset))
                .page(page, pagesize);
    }

    //插入图表数据
    @Insert("insert into charts(type, tradedate, averageprice, createtime, name) values(" +
            "#{type}, #{tradedate}, #{averageprice}, now(), #{name})")
    public int addChart(Chart chart);

    //删除记录
    @Delete("delete from charts where id=#{id}")
    public void deleteOne(int id);

    //查询BSPI的最近12条记录
    @Select("select * from charts where type='BSPI' order by tradedate desc limit 12")
    public List<Chart> getAllBSPI();

    //查询易煤数据的最近12条记录
    @Select("select * from charts where type='ZSYC' order by tradedate desc limit 7")
    public List<Chart> getAllZSYC();

    //查询API8的最近10条记录
    @Select("select * from charts where type='API8' order by tradedate desc limit 10")
    public List<Chart> getAllAPI8();

    //查询TC的最近10条记录
    @Select("select * from charts where type like 'TC%' order by tradedate desc limit 10")
    public List<Chart> getAllTC();

   /* //获取图表上限和下限
    @Select("select * from chartconfine where type=#{type}")
    public ChartConfine getChartConfineByType(String type);

    //修改图表上下限
    @Update("update chartconfine set highlimit=#{highlimit},lowlimit=#{lowlimit} where type=#{type}")
    public void modifyChartConfine(ChartConfine chartConfine);

    //新增上下限
    @Insert("insert into chartconfine(type,highlimit,lowlimit) values(#{type},#{highlimit},#{lowlimit})")
    public void addChartConfine(ChartConfine chartConfine);

    //根据类型查询记录数
    @Select("select count(*) from chartconfine where type=#{type}")
    public int countChartConfineByType(String type);*/

    //验证交易日期唯一性
    @Select("select count(*) from charts where tradedate=#{tradedate} and type=#{type}")
    public int checkTradedateSole(@Param("tradedate") String tradedate,@Param("type") String type);

    //获取最新的一条TC合约记录
    @Select("select * from charts where type like 'TC%' order by createtime desc limit 1")
    public Chart getNewTC();

    /**
     * 通过type获取name
     */
    @Select("select distinct(name) from charts where type=#{type} order by name desc limit 1")
    String getNameByType(String type);

    /**
     * 通过type修改name
     */
    @Update("update charts set name=#{name} where type=#{type}")
    int changeNameByType(@Param("type") String type,
                             @Param("name") String name);
}
