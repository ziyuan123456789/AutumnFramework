package org.example.Injector;

import org.example.FrameworkUtils.DataStructure.MethodWrapper;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector.ControllerInjector;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector.Injector;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;

import java.lang.reflect.Parameter;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Injector
public class UserInjector implements ControllerInjector {


    @Override
    public void inject(MethodWrapper method, Object object, Object[] methodParams, AutumnRequest myRequest, AutumnResponse myResponse) {
        Parameter[] parameters = method.getMethod().getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (methodParams[i] != null) {
                continue;
            }
            Parameter parameter = parameters[i];
            if (parameter.getType().equals(ColorMappingEnum.class)) {
                Object value = useUrlGetParam("color", myRequest);
                if (value != null) {
                    methodParams[i] = ColorMappingEnum.fromName(value.toString());
                }
            }
        }
    }
}
