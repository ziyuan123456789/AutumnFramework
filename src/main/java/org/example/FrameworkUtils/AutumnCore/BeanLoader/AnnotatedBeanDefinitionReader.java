package org.example.FrameworkUtils.AutumnCore.BeanLoader;

import org.example.FrameworkUtils.AutumnCore.Ioc.BeanDefinitionRegistry;
import org.example.FrameworkUtils.AutumnCore.Ioc.ConditionEvaluator;
import org.example.FrameworkUtils.AutumnCore.Ioc.ConfigurationClassPostProcessor;

/**
 * @author ziyuan
 * @since 2025.01
 */
public class AnnotatedBeanDefinitionReader {

    private final BeanDefinitionRegistry registry;

    private final ConditionEvaluator conditionEvaluator;

    public AnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
        this.registry = registry;
        this.conditionEvaluator = new ConditionEvaluator(registry);
        if (!registry.containsBeanDefinition(ConfigurationClassPostProcessor.CLASS_NAME)) {
            MyBeanDefinition def = new MyBeanDefinition(ConfigurationClassPostProcessor.CLASS_NAME, ConfigurationClassPostProcessor.class);
            registry.registerBeanDefinition(ConfigurationClassPostProcessor.CLASS_NAME, def);
        }
    }

    public void register(Class<?> componentClasses) {
        this.doRegisterBean(componentClasses);
    }

    public void register(String beanName, Class<?> componentClasses) {
        this.doRegisterBean(beanName, componentClasses);
    }

    private <T> void doRegisterBean(Class<T> beanClass) {
        MyBeanDefinition abd = new MyBeanDefinition(beanClass);
        if (!this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
//            if (!registry.containsBeanDefinition(abd.getName())) {
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd);
            this.registry.registerBeanDefinition(definitionHolder.getBeanName(), definitionHolder.getBeanDefinition());
//            }
        }
    }

    private <T> void doRegisterBean(String beanName, Class<T> beanClass) {
        MyBeanDefinition abd = new MyBeanDefinition(beanName, beanClass);
        if (!this.conditionEvaluator.shouldSkip(abd.getMetadata())) {
//            if (!registry.containsBeanDefinition(abd.getName())) {
            BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(abd, beanName);
            this.registry.registerBeanDefinition(definitionHolder.getBeanName(), definitionHolder.getBeanDefinition());
//            }

        }
    }
}
