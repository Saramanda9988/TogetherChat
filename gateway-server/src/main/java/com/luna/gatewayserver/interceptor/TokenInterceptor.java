package com.luna.gatewayserver.interceptor;

import com.luna.common.annotation.PublicAPI;
import com.luna.common.domain.dto.RequestInfo;
import com.luna.common.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.HandlerInterceptor;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;

/**
 * 改进版的Token拦截器，使用注解方式标记公共API
 * 若要使用此拦截器，请在WebMvcConfigurer配置类中替换原有拦截器
 */
@Order(-2)
@Slf4j
@Component
public class TokenInterceptor implements GlobalFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";

    // 网关传入的用户ID请求头，网关在验证token后设置
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_APP_ID = "X-App-Id";
    public static final String HEADER_CLIENT_TYPE = "X-Client-Type";
    public static final String HEADER_IMEI = "X-IMEI";

    // 需要排除的公共路径
    @Value("${togetherchat.interceptor.exclude_paths}")
    private String[] EXCLUDED_PATHS;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        // 检查是否为排除路径
        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.startsWith(excludedPath)) {
                return chain.filter(exchange);
            }
        }

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = Optional.ofNullable(headers.getFirst(AUTHORIZATION_HEADER))
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.substring(AUTHORIZATION_SCHEMA.length()))
                .orElse(null);

        try {

            RequestInfo requestInfo = JwtUtils.parseJwtToken(token);

            if (requestInfo == null || requestInfo.getUserId() == null) {
                throw new RuntimeException("token无效");
            }

            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                    .header(HEADER_USER_ID, String.valueOf(requestInfo.getUserId()))
                    .header(HEADER_IMEI, requestInfo.getImei())
                    .header(HEADER_APP_ID, String.valueOf(requestInfo.getAppId()))
                    .header(HEADER_CLIENT_TYPE, String.valueOf(requestInfo.getClientType()))
                    .build();
            // 继续执行过滤器链
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (Exception e) {
            // token无效，拒绝访问
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}