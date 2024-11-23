package com.autumn.ormstarter;

import org.example.FrameworkUtils.Orm.MineBatis.Io.TransactionContext;

import java.sql.Connection;

/**
 * @author ziyuan
 * @since 2024.11
 */
public class ORMUtils {
    public static Connection getCurrentConnection() {
        Connection connection = TransactionContext.getCurrentConnection();
        if (connection != null) {
            return connection;
        } else {
            throw new RuntimeException("当前线程没有活跃的连接");
        }
    }
}
