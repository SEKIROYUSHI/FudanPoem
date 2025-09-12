//package org.example.fudanPoem.aspect;
//
//
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.example.fudanPoem.annotation.UserOperationLog;
//import org.springframework.stereotype.Component;
//
//import java.util.Arrays;
//
//
//@Aspect
//@Component
//@Slf4j
//public class UserOperationAspect {
//
//    // 定义切点：拦截所有被@UserOperationLog注解标记的方法
//    @Pointcut("@annotation(org.example.fudanPoem.annotation.UserOperationLog)")
//    public void userOperationPointcut() {}
//
//    // 环绕通知：在方法执行前后都能执行逻辑（适合记录耗时、参数、结果）
//    @Around("userOperationPointcut() && @annotation(logAnnotation)")
//    public Object recordOperationLog(ProceedingJoinPoint joinPoint, UserOperationLog logAnnotation) throws Throwable {
//        // 1. 方法执行前：记录操作信息、参数
//        long startTime = System.currentTimeMillis(); // 开始时间
//        String operation = logAnnotation.value(); // 从注解获取操作描述
//        String methodName = joinPoint.getSignature().getName(); // 方法名（如save、login）
//        Object[] args = joinPoint.getArgs(); // 方法参数（如User对象、loginInfo）
//
//        log.info("[操作开始] 操作：{}，方法：{}，参数：{}", operation, methodName, Arrays.toString(args));
//
//        // 2. 执行目标方法（即被拦截的业务方法，如UserServiceImpl.save()）
//        Object result;
//        try {
//            result = joinPoint.proceed(); // 执行原方法
//        } catch (Exception e) {
//            // 3. 方法执行异常：记录错误日志
//            log.error("[操作异常] 操作：{}，方法：{}，异常：{}", operation, methodName, e.getMessage(), e);
//            throw e; // 继续抛出异常，让全局异常处理器处理
//        }
//
//        // 4. 方法执行后：记录结果、耗时
//        long costTime = System.currentTimeMillis() - startTime; // 耗时（毫秒）
//        log.info("[操作结束] 操作：{}，方法：{}，结果：{}，耗时：{}ms",
//                operation, methodName, result, costTime);
//
//        return result; // 返回方法执行结果
//    }
//}
