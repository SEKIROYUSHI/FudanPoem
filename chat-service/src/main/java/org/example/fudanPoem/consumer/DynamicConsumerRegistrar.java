package org.example.fudanPoem.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.example.fudanPoem.entity.ChatMessage;
import org.example.fudanPoem.service.impl.ChatMessageServiceImpl;
import org.example.fudanPoem.webSocket.ChatWebSocketHandler;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    @Autowired
    @Lazy
    private ChatMessageServiceImpl messageService;

    // 初始化时设置一次全局监听器（只执行一次）
    @PostConstruct
    public void initGlobalListener() {
        ChannelAwareMessageListener globalListener = new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message amqpMessage, Channel channel) throws Exception {
                ChatMessage message = (ChatMessage) jackson2JsonMessageConverter.fromMessage(amqpMessage);
                Long receiverId = message.getReceiverId();
                Long senderId = message.getSenderId();

                log.info("动态消费者收到消息：接收者[" + receiverId + "]，发送者[" +senderId + "]，内容：" + message.getContent());

                // 2. 获取消息的DeliveryTag（RabbitMQ用于标识消息的唯一ID，必须用于手动确认）
                long deliveryTag = amqpMessage.getMessageProperties().getDeliveryTag();

                // 3. 尝试推送给WebSocket
                WebSocketSession session = webSocketHandler.getUserSessionMap().get(receiverId);
                if (session != null && session.isOpen()) {
                    try {
                        String jsonMessage = objectMapper.writeValueAsString(message);
                        session.sendMessage(new TextMessage(jsonMessage));
                        log.info("消息已推送给用户[" + receiverId + "]的 WebSocket");

                        // 3.1 推送成功：手动确认消息（通知RabbitMQ删除该消息）
                        channel.basicAck(deliveryTag, false);

                       messageService.markUnreadAsRead(senderId, receiverId);
                    } catch (IOException e) {
                        log.error("推送消息给用户[" + receiverId + "]失败", e);
                        // 3.2 推送失败：拒绝消息并重新入队（等待下次重试）
                        channel.basicNack(deliveryTag, false, true);
                    }
                } else {
                    log.info("用户[" + receiverId + "]的 WebSocket 未连接，消息保留在队列");
                    // 4. 用户离线：不确认消息，也不拒绝（消息会留在队列，等待用户上线后重新消费）
                }
            }
        };

        // 容器关联手动确认的监听器
        listenerContainer.setMessageListener(globalListener);
    }
    // 注册消费者时，只需添加队列即可（无需再设置监听器）
    public void registerConsumerForUser(Long receiverId) {
        String queueName = "chat.user." + receiverId;
        // 动态添加队列（容器会用全局监听器处理这个队列的消息）
            listenerContainer.addQueueNames(queueName);
        // 启动容器（如果未启动）
        if (!listenerContainer.isRunning()) {
            listenerContainer.start();
        }
    }

    public void unregisterConsumerForUser(Long receiverId) {
        String queueName = "chat.user." + receiverId;
        // 从容器中移除队列（监听器不再处理该队列的消息）
        listenerContainer.removeQueueNames(queueName);
        log.info("用户[" + receiverId + "]已离线，停止监听队列：" + queueName);
    }
}