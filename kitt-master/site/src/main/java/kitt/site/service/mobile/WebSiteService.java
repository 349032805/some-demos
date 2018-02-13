package kitt.site.service.mobile;

import kitt.core.domain.AboutUs;
import kitt.core.domain.Article;
import kitt.core.persistence.AboutUsMapper;
import kitt.core.persistence.ArticleMapper;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by xiangyang on 15-6-19.
 */
@Service
@Transactional(readOnly = true)
public class WebSiteService {
    @Autowired
    protected AboutUsMapper aboutUsMapper;
    @Autowired
    private ArticleMapper articleMapper;
    //网站公告

    /**
     *
     * @param param
     * @param type  notices 网站公告
     * @return
     */
    public PageQueryParam websiteNews(PageQueryParam param,String pathName) {
        Article article = articleMapper.getArticleByPathname(pathName);
        if(article!=null) {
          int totalCount = articleMapper.getArticleCountByParentid(article.getId());
          List<Article> notice = articleMapper.getArticleListByParentid(article.getId(), param.getPagesize(), param.getIndexNum());
          int totalPage = totalCount / param.getPagesize();
          totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
          param.setTotalCount(totalCount);
          param.setTotalPage(totalPage);
          param.setList(notice);
        }
        return param;
    }

    //文章详细
    @Transactional(readOnly = false)
    public Article articleDetail(int id) {
      Article article = articleMapper.getById(id);
        if (article == null) {
            throw new NotFoundException();
        }
      articleMapper.doAddViewTimesById(id);
        return article;
    }


}
