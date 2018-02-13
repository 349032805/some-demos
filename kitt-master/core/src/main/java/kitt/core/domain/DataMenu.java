package kitt.core.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by jack on 6/11/15.
 */
public class DataMenu implements Serializable {
    private IndIndexCfg menu;
    private List<DataMenu> menuList;

    public DataMenu(){}

    public DataMenu(IndIndexCfg menu, List<DataMenu> menuList) {
        this.menu = menu;
        this.menuList = menuList;
    }

    public IndIndexCfg getMenu() {
        return menu;
    }

    public void setMenu(IndIndexCfg menu) {
        this.menu = menu;
    }

    public List<DataMenu> getMenuList() {
        return menuList;
    }

    public void setMenuList(List<DataMenu> menuList) {
        this.menuList = menuList;
    }
}
