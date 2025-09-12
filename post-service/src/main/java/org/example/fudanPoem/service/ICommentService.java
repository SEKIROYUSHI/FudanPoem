package org.example.fudanPoem.service;

import org.example.fudanPoem.entity.Comment;

import java.util.List;

public interface ICommentService {

    // 1. 添加评论
    Comment addComment(Comment comment);

    // 2. 按帖子 ID 查评论
    List<Comment> getCommentsByPostId(Long postId);

    // 3. 按父评论 ID 查子评论
    List<Comment> getChildCommentsByParentId(String parentId);

    // 4. 评论点赞
    Comment likeComment(String commentId);

    // 5. 删除评论
    void deleteComment(String commentId);

    public boolean toggleLike(String commentId, Long userId);
}
