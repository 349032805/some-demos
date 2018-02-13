package kitt.core.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuxinjie on 15/9/18.
 */
public class ArticleMenu implements Serializable {
    private Article article;                                  //article  对象
    private List<Article> articleList;                        //article  的子文章目录

    public ArticleMenu() {
    }

    public ArticleMenu(Article article, List<Article> articleList) {
        this.article = article;
        this.articleList = articleList;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }
}
