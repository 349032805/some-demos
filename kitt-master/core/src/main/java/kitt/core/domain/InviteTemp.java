package kitt.core.domain;

/**
 * Created by xiangyang on 16/1/15.
 */
public class InviteTemp {

    private int id;
    private String inviteCompanyname;
    private String invitePhone;
    private int userId;
    //邀请状态  1 未注册   2 已注册，公司信息未通过   3、已注册，公司信息已通过
    private int status;
    private int tenderdeclarationId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInviteCompanyname() {
        return inviteCompanyname;
    }

    public void setInviteCompanyname(String inviteCompanyname) {
        this.inviteCompanyname = inviteCompanyname;
    }

    public String getInvitePhone() {
        return invitePhone;
    }

    public void setInvitePhone(String invitePhone) {
        this.invitePhone = invitePhone;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getTenderdeclarationId() {
        return tenderdeclarationId;
    }

    public void setTenderdeclarationId(int tenderdeclarationId) {
        this.tenderdeclarationId = tenderdeclarationId;
    }
}
