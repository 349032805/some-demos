package kitt.site.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import kitt.ext.spring.jackson.Java8TimeModule;
import kitt.site.basic.BindValidateMethodArgumentResolver;
import kitt.site.basic.CurrentUserMethodArgumentHandler;
import kitt.site.basic.ClientInfoMethodArgumentHandler;
import kitt.site.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;
import javax.validation.Validator;
import java.util.List;

/**
 * Created by joe on 11/2/14.
 */
@Configuration
public class SiteConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/bower_components/**").addResourceLocations("file:./src-web/bower_components/");
        registry.addResourceHandler("/styles/**").addResourceLocations("file:./src-web/.tmp/styles/");
        registry.addResourceHandler("/images/**").addResourceLocations("file:./src-web/app/images/");
        registry.addResourceHandler("/scripts/**").addResourceLocations("file:./src-web/app/scripts/");
        registry.addResourceHandler("/files/**").addResourceLocations("file:../files/");
        // static files for mobile site
        registry.addResourceHandler("/m/**").addResourceLocations("file:./src-web/m/");
    }
    @Autowired
    protected ACLInterceptor aclInterceptor;
    @Autowired
    protected CurrentUserMethodArgumentHandler currentUserMethodArgumentHandler;
    @Autowired
    protected BindValidateMethodArgumentResolver bindValidateMethodArgumentResolver;
    @Autowired
    protected UserInfoCollectInterceptor userInfoCollectInterceptor;
    @Autowired
    protected SecureTokenInterceptor secureTokenInterceptor;
    @Autowired
    protected GlobalVariableInterceptor globalVariableInterceptor;
    @Autowired
    protected ClientInfoMethodArgumentHandler clientInfoMethodArgumentHandler;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(aclInterceptor).addPathPatterns("/**");
        registry.addInterceptor(secureTokenInterceptor).addPathPatterns("/**");
        registry.addInterceptor(userInfoCollectInterceptor).addPathPatterns("/**");                     //记录用户浏览日期
        registry.addInterceptor(globalVariableInterceptor).addPathPatterns("/**");                      //定义全局变量
    }

    //添加自定义方法参数解析器
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserMethodArgumentHandler);                                        //方法参数直接拿当前用户对象
        argumentResolvers.add(bindValidateMethodArgumentResolver);                                      //绑定业务校验
        argumentResolvers.add(clientInfoMethodArgumentHandler);                                         //获取客户端信息
    }

    @Autowired
    protected KittHandlerExceptionResolver kittHandlerExceptionResolver;
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(kittHandlerExceptionResolver);
    }
    @Bean
    public EmbeddedServletContainerCustomizer containerCustomizer(){
        return new EmbeddedServletContainerCustomizer() {
            @Override
            public void customize(ConfigurableEmbeddedServletContainer container) {
                container.addErrorPages(new ErrorPage(HttpStatus.BAD_REQUEST, "/400"));
                container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/404"));
                container.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/401"));
                container.addErrorPages(new ErrorPage(HttpStatus.FORBIDDEN, "/403"));
            }
        };
    }

    //JSR-303
    @Bean(name = "validator")
    public Validator createBeanValidator(){
        return new LocalValidatorFactoryBean();
    }

    @Autowired
    protected ObjectMapper objectMapper;
    @PostConstruct
    public void setThings(){
        objectMapper.registerModule(new Java8TimeModule());
    }
}
