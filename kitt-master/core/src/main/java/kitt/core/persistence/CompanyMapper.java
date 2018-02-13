package kitt.core.persistence;

import kitt.core.domain.CompVerSus;
import kitt.core.domain.Company;
import kitt.core.domain.CompanyVerify;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Created by fanjun on 14-11-13.
 */
public interface CompanyMapper {


    //插入公司信息
    @Insert("insert into companies(verifystatus,name,address,province,city,country,detailaddress,provinceCode,cityCode,countryCode,phone,fax,legalperson,businesslicense,identificationnumber," +
            "organizationcode,operatinglicense,userid,legalpersonname,account,openingbank, traderphone," +
            "identificationnumword,zipcode,openinglicense,invoicinginformation) values(#{verifystatus}," +
            "#{name},#{address},#{province},#{city},#{country},#{detailaddress},#{provinceCode},#{cityCode},#{countryCode},#{phone},#{fax},#{legalperson},#{businesslicense},#{identificationnumber}," +
            "#{organizationcode},#{operatinglicense},#{userid},#{legalpersonname},#{account}," +
            "#{openingbank},#{traderphone},#{identificationnumword},#{zipcode},#{openinglicense}," +
            "#{invoicinginformation})")
    public int addCompany(Company company);

    //公司信息验证成功，备份公司信息
    @Insert("insert into compversus(name, address, phone, fax," +
            "legalperson, businesslicense, identificationnumber, organizationcode," +
            "operatinglicense, userid, legalpersonname, account, openingbank) values(" +
            "#{name}, #{address}, #{phone}, #{fax}, " +
            "#{legalperson}, #{businesslicense}, #{identificationnumber}, #{organizationcode}," +
            "#{operatinglicense}, #{userid}, #{legalpersonname}, #{account}, #{openingbank})")
    public int addCompVerSus(CompVerSus compVerSus);

    //判断是否已有该公司,做新增还是修改
    @Select("select count(*) from companies where userid=#{userid}")
    public int countCompany(int userid);

    //修改公司信息
    @Update("update companies set verifystatus=#{verifystatus},name=#{name}," +
            "province=#{province},city=#{city},country=#{country},detailaddress=#{detailaddress},provinceCode=#{provinceCode},cityCode=#{cityCode},countryCode=#{countryCode}," +
            " phone=#{phone},fax=#{fax}," +
            "legalperson=#{legalperson},businesslicense=#{businesslicense}," +
            "identificationnumber=#{identificationnumber},organizationcode=#{organizationcode}," +
            "operatinglicense=#{operatinglicense},legalpersonname=#{legalpersonname}," +
            "account=#{account},openingbank=#{openingbank},traderphone=#{traderphone}," +
            "identificationnumword=#{identificationnumword},zipcode=#{zipcode}, " +
            "openinglicense=#{openinglicense}, invoicinginformation=#{invoicinginformation}" +
            " where userid=#{userid}")
    public int modifyCompany(Company company);

    //获取公司对象
    @Select("select * from companies where userid=#{userid} limit 1")
    public Company getCompanyByUserid(int userid);

    //添加公司验证信息
    @Insert("insert into compverify(status,applytime,companyid,userid,infos) values(#{status}, #{applytime}, #{companyid}, #{userid}, #{infos})")
    public int addCompVerify(CompanyVerify companyVertify);

    //修改公司验证状态
    @Update("update compverify set status=#{status} where id=#{id}")
    public void updateVerifyStatus(@Param("status") String status, @Param("id") int id);

    //查询公司id
    @Select("select id from companies where userid=#{userid} limit 1")
    public Integer getIdByUserid(int userid);

    //按照id查找公司
    @Select("select * from companies where id=#{id}")
    public Company getCompanyById(int id);

    //设置公司信息状态  待审核
    @Update("update companies set verifystatus=#{verifystatus} where id=#{id}")
    public void setCompanyStatusWait(@Param("verifystatus") String verifystatus, @Param("id") int id);


    //设置公司信息状态为 审核通过
    @Update("update companies set verifystatus=#{verifystatus}, remarks=#{remarks} where id=#{id}")
    public int setCompanyStatus(@Param("verifystatus") String verifystatus,
                                 @Param("remarks") String remarks,
                                 @Param("id") int id);

    //验证通过公司信息
    @Update("update compverify set status=#{status}, verifyman=#{verifyman}, " +
            "verifytime=#{verifytime}, remarks=#{remarks} " +
            "where companyid=#{companyid} and id=#{id} and status='待审核'")
    public int setCompVerify(@Param("status") String status,
                             @Param("verifyman") String verifyman,
                             @Param("verifytime") LocalDateTime verifytime,
                             @Param("remarks") String remarks,
                             @Param("companyid") int companyid,
                             @Param("id")int id);

    @Select("select * from compverify where companyid=#{companyid}")
    public List<CompanyVerify> getVerifyList(int companyid);

    //统计公司名称是否存在
    @Select("select count(*) from companies where name=#{name} and userid <> #{id}")
    public int countCompanyIsExist(@Param("name") String name, @Param("id") int id);

    //获取最新的companyVerify验证信息
    @Select("select * from compverify where companyid=#{companyid} and userid=#{userid} and status=#{status} order by id desc limit 1")
    CompanyVerify getTheLatestCompanyVerifyByCompanyIdUserIdStatus(@Param("companyid")int companyId,
                                                                   @Param("userid")int userId,
                                                                   @Param("status")String status);

    //获取所有审核通过的公司记录
    @Select("select name from companies c left join users u on c.userid=u.id where c.verifystatus='审核通过' and u.isactive=1")
    public List<String> getAllPassCompanies();

    //根据公司名称获取公司对象,条件:已审核通过
    @Select("select * from companies where name=#{name} and verifystatus='审核通过' limit 1")
    public Company getCompanyByName(String name);

    /**
     * 招标公司列表
     */
    @Select("<script>" +
            "select count(distinct(c.id)) from companies c inner join tenderdeclaration t on c.id = t.companyid " +
            "<where> " +
            " c.verifystatus='审核通过' " +
            "<if test='content!=null and content!=\"\"'> and c.name like #{content}</if>" +
            "</where>" +
            "</script>")
    public int countAllCompanies(@Param("content")String content);

    @Select("<script>" +
            "select c.id, c.name, c.logopic, c.bannerpic, c.istender, u.registertime as createtime from companies c " +
            " left join users u on c.userid = u.id " +
            " inner join tenderdeclaration t on c.id = t.companyid " +
            "<where>" +
            " c.verifystatus='审核通过' " +
            "<if test='content!=null and content!=\"\"'> and c.name like #{content}</if>" +
            "</where>" +
            " group by c.id order by u.registertime desc limit #{limit} offset #{offset}" +
            "</script>")
    public List<Map<String, Object>> listAllCompanies(@Param("content")String content,
                                                      @Param("limit")int limit,
                                                      @Param("offset")int offset);

    public default Pager<Map<String, Object>> pageAllTenderCompanies(String content, int page, int pagesize){
        return Pager.config(this.countAllCompanies(content), (int limit, int offset) -> this.listAllCompanies(content, limit, offset))
                .page(page, pagesize);
    }

    //更改招标公司信息
    @Update("update companies set logopic=#{logopic}, bannerpic=#{bannerpic}, shortername=#{shortername} where id=#{id}")
    int saveCompanyTenderInfo(@Param("id") int id,
                              @Param("logopic") String logopic,
                              @Param("bannerpic") String bannerpic,
                              @Param("shortername") String shortername);

    @Update("update companies set istender=#{istender} where id=#{id} and istender!=#{istender}")
    int changeCompanyTenderStatus(@Param("id")int id,
                                  @Param("istender")boolean istender);

    @Select("select * from companies where name like '%' #{value} '%'")
    public Company likeCompanyName(String companyName);


}
