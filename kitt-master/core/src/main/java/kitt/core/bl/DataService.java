package kitt.core.bl;

import kitt.core.domain.PricePort;
import kitt.core.domain.PricePortValue;
import kitt.core.persistence.PricePortMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Created by liuxinjie on 16/1/21.
 */
@Service
public class DataService {
    @Autowired
    private PricePortMapper pricePortMapper;

    /**
     * 添加港口价格
     * @param pricePort               PricePort 对象
     */
    @Transactional
    public boolean addPricePort(PricePort pricePort) {
        return pricePortMapper.addPricePort(pricePort) == 1;
    }

    /**
     * 添加港口价格数值
     * @param pricePortValue          PricePortValue 对象
     */
    public boolean addPricePortValue(PricePortValue pricePortValue) {
        return pricePortMapper.addPricePortValue(pricePortValue) == 1;
    }

    /**
     * 添加港口价格数值
     * @param pricePortValueList      PricePortValue 对象
     */
    @Transactional
    public void addPricePortValueList(List<PricePortValue> pricePortValueList) {
        pricePortMapper.addPricePortValueList(pricePortValueList);
    }

    /**
     * 设置,取消 港口价格在前台显示
     * @param id                      PricePort id
     */
    public boolean doSetCancelShowPricePort(int id) {
        return pricePortMapper.doSetCancelShowPricePortMethod(id) == 1;
    }

    /**
     * 改变港口价格在前台显示顺序
     * @param id                       PricePort id
     * @param sequence                 PricePort sequence
     */
    public boolean doChangePricePortSequence(int id, int sequence) {
        return pricePortMapper.doChangePricePortSequenceMethod(id, sequence);
    }

    /**
     * 删除港口价格
     * @param id                       PricePort id
     */
    public boolean doDeletePricePort(int id) {
        return pricePortMapper.doDeletePricePortById(id) == 1;
    }

    /**
     * 删除港口价格数值
     * @param id                       PricePortValue id
     */
    public boolean doDeletePricePortValue(int id) {
        return pricePortMapper.doDeletePricePortValueById(id) == 1;
    }


}
