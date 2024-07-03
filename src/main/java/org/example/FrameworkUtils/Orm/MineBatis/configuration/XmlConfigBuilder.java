package org.example.FrameworkUtils.Orm.MineBatis.configuration;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.example.FrameworkUtils.Orm.MineBatis.Io.Resources;
import org.xml.sax.SAXException;


import java.beans.PropertyVetoException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author ziyuan
 * @since 2024.04
 */
public class XmlConfigBuilder {
    private Configuration configuration;

    public XmlConfigBuilder(Configuration configuration) {
        this.configuration = configuration;
    }


    public Configuration parseMineBatisXmlConfig(InputStream inputStream) throws DocumentException, PropertyVetoException, SAXException {
        SAXReader saxReader = new SAXReader();
        // 禁止加载外部 DTD
        saxReader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        // 禁用所有外部实体
        saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        // 禁用 DOCTYPE 声明处理
        saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        Document document = saxReader.read(inputStream);
        Element rootElement = document.getRootElement();
        List<Element> propertyNodes = rootElement.selectNodes("//property");
        //xxx:解析xml
        Properties properties = new Properties();
        for (Element propertyNode : propertyNodes) {
            String name = propertyNode.attributeValue("name");
            String value = propertyNode.attributeValue("value");
            properties.setProperty(name, value);
        }
        //xxx:构建sql连接池
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setDriverClass(properties.getProperty("driverClass"));
        comboPooledDataSource.setJdbcUrl(properties.getProperty("jdbcUrl"));
        comboPooledDataSource.setUser(properties.getProperty("username"));
        comboPooledDataSource.setPassword(properties.getProperty("password"));
        configuration.setDataSource(comboPooledDataSource);
        //xxx:开始寻找mapper
        List<Element> mapperNodes = rootElement.selectNodes("//mapper");
        for (Element mapperNode : mapperNodes) {
            String resource = mapperNode.attributeValue("resource");
            InputStream resourceAsStream = Resources.getResourceAsSteam(resource);
            //xxx:解析所有的mapper
            XmlMapperBuilder xmlMapperBuilder = new XmlMapperBuilder(configuration);
            xmlMapperBuilder.parse(resourceAsStream);
        }
        return configuration;
    }
}
