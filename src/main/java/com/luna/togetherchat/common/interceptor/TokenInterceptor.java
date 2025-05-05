package com.luna.togetherchat.common.interceptor;

import com.luna.togetherchat.common.annotation.PublicAPI;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.Optional;

/**
 * 改进版的Token拦截器，使用注解方式标记公共API
 * 若要使用此拦截器，请在WebMvcConfigurer配置类中替换原有拦截器
 */
@Order(-2)
@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_SCHEMA = "Bearer ";
    public static final String ATTRIBUTE_UID = "uid";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 判断是否为公共API（使用注解方式）
        if (isPublicAPI(handler)) {return true;}

        // 获取用户登录token
        String header = request.getHeader(AUTHORIZATION_HEADER);
        String token = Optional.ofNullable(header)
                .filter(h -> h.startsWith(AUTHORIZATION_SCHEMA))
                .map(h -> h.substring(AUTHORIZATION_SCHEMA.length()))
                .orElse(null);

        // Long validUid = loginService.getValidUid(token);

        Long validUid = null;
        try {
            validUid = Long.parseLong(Objects.requireNonNull(token));
        } catch (Exception e) {
            // 非公共接口且token无效，拒绝访问
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        //有登录态
        request.setAttribute(ATTRIBUTE_UID, validUid);
        return true;
    }

    /**
     * 通过注解判断是否为公共API
     *
     * @param handler 处理器
     * @return 是否为公共API
     */
    private boolean isPublicAPI(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            // 检查方法上是否有@PublicAPI注解
            PublicAPI methodAnnotation = handlerMethod.getMethodAnnotation(PublicAPI.class);
            if (methodAnnotation != null) {return true;}

            // 检查控制器类上是否有@PublicAPI注解
            PublicAPI classAnnotation = handlerMethod.getBeanType().getAnnotation(PublicAPI.class);
            return classAnnotation != null;
        }
        return false;
    }

}
