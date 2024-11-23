package com.autumn.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author ziyuan
 * @since 2024.09
 */
public interface ConnectionManager {

    void setAutoConnection() throws SQLException;

    Connection getConnection();

    void setConnection(Connection connection);

    void removeConnection();
}
