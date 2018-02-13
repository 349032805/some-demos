package kitt.site.controller.mobile;

import com.google.common.collect.Maps;
import kitt.core.domain.Areaport;
import kitt.core.domain.SellInfo;
import kitt.core.persistence.BuyMapper;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.BaseController;
import kitt.site.service.mobile.DemandService;
import kitt.site.service.mobile.SupplyService;
import kitt.site.service.mobile.WebSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Eno on 04/29/15.
 */
@Controller
@RequestMapping("/m")
public class HomeController extends BaseController {

    @Autowired
    private BuyMapper buyMapper;

    @Autowired
    private SupplyService supplyService;

    @Autowired
    private DemandService demandService;

    @Autowired
    private WebSiteService webSiteService;


    /**
     * 首页
     */
    @RequestMapping(method = RequestMethod.GET)
    public String home(Model model) {
        List<SellInfo> recommendList = buyMapper.getRecommendSellinfoList();
        model.addAttribute("recommendList", recommendList);
        return "/m/index";
    }


    @RequestMapping(value = "/loadBaseData", method = RequestMethod.GET)
    @ResponseBody
    public Object loadBaseDatas() {
        Map<String, Object> maps = Maps.newHashMap();
        List<Areaport> regions = supplyService.loadRegion();
        List<Areaport> provinces = supplyService.loadAreaById(regions.get(0).getId());
        List<Areaport> ports = supplyService.loadAreaById(provinces.get(0).getId());
        maps.put("regions", regions);
        maps.put("provinces", provinces);
        maps.put("ports", ports);
        maps.put("coalTypes", demandService.loadCoalTypes());
        maps.put("deliveryModes", demandService.loadDeliveryModes());
        maps.put("inspectionAgencys", demandService.loadAllInspectionagencys());
        return maps;
    }

    @RequestMapping(value = "/loadAllProvince", method = RequestMethod.GET)
    @ResponseBody
    public Object loadAllProvince() {
        return supplyService.loadProvince();
    }


    @RequestMapping(value = "/loadArea/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Object loadAreaById(@PathVariable int id) {
        return supplyService.loadAreaById(id);
    }

    @RequestMapping(value = "/contactUs", method = RequestMethod.GET)
    public String aboutUs() {
        return "/m/news/contactUs";
    }

    //公司简介
    @RequestMapping(value = "/cpProfile", method = RequestMethod.GET)
    public String companyProfile() {
        return "/m/news/cpProfile";
    }

    //网站公告
    @RequestMapping(value = "/webNotice", method = RequestMethod.GET)
    public String webNotice(PageQueryParam queryParam, Model model) {
        final String type = "/网站公告";
        model.addAttribute("webNotice", webSiteService.websiteNews(queryParam, type));
        return "/m/news/webNotice";
    }

    //网站公告加载更多
    @RequestMapping(value = "/webNoticePage", method = RequestMethod.GET)
    public String webNoticePage(PageQueryParam queryParam, Model model) {
        final String type = "/网站公告";
        model.addAttribute("webNoticePage", webSiteService.websiteNews(queryParam, type));
        return "/m/news/webNewsList";
    }

    //最新动态
    @RequestMapping(value = "/webNews", method = RequestMethod.GET)
    public String webNews(PageQueryParam queryParam, Model model) {
        final String type = "/网站动态";
        model.addAttribute("webNews", webSiteService.websiteNews(queryParam, type));
        return "/m/news/webNews";
    }

    //最新动态加载更多
    @RequestMapping(value = "/webNewsPage", method = RequestMethod.GET)
    public String webNewsPage(PageQueryParam queryParam, Model model) {
        final String type = "/网站动态";
        model.addAttribute("webNewsPage", webSiteService.websiteNews(queryParam, type));
        return "/m/news/webNewsList";
    }

    //加载文章详细
    @RequestMapping(value = "/article/{id}", method = RequestMethod.GET)
    public String articleDetail(@PathVariable("id") int id, Model model) {
        model.addAttribute("articleDetail", webSiteService.articleDetail(id));
        return "/m/news/articleDetail";
    }

    //进入个人中心
    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public String accountCenter() {
        return "/m/myzone/myZone";
    }

    //敬请期待
    @RequestMapping(value = "/expecting",method = RequestMethod.GET)
    public String stayTuned(){
        return "/m/expecting";
    }
}
