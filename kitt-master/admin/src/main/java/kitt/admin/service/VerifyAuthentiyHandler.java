package kitt.admin.service;

import kitt.admin.annotation.Authorities;
import kitt.admin.annotation.Authority;
import kitt.admin.basic.exception.ForbiddenException;
import kitt.core.domain.AuthenticationRole;
import kitt.core.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by zhangbolun on 15/5/15.
 */
@Service
public class VerifyAuthentiyHandler extends HandlerInterceptorAdapter {
    @Autowired
    protected Session session;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            Authority[] authorities = null;
            AuthenticationRole authenticationRole = null;
            boolean pass = false;

            if(method.getMethodAnnotation(Authority.class)!=null)
                authenticationRole = method.getMethodAnnotation(Authority.class).role();
            if(method.getMethodAnnotation(Authorities.class)!=null)
                authorities = method.getMethodAnnotation(Authorities.class).value();

//            if(method.getBeanType().getDeclaredAnnotation(Authority.class) != null)
//                authenticationRole = method.getBeanType().getDeclaredAnnotation(Authority.class).role();
//            if(method.getBeanType().getDeclaredAnnotation(Authorities.class)!=null)
//                authorities = method.getBeanType().getDeclaredAnnotation(Authorities.class).value();
            if(authorities != null) {
                for (Authority authority : authorities) {
                    if (checkRole(authority.role())) {
                        pass = true;
                        break;
                    }
                }
                if (!pass) {
                    throw new ForbiddenException();
                }
            } else {
                if(authenticationRole!=null) {
                    pass = checkRole(authenticationRole);
                    if (!pass) {
                        throw new ForbiddenException();
                    }
                }
            }
        }
        return super.preHandle(request, response, handler);
    }

    private boolean checkRole(AuthenticationRole authenticationRole) {
        List<Role> roleList = session.getRoleList();
        if(roleList!=null&&roleList.size()>0) {
            for (Role role : roleList) {
                if (role.getRolecode().equals(authenticationRole.toString())) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
}
