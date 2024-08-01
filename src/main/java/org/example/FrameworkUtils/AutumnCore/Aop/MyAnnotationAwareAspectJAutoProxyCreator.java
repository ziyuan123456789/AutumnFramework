package org.example.FrameworkUtils.AutumnCore.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.InstantiationAwareBeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.List;

/**
 * @author ziyuan
 * @since 2024.05
 */
@MyComponent
@Slf4j
public class MyAnnotationAwareAspectJAutoProxyCreator implements CgLibAop, InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private AutumnBeanFactory beanFactory;

    private boolean shouldCreateProxy(List<AutumnAopFactory> factories, Class<?> beanClass) {

        for (AutumnAopFactory factory : factories) {
            if (factory.shouldNeedAop(beanClass, beanFactory)) {
                return true;
            }
        }
        return false;
    }

    public <T> T create(List<AutumnAopFactory> factories, Class<T> beanClass) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanClass);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            for (AutumnAopFactory aopFactory : factories) {
                if (aopFactory.shouldIntercept(method, beanClass, beanFactory)) {
                    try {
                        aopFactory.doBefore(obj, method, args);
                        Object result = aopFactory.intercept(obj, method, args, proxy);
                        aopFactory.doAfter(obj, method, args);
                        return result;
                    } catch (Exception e) {
                        aopFactory.doThrowing(obj, method, args, e);
                        throw e;
                    }
                }
            }

            return proxy.invokeSuper(obj, args);
        });
        return (T) enhancer.create();
    }

    @Override
    public Object postProcessBeforeInstantiation(List<AutumnAopFactory> factories, Class<?> beanClass, String beanName) {
        if (shouldCreateProxy(factories, beanClass)) {
            return create(factories, beanClass);
        }
        return null;
    }
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return null;
    }

    @Override
    public void setBeanFactory(AutumnBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
