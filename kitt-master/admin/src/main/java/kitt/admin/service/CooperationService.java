package kitt.admin.service;

import kitt.core.domain.Cooperation;
import kitt.core.persistence.CooperationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuxinjie on 15/10/9.
 */
@Service
public class CooperationService {
    @Autowired
    private CooperationMapper cooperationMapper;

    @Transactional
    public boolean doUpdateCooperationMethod(Cooperation cooperation){
        return cooperationMapper.doUpdateCooperationMethod(cooperation);
    }

}
