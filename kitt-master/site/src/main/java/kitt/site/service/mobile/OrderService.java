package kitt.site.service.mobile;

import freemarker.template.TemplateException;
import kitt.core.bl.BuyService;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.ConfigConsts;
import kitt.core.service.Freemarker;
import kitt.core.service.MessageNotice;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.JsonController.BindResult;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.service.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;



import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xiangyang on 15-6-9.
 */
@Service("mobileOrderService")
@Transactional(readOnly = true)
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SupplyService supplyService;
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private BuyService buyService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private Freemarker freemarker;
    @Autowired
    private Auth auth;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private FileService fileService;
    @Autowired
    private DataBookMapper dataBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private Session session;



    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);


    //保证金百分比:10%
    private static final BigDecimal DEPOSITPERCENT = BigDecimal.valueOf(0.1);

    public Order loadOrderById(int orderId, int userId) {
        Order order = orderMapper.loadOrder(orderId, userId);
        if (order == null) {
            throw new NotFoundException("您查询的订单不存在!");
        }
        return order;
    }

    @Transactional(readOnly = false)
    public void saveOrder(Order order, SellInfo sellInfo, User currentUser) {
        BigDecimal unitPrice = supplyService.matchUnitPrice(sellInfo, order.getAmount());
        double totalPrice = unitPrice.doubleValue() * order.getAmount();
        EnumOrder payType = order.getPaytype() == null ? EnumOrder.PayTheWhole : order.getPaytype();
        //是否是期货
        boolean isfutures = (order.getDeliverytime1().minusDays(7).isAfter(LocalDate.now()) == true ? true : false);
        //待付货款 都是订单总金额
        order.setWaitmoney(BigDecimal.valueOf(totalPrice));
        //总货款
        order.setTotalmoney(BigDecimal.valueOf(totalPrice));
        //根据付款方式计算总货款 区分保证金的方式
        BigDecimal totalMoney = BigDecimal.valueOf(calculateByPayType(totalPrice, payType));
        //单价
        order.setPrice(unitPrice);
        order.setIsfutures(isfutures);
        order.setPaytype(payType);
        order.setSellinfoid(sellInfo.getId());
        order.setPid(sellInfo.getPid());
        order.setSeller(sellInfo.getSeller());
        order.setDeliverymode(sellInfo.getDeliverymode());
        order.setLinkmanphone(currentUser.getSecurephone());
        order.setUserid(currentUser.getId());
        order.setDealername(sellInfo.getDealername());
        order.setDealerphone(sellInfo.getDealerphone());
        order.setRegionId(sellInfo.getRegionId());
        order.setProvinceId(sellInfo.getProvinceId());
        order.setPortId(sellInfo.getPortId());
        order.setDeliveryregion(sellInfo.getDeliveryregion());
        order.setDeliveryprovince(sellInfo.getDeliveryprovince());
        order.setDeliveryplace(sellInfo.getDeliveryplace());
        order.setCreatetime(LocalDateTime.now());
        order.setClienttype(ConfigConsts.Weixin);
        order.setTraderid(sellInfo.getTraderid());
        order.setLinkman(currentUser.getNickname());
        order.setOtherharbour(sellInfo.getOtherharbour());
        //判断库存
        SellInfo currentSellInfo = supplyService.loadSellDeatil(sellInfo.getId());
        if (order.getAmount() > currentSellInfo.getAvailquantity()) {
            logger.error("{}商品可售库存不足,欲购{}吨,当前库存{}吨", currentSellInfo.getPid(), order.getAmount(), currentSellInfo.getAvailquantity());
            throw new BusinessException("商品可售库存不足");
        }
        //type==0是自营的
        if (sellInfo.getType()==0) {
            order.setStatus(EnumOrder.WaitConfirmed);
            order.setOrdertype(EnumOrder.MallOrder);
            orderMapper.addZYOrder(order);
        } else {
            String paymode = dataBookMapper.getDataBookNameByTypeSequence("paymode", sellInfo.getPaymode());
            if(!StringUtils.isEmpty((sellInfo.getPayperiod().toString())) && !(sellInfo.getPayperiod().compareTo(BigDecimal.valueOf(0.0)) == 0)){
                paymode += " " + sellInfo.getPayperiod() + "个月";
            }
            order.setPaymode(paymode);
            order.setStatus(EnumOrder.MakeMatch);
            order.setOrdertype(EnumOrder.OtherOrder);
            //委托单，卖家id字段
            order.setSellerid(sellInfo.getSellerid());
            orderMapper.addWTOrder(order);
            //发送短信通知用户
            MessageNotice.ORDER.noticeUser(session.getUser().getSecurephone(),order.getId());

        }


        //添加订单历史信息
        OrdersInfo ordersInfo =new OrdersInfo();
        ordersInfo.setStatus(order.getStatus());
        ordersInfo.setOperatemanid(currentUser.getId());
        ordersInfo.setOid(order.getId());
        ordersInfo.setOrderid(order.getOrderid());
        ordersInfo.setCreatetime(LocalDateTime.now());
        ordersInfo.setRemarks("创建订单");
        orderMapper.addOrdersInfo(ordersInfo);

    }

    //修改订单
    @Transactional(readOnly = false)
    public void updateOrder(Order order, SellInfo sellInfo, User currentUser) {
        BigDecimal unitPrice = supplyService.matchUnitPrice(sellInfo, order.getAmount());
        double totalPrice = unitPrice.doubleValue()*order.getAmount();
        EnumOrder payType = order.getPaytype() == null ? EnumOrder.PayTheWhole : order.getPaytype();
        //根据付款方式计算总货款
        BigDecimal totalMoney = BigDecimal.valueOf(calculateByPayType(totalPrice, payType));
        order.setPaytype(payType);
        order.setWaitmoney(BigDecimal.valueOf(totalPrice));
        order.setTotalmoney(BigDecimal.valueOf(totalPrice));
        order.setPrice(unitPrice);
        order.setUserid(currentUser.getId());
        orderMapper.updateOrder(order);
    }


    //修改商品库存和已售量
    @Transactional(readOnly = false)
    public void updateSupplyStockAndsolded(Order order) {
       SellInfo sellInfo= buyMapper.lockSupplyById(order.getSellinfoid());
        EnumSellInfo supplyStatus=sellInfo.getStatus();
        if(EnumSellInfo.OutOfDate.equals(supplyStatus)||EnumSellInfo.OutOfStack.equals(supplyStatus)||EnumSellInfo.Canceled.equals(supplyStatus)){
            //验证购买商品是否已过期、或者已下架
            throw new BusinessException("您购买的商品已下架!","/m/mall");
        }
        if(order.getAmount()>sellInfo.getAvailquantity()){
                logger.error("商品库存不足,产品实际库存{},订单购买量{}",sellInfo.getAvailquantity(),order.getAmount());
                throw new BusinessException("商品可售库存不足","/m/mall");
        }
        buyMapper.updateStockAndsolded(order.getSellinfoid(), order.getAmount());
    }


    @Transactional(readOnly = false)
    public void changeOrderStatus(Order order, EnumOrder status) {
        //改变订单状态,version+1
        orderMapper.setOrderStatus(status, order.getId());
        //发送短信通知
        MessageNotice.ORDER.noticeUser(session.getUser().getSecurephone(),order.getId());
        //增加订单历史
        OrdersInfo ordersInfo =new OrdersInfo();
        ordersInfo.setStatus(status);
        ordersInfo.setOperatemanid(order.getUserid());
        ordersInfo.setOid(order.getId());
        ordersInfo.setOrderid(order.getOrderid());
        ordersInfo.setCreatetime(LocalDateTime.now());
        ordersInfo.setRemarks(status.value);
        orderMapper.addOrdersInfo(ordersInfo);
    }



    public BindResult validateOrder(Order order, SellInfo sellInfo, User user) {
        Company company = companyService.loadByUserId(user.getId());
        BindResult result = new BindResult();
        String deliverymode = sellInfo.getDeliverymode();
        EnumSellInfo status=sellInfo.getStatus();
        user = userMapper.getUserById(user.getId());
        if (company == null) {
            result.addError(ConfigConsts.companyWaitComplete, "您的公司信息不完整,请完善!");
        } else if (company.getVerifystatus().equals("审核未通过")|| user.getVerifystatus().equals("审核未通过")) {
            result.addError(ConfigConsts.companynoPass,"您的公司信息审核未通过!");
        } else if (!company.getVerifystatus().equals("审核通过") || !user.getVerifystatus().equals("审核通过")) {
            result.addError(ConfigConsts.companyChecking,"您的公司信息正在审核中,请您耐心等待!");
        }else if (order.getAmount() > sellInfo.getAvailquantity()) {
            result.addError("orderError", "认购吨数不能超过卖家的可销售库存!");
        } else if (order.getAmount() < 50) {
            result.addError("orderError", "认购吨数不能小于50吨!");
        } else if (order.getDeliverytime1() == null) {
            result.addError("orderError", "请输入正确的提货时间!");
        }else if(sellInfo.getSellerid() ==user.getId()){
            //商城发布产品sellerid为0,sellerid是用户id,委托单不能购买自己发布的商品
            result.addError("orderError", "不能购买自己公司发布的产品!");
        }else if(EnumSellInfo.OutOfDate.equals(status)
          ||EnumSellInfo.OutOfStack.equals(status)
          || EnumSellInfo.Canceled.equals(status)
          ||EnumSellInfo.Deleted.equals(status)
          ||EnumSellInfo.WaitShopRun.equals(status)){
            //验证购买商品是否已过期、或者已下架
            throw new BusinessException("您购买的商品已下架!","/m/mall");
        }
        if (result.errors.size() <= 0) {
            //商城订单校验
            if (sellInfo.getSeller().equals("自营")) {
                if (order.getDeliverytime1().isAfter(LocalDate.now().plusDays(7)) && order.getPaytype() == null) {
                    result.addError("orderError", "请选择付款类型!");
                } else if ((deliverymode.equals(DeliveryMode.oneselfPickUp) || deliverymode.equals(DeliveryMode.motorDelivery)) && (null == order.getDeliverytime2())) {
                    //场地自提、车板交货是时间段、开始、结束时间
                    result.addError("orderError", "请选择结束时间!");
                } else if (order.getDeliverytime1().isBefore(sellInfo.getDeliverytime1()) || (null != order.getDeliverytime2() && order.getDeliverytime2().isAfter(sellInfo.getDeliverytime2()))) {
                    result.addError("orderError", "提货时间必须在卖家提货时间内!");
                }
            } else {
                //委托订单校验
                if (order.getDeliverytime1().isBefore(sellInfo.getDeliverytime1())) {
                    result.addError("orderError", "提货时间必须在卖家提货时间内!");
                }
            }
        }
        return result;
    }

    /**
     *
     * @param order
     * @return  根据订单提货方式加载不同的合同模板
     */
    public String loadContractTemplate(Order order) {
        String contractType = null;
        String deliveryMode = order.getDeliverymode();
        if (StringUtils.isEmpty(deliveryMode)) {
            logger.error("{}订单没有交货方式", order.getOrderid());
            throw new RuntimeException("订单信息异常:订单编号" + order.getOrderid());
        }
        if (DeliveryMode.arriveCabin.value.equals(deliveryMode)) {
            contractType = "/m/mContracts/shoreBottom";
        } else if (DeliveryMode.portFlatdepot.value.equals(deliveryMode)) {
            contractType = "/m/mContracts/portUnwinding";
        } else if (DeliveryMode.oneselfPickUp.value.equals(deliveryMode)) {
            if (order.getPaytype().equals(EnumOrder.PayTheWhole)) {
                contractType = "/m/mContracts/spaceDeliveryPayAll";
            } else {
                contractType = "/m/mContracts/spaceDeliveryPayHalf";
            }
        }
            return contractType;
    }

    /**
     *
     * @param order
     * @param user
     * @param templateLocation
     * @param isStandardCon 是否是标准合同，标准合同没有乙方(买方)信息
     * @return
     * @throws IOException
     * @throws TemplateException
     */
    public String generateContractStr(Order order, User user, String templateLocation, boolean isStandardCon) {
        if (StringUtils.isEmpty(templateLocation)) {
            throw new RuntimeException("没有相匹配的合同类型,当前订单交货方式" + order.getDeliverymode());
        }
        SellInfo supply = buyMapper.getSellInfoById(order.getSellinfoid());
        Company company = companyService.loadByUserId(user.getId());
        String contract = null;
        try {
            contract = freemarker.render(templateLocation, new HashMap<String, Object>() {{
                if(!isStandardCon){
                    put("companyname", company.getName());
                    put("companyaddress", company.getAddress());
                    put("companylegalpersonname", company.getLegalpersonname());
                    put("companyphone", company.getPhone());
                    put("companyopeningbank", company.getOpeningbank());
                    put("companyaccount", company.getAccount());
                    put("companyidentificationnumword", company.getIdentificationnumword());
                    put("companyfax", company.getFax());
                    put("companyzipcode", company.getZipcode());
                    //order表信息
                    put("contractno", order.getContractno());
                    put("createtime", order.getCreatetime().toLocalDate());
                    put("orderpid", order.getPid());
                    put("orderamount", order.getAmount());
                    put("orderprice", order.getPrice());
                    put("orderdeliveryregion", order.getDeliveryregion());
                    put("orderdeliveryprovince", order.getDeliveryprovince());
                    put("orderdeliveryplace", order.getPortId() == -1 ? order.getOtherharbour() : order.getDeliveryplace());
                    put("orderdeliverytime1year", String.valueOf(order.getDeliverytime1().getYear()));
                    put("orderdeliverytime1month", order.getDeliverytime1().getMonthValue());
                    put("orderdeliverytime1day", order.getDeliverytime1().getDayOfMonth());
                    if (order.getDeliverymode().equals("场地自提")) {
                        put("orderdeliverytime2year", String.valueOf(order.getDeliverytime2().getYear()));
                        put("orderdeliverytime2month", order.getDeliverytime2().getMonthValue());
                        put("orderdeliverytime2day", order.getDeliverytime2().getDayOfMonth());
                    }
                    if(supply.getNCV()!=0 && supply.getNCV02()!=0){
                      if(supply.getNCV().compareTo(supply.getNCV02())==0){
                        put("sellInfoNCV", supply.getNCV());
                      }else {
                        put("sellInfoNCV", String.valueOf(supply.getNCV()) + "-" + String.valueOf(supply.getNCV02()));
                      }
                    }
                    if(supply.getRS().doubleValue()!=0 && supply.getRS02().doubleValue()!=0){
                        if(supply.getRS().compareTo(supply.getRS02())==0){
                          put("sellInfoRS", String.valueOf(supply.getRS()));
                        }else {
                          put("sellInfoRS", String.valueOf(supply.getRS()) + "-" + String.valueOf(supply.getRS02()));
                        }
                    }
                    put("sellInfoinspectorg", supply.getInspectorg().equals("其它") ? supply.getOtherinspectorg():supply.getInspectorg());
                }
            }});
        } catch (IOException e) {
            throw  new BusinessException("合同生成错误");
        } catch (TemplateException e) {
            throw  new BusinessException("合同生成错误");
        }
        return contract;
    }

    /**
     * 根据付款方式计算总金额,目前总共付款方式，付全款、付总货款的10%保证金
     *
     * @param totalPrice
     * @param payType    PayTheWhole,PayCashDeposit,
     * @return
     */
    public double calculateByPayType(double totalPrice, EnumOrder payType) {

        //付总货款的10%保证金
        if (payType == EnumOrder.PayCashDeposit) {
            return BigDecimal.valueOf(totalPrice).multiply(DEPOSITPERCENT).doubleValue();
        }
        return totalPrice;
    }

    public enum DeliveryMode {
        /**
         * 提货方式为港口平仓、场地自提并且提货时间比当前时间大7天，将有付款方式供选择*
         */
        oneselfPickUp("场地自提"),

        portFlatdepot("港口平仓"),

        /**
         * 车板交货,场地自提 提货时间都是时间段*
         */
        motorDelivery("车板交货"),

        /**
         * 到岸舱底*
         */
        arriveCabin("到岸舱底");

        public String value;

        DeliveryMode(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }


    /**
     * **************************************个人中心买货订单***************************************************
     * 买货订单查询-进行中
     * @param
     * @return PageQueryParam
     */
    public PageQueryParam loadBuyOrderInProgress(PageQueryParam param, int userId) {
        List<Order> orderList = orderMapper.getSixStatusOrdersBuy(userId,
                EnumOrder.WaitPayment, EnumOrder.WaitVerify,
                EnumOrder.VerifyPass, EnumOrder.VerifyNotPass, EnumOrder.WaitBalancePayment,
                EnumOrder.MakeMatch, param.getPagesize(), param.getIndexNum());
        int totalCount = orderMapper.countSixStatusOrdersBuy(userId,
                EnumOrder.WaitPayment, EnumOrder.WaitVerify,
                EnumOrder.VerifyPass, EnumOrder.VerifyNotPass, EnumOrder.WaitBalancePayment,
                EnumOrder.MakeMatch);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(orderList);
        return param;
    }

    /**
     * 买货订单查询-退货中
     *
     * @param
     * @return PageQueryParam
     */
    public PageQueryParam loadBuyOrderReturnGoods(PageQueryParam param, int userId) {
        List<Order> orderList = orderMapper.getOneStatusOrdersBuy(userId, EnumOrder.ReturnGoods, param.getPagesize(), param.getIndexNum());
        int totalCount = orderMapper.countOneStatusOrdersBuy(userId, EnumOrder.ReturnGoods);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(orderList);
        return param;
    }

    /**
     * 买货订单查询-已取消
     *
     * @param
     * @return PageQueryParam
     */
    public PageQueryParam loadBuyOrderCanceled(PageQueryParam param, int userId) {
        List<Order> orderList = orderMapper.getOneStatusOrdersBuy(userId, EnumOrder.Canceled, param.getPagesize(), param.getIndexNum());
        int totalCount = orderMapper.countOneStatusOrdersBuy(userId, EnumOrder.Canceled);
        //orderMapper.countOneStatusOrdersSell(userId, EnumOrder.Canceled, EnumOrder.OtherOrder);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(orderList);
        return param;
    }

    /**
     * 买货订单查询-已完成
     *
     * @param
     * @return PageQueryParam
     */
    public PageQueryParam loadBuyOrderCompleted(PageQueryParam param, int userId) {
        List<Order> orderList = orderMapper.getTwoStatusOrdersBuy(userId, EnumOrder.Completed, EnumOrder.ReturnCompleted, param.getPagesize(), param.getIndexNum());
        int totalCount = orderMapper.countTwoStatusOrdersBuy(userId, EnumOrder.Completed, EnumOrder.ReturnCompleted);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(orderList);
        return param;
    }

    /**
     * ***************************************个人中心卖货订单，卖货订单**************************************************
     * 卖货订单查询-进行中
     *
     * @param
     * @return PageQueryParam
     */
    public PageQueryParam loadSellOrderInProgress(PageQueryParam param, int userId) {
        List<Order> orderList = orderMapper.getOneStatusOrdersSell(userId, EnumOrder.MakeMatch, EnumOrder.OtherOrder, param.getPagesize(), param.getIndexNum());
        int totalCount = orderMapper.countOneStatusOrdersSell(userId, EnumOrder.MakeMatch, EnumOrder.OtherOrder);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(orderList);
        return param;
    }


    /**
     * 卖货订单查询-已完成
     * @param
     * @return PageQueryParam
     */
    public PageQueryParam loadSellOrderCompleted(PageQueryParam param, int userId) {
        List<Order> orderList = orderMapper.getOneStatusOrdersSellIncludeDeleted(userId, EnumOrder.Completed, EnumOrder.OtherOrder, param.getPagesize(), param.getIndexNum());
        int totalCount = orderMapper.countOneStatusOrdersSellIncludeDeleted(userId, EnumOrder.Completed, EnumOrder.OtherOrder);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(orderList);
        return param;
    }

    /**
     * 卖货订单 卖货订单详情信息
     * @param id
     * @param model
     */
    public void orderInfos(int id, Map<String, Object> model) {
        Order order = orderMapper.getOrderById(id);
        if (order == null)
            throw new NotFoundException();
        auth.doCheckUserRight(order.getUserid(), order.getSellerid());
        SellInfo sellInfo = buyMapper.getSellInfoById(order.getSellinfoid());
        if (sellInfo == null)
            throw new NotFoundException();
        Object check7days = 0;
        if (order.getDeliverytime1().minusDays(7).isAfter(LocalDate.now())) {
            check7days = 1;
        }
        model.put("check7days", check7days);
        model.put("orderInfo", order);
        model.put("supply", sellInfo);
    }

    /**
     * 卖货订单查询-已取消
     *
     * @param
     * @return PageQueryParam
     */
    public PageQueryParam loadSellOrderCanceled(PageQueryParam param, int userId) {
        List<Order> orderList = orderMapper.getOneStatusOrdersSellIncludeDeleted(userId, EnumOrder.Canceled, EnumOrder.OtherOrder, param.getPagesize(), param.getIndexNum());
        int totalCount = orderMapper.countOneStatusOrdersSellIncludeDeleted(userId, EnumOrder.Canceled, EnumOrder.OtherOrder);
        int totalPage = totalCount / param.getPagesize();
        totalPage = totalCount % param.getPagesize() == 0 ? totalPage : totalPage + 1;
        param.setTotalCount(totalCount);
        param.setTotalPage(totalPage);
        param.setList(orderList);
        return param;
    }

    /**
     * 取消订单
     *
     * @param
     * @return PageQueryParam
     */
    @Transactional(readOnly = false)
    public void cancelOrder(int orderId, int version, User user) {

        Order order = orderMapper.getOrderByIdAndVersion(orderId, version);
        if (order == null || order.getPid() == null)
            throw new BusinessException("信息已更新，请刷新!");

        if (order.getStatus().equals(EnumOrder.Canceled))
            throw new BusinessException("订单已取消");

        auth.doCheckUserRight(order.getUserid());
        SellInfo sellInfo = buyMapper.getSellInfoById(order.getSellinfoid());
        if (sellInfo == null)
            throw new NotFoundException();

        if (order.getOrdertype().equals(EnumOrder.MallOrder) || (order.getOrdertype().equals(EnumOrder.OtherOrder) && order.getSellerstatus().equals(EnumOrder.Completed))) {
            if(!buyService.plusSellinfoQuantity(order.getSellinfoid(), order.getAmount())){
                auth.doOutputErrorInfo("取消订单出错，订单id=" + order.getId() + ", version=" + order.getVersion());
                throw new BusinessException("取消订单失败，请刷新！");
            }
        }

        if (orderMapper.setOrderStatusByIdVersion(EnumOrder.Canceled, order.getId(), order.getVersion()) != 1) {
            auth.doOutputErrorInfo("取消订单出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("取消订单失败，请刷新页面后重试！");
        }

        if (sellInfo.getAvailquantity() < 0 || sellInfo.getSoldquantity() < 0) {
            auth.doOutputErrorInfo("供应信息可售库存 或 已售库存 为负数，供应信息id=" + sellInfo.getId() + ", 可用库存（availquantity)=" + sellInfo.getAvailquantity() + ", 已售库存（soldquantity)=" + sellInfo.getSoldquantity());
            throw new BusinessException("可用库存不足！");
        }

        orderMapper.addOrdersInfo(new OrdersInfo(EnumOrder.Canceled, user.getNickname(), user.getId(), order.getId(), order.getOrderid(), "取消订单"));

    }

    /**
     * 删除买货订单
     * @param
     * @return
     */
    @Transactional(readOnly = false)
    public void deleteOrder(int orderId, int verion, User user) {
        Order order = orderMapper.getOrderByIdAndVersion(orderId, verion);

        if (order == null || order.getOrderid() == null)
            throw new BusinessException("信息已更新，请刷新!");
        auth.doCheckUserRight(order.getUserid());

        if (orderMapper.setOrderStatusByIdVersion(EnumOrder.Deleted, order.getId(), order.getVersion()) != 1) {
            auth.doOutputErrorInfo("买家删除订单出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("删除订单失败，请刷新页面后重试！");
        }
        orderMapper.addOrdersInfo(new OrdersInfo(EnumOrder.Deleted, user.getNickname(), user.getId(), order.getId(), order.getOrderid(), "删除订单--买货订单"));
    }

    /**
     * 删除卖货订单
     * @param
     * @return
     */
    @Transactional(readOnly = false)
    public void deleteSellOrder(int orderId, int version, User user) {
        Order order = orderMapper.getOrderByIdAndVersion(orderId, version);
        if (order == null || order.getOrderid() == null)
            throw new BusinessException("信息已更新，请刷新!");
        auth.doCheckUserRight(order.getSellerid());
        if (orderMapper.setOrderSellerStatusByIdVersion(EnumOrder.Deleted, order.getId(), order.getVersion()) != 1) {
            auth.doOutputErrorInfo("卖家删除订单出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("删除订单失败，请刷新页面后重试！");
        }
        orderMapper.addOrdersInfo(new OrdersInfo(EnumOrder.Deleted, user.getNickname(), user.getId(), order.getId(), order.getOrderid(), "删除订单--卖货订单"));
    }

    /**
     * 申请退货
     * @param
     * @return
     */
    @Transactional(readOnly = false)
    public void applyReturnGoods(int orderId, int version, User user) {
        Order order = orderMapper.getOrderByIdAndVersion(orderId, version);
        if (order == null || order.getOrderid() == null)
            throw new BusinessException("信息已更新，请刷新!");
        auth.doCheckUserRight(order.getUserid());
        if (!order.getStatus().equals(EnumOrder.ReturnGoods) || !order.getStatus().equals(EnumOrder.Completed)) {
            if (orderMapper.setOrderStatusByIdVersion(EnumOrder.ReturnGoods, order.getId(), order.getVersion()) != 1) {
                auth.doOutputErrorInfo("确认退货出错，订单id=" + order.getId() + ", version=" + order.getVersion());
                throw new BusinessException("确认退货失败，请刷新页面后重试！");
            }
            orderMapper.addOrderReturn(new OrderReturn(order.getStatus(), order.getId(), order.getOrderid(), user.getId(), user.getNickname(), false));
            orderMapper.addOrdersInfo(new OrdersInfo(EnumOrder.ReturnGoods, user.getNickname(), user.getId(), order.getId(), order.getOrderid(), "申请退货"));
        }
    }

    /**
     * 撤销退货
     * @param
     * @return
     */
    @Transactional(readOnly = false)
    public void cancelReturnGoods(int orderId, int version, User user) {
        Order order = orderMapper.getOrderByIdAndVersion(orderId, version);
        if (order == null || order.getOrderid() == null)
            throw new BusinessException("信息已更新，请刷新!");

        OrderReturn orderReturn = orderMapper.getOrderReturnByOrderId(order.getId());
        if (orderReturn == null)
            throw new NotFoundException();

        auth.doCheckUserRight(order.getUserid());

        if (order.getStatus().equals(EnumOrder.ReturnGoods)) {
            if (orderMapper.cancelOrderByIdVersion(orderReturn.getLaststatus(),EnumOrder.NULL, order.getId(), order.getVersion()) != 1) {
                auth.doOutputErrorInfo("取消退货出错，订单id=" + order.getId() + ", version=" + order.getVersion());
                throw new BusinessException("取消退货失败，请刷新页面后重试！");
            }
            orderMapper.addOrdersInfo(new OrdersInfo(orderReturn.getLaststatus(), user.getNickname(), user.getId(), order.getId(), order.getOrderid(), "取消退货"));
            orderMapper.setOrderReturnIsCanceled(orderReturn.getId());
        } else {
            throw new BusinessException("撤销退货失败");
        }
    }


    @Transactional(readOnly = false)
    public String uploadFile(MultipartFile file)throws Exception {
        String picSavePath = fileService.uploadPicture(file);
        return picSavePath;
    }

    public Order getOrderByIdAndVersion(int id,int version){
        return orderMapper.getOrderByIdAndVersion(id, version);
    }

    @Transactional(readOnly = false)
    public void payOrderCompleted(Order order, int pid01, int pid02, int pid03,User user) {
        if(order == null) throw new NotFoundException();
        int[] pids = {pid01, pid02, pid03};
        for(int i=0; i<pids.length; i++) {
            if(pids[i] != 0) {
                if(paymentMapper.confirmPaymentById(pids[i], 0) != 1){
                    auth.doOutputErrorInfo("上传完支付凭证，确认提交出错，订单id=" + order.getId() + ", 支付凭证id=" + pids[i]);
                    throw new BusinessException("提交失败，请刷新网页，重新操作！");
                }
            }
        }
        if(orderMapper.setOrderStatusByIdVersion(EnumOrder.WaitVerify, order.getId(), order.getVersion()) != 1){
            auth.doOutputErrorInfo("上传支付凭证，确认提交出错，订单id=" + order.getId() + ", version=" + order.getVersion());
            throw new BusinessException("提交失败，请刷新网页，重新操作！");
        }
        orderMapper.addOrderVerify(new OrderVerify(EnumOrder.WaitVerify, LocalDateTime.now(), order.getId(), user.getId()));
        orderMapper.addOrdersInfo(new OrdersInfo(EnumOrder.WaitVerify, user.getNickname(), user.getId(), order.getId(), order.getOrderid(), "上传支付凭证完成，待审核"));
    }

    @Transactional(readOnly = false)
    public int uploadPayment(Order order,User user,String picSavePath) throws Exception {
        Payment payment = new Payment(order.getOrderid(), order.getId(), user.getId(), user.getNickname(), picSavePath);
        paymentMapper.addPayment(payment);
        int pid = payment.getId();
        return pid;
    }

    @Transactional(readOnly = false)
    public void deletePayDocumentPic(int id){
        Payment payment = paymentMapper.getPaymentById(id);
        if(payment == null) throw new NotFoundException();
        paymentMapper.deletePaymentById(id);
    }
}
