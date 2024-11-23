package com.autumn.ormstarter;

import com.autumn.transaction.ConnectionManager;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Event.Event;
import org.example.FrameworkUtils.AutumnCore.Event.IocInitEvent;
import org.example.FrameworkUtils.AutumnCore.Event.Listener.EventListener;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author ziyuan
 * @since 2024.11
 */
@Slf4j
public class ConnectionManagerMinebatisImpl implements ConnectionManager, BeanFactoryAware, EventListener<IocInitEvent> {

    private AutumnBeanFactory beanFactory;

    @MyAutoWired
    private SqlSession sqlSession;

    public Connection getNewConnection() throws SQLException {
        return sqlSession.getConnection();
    }

    @Override
    public void setAutoConnection() throws SQLException {
//        Connection connection = sqlSession.getConnection();
//        ThreadConnection.setConnection(connection);
    }

    @Override
    public Connection getConnection() {
       return null;
    }

    @Override
    public void setConnection(Connection connection) {
//        ThreadConnection.setConnection(connection);
    }

    @Override
    public void removeConnection() {

    }

    @Override
    public void setBeanFactory(AutumnBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void onEvent(IocInitEvent event) {
        log.warn("成功接收到开机信号,MineBatis正在初始化连接并注册事务处理器");
    }

    @Override
    public boolean supportsEvent(Event event) {
        return event instanceof IocInitEvent;
    }
}

