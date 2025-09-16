package org.example.fudanPoem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.fudanPoem.entity.ChatMessage;

import java.util.List;

/**
 * 聊天消息的Service接口（定义业务方法）
 * 继承IService<ChatMessage>：获得MyBatis-Plus提供的基础CRUD方法
 */
public interface ChatMessageService extends IService<ChatMessage> {

    /**
     * 发送消息（核心业务）
     * 功能：1. 校验发送者/接收者存在；2. 保存消息到数据库；3. 发送消息到RabbitMQ
     * @param senderId 发送者ID
     * @param receiverId 接收者ID
     * @param content 消息内容
     */
    void sendMessage(Long senderId, Long receiverId, String content);

    /**
     * 查询两个用户的聊天记录（分页）
     * @param userId 当前用户ID
     * @param otherId 聊天对象ID
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页条数
     * @return 分页后的聊天记录列表
     */
    IPage<ChatMessage> getChatHistory(Long userId, Long otherId, Integer pageNum, Integer pageSize);

    /**
     * 把“对方发给自己的未读消息”全部标为已读
     * @param senderId 对方用户ID（消息发送者）
     * @param receiverId 自己的用户ID（消息接收者）
     * @return 影响的行数（标为已读的消息数）
     */
    int markUnreadAsRead(Long senderId, Long receiverId);

    /**
     * 查询用户的未读消息总数
     * @param userId 接收者ID（自己）
     * @return 未读消息总数
     */
    long getUnreadCount(Long userId);

}