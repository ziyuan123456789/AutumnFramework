package org.example.FrameworkUtils.Webutils.Json;

import org.example.FrameworkUtils.Annotation.MyComponent;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author wangzhiyi
 * @since 2023.11
 */
@MyComponent
public class JsonFormatter {
    public <T extends Map> String mapTojson(T map) {
        StringBuilder sb = new StringBuilder();
        return map.toString();
    }

}
