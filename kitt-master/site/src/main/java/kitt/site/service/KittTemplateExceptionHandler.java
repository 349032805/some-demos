package kitt.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import kitt.core.service.ExceptionReporter;
import kitt.ext.WithLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by xiangyang on 15/10/27.
 */
@Service
public class KittTemplateExceptionHandler implements TemplateExceptionHandler, WithLogger {

    @Autowired
    private ExceptionReporter reporter;
    @Autowired
    private Request request;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private Session session;


    @Override
    public void handleTemplateException(TemplateException te, Environment env, Writer out) throws TemplateException {
           handler500(te);
    }

    @Async
    private void handler500(Exception ex) {
      logger().warn("开始发送邮件");
      try {
        HttpServletRequest currentRequest =request.getRequest();
        reporter.handle(ex, currentRequest.getRequestURL().toString(), KittHandlerExceptionResolver.extractPostRequestBody(currentRequest), getHeadersInfo(currentRequest), session.getUser());
      } catch (Exception e) {
        logger().warn("邮件发送失败", e);
      }
      logger().warn("邮件发送结束");
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
        map.put("Request Method",request.getMethod());
        map.put("requestURL", request.getRequestURL().toString());
        return om.writeValueAsString(map);
    }
}
