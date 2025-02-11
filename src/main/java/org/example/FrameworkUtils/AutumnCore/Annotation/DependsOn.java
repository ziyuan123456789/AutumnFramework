package org.example.FrameworkUtils.AutumnCore.Annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 用于显式声明Bean的顺序依赖,只能声明在类上,告知框架先去创建我要求的Bean再来创建我
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DependsOn {
    String[] value() default {};
}
