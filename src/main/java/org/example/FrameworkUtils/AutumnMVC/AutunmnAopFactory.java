package org.example.FrameworkUtils.AutumnMVC;


import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author ziyuan
 * @since 2023.11
 */
public interface AutunmnAopFactory {

    Object intercept(Object obj, Method method, Object[] args, MethodProxy pr) throws Throwable;

}
