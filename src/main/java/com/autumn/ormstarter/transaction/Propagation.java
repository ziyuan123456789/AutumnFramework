package com.autumn.ormstarter.transaction;

/**
 * @author ziyuan
 * @since 2024.11
 */

// 事务传播行为
public enum Propagation {

    //如果存在一个事务则继续使用同一个连接,如果没有事务则开启一个新的
    REQUIRED,
    //从数据源拿一个新的连接
    REQUIRES_NEW
}
