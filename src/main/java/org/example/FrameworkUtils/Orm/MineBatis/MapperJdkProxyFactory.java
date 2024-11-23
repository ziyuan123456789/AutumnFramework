package org.example.FrameworkUtils.Orm.MineBatis;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MySelect;
import org.example.FrameworkUtils.Orm.MineBatis.session.SqlSession;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.Impl.IntHandler;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.Impl.IntegerHandler;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.Impl.LocalDateTimeHandler;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.Impl.StringHandler;
import org.example.FrameworkUtils.Orm.MineBatis.type.TypeHandler.TypeHandler;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.04
 */
@Slf4j
public class MapperJdkProxyFactory implements SqlSession {
    //xxx:这里是一个map,用来存放不同类型的处理器,这里只有int和String,LocalDateTime三种
    private static Map<Class<?>, TypeHandler> handlerMap = new HashMap<>();

    static {
        handlerMap.put(int.class, new IntHandler());
        handlerMap.put(String.class, new StringHandler());
        handlerMap.put(Integer.class, new IntegerHandler());
        handlerMap.put(LocalDateTime.class, new LocalDateTimeHandler());
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T getMapper(Class<?> mapperClass) throws Exception {
        Object proxyInstance = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{mapperClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //xxx:跳过object里本来就有的方法,不进行代理
                if (Object.class.equals(method.getDeclaringClass())) {
                    return method.invoke(this, args);
                }
                //xxx:获取数据库连接
                Connection connection = getConnection();
                //xxx:进行注解内容的获取
                MySelect annotation = method.getAnnotation(MySelect.class);
                String sql = annotation.value();
                if (sql == null || sql.isEmpty()) {
                    log.warn("sql为空");
                    throw new IllegalStateException("sql为空");
                }
                //xxx:把参数和参数名对应起来,放到map里
                Map<String, Object> paramValueMapping = new HashMap<>();
                Parameter[] parameters = method.getParameters();
                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    paramValueMapping.put(parameter.getName(), args[i]);

                }
                //xxx:构建解析器,把mybatis#{}风格转化为jdbc ?风格
                ParameterMappingTokenHandler parameterMappingTokenHandler = new ParameterMappingTokenHandler();
                GenericTokenParser genericTokenParser = new GenericTokenParser("#{", "}", parameterMappingTokenHandler);
                String jdbcSql = genericTokenParser.parse(sql);
                PreparedStatement statement = connection.prepareStatement(jdbcSql);
                //xxx:在替换#{}的同时,取出里面的参数名称封装为ParameterMapping,放到数组中,保存好顺序
                List<ParameterMapping> parameterMapping = parameterMappingTokenHandler.getParameterMapping();
                for (int i = 0; i < parameterMapping.size(); i++) {
                    //xxx:拿到参数名字,根据名字找到对应的值,然后根据值的类型找到对应的处理器,进行setParameter
                    String argName = parameterMapping.get(i).getProperty();
                    Class<?> clazz = paramValueMapping.get(argName).getClass();
                    //xxx:jdbc这个为什么不从0开始?反而从1开始?
                    handlerMap.get(clazz).setParameter(statement, i + 1, paramValueMapping.get(argName));
                }
                statement.execute();

                //xxx:上面都是解析参数拼接参数,构建sql的代码块,相比之前使用正则匹配的版本,改用了jdbc的拼接方式,可以防止sql注入,更安全.
                //xxx:下面则是解析结果集,把结果集的内容注入到返回值中
                ResultSet resultSet = statement.getResultSet();
                Class<?> clazz = method.getReturnType();
                List<Object> returnList=new ArrayList<>();
                boolean isList = false;
                //xxx:先看看是不是一个List 如果是则取出泛型类型覆盖clazz,并且标记isList为true
                if(clazz.isAssignableFrom(List.class)){
                    Type returnType = method.getGenericReturnType();
                    if(returnType instanceof ParameterizedType type){
                        clazz= (Class<?>) type.getActualTypeArguments()[0];
                        isList=true;
                    }
                }
                //xxx:用来存放字段名和对应的setter方法,之前版本直接注入字段,这里改用setter方法,更符合java的规范
                Map<String, Method> fieldNames = new HashMap<>();
                PropertyDescriptor[] props = Introspector.getBeanInfo(clazz).getPropertyDescriptors();
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
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    for (String columnName : columnNames) {
                        Method setter = fieldNames.get(columnName);
                        TypeHandler typeHandler = handlerMap.get(setter.getParameterTypes()[0]);
                        setter.invoke(instance, typeHandler.getResult(resultSet, columnName));

                    }
                    returnList.add(instance);

                }
                connection.close();
                if (returnList.isEmpty()) {
                    return null;
                }
                if (isList) {
                    return returnList;
                } else {
                    return returnList.get(0);
                }

            }

            private static Connection getConnection() throws SQLException {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true", "root", "root");
                return connection;
            }
        });
        return (T) proxyInstance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        log.error("获取数据库连接,但这个代理工厂暂时没有实现getConnection,请修改源代码适配你的数据库");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true", "root", "root");
    }

    @Override
    public <T> List<T> selectList(String statementId, Method method, Object[] args) throws Exception {
        return List.of();
    }

    @Override
    public <T> T selectOne(String statementId,Method method,  Object[] args) throws Exception {
        return null;
    }

    @Override
    public int insert(String statementId, Method method, Object[] args) throws Exception {
        return 0;
    }

    @Override
    public int update(String statementId,Method method,  Object[] args) throws Exception {
        return 0;
    }

    @Override
    public int delete(String statementId,Method method,  Object[] args) throws Exception {
        return 0;
    }


}
