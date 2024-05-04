package org.example.FrameworkUtils.Orm.MineBatis.configuration;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.example.FrameworkUtils.Orm.MineBatis.mapping.ResultMap;
import org.example.FrameworkUtils.Orm.MineBatis.mapping.ResultMapField;


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
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(inputStream);
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


//    public void parse(InputStream inputStream) throws DocumentException {
//        //xxx:解析xml文件
//        SAXReader saxReader = new SAXReader();
//        Document document = saxReader.read(inputStream);
//        Element rootElement = document.getRootElement();
//        Attribute namespace = rootElement.attribute("namespace");
//        String namespaceValue = namespace.getValue();
//        List<Element> selectNodes = rootElement.selectNodes("//select");
//        List<Element> insertNodes = rootElement.selectNodes("//insert");
//        List<Element> deleteNodes = rootElement.selectNodes("//delete");
//        List<Element> updateNodes = rootElement.selectNodes("//update");
//        List<Element> resultMapNodes = rootElement.selectNodes("//resultMap");
//        List<Element> allList=new ArrayList<>();
//        allList.addAll(selectNodes);
//        allList.addAll(insertNodes);
//        allList.addAll(deleteNodes);
//        allList.addAll(updateNodes);
//        allList.addAll(resultMapNodes);
//
//        for (Element element : allList) {
//            String sqlCommandType = element.getName();
//            MappedStatement mappedStatement = new MappedStatement();
//            String id = element.attributeValue("id");
//            String resultType = element.attributeValue("resultType");
//            String resultMapName = element.attributeValue("resultMap");
//            String parameterType = element.attributeValue("parameterType");
//            String sqltext = element.getTextTrim();
//            String key = namespaceValue + "." + id;
//            mappedStatement.setId(id);
//            mappedStatement.setResultType(resultType);
//            mappedStatement.setParameterType(parameterType);
//            mappedStatement.setSql(sqltext);
//            mappedStatement.setSqlCommandType(sqlCommandType);
//            mappedStatement.setResultMapId(resultMapName);
//            if (resultMapName != null) {
//                System.out.println();
//                for (Element resultMapElement : resultMapNodes) {
//                    System.out.println(resultMapElement);
//                    String resultMapId = resultMapElement.attributeValue("id");
//                    String type = resultMapElement.attributeValue("type");
//                    List<Element> resultMapChildren = resultMapElement.elements();
//                    for (Element childElement : resultMapChildren) {
//                        String property = childElement.attributeValue("property");
//                        String column = childElement.attributeValue("column");
//                        String jdbcType = childElement.attributeValue("jdbcType");
//                        ResultMap resultMapField = new ResultMap(resultMapId, column, property, jdbcType, type);
//                        mappedStatement.addResultMap(resultMapField);
//                    }
//                }
//            }
//            configuration.getMappedStatementMap().put(key, mappedStatement);
//
//        }
//        System.out.println(configuration.getMappedStatementMap());
//    }
}
