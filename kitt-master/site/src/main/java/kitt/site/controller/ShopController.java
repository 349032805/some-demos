package kitt.site.controller;

import com.mysql.jdbc.StringUtils;
import kitt.core.domain.Shop;
import kitt.core.persistence.*;
import kitt.core.util.PageQueryParam;
import kitt.ext.mybatis.Where;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.BuyMethod;
import kitt.site.service.CustomerMethod;
import kitt.site.service.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by liuxinjie on 15/9/9.
 */
@Controller
@RequestMapping("/store")
public class ShopController {
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private DictionaryMapper dictionaryMapper;
    @Autowired
    private Session session;
    @Autowired
    private MyInterestMapper myInterestMapper;
    @Autowired
    private AreaportMapper areaportMapper;
    @Autowired
    private BuySearchMapper buySearchMapper;
    @Autowired
    private DemandMapper demandMapper;
    @Autowired
    private BuyMethod buyMethod;
    @Autowired
    private CustomerMethod customerMethod;

    final int ShopPageSize = 8;

    /**
     * 旗舰店详细
     * @param id               旗舰店id
     * @param map
     * @return
     */
    @RequestMapping("/detail")
    public String doShowShopDetail(@RequestParam(value = "id", required = true)int id,
                                   @RequestParam(value = "price01", required = false, defaultValue = "0")Integer price01,
                                   @RequestParam(value = "price02", required = false, defaultValue = "0")Integer price02,
                                   @RequestParam(value = "pname", required = false, defaultValue = "")String pname,
                                   @RequestParam(value = "portId", required = false, defaultValue = "0")Integer portId,
                                   @RequestParam(value = "sortType", required = false, defaultValue = "0")Integer sortType,
                                   @RequestParam(value = "sortOrder", required = false, defaultValue = "1")Integer sortOrder,
                                   @RequestParam(value = "pid", required = false, defaultValue = "")String pid,
                                   @RequestParam(value = "type", required = false, defaultValue = "")String type,
                                   Map<String, Object> map, PageQueryParam param){
        param.setPagesize(ShopPageSize);
        Shop shop = shopMapper.getActiveShopById(id);
        if(shop == null) throw new NotFoundException("该旗舰店不存在或者已经下架！");
        map.put("shop", shop);
        int totalCount = 0;
        if(StringUtils.isNullOrEmpty(type)){
            totalCount = shopMapper.getSelectSellInfoCountByShopId(id, price01, price02, pname, portId, sortType, sortOrder);
            param.setCount(totalCount);
            map.put("sellInfoList", shopMapper.getSelectSellInfoListByShopId(id, price01, price02, pname, portId, sortType, sortOrder, param.getPagesize(), param.getIndexNum()));
        } else{
            if(type.equals("supply")){
                totalCount = buySearchMapper.getSellInfoCountByPid(Where.$like$(pid), id);
                param.setCount(totalCount);
                map.put("sellInfoList", buySearchMapper.getSellInfoListByPid(Where.$like$(pid), id, param.getPagesize(), param.getIndexNum()));
            } else if(type.equals("demand")){
                totalCount = demandMapper.getDemandCountByPid(Where.$like$(pid), id);
                param.setCount(totalCount);
                map.put("sellInfoList", demandMapper.getDemandListByPid(Where.$like$(pid), id, param.getPagesize(), param.getIndexNum()));
            }
        }
        map.put("totalCount", totalCount);
        map.put("page", param.getPage());
        map.put("pagesize", param.getPagesize());
        map.put("pnames", dictionaryMapper.getAllCoalTypes());
        map.put("harbourlist", shopMapper.getSellInfoHarbourListByShopId(id));
        map.put("price01", price01);
        map.put("price02", price02);
        map.put("pname", pname);
        map.put("portId", portId);
        map.put("pid", pid);
        String portName = portId == 0 ? "交货地" : (areaportMapper.getNameById(portId) == null ? "其它" : areaportMapper.getNameById(portId));
        map.put("portName", portName);
        map.put("sortType", sortType);
        map.put("sortOrder", sortOrder);
        map.put("interest", (session != null && session.getUser() != null && myInterestMapper.getMyInterestBySid(shop.getId(), session.getUser().getId(), "shop") != null && !myInterestMapper.getMyInterestBySid(shop.getId(), session.getUser().getId(), "shop").isIsdelete()));
        map.put("FriendlyLinkList", customerMethod.getHomePageFriendlyLinkList());
        return "/store/store";
    }


}
