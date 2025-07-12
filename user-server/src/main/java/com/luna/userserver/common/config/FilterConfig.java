package com.luna.userserver.common.config;

import com.luna.userserver.common.interceptor.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter();
    }
}
