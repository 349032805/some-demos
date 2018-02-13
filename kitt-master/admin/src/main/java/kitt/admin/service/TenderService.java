package kitt.admin.service;

import kitt.admin.basic.exception.BusinessException;
import kitt.core.domain.*;
import kitt.core.persistence.CompanyMapper;
import kitt.core.persistence.IndexBannerMapper;
import kitt.core.persistence.TenderdeclarMapper;
import kitt.core.persistence.UserMapper;
import kitt.ext.mybatis.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuxinjie on 15/11/15.
 */
@Service
public class TenderService {
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private IndexBannerMapper indexBannerMapper;
    @Autowired
    private Session session;

    /**
     * 招标列表
     * @param content          搜索框里的内容
     * @param startDate        搜索开始时间
     * @param endDate          搜索结束时间
     * @param status           状态
     * @param page             页码
     */
    public Object getTenderList(String content, LocalDate startDate, LocalDate endDate, String status, int page){
        LocalDate endDateTemp = endDate == null ? null : endDate.plusDays(1);
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("startDate", startDate == null ? "" : String.valueOf(startDate));
        map.put("endDate", endDate == null ? "" : String.valueOf(endDate));
        map.put("tenderList", tenderdeclarMapper.pageAllTenderDeclaration(status, Where.$like$(content), 0, startDate == null ? "" : String.valueOf(startDate), endDateTemp == null ? "" : String.valueOf(endDateTemp), page, 10));
        return map;
    }

    /**
     * 审核TenderDeclaration
     * @param tenderDeclaration    TenderDeclaration 对象
     * @param user                 招标公户user
     * @param trader               交易员对象
     */
    @Transactional
    public boolean doVerifyTenderDeclarationMethod(TenderDeclaration tenderDeclaration, User user, Admin trader) {
        if(user.getTraderid() == null && trader != null){
            if(!userMapper.doAddUpdateUserTraderMethod(user.getId(), trader.getId())) {
                throw new BusinessException("服务器出错，请联系技术人员！");
            }
        }
        return tenderdeclarMapper.verifyTenderDeclarationMethod(tenderDeclaration.getId(), tenderDeclaration.getVersion(), trader == null ? 0 :trader.getId(), trader == null ? "" : trader.getName(), trader == null ? "" : trader.getPhone(), tenderDeclaration.getStatus(), tenderDeclaration.getVerifyremarks()) == 1;
    }

    /**
     * 保存招标公司相关信息
     * @param id               公司 id
     * @param logopic          公司logo图片 url
     * @param bannerpic        公司Banner图片 url
     * @param shortername      公司名称简称
     */
    @Transactional
    public boolean doSaveTenderCompanyMethod(int id, String logopic, String bannerpic, String shortername) {
        return companyMapper.saveCompanyTenderInfo(id, logopic, bannerpic, shortername) == 1;
    }

    /**
     * 下架招标公告
     * @param id               招标公告 id
     * @param giveupremarks    下架公告 反馈信息
     */
    @Transactional
    public boolean doGiveUpTenderDeclarationMethod(int id, int version, String status, String giveupremarks) {
        return tenderdeclarMapper.giveUpDeclarationMethod(id, version, status, giveupremarks) == 1;
    }

    /**
     * 下架公司(将公司设为 非招标状态), 并下架该公司的所有招标信息
     * @param id               公司 id
     */
    public boolean doGiveUpCompanyTenderMethod(int id) {
        tenderdeclarMapper.giveUpCompanyDeclarationMethod(id, TenderStatus.TENDER_GIVEUP.toString(), "管理员将该公司的所有非下架招标下架");
        return companyMapper.changeCompanyTenderStatus(id, false) == 1;
    }

}
