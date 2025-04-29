package org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Injector
@Slf4j
public class BaseInjector implements ControllerInjector {

    @Override
    public void inject(Method method, Object object, Object[] methodParams, AutumnRequest request, AutumnResponse response) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (methodParams[i] != null) {
                continue;
            }

            Parameter parameter = parameters[i];
            Class<?> paramType = parameter.getType();
            if (paramType.equals(AutumnRequest.class)) {
                methodParams[i] = request;
            } else if (paramType.equals(AutumnResponse.class)) {
                methodParams[i] = response;
            } else {
                String paramName = parameter.getName();
                Object paramValue = useUrlGetParam(paramName, request);
                if (paramValue != null && checkParamType(paramType)) {
                    methodParams[i] = paramValue;
                }
            }
        }
    }

    private Boolean checkParamType(Class paramType) {
        return paramType.equals(String.class) || paramType.equals(Integer.class) || paramType.equals(Long.class) || paramType.equals(Double.class) || paramType.equals(Float.class) || paramType.equals(Boolean.class);
    }
}
