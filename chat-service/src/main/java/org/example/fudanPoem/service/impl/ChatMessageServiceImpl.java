package org.example.fudanPoem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.fudanPoem.component.QueueManager;
import org.example.fudanPoem.entity.ChatMessage;
import org.example.fudanPoem.mapper.ChatMessageMapper;
import org.example.fudanPoem.service.ChatMessageService;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private QueueManager queueManager;

    public ChatMessageServiceImpl(QueueManager queueManager, RabbitTemplate rabbitTemplate) {
        this.queueManager = queueManager;
        this.rabbitTemplate = rabbitTemplate;
    }


    /**
     * 发送消息的核心逻辑
     */
    @Transactional
    @Override
    public void sendMessage(Long senderId, Long receiverId, String content) {
        ChatMessage message = new ChatMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setSendTime(LocalDateTime.now());
        message.setIsRead(0);

        this.save(message);

        // 创建唯一的消息ID（用UUID，保证全局唯一）
        String messageId = UUID.randomUUID().toString();
        CorrelationData correlationData = new CorrelationData(messageId);

        // 确保接收者的队列存在（幂等创建，不会重复建）
        queueManager.createQueueIfNotExists(receiverId);


        // 路由键格式："chat.user." + 接收者ID（方便接收者的队列监听）
        try {
            String routingKey = "chat.user." + receiverId;
            rabbitTemplate.convertAndSend("chat.exchange", routingKey, message, correlationData);

            boolean isAck = correlationData.getFuture().get(3, TimeUnit.SECONDS).isAck();

            if (isAck) {
                log.info("消息[{}]发送成功（已被交换机接收）：{} -> {}：{}", messageId, senderId, receiverId, content);
            } else {
                throw new RuntimeException("消息[" + messageId + "]未被交换机接收，发送失败");
            }
        } catch (Exception e) {
            log.error("消息[" + messageId + "]发送失败！", e);
            throw new RuntimeException("发送消息失败：" + e.getMessage());
        }
    }


    /**
     * 查询聊天记录（分页）
     */
    @Override
    public IPage<ChatMessage> getChatHistory(Long userId, Long otherId, Integer pageNum, Integer pageSize) {
        // 1. 校验分页参数（页码不能小于1，每页条数不能太大）
        if (pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize < 1 || pageSize > 100) {  // 限制最大100条，避免一次查太多
            pageSize = 20;  // 默认20条
        }

        // 2. 创建分页对象（MyBatis-Plus的Page，封装页码和每页条数）
        IPage<ChatMessage> page = new Page<>(pageNum, pageSize);

        // 3. 调用Mapper的自定义方法，查询分页数据
        return chatMessageMapper.selectChatHistory(page,userId, otherId);
    }


    /**
     * 批量标未读消息为已读
     */
    @Override
    public int markUnreadAsRead(Long senderId, Long receiverId) {
        // 调用Mapper的自定义方法，返回影响行数
        return chatMessageMapper.updateUnreadToRead(senderId, receiverId);
    }


    /**
     * 查询未读消息总数
     */
    @Override
    public long getUnreadCount(Long userId) {
        return this.count(new QueryWrapper<ChatMessage>()
                .eq("receiver_id", userId)
                .eq("is_read", 0));
    }
}