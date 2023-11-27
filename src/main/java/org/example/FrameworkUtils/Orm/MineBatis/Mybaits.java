package org.example.FrameworkUtils.Orm.MineBatis;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wsh
 */
public class Mybaits {
    public static <T> List<T> reflexByClass(Class<T> clazz, ResultSet resultSet, String[] fieldList) throws SQLException, NoSuchMethodException, NoSuchFieldException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<T> collectList = new ArrayList<>();
        while (resultSet.next()) {
            T targetObject = clazz.newInstance();
            for (String fieldName : fieldList) {
                Object fieldValue=null;
                Field field = clazz.getDeclaredField(fieldName);
                Class<?> fieldType = field.getType();
                if (fieldType.equals(String.class)) {
                    fieldValue = resultSet.getString(fieldName);
                } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
                    fieldValue = resultSet.getInt(fieldName);
                } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
                    fieldValue = resultSet.getDouble(fieldName);
                }
                field.setAccessible(true);
                field.set(targetObject, fieldValue);
            }
            collectList.add(targetObject);
        }
        return collectList;
    }
}
