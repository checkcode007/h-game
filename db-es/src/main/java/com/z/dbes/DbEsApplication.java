package com.z.dbes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.z.dbes.service","com.z.dbes.config"     // 指定app模块的包
})
public class DbEsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbEsApplication.class, args);
    }

}
