package org.example.Bean;

import lombok.Data;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.AutumnCore.Ioc.FactoryBean;
import org.example.FrameworkUtils.Orm.MineBatis.Io.Resources;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactory;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;

/**
 * @author ziyuan
 * @since 2024.07
 */
@Data
@MyComponent
public class factorybeantest implements FactoryBean<SqlSession>, BeanFactoryAware {
    private AutumnBeanFactory beanFactory;

    @Override
    public SqlSession getObject() throws Exception {
        String minebatisXml = beanFactory.getProperties().getProperty("MineBatis-configXML");
        InputStream inputStream;
        if (minebatisXml == null || minebatisXml.isEmpty()) {
            inputStream = Resources.getResourceAsSteam("minebatis-config.xml");
        } else {
            inputStream = Resources.getResourceAsSteam(minebatisXml);
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        inputStream.close();
        return sqlSessionFactory.openSession();

    }

    @Override
    public Class<?> getObjectType() {
        return this.getClass();
    }

    @Override
    public void setBeanFactory(AutumnBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}