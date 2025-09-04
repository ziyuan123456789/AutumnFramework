package org.example.FrameworkUtils.AutumnCore.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2023.11
 */

/**
 * 声明一个类为配置类并进行代理,保证@Bean的单例性
 * 此外与Spring不同,只有被@Config标记的类中的@Bean才会生效
 */

@Target({ElementType.PARAMETER,ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface MyConfig {
    boolean proxyBeanMethods() default true;
}
