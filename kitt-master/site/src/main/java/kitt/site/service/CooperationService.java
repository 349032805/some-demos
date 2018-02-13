package kitt.site.service;

import kitt.core.domain.Cooperation;
import kitt.core.persistence.CooperationMapper;
import kitt.site.basic.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuxinjie on 15/9/28.
 */
@Service
public class CooperationService {
    @Autowired
    private CooperationMapper cooperationMapper;
    @Autowired
    private Session session;

    @Transactional
    public boolean doAddCooperationMethod(Cooperation cooperation) {
        if (session.getUser() != null) {
          cooperation.setUserid(session.getUser().getId());
          cooperation.setUserphone(session.getUser().getSecurephone());
          cooperation.setLogin(true);
        }
        if(cooperationMapper.doAddCooperationMethod(cooperation) == 1){
            return true;
        }
        throw new BusinessException("系统出错，请刷新页面重试！");
    }
}
