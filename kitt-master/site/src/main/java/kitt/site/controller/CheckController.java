package kitt.site.controller;

import kitt.core.domain.Company;
import kitt.core.domain.SellInfo;
import kitt.core.domain.User;
import kitt.core.persistence.BuyMapper;
import kitt.core.persistence.CompanyMapper;
import kitt.core.persistence.UserMapper;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jack on 15/1/17.
 */
@Controller
public class CheckController extends JsonController {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private CompanyMapper companyMapper;

    //检查公司信息状态
    @RequestMapping("/doCheckCompany")
    @ResponseBody
    @LoginRequired
    public Object doCheckCompany() {
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        String error = "";
        User user = userMapper.getUserByPhone(session.getUser().getSecurephone());
        if(user == null) throw new NotFoundException();
        Company company = companyMapper.getCompanyByUserid(user.getId());
        if(company != null) {
            if (user.getVerifystatus().equals("待完善信息")) {
                error = "lackinfo";
            } else if (user.getVerifystatus().equals("待审核") || company.getVerifystatus().equals("待审核")) {
                error = "verifying";
            } else if (user.getVerifystatus().equals("审核未通过") || company.getVerifystatus().equals("审核未通过")) {
                error = "notpass";
            } else if (user.getVerifystatus().equals("审核通过") && company.getVerifystatus().equals("审核通过")) {
                success = true;
            }
        } else{
            error = "lackinfo";
        }
        map.put("error", error);
        map.put("success", success);
        return map;
    }

    //检查用户是否登录
    @RequestMapping("/doCheckLogin")
    @ResponseBody
    public Object doCheckLogin(){
        if(session.getUser() == null){
            return false;
        } else{
            return true;
        }
    }

    //检测卖家和买家是否是同一公司
    @RequestMapping("/doCheckSeller")
    @ResponseBody
    @LoginRequired
    public Object doCheckSeller(@RequestParam(value = "id", required = true)int id) {
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if(sellInfo == null) throw new NotFoundException();
        if(!sellInfo.getSeller().equals("自营") && sellInfo.getSellerid() == session.getUser().getId()){
            return false;
        } else{
            return true;
        }
    }

    /**
     * 检查session 及 用户是否登录
     */
    public void doCheckSessionUser() {
        if(session == null || userMapper.getUserById(session.getUser().getId()) == null){
            throw new BusinessException("请刷新页面,重新登录!");
        }
    }


}
