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
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutumnTransactional {
    Propagation propagation() default Propagation.REQUIRED;

    Isolation isolation() default Isolation.DEFAULT;

    Class<? extends Throwable>[] rollbackFor() default {RuntimeException.class};
}
