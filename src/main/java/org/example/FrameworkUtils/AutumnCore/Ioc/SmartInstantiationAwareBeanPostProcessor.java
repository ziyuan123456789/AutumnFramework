package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2025.02
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

    default Object getEarlyBeanReference(Object bean, String beanName) {
        return bean;
    }
}
