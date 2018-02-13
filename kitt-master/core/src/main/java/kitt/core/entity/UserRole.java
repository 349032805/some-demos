package kitt.core.entity;

/**
 * Created by xiangyang on 15/9/7.
 */
public class UserRole extends BaseEntity {
    private int id;
    private int userId;                  //用户id
    private int roleId;                  //角色Id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
