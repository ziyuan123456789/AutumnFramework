package org.example.FrameworkUtils.DataStructure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author ziyuan
 * @since 2025.02
 */
@Data
public class MethodWrapper {

    @JsonIgnore
    private Method method;

    private HttpMethod httpMethod;

    private String beanName;

    private String methodName;

    private String returnType;

    private String[] parameterTypes;

    private String[] parameterNames;

    private Map<String, String> paramMap;


    public MethodWrapper(Method method, String beanName, HttpMethod httpMethod) {
        this.method = method;
        this.beanName = beanName;
        this.methodName = method.getName();
        this.returnType = method.getReturnType().getSimpleName();
        this.httpMethod = httpMethod;

        this.parameterTypes = Stream.of(method.getParameterTypes())
                .map(Class::getSimpleName)
                .toArray(String[]::new);

        this.parameterNames = Stream.of(method.getParameters())
                .map(Parameter::getName)
                .toArray(String[]::new);
    }


}
