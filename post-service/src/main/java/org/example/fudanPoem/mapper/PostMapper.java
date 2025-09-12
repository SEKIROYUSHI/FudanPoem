package org.example.fudanPoem.mapper;

import org.apache.ibatis.annotations.*;
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

    @Update("update post set views = views + 1 where id = #{postId}")
    int viewPost(Long postId);

    @Delete("DELETE FROM history_view WHERE user_id = #{userId} AND post_id = #{postId}")
    int deleteOldSamePostHistory(Long postId,Long userId);

    // 2. 插入新的浏览记录
    @Insert("INSERT INTO history_view (post_id, user_id, viewed_at) VALUES (#{postId}, #{userId}, NOW())")
    int insertNewView(Long postId, Long userId);

    // 3. 查询用户当前的总记录数
    @Select("SELECT COUNT(*) FROM history_view WHERE user_id = #{userId}")
    int countUserViews(Long userId);

    // 4. 当总记录数超过100时，删除最旧的记录（保留最近100条）
    @Delete("""
            DELETE FROM history_view
              WHERE
                user_id = #{userId}  -- 限定当前用户
                AND id NOT IN (  -- 排除“最近100条”的记录
                  SELECT id FROM (
                    -- 子查询：获取当前用户最近100条记录的id（按浏览时间倒序）
                    SELECT id
                    FROM history_view
                    WHERE user_id = #{userId}
                    ORDER BY viewed_at DESC  -- DESC：最新的在前
                    LIMIT 100  -- 保留最近100条
                  ) AS temp  -- 嵌套子查询避免MySQL“不能删除同表查询结果”的限制
                )
        """)
    int deleteOldestViews( Long userId);

    @Update("update post set likes = likes + 1 where id = #{postId}")
   int likePost(Long postId);

    @Update("update post set likes = likes - 1 where id = #{postId} and likes > 0")
    int unlikePost(Long postId);

    @Select("SELECT COUNT(1) FROM post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    int checkLikeStatus(Long postId,  Long userId);

    // 2. 新增点赞记录（未点赞时调用）
    @Insert("INSERT INTO post_like (post_id, user_id, created_at) VALUES (#{postId}, #{userId}, NOW())")
    int addLike( Long postId, Long userId);

    // 3. 删除点赞记录（已点赞时调用）
    @Delete("DELETE FROM post_like WHERE post_id = #{postId} AND user_id = #{userId}")
    int cancelLike( Long postId,Long userId);
}
