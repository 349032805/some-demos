package kitt.site.service;

import kitt.core.domain.*;
import kitt.core.persistence.GroupBuyOrderMapper;
import kitt.core.persistence.GroupBuyQualificationMapper;
import kitt.core.persistence.GroupBuySupplyMapper;
import kitt.site.basic.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by zhangbolun on 15/5/7.
 */
@Service
public class GroupBuyService{

    @Autowired
    private GroupBuySupplyMapper groupBuySupplyMapper;
    @Autowired
    private GroupBuyQualificationMapper groupBuyQualifyMapper;
    @Autowired
    private GroupBuyOrderMapper groupBuyOrderMapper;

    @Transactional
    public GroupBuyOrder generateGroupBuyOrder(GroupBuySupply groupBuySupply,int volume,int groupBuySupplyId,User user){

        //获取一个有效的团购资质
        List<GroupBuyQualification> groupBuyQualifications = groupBuyQualifyMapper.getGroupBuyQualifyByStatusId(user.getId(), QualifyStatus.QUALIFY_ACTIVE.toString());
        if(groupBuyQualifications==null)
            throw new BusinessException("未找到有效团购资质！");
        GroupBuyQualification groupBuyQualify=groupBuyQualifications.get(0);

        GroupBuyOrder oldgroupBuyOrder = groupBuyOrderMapper.getOrderByQualifyCode(groupBuyQualify.getQualificationcode(),groupBuySupply.getGroupbuysupplycode());
        if(oldgroupBuyOrder==null) {
            //生成团购订单
            GroupBuyOrder groupBuyOrder = new GroupBuyOrder();
            groupBuyOrder.setUserid(user.getId());
            groupBuyOrder.setCreatetime(LocalDateTime.now());
            groupBuyOrder.setStatus(OrderStatus.ORDER_CREATE.toString());
            groupBuyOrder.setGroupbuysupplyid(groupBuySupplyId);
            groupBuyOrder.setVolume(volume);
            groupBuyOrder.setQualificationcode(groupBuyQualify.getQualificationcode());
            groupBuyOrder.setGroupbuysupplycode(groupBuySupply.getGroupbuysupplycode());
            groupBuyOrder.setClienttype(1);
            groupBuyOrderMapper.addGroupBuyOrder(groupBuyOrder);
            return groupBuyOrderMapper.getOrderById(groupBuyOrder.getId());
        }else{
            groupBuyOrderMapper.updateVolumeById(volume,oldgroupBuyOrder.getId());
            oldgroupBuyOrder.setVolume(volume);
            return oldgroupBuyOrder;
        }
    }

    @Transactional
    public boolean confirmGroupBuyOrder(String groupbuyordercode,int volume){


        GroupBuyOrder groupBuyOrder = groupBuyOrderMapper.getOrderByCode(groupbuyordercode);
        if(groupBuyOrder==null)
            throw new BusinessException("该订单失效,请重新下单!");
        GroupBuySupply groupBuySupply = groupBuySupplyMapper.getGroupBuySupplyById(groupBuyOrder.getGroupbuysupplyid());
        if(groupBuySupply==null)
            throw new BusinessException("团购供应信息不存在!");

        LocalDateTime now=LocalDateTime.now();
        if(!(groupBuySupply.getGroupbuybegindate().isBefore(now)&&groupBuySupply.getGroupbuyenddate().isAfter(now)))
            throw new BusinessException("团购未开始!");

        //设置资质状态已经绑定
        GroupBuyQualification groupBuyQualify = groupBuyQualifyMapper.getGroupBuyQualifyByCode(groupBuyOrder.getQualificationcode());
        if (groupBuyQualify == null)
            throw new BusinessException("未找到有效团购资质!");

        int selledamount = groupBuySupply.getSelledamount() + volume;
        int surplusamount = groupBuySupply.getSupplyamount()-selledamount;
        if(surplusamount<0)
            throw new BusinessException("库存不足!");

        groupBuyQualifyMapper.bindOrderForQualify(groupBuyQualify.getId(), QualifyStatus.QUALIFY_INPROCESS.toString(), groupBuyOrder.getGroupbuyordercode());

        //增加团购订单量
        groupBuySupply.setGroupbuyordercount(groupBuySupply.getGroupbuyordercount() + 1);
        groupBuySupply.setStatus(GroupBuySupplyStatus.GROUP_BUY_SUPPLY_INPROGRESS.toString());
        //更新团购供应量
        groupBuySupply.setSelledamount(selledamount);
        groupBuySupply.setSurplusamount(surplusamount);
        groupBuySupplyMapper.updateGroupBuySupply(groupBuySupply);
        //修改订单状态
        groupBuyOrderMapper.updateStatusByOrderCode(groupBuyOrder.getGroupbuyordercode(), OrderStatus.ORDER_ACTIVE.toString());

        return true;
    }
}
