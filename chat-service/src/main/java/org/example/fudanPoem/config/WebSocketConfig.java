package org.example.fudanPoem.config;

import org.example.fudanPoem.webSocket.ChatWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;
import org.example.fudanPoem.webSocket.AuthHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements org.springframework.web.socket.config.annotation.WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    // 2. 构造函数：把处理器传进来（这是Spring依赖注入的方式，之前讲过）
    public WebSocketConfig(ChatWebSocketHandler chatWebSocketHandler) {
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    /**
     * 注册WebSocket的“连接规矩”：前端怎么连、用哪个处理器处理连接
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                // ① 连接地址：前端要连这个地址 → ws://localhost:8080/ws/chat
                .addHandler(chatWebSocketHandler, "/ws/chat")
                .addInterceptors(new AuthHandshakeInterceptor())
                // ② 允许跨域：测试阶段必须加，否则前端连不上（后面上线再改严格点）
                .setAllowedOrigins("*");
    }

    /**
     * 给Spring Boot内置的服务器（Tomcat）用的：帮我们自动管理WebSocket连接
     * （没有这个Bean，WebSocket连接会失败，照抄就行）
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}