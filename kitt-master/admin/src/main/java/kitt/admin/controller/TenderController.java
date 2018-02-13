package kitt.admin.controller;

import com.mysql.jdbc.StringUtils;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.BusinessException;
import kitt.admin.basic.exception.NotFoundException;
import kitt.admin.service.Auth;
import kitt.admin.service.Session;
import kitt.admin.service.TenderService;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.MessageNotice;
import kitt.core.service.MySupplyerService;
import kitt.ext.mybatis.Where;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 15/11/12.
 */
@RestController
@RequestMapping("/tender")
public class TenderController {
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private TenderitemMapper tenderitemMapper;
    @Autowired
    private TenderpacketMapper tenderpacketMapper;
    @Autowired
    private TenderService tenderService;
    @Autowired
    private IndexBannerMapper indexBannerMapper;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private Auth auth;
    @Autowired
    private MytenderMapper mytenderMapper;
    @Autowired
    private BidMapper bidMapper;
    @Autowired
    private MySupplyerService mySupplyerService;

    /**
     * 招标列表 - 待审核
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/wait")
    public Object getWaitTenderDeclarationList(@RequestParam(value = "content", required = false, defaultValue = "")String content,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "startDate", required = false, defaultValue = "")LocalDate startDate,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "endDate", required = false, defaultValue = "")LocalDate endDate,
                                               @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return tenderService.getTenderList(content, startDate, endDate, TenderStatus.TENDER_APPLY.toString(), page);
    }

    /**
     * 招标列表 - 审核通过
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/pass")
    public Object getPassTenderDeclarationList(@RequestParam(value = "content", required = false, defaultValue = "")String content,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "startDate", required = false, defaultValue = "")LocalDate startDate,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "endDate", required = false, defaultValue = "")LocalDate endDate,
                                               @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return tenderService.getTenderList(content, startDate, endDate, TenderStatus.TENDER_VERIFY_PASS.toString(), page);
    }

    /**
     * 招标列表 - 审核未通过
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/fail")
    public Object getFailTenderDeclarationList(@RequestParam(value = "content", required = false, defaultValue = "")String content,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "startDate", required = false, defaultValue = "")LocalDate startDate,
                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "endDate", required = false, defaultValue = "")LocalDate endDate,
                                               @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return tenderService.getTenderList(content, startDate, endDate, TenderStatus.TENDER_VERIFY_FAIL.toString(), page);
    }

    /**
     * 招标列表 - 已作废
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/cancel")
    public Object getCancelTenderDeclarationList(@RequestParam(value = "content", required = false, defaultValue = "")String content,
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "startDate", required = false, defaultValue = "")LocalDate startDate,
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "endDate", required = false, defaultValue = "")LocalDate endDate,
                                                 @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        return tenderService.getTenderList(content, startDate, endDate, TenderStatus.TENDER_CANCEL.toString(), page);
    }

    /**
     * 招标详细(tenderdeclaration表)
     * @param id              TenderDeclaration id
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/detail")
    public Object getTenderDeclarationDetail(@RequestParam(value = "id", required = true) int id,
                                             @RequestParam(value = "page", required = false, defaultValue = "1") int page){
        TenderDeclaration tenderDeclaration = tenderdeclarMapper.findTendDeclarById(id);
        if(tenderDeclaration == null) throw new NotFoundException();
        User user = userMapper.getUserById(tenderDeclaration.getUserid());
        if (user == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<>();
        List<TenderItem>  tenderItemList = tenderitemMapper.findTendItemByDecalarId(id);
        for(TenderItem item : tenderItemList){
            item.setPacketList(tenderpacketMapper.findTendpackgeByItemId(item.getId()));
        }
        tenderDeclaration.setItemList(tenderItemList);
        map.put("traderid", user.getTraderid());
        map.put("traderList", auth.getDealerList());
        map.put("tenderDeclaration", tenderDeclaration);
        if (auth.checkIFSuperAdminById()) {
            map.put("bidList", bidMapper.pageBidAndCompanyListByTenderDeclarationId(tenderDeclaration.getId(), page, 10));
        }
        return map;
    }

    /**
     * 审核招标 TenderDeclaration
     * @param id             TenderDeclaration id
     * @param result         审核结果, 0: 审核通过, 1: 审核未通过
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/verify")
    public boolean doVerifyTenderDeclaration(@RequestParam(value = "id", required = true)int id,
                                             @RequestParam(value = "version", required = true, defaultValue = "0")int version,
                                             @RequestParam(value = "traderId", required = true, defaultValue = "0")int traderid,
                                             @RequestParam(value = "result", required = true)int result,
                                             @RequestParam(value = "verifyremarks", required = false)String verifyremarks){
        TenderDeclaration tenderDeclaration = tenderdeclarMapper.findTendDeclarById(id);
        if(tenderDeclaration == null) throw new NotFoundException();
        Company company = companyMapper.getCompanyById(tenderDeclaration.getCompanyid());
        if(company == null) throw new NotFoundException();
        User user = userMapper.getUserById(tenderDeclaration.getUserid());
        if(user == null) throw new NotFoundException();
        if(StringUtils.isNullOrEmpty(company.getLogopic()) || StringUtils.isNullOrEmpty(company.getBannerpic())){
            throw new BusinessException("该公司还没有上传公司logo和公司图片, 请先去招标公司详细页面上传公司logo和公司图片! ");
        }
        Admin trader = adminMapper.getAdminById(traderid);
        if((result == 0 && trader == null) || (result == 1 && StringUtils.isEmptyOrWhitespaceOnly(verifyremarks))) throw new BusinessException("审核出错, 请联系技术人员! ");
        tenderDeclaration.setVersion(version);
        tenderDeclaration.setVerifyremarks(verifyremarks);
        String status = null;
        final String PassContent = "您好,您在易煤网平台发布的招标" + tenderDeclaration.getTendercode() + "已经审核通过, 请及时关注投标情况。";
        final String NotPassContent = "您好,您在易煤网平台发布的招标" + tenderDeclaration.getTendercode() + "审核没有通过, 原因是:" + verifyremarks + ", 请您及时修改。";
        final String NotPassContentTwo = "您好,您在易煤网平台发布的招标" + tenderDeclaration.getTendercode() + "审核没有通过, 原因是:投标截止日期已过, 请重新发布招标!";
        if(result == 1){
            status = TenderStatus.TENDER_VERIFY_FAIL.toString();
            MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), NotPassContent);
        } else if(result == 0) {
            if (!tenderDeclaration.getTenderbegindate().isAfter(LocalDateTime.now()) && !tenderDeclaration.getTenderenddate().isBefore(LocalDateTime.now())) {
                status = TenderStatus.TENDER_START.toString();
                MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), PassContent);
                mySupplyerService.sendTenderInviteMessage(id);
            } else if (tenderDeclaration.getTenderenddate().isBefore(LocalDateTime.now())) {
                tenderDeclaration.setVerifyremarks("投标截止日期已过, 请重新发布招标! ");
                status = TenderStatus.TENDER_CANCEL.toString();
                MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), NotPassContentTwo);
            } else {
                status = TenderStatus.TENDER_VERIFY_PASS.toString();
                MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), PassContent);
            }
        } else {
            throw new NotFoundException();
        }
        if (StringUtils.isNullOrEmpty(status)) throw new NotFoundException();
        tenderDeclaration.setStatus(status);
        return tenderService.doVerifyTenderDeclarationMethod(tenderDeclaration, user, trader);
    }

    /**
     * 下架审核通过的招标公告
     * @param id              招标公告 id
     * @param giveupremarks   下架备注信息
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/giveuptender")
    public boolean doGiveUpTenderDeclaration(@RequestParam(value = "id", required = true)int id,
                                             @RequestParam(value = "version", required = true)int version,
                                             @RequestParam(value = "giveupremarks", required = true)String giveupremarks){
        if(tenderdeclarMapper.findTendDeclarById(id) == null) throw new NotFoundException();
        return tenderService.doGiveUpTenderDeclarationMethod(id, version, TenderStatus.TENDER_GIVEUP.toString(), giveupremarks);
    }

    /**
     * 招标Banner图列表
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @RequestMapping("/banner")
    public List<IndexBanner> getTenderIndexBannerList(){
        return indexBannerMapper.getAdminIndexBannnersWithLimit("tenderbanner", 5);
    }

    /**
     * 上传招标Banner图片
     * @param file            图片对象
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.NetworkEditor)
    @RequestMapping("/addBannerPic")
    public Object addTenderBrandPic(@RequestParam("file") MultipartFile file) throws Exception {
        return auth.uploadPicMethod(EnumFileType.File_Tender.toString(), EnumFileType.IMG.toString(), file, 2000, 320);
    }

    /**
     * 上传招标公司图片
     * @param file            图片对象
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/addCompanyBannerPic")
    public Object addTenderCompanyBrandPic(@RequestParam("file") MultipartFile file) throws Exception {
        return auth.uploadPicMethod(EnumFileType.File_Tender.toString(), EnumFileType.IMG.toString(), file, null, null);
    }

    /**
     * 招标公司列表
     * @param content     搜索框输入的内容
     * @param page        页码
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/companyList")
    public Object doGetCompanyList(@RequestParam(value = "content", required = false)String content,
                                   @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        Map<String, Object> map = new HashMap<>();
        map.put("content", content);
        map.put("companyList", companyMapper.pageAllTenderCompanies(Where.$like$(content), page, 10));
        return map;
    }

    /**
     * 下架公司 及 公司下所有非下架招标
     * @param id          公司 id
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/giveupcompanytender")
    public boolean doGiveUpCompanyTender(@RequestParam(value = "id", required = true)int id){
        if(companyMapper.getCompanyById(id) == null) throw new NotFoundException();
        return tenderService.doGiveUpCompanyTenderMethod(id);
    }

    /**
     * 招标公司详细页面
     * @param id          公司 id
     * @param page        该公司的招标信息 页码
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/company/detail")
    public Object doGetTenderCompanyDetail(@RequestParam(value = "id", required = true)int id,
                                           @RequestParam(value = "type", required = false, defaultValue = "company")String type,
                                           @RequestParam(value = "page", required = false, defaultValue = "1")int page){
        Company company = companyMapper.getCompanyById(id);
        if(company == null) throw new NotFoundException();
        Map<String, Object> map = new HashMap<>();
        map.put("company", company);
        map.put("type", type);
        map.put("tenderList", tenderdeclarMapper.pageAllTenderDeclaration(null, null, id, null, null, page, 10));
        return map;
    }

    /**
     * 保存招标公司相关信息
     * @param id               公司 id
     * @param logopic          公司logo图片 url
     * @param bannerpic        公司Banner图片 url
     * @param shortername      公司名称简称
     */
    @Authority(role = AuthenticationRole.Admin)
    @Authority(role = AuthenticationRole.TraderAssistant)
    @RequestMapping("/saveTenderCompanyInfo")
    public boolean doSaveTenderCompany(@RequestParam(value = "id", required = true)int id,
                                       @RequestParam(value = "logopic", required = true)String logopic,
                                       @RequestParam(value = "bannerpic", required = true)String bannerpic,
                                       @RequestParam(value = "shortername", required = false)String shortername){
        Company company = companyMapper.getCompanyById(id);
        if(company == null) throw new NotFoundException();
        return tenderService.doSaveTenderCompanyMethod(id, logopic, bannerpic, shortername);
    }

    /**
     * 投标详细页面
     * @param id               bid表对应的id
     */
    @Authority(role = AuthenticationRole.Admin)
    @RequestMapping("/bid/detail")
    public Object doShowBidDetail(@RequestParam(value = "id", required = true)int id) {
        if (auth.checkIFSuperAdminById()) {
            Bid bid = bidMapper.getBidById(id);
            if (bid == null) throw new NotFoundException();
            return new Object() {
                public List<Map<String, Object>> mytenderList = mytenderMapper.getMyTenderItemPackerSequenceByBidId(id);
            };
        } else {
            return null;
        }
    }

}
