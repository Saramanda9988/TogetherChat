package com.luna.togetherchat.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/togetherchat?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123456";

        FastAutoGenerator.create(url, username, password)
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("LunaRain_079") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java") // 输出路径
                            .disableOpenDir() // 不自动打开输出目录
                            .enableSpringdoc(); // 启用 Springdoc 注解（OpenAPI 支持）
                })
                // 数据源配置
                .dataSourceConfig(builder -> builder.schema("togetherchat"))
                // 包配置
                .packageConfig(builder -> {
                    builder.parent("com.luna.togetherchat") // 父包名
                            .controller("controller") // Controller 包名
                            .entity("domain.entity") // 实体类包名
                            .mapper("mapper") // Mapper 接口包名
                            .service("service") // Service 接口包名
                            .serviceImpl("service.impl") // Service 实现类包名
                            .xml("mapper") // Mapper XML 文件包名
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml,
                                    System.getProperty("user.dir") + "/src/main/resources/mapper"
                            )); // Mapper XML 输出路径
                })
                // 策略配置
                .strategyConfig(builder -> {
                    // 包含的表
                    builder.addInclude(
                            // 聊天模块
                            "chat_message", "chat_group", "chat_group_member", "chat_group_permission",
                            // 通话模块
                            "call_session", "call_participant", "live_broadcast",
                            // 协作模块
                            "collab_document", "collab_whiteboard", "collab_draw_operation",
                            // 用户模块
                            "user_info", "user_blacklist"
                    );

                    // 控制器配置
                    builder.controllerBuilder()
                            .enableRestStyle(); // 启用 RESTful 风格（@RestController）

                    // 实体类配置
                    builder.entityBuilder()
                            .enableLombok() // 启用 Lombok 注解
                            .enableTableFieldAnnotation(); // 启用字段注释

                    // Mapper 配置
                    builder.mapperBuilder()
                            .enableFileOverride() // 覆盖已存在的文件
                            .formatMapperFileName("%sMapper") // Mapper 文件命名格式
                            .formatXmlFileName("%sMapper"); // XML 文件命名格式

                    // Service 配置
                    builder.serviceBuilder()
                            .enableFileOverride() // 覆盖已存在的文件
                            .formatServiceFileName("%sService") // Service 接口命名
                            .formatServiceImplFileName("%sServiceImpl"); // Service 实现类命名

                    // 字段自动填充配置
                    builder.entityBuilder()
                            .addTableFills(new Column("createTime", FieldFill.INSERT))
                            .addTableFills(new Column("updateTime", FieldFill.INSERT_UPDATE));

                    // 逻辑删除字段配置
                    builder.entityBuilder()
                            .logicDeleteColumnName("is_deleted"); // 逻辑删除字段名

                    // 忽略表前缀
                    builder.addTablePrefix("chat_", "call_", "live_", "collab_", "user_");
                })
                // 模板引擎
                .templateEngine(new FreemarkerTemplateEngine())
                // 执行生成
                .execute();
    }
}
