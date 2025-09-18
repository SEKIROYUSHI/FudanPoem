package org.example.fudanPoem.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventExchangeConfig {

    // 交换机名称：统一前缀+业务标识
    public static final String EVENT_EXCHANGE_NAME = "fudan.poem.event.exchange";

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(EVENT_EXCHANGE_NAME, false, false);
    }
}