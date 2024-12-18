package org.example.FrameworkUtils.AutumnCore.Aop;


import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author ziyuan
 * @since 2023.11
 */
public interface AutumnAopFactory {
    boolean shouldNeedAop(Class clazz, AutumnBeanFactory myContext);
    boolean shouldIntercept(Method method,Class clazz, AutumnBeanFactory myContext);
    void doBefore(Object obj, Method method, Object[] args);

     default Object intercept(Object obj, Method method, Object[] args, MethodProxy pr) throws Throwable {
         return null;
     };

    void doAfter(Object obj, Method method, Object[] args);
    void doThrowing(Object obj, Method method, Object[] args,Exception e);

}
