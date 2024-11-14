package com.z.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@ComponentScan(basePackages = {
        "com.z.dbmysql", "com.z.dbes","com.z.core"     // 指定app模块的包
})
@SpringBootApplication
@EnableDiscoveryClient
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

}
