package kitt.admin.controller;

import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Session;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 15/3/13.
 */
@RestController
public class MyCenterController {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private Session session;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private DealerMapper dealerMapper;

    @RequestMapping("/center/view")
    public Object showMyInfo(){
        Map<String, Object> map = new HashMap<String, Object>();
        Admin admin = session.getAdmin();
        if(admin == null) throw new NotFoundException();
        map.put("admin", adminMapper.getAdminById(session.getAdmin().getId()));
        List<UserRole> userRoleList = userRoleMapper.getUserRolesByUserid(admin.getId());
        String roles = "";
        for(UserRole userRole : userRoleList){
            Role role = roleMapper.getRoleById(userRole.getRoleid());
            if(role != null && role.getRolecode() != null) {
                roles += role.getRolename() + " ";
            }
        }
        map.put("roles", roles);
        return map;
    }

    @RequestMapping("/center/editphone")
    public Object doEditPhone(@RequestParam(value = "phone", required = true)String phone){
        if(session.getAdmin() == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        if(adminMapper.getByPhone(phone) == null) {
            adminMapper.updatePhoneById(phone, session.getAdmin().getId());
            dealerMapper.updateSellinfoDealerphoneByTraderId(phone, session.getAdmin().getId());
            dealerMapper.updateDemandTraderphoneByTraderId(phone, session.getAdmin().getId());
            dealerMapper.updateOrderTraderphoneByTraderId(phone, session.getAdmin().getId());
            success = true;
        } else{
            map.put("error", "phoneexist");
        }
        map.put("success", success);
        return map;
    }

    @RequestMapping("/center/resetpassword")
    public Object doResetPassword(@RequestParam("formerpassword")String formerPassword,
                                  @RequestParam("password")String password,
                                  @RequestParam("repeatpassword")String repeatPassword){
        if(session.getAdmin() == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        String error = "";
        Admin admin = adminMapper.getAdminById(session.getAdmin().getId());
        if(admin.getPassword().equals(DigestUtils.md5Hex(formerPassword))) {
            if (password.equals(repeatPassword)) {
                adminMapper.resetPasswordById(DigestUtils.md5Hex(password), session.getAdmin().getId());
                success = true;
            } else{
                error = "notequal";
            }
        } else{
            error = "incorrect";
        }
        map.put("error", error);
        map.put("success", success);
        return map;
    }

}








