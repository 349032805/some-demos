package kitt.site.service;

import kitt.core.domain.User;
import kitt.core.domain.WebLog;
import kitt.core.persistence.WebLogMapper;
import kitt.core.util.ToolsMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by liuxinjie on 15/8/26.
 * 此Service专门用来收集客户访问我们网站信息
 */
@Service
public class UserInfoCollectInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private Session session;
    @Autowired
    private WebLogMapper webLogMapper;
    @Autowired
    private ToolsMethod toolsMethod;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            if (session != null && session.isLogined()) {
                User user = session.getUser();
                webLogMapper.addWebLog(new WebLog(request.getRequestURI(), request.getQueryString(), method.getMethod().getName(), user.getId(), user.getSecurephone(), request.getHeader("user-agent"), toolsMethod.getIpAddress(request), request.getMethod()));
            } else {
                webLogMapper.addWebLog(new WebLog(request.getRequestURI(), request.getQueryString(), method.getMethod().getName(), request.getHeader("user-agent"), toolsMethod.getIpAddress(request), request.getMethod()));
            }
        }
        return super.preHandle(request, response, handler);
    }



}
