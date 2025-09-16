package org.example.fudanPoem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.example.fudanPoem.entity.ChatMessage;


@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    IPage<ChatMessage> selectChatHistory(
            IPage<ChatMessage> page , // 分页参数（避免一次性查太多消息卡顿）
            Long userId,  // 当前用户ID（比如A）
            Long otherId  // 聊天对象ID（比如B）
    );


    // 场景：用户点开聊天窗口后，需要把对方发的未读消息都标为已读
    int updateUnreadToRead(
           Long senderId,  // 发送者ID（比如B）
           Long receiverId  // 接收者ID（当前用户A）
    );
}
