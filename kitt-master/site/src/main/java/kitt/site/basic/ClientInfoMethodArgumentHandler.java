package kitt.site.basic;

import kitt.core.domain.ClientInfo;
import kitt.core.util.ToolsMethod;
import kitt.site.basic.annotation.Client;
import kitt.site.service.Auth;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by liuxinjie on 16/1/15.
 */
@Service
public class ClientInfoMethodArgumentHandler implements HandlerMethodArgumentResolver {
    @Autowired
    private ToolsMethod toolsMethod;

    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(Client.class)) {
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        return new ClientInfo(toolsMethod.getIpAddress(request), request.getHeader("user-agent"), request.getHeader("accept-language"));
    }



}
