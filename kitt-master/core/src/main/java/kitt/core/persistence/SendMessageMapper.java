package kitt.core.persistence;

import kitt.core.domain.SendMessage;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Created by lich on 15/12/30.
 */
public interface SendMessageMapper {
    @Insert("insert into sendmessagelog (telephone,content,userid,operator,createtime) value(#{telephone},#{content},#{userid},#{operator},now())")
    public void addMessage(SendMessage sendMessage);


    //获取短信总数
    @Select("<script>select count(*) from sendmessagelog where isdelete=0 " +
            "<if test='oper!=null and oper!=\"\"'> and operator like #{oper}</if>" +
            "<if test='mobile!=null and mobile!=\"\"'> and telephone like #{mobile}</if>" +
            "</script>")
    public int countMessage(@Param("oper")String oper,@Param("mobile")String mobile);
    //获取短信列表
    @Select("<script>select * from sendmessagelog where isdelete=0" +
            "<if test='oper!=null and oper!=\"\"'> and operator like #{oper}</if>" +
            "<if test='mobile!=null and mobile!=\"\"'> and telephone like #{mobile}</if>" +
            " order by lastupdatetime desc limit #{limit} offset #{offset}</script>")
    public List<SendMessage> findMessageList(@Param("oper") String oper, @Param("mobile") String mobile,@Param("limit") int limit, @Param("offset") int offset);

    public default Pager<SendMessage> getMessageList(String oper,String mobile,int page, int pagesize){
        return Pager.config(this.countMessage(oper,mobile), (int limit, int offset) -> this.findMessageList(oper,mobile,limit, offset))
                .page(page, pagesize);
    }
}
