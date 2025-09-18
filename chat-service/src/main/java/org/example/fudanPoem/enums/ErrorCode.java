package org.example.fudanPoem.enums;
import lombok.Getter;

@Getter
public enum ErrorCode {
    CHAT_RECEIVER_NOT_EXIST(2001, "消息接收者不存在"), // 比如给不存在的用户发消息
    CHAT_MESSAGE_CONTENT_EMPTY(2002, "消息内容不能为空"), // 空消息不允许发送
    CHAT_MESSAGE_CONTENT_ILLEGAL(2003, "消息内容包含非法字符或长度超限"), // 内容过长/敏感词
    CHAT_SENDER_NOT_LOGIN(2004, "发送者未登录，无法发送消息"), // 未登录用户发起聊天

    // 2. RabbitMQ消息投递错误（对应你代码中交换机/路由问题）
    CHAT_MESSAGE_EXCHANGE_FAILED(2005, "消息未被交换机接收，发送失败"), // 交换机不存在/不可用
    CHAT_MESSAGE_ROUTE_FAILED(2006, "消息路由失败（接收者队列不存在）"), // 未注册消费者时路由失败
    CHAT_MESSAGE_PUBLISH_ERROR(2007, "消息发布到RabbitMQ时发生异常"), // 连接超时/队列满等

    // 3. WebSocket相关错误（实时推送问题）
    CHAT_WEBSOCKET_CONNECT_FAILED(2008, "WebSocket连接失败，无法实时接收消息"), // 前端连接失败
    CHAT_WEBSOCKET_SESSION_CLOSED(2009, "WebSocket会话已关闭，无法推送消息"), // 会话意外断开
    CHAT_USER_OFFLINE_CANNOT_PUSH(2010, "接收者已离线，无法实时推送消息"), // 离线时推送（非错误，但可预留）

    // 4. 消息处理错误（确认/存储问题）
    CHAT_MESSAGE_ACK_FAILED(2011, "消息消费确认失败（RabbitMQ ACK异常）"), // 手动ACK时出错
    CHAT_MESSAGE_SAVE_FAILED(2012, "聊天消息存储到数据库失败"), // 消息入库异常
    CHAT_HISTORY_QUERY_FAILED(2013, "查询历史聊天记录失败"); // 拉取历史消息时出错

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
