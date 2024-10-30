package org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.MyMultipartFile;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Injector
@Slf4j
public class BaseInjector implements ControllerInjector {

    @Override
    public void inject(Method method, Object object, List<Object> objectList, AutumnRequest myRequest, AutumnResponse myResponse) {
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            String paramName = parameter.getName();
            Class<?> paramType = parameter.getType();

            if (paramType.equals(AutumnRequest.class)) {
                objectList.add(myRequest);
            } else if (paramType.equals(MyMultipartFile.class)) {
            } else if (paramType.equals(AutumnResponse.class)) {
                objectList.add(myResponse);
            } else {
                Object paramValue = useUrlGetParam(paramName, myRequest);
                if (paramValue != null) {
                    objectList.add(paramValue);
                }
            }
        }
    }
}
