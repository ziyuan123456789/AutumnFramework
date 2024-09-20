package org.example.Injector;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector.ControllerInjector;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector.Injector;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Injector
public class UserInjector implements ControllerInjector {
    @Override
    public void inject(Method method, Object object, List<Object> objectList, AutumnRequest myRequest, AutumnResponse myResponse) {
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            if (parameter.getType().equals(ColorMappingEnum.class)) {
                objectList.add(ColorMappingEnum.fromName(useUrlGetParam("color", myRequest).toString()));
            }

        }
    }
}
