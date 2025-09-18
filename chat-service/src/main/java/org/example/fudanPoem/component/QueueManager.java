package org.example.fudanPoem.component;

import org.example.fudanPoem.consumer.DynamicConsumerRegistrar;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class QueueManager {
    // 注入RabbitAdmin，用于动态创建队列和绑定
    private final RabbitAdmin rabbitAdmin;


    private final DirectExchange chatExchange;


    public QueueManager(RabbitAdmin rabbitAdmin,@Qualifier("chatExchange") DirectExchange chatExchange) {
        this.rabbitAdmin = rabbitAdmin;
        this.chatExchange = chatExchange;
    }

    // 核心方法：为指定用户动态创建队列并绑定到交换机
    public void createQueueIfNotExists(Long userId) {
        String queueName = "chat.user." + userId;

        Queue queue = QueueBuilder.durable(queueName)
                .build();

        // 动态创建队列（如果队列已存在，此操作会被忽略，不会报错）
        rabbitAdmin.declareQueue(queue);

        // 动态绑定队列到交换机（路由键=队列名）
        Binding binding = BindingBuilder.bind(queue)
                .to(chatExchange)
                .with(queueName);

        rabbitAdmin.declareBinding(binding);
    }
}