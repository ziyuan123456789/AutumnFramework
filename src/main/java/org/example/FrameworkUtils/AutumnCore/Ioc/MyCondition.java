package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2023.11
 */

//自定义条件接口,在默认情况下处理器不会被框架包装为Bean定义,也就意味着在单例池中不存在该Bean
@FunctionalInterface
public interface MyCondition {
    boolean matches(ApplicationContext myContext, Class<?> clazz);
}
