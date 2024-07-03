package org.example.FrameworkUtils.AutumnCore.Ioc;

import java.lang.annotation.Annotation;
import java.util.List;
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

    List<Object> getBeansByAnnotation(Class<? extends Annotation> annotationClass);

    void addBean(String name, Object bean);

    <T> List<T> getBeansOfType(Class<T> type);
}