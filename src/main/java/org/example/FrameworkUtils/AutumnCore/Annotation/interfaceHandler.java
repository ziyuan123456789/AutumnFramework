package org.example.FrameworkUtils.AutumnCore.Annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2024.04
 */
//我希望这个注解可以标注一个接口处理器,我希望我的框架仅仅做最简单的事情,把mapper注册这些事解耦合出去
//2024/8/12已废弃
@Deprecated
@Target({ElementType.PARAMETER,ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface interfaceHandler {
    Class<?> clazz();
}
