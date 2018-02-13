package kitt.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by xiangyang on 15-7-7.
 */
@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {


    private static ApplicationContext applicationContext = null;
    private static Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        logger.info("注入ApplicationContext到SpringContextHolder:"
                + applicationContext);
        SpringContextHolder.applicationContext = applicationContext;
    }

    //清空applicationContext
    public void destroy() throws Exception {
        applicationContext = null;
    }

    /**
     * 根据bean名称取出想要的bean
     *
     * @param beanName
     * @return
     */
    public static <T> T getBean(String beanName) {
        checkApplicationContext();
        return (T) applicationContext.getBean(beanName);
    }

    /**
     * 根据bean的Class取出想要的bean
     *
     * @param
     * @return
     */
    public static <T> T getBean(Class<?> classType) {
        checkApplicationContext();
        return (T) applicationContext.getBean(classType);
    }

    private static void checkApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException(
                    "applicaitonContext未注入,请在spring上下文中注册SpringContextHolder");
        }
    }

}
