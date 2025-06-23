package org.example.FrameworkUtils.DataStructure;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2025.02
 */
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

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Map<String, String> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, String> paramMap) {
        this.paramMap = paramMap;
    }
}
