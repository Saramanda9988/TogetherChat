package com.luna.websocketserver;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient // 启用服务发现
@ComponentScan(basePackages = {
        "com.luna.common",     // 扫描公共模块
        "com.luna.websocketserver"  // 扫描用户服务模块
})
@EnableDubbo
@DubboComponentScan
public class WebsocketServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketServerApplication.class, args);
    }

}
