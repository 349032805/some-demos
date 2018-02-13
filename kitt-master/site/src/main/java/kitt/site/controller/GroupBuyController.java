package kitt.site.controller;

import freemarker.template.TemplateException;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.Freemarker;
import kitt.core.service.MessageNotice;
import kitt.core.service.PDF;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.FileDownload;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by zhangbolun on 15/1/19.
 */
@Controller
public class GroupBuyController extends JsonController {
    @Autowired
    private Session session;
    @Autowired
    private ProviderInfoMapper providerInfoMapper;
    @Autowired
    private GroupBuyQualificationMapper groupBuyQualifyMapper;
    @Autowired
    private GroupBuySupplyMapper groupBuySupplyMapper;
    @Autowired
    private GroupBuyOrderMapper groupBuyOrderMapper;
    @Autowired
    private Freemarker freemarker;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    protected FileService fileService;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected GroupBuyService groupBuyService;
    @Autowired
    private Auth auth;
    private String contractcontent;

    //团购首页信息列表
    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public String getAllGroupBuySupplys(@RequestParam(value = "page", required = false, defaultValue = "1")int page,
                                        Map<String, Object> model, PageQueryParam param){
        int totalCount = groupBuySupplyMapper.getGroupBuySupplyCount();
        param.setCount(totalCount);
        model.put("groupBuySupplies", groupBuySupplyMapper.getRemindGroupBuySupplyList(param.getPagesize(), param.getIndexNum()));
        model.put("count", totalCount);
        model.put("pagesize", param.getPagesize());
        model.put("page", param.getPage());
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        model.put("serverdatetime",df.format(new Date()));
        model.put("mallType", "mall");

        model.put("title",Seoconfig.groupBuy_title);
        model.put("keywords",Seoconfig.groupBuy_keywords);
        model.put("description",Seoconfig.groupBuy_description);
        return "group";
    }

    //获取团购资质
    @RequestMapping(value = "/group/getGroupCertification")
    @LoginRequired
    public String doGetGroupCertification(HttpServletRequest request, Map<String, Object> model) throws IOException, TemplateException, ServletException {
        List<GroupBuyQualification> groupBuyQualifications = groupBuyQualifyMapper.getGroupBuyQualifyByStatusId(session.getUser().getId(), QualifyStatus.QUALIFY_START.toString());
        if(groupBuyQualifications.size()==0) {
            GroupBuyQualification groupBuyQualification = new GroupBuyQualification(session.getUser().getId(), QualifyStatus.QUALIFY_START.toString(), 1);
            groupBuyQualifyMapper.addGroupBuyQualify(groupBuyQualification);
            contractContent(groupBuyQualification.getId(), null, request, model);
        }else {
            groupBuyQualifications.get(0).setCreatetime(LocalDateTime.now());
            groupBuyQualifyMapper.updateGroupBuyQualifyById(groupBuyQualifications.get(0));
            groupBuyQualifications=groupBuyQualifyMapper.getGroupBuyQualifyByStatusId(session.getUser().getId(), QualifyStatus.QUALIFY_START.toString());
            contractContent(groupBuyQualifications.get(0).getId(), null, request, model);
        }
        return "groupQualification";
    }


    //上传保证金图片，申请团购资质
    @LoginRequired
    @RequestMapping(value = "/group/saveGroupQualification", method = RequestMethod.POST)
    @ResponseBody
    public Object saveGroupQualification(@RequestParam(value = "id", required = true)int id, @RequestParam("file") MultipartFile file) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        User user= userMapper.getUserById(session.getUser().getId());
        boolean succeed=true;
        if(user.getVerifystatus().equals("审核通过")) {
            String picSavePath = fileService.uploadPicture(file);
            map.put("picSavePath", picSavePath);
            map.put("id", id);
            map.put("succeed",succeed);
        }else {
            succeed=false;
            map.put("error","公司审核未通过，请查看公司信息!");
            map.put("succeed",succeed);
        }
        return map;
    }

    //确认申请团购资质
    @LoginRequired
    @RequestMapping(value = "/group/confirmGroupQualification", method = RequestMethod.POST)
    @ResponseBody
    public Object confirmGroupQualification(@RequestParam(value = "id", required = true)int id,
                                            @RequestParam("picSavePath") String picSavePath,
                                            HttpServletResponse response) throws Exception{
        Map<String, Object> map = new HashMap<String, Object>();
        User user= userMapper.getUserById(session.getUser().getId());
        boolean succeed=true;
        if(user.getVerifystatus().equals("审核通过")) {
            response.setContentType("text/html");
            GroupBuyQualification groupBuyQualification = groupBuyQualifyMapper.getGroupBuyQualifyById(id);
            if (groupBuyQualification == null) throw new NotFoundException();
            map.put("picSavePath", picSavePath);
            groupBuyQualification.setPhotopath(picSavePath);
            groupBuyQualification.setStatus(QualifyStatus.QUALIFY_APPLY.toString());
            groupBuyQualification.setContractverify(true);
            groupBuyQualification.setConfirmtime(LocalDateTime.now());
            groupBuyQualifyMapper.updateGroupBuyQualifyById(groupBuyQualification);
            //短信通知用户资质申请成功
            MessageNotice.GROUPQualify.noticeUser(user.getSecurephone(),groupBuyQualification.getQualificationcode());
            map.put("id", id);
            map.put("succeed",succeed);
        }else {
            succeed=false;
            map.put("error","公司审核未通过，请查看公司信息!");
            map.put("succeed",succeed);
        }
        return map;
    }


    //删除团购资质图片
    @RequestMapping(value = "/group/deletePicture")
    @ResponseBody
    @LoginRequired
    public Object doDeletePicture(@RequestParam(value = "id", required = true)int id){
        GroupBuyQualification groupBuyQualification = groupBuyQualifyMapper.getGroupBuyQualifyById(id);
        if(groupBuyQualification == null) throw new NotFoundException();
        groupBuyQualifyMapper.deletePictureById(id);
        groupBuyQualifyMapper.setGroupBuyQualificationStatus(QualifyStatus.QUALIFY_START.toString(), id);
        return true;
    }

    //检查用户是否有可用团购资质
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/group/checkActiveQualify", method = RequestMethod.POST)
    public Object checkActiveQualify(){
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        List<GroupBuyQualification> groupBuyQualificationList = groupBuyQualifyMapper.getGroupBuyQualifyByUserid(session.getUser().getId());
        if(groupBuyQualificationList.size() == 0){
            map.put("error", "none");
        } else {
            for(GroupBuyQualification groupBuyQualification : groupBuyQualificationList){
                if(groupBuyQualification.getStatus().equals(QualifyStatus.QUALIFY_ACTIVE.toString())){
                    success = true;
                    break;
                }
            }
        }
        map.put("success", success);
        return map;
    }

    //查看团购详细信息
//    @LoginRequired
    @RequestMapping(value = "/group/showGroupInfo", method = RequestMethod.GET)
    public String groupBuySupplyDetail(@RequestParam(value = "groupBuySupplyId", required = true)int groupBuySupplyId, Map<String, Object> model){
        /**
         * add by xj for viewtime -->浏览量的增加
         */
        groupBuySupplyMapper.setPageViewTimesById(groupBuySupplyId);

        GroupBuySupply groupBuySupply = groupBuySupplyMapper.getGroupBuySupplyById(groupBuySupplyId);
        if(groupBuySupply == null) throw new NotFoundException();
        ProviderInfo providerInfo = providerInfoMapper.getProviderInfoById(groupBuySupply.getProviderinfoid());
        if(providerInfo == null) throw new NotFoundException();
        //获取最新成交记录
        List<TransactionOrder> transactionOrders = new ArrayList<TransactionOrder>();
        List<GroupBuyOrder> groupBuyOrders = groupBuyOrderMapper.getTransactionOrders();
        for(GroupBuyOrder groupBuyOrder : groupBuyOrders){
            TransactionOrder transactionOrder = new TransactionOrder();
            transactionOrder.setVolume(groupBuyOrder.getVolume());
            GroupBuySupply temp = groupBuySupplyMapper.getGroupBuySupplyById(groupBuyOrder.getGroupbuysupplyid());
            if(temp != null){
                transactionOrder.setGroupbuyprice(temp.getGroupbuyprice());
                transactionOrder.setCoaltype(temp.getCoaltype());
                transactionOrder.setDeliverytime(groupBuyOrder.getCreatetime());
                transactionOrder.setMarketprice(temp.getMarketprice());
                transactionOrder.setPort(temp.getPort());
            }
            transactionOrders.add(transactionOrder);
        }
        //获取修改前吨数
        int volume=0;
        double totalprice=0.0;

        model.put("volume", volume);
        model.put("totalprice", totalprice);
        model.put("groupBuySupply", groupBuySupply);
        model.put("providerInfo", providerInfo);
        model.put("transactionOrders", transactionOrders);
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        model.put("serverdatetime",df.format(new Date()));
        return "groupActivityDetail";
    }

    //验证团购订单
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/group/checkGroupBuyOrder", method = RequestMethod.POST)
    public Object checkGroupBuyOrder(@RequestParam(value = "groupBuySupplyId", required = true)int groupBuySupplyId,@RequestParam(value = "volume", required = true)int volume){
        User user= userMapper.getUserById(session.getUser().getId());
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        if(!user.getVerifystatus().equals("审核通过")) {
            map.put("success",success);
            map.put("message","verifyfail");
            return map;
        }

        GroupBuySupply groupBuySupply = groupBuySupplyMapper.getGroupBuySupplyById(groupBuySupplyId);
        String message="";
        if(groupBuySupply!=null){

            LocalDateTime now=LocalDateTime.now();
            if(groupBuySupply.getGroupbuybegindate().isBefore(now)&&groupBuySupply.getGroupbuyenddate().isAfter(now) && groupBuySupply.getSurplusamount() >= volume) {
                success= true;
            }
            else {
                if(groupBuySupply.getGroupbuybegindate().isAfter(now)){
                    message= "notbegin";
                }else if(groupBuySupply.getSurplusamount() < volume){
                    message = "inventory"; //库存不足
                }else {
                    message= "finished";
                }
            }
        }
        map.put("success",success);
        map.put("message",message);
        return map;
    }

    //生成团购订单信息
    @LoginRequired
    @RequestMapping(value = "/group/generateGroupBuyOrder", method = RequestMethod.GET)
    public String generateGroupBuyOrder(@RequestParam(value = "groupBuySupplyId", required = true)int groupBuySupplyId,
                                        @RequestParam(value = "volume", required = true)int volume,
                                        @RequestParam(value = "type", required = false, defaultValue = "null")String type,
                                        Map<String, Object> model){

        //检查团购供应信息是否存在
        GroupBuySupply groupBuySupply = groupBuySupplyMapper.getGroupBuySupplyById(groupBuySupplyId);
        if(groupBuySupply==null)
            throw new BusinessException("团购供应信息不存在！");
        if (groupBuySupply.getSurplusamount()<volume)
            throw new BusinessException("库存不足！");

        GroupBuyOrder groupBuyOrder= groupBuyService.generateGroupBuyOrder(groupBuySupply, volume, groupBuySupplyId, session.getUser());

        model.put("groupBuyOrder", groupBuyOrder);
        model.put("groupBuySupply", groupBuySupply);
        model.put("volume", volume);
        model.put("groupbuyordercode", groupBuyOrder.getGroupbuyordercode());
        model.put("type", type);

        return "purchaseDetail";
    }

    //提交团购订单,合同授权,更新团购供应量
    @LoginRequired
    @ResponseBody
    @RequestMapping(value = "/group/confirmGroupBuyOrder", method = RequestMethod.POST)
    public boolean confirmGroupBuyOrder(@RequestParam(value = "groupbuyordercode", required = true)String groupbuyordercode,
                                       @RequestParam(value = "volume", required = true)int volume,
                                       @CurrentUser User user){
        boolean result= groupBuyService.confirmGroupBuyOrder(groupbuyordercode,volume);
        MessageNotice.GROUPOrder.noticeUser(user.getSecurephone(), groupbuyordercode);
        return result;
    }

    //个人中心获取我的团购资质列表
    @LoginRequired
    @RequestMapping(value = "/account/getMyQualification", method = RequestMethod.GET)
    public String getMyQualifcation(@RequestParam(value="pagesize", required = false, defaultValue = "10")int pagesize,
                                    PageQueryParam param, Map<String, Object> model){
        int totalCount = groupBuyQualifyMapper.countGroupBuyQualifyBuStatusId(session.getUser().getId());
        param.setCount(totalCount);
        List<GroupBuyQualification> groupBuyQualificationList = groupBuyQualifyMapper.getGroupBuyQualifyByStatusIdPage(session.getUser().getId(), pagesize, param.getIndexNum());
        model.put("groupBuyQualificationList", groupBuyQualificationList);
        model.put("qualifyCount", totalCount);
        model.put("pagesizeQualify", pagesize);
        model.put("qualifyPageNumber", param.getPage());
//        return "individualCenter";
        return "person/groupBuyQualification";
    }

    //个人中心-获取我的团购订单-进行中的团购
    @LoginRequired
    @RequestMapping(value = "/account/getMyOrderActive", method = RequestMethod.GET)
    public String getMyOrderActive(@RequestParam(value = "pagesize", required = false, defaultValue = "10")int pagesize,
                                   PageQueryParam param, Map<String, Object> model){
        int totalCount = groupBuyOrderMapper.countOrdersByUserId(session.getUser().getId(), OrderStatus.ORDER_ACTIVE.toString());
        param.setCount(totalCount);
        List<GroupBuyOrder> groupBuyOrdersActiveList = groupBuyOrderMapper.getOrdersByUserId(session.getUser().getId(), OrderStatus.ORDER_ACTIVE.toString(), pagesize, param.getIndexNum());
        if(groupBuyOrdersActiveList!=null){
            for(GroupBuyOrder groupBuyOrder:groupBuyOrdersActiveList){
                GroupBuySupply groupBuySupply=  groupBuySupplyMapper.getGroupBuySupplyById(groupBuyOrder.getGroupbuysupplyid());
                if(groupBuySupply!=null){
                    groupBuyOrder.setGroupbuyprice(groupBuySupply.getGroupbuyprice());
                    groupBuyOrder.setGroupbuyenddate(groupBuySupply.getGroupbuyenddate());
                }
            }
        }
        model.put("groupBuyOrdersActiveList", groupBuyOrdersActiveList);
        model.put("activeSupplyCount", totalCount);
        model.put("activePageNumber", param.getPage());
        model.put("pagesizeGroupActive", pagesize);
        return "person/groupBuyOrder";
    }

    //个人中心-获取我的团购订单-已结束的团购
    @LoginRequired
    @RequestMapping(value = "/account/getMyOrderFinish", method = RequestMethod.GET)
    public String getMyOrderFinish(@RequestParam(value = "pagesize", required = false, defaultValue = "10")int pagesize,
                                   PageQueryParam param, Map<String, Object> model){
        List<GroupBuyOrder> groupBuyOrdersFinishes= groupBuyOrderMapper.getOrdersByStatuses(session.getUser().getId(), OrderStatus.ORDER_FINISH.toString(),OrderStatus.ORDER_FAIL.toString(), pagesize, param.getIndexNum());
        if(groupBuyOrdersFinishes!=null){
            for(GroupBuyOrder groupBuyOrder:groupBuyOrdersFinishes){
                GroupBuySupply groupBuySupply=  groupBuySupplyMapper.getGroupBuySupplyById(groupBuyOrder.getGroupbuysupplyid());
                if(groupBuySupply!=null){
                    groupBuyOrder.setGroupbuyprice(groupBuySupply.getGroupbuyprice());
                    groupBuyOrder.setGroupbuyenddate(groupBuySupply.getGroupbuyenddate());
                }
            }
        }
        int totalCount = groupBuyOrderMapper.countOrdersByStatuses(session.getUser().getId(), OrderStatus.ORDER_FINISH.toString(), OrderStatus.ORDER_FAIL.toString());
        model.put("groupBuyOrdersFinishes",groupBuyOrdersFinishes);
        model.put("finishedSupplyCount", totalCount);
        model.put("pagesizeGroupFinish", pagesize);
        model.put("finishedPageNumber", param.getPage());
        return "person/groupBuyOrder";
    }

    //个人中心-团购保证金-放弃资格
    @LoginRequired
    @RequestMapping(value = "/account/giveupQualify", method = RequestMethod.POST)
    @ResponseBody
    public Object giveupQualify(@RequestParam(value = "qualificationcode", required = true) String qualificationcode){
        GroupBuyQualification groupBuyQualify = groupBuyQualifyMapper.getGroupBuyQualifyByCode(qualificationcode);
        if(groupBuyQualify == null) throw new NotFoundException();
        if(groupBuyQualify.getStatus().equals(QualifyStatus.QUALIFY_ACTIVE.toString())) {
            groupBuyQualifyMapper.updateStatusByCode(qualificationcode, QualifyStatus.QUALIFY_GIVEUP.toString());
            return true;
        }
        return false;
    }

    //个人中心-进行中的团购-查看详情
    @LoginRequired
    @RequestMapping(value = "/account/selectOrderDetail", method = RequestMethod.GET)
    public String selectOrderDetail(@RequestParam(value = "groupbuyordercode", required = true)String groupbuyordercode,
                                    Map<String, Object> model){
        GroupBuyOrder groupBuyOrder = groupBuyOrderMapper.getOrderByCode(groupbuyordercode);
        if(groupBuyOrder == null) throw new NotFoundException();
        GroupBuySupply groupBuySupply = groupBuySupplyMapper.getGroupBuySupplyById(groupBuyOrder.getGroupbuysupplyid());
        if(groupBuySupply == null) throw new NotFoundException();
        model.put("groupBuyOrder", groupBuyOrder);
        model.put("groupBuySupply",groupBuySupply);
        return "purchaseDetail";
    }

    //是否删除
    @LoginRequired
    @RequestMapping(value = "/account/deleteFinishedGroupOrder", method = RequestMethod.POST)
    @ResponseBody
    public Object doDeleteGroupOrder(@RequestParam(value = "groupbuyordercode", required = true)String groupbuyordercode){
        GroupBuyOrder groupBuyOrder = groupBuyOrderMapper.getOrderByCode(groupbuyordercode);
        if(groupBuyOrder == null) throw new NotFoundException();
        groupBuyOrderMapper.deleteOrderByCode(groupbuyordercode);
        return true;
    }

    //下载电子合同
    @RequestMapping("/group/downloadContract")
    public void testHtml(@RequestParam(value="id", required = true)int id,
                         HttpServletResponse response) throws Exception {
        GroupBuyQualification groupBuyQualification = groupBuyQualifyMapper.getGroupBuyQualifyById(id);
        if (groupBuyQualification == null) throw new NotFoundException();
        String filename = groupBuyQualification.getQualificationcode()+".pdf";
        File target = fileService.getDownloadFileByFileName(filename);
        if(!target.exists()) {
            File file = PDF.create(contractcontent);
            fileService.copyToDownload(file,filename);
        }
        FileDownload.doDownloadFile(target, response);
    }

    //查看合同
    @RequestMapping("/account/showDepositContract")
    public String showContract(@RequestParam(value = "id", required = true)int id,
                               @RequestParam(value = "root", required = true)String root,
                               HttpServletRequest request,
                               Map<String, Object> model) throws IOException, TemplateException, ServletException {
        contractContent(id, root, request, model);
        return "groupQualification";
    }

    //查看合同--履约金
    @RequestMapping("/account/showExecutionContract")
    public String showExecutionContract(@RequestParam(value = "id", required = true)int id,
                                        @RequestParam(value = "root", required = true)String root,
                                        HttpServletRequest request,
                                        Map<String, Object> model) throws IOException, TemplateException, ServletException {
        contractContent(id, root, request, model);
        return "showExecutionContract";
    }

    //个人中心团购退款申请
    @LoginRequired
    @ResponseBody
    @RequestMapping("/account/applyRefund")
    public Object applyRefund(@RequestParam(value = "qualificationcode",required = true)String qualificationcode){
        Map<String, Object> map = new HashMap<String, Object>();
        GroupBuyQualification groupBuyQualification= groupBuyQualifyMapper.getGroupBuyQualifyByCode(qualificationcode);
        if(groupBuyQualification==null)
            throw new NotFoundException();
        if(groupBuyQualification.getStatus().equals(QualifyStatus.QUALIFY_INPROCESS.toString())) {
            throw new BusinessException("此资质正在团购使用中，无法申请退款！");
        }else {
            groupBuyQualifyMapper.setGroupBuyQualificationStatus(QualifyStatus.QUALIFY_APPLY_REFUND.toString(), groupBuyQualification.getId());
            map.put("succeed",true);
        }
        return map;
    }


    //删除已经放弃，已经退款的团购资质
    @LoginRequired
    @ResponseBody
    @RequestMapping("/account/deleteQualification")
    public Object deleteQualification(@RequestParam(value = "qualificationcode",required = true)String qualificationcode){
        Map<String, Object> map = new HashMap<String, Object>();
        GroupBuyQualification groupBuyQualification= groupBuyQualifyMapper.getGroupBuyQualifyByCode(qualificationcode);
        if(groupBuyQualification==null)
            throw new NotFoundException();
        auth.doCheckUserRight(groupBuyQualification.getUserid());
        if(groupBuyQualification.getStatus().equals(QualifyStatus.QUALIFY_GIVEUPED.toString())||groupBuyQualification.getStatus().equals(QualifyStatus.QUALIFY_REFUNDED.toString())){
            groupBuyQualifyMapper.deleteByCode(groupBuyQualification.getQualificationcode());
            map.put("succeed",true);
        }else {
            throw new BusinessException("此团购资质不能删除！");
        }

        return map;
    }

    //重新申请团购资质
    @RequestMapping(value = "/group/reapplyGroupCertification")
    @LoginRequired
    public String reapplyGroupCertification(@RequestParam(value = "id",required = true)int id,@RequestParam(value = "tag",required = true)String tag, HttpServletRequest request, Map<String, Object> model) throws IOException, TemplateException, ServletException {
        GroupBuyQualification groupBuyQualification =  groupBuyQualifyMapper.getGroupBuyQualifyById(id);
        if(groupBuyQualification==null)
            throw new NotFoundException();
        contractContent(groupBuyQualification.getId(), null, request, model);
        model.put("tag",tag);
        return "groupQualification";
    }

    //--------------------private----------------------------------------------------------------------
    //电子合同
    public void contractContent(@RequestParam(value = "id", required = true)int id,
                                @RequestParam(value = "root", required = false, defaultValue = "null")String root,
                                final HttpServletRequest request,
                                Map<String, Object> model) throws IOException, TemplateException, ServletException {
        final GroupBuyQualification groupBuyQualification = groupBuyQualifyMapper.getGroupBuyQualifyById(id);
        if (groupBuyQualification == null) throw new NotFoundException();
        final Company company = companyMapper.getCompanyByUserid(groupBuyQualification.getUserid());
        if (company == null) throw new NotFoundException();
        String contract = freemarker.render("/contracts/depositContract", new HashMap<String, Object>() {{
            put("contractno", groupBuyQualification.getMarginscode());
            int year=0;
            int month=0;
            int day=0;
            if(groupBuyQualification.getConfirmtime()==null){
                year=groupBuyQualification.getLastupdatetime().getYear();
                month=groupBuyQualification.getLastupdatetime().getMonthValue();
                day=groupBuyQualification.getLastupdatetime().getDayOfMonth();
            }else {
                year=groupBuyQualification.getConfirmtime().getYear();
                month=groupBuyQualification.getConfirmtime().getMonthValue();
                day=groupBuyQualification.getConfirmtime().getDayOfMonth();
            }

            put("createtime", year + " 年 " + month +" 月 "+ day +" 日 ");
            //公司表信息
            put("companyname", company.getName());
            put("companyaddress", company.getAddress());
            put("companylegalpersonname", company.getLegalpersonname());
            put("companyphone", company.getPhone());
            put("companyopeningbank", company.getOpeningbank());
            put("companyaccount", company.getAccount());
            put("companyidentificationnumword", company.getIdentificationnumword());
            put("companyfax", company.getFax());
            put("companyzipcode", company.getZipcode());
            //签订时间
            put("sellsignyear", String.valueOf(LocalDate.now().getYear()));
            put("sellsignmonth", LocalDate.now().getMonthValue());
            put("sellsignday", LocalDate.now().getDayOfMonth());
            put("buysignyear", String.valueOf(LocalDate.now().getYear()));
            put("buysignmonth", LocalDate.now().getMonthValue());
            put("buysignday", LocalDate.now().getDayOfMonth());
            //
            put("localhost", getCurrentURL(request));
        }});
        contractcontent = contract;
        model.put("root", root);
        model.put("id", id);
        model.put("contract", contract);
    }
    public Object getCurrentURL(HttpServletRequest request)
            throws IOException, ServletException {
        return "http://" + request.getServerName() + ":" + request.getServerPort();
    }
}

