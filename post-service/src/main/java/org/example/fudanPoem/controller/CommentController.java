package org.example.fudanPoem.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.bson.types.ObjectId;
import org.example.fudanPoem.entity.Comment;
import org.example.fudanPoem.service.impl.CommentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 假设统一返回 Result 类（你项目中已定义）
import org.example.fudanPoem.common.Result;

@RestController
@RequestMapping("/fudanpoem/comments")  // 接口前缀
public class CommentController {

    @Autowired
    private CommentServiceImpl commentService;

    @PostMapping
    public Result<?> addComment(@RequestBody Comment comment, HttpServletRequest request) {
        // 实际场景：从 JWT 中获取当前用户 ID（替换硬编码）
        Long userId = (Long) request.getAttribute("userId");
        comment.setUserId(userId);

        Comment savedComment = commentService.addComment(comment);
        return Result.success(savedComment);
    }

    /**
     * 2. 按帖子 ID 查评论（前端传 postId 路径参数）
     * 接口示例：GET /comments/post/1
     */
    @GetMapping("/post/{postId}")
    public Result<List<Comment>> getCommentsByPostId(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return Result.success(comments);
    }

    /**
     * 3. 按父评论 ID 查子评论
     * 接口示例：GET /comments/parent/101
     */
    @GetMapping("/parent/{parentId}")
    public Result<List<Comment>> getChildComments(@PathVariable String parentId) {
        List<Comment> childComments = commentService.getChildCommentsByParentId(parentId);
        return Result.success(childComments);
    }

    /**
     * 5. 删除评论
     * 接口示例：DELETE /comments/101
     */
    @DeleteMapping("/{commentId}")
    public Result<?> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return Result.success("评论删除成功");
    }

    @PostMapping("/like/{commentId}")
    public Result<?> toggleLike(@PathVariable String commentId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean liked = commentService.toggleLike(commentId, userId);
        return Result.success(liked ? "已点赞" : "已取消点赞");
    }

}