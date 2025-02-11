package org.example.FrameworkUtils.AutumnCore.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2024.07
 */

/**
 * 用于启动自动配置,当声明这个注解之后框架会扫描所有jar包下的AutoConfiguration.imports 并进行实例化
 * 这样可以从配置文件引入Bean或者BeanDefinitionRegistryPostProcessor用来生产更多的Bean
 */
@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableAutoConfiguration {
}
