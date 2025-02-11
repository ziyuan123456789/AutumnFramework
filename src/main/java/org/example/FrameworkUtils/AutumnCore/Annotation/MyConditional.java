package org.example.FrameworkUtils.AutumnCore.Annotation;

import org.example.FrameworkUtils.AutumnCore.Ioc.MyCondition;

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
 * 用于条件注解,在满足条件的情况下才会生效
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MyConditional {
    Class<? extends MyCondition>[] value();
}
