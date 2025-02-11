package org.example.FrameworkUtils.AutumnCore.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2025.01
 */

/**
 * 用于包扫描,你可以在在注解中声明扫包的范围
 * 框架在启动的方法会遍历调用栈找到真正的入口方法,然后扫描这个方法所在的类的包
 * 如果你希望进行自定义扫包那么也需要在默认扫包范围内声明注解,框架会自动拓展扫包范围
 */
@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ComponentScan {
    String[] value() default {};
}
