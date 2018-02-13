package kitt.site.service;

import com.fasterxml.jackson.databind.JavaType;
import kitt.core.domain.MySupplyer;
import kitt.core.domain.TenderDeclaration;
import kitt.core.domain.TenderStatus;
import kitt.core.domain.User;
import kitt.core.domain.result.InviteRs;
import kitt.core.persistence.MySupplyerMapper;
import kitt.core.persistence.TenderInviteMapper;
import kitt.core.persistence.TenderdeclarMapper;
import kitt.core.persistence.UserMapper;
import kitt.core.service.MySupplyerService;
import kitt.core.util.JsonMapper;
import kitt.site.basic.JsonController;
import kitt.site.basic.annotation.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xiangyang on 16/1/15.
 */
@Service
@Transactional(readOnly = false)
public class TenderInviteService {

    @Autowired
    private TenderInviteMapper tenderInviteMapper;
    @Autowired
    private MySupplyerMapper mySupplyerMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private MySupplyerService mySupplyerService;
    @Autowired
    private Session session;



    private static final int WHITELIST = 1;
    private static final int BLACKLIST = 0;

    /**
     * 根据招标,添加混合的投标邀请记录
     *
     * @param declareId
     * @param inviteStr
     * @param tenderOpen
     */
    @Transactional(readOnly = false)
    public void addCompositeTenderInvite(int declareId, String inviteStr, boolean tenderOpen) {
        User currentUser = session.getUser();
        if (!tenderOpen) {
            JavaType javaType = JsonMapper.nonDefaultMapper().contructCollectionType(List.class, InviteRs.class);
            inviteStr = inviteStr.startsWith(",") == true ? inviteStr.substring(1, inviteStr.length()) : inviteStr;
            List<InviteRs> inviteList = JsonMapper.nonDefaultMapper().fromJson(inviteStr, javaType);
            List<Integer> supplyerIds = new LinkedList<>();
            List<Integer> inviteTempIds=new LinkedList<>();
            //邀请易煤网会员
            inviteList.stream().filter(t -> t.getInviteType() == 1).forEach(t -> {
            //用户最新的邀请ID
                supplyerIds.add(t.getUserId());
                addTenderInvite(declareId, currentUser.getId(), t.getUserId());
            });
            //邀请非易煤网会员
            inviteList.stream().filter(t -> t.getInviteType() == 0).forEach(t -> {
                if(t.getUserId()==null){
                    addInviteTemp(currentUser.getId(), declareId, t);
                    inviteTempIds.add(t.getId());
                }else {
                    inviteTempIds.add(t.getUserId());
                }
            });
            //删除正式邀请
            deleteTenderInvite(supplyerIds,declareId);
           //删除临时邀请
            deleteInviteTemp(inviteTempIds,declareId);
        }else{
            //删除所有投标邀请
            tenderInviteMapper.deleteAllTenderinvite(declareId,currentUser.getId());
            tenderInviteMapper.deleteAllTenderTempinvite(declareId,currentUser.getId());
        }
    }

    //删除正式邀请数据
    @Transactional(readOnly = false)
    public void deleteTenderInvite(List<Integer> newSupplyerId, int declareId) {
        User currentUser = session.getUser();
        //已邀请的供应商id
        List<Integer> supplyerId = tenderInviteMapper.findSupplyerIdByDeclareId(currentUser.getId(), declareId);
        List<Integer> deleteIds = getSubSet(supplyerId, newSupplyerId);
        if (deleteIds.size() > 0) {
            tenderInviteMapper.deleteTenderInvite(currentUser.getId(), declareId, deleteIds);
        }

    }

    //删除临时邀请数据
    @Transactional(readOnly = false)
    public void deleteInviteTemp(List<Integer> newSupplyerId, int declareId) {
        User currentUser = session.getUser();
        //临时邀请id
        List<Integer> inviteIds = tenderInviteMapper.findTempInviteIdByDeclareId(currentUser.getId(), declareId);
        List<Integer> deleteIds = getSubSet(inviteIds, newSupplyerId);
        if (deleteIds.size() > 0) {
            tenderInviteMapper.deleteTenderTempInvite(currentUser.getId(), declareId, deleteIds);
        }
    }

    //得到两个集合的差集
    private List<Integer> getSubSet(List<Integer> c1, List<Integer> c2) {
        List<Integer> result = new ArrayList<Integer>();
        result.addAll(c1);
        result.removeAll(c2);
        return result;
    }

    /**
     * 邀请易煤认证通过会员
     *
     * @param declareId
     * @param userId
     * @param supplyerId
     */
    @Transactional(readOnly = false)
    public void addTenderInvite(int declareId, int userId, int supplyerId) {
        if (mySupplyerMapper.isMySupplyer(userId, supplyerId) == 0) {
            //添加我的供应商(白名单)
            mySupplyerMapper.addInvite(new MySupplyer(userId, supplyerId, WHITELIST));
        }
        //添加投标邀请
        if (tenderInviteMapper.isExistsInvite(declareId, userId, supplyerId) == 0) {
            tenderInviteMapper.addTenderInvite(declareId, userId, supplyerId);
            sendMessage(declareId,supplyerId,1);
        }
    }

    /**
     *
     * @param declareId
     * @param supplyerId
     * @param inventType  1 正式供应商邀请 0 临时邀请
     */
    public  void sendMessage(int declareId,int supplyerId,int inventType){
        TenderDeclaration td  = tenderdeclarMapper.findTendDeclarById(declareId);
        //审核通过的招标,邀请供应商发送短信通知
        if(td.getStatus().equals(TenderStatus.TENDER_START.name())){
            //正式邀请
            if(inventType==1){
                mySupplyerService.sendTenderInviteMessage(declareId,mySupplyerMapper.findSupplyerById(supplyerId));
            }else if (inventType==0){
                mySupplyerService.sendTenderInviteMessage(declareId,tenderInviteMapper.findInviteTempByIdAndDeclareId(supplyerId,declareId));
            }
        }
    }


    /**
     * 邀请未注册的会员
     *
     * @param userId
     * @param declareId
     * @param inviteRs
     */
    @Transactional(readOnly = false)
    public void addInviteTemp(int userId, int declareId, InviteRs inviteRs) {
        int flag = 1;
        User user = userMapper.getUserByPhone(inviteRs.getPhone());
        if (user == null) {
            //邀请客户未注册
            flag = 1;
        } else if (user.getVerifystatus().equals("审核未通过") || user.getVerifystatus().equals("待完善信息")) {
            //邀请客户公司信息未通过
            flag = 2;
        }
        inviteRs.setStatus(flag);
        inviteRs.setTenderDeclarationId(declareId);
        inviteRs.setUserId(userId);
        tenderInviteMapper.addTenderInviteTemp(inviteRs);
        sendMessage(declareId,inviteRs.getId(),0);
    }

    //招标是否是公开
    public boolean tenderIsOpen(int declareId,int userId) {
        boolean flag = tenderdeclarMapper.isTenderOpen(declareId);
        if (!flag) {
            if (tenderInviteMapper.isAllowTender(userId, declareId) <= 0) {
                return false;
            }
        }
        return true;
    }


}
