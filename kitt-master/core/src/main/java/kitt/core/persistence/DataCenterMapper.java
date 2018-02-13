package kitt.core.persistence;

import kitt.core.domain.*;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.*;

/**
 * Created by jack on 6/9/15.
 */
public interface DataCenterMapper {

    //查询所有目录  除去已删除的
    @Select("select * from indindexcfg where isdelete=0 order by sequence")
    List<IndIndexCfg> getAllMenuList();

    //根据 parentid 查询 目录列表   除去已删除的
    @Select("select * from indindexcfg where parentid=#{parentid} and isdelete=0 order by sequence")
    List<IndIndexCfg> getMenuListByParentId(String parentid);

    //根据 parentid 查询 在前台显示的 目录列表
    @Select("select * from indindexcfg where parentid=#{parentid} and isdelete=0 and isshow=0 order by sequence")
    List<IndIndexCfg> getShowMenuListByParentId(String parentid);

    //根据 parentid 查询 目录列表   除去已删除的
    @Select("select id, name, isleaf, defaultone, isshow, display from indindexcfg where parentid=#{parentid} and isdelete=0 order by sequence")
    List<IndIndexCfg> getMenuListByParentIdSimple(String parentid);

    //根据 parentid 查询 一个 子目录
    @Select("select * from indindexcfg where parentid=#{parentid} and isdelete=0 order by sequence limit 1")
    IndIndexCfg getOneMenuByParentId(String parentid);

    //根据 parentid 查询 非叶子节点 目录列表  除去已删除的
    @Select("select * from indindexcfg where parentid=#{parentid} and isleaf=0 and isdelete=0 order by sequence")
    List<IndIndexCfg> getMenuListNotLeafByParentId(String parentid);

    //根据怕人体的 查询 叶子节点 个数  出去已删除的
    @Select("select count(id) from indindexcfg where parentid=#{parentid} and isleaf=1 and isdelete=0")
    int getMenuCountLeafByParentId(String parentid);

    //根据 id 查询 目录
    @Select("select * from indindexcfg where id=#{id} and isdelete=0")
    IndIndexCfg getMenuById(String id);

    //根据name 获取 id
    @Select("select id from indindexcfg where name=#{name} and isdelete=0 limit 1")
    String getIdByName(String name);

    //根据 level 查询 目录  除去已删除的
    @Select("select * from indindexcfg where level=#{level} and isdelete=0")
    List<IndIndexCfg> getMenuListByLevel(int level);

    //根据 parentid 查询 目录个数  除去已删除的
    @Select("select count(parentid) from indindexcfg where parentid=#{parentid} and isdelete=0")
    int getAllMenuCountByParentId(String parentid);

    //添加目录
    @Insert("insert into indindexcfg(id, name, parentid, isleaf, level, sequence, fqcy, weekdate, unit, " +
            "origin, showstyle, remarks, lastupdateuserid, createtime, createuserid, precesion,orderid) values(#{id}," +
            "#{name}, #{parentid}, #{isleaf}, #{level}, #{sequence}, #{fqcy}, #{weekdate}, #{unit}, #{origin}," +
            "#{showstyle}, #{remarks}, #{lastupdateuserid}, #{createtime}, #{createuserid}, #{precesion},#{orderid})")
    int addIndIndexCfg(IndIndexCfg indIndexCfg);

    @Update("update indindexcfg set name=#{name}, sequence=#{sequence}, fqcy=#{fqcy}, weekdate=#{weekdate}," +
            "unit=#{unit}, precesion=#{precesion}, origin=#{origin}, showstyle=#{showstyle}, remarks=#{remarks}," +
            " lastupdateuserid=#{lastupdateuserid},orderid=#{orderid} where id=#{id}")
    int updateIndIndexCfg(IndIndexCfg indIndexCfg);

    //添加目录方法
    default boolean doAddIndIndexCfg(IndIndexCfg indIndexCfg) {
        if (indIndexCfg.getParentid().equals("00")) {
            int sonIdNum = findOneAvailableId(indIndexCfg.getParentid(), 2);
            if(sonIdNum == 0){
                return false;
            }
            indIndexCfg.setId(setFixedLengthString(sonIdNum, 2));
            //设置排序id
            indIndexCfg.setOrderid(indIndexCfg.getId());
        } else {
            int sonIdNum = findOneAvailableId(indIndexCfg.getParentid(), 4);
            if(sonIdNum == 0){
                return false;
            }
            indIndexCfg.setId(indIndexCfg.getParentid() + setFixedLengthString(sonIdNum, 4));
            //设置排序id
            indIndexCfg.setOrderid(indIndexCfg.getId());
        }
        int rows = addIndIndexCfg(indIndexCfg);
        return rows == 1 ? true : false;
    }

    default int findOneAvailableId(String parentId, int length){
        int totalSons = getAllMenuCountByParentId(parentId) + 1;
        int maxNum = 9;
        for(int i=1; i<length; i++){
            maxNum = maxNum*10 + 9;
        }
        for(int i=1; i<=totalSons; i++){
            IndIndexCfg indIndexCfg;
            if(parentId.equals("00")){
                indIndexCfg = getMenuById(setFixedLengthString(i, length));
            } else {
                indIndexCfg = getMenuById(parentId + setFixedLengthString(i, length));
            }
            if(indIndexCfg == null){
                return i;
            }
        }
        do{
            IndIndexCfg indIndexCfg = getMenuById(parentId+setFixedLengthString(totalSons + 1, length));
            if(indIndexCfg == null){
                return totalSons + 1;
            }
            totalSons++;
        }while(totalSons < maxNum);
        return 0;
    }

    //修改目录方法
    default boolean doUpdateIndIndexCfg(IndIndexCfg indIndexCfg) {
        int rows = updateIndIndexCfg(indIndexCfg);
        return rows == 1 ? true : false;
    }

    //设置目录 id
    default String setFixedLengthString(int num, int length) {
        String resLength = "";
        for (int i = 0; i < length; i++) {
            resLength += "0";
        }
        DecimalFormat df = new DecimalFormat(resLength);
        return df.format(num);
    }

    //获取目录树 List
    default List<DataMenu> getDataMenuList() {
        return getDataMenuListByParentId("00");
    }

    default List<DataMenu> getDataMenuListByParentId(String parentid) {
        List<DataMenu> dataMenuList = new ArrayList<>();
        List<IndIndexCfg> menuList = getMenuListByParentIdSimple(parentid);
        if (menuList != null && menuList.size() != 0) {
            for (IndIndexCfg indIndexCfg : menuList) {
                if (!indIndexCfg.isleaf()) {
                    dataMenuList.add(new DataMenu(indIndexCfg, getDataMenuListByParentId(indIndexCfg.getId())));
                } else {
                    dataMenuList.add(new DataMenu(indIndexCfg, null));
                }
            }
        }
        return dataMenuList;
    }

    String selectDateBetweenDate = "<if test='startDate!=null and endDate!=null'> and d1 between #{startDate} and #{endDate}</if>";
    //获取某一叶子节点下的数据列表   除去已删除的
    @Select("<script>" +
            "select * from indindex " +
            "<where>" +
            "indindexcfgid=#{menuid} and isdelete=0" +
            selectDateBetweenDate +
            "</where>" +
            "order by d1 desc limit #{limit} offset #{offset}" +
            "</script>")
    List<IndIndex> getDataValueListByMenuId(@Param("menuid") String menuid,
                                            @Param("startDate") String startDate,
                                            @Param("endDate") String endDate,
                                            @Param("limit") int limit,
                                            @Param("offset") int offset);

    @Select("<script>" +
            "select count(val) from indindex " +
            "<where>" +
            "indindexcfgid=#{menuid} and isdelete=0 " +
            selectDateBetweenDate +
            "</where>" +
            "</script>")
    int getDataValueCountByMenuId(@Param("menuid") String menuid,
                                  @Param("startDate") String startDate,
                                  @Param("endDate") String endDate);

    public default Pager<IndIndex> pageAllDataValue(String menuid, String startDate, String endDate, int page, int pagesize) {
        return Pager.config(this.getDataValueCountByMenuId(menuid, startDate, endDate), (int limit, int offset) -> this.getDataValueListByMenuId(menuid, startDate, endDate, limit, offset))
                .page(page, pagesize);
    }

    @Select("select d1 from indindex where indindexcfgid=#{indindexcfgid} and isdelete=0")
    List<String> getAllDataD1ValueList(@Param("indindexcfgid")String indindexcfgid);

    //根据id 更改数据
    @Update("update indindex set d1=#{d1}, val=#{val} where indindexcfgid=#{indindexcgfid}")
    int updateIndIndex(IndIndex indIndex);

    //添加 数据
    @Insert("insert into indindex(indindexcfgid, d1, val) values(#{indindexcfgid}, #{d1}, #{val})")
    int doAddIndIndex(IndIndex indIndex);

    @Insert("<script>" +
            " insert into indindex(indindexcfgid, d1, val) values " +
            " <foreach collection=\"indIndexList\" index=\"index\" item=\"item\" separator=\",\" >" +
            " (#{item.indindexcfgid}, #{item.d1}, #{item.val} )" +
            " </foreach> " +
            "</script>")
    int doAddIndIndexList(@Param("indIndexList") List<IndIndex> indIndexList);

    //更新 数据
    @Update("update indindex set val=#{val} where indindexcfgid=#{indindexcfgid} and d1=#{d1}")
    int doUpdateIndIndex(IndIndex indIndex);

    default Map<String, Object> doAddUpdateIndIndexMethod(IndIndex indIndex) {
        IndIndexCfg indIndexCfg = getMenuById(indIndex.getIndindexcfgid());
        Map<String, Object> map = new HashMap<>();
        if(indIndex.getD1().length() == 4){
            indIndex.setD1(indIndex.getD1() + "-12-31");
        } else if(indIndex.getD1().length() == 7){
            String d12 = indIndex.getD1().substring(5, 7);
            if(d12.equals("02")){
                indIndex.setD1(indIndex.getD1() + "-28");
            } else if(d12.equals("04") || d12.equals("06") || d12.equals("09") || d12.equals("11")){
                indIndex.setD1(indIndex.getD1() + "-30");
            } else{
                indIndex.setD1(indIndex.getD1() + "-31");
            }
        }
        boolean success = false;
        String error = "";
        if(indIndexCfg.getFqcy() == 2){
            int year = Integer.valueOf(indIndex.getD1().substring(0, 4));
            int month = Integer.valueOf(indIndex.getD1().substring(5, 7));
            int day = Integer.valueOf(indIndex.getD1().substring(8, 10));
            LocalDate weekDate = LocalDate.of(year, month, day);
            LocalDate firstDay = getTheFirstLastDayOfWeek(weekDate).get("firstDay");
            LocalDate lastDay = getTheFirstLastDayOfWeek(weekDate).get("lastDay");
            if(getIndIndexByIndIndexCfgIdD1Region(indIndexCfg.getId(), firstDay, lastDay) != null){
                error = indIndexCfg.getName() + " 节点(频率：每周一条)，在 " + indIndex.getD1() + "(这一周) 已经存在数据！";
            } else{
                success = doAddIndIndex(indIndex) == 1 ? true : false;
            }
        } else {
            if (getIndIndexByIndIndexCfgIdD1(indIndex) != null) {
                switch (indIndexCfg.getFqcy()) {
                    case 1:
                        error = indIndexCfg.getName() + " 节点(频率：每天一条)，在 " + indIndex.getD1() + " 已经存在数据！";
                        break;
                    case 3:
                        error = indIndexCfg.getName() + " 节点(频率：每月一条)，在 " + indIndex.getD1() + " 已经存在数据！";
                        break;
                    case 4:
                        error = indIndexCfg.getName() + " 节点(频率：每季度一条)，在 " + indIndex.getD1() + " 已经存在数据！";
                        break;
                    case 5:
                        error = indIndexCfg.getName() + " 节点(频率：每年一条), 在 " + indIndex.getD1() + " 已经存在数据！";
                        break;
                }
            } else {
                success = doAddIndIndex(indIndex) == 1 ? true : false;
            }
        }
        map.put("success", success);
        map.put("error", error);
        return map;
    }

    default Map<String, LocalDate> getTheFirstLastDayOfWeek(LocalDate localDate){
        Map<String, LocalDate> map = new HashMap<>();
        LocalDate localDate1 = null;
        LocalDate localDate2 = null;
        if(localDate.getDayOfWeek().getValue() == 1){
            localDate1 = localDate;
            localDate2 = localDate.plusDays(6);
        } else if(localDate.getDayOfWeek().getValue() == 2){
            localDate1 = localDate.minusDays(1);
            localDate2 = localDate.plusDays(5);
        } else if(localDate.getDayOfWeek().getValue() == 3){
            localDate1 = localDate.minusDays(2);
            localDate2 = localDate.plusDays(4);
        } else if(localDate.getDayOfWeek().getValue() == 4){
            localDate1 = localDate.minusDays(3);
            localDate2 = localDate.plusDays(3);
        } else if(localDate.getDayOfWeek().getValue() == 5){
            localDate1 = localDate.minusDays(4);
            localDate2 = localDate.plusDays(2);
        } else if(localDate.getDayOfWeek().getValue() == 6){
            localDate1 = localDate.minusDays(5);
            localDate2 = localDate.plusDays(1);
        } else if(localDate.getDayOfWeek().getValue() == 7){
            localDate1 = localDate.minusDays(6);
            localDate2 = localDate;
        }
        map.put("firstDay", localDate1);
        map.put("lastDay", localDate2);
        return map;
    }

    //删除目录
    @Update("update indindexcfg set id=#{newid}, isdelete=1 where id=#{id}")
    int deleteIndIndexCfgById(@Param("newid")String newid, @Param("id")String id);

    //删除数据
    @Update("update indindex set isdelete=1 where indindexcfgid=#{indindexcfgid} and d1=#{d1} and isdelete=0")
    int deleteIndIndexByIndIndexCfgIdD1(@Param("indindexcfgid")String indindexcfgid,
                                        @Param("d1")String d1);

    //根据indindexcfgid 和 d1 查询数据
    @Select("select * from indindex where indindexcfgid=#{indindexcfgid} and d1=#{d1} and isdelete=0 limit 1")
    IndIndex getIndIndexByIndIndexCfgIdD1(IndIndex indIndex);

    //根据indindexcfgid 和 d1 区间 查询数据
    @Select("select * from indindex where indindexcfgid=#{indindexcfgid} and (d1 between #{firstDay} and #{lastDay}) and isdelete=0")
    IndIndex getIndIndexByIndIndexCfgIdD1Region(@Param("indindexcfgid")String indindexcfgid,
                                                @Param("firstDay")LocalDate firstDay,
                                                @Param("lastDay")LocalDate lastDay);

    //根据id获取点击菜单的数据
    @Select("select * from indindex where indindexcfgid=#{menuid} and isdelete=0 order by d1")
    public List<IndIndex> getDataByMenuId(String menuid);


    //改变已删除的目录的数据的状态
    @Update("update indindex set isdelete=1, indindexcfgid=#{newindindexcfgid} where indindexcfgid=#{indindexcfgid}")
    public int setMenuDeletedDataDelete(@Param("newindindexcfgid")String newindindexcfgid,
                                        @Param("indindexcfgid")String indindexcfgid);

    //设置 节点为默认选项  和 取消默认选项
    @Update("update indindexcfg set defaultone=#{defaultone} where id=#{id}")
    public int setDefaultById(@Param("defaultone")int defaultone, @Param("id")String id);

    //获取第一个子节点的菜单
    @Select("select * from indindexcfg where isleaf=1 and isdelete=0 limit 1")
    IndIndexCfg getFirstLeafMenu();

    default Object doDeleteMenuByIdMethod(String id) {
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        if (getMenuListByParentId(id).size() > 0) {
            map.put("error", "您要删除的目录包含的有子目录，请先删除子目录，再删除！");
        } else {
            success = doDeleteMenuAndData(id);
        }
        map.put("success", success);
        return map;
    }

    default  boolean doDeleteMenuAndData(String id){
        String newid = "d" + id + setFixedLengthString(0, 6);
        boolean success = false;
        if(getMenuById(newid) != null){
            for(int i=1; i<10000; i++) {
                for (int j = 1; j < 999999; j++) {
                    newid = "d" + i + id + setFixedLengthString(j, 6);
                    if (getMenuById(newid) == null){
                        success = true;
                        break;
                    }
                }
                if(success) break;
            }
        }
        return doDeleteIndIndexCfgByIdMethod(newid, id);
    }

    default boolean doDeleteIndIndexCfgByIdMethod(String newid, String id){
        IndIndexCfg indIndexCfg = getMenuById(id);
        int rows = deleteIndIndexCfgById(newid, id);
        if(rows == 1) {
            if (indIndexCfg.isleaf()) {
                setMenuDeletedDataDelete(newid, id);
                return true;
            } else{
                return true;
            }
        } else{
            return false;
        }
    }

    default boolean doSetDefaultMenuByIdMethod(String id){
        IndIndexCfg formerDefault = getDefaultMenu();
        int row1 = setDefaultById(1, id);
        if(formerDefault != null){
            int row2 = setDefaultById(0, formerDefault.getId());
            return row1 == 1 && row2 == 1;
        } else {
            return row1 == 1;
        }
    }

    //设置 或者 取消 前台显示
    @Update("update indindexcfg set isshow=1 where id=#{id}")
    int doSetShowById(String id);
    @Update("update indindexcfg set isshow=0 where id=#{id}")
    int doCancelShowById(String id);

    @Update("update indindex set isdelete=1 where d1 between #{startDate} and #{endDate} and indindexcfgid=#{indindexcfgid} and isdelete=0")
    int doDeleteDataByDateZone(@Param("startDate")String startDate,
                               @Param("endDate")String endDate,
                               @Param("indindexcfgid")String indindexcfgid);
    @Update("update indindex set isdelete=1 where indindexcfgid=#{indindexcfgid} and isdelete=0")
    int doDeleteAllData(String indindexcfgid);

    //根据id集合查询菜单组,按频率,字符排列
    @Select("<script>select * from indindexcfg where id in " +
            "<foreach collection='array' index='i' open='(' separator=',' close=')' item='idArr'>#{idArr}</foreach> " +
            " and isdelete=0 " +
            "order by fqcy,id</script>")
    public List<IndIndexCfg> getMenusByIdArr(String[] idArr);

    final String checkDateSql = "<if test='startDate!=null and startDate !=\"\" and endDate!=null and endDate !=\"\"'> and STR_TO_DATE(d1, '%Y-%m-%d') between #{startDate} and #{endDate} </if>";
    final String orderBySql = "<if test='orderByDesc!=null and orderByDesc !=\"\"'>  desc </if>";
    final String limitSql = "<if test='needLimit!=null and needLimit !=\"\"'>  limit #{limitnum},#{offset} </if>";


    //选中一个菜单
    @Select("<script>" +
            "select d1, val as column1 from indindex where indindexcfgid =#{firstMenuId} and isdelete=0 "+
            checkDateSql+
            " order by d1 " +
            orderBySql +
            limitSql +
            "</script>")
    public List<DataCenter> getDataByOneMenu(@Param("firstMenuId") String firstMenuId,
                                             @Param("startDate") String startDate, @Param("endDate") String endDate,
                                             @Param("limitnum") int limitnum, @Param("offset") int offset,
                                             @Param("orderByDesc") String orderByDesc,@Param("needLimit") String needLimit);

    //选中两个菜单
    @Select("<script>" +
            "select t.d1 , min(t.column1) as column1, min(t.column2) as column2  from (" +
            "select d1, val as column1 , null as column2 from indindex  t  where  t.indindexcfgid =#{firstMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , val as column2  from indindex  t where  t.indindexcfgid =#{secMenuId} and isdelete=0 " +
            checkDateSql+
            ") t group by d1 " +
            " order by d1 " +
            orderBySql +
            limitSql +
            "</script>")
    public List<DataCenter> getDataByTwoMenus(@Param("firstMenuId") String firstMenuId, @Param("secMenuId") String secMenuId,
                                              @Param("startDate") String startDate, @Param("endDate") String endDate,
                                              @Param("limitnum") int limitnum, @Param("offset") int offset,
                                              @Param("orderByDesc") String orderByDesc,@Param("needLimit") String needLimit);

    //选中三个菜单
    @Select("<script>" +
            "select t.d1 , min(t.column1) as column1, min(t.column2) as column2, min(t.column3) as column3  from (" +
            "select d1, val as column1 , null as column2 , null as column3 from indindex  t  where  t.indindexcfgid =#{firstMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , val as column2, null as column3  from indindex  t where  t.indindexcfgid =#{secMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , null as column2, val as column3 from indindex  t where  t.indindexcfgid =#{thirdMenuId} and isdelete=0 " +
            checkDateSql+
            ") t group by d1 " +
            " order by d1 " +
            orderBySql +
            limitSql +
            "</script>")
    public List<DataCenter> getDataByThreeMenus(@Param("firstMenuId") String firstMenuId, @Param("secMenuId") String secMenuId,
                                                @Param("thirdMenuId") String thirdMenuId,
                                                @Param("startDate") String startDate, @Param("endDate") String endDate,
                                                @Param("limitnum") int limitnum, @Param("offset") int offset,
                                                @Param("orderByDesc") String orderByDesc,@Param("needLimit") String needLimit);

    //选中四个菜单
    @Select("<script>" +
            "select t.d1 , min(t.column1) as column1, min(t.column2) as column2, min(t.column3) as column3, min(t.column4) as column4 from (" +
            "select d1, val as column1 , null as column2 , null as column3, null as column4 from indindex  t  where  t.indindexcfgid =#{firstMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , val as column2, null as column3, null as column4  from indindex  t where  t.indindexcfgid =#{secMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , null as column2, val as column3, null as column4 from indindex  t where  t.indindexcfgid =#{thirdMenuId} and isdelete=0 " +
            checkDateSql+
            "  union all " +
            "select d1, null as column1 , null as column2, null as column3,val as column4 from indindex  t where  t.indindexcfgid =#{fourthMenuId} and isdelete=0 " +
            checkDateSql+
            ") t group by d1 " +
            " order by d1 " +
            orderBySql +
            limitSql +
            "</script>")
    public List<DataCenter> getDataByFourMenus(@Param("firstMenuId") String firstMenuId, @Param("secMenuId") String secMenuId,
                                               @Param("thirdMenuId") String thirdMenuId, @Param("fourthMenuId") String fourthMenuId,
                                               @Param("startDate") String startDate, @Param("endDate") String endDate,
                                               @Param("limitnum") int limitnum, @Param("offset") int offset,
                                               @Param("orderByDesc") String orderByDesc,@Param("needLimit") String needLimit);

    //选中五个菜单
    @Select("<script>" +
            "select t.d1 , min(t.column1) as column1, min(t.column2) as column2, min(t.column3) as column3, min(t.column4) as column4 ,min(t.column5) as column5 from (" +
            "select d1, val as column1 , null as column2 , null as column3, null as column4, null as column5 from indindex  t  where  t.indindexcfgid =#{firstMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , val as column2, null as column3, null as column4, null as column5  from indindex  t where  t.indindexcfgid =#{secMenuId} and isdelete=0 " +
            checkDateSql+
            "union all " +
            "select d1, null as column1 , null as column2, val as column3, null as column4, null as column5 from indindex  t where  t.indindexcfgid =#{thirdMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , null as column2, null as column3,val as column4, null as column5 from indindex  t where  t.indindexcfgid =#{fourthMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , null as column2, null as column3,null as column4, val as column5 from indindex  t where  t.indindexcfgid =#{fifthMenuId} and isdelete=0 " +
            checkDateSql+
            ") t group by d1 " +
            " order by d1 " +
            orderBySql +
            limitSql +
            "</script>")
    public List<DataCenter> getDataByFiveMenus(@Param("firstMenuId") String firstMenuId, @Param("secMenuId") String secMenuId,
                                               @Param("thirdMenuId") String thirdMenuId, @Param("fourthMenuId") String fourthMenuId,
                                               @Param("fifthMenuId") String fifthMenuId,
                                               @Param("startDate") String startDate, @Param("endDate") String endDate,
                                               @Param("limitnum") int limitnum, @Param("offset") int offset,
                                               @Param("orderByDesc") String orderByDesc,@Param("needLimit") String needLimit);

    //获取默认的图表菜单
    @Select("select * from indindexcfg where defaultone=1 and isdelete=0 limit 1")
    public IndIndexCfg getDefaultMenu();

    @Select("select * from indindexcfg where isdelete=0 and isshow=1 order by orderid")
    List<IndIndexCfg> getAllShowMenuList();

    @Select("select * from indindexcfg where isdelete=0 and isshow=0")
    List<IndIndexCfg> getAllNoShowMenuList();

    //根据第一天和最后一天查选是否有记录
    @Select("select * from indindex where indindexcfgid=#{menuid} and STR_TO_DATE(d1, '%Y-%m-%d') between #{firstDayParam} and #{lastDayParam} and isdelete=0 ")
    public List<IndIndex> getListByFirstDayAndLastDay(@Param("menuid") String menuid,
                                                      @Param("firstDayParam") String firstDayParam,
                                                      @Param("lastDayParam") String lastDayParam);
    //设置菜单的子菜单是否在后台显示
    @Update("update indindexcfg set display=(display+1)%2 where id=#{id}")
    int setMenuDisplayStyle(String id);

    //根据id,日期查询点击菜单的数据
    @Select("select * from indindex where indindexcfgid=#{menuid} and STR_TO_DATE(d1, '%Y-%m-%d') between #{startDate} and #{endDate} and isdelete=0 order by d1")
    public List<IndIndex> getDataByMenuIdAndDate(@Param("menuid") String menuid,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate);

    //根据id获取点击菜单的分页数据
    @Select("select * from indindex where indindexcfgid=#{menuid} and isdelete=0 order by d1 desc limit #{limitnum},#{offset}")
    public List<IndIndex> getDataByMenuIdWithLimit(@Param("menuid") String menuid,@Param("limitnum") int limitnum,@Param("offset") int offset);

    //根据id获取点击菜单的查询分页数据
    @Select("select * from indindex where indindexcfgid=#{menuid} and STR_TO_DATE(d1, '%Y-%m-%d') between #{startDate} and #{endDate} and isdelete=0 order by d1 desc limit #{limitnum}, #{offset}")
    public List<IndIndex> getDataByMenuIdByDataWithLimit(@Param("menuid") String menuid,
                                                         @Param("startDate") String startDate, @Param("endDate") String endDate,
                                                         @Param("limitnum") int limitnum, @Param("offset") int offset);

    //根据菜单id统计有多少数据
    @Select("select count(*) from indindex where indindexcfgid=#{menuid} and isdelete=0")
    public int countIndexDataById(String menuid);

    //根据菜单id和日期统计有多少数据
    @Select("select count(*) from indindex where indindexcfgid=#{menuid} and isdelete=0 and STR_TO_DATE(d1, '%Y-%m-%d') between #{startDate} and #{endDate}")
    public int countIndexDataByIdAndDate(@Param("menuid") String menuid,
                                         @Param("startDate") String startDate, @Param("endDate") String endDate);

    //统计一个菜单
    @Select("<script>" +
            "select count(*) from(select d1, val as column1 from indindex where indindexcfgid =#{firstMenuId} and isdelete=0" +
            checkDateSql+
            ") t" +
            "</script>")
    public int countIndexDataByOneMenu(@Param("firstMenuId") String firstMenuId,
                                       @Param("startDate") String startDate, @Param("endDate") String endDate);

    //统计两个菜单
    @Select("<script>" +
            "select count(*) from(" +
            "select t.d1 , min(t.column1) as column1, min(t.column2) as column2  from (" +
            "select d1, val as column1 , null as column2 from indindex  t  where  t.indindexcfgid =#{firstMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , val as column2  from indindex  t where  t.indindexcfgid =#{secMenuId} and isdelete=0 " +
            checkDateSql+
            ") t group by d1" +
            ") m" +
            "</script>")
    public int countIndexDataByTwoMenus(@Param("firstMenuId") String firstMenuId, @Param("secMenuId") String secMenuId,
                                        @Param("startDate") String startDate, @Param("endDate") String endDate);

    //统计三个菜单
    @Select("<script>" +
            "select count(*) from(" +
            "select t.d1 , min(t.column1) as column1, min(t.column2) as column2, min(t.column3) as column3  from (" +
            "select d1, val as column1 , null as column2 , null as column3 from indindex  t  where  t.indindexcfgid =#{firstMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , val as column2, null as column3  from indindex  t where  t.indindexcfgid =#{secMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , null as column2, val as column3 from indindex  t where  t.indindexcfgid =#{thirdMenuId} and isdelete=0 " +
            checkDateSql+
            ") t group by d1" +
            ") m" +

            "</script>")
    public int countIndexDataByThreeMenus(@Param("firstMenuId") String firstMenuId, @Param("secMenuId") String secMenuId,
                                          @Param("thirdMenuId") String thirdMenuId,
                                          @Param("startDate") String startDate, @Param("endDate") String endDate);

    //选中四个菜单
    @Select("<script>" +
            "select count(*) from(" +
            "select t.d1 , min(t.column1) as column1, min(t.column2) as column2, min(t.column3) as column3, min(t.column4) as column4 from (" +
            "select d1, val as column1 , null as column2 , null as column3, null as column4 from indindex  t  where  t.indindexcfgid =#{firstMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , val as column2, null as column3, null as column4  from indindex  t where  t.indindexcfgid =#{secMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , null as column2, val as column3, null as column4 from indindex  t where  t.indindexcfgid =#{thirdMenuId} and isdelete=0 " +
            checkDateSql+
            "  union all " +
            "select d1, null as column1 , null as column2, null as column3,val as column4 from indindex  t where  t.indindexcfgid =#{fourthMenuId} and isdelete=0 " +
            checkDateSql+
            ") t group by d1" +
            ") m" +

            "</script>")
    public int countIndexDataByFourMenus(@Param("firstMenuId") String firstMenuId, @Param("secMenuId") String secMenuId,
                                         @Param("thirdMenuId") String thirdMenuId, @Param("fourthMenuId") String fourthMenuId,
                                         @Param("startDate") String startDate, @Param("endDate") String endDate);

    //统计五个菜单
    @Select("<script>" +
            "select count(*) from(" +
            "select t.d1 , min(t.column1) as column1, min(t.column2) as column2, min(t.column3) as column3, min(t.column4) as column4 ,min(t.column5) as column5 from (" +
            "select d1, val as column1 , null as column2 , null as column3, null as column4, null as column5 from indindex  t  where  t.indindexcfgid =#{firstMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , val as column2, null as column3, null as column4, null as column5  from indindex  t where  t.indindexcfgid =#{secMenuId} and isdelete=0 " +
            checkDateSql+
            "union all " +
            "select d1, null as column1 , null as column2, val as column3, null as column4, null as column5 from indindex  t where  t.indindexcfgid =#{thirdMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , null as column2, null as column3,val as column4, null as column5 from indindex  t where  t.indindexcfgid =#{fourthMenuId} and isdelete=0 " +
            checkDateSql+
            " union all " +
            "select d1, null as column1 , null as column2, null as column3,null as column4, val as column5 from indindex  t where  t.indindexcfgid =#{fifthMenuId} and isdelete=0 " +
            checkDateSql+
            ") t group by d1" +
            ") m" +
            "</script>")
    public int countIndexDataByFiveMenus(@Param("firstMenuId") String firstMenuId, @Param("secMenuId") String secMenuId,
                                         @Param("thirdMenuId") String thirdMenuId, @Param("fourthMenuId") String fourthMenuId,
                                         @Param("fifthMenuId") String fifthMenuId,
                                         @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据indindexcfgId 查询最新的一条数据
     */
    @Select("select * from indindex where indindexcfgid=#{indindexcfgid} and isdelete=0 order by d1 desc limit #{limit}")
    List<IndIndex> getLatestIndIndexByIndIndexCfgId(@Param("indindexcfgid") String indindexcfgid,
                                                  @Param("limit")int limit);

}