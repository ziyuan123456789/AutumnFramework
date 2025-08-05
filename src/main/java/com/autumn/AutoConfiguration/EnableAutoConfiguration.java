package com.autumn.AutoConfiguration;

import org.example.FrameworkUtils.AutumnCore.Annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2024.07
 */

/**
 * 当声明这个注解之后,复合注解导入AutoConfigurationImportSelector
 * AutoConfigurationImportSelector会扫描Meta-INF.autumn下的AutoConfiguration.imports文件
 * 并按照条件注解判断是否应该进行自动装配
 */

@Target({ElementType.TYPE})
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Import(AutoConfigurationImportSelector.class)
public @interface EnableAutoConfiguration {
}
