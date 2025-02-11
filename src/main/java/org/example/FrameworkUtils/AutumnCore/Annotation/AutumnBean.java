package org.example.FrameworkUtils.AutumnCore.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2023.11
 */

/**
 * 在受管对象方法上声明此注解,可以将方法注册为Bean,容器会调用这方法一次
 * 后续应该也会像spring那样对@Config标记的类进行代理,保证默认状态下生产方法只会被调用一次
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AutumnBean {
    String value() default "";
}
