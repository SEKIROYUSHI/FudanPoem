package org.example.fudanPoem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author SEKIROYUSHI
 * @since 2025-07-30
 */
@Data
public class Post implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 帖子ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 作者ID
     */
    private Integer userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 诗歌内容
     */
    private String content;

    /**
     * 浏览量
     */
    private Integer views;

    /**
     * 点赞数
     */
    private Integer likes;

    /**
     * 评论数
     */
    private Integer commentsCount;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

}
