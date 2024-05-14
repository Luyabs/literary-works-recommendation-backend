package edu.shu.abs.common.aop.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解@Log
 * 用于记录日志
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    boolean something() default true;   // something
}
