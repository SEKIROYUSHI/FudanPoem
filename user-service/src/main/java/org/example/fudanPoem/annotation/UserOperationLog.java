package org.example.fudanPoem.annotation;

import java.lang.annotation.*;

// 注解作用在方法上
@Target(ElementType.METHOD)
// 注解在运行时生效（AOP需要在运行时解析）
@Retention(RetentionPolicy.RUNTIME)
// 注解信息包含在JavaDoc中
@Documented
public @interface UserOperationLog {
    // 操作描述（如"用户注册"、"用户登录"）
    String value() default "";
}