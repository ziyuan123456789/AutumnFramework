package org.example.FrameworkUtils.AutumnMVC;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
public interface AutunmnAopFactory {

    Object intercept(Object obj, Method method, Object[] args, MethodProxy pr) throws Throwable;

}
