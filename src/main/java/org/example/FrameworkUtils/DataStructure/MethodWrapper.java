package org.example.FrameworkUtils.DataStructure;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2025.02
 */
@Data
public class MethodWrapper {

    private Method method;

    private String beanName;

    private Map<String, String> paramMap;

    public MethodWrapper(Method method, String beanName, Map<String, String> paramMap) {
        this.method = method;
        this.beanName = beanName;
        this.paramMap = paramMap;
    }

    public MethodWrapper(Method method, String beanName) {
        this.method = method;
        this.beanName = beanName;
    }

}
