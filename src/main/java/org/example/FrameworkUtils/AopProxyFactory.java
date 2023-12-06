package org.example.FrameworkUtils;

import lombok.extern.slf4j.Slf4j;

import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.AutunmnAopFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;
import java.util.Arrays;


/**
 * @author ziyuan
 * @since 2023.10
 */
@Slf4j
@MyComponent
public class  AopProxyFactory {

    public <T>T create(Class aopadvice,Class clazz, String[] methods){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            Class<?> father=obj.getClass().getSuperclass();
            for(Method m:father.getDeclaredMethods()){
                if(m.getName().equals(method.getName())){
                    method=m;
                    break;
                }
            }
            for (String methodName : methods) {
                if (methodName.equals(method.getName())) {
                    return ((AutunmnAopFactory) aopadvice.getDeclaredConstructor().newInstance()).intercept(obj, method, args, proxy);
                }
            }
            return proxy.invokeSuper(obj, args);
        });
        return (T)enhancer.create();

    }

}

