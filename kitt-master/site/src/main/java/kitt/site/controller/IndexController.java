package kitt.site.controller;

import kitt.core.dao.DemoDao;
import kitt.core.domain.*;
import kitt.core.domain.rs.CoalZoneRs;
import kitt.core.persistence.*;
import kitt.core.service.Freemarker;
import kitt.core.util.PageQueryParam;
import kitt.ext.mybatis.Where;
import kitt.site.basic.BaseController;
import kitt.site.service.BuyMethod;
import kitt.site.service.CustomerMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by joe on 10/26/14.
 */
@Controller
public class IndexController extends BaseController{
    @Autowired
    protected Freemarker freemarker;
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    DemoDao demoDao;
    @Autowired
    private IndexBannerMapper indexBannerMapper;
    @Autowired
    protected DemandMapper demandMapper;
    @Autowired
    private PartnerMapper partnerMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private BuyMethod buyMethod;
    @Autowired
    private FriendlyLinkMapper friendlyLinkMapper;
    @Autowired
    private CustomerMethod customerMethod;

    /** Single quote */
    public static final String SINGLE_QUOTE = "\'";
    /** Left square bracket */
    public static final String L_SQUARE_BRACKET = "[";
    /** Right square bracket */
    public static final String R_SQUARE_BRACKET = "]";
    /** Comma */
    public static final String COMMA = ",";

    public static final String ANTHRACITECOAL = "无烟煤";
    public static final String POWERCOAL = "动力煤";
    public static final String InjectionCoal = "喷吹煤";
    public static final String CokingCoal = "焦煤";
    @RequestMapping("/")
    public String index(HttpServletRequest request, Map<String, Object> model,
                        @RequestParam(value="f", required=false) String userFrom) {

        //煤炭商城
//        model.put("sellInfoList", buyMapper.getRecommendSellinfoList());
        List<SellInfo> mtscList = buyMapper.getRecommendSellinfoList();
        if(mtscList!=null&&mtscList.size()>0){
            int j = 5-mtscList.size();
            if(j==0){
                model.put("sellInfoList", mtscList);
            } else{
                List<SellInfo> subMtList=  buyMapper.getRecommendHistoryList(j);
                mtscList.addAll(subMtList);
                model.put("sellInfoList", mtscList);
            }
        } else{
            model.put("sellInfoList", buyMapper.getRecommendHistoryList(5));
        }

        //需求专区
        model.put("demandList", demandMapper.getRecommendDemandList());

        /**
         *  新进展
         */
        if(null != session.getUser()){
            model.put("latestChangeDemand", demandMapper.getPersonalLatestChangeDemand(session.getUser().getId()));
            model.put("latestChangeSellInfo", buyMapper.getPersonalLatestChangeSellInfo(session.getUser().getId()));
//            //招标信息条数
//            model.put("tenderDeclareCount", tenderdeclarMapper.countTenderDeclare(session.getUser().getId()));
//            //投标信息条数
//            model.put("tenderCount", tenderdeclarMapper.countMytender(session.getUser().getId()));
//            orderMapper.tenderTotalamount()
//            orderMapper.tenderTotalamountYesterday()
        }
        //总成交吨数, 总金额, 昨日更新总吨数
        model.put("totalAmount", orderMapper.getOrdersTotalAmount());
        model.put("totalMoney", orderMapper.getOrdersTotalMoney());
        model.put("totalAmountYesterday", orderMapper.getTotalAmountYesterday());

        //成交记录
        model.put("orderTransactionList", orderMapper.listRecentTransactionOrders());

        //首页图片
        model.put("indexBannerList", indexBannerMapper.getIndexBannnersWithLimit("web", 5));

        //首页广告
        List<IndexBanner> advertList = indexBannerMapper.getIndexBannnersWithLimit("advert", 1);
        //判断是否为空,以便取集合第一个对象不出错
        if(advertList != null && advertList.size() > 0) {
            IndexBanner adImg = advertList.get(0);
            String referer = request.getHeader("referer");

            // Show ad img only when user visits homepage directly Or visit it from other site.
            // referer=null : user visit site directly
            // referer.indexOf(request.getServerName())>0 : visit site from the link within this site
            // (not from other site, such as Search Engine, Wechat etc.)
            if (null != referer && (referer.indexOf(request.getServerName())>-1) ) {
                adImg.setIsshow(false);
            }
            model.put("advert", adImg);
        }

        //判断userFrom参数是否存在*************
        if(userFrom != null){
            if(userFrom.length() > 50) userFrom = userFrom.substring(0, 50);
            model.put("urlFlag", userFrom);
        }

        //煤矿专区 店铺列表
//        model.put("shopList", shopMapper.getAllShopObjectShowList());
        List<CoalZoneRs> shopList = shopMapper.getIndexShopList();
        for(CoalZoneRs r:shopList){
            //设置店铺主售商品信息
            CoalZoneRs coal= shopMapper.findMajorProduct(r.getShopId());
            if(coal!=null){
                r.setCoaltype(coal.getCoaltype());
                r.setPrice(coal.getPrice());
                r.setPnameCount(coal.getPnameCount());
                r.setSupplyQuantity(coal.getSupplyQuantity());
            }
        }
        model.put("shopList",shopList);

        //资讯模块
        model.put("newsFirstArea", articleMapper.getHotNewesListByFocusname(Where.$like$("首页"), 3, 0));
        model.put("newsSecondArea", articleMapper.getHotNewesListByFocusname(Where.$like$("首页"), 4, 3));
        Article article = null;
        article = articleMapper.getArticleByPathname("/独家视点");
        if(article != null) {
            List<String> sonMenuList = Arrays.asList(new String[]{"日评", "周报", "月报", "专题"});
            model.put("newsThirdArea", articleMapper.getSpecialGrandSonArticlesById(article.getId(), 4, sonMenuList));
        } else{
            model.put("newsThirdArea", new ArrayList<>());
        }
        article = articleMapper.getArticleByPathname("/相关行业");
        if(article != null) {
            List<String> sonMenuList = Arrays.asList(new String[]{"电力", "钢铁", "建材", "化工"});
            model.put("newsForthArea", articleMapper.getSpecialGrandSonArticlesById(article.getId(), 4, sonMenuList));
        } else{
            model.put("newsForthArea", new ArrayList<>());
        }


        //独家视点子目录menu
        model.put("DJDPSonMenuList", getDJDPSonMenuList());
        //相关行业子目录menu
        model.put("XGHYSonMenuList", getXGHYSonMenuList());
        //视频list
        model.put("videoList", articleMapper.getArtivleVideoList());
        //最新动态
        article = articleMapper.getArticleByPathname("/易煤动态");
        if(article != null) {
            model.put("latestNews", articleMapper.getSeveralArticlesByParentPath(article.getPath(), 3));
        } else{
            model.put("latestNews", new ArrayList<>());
        }
        //最新公告
        article = articleMapper.getArticleByPathname("/网站公告");
        if(article != null) {
            model.put("latestNotice", articleMapper.getSeveralArticlesByParentPath(article.getPath(), 1));
        } else{
            model.put("latestNotice", new ArrayList<>());
        }

        //合作伙伴list
        model.put("partnerList", partnerMapper.findPartnerList());
        model.put("title",Seoconfig.index_title);
        model.put("keywords",Seoconfig.index_keywords);
        model.put("description",Seoconfig.index_description);
        model.put("FriendlyLinkList", customerMethod.getHomePageFriendlyLinkList());
        //易煤快讯
        List<Article> ymalertsList=articleMapper.findArticlesByTitle("易煤快讯", 0, 4);
        model.put("ymalertsList",ymalertsList);
        return "index";
    }

    /**
     * Generate homepage chart info.
     * @param chartList
     * @return
     */
    public ChartInfo generateChartInfo(List<Chart> chartList) {
        if (chartList == null || chartList.size() == 0)
            return null;

        // init chart date & value collection
        StringBuffer chartDate = new StringBuffer(L_SQUARE_BRACKET);
        StringBuffer chartPrice =  new StringBuffer(L_SQUARE_BRACKET);

        // init max & min value, max=min=first_averagePrice
        double chartMin = chartList.get(0).getAverageprice();
        double chartMax = chartMin;

        for (int i=chartList.size()-1; i>=0; i--) {
            // get month & day: 2015-03-27 --> 03-27
            String date = chartList.get(i).getTradedate().toString().substring(5);
            double averagePrice = chartList.get(i).getAverageprice();

            chartDate = chartDate.append(SINGLE_QUOTE).append(date).append(SINGLE_QUOTE);
            chartPrice = chartPrice.append(averagePrice);

            // add comma if it's not the last element
            if (i > 0) {
                chartDate.append(COMMA);
                chartPrice.append(COMMA);
            }
            // find the max & min price for this chart
            chartMin = chartMin < averagePrice ? chartMin : averagePrice;
            chartMax = chartMax > averagePrice ? chartMax : averagePrice;
        }

        chartDate.append(R_SQUARE_BRACKET);
        chartPrice.append(R_SQUARE_BRACKET);

        // if chartMax!=chartMin, chart offset = (max - min)/10
        double offset = (chartMax != chartMin) ? (chartMax - chartMin) / 10 : 2;
        // add offset for the chart confine
        chartMin = chartMin - offset;
        chartMax = chartMax + offset;

        return new ChartInfo(chartList.size(), chartDate.toString(), chartPrice.toString(), chartMax, chartMin, offset);
    }

//    @RequestMapping("/test")
//    @LoginRequired
//    public String index2(){
//        return "index";
//    }

    //新手上路
    @RequestMapping("/footer/startOff")
    public String startOff(String pos,Map<String, Object> model) {
        model.put("pos",pos);
        return "/footer/startOff";
    }
    //如何买煤
    @RequestMapping("/footer/buyCoal")
    public String buyCoal() {
        return "/footer/buyCoal";
    }
    //如何卖煤
    @RequestMapping("/footer/sellCoal")
    public String sellCoal() {
        return "/footer/sellCoal";
    }

    //关于买卖
    @RequestMapping("/footer/aboutContract")
    public String aboutContract(String pos,Map<String, Object> model) {
        model.put("pos",pos);
        return "/footer/aboutContract";
    }
    //增值服务
    @RequestMapping("/footer/aboutServices")
    public String aboutServices(String pos,Map<String, Object> model) {
        model.put("pos",pos);
        return "/footer/aboutServices";
    }
    //增值服务
    @RequestMapping("/footer/aboutUs")
    public String aboutUs(String pos,Map<String, Object> model) {
        model.put("pos",pos);
        return "/footer/aboutUs";
    }
     static  class DateComparator implements Comparator<Chart>{

         public int compare(Chart c, Chart c2) {
             if(c.getTradedate().isBefore(c2.getTradedate())){
                 return -1;
             }
             return 1;
         }
    }
    private Set<SellDistribute> findBySellDistribute(String coalType) {

        List<SellInfo> originalData = buyMapper.findByCoalType(coalType);
        SellDistribute distribute = new SellDistribute();
        Set<SellDistribute> lists = new HashSet<SellDistribute>();

//		//构造区域分布的数据
        for (SellInfo sell : originalData) {
            if (null!=sell.getDeliveryregion() && sell.getDeliveryregion().equals(distribute.getSell())) {
                //得到区域下的港口
                distribute.setDeliverys(findPorts(originalData, sell.getDeliveryregion()));
                //得到港口所对应的低位热值
                distribute.setNcvs(findNcvs(originalData, distribute.getDeliverys()));
                continue;
            }
            distribute = new SellDistribute();
            distribute.setSell(sell.getDeliveryregion());
            //得到区域下的港口
            distribute.setDeliverys(findPorts(originalData, sell.getDeliveryregion()));
            //得到港口所对应的低位热值
            distribute.setNcvs(findNcvs(originalData, distribute.getDeliverys()));
            lists.add(distribute);
        }
            return lists;
    }

    private static Set<String> findPorts(List<SellInfo> originalData,String region){
        Set<String> ports = new HashSet<String>();
        for(SellInfo sell : originalData) {
            if(region.equals(sell.getDeliveryregion())){
                ports.add(sell.getDeliveryplace());
            }
        }
        return ports;
    }
    private static Set<Integer> findNcvs(List<SellInfo> originalData,Set<String> ports){
        Set<Integer> ncvs = new HashSet<Integer>();
        for(SellInfo s:originalData){
            for(String str : ports) {
                if (str.equals(s.getDeliveryplace())){
                    ncvs.add(s.getNCV());
                }
            }
        }
        return ncvs;
    }

    //@RequestMapping(value = "/getCoal", method = RequestMethod.POST)
    //@ResponseBody
    public Object getCoal(){
        List<SellInfo> powerCoalList = buyMapper.getByDeliveryregion(POWERCOAL);
        List<SellInfo> anthrAciteCoalList = buyMapper.getByDeliveryregion(ANTHRACITECOAL);
        List<SellInfo> InjectionCoalList = buyMapper.getByDeliveryregion(InjectionCoal);
        List<SellInfo> CokingCoalList = buyMapper.getByDeliveryregion(CokingCoal);
        Map<String, Object> map = new HashMap<String, Object>();
        if(powerCoalList != null){
            map.put("powerCoalList", powerCoalList);
        }
        if(anthrAciteCoalList != null){
            map.put("AnthrAciteCoalList", anthrAciteCoalList);
        }
        if(InjectionCoalList != null){
            map.put("InjectionCoalList", InjectionCoalList);
        }
        if(CokingCoalList != null){
            map.put("CokingCoalList", CokingCoalList);
        }
        return map;
    }

    //移动客户端跳转
//    @RequestMapping("/app")
//    public String getAppInfo(Map<String, Object> model) {
//        model.put("title", Seoconfig.mobileClient_title);
//        model.put("keywords",Seoconfig.mobileClient_keywords);
//        model.put("description", Seoconfig.mobileClient_description);
//        return "app";
//
//    }

    /**
     * 独家视点子目录list
     * @return
     */
    public List<Map<String, Object>> getDJDPSonMenuList(){
        List<Map<String, Object>> menuList = new ArrayList<>();
        Map<String, Object> map;
        Article article;
        article = articleMapper.getArticleByPathname("/独家视点/日评");
        if(article != null) {
            map = new HashMap<>();
            map.put("name", "日评");
            map.put("path", article.getCategory());
            menuList.add(map);
        }

        article = articleMapper.getArticleByPathname("/独家视点/周报");
        if(article != null) {
            map = new HashMap<>();
            map.put("name", "周报");
            map.put("path", article.getCategory());
            menuList.add(map);
        }

        article = articleMapper.getArticleByPathname("/独家视点/月报");
        if(article != null){
            map = new HashMap<>();
            map.put("name", "月报");
            map.put("path", article.getCategory());
            menuList.add(map);
        }

        article = articleMapper.getArticleByPathname("/独家视点/专题");
        if(article != null){
            map = new HashMap<>();
            map.put("name", "专题");
            map.put("path", article.getCategory());
            menuList.add(map);
        }
        return menuList;
    }

    /**
     * 相关行业子目录list
     * @return
     */
    public List<Map<String, Object>> getXGHYSonMenuList(){
        List<Map<String, Object>> menuList = new ArrayList<>();
        Map<String, Object> map;
        Article article;
        article = articleMapper.getArticleByPathname("/相关行业/电力");
        if(article != null) {
            map = new HashMap<>();
            map.put("name", "电力");
            map.put("path", article.getCategory());
            menuList.add(map);
        }

        article = articleMapper.getArticleByPathname("/相关行业/钢铁");
        if(article != null){
            map = new HashMap<>();
            map.put("name", "钢铁");
            map.put("path", article.getCategory());
            menuList.add(map);
        }

        article = articleMapper.getArticleByPathname("/相关行业/建材");
        if(article != null){
            map = new HashMap<>();
            map.put("name", "建材");
            map.put("path", article.getCategory());
            menuList.add(map);
        }

        article = articleMapper.getArticleByPathname("/相关行业/化工");
        if(article != null){
            map = new HashMap<>();
            map.put("name", "化工");
            map.put("path", article.getCategory());
            menuList.add(map);
        }
        return menuList;
    }


    //免费注册认证
    @RequestMapping("/teach/register")
    public String howRegister(Map<String, Object> model) {
        model.put("flag","register");
        return "teach";
    }

    //如何买煤
    @RequestMapping("/teach/buy")
    public String howBuy(Map<String, Object> model) {
        model.put("flag","buy");
        return "teach";
    }


    //如何卖煤
    @RequestMapping("/teach/sell")
    public String howSell(Map<String, Object> model) {
        model.put("flag","sell");
        return "teach";
    }

    //常见问题
    @RequestMapping("/teach/questions")
    public String questions(Map<String, Object> model) {
        model.put("flag","questions");
        return "teach";
    }

    /**
     * 网站底部友情链接list
     */
    @RequestMapping("/teach/friendlylink")
    public String getAllFriendLinkList(PageQueryParam param, Map<String, Object> model){
        int count = friendlyLinkMapper.countFriendlyList();
        param.setCount(count);
        List<FriendlyLink> friendlyList = friendlyLinkMapper.getFriendlyListBySequence(80, param.getIndexNum());
        model.put("page", param.getPage());
        model.put("pagesize", 80);
        model.put("count", count);
        model.put("friendlyList", friendlyList);
        return "/friendlyLink";
    }

    /**
     * 物流指导
     * @param queryParam
     * @return
     */
    @RequestMapping(value = "/teach/logisticsLink")
    public String logisticsLink(PageQueryParam queryParam) {
        return "/teach/logisticsLink";
    }

}
