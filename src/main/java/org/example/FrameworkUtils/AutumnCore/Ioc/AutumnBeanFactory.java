package org.example.FrameworkUtils.AutumnCore.Ioc;

import java.util.Map;
import java.util.Properties;

/**
 * @author ziyuan
 * @since 2024.06
 */
public interface AutumnBeanFactory {
    Object getBean(String beanName);

    void put(String key, Object value);

    Object get(String key);

    <T> T get(Class<T> clazz) throws Exception;

    Map<String, Object> getIocContainer();

    Properties getProperties();
}