package kitt.admin.controller;

import kitt.admin.basic.BaseController;
import kitt.admin.service.Auth;
import kitt.core.domain.Frame;
import kitt.core.domain.Menu;
import kitt.core.persistence.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

/**
 * Created by joe on 10/26/14.
 */
@Controller
public class IndexController extends BaseController {
    @Autowired
    private MenuMapper menuMapper;

    @RequestMapping("/")
    public String index(Map<String, Object> model) {
        if(session.getAdmin() != null) {
            List<Menu> firstRoleMenuList = menuMapper.getMenuByUseridParentid(session.getAdmin().getId(), 0);
            if(firstRoleMenuList != null && firstRoleMenuList.size() != 0){
                Collections.sort(firstRoleMenuList, new Comparator<Menu>() {
                    @Override
                    public int compare(Menu o1, Menu o2) {
                        return o1.getSequence().compareTo(o2.getSequence());
                    }
                });
                List<Frame> frameList = new ArrayList<Frame>();
                for (Menu menu : firstRoleMenuList) {
                    List<Menu> secondMenusList = menuMapper.getMenuByUseridParentid(session.getAdmin().getId(), menu.getId());
                    if(secondMenusList != null && secondMenusList.size() != 0) {
                        Collections.sort(secondMenusList, new Comparator<Menu>() {
                            @Override
                            public int compare(Menu o1, Menu o2) {
                                return o1.getSequence().compareTo(o2.getSequence());
                            }
                        });
                        frameList.add(new Frame(menu, secondMenusList));
                    } else{
                        frameList.add(new Frame(menu, null));
                    }
                }
                model.put("frameList", frameList);
                model.put("menuNums", frameList.size()+2);
            } else{
                model.put("frameList", null);
            }
            return "index";
        } else {
            return "redirect:/login";
        }
    }


}
