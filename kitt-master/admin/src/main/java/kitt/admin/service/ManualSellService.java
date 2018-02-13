package kitt.admin.service;

import kitt.admin.basic.exception.BusinessException;
import kitt.core.domain.ManualSell;
import kitt.core.persistence.ManualSellMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yimei on 15/7/21.
 */
@Service
public class ManualSellService {
    @Autowired
    private ManualSellMapper manualSellMapper;
    @Autowired
    private Session session;

    @Transactional
    public boolean doSolveManualMethod(ManualSell manualSell) {
        if(manualSellMapper.solveManualSellById(manualSell.getId())==1){
            if(manualSellMapper.updateManualAddSolvedInfo(session.getAdmin().getId(), session.getAdmin().getUsername(), manualSell.getSolvedremarks(), manualSell.getId()) == 1){
                return true;
            }
        }
        throw new BusinessException("此委托单已经被处理，请刷新页面！");
    }
}
