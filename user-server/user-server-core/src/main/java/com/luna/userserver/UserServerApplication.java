package com.luna.userserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient  // 启用服务发现
@MapperScan({"com.luna.userserver.**.mapper"})
@ComponentScan(basePackages = {
        "com.luna.common",     // 扫描公共模块
        "com.luna.userserver"  // 扫描用户服务模块
})
public class UserServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServerApplication.class, args);
    }
}
