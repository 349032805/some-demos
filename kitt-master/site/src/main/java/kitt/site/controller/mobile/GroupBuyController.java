package kitt.site.controller.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by xiangyang on 15-6-10.
 */
@Controller("mobileGroupbuyController")
@RequestMapping("/m")
public class GroupBuyController {

    //团购资质
    @RequestMapping("/account/groupQualification")
    public String qualificationList(){
            return "/m/myZone_certification";
    }

    //团购订单
    @RequestMapping("/account/groupOrderList")
    public String groupOrderList(){
        return "/m/myZone_groupOrder";
    }

}
