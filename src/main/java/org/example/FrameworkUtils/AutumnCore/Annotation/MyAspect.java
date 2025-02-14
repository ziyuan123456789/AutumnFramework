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
 * 用于标记切面处理器
 */
@Target({ElementType.PARAMETER,ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@MyComponent
public @interface MyAspect {
}
