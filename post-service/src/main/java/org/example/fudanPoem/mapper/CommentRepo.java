package org.example.fudanPoem.mapper;

import org.bson.types.ObjectId;
import org.example.fudanPoem.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MongoDB Repository：继承 MongoRepository<实体类, 主键类型>
 * 自动拥有 CRUD 方法，也可自定义查询方法（按 Spring Data 规范命名）
 */
@Repository
public interface CommentRepo extends MongoRepository<Comment, ObjectId> {

    // 1. 自定义查询：按 postId 查某帖子的所有评论（按创建时间倒序）
    // 方法名遵循规范：findBy + 字段名 + 排序（OrderBy + 字段名 + Desc/Asc）
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    // 2. 自定义查询：按 parentId 查某父评论的所有子评论（按创建时间正序）
    List<Comment> findByParentIdOrderByCreatedAtAsc(String parentId);

    // 3. 自定义查询：按 userId 查某用户的所有评论
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("{ '_id' : ?0 }")
    @Update("{ '$inc' : { 'likes' : 1 } }")
    long likeComment(ObjectId commentId);

    // 取消点赞：likes - 1（仅当 likes > 0 时执行，避免负数）
    @Query("{ '_id' : ?0, 'likes' : { '$gt' : 0 } }")
    @Update("{ '$inc' : { 'likes' : -1 } }")
    long unlikeComment(ObjectId commentId);

}