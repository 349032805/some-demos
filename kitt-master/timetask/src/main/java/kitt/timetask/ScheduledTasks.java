package kitt.timetask;

import kitt.core.bl.BuyService;
import kitt.core.config.exception.SQLExcutionErrorException;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.MessageNotice;
import kitt.core.service.SMS;
import kitt.ext.WithLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class ScheduledTasks implements WithLogger {
    @Autowired
    protected TimeTaskMapper timeTaskMapper;
    @Autowired
    protected OrderMapper orderMapper;
    @Autowired
    protected BuyMapper buyMapper;
    @Autowired
    protected SMS sms;
    @Autowired
    protected UserMapper userMapperAutowired;
    @Autowired
    protected PaymentMapper paymentMapper;
    @Autowired
    private GroupBuyQualificationMapper groupBuyQualifyMapper;
    @Autowired
    private GroupBuySupplyMapper groupBuySupplyMapper;
    @Autowired
    private GroupBuyOrderMapper groupBuyOrderMapper;
    @Autowired
    private BuyService buyService;
    @Autowired
    private BidMapper bidMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private DemandMapper demandMapper;


    @Scheduled(fixedRate = 180000)
    public void doCheckOrderStatus() throws Exception {
        //取消24小时不付款的订单
        doCancelWaitPaymentOrders();

        //将过期的供应信息设置位已过期
        doSetSellInfoOutOfDate();

        //删除 超过 1个小时 不确认的供应信息
        doDeleteWaitConfirmedSellInfo();
    }


    /**
     * 取消24小时不付款的订单
     */
    @Transactional
    public void doCancelWaitPaymentOrders() {
        List<Order> orderPayList = orderMapper.getOrderListByStatus(EnumOrder.WaitPayment);
        for (Order order : orderPayList) {
            if (order.getLastupdatetime().plusDays(1).isBefore(LocalDateTime.now())) {
                if (buyMapper.getSellInfoById(order.getSellinfoid()) == null) throw new NotFoundException();
                try {
                    buyService.changeOrderStatusPlusSellInfoQuantity(order, true, EnumOrder.Canceled, 0, "易煤网",  "超过24小时未支付订单，易煤网自动取消订单");
                } catch (SQLExcutionErrorException e) {
                    throw new RuntimeException("超过24小时未支付订单，易煤网自动取消订单 出错, 订单id="+order.getId());
                }
            }
        }
    }

    /**
     * 将过期的供应信息设置位已过期
     */
    public void doSetSellInfoOutOfDate() throws SQLExcutionErrorException {
        List<SellInfo> sellInfoList = buyMapper.getAllSellInfo(EnumSellInfo.VerifyPass);
        for (SellInfo sellInfo : sellInfoList) {
            if (sellInfo.getDeliverytime2().isBefore(LocalDate.now())) {
                buyService.changeSellInfoStatus(sellInfo.getId(), sellInfo.getVersion(), EnumSellInfo.OutOfDate, "已过期，易煤网将该产品下架");
            }
        }
    }

    /**
     * 删除超过 1个小时不确认的供应信息
     */
    public void doDeleteWaitConfirmedSellInfo() throws SQLExcutionErrorException {
        List<SellInfo> sellInfoWaitList = buyMapper.getAllSellInfo(EnumSellInfo.WaitConfirmed);
        for(SellInfo sellInfo : sellInfoWaitList){
            if(sellInfo.getCreatetime().plusHours(1).isBefore(LocalDateTime.now())){
                buyService.changeSellInfoStatus(sellInfo.getId(), sellInfo.getVersion(), EnumSellInfo.OutOfDate, "1个小时不确认的供应信息，易煤网将其删除");
            }
        }
    }

    /*@Scheduled(cron="0 31 9 * *")
    public void sendRemindSMS() throws Exception {
        //待付尾款的订单，提货日前3天，
        List<Order> orderLeftMoneyList = orderMapper.getOrderListByStatus("待付尾款");
        for(int i=0; i<orderLeftMoneyList.size(); i++) {
            Order order = orderLeftMoneyList.get(i);
            if ((order.getDeliverytime2() != null && order.getDeliverytime2().minusDays(4).isBefore(LocalDate.now()) && LocalDate.now().isBefore(order.getDeliverytime2())) || (order.getDeliverytime2() == null && order.getDeliverytime1().minusDays(4).isBefore(LocalDate.now()) && LocalDate.now().isBefore(order.getDeliverytime1()))) {
                if (LocalDateTime.now().getHour() == 9) {
                    User user = userMapper.getUserById(order.getUserid());
                    if(user != null) {
                        String hellowordsClient = "温馨提示：";
                        String contentClient = "贵公司在易煤网上的采购订单，订单编号：" + order.getOrderid() + "，已到达最后付款日，请尽快补交尾款。";
                        String signature = "【易煤网】";
                        sms.send(user.getSecurephone(), contentClient, hellowordsClient, signature); //发送短信验证码
                        String hellowordsDealer = "您好：";
                        String contentDealer = "您的客户，" + order.getSeller() + "在易煤网上的采购订单，订单编号：" + order.getOrderid() + "，已到达最后付款日，请尽快催促买方付款。";
                        sms.send(order.getDealerphone(), contentDealer, hellowordsDealer, signature);
                    }
                }
            }
        }
    }*/


    /**
     * 定时发送短信
     * 下面cron里面参数依次是：second, minute, hour, day, month, weekday
     * @throws Exception
     */
    @Scheduled(cron="0 0 19 12 9 ?")
    public void scheduleTaskUsingCronExpression() throws Exception {
        List<User> userInfoList = userMapper.getAllInfoUserList();
        if(userInfoList != null && userInfoList.size() != 0){
            String content = "尊敬的上官逸枚先生，你已经成功卖出10万吨煤，恭喜您！　　　详情请您登录易煤网（http://yimei180.com）查看。";
            String[] phones = {"18037886166", "15618177577"};
            //String[] phones = {"15618177577"};
            //sms.send("18616319335", content, hellowords, signature);
            for(int i=0; i<phones[i].length(); i++){
                //MessageNotice.CommonMessage.noticeUser(phones[i], content);
                logger().info("---------------" + phones[i] + "------" + content);
            }
        }
    }

    /**
     * 因为0点时系统和数据库的时间不同步,所以改为1点执行
     *
     *1.需求表:demands---检查修改到昨天报价截止的需求记录改为报价结束
     *2.需求表:demands---将超过7天的记录状态改为删除
     *3.我的需求表:mydemands---过了报价截止时间后,交易状态改为匹配中
     *4.我的需求表:mydemands---提货截止时间到后,将匹配中的记录改为交易结束
     *5.报价表:quotes---将到了报价截止日期的记录改为比价中
     *6.报价表:quotes---将过了提货时间截止仍没有中标的改为未中标
     */

    @Scheduled(cron="0 0-10/1 0 * * *")
    public void modifyTradeStatus(){

        //报价截止日  有报价  的需求状态更改  ”匹配中“
        timeTaskMapper.modifyTradestatusTask();
        //报价截止日 没有报价 的需求状态更改为  “已过期”
        timeTaskMapper.modifyTradestatusTask1();
        ////将报价中的，过了提货截止日期的产品状态改为已过期
        timeTaskMapper.modifyTradestatusTask2();

        //将超过7天的记录状态改为删除
        timeTaskMapper.modifyIsdeleteTask();
        //过了报价截止时间后,交易状态改为匹配中
        timeTaskMapper.modifyStatusToMatchingTask();
        timeTaskMapper.modifyStatusToMatchingTask2();

        timeTaskMapper.modifyStatusToTradeOverTask();
        timeTaskMapper.modifyStatusToTradeOverTask2();
        timeTaskMapper.modifyStatusToTradeOverTask3();

        timeTaskMapper.modifyStatusToCompareQuoteTask();
        timeTaskMapper.modifyStatusToNotBidTask();

        //招标定时任务
        publishTenderDate();
        //投标定时任务
        publishMyTenderDate();
    }

    @Scheduled(fixedDelay = 120000)
    public void tenderTask(){
        //招标定时任务
        publishTenderTime();
        //投标定时任务
        publishMyTenderTime();
    }

    /**
     * 招标定时任务
     */
    public  void publishTenderDate(){
        //更新选标阶段为公布结果已完成(当前系统时间大于等于合同执行结束时间)
        timeTaskMapper.updateAllVerifiedAsResultWhenInTime();
    }

    public  void publishTenderTime(){
        //设置发布招标审核通过状态进入投标状态(当前系统时间大于等于投标开始时间)
        timeTaskMapper.updateAllVerifiedAsStartedWhenInTime();
        //更新投标阶段为选标阶段(当前系统时间大于投标结束日期，小于开标时间)
        timeTaskMapper.updateAllVerifiedAsConfirmWhenInTime();
        //更新申请状态为已作废(如果当前时间大于等于开始投标时间)
        timeTaskMapper.updateAllVerifiedAsEditWhenInTime();
        //投标期间无人竞标,发布招标已作废(当前系统时间大于等于选标结束日期)
        timeTaskMapper.updateAllVerifiedAsTenderCancel();
    }

    public void publishMyTenderTime(){
        //更新暂缓投标为错过投标
        timeTaskMapper.updateAllVerifiedAsMissing();
        timeTaskMapper.updateAllVerifiedBidMissing();
        //更新已投标为待选标(不需要支付凭证,当前系统时间大于选标时间)
        timeTaskMapper.updateAllVerifiedAsWaitChooseFree();
        timeTaskMapper.updateAllVerifiedBidWaitChooseFree();
        //更新已投标(支付凭证审核未通过)为待选标(支付凭证审核未通过,当前系统时间大于选标时间)
        timeTaskMapper.updateAllVerifiedAsWaitChoose();
        timeTaskMapper.updateAllVerifiedBidWaitChoose();
        //更新已投标(支付凭证审核通过)为待选标(支付凭证审核通过,当前系统时间大于选标时间)
        timeTaskMapper.updateAllVerifiedAsChoose();
        timeTaskMapper.updateAllVerifiedBidChoose();
        //更新待选标(支付凭证审核未通过)为已作废(当前系统时间大于开标日期)
        timeTaskMapper.updateAllVerifiedAsCancel();
        timeTaskMapper.updateAllVerifiedBidCancel();
        //更新待选标(不需要支付凭)为未中标(当前系统时间大于开标日期)
        //tenderdeclarMapper.updateAllFreeAsFail();
        //tenderdeclarMapper.updateAllFreeBidFail();
    }

    public void publishMyTenderDate(){
        //更新待选标(支付凭证审核通过)为未中标(当前系统时间大于开标日期)
        timeTaskMapper.updateAllVerifiedAsFail();
        timeTaskMapper.updateAllVerifiedBidFail();
    }

    /**
     * 定时发短信 早上8点30分
     */
    @Scheduled(cron="0 30 8 * * *")
    public void senderTenderTimerSMS(){
        //投标截止日期的第二天上午8点给招标人标人发送短信
        waitSelectTenderPacker();
        //供应信息过期当日,发送给客户
        outOfDateSellInfo();
        //需求信息过期第二天,发送给客户
        outOfDateDemand();
    }

    /**
     * 投标截止日期的第二天上午8点给招标人标人发送短信
     */
    public void waitSelectTenderPacker() {
        List<TenderDeclaration> tenderDeclarationList = tenderdeclarMapper.getYesterdayTenderEndList(TenderStatus.TENDER_CHOOSE_CONFIRM.toString());
        for(TenderDeclaration tenderDeclaration : tenderDeclarationList){
            int totalNums = bidMapper.findBidCountByStatusTenderDeclarationId(tenderDeclaration.getId(), TenderStatus.MYTENDER_WAITING_CHOOSE.toString(), TenderStatus.MYTENDER_CHOOSE.toString());
            User user = userMapper.getUserById(tenderDeclaration.getUserid());
            if(user == null) throw new NotFoundException();
            final String content = "您好，客户投标时间已结束，共有 " + totalNums + " 家客户参与投标，请在 " + tenderDeclaration.getContractconenddate() + " 之前审核并进行选标。";
            MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
        }
    }

    /**
     * 供应信息过期当日,发送给客户
     */
    public void outOfDateSellInfo() {
        List<SellInfo> sellInfoList = buyMapper.getSellInfoTodayOutOfDateList();
        for (SellInfo sellInfo : sellInfoList) {
            User user = userMapper.getUserById(sellInfo.getSellerid());
            if (user == null) throw new NotFoundException();
            String createdate = sellInfo.getCreatedate().getYear() + "年" + sellInfo.getCreatedate().getMonthValue() + "月" + sellInfo.getCreatedate().getDayOfMonth() + "日";
            String outOfDateDate = sellInfo.getDeliverytime2().getYear() + "年" + sellInfo.getDeliverytime2().getMonthValue() + "月" + sellInfo.getDeliverytime2().getDayOfMonth() + "日23点59分";
            final String content = "您好，您于 " + createdate + " 在易煤网平台发布的供应信息（" + sellInfo.getPid() + "）将于" + outOfDateDate + "过期，请您及时更新产品信息。如需帮助，请拨打易煤专线4009601180";
            MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
        }
    }

    /**
     * 需求信息过期第二天,发送给客户
     */
    public void outOfDateDemand() {
        List<Demand> demandList = demandMapper.getDemandYestdayOutOfDateList();
        for (Demand demand : demandList) {
            User user = userMapper.getUserById(demand.getUserid());
            if (user == null) throw new NotFoundException();
            String createdate = demand.getReleasetime().getYear() + "年" + demand.getReleasetime().getMonthValue() + "月" + demand.getReleasetime().getDayOfMonth() + "日";
            final String content = "您好，您于 " + createdate + " 在易煤网平台发布的需求信息（" + demand.getDemandcode() + "）已过期，请您及时重新发布。如需帮助，请拨打易煤专线4009601180";
            MessageNotice.CommonMessage.noticeUser(user.getSecurephone(), content);
        }
    }

    //检查团购截止时间，修改状态
    @Scheduled(fixedRate = 3600000)
    public void updateGroupBuyStatus() {
        List<GroupBuySupply> groupBuySupplies= groupBuySupplyMapper.getGroupBuySuppliesByStatus(GroupBuySupplyStatus.GROUP_BUY_SUPPLY_INPROGRESS.toString());
        List<GroupBuySupply> deadlineList=new ArrayList<GroupBuySupply>();
        for(GroupBuySupply gbs:groupBuySupplies){
            if(gbs.getGroupbuyenddate().isBefore(LocalDateTime.now())){
                deadlineList.add(gbs);
            }
        }
        if(deadlineList.size()>0){
            for(GroupBuySupply gs:deadlineList) {
                int succeedconditionmin= BigDecimal.valueOf(gs.getSelledamount()).compareTo(BigDecimal.valueOf(gs.getSupplyamount()).multiply(gs.getSucceedpercentmin()).divide(BigDecimal.valueOf(100)));
                int succeedconditionmax= BigDecimal.valueOf(gs.getSelledamount()).compareTo(BigDecimal.valueOf(gs.getSupplyamount()).multiply(gs.getSucceedpercentmax()).divide(BigDecimal.valueOf(100)));
                if(succeedconditionmin!=-1&&succeedconditionmax!=1) {
                    groupBuySupplyMapper.updateStatusById(gs.getId(), GroupBuySupplyStatus.GROUP_BUY_SUPPLY_FINISH.toString());
                    List<GroupBuyOrder> groupBuyOrders = groupBuyOrderMapper.getOrdersBySupplyId(gs.getId());
                    if (groupBuyOrders != null) {
                        for (GroupBuyOrder bgo : groupBuyOrders) {
                            User user=userMapper.getUserById(bgo.getUserid());
                            if(user!=null)
                                MessageNotice.GROUPOrderSuccess.noticeUser(user.getSecurephone(),bgo.getGroupbuyordercode());
                            groupBuyOrderMapper.updateStatusById(bgo.getId(), OrderStatus.ORDER_FINISH.toString());
                            groupBuyQualifyMapper.updateStatusByCode(bgo.getQualificationcode(), QualifyStatus.QUALIFY_FINISH.toString());
                        }
                    }
                }else {
                    groupBuySupplyMapper.updateStatusById(gs.getId(), GroupBuySupplyStatus.GROUP_BUY_SUPPLY_FAIL.toString());
                    List<GroupBuyOrder> groupBuyOrders = groupBuyOrderMapper.getOrdersBySupplyId(gs.getId());
                    if (groupBuyOrders != null) {
                        for (GroupBuyOrder bgo : groupBuyOrders) {
                            User user=userMapper.getUserById(bgo.getUserid());
                            if(user!=null)
                                MessageNotice.GROUPOrderFail.noticeUser(user.getSecurephone(),bgo.getGroupbuyordercode());
                            groupBuyOrderMapper.updateStatusById(bgo.getId(), OrderStatus.ORDER_FAIL.toString());
                            groupBuyQualifyMapper.updateStatusByCode(bgo.getQualificationcode(), QualifyStatus.QUALIFY_ACTIVE.toString());
                        }
                    }
                }
            }
        }

        //团购活动过期也无人购买状态设置为失败
        List<GroupBuySupply> groupBuyoutofdateSupplies= groupBuySupplyMapper.getGroupBuySuppliesByStatus(GroupBuySupplyStatus.GROUP_BUY_SUPPLY_RELEASE.toString());
        if(groupBuyoutofdateSupplies.size()>0) {
            for (GroupBuySupply groupBuySupply : groupBuyoutofdateSupplies) {
                if (groupBuySupply.getGroupbuyenddate().isBefore(LocalDateTime.now())) {
                    groupBuySupplyMapper.updateStatusById(groupBuySupply.getId(), GroupBuySupplyStatus.GROUP_BUY_SUPPLY_FAIL.toString());
                }
            }
        }
    }


    //删除临时团购订单
    @Scheduled(fixedRate = 3600000)
    public void deleteTempGroupBuyOrder(){
        groupBuyOrderMapper.deleteTempGroupBuyOrder();
    }

}

