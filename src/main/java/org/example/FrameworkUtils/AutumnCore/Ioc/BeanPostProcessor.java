package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2024.05
 */

/**
 * BeanPostProcessor就像冬天的老妈,早上问你有没有穿秋裤,晚上也问你有没有穿秋裤
 */
public interface BeanPostProcessor {
    Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception;
    Object postProcessAfterInitialization(Object bean, String beanName) throws Exception;
}
