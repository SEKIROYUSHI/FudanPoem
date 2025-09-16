package org.example.fudanPoem.webSocket;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    // 握手前执行：转移 userId
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // 把 WebSocket 的请求转成普通 HTTP 请求，才能拿到之前存的 userId
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            // 从 HTTP 请求里取 userId（你 JWT 过滤器里存的）
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId != null) {
                // 把 userId 存到 WebSocketSession 的属性里
                attributes.put("userId", userId);
                return true; // 允许握手
            }
        }
        return false; // 没拿到 userId，拒绝握手
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {}
}