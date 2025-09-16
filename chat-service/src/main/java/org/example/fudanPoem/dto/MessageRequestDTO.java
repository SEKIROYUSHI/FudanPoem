package org.example.fudanPoem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

// 消息发送请求的DTO
@Data  // Lombok注解，自动生成getter/setter，简化代码
public class MessageRequestDTO {
    // 接收者ID（必传）
    @NotNull(message = "接收者ID不能为空")  // 可加参数校验，增强接口健壮性
    private Long receiverId;

    // 长消息内容（必传）
    @NotBlank(message = "消息内容不能为空")
    private String content;
}