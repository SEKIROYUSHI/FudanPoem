package org.example.fudanPoem.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.fudanPoem.entity.Post;
import org.example.fudanPoem.mapper.PostMapper;
import org.example.fudanPoem.service.IPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
