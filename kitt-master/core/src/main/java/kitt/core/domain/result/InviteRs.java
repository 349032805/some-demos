package kitt.core.domain.result;



import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by xiangyang on 16/1/15.
 */
public class InviteRs {

    private int id;
    private String companyName;
    private String phone;
    private LocalDateTime registerDate;
    private String companyAddress;
    private Integer tenderDeclarationId;
    private Integer userId;
    //邀请状态  1 未注册   2 已注册，公司信息未通过   3、已注册，公司信息已通过
    private Integer status;
    List<Integer> userIds;
    private int inviteType;


    public Integer getTenderDeclarationId() {
        return tenderDeclarationId;
    }

    public void setTenderDeclarationId(Integer tenderDeclarationId) {
        this.tenderDeclarationId = tenderDeclarationId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        StringUtils.trim(companyName);
        this.companyName = StringUtils.isEmpty(companyName)?null:companyName;
    }

    public LocalDateTime getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(LocalDateTime registerDate) {
        this.registerDate = registerDate;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = StringUtils.trim(phone);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Integer> userIds) {
        this.userIds = userIds;
    }

    public int getInviteType() {
        return inviteType;
    }

    public void setInviteType(int inviteType) {
        this.inviteType = inviteType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
