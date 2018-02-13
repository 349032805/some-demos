package kitt.site.controller.mobile;

import kitt.site.basic.JsonController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by fanjun on 15/12/4.
 */

@Controller("mobileFinanceController")
@RequestMapping("/m")
public class FinanceController extends JsonController {

    //tab页
    @RequestMapping(value = "/finance/{flag}", method = RequestMethod.GET)
    public String finance(@PathVariable("flag") String flag, Model model) {
        model.addAttribute("flag", flag);
        return "/m/finance/finance";
    }

    //填写表格页
    @RequestMapping(value = "/financeApply/{flag}", method = RequestMethod.GET)
    public String financeApply(@PathVariable("flag") String flag, Model model) {
        model.addAttribute("flag", flag);
        return "/m/finance/financeApply";
    }

    //详情介绍页
    @RequestMapping(value = "/financeDetail/{flag}", method = RequestMethod.GET)
    public String financeDetail(@PathVariable("flag") String flag, Model model) {
        model.addAttribute("flag", flag);
        return "/m/finance/financeDetail";
    }

    //金融成功
    @RequestMapping(value = "/finance/goToFinanceSuccess", method = RequestMethod.GET)
    public String goToFinanceSuccess() {
        return "/m/finance/financeSuccess";
    }
}
