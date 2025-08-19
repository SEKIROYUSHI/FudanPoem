package org.example.fudanPoem.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.fudanPoem.entity.Post;
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
public interface PostMapper extends BaseMapper<Post> {

}
