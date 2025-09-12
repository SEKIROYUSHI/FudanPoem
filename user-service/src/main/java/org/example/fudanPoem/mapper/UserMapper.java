package org.example.fudanPoem.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.fudanPoem.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author SEKIROYUSHI
 * @since 2025-07-30
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE email = #{email}")
    User findByEmail(String email);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUserName(String username);

    @Update("UPDATE user SET avatar_url = #{avatarUrl} WHERE id = #{userId}")
    int updateUserAvatar(Long userId, String avatarUrl);

    //todo:增加修改其他个人信息和密码的接口
}
