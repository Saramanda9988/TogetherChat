package com.luna.common.interceptor;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 用户上下文过滤器
 * 从网关传入的请求头中提取用户信息，放入请求属性中供后续业务处理使用
 */
@Component
@Order(-1)
@Slf4j
public class UserContextFilter implements Filter {

    // 网关传入的用户ID请求头，网关在验证token后设置
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_APP_ID = "X-App-Id";
    public static final String HEADER_CLIENT_TYPE = "X-Client-Type";
    public static final String HEADER_IMEI = "X-IMEI";
    // 与原TokenInterceptor保持一致的属性名
    public static final String ATTRIBUTE_UID = "uid";
    public static final String ATTRIBUTE_APP_ID = "appId";
    public static final String ATTRIBUTE_CLIENT_TYPE = "clientType";
    public static final String ATTRIBUTE_IMEI = "imei";

    /**
     * UserContextFilter在微服务架构中的工作原理：
     * 1. 网关验证token后，提取用户信息（如用户ID）并添加到请求头中
     * 2. 请求转发到微服务时，携带这些自定义请求头
     * 3. 该过滤器从请求头中获取用户信息，并放入请求属性中
     * 4. 微服务中的业务代码可以通过请求属性获取用户信息
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 从请求头中获取网关传入的用户ID
        String userId = request.getHeader(HEADER_USER_ID);
        String imei = request.getHeader(HEADER_IMEI);
        String appIdStr = request.getHeader(HEADER_APP_ID);
        String typeStr = request.getHeader(HEADER_CLIENT_TYPE);

        if (userId != null && !userId.isEmpty() && appIdStr != null && !appIdStr.isEmpty()
                && typeStr != null && !typeStr.isEmpty()) {
            try {
                Long uid = Long.parseLong(userId);
                Integer appId = Integer.parseInt(appIdStr);
                Integer clientType = Integer.parseInt(typeStr);
                // 将用户ID设置到请求属性中，与原TokenInterceptor行为一致
                request.setAttribute(ATTRIBUTE_UID, uid);
                request.setAttribute(ATTRIBUTE_IMEI, imei);
                request.setAttribute(ATTRIBUTE_APP_ID, appId);
                request.setAttribute(ATTRIBUTE_CLIENT_TYPE, clientType);
                log.debug("Set user context: uid={}", uid);
            } catch (NumberFormatException e) {
                log.warn("Invalid user ID format in request header: {}, {}, {}", userId, appIdStr, typeStr);
            }
        }
        
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
