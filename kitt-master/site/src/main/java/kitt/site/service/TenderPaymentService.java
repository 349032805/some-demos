package kitt.site.service;

import kitt.core.domain.Bid;
import kitt.core.domain.TenderDeclaration;
import kitt.core.domain.TenderStatus;
import kitt.core.domain.User;
import kitt.core.persistence.*;
import kitt.core.service.MessageNotice;
import kitt.core.util.PageQueryParam;
import kitt.site.basic.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by xiangyang on 15/11/15.
 */
@Service
@Transactional(readOnly = true)
public class TenderPaymentService {

    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private TenderPayMentMapper tenderPayMentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BidMapper bidMapper;
    @Autowired
    private MytenderMapper mytenderMapper;
    /**
     * 审核投标支付凭证状态:暂无用户上传
     */
    public static final int NO_ONE = -1;
    /**
     * 审核投标支付凭证状态:正常情况，没有任何提示
     */
    public static final int NORMAL = 1;

    /**
     * 审核投标支付凭证状态:等待其它用户上传
     */
    public static final int WAIT_USER = 2;
    /**
     * 审核投标支付凭证状态:选标
     */
    public static final int CHOOSE_TENDER = 3;

    /**
     * 公告下所有支付凭证列表
     *
     * @param queryParam
     * @param user
     * @param declareId
     * @param status
     * @return
     */
    public PageQueryParam listPaymentByDeclareId(PageQueryParam queryParam, User user, int declareId, TenderStatus status) {
        int totalCount = tenderPayMentMapper.countByUserId(declareId, user.getId(), status.toString());
        List<Bid> tenderPayments = tenderPayMentMapper.findAllByUserId(queryParam, declareId, user.getId(), status.toString());
        int totalPage = totalCount / queryParam.getPagesize();
        totalPage = totalCount % queryParam.getPagesize() == 0 ? totalPage : totalPage + 1;
        queryParam.setTotalCount(totalCount);
        queryParam.setTotalPage(totalPage);
        queryParam.setList(tenderPayments);
        return queryParam;
    }

    /**
     * 通过公告加载投标支付凭证状态
     *
     * @param userId    用户id
     * @param declareId 公告id
     */
    public int loadTenderStatusByDeclareId(int userId, int declareId) {
        //获取当前支付凭证所属的公告
        TenderDeclaration tenderDeclaration = tenderdeclarMapper.findTenderDeclarByIdAndUserId(declareId, userId);
        if (tenderDeclaration == null) {
            throw new NotFoundException();
        }
        //以投标投标截止日为节点，给用户不同提示
        LocalDateTime endDate = tenderDeclaration.getTenderenddate();
        //公告下的所有投标
        List<Bid> tenders = bidMapper.findTenderInTenderDec(declareId);
        long waitVerifyCount = tenders.stream().filter(t -> TenderStatus.waitVerify.toString().equals(t.getPaymentstatus())).count();
        long unPickUpCount = tenders.stream().filter(t -> TenderStatus.unPaidUp.toString().equals(t.getPaymentstatus())).count();
        long pickUpCount = tenders.stream().filter(t -> TenderStatus.paidUp.toString().equals(t.getPaymentstatus())).count();
        if (tenders.size() == 0) {
            return NO_ONE;
        } else if (waitVerifyCount > 0) {
            return NORMAL;
        } else if (waitVerifyCount == 0 && (pickUpCount > 0 || unPickUpCount > 0) && LocalDateTime.now().isBefore(endDate)) {
            //如果待审核没有一条数据 && 其它状态有数据 && 当前时间在投标截止日之前，提示:等待其他用户上传支付凭证
            return WAIT_USER;
            //如果待审核没有一条数据 && 其它状态有数据 && 当前时间在投标截止日之后，提示:立即选标
        } else if (waitVerifyCount == 0 && (pickUpCount > 0 || unPickUpCount > 0) && LocalDateTime.now().isAfter(endDate)) {
            return CHOOSE_TENDER;
        }
        return NORMAL;
    }

    /**
     * @param status
     * @param currentUser 当前登陆用户
     * @param bidId       投标Id
     */
    @Transactional(readOnly = false)
    public void updateTenderPaymentStatus(TenderStatus status, User currentUser, int bidId) {
        //判断当前投标是否是当前用户公告下的投标
        Bid bid = bidMapper.findById(currentUser.getId(), bidId);
        if (bid == null) {
            throw new NotFoundException();
        }
        User user = userMapper.getUserById(bid.getUserid());
        //已缴纳
        if (status.equals(TenderStatus.paidUp)) {
            MessageNotice.TenderPaymentSuccess.noticeUser(user.getSecurephone());
        } else if (status.equals(TenderStatus.unPaidUp)) {
            //未缴纳
            TenderDeclaration declare = tenderdeclarMapper.findTendDeclarById(bid.getTenderdeclarationid());
            //把招标修改为已作废
            bidMapper.updateBidMytenderStatus(TenderStatus.MYTENDER_CANCEL, user.getId(), bid.getId());
            MessageNotice.TenderPaymentFail.noticeUser(user.getSecurephone(), declare.getTradername(), declare.getTraderphone());
        }
        bidMapper.updateBidStatusUseridBidid(status, user.getId(), bid.getId());
        //如果公告下的所有投标都审核未通过，把该条招标设置为已作废
        updateTenderDeclaration(bid.getTenderdeclarationid(),currentUser.getId());
    }

    /**
     *  如果公告下的所有投标都审核未通过，把该条招标设置为已作废
     * @param declareId
     * @param userId
     */
    @Transactional(readOnly = false)
    public void updateTenderDeclaration(int declareId, int userId) {
        List<Bid> paymentList =  tenderPayMentMapper.findBidByDeclare(userId,declareId);
        long unPaidCount=paymentList.stream().filter(t -> TenderStatus.unPaidUp.toString().equals(t.getPaymentstatus())).count();
        if(paymentList.size()==unPaidCount){
                tenderdeclarMapper.updateStatusById(declareId,TenderStatus.TENDER_CANCEL.toString(),userId);
        }

    }
}
