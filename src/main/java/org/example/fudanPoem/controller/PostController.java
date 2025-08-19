package org.example.fudanPoem.controller;

import org.example.fudanPoem.common.Result;
import org.example.fudanPoem.entity.Post;
import org.example.fudanPoem.service.impl.PostServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<String> submitPost(Post post) {
        postService.save(post);
        return Result.success("帖子提交成功");
    }

}
