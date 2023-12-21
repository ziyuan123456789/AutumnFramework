package org.example.FrameworkUtils.Orm.MineBatis;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.Annotation.MyComponent;
import org.example.FrameworkUtils.Annotation.MyParam;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyDelete;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyInsert;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MySelect;
import org.example.FrameworkUtils.Orm.MineBatis.OrmAnnotations.MyUpdate;
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
    static Jdbcinit jdbcinit = MyContext.getInstance().getBean(Jdbcinit.class);

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
            String sql = getSqlFromAnnotation(method);
            if (sql == null || sql.isEmpty()) {
                log.warn("sql为空");
                throw new IllegalStateException("sql为空");
            }
            sql = parseSql(sql, method, args);
            log.info(sql);
            Class<?> clazz = method.getReturnType();
            ResultSet r;
            MySelect select = method.getAnnotation(MySelect.class);
            if (select != null) {
                r = jdbcinit.querySql(sql);
            } else {
                return jdbcinit.executeUpdate(sql, clazz);
            }
            if (Collection.class.isAssignableFrom(clazz)) {
                Type returnType = method.getGenericReturnType();
                Class<?> clazzListType = null;
                if (returnType instanceof ParameterizedType) {
                    Type actualType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
                    clazzListType = Class.forName(actualType.getTypeName());
                }
                Field[] fields = clazzListType.getDeclaredFields();
                String[] fieldNames = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    fieldNames[i] = fields[i].getName();
                }
                log.info(Arrays.toString(fieldNames));
                return Mybaits.reflexByClass(clazzListType, r, fieldNames);
            } else {
                Field[] fields = clazz.getDeclaredFields();
                String[] fieldNames = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    fieldNames[i] = fields[i].getName();
                }
                List<?> result = Mybaits.reflexByClass(clazz, r, fieldNames);
                if(result.isEmpty()){
                    return null;
                }
                return result.get(0);
            }
        }
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
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = String.valueOf(paramMap.getOrDefault(paramName, ""));
            matcher.appendReplacement(buffer, paramValue);
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    private static String getSqlFromAnnotation(Method method) {
        MySelect select = method.getAnnotation(MySelect.class);
        if (select != null) {
            return select.value();
        }

        MyInsert insert = method.getAnnotation(MyInsert.class);
        if (insert != null) {
            return insert.value();
        }

        MyUpdate update = method.getAnnotation(MyUpdate.class);
        if (update != null) {
            return update.value();
        }

        MyDelete delete = method.getAnnotation(MyDelete.class);
        if (delete != null) {
            return delete.value();
        }

        return null;
    }


}

