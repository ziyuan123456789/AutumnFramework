package org.example.FrameworkUtils.Orm.MineBatis.session;

import org.example.FrameworkUtils.Orm.MineBatis.configuration.Configuration;
import org.example.FrameworkUtils.Orm.MineBatis.type.BaseTypeHandlerRegistry;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandlerRegistry;

import java.sql.Connection;

/**
 * @author ziyuan
 * @since 2024.07
 */
public class AutumnSqlSessionFactory implements SqlSessionFactory {
    private Configuration configuration;
    private TypeHandlerRegistry typeHandlerRegistry;

    public AutumnSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
        typeHandlerRegistry = new BaseTypeHandlerRegistry();
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration, typeHandlerRegistry);
    }

    //xxx:给你一个切换Connection的机会,我想这样可以支持读写分离?或者多数据库连接
    @Override
    public SqlSession openSession(Connection connection) {
        return null;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }
}
