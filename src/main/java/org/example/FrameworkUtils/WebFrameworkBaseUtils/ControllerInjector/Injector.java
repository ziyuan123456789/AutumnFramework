package org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@MyComponent
public @interface Injector {
}
