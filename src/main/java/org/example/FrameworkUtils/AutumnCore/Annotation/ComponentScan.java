package org.example.FrameworkUtils.AutumnCore.Annotation;

import java.lang.annotation.*;

/**
 * @author ziyuan
 * @since 2025.01
 */
@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentScan {
    String[] value() default {};
}
