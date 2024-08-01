package org.example.FrameworkUtils.AutumnCore.Aop;

import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.InstantiationAwareBeanPostProcessor;

import java.util.List;

/**
 * @author ziyuan
 * @since 2024.07
 */
public class TransactionAspect  implements CgLibAop, InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    @Override
    public Object postProcessBeforeInstantiation(List<AutumnAopFactory> factories, Class<?> beanClass, String beanName) {
        return null;
    }

    @Override
    public void setBeanFactory(AutumnBeanFactory beanFactory) {

    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return null;
    }
}
