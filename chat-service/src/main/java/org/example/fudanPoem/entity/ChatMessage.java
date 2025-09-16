package org.example.fudanPoem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 聊天消息实体（数据库存储用，对应表 chat_message）
 */
@Data
@TableName("chat_message")
public class ChatMessage implements Serializable {

    // 1. 主键ID
    @TableId(type = IdType.AUTO)
    private Long id;

    // 2. 发送者ID（关联User表的id字段）
    private Long senderId;

    // 3. 接收者ID（关联User表的id字段）
    private Long receiverId;

    // 4. 消息内容
    private String content;  // 用String类型：存储文本消息（如果有图片/文件，可存URL，这里先简化）

    // 5. 消息发送时间
    private Date sendTime;

    // 6. 消息状态：0-未读，1-已读
    private Integer isRead;

    // 7. 记录创建时间（存入数据库的时间，和sendTime可能一致，也可能有延迟）
    private Date createTime;

}