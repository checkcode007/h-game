package com.z.core.util;

import com.z.core.service.wallet.BankLogBizService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
//    private static final Logger log = LoggerFactory.getLogger(SpringContext.class);
    private static final Log log = LogFactory.getLog(SpringContext.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        log.info("ApplicationContext has been set: " +applicationContext);
        System.err.println("ApplicationContext has been set: " + applicationContext);
    }

    /**
     * 获取指定类型的Bean
     * @param clazz Bean的类型
     * @param <T> 返回值类型
     * @return T 类型的 Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            log.error("ApplicationContext is not initialized");
            throw new IllegalStateException("ApplicationContext is not initialized");
        }
        try {
            return applicationContext.getBean(clazz);
        } catch (BeansException e) {
            log.error("Error getting bean of type:"+clazz.getName(), e);
            throw e; // rethrow to ensure caller can handle it properly
        }
    }

    /**
     * 通过Bean名称获取Bean
     * @param name Bean的名称
     * @param <T> 返回值类型
     * @return T 类型的 Bean
     */
    public static <T> T getBean(String name) {
        if (applicationContext == null) {
            log.error("ApplicationContext is not initialized");
            throw new IllegalStateException("ApplicationContext is not initialized");
        }
        try {
            return (T) applicationContext.getBean(name);
        } catch (BeansException e) {
            log.error("Error getting bean with name :"+ name, e);
            throw e; // rethrow to ensure caller can handle it properly
        }
    }

}
