package org.example.FrameworkUtils.Orm.MineBatis.session;

import org.example.FrameworkUtils.Orm.MineBatis.configuration.Configuration;

import java.sql.Connection;

/**
 * @author ziyuan
 * @since 2024.04
 */
public interface SqlSessionFactory {
    SqlSession openSession();
    SqlSession openSession(Connection connection);
    Configuration getConfiguration();
}
