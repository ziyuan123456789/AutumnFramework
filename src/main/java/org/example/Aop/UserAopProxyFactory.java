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
        log.warn("用户切面方法开始执行" + method.getName());
        Object result = proxy.invokeSuper(obj, args);
        return result;
    }

}

