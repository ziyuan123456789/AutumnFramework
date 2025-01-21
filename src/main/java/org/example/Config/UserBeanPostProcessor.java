package org.example.Config;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanPostProcessor;
import org.example.FrameworkUtils.AutumnCore.Ioc.Ordered;

/**
 * @author ziyuan
 * @since 2024.05
 */
@MyComponent
@Slf4j
public class UserBeanPostProcessor implements BeanPostProcessor, Ordered , BeanFactoryAware {
    private ApplicationContext beanFactory;


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        log.info("before -- {}", beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName){
        log.info("after -- {}", beanName);
        return bean;
    }

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory=beanFactory;
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
