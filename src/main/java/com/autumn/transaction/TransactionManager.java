package com.autumn.transaction;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.Orm.MineBatis.Io.TransactionContext;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author ziyuan
 * @since 2024.11
 */

@Slf4j
public class TransactionManager {
    @MyAutoWired
    private SqlSession sqlSession;


    // 开始事务
    public void beginTransaction(Propagation propagation) throws SQLException {
        switch (propagation) {
            case REQUIRED:
                if (TransactionContext.getCurrentConnection() == null) {
                    Connection connection = sqlSession.getConnection();
                    connection.setAutoCommit(false);
                    TransactionContext.pushConnection(connection);
                } else {
                }
                break;
            case REQUIRES_NEW:
                Connection newConnection = sqlSession.getConnection();
                newConnection.setAutoCommit(false);
                TransactionContext.pushConnection(newConnection);
                break;
            default:
                throw new UnsupportedOperationException("不支持的事务传播行为：" + propagation);
        }
    }


    public void commitTransaction() throws SQLException {
        Connection connection = TransactionContext.getCurrentConnection();
        if (connection != null) {
            connection.commit();
            connection.close();
            TransactionContext.popConnection();
        }
    }


    public void rollbackTransaction() throws SQLException {
        Connection connection = TransactionContext.getCurrentConnection();
        if (connection != null) {
            connection.rollback();
            connection.close();
            TransactionContext.popConnection();
        }
    }
}
