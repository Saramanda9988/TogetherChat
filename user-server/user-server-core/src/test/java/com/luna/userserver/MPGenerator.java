package com.luna.userserver;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Property;

import java.util.Collections;

public class MPGenerator {
    public static void main(String[] args) {
        // 使用 FastAutoGenerator 快速配置代码生成器

        FastAutoGenerator
                .create(
                        "jdbc:mysql://localhost:3306/togetherchat?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai",
                        "root",
                        "123456"
                )
                .globalConfig(builder -> {
                    builder.disableOpenDir() // 不允许自动打开输出目录
                            .author("LunaRain_079") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java") // 指定输出目录
                            .enableSpringdoc();
                })
                .packageConfig(builder -> {
                    builder.parent("com.luna.togetherchat.conversation") // 设置父包名
                            .controller("controller") // 设置 Controller 包名
                            .entity("domain.entity") // 设置实体类包名
                            .mapper("mapper") // 设置 Mapper 接口包名
                            .service("service") // 设置 Service 接口包名
                            .serviceImpl("service.impl") // 设置 Service 实现类包名
                            .xml("mapper") // 设置 Mapper XML 文件包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")); // 设置 Mapper XML 文件的输出路径
                })

                .strategyConfig(builder -> {
                    builder.addInclude(
                                    "conversation_member",
                                    "conversation"
                            )// 设置需要生成的表名
                            .controllerBuilder().enableRestStyle().entityBuilder().enableLombok().enableTableFieldAnnotation()
                            .enableFileOverride()
                            .logicDeleteColumnName("isDelete").addTableFills(new Property("createTime", FieldFill.INSERT))
                            .addTableFills(new Property("updateTime", FieldFill.INSERT_UPDATE))
                            .mapperBuilder().enableFileOverride().formatMapperFileName("%sMapper").formatXmlFileName("%sMapper")
                            .serviceBuilder().enableFileOverride().formatServiceFileName("%sService").formatServiceImplFileName("%sServiceImpl");
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
                .execute(); // 执行生成
    }
}
