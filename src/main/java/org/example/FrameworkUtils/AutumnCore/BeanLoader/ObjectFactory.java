package org.example.FrameworkUtils.AutumnCore.BeanLoader;

/**
 * @author ziyuan
 * @since 2024.04
 */

/**
 * ObjectFactory,好名字,不知道的还以为他才是容器本身呢
 */
@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws Exception;
}

