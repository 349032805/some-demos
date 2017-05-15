package com.joindata.bss.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joindata.bss.service.config.ApplicationProperties;
import com.joindata.bss.util.ApplicationContextUtils;

public class CrossAccessControlFilter implements Filter {

    private static Logger logger = LoggerFactory.getLogger(CrossAccessControlFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletResponse resp = (HttpServletResponse) response;
		ApplicationProperties properties = ApplicationContextUtils.getBean(ApplicationProperties.class);
		if(properties.isAccessControlAllowCross()) {
//			logger.warn("系统已设置为跨域访问：[{}]", properties.getAccessControlAllowOrigin());
			resp.addHeader("Access-Control-Allow-Origin", "http://10.15.110.131:8080");
			resp.addHeader("Access-Control-Allow-Credentials", "true");
			resp.addHeader("Access-Control-Allow-Methods", "GET,HEAD,POST,PUT,PATCH, DELETE,OPTIONS,TRACE");
			resp.addHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin,Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
