package org.example.fudanPoem.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommentLikeMapper {

    @Select("SELECT COUNT(1) FROM comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int checkLikeStatus(String commentId,  Long userId);

    // 2. 新增点赞记录（未点赞时调用）
    @Insert("INSERT INTO comment_like (comment_id, user_id) VALUES (#{commentId}, #{userId})")
    int addLike( String commentId, Long userId);

    // 3. 删除点赞记录（已点赞时调用）
    @Delete("DELETE FROM comment_like WHERE comment_id = #{commentId} AND user_id = #{userId}")
    int cancelLike( String commentId,Long userId);
}
