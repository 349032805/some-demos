package kitt.admin.controller;

import kitt.admin.annotation.Authority;
import kitt.admin.basic.JsonController;
import kitt.admin.basic.exception.NotFoundException;
import kitt.core.domain.AuthenticationRole;
import kitt.core.domain.Frame;
import kitt.core.domain.Menu;
import kitt.core.persistence.MenuMapper;
import kitt.core.util.text.TextCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 15/3/5.
 */
@Controller
public class MenuController extends JsonController {
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private TextCheck textCheck;

    @RequestMapping("/menu/list")
    @ResponseBody
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Admin)
    public Object getMenuList() {
        if (session.getAdmin() != null) {
            Map<String, Object> map = new HashMap<String, Object>();
            List<Menu> firstMenuList =  menuMapper.getMenusByParentId(0);
            if (firstMenuList != null) {
                List<Frame> frameList = new ArrayList<Frame>();
                for (Menu menu : firstMenuList) {
                    List<Menu> menusList = menuMapper.getMenusByParentId(menu.getId());
                    if (menusList != null && menusList.size() != 0) frameList.add(new Frame(menu, menusList));
                    else frameList.add(new Frame(menu, null));
                }
                map.put("frameList", frameList);
            } else {
                map.put("frameList", null);
            }
            return map;
        }
        return null;
    }

    //获取父级菜单
    @RequestMapping("/menu/getParentMenus")
    @ResponseBody
    public Object doGetParentMenus(@RequestParam(value = "level", required = true) int level){
        return menuMapper.getMenuListByParentid(level-2);
    }

    //新增菜单 编辑菜单
    @RequestMapping("/menu/addUpdate")
    @ResponseBody
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    public Object doAddUpdateMenu(Menu newmenu){
        newmenu.setMenuname(newmenu.getMenuname().replaceAll(" ", ""));
        newmenu.setUrl(newmenu.getUrl().replaceAll(" ", ""));
        if(!textCheck.doTextCheckThree(newmenu.getMenuname())){
            return new Object(){
                public boolean success = false;
                public String error = "菜单名称中包含非法字符！";
            };
        } else if(!textCheck.doTextCheckThree(newmenu.getUrl())){
            return new Object(){
                public boolean success = false;
                public String error = "菜单URL中包含非法字符！";
            };
        }
        List<Menu> menuList = menuMapper.getMenuListByParentid(newmenu.getParentid());
        for(Menu menu : menuList){
            if(menu.getMenuname().equals(newmenu.getMenuname()) && !menu.getId().equals(newmenu.getId())){
                return new Object(){
                    public boolean success = false;
                    public String error = "同一目录下已存在相同名字的菜单，操作失败！";
                };
            }
        }
        return menuMapper.saveMenu(newmenu);
    }

    @RequestMapping("/menu/getMenuById")
    @ResponseBody
    public Menu doGetMenuById(@RequestParam(value = "id", required = true)int id){
        return menuMapper.getMenuById(id);
    }

    @RequestMapping("/menu/deleteMenu")
    @ResponseBody
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Admin)
    public Object doDeleteMenus(@RequestParam(value = "id", required = true)int id){
        if(menuMapper.getMenuById(id) == null) {
            return new Object(){
                public boolean success = false;
                public String error = "该菜单不存在，或者已经被删除！";
            };
        }
        List<Menu> menuList = menuMapper.getMenusByParentId(id);
        if(menuList != null && menuList.size() > 0){
            return new Object(){
                public boolean success = false;
                public String error = "你要删除的目录中包含有子目录，请先删除子目录，再删除！";
            };
        } else {
            return new Object(){
                public boolean success = menuMapper.deleteMenuById(id) == 1 ? true : false;
            };
        }
    }

    @RequestMapping("/menu/changeSequence")
    @ResponseBody
    @Authority(role = AuthenticationRole.BackgroundSupporter)
    @Authority(role = AuthenticationRole.Admin)
    public Object doChangeSequence(@RequestParam(value = "id", required = true)int id,
                                   @RequestParam(value = "sequence", required = true)int sequence){
        Menu menu = menuMapper.getMenuById(id);
        if(menu == null) throw new NotFoundException();
        return menuMapper.changeSequenceById(sequence, id) == 1 ? true : false;
    }

}
