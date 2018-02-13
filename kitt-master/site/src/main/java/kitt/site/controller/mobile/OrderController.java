package kitt.site.controller.mobile;

import com.google.common.collect.Maps;
import kitt.core.domain.*;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import kitt.site.basic.annotation.LoginRequired;
import kitt.site.basic.exception.BusinessException;
import kitt.site.service.Auth;
import kitt.site.service.FileService;
import kitt.site.service.mobile.CompanyService;
import kitt.site.service.mobile.OrderService;
import kitt.site.service.mobile.SupplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiangyang on 15-6-9.
 */
@Controller("mobileOrderController")
@RequestMapping("/m")
@LoginRequired
public class OrderController extends JsonController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private SupplyService supplyService;
    @Autowired
    private Auth auth;
    @Autowired
    protected FileService fileService;
    @Autowired
    private CompanyService companyService;

    private Logger logger = LoggerFactory.getLogger(OrderController.class);

    /**
     * 请求参数名:
     * sellinfoid 供应id
     * amount:认购吨数
     * paytype: PayTheWhole, PayCashDeposit
     * deliverytime1,
     * deliverytime2
     * <p>
     * responseData:{success:true,orderId:332}
     * {success:false,errors:[{field:companyStatus,errMsg:请完善公司信息}]}
     *
     * @param order
     * @param user
     * @return
     */
    //商城订单下单，下单成功跳转到订单确认页面
    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    @LoginRequired
    public @ResponseBody Object addOrder(Order order, @CurrentUser User user) {
        SellInfo sellInfo = supplyService.loadSellDeatil(order.getSellinfoid());
        Map<String, Object> maps = Maps.newHashMap();
        BindResult result = orderService.validateOrder(order, sellInfo, user);
        if (result.errors.size() <= 0) {
            orderService.saveOrder(order, sellInfo, user);
            maps.put("success", true);
            maps.put("orderId", order.getId());
            //isMallOrder true为商城订单, fasle为委托单
            maps.put("isMallOrder",sellInfo.getType() == 0 ? true:false);
            return maps;
        }
        return json(result);
    }
    @RequestMapping(value = "/orderSuccess",method = RequestMethod.GET)
    public String delegationOrderSuccess(){
        return "/m/orderSuccess";
    }

    //订单确认页面
    @RequestMapping(value = "/orderDetail/{orderId}", method = RequestMethod.GET)
    @LoginRequired
    public String orderDeatil(@PathVariable("orderId") int orderId, @CurrentUser User user, Model model) {
        Order order = orderService.loadOrderById(orderId, user.getId());
        //如果等于待签合同不能修改，到系统繁忙页面
        if (order.getStatus().equals(EnumOrder.WaitSignContract)) {
            logger.warn("待签合同的订单不能修改,订单编号为{}", orderId);
            return "/m/error/busy";
        } else {
            loadOrder(orderId, user, model);
        }
        return "/m/orderDetail";
    }

    //打开修改订单页面
    @RequestMapping(value = "/orderUpdate/{orderId}", method = RequestMethod.GET)
    @LoginRequired
    public String updateOrder(@PathVariable("orderId") int orderId, @CurrentUser User user, Model model) {
        Order order = orderService.loadOrderById(orderId, user.getId());
        //如果等于待签合同不能修改，到系统繁忙页面
        if (order.getStatus().equals(EnumOrder.WaitSignContract)) {
            logger.debug("待签合同的订单不能修改,订单编号为{}", orderId);
            return "/m/error/busy";
        } else {
            SellInfo sellInfo = supplyService.loadSellDeatil(order.getSellinfoid());
            model.addAttribute("orderInfo", order);
            model.addAttribute("supply", sellInfo);
        }
        return "/m/orderUpdate";
    }

    //订单修改
    @RequestMapping(value = "/updateOrder", method = RequestMethod.POST)
    @LoginRequired
    public @ResponseBody Object doUpdateOrder(Order order, @CurrentUser User user) {
        //如果订单不存在抛异常
        orderService.loadOrderById(order.getId(),user.getId());
        SellInfo sellInfo = supplyService.loadSellDeatil(order.getSellinfoid());
        BindResult result = orderService.validateOrder(order, sellInfo, user);
        if (result.errors.size() <= 0) {
            //修改订单
            orderService.updateOrder(order, sellInfo, user);
            result.addAttribute("orderId", order.getId());
        }
        return json(result);
    }

    //商城订单确定认购,到支付凭证页面(验证订单状态)
    @RequestMapping(value = "/confirmOrder/{orderId}", method = RequestMethod.GET)
    @LoginRequired
    public @ResponseBody Object confirmOrder(@PathVariable("orderId") int orderId, @CurrentUser User user,BindResult result) {
        Order order = orderService.loadOrderById(orderId, user.getId());
        EnumOrder status = order.getStatus();
        if (status.equals(EnumOrder.WaitConfirmed)) {
            //减库存，已售量
            orderService.updateSupplyStockAndsolded(order);
            //改变订单状态&增加订单历史信息
            orderService.changeOrderStatus(order, EnumOrder.WaitPayment);
        }
        return json(result);
    }

    //验证是否可以到支付凭证页面
    @RequestMapping(value = "/validatePayOrder/{orderId}", method = RequestMethod.GET)
    @LoginRequired
    public @ResponseBody Object payOrder(@PathVariable("orderId") int orderId, @CurrentUser User user, BindResult result) {
        Order order = orderService.loadOrderById(orderId, user.getId());
        EnumOrder status = order.getStatus();
        if(EnumOrder.Canceled.equals(status)){
            throw new BusinessException("订单已过期，请重新下单!","/m/mall");
        }
        if (EnumOrder.WaitPayment.equals(status) || EnumOrder.WaitBalancePayment.equals(status) || EnumOrder.VerifyNotPass.equals(status)) {
            //待付款、待付尾款、审核未通过 可以跳转到支付凭证页面，其它的情况给用户提示
            result.addAttribute("orderId",order.getId());
            return json(result);
        }else {
            throw new BusinessException("您的订单状态已更改!", "/m/account/buyOrder");
        }
    }

    //打开支付凭证
    @RequestMapping(value = "/payOrder/{orderId}",method = RequestMethod.GET)
    @LoginRequired
    public String openPayOrder(@PathVariable("orderId") int orderId, @CurrentUser User user,Model model){
        Order order = orderService.loadOrderById(orderId, user.getId());
        SellInfo sellInfo = supplyService.loadSellDeatil(order.getSellinfoid());
        model.addAttribute("orderInfo", order);
        model.addAttribute("supply", sellInfo);
        return "/m/uploadPayCertification";
    }

    @RequestMapping(value = "/standardContract/{orderId}", method = RequestMethod.GET)
    @LoginRequired
    public String standardContract(@PathVariable("orderId") int orderId,@CurrentUser User user, Model model) {
        Order order = orderService.loadOrderById(orderId, user.getId());
        //生成合同
        String contractTemplate=orderService.loadContractTemplate(order);
        model.addAttribute("contractStr",orderService.generateContractStr(order, user, contractTemplate, true));
        return "m/contract";
    }


    //查看合同
    @RequestMapping(value = "/lookoverContract/{orderId}", method = RequestMethod.GET)
    @LoginRequired
    public String loadConstract(@PathVariable("orderId") int orderId, @CurrentUser User user, Model model){
        Order order = orderService.loadOrderById(orderId, user.getId());
        Company company = companyService.loadByUserId(user.getId());
        //生成合同
        String contractTemplate=orderService.loadContractTemplate(order);
        model.addAttribute("contractStr", orderService.generateContractStr(order, user, contractTemplate, false));
        model.addAttribute("orderId", orderId);
        model.addAttribute("orderStatus", order.getStatus());
        model.addAttribute("contractno", order.getContractno());
        model.addAttribute("createtime", order.getCreatetime().toLocalDate());
        model.addAttribute("companyname", company.getName());
        model.addAttribute("companyaddress", company.getAddress());
        model.addAttribute("companylegalpersonname", company.getLegalpersonname());
        return "m/contract";
    }

    //确定签订合同
//    @RequestMapping(value = "/order/confirmSignContract/{orderId}",method = RequestMethod.GET)
//    public @ResponseBody Object confirmSignContract(@PathVariable("orderId")int orderId,@CurrentUser User user,BindResult result){
//        Order order = orderService.loadOrderById(orderId, user.getId());
//        if(EnumOrder.Canceled.equals(order.getStatus())){
//                throw new BusinessException("订单已过期，请重新下单!","/m/mall");
//        }
//        if(order.getStatus().equals(EnumOrder.WaitSignContract)){
//            orderService.changeOrderStatus(order, EnumOrder.WaitPayment);
//        }
//        result.addAttribute("orderId",order.getId());
//        return json(result);
//    }



    //保存支付凭证
    @ResponseBody
    @LoginRequired
    @RequestMapping(value = "/saveCertificationPic", method = RequestMethod.POST)
    public Object savePayDocumentPic(@RequestParam(value = "id", required = true) int id,
                                     @RequestParam(value = "version", required = true) int version,
                                     @RequestParam("files") MultipartFile[] files,
                                     HttpServletResponse response,
                                     @CurrentUser User user) throws Exception {
        Map map = new HashMap<>();
        boolean success = true;
        Order order = orderService.getOrderByIdAndVersion(id, version);
        String message = null;
        if (order == null) {
//            map.put("message", "支付已过期");
            message = "支付已过期";
            success=false;
        }else if(map.isEmpty() && order.getStatus().equals(EnumOrder.WaitPayment) || order.getStatus().equals(EnumOrder.WaitBalancePayment) || order.getStatus().equals(EnumOrder.VerifyNotPass)) {
            auth.doCheckUserRight(order.getUserid());
            response.setContentType("text/html");
            int[] pidlist = {0, 0, 0};

            for (int i = 0; i < files.length; i++) {
                if (files[i].getSize() / 1000 / 1000 > 10) {
                    success = false;
//                    map.put("message", "图片尺寸过大");
                    message = "图片尺寸过大";
                    break;
                }
                if (files[i] != null && files[i].getSize() > 0) {
                    String picSavePath = fileService.uploadPictureToUploadDir(files[i]);
                    pidlist[i] = orderService.uploadPayment(order, user, picSavePath);
                }
            }
            orderService.payOrderCompleted(order, pidlist[0], pidlist[1], pidlist[2], user);
        }else {
//            map.put("message", "支付已过期");
            message = "支付已过期";
            success=false;
        }
        map.put("success", success);
        map.put("message", message);
        return map;
    }

    //删除支付凭证
    @ResponseBody
    @LoginRequired
    @RequestMapping(value = "/deleteCertificationPic", method = RequestMethod.POST)
    public Object deletePayDocumentPic(@RequestParam(value = "id", required = true) int id) {
        orderService.deletePayDocumentPic(id);
        return true;
    }

    private void loadOrder(int orderId, User user, Model model) {
        Order order = orderService.loadOrderById(orderId, user.getId());
        SellInfo sellInfo = supplyService.loadSellDeatil(order.getSellinfoid());
        model.addAttribute("orderInfo", order);
        model.addAttribute("supply", sellInfo);
    }

    //上传支付凭证跳转到成功页面
    @RequestMapping(value = "/payOrder/success", method = RequestMethod.GET)
    public String paySuccess() {
        return "/m/releaseSuccessPay";
    }
}
