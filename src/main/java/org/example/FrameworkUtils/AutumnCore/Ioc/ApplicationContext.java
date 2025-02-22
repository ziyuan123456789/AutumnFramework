package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.env.Environment;
import org.example.FrameworkUtils.Exception.BeanCreationException;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.06
 */


public interface ApplicationContext extends BeanFactory, Environment, SingletonBeanRegistry, BeanDefinitionRegistry, ApplicationEventPublisher {

    String BASE_CONTEXT = "AnnotationConfigApplicationContext";

    String SERVLET_CONTEXT = "AnnotationConfigServletWebServerApplicationContext";

    Environment getEnvironment();

    void setEnvironment(Environment environment);

    void refresh();

    void put(String key, Object value);

    Object get(String key);

    Properties getProperties();

    List<Object> getBeansByAnnotation(Class<? extends Annotation> annotationClass);

    void addBean(String name, Object bean);

    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeanCreationException;

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    Set<BeanPostProcessor> getAllBeanPostProcessors();
}
