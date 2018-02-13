package kitt.admin.service;

import kitt.admin.basic.exception.BusinessException;
import kitt.core.persistence.FinanceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by yimei on 15/7/23.
 */
@Service
public class FinanceService {
    @Autowired
    private FinanceMapper financeMapper;
    @Autowired
    private Session session;

    @Transactional
    public boolean doSolveFinanceOrderMethod(int id, String solvedremarks){
        if(financeMapper.doSolveFinanceOrderById(id) == 1){
            if(financeMapper.doAddFinanceSolvedInfo(solvedremarks, session.getAdmin().getId(), session.getAdmin().getUsername(), session.getAdmin().getName(), id) == 1){
                return true;
            }
        }
        throw new BusinessException("处理金融申请单失败，请刷新页面后重试！");
    }

    @Transactional
    public boolean doDeleteFinanceOrderMethod(int id) {
        if(financeMapper.doDeleteFinanceOrderById(id) != 1){
            throw new BusinessException("删除金融申请单失败，请刷新页面后重试！");
        }
        return true;
    }
}

