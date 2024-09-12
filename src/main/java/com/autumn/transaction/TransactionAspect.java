package com.autumn.transaction;

import com.autumn.transaction.annotation.AutumnTransactional;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.Ordered;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyuan
 * @since 2024.07
 */

@MyAspect
@Slf4j
//还没有完成
public class TransactionAspect implements AutumnAopFactory, Ordered {
    private final Map<Class<?>, Boolean> cache = new ConcurrentHashMap<>();

    @Override
    public boolean shouldNeedAop(Class clazz, AutumnBeanFactory myContext) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(AutumnTransactional.class) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean shouldIntercept(Method method, Class clazz, AutumnBeanFactory myContext) {
        return cache.computeIfAbsent(clazz, cls -> {
            for (Method m : cls.getMethods()) {
                if (m.getAnnotation(AutumnTransactional.class) != null) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void doBefore(Object obj, Method method, Object[] args) {
        log.warn("事务开始：{}", method.getName());
//        TransactionManager.beginTransaction();
    }

    @Override
    public void doAfter(Object obj, Method method, Object[] args) {
        log.info("事务结束并提交：{}", method.getName());
//        TransactionManager.commitTransaction();
    }

    @Override
    public void doThrowing(Object obj, Method method, Object[] args, Exception e) {
        log.error("事务异常，准备回滚", e);
//        TransactionManager.rollbackTransaction();
    }

}

