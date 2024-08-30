package com.autumn.async;

import org.example.FrameworkUtils.AutumnCore.Annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2024.08
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({AutumnAsyncConfiguration.class, AsyncAopProxyHandler.class})
public @interface EnableAutumnAsync {
}
