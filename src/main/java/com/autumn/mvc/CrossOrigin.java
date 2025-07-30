package com.autumn.mvc;

/**
 * @author ziyuan
 * @since 2025.07
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记跨域请求
 * 该注解可以应用于类或方法上
 * value属性指定允许的源
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CrossOrigin {
    String[] value();
}
