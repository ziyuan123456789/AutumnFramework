package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2024.07
 */
public interface FactoryBean<T> {


    String FACTORY_BEAN_PREFIX = "&";


    T getObject() throws Exception;

    Class<?> getObjectType();

    default boolean isSingleton() {
        return true;
    }

}
