package kitt.core.persistence;

import kitt.core.domain.TenderDeclaration;
import kitt.core.domain.TenderQuotes;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by lich on 15/11/13.
 */
public interface TenderQuotesMapper {
    @Insert("<script>" +
            "insert into tenderquotes(" +
            "<if test='companyname!=null and companyname!=\"\"'>companyname,</if>" +
            "userid,tenderdeclarationid,tendercode,status,createtime) values(" +
            "<if test='companyname!=null and companyname!=\"\"'>#{companyname},</if>" +
            "#{userid},#{tenderdeclarationid},#{tendercode},#{status}," +
            "#{createtime} )" +
            "</script>")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    public int addTenderquote(TenderQuotes tenderQuotes);

    @Update("update tenderquotes set isdelete=#{isdelete} where userid=#{userid} and tenderdeclarationid=#{tenderdeclarationid}")
    public int updateQuoteStatusBy(@Param("isdelete")int isdelete,@Param("tenderdeclarationid")int tenderdeclarationid,@Param("userid")int userid);



    @Delete("delete from tenderquotes where tenderdeclarationid=#{id} and userid=#{userid}")
    public int deleteTenderquote(@Param("id")int id,@Param("userid")int userid);

    @Select("select * from tenderquotes where userid=#{userid} and tenderdeclarationid=#{tenderdeclarationid} and isdelete=0 ")
    public List<TenderQuotes> selTenderquote(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("userid")int userid);

    @Select("select * from tenderquotes where userid=#{userid} and tenderdeclarationid=#{tenderdeclarationid}")
    public List<TenderQuotes> selTeQuote(@Param("tenderdeclarationid")int tenderdeclarationid,@Param("userid")int userid);





}
