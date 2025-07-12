package com.luna.webrtcserver.common.config;

import com.luna.webrtcserver.common.interceptor.CorsFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter();
    }
}
