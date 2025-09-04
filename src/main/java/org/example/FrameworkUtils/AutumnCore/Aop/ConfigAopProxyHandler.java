package org.example.FrameworkUtils.AutumnCore.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.AutumnBean;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyConfig;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyOrder;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2025.02
 */

// 这个类用来保护@Bean的单例性,对MyConfig配置类进行代理
@Slf4j
@MyAspect
@MyOrder(Integer.MIN_VALUE)
public class ConfigAopProxyHandler implements AutumnAopFactory, BeanFactoryAware {

    private final Set<Method> cache = new HashSet<>();

    private ApplicationContext beanFactory;


    @Override
    public boolean shouldNeedAop(Class clazz, ApplicationContext myContext) {
        MyConfig myConfig = (MyConfig) clazz.getAnnotation(MyConfig.class);
        if (myConfig != null) {
            if (myConfig.proxyBeanMethods()) {
                log.debug("对配置类{}进行代理,保证@Bean方法的单例性", clazz.getName());
                return true;
            }
            log.debug("你关闭了对配置类{}的代理,出了问题不要怪我", clazz.getName());
            return false;
        } else {
            return false;
        }

    }

    @Override
    public boolean shouldIntercept(Method method, Class clazz, ApplicationContext myContext) {
        return method.getAnnotation(AutumnBean.class) != null;
    }

    @Override
    public void doBefore(Object obj, Method method, Object[] args) {
    }

    @Override
    public void doAfter(Object obj, Method method, Object[] args) {

    }

    @Override
    public void doThrowing(Object obj, Method method, Object[] args, Exception e) {
        log.error("切面方法抛出异常", e);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy pr) throws Throwable {
        if (cache.contains(method)) {
            log.warn("请不要调用配置类的@Bean方法,现在从容器getBean获取对象返还,@Bean生产的单例对象在整个生命周期中仅仅会被创建一次");
            AutumnBean autumnBean = method.getAnnotation(AutumnBean.class);
            if (autumnBean.value() == null || autumnBean.value().isEmpty()) {
                return beanFactory.getBean(method.getReturnType());
            } else {
                return beanFactory.getBean(autumnBean.value());
            }
        }
        Object o = method.invoke(obj, args);
        cache.add(method);
        return o;
    }


    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }
}
