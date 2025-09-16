package org.example.fudanPoem.component;

import org.example.fudanPoem.consumer.DynamicConsumerRegistrar;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class QueueManager {
    // 注入RabbitAdmin，用于动态创建队列和绑定
    private final RabbitAdmin rabbitAdmin;

    // 注入交换机（确保交换机已存在，可通过@Bean提前创建）
    private final DirectExchange chatExchange;

    private final DynamicConsumerRegistrar consumerRegistrar;

    public QueueManager(RabbitAdmin rabbitAdmin,@Qualifier("chatExchange") DirectExchange chatExchange,DynamicConsumerRegistrar consumerRegistrar) {
        this.rabbitAdmin = rabbitAdmin;
        this.chatExchange = chatExchange;
        this.consumerRegistrar = consumerRegistrar;
    }

    // 核心方法：为指定用户动态创建队列并绑定到交换机
    public void createQueueIfNotExists(Long userId) {
        // 1. 队列名称规则：和路由键一致（chat.user.{userId}）
        String queueName = "chat.user." + userId;

        // 2. 定义队列（持久化、非排他、不自动删除）
        Queue queue = QueueBuilder.durable(queueName)  // durable=true：持久化，重启不丢失
                .build();

        // 3. 动态创建队列（如果队列已存在，此操作会被忽略，不会报错）
        rabbitAdmin.declareQueue(queue);

        // 4. 动态绑定队列到交换机（路由键=队列名）
        Binding binding = BindingBuilder.bind(queue)
                .to(chatExchange)
                .with(queueName); // 路由键=队列名，和发送消息时的routingKey一致

        rabbitAdmin.declareBinding(binding);

        consumerRegistrar.registerConsumerForUser(userId);
    }
}