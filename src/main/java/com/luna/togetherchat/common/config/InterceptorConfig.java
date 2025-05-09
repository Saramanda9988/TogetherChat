package com.luna.togetherchat.common.config;


import com.luna.togetherchat.common.interceptor.CollectorInterceptor;
import com.luna.togetherchat.common.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Description: 配置所有拦截器
 *
 * CorsFilter 首先处理跨域问题
 * TokenInterceptor 验证用户身份
 * CollectorInterceptor 收集请求信息
 * Controller方法执行
 * WebLogAspect 在整个过程中记录日志
 *
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-05
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private TokenInterceptor tokenInterceptor;
    @Autowired
    private CollectorInterceptor collectorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/capi/**");
        registry.addInterceptor(collectorInterceptor)
                .addPathPatterns("/capi/**");
    }
}
