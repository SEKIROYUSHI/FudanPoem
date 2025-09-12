package org.example.fudanPoem.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * Comment 实体：对应 MongoDB 的 "comment" 集合（自动创建）
 */
@Data
@Document(collection = "comment")  // 指定 MongoDB 集合名（推荐与实体名一致，小写）
public class Comment implements Serializable {

    /**
     * 主键：MongoDB 中默认存储为 "_id" 字段，支持 Long/ObjectId 类型
     * 用 @Id 标记，Spring Data MongoDB 会自动处理自增（若用 Long 类型）
     */
    @Id
    private ObjectId id;

    /**
     * 评论内容：@Field 可选，若字段名与文档一致可省略
     */
    @Field("content")
    private String content;

    /**
     * 评论时间：MongoDB 会自动存储为 ISODate 类型（如 2025-09-12T16:30:00.000Z）
     * 建议插入时手动设置，或用默认值
     */
    @Field("created_at")
    private Date createdAt;

    /**
     * 评论者 ID：关联用户表（MongoDB 无强外键，靠业务逻辑保证关联）
     */
    @Field("user_id")
    private Long userId;

    /**
     * 帖子 ID：关联帖子表（按帖子查评论的关键字段）
     */
    @Field("post_id")
    private Long postId;

    /**
     * 点赞数：默认 0
     */
    @Field("likes")
    private Integer likes = 0;  // 给默认值，避免 null

    /**
     * 是否子评论：注意布尔字段命名，Lombok 生成的 getter 是 isChildComment()
     * MongoDB 文档中字段名会存储为 "is_child_comment"（推荐下划线命名）
     */
    @Field("is_child_comment")
    private boolean isChildComment = false;  // 默认不是子评论

    /**
     * 父评论 ID：若为子评论，存储父评论的 id；否则为 null
     */
    @Field("parent_id")
    private String parentId;
}