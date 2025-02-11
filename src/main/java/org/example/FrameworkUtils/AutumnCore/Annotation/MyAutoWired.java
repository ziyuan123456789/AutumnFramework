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
 * 用于标记需要自动注入的字段,当声明这个注解之后框架会自动注入Bean
 * 但请注意Bean的生命周期,依赖注入将会在InstantiationAwareBeanPostProcessor阶段进行
 * 所以如果在InstantiationAwareBeanPostProcessor之前希望获取Bean请实现BeanFactoryAware接口
 * 获得BeanFactory,然后通过BeanFactory.getBean()方法获取Bean
 */
@Target({ElementType.FIELD,ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MyAutoWired {
    String value() default "";

}
