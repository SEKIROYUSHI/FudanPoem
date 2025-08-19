package org.example.fudanPoem.controller;

import org.example.fudanPoem.common.Result;
import org.example.fudanPoem.entity.User;
import org.example.fudanPoem.service.impl.UserServiceImpl;
import org.example.fudanPoem.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author SEKIROYUSHI
 * @since 2025-07-30
 */

@RestController
@RequestMapping("/fudanpoem/user")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Result<String> register(@RequestBody User user) {
        userService.save(user);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("loginInfo");
        String password = credentials.get("password");
        User user = userService.login(email, password);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);  // 返回用户基本信息（不含密码）
        return Result.success(response);
    }


}
