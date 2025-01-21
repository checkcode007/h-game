package com.z.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
@ComponentScan(basePackages = {

        "com.z.dbmysql", "com.z.dbes","com.z.core" ,"com.z.common","com.z.core.service.game.slot" // 指定app模块的包
})
@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableAsync
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
//        ApplicationContext context = SpringApplication.run(CoreApplication.class, args);
//        SpringContext.setApplicationContext(context);
    }

}
