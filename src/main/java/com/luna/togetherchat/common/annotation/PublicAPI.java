package com.luna.togetherchat.common.annotation;

import java.lang.annotation.*;

/**
 * 标记公共API的注解，被此注解标记的API不需要token验证
 * 在微服务架构下，此注解可用于生成网关路由配置
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PublicAPI {
    /**
     * API描述
     */
    String value() default "";
    
    /**
     * 是否跳过网关鉴权
     */
    boolean skipAuth() default true;
}
