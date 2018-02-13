package kitt.site.service.mobile;

import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by zhangbolun on 15/6/25.
 */
@Service
@Transactional(readOnly = true)
public class ManualsellService {
    @Autowired
    private ManualSellMapper manualSellMapper;
    /**
     * 个人中心--人工找货，人工销售 列表查询
     * @param param
     * @param manualsellType
     * @param dateRange
     * @return
     */
    public PageQueryParam show(PageQueryParam param, boolean manualsellType, String dateRange,User user) {
        LocalDate searchDate = getDateByCode(dateRange);
        List<ManualSell> manualSellLists = manualSellMapper.list(user.getId(), manualsellType, searchDate, param.getPagesize(), param.getIndexNum(), null);
        int totalCount = manualSellMapper.count(user.getId(), manualsellType, searchDate, null);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(manualSellLists);
        return param;
    }

    /**
     * 个人中心--删除人工信息
     * @param manualsellId
     * @param user
     */
    @Transactional(readOnly = false)
    public void DeleteManualSell(String manualsellId,User user){
        ManualSell manualSell= manualSellMapper.loadByUserIdandManualId(user.getId(), manualsellId);
        if(manualSell==null)
            throw new BusinessException("数据未找到，删除失败");
        manualSellMapper.deleteManualSellById(manualsellId,user.getId());
    }

    /******************************private  function************************************/
    private static LocalDate getDateByCode(String rangeType) {
        LocalDate nowDate = LocalDate.now();
        LocalDate newDate = null;
        switch (rangeType) {
            case "thirty_day":
                newDate = nowDate.minusDays(30);
                break;
            case "three_month":
                newDate = nowDate.minusMonths(2);
                break;
            case "half_year":
                newDate = nowDate.minusMonths(5);
                break;
        }
        return newDate;
    }

    private static boolean checkPhonenum(String securephone) {
        return Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$").matcher(securephone).matches();
    }

}
