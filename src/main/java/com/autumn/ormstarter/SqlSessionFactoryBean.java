package com.autumn.ormstarter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Ioc.ApplicationContext;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanNameAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.FactoryBean;
import org.example.FrameworkUtils.Orm.MineBatis.Io.Resources;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * @author ziyuan
 * @since 2024.07
 */
@Data
@Slf4j
public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, BeanFactoryAware, BeanNameAware {

    private ApplicationContext beanFactory;


    @Override
    public SqlSessionFactory getObject() throws Exception {
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        String minebatisXml = beanFactory.getProperties().getProperty("MineBatis-configXML");
        InputStream inputStream;
        if (minebatisXml == null || minebatisXml.isEmpty()) {
            inputStream = Resources.getResourceAsSteam("minebatis-config.xml");
        } else {
            inputStream = Resources.getResourceAsSteam(minebatisXml);
        }
        SqlSessionFactory sqlSessionFactory = builder.build(inputStream);
        inputStream.close();
        return sqlSessionFactory;

    }

    @Override
    public Class<?> getObjectType() {
        return SqlSessionFactory.class;
    }

    @Override
    public void setBeanFactory(ApplicationContext beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setBeanName(String beanName) {
        log.info("我在IOC里的名字为:{}", beanName);
    }
}
