package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;

/**
 * @author ziyuan
 * @since 2025.01
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor {
    public static final String CLASS_NAME = "org.example.FrameworkUtils.AutumnCore.Ioc.ConfigurationClassPostProcessor";

    @Override
    public void postProcessBeanDefinitionRegistry(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {

    }

    @Override
    public void postProcessBeanFactory(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws Exception {

    }
}
