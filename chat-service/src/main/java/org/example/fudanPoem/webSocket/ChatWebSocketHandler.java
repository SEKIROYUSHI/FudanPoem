package org.example.fudanPoem.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.fudanPoem.component.QueueManager;
import org.example.fudanPoem.consumer.DynamicConsumerRegistrar;
import org.example.fudanPoem.dto.MessageRequestDTO;
import org.example.fudanPoem.service.impl.ChatMessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
@Data
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 2. 存储“用户ID → WebSocket连接”的映射表：
    //    - 比如用户123连了，就存 123 → 他的连接对象
    //    - ConcurrentHashMap：线程安全，多用户同时连也不会乱
    private final Map<Long, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();

    // 3. JSON工具：后面要把消息转成JSON字符串发给前端（比如把ChatMessage对象转成JSON）
    @Autowired
    private final ObjectMapper objectMapper;

    @Autowired
    private ChatMessageServiceImpl chatService;

    @Autowired
    @Lazy
    private DynamicConsumerRegistrar consumerRegistrar;

    /**
     * 4. 当用户成功建立WebSocket连接时，会自动调用这个方法
     *
     */

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage webSocketMessage) throws Exception {
        // 1. 解析前端发来的JSON消息（假设前端发的格式是 {senderId:1, receiverId:2, content:"你好"}）
        String json = webSocketMessage.getPayload();
        // 用全局 ObjectMapper 转成 DTO（避免重复创建）
        MessageRequestDTO dto = objectMapper.readValue(json, MessageRequestDTO.class);

        // 2. 校验发送者身份（从Session中获取当前连接的用户ID，避免伪造senderId）
        Long currentUserId = (Long) session.getAttributes().get("userId");

        // 3. 复用你现有业务逻辑（调用 sendMessage 发消息，走MQ+存库）
        try {
            chatService.sendMessage(currentUserId, dto.getReceiverId(), dto.getContent());
            // 4. 给发送端返回“发送成功”的响应（WebSocket也能双向通信）
            session.sendMessage(new TextMessage("{\"code\":200,\"msg\":\"发送成功\"}"));
        } catch (Exception e) {
            // 发送失败，返回错误
            session.sendMessage(new TextMessage("{\"code\":500,\"msg\":\"发送失败：" + e.getMessage() + "\"}"));
            log.error("WebSocket发送消息失败", e);
        }
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        // ② 把“用户ID和连接”存到映射表里
        userSessionMap.put(userId, session);

        consumerRegistrar.registerConsumerForUser(userId);

        // ③ 打印日志，方便我们看是否连成功
        log.info("用户[" + userId + "]连入WebSocket！当前在线人数：" + userSessionMap.size());
    }

    /**
     * 5. 当用户关闭WebSocket连接时，会自动调用这个方法
     *    （比如前端关了页面，或者断网了）
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        // ① 从映射表里删掉断开的连接（避免内存浪费）
        userSessionMap.entrySet().removeIf(entry -> entry.getValue().equals(session));

        consumerRegistrar.unregisterConsumerForUser(userId);

        // ② 打印日志
       log.info("用户断开WebSocket连接！当前在线人数：" + userSessionMap.size());
    }
}