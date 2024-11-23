package org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Injector
@Slf4j
public class BaseInjector implements ControllerInjector {

    @Override
    public void inject(Method method, Object object, List<Object> methodParams, Set<Integer> processedIndices, AutumnRequest myRequest, AutumnResponse myResponse) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (processedIndices.contains(i)) {
                continue;
            }
            Parameter parameter = parameters[i];
            String paramName = parameter.getName();
            Class<?> paramType = parameter.getType();
            if (paramType.equals(AutumnRequest.class)) {
                methodParams.add(myRequest);
                processedIndices.add(i);
            } else if (paramType.equals(AutumnResponse.class)) {
                methodParams.add(myResponse);
                processedIndices.add(i);
            } else {
                Object paramValue = useUrlGetParam(paramName, myRequest);
                if (paramValue != null) {
                    methodParams.add(paramValue);
                    processedIndices.add(i);
                }
            }
        }
    }
}
