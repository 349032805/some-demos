package kitt.core.persistence;


import com.mysql.jdbc.StringUtils;
import kitt.core.domain.User;
import kitt.core.domain.Userlogin;
import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public interface UserMapper {

	//新增用户 lxj
    @Insert("insert into users (securephone, nickname, password, registertime, isactive, clienttype, userFrom) values (#{securephone}, " +
		            "#{nickname}, #{password}, #{registertime}, #{isactive}, #{clienttype}, #{userFrom})")
    @Options(useGeneratedKeys=true)
    public void addUser(User user);

	//禁用，启用账户 add by lxj
	@Update("update users set isactive=#{isactive} where securephone=#{securephone}")
	public int editUserAccount(@Param("isactive")boolean isactive, @Param("securephone")String securephone);

    //设置用户公司信息审核状态
    @Update("update users set verifystatus=#{verifystatus}, verifytime=#{verifytime} where id=#{id}")
    public int setUserVerifyStatus(@Param("verifystatus")String verifystatus,
                                   @Param("verifytime")LocalDateTime verifytime,
                                   @Param("id")int id);

    //登陆
    @Select("select * from users where securephone=#{securephone} limit 1")
    public User getUserByPhone(String securephone);

    //登陆
    @Select("select * from users where wxopenid=#{wexinopenid} limit 1")
    public User getUserByWxopenid(String wexinopenid);

    //email登录
    @Select("select * from users where email=#{email} limit 1")
    public User getUserByEmail(String email);

    //判断用户修改的手机是否存在
    @Select("select count(*) from users where securephone=#{securephone}")
    public int getUserByNewSecurephone(String securephone);

    final String UserSelect1 = "<if test='content!= null'> securephone like #{content} or id in(select userid from companies where name like #{content} or legalpersonname like #{content})</if>";
	@Select("<script>" +
            "select distinct count(*) from users" +
            "<where>" + UserSelect1 + "</where>" +
            "</script>")
	public int countUserBySelect(@Param("content")String content);

    @Select("<script>" +
            "select distinct * from users" +
            "<where>" + UserSelect1 + "</where> limit #{limit} offset #{offset}"+
            "</script>")
    public List<User> getUserBySelect(@Param("content")String content,
                                      @Param("limit")int limit,
                                      @Param("offset")int offset);

    public default Pager<User> pageSelectUser(String content, int page, int pagesize){
        return Pager.config(this.countUserBySelect(content), (int limit, int offset) -> this.getUserBySelect(content, limit, offset))
                .page(page, pagesize);
    }

    public default Pager<User> pageAllSelectUser(String content, int page, int pagesize){
        if(!StringUtils.isNullOrEmpty(content)){
            content = "%" + content + "%";
        }
        return pageSelectUser(content, page, pagesize);
    }

    //通过ID修改密码
    @Update("update users set password=#{password} where id=#{id} and password!=#{password}")
    public int modifyPasswdById(@Param("id") int id,
                                @Param("password") String password);

    //通过手机号修改密码
    @Update("update users set password=#{password} where securephone=#{securephone} and password!=#{password}")
    public int modifyPasswdByPhone(@Param("password") String password,
                                   @Param("securephone") String securephone);

    //通过邮箱修改密码
    @Update("update users set password=#{password} where email=#{email} and password!=#{password}")
    public int modifyPasswdByEmail(@Param("password") String password, @Param("email") String email);

    //插入用户登陆记录
    @Insert("insert into userlogins (userid,logintime,loginip,loginby,useragent,acceptlanguage) values (#{userid},#{logintime},#{loginip},#{loginby},#{useragent},#{acceptlanguage})")
    public void addUserlogin(Userlogin userlogin);

    final String UserSelectAdminStr1 = "<if test='securephone==null or securephone==\"\" '> and u.verifystatus=#{status}</if>";
    final String UserSelectAdminStr2 = " u.verifystatus=#{status} ";
    final String UserSelectAdminStr3 = "" +
            "<if test='securephone!=null and securephone!=\"\" '>" +
            " and (c.name like #{securephone} or u.securephone like #{securephone} or c.legalpersonname like #{securephone} " +
            " <if test='status==\"审核通过\"'> or a.name like #{securephone} </if>" +
            ") </if>" +
            "<if test='clienttype!=0'> and u.clienttype=#{clienttype}</if>" +
            "<if test='startDate!=null and  endDate!=null'> and date(u.registertime) between  #{startDate} and #{endDate}</if> " +
            "<if test='startDate!=null and endDate==null '> and date(u.registertime) between  #{startDate} and CURDATE()</if>";

    @Select("<script>" +
            " select count(u.id) from users u " +
            " left join companies c on u.id=c.userid " +
            " left join admins a on u.traderid=a.id "
            + Where.where + UserSelectAdminStr1 + UserSelectAdminStr3 + Where.$where +
            "</script>")
    public int countAllUser(@Param("status")String status,
                            @Param("securephone")String securephone,
                            @Param("clienttype")int clienttype,
                            @Param("startDate") LocalDate date1,
                            @Param("endDate") LocalDate date2);

    @Select("<script>" +
            " select u.id,u.securephone, u.nickname,u.registertime, u.verifytime, u.isactive, u.verifystatus, u.clienttype, u.userfrom,c.name, s.id as shopid, a.name as tradername " +
            " from users u " +
            " left join companies c on u.id=c.userid " +
            " left join shop s on u.id=s.userid " +
            " left join admins a on u.traderid=a.id "
            + Where.where + UserSelectAdminStr1 + UserSelectAdminStr3 + Where.$where +
            " order by verifytime desc, registertime desc limit #{limit} offset #{offset}</script>")
    public List<Map<String, Object>> listAllUser(@Param("status")String status,
                                                 @Param("securephone")String securephone,
                                                 @Param("clienttype")int clienttype,
                                                 @Param("startDate") LocalDate date1,
                                                 @Param("endDate") LocalDate date2,
                                                 @Param("limit") int limit,
                                                 @Param("offset") int offset);


    public default Pager<Map<String, Object>> pageAllUser(String status, String securepPhone, int clienttype, LocalDate date1, LocalDate date2, int page, int pagesize){
       return Pager.config(this.countAllUser(status,Where.$like$(securepPhone),clienttype,date1,date2), (int limit, int offset) -> this.listAllUser(status,Where.$like$(securepPhone),clienttype,date1, date2,limit, offset))
                .page(page, pagesize);
    }


    @Select("<script>" +
            " select c.name as companyname, c.address, u.registertime, DATE_FORMAT(u.verifytime, '%Y-%m-%d') as verifytime, u.securephone, a.name as tradername from users u " +
            " left join companies c on u.id=c.userid " +
            " left join admins a on u.traderid=a.id "
            + Where.where + UserSelectAdminStr2 + UserSelectAdminStr3 + Where.$where +
            "</script>")
    public List<Map<String,Object>> userExport(@Param("status")String status,
                                               @Param("securephone")String securephone,
                                               @Param("clienttype")int clienttype,
                                               @Param("startDate") LocalDate date1,
                                               @Param("endDate") LocalDate date2);


    //修改昵称
    @Update("update users set nickname=#{nickname} where securephone=#{securephone}")
    public void modifyNickname(@Param("nickname") String nickname, @Param("securephone") String securephone);

    //修改手机号
    @Update("update users set securephone=#{securephone} where id=#{userid}")
    public int modifyPhone(@Param("securephone") String securephone, @Param("userid") int userid);

    //修改email
    @Update("update users set email=#{email} where id=#{userid}")
    public int modifyEmail(@Param("email") String email, @Param("userid") int userid);

    //修改账户密码
    @Update("update users set password=#{newpassword} where id=#{userid}")
    public void modifyAccountPasswd(@Param("newpassword") String newpassword,@Param("userid") int userid);

    //修改用户信息
    @Update("update users set password=#{password}, nickname=#{nickname} where securephone=#{securephone}")
    public void updateUser(@Param("securephone") String securephone,@Param("password") String password,@Param("nickname") String nickname);

    //保存我的账户的固定电话和QQ
    @Update("update users set telephone=#{telephone},qq=#{qq} where id=#{id}")
    public void modifyPhoneAndQQ(@Param("telephone") String telephone, @Param("qq") String qq,@Param("id") int id);

    @Update("update users set telephone=#{telephone}, qq=#{qq}, nickname=#{nickname} where id=#{id}")
    public void modifyPhoneQQNickname(@Param("telephone") String telephone,
                                      @Param("qq") String qq,
                                      @Param("nickname") String nickname,
                                      @Param("id") int id);

    //查询user by id
    @Select("select * from users where id = #{id}")
    public User getUserById(int id);

    @Select("select * from users where verifystatus='待完善信息'")
    List<User> getAllInfoUserList();


    //记录邮箱地址,激活码uuid和发送邮件的时间
    @Update("update users set email=#{email},verifyuuid=#{uuid},sendmailtime=now() where id=#{id}")
    public void modifyEmailAndSendMailTime(@Param("email") String email,@Param("uuid") String uuid,@Param("id") int id);

    //记录邮箱和激活时间
    @Update("update users set verifymailtime=now() where email=#{email}")
    public void modifyVerifyMailTime(String email);

    //根据激活码uuid查询用户
    @Select("select * from users where verifyuuid=#{uuid}")
    public User getUserByVerifyuuid(String uuid);


    //绑定微信账号时间
    @Update("update users set wxopenid=#{wxopenid},wxopenidtime=now() where id=#{userid}")
    public void updateWxopenid(  @Param("wxopenid")  String wxopenid, @Param("userid")  int userid);

    //根据nickname获取用户
    @Select("select id from users where nickname=#{nickname}")
    Integer getUserIdByNickname(String nickname);

    @Select("select * from users where securephone=#{securephone} or email =#{securephone}")
    public User loadUserByPhoneOrEmail(@Param("securephone")String securephone);


    @Select("select securephone from users  where id= (select sellerid from sellinfo where id=#{value})")
    public String findUserPhoneBySupplyId(int supplyId);

    @Select("select securephone from users  where id= (select userid from demands where demandcode=#{value})")
    public String findUserPhoneBydemandId(String demandId);

    /**
     * 添加，修改客户的交易员
     * @param id                  users 表id
     * @param traderid            交易员id（admins表id）
     */
    @Update("update users set traderid=#{traderid} where id=#{id}")
    boolean doAddUpdateUserTraderMethod(@Param("id")int id,
                                        @Param("traderid")int traderid);

    @Update("update users set traderid=#{newDealerId} where traderid=#{oldId}")
    void updateUserDealerByDealerId(@Param("newDealerId") int newDealerId,
                                    @Param("oldId") int oldId);

    /**
     * 通过交易员管对Id查询该团队下的所有客户
     */
    @Select("select u.id, c.name from users u, companies c where u.id=c.userid and u.traderid in (select id from admins where teamId=#{teamId})")
    List<Map<String, Object>> getUserListByTraderTeamId(int teamid);

    /**
     * 通过userid查询公司名称
     */
    @Select("select name from companies where userid=#{userid} limit 1")
    String getCompanyNameByUserId(int userid);

}
