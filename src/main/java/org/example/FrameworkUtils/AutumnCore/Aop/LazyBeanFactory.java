package org.example.FrameworkUtils.AutumnCore.Aop;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.LazyLoader;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.function.Supplier;

/**
 * @author ziyuan
 * @since 2024.08
 */

/**
 * LazyBeanFactory这个人很懒,他常常问我:Autumn之神,为什么我自己不能懒加载
 */
public class LazyBeanFactory {

    public static Object createLazyBeanProxy(Class<?> beanClass, Supplier<Object> beanSupplier) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(beanClass);
        enhancer.setCallback((LazyLoader) () -> beanSupplier.get());
        return enhancer.create();
    }


}