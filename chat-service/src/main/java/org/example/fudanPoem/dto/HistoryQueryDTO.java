package org.example.fudanPoem.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HistoryQueryDTO {
    // 当前用户ID（必传）
    @NotNull(message = "当前用户ID不能为空")
    private Long userId;

    // 聊天对象ID（必传）
    @NotNull(message = "聊天对象ID不能为空")
    private Long otherId;

    // 页码（默认第1页）
    private Integer pageNum = 1;

    // 每页条数（默认20条）
    private Integer pageSize = 20;
}