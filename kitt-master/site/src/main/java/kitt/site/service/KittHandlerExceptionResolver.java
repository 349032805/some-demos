package kitt.site.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kitt.core.service.ExceptionReporter;
import kitt.ext.WithLogger;
import kitt.site.basic.exception.BusinessException;
import kitt.site.basic.exception.NotFoundException;
import kitt.site.basic.exception.UnauthorizedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.eclipse.jetty.io.EofException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;


/**
 * Created by joe on 1/15/15.
 */
@Service
public class KittHandlerExceptionResolver extends AbstractHandlerExceptionResolver implements WithLogger {
    @Autowired
    ExceptionReporter reporter;
    @Autowired
    private ObjectMapper om;
    @Autowired
    private Session session;
    public static final String MOBILEFTLPREFIX = "/m/error";

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView modelAndView = new ModelAndView();
        //模板前缀
        String ftlPrefix = isFromWxRequest(request.getRequestURI()) == true ? MOBILEFTLPREFIX : StringUtils.EMPTY;
        if (ex instanceof NotFoundException
                || ex instanceof NoSuchRequestHandlingMethodException
                || ex instanceof NoHandlerFoundException
                || ex instanceof org.springframework.web.HttpRequestMethodNotSupportedException
                || ex instanceof org.springframework.web.multipart.MultipartException) {
            //logger().warn("404", ex);
            modelAndView.setViewName(ftlPrefix + "/404");
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            modelAndView.addObject("errorInfo", ex.getMessage());
        } else if (ex instanceof UnauthorizedException) {
            String loginFtlLocation = isFromWxRequest(request.getRequestURI()) == true ? "/m/login" : "/login";
            modelAndView.setViewName(loginFtlLocation);
            modelAndView.addObject("exception", ((UnauthorizedException) ex).getUrl());
            response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        } else if (ex instanceof BindException || ex instanceof TypeMismatchException || ex instanceof MissingServletRequestParameterException) {
            logger().warn("400", request.getRequestURL().toString(),ex);
            modelAndView.setViewName(ftlPrefix + "/400");
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
        } else if (ex instanceof BusinessException) {
                logger().warn("409", ex);
                  try {
                        response.setHeader("content-type", "application/json;charset=UTF-8");
                        response.setStatus(HttpStatus.SC_CONFLICT);
                        response.getWriter().write(ex.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                  }
        } else {
            logger().error("500", ex);
            if(!(ex instanceof EofException)){
                handler500(request, ex);
            }
            modelAndView.setViewName(ftlPrefix + "/500");
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        }
        return modelAndView;
    }

    @Async
    private void handler500(HttpServletRequest request, Exception ex) {
        logger().warn("开始发送邮件");
        try {
            if("application/json".equals(request.getContentType())){
                reporter.handle(ex, request.getRequestURL().toString(), om.writeValueAsString(extractPostRequestBody(request)), getHeadersInfo(request), session.getUser());
            }else{
                reporter.handle(ex, request.getRequestURL().toString(), om.writeValueAsString(request.getParameterMap()), getHeadersInfo(request), session.getUser());
            }
        } catch (Exception e) {
            logger().warn("邮件发送失败", e);
        }
        logger().warn("邮件发送结束");
    }


    /**
     * 判断是微信的请求、pc主站的请求
     *
     * @param url
     * @return true 微信请求、false 主站请求
     */
    private boolean isFromWxRequest(String url) {
        Pattern p = Pattern.compile("/m/{1}.*");
        return p.matcher(url).matches();
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


    //获取  application/json 数据
    static String extractPostRequestBody(HttpServletRequest request) {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            Scanner s = null;
            try {
                s = new Scanner(request.getInputStream(), "UTF-8").useDelimiter("\\A");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return s.hasNext() ? s.next() : "";
        }else {
            return "{}";
        }
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
    }

}
