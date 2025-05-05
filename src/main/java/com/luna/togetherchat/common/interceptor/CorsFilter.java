package com.luna.togetherchat.common.interceptor;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;

import java.io.IOException;


@Slf4j
@Order(1)
@WebFilter(filterName = "testFilter")
public class CorsFilter implements Filter {
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse) servletResponse;
    HttpServletRequest request = (HttpServletRequest)servletRequest;

    String origin = request.getHeader("Origin");
    response.setHeader("Access-Control-Allow-Origin", origin);
    response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");
    response.setHeader("Access-Control-Max-Age", "3600");
    // 自定义的请求头也要添加
    response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Tenant-Id, Blade-Auth, Content-Type, Authorization, credential, X-XSRF-TOKEN, token, username, client");
    response.setHeader("Access-Control-Allow-Credentials", "true");
    String method = request.getMethod();
    if(method.equalsIgnoreCase("OPTIONS")){
      servletResponse.getOutputStream().write("Success".getBytes("utf-8"));
    }else{
      filterChain.doFilter(servletRequest, servletResponse);
    }
//    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void destroy() {

  }
}