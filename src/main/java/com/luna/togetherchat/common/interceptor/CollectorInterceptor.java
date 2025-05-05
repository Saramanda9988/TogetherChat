package com.luna.togetherchat.common.interceptor;

import cn.hutool.extra.servlet.JakartaServletUtil;
import com.jxc.tuanchat.common.domain.dto.RequestInfo;
import com.jxc.tuanchat.common.utils.RequestHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

/**
 * 信息收集的拦截器
 */
@Order(1)
@Slf4j
@Component
public class CollectorInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        RequestInfo info = new RequestInfo();
        info.setUserId(Optional.ofNullable(request.getAttribute(TokenInterceptor.ATTRIBUTE_UID))
                .map(Object::toString)
                .map(Long::parseLong)
                .orElse(null));

        info.setIp(JakartaServletUtil.getClientIP(request));

        RequestHolder.set(info);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        RequestHolder.remove();
    }

}