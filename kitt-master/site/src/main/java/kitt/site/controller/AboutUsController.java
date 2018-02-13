package kitt.site.controller;

import kitt.core.domain.AboutUs;
import kitt.core.domain.Article;
import kitt.core.domain.Seoconfig;
import kitt.core.persistence.AboutUsMapper;
import kitt.core.persistence.ArticleMapper;
import kitt.core.util.PageQueryParam;
import kitt.site.service.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanjun on 15-4-28.
 */
@Controller
public class AboutUsController {

    @Autowired
    protected AboutUsMapper aboutUsMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private Session session;

    @RequestMapping("/aboutUs")
    public String aboutUs(Map<String, Object> model) {
        model.put("flag","intro");
        model.put("title", Seoconfig.aboutUs_title);
        model.put("keywords",Seoconfig.aboutUs_keywords);
        model.put("description",Seoconfig.aboutUs_description);
        return "/aboutUs/aboutCompany";
    }

    //易煤简介
    @RequestMapping("/aboutUs/intro")
    public String intro(Map<String, Object> model,String pos) {
        if(!StringUtils.isBlank(pos)){
            model.put("pos",pos);
        }
        model.put("flag","intro");
        model.put("title", Seoconfig.companyProfile_title);
        model.put("keywords",Seoconfig.companyProfile_keywords);
        model.put("description",Seoconfig.companyProfile_description);
        return "/aboutUs/aboutCompany";
    }

    //最新动态
    @RequestMapping(value = "/aboutUs/news", method = RequestMethod.GET)
    public String news(PageQueryParam param, Map<String, Object> model) {
        getNewsList(param, "news", "/易煤动态", model);
        model.put("title", Seoconfig.ymdt_title);
        model.put("keywords",Seoconfig.ymdt_keywords);
        model.put("description", Seoconfig.ymdt_description);
        return "/aboutUs/aboutNewsList";
    }

    //网站公告
    @RequestMapping("/aboutUs/notices")
    public String notices(PageQueryParam param,Map<String, Object> model) {
        getNewsList(param, "notices", "/网站公告", model);
        model.put("title", Seoconfig.webSiteAnnounce_title);
        model.put("keywords",Seoconfig.webSiteAnnounce_keywords);
        model.put("description", Seoconfig.webSiteAnnounce_description);
        return "/aboutUs/aboutNewsList";
    }

    private void getNewsList(PageQueryParam param, String type, String pathname, Map<String, Object> model) {
        model.put("flag",type);
        Article article = articleMapper.getArticleByPathname(pathname);
        List<Article> list = new ArrayList<>();
        if(article == null){
            model.put("list", list);
            model.put("totalCount", 0);
        } else {
            int totalCount = articleMapper.getArticleCountByParentid(article.getId());
            param.setCount(totalCount);
            list = articleMapper.getArticleListByParentid(article.getId(), param.getPagesize(), param.getIndexNum());
            model.put("list", list);
            model.put("totalCount", totalCount);
        }
        model.put("pageNumber", param.getPage());
        model.put("pagesize", param.getPagesize());
    }

    @RequestMapping(value="/aboutUs/newsDetail", method = RequestMethod.GET)
    public String newsDetail(@RequestParam(value = "id", required = true)int id, Model model) {
        getNewsDetail("news", id, model);
        model.addAttribute("title", Seoconfig.latestNews_title);
        model.addAttribute("keywords",Seoconfig.latestNews_keywords);
        model.addAttribute("description", Seoconfig.latestNews_description);
        return "/aboutUs/aboutUsDetail";
    }

    @RequestMapping("/aboutUs/noticesDetail")
    public String noticesDetail(@RequestParam(value = "id", required = true)int id, Model model) {
        getNewsDetail("notices", id, model);
        model.addAttribute("title", Seoconfig.webSiteAnnounce_title);
        model.addAttribute("keywords",Seoconfig.webSiteAnnounce_keywords);
        model.addAttribute("description",Seoconfig.webSiteAnnounce_description);
        return "/aboutUs/aboutUsDetail";
    }

    private void getNewsDetail(String type, int id, Model model) {
        model.addAttribute("flag", type);
        Article news = articleMapper.getById(id);
        articleMapper.doAddViewTimesById(id);
        model.addAttribute("news", news);
    }

    //招贤纳士
    @RequestMapping("/aboutUs/invite")
    public String invite(Map<String, Object> model) {
        model.put("flag","invite");
        List<AboutUs> inviteList = aboutUsMapper.getAboutUsByType("invite");
        model.put("inviteList",inviteList);
        model.put("title", Seoconfig.recruiting_title);
        model.put("keywords",Seoconfig.recruiting_keywords);
        model.put("description", Seoconfig.recruiting_description);
        return "/aboutUs/hire";
    }

    //联系我们
    @RequestMapping("/aboutUs/contact")
    public String contact(Map<String, Object> model) {
        model.put("flag","contact");
        model.put("title", Seoconfig.contactUs_title);
        model.put("keywords",Seoconfig.contactUs_keywords);
        model.put("description", Seoconfig.contactUs_description);
        return "/aboutUs/contactUs";
    }

    //建议反馈
    @RequestMapping("/aboutUs/advice")
    public String advice(Map<String, Object> model) {
        model.put("flag","advice");
        model.put("title", Seoconfig.adviceFeedback_title);
        model.put("keywords",Seoconfig.adviceFeedback_keywords);
        model.put("description", Seoconfig.adviceFeedback_description);
        return "/aboutUs/advice";
    }

    //获取验证码
    @RequestMapping("/aboutUs/checkCode")
    @ResponseBody
    public Object checkCode() {
        String picCode = session.getPicCode();
        Map map = new HashMap<>();
        map.put("picCode",picCode);
        return map;
    }

    //保存建议
    @RequestMapping("/aboutUs/saveAdvice")
    @ResponseBody
    public Object saveAdvice(String content,String contact) {
        if(content != null && contact != "" && contact != null && contact != ""){
            AboutUs advice = new AboutUs();
            advice.setContent(content);
            advice.setType("advice");
            advice.setContact(contact);
            advice.setStatus("noCommunicate");
            advice.setUpdatetime(LocalDateTime.now());
            aboutUsMapper.addAboutUs(advice);
        }
        boolean success = true;
        Map map = new HashMap<>();
        map.put("success", success);
        return map;
    }

}
