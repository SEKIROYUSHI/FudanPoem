package org.example.fudanPoem.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.fudanPoem.common.Result;
import org.example.fudanPoem.dto.HistoryQueryDTO;
import org.example.fudanPoem.dto.MessageRequestDTO;
import org.example.fudanPoem.entity.ChatMessage;
import org.example.fudanPoem.service.ChatMessageService;
import org.example.fudanPoem.service.impl.ChatMessageServiceImpl;
import org.example.fudanPoem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * 聊天相关接口（供前端调用）
 * 基础路径：/fudanpoem/chat（所有接口都以这个开头，方便区分模块）
 */
@RestController  // = @Controller + @ResponseBody：返回JSON格式数据
@RequestMapping("/fudanpoem/chat")  // 所有接口的基础路径
@Slf4j
public class ChatMessageController {

    // 注入Service层对象（调用业务逻辑）
    @Autowired
    private ChatMessageServiceImpl chatMessageService;


    @PostMapping("/send")
    public Result<Void> sendMessage(HttpServletRequest request, @Valid @RequestBody MessageRequestDTO messageRequest){
            Long senderId = (Long)request.getAttribute("userId");
            Long receiverId = messageRequest.getReceiverId();
            String content = messageRequest.getContent();
            chatMessageService.sendMessage(senderId, receiverId, content);
            return Result.success(null);
    }

    @GetMapping("/history")
    public Result<IPage<ChatMessage>> getChatHistory(HttpServletRequest request, @RequestBody HistoryQueryDTO historyQueryDTO) {
            // 调用Service层的查询方法，获取分页数据
            Long userId = (Long)request.getAttribute("userId");
            Long otherId = historyQueryDTO.getOtherId();
            Integer pageNum = historyQueryDTO.getPageNum();
            Integer pageSize = historyQueryDTO.getPageSize();
            IPage<ChatMessage> page = chatMessageService.getChatHistory(userId, otherId, pageNum, pageSize);
            return Result.success(page);  // 成功：返回分页数据（包含总条数、当前页消息列表）
    }


    /**
     * 3. 标记未读消息为已读接口（用户点开聊天窗口时调用）
     * 请求方式：PUT（更新数据用PUT）
     */
    @PutMapping("/mark-read")
    public Result<Integer> markUnreadAsRead(
         HttpServletRequest request,
         @RequestParam Long receiverId  // 对方用户ID（消息发送者）
    ) {
        Long senderId = (Long)request.getAttribute("userId"); // 自己的用户ID（消息接收者）

        int count = chatMessageService.markUnreadAsRead(senderId, receiverId);
        return Result.success(count);
    }


    /**
     * 4. 获取未读消息总数接口（用户进入首页时调用，显示小红点数字）
     * 请求方式：GET
     * 接口路径：/fudanpoem/chat/unread-count
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(
           HttpServletRequest request
    ) {
        Long userId = (Long)request.getAttribute("userId"); // 自己的用户ID（消息接收者）
            // 调用Service层的查询方法，获取未读总数
            long count = chatMessageService.getUnreadCount(userId);
            return Result.success(count);  // 成功：返回未读消息数
    }
}