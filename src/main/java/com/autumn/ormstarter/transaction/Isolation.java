package com.autumn.ormstarter.transaction;

/**
 * @author ziyuan
 * @since 2024.11
 */
public enum Isolation {
    DEFAULT,
    READ_COMMITTED,
    REPEATABLE_READ,
    SERIALIZABLE
}
