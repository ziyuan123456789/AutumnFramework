package org.example.FrameworkUtils.AutumnCore.Annotation;

import com.autumn.AutoConfiguration.EnableAutoConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 复合注解,标记启动类
 * 这个注解相当于 @MyConfig与@EnableAutoConfiguration与@ComponentScan
 * 其实符合注解里的@ComponentScan没什么用
 */
@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@ComponentScan
@MyConfig
@EnableAutoConfiguration
public @interface AutumnBootApplication {
}
