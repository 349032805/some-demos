package kitt.core.persistence;

import kitt.core.domain.Phonevalidator;
import kitt.core.domain.ValidateType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * Created by fanjun on 14-11-7.
 */
public interface ValidMapper {
    //插入验证码
    @Insert("insert into phonevalidators (expiretime, phone,code,userid,contextjson,validated,validatetype,createtime,ip) values (#{expiretime}, #{phone}," +
            "#{code}, #{userid}, #{contextjson}, #{validated}, #{validatetype}, now(), #{ip})")
    public int addValid(Phonevalidator phonevalidator);

    //获取验证码对象
    @Select("select * from phonevalidators where phone=#{securephone} and code=#{code} limit 1")
    public Phonevalidator getValidcodeObj(@Param("securephone") String securephone, @Param("code") String code);

    //修改验证码无效
    @Update("update phonevalidators set validated=1, validatetime=now() where phone=#{securephone} and code=#{code} and validated=0")
    public int modifyValidatedAndTime(@Param("securephone") String securephone, @Param("code") String code);

    //根据客户手机查找最新一条验证码
    @Select("select * from phonevalidators where phone=#{phone} order by id desc limit 1")
    public Phonevalidator getValidcodeObjByPhone(@Param("phone") String phone);

    /****************author zxy ******************/

    //查询该手机号是否有10分钟以内有效的验证码
    //  @Select("select phone,code,expiretime from  phonevalidators where phone=#{phone} and validatetype=#{type}  and validated=0 and timestampdiff(MINUTE,DATE_FORMAT(NOW(),'%Y-%c-%e %H:%i'),DATE_FORMAT(expiretime,'%Y-%c-%e %H:%i'))>=0 limit 1 ")
    @Select("select * from phonevalidators where phone=#{phone} and validatetype=#{type} and validated=0 and expiretime > NOW() order by id desc limit 1 ")
    public  Phonevalidator findCodeWithin10Minute(@Param("phone")String phone,
                                                  @Param("type")ValidateType type);

    //查询验证码是否过期
    @Select("select phone,code,expiretime from phonevalidators where phone=#{phone} and code=#{code} and validatetype=#{type}  and validated=0 order by id desc limit 1")
    public  Phonevalidator findVerifyCode(@Param("phone") String securephone,
                                          @Param("code") String code,
                                          @Param("type") ValidateType type);

    //查询今日这个手机号发送短信的条数
    @Select("select count(1) from phonevalidators where phone=#{phone} and date_format(createtime,'%Y-%m-%d') = date_format(now(), '%Y-%m-%d')")
    int countTodaySMSCodeTimesByPhone(String phone);

    //查询今天这个IP地址发送短信条数
    @Select("select count(1) from phonevalidators where ip=#{ip} and date_format(createtime,'%Y-%m-%d') = date_format(now(), '%Y-%m-%d')")
    int countTodaySMSCodeTimesByIP(String ip);

}
