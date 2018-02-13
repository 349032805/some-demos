package kitt.core.persistence;

import kitt.core.domain.DataBook;
import kitt.core.domain.Dictionary;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Created by fanjun on 14-11-25.
 */
public interface DictionaryMapper {

    //获取所有区域
    @Select("select * from dictionaries where code='region'")
    public List<Dictionary> getAllRegions();

    @Select("select name from dictionaries where id=#{id}")
    public String getNameById(int id);

    @Select("select id from dictionaries where name=#{name}")
    public Integer getIdByName(String name);

    //获取所有提货方式
    @Select("select * from dictionaries where code='deliverymode'")
    public List<Dictionary> getDeliverymodes();

    //获取所有检验机构
    @Select("select * from dictionaries where code='inspectionagency'")
    public List<Dictionary> getAllInspectionagencys();

    //获取所有煤的种类
    @Select("select * from dictionaries where code='coaltype'")
    public List<Dictionary> getAllCoalTypes();

    //获取手机APK版本号
    @Select("select * from dictionaries where code='APK'")
    public Dictionary getAPKVersion();

    //根据煤id获取名称
    @Select("select name from dictionaries where id=#{id}")
    public String getCaolNameByCoalId(int id);

    //添加app版本号
    @Insert("insert into dictionaries(code,name) values('APK',#{version})")
    public void addAPKVersion(String version);

    //修改版本号
    @Update("update dictionaries set name=#{newVersion} where code='APK'")
    public void moidyfAPKVersion(String newVersion);

    //查询app下载数量
    @Select("select * from dictionaries where code='appDownloadNum'")
    public Dictionary getAppDownloadNum();

    //添加appDownloadNum
    @Insert("insert into dictionaries(code,name) values('appDownloadNum','1')")
    public void addAppDownloadNum();

    //修改appDownloadNum
    @Update("update dictionaries set name=cast(name as SIGNED INTEGER)+1 where code='appDownloadNum'")
    public void modifyAppDownloadNum();

    //获取所有客户端类型
    @Select("select * from dictionaries where code='clienttype'")
    List<Dictionary> getAllClientType();

    //根据id获取clienttype
    @Select("select name from dictionaries where code=(select name from dictionaries where id=#{id})")
    String getClientTypeNameById(int id);

}

