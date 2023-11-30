package org.example.FrameworkUtils.AutumnMVC;

import org.example.FrameworkUtils.Webutils.MyContext;

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
