package org.example.FrameworkUtils.AutumnCore.Ioc;


import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;
import org.example.FrameworkUtils.Exception.BeanDefinitionCreationException;

/**
 * @author ziyuan
 * @since 2024.05
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    void postProcessBeanDefinitionRegistry(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws  Exception;
}
