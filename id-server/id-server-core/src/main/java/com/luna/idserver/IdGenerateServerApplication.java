package com.luna.idserver;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.luna.common",     // 扫描公共模块
        "com.luna.idserver"  // 扫描房间服务模块
})
@EnableDubbo
@DubboComponentScan
public class IdGenerateServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdGenerateServerApplication.class, args);
    }
}
