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

    /**
     * 这是一个手动的构造器注入,构造器注入的条件已经都具备,但是我还没研究spring是如何处理构造器的注入,因此没有启用@MyAutoWired的自动构造器注入
     * 在starter中,把mapper包装为Bean定义的同时替换实现类为FactoryBean,并手动配置了构造器参数(具体查看MineBatisStarter#postProcessBeanDefinitionRegistry)
     * 在creteBean环节,框架发现存在有候选的构造器还有现成的参数,于是便可以反射调用构造器创建对象
     */
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

