
package kitt.admin.controller;

import com.mysql.jdbc.StringUtils;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.JsonController;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.ArticleService;
import kitt.admin.service.Auth;
import kitt.admin.service.FileService;
import kitt.core.domain.*;
import kitt.core.persistence.ArticleMapper;
import kitt.core.persistence.DataBookMapper;
import kitt.core.service.FileStore;
import kitt.core.util.text.TextCheck;
import kitt.core.util.Pager;
import kitt.ext.mybatis.Where;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Created by lxj on 14/11/12.
 */
@RestController
@RequestMapping("/article")
public class ArticleController extends JsonController {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private TextCheck textCheck;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private Auth auth;

    /*******************  第一部分 行业资讯  *******************/
    /*******************  第一部分 行业资讯  *******************/
    /*******************  第一部分 行业资讯  *******************/

    /**
     * 添加行业资讯
     * @param article            文章对象
     */
    @RequestMapping(value = "/indnews/add", method = RequestMethod.POST)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object addArticle(Article article){
        if(article.getLastedittime() == null) article.setLastedittime(LocalDateTime.now());
        article.setPathname("/" + article.getTitle());
        article.setCategory(getCategoryByTitle(article.getTitle()));
        if(article.getTitle().replaceAll("[\\p{Punct}\\pP]", "").equals("互联网")){
            article.setTitle(article.getTitle().replaceAll("[\\p{Punct}\\pP]", "")+"＋");
            article.setPathname("/" + article.getTitle());
        }
        article.setContent(article.getContent().replace("/bower_components/ueditor/dialogs/attachment/fileTypeImages/", "/images/icon/"));
        if(!article.getContent().startsWith("<head><base target='_blank'></head>")) article.setContent("<head><base target='_blank'></head>" + article.getContent());
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        String error = "";
        String type = "";
        if(!StringUtils.isNullOrEmpty(article.getTitle()) && !StringUtils.isNullOrEmpty(article.getTitle().replace(" ", "")) && !textCheck.doTextCheckThree(article.getTitle().replace(" ", ""))){
            type = "title";
            error = "文章题目中包含非法字符！";
        } else if(!StringUtils.isNullOrEmpty(article.getSource()) && !StringUtils.isNullOrEmpty(article.getSource().replace(" ", "")) && !textCheck.doTextCheckThree(article.getSource().replace(" ", ""))){
            type = "source";
            error = "来源中包含非法字符！";
        } else if(!StringUtils.isNullOrEmpty(article.getKeywords()) && !StringUtils.isNullOrEmpty(article.getKeywords().replace(" ", "")) && !textCheck.doTextCheckThree(article.getKeywords().replace(" ", ""))){
            type = "keywords";
            error = "关键字中包含非法字符！";
        } else if(!StringUtils.isNullOrEmpty(article.getAuthor()) && !StringUtils.isNullOrEmpty(article.getAuthor().replace(" ", "")) && !textCheck.doTextCheckThree(article.getAuthor().replace(" ", ""))){
            type = "author";
            error = "作者中包含非法字符！";
        } else if(!StringUtils.isNullOrEmpty(article.getSummary()) && !StringUtils.isNullOrEmpty(article.getSummary().replace(" ", "")) && !textCheck.doTextCheckThree(article.getSummary().replace(" ", ""))){
            type = "summary";
            error = "摘要中包含非法字符！";
        } else if(!StringUtils.isNullOrEmpty(article.getContent()) && !StringUtils.isNullOrEmpty(article.getContent().replace(" ", "")) && !textCheck.doTextCheckThree(article.getContent().replace(" ", ""))){
            char[] contentText = article.getContent().replace(" ", "").replace("　", "").toCharArray();
            int paragraph = 0;
            for(int i=0; i<contentText.length; i++){
                if(contentText[i] == '<' && contentText[i+1] == 'p'){
                    paragraph++;
                }
                if(!textCheck.doTextCheckThree(String.valueOf(contentText[i]))){
                    break;
                }
            }
            type = "content";
            error = "正文中第 " + paragraph + " 段包含非法字符！";
        } else {
            if (article.getParentid() != 0) {
                Article parent = articleMapper.$getById(article.getParentid());
                if (parent == null) {
                    return new Object() {
                        public boolean success = false;
                        public String error = "该文章父目录已经被删除！添加失败！";
                    };
                }
                article.setPathname(parent.getPathname() + article.getPathname());
            }
            if (!checkArticle(article)) {
                error = "同一目录下已存在标题相同的资讯，请更改后再提交！";
                type = "title";
            } else {
                article.setPath(Hex.encodeHexString(DigestUtils.md5(article.getPathname())));
                article.setLastupdateman("id=" + session.getAdmin().getId());
                return new Object() {
                    public boolean success = articleService.doAddArticleMethod(article);
                };
            }
        }
        map.put("success", success);
        map.put("error", error);
        map.put("type", type);
        return map;
    }


    public String getCategoryByTitle(String title){
        title.replaceAll("\\+","+"); //把英文的+改成中文的加
        if(!org.apache.commons.lang3.StringUtils.isBlank(title)){
            if(title.replaceAll("[\\p{Punct}\\pP]", "").equals("易煤快讯")){
                return "ymkx";
            }else if(title.replaceAll("[\\p{Punct}\\pP]", "").equals("易煤动态")){
                return "ymdt";
            }else if(title.replaceAll("[\\p{Punct}\\pP]", "").equals("第三方服务")){
                return "dsffw";
            }else if (title.replaceAll("[\\p{Punct}\\pP]", "").equals("互联网")){
                return "hlw";
            }
        }
        return null;
    }

    /**
     * 检查文章是否存在
     * @param article                文章对象
     */
    private boolean checkArticle(Article article) {
        Article article1 = articleMapper.getArticleByPathname(article.getPathname());
        if(article1 == null || (article.getId() != null && article.getId().equals(article1.getId())) || !article.getParentid().equals(article1.getParentid())){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据id查询文章
     * @param id                         article id
     */
    @RequestMapping("/queryById")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object queryArticle(@RequestParam(value = "id", required = true)final int id){
        return new Object() {
            public Article article = articleMapper.getById(id);
            public String lastedittime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toString();
        };
    }


    @RequestMapping("/indnews/tostick")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object tostick(@RequestParam(value = "id", required = true)int id,@RequestParam(value = "type", required = true)int type){
        if(type==2){
            return new Object() {
                public boolean success = articleMapper.toSticked(id);
                public String message = "操作成功!";
            };
        }else {
            int j= articleMapper.countStick();
            if (j < 2) {
                return new Object() {
                    public boolean success = articleMapper.toSticked(id);
                    public String message = "操作成功!";
                };
            } else {
                return new Object() {
                    public boolean success = false;
                    public String message = "置顶文章最多只能设置两篇";
                };
            }
        }
    }

    /**
     * 批量删除文章
     * @param ids                      文章id数组
     * @return
     */
    @RequestMapping("/indnews/delete")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object deleteArticle(@RequestParam("ids[]") int[] ids){
        if(ids.length == 0) throw new NotFoundException();
        return articleService.doDeleteArticleMethod(ids);
    }

    /**
     * 行业资讯列表
     * @param parentid                 父id
     * @param content                  搜索框输入的内容
     * @param page                     页数
     * @return
     */
    @RequestMapping(value="/indnews/list", method=RequestMethod.POST)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object articleList(@RequestParam(value = "parentid", required = false, defaultValue = "0")final int parentid,
                              @RequestParam(value = "content", required = false, defaultValue = "")String content,
                              @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        boolean showHotNews = false;
        boolean showHomePage = false;
        if(parentid != 0){
            List<String> pathname1 = Arrays.asList(new String[]{"行业要闻", "国际煤炭", "企业动态", "政策追踪"});
            List<String> pathname2 = Arrays.asList(new String[]{"宏观经济"});
            List<String> pathname2son = Arrays.asList(new String[]{"国内", "国际"});
            List<String> pathname3 = Arrays.asList(new String[]{"相关行业", "独家视点"});
            List<String> pathname3son = Arrays.asList(new String[]{"电力", "钢铁", "建材", "化工", "日评", "周报", "月报", "专题"});
            Article article = articleMapper.$getById(parentid);
            if(pathname1.contains(article.getTitle())){
                showHotNews = true;
                showHomePage = true;
            } else if(article.getParentid() != 0){
                Article parent = articleMapper.$getById(article.getParentid());
                if(pathname2.contains(parent.getTitle()) && pathname2son.contains(article.getTitle())){
                    showHotNews = true;
                    showHomePage = true;
                }
                if(pathname3.contains(parent.getTitle()) && pathname3son.contains(article.getTitle())){
                    showHotNews = true;
                }
            }
        }
        final boolean showHotBox = showHotNews;
        final boolean showHomeBox = showHomePage;
        return new Pager(articleMapper.pageArticleByParentidAndContent(parentid, content, page, 10)){
            public Map<String, Object> parentMap = articleMapper.getAncestorsById(parentid);
            public boolean showHotCheckBox = showHotBox;
            public boolean showHomePageBox = showHomeBox;
        };
    }

    /**
     * 文章改变顺序
     * @param id                     article id
     * @param sequence               article sequence
     * @return
     */
    @RequestMapping("/indnews/changeSequence")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doChangeSquence(@RequestParam(value = "id", required = true)final int id,
                                   @RequestParam(value = "sequence", required = true)int sequence){
        return articleService.changeIndNewsSequence(sequence, id);
    }

    /**
     * 设置文章为热点咨询
     * @param id                      文章id
     */
    @RequestMapping("/setArticleFocus")
    public Object doSetArticleFocus(@RequestParam(value = "id", required = true)int id,
                                    @RequestParam(value = "focus", required = true)int focus){
        Article article = articleMapper.getById(id);
        if(article == null) throw new NotFoundException();
        if(StringUtils.isNullOrEmpty(article.getBannerurl()))
            return new Object(){
                public boolean success = false;
                public String error = "被推荐的资讯必须有宣传图片，请先进入详情页面添加宣传图片！";
            };
        int focusTemp = 0;
        if (article.getFocus() != 0 && !StringUtils.isNullOrEmpty(article.getFocusname()) && article.getFocusname().contains(dataBookMapper.getDataBookNameByTypeSequence("articlefocus", focus))) focusTemp = article.getFocus() - focus;
        else focusTemp = article.getFocus() + focus;
        article.setFocus(focusTemp);
        return new Object(){
            public boolean success = articleService.doSetArticleFocusMethod(id, article.getFocus(), dataBookMapper.getDataBookNameByTypeSequence("articlefocus", article.getFocus()));
        };
    }



    /*************************  第二部分，热点资讯  ******************************/
    /*************************  第二部分，热点资讯  ******************************/
    /*************************  第二部分，热点资讯  ******************************/


    /**
     * 上传文章宣传图片
     * @param file                       图片文件
     */
    @RequestMapping("/hotnews/addBrandPic")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object addBrandPic(@RequestParam("file") MultipartFile file) throws IOException, FileStore.UnsupportedContentType {
        return auth.uploadPicMethod(EnumFileType.File_Article.toString(), EnumFileType.IMG.toString(), file, 560, 340);
    }

    /**
     * 确认，提交  文章宣传图片
     * @param id                        文章id
     * @param bannerurl                 热点资讯图片url
     */
    @RequestMapping("/hotnews/confirmAddBrandPic")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean confirmAddBrandPic(@RequestParam(value = "id", required = true)int id,
                                      @RequestParam(value = "bannerurl", required = true)String bannerurl){
        if(articleMapper.getById(id) == null) throw new NotFoundException();
        return articleService.doConfirmAddBrandPicMethod(id, bannerurl);
    }

    /**获取热点资讯列表
     * @param page                      页数
     */
    @RequestMapping("/hotnews/list")
    public Object doGetHotNewsList(@RequestParam(value = "page", required = false, defaultValue = "1")int page){
        List<Integer> ids = articleMapper.getIdListByTitle("独家视点", "相关行业");
        if(ids == null || ids.size() == 0){
            return articleMapper.pageHotNews(0, 0, page, 10);
        } else{
            if(ids.size() == 1) ids.add(1,0);
            return articleMapper.pageHotNews(ids.get(0), ids.get(1), page, 10);
        }
    }

    /**
     * 改变热点资讯的level
     * @param id                        热点资讯 的id
     * @param level                     热点资讯 的level
     */
    @RequestMapping("/hotnews/changeLevel")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doChangeHotnewsLevel(@RequestParam(value = "id", required = true)int id,
                                       @RequestParam(value = "level", required = true)int level){
        HotNews hotNews = articleMapper.getHotNewsById(id);
        if(hotNews == null) throw new NotFoundException();
        Article article = articleMapper.getById(hotNews.getAid());
        if(level == 1 && StringUtils.isNullOrEmpty(article.getBannerurl())){
            return new Object(){
                public boolean success = false;
                public String error = "类别为 1 的图片必须有宣传图片，请先进入详细页面添加宣传图片！";
            };
        }
        return new Object() {
            public boolean success = articleService.doChangeHotNewsLevelMethod(id, level);
        };
    }

    /**
     * 改变热点资讯的sequence
     * @param id                        热点资讯 id
     * @param sequence                  热点资讯 sequence
     */
    @RequestMapping("/hotnews/changeSequence")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doChangeHotNewsSequence(@RequestParam(value = "id", required = true)int id,
                                           @RequestParam(value = "sequence", required = true)int sequence){
        if(articleMapper.getHotNewsById(id) == null) throw new NotFoundException();
        return articleService.doChangeHotNewsSequenceMethod(id, sequence);
    }


    /**
     * 删除一篇热点资讯
     * @param id                        热点资讯id
     */
    @RequestMapping("/hotnews/delete")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeleteHotNewsById(@RequestParam(value = "id", required = true)int id){
        HotNews hotNews = articleMapper.getHotNewsById(id);
        if(hotNews == null) throw new NotFoundException();
        return articleService.doDeleteHotNewMethod(hotNews);
    }

    /**
     * 删除多篇热点资讯
     * @param ids                       多篇热点资讯id数组
     */
    @RequestMapping("/hotnews/deleteSeveral")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean deleteSeveralHotNews(@RequestParam("ids[]") int[] ids){
        if(ids.length == 0) throw new NotFoundException();
        return articleService.doDeleteSeveralHotNewsMethod(ids);
    }

    /**
     * 设置热点文章是否在前台显示
     * @param id                         hotnews 的 id
     */
    @RequestMapping("/hotnews/setCancelHotNewsShow")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doSetCancelHotNewsShow(@RequestParam(value = "id", required = true)int id){
        if(articleMapper.getHotNewsById(id) == null) throw new NotFoundException();
        return articleService.doSetCancelHotNewsShowMethod(id);
    }


    /******************************  第三部分，宣传视频  ********************************/
    /******************************  第三部分，宣传视频  ********************************/
    /******************************  第三部分，宣传视频  ********************************/

    /**
     * 获取视频列表
     * @param content                 搜索框里输入的信息
     * @param page                    page
     */
    @RequestMapping("/videos/list")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object getVideoList(@RequestParam(value = "searchcontent", required = false, defaultValue = "")String content,
                               @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return new Object(){
            public Pager<Video> VideoList = articleMapper.pageArticleVideoByContent(Where.$like$(content), page, 10);
            public String searchcontent = content;
        };
    }

    /**
     * 添加 修改 视频
     * @param video                    video对象
     */
    @RequestMapping("/videos/add")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doAddArticleVideo(Video video){
        video.setLasteditman("工号：" + session.getAdmin().getJobnum() + ", 姓名：" + session.getAdmin().getName());
        if(video.getId() == 0) {
            return articleService.doAddArticleVideoMethod(video);
        } else{
            if(articleMapper.getVideoById(video.getId()) == null) throw new NotFoundException();
            return articleService.doUpdateArticleVideoMethod(video);
        }
    }

    /**
     * 添加 视频 海报图片
     * @param file                    图片对象
     */
    @RequestMapping("/video/addBrandPic")
    public Object doAddVideoBannerPic(@RequestParam("file") MultipartFile file) throws IOException, FileStore.UnsupportedContentType {
        return auth.uploadPicMethod(EnumFileType.File_Article.toString(), EnumFileType.IMG.toString(), file, null, null);
    }

    /**
     * 视频详细页面
     * @param id                      视频id
     * @return
     */
    @RequestMapping("/videos/detail")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object doShowVideoDetail(@RequestParam(value = "id", required = true)int id){
        Map<String, Object> map = new HashMap<>();
        Video video =  articleMapper.getVideoById(id);
        map.put("video", video);
        if(!StringUtils.isNullOrEmpty(video.getLength())) {
            map.put("hour",  Integer.valueOf(video.getLength().substring(0, 1)));
            map.put("minute", Integer.valueOf(video.getLength().substring(3, 4)));
            map.put("second", Integer.valueOf(video.getLength().substring(7, 8)));
        }
        return map;
    }

    /**
     * 设置，取消 视频在前台显示
     * @param id              Video id
     * @return
     */
    @RequestMapping("/videos/setcancelshow")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doSetShowCancelVideo(@RequestParam(value = "id", required = true)int id){
        Video video = articleMapper.getVideoById(id);
        if(video == null) throw new NotFoundException();
        return articleService.doSetShowCancelVideoMethod(id);
    }

    /**
     * 改变视频顺序
     * @param id              Video id
     * @param sequence        Video sequence
     * @return
     */
    @RequestMapping("/videos/changeSequence")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doChangeVideoSequence(@RequestParam(value = "id", required = true)int id,
                                         @RequestParam(value = "sequence", required = true)int sequence){
        Video video = articleMapper.getVideoById(id);
        if(video == null) throw new NotFoundException();
        return articleService.doChangeVideoSequenceMethod(id, sequence);
    }


    /******************************  第四部分，广告图片  ********************************/
    /******************************  第四部分，广告图片  ********************************/
    /******************************  第四部分，广告图片  ********************************/


    /**
     * 删除视频
     * @param id              Video id
     * @return
     */
    @RequestMapping("/videos/delete")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeleteArticleVideo(@RequestParam(value = "id", required = true)int id){
        Video video = articleMapper.getVideoById(id);
        if(video == null) throw new NotFoundException();
        return articleService.doDeleteArticleVideoMethod(id);
    }

    /**
     * 添加,修改广告图片
     */
    @RequestMapping(value="/adverpic/add", method=RequestMethod.POST,produces = "text/plain;charset=UTF-8")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object uploadAdvPic(@RequestParam("file") MultipartFile file,
                               @RequestParam(value = "comment") String comment,
                               @RequestParam(value = "linkurl") String linkurl,String picId) throws Exception{
        Map map=new HashMap<String,Object>();
        String fileType = fileStore.getFileType(file);
        List<String> picTypeList = Arrays.asList(new String[]{"jpg", "bmp", "png", "jpeg"});
        if(picTypeList.contains(fileType)) {
            if (file.getSize() / 1000 / 1000 <= 10) {
                String filePath1 = fileService.uploadAdverPic(file);
                Adverpic adverpic=  new Adverpic(filePath1,LocalDateTime.now(),linkurl,comment);
                adverpic.setLasteditman("工号：" + session.getAdmin().getJobnum() + ", 姓名：" + session.getAdmin().getName());
                adverpic.setLastedittime(LocalDateTime.now());
                if(picId!=null&&!picId.equals("")){
                    //修改广告图片  updateAderverPic
                    adverpic.setId(Integer.parseInt(picId));
                    int j= articleMapper.updateAderverPic(adverpic);
                    if(j!=0) {
                        map.put("filePath",filePath1);
                        map.put("success",true);
                        return map;
                    }else{
                        throw new BusinessException("修改广告图片失败!");
                    }

                }else{
                    //添加广告图片
                    int j= articleMapper.addAderverPic(adverpic);
                    if(j!=0) {
                        map.put("filePath",filePath1);
                        map.put("success",true);
                        return map;
                    }else{
                        throw new BusinessException("上传广告图片失败!");
                    }
                }

            }
            throw new BusinessException("上传的图片不能超过10M！");
        }
        throw new BusinessException("请选择 jpg, bmp, png, jpeg 格式的图片上传！");
    }


    /**
     * 获取广告图片列表
     * @param page                      page
     * @return
     */
    @RequestMapping("/adverpic/list")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public Object getAdverList(@RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return new Object(){
            public Pager<Adverpic> adverpicList = articleMapper.getArticleAdverpic(page, 10);
        };
    }


    /**
     * 设置，取消 视频在前台显示
     * @param id              adverpic id
     * @return
     */
    @RequestMapping("/adverpic/setcancelshow")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public int doSetShowCancelAdverpic(@RequestParam(value = "id", required = true)int id){
       return articleMapper.setShowCancelAdverMethod(id);
    }

    /**
     * 改变广告图片顺序
     * @param id              adverpic id
     * @param sequence        adverpic sequence
     * @return
     */
    @RequestMapping("/adverpic/changeSequence")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doChangeAdverSequence(@RequestParam(value = "id", required = true)int id,
                                         @RequestParam(value = "sequence", required = true)int sequence){
       return articleMapper.doChangeAdverpicSequenceMethod(id, sequence);
    }


    /**
     * 删除广告图片
     * @param id              adverpic id
     * @return
     */
    @RequestMapping("/adverpic/delete")
    @Authority(role = AuthenticationRole.NetworkEditor)
    @Authority(role = AuthenticationRole.Admin)
    public boolean doDeleteArticleAdver(@RequestParam(value = "id", required = true)int id){
      return  articleMapper.doDeleteArticleAdverMethod(id);

    }



}