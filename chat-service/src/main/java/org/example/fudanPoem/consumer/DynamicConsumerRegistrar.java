package org.example.fudanPoem.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.example.fudanPoem.entity.ChatMessage;
import org.example.fudanPoem.webSocket.ChatWebSocketHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
public class DynamicConsumerRegistrar {
    @Autowired
    private SimpleMessageListenerContainer listenerContainer;
    @Autowired
    private ChatWebSocketHandler webSocketHandler;
    @Autowired
    private MessageConverter jackson2JsonMessageConverter;
    @Autowired
    private ObjectMapper objectMapper;

    // 初始化时设置一次全局监听器（只执行一次）
    @PostConstruct
    public void initGlobalListener() {
        // 创建全局监听器（处理所有队列的消息）
        MessageListenerAdapter globalAdapter = new MessageListenerAdapter();
        globalAdapter.setDelegate(new Object() {
            // 处理所有队列的消息，通过队列名区分用户
            public void handleMessage(ChatMessage message) {
                Long receiverId = message.getReceiverId();

                log.info("动态消费者收到消息：接收者[" + receiverId + "]，发送者[" + message.getSenderId() + "]，内容：" + message.getContent());

                //todo:此处需要处理离线消息存储以及推送消息的已读
                WebSocketSession session = webSocketHandler.getUserSessionMap().get(receiverId);
                if (session != null && session.isOpen()) {
                    try {
                        String jsonMessage = objectMapper.writeValueAsString(message);
                        session.sendMessage(new TextMessage(jsonMessage));
                        log.info("\"消息已推送给用户[" + receiverId + "]的 WebSocket\"");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    log.info("用户[" + receiverId + "]的 WebSocket 未连接，消息推送作其他处理");
                }
            }
        });
        // 设置转换器
        globalAdapter.setMessageConverter(jackson2JsonMessageConverter);
        // 容器只关联这一个全局监听器（只设置一次）
        listenerContainer.setMessageListener(globalAdapter);
    }

    // 注册消费者时，只需添加队列即可（无需再设置监听器）
    public void registerConsumerForUser(Long receiverId) {
        String queueName = "chat.user." + receiverId;
        // 动态添加队列（容器会用全局监听器处理这个队列的消息）
        if (!Arrays.asList(listenerContainer.getQueueNames()).contains(queueName)) {
            listenerContainer.addQueueNames(queueName);
        }
        // 启动容器（如果未启动）
        if (!listenerContainer.isRunning()) {
            listenerContainer.start();
        }
    }
}