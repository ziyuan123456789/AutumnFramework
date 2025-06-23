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
 * 用于标记一个字段的值,可以在配置文件中使用
 * 例如: @Value("my.property") private String myProperty;
 * 这将从配置文件中读取 "my.property" 的值并注入到 myProperty 字段中
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Value {
    String value() ;
}
