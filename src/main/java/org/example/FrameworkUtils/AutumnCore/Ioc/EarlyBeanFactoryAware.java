package org.example.FrameworkUtils.AutumnCore.Ioc;

/**
 * @author ziyuan
 * @since 2025.01
 */
public interface EarlyBeanFactoryAware extends BeanFactoryAware {
    @Override
    void setBeanFactory(ApplicationContext beanFactory);
}
