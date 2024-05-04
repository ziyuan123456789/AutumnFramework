package org.example.FrameworkUtils.Orm.MineBatis.session;

import lombok.Data;
import org.example.FrameworkUtils.Orm.MineBatis.configuration.Configuration;
import org.example.FrameworkUtils.Orm.MineBatis.type.BaseTypeHandlerRegistry;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.Impl.IntHandler;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.Impl.IntegerHandler;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.Impl.LocalDateTimeHandler;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.Impl.StringHandler;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandlerRegistry;


import java.sql.Connection;
import java.time.LocalDateTime;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Data
//xxx:这个是默认工厂,直接硬编码注册TypeHandler,再另一个实现类由AutumnIoc接手,自动扫描注册
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private Configuration configuration;
    private TypeHandlerRegistry typeHandlerRegistry;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
        typeHandlerRegistry = new BaseTypeHandlerRegistry();
        typeHandlerRegistry.register(int.class, new IntHandler());
        typeHandlerRegistry.register(String.class, new StringHandler());
        typeHandlerRegistry.register(Integer.class, new IntegerHandler());
        typeHandlerRegistry.register(LocalDateTime.class, new LocalDateTimeHandler());
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration,typeHandlerRegistry);
    }

    //xxx:给你一个切换Connection的机会,我想这样可以支持读写分离?或者多数据库连接
    @Override
    public SqlSession openSession(Connection connection) {
        return null;
    }
}
