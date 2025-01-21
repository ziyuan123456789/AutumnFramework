package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2025.01
 */
public interface BeanFactory {

    Object getBean(String name);

    <T> T getBean(Class<T> requiredType);
}
