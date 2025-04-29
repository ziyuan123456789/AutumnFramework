package org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.09
 */
public interface ControllerInjector {
    void inject(Method method, Object object, Object[] methodParams, AutumnRequest myRequest, AutumnResponse myResponse);

    default Object useUrlGetParam(String paramName, AutumnRequest myRequest) {
        Map<String, String> param = myRequest.getParameters();
        return param.get(paramName);
    }
}
