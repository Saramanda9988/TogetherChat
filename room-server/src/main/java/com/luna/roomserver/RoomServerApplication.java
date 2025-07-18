package com.luna.roomserver;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.luna.common",     // 扫描公共模块
        "com.luna.roomserver"  // 扫描房间服务模块
})
@EnableDubbo
@DubboComponentScan
@MapperScan("com.luna.roomserver.room.mapper") // 扫描房间服务的Mapper接口
public class RoomServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoomServerApplication.class, args);
    }
}
