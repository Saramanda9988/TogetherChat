package com.luna.togetherchat.common.config;

import com.luna.togetherchat.common.interceptor.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter();
    }
}
