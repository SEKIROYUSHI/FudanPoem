package org.example.fudanPoem.consumer;

import org.example.fudanPoem.component.QueueManager;
import org.example.fudanPoem.config.RegisterEventConsumerConfig;
import org.example.fudanPoem.event.RegisterEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RegisterEventListener {

    // 注入聊天队列管理器（用于创建用户专属聊天队列）
    @Autowired
    private QueueManager queueManager;



    /**
     * 监听登录事件队列，收到消息后创建用户的聊天队列
     */
    @RabbitListener(queues = RegisterEventConsumerConfig.LOGIN_EVENT_QUEUE_NAME)
    public void onLoginEvent(RegisterEvent event) {
        System.out.println("收到注册事件：用户" + event.getUserId() + "注册，准备创建聊天队列");

        // 调用队列管理器，为该用户创建专属聊天队列（如"chat.user.123.queue"）
        queueManager.createQueueIfNotExists(event.getUserId());
    }
}