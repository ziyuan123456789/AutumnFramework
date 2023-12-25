package org.example.FrameworkUtils.AutumnMVC.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * @author ziyuan
 * @since 2023.11
 */
@Target({ElementType.PARAMETER,ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableAop {
    String[] getMethod();
    Class<?> getClassFactory();
}
