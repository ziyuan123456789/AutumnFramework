package org.example.FrameworkUtils.Orm.MineBatis.configuration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.example.FrameworkUtils.Orm.MineBatis.mapping.ResultMap;
import org.example.FrameworkUtils.Orm.MineBatis.mapping.ResultMapField;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.04
 */
//xxx:dom4j解析xml文件
public class XmlMapperBuilder {
    private Configuration configuration;

    public XmlMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }


    public void parse(InputStream inputStream) throws DocumentException {
        InputStream bufferedInput = new BufferedInputStream(inputStream);
        SAXReader saxReader = new SAXReader(false);
        Document document = saxReader.read(bufferedInput);
        Element rootElement = document.getRootElement();

        String namespaceValue = rootElement.attributeValue("namespace");
        configuration.addMapperLocation(namespaceValue);

        parseResultMaps(rootElement.selectNodes("//resultMap"));

        parseStatements(rootElement, namespaceValue);
    }

    private void parseResultMaps(List<Element> resultMapNodes) {
        for (Element resultMapElement : resultMapNodes) {
            String resultMapId = resultMapElement.attributeValue("id");
            String type = resultMapElement.attributeValue("type");
            boolean isDisable = Boolean.parseBoolean(resultMapElement.attributeValue("isDisable"));
            List<Element> resultMapChildren = resultMapElement.elements();
            ResultMap resultMap = new ResultMap(resultMapId,type,isDisable);
            for (Element childElement : resultMapChildren) {
                String property = childElement.attributeValue("property");
                String column = childElement.attributeValue("column").toLowerCase();
                String jdbcType = childElement.attributeValue("jdbcType");
                String javaType = childElement.attributeValue("javaType");
                ResultMapField resultMapField = new ResultMapField(column, property, jdbcType, javaType);
                resultMap.addField(resultMapField);
                configuration.addResultMap(resultMapId, resultMap);
            }
        }
    }

    private void parseStatements(Element rootElement, String namespace) {
        List<Element> nodes = new ArrayList<>();
        nodes.addAll(rootElement.selectNodes("//select"));
        nodes.addAll(rootElement.selectNodes("//insert"));
        nodes.addAll(rootElement.selectNodes("//delete"));
        nodes.addAll(rootElement.selectNodes("//update"));

        for (Element element : nodes) {
            parseStatement(element, namespace);
        }

    }

    private void parseStatement(Element element, String namespace) {
        MappedStatement mappedStatement = new MappedStatement();
        String id = element.attributeValue("id");
        String resultType = element.attributeValue("resultType");
        String resultMapId = element.attributeValue("resultMap");
        String parameterType = element.attributeValue("parameterType");
        String sqltext = element.getTextTrim();
        String key = namespace + "." + id;

        mappedStatement.setId(id);
        mappedStatement.setResultType(resultType);
        mappedStatement.setParameterType(parameterType);
        mappedStatement.setSql(sqltext);
        mappedStatement.setSqlCommandType(element.getName());
        mappedStatement.setResultMapId(resultMapId);
        configuration.getMappedStatementMap().put(key, mappedStatement);
    }
}
