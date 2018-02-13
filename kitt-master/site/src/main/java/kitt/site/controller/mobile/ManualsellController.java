package kitt.site.controller.mobile;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by xiangyang on 15-6-10.
 */
@Controller("mobileManualsellController")
@RequestMapping("/m")

public class ManualsellController {


    //人工找货
    @RequestMapping(value = "/account/manualFindList")
    public String showSellList() {
        return "/m/myZone_manualFind";
    }

    //人工销售
    @RequestMapping(value = "/account/manualSellList")
    public String showlookupList() {
        return "/m/myZone_manualSale";
    }
}
