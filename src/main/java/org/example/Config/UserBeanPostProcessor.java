package org.example.Config;

import lombok.extern.slf4j.Slf4j;
import org.apache.naming.factory.BeanFactory;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanPostProcessor;
import org.example.FrameworkUtils.AutumnCore.Ioc.Ordered;
import org.example.FrameworkUtils.Orm.MineBatis.session.DefaultSqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author ziyuan
 * @since 2024.05
 */
@MyComponent
@Slf4j
public class UserBeanPostProcessor implements BeanPostProcessor, Ordered , BeanFactoryAware {
    private AutumnBeanFactory beanFactory;
    private boolean state;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        log.info("before -- {}", beanName);
        if (Proxy.isProxyClass(bean.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(bean);
            String handlerClassName = handler.getClass().getName();
            int dollarIndex = handlerClassName.indexOf('$');
            if (dollarIndex != -1) {
                if(handlerClassName.substring(0, dollarIndex).equals(DefaultSqlSession.class.getName())){

                }

            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName){
        log.info("after -- {}", beanName);
        return bean;
    }

    @Override
    public void setBeanFactory(AutumnBeanFactory beanFactory) {
        this.beanFactory=beanFactory;
    }
}
