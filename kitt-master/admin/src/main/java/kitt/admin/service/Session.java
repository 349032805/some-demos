package kitt.admin.service;

import kitt.core.domain.Admin;
import kitt.core.domain.Role;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * Created by joe on 10/26/14.
 */
@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Session  implements Serializable{
    protected Admin admin;
    private List<Role> roleList;
    //取消后台权限标签设置为 false
    private boolean verifyAuthentication=true;

    public Admin getAdmin() {
        return admin;
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public boolean getVerifyAuthentication(){return this.verifyAuthentication;}

    public boolean login(Admin admin, List<Role> roleList){
        this.admin = admin;
        this.roleList = roleList;
        return true;
    }

    public boolean isLogined(){
        return this.admin!=null;
    }
    public void logout(){
        this.admin = null;
    }

}
