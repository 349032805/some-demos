package kitt.admin.controller;

import kitt.core.domain.Menu;
import kitt.core.domain.Operateauth;
import kitt.core.persistence.OperateMapper;
import kitt.core.persistence.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanjun on 15-3-5.
 */
@RestController
public class OperateAuthController {

    @Autowired
    private OperateMapper operateMapper;

    @Autowired
    private MenuMapper roleMenuMapper;

    @RequestMapping("/operateauth/list")
    public Object list(int page) {
        Map map = new HashMap();
        map.put("operateauth", operateMapper.pageAllOperateauth(page, 10));
        return map;
    }

    //删除操作权限
    @RequestMapping("/operateauth/deleteOperateauth")
    public boolean deleteOperateauth(int id) {
        operateMapper.deleteOperateauth(id);
        return true;
    }

    //验证操作代码唯一性
    @RequestMapping("/operateauth/checkOperatecode")
    public Object checkOperatecode(String operatecode) {
        int i = operateMapper.countOperatecode(operatecode);
        Map map = new HashMap<>();
        map.put("num",i);
        return map;
    }

    //保存操作权限
    @RequestMapping("/operateauth/saveOperateauth")
    public boolean saveOperateauth(int menuid,String operatename,String operatecode) {
        Operateauth operateauth = new Operateauth();
        operateauth.setOperatename(operatename);
        operateauth.setOperatecode(operatecode);
        Menu menu = roleMenuMapper.getMenuById(menuid);
        operateauth.setMenuid(menuid);
        operateauth.setMenuname(menu.getMenuname());
        operateMapper.addOperateauth(operateauth);
        return true;
    }

    //获取所有一级菜单供选择
    @RequestMapping("/operateauth/getParentMenus")
    public Object getParentMenus() {
        List<Menu> pmenusList = roleMenuMapper.getAllParentMenus();
        Map map = new HashMap<>();
        map.put("pmenusList",pmenusList);
        return map;
    }

    //获取所有二级菜单供选择
    @RequestMapping("/operateauth/getChildMenus")
    public Object getChildMenus(int id) {
        List<Menu> cmenusList = roleMenuMapper.getChildMenusByParentid(id);
        Map map = new HashMap<>();
        map.put("cmenusList",cmenusList);
        return map;
    }

}
