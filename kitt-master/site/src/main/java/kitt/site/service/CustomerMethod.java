package kitt.site.service;

import kitt.core.domain.FriendlyLink;
import kitt.core.persistence.FriendlyLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by liuxinjie on 16/1/16.
 */
@Service
public class CustomerMethod {
    @Autowired
    private FriendlyLinkMapper friendlyLinkMapper;

    /**---------------------------------------第一部分 友情链接 start --------------------------------**/
    /**---------------------------------------第一部分 友情链接 start --------------------------------**/

    public List<FriendlyLink> getHomePageFriendlyLinkList(){
        return friendlyLinkMapper.getFriendlyListBySequence(30, 0);
    }



    /**---------------------------------------第一部分 友情链接 end ----------------------------------**/
    /**---------------------------------------第一部分 友情链接 end ----------------------------------**/
}
