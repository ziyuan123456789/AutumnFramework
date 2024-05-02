package org.example.FrameworkUtils.AutumnMVC.Ioc;

import org.example.FrameworkUtils.AutumnMVC.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.MyBeanDefinition;
import org.example.FrameworkUtils.AutumnMVC.BeanLoader.ObjectFactory;

import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.04
 */
public interface AutumnStarterRegisterer {
    void postProcessBeanDefinitionRegistry(AnnotationScanner scanner, Map<String, MyBeanDefinition> registry) throws Exception;

    default ObjectFactory<?> createFactoryMethod(Class<?> beanClass) throws Exception {
        return null;
    }
}
