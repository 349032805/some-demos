package kitt.core.service;

import kitt.core.domain.*;
import kitt.core.domain.result.InviteRs;
import kitt.core.persistence.MySupplyerMapper;
import kitt.core.persistence.TenderInviteMapper;
import kitt.core.persistence.TenderdeclarMapper;
import kitt.core.persistence.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by xiangyang on 16/1/18.
 */
@Service
@Transactional(readOnly = true)
public class MySupplyerService {

    @Autowired
    private MySupplyerMapper mySupplyerMapper;

    //已注册
    private static final int REGISTERED = 1;
    //未认证
    private static final int UNAUTHOR = 2;
    //供应商白名单
    private static final int WHITE_LIST = 1;
    @Autowired
    private TenderInviteMapper tenderInviteMapper;
    @Autowired
    private TenderdeclarMapper tenderdeclarMapper;
    @Autowired
    private SMS sms;
    @Autowired
    private UserMapper userMapper;
   public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Transactional(readOnly = false)
    public void registerValidateSupplyer(String phone) {
        mySupplyerMapper.isExistsInviteSupplyerByPhone(phone).forEach(t -> {
            mySupplyerMapper.updateInviteTempByPhone(t.getInvitePhone(), REGISTERED);
        });
    }


    /**
     * 添加多个会员邀请记录
     *
     * @param inviteList
     */
    @Transactional(readOnly = false)
    public void addSupplyer(List<InviteRs> inviteList, int userId) {
        inviteList.stream().forEach(t -> {
            InviteRs inviteRs = mySupplyerMapper.isExistsSupplyer(t.getPhone());
            //不是易煤网会员
            if (null == inviteRs) {
                t.setStatus(loadTempInviteStatus(t.getPhone()));
                mySupplyerMapper.addInviteTemp(userId, t);
            } else {
                //是易煤网会员
                if (0 == mySupplyerMapper.isMySupplyer(userId, inviteRs.getUserId())) {
                    mySupplyerMapper.addInvite(new MySupplyer(userId, inviteRs.getUserId(), 1));
                }
            }
        });
    }

    @Transactional(readOnly = false)
    public void auditSupplyer(String phone) {
        User user = userMapper.getUserByPhone(phone);
        if(user==null){
            throw  new RuntimeException("该用户不存在!");
        }
        if(user.getVerifystatus().equals("审核通过")){
            List<InviteTemp> list = tenderInviteMapper.findInviteTempByPhone(phone);
            list.forEach(t->{
                //添加为我的供应商
                mySupplyerMapper.addInvite(new MySupplyer(t.getUserId(),user.getId(),WHITE_LIST));
                //添加投标邀请
                if(t.getTenderdeclarationId()!=0){
                    tenderInviteMapper.addTenderInvite(t.getTenderdeclarationId(),t.getUserId(),user.getId());
                }
                //删除临时邀请记录
                mySupplyerMapper.deleteInviteTemp(t.getId());
            });
        }
    }

    private int loadTempInviteStatus(String phone) {
        User user = userMapper.getUserByPhone(phone);
        if (user == null) {
            //未注册
            return REGISTERED;
        }
             //已注册
            return UNAUTHOR;
    }

    //招标审核通过给邀请用户发送短信提醒
    public void sendTenderInviteMessage(int declareId)  {

        TenderDeclaration tender = tenderdeclarMapper.lodaDeclareById(declareId);
        String str = "尊敬的companyName您好!" + tender.getTenderunits() + "将在" + tender.getTenderbegindate().format(dtf) + "——" + tender.getTenderenddate().format(dtf) + "期间进行" +
                tender.getContractconenddate().getMonthValue() + "月份煤炭采购招标，诚邀贵公司参与投标，请贵公司在" + tender.getTenderenddate().format(dtf) +
                "前登录易煤网—阳光采购板块进行投标，过期将投标失效！【易煤网】" + LocalDate.now();
        tenderInviteMapper.loadInviteByDeclareId(declareId, tender.getUserid()).forEach(t -> {
            try {
                sms.send(t.getPhone(), str.replace("companyName", t.getCompanyName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    //招标审核通过给邀请用户发送短信提醒
    public void sendTenderInviteMessage(int declareId, InviteRs inviteRs)  {
        TenderDeclaration tender = tenderdeclarMapper.lodaDeclareById(declareId);
        String str = "尊敬的companyName您好!" + tender.getTenderunits() + "将在" + tender.getTenderbegindate().format(dtf) + "——" + tender.getTenderenddate().format(dtf) + "期间进行" +
                tender.getContractconenddate().getMonthValue() + "月份煤炭采购招标，诚邀贵公司参与投标，请贵公司在" + tender.getTenderenddate().format(dtf) +
                "前登录易煤网—阳光采购板块进行投标，过期将投标失效！【易煤网】" + LocalDate.now();
            try {
                sms.send(inviteRs.getPhone(), str.replace("companyName", inviteRs.getCompanyName()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

    }

    //修改公司信息,供应商数据
    @Transactional(readOnly = false)
    public void updateSupplyerForAuditCompanies(Company company){
        if(company==null){
            throw  new RuntimeException("公司信息不能为空!");
        }
        User user = userMapper.getUserById(company.getUserid());
        if(user==null) {
            throw  new RuntimeException("用户信息不能为空!");
        }
        InviteRs inviteTemp =new InviteRs();
        inviteTemp.setCompanyName(company.getName());
        inviteTemp.setPhone(user.getSecurephone());
        inviteTemp.setStatus(UNAUTHOR);
        mySupplyerMapper.findUserbySupplyerId(company.getUserid()).forEach(t->{
            inviteTemp.setUserId(t);
            tenderInviteMapper.addTenderInviteTemp(inviteTemp);
        });
        mySupplyerMapper.deleteSupplyer(company.getUserid());
    }

}
