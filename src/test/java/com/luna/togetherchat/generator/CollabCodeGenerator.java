package com.luna.togetherchat.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Property;

import java.util.Collections;

public class CollabCodeGenerator {
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
                    builder.parent("com.luna.togetherchat.collab")
                            .controller("controller")
                            .entity("domain.entity")
                            .mapper("mapper")
                            .service("service")
                            .serviceImpl("service.impl")
                            .xml("mapper")
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    System.getProperty("user.dir") + "/src/main/resources/mapper/collab"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("collab_document", "collab_whiteboard", "collab_draw_operation");
                    builder.controllerBuilder().enableRestStyle();
                    builder.entityBuilder()
                            .enableLombok()
                            .enableTableFieldAnnotation()
                            .addTableFills(new Property("createTime", FieldFill.INSERT))
                            .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                            .logicDeleteColumnName("is_deleted");
                    builder.mapperBuilder().enableFileOverride();
                    builder.serviceBuilder().enableFileOverride();
                    builder.addTablePrefix("collab_");
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
