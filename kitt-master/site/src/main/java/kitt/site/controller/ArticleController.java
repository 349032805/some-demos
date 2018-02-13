package kitt.site.controller;

import com.mysql.jdbc.StringUtils;
import kitt.core.domain.*;
import kitt.core.persistence.ArticleMapper;
import kitt.core.persistence.ChartMapper;
import kitt.core.persistence.DataMarketMapper;
import kitt.core.persistence.FocusImageMapper;
import kitt.core.util.PageQueryParam;
import kitt.ext.mybatis.Where;
import kitt.site.basic.JsonController;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by fanjun on 14-11-20.
 */
@Controller
@RequestMapping("/news")
public class ArticleController extends JsonController {
    @Autowired
    protected ArticleMapper articleMapper;
    @Autowired
    private ChartMapper chartMapper;
    @Autowired
    private DataMarketMapper dataMarketMapper;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private FocusImageMapper focusImageMapper;

    //private volatile int totalnums=20;


    // 根据path：获取资讯目录，资讯列表
    @RequestMapping(value = "/{category}")
    public String getArticleList(@PathVariable("category") String path,Map<String, Object> model, PageQueryParam param){
        Article parent = null;
        if(StringUtils.isNullOrEmpty(path)){
            parent = articleMapper.findArticleByCategory("/hyyw");
        } else{
            parent = articleMapper.findArticleByCategory(path);
        }
        if(parent == null) throw new NotFoundException("暂时没有此类资讯，敬请期待！");
        model.put("parentList", getParentArticleSamePathById(parent));
        model.put("path", path);
//        param.setCount(articleMapper.$countArticleByParentidAndContent(parent.getId(), null));
        articleMapper.siteArticleByParentidAndContent(parent.getId(), null, param.getPage(), 30).putToMap(model);
        model.put("hotnewsList", articleMapper.getHotNewesListByFocusname(Where.$like$("热点2"), 5, 0));
        //最新文章--改版新增
        model.put("newest",articleMapper.getNewestArticles(0, 5));

        model.put("newsListMenu", doShowNewsListMenu());
        model.put("dataListMenu", doShowDataListMenu());

        model.put("currentPath", path);
        List<Chart> ZSYCList = chartMapper.getAllZSYC();
        Collections.reverse(ZSYCList);
        model.put("ZSYCList", ZSYCList);
        String[] st=findThreeElementsByType(path);
        model.put("title",st[0]);
        model.put("keywords",st[1]);
        model.put("description",st[2]);
        return "articleList";
    }


    public String[] findThreeElementsByType(String type){
        String[] str=new String[3];
        if(type.equals("hyyw")){
            str[0]=Seoconfig.industryNews_title;
            str[1]=Seoconfig.industryNews_keywords;
            str[2]=Seoconfig.industryNews_description;
        }else if(type.equals("hgjj")){
            str[0]=Seoconfig.macroEconomic_title;
            str[1]=Seoconfig.macroEconomic_keywords;
            str[2]=Seoconfig.macroEconomic_description;
        }else if(type.equals("hgjj_gn")){
            str[0]=Seoconfig.native_title;
            str[1]=Seoconfig.native_keywords;
            str[2]=Seoconfig.native_description;
        }else if(type.equals("hgjj_gj")){
            str[0]=Seoconfig.international_title;
            str[1]=Seoconfig.international_keywords;
            str[2]=Seoconfig.international_description;
        }else if(type.equals("gjmt")){
            str[0]=Seoconfig.interCoal_title;
            str[1]=Seoconfig.interCoal_keywords;
            str[2]=Seoconfig.interCoal_description;
        }else if(type.equals("qydt")){
            str[0]=Seoconfig.interpriceDynamic_title;
            str[1]=Seoconfig.interpriceDynamic_keywords;
            str[2]=Seoconfig.interpriceDynamic_description;
        }else if(type.equals("zczz")){
            str[0]=Seoconfig.policyTrack_title;
            str[1]=Seoconfig.policyTrack_keywords;
            str[2]=Seoconfig.policyTrack_description;
        }else if(type.equals("xghy")){
            str[0]=Seoconfig.relateIndustry_title;
            str[1]=Seoconfig.relateIndustry_keywords;
            str[2]=Seoconfig.relateIndustry_description;
        }else if(type.equals("xghy_dl")){
            str[0]=Seoconfig.elecPower_title;
            str[1]=Seoconfig.elecPower_keywords;
            str[2]=Seoconfig.elecPower_description;
        } else if(type.equals("xghy_gt")){
            str[0]=Seoconfig.ironAndSteel_title;
            str[1]=Seoconfig.ironAndSteel_keywords;
            str[2]=Seoconfig.ironAndSteel_description;
        }else if(type.equals("xghy_jc")){
            str[0]=Seoconfig.buildingMater_title;
            str[1]=Seoconfig.buildingMater_keywords;
            str[2]=Seoconfig.buildingMater_description;
        }else if(type.equals("xghy_hg")){
            str[0]=Seoconfig.chemical_title;
            str[1]=Seoconfig.chemical_keywords;
            str[2]=Seoconfig.chemical_description;
        }else if(type.equals("djsd")){
            str[0]=Seoconfig.exclusiveViewpoint_title;
            str[1]=Seoconfig.exclusiveViewpoint_keywords;
            str[2]=Seoconfig.exclusiveViewpoint_description;
        }else if(type.equals("djsd_rp")){
            str[0]=Seoconfig.dailyReview_title;
            str[1]=Seoconfig.dailyReview_keywords;
            str[2]=Seoconfig.dailyReview_description;
        }else if(type.equals("djsd_zb")){
            str[0]=Seoconfig.weekly_title;
            str[1]=Seoconfig.weekly_keywords;
            str[2]=Seoconfig.weekly_description;
        }else if(type.equals("djsd_zt")){
            str[0]=Seoconfig.speacialSubject_title;
            str[1]=Seoconfig.speacialSubject_keywords;
            str[2]=Seoconfig.speacialSubject_description;
        }else if(type.equals("mtsc_zs")){
            str[0]=Seoconfig.indicies_title;
            str[1]=Seoconfig.indicies_keywords;
            str[2]=Seoconfig.indicies_description;
        }else if(type.equals("mtsc_jg")){
            str[0]=Seoconfig.price_title;
            str[1]=Seoconfig.price_keywords;
            str[2]=Seoconfig.price_description;
        }else if(type.equals("cykc_gk")){
            str[0]=Seoconfig.port_title;
            str[1]=Seoconfig.port_keywords;
            str[2]=Seoconfig.port_description;
        }else if(type.equals("cykc_dc")){
            str[0]=Seoconfig.powerPlant_title;
            str[1]=Seoconfig.powerPlant_keywords;
            str[2]=Seoconfig.powerPlant_description;
        }else if(type.equals("ysdd_dd")){
            str[0]=Seoconfig.scheduling_title;
            str[1]=Seoconfig.scheduling_keywords;
            str[2]=Seoconfig.scheduling_description;
        }else if(type.equals("ysdd_hy")){
            str[0]=Seoconfig.shipping_title;
            str[1]=Seoconfig.shipping_keywords;
            str[2]=Seoconfig.shipping_description;
        }else if(type.equals("ysdd_tl")){
            str[0]=Seoconfig.railway_title;
            str[1]=Seoconfig.railway_keywords;
            str[2]=Seoconfig.railway_description;
        }else if(type.equals("jck_jk")){
            str[0]=Seoconfig.import_title;
            str[1]=Seoconfig.import_keywords;
            str[2]=Seoconfig.import_description;
        }else if(type.equals("jck_ck")){
            str[0]=Seoconfig.export_title;
            str[1]=Seoconfig.export_keywords;
            str[2]=Seoconfig.export_description;
        }else if(type.equals("mtcl_cl")){
            str[0]=Seoconfig.production_title;
            str[1]=Seoconfig.production_keywords;
            str[2]=Seoconfig.production_description;
        }else if(type.equals("mtcl_xl")){
            str[0]=Seoconfig.sales_title;
            str[1]=Seoconfig.sales_keywords;
            str[2]=Seoconfig.sales_description;
        }else if(type.equals("xyhy_dl")){
            str[0]=Seoconfig.electricPower_title;
            str[1]=Seoconfig.electricPower_keywords;
            str[2]=Seoconfig.electricPower_description;
        }else if(type.equals("xyhy_gc")){
            str[0]=Seoconfig.steels_title;
            str[1]=Seoconfig.steels_keywords;
            str[2]=Seoconfig.steels_description;
        }else if(type.equals("xyhy_hg")){
            str[0]=Seoconfig.chemicalEngi_title;
            str[1]=Seoconfig.chemicalEngi_keywords;
            str[2]=Seoconfig.chemicalEngi_description;
        }else if(type.equals("xyhy_jc")){
            str[0]=Seoconfig.buildingMaterials_title;
            str[1]=Seoconfig.buildingMaterials_keywords;
            str[2]=Seoconfig.buildingMaterials_description;
        }else if(type.equals("mtsc")){
            str[0]=Seoconfig.coalMarket_title;
            str[1]=Seoconfig.coalMarket_keywords;
            str[2]=Seoconfig.coalMarket_description;
        }else if(type.equals("cykc")){
            str[0]=Seoconfig.industryInventory_title;
            str[1]=Seoconfig.industryInventory_keywords;
            str[2]=Seoconfig.industryInventory_description;
        }else if(type.equals("ysdd")){
            str[0]=Seoconfig.TransScheduling_title;
            str[1]=Seoconfig.TransScheduling_keywords;
            str[2]=Seoconfig.TransScheduling_description;
        }else if(type.equals("jck")){
            str[0]=Seoconfig.importAndExport_title;
            str[1]=Seoconfig.importAndExport_keywords;
            str[2]=Seoconfig.importAndExport_description;
        }else if(type.equals("mtcx")){
            str[0]=Seoconfig.coalProdAndMark_title;
            str[1]=Seoconfig.coalProdAndMark_keywords;
            str[2]=Seoconfig.coalProdAndMark_description;
        }else if(type.equals("xyhy")){
            str[0]=Seoconfig.downstreamIndustry_title;
            str[1]=Seoconfig.downstreamIndustry_keywords;
            str[2]=Seoconfig.downstreamIndustry_description;
        }else if(type.equals("ymkx")){
            str[0]=Seoconfig.ymkx_title;
            str[1]=Seoconfig.ymkx_keywords;
            str[2]=Seoconfig.ymkx_description;
        }else if(type.equals("hlw")){
            str[0]=Seoconfig.hlw_title;
            str[1]=Seoconfig.hlw_keywords;
            str[2]=Seoconfig.hlw_description;
        }else if(type.equals("ymdt")){
            str[0]=Seoconfig.ymdt_title;
            str[1]=Seoconfig.ymdt_keywords;
            str[2]=Seoconfig.ymdt_description;
        }else if(type.equals("dsffw")){
            str[0]=Seoconfig.dsffw_title;
            str[1]=Seoconfig.dsffw_keywords;
            str[2]=Seoconfig.dsffw_description;
        }else if(type.equals("wzgg")){
            str[0]=Seoconfig.gg_title;
            str[1]=Seoconfig.gg_keywords;
            str[2]=Seoconfig.gg_description;
        }
        return str;
    }

    // 获取指定文章内容
    @RequestMapping(value = "/article/{id}")
    public String getArticle(@PathVariable("id") int id, Map<String, Object> model){
        if(articleMapper.getById(id) == null){
            throw new NotFoundException();
        }
        Article article = articleMapper.getById(id);
        if(article == null) throw new NotFoundException();
        articleMapper.doAddViewTimesById(id);
        System.out.println(article.getContent());
        model.put("article", article);
        model.put("parentList", getParentArticleSamePathById(article));
        model.put("hotnewsList", articleMapper.getHotNewesListByFocusname(Where.$like$("热点2"), 5, 0));
        //最新文章--改版新增
        model.put("newest", articleMapper.getNewestArticles(0, 5));
        //行业要闻--改版新增
        model.put("hyywList",articleMapper.findArticlesByTitle("行业要闻",0, 5));
        //相关文章--改版新增
        //Article parent=articleMapper.getById(article.getParentid());
        //String category=parent==null?"":parent.getCategory();
        model.put("relateList",articleMapper.getRelatedArticles(article.getId(),article.getParentid()));
        model.put("title",article.getTitle()+"-易煤网");
        model.put("keywords",article.getKeywords());
        model.put("description",article.getSummary());
        List<Chart> ZSYCList = chartMapper.getAllZSYC();
        Collections.reverse(ZSYCList);
        model.put("ZSYCList", ZSYCList);
        return "articleDetails";
    }

    /**
     * 视频列表
     * @return
     */
    @RequestMapping("/videolist")
    public String doShowVideoList(@RequestParam(value="id", required=false, defaultValue = "0")int id,
                                  Map<String, Object> map){
        Video video = articleMapper.getVideoById(id);
        List<Video> videoList = articleMapper.getArtivleVideoList();
        if(video == null && videoList.size() > 0) video = articleMapper.getArtivleVideoList().get(0);
        if(video != null) {
            articleService.doAddVideoViewTimes(id);
            map.put("videoList", articleMapper.getArtivleVideoList());
            map.put("video", video);
        }
        return "video/videoList";
    }



    //首页
    @RequestMapping(value = "")
    public String toIndex(Map<String, Object> model){
        //totalnums=20;
        //首页焦点图
        Focusimage bigimage= focusImageMapper.findFocusImageByName("大焦点图");
        Focusimage smallimage1= focusImageMapper.findFocusImageByName("小焦点图1");
        Focusimage smallimage2= focusImageMapper.findFocusImageByName("小焦点图2");
        Focusimage smallimage3= focusImageMapper.findFocusImageByName("小焦点图3");
        Focusimage smallimage4= focusImageMapper.findFocusImageByName("小焦点图4");
        //左侧专栏一(易煤快讯,产地价格,港口价格) 产地价格,港口价格--待定
        List<Article> ymalertsList=articleMapper.findArticlesByTitle("易煤快讯", 0, 5);
        List<Article>  mtscjgList= articleMapper.findArtics("煤炭市场", "价格");
        List<Article>  mtsczsList= articleMapper.findArtics("煤炭市场", "指数");
        //左侧专栏二(日评,周报,专题)
        List<Article> dayList=articleMapper.findArticlesByTitle("日评",0,5);
        List<Article> monthList=articleMapper.findArticlesByTitle("周报", 0, 5);
        List<Article> specialList=articleMapper.findArticlesByTitle("专题", 0, 5);

        //右侧专栏
        List<Article> allList=articleMapper.findAllArticles(0, 20);  //全部

        // 行业要闻path
        String hyywPath="hyyw";
         //易煤快讯path
        String ymkxPath="ymkx";
        //独家视点path
        String djsdPath="djsd";
        //易煤动态path
        String ymdtPath="ymdt";
        //第三方服务
        String dsffwPath="dsffw";
        //互联网+
        String hlwPath="hlw";
        //公告
        String ggPath="wzgg";
        String mtsczsPath="mtsc_zs";
        String mtscjgPath="mtsc_jg";
           //独家视点(日评,周报,月报,专题)
        String djsdRpPath="djsd_rp";
        String djsdZbPath="djsd_zb";
        String djsdYbPath="djsd_yb";
        String djsdZtPath="djsd_zt";

        model.put("bigimage",bigimage);
        model.put("smallimage1",smallimage1);
        model.put("smallimage2",smallimage2);
        model.put("smallimage3",smallimage3);
        model.put("smallimage4",smallimage4);
        model.put("ymalertsList",ymalertsList);
        model.put("dayList",dayList);
        model.put("monthList",monthList);
        model.put("specialList",specialList);
        model.put("allList",allList);
        model.put("hyywPath",hyywPath);
        model.put("ymkxPath",ymkxPath);
        model.put("djsdPath",djsdPath);
        model.put("ymdtPath",ymdtPath);
        model.put("hlwPath",hlwPath);
        model.put("dsffwPath",dsffwPath);
        model.put("ggPath",ggPath);
        model.put("djsdRpPath",djsdRpPath);
        model.put("djsdZbPath",djsdZbPath);
        model.put("djsdYbPath",djsdYbPath);
        model.put("djsdZtPath",djsdZtPath);
        model.put("mtscjgList",mtscjgList);
        model.put("mtsczsList",mtsczsList);
        model.put("mtsczsPath",mtsczsPath);
        model.put("mtscjgPath",mtscjgPath);
        int count=articleMapper.countAllArticles();
        model.put("totalpage",count%60==0?count/60:count/60+1);
        model.put("totalCount",count);
        return "news/index";
    }


    //首页文章异步刷新
    @RequestMapping("/getByTitle/{title}/{type}/{nums}")
    @ResponseBody
    public Object getByTitle(@PathVariable("title") String title,@PathVariable("type") String type,@PathVariable("nums") int nums) {
        System.out.println("------------分类:"+title);
        System.out.println("------------方式:"+type);
            Map<String, Object> map = new HashMap<String, Object>();
           if(!org.apache.commons.lang3.StringUtils.isBlank(type)){
               if(type.equals("1")){
                   //加载更多
                   //if(totalnums<60){
                       if(!org.apache.commons.lang3.StringUtils.isBlank(title)){
                           map=getDatasByTitle(map,title,nums,10);
                           map.put("message", 0);   //查询成功
                           //totalnums=totalnums+10;
                       }else{
                           map.put("message", -2);  //标题为空
                       }
                   //}else{
                   //    map.put("message", -1); //已达到60条数据
                   //}
                   //map.put("totalnums",totalnums);
               }else if(type.equals("2")){
                   //totalnums=20;
                   //countNums=0;
                   //tab 切换
                   if(!org.apache.commons.lang3.StringUtils.isBlank(title)){
                       map=getDatasByTitle(map,title,0,20);
                       map.put("message", 0);   //查询成功
                   }else{
                       map.put("message", -2);  //标题为空
                   }

               }
           }else{
               map.put("message",-3); //type为空
           }

        return map;
    }


    //首页文章异步刷新
    @RequestMapping("/getByTitle/{title}/{type}")
    @ResponseBody
    public Object getByTitle(@PathVariable("title") String title,@PathVariable("type") String type) {
        System.out.println("------------分类:"+title);
        System.out.println("------------方式:"+type);
        Map<String, Object> map = new HashMap<String, Object>();
        if(!org.apache.commons.lang3.StringUtils.isBlank(type)){
           if(type.equals("2")){
                //totalnums=20;
                //countNums=0;
                //tab 切换
                if(!org.apache.commons.lang3.StringUtils.isBlank(title)){
                    map=getDatasByTitle(map,title,0,20);
                    map.put("message", 0);   //查询成功
                }else{
                    map.put("message", -2);  //标题为空
                }

            }
        }else{
            map.put("message",-3); //type为空
        }

        return map;
    }

    ////首页右侧文章--分页
    //@RequestMapping(value = "/pagearticle")
    //@ResponseBody
    //public Object pagearticle(
    //        @RequestParam(value = "title", required =true) String title,
    //        @RequestParam(value = "page", required = false,defaultValue = "1") int page
    //        ) {
    //    totalnums=20;
    //    countNums=(page - 1) * 60+20;
    //    title=title.trim();
    //    Map map=new HashMap<String,Object>();
    //    int totalCount=0;
    //    List<Article> pageList=new ArrayList<Article>();
    //    if(title.equals("全部")){
    //        totalCount = articleMapper.countArticlesNum();
    //        pageList =articleMapper.findAllArticles((page - 1) * 60, 20);
    //    }else if(title.equals("行业要闻")){
    //        totalCount=articleMapper.countByTitle();
    //        pageList =articleMapper.findByTitle((page - 1) * 60, 20);
    //    }else if(title.equals("易煤快讯")||title.equals("易煤动态")||title.equals("第三方服务")||title.equals("互联网＋")
    //            ||title.equals("公告")){
    //        totalCount=articleMapper.countArticlesByTitle(title);
    //        pageList=articleMapper.findArticlesByTitle(title, (page - 1) * 60, 20);
    //    }else if(title.equals("独家视点")){
    //        totalCount=articleMapper.countArticByTitle(new String[]{"独家视点"});
    //        pageList=articleMapper.getArticlesByTitle((page - 1) * 60, 20, new String[]{"独家视点"});
    //    }
    //    int totalPage=totalCount%60==0?totalCount/60:totalCount/60+1;
    //    map.put("pageList", pageList);
    //    map.put("totalCount", totalCount);
    //    map.put("totalpage",totalPage);
    //    map.put("Pagesize",60);
    //    map.put("page",page);
    //    return map;
    //}



    public Map<String, Object> getDatasByTitle( Map<String, Object> map,String title,int index,int size){
      title=title.trim();
       int count=0;
        if(title.equals("全部")){
            map.put("subList_1",articleMapper.findAllArticles(index, size));
            count=articleMapper.countAllArticles();
        }else if(title.equals("行业要闻")){
            map.put("subList_1", articleMapper.findByTitle(index, size));
            count=articleMapper.countByTitle();
        }else if(title.equals("易煤快讯")){
            map.put("subList_1",articleMapper.findArticlesByTitle("易煤快讯", index, size));
            count=articleMapper.countArticlesByTitle(title);
        }else if(title.equals("独家视点")){
            map.put("subList_1",articleMapper.getArticlesByTitle(index, size, new String[]{"独家视点"}));
            count=articleMapper.countArticByTitle(new String[]{"独家视点"});
        }else if(title.equals("易煤动态")){
            map.put("subList_1",articleMapper.findArticlesByTitle("易煤动态", index, size));
            count=articleMapper.countArticlesByTitle(title);
        }else if(title.equals("第三方服务")){
            map.put("subList_1",articleMapper.findArticlesByTitle("第三方服务", index, size));
            count=articleMapper.countArticlesByTitle(title);
        }else if(title.equals("互联网＋")){
            map.put("subList_1",articleMapper.findArticlesByTitle("互联网＋", index, size));
            count=articleMapper.countArticlesByTitle(title);
        }else if(title.equals("公告")){
            map.put("subList_1",articleMapper.findArticlesByTitle("网站公告", index, size));
            count=articleMapper.countArticlesByTitle("网站公告");
        }
        map.put("totalpage",count%60==0?count/60:count/60+1);
        map.put("totalCount",count);
        return map;
    }




//    /**
//     @author lich
//     */
//    @RequestMapping(value = "")
//    public String getAllArticles(Map<String, Object> model,@RequestParam(value = "type",required = false,defaultValue = "1")int type){
//        //热点
//        List<Map<String, Object>> hotList=  articleMapper.getHotNewsByFocusname(Where.$like$("热点1"), 5, 0);
//        //行业要闻
//        List<Article> industryList=articleMapper.findIndustryNews("行业要闻", 9);
//
//        //国际煤炭
//        List<Article> internationalList=articleMapper.findIndustryNews("国际煤炭", 5);
//
//        //企业动态
//        List<Article> interPriceList=articleMapper.findIndustryNews("企业动态", 5);
//        //政策追踪
//        List<Article> policyTrackList=articleMapper.findIndustryNews("政策追踪", 5);
//        //宏观经济
//        List<Article>  nativeList=articleMapper.findList("宏观经济", "国内", 5);
//        List<Article>  nationList=articleMapper.findList("宏观经济", "国际", 5);
//        //相关行业
//        List<Article>  powerList=articleMapper.findList("相关行业", "电力", 3);
//        List<Article>  ironAndSteelList=articleMapper.findList("相关行业", "钢铁", 3);
//        List<Article>  buildList=articleMapper.findList("相关行业", "建材", 3);
//        List<Article>  chemicalList=articleMapper.findList("相关行业", "化工", 3);
//
//        //独家视点
//        List<Article>  dayList=articleMapper.findList("独家视点", "日评", 3);
//        List<Article>  weekList=articleMapper.findList("独家视点", "周报", 3);
//        List<Article>  monthList=articleMapper.findList("独家视点", "月报", 3);
//        List<Article>  specialList=articleMapper.findList("独家视点", "专题", 3);
//
//        //行业数据---煤炭市场
//        List<Article>  digitalList=articleMapper.findCoalList("煤炭市场", 4);
//
//        //行业数据---产业库存
//        List<Article>  portList=articleMapper.findCoalList("产业库存", 4);
//
//        //行业数据---运输调度
//        List<Article> railwayList=articleMapper.findCoalList("运输调度", 4);
//
//        //行业数据---煤炭产销
//        List<Article> yieldList=articleMapper.findCoalList("煤炭产销", 4);
//
//        //行业数据---进出口
//        List<Article> importList=articleMapper.findCoalList("进出口", 4);
//
//        //行业数据---下游行业
//        List<Article> xPowerList=articleMapper.findCoalList("下游行业", 4);
//
//        //广告图片
//        List<Adverpic>  partList=articleMapper.findPartAdverPic(4);
//
//        //第一级标题path  行业要闻 宏观经济 国际煤炭 企业动态  政策追踪  相关行业   独家视点  煤炭市场  产业库存   运输调度  进出口  煤炭产销 下游行业
//
//        // 行业要闻path
//        String hyywPath="hyyw";
//
//        //宏观经济path
//        String hgjjPath="hgjj";
//
//        //国际煤炭path
//        String gjmtPath="gjmt";
//        // 企业动态path
//        String qydtPath="qydt";
//
//        //政策追踪path
//        String zczzPath="zczz";
//
//        //相关行业path
//        String xghyPath="xghy";
//
//        //独家视点path
//        String djsdPath="djsd";
//
//        //煤炭市场path
//        String mtscPath="mtsc";
//        //产业库存path
//        String cykcPath="cykc";
//        //运输调度path
//        String ystdPath="ysdd";
//        //进出口path
//        String jckPath="jck";
//        //煤炭产销path
//        String mtcxPath="mtcx";
//        //下游行业path
//        String xyhyPath="xyhy";
//
//
//      //第二级标题path
//           //宏观经济下的(国内,国际)
//        String hgjjGnPath="hgjj_gn";
//        String hgjjGjPath="hgjj_gj";
//
//           //相关行业下的(电力,钢铁,建材,化工)
//        String xghyDlPath="xghy_dl";
//        String xghyGtPath="xghy_gt";
//        String xghyJcPath="xghy_jc";
//        String xghyHgPath="xghy_hg";
//
//           //独家视点(日评,周报,月报,专题)
//        String djsdRpPath="djsd_rp";
//        String djsdZbPath="djsd_zb";
//        String djsdYbPath="djsd_yb";
//        String djsdZtPath="djsd_zt";
//
//        model.put("hotList",hotList);
//        model.put("industryList",industryList);
//        model.put("internationalList",internationalList);
//        model.put("interPriceList",interPriceList);
//        model.put("policyTrackList",policyTrackList);
//        model.put("nativeList",nativeList);
//        model.put("nationList",nationList);
//        model.put("powerList",powerList);
//        model.put("ironAndSteelList",ironAndSteelList);
//        model.put("buildList",buildList);
//        model.put("chemicalList",chemicalList);
//        model.put("dayList",dayList);
//        model.put("weekList",weekList);
//        model.put("monthList",monthList);
//        model.put("specialList",specialList);
//        model.put("digitalList",digitalList);
//        model.put("portList",portList);
//        model.put("railwayList",railwayList);
//        model.put("yieldList",yieldList);
//        model.put("importList",importList);
//        model.put("xPowerList",xPowerList);
//
//        //易煤行情
//        List<DataMarket>  dates=dataMarketMapper.findDate();
//        List<DataMarket>  cdmjList=new ArrayList<DataMarket>();
//        List<DataMarket>  gkmjList=new ArrayList<DataMarket>();
//        if(dates!=null&&dates.size()==2){
//            cdmjList=dataMarketMapper.findDataMarket(dates.get(1).getDate().toString(),dates.get(0).getDate().toString(),1,5);
//            gkmjList=dataMarketMapper.findDataMarket(dates.get(1).getDate().toString(),dates.get(0).getDate().toString(),2,5);
//        }
//        //两个日期
////        model.put("dates",dates);
//        String date1 = "";
//        String date2 = "";
//        if(dates.size() ==2){
//            date1 = getDateWord(dates.get(1).getDate());
//            date2 = getDateWord(dates.get(0).getDate());
//        }
//        model.put("date1",date1);
//        model.put("date2",date2);
//        model.put("cdmjList",cdmjList);
//        model.put("gkmjList",gkmjList);
//        if(partList!=null&&partList.size()>0){
//            model.put("adVerPic",partList.get(0));
//            //删除第一个元素
//            partList.remove(0);
//            model.put("adVerList",partList);
//        }
//
//        //第一级标题path返回
//        model.put("xghyPath",xghyPath);
//        model.put("djsdPath",djsdPath);
//        model.put("qydtPath",qydtPath);
//        model.put("gjmtPath",gjmtPath);
//        model.put("zczzPath",zczzPath);
//        model.put("hyywPath",hyywPath);
//        model.put("hgjjPath",hgjjPath);
//        model.put("mtscPath",mtscPath);
//        model.put("cykcPath",cykcPath);
//        model.put("ystdPath",ystdPath);
//        model.put("jckPath",jckPath);
//        model.put("mtcxPath",mtcxPath);
//        model.put("xyhyPath",xyhyPath);
//
//        //第二级标题path返回
//
//        //宏观经济下的(国内,国际)
//        model.put("hgjjGnPath",hgjjGnPath);
//        model.put("hgjjGjPath",hgjjGjPath);
//        //相关行业下的(电力,钢铁,建材,化工)
//        model.put("xghyDlPath",xghyDlPath);
//        model.put("xghyGtPath",xghyGtPath);
//        model.put("xghyJcPath",xghyJcPath);
//        model.put("xghyHgPath",xghyHgPath);
//        //独家视点(日评,周报,月报,专题)
//        model.put("djsdRpPath",djsdRpPath);
//        model.put("djsdZbPath",djsdZbPath);
//        model.put("djsdYbPath",djsdYbPath);
//        model.put("djsdZtPath",djsdZtPath);
//
//        model.put("title",Seoconfig.marketInfo_title);
//        model.put("keywords",Seoconfig.marketInfo_keywords);
//        model.put("description",Seoconfig.marketInfo_description);
//
////        return "industryInfo";
//        return "news/index";
//
//    }

    public String getDateWord(LocalDate date){
        int month = date.getMonth().getValue();
        int day = date.getDayOfMonth();
        return month+"月"+day+"日";
    }

    /**
     * 资讯菜单List
     *
     * 易煤快讯 第三方服务 易煤动态  网站公告  独家视点(日报/周报/专题)  互联网＋
     *
     * @return
     */
    public Object doShowNewsListMenu(){
        List<ArticleMenu> articleMenuList = new ArrayList<>();
        Collection nullValue = new Vector();
        nullValue.add(null);
        Article article = null;
        List<Article> articleList;
        article = articleMapper.getArticleByPathname("/行业要闻");
        if(article != null) articleMenuList.add(new ArticleMenu(article, null));
        article = articleMapper.getArticleByPathname("/易煤快讯");
        if(article != null) articleMenuList.add(new ArticleMenu(article, null));
        article = articleMapper.getArticleByPathname("/易煤动态");
        if(article != null) articleMenuList.add(new ArticleMenu(article, null));
        article = articleMapper.getArticleByPathname("/网站公告");
        if(article != null) articleMenuList.add(new ArticleMenu(article, null));
        article = articleMapper.getArticleByPathname("/互联网＋");
        if(article != null) articleMenuList.add(new ArticleMenu(article, null));
        article = articleMapper.getArticleByPathname("/第三方服务");
        if(article != null) articleMenuList.add(new ArticleMenu(article, null));
        article = articleMapper.getArticleByPathname("/宏观经济");
        if(article != null) {
            articleList = new ArrayList<>();
            articleList.add(articleMapper.getArticleByPathname("/宏观经济/国内"));
            articleList.add(articleMapper.getArticleByPathname("/宏观经济/国际"));
            articleList.removeAll(nullValue);
            articleMenuList.add(new ArticleMenu(article, articleList));
        }
        article = articleMapper.getArticleByPathname("/国际煤炭");
        if(article != null) articleMenuList.add(new ArticleMenu(article, null));
        article = articleMapper.getArticleByPathname("/企业动态");
        if(article != null) articleMenuList.add(new ArticleMenu(article, null));
        article = articleMapper.getArticleByPathname("/政策追踪");
        if(article != null) articleMenuList.add(new ArticleMenu(article, null));
        article = articleMapper.getArticleByPathname("/相关行业");
        if(article != null) {
            articleList = new ArrayList<>();
            articleList.add(articleMapper.getArticleByPathname("/相关行业/电力"));
            articleList.add(articleMapper.getArticleByPathname("/相关行业/钢铁"));
            articleList.add(articleMapper.getArticleByPathname("/相关行业/建材"));
            articleList.add(articleMapper.getArticleByPathname("/相关行业/化工"));
            articleList.removeAll(nullValue);
            articleMenuList.add(new ArticleMenu(article, articleList));
        }
        article = articleMapper.getArticleByPathname("/独家视点");
        if(article != null) {
            articleList = new ArrayList<>();
            articleList.add(articleMapper.getArticleByPathnameParentid("/独家视点/日评", article.getId()));
            articleList.add(articleMapper.getArticleByPathnameParentid("/独家视点/周报", article.getId()));
            articleList.add(articleMapper.getArticleByPathnameParentid("/独家视点/月报", article.getId()));
            articleList.add(articleMapper.getArticleByPathnameParentid("/独家视点/专题", article.getId()));
            articleList.removeAll(nullValue);
            articleMenuList.add(new ArticleMenu(article, articleList));
        }
        return articleMenuList;
    }

    /**
     * 数据菜单List
     * @return
     */
    public Object doShowDataListMenu(){
        List<ArticleMenu> articleMenuList = new ArrayList<>();
        Collection nullValue = new Vector();
        nullValue.add(null);
        Article article = null;
        List<Article> articleList;
        article = articleMapper.getArticleByPathname("/煤炭市场");
        if(article != null) {
            articleList = new ArrayList<>();
            articleList.add(articleMapper.getArticleByPathname("/煤炭市场/指数"));
            articleList.add(articleMapper.getArticleByPathname("/煤炭市场/价格"));
            articleList.removeAll(nullValue);
            articleMenuList.add(new ArticleMenu(article, articleList));
        }
        article = articleMapper.getArticleByPathname("/产业库存");
        if(article != null) {
            articleList = new ArrayList<>();
            articleList.add(articleMapper.getArticleByPathname("/产业库存/港口"));
            articleList.add(articleMapper.getArticleByPathname("/产业库存/电厂"));
            articleList.add(articleMapper.getArticleByPathname("/产业库存/钢厂"));
            articleList.removeAll(nullValue);
            articleMenuList.add(new ArticleMenu(article, articleList));
        }
        article = articleMapper.getArticleByPathname("/运输调度");
        if(article != null) {
            articleList = new ArrayList<>();
            articleList.add(articleMapper.getArticleByPathname("/运输调度/铁路"));
            articleList.add(articleMapper.getArticleByPathname("/运输调度/海运"));
            articleList.add(articleMapper.getArticleByPathname("/运输调度/调度"));
            articleList.removeAll(nullValue);
            articleMenuList.add(new ArticleMenu(article, articleList));
        }
        article = articleMapper.getArticleByPathname("/煤炭产销");
        if(article != null) {
            articleList = new ArrayList<>();
            articleList.add(articleMapper.getArticleByPathname("/煤炭产销/产量"));
            articleList.add(articleMapper.getArticleByPathname("/煤炭产销/销量"));
            articleList.removeAll(nullValue);
            articleMenuList.add(new ArticleMenu(article, articleList));
        }
        article = articleMapper.getArticleByPathname("/进出口");
        if(article != null) {
            articleList = new ArrayList<>();
            articleList.add(articleMapper.getArticleByPathname("/进出口/进口"));
            articleList.add(articleMapper.getArticleByPathname("/进出口/出口"));
            articleList.removeAll(nullValue);
            articleMenuList.add(new ArticleMenu(article, articleList));
        }
        article = articleMapper.getArticleByPathname("/下游行业");
        if(article != null) {
            articleList = new ArrayList<>();
            articleList.add(articleMapper.getArticleByPathname("/下游行业/电力"));
            articleList.add(articleMapper.getArticleByPathname("/下游行业/钢材"));
            articleList.add(articleMapper.getArticleByPathname("/下游行业/建材"));
            articleList.add(articleMapper.getArticleByPathname("/下游行业/化工"));
            articleList.removeAll(nullValue);
            articleMenuList.add(new ArticleMenu(article, articleList));
        }
        return articleMenuList;
    }

    /**
     * 获取文章以及其父文章组成的List
     * @param article
     * @return
     */
    public List<Article> getParentArticleById(Article article){
        List<Article> articleList = new ArrayList<>();
        while (article != null){
            articleList.add(article);
            article = articleMapper.getById(article.getParentid());
        }
        Collections.reverse(articleList);
        return articleList;
    }

    public List<Article> getParentArticleSamePathById(Article article){
        List<Article> articleList = new ArrayList<>();
        while (article != null){
            article.setPath(getSonMenuPathByParentPath(article.getPath()));
            articleList.add(article);
            article = articleMapper.getById(article.getParentid());
        }
        Collections.reverse(articleList);
        return articleList;
    }

    /**
     * 根据父亲path 获取 最低一层 子menu path
     * @param path                         父path
     * @return
     */
    public String getSonMenuPathByParentPath(String path){
        Article article = articleMapper.getArticleByPath(path);
        if(article == null) throw new NotFoundException("暂时没有此类资讯，敬请期待！");
        String sonpath = "";
        List<Article> articleList = new ArrayList<>();
        List<String> lastMenuList =  Arrays.asList(new String[]{"行业要闻", "国内", "国际", "国际煤炭", "企业动态", "政策追踪", "电力", "钢铁", "建材", "化工", "日评", "周报", "月报", "专题", "指数", "价格", "港口", "电厂", "钢厂", "铁路", "海运", "调度", "产量", "销量", "进口", "出口", "电力", "钢铁", "建材", "化工"});
        List<String> lsecodeMenuList = Arrays.asList(new String[]{"宏观经济", "相关行业", "独家视点", "煤炭市场", "产业库存", "运输调度", "煤炭产销", "进出口", "下游行业"});
        if(lastMenuList.contains(article.getTitle())){
            sonpath = article.getPath();
        } else if(lsecodeMenuList.contains(article.getTitle())) {
            articleList = articleMapper.getArticleListByParentidNoLimit(article.getId());
            if(articleList.size() > 0) {
                int sequence = 0;
                int i = -1;
                for (Article a : articleList) {
                    if (lastMenuList.contains(a.getTitle())){
                        if(i == -1) {
                            i = articleList.indexOf(a);
                            sequence = lastMenuList.indexOf(a.getTitle());
                        }
                        if(sequence >= lastMenuList.indexOf(a.getTitle())){
                            sequence = lastMenuList.indexOf(a.getTitle());
                            i = articleList.indexOf(a);
                        }
                    }
                }
                if(i != -1) {
                    sonpath = articleList.get(i).getPath();
                }
            }
        }
        return sonpath;
    }


}



