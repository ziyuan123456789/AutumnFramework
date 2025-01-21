package org.example.FrameworkUtils.AutumnCore.Ioc;


import org.example.FrameworkUtils.AutumnCore.BeanLoader.AnnotationScanner;

/**
 * @author ziyuan
 * @since 2024.05
 */

/**
 * 当你觉得引入一个jar包再写一个@Resouce注解就能开箱即用的时候
 * 那么一定有人在为你负重前行
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {
    void postProcessBeanDefinitionRegistry(AnnotationScanner scanner, BeanDefinitionRegistry registry) throws  Exception;
}
