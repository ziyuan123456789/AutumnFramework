package org.example.FrameworkUtils.AutumnCore.Aop;

import lombok.extern.slf4j.Slf4j;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;


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
                    AutumnAopFactory autumnAopFactory = ((AutumnAopFactory) aopadvice.getDeclaredConstructor().newInstance());
                    Object o = null;
                    try {
                        autumnAopFactory.doBefore(obj, method, args);
                        o = autumnAopFactory.intercept(obj, method, args, proxy);
                        autumnAopFactory.doAfter(obj, method, args);
                    } catch (Exception e) {
                        autumnAopFactory.doThrowing(obj, method, args, e);
                    }

                    return o;
                }
            }
            return proxy.invokeSuper(obj, args);
        });
        return (T)enhancer.create();

    }

}

