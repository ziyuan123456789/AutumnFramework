package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;

/**
 * @author ziyuan
 * @since 2024.04
 */
public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception;

}
