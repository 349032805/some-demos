package kitt.admin.service;

import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.core.domain.Article;
import kitt.core.domain.HotNews;
import kitt.core.domain.Video;
import kitt.core.persistence.ArticleMapper;
import kitt.core.service.FileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yimei on 15/7/28.
 */
@Service
public class ArticleService {
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private Session session;
    @Autowired
    private FileService fileService;

    /**
     * 添加文章方法
     * @param article    文章对象
     * @return           true or false
     */
    @Transactional
    public boolean doAddArticleMethod(Article article){
        if(articleMapper.saveArticle(article) == 1){
            return true;
        }
        throw new BusinessException("添加文章失败！请刷新页面重试！");
    }

    /**
     * 批量删除文章
     * @param ids  文章id集合
     */
    @Transactional
    public Object doDeleteArticleMethod(int[] ids) {
        Map<String, Object> map = new HashMap<>();
        boolean success = true;
        for(int id : ids){
            Article article = articleMapper.getById(id);
            if(article == null) throw new NotFoundException();
            if(article.isHaschild()){
                success = false;
                map.put("error", "你要删除的文章中，有些包含有子文章， 请先删除其子文章，再删除！");
                break;
            }
            if (articleMapper.deleteById(id, "id=" + session.getAdmin().getId() + ", jobnum=" + session.getAdmin().getJobnum()) != 1) {
                throw new BusinessException("文章删除失败！请刷新页面后重试！");
            }
        }
        map.put("success", success);
        return map;
    }


    /**
     * 改变行业资讯顺序
     * @param sequence          行业资讯sequence
     * @param id                行业资讯id
     */
    @Transactional
    public boolean changeIndNewsSequence(int sequence, int id) {
        if(articleMapper.changeSequence(sequence, id)){
            return true;
        }
        throw new BusinessException("改变资讯顺序出错,请联系技术人员！");
    }

    /**
     * 是指资讯为热点文章
     * @param id
     * @param focus
     * @return
     */
    @Transactional
    public boolean doSetArticleFocusMethod(int id, int focus, String focusname) {
        int row1 = articleMapper.setArticleFocus(id, focus, focusname);
        int row2 = articleMapper.addOrCancelHotNews(new HotNews(id), focus);
        if(row1 == 1 && row2 ==1){
            return true;
        }
        throw new BusinessException("设置热点文章出错，请联系技术人员！");
    }

    /**
     * 改变hotnews level 方法
     * @param id                 hotnews id
     * @param level              hotnews level
     * @return
     */
    @Transactional
    public boolean doChangeHotNewsLevelMethod(int id, int level) {
        if(articleMapper.doChangeHotNewsLevelMethod(id, level)){
            return true;
        }
        throw new BusinessException("改变热点资讯等级出错，请联系技术人员！");
    }

    /**
     * 改变hotnews sequence 方法
     * @param id                 hotnews id
     * @param sequence           hotnews sequence
     * @return
     */
    @Transactional
    public boolean doChangeHotNewsSequenceMethod(int id, int sequence) {
        if(articleMapper.doChangeHotNewsSequenceMethod(id, sequence)){
            return true;
        }
        throw new BusinessException("改变热点资讯顺序出错，请联系技术人员！");
    }


    /**
     * @param id
     * @param bannerurl
     * @return
     */
    @Transactional
    public boolean doConfirmAddBrandPicMethod(int id, String bannerurl) {
        return articleMapper.setArticleBannerUrlById(id, bannerurl);
    }


    /**
     * 删除一篇热点资讯的方法
     * @param hotNews            hotnews
     * @return
     */
    @Transactional
    public boolean doDeleteOneHotNewsMethod(HotNews hotNews){
        int row1 = articleMapper.setArticleFocus(hotNews.getAid(), 0, null);
        int row2 = articleMapper.doDeleteHotNewMethod(hotNews.getId());
        return row1 == 1 && row2 == 1 ? true : false;
    }

    /**
     * 删除一篇热点资讯
     * @param hotNews            hotnews
     * @return
     */
    @Transactional
    public boolean doDeleteHotNewMethod(HotNews hotNews) {
        if(doDeleteOneHotNewsMethod(hotNews)){
            return true;
        }
        throw new BusinessException("删除热点资讯出错！请联系技术人员！");
    }

    /**
     * 删除多篇热点资讯
     * @param ids               热点资讯 id 集合
     * @return
     */
    @Transactional
    public boolean doDeleteSeveralHotNewsMethod(int[] ids) {
        for(int id : ids){
            HotNews hotNews = articleMapper.getHotNewsById(id);
            if(!doDeleteOneHotNewsMethod(hotNews)){
                throw new BusinessException("删除热点资讯出错，请联系技术人员！");
            }
        }
        return true;
    }

    /**
     * 添加视频到数据库
     * @param video            视频对象
     */
    @Transactional
    public boolean doAddArticleVideoMethod(Video video) {
        if(articleMapper.doAddArticleVideoMethod(video) == 1){
            return true;
        }
        throw new BusinessException("添加宣传视频出错，请联系技术人员！");
    }

    /**
     * 修改宣传视频
     * @param video            视频对象
     * @return
     */
    @Transactional
    public boolean doUpdateArticleVideoMethod(Video video){
        if(articleMapper.doUpdateArticleVideoMethod(video)){
            return true;
        }
        throw new BusinessException("修改宣传视频出错，请联系技术人员！");
    }

    /**
     * 设置，取消 视频 在前台显示
     * @param id
     * @return
     */
    @Transactional
    public boolean doSetShowCancelVideoMethod(int id) {
        if(articleMapper.setShowCancelVideoMethod(id) == 1){
            return true;
        }
        throw new BusinessException("设置视频显示出错，请联系技术人员！");
    }

    /**
     * 改变视频顺序
     * @param id
     * @return
     */
    @Transactional
    public boolean doChangeVideoSequenceMethod(int id, int sequence) {
        if(articleMapper.doChangeVideoSequenceMethod(id, sequence)){
            return true;
        }
        throw new BusinessException("改变视频顺序出错，请联系技术人员！");
    }

    /**
     * 给视频添加备注
     * @param id               Video id
     * @param remarks          Video remarks
     * @return
     */
    @Transactional
    public boolean doAddArticleRemarksMethod(int id, String remarks) {
        if(articleMapper.doAddArticleRemarksMethod(id, remarks) == 1){
            return true;
        }
        throw new BusinessException("系统出错，请联系技术人员！");
    }

    /**
     * 设置热点文章是否在前台显示
     * @param id               HotNews id
     * @return
     */
    public boolean doSetCancelHotNewsShowMethod(int id) {
        if(articleMapper.doSetCancelHotNewsShowMethod(id) == 1){
            return true;
        }
        throw new BusinessException("设置在前台显示出错，请联系技术人员！");
    }

    /**
     * 删除视频
     * @param id              Video id
     * @return
     */
    public boolean doDeleteArticleVideoMethod(int id) {
        if(articleMapper.doDeleteArticleVideoMethod(id) == 1){
            return true;
        }
        throw new BusinessException("删除视频出错，请联系技术人员！");
    }

    /**
     * 上传视频海报图片
     * @param file
     * @return
     */
    public String doAddVideoBannerPicMethod(MultipartFile file) throws FileStore.UnsupportedContentType, IOException {
        return fileService.uploadVideoBannerPicture(file);
    }
}
