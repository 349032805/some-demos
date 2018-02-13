package kitt.core.bl;

import kitt.core.domain.EnumRemindInfo;
import kitt.core.domain.Finance;
import kitt.core.domain.FriendlyLink;
import kitt.core.domain.MapObject;
import kitt.core.persistence.FinanceMapper;
import kitt.core.persistence.FriendlyLinkMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuxinjie on 15/12/16.
 */
@Service
public class CustomerService {
    @Autowired
    private FriendlyLinkMapper friendlyLinkMapper;
    @Autowired
    private FinanceMapper financeMapper;

    /**--------------------------------------------- 第一部分 友情链接 start -------------------------------------------**/
    /**--------------------------------------------- 第一部分 友情链接 start -------------------------------------------**/

    /**
     * 添加友情链接
     * @param friendlyLink     友情链接对象
     */
    public boolean doAddFriendlyLinkMethod(FriendlyLink friendlyLink) {
        return friendlyLinkMapper.addFriendlyLinkMethod(friendlyLink) == 1;
    }

    /**
     * 修改友情链接
     * @param friendlyLink     友情链接对象
     */
    public boolean doUpdateFriendlyLinkMethod(FriendlyLink friendlyLink) {
        return friendlyLinkMapper.doUpdateFriendlyLinkMethod(friendlyLink) == 1;

    }

    /**
     * 更改友情链接顺序
     * @param sequence         顺序
     * @param id               id
     */
    public boolean doChangeFriendlyLinkSequenceMethod(int sequence, int id) {
        return friendlyLinkMapper.doChangeFriendlyLinkSequenceMethod(sequence, id);
    }

    /**
     * 设置,取消友情链接在前台显示
     * @param id               id
     */
    public boolean doSetCanceleFriendlyLinkShowMethod(int id) {
        return friendlyLinkMapper.doSetCancelFriendlyLinkShowMethod(id) == 1;
    }

    public boolean doDeleteFriendlyLinkMethod(int id) {
        return friendlyLinkMapper.doDeleteFriendlyLinkMethod(id) == 1;
    }


    /**--------------------------------------------- 第一部分 友情链接 end ---------------------------------------------**/
    /**--------------------------------------------- 第一部分 友情链接 end ---------------------------------------------**/


    /**--------------------------------------------- 第二部分 金融 start ----------------------------------------------**/
    /**--------------------------------------------- 第二部分 金融 start ----------------------------------------------**/

    @Transactional
    public MapObject doAddFinance(MapObject map, Finance finance) {
        if(financeMapper.addFinance(finance) != 1) {
            map.setSuccess(false);
            map.setError(EnumRemindInfo.Site_System_Error.value());
        }
        return map;
    }

    /**--------------------------------------------- 第二部分 金融 end ------------------------------------------------**/
    /**--------------------------------------------- 第二部分 金融 end ------------------------------------------------**/


}
