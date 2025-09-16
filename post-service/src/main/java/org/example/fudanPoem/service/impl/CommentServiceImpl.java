package org.example.fudanPoem.service.impl;

import org.bson.types.ObjectId;
import org.example.fudanPoem.entity.Comment;
import org.example.fudanPoem.mapper.CommentLikeMapper;
import org.example.fudanPoem.mapper.CommentRepo;
import org.example.fudanPoem.service.ICommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentRepo commentRepo;  // 注入 MongoDB Repository

    @Autowired
    private CommentLikeMapper commentLikeMapper;

    @Override
    public Comment addComment(Comment comment) {
        // 补全默认字段
        comment.setCreatedAt(new Date());
        if (comment.getLikes() == null) {
            comment.setLikes(0);
        }
        if (comment.getParentId() != null) {
            comment.setChildComment(true);
        }
        return commentRepo.save(comment);  // 调用 Repository 保存
    }

    @Override
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepo.findByPostIdOrderByCreatedAtDesc(postId);
    }

    @Override
    public List<Comment> getChildCommentsByParentId(String parentId) {
        return commentRepo.findByParentIdOrderByCreatedAtAsc(parentId);
    }

    @Override
    public Comment likeComment(String commentId) {
        ObjectId objectCommentId = new ObjectId(commentId);
        Optional<Comment> optionalComment = commentRepo.findById(objectCommentId);
        if (optionalComment.isEmpty()) {
            throw new RuntimeException("评论不存在");
        }
        Comment comment = optionalComment.get();
        comment.setLikes(comment.getLikes() + 1);
        return commentRepo.save(comment);
    }

    @Override
    public void deleteComment(String commentId) {
        ObjectId objectCommentId = new ObjectId(commentId);
        commentRepo.deleteById(objectCommentId);
    }


    @Override
    @Transactional // 核心：添加事务注解，确保操作原子性
    public boolean toggleLike(String commentId, Long userId) {
        // 1. 检查用户当前点赞状态
        int isLiked = commentLikeMapper.checkLikeStatus(commentId, userId);

        ObjectId objectCommentId = new ObjectId(commentId);

        boolean result  = true;

        if (isLiked > 0) {
            // 情况1：已点赞 → 执行取消点赞（两个操作必须同时成功/失败）
            // ① 删除点赞记录
            int deleteRows = commentLikeMapper.cancelLike(commentId, userId);
            // ② 帖子点赞数-1
            int updateRows = (int) commentRepo.unlikeComment(objectCommentId);

            result = false;
            // 校验操作是否成功（可选，根据业务需要）
            if (deleteRows <= 0 || updateRows <= 0) {
                throw new RuntimeException("取消点赞失败"); // 抛出异常，触发事务回滚
            }
        } else {
            // 情况2：未点赞 → 执行点赞（两个操作必须同时成功/失败）
            // ① 添加点赞记录
            int insertRows =commentLikeMapper.addLike(commentId, userId);
            // ② 帖子点赞数+1
            int updateRows = (int) commentRepo.likeComment(objectCommentId);

            // 校验操作是否成功
            if (insertRows <= 0 || updateRows <= 0) {
                throw new RuntimeException("点赞失败"); // 抛出异常，触发事务回滚
            }
        }

        // 3. 查询最新点赞数
        return result;
    }
}
