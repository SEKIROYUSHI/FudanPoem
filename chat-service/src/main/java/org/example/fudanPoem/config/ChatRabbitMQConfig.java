package org.example.fudanPoem.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ChatRabbitMQConfig {

    @Bean(name = "chatExchange")
    public DirectExchange chatExchange() {
        return new DirectExchange("chat.exchange", false, false);
    }

}