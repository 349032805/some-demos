package kitt.site.controller;

import kitt.core.bl.CustomerService;
import kitt.core.domain.Finance;
import kitt.core.domain.MapObject;
import kitt.core.domain.Seoconfig;
import kitt.site.basic.JsonController;
import kitt.site.service.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by fanjun on 15-1-21.
 */
@Controller
public class FinanceController extends JsonController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private Auth auth;

    //跳转到煤易融页面
    @RequestMapping("/finance")
    public String gotoFinance(String flag,Map<String, Object> model) {
        model.put("flag",flag);
        model.put("title", Seoconfig.financial_title);
        model.put("keywords",Seoconfig.financial_keywords);
        model.put("description",Seoconfig.financial_description);
        return "financing";
    }

    //保存金融申请单
    @RequestMapping(value = "/finance/add", method = RequestMethod.POST)
    @ResponseBody
    public Object saveContact(Finance finance, @RequestParam(value = "imagecode", required = true)String imagecode) {
        MapObject map = auth.doCheckImageCode(imagecode);
        if (map.isSuccess()) {
            map = customerService.doAddFinance(map, finance);
        }
        return map;
    }
}
