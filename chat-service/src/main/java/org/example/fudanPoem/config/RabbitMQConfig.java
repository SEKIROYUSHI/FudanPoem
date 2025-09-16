package org.example.fudanPoem.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RabbitMQConfig {

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    // 配置「RabbitTemplate」：发送消息时用的模板，绑定上面的转换器
    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jackson2JsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            // correlationData：发送消息时传入的唯一标识（可跟踪具体消息）
            // ack：true=消息已被交换机接收；false=未接收
            // cause：如果ack=false，这里会说明失败原因（如交换机不存在）

            String messageId = correlationData != null ? correlationData.getId() : "未知ID";
            if (ack) {
                log.info("消息[{}]已成功到达交换机", messageId);
            } else {
                log.error("消息[{}]未到达交换机！失败原因：{}", messageId, cause);
            }
        });

        //todo:此处的回调函数还没有成功报错过
        rabbitTemplate.setReturnsCallback(returnedMessage -> {
            String messageId = returnedMessage.getMessage().getMessageProperties().getCorrelationId();
            log.error(
                    "消息[{}]路由失败！交换机={}, 路由键={}, 错误码={}, 原因={}",
                    messageId,
                    returnedMessage.getExchange(),
                    returnedMessage.getRoutingKey(),
                    returnedMessage.getReplyCode(),
                    returnedMessage.getReplyText()
            );
            // 路由失败时，手动抛异常（会被发送消息的线程捕获）
            throw new RuntimeException(
                    "消息路由失败：" + returnedMessage.getReplyText()
            );
        });
        return rabbitTemplate;
    }

    // 4. 配置「监听器容器工厂」：接收消息时用的容器，也绑定转换器
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter jackson2JsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        // 绑定连接工厂
        factory.setConnectionFactory(connectionFactory);
        // 给监听器设置消息转换器：这样接收消息时，能把JSON反序列化成Java对象（比如你的User）
        factory.setMessageConverter(jackson2JsonMessageConverter);

        SimpleMessageListenerContainer container = factory.createListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean(name = "chatExchange")
    public DirectExchange chatExchange() {
        return new DirectExchange("chat.exchange", false, false);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        // 传入连接工厂，创建RabbitAdmin实例
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(
            ConnectionFactory connectionFactory,
            MessageConverter jackson2JsonMessageConverter
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        return container;
    }
}