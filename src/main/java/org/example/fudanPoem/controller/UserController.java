package org.example.fudanPoem.controller;

import jakarta.validation.Valid;
import org.example.fudanPoem.common.Result;
import org.example.fudanPoem.dto.UserLoginDTO;
import org.example.fudanPoem.dto.UserRegisterDTO;
import org.example.fudanPoem.dto.UserSimpleDTO;
import org.example.fudanPoem.entity.User;
import org.example.fudanPoem.service.impl.UserServiceImpl;
import org.example.fudanPoem.utils.JwtUtil;
import org.springframework.beans.BeanUtils;
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
    public Result<String> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        User user = new User();
        BeanUtils.copyProperties(userRegisterDTO,user);
        userService.save(user);
        return Result.success("注册成功");
    }

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody UserLoginDTO user) {
        UserSimpleDTO userSimpleDTO = userService.login(user);
        String token = jwtUtil.generateToken(userSimpleDTO.getId(),userSimpleDTO.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userSimpleDTO);  // 返回用户基本信息（不含密码）
        return Result.success(response);
    }


}
