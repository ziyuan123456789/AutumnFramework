package org.example.FrameworkUtils.Orm.MineBatis.session;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.04
 */
public interface SqlSession {

    <T>List<T> selectList(String statementId, Method method, Object[] params) throws Exception;
    <T>T selectOne(String statementId,Method method,  Object[] params)throws Exception;
    <T>T insert(String statementId,Method method,  Object[] params)throws Exception;
    <T>T update(String statementId,Method method,  Object[] params)throws Exception;
    <T>T delete(String statementId, Method method, Object[] params)throws Exception;
    <T>T getMapper(Class<?> mapperClass) throws Exception;

}
