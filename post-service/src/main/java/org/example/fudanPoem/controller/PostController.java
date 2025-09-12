package org.example.fudanPoem.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.fudanPoem.common.Result;
import org.example.fudanPoem.entity.Post;
import org.example.fudanPoem.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author SEKIROYUSHI
 * @since 2025-07-30
 */
@RestController
@RequestMapping("/fudanpoem/post")
public class PostController {

    @Autowired
    private PostServiceImpl postService;

    @PostMapping
    public Result<String> submitPost(@RequestBody Post post, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        post.setUserId(userId);
        postService.save(post);
        return Result.success("帖子提交成功");
    }

    @PostMapping("/{postId}/view")
    public Result<String> viewPost(@PathVariable Long postId,HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = postService.viewPost(postId,userId);
        if (success) {
            return Result.success("帖子浏览量+1");
        } else {
            return Result.error("帖子不存在或浏览失败");
        }
    }

    @PostMapping("/{postId}/like")
    public Result<String> likePost(@PathVariable Long postId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        boolean success = postService.toggleLike(postId,userId);
        if (success) {
            return Result.success("帖子点赞/取消点赞成功");
        } else {
            return Result.error("帖子不存在或点赞/取消点赞失败");
        }
    }

}
