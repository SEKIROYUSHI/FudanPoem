package org.example.fudanPoem.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RegisterEventConsumerConfig {

    // 队列名称：建议包含服务名+事件类型，避免重名
    public static final String LOGIN_EVENT_QUEUE_NAME = "chat.service.login.event.queue";

    // 1. 定义聊天服务专用的登录事件队列（持久化，避免服务重启后消息丢失）
    @Bean
    public Queue loginEventQueue() {
        return QueueBuilder.durable(LOGIN_EVENT_QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", "") // 可选：配置死信队列
                .build();
    }

    // 2. 将队列绑定到公共事件交换机（只关心"event.user.register"路由的消息）
    @Bean
    public Binding loginEventBinding(Queue loginEventQueue, TopicExchange eventExchange) {
        return BindingBuilder.bind(loginEventQueue)
                .to(eventExchange) // 直接使用注入的eventExchange（来自common的Bean）
                .with("event.user.register"); // 只接收该路由键的消息
    }
}