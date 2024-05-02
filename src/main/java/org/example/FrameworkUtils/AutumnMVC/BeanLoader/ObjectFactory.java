package org.example.FrameworkUtils.AutumnMVC.BeanLoader;

/**
 * @author ziyuan
 * @since 2024.04
 */
@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws Exception;
}

