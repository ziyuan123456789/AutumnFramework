package org.example.Aop;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodProxy;
import org.example.FrameworkUtils.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnMVC.AutunmnAopFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@Slf4j
@MyAspect
public class UserAopProxyFactory implements AutunmnAopFactory {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("用户切面方法" + method.getName());
        Object result = proxy.invokeSuper(obj, args);
        System.out.println("用户切面执行结束" + method.getName());
        return result;
    }

}

