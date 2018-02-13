package kitt.admin.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kitt.admin.basic.exception.BusinessException;
import kitt.core.service.ExceptionReporter;
import kitt.ext.WithLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by joe on 1/15/15.
 */
@Service
public class KittHandlerExceptionResolver implements HandlerExceptionResolver, WithLogger {
    @Autowired
    ExceptionReporter reporter;
    @Autowired
    ObjectMapper om;
    @Autowired
    private Session session;

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        StringBuffer formData = new StringBuffer();
        StringBuffer value = null;
        ResponseStatus status = ex.getClass().getDeclaredAnnotation(ResponseStatus.class);
        ModelAndView modelAndView = new ModelAndView("http/500");
        modelAndView.addObject("exception", ex);
        if (status != null) {
            modelAndView.setViewName("http/" + status.value().value());
            response.setStatus(status.value().value());
        } else if (ex instanceof BusinessException) {
            try {
                response.setHeader("content-type", "application/json;charset=UTF-8");
                response.getWriter().write(ex.getMessage());
                response.setStatus(409);
            } catch (IOException e) {
                logger().warn("", e);
            }
            return new ModelAndView();
        } else {
            modelAndView = new ModelAndView("http/500");
            modelAndView.addObject("exception", ex);
            request.getParameterMap();
            logger().error("500", ex);
            try {
                reporter.handle(ex, request.getRequestURL().toString(), om.writeValueAsString(request.getParameterMap()), getHeadersInfo(request), session.getAdmin());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            response.setStatus(500);
        }
        return modelAndView;
    }

    //获取header对象
    private String getHeadersInfo(HttpServletRequest request) throws JsonProcessingException {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        map.put("requestURL", request.getRequestURL().toString());
        return om.writeValueAsString(map);
    }
}
