package org.example.FrameworkUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.Value;
import org.example.FrameworkUtils.Webutils.MyContext;

import java.lang.reflect.Field;


/**
 * @author wangzhiyi
 * @since 2023.10
 */
@Slf4j
@MyComponent
public class  AopProxyFactory {
    public <T>T create(Class clazz){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            log.info("Cglib启动!  " + method.getName());
            long startTime = System.currentTimeMillis();
            Object result = proxy.invokeSuper(obj, args);
            long endTime = System.currentTimeMillis();
            log.info("执行时间：" + (endTime-startTime) + " 毫秒");
            return result;
        });
        return (T)enhancer.create();

    }

}

