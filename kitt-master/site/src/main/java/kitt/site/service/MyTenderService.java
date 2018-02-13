package kitt.site.service;

import com.mysql.jdbc.StringUtils;
import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.FileStore;
import kitt.core.service.MessageNotice;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangbolun on 15/11/14.
 */
@Service
public class MyTenderService {

    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private TenderpacketMapper tenderpacketMapper;
    @Autowired
    private MytenderMapper mytenderMapper;
    @Autowired
    protected FileStore fileStore;
    @Autowired
    protected CompanyMapper companyMapper;
    @Autowired
    private Session session;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BidMapper bidMapper;

    /**
     * 添加投标包数据
     * @param mytender
     */
    @Transactional
    private void addMytender(Mytender mytender) {
        checkCanTender(mytender.getTenderpacketid());
        TenderPacket tenderPacket = tenderpacketMapper.getTendpackgeById(mytender.getTenderpacketid());
        if (tenderPacket != null) {
            User competeuser = session.getUser();
            if (competeuser == null)
                throw new NotFoundException();
            Company competecompany = companyMapper.getCompanyByUserid(competeuser.getId());
            if (competecompany == null)
                throw new NotFoundException();
            TenderDeclaration declaration = tenderdeclarMapper.findTendDeclarById(tenderPacket.getTenderdeclarationid());
            if (declaration == null)
                throw new NotFoundException();
            Company releasecompany = companyMapper.getCompanyById(declaration.getCompanyid());
            if (releasecompany == null)
                throw new NotFoundException();
            User releaseuser = userMapper.getUserById(releasecompany.getUserid());
            if (releaseuser == null)
                throw new NotFoundException();
            mytender.setReleasecompanyid(releasecompany.getId());
            mytender.setReleasecompanyname(releasecompany.getName());
            mytender.setCompetecompanyid(competecompany.getId());
            mytender.setCompetecompanyname(competecompany.getName());
            mytender.setTenderdeclarationid(declaration.getId());
            mytender.setTenderitemid(tenderPacket.getTenderitemid());
            mytender.setTenderdate(LocalDate.now());
            mytender.setTendercode(declaration.getTendercode());
            mytender.setReleaseuserid(releaseuser.getId());
            mytender.setCompeteuserid(competeuser.getId());
            if(mytender.getADV()!=null&&mytender.getADV02()!=null) {
                if (mytender.getADV().compareTo(mytender.getADV02()) == 1) {
                    BigDecimal bigtemp = mytender.getADV02();
                    mytender.setADV02(mytender.getADV());
                    mytender.setADV(bigtemp);
                }
            }
            mytenderMapper.addMyTender(mytender);
        } else {
            throw new BusinessException("添加投标失败");
        }
    }

    @Transactional
    public void addMyBidEdit(int tenderid, String attachmentpath, String attachmentfilename,boolean needpay, List<Mytender> mytenders) throws Exception{
        //更新临时图片
        if (attachmentpath != null && attachmentpath.contains("temp")) {
            fileStore.copyFileToUploadDir(attachmentpath);
            attachmentpath = attachmentpath.replace("temp", "upload");
        }
        //供应量统计
        BigDecimal supplyamount = BigDecimal.valueOf(0);
        for (Mytender mytender : mytenders) {
            if (mytender.getSupplyamount() != null) {
                supplyamount = supplyamount.add(mytender.getSupplyamount());
            }
        }

        //添加投标表信息
        Bid bid = new Bid();
        bid.setMytenderstatus(TenderStatus.MYTENDER_EDIT.name());
        bid.setTenderdeclarationid(tenderid);
        bid.setUserid(session.getUser().getId());
        bid.setSupplyamount(supplyamount);
        bid.setAttachmentpath(attachmentpath);
        bid.setAttachmentfilename(attachmentfilename);
        bid.setNeedpay(needpay);
        bidMapper.addBid(bid);
        //添加投标包表信息
        for (Mytender mytender : mytenders) {
            mytender.setBidid(bid.getId());
            mytender.setStatus(TenderStatus.MYTENDER_EDIT.name());
            addMytender(mytender);
        }
    }

    @Transactional
    public int addMyBidCommit(int bidid, int tenderid, String attachmentpath, String attachmentfilename,boolean needpay, List<Mytender> mytenders) throws Exception{
        //更新临时图片
        if (attachmentpath != null && attachmentpath.contains("temp")) {
            fileStore.copyFileToUploadDir(attachmentpath);
            attachmentpath = attachmentpath.replace("temp", "upload");
        }
        //供应量统计
        BigDecimal supplyamount = BigDecimal.valueOf(0);
        for (Mytender mytender : mytenders) {
            if (mytender.getSupplyamount() != null) {
                supplyamount = supplyamount.add(mytender.getSupplyamount());
            }
        }
        //辨别是否需要支付凭证
        TenderDeclaration declaration = tenderdeclarMapper.findTendDeclarById(tenderid);
        String status = null;
        if (declaration.getMargins().compareTo(BigDecimal.ZERO) == 0) {
            status= TenderStatus.MYTENDER_TENDERED_FREE.name();
        } else {
            status = TenderStatus.MYTENDER_TENDERED.name();
        }

        Bid bid = new Bid();
        List<Bid> bidList = bidMapper.getBidByTenderidUserid(tenderid, session.getUser().getId());
        for (Bid bid1 : bidList) {
            if (!bid1.getMytenderstatus().equals(TenderStatus.MYTENDER_EDIT.toString())) {
                bid.setId(bid1.getId());
                bid.setCreatetime(bid1.getCreatetime());
                bid.setPaymentstatus(bid1.getPaymentstatus());
                status = bid1.getMytenderstatus(); //本身的状态
            }
            //删除草稿
            bidMapper.deleteBid(bid1.getId(),session.getUser().getId()) ;
            mytenderMapper.deleteTender(bid1.getId(), session.getUser().getId());
        }
        //添加投标表信息
        bid.setMytenderstatus(status);
        bid.setTenderdeclarationid(tenderid);
        bid.setUserid(session.getUser().getId());
        bid.setSupplyamount(supplyamount);
        bid.setAttachmentpath(attachmentpath);
        bid.setAttachmentfilename(attachmentfilename);
        bid.setNeedpay(needpay);
        if(bid.getId() != 0 && !StringUtils.isNullOrEmpty(bid.getCreatetime().toString())) {//以现有id插入
            bidMapper.addBidWithId(bid);
        } else {
            bidMapper.addBid(bid);
        }
        //添加投标包表信息
        for (Mytender mytender : mytenders) {
            mytender.setBidid(bid.getId());
            mytender.setStatus(status);
            addMytender(mytender);
        }
        //短信通知
        MessageNotice.ALREADYSUBMITTENDERPACKET.noticeUser(session.getUser().getId(), bid.getId());
        return bid.getId();
    }

    @Transactional
    public void deleteMyBid(int bidid){
        int a = bidMapper.deleteBid(bidid, session.getUser().getId());
        int b = mytenderMapper.deleteTender(bidid, session.getUser().getId());
        if(a==0 || b==0) throw new BusinessException("投标删除失败");
    }

    /**
     * 检查是否发布投标方自己投自己
     *
     * @param tenderpacketid
     * @return
     */
    public void checkCanTender(int tenderpacketid) {
        TenderPacket tenderPacket = tenderpacketMapper.getTendpackgeById(tenderpacketid);
        if (tenderPacket != null) {
            TenderDeclaration declaration = tenderdeclarMapper.findTendDeclarById(tenderPacket.getTenderdeclarationid());
            if (declaration != null) {
                if (declaration.getUserid() == session.getUser().getId()) {
                    throw new BusinessException("招标方不能投自己发布的标");
                }
            }
        }
    }



    /**
     * 选标
     * @param mytenderid
     */
    @Transactional
    public void chooseTender(int mytenderid) {
        Mytender mytender = mytenderMapper.getMyTenderById(mytenderid);
        if (mytender == null)
            throw new BusinessException("投标信息未找到");
//        if (!mytender.getStatus().equals(TenderStatus.MYTENDER_WAITING_CHOOSE.name()))
//            throw new BusinessException("投标信息状态不正确，不能选标");
        bidMapper.updateBidStatusById(mytender.getBidid(), TenderStatus.MYTENDER_SUCCEED.name());
        mytenderMapper.updateMytenderStatusById(mytenderid,session.getUser().getId(), TenderStatus.MYTENDER_SUCCEED.name());
    }


    /**
     * @param declareId 公告Id
     * @param userId    用户id
     * @return
     */
    public TenderDeclaration findByDeclareId(int declareId, int userId) {
        TenderDeclaration tenderDeclaration = tenderdeclarMapper.findTendDeclarByIdAndUserId(declareId, userId);
        if (tenderDeclaration == null) {
            throw new NotFoundException();
        }
        return tenderDeclaration;
    }

    @Transactional
    public boolean commitSelectMytender(int tenderdeclarationId, int userid, Map<Integer, BigDecimal> selectTenders) {
        TenderDeclaration declare = tenderdeclarMapper.getTenderdeclarByIdUserId(tenderdeclarationId, session.getUser().getId());
        if (declare == null)
            throw new NotFoundException();
        String status="";
        tenderdeclarMapper.updateStatusById(tenderdeclarationId, TenderStatus.TENDER_RELEASE_RESULT.name(), session.getUser().getId());
        if (declare != null && declare.getUserid() == userid && selectTenders != null) {
            ArrayList<Integer> bididlist =new ArrayList<Integer>();
            for (Map.Entry<Integer, BigDecimal> entry : selectTenders.entrySet()) {
                Mytender mytender = mytenderMapper.getMyTenderById(entry.getKey());
                if(!bididlist.contains(Integer.valueOf(mytender.getBidid())))
                    bididlist.add(Integer.valueOf(mytender.getBidid()));
                if(entry.getValue().compareTo(BigDecimal.ZERO) > 0){
                    status=TenderStatus.MYTENDER_SUCCEED.name();
                    bidMapper.updateBidStatusById(mytender.getBidid(),status);
                }else {
                    status=TenderStatus.MYTENDER_FAIL.name();
                }
                mytenderMapper.commitSelectMytender1(entry.getKey(),session.getUser().getId(), tenderdeclarationId, status, entry.getValue());
            }
            mytenderMapper.commitMytenderSetCancel(tenderdeclarationId, session.getUser().getId());
            bidMapper.setBidCancel(tenderdeclarationId);

            Bid bid=null;
            for(Integer bidid:bididlist){
                bid=bidMapper.getBidById(bidid.intValue());
                if(!bid.getMytenderstatus().equals(TenderStatus.MYTENDER_SUCCEED.name())){
                   List<Mytender> mytenderlist=mytenderMapper.getMyTenderByBidId(bidid.intValue());
                    int countfail=0;
                    if(mytenderlist!=null&&mytenderlist.size()>0){
                        for(Mytender mytender:mytenderlist){
                            if(mytender.getStatus().equals(TenderStatus.MYTENDER_FAIL.name())){
                                countfail=countfail+1;
                            }
                        }
                    }
                    if(mytenderlist.size()==countfail){
                        bidMapper.updateBidStatusById(bidid.intValue(),TenderStatus.MYTENDER_FAIL.name());
                    }
                }
            }

            return true;
        }
        return false;
    }
}
