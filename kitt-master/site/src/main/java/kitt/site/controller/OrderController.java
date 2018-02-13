package kitt.site.controller;

import com.mysql.jdbc.StringUtils;
import freemarker.template.TemplateException;
import kitt.core.bl.BuyService;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.MessageNotice;
import kitt.core.service.PDF;
import kitt.core.util.text.HanYuToPinYin;
import kitt.site.basic.FileDownload;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jack on 15/1/17.
 */
@Controller
@LoginRequired
public class OrderController extends JsonController {
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    protected FileService fileService;
    @Autowired
    private Auth auth;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private HanYuToPinYin hanYuToPinYin;
    @Autowired
    private PriceLadderMapper priceLadderMapper;
    @Autowired
    private BuyService buyService;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private BuyMethod buyMethod;

    public static class MyOrderForm {
        protected int id;
        @NotNull(message = "购买数量不能为空")
        protected int amount;
        @NotNull(message = "提货时间不能为空")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        protected LocalDate deliverytime1;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        protected LocalDate deliverytime2;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public LocalDate getDeliverytime1() {
            return deliverytime1;
        }

        public void setDeliverytime1(LocalDate deliverytime1) {
            this.deliverytime1 = deliverytime1;
        }

        public LocalDate getDeliverytime2() {
            return deliverytime2;
        }

        public void setDeliverytime2(LocalDate deliverytime2) {
            this.deliverytime2 = deliverytime2;
        }

    }

    /**
     * 立即下单，判断填写参数是否正确
     * @param myOrderForm           订单Form
     * @param bindingResult         BindingResult
     * @return                      参数判断结果
     */
    @RequestMapping(value = "/buy/addOrder", method = RequestMethod.POST)
    @ResponseBody
    public Object addMyOrder(@Valid MyOrderForm myOrderForm, BindingResult bindingResult) {
        if (myOrderForm.deliverytime1.isBefore(LocalDate.now().minusDays(1))) {
            bindingResult.rejectValue("", "timeerror", "提货时间不能在今天之前！");
        } else {
            SellInfo sellInfo = buyMapper.getSellInfoById(myOrderForm.id);
            if (sellInfo == null || myOrderForm.deliverytime1 == null) throw new NotFoundException();
            if (!sellInfo.getStatus().equals(EnumSellInfo.VerifyPass)) throw new BusinessException("该商品已下架！");
            if (myOrderForm.deliverytime2 != null && myOrderForm.deliverytime1.isAfter(myOrderForm.deliverytime2)) {
                bindingResult.rejectValue("", "timeerror", "提货时间区间填写错误！");
            } else if (myOrderForm.deliverytime2 != null && myOrderForm.deliverytime2.isAfter(sellInfo.getDeliverytime2())) {
                bindingResult.rejectValue("", "timeover", "提货时间必须在卖家提货时间内！");
            } else if (sellInfo.getDeliverytime1().isAfter(myOrderForm.deliverytime1) || myOrderForm.deliverytime1.isAfter(sellInfo.getDeliverytime2())) {
                bindingResult.rejectValue("", "timeover", "提货时间必须在卖家提货时间内！");
            } else if (sellInfo.getAvailquantity() < myOrderForm.amount){
                bindingResult.rejectValue("", "amountexceed", "认购吨数不能超过卖家的可销售库存！");
            }
        }
        return json(bindingResult);
    }

    /**
     * 确认订单， 添加订单
     * @param myOrderForm                 订单Form
     * @return                            添加是否成功：success， id， version
     */
    @RequestMapping("/mall/confirmOrder")
    @ResponseBody
    @Transactional
    public Object doConfirmOrder(@Valid MyOrderForm myOrderForm) throws IOException, TemplateException, ServletException {
        SellInfo sellInfo = buyMapper.getSellInfoById(myOrderForm.id);
        if (sellInfo == null || myOrderForm.deliverytime1 == null) throw new NotFoundException();
        if (!sellInfo.getStatus().equals(EnumSellInfo.VerifyPass)) throw new BusinessException("该商品已下架！");
        BigDecimal price = sellInfo.getYkj().doubleValue() == 0 ? getJTJByAmontId(myOrderForm.id, myOrderForm.amount) : sellInfo.getYkj();
        boolean isfutures = (myOrderForm.deliverytime1.minusDays(7).isAfter(LocalDate.now()) == true ? true : false);
        BigDecimal totalmoney = BigDecimal.valueOf(price.doubleValue() * myOrderForm.amount);
        Map<String, Object> map = new HashMap<String, Object>();
        boolean success = false;
        String paymode = dataBookMapper.getDataBookNameByTypeSequence("paymode", sellInfo.getPaymode());
        if(sellInfo.getPaymode() == 2) {
            paymode += " " + sellInfo.getPayperiod() + "个月";
        }
        //type==0 自营
        int id = 0;
        if (sellInfo.getType()==0) {
            Order order = new Order(sellInfo.getPid(), myOrderForm.id, EnumOrder.WaitPayment, price, myOrderForm.amount, LocalDateTime.now(), myOrderForm.deliverytime1, myOrderForm.deliverytime2, sellInfo.getSeller(), sellInfo.getDeliveryregion(), sellInfo.getDeliveryprovince(), sellInfo.getDeliveryplace(), sellInfo.getOtherharbour(), sellInfo.getDeliverymode(), totalmoney, totalmoney, session.getUser().getNickname(), session.getUser().getSecurephone(), session.getUser().getId(), EnumOrder.MallOrder, EnumOrder.PayTheWhole, isfutures, sellInfo.getSellerid(), sellInfo.getTraderid(), sellInfo.getDealername(), sellInfo.getDealerphone(), sellInfo.getRegionId(), sellInfo.getProvinceId(), sellInfo.getPortId(), 1, paymode);
            try {
                id = buyService.addZYOrder(order, session.getUser().getId(), session.getUser().getNickname());
                map.put("version", orderMapper.getOrderById(id).getVersion());
                map.put("id", id);
                success = true;
                MessageNotice.ORDER.noticeUser(session.getUser().getSecurephone(), id);
            } catch (SQLExcutionErrorException e) {
                throw new BusinessException("下单失败, 请刷新页面重新下单! ");
            }
        } else {
            Order order = new Order(sellInfo.getPid(), myOrderForm.id, EnumOrder.MakeMatch, price, myOrderForm.amount, LocalDateTime.now(), myOrderForm.deliverytime1, myOrderForm.deliverytime2, sellInfo.getSeller(), sellInfo.getDeliveryregion(), sellInfo.getDeliveryprovince(), sellInfo.getDeliveryplace(), sellInfo.getOtherharbour(), sellInfo.getDeliverymode(), totalmoney, session.getUser().getNickname(), session.getUser().getSecurephone(), session.getUser().getId(), EnumOrder.OtherOrder, sellInfo.getSellerid(), sellInfo.getTraderid(), sellInfo.getDealername(), sellInfo.getDealerphone(), sellInfo.getRegionId(), sellInfo.getProvinceId(), sellInfo.getPortId(), 1, paymode);
            try {
                id = buyService.addWTOrder(order, session.getUser().getId(), session.getUser().getNickname());
                success = true;
                MessageNotice.ORDER.noticeUser(session.getUser().getSecurephone(), id);
            } catch (SQLExcutionErrorException e) {
                e.printStackTrace();
            }
        }
        map.put("success", success);
        return map;
    }

    //获取阶梯价
    public BigDecimal getJTJByAmontId(int id, int amount) {
        List<PriceLadder> pricelist = priceLadderMapper.getPriceLadderListBySellinfoId(id);
        if (pricelist.size() == 0) throw new NotFoundException();
        BigDecimal price = new BigDecimal(0);
        for (int i = 0; i < pricelist.size(); i++) {
            if (amount <= pricelist.get(i).getAmount2() && amount > pricelist.get(i).getAmount1()) {
                price = pricelist.get(i).getPrice();
            }
        }
        if (price.doubleValue() == 0) {
            price = pricelist.get(pricelist.size() - 1).getPrice();
        }
        return price;
    }

    /**
     * 查看标准合同
     * @param id                     供应信息id
     */
    @RequestMapping("/mall/standardcontract")
    public String doShowStandardContract(@RequestParam(value = "id", required = true)int id,
                                         HttpServletRequest request, Map<String, Object> model) throws IOException, TemplateException, ServletException {
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if(sellInfo == null) throw new NotFoundException();
        getStandardContractObjects(sellInfo, request, model);
        return "showStandardContract";
    }

    /**
     * 下载标准合同
     */
    @RequestMapping("/downloadStandardContract")
    public void downStandardDZHT(@RequestParam(value = "id", required = true)int id,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if(sellInfo == null) throw new NotFoundException();
        String filename = hanYuToPinYin.HanYuToPinYinFirstLetter(sellInfo.getDeliverymode(), true) + "_" + LocalDate.now() + ".pdf";
        File target = fileService.getDownloadFileByFileName(filename);
        if(!target.exists()) {
            File file = PDF.create(buyMethod.standardContractContent(sellInfo, request));
            fileService.copyToDownload(file, filename);
        }
        FileDownload.doDownloadFile(target, response);
    }

    /**
     * 查看标准合同
     * @param sellInfo               供应信息对象
     */
    public void getStandardContractObjects(SellInfo sellInfo, HttpServletRequest request, Map<String, Object> model) throws ServletException, TemplateException, IOException {
        model.put("contract", buyMethod.standardContractContent(sellInfo, request));
    }

    /**
     * 下载正式电子合同
     * @param id                 订单id
     */
    @RequestMapping("/downloadContract")
    public void downDZHT(@RequestParam(value = "id", required = true)int id,
                         HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        Order order = orderMapper.getOrderById(id);
        if(order == null) throw new NotFoundException();
        auth.doCheckUserRight(order.getUserid());
        String filename = order.getContractno() + ".pdf";
        File target = fileService.getDownloadFileByFileName(filename);
        if(!target.exists()) {
            File file = PDF.create(buyMethod.contractContent(id, request));
            fileService.copyToDownload(file, filename);
        }
        FileDownload.doDownloadFile(target, response);
    }

    /**
     * 查看正式合同
     * @param id                     订单id
     * @param version                订单版本号
     */
    @RequestMapping("/account/showContract")
    public String showContract(@RequestParam(value = "orderId", required = true)int id,
                               @RequestParam(value = "version", required = true)int version,
                               HttpServletRequest request, Map<String, Object> model) throws IOException, TemplateException, ServletException {
        getContractObjects(id, version, request, model);
        return "showContract";
    }

    /**
     * 查看正式合同
     * @param id                      订单id
     */
    public void getContractObjects(int id, int version, HttpServletRequest request, Map<String, Object> model) throws ServletException, TemplateException, IOException {
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException();
        auth.doCheckUserRight(order.getUserid());
        SellInfo sellInfo = buyMapper.getSellInfoById(order.getSellinfoid());
        Company company = companyMapper.getCompanyByUserid(order.getUserid());
        if (sellInfo == null || company == null) throw new NotFoundException();
        model.put("order", order);
        model.put("company", company);
        model.put("sellInfo", sellInfo);
        model.put("contract", buyMethod.contractContent(id, request));
    }

    //去付款
    @RequestMapping("/mall/payment")
    public String goPayment(@RequestParam(value = "id", required = true)int id,
                            @RequestParam(value = "version", required = true)int version,
                            Map<String, Object> model) {
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException("该订单不存在，或者已经被更改！");
        auth.doCheckUserRight(order.getUserid());
        if (order.getStatus().equals(EnumOrder.WaitPayment) || order.getStatus().equals(EnumOrder.WaitBalancePayment) || order.getStatus().equals(EnumOrder.VerifyNotPass)) {
            model.put("order", order);
            return "payment";
        }
        return "/account/getMyOrders";
    }

    /**
     * 保存支付凭证
     * @param id                         订单 id
     * @param version                    订单 version
     * @param file                       支付凭证文件对象
     */
    @RequestMapping(value = "/saveCertificationPic", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public Object savePayDocumentPic(@RequestParam(value = "id", required = true)int id,
                                     @RequestParam(value = "version", required = true)int version,
                                     @RequestParam("file") MultipartFile file,
                                     HttpServletResponse response) throws Exception{
        if(file.getSize() / 1000 / 1000 <= 10) {
            Order order = orderMapper.getOrderByIdAndVersion(id, version);
            if (order == null) throw new NotFoundException();
            auth.doCheckUserRight(order.getUserid());
            Map<String, Object> map = new HashMap<String, Object>();
            response.setContentType("text/html");
            String picSavePath = fileService.uploadPicture(file);
            if (StringUtils.isNullOrEmpty(picSavePath)) throw new NotFoundException();
            Payment payment = new Payment(order.getOrderid(), order.getId(), session.getUser().getId(), session.getUser().getNickname(), picSavePath);
            if(!buyService.savePaymentPicture(payment)){
                throw new BusinessException("支付凭证保存失败, 请重新上传! ");
            }
            int pid = payment.getId();
            map.put("picSavePath", picSavePath);
            map.put("pid", pid);
            return map;
        }
        return null;
    }

    /**
     * 删除支付凭证
     * @param id                       支付凭证 id
     */
    @RequestMapping(value = "/deleteCertificationPic", method = RequestMethod.POST)
    @ResponseBody
    @LoginRequired
    public void deletePayDocumentPic(@RequestParam(value="id", required = true)int id){
        Payment payment = paymentMapper.getPaymentById(id);
        if(payment == null) throw new NotFoundException();
        buyService.deletePaymentPicture(id);
    }

    /**
     * 订单详细页面
     * @param id                         订单 id
     * @param version                    订单 version
     * @param reqsource                  来源, 供前台定位从哪里进入这个页面使用
     */
    @RequestMapping("/mall/OrderInfo")
    public String doGetMyOrders(@RequestParam(value = "id", required = true)int id,
                                @RequestParam(value = "version", required = true)int version,
                                String reqsource, HttpServletRequest request,
                                Map<String, Object> model) throws ServletException, TemplateException, IOException {
        orderInfos(id, version, reqsource, request, model);
        return "orderInfo";
    }

    public void orderInfos(int id, int version, String reqsource, final HttpServletRequest request, Map<String, Object> model) throws ServletException, TemplateException, IOException {
        Order order = orderMapper.getOrderByIdAndVersion(id, version);
        if (order == null) throw new NotFoundException("该订单不存在，或者已经被更改！");
        auth.doCheckUserRight(order.getUserid(), order.getSellerid());
        SellInfo sellInfo = buyMapper.getSellInfoById(order.getSellinfoid());
        if (sellInfo == null) throw new NotFoundException();
        Object check7days = 0;
        if (order.getDeliverytime1().minusDays(7).isAfter(LocalDate.now())) {
            check7days = 1;
        }
        if(session.getUser().getId() == order.getUserid()){
            model.put("userType", 0);
        } else if(session.getUser().getId() == order.getSellerid()){
            model.put("userType", 1);
        }
        model.put("check7days", check7days);
        model.put("orderInfo", order);
        model.put("sellInfo", sellInfo);
        model.put("reqsource", reqsource);
        model.put("contract", buyMethod.contractContent(id, request));
    }

    /**
     * 获取阶梯价价格
     * @param id                        供应信息 id
     * @param amount                    购买数量
     */
    @RequestMapping("/getjtj")
    @ResponseBody
    public Object getJTJ(int id, int amount) {
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if (sellInfo == null) throw new NotFoundException();
        return getJTJByAmontId(amount, id);
    }

    /**
     * 检查可售库存是否充足
     * @param id                      供应信息id
     * @param amount                  购买数量, 吨数
     */
    @RequestMapping("/checkAvailableAmount")
    @ResponseBody
    public Object getAvailAmount(@RequestParam(value = "id", required = true)int id,
                                 @RequestParam(value = "amount", required = false, defaultValue = "1")int amount){
        SellInfo sellInfo = buyMapper.getSellInfoById(id);
        if(sellInfo == null) throw new NotFoundException();
        if (!sellInfo.getStatus().equals(EnumSellInfo.VerifyPass)) throw new BusinessException("该商品已下架！");
        return sellInfo.getAvailquantity() < amount ? false : true;
    }

    /**
     * 检查订单状态
     * @param id                      订单 id
     * @param status                  订单状态
     * @return
     */
    @RequestMapping("/mall/checkOrderStatus")
    @ResponseBody
    public Object doCheckOrderStatus(@RequestParam(value = "id", required = true)int id,
                                     @RequestParam(value = "status", required = true)EnumOrder status){
        Order order = orderMapper.getOrderById(id);
        if(order == null) throw new NotFoundException("此订单不存在！");
        Map<String, Object> map = new HashMap<>();
        boolean success = false;
        if(order.getStatus().equals(status)){
            success = true;
        } else if(order.getStatus().equals(EnumOrder.Canceled)){
            map.put("error", "canceled");
        }
        map.put("success", success);
        return map;
    }

    /**
     * 检查 待付款订单的状态
     * @param id                      订单 id
     */
    @RequestMapping("/mall/checkPaymentOrderStatus")
    @ResponseBody
    public boolean doCheckPaymentOrderStatus(@RequestParam(value = "id", required = true)int id){
        Order order = orderMapper.getOrderById(id);
        if(order == null) throw new NotFoundException("此订单不存在！");
        if(order.getStatus().equals(EnumOrder.WaitPayment) || order.getStatus().equals(EnumOrder.WaitBalancePayment) || order.getStatus().equals(EnumOrder.VerifyNotPass)) {
            return true;
        } else{
            return false;
        }
    }

    /**
     * 判断下单时间距离今天是否超过7天
     * @param deliverytime1           提货时间(如果是时间段, 为第一个提货时间)
     */
    @RequestMapping("/check7days")
    @ResponseBody
    public Object doCheckDays(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "deliverytime1", required = true) LocalDate deliverytime1) {
        if (!deliverytime1.minusDays(7).isAfter(LocalDate.now())) return true;
        return false;
    }

}



