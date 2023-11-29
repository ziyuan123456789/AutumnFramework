package org.example.FrameworkUtils;

import lombok.extern.slf4j.Slf4j;

import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.AutunmnAopFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.util.Arrays;


/**
 * @author wangzhiyi
 * @since 2023.10
 */
@Slf4j
@MyComponent
public class  AopProxyFactory {

    public <T>T create(Class aopadvice,Class clazz, String[] methods){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            for (String methodName : methods) {
                if (methodName.equals(method.getName())) {
                    long startTime = System.currentTimeMillis();
                    Object result = ((AutunmnAopFactory) aopadvice.getDeclaredConstructor().newInstance()).intercept(obj, method, args, proxy);
                    long endTime = System.currentTimeMillis();
                    log.info("执行时间：" + (endTime - startTime) + " 毫秒");
                    return result;
                }
            }
            return proxy.invokeSuper(obj, args);
        });
        return (T)enhancer.create();

    }

}

