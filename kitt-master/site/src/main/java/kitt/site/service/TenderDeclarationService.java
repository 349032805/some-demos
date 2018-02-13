package kitt.site.service;

import com.google.common.collect.Lists;
import kitt.core.domain.TenderDeclaration;
import kitt.core.domain.TenderStatus;
import kitt.core.persistence.TenderdeclarMapper;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xiangyang on 15/11/15.
 */
@Service
@Transactional(readOnly = true)
public class TenderDeclarationService {

    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private Session session;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional(readOnly = false)
    public void updateDeclarationGiveUp(int id, int userId) {
        TenderDeclaration declare = tenderdeclarMapper.findTenderDeclarByIdAndUserId(id, userId);
        if (declare == null) {
            throw new NotFoundException();
        }
        if (!TenderStatus.TENDER_EDIT.name().equals("TENDER_EDIT")) {
            logger.warn("{}公告不是可编辑状态，状态为:", declare.getId(), declare.getStatus());
            throw new BusinessException("当前公告不是可编辑状态，不能正常放弃!");
        }
        tenderdeclarMapper.updateStatusById(id, TenderStatus.TENDER_GIVEUP.name(), session.getUser().getId());
    }

    public List<Integer> findYear() {
        List<Integer> yearInteval = tenderdeclarMapper.findDeclarationYear();
        if (yearInteval.size() == 0) {
            yearInteval.add(LocalDate.now().getYear());
            return yearInteval;
        }
        if (yearInteval.size() == 1) {
            return yearInteval;
        }
        if (yearInteval.size() > 1) {
            List<Integer> temp = Lists.newArrayList();
            Iterator<Integer> it = yearInteval.iterator();
            Integer numA = it.next();
            if (it.hasNext()) {
                int difference = numA - it.next() - 1;
                for (int i = difference; i > 0; i--) {
                    temp.add(numA - i);
                }
            }
            yearInteval.addAll(temp);
            Collections.sort(yearInteval);
        }
        return yearInteval;
    }
}