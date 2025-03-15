package com.autumn.ormstarter.transaction;

import static com.autumn.ormstarter.transaction.Propagation.REQUIRED;
import static com.autumn.ormstarter.transaction.Propagation.REQUIRES_NEW;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Event.ApplicationEvent;
import org.example.FrameworkUtils.AutumnCore.Event.ContextFinishRefreshEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.ApplicationListener;
import org.example.FrameworkUtils.Orm.MineBatis.Io.TransactionContext;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author ziyuan
 * @since 2024.11
 */

@Slf4j
public class TransactionManager implements ApplicationListener<ContextFinishRefreshEvent> {

    @MyAutoWired
    private SqlSessionFactory sqlSessionFactory;

    // 开始事务
    public void beginTransaction(Propagation propagation) throws SQLException {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        switch (propagation) {
            case REQUIRED:
                if (TransactionContext.getCurrentConnection() == null) {
                    Connection connection = sqlSession.getConnection();
                    connection.setAutoCommit(false);
                    TransactionContext.pushConnection(connection);
                    log.info("开启事务,隔离等级为：{}", REQUIRED);
                } else {
                }
                break;
            case REQUIRES_NEW:
                Connection newConnection = sqlSession.getConnection();
                newConnection.setAutoCommit(false);
                TransactionContext.pushConnection(newConnection);
                log.info("开启事务,隔离等级为：{}", REQUIRES_NEW);
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
            log.info("事务提交成功");
        }
    }


    public void rollbackTransaction() throws SQLException {
        Connection connection = TransactionContext.getCurrentConnection();
        if (connection != null) {
            connection.rollback();
            connection.close();
            TransactionContext.popConnection();
            log.info("事务回滚成功");
        }
    }

    @Override
    public void onApplicationEvent(ContextFinishRefreshEvent event) {
        log.warn("成功接收到开机信号,MineBatis事务处理器已成功注册");
    }

    @Override
    public boolean supportsEvent(ApplicationEvent event) {
        return event instanceof ContextFinishRefreshEvent;
    }
}
