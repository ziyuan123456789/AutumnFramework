package org.example.FrameworkUtils.Orm.MineBatis.session;

import org.dom4j.DocumentException;
import org.example.FrameworkUtils.Orm.MineBatis.configuration.Configuration;
import org.example.FrameworkUtils.Orm.MineBatis.configuration.XmlConfigBuilder;

import java.beans.PropertyVetoException;
import java.io.InputStream;

/**
 * @author ziyuan
 * @since 2024.04
 */
public class SqlSessionFactoryBuilder {
    private Configuration configuration;
    public SqlSessionFactory build(InputStream in) throws PropertyVetoException, DocumentException {

        configuration = new Configuration();
        XmlConfigBuilder xmlConfigBuilder = new XmlConfigBuilder(configuration);
        xmlConfigBuilder.parseMineBatisXmlConfig(in);
        DefaultSqlSessionFactory defaultSqlSessionFactory = new DefaultSqlSessionFactory(configuration);
        return defaultSqlSessionFactory;
    }

}
