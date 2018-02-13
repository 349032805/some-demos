package kitt.core.persistence;

import kitt.core.domain.Admin;
import kitt.core.domain.Dealer;
import kitt.core.domain.DealerBackup;
import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by jack on 15/1/9.
 */
public interface DealerMapper{
    String dealerSelectSQL1 =
                    "<if test='deliveryregion!=null'> deliveryregion=#{deliveryregion}</if>" +
                    "<if test='deliveryprovince!=null'> and deliveryprovince=#{deliveryprovince}</if>" +
                    "<if test='deliveryplace!=null'> and deliveryplace=#{deliveryplace}</if>";
    String startWhere = " <where>";
    String endWhere = " </where>";
    String statusNotDelete = " and status != '已删除'";

    final String limitOffset = " limit #{limit} offset #{offset}";

    //交易员列表
    @Select("select count(*) from dealers where status != '已删除'")
    public int countAllDealers();

    @Select("select * from dealers where status != '已删除' order by status" + limitOffset)
    public List<Dealer> listAllDealers(@Param("limit") int limit, @Param("offset") int offset);

    public default Pager<Dealer> getAllDealersList(int page, int pagesize){
        return Pager.config(this.countAllDealers(), (int limit, int offset) -> this.listAllDealers(limit, offset))
                .page(page, pagesize);
    }

    //查询交易员-
    @Select("<script>" +
            "select count(*) from dealers " +
            startWhere +  dealerSelectSQL1 + statusNotDelete + endWhere +
            "</script>")
    public int countSelectDealers(@Param("deliveryregion")String deliveryregion,
                                  @Param("deliveryprovince")String deliveryprovince,
                                  @Param("deliveryplace")String deliveryplace);
    @Select("<script>" +
            "select * from dealers " +
            startWhere + dealerSelectSQL1 + statusNotDelete + endWhere + limitOffset +
            "</script>")

    public List<Dealer> listSelectDealers(@Param("deliveryregion")String deliveryregion,
                                          @Param("deliveryprovince")String deliveryprovince,
                                          @Param("deliveryplace")String deliveryplace,
                                          @Param("limit")int limit,
                                          @Param("offset")int offset);

    public default Pager<Dealer> getDealersBySelect(String deliveryregion, String deliveryprovince, String deliveryplace, int page, int pagesize){
        return Pager.config(this.countSelectDealers(deliveryregion, deliveryprovince, deliveryplace), (int limit, int offset) -> this.listSelectDealers(deliveryregion, deliveryprovince, deliveryplace, limit, offset))
                .page(page, pagesize);
    }

    @Select("select * from dealers where id=#{id}")
    public Dealer getDealerById(int id);

    @Select("select * from dealers where dealerid=#{dealerid} and status='在职'")
    public List<Dealer> getDealerByDealerId(String dealerid);

    @Select("select * from dealers where deliveryplace=#{deliveryplace} and status='在职'")
    public List<Dealer> getDealerByDeliveryplace(String deliveryplace);

    @Select("select * from dealers where dealername='易煤网' ")
    public List<Dealer> getYMWKFDealer();

    @Select("select * from dealers where deliveryprovince=#{province} and deliveryplace=#{place} and status='在职'")
    public List<Dealer> getDealerByProvincePlace(@Param("province")String deliveryprovince,
                                                 @Param("place")String deliveryplace);

    @Select("select * from dealers where deliveryprovince=#{deliveryprovince} and status='在职'")
    public List<Dealer> getDealerByDeliveryprovince(String deliveryprovince);

    @Select("select * from dealers where deliveryregion=#{deliveryregion} and status='在职'")
    public List<Dealer> getDealerByDeliveryregion(String deliveryregion);

    //添加交易员
    @Insert("insert into dealers(dealerid, dealername, dealerphone, deliveryregion, deliveryprovince, " +
            "deliveryplace, lastupdatemanid, lastupdatemanname, createtime) values(#{dealerid}, " +
            "#{dealername}, #{dealerphone}, #{deliveryregion}, #{deliveryprovince}, #{deliveryplace}," +
            " #{lastupdatemanid}, #{lastupdatemanname}, now())")
    @Options(useGeneratedKeys=true)
    public void addDealerPart(Dealer dealer);
    @Update("update dealers set dealerid=#{dealerid} where id=#{id}")
    public void setDealeridById(@Param("dealerid")String dealerid, @Param("id")int id);

    public default void addDealer(Dealer dealer){
        this.addDealerPart(dealer);
        dealer.setDealerid("YMW"+dealer.getId());
        this.setDealeridById(dealer.getDealerid(), dealer.getId());
    }

    //更新交易员
    @Update("update dealers set dealerid=#{dealerid}, dealername=#{dealername}, dealerphone=#{dealerphone}," +
            "lastupdatemanid=#{lastupdatemanid}, lastupdatemanname=#{lastupdatemanname}" +
            "where deliveryregion=#{deliveryregion} and deliveryprovince=#{deliveryprovince}" +
            "and deliveryplace=#{deliveryplace}")
    public void updateDealer(Dealer dealer);

    //交易员备份信息
    @Insert("insert into dealerbackup(dealerid, dealername, dealerphone, deliveryregion, deliveryprovince, " +
            "deliveryplace, lastupdatemanid, lastupdatemanname, iswork, createtime) values(#{dealerid}, " +
            "#{dealername}, #{dealerphone}, #{deliveryregion}, #{deliveryprovince}, #{deliveryplace}, " +
            "#{lastupdatemanid}, #{lastupdatemanname}, #{iswork}, now())")
    public void addDealerBackup(DealerBackup dealerBackup);


    //通过区域，省市，港口，姓名，电话判断 此港口是否存在此交易员
    @Select("select * from dealers where deliveryprovince=#{deliveryprovince} and deliveryplace=#{deliveryplace} and dealerid=#{dealerid}")
    public Dealer getDealerByProvincePlaceDealerid(@Param("deliveryprovince")String deliveryprovince,
                                                   @Param("deliveryplace")String deliveryplace,
                                                   @Param("dealerid")String dealerid);

    //禁用交易员
    @Update("update dealers set status=#{status} where id=#{id}")
    public void setDealerStatusById(@Param("status")String status, @Param("id")int id);

    //更改交易员手机号
    @Update("update admins set phone=#{phone} where id=#{id}")
    public void updateDealerPhoneById(@Param("phone")String dealerphone,
                                      @Param("id")int id);

    //按照区域，省市，港口 查找交易员
    @Select("select * from dealers where deliveryregion=#{deliveryregion} and " +
            "deliveryprovince=#{deliveryprovince} and deliveryplace=#{deliveryplace}")
    public List<Dealer> getDealerByRegionProvincePlace(@Param("deliveryregion")String deliveryregion,
                                                       @Param("deliveryprovince")String deliveryprovince,
                                                       @Param("deliveryplace")String deliveryplace);



    /*****************************author zxy*****************************/

    //增加交易员所管理的港口
    @Insert("<script>insert into dealerport (dealerid,portId) <foreach collection=\'ports\' index=\'i\' item=\'port\' separator=\'union all\'>select #{id},#{port.id} from dual</foreach></script>")
    public void addDealerPort(Admin dealer);


    @Select("<script>select count(distinct(a.id)) from admins a left join dealerport dp on a.id=dp.dealerid where exists(select ur.id from usersroles ur where a.id=ur.userid and ur.roleid=1) and a.status!='Deleted'" +
            "<if test='regionId!=0'> and dp.portid in (select id from areaports where parentid in(select id  from areaports where parentid=#{regionId}))</if>" +
            "<if test='provinceId!=0'> and dp.portid in(select id from areaports where parentid=#{provinceId})</if>" +
            "<if test='portId!=0'> and dp.portid=#{portId}</if>" +
            "<if test='content!=null'> and (a.phone like #{content} or a.name like #{content})</if></script>")
    public int countDealer(@Param("regionId")int regionId,
                           @Param("provinceId")int provinceId,
                           @Param("portId")int portId,
                           @Param("content")String content);

    @Select("<script>select a.id,a.username,a.isactive,a.phone,a.jobnum,a.name,a.status from admins a left join dealerport dp on a.id=dp.dealerid where exists(select ur.id from usersroles ur where a.id=ur.userid and ur.roleid=1) and a.status!='Deleted'" +
            "<if test='regionId!=0'> and dp.portid in (select id from areaports where parentid in(select id  from areaports where parentid=#{regionId}))</if>" +
            "<if test='provinceId!=0'> and dp.portid in(select id from areaports where parentid=#{provinceId})</if>" +
            "<if test='portId!=0'> and dp.portid=#{portId}</if>" +
            "<if test='content!=null'> and (a.phone like #{content} or a.name like #{content})</if> " +
            "group by a.id order by a.status desc,a.id asc  limit #{limit} offset #{offset}</script>")
    @Results(value= {
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "isactive", column = "isactive"),
            @Result(property = "phone", column = "phone"),
            @Result(property = "name", column = "name"),
            @Result(property = "jobnum", column = "jobnum"),
            @Result(property = "status", column = "status"),
            @Result(property = "ports", column = "id",
                    javaType = List.class,
                    many = @Many(select = "kitt.core.persistence.AreaportMapper.findPortByDealerId"))
    })
    public List<Admin> findDealers(@Param("regionId")int regionId,
                                   @Param("provinceId")int provinceId,
                                   @Param("portId")int portId,
                                   @Param("content")String content,
                                   @Param("limit") int limit,
                                   @Param("offset") int offset);

    public default Pager<Admin> pageAllDealer(int regionId, int provinceId, int portId, String content, int page, int pagesize){
        return Pager.config(this.countDealer(regionId,provinceId,portId, Where.$like$(content)), (int limit, int offset) -> this.findDealers(regionId, provinceId, portId, Where.$like$(content), limit, offset))
                .page(page, pagesize);

    }
    //通过交易员id查找港口
    @Select("select portid from dealerport where dealerid=#{value}")
    public List<Integer> findPortIdByDealerId(int dealerId);


    //删除交易员下的港口
    @Delete("<script>delete from dealerport where dealerId=#{dealerId} and portid in  <foreach collection='portIds' index='i' open=\'(\' separator=\',\' close=\')\' item='portId'>#{portId}</foreach></script>")
    public void deleteDealerInPort(@Param("dealerId")int dealerId,@Param("portIds")List<Integer>dealerIds);

    //添加交易员所管理的港口
    @Insert("<script>insert into dealerport (dealerid,portId) <foreach collection=\'portIds\' index=\'i\' item=\'portId\' separator=\'union all\'>select #{dealerId},#{portId} from dual</foreach></script>")
    public void addDealerInPort(@Param("dealerId")int dealerId,@Param("portIds")List<Integer>portIds);


    @Insert("<script>insert into dealerport (dealerid,portId) <foreach collection=\'dealerIds\' index=\'i\' item=\'dealerId\' separator=\'union all\'>select #{dealerId},#{portId} from dual</foreach></script>")
    public void addPortInDealer(@Param("portId")int portId,@Param("dealerIds")List<Integer>dealerIds);


    //删除港口下的交易员
    @Delete("<script>delete from dealerport where portId=#{portId} and dealerId in  <foreach collection='dealerIds' index='i' open=\'(\' separator=\',\' close=\')\' item='dealerId'>#{dealerId}</foreach></script>")
    public void deleteDealerPort(@Param("portId")int portId,@Param("dealerIds")List<Integer>dealerIds);

    //删除交易员的港口信息
    @Delete("delete from dealerport where dealerid=#{value}")
    public void deletePort(int dealerId);

    //查看该港口的所有交易员
    @Select("select a.id, a.name, a.phone from admins a where exists (select dealerid from dealerport dp where a.id=dp.dealerid and dp.portid=#{value} and a.isactive=1)")
    public List<Admin> findAllDealerByPortId(int portId);

    //查看不是该港口的所有交易员

    @Select("select a.id,a.name,a.phone from admins a left join usersroles ur on a.id=ur.userid where not \n" +
            "exists (select dealerid from dealerport dp where a.id=dp.dealerid and dp.portid=#{value}) \n" +
            "and ur.roleid=1 and a.isactive=1 group by a.id ")
    public List<Admin> findAllDealer(int portId);

    //查看该港口的所有交易员id
    @Select("select dp.dealerid from  dealerport dp where  dp.portid=#{value} group by dp.dealerid")
    public List<Integer> findAllDealerIdByPortId(int portId);

    @Select("select id,username,name,phone from admins where id=#{value}")
    public Admin findDealerById(int id);

    //根据省份id找交易员
    @Select("select a.id, a.name, a.phone from admins a, dealerport dp where a.id = dp.dealerid and  dp.portid in (select id from areaports where parentid = #{value})  and a.isactive=1 group by a.id")
    public List<Admin> findDealerByProvinceId(int provinceId);

    //查找区域下的所有交易员
    @Select("select a.id,a.name,a.phone from admins a, dealerport dp where a.id=dp.dealerid and a.isactive=1 and  dp.portid in " +
            "(" +
            "select id from areaports  where parentid in(" +
            "select id from areaports where parentid=(select parentid from areaports where id=#{value})" +
            ")" +
            ") group by a.id")
    public List<Admin> findRegionAllDealer(int provinceId);
    //找系统内所有的交易员
    @Select("select a.id,a.username,a.isactive,a.phone,a.jobnum,a.name,a.status from admins a , roles,usersroles   where a.id=usersroles.userid   " +
            "and roles.id=usersroles.roleid and roles.rolecode='Trader'  and a.isactive=1  group by a.id order by a.id")
    public List<Admin> findyiMeiAllDealer();

    //找不包括当前用户的所有交易员
    @Select("select a.id,a.username,a.isactive,a.phone,a.jobnum,a.name,a.status from admins a " +
            " where exists(select * from usersroles ur where ur.userid=a.id and ur.roleid=1 and a.isactive=1 and a.id!=#{value})")
    public  List<Admin> findyiMeiDealer(int id);

    @Select("select * from dealers where status='在职'")
    List<Dealer> getAllOnJobDealers();

    @Select("select * from admins where jobnum=#{jobnum}")
    List<Admin> getAdminByJobNum(String jobnum);

    @Update("update admins set status=#{status}, name=#{name}, phone=#{phone}, jobnum=#{jobnum} where jobnum=#{oldjobnum}")
    int updateAdminsByJobNum(@Param("status")String status,
                             @Param("name")String name,
                             @Param("phone")String phone,
                             @Param("jobnum")String jobnum,
                             @Param("oldjobnum")String oldjobnum);

    @Select("select count(id) from admins where username=#{username}")
    int getUsernameCount(String username);

    @Insert("insert into admins(username, password, isactive, phone, jobnum, name) " +
            "values(#{username}, #{password}, 1, #{phone}, #{jobnum}, #{name})")
    @Options(useGeneratedKeys=true)
    int addAdmin(Admin admin);

    @Update("update admins set username=#{username} where id=#{id}")
    int setUsernameById(@Param("username")String username,
                        @Param("id")int id);

    @Update("update admins set jobnum=#{jobnum} where id=#{id}")
    int setJobNumById(@Param("jobnum")String jobnum,
                      @Param("id")int id);

    @Update("update dealerport set dealerid=#{newdealerid} where dealerid=#{dealerid}")
    int updateDealerid(@Param("newdealerid")int newdealerid,
                       @Param("dealerid")int dealerid);


    @Update("update admins set  name=#{name}, phone=#{phone} where id=#{id}")
    int updateNameAndPhone(@Param("name")String name,@Param("phone")String phone,@Param("id")int id);

    @Update("update admins set jobnum=#{jobnum} where id=#{id}")
    int updateAdminsJobNumById(@Param("jobnum")String jobnum,
                               @Param("id")int id);

    @Select("select * from admins")
    List<Admin> getAdminList();

    /*default void doChangeAdminJobnum(){
        List<Admin> adminList = getAdminList();
        for(Admin admin : adminList){
            String jobnum = "YMW" + admin.getId();
            updateAdminsJobNumById(jobnum, admin.getId());
        }
    }*/


    /*
    @Transactional
    default void doChangeDealersAdmins(){
        List<Dealer> dealerList = getAllOnJobDealers();
        for(Dealer dealer : dealerList){
            if(!StringUtils.isNullOrEmpty(dealer.getDealerid()) && getAdminByJobNum(dealer.getDealerid()).size() == 1){
                Admin admin = getAdminByJobNum(dealer.getDealerid()).get(0);
                int adminId = admin.getId();
                updateAdminsByJobNum(EnumAdmin.OnJob.toString(), dealer.getDealername(), dealer.getDealerphone(), "YMW" + adminId, dealer.getDealerid());
                if(updateDealerid(adminId, dealer.getId()) != 1){
                    for(int i=0; i<10; i++){
                        System.out.println("----------设置dealer - id 出错，AAAAAAAAAAAAAAAAAAAAAAAAAAAAA--------------- newdealer - id=" + admin.getId() + ", --- olddealer - id" + dealer.getId());
                    }
                }
                updateSellinfoTraderIdList(dealer.getId(), adminId);
                updateDemandTraderIdList(dealer.getId(), adminId);
                updateOrderTraderIdList(dealer.getId(), adminId);

                System.out.println("ERROR-ERROR ------ 更新成功！ BBBBBBBBBBBBBBBBBBBBBBBBBBBBBB --------" + dealer.getId() + "---" + dealer.getDealername() + "---" + dealer.getDealerphone());
            } else{
                String username = HanyuToPinyin(dealer.getDealername());
                if(username.length() > 20){
                    username = username.substring(0, 20);
                }
                String password = DigestUtils.md5Hex("123456");
                //String phone = dealer.getDealerphone();
                Admin admin = new Admin();
                //admin.setPassword(password);
                //admin.setName(dealer.getDealername());
                //admin.setPhone(phone);
                //admin.setStatus(EnumAdmin.OnJob);
                //admin.setIsactive(true);
                //int id;
                //if(getUsernameCount(username) <= 0){
                    admin.setUsername(username);
                    admin = new Admin(EnumAdmin.OnJob.toString(), username, password, dealer.getDealername(), dealer.getDealerphone(), true);
                    addAdmin(admin);
                    id = admin.getId();
                    for(int i=0; i<10; i++) {
                        System.out.println("添加admin CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC id = " + id);
                    }
                } else{
                    admin = new Admin(EnumAdmin.OnJob.toString(), password, dealer.getDealername(), dealer.getDealerphone(), true);
                    addAdmin(admin);
                    id = admin.getId();
                    for(int i=0; i<10; i++){
                        System.out.println("添加admin DDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDDD id = " + id);
                    }
                    username = username + id;
                    if(setUsernameById(username, id) != 1){
                        for(int i=0; i<10; i++) {
                            System.out.println("设置用户名（username）出错了1111111111111111111111111 -------- id " + id);
                        }
                    }
                }
                if(setJobNumById("YMW" + id, id) != 1){
                    for(int i=0; i<10; i++) {
                        System.out.println("设置工号（jobnum）出错了22222222222222222222222222 -------- id " + id);
                    }
                }
                if(updateDealerid(id, dealer.getId()) != 1){
                    for(int i=0; i<10; i++){
                        System.out.println("更改dealerport表中的dealerid出错，3333333333333333333333333333 ------------id " + id);
                    }
                }

                updateSellinfoTraderIdList(dealer.getId(), admin.getId());
                updateDemandTraderIdList(dealer.getId(), admin.getId());
                updateOrderTraderIdList(dealer.getId(), admin.getId());

                System.out.println("ERROR-ERROR 444444444444444444444444444444444444  ------ 更新成功！----------   " + dealer.getId() + "---" + dealer.getDealername() + "---" + dealer.getDealerphone());
            }
        }

    }

    */
/*
    @Update("update sellinfo set traderid=#{newtraderid} where traderid=#{traderid}")
    void updateSellinfoTraderIdList(@Param("traderid")int traderid,
                                    @Param("newtraderid")int newtraderid);

    @Update("update demands set traderid=#{newtraderid} where traderid=#{traderid}")
    void updateDemandTraderIdList(@Param("traderid")int traderid,
                                  @Param("newtraderid")int newtraderid);

    @Update("update orders set traderid=#{newtraderid} where traderid=#{traderid}")
    void updateOrderTraderIdList(@Param("traderid")int traderid,
                                 @Param("newtraderid")int newtraderid);
*/

    /*default String HanyuToPinyin(String name){
        String pinyinName = "";
        char[] nameChar = name.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i=0; i<nameChar.length; i++) {
            if (nameChar[i] > 128) {
                try {
                    pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return pinyinName;
    }*/

    @Update("update sellinfo set dealerphone=#{phone} where traderid=#{traderid}")
    int updateSellinfoDealerphoneByTraderId(@Param("phone")String phone,
                                            @Param("traderid")int traderid);

    @Update("update demands set traderphone=#{phone} where traderid=#{traderid}")
    int updateDemandTraderphoneByTraderId(@Param("phone")String phone,
                                          @Param("traderid")int traderid);

    @Update("update orders set dealerphone=#{phone} where traderid=#{traderid}")
    int updateOrderTraderphoneByTraderId(@Param("phone")String phone,
                                         @Param("traderid")int traderid);
    //查询指定团队里的交易员
    @Select("select id,name,phone from admins where teamId=#{value} and isactive=1 ")
    public List<Admin> findAdminByTeamId(Integer value);

    //查询指定团队里的交易员id
    @Select("select id from admins where teamId=#{value} and isactive=1")
    public List<Integer> findAdminIdByTeamId(Integer value);

    //查找不是这个团队的交易员
//    @Select("select * from admins where isactive=1  or (teamId!=#{value}  and teamId is not null )")

    @Select("select a.id,a.username,a.isactive,a.phone,a.jobnum,a.name,a.status from admins a , roles,usersroles   where a.id=usersroles.userid   " +
            "and roles.id=usersroles.roleid and roles.rolecode='Trader'  and a.isactive=1 and (a.teamId!=#{value} or a.teamId is  null)    group by a.id order by a.id")
    public List<Admin> findWithoutAdminByTeamIds(Integer value);

    @Update("<script>update admins set teamid=#{teamId} where id in <foreach collection='list' index='i' open='(' separator=',' close=')' item='adminId'>#{adminId}</foreach></script>")
    public  void updateTeamIdForDealer(@Param("teamId") Integer teamId,@Param("list") List<Integer>adminIds);

}






