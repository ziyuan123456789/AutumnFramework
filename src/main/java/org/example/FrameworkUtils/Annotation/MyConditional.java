package org.example.FrameworkUtils.Annotation;

import org.example.FrameworkUtils.AutumnMVC.Condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MyConditional {
    Class<? extends Condition> value();
}
