package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2024.07
 */
public interface FactoryBean<T> {


    T getObject() throws Exception;

    Class<?> getObjectType();

}
