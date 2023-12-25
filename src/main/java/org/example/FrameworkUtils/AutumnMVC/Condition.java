package org.example.FrameworkUtils.AutumnMVC;

/**
 * @author ziyuan
 * @since 2023.11
 */
@FunctionalInterface
public interface Condition {
    default void init() {
    }

    boolean matches(MyContext myContext, Class<?> clazz);
}
