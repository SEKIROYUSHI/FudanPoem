package org.example.fudanPoem.consumer;

import org.example.fudanPoem.entity.ChatMessage;
import org.example.fudanPoem.webSocket.ChatWebSocketHandler;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Arrays;

@Component
public class DynamicConsumerRegistrar {

    @Autowired
    private SimpleMessageListenerContainer listenerContainer; // 容器（只管理队列）

    @Autowired
    private ChatWebSocketHandler webSocketHandler; // WebSocket处理器

    // 1. 注入你在 RabbitMQConfig 中定义的 JSON 转换器
    @Autowired
    private MessageConverter jackson2JsonMessageConverter;

    /**
     * 为指定用户的队列注册消费者
     */
    public void registerConsumerForUser(Long receiverId) {
        String queueName = "chat.user." + receiverId;

        // 2. 创建消息监听器适配器（负责处理消息，这里配置转换器）
        MessageListenerAdapter listenerAdapter = new MessageListenerAdapter();
        // 设置消息处理的"委托对象"（里面的 handleMessage 方法会处理消息）
        listenerAdapter.setDelegate(new Object() {
            // 消息处理方法：参数是 ChatMessage（转换器会自动把JSON转成这个对象）
            public void handleMessage(ChatMessage message) {
                System.out.println("动态消费者收到消息：接收者[" + receiverId + "]，发送者[" + message.getSenderId() + "]，内容：" + message.getContent());

                // 3. 尝试通过 WebSocket 推送给前端
                WebSocketSession session = webSocketHandler.getUserSessionMap().get(receiverId);
                if (session != null && session.isOpen()) {
                    try {
                        // 把 ChatMessage 转成 JSON 字符串发给前端
                        String jsonMessage = new com.fasterxml.jackson.databind.ObjectMapper()
                                .writeValueAsString(message);
                        session.sendMessage(new TextMessage(jsonMessage));
                        System.out.println("消息已推送给用户[" + receiverId + "]的 WebSocket");
                    } catch (IOException e) {
                        System.err.println("推送消息失败：" + e.getMessage());
                    }
                } else {
                    System.out.println("用户[" + receiverId + "]不在线，消息暂存（可后续加离线逻辑）");
                }
            }
        });

        // 关键：把转换器配置到适配器上（正确位置！）
        listenerAdapter.setMessageConverter(jackson2JsonMessageConverter);

        // 4. 给容器添加要监听的队列
        if (!Arrays.asList(listenerContainer.getQueueNames()).contains(queueName)) {
            listenerContainer.addQueueNames(queueName);
        }
        // 5. 给容器设置监听器适配器
        listenerContainer.setMessageListener(listenerAdapter);
        // 6. 启动容器（如果没启动）
        if (!listenerContainer.isRunning()) {
            listenerContainer.start();
            System.out.println("RabbitMQ 消费者容器已启动");
        }
    }
}