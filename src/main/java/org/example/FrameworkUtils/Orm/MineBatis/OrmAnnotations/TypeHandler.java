package org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
//xxx:为扫描注册做准备
public @interface TypeHandler {
}
