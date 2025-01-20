package com.autumn.ormstarter.minijpa;

import com.autumn.cache.CacheAopProxyHandler;
import com.autumn.cache.CacheManager;
import org.example.FrameworkUtils.AutumnCore.Annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2024.09
 */
//引入JpaRepositoriesRegistrar和MiniJpaCglibFactory
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({JpaRepositoriesRegistrar.class,MiniJpaCglibFactory.class})
public @interface EnableJpaRepositories {
}
