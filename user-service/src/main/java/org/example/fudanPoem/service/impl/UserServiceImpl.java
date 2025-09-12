package org.example.fudanPoem.service.impl;

import org.example.fudanPoem.annotation.UserOperationLog;
import org.example.fudanPoem.dto.UserLoginDTO;
import org.example.fudanPoem.dto.UserRegisterDTO;
import org.example.fudanPoem.dto.UserSimpleDTO;
import org.example.fudanPoem.enums.ErrorCode;
import org.example.fudanPoem.entity.User;
import org.example.fudanPoem.exception.UserBusinessException;
import org.example.fudanPoem.mapper.UserMapper;
import org.example.fudanPoem.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author SEKIROYUSHI
 * @since 2025-07-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    public static String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$"; // 至少8位，包含大写字母、小写字母和数字

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    @UserOperationLog("用户注册")
    public boolean save(User user) {

        String password = user.getPassword();


        if(this.baseMapper.findByEmail(user.getEmail())!=null){
            throw new UserBusinessException(ErrorCode.EMAIL_ALREADY_REGISTERED.getCode(), ErrorCode.EMAIL_ALREADY_REGISTERED.getMessage());
        }

        if(this.baseMapper.findByUserName(user.getUsername())!=null){
            throw new UserBusinessException(ErrorCode.USERNAME_ALREADY_REGISTERED.getCode(), ErrorCode.USERNAME_ALREADY_REGISTERED.getMessage());
        }

        if(!password.matches(PASSWORD_REGEX)){
          throw new UserBusinessException(ErrorCode.PASSWORD_FORMAT_ERROR.getCode(), ErrorCode.PASSWORD_FORMAT_ERROR.getMessage());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        boolean result = super.save(user);
        if (result) {
        } else {
            throw new UserBusinessException(ErrorCode.UNKNOWN_ERROR.getCode(), ErrorCode.UNKNOWN_ERROR.getMessage());
        }

        return result;
    }


    @Override
    @UserOperationLog("用户登录")
    public UserSimpleDTO login(UserLoginDTO loginUser) {
        // 根据登录信息查询密码
        String account = loginUser.getAccount();
        String password = loginUser.getPassword();
        User user = this.baseMapper.findByEmail(account);
        String storedPassword;
        if (user == null) {
            user = this.baseMapper.findByUserName(account);
            if (user == null) {
                throw new UserBusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
            }
        }

        // 如果密码仍然为空，说明用户不存在
        storedPassword = user.getPassword();

        // 验证密码是否匹配
        if (!passwordEncoder.matches(password, storedPassword)) {
            throw new UserBusinessException(ErrorCode.PASSWORD_ERROR.getCode(), ErrorCode.PASSWORD_ERROR.getMessage());
        }
        UserSimpleDTO userSimpleDTO = new UserSimpleDTO();
        BeanUtils.copyProperties(user,userSimpleDTO);
        return userSimpleDTO;
    }

    @Override
    public boolean updateUserAvatar(Long userId, String avatarUrl) {
        // 1. 检查用户是否存在
        User existingUser = this.baseMapper.selectById(userId);
        if (existingUser == null) {
            throw new UserBusinessException(ErrorCode.USER_NOT_FOUND.getCode(), ErrorCode.USER_NOT_FOUND.getMessage());
        }


        return this.baseMapper.updateUserAvatar(userId, avatarUrl) > 0;
    }

}
