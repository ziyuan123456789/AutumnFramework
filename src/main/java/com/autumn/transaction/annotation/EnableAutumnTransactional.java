package com.autumn.transaction.annotation;

import com.autumn.transaction.TransactionAspect;
import com.autumn.transaction.TransactionManager;
import org.example.FrameworkUtils.AutumnCore.Annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Import({TransactionAspect.class, TransactionManager.class})
public @interface EnableAutumnTransactional {
}
