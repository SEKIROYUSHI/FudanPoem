package org.example.fudanPoem.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.fudanPoem.exception.UserBusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class LoginLimitAspect {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 1. 修复切点表达式语法错误（补充闭合括号）
    @Pointcut("execution(* org.example.fudanPoem.service.impl.UserServiceImpl.login(..))")
    public void limitLoginTimesPointCut() {}

    @Around("limitLoginTimesPointCut()")
    public Object limitLoginTimes(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            throw new UserBusinessException(400, "登录参数不能为空");
        }
        String userKey = "login:fail:" + args[0].toString(); // Redis键：前缀+用户名，避免key冲突

        Integer failCount = (Integer) redisTemplate.opsForValue().get(userKey);
        int maxFailTimes = 3;
        int lockSeconds = 60;

        if (failCount != null && failCount >= maxFailTimes) {
            // 已超过最大次数，计算剩余锁定时间
            Long remainTime = redisTemplate.getExpire(userKey, TimeUnit.SECONDS);
            throw new UserBusinessException(400,
                    String.format("登录失败次数过多，请%d秒后再试", remainTime));
        }

        try {
            // 4. 执行原登录方法
            Object result = joinPoint.proceed();

            // 5. 登录成功：清除失败计数
            redisTemplate.delete(userKey);
            return result;
        } catch (Exception e) {
            // 6. 登录失败：增加失败次数，并设置过期时间
            if (failCount == null) {
                // 第一次失败：初始化计数，设置过期时间
                redisTemplate.opsForValue().set(userKey, 1, lockSeconds, TimeUnit.SECONDS);
            } else {
                // 非第一次失败：次数+1，重置过期时间
                redisTemplate.opsForValue().increment(userKey);
                redisTemplate.expire(userKey, lockSeconds, TimeUnit.SECONDS);
            }
            throw e;
        }
    }
}
