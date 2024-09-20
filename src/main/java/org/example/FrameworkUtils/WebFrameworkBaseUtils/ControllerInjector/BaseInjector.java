package org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector;

import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestParam;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.MyMultipartFile;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;

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
            MyRequestParam myRequestParam = parameter.getAnnotation(MyRequestParam.class);
            if (parameter.getType().equals(AutumnRequest.class)) {
                objectList.add(myRequest);
            }
            if (parameter.getType().equals(MyMultipartFile.class)) {
//                        objectList.add(myRequest.getMyMultipartFile());
            }
            if (parameter.getType().equals(AutumnResponse.class)) {
                objectList.add(myResponse);
            }
            if (myRequestParam != null) {
                if (!myRequestParam.value().isEmpty()) {
                    objectList.add(useUrlGetParam(myRequestParam.value(), myRequest));
                }
            }
        }
    }
}
