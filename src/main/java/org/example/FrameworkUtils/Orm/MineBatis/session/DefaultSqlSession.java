package org.example.FrameworkUtils.Orm.MineBatis.session;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Orm.MineBatis.configuration.Configuration;
import org.example.FrameworkUtils.Orm.MineBatis.configuration.MappedStatement;
import org.example.FrameworkUtils.Orm.MineBatis.executor.Executor;
import org.example.FrameworkUtils.Orm.MineBatis.executor.SimpleExecutor;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandlerRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Slf4j
//xxx:如何水代码行数?
public class DefaultSqlSession implements SqlSession {
    private final Configuration configuration;
    @Getter
    private final Executor executor;
    @Getter
    private TypeHandlerRegistry typeHandlerRegistry;

    public DefaultSqlSession(Configuration configuration, TypeHandlerRegistry typeHandlerRegistry) {
        this.configuration = configuration;
        executor=new SimpleExecutor(typeHandlerRegistry);
        this.typeHandlerRegistry=typeHandlerRegistry;
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) throws Exception {
        Object proxyInstance = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    String methodName = method.getName();
                    String className = method.getDeclaringClass().getName();
                    String statementId = className + "." + methodName;
                    Type genericReturnType = method.getGenericReturnType();
                    MappedStatement mappedStatement = configuration.getMappedStatementMap().get(statementId);
                    if (mappedStatement == null) {
                        throw new IllegalStateException("没检测到标签" + statementId);
                    }
                    //xxx:依照xml标签确认增删改查
                    String sqlCommandType = mappedStatement.getSqlCommandType();
                    //xxx:根据sql类型调用不同的方法
                    switch (sqlCommandType) {
                        case "insert":
                            return insert(statementId, method, args);
                        case "delete":
                            return delete(statementId, method, args);
                        case "update":
                            return update(statementId, method, args);
                        case "select":
                            if (genericReturnType instanceof ParameterizedType) {
                                return selectList(statementId, method, args);
                            } else {
                                return selectOne(statementId, method, args);
                            }
                        default:
                            throw new UnsupportedOperationException("你写的什么?" + sqlCommandType);
                    }
                }
            }
        );
        return (T) proxyInstance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return configuration.getDataSource().getConnection();
    }


    @Override
    public <T> List<T> selectList(String statementId, Method method, Object[] args) throws Exception {
        List<T> returnList;
        try {
            MappedStatement ms = this.configuration.getMappedStatement(statementId);
            returnList = this.executor.query(configuration,ms,method,args);
        } catch (Exception e) {
            log.error("sql执行失败");
            throw e;
        }
        return returnList;
    }

    @Override
    public <T> T selectOne(String statementId, Method method, Object[] args) throws Exception {
        List<T> list = this.selectList(statementId, method,args);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new RuntimeException("不唯一" + list.size());
        } else {
            return null;
        }
    }

    @Override
    public int insert(String statementId, Method method, Object[] args) throws Exception {
        return update(statementId, method, args);
    }

    @Override
    public int update(String statementId, Method method, Object[] args) throws Exception {
        try {
            MappedStatement ms = this.configuration.getMappedStatement(statementId);
            return this.executor.update(configuration, ms, method, args);
        } catch (Exception e) {
            log.error("更新sql执行失败");
            throw e;
        }
    }

    @Override
    public int delete(String statementId, Method method, Object[] args) throws Exception {
        return update(statementId, method, args);
    }
}
