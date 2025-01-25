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

    //为什么spring那么喜欢数组?????? 我不喜欢!我只喜欢List<?>
    List<String> getSingletonNames();

    int getSingletonCount();

}
