package org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector;

import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ziyuan
 * @since 2024.09
 */
public interface ControllerInjector {
    void inject(Method invokeMethod, Object controllerObject, List<Object> methodParams, Set<Integer> processedIndices, AutumnRequest myRequest, AutumnResponse myResponse);

    default Object useUrlGetParam(String paramName, AutumnRequest myRequest) {
        Map<String, String> param = myRequest.getParameters();
        return param.get(paramName);
    }
}
