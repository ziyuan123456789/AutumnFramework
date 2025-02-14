package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2024.05
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return null;
    }

    default boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    default Object postProcessProperties(Object bean, String beanName) {
        return bean;
    }

}
