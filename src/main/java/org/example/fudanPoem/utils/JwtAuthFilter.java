package org.example.fudanPoem.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.fudanPoem.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class JwtAuthFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    // 不需要验证Token的接口（白名单）
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/fudanpoem/user/login",
            "/fudanpoem/user/register"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. 获取请求路径
        String requestURI = httpRequest.getRequestURI();

        // 2. 白名单接口直接放行
        if (WHITE_LIST.contains(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        String token = httpRequest.getHeader("Authorization");

        // 验证 Token 不存在时
        if (token == null || token.trim().isEmpty()) {
            httpResponse.setStatus(401);
            httpResponse.setContentType("application/json;charset=UTF-8");
            // 将 Result 对象序列化为 JSON 字符串
            String json = objectMapper.writeValueAsString(Result.error("未提供Token"));
            httpResponse.getWriter().write(json);
            return;
        }

        // 验证 Token 无效时
        if (!jwtUtil.isTokenValid(token)) {
            httpResponse.setStatus(401);
            httpResponse.setContentType("application/json;charset=UTF-8");
            String json = objectMapper.writeValueAsString(Result.error("Token无效或已过期"));
            httpResponse.getWriter().write(json);
            return;
        }

        // 6. Token有效，继续执行后续过滤器
        chain.doFilter(request, response);
    }
}