
package kitt.core.persistence;

import kitt.core.domain.Finance;
import kitt.core.util.Pager;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by fanjun on 15-1-21.
 */
public interface FinanceMapper {

    @Insert("insert into finance(type,companyname,address,businessarea,amountnum,contact,phone,createtime) values(" +
            "#{type},#{companyname},#{address},#{businessarea},#{amountnum},#{contact},#{phone},now())")
    public int addFinance(Finance finance);

    //后台page
    @Select("select count(*) from finance where status=#{status} and isdelete=0")
    public int countAllFinanceCustomer(String status);

    @Select("select * from finance where status=#{status} and isdelete=0 order by createtime desc limit #{limit} offset #{offset}")
    public List<Finance> listAllFinanceCustomer(@Param("status")String status,
                                                @Param("limit") int limit,
                                                @Param("offset") int offset);

    public default Pager<Finance> pageAllFinanceCustomer(String status, int page, int pagesize){
        return Pager.config(this.countAllFinanceCustomer(status), (int limit, int offset) -> this.listAllFinanceCustomer(status, limit, offset))
                .page(page, pagesize);
    }

    //获取今天金融客户集合
    @Select("select * from finance where date_format(createtime,'%Y-%m-%d') = date_format(now(),'%Y-%m-%d') and isdelete=0")
    public List<Finance> getTodayFinaceCustomers();

    //获取所有金融客户
    @Select("select * from finance where isdelete=0")
    public List<Finance> getAllFinanceCustomers();

    //根据id获取金融客户申请单
    @Select("select * from finance where id=#{id}")
    Finance getFinanceById(int id);

    //根据id 处理 金融客户申请单
    @Update("update finance set status='Solved' where status='ToBeSolved' and id=#{id} and isdelete=0")
    int doSolveFinanceOrderById(int id);

    //添加金融申请单的 处理备注等信息
    @Update("update finance set solvedremarks=#{solvedremarks}, solvedmanid=#{solvedmanid}, solvedmanusername=#{solvedmanusername}, solvedmanname=#{solvedmanname} where id=#{id} and status='Solved' and isdelete=0")
    int doAddFinanceSolvedInfo(@Param("solvedremarks")String solvedremarks,
                               @Param("solvedmanid")int solvedmanid,
                               @Param("solvedmanusername")String solvedmanusername,
                               @Param("solvedmanname")String solvedmanname,
                               @Param("id")int id);

    //删除已经处理的 金融申请单
    @Update("update finance set isdelete=1 where id=#{id} and status='Solved'")
    int doDeleteFinanceOrderById(int id);



}