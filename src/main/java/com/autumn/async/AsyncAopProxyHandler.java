package com.autumn.async;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.Ordered;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * @author ziyuan
 * @since 2024.08
 */


@Slf4j
@MyAspect
public class AsyncAopProxyHandler implements AutumnAopFactory, Ordered {

    @MyAutoWired
    ExecutorService executorService;

    @Override
    public boolean shouldNeedAop(Class clazz, ApplicationContext myContext) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(Async.class) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldIntercept(Method method, Class clazz, ApplicationContext myContext) {
        return method.getAnnotation(Async.class) != null;
    }

    @Override
    public void doBefore(Object obj, Method method, Object[] args) {
        log.warn("异步切面方法开始预处理,切面处理器是{}处理的方法为:{}", this.getClass().getSimpleName(), method.getName());
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        CompletableFuture<Object> future = new CompletableFuture<>();
        executorService.submit(() -> {
            try {
                Object result = proxy.invokeSuper(obj, args);
                future.complete(result);
            } catch (Throwable throwable) {
                log.error("异步切面方法执行异常", throwable);
                future.completeExceptionally(throwable);
                throw new RuntimeException(throwable);
            }
        });
        log.info("异步任务已经提交,返回TASK");
        return future;
    }

    @Override
    public void doAfter(Object obj, Method method, Object[] args) {
        log.info("异步逻辑执行结束");
    }

    @Override
    public void doThrowing(Object obj, Method method, Object[] args, Exception e) {
        log.error("异步切面方法抛出异常", e);
    }

    @Override
    public int getOrder() {
        return 3;
    }
}

