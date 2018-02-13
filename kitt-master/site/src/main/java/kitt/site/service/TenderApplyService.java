package kitt.site.service;

import kitt.core.domain.*;
import kitt.core.persistence.*;
import kitt.core.service.FileStore;
import kitt.core.util.JsonMapper;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangbolun on 15/11/11.
 */
@Service
public class TenderApplyService {

    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private TenderitemMapper tenderitemMapper;
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
    private TenderPayMentMapper tenderPayMentMapper;
    @Autowired
    private BidMapper bidMapper;
    @Autowired
    protected UserMapper userMapper;


    @Transactional
    public int deleteByBidid(int bidid){
        tenderPayMentMapper.deleteTenderpayment(bidid);
        bidMapper.updateBidStatus(TenderStatus.MYTENDER_GIVEUP, bidid);
        mytenderMapper.updateMytenderStatus(bidid,session.getUser().getId(), "MYTENDER_GIVEUP");
        return 1;
    }

    @Transactional
    public int addTender(TenderDeclaration declaration){
        declaration.setStatus(TenderStatus.TENDER_EDIT.name());
        declaration.setCompanyid(companyMapper.getIdByUserid(session.getUser().getId())) ;
        declaration.setUserid(session.getUser().getId());
        declaration.setStarttenderdate(declaration.getContractconbegindate());
        tenderdeclarMapper.addTenderdeclar(declaration);
        saveItemPacket(declaration);
        List<TenderItem> tenderItems= tenderitemMapper.findTendItemByDecalarId(declaration.getId());
        if(tenderItems!=null&&tenderItems.size()>0)
            tenderdeclarMapper.updatePurchaseamount(declaration.getId(),countdeclaration(tenderItems),session.getUser().getId());
        return declaration.getId();
    }

    //根据id查询招标以及关联的项目和标包
    public TenderDeclaration findDeclarById(int id){
        TenderDeclaration tenderdeclar= tenderdeclarMapper.findTendDeclarById(id);
        if(tenderdeclar!=null) {
            List<TenderItem> itemList = tenderitemMapper.findTendItemByDecalarId(id);
            for (TenderItem item : itemList) {
                List<TenderPacket> packetList = tenderpacketMapper.findTendpackgeByItemId(item.getId());
                if (packetList != null && packetList.size() > 0) {
                    for (TenderPacket packet : packetList) {
                        List<Mytender> mytenders = new ArrayList<Mytender>();
                        if (mytenders != null && mytenders.size() > 0) {
                            for (Mytender mytender : mytenders) {
                                BigDecimal tempBigDecimal = BigDecimal.valueOf(0);
                                if (mytender.getADV().compareTo(mytender.getADV02()) == 1) {
                                    tempBigDecimal = mytender.getADV().add(mytender.getADV02());
                                    mytender.setADV(tempBigDecimal.subtract(mytender.getADV()));
                                    mytender.setADV02(tempBigDecimal.subtract(mytender.getADV02()));
                                }
                            }
                        }
                        packet.setMytenderList(mytenders);
                    }
                }
                item.setPacketList(packetList);
            }

            tenderdeclar.setItemList(itemList);
            return tenderdeclar;
        }else{
            throw new NotFoundException();
        }
    }

    /**
     *
     * @param id
     * @param bidid 投标表id
     * @return
     */
    public TenderDeclaration findDeclarcDetailByDidId(int id, int bidid){
        TenderDeclaration tenderdeclar = tenderdeclarMapper.findTendDeclarById(id);
        List<TenderItem>  itemList = tenderitemMapper.findTendItemByDecalarId(id);
        for(TenderItem item:itemList) {
            List<TenderPacket> packetList = tenderpacketMapper.findTendpackgeByItemId(item.getId());
            if(packetList!=null && packetList.size()>0){
                for(TenderPacket packet : packetList){
                    List<Mytender> mytenders = mytenderMapper.getMyTenderByBidIdPacket(bidid, packet.getId());
                    if(mytenders!=null && mytenders.size()>0){
                        for(Mytender mytender : mytenders){
                            if(mytender.getADV()!=null && mytender.getADV02()!=null) {
                                if (mytender.getADV().compareTo(mytender.getADV02()) == 1) {
                                    BigDecimal tempBigDecimal = mytender.getADV();
                                    mytender.setADV(mytender.getADV02());
                                    mytender.setADV02(tempBigDecimal);
                                }
                            }
                        }
                    }
                    packet.setMytenderList(mytenders);
                }
            }
            item.setPacketList(packetList);
        }
        tenderdeclar.setItemList(itemList);
        return tenderdeclar;
    }


    @Transactional
    public boolean editTender(TenderDeclaration declaration){
        //重新加载公告状态
        TenderDeclaration   delcareInDB =  tenderdeclarMapper.findTendDeclarById(declaration.getId());
        if(delcareInDB==null|| StringUtils.isBlank(delcareInDB.getStatus())){
            throw  new NotFoundException();
        }
        if(TenderStatus.TENDER_EDIT.name().equals(delcareInDB.getStatus())) {
            //删除原项目与标包
            tenderpacketMapper.deletePacketbydeclarid(declaration.getId(),session.getUser().getId());
            tenderitemMapper.deleteItemByDeclarId(declaration.getId(),session.getUser().getId());
            //添加新项目与标包
            saveItemPacket(declaration);
            List<TenderItem> tenderItems = tenderitemMapper.findTendItemByDecalarId(declaration.getId());
            declaration.setPurchaseamount(countdeclaration(tenderItems));
            declaration.setStarttenderdate(declaration.getContractconbegindate());
            declaration.setUserid(session.getUser().getId());
            if (tenderdeclarMapper.updateTenderdeclar(declaration) != 1)
                throw new BusinessException("招标公告保存失败,此信息已修改,请刷新页面!");
            /**
             * add by xj
             */
        }else if(TenderStatus.TENDER_APPLY.name().equals(delcareInDB.getStatus())){
            tenderpacketMapper.deletePacketbydeclarid(declaration.getId(),session.getUser().getId());
            tenderitemMapper.deleteItemByDeclarId(declaration.getId(),session.getUser().getId());
            tenderdeclarMapper.deleteTenderdeclarationByid(declaration.getId(),session.getUser().getId());
            addTender(declaration);
        }else if(TenderStatus.TENDER_VERIFY_FAIL.name().equals(delcareInDB.getStatus())){
            tenderdeclarMapper.updateStatusById(declaration.getId(),TenderStatus.TENDER_CANCEL.name(),session.getUser().getId());
            addTender(declaration);
        }else {
            addTender(declaration);
        }
        return true;
    }

    @Transactional
    private  void saveItemPacket(TenderDeclaration declaration){
        int itemsequence=1;
        int packetsequence=1;
        String purchasecontent=null;
            List<TenderItem> tenderItems= declaration.getItemList();
            if(tenderItems!=null&&tenderItems.size()>0){
                for(TenderItem item:tenderItems){
                    if (item.getPurchasecontent()!=null) {
                        purchasecontent = item.getPurchasecontent();
                    }
                    item.setPurchasecontent(purchasecontent);
                    item.setTenderdeclarationid(declaration.getId());
                    item.setSequence(itemsequence);
                    item.setPurchaseamount(countItemamount(item.getPacketList()));
                    item.setUserid(session.getUser().getId());
                    tenderitemMapper.addTenderItem(item);
                    if(item.getId()>0){
                        List<TenderPacket> tenderPackets= item.getPacketList();
                        if(tenderPackets!=null&&tenderPackets.size()>0){
                            for(TenderPacket packet:tenderPackets){
                                if(packet.getADV()==null) {
                                    packet.setADV(packet.getADV02());
                                }else if(packet.getADV02()==null) {
                                    packet.setADV02(packet.getADV());
                                }else if(packet.getADV02().compareTo(packet.getADV())!=1){
                                    BigDecimal tem=packet.getADV02();
                                    packet.setADV02(packet.getADV());
                                    packet.setADV(tem);
                                }
                                packet.setTenderitemid(item.getId());
                                packet.setSequence(packetsequence);
                                packet.setTenderdeclarationid(declaration.getId());
                                packet.setUserid(session.getUser().getId());
                                tenderpacketMapper.addTenderPacket(packet);
                                if(packet.getId()<=0){
                                    throw new BusinessException("标包保存失败!");
                                }
                                packetsequence=packetsequence+1;
                            }
                        }
                    }else {
                        throw new BusinessException("招标项目保存失败!");
                    }
                    itemsequence=itemsequence+1;
                    packetsequence=1;
                }
            }
    }

    @Transactional
    public void confirmTender(String filepath, int tenderid) throws Exception{
        fileStore.copyFileToUploadDir(filepath);
        filepath= filepath.replace("temp", "upload");
        TenderDeclaration tenderDeclaration = tenderdeclarMapper.findTendDeclarById(tenderid);
        companyMapper.changeCompanyTenderStatus(tenderDeclaration.getCompanyid(),true);
        tenderdeclarMapper.updatePathById(tenderid,filepath,session.getUser().getId());
        tenderdeclarMapper.updateStatusById(tenderid, TenderStatus.TENDER_APPLY.name(),session.getUser().getId());
    }

    @Transactional
    public boolean verifyTenderCommit(TenderDeclaration declaration){

        //验证公告字段
        BeanValidators.validateWithException(declaration);
        //验证时间顺序
        verfyDate(declaration.getTenderbegindate(),declaration.getTenderenddate(),declaration.getStarttenderdate(),declaration.getContractconbegindate(),declaration.getContractconenddate());
        List<TenderItem> tenderItems= declaration.getItemList();
        List<TenderPacket> tenderPackets=null;
        if(tenderItems!=null&&tenderItems.size()>0){
            for(TenderItem item:tenderItems){
                //验证公告中项目字段
                BeanValidators.validateWithException(item);
                tenderPackets=item.getPacketList();
                for(TenderPacket packet:tenderPackets){
                    //验证项目中标包字段
                    BeanValidators.validateWithException(packet);
                }
            }
        }else{
            throw new BusinessException("提交公告项目不能为空");
        }
        return true;
    }

    /**
     * 校验发布投标公告时间顺序
     * @param date1
     * @param date2
     * @param date3
     * @param date4
     * @return
     */
    private boolean verfyDate(LocalDateTime date1,LocalDateTime date2, LocalDate date3, LocalDate date4,LocalDate date5){
        if(LocalDateTime.now().plusDays(-1).isAfter(date1))
            throw new BusinessException("投标起始日应大于等于当前时间");
        if(date1.isAfter(date2)||date1.compareTo(date2)==0)
            throw new BusinessException("投标截止日应大于投标起始日");
//        if(date2.isAfter(date3.atTime(0,0,0))||date2.compareTo(date3.atTime(0,0,0))==0)
//            throw new BusinessException("开标日期应大于投标截止日");
//        if(date3.isAfter(date4)||date3.compareTo(date4)==0)
//            throw new BusinessException("合同执行开始日期应大于开标日期");
        if(date2.isAfter(date4.atTime(0,0,0))||date2.compareTo(date4.atTime(0,0,0))==0)
            throw new BusinessException("合同执行开始日期应大于投标截止日期");
        if(date4.isAfter(date5)||date4.compareTo(date5)==0)
            throw new BusinessException("合同执行结束日期应大于合同执行开始日期");
        return  true;
    }

    private BigDecimal countItemamount(List<TenderPacket> packetLisnt ){
        BigDecimal amount=BigDecimal.valueOf(0);
        if(packetLisnt!=null&&packetLisnt.size()>0){
            for(TenderPacket packet:packetLisnt){
                if(packet.getPurchaseamount()!=null) {
                    amount = amount.add(packet.getPurchaseamount());
                }else {
                    amount=BigDecimal.valueOf(0);
                }
            }
        }
        return amount;
    }

    private BigDecimal countdeclaration(List<TenderItem>declarations){
        BigDecimal amount=BigDecimal.valueOf(0);
        if(declarations!=null&&declarations.size()>0){
            for(TenderItem packet:declarations){
                if(packet.getPurchaseamount()!=null){
                    amount=amount.add(packet.getPurchaseamount());
                }else {
                    amount=BigDecimal.valueOf(0);
                }
            }
        }
        return amount;
    }

    //投标首页
    public TenderDeclaration findDeclarewithEditTender(int declareId){
        TenderDeclaration tenderdeclar = tenderdeclarMapper.findTendDeclarById(declareId);
        List<TenderItem>  itemList=tenderitemMapper.findTendItemByDecalarId(declareId);
        for(TenderItem item:itemList){
            List<TenderPacket> packetList= tenderpacketMapper.findTendpackgeByItemId(item.getId());
            if(packetList!=null&&packetList.size()>0){
                for(TenderPacket packet:packetList){
                    packet.setMytenderList(new ArrayList<Mytender>());
                }
            }
            item.setPacketList(packetList);
        }
        tenderdeclar.setItemList(itemList);
        return tenderdeclar;
    }

    public void checkCompany(){
        User user = userMapper.getUserByPhone(session.getUser().getSecurephone());
        if(user == null) throw new NotFoundException();
        Company company = companyMapper.getCompanyByUserid(user.getId());
        if(company != null) {
            if (user.getVerifystatus().equals("待完善信息")) {
                throw new BusinessException("请完善公司信息");
            } else if (user.getVerifystatus().equals("待审核") || company.getVerifystatus().equals("待审核")) {
                throw new BusinessException("公司信息审核中");
            } else if (user.getVerifystatus().equals("审核未通过") || company.getVerifystatus().equals("审核未通过")) {
                throw new BusinessException("公司信息审核未通过");
            }
        } else{
            throw new BusinessException("公司信息未找到");
        }
    }
}
