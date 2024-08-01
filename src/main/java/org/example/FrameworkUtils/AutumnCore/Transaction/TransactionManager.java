package org.example.FrameworkUtils.AutumnCore.Transaction;

/**
 * @author ziyuan
 * @since 2024.07
 */
public interface TransactionManager {
    void beginTransaction();
    void commit();
    void rollback();
}