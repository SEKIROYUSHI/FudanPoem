package org.example.fudanPoem.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.fudanPoem.entity.Post;
import org.example.fudanPoem.mapper.PostMapper;
import org.example.fudanPoem.service.IPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author SEKIROYUSHI
 * @since 2025-07-30
 */

@Slf4j
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements IPostService {


    @Override
    @Transactional
    public boolean viewPost(Long postId,Long userId) {
        if(this.baseMapper.deleteOldSamePostHistory(postId, userId)>0){
            log.info("用户{}重复浏览帖子{}",userId,postId);
        }
        if(this.baseMapper.insertNewView(postId, userId)<=0||this.baseMapper.viewPost(postId)<=0){
            log.error("用户{}浏览帖子{}失败",userId,postId);
            throw new RuntimeException("浏览帖子失败");
        }
        this.baseMapper.deleteOldestViews(userId);
        return true;
    }

    //todo:怎么实现前端点赞后能够立刻更新数字
    @Transactional // 核心：添加事务注解，确保操作原子性
    public boolean toggleLike(Long postId, Long userId) {
        // 1. 检查用户当前点赞状态
        int isLiked = this.baseMapper.checkLikeStatus(postId, userId);

        boolean result  = true;

        if (isLiked > 0) {
            // 情况1：已点赞 → 执行取消点赞（两个操作必须同时成功/失败）
            // ① 删除点赞记录
            int deleteRows = this.baseMapper.cancelLike(postId, userId);
            // ② 帖子点赞数-1
            int updateRows = this.baseMapper.unlikePost(postId);

            // 校验操作是否成功（可选，根据业务需要）
            if (deleteRows <= 0 || updateRows <= 0) {
                throw new RuntimeException("取消点赞失败"); // 抛出异常，触发事务回滚
            }
        } else {
            // 情况2：未点赞 → 执行点赞（两个操作必须同时成功/失败）
            // ① 添加点赞记录
            int insertRows =this.baseMapper.addLike(postId, userId);
            // ② 帖子点赞数+1
            int updateRows = this.baseMapper.likePost(postId);

            // 校验操作是否成功
            if (insertRows <= 0 || updateRows <= 0) {
                throw new RuntimeException("点赞失败"); // 抛出异常，触发事务回滚
            }
        }

        // 3. 查询最新点赞数
        return result;
    }
}
