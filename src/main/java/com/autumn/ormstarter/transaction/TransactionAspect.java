package com.autumn.ormstarter.transaction;

import com.autumn.ormstarter.transaction.annotation.AutumnTransactional;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAspect;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Aop.AutumnAopFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ziyuan
 * @since 2024.07
 */


/**
 * 事务切面负责处理事务的开启提交和回滚
 * 如果有@AutumnTransactional 注解则开启事务 在方法执行后提交事务 如果发生异常则回滚
 * 具体事情委托给TransactionManager完成
 */
@MyAspect
@Slf4j
public class TransactionAspect implements AutumnAopFactory {

    @MyAutoWired
    private TransactionManager transactionManager;

    private final Map<Class<?>, Boolean> cache = new ConcurrentHashMap<>();

    @Override
    public boolean shouldNeedAop(Class clazz, ApplicationContext myContext) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(AutumnTransactional.class) != null) {
                return true;
            }
        }
        return false;
    }
    @Override
    public boolean shouldIntercept(Method method, Class clazz, ApplicationContext myContext) {
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
        AutumnTransactional transactional = method.getAnnotation(AutumnTransactional.class);
        if (transactional != null) {
            Propagation propagation = transactional.propagation();
            Isolation isolation = transactional.isolation();
            try {
                transactionManager.beginTransaction(propagation, isolation);
            } catch (SQLException e) {
                throw new RuntimeException("开启事务失败", e);
            }
        }
    }

    @Override
    public void doAfter(Object obj, Method method, Object[] args) {
        AutumnTransactional transactional = method.getAnnotation(AutumnTransactional.class);
        if (transactional != null) {
            try {
                transactionManager.commitTransaction();
            } catch (SQLException e) {
                throw new RuntimeException("提交事务失败", e);
            }
        }
    }

    @Override
    public void doThrowing(Object obj, Method method, Object[] args, Exception e) {
        AutumnTransactional transactional = method.getAnnotation(AutumnTransactional.class);
        if (transactional != null) {
            Class<? extends Throwable>[] rollbackFor = transactional.rollbackFor();
            boolean shouldRollback = false;
            for (Class<? extends Throwable> rollbackException : rollbackFor) {
                if (rollbackException.isAssignableFrom(e.getClass())) {
                    shouldRollback = true;
                    break;
                }
            }
            if (shouldRollback) {
                try {
                    transactionManager.rollbackTransaction();
                } catch (SQLException ex) {
                    throw new RuntimeException("回滚事务失败", ex);
                }
            }
        }
    }


}

