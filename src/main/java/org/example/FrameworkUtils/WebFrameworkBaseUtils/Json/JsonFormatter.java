package org.example.FrameworkUtils.WebFrameworkBaseUtils.Json;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyComponent
public class JsonFormatter {


    public String toJson(Object obj) throws IllegalAccessException {
        if (obj == null) {
            return "null";
        }

        if (obj instanceof String || classChecker(obj)) {
            return "\"" + obj + "\"";
        }

        if (obj instanceof Map<?, ?> map) {
            String jsonMap = map.entrySet().stream()
                    .map(entry -> {
                        try {
                            String key = entry.getKey().toString();
                            String value = toJson(entry.getValue());
                            return "\"" + key + "\":" + value;
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            return "";
                        }
                    })
                    .collect(Collectors.joining(","));
            return "{" + jsonMap + "}";
        }

        // 新增对List的支持
        if (obj instanceof List<?> list) {
            String jsonList = list.stream()
                    .map(item -> {
                        try {
                            return toJson(item);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                            return "";
                        }
                    })
                    .collect(Collectors.joining(","));
            return "[" + jsonList + "]";
        }

        Field[] fields = obj.getClass().getDeclaredFields();
        String json = Arrays.stream(fields)
                .map(field -> {
                    field.setAccessible(true);
                    String name = field.getName();
                    try {
                        Object value = field.get(obj);
                        String jsonValue = toJson(value);
                        return "\"" + name + "\":" + jsonValue;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return "";
                    }
                })
                .collect(Collectors.joining(","));
        return "{" + json + "}";
    }

    private boolean classChecker(Object obj) {
        Class<?> clazz = obj.getClass();
        return clazz.equals(Boolean.class) || clazz.equals(Integer.class) ||
                clazz.equals(Character.class) || clazz.equals(Byte.class) ||
                clazz.equals(Short.class) || clazz.equals(Double.class) ||
                clazz.equals(Long.class) || clazz.equals(Float.class);
    }
}
