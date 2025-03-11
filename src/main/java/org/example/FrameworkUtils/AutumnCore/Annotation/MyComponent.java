package org.example.FrameworkUtils.AutumnCore.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author wsh
 */

/**
 * 用于标记组件,此注解声明的类,可以被扫描到
 * 你可以选择手动创建复合注解,在你的类注解上标记此注解,新的复合注解也会有@MyComponent的能力
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MyComponent {
    String value() default "";
}
