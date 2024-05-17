package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.AutumnCore.BeanLoader.ObjectFactory;

/**
 * @author ziyuan
 * @since 2024.04
 */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception;

    default ObjectFactory<?> createFactoryMethod(Class<?> beanClass) throws Exception {
        return null;
    }
}
