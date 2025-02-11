package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2024.05
 */

import org.example.FrameworkUtils.Exception.BeanCreationException;

/**
 * BeanPostProcessor就像冬天的老妈,早上问你有没有穿秋裤,晚上也问你有没有穿秋裤
 */
public interface BeanPostProcessor {
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeanCreationException {
        return bean;
    }


    default Object postProcessAfterInitialization(Object bean, String beanName) throws BeanCreationException {
        return bean;
    }


}
