package org.example.service;

import java.sql.SQLException;

/**
 * @author ziyuan
 * @since 2024.11
 */

public interface TransactionService {
    String transactionTest() throws SQLException;

    void transactionRequireNew() throws SQLException;

    void transactionRequire() throws SQLException;
}
