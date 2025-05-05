package com.luna.togetherchat.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Property;

import java.util.Collections;

public class ChatCodeGenerator {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/togetherchat?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123456";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("LunaRain_079")
                            .outputDir(System.getProperty("user.dir") + "/src/main/java")
                            .disableOpenDir()
                            .enableSpringdoc();
                })
                .dataSourceConfig(builder -> builder.schema("togetherchat"))
                .packageConfig(builder -> {
                    builder.parent("com.luna.togetherchat.chat")
                            .controller("controller")
                            .entity("domain.entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .xml("mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    System.getProperty("user.dir") + "/src/main/resources/mapper/chat"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("chat_message", "chat_group", "chat_group_member", "chat_group_permission");
                    builder.controllerBuilder().enableRestStyle();
                    builder.entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .addTableFills(new Property("createTime", FieldFill.INSERT))
                            .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                            .logicDeleteColumnName("is_deleted");
                    builder.mapperBuilder().enableFileOverride();
                    builder.serviceBuilder().enableFileOverride();
                    builder.addTablePrefix("chat_");
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
