package com.autumn.cache;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.Ordered;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Slf4j
@MyAspect
public class CacheAopProxyHandler implements AutumnAopFactory, Ordered {

    @MyAutoWired
    private CacheManager cacheManager;

    @Override
    public boolean shouldNeedAop(Class clazz, ApplicationContext myContext) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getAnnotation(Cache.class) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldIntercept(Method method, Class clazz, ApplicationContext myContext) {
        return method.getAnnotation(Cache.class) != null;
    }

    @Override
    public void doBefore(Object obj, Method method, Object[] args) {
        log.warn("缓存切面方法开始预处理,切面处理器是{}处理的方法为:{}", this.getClass().getSimpleName(), method.getName());
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        String cacheKey = generateCacheKey(method, args);

        if (Boolean.TRUE.equals(cacheManager.containsCache(cacheKey))) {
            Object cachedResult = cacheManager.getCache(cacheKey);
            if (method.getReturnType().isInstance(cachedResult)) {
                log.info("缓存命中 - cacheKey 为 {} ", cacheKey);
                return cachedResult;
            }
        }
        log.info("缓存失效  - cacheKey 为 {} 准备invoke目标方法 ", cacheKey);
        Object result = method.invoke(obj, args);
        cacheManager.addCache(cacheKey, result);
        log.info("缓存更新 - cacheKey 为 {}", cacheKey);
        return result;
    }


    private String generateCacheKey(Method method, Object[] args) {
        StringBuilder keyBuilder = new StringBuilder(method.getName());
        for (Object arg : args) {
            keyBuilder.append("-").append(arg != null ? arg.hashCode() : "null");
        }
        return keyBuilder.toString();
    }


    @Override
    public void doAfter(Object obj, Method method, Object[] args) {
        log.info("缓存逻辑执行结束");
    }

    @Override
    public void doThrowing(Object obj, Method method, Object[] args, Exception e) {
        log.error("缓存切面方法抛出异常", e);
    }

    @Override
    public int getOrder() {
        return 2;
    }
}

