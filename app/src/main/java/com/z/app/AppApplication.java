package com.z.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

//@Import(MysqlConf.class)  // 导入 mysql 模块的配置类s
@SpringBootApplication
@EnableScheduling
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "com.z.dbmysql","com.z.model", "com.z.dbes","com.z.core","com.z.model.proto"
//        "com.z.dbmysql", "com.z.dbes","com.z.core"  // 指定app模块的包
})
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}
