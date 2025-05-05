package com.luna.togetherchat.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("tuanchat")
                .packagesToScan("com.jxc.tuanchat")
                .build();
    }

    @Bean
    public OpenAPI springShopOpenAPI() {
        Contact contact = new Contact();
        contact.setUrl("https://space.bilibili.com/108753930");
        contact.setName("降星驰");
        contact.setEmail("starrybamboo@qq.com");
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("Authorization",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"))
                .info(new Info().title("团剧共创")
                        .contact(contact)
                        .description("接口文档")
                        .version("v1.0"));
    }
}
