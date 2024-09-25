package com.autumn.transaction;

import java.sql.Connection;

/**
 * @author ziyuan
 * @since 2024.09
 */
public class ConnectionManager {
    private static final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    public static void setConnection(Connection connection) {
        connectionHolder.set(connection);
    }

    public static Connection getConnection() {
        return connectionHolder.get();
    }

    public static void removeConnection() {
        connectionHolder.remove();
    }
}
