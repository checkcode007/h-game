package com.z.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

//@Log4j2
@Component
public class SpringContext implements ApplicationContextAware {
    protected Logger log = LoggerFactory.getLogger(getClass());
    @Autowired
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        log.info("ApplicationContext has been set: {}", applicationContext);
    }

    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
//            log.error("ApplicationContext is null");
            throw new IllegalStateException("ApplicationContext is not initialized");
        }
        return applicationContext.getBean(clazz);
    }


    public static  <T> T getBean(String name){
        return (T) applicationContext.getBean(name);
    }

}