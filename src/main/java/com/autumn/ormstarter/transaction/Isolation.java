package com.autumn.ormstarter.transaction;

/**
 * @author ziyuan
 * @since 2024.11
 */
//事务隔离等级
public enum Isolation {

    TRANSACTION_NONE(0),

    TRANSACTION_READ_UNCOMMITTED(1),

    TRANSACTION_REPEATABLE_READ(4),

    TRANSACTION_READ_COMMITTED(2),

    TRANSACTION_SERIALIZABLE(8);

    private final int value;

    Isolation(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
