package kitt.admin.service;

import kitt.admin.basic.exception.BusinessException;
import kitt.core.domain.*;
import kitt.core.persistence.CompanyMapper;
import kitt.core.persistence.UserMapper;

import kitt.core.service.*;

import kitt.core.service.CODE;
import kitt.core.service.MessageNotice;
import kitt.core.service.SMS;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Created by yimei on 15/7/27.
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SMS sms;
    @Autowired
    private CODE code;
    @Autowired
    private CompanyMapper companyMapper;
    @Autowired
    private Session session;

    /**
     * 重置密码
     * @param securephone   手机号
     * @return              true or false
     * @throws Exception
     */
    @Transactional
    public boolean doResetPasswordMethod(String securephone) throws Exception {
        String randompwd = code.CreateCode();
        if(userMapper.modifyPasswdByPhone(DigestUtils.md5Hex(randompwd), securephone) == 1){
            MessageNotice.CommonMessage.noticeUser(securephone, "您好，您的随机密码是：" + randompwd);
            return true;
        }
        throw new BusinessException("重置密码失败！请联系技术人员！");
    }

    /**
     * 禁用，启用 用户账号
     * @param user   用户对象
     * @return       success: true or false
     * @return       isactive: 用户的状态
     */
    @Transactional
    public Object doDisableEnableAccountMethod(User user) {
        int rows;
        if (user.isIsactive()) {
            rows = userMapper.editUserAccount(false, user.getSecurephone());
        } else {
            rows = userMapper.editUserAccount(true, user.getSecurephone());
        }
        if(rows == 1) {
            boolean activeStatus = userMapper.getUserById(user.getId()).isIsactive();
            return new Object() {
                public boolean isactive = activeStatus;
                public boolean success = true;
            };
        }
        throw new BusinessException("禁用账户失败！请联系技术人员！");
    }
    @Autowired
    private MySupplyerService mySupplyerService;

    /**
     * 审核公司信息 -- 审核通过
     * @param company   公司对象
     * @param remarks   审核备注
     * @return          true or false
     */
    @Transactional
    public boolean doCompanyVerifyPassMethod(Company company, String remarks) {
        CompanyVerify companyVerify = companyMapper.getTheLatestCompanyVerifyByCompanyIdUserIdStatus(company.getId(), company.getUserid(), "待审核");
        if(companyVerify != null) {
            String status = EnumCommonString.VerifyPass.value();
            int row1 = companyMapper.setCompVerify(status, session.getAdmin().getUsername(), LocalDateTime.now(), remarks, company.getId(), companyVerify.getId());
            int row2 = companyMapper.setCompanyStatus(status, remarks, company.getId());
            int row3 = userMapper.setUserVerifyStatus(status, LocalDateTime.now(), company.getUserid());
            int row4 = companyMapper.addCompVerSus(new CompVerSus(company.getName(), company.getAddress(), company.getPhone(), company.getFax(), company.getLegalperson(), company.getBusinesslicense(), company.getIdentificationnumber(), company.getOrganizationcode(), company.getOperatinglicense(), company.getUserid(), company.getLegalpersonname(), company.getAccount(), company.getOpeningbank()));
            if (row1 == 1 && row2 == 1 && row3 == 1 && row4 == 1) {
                String phone=userMapper.getUserById(company.getUserid()).getSecurephone();
                final String content = "您好，您的公司信息审核已通过，可以享受易煤网的各项交易功能与服务。";
                MessageNotice.CommonMessage.noticeUser(phone, content);
                //增加审计公司
                mySupplyerService.auditSupplyer(phone);
                return true;
            }
        }
        throw new BusinessException("审核客户失败！请联系技术人员！");

    }

    /**
     * 审核公司信息 -- 审核不通过
     * @param company   公司对象
     * @param remarks   审核备注
     * @return          true of false
     */
    @Transactional
    public boolean doCompanyVerifyNotPassMethod(Company company, String remarks) {
        CompanyVerify companyVerify = companyMapper.getTheLatestCompanyVerifyByCompanyIdUserIdStatus(company.getId(), company.getUserid(), "待审核");
        if(companyVerify != null) {
            String status = EnumCommonString.VerifyNotPass.value();
            int row1 = companyMapper.setCompVerify(status, session.getAdmin().getUsername(), LocalDateTime.now(), remarks, company.getId(), companyVerify.getId());
            int row2 = companyMapper.setCompanyStatus(status, remarks, company.getId());
            int row3 = userMapper.setUserVerifyStatus(status, LocalDateTime.now(), company.getUserid());
            if (row1 == 1 && row2 == 1 && row3 == 1) {
                String phone = userMapper.getUserById(company.getUserid()).getSecurephone();
                final String content = "您好，您的公司信息审核未通过，审核反馈为:" + remarks + ", 如需帮助，请拨打客服热线400-960-1180。";
                MessageNotice.CommonMessage.noticeUser(phone, content);
                return true;
            }
        }
        throw new BusinessException("审核客户失败！请联系技术人员！");
    }

    /**
     * 后台客服，管理员 帮助客户完善公司信息
     * @param companyc    公司对象， 如果companyc 为空， 是 新增公司信息， 如果companyc 不为空，是修改， 更新公司信息
     * @param company     公司对象
     * @return            true or false
     */
    @Transactional
    public boolean doSaveCompanyInfoMethod(Company companyc, Company company, int userid) {
        int row1;
        if(companyc == null){
            row1 = companyMapper.addCompany(company);
        } else{
            row1 = companyMapper.modifyCompany(company);
        }
        int row2 = companyMapper.setCompanyStatus("待审核", null, companyMapper.getIdByUserid(userid));
        int row3 = companyMapper.addCompVerify(new CompanyVerify("待审核", LocalDateTime.now(), companyMapper.getIdByUserid(userid), userid, "操作人id=" + session.getAdmin().getId() + ", 操作人账号=" + session.getAdmin().getUsername() + ", 操作人工号=" + session.getAdmin().getJobnum()));
        int row4 = userMapper.setUserVerifyStatus("待审核", null, userid);
        if(row1 == 1 && row2 ==1 && row3 == 1 && row4 == 1){
            mySupplyerService.updateSupplyerForAuditCompanies(company);
            return true;
        }
        throw new BusinessException("保存公司信息失败！请联系技术人员！");
    }


    /**
     * 为用户添加交易员方法
     * @param id                  users表id
     * @param traderid            交易员id（admins 表id）
     */
    @Transactional
    public boolean doAddUpdateUserTraderMethod(int id, int traderid) {
        return userMapper.doAddUpdateUserTraderMethod(id, traderid);
    }
}
