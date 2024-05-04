package org.example.FrameworkUtils.Orm.MineBatis.executor;



import org.example.FrameworkUtils.Orm.MineBatis.configuration.Configuration;
import org.example.FrameworkUtils.Orm.MineBatis.configuration.MappedStatement;

import java.lang.reflect.Method;
import java.sql.SQLException;

/**
 * @author ziyuan
 * @since 2024.04
 */
//xxx:sql执行器
public interface Executor {
    int update(Configuration configuration, MappedStatement mappedStatement, Method method, Object[] params) throws SQLException;
    //xxx:查询
    <T> T query(Configuration configuration, MappedStatement mappedStatement, Object[] params) throws Exception;

    <T> T query(Configuration configuration, MappedStatement mappedStatement, Method method, Object[] params) throws Exception;


    <T> T selectQuery(Configuration configuration, MappedStatement mappedStatement, Object[] params) throws Exception;

}
