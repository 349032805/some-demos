package kitt.site.service;

import kitt.core.domain.LogisticsStatus;
import kitt.core.domain.Logisticsintention;
import kitt.core.domain.Shipintention;
import kitt.core.domain.User;
import kitt.core.persistence.CompanyMapper;
import kitt.core.persistence.LogisticsMapper;
import kitt.core.persistence.ShipMapper;
import kitt.core.persistence.UserMapper;
import kitt.core.util.EmojiFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by lich on 16/1/11.
 */
@Service
public class ShipService {
    @Autowired
    private Session session;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private ShipMapper shipMapper;
    @Autowired
    private LogisticsMapper logisticsMapper;
    @Autowired
    protected UserMapper userMapper;

    //增加船运意向单
    @Transactional
    public boolean saveShipIntention(Shipintention shipintention){
        shipintention.setLoadport(EmojiFilter.filterEmoji(shipintention.getLoadport()));
        shipintention.setUnloadport(EmojiFilter.filterEmoji(shipintention.getUnloadport()));
        shipintention.setLoaddock(EmojiFilter.filterEmoji(shipintention.getLoaddock()));
        shipintention.setUnloaddock(EmojiFilter.filterEmoji(shipintention.getUnloaddock()));
        shipintention.setRemark(EmojiFilter.filterEmoji(shipintention.getRemark()));
        shipMapper.addShipIntenttion(shipintention);
        Logisticsintention logisticsintention=new Logisticsintention();
        logisticsintention.setVenderid(2);     //待定
        logisticsintention.setVendername("超级船东");  //待定
        if (session != null && session.getUser() != null) {
            logisticsintention.setUserid(session.getUser().getId());
        } else {
            User user = userMapper.getUserByPhone(shipintention.getMobile());
            if (user != null) logisticsintention.setUserid(user.getId());
        }
        logisticsintention.setCreatetime(LocalDateTime.now());
        logisticsintention.setStatus(LogisticsStatus.TREATED_NOT.toString());
        logisticsintention.setType(2);  //type:1 物流 2;船运
        logisticsintention.setGoodsType(shipintention.getGoodsType());
        logisticsintention.setGoodsWeight(shipintention.getGoodsWeight());
        logisticsintention.setContacts(shipintention.getContacts());
        logisticsintention.setMobile(shipintention.getMobile());
        logisticsintention.setCompanyname(shipintention.getCompanyname());
        logisticsintention.setRemark(shipintention.getRemark());
        logisticsintention.setShipid(shipintention.getId());
        logisticsMapper.addLogisticsintentionS(logisticsintention);
        return true;
    }
}
