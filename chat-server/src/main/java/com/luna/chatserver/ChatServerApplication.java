package com.luna.chatserver;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.luna.chatserver",
    "com.luna.common"
})
@EnableDubbo
@DubboComponentScan
public class ChatServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServerApplication.class, args);
    }

}
