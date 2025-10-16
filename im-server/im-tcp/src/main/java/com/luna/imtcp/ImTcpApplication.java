package com.luna.imtcp;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.luna.common",     // 扫描公共模块
})
@EnableDubbo
@DubboComponentScan
public class ImTcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImTcpApplication.class, args);
    }

}
