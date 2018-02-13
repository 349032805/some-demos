package kitt.core.bl;

import com.mysql.jdbc.StringUtils;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by liuxinjie on 15/11/12.
 */
@Service
public class BuyService {
    @Autowired
    private BuyMapper buyMapper;
    @Autowired
    private PriceLadderMapper priceLadderMapper;
    @Autowired
    private MyInterestMapper myInterestMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private ReservationMapper reservationMapper;

    /**--------------------------------------- 第一部分 供应信息 start ------------------------------------**/
    /**--------------------------------------- 第一部分 供应信息 start ------------------------------------**/

    /**
     * 添加供应信息审核表(supplyverify表)数据
     * @param supplyVerify      供应信息审核表对象
     */
    public boolean addSupplyVerify(SupplyVerify supplyVerify) {
        return buyMapper.addSupplyVerify(supplyVerify) == 1;
    }

    /**
     * 改变供应信息(sellinfo表)状态
     * @param id                供应信息 id
     * @param version           供应信息 版本号
     * @param status            状态
     * @param remarks           备注
     */
    public boolean changeSellInfoStatus(int id, int version, EnumSellInfo status, String remarks) {
        return buyMapper.changeSellInfoStatus(id, version, status, remarks) == 1;
    }

    /**
     * 供应信息(sellinfo表) 修改 可售库存,已售库存 方法
     * @param id                供应信息id
     * @param amount            可售库存 需要减掉的吨数
     */
    public boolean minusSellinfoQuantity(int id, int amount) {
        return amount>0 && buyMapper.changeSellInfoAvailSoldAmount(id, -amount) == 1;
    }

    /**
     * 供应信息(sellinfo表) 修改 可售库存,已售库存
     * @param amount            可售库存 需要减掉的吨数
     * @param id                供应信息id
     */
    public boolean plusSellinfoQuantity(int id, int amount) {
        return amount>0 && buyMapper.changeSellInfoAvailSoldAmount(id, amount) == 1;
    }

    /**
     * 添加我的关注(myinterest表)数据
     * @param myInterest        myInterest 对象
     */
    public boolean addMyInterest(MyInterest myInterest) {
        return myInterestMapper.addMyInterest(myInterest) == 1;
    }

    /**
     * 更新我的关注(myinterest表)
     * @param sid               sid, 如果关注的对象是 sellinfo, sid就是 sellinfo.getId(), 如果关注的对象是demand, sid就是demand.getId()
     * @param id                myInterest id
     * @param type              类型: supply demand shop 等等
     */
    public boolean updateMyInterest(int sid, int id, String type) {
        return myInterestMapper.setMyInterestStatusBySid(sid, id, type) == 1;
    }

    /**
     * 推荐,取消推荐产品(sellinfo表)
     * @param id                供应信息 id
     * @param version           供应信息版本号
     * @param recommendType     推荐类型
     */
    public boolean changeProductRecommendType(int id, int version, String recommendType) {
        return buyMapper.changeRecommentStatus(id, version, recommendType) == 1;
    }

    /**
     * 删除供应信息化验报告(sellinfo表)
     * @param id                供应信息 id
     * @param type              化验报告类型(编号)
     */
    public boolean deleteSellInfoChemicalExam(int id, int type){
        return buyMapper.deleteChemicalExam(id, type) == 1;
    }

    /**
     * 更改供应信息化验报告(sellinfo表)
     * @param id                供应信息 id
     * @param type              化验报告类型(编号)
     * @param chemicalExam      化验报告url字符串
     */
    public boolean changeSellInfoChemicalExam(int id, int type, String chemicalExam){
        return buyMapper.updateChemicalExam(id, type, chemicalExam) == 1;
    }

    /**
     * 发布, 审核未通过时重新发布 供应信息(sellinfo表)
     * @param sellInfo          供应信息 对象
     * @param priceLadderList   新的供应信息 阶梯价List
     * @param jtjLast           新的供应信息 最小的阶梯价
     * @param userid            发布人(供应方) id
     * @param userphone         发布人(供应方) 电话
     */
    @Transactional
    public int addSellInfo(SellInfo sellInfo, List<PriceLadder> priceLadderList, BigDecimal jtjLast, int userid, String userphone) throws SQLExcutionErrorException {
        if((sellInfo.getId() != 0 && !changeSellInfoStatus(sellInfo.getId(), sellInfo.getVersion(), EnumSellInfo.OutOfStack, "审核未通过时重新发布供应信息,将老的供应信息下架")) ||
                buyMapper.addSellinfo(sellInfo) != 1 || buyMapper.setParentIdById(sellInfo.getId(), sellInfo.getId()) != 1 ||
                !AddPriceLadder(priceLadderList, sellInfo.getId(), jtjLast, userid, userphone) ||
                !doAddSupplyReservation(sellInfo, userid)){
            throw new SQLExcutionErrorException();
        }
        return sellInfo.getId();
    }

    /**
     * 修改供应信息(sellinfo表)(审核通过,或者曾经审核通过)
     * @param sellInfo          新的供应信息 对象
     * @param sid               老的供应信息 id
     * @param version           老的供应信息 版本号
     * @param priceLadderList   新的供应信息阶梯价 List
     * @param jtjLast           新的供应信息 最小的阶梯价
     * @param userid            发布人(供应方) userid
     * @param userphone         发布人(供应方) userphone
     */
    @Transactional
    public int addSellinfoForUpdate(SellInfo sellInfo, int sid, int version, List<PriceLadder> priceLadderList, BigDecimal jtjLast, int userid, String userphone) throws SQLExcutionErrorException {
        if(!changeSellInfoStatus(sid, version, EnumSellInfo.OutOfStack, "修改审核通过的供应信息,将老的供应信息下架.") ||
                buyMapper.addSellinfoForUpdate(sellInfo) != 1 ||
                !addSupplyVerify(new SupplyVerify(EnumSellInfo.WaitVerify, LocalDateTime.now(), sellInfo.getId(), userid)) ||
                !AddPriceLadder(priceLadderList, sellInfo.getId(), jtjLast, userid, userphone) ||
                !doAddSupplyReservation(sellInfo, userid)){
            throw new SQLExcutionErrorException();
        }
        return sellInfo.getId();
    }

    /**
     * 修改供应信息(sellinfo表)(从来没有审核通过)
     * @param sellInfo          供应信息对象
     * @param priceLadderList   阶梯价List
     * @param jtjLast           最小的阶梯价
     * @param userid            发布人(供应方) userid
     * @param userphone         发布人(供应方) userphone
     */
    @Transactional
    public void updateSellInfo(SellInfo sellInfo, List<PriceLadder> priceLadderList, BigDecimal jtjLast, int userid, String userphone) throws SQLExcutionErrorException {
        if(buyMapper.updateSellinfo(sellInfo) != 1 || !addSupplyVerify(new SupplyVerify(EnumSellInfo.WaitVerify, LocalDateTime.now(), sellInfo.getId(), userid))
                || !AddPriceLadder(priceLadderList, sellInfo.getId(), jtjLast, userid, userphone) ||
                !doAddSupplyReservation(sellInfo, userid)){
            throw new SQLExcutionErrorException();
        }
    }

    /**
     * 添加预约
     */
    @Transactional
    public boolean doAddSupplyReservation(SellInfo sellInfo, int userid) {
        Reservation reservation = new Reservation();
        reservation.setCustomerId(userid);
        reservation.setAmount(0);
        reservation.setSupplyId(sellInfo.getId());
        reservation.setType(EnumReservation.Release_Supply.value());
        if (sellInfo.getSellerid() != 0 && (sellInfo.getLogistics() == 1 || sellInfo.getFinance() == 1)) {
            Reservation res = reservationMapper.countByUserId(userid, reservation.getSupplyId());
            if (sellInfo.getId() > 0 && res != null) {
                reservation.setId(res.getId());
                return reservationMapper.updateReservation(reservation) == 1;
            } else {
                return reservationMapper.addReservation(reservation) == 1;
            }
        }
        return true;
    }

    /**
     * 添加阶梯价方法(priceladder表)
     * @param priceLadderList   阶梯价List
     * @param id                供应信息 id
     * @param jtjLast           最小的阶梯价
     * @param userid            发布人(供应方) userid
     * @param userphone         发布人(供应方) userphone
     */
    public boolean AddPriceLadder(List<PriceLadder> priceLadderList, int id, BigDecimal jtjLast, int userid, String userphone) {
        priceLadderMapper.deletePriceLadderList(id, 1);
        if(priceLadderList != null && priceLadderList.size() != 0){
            for(PriceLadder priceLadder : priceLadderList){
                priceLadder.setSellinfoid(id);
                priceLadder.setCreatetime(LocalDateTime.now());
                priceLadder.setUserid(userid);
                priceLadder.setUsername(userphone);
                priceLadder.setSquence(priceLadderList.indexOf(priceLadder) + 1);
                if(priceLadderMapper.addPriceLadder(priceLadder) != 1){
                    return false;
                }
            }
            buyMapper.setJtjLastById(jtjLast, id);
        }
        return true;
    }

    /**
     * 审核供应信息(sellinfo表, supplyverify表)
     * @param dealer            交易员对象
     * @param status            状态,审核通过,审核未通过
     * @param sellInfo          供应信息对象
     * @param supplyVerify      供应信息审核表对象
     * @param solvedmanname     审核人(处理此供应的客服)姓名
     */
    @Transactional
    public void verifySellInfo(Admin dealer, EnumSellInfo status, SellInfo sellInfo, SupplyVerify supplyVerify, String solvedmanname) throws SQLExcutionErrorException {
        if(buyMapper.verifySellinfoStatus(dealer != null ? dealer.getPhone() : null, dealer != null ? dealer.getName() : null, dealer != null ? dealer.getId() : null, status, sellInfo.getRemarks(), sellInfo.getId(), sellInfo.getVersion()) != 1
                || buyMapper.verifySupplyInfo(status, solvedmanname, sellInfo.getRemarks(), LocalDateTime.now(), supplyVerify.getId()) != 1){
            throw new SQLExcutionErrorException();
        }
    }

    /**
     * 确认发布供应信息(sellinfo)
     * @param id                id
     * @param version           版本号
     * @param status            状态
     * @param remarks           备注
     * @param userid            发布人userid
     */
    @Transactional
    public void confirmSellInfo(int id, int version, EnumSellInfo status, String remarks, int userid) throws SQLExcutionErrorException {
        if(!changeSellInfoStatus(id, version, status, remarks) ||
                !addSupplyVerify(new SupplyVerify(status, LocalDateTime.now(), id, userid))){
            throw new SQLExcutionErrorException();
        }

    }

    /**--------------------------------------- 第一部分 供应信息 end ---------------------------------------**/
    /**--------------------------------------- 第一部分 供应信息 end ---------------------------------------**/

    /**--------------------------------------- 第二部分 订单 start ----------------------------------------**/
    /**--------------------------------------- 第二部分 订单 start ----------------------------------------**/

    /**
     * 保存支付凭证(payment表)
     * @param payment                    支付凭证对象
     */
    public boolean savePaymentPicture(Payment payment){
        return paymentMapper.addPayment(payment) == 1;
    }

    /**
     * 删除支付凭证(payment表)
     * @param id                         支付凭证 id
     */
    public boolean deletePaymentPicture(int id){
        return paymentMapper.deletePaymentById(id) == 1;
    }

    /**
     * 订单上传完支付凭证, 提交时, 确认支付凭证(payment表)
     * @param id                         支付凭证 id
     */
    public boolean confirmPayment(int id, int version) {
        return id == 0 || paymentMapper.confirmPaymentById(id, version) == 1;
    }

    /**
     * 撤销退货,更改OrderReturn操作(orderreturn表)
     * @param id                          OrderReturn表 id
     */
    public boolean cancelOrderReturn(int id) {
        return orderMapper.setOrderReturnIsCanceled(id) == 1;
    }


    /**
     * 添加订单审核表数据(orderverify表)
     * @param orderVerify                订单审核表对象
     */
    public boolean addOrderVerify(OrderVerify orderVerify) {
        return orderMapper.addOrderVerify(orderVerify) == 1;
    }

    /**
     * 添加订单详细表数据(ordersinfo表)
     * @param ordersInfo                 订单详细表对象
     */
    public boolean addOrdersInfo(OrdersInfo ordersInfo){
        return orderMapper.addOrdersInfo(ordersInfo) == 1;
    }

    /**
     * 添加退货单(订单)表数据(orderreturn表)
     * @param orderReturn                退货单(订单)对象
     */
    public boolean addOrderReturn(OrderReturn orderReturn) {
        return orderMapper.addOrderReturn(orderReturn) == 1;
    }

    /**
     * 审核订单, 设置 money(orders表)
     * @param id                         订单 id
     * @param paidmoney                  已付款
     * @param waitmoney                  代付款
     * @param savemoney                  结余款
     */
    public boolean doVerifyOrderSetMoney(int id, BigDecimal paidmoney, BigDecimal waitmoney, BigDecimal savemoney) {
        return orderMapper.doVerifyOrderSetMoney(id, paidmoney, waitmoney, savemoney) == 1;
    }

    /**
     * 支付凭证审核(payment表)
     * @param paymentList                支付凭证list
     * @param userId                     审核人userId
     * @param userName                   审核人username
     * @return
     */
    public boolean doVerifyPayment(List<Payment> paymentList, int userId, String userName) {
        for(Payment payment : paymentList){
            if (payment.getId() != 0 && paymentMapper.verifyPayment(payment.getId(), payment.getMoney(), userId, userName) != 1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 改变订单状态
     * @param order          订单对象
     * @param status         状态
     * @param remarks        备注信息
     */
    public boolean changeOrderStatus(Order order, EnumOrder status, String remarks){
        return orderMapper.changeOrderStatusByIdVersion(order.getId(), order.getVersion(), status, remarks) == 1;
    }

    /**
     * 改变订单卖家状态
     * @param order          订单对象
     * @param status         状态
     * @param remarks        备注信息
     */
    public boolean changeSellerOrderStatus(Order order, EnumOrder status, String remarks){
        return orderMapper.changeOrderSellerStatusByIdVersion(order.getId(), order.getVersion(), status, remarks) == 1;
    }

    /**
     * 下单 - 自营单(orders表)
     * @param order                      订单对象
     * @param userid                     下单人userid
     * @param usernickname               下单人usernickname
     */
    @Transactional
    public int addZYOrder(Order order, int userid, String usernickname) throws SQLExcutionErrorException {
        int row1 = orderMapper.addZYOrder(order);
        Order newOrder = orderMapper.getOrderById(order.getId());
        OrdersInfo ordersInfo = new OrdersInfo(newOrder.getStatus(), usernickname, userid, newOrder.getId(), newOrder.getOrderid(), "创建订单");
        if(row1 != 1 || !addOrdersInfo(ordersInfo) || !minusSellinfoQuantity(order.getSellinfoid(), order.getAmount())){
            throw new SQLExcutionErrorException();
        }
        return order.getId();
    }

    /**
     * 下单 - 委托单(orders表)
     * @param order                      订单对象
     * @param userid                     下单人userid
     * @param usernickname               下单人usernickname
     */
    @Transactional
    public int addWTOrder(Order order, int userid, String usernickname) throws SQLExcutionErrorException {
        int row1 = orderMapper.addWTOrder(order);
        Order newOrder = orderMapper.getOrderById(order.getId());
        OrdersInfo ordersInfo = new OrdersInfo(newOrder.getStatus(), usernickname, userid, newOrder.getId(), newOrder.getOrderid(), "创建订单");
        if(row1 != 1 || !addOrdersInfo(ordersInfo)) throw new SQLExcutionErrorException();
        return order.getId();
    }

    /**
     * 上传完支付凭证, 提交订单(orders表)
     * @param order            订单对象
     * @param orderVerify      OrderVerify 对象
     * @param userId           买家userId
     * @param userNickname     买家userNickname
     * @param pids             支付凭证1  数组
     */
    @Transactional
    public void payOrderCompleted(Order order, EnumOrder status, OrderVerify orderVerify, int userId, String userNickname, int[] pids, String remarks) throws SQLExcutionErrorException {
        if(!addOrdersInfo(new OrdersInfo(status, userNickname, userId, order.getId(), order.getOrderid(), remarks)) ||
                !changeOrderStatus(order, status, remarks) || !addOrderVerify(orderVerify) ||
                !confirmPayment(pids[0], 0) || !confirmPayment(pids[1], 0) || !confirmPayment(pids[2], 0)){
            throw new SQLExcutionErrorException();
        }
    }

    /**
     * 申请退货订单(orders表, orderreturn表)
     * @param order            订单对象
     * @param userId           买家userid
     * @param userNickname     买家userNickname
     */
    @Transactional
    public void applyReturnGoodsOrder(Order order, EnumOrder status, int userId, String userNickname, String remarks) throws SQLExcutionErrorException {
        if(!addOrdersInfo(new OrdersInfo(status, userNickname, userId, order.getId(), order.getOrderid(), remarks)) ||
                !changeOrderStatus(order, status, remarks) ||
                !addOrderReturn(new OrderReturn(order.getStatus(), order.getId(), order.getOrderid(), userId, userNickname, false))){
            throw new SQLExcutionErrorException();
        }
    }

    /**
     * 撤销退货(orderreturn表)
     * @param order            订单对象
     * @param orderReturn      订单退货表 对象
     * @param userId           买家userid
     * @param userNickname     买家userNickname
     */
    @Transactional
    public void cancelReturnGoodsOrder(Order order, OrderReturn orderReturn, int userId, String userNickname, String remarks) throws SQLExcutionErrorException {
        if(!addOrdersInfo(new OrdersInfo(orderReturn.getLaststatus(), userNickname, userId, order.getId(), order.getOrderid(), remarks)) ||
                !changeOrderStatus(order, orderReturn.getLaststatus(), remarks) ||
                !(order.getSellerstatus().equals(EnumOrder.NULL) || StringUtils.isNullOrEmpty(String.valueOf(order.getSellerstatus())) || (!order.getSellerstatus().equals(EnumOrder.NULL) && !StringUtils.isNullOrEmpty(String.valueOf(order.getSellerstatus())) && changeSellerOrderStatus(orderMapper.getOrderById(order.getId()), EnumOrder.NULL, remarks))) ||
                !cancelOrderReturn(orderReturn.getId())){
            throw new SQLExcutionErrorException();
        }
    }

    /**
     * 改变订单状态(orders表, ordersinfo表), 添加ordersinfo 数据
     * @param order            订单对象
     * @param userid           买家userid
     * @param userNickname     买家userNickname
     */
    @Transactional
    public boolean changeOrderStatus(Order order, EnumOrder status, int userid, String userNickname, String remarks) throws SQLExcutionErrorException {
        if(orderMapper.changeOrderStatusByIdVersion(order.getId(), order.getVersion(), status, remarks) != 1 ||
                !addOrdersInfo(new OrdersInfo(status, userNickname, userid, order.getId(), order.getOrderid(), remarks))){
            throw new SQLExcutionErrorException();
        }
        return true;
    }

    /**
     * 改变订单状态, 加上供应信息库存
     * @param order            订单对象
     * @param userId           操作人userId
     * @param userNickname     操作人userNickname / username
     */
    @Transactional
    public void changeOrderStatusPlusSellInfoQuantity(Order order, boolean isPlusQuantity, EnumOrder status, int userId, String userNickname, String remarks) throws SQLExcutionErrorException {
        if((isPlusQuantity && !plusSellinfoQuantity(order.getSellinfoid(), order.getAmount())) || !addOrdersInfo(new OrdersInfo(status, userNickname, userId, order.getId(), order.getOrderid(), remarks)) ||
                !changeOrderStatus(order, status, remarks)){
            throw new SQLExcutionErrorException();
        }
    }

    /**
     * 改变订单卖家状态, 减去供应信息库存
     * @param order            订单对象
     * @param userId           操作人userId
     * @param userNickname     操作人userNickname / username
     */
    @Transactional
    public void changeSellerOrderStatusMinusSellInfoQuantity(Order order, boolean isMinusQuantity, EnumOrder status, int userId, String userNickname, String remarks) throws SQLExcutionErrorException {
        if((isMinusQuantity && !minusSellinfoQuantity(order.getSellinfoid(), order.getAmount())) || !addOrdersInfo(new OrdersInfo(status, userNickname, userId, order.getId(), order.getOrderid(), remarks)) ||
                !changeSellerOrderStatus(order, status, remarks)){
            throw new SQLExcutionErrorException();
        }
    }

    /**
     * 改变订单卖家状态, 加上供应信息库存
     * @param order            订单对象
     * @param userId           操作人userId
     * @param userNickname     操作人userNickname / username
     */
    @Transactional
    public void changeSellerOrderStatusPlusSellInfoQuantity(Order order, boolean isPlusQuantity, EnumOrder status, int userId, String userNickname, String remarks) throws SQLExcutionErrorException {
        if((isPlusQuantity && !plusSellinfoQuantity(order.getSellinfoid(), order.getAmount())) ||
                !addOrdersInfo(new OrdersInfo(status, userNickname, userId, order.getId(), order.getOrderid(), remarks)) ||
                !changeSellerOrderStatus(order, status, remarks)){
            throw new SQLExcutionErrorException();
        }
    }

    /**
     * 审核支付凭证(payment表, orders表)
     * @param order              订单对象
     * @param paymentList        支付凭证list
     * @param status             订单状态
     * @param userId             操作人id
     * @param userName           操作人登录名
     * @param remarks            备注
     */
    @Transactional
    public void verifyOrderPayment(Order order, List<Payment> paymentList, EnumOrder status, int userId, String userName, String remarks) throws SQLExcutionErrorException {
        BigDecimal money01 = paymentList.get(0).getMoney();
        BigDecimal money02 = paymentList.get(1).getMoney();
        BigDecimal money03 = paymentList.get(2).getMoney();
        BigDecimal paidmoney = money01.add(money02).add(money03).add(order.getPaidmoney());
        BigDecimal waitmoney = (paidmoney.compareTo(order.getTotalmoney()) == -1 ? order.getTotalmoney().subtract(paidmoney):BigDecimal.valueOf(0));
        BigDecimal savemoney = (paidmoney.compareTo(order.getTotalmoney()) == 1 ? paidmoney.subtract(order.getTotalmoney()): BigDecimal.valueOf(0));
        if(!doVerifyOrderSetMoney(order.getId(), paidmoney, waitmoney, savemoney) ||
                orderMapper.verifyOrderPaidCredit(status, remarks, order.getId(), order.getVersion()) != 1 ||
                orderMapper.verifyOrderVerifyPaidCredit(status, userName, LocalDateTime.now(), remarks, order.getId()) != 1 ||
                !addOrdersInfo(new OrdersInfo(status, userName, userId, order.getId(), order.getOrderid(), remarks)) ||
                !doVerifyPayment(paymentList, userId, userName)){
            throw new SQLExcutionErrorException();
        }
    }

    /**--------------------------------------- 第二部分 订单 end -------------------------------------------**/
    /**--------------------------------------- 第二部分 订单 end -------------------------------------------**/

}
