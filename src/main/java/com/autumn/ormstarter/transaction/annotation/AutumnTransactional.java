package com.autumn.ormstarter.transaction.annotation;

import com.autumn.ormstarter.transaction.Isolation;
import com.autumn.ormstarter.transaction.Propagation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2024.07
 */

/**
 * 用于标记一个方法为事务方法
 * 该注解可以配置事务的传播行为,隔离级别以及回滚规则
 * 默认情况下 事务传播行为为 REQUIRED 隔离级别为 TRANSACTION_READ_COMMITTED
 * 回滚规则默认为 RuntimeException.class
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutumnTransactional {
    Propagation propagation() default Propagation.REQUIRED;

    Isolation isolation() default Isolation.TRANSACTION_READ_COMMITTED;

    Class<? extends Throwable>[] rollbackFor() default {RuntimeException.class};
}
