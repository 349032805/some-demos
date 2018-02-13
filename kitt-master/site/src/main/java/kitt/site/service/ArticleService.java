package kitt.site.service;

import kitt.core.persistence.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuxinjie on 15/9/28.
 */
@Service
public class ArticleService {
    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 视频浏览次数加1
     * @param id             Video id
     */
    @Transactional
    public void doAddVideoViewTimes(int id) {
        articleMapper.doAddVideoViewTimesMethod(id);
    }
}
