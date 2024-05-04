package org.example.FrameworkUtils.Orm.MineBatis.executor;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Orm.MineBatis.GenericTokenParser;
import org.example.FrameworkUtils.Orm.MineBatis.ParameterMapping;
import org.example.FrameworkUtils.Orm.MineBatis.ParameterMappingTokenHandler;
import org.example.FrameworkUtils.Orm.MineBatis.configuration.BoundSql;
import org.example.FrameworkUtils.Orm.MineBatis.configuration.Configuration;
import org.example.FrameworkUtils.Orm.MineBatis.configuration.MappedStatement;
import org.example.FrameworkUtils.Orm.MineBatis.mapping.ResultMap;
import org.example.FrameworkUtils.Orm.MineBatis.mapping.ResultMapField;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.TypeHandler;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandlerRegistry;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Slf4j
//xxx:简单执行器
public class SimpleExecutor implements Executor {

    private TypeHandlerRegistry typeHandlerRegistry;

    private ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
    public SimpleExecutor(TypeHandlerRegistry typeHandlerRegistry){
        this.typeHandlerRegistry=typeHandlerRegistry;
    }

    @Override
    public int update(Configuration configuration, MappedStatement mappedStatement, Method method, Object[] params) throws SQLException {
        return 0;
    }

    @Override
    public <T> T query(Configuration configuration, MappedStatement mappedStatement, Object[] params) throws Exception {

        return null;
    }

    @Override
    public <T> T query(Configuration configuration, MappedStatement mappedStatement, Method method, Object[] args) throws Exception {
        boolean isUseResultMap = false;
        Connection connection = configuration.getDataSource().getConnection();
        String sql = mappedStatement.getSql();
        BoundSql boundSql = getBoundSql(sql);
        String parameterType = mappedStatement.getResultType();
        if (parameterType == null) {
            parameterType = configuration.getResultMap(mappedStatement.getResultMapId()).getType();
            isUseResultMap = true;
        }
        //xxx:把参数和参数名对应起来,放到map里
        Map<String, Object> paramValueMapping = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            paramValueMapping.put(parameter.getName(), args[i]);
        }
        String jdbcSql = boundSql.getJdbcsqlText();
        PreparedStatement statement = connection.prepareStatement(jdbcSql);
        //xxx:在替换#{}的同时,取出里面的参数名称封装为ParameterMapping,放到数组中,保存好顺序
        List<ParameterMapping> parameterMapping = parameterMappingTokenHandler.getParameterMapping();
        for (int i = 0; i < parameterMapping.size(); i++) {
            //xxx:拿到参数名字,根据名字找到对应的值,然后根据值的类型找到对应的处理器,进行setParameter
            String argName = parameterMapping.get(i).getProperty();
            Class<?> clazz = paramValueMapping.get(argName).getClass();
            //xxx:jdbc这个为什么不从0开始?反而从1开始?
            typeHandlerRegistry.getTypeHandlers().get(clazz).setParameter(statement, i + 1, paramValueMapping.get(argName));
        }
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        List<T> returnList = new ArrayList<>();
        //xxx:依照resultType确定返回值
        Class<?> parameterTypeClass = getClassType(parameterType);
        if (isUseResultMap) {
            ResultMap resultMap = configuration.getResultMap(mappedStatement.getResultMapId());
            ResultSetMetaData metaData = resultSet.getMetaData();

            //xxx 当 isDisable 为 true 时，仅映射 ResultMap 中定义的字段
            if (resultMap.isDisable()) {
                while (resultSet.next()) {
                    T instance = (T) getClassType(resultMap.getType()).getDeclaredConstructor().newInstance();
                    for (ResultMapField field : resultMap.getFields()) {
                        mapFieldWithHandler(resultSet, field, instance, typeHandlerRegistry);
                    }
                    returnList.add(instance);
                }
            } else {
                //xxx 映射整个 ResultSetMetaData，但对 ResultMap 中定义的字段执行特殊映射
                while (resultSet.next()) {
                    T instance = (T) getClassType(resultMap.getType()).getDeclaredConstructor().newInstance();
                    Map<String, ResultMapField> fieldMap = resultMap.getFields()
                            .stream()
                            .collect(Collectors.toMap(ResultMapField::getColumn, Function.identity()));
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        String columnName = metaData.getColumnLabel(i).toLowerCase();

                        ResultMapField field = fieldMap.getOrDefault(columnName, new ResultMapField(metaData.getColumnLabel(i), metaData.getColumnLabel(i), "A", null));

                        mapFieldWithHandler(resultSet, field, instance, typeHandlerRegistry);
                    }
                    returnList.add(instance);
                }

            }
        } else {
            Map<String, Method> fieldNames = new HashMap<>();
            PropertyDescriptor[] props = Introspector.getBeanInfo(parameterTypeClass).getPropertyDescriptors();
            for (PropertyDescriptor prop : props) {
                Method setter = prop.getWriteMethod();
                if (setter != null) {
                    //xxx:无论怎么样全都改为小写,避免繁琐的大小写问题
                    String fieldName = prop.getName().toLowerCase();
                    fieldNames.put(fieldName, setter);
                }
            }
            //xxx:获取结果集的元数据,拿到字段名,然后根据字段名找到对应的setter方法,然后根据setter方法的参数类型找到对应的处理器,进行注入
            ResultSetMetaData metaData = statement.getMetaData();
            List<String> columnNames = new ArrayList<>();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                columnNames.add(metaData.getColumnName(i + 1).toLowerCase());
            }
            //xxx:调用无参构造器,构建一个实例
            while (resultSet.next()) {
                T instance = (T) parameterTypeClass.getDeclaredConstructor().newInstance();
                for (String columnName : columnNames) {
                    Method setter = fieldNames.get(columnName);
                    TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandlers().get(setter.getParameterTypes()[0]);
                    setter.invoke(instance, typeHandler.getResult(resultSet, columnName));

                }
                returnList.add(instance);

            }
        }
        parameterMappingTokenHandler.resetParameterMappings();
        connection.close();
        return (T) returnList;

    }

    private void mapFieldWithHandler(ResultSet resultSet, ResultMapField field, Object instance, TypeHandlerRegistry typeHandlerRegistry) throws Exception {
        String column = field.getColumn();

        Object value;
        if (field.getJavaType() != null) {
            Class<?> javaTypeClass = getClassType(field.getJavaType());
            TypeHandler<?> typeHandler = typeHandlerRegistry.getTypeHandlers().get(javaTypeClass);
            value = typeHandler.getResult(resultSet, column);
        } else {
            value = resultSet.getObject(column);
        }
        PropertyDescriptor propDesc = new PropertyDescriptor(field.getProperty(), instance.getClass());
        Method setter = propDesc.getWriteMethod();
        if (setter != null) {
            setter.invoke(instance, value);
        }
    }


    @Override
    public <T> T selectQuery(Configuration configuration, MappedStatement mappedStatement, Object[] params) throws Exception {
        return null;
    }

    private Class<?> getClassType(String parameterType) throws ClassNotFoundException {
        if (parameterType != null) {
            return Class.forName(parameterType);
        }
        return null;
    }

    private BoundSql getBoundSql(String sql) {

        GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
        String jdbcSql = genericTokenParser.parse(sql);
        return new BoundSql(sql, jdbcSql);
    }
}
