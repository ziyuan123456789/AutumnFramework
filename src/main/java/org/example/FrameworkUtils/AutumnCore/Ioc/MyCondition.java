package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2023.11
 */
@FunctionalInterface
public interface MyCondition {
    default void init() {
    }

    boolean matches(ApplicationContext myContext, Class<?> clazz);
    default void after(){

    }
}
