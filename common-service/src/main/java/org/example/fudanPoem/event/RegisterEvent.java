package org.example.fudanPoem.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterEvent implements Serializable {
    private Long userId; // 用户ID
    private LocalDateTime loginTime; // 登录时间
}