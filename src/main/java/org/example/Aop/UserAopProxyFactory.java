package org.example.Aop;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnMVC.AutunmnAopFactory;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Stack;


@Slf4j
@MyAspect
public class UserAopProxyFactory implements AutunmnAopFactory {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Stack stack = new Stack();

        log.warn("用户切面方法开始预处理,切面处理器是"+this.getClass().getName()+"处理的方法为:"+method.getName() );
        return proxy.invokeSuper(obj, args);

    }

}

