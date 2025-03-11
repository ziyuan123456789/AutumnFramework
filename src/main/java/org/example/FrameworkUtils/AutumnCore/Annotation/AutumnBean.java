package org.example.FrameworkUtils.AutumnCore.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * @author ziyuan
 * @since 2023.11
 */

/**
 * 在受管对象方法上声明此注解,可以将方法注册为Bean,容器会调用这方法一次
 * 同时如果存在@Config注解会创建代理,防止多次生产对象
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutumnBean {
    String value() default "";

    String initMethod() default "";

    String destroyMethod() default "";
}
