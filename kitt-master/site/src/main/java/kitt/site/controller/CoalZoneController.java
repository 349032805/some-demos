package kitt.site.controller;

import kitt.core.domain.OrderByType;
import kitt.core.domain.SellInfo.*;
import kitt.core.domain.rs.CoalZoneRs;
import kitt.core.persistence.DataBookMapper;
import kitt.core.persistence.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Created by fanjun on 16/1/8.
 */
@Controller
public class CoalZoneController {
    @Autowired
    protected DataBookMapper dataBookMapper;
    @Autowired
    private ShopMapper shopMapper;


    /**
     *
     * @param coalZoneRs
     * @param pageQueryParam
     * @param orderType 排序类型 priceAsc,priceDesc,salesAsc,salesDesc
     * @param displaymode  展示类型:enterprise,product
     * @param model
     * @return
     */
    @RequestMapping(value = "/coalzone",method = RequestMethod.GET)
    public String coalzone(CoalZoneRs coalZoneRs,
                           CoalZoneRs.CoalZoneRsPage pageQueryParam,
                           @RequestParam(required = false,defaultValue = "shopNameAsc")OrderType orderType,
                           @RequestParam(required = false,value = "displaymode",defaultValue = "enterprise") CoalZoneRs.Type  displaymode,
                           Model model) {
         int totalPage=-1;
         int totalCount=-1;
         List<CoalZoneRs>rs = null;
        if(displaymode== CoalZoneRs.Type.enterprise){
            //按照所在地&煤矿企业个数 e.x:内蒙古(20)  展示店铺
             totalCount=shopMapper.findCoalZoneShopCount(coalZoneRs);
             rs = shopMapper.findCoalZoneShopList(coalZoneRs,pageQueryParam,enterpriseOrderStrategy(orderType));
            for(CoalZoneRs r:rs){
                //设置店铺主售商品信息
                CoalZoneRs coal= shopMapper.findMajorProduct(r.getShopId());
                if(coal!=null){
                    r.setCoaltype(coal.getCoaltype());
                    r.setPrice(coal.getPrice());
                    r.setPnameCount(coal.getPnameCount());
                    r.setSupplyQuantity(coal.getSupplyQuantity());
                }
            }
            model.addAttribute("provinceList",shopMapper.findCoalEnterprise());
        }else {
            //按煤矿企业统计:按煤矿企业统计 e.x:内蒙古: 蒙泰集团 远兴能源   展示商品
             totalCount=shopMapper.findCoalZoneSupplyCount(coalZoneRs);
             rs = shopMapper.findCoalZoneSupply(coalZoneRs,pageQueryParam,orderStrategy(orderType));
             model.addAttribute("companyList",shopMapper.findAllCoalEnterprise());
        }
        //煤矿企业个数
        model.addAttribute("coalEnterpriseCount",shopMapper.countCoalEnterprise());
        //煤矿专区产品个数
        model.addAttribute("supplyCount",shopMapper.countSupplyWithInCoalZone());
        //煤矿专区商品煤种统计
        model.addAttribute("coalList",shopMapper.coalTypeWithInCoalZone());
        model.addAttribute("coalzone", coalZoneRs);
        model.addAttribute("orderType",orderType);
        model.addAttribute("displaymode",displaymode);
        totalPage = totalCount / pageQueryParam.getPagesize() ;
        totalPage = totalCount % pageQueryParam.getPagesize() == 0 ? totalPage : totalPage + 1;
        pageQueryParam.setTotalCount(totalCount);
        pageQueryParam.setTotalPage(totalPage);
        pageQueryParam.setList(rs);
        model.addAttribute("page",pageQueryParam);
        return "coalzone/index";
    }

    //排序规则
    private OrderByType enterpriseOrderStrategy(OrderType orderType){
        //封装排序参数
        OrderByType orderByType = new OrderByType();
        if (orderType == OrderType.priceAsc) {
            orderByType.asc("price");
        } else if (orderType == OrderType.priceDesc) {
            orderByType.desc("price");
        } else if (orderType == OrderType.salesAsc) {
            orderByType.asc("soldquantity");
        } else if (orderType == OrderType.salesDesc) {
            orderByType.desc("soldquantity");
        } else if(orderType == OrderType.shopNameAsc){
            orderByType.asc("convert(provinceName using gbk)");
        }
        return  orderByType;
    }

    //排序规则
    private OrderByType orderStrategy(OrderType orderType){
        //封装排序参数
        OrderByType orderByType = new OrderByType();
        if (orderType == OrderType.priceAsc) {
            orderByType.asc("IFNULL(ykj,0)+IFNULL(jtjlast,0)");
        } else if (orderType == OrderType.priceDesc) {
            orderByType.desc("IFNULL(ykj,0)+IFNULL(jtjlast,0)");
        } else if (orderType == OrderType.salesAsc) {
            orderByType.asc("soldquantity");
        } else if (orderType == OrderType.salesDesc) {
            orderByType.desc("soldquantity");
        } else if(orderType == OrderType.shopNameAsc){
            orderByType.asc("shopName");
        }
        return  orderByType;
    }



}
