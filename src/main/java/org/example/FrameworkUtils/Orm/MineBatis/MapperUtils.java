package org.example.FrameworkUtils.Orm.MineBatis;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyParam;
import org.example.FrameworkUtils.Annotation.MySelect;
import org.example.FrameworkUtils.Webutils.MyContext;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wsh
 */
@Slf4j
@MyComponent
public class MapperUtils {
    //xxx jdk动态代理,代理接口
    public static <T> T init(Class<T> targetClass) {
        return (T) Proxy.newProxyInstance(targetClass.getClassLoader(), new Class<?>[]{targetClass},
                new InvokeHandler());
    }

    static class InvokeHandler implements InvocationHandler {
        //xxx 把sql执行后的内容依照接口的类型自动注入,并返还
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //xxx :检查是否调用object方法,是的话反射执行,不进行代理
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }
            MySelect select = method.getAnnotation(MySelect.class);
            String sql = null;
            if (select != null) {
                sql = select.value();
                sql=parseSql(sql, method, args);
                System.out.println(sql);
            } else {
                log.warn("sql为空");
            }
            Class<?> clazz = method.getReturnType();
            Jdbcinit jdbcinit = (Jdbcinit) MyContext.getInstance().getBean(Jdbcinit.class);

            ResultSet r = jdbcinit.querySql(sql);
            if (Collection.class.isAssignableFrom(clazz)) {
                Type returnType = method.getGenericReturnType();
                Class<?> clazz123 = null;
                if (returnType instanceof ParameterizedType) {
                    Type actualType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
                    clazz123 = Class.forName(actualType.getTypeName());
                }
                Field[] fields = clazz123.getDeclaredFields();
                String[] fieldNames = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    fieldNames[i] = fields[i].getName();
                }
                log.error(Arrays.toString(fieldNames));
                List<?> result = Mybaits.reflexByClass(clazz123, r, fieldNames);
                return result;
            } else {
                Field[] fields = clazz.getDeclaredFields();
                String[] fieldNames = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    fieldNames[i] = fields[i].getName();
                }
                List<?> result = Mybaits.reflexByClass(clazz, r, fieldNames);
                if(result.size()==0){
                    return null;
                }
                return result.get(0);
            }
        }
    }

    private String sqlMatcher(String sql) {

        return sql;
    }

    private static String parseSql(String sql, Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < parameters.length; i++) {
            MyParam myParam = parameters[i].getAnnotation(MyParam.class);
            if (myParam != null) {
                if (args[i].getClass()==Integer.class){
                    paramMap.put(myParam.value(), args[i]);
                }else{
                    paramMap.put(myParam.value(), "'"+args[i]+"'");
                }

            }
        }
        Pattern pattern = Pattern.compile("#\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(sql);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = String.valueOf(paramMap.getOrDefault(paramName, ""));
            matcher.appendReplacement(buffer, paramValue);
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

}

