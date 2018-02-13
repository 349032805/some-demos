package kitt.admin.controller;

import java.math.BigDecimal;
import kitt.admin.annotation.Authority;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.FileStore;
import kitt.core.service.MessageNotice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fanjun on 15-1-27.
 */
@Controller
public class TimeTaskController {

    @Autowired
    protected TimeTaskMapper timeTaskMapper;
    @Autowired
    private GroupBuyQualificationMapper groupBuyQualifyMapper;
    @Autowired
    private GroupBuySupplyMapper groupBuySupplyMapper;
    @Autowired
    private GroupBuyOrderMapper groupBuyOrderMapper;
    @Autowired
    private UploadFileByUserMapper uploadFileByUserMapper;
    @Autowired
    private FileStore fileStore;
    @Autowired
    private UserMapper userMapper;

    //手动触发定时任务
    @RequestMapping("/startTimeTasks")
    @Authority(role = AuthenticationRole.Operation)
    @Authority(role = AuthenticationRole.Admin)
    @ResponseBody
    public Object startTimeTasks(){

        timeTaskMapper.modifyTradestatusTask();
        timeTaskMapper.modifyTradestatusTask1();
        timeTaskMapper.modifyTradestatusTask2();
        timeTaskMapper.modifyIsdeleteTask();
        timeTaskMapper.modifyStatusToMatchingTask();
        timeTaskMapper.modifyStatusToMatchingTask2();
        timeTaskMapper.modifyStatusToTradeOverTask();
        timeTaskMapper.modifyStatusToTradeOverTask2();
        timeTaskMapper.modifyStatusToTradeOverTask3();
        timeTaskMapper.modifyStatusToCompareQuoteTask();
        timeTaskMapper.modifyStatusToNotBidTask();
        //团购定时任务
        updateGroupBuyStatus();
        deleteTempGroupBuyOrder();
        //招标定时任务
        publishTender();
        //投标定时任务
        publishMyTender();


        boolean success = true;
        Map map = new HashMap();
        map.put("success",success);
        return map;
    }

    public void updateGroupBuyStatus(){
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
    public void deleteTempGroupBuyOrder(){
        groupBuyOrderMapper.deleteTempGroupBuyOrder();
    }

    //删除临时文件夹的图片
    @RequestMapping("/deleteTempPic")
    @ResponseBody
    public Object deleteTempPic() throws Exception{
        List<UploadFileByUser> list = uploadFileByUserMapper.getOutofTimeList(2);
        for(UploadFileByUser u :list){
            fileStore.deleteTempPic(u.getFilepath());
        }

        boolean success = true;
        Map map = new HashMap();
        map.put("success",success);
        return map;
    }


    public  void publishTender(){
        //设置发布招标审核通过状态进入投标状态(当前系统时间大于等于投标开始时间)
        timeTaskMapper.updateAllVerifiedAsStartedWhenInTime();
        //更新投标阶段为选标阶段(当前系统时间大于投标结束日期，小于开标时间)
        timeTaskMapper.updateAllVerifiedAsConfirmWhenInTime();
        //更新选标阶段为公布结果已完成(当前系统时间大于等于开标日期)
        timeTaskMapper.updateAllVerifiedAsResultWhenInTime();
        //更新申请状态为已作废(如果当前时间大于等于开始投标时间)
        timeTaskMapper.updateAllVerifiedAsEditWhenInTime();
        //投标期间无人竞标,发布招标已作废(当前系统时间大于等于选标结束日期)
        timeTaskMapper.updateAllVerifiedAsTenderCancel();
    }

    public void publishMyTender(){
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
        //更新待选标(支付凭证审核通过)为未中标(当前系统时间大于开标日期)
        timeTaskMapper.updateAllVerifiedAsFail();
        timeTaskMapper.updateAllVerifiedBidFail();
        //更新待选标(不需要支付凭)为未中标(当前系统时间大于开标日期)
        //tenderdeclarMapper.updateAllFreeAsFail();
        //tenderdeclarMapper.updateAllFreeBidFail();
    }
}
