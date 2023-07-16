package com.king.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * web 服务启动类
 *
 * @author king
 * @version 1.0
 * @since 2023-06-20
 **/
@EnableCaching // 开启缓存
@SpringBootApplication(scanBasePackages = "com.king.*", exclude = {DataSourceAutoConfiguration.class})
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
