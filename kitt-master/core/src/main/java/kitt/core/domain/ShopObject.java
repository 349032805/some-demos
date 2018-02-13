package kitt.core.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 15/9/24.
 * 店铺封装对象
 */
public class ShopObject implements Serializable {
    private Shop shop;
    private List<Map<String, Object>> coalMapList;

    public ShopObject() {
    }

    public ShopObject(Shop shop, List<Map<String, Object>> coalMapList) {
        this.shop = shop;
        this.coalMapList = coalMapList;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public List<Map<String, Object>> getCoalMapList() {
        return coalMapList;
    }

    public void setCoalMapList(List<Map<String, Object>> coalMapList) {
        this.coalMapList = coalMapList;
    }
}
