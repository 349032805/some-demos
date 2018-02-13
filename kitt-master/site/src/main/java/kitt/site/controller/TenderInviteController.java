package kitt.site.controller;


import com.fasterxml.jackson.databind.JavaType;
import kitt.core.domain.MySupplyer;
import kitt.core.domain.User;
import kitt.core.domain.result.InviteRs;
import kitt.core.domain.result.TenderInviteRs;
import kitt.core.persistence.MySupplyerMapper;
import kitt.core.persistence.TenderInviteMapper;
import kitt.core.persistence.TenderdeclarMapper;
import kitt.core.persistence.UserMapper;
import kitt.core.service.MySupplyerService;
import kitt.core.util.JsonMapper;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.service.TenderInviteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by xiangyang on 16/1/15.
 */
@Controller
@LoginRequired
public class TenderInviteController extends JsonController {

    @Autowired
    private TenderInviteMapper tenderInviteMapper;
    @Autowired
    private MySupplyerMapper mySupplyerMapper;
    @Autowired
    private TenderInviteService tenderInviteService;
    @Autowired
    private MySupplyerService mySupplyerService;
    @Autowired
    private UserMapper userMapper;

    public static final int TEMPINVITE = 0;

    public static final int FORMALINVITE = 1;

    /**
     * 验证是否是审核通过的供应商
     *
     * @param companyName
     * @param phone
     * @param result
     * @return
     */
    @RequestMapping(value = "/tender/invitetender", method = RequestMethod.POST)
    @ResponseBody
    public Object mySuppliers(@RequestParam(value = "companyName", required = true) String companyName,
                              @RequestParam(value = "phone", required = true) String phone,
                              @CurrentUser User user, BindResult result) {
        if (user.getSecurephone().equals(phone)) {
            throw new BusinessException("不能邀请自己成为自己的供应商!");
        }
        //该邀请是否是审核通过的用户
        InviteRs tenderInviteRs = mySupplyerMapper.isExistsSupplyer(phone);
        //易煤网的会员
        if (tenderInviteRs != null) {
            //正式邀请
            result.addAttribute("companyName", tenderInviteRs.getCompanyName());
            result.addAttribute("phone", tenderInviteRs.getPhone());
            result.addAttribute("userId", tenderInviteRs.getUserId());
            result.addAttribute("inviteType", FORMALINVITE);
        } else {
            //临时邀请
            String cName = mySupplyerMapper.isUnVerifySupplyer(phone);
            result.addAttribute("companyName", cName != null ? cName : companyName);
            result.addAttribute("phone", phone);
            result.addAttribute("userId", StringUtils.EMPTY);
            result.addAttribute("inviteType", TEMPINVITE);
        }
        return json(result);
    }

    //当前用户是否有权限投标
    @RequestMapping(value = "/tender/isopentender", method = RequestMethod.POST)
    @ResponseBody
    public Object tenderIsOpen(@RequestParam(value = "declareId", required = true) int declareId, @CurrentUser User user, BindResult result) {
        if (!tenderInviteService.tenderIsOpen(declareId,user.getId())) {
                result.addError("fail", "unopen");
        }
        return json(result);
    }

    //添加投标邀请
    @RequestMapping(value = "/tender/addTenderInvite", method = RequestMethod.POST)
    @ResponseBody
    public Object addTenderInvite(@RequestParam(value = "inviteStr", required = true) String inviteStr,
                                  @RequestParam(value = "tenderDeclareId", required = true) Integer declareId,
                                  BindResult result) {
        tenderInviteService.addCompositeTenderInvite(declareId, inviteStr, false);
        return json(result);
    }


    //弹框带分页:我的供应商
    // account/mysupplyers 我的供应商
    @RequestMapping(value = "/tender/mysuppliers", method = RequestMethod.GET, produces = "text/html")
    public String myAllSuppliers(InviteRs tenderInviteRs,
                                 PageQueryParam pageParam,
                                 Model mode,
                                 @CurrentUser User user) {
        pageParam.setPagesize(5);
        tenderInviteRs.setStatus(1);
        int totalCount = mySupplyerMapper.countAllSupplyer(user.getId(), tenderInviteRs);
        List<InviteRs> tenderInviteRses = mySupplyerMapper.findAllSupplyer(user.getId(), tenderInviteRs, pageParam);
        pageParam.setTotalCount(totalCount);
        pageParam.setTotalPage((int)Math.ceil((double) totalCount/(double) pageParam.getPagesize()));
        pageParam.setList(tenderInviteRses);
        mode.addAttribute("page", pageParam);
        mode.addAttribute("companyName", tenderInviteRs.getCompanyName());
        mode.addAttribute("supplyerTotalCount", mySupplyerMapper.countMySupplyerTotalCount(user.getId()));
        return "/person/supplylist";
    }


    //个人中心我的供应商列表
    @RequestMapping(value = "/account/suppliers", method = RequestMethod.GET)
    public String accountSuppliers(
            @RequestParam(value = "type", defaultValue = "whitelist") supplyerType type,
            InviteRs inviteRs,
            PageQueryParam pageParam,
            @CurrentUser User user,
            Model mode) {
        int totalCount = -1;
        List<InviteRs> inviteRses = null;
        if (type.equals(supplyerType.blacklist)) {
            inviteRs.setStatus(0);
            totalCount = mySupplyerMapper.countAllSupplyer(user.getId(), inviteRs);
            if(pageParam.getPage()>(int)Math.ceil((double) totalCount/(double) pageParam.getPagesize())){
                    pageParam.setPage(totalCount/pageParam.getPagesize());
            }
            inviteRses = mySupplyerMapper.findAllSupplyer(user.getId(), inviteRs, pageParam);
        } else if (type.equals(supplyerType.alternative)) {
            totalCount = mySupplyerMapper.countAlternativeSupplyer(user.getId(), inviteRs);
            if(pageParam.getPage()>(int)Math.ceil((double) totalCount/(double) pageParam.getPagesize())){
                pageParam.setPage(totalCount/pageParam.getPagesize());
            }
            inviteRses = mySupplyerMapper.findAlternativeSupplyer(user.getId(), inviteRs, pageParam);
        } else if (type.equals(supplyerType.whitelist)) {
            inviteRs.setStatus(1);
            totalCount = mySupplyerMapper.countAllSupplyer(user.getId(), inviteRs);
            if(pageParam.getPage()>(int)Math.ceil((double) totalCount/(double) pageParam.getPagesize())){
                pageParam.setPage(totalCount/pageParam.getPagesize());
            }
            inviteRses = mySupplyerMapper.findAllSupplyer(user.getId(), inviteRs, pageParam);
        }
        Map<String, Integer> res = mySupplyerMapper.countMyInvite(user.getId());

        pageParam.setTotalCount(totalCount);
        pageParam.setTotalPage((int)Math.ceil((double) totalCount/(double) pageParam.getPagesize()));
        pageParam.setList(inviteRses);
        mode.addAttribute("page", pageParam);
        mode.addAttribute("companyName", inviteRs.getCompanyName());
        mode.addAttribute("blackListCount", res.get("blackListCount"));
        mode.addAttribute("whiteListCount", res.get("whiteListCount"));
        mode.addAttribute("alternativeCount", res.get("alternativeCount"));
        mode.addAttribute("type", type);
        return "/person/suppliers";
    }

    //加入黑名单
    @RequestMapping(value = "/account/addSupplyertoBlackList", method = RequestMethod.POST)
    @ResponseBody
    public Object addSupplyerBlack(@RequestParam("ids") List<Integer> ids, @CurrentUser User user, BindResult result) {
        mySupplyerMapper.updateMySupplyerStatus(user.getId(), 0, ids);
        tenderInviteMapper.updateTenderInvite(user.getId(), ids);
        return json(result);
    }

    //删除我的供应商
    @RequestMapping(value = "/account/deleteMySupplyer", method = RequestMethod.POST)
    @ResponseBody
    public Object deleteBlackListSupplyer(@RequestParam(value = "type", required = true) supplyerType type,
                                          @RequestParam("ids") List<Integer> ids,
                                          @CurrentUser User user, BindResult result) {
        if (type.equals(supplyerType.whitelist) || type.equals(supplyerType.blacklist)) {
            mySupplyerMapper.deleteMySupplyer(user.getId(), ids);
        } else if (type.equals(supplyerType.alternative)) {
            mySupplyerMapper.deleteAlternativeSupplyer(user.getId(), ids);
        }
        return json(result);
    }


    //一次添加多个供应商
    @RequestMapping(value = "/account/addSuppliers", method = RequestMethod.POST)
    @ResponseBody
    public Object addMySupplyer(@RequestParam("tenderinvite") String inviteStr,
                                @CurrentUser User user,
                                BindResult result) {
        JavaType javaType = JsonMapper.nonDefaultMapper().contructCollectionType(List.class, InviteRs.class);
        List<InviteRs> inviteList = JsonMapper.nonDefaultMapper().fromJson(inviteStr, javaType);
        if (inviteList.stream().filter(t -> t.getPhone().equals(user.getSecurephone())).count() > 0) {
            throw new BusinessException("不能邀请自己成为自己的供应商!");
        }
        mySupplyerService.addSupplyer(inviteList, user.getId());
        return result;
    }

    //从黑名单添加为我的供应商
    @RequestMapping(value = "/account/addSupplyers", method = RequestMethod.POST)
    @ResponseBody
    public Object addSupplyers(@RequestParam("ids") List<Integer> ids, @CurrentUser User user, BindResult result) {
        mySupplyerMapper.updateMySupplyerStatus(user.getId(), 1, ids);
        return json(result);
    }

    //招标:招标设置
    @RequestMapping(value = "/account/tender_suppliers", method = RequestMethod.GET)
    public String tenderSuppliers(@RequestParam(value = "type", defaultValue = "inviteing") tenderInviteType type, @CurrentUser User user, PageQueryParam pageParam, Model model) {
        Map<String, Integer> countMap = tenderInviteMapper.findInviteCount(user.getId());
        int totalCount = tenderInviteMapper.countInviteInMyDeclaretion(user.getId(), type.name());
        List<TenderInviteRs> inviteRses = tenderInviteMapper.findInviteInMyDeclaretion(user.getId(), type.name(), pageParam);
        pageParam.setTotalCount(totalCount);
        pageParam.setTotalPage((int)Math.ceil((double) totalCount/(double) pageParam.getPagesize()));
        pageParam.setList(inviteRses);
        model.addAttribute("inviteCount", countMap.get("inviteCount"));
        model.addAttribute("inviteCompleteCount", countMap.get("inviteCompleteCount"));
        model.addAttribute("page", pageParam);
        model.addAttribute("type", type);
        return "/person/tenderSetting";
    }

    @RequestMapping(value = "/account/loadInvitedSupplyer", method = RequestMethod.POST)
    @ResponseBody
    public Object loadtenderInviteSupplyer(@RequestParam(value = "declareId", required = true) int declareId, @CurrentUser User user, BindResult result) {
        result.addAttribute("inviteSupplyer", tenderInviteMapper.loadInviteByDeclareId(declareId, user.getId()));
        return json(result);
    }

    /**
     * 是否已经存在我的供应商列表里
     *
     * @param phone
     * @param result
     * @return
     */
    @RequestMapping(value = "/account/isyimeiUser", method = RequestMethod.POST)
    @ResponseBody
    public Object isYimeiUser(@RequestParam(value = "phone") String phone,
                              @CurrentUser User user, BindResult result) {
        MySupplyer mySupplyer = mySupplyerMapper.isExistsMySupplyer(user.getId(), phone);
        if (mySupplyer != null && mySupplyer.getStatus() == 0) {
            result.addError("result", "isExistsBlack");
        }
        if (userMapper.getUserByPhone(phone) == null) {
            result.addError("result", "isNotExist");
            return json(result);
        }
        return json(result);
    }

    /**
     * 是否已经存在供应商列表
     *
     * @param phone
     * @param result
     * @return
     */
    @RequestMapping(value = "/account/isExistsMySupplyer", method = RequestMethod.POST)
    @ResponseBody
    public Object isExistsMySupplyer(@RequestParam(value = "phone") String phone,
                                     @CurrentUser User user, BindResult result) {
        MySupplyer mySupplyer = mySupplyerMapper.isExistsMySupplyer(user.getId(), phone);
        if (mySupplyer != null && mySupplyer.getStatus() == 1) {
            result.addError("result", "手机号已经存在供应商列表!");
        }
        if (mySupplyer != null && mySupplyer.getStatus() == 0) {
            result.addError("result", "手机号已经存在供应商黑名单列表!");
        }
        if (mySupplyerMapper.isExistsAlternativeSupplyer(user.getId(), phone) > 0) {
            result.addError("result", "手机号已经存在备选供应商列表!");

        }
        return json(result);
    }


    public enum supplyerType {
        blacklist, whitelist, alternative
    }

    public enum tenderInviteType {
        inviteing, inviteComplete
    }


}
