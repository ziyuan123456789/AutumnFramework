package com.autumn.ormstarter;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Ioc.FactoryBean;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;

/**
 * @author ziyuan
 * @since 2025.02
 */
public class MapperFactoryBean<T> implements FactoryBean<T> {

    private final Class<T> mapperInterface;

    @MyAutoWired
    private SqlSessionFactory sqlSessionFactory;

    @MyAutoWired
    public MapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    public T getObject() throws Exception {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.getMapper(mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }


}

