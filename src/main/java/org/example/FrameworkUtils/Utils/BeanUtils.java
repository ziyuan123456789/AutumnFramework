package org.example.FrameworkUtils.Utils;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.11
 */
public class BeanUtils {

    public static boolean isJavaBean(Object obj) {
        if (obj == null) {
            return false;
        }
        Class<?> clazz = obj.getClass();
        try {
            clazz.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return false;
        }
        try {
            if (Collection.class.isAssignableFrom(clazz) ||
                    Map.class.isAssignableFrom(clazz) ||
                    clazz.isArray() ||
                    clazz.isPrimitive() ||
                    clazz == String.class ||
                    Number.class.isAssignableFrom(clazz) ||
                    Date.class.isAssignableFrom(clazz)) {
                return false;
            }

            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clazz, Object.class).getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                if (pd.getReadMethod() == null || pd.getWriteMethod() == null) {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
