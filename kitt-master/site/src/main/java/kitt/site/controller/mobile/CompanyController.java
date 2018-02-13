package kitt.site.controller.mobile;

import kitt.core.domain.Company;
import kitt.core.domain.User;
import kitt.core.persistence.UserMapper;
import kitt.core.service.ConfigConsts;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.service.mobile.CompanyService;
import kitt.site.service.mobile.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Created by xiangyang on 15-5-19.
 */
@Controller("mobileCompanyController")
@RequestMapping("/m")
@LoginRequired
public class CompanyController extends JsonController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private UserMapper userMapper;
    @RequestMapping(value = "/company/checkCompanyStatus", method = RequestMethod.GET)
    public
    @ResponseBody
    Object checkCompanyStatus(@CurrentUser User user, BindResult result) {
        Company company = companyService.loadByUserId(user.getId());
        user = userMapper.getUserById(user.getId());
        if (company == null) {
            result.addError(ConfigConsts.companyWaitComplete, "您的公司信息不完整,请完善!");
        } else if (company.getVerifystatus().equals("审核未通过")||user.getVerifystatus().equals("审核未通过")) {
            result.addError(ConfigConsts.companynoPass,"您的公司信息审核未通过!");
        } else if (!company.getVerifystatus().equals("审核通过")||!user.getVerifystatus().equals("审核通过")) {
            result.addError(ConfigConsts.companyChecking,"您的公司信息正在审核中,请您耐心等待!");
        }
        return json(result);
    }


}
