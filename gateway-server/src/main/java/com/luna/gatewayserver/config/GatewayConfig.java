package com.luna.gatewayserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class GatewayConfig {

    @Value("${togetherchat.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service路由
                .route("user-server", r -> r.path("/capi/user/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-server"))
                
                // Chat Service路由
                .route("chat-server", r -> r.path("/capi/chat/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://chat-server"))
                
                // Room Service路由
                .route("room-server", r -> r.path("/capi/room/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://room-server"))
                
                // WebRTC Service路由
                .route("webrtc-server", r -> r.path("/capi/webrtc/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://webrtc-server"))
                
                // Auth Service路由 - 公开接口
                .route("auth-service-public", r -> r.path("/capi/auth/login", "/capi/auth/register", "/capi/auth/refresh")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://auth-server"))
                
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // 允许的源
        corsConfig.setAllowedOrigins(allowedOrigins);
        
        // 允许的HTTP方法
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // 允许的请求头
        corsConfig.addAllowedHeader("*");
        
        // 允许携带凭证
        corsConfig.setAllowCredentials(true);
        
        // 预检请求的缓存时间
        corsConfig.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);
        
        return new CorsWebFilter(source);
    }
}