package org.example.FrameworkUtils.AutumnCore.Ioc;

import java.util.List;

/**
 * @author ziyuan
 * @since 2025.01
 */
public interface SingletonBeanRegistry {

    void registerSingleton(String beanName, Object singletonObject);

    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    List<String> getSingletonNames();

    int getSingletonCount();

}
