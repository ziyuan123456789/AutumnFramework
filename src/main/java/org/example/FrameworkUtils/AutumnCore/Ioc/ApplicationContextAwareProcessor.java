package org.example.FrameworkUtils.AutumnCore.Ioc;

import org.example.FrameworkUtils.Exception.BeanCreationException;

/**
 * @author ziyuan
 * @since 2025.02
 */
public class ApplicationContextAwareProcessor implements BeanPostProcessor {

    private ApplicationContext applicationContext;

    public ApplicationContextAwareProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeanCreationException {
        if (bean instanceof Aware) {
            this.invokeAwareInterfaces(bean);
        }
        return bean;
    }

    private void invokeAwareInterfaces(Object bean) {
        if (bean instanceof EnvironmentAware environmentAware) {
            environmentAware.setEnvironment(this.applicationContext.getEnvironment());
        }


        if (bean instanceof ApplicationEventPublisherAware applicationEventPublisherAware) {
            applicationEventPublisherAware.setApplicationEventPublisher(this.applicationContext);
        }


        if (bean instanceof ApplicationContextAware applicationContextAware) {
            applicationContextAware.setApplicationContext(this.applicationContext);
        }

    }


}
