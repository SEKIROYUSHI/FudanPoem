package org.example.fudanPoem.service;

import org.example.fudanPoem.dto.UserLoginDTO;
import org.example.fudanPoem.dto.UserSimpleDTO;
import org.example.fudanPoem.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author SEKIROYUSHI
 * @since 2025-07-30
 */
public interface IUserService extends IService<User> {
    public UserSimpleDTO login(UserLoginDTO loginUser);

    public boolean updateUserAvatar(Long userId, String avatarUrl);
}
