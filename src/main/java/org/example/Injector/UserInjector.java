package org.example.Injector;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector.ControllerInjector;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector.Injector;
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
public class UserInjector implements ControllerInjector {
    @Override
    public void inject(Method method, Object object, List<Object> methodParams, Set<Integer> processedIndices, AutumnRequest myRequest, AutumnResponse myResponse) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (processedIndices.contains(i)) {
                continue;
            }

            Parameter parameter = parameters[i];
            if (parameter.getType().equals(ColorMappingEnum.class)) {
                Object value = useUrlGetParam("color", myRequest);
                if (value != null) {
                    methodParams.add(ColorMappingEnum.fromName(value.toString()));
                    processedIndices.add(i);
                }
            }
        }
    }
}
