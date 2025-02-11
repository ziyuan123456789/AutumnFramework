package org.example.FrameworkUtils.PropertiesReader;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyComponent;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author ziyuan
 * @since 2024.05
 */
@Slf4j
@MyComponent
public class PropertiesReader {
    public Properties initProperties() {
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/test.properties");
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return properties;
    }

    //xxx:配置文件编码器
    public static Object convertStringToType(String value, Class<?> type) {
        if (Integer.TYPE.equals(type) || Integer.class.equals(type)) {
            return Integer.parseInt(value);
        } else if (Float.TYPE.equals(type) || Float.class.equals(type)) {
            return Float.parseFloat(value);
        } else if (Double.TYPE.equals(type) || Double.class.equals(type)) {
            return Double.parseDouble(value);
        } else if (Long.TYPE.equals(type) || Long.class.equals(type)) {
            return Long.parseLong(value);
        } else if (Boolean.TYPE.equals(type) || Boolean.class.equals(type)) {
            return Boolean.parseBoolean(value);
        } else if (Byte.TYPE.equals(type) || Byte.class.equals(type)) {
            return Byte.parseByte(value);
        } else if (Short.TYPE.equals(type) || Short.class.equals(type)) {
            return Short.parseShort(value);
        } else if (Character.TYPE.equals(type) || Character.class.equals(type)) {
            if (value.length() == 1) {
                return value.charAt(0);
            } else {
                throw new IllegalArgumentException("Cannot convert String to Character: \"" + value + "\" is not a single character");
            }
        } else if (String.class.equals(type)) {
            return value;
        }
        throw new IllegalArgumentException("不支持的类型: " + type);
    }
}
