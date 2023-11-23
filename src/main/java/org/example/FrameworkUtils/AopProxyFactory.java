package org.example.FrameworkUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.example.Aop.UserAopProxyFactory;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnMVC.AutunmnAopFactory;

import java.util.Arrays;


/**
 * @author wangzhiyi
 * @since 2023.10
 */
@Slf4j
@MyComponent
public class  AopProxyFactory {
//    private Class clazz;
//    public AopProxyFactory() {
//
//    }
//    public AopProxyFactory(Class<?> clazz) {
//        this.clazz = clazz;
//    }

    public <T>T create(Class aopadvice,Class clazz, String[] methods){
        System.out.println(Arrays.toString(methods));
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            log.info("Cglib启动!  " + method.getName());
            long startTime = System.currentTimeMillis();
            Object result =  ((AutunmnAopFactory) aopadvice.getDeclaredConstructor().newInstance()).intercept(obj, method, args,proxy);
            long endTime = System.currentTimeMillis();
            log.info("执行时间：" + (endTime-startTime) + " 毫秒");
            return result;
        });
        return (T)enhancer.create();

    }

}

