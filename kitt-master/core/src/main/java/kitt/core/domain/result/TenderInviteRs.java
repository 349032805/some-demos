package kitt.core.domain.result;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by xiangyang on 16/1/17.
 */
public class TenderInviteRs {

    private Integer id;
    private String tenderCode;
    private LocalDateTime createtime;
    private String status;
    private int inviteCount;
    private String companyName;
    //邀请的公司名称
    private String[] companyNames;
    //已经邀请的供应商
    private String inviteId;
    private String[] inviteIds;
    //1 正式邀请   0 临时邀请
    private int inviteType;
    private String phone;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenderCode() {
        return tenderCode;
    }

    public void setTenderCode(String tenderCode) {
        this.tenderCode = tenderCode;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public int getInviteCount() {
        return inviteCount;
    }

    public void setInviteCount(int inviteCount) {
        this.inviteCount = inviteCount;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        this.companyNames=StringUtils.split(companyName,",");
    }

    public String[] getCompanyNames() {
        return companyNames;
    }


    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
        this.inviteIds=StringUtils.split(inviteId,",");
    }

    public String[] getInviteIds() {
        return inviteIds;
    }

    public void setInviteIds(String[] inviteIds) {
        this.inviteIds = inviteIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getInviteType() {
        return inviteType;
    }

    public void setInviteType(int inviteType) {
        this.inviteType = inviteType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
