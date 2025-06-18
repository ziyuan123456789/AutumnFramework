package org.example.FrameworkUtils.WebFrameworkBaseUtils.ControllerInjector;

import com.autumn.mvc.AutumnNotBlank;
import com.autumn.mvc.AutumnNotNull;
import com.autumn.mvc.ErrorHandler;
import com.autumn.mvc.SessionAttribute;
import lombok.extern.slf4j.Slf4j;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.Json.JsonFormatter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnRequest;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.09
 */
@Injector
@Slf4j
public class BaseInjector implements ControllerInjector {

    @MyAutoWired
    private JsonFormatter jsonFormatter;

    @Override
    public void inject(Method method, Object object, Object[] methodParams,
                       AutumnRequest request, AutumnResponse response) {
        Parameter[] parameters = method.getParameters();
        List<Map<String, Object>> errors = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            if (methodParams[i] != null) {
                continue;
            }
            Parameter parameter = parameters[i];
            Class<?> paramType = parameter.getType();
            SessionAttribute sessionAttribute = parameter.getAnnotation(SessionAttribute.class);
            if (paramType.equals(AutumnRequest.class)) {
                methodParams[i] = request;
            } else if (paramType.equals(AutumnResponse.class)) {
                methodParams[i] = response;
            } else if (sessionAttribute != null && paramType.equals(String.class)) {
                if (sessionAttribute.required()) {
                    methodParams[i] = request.getSession().getAttribute(sessionAttribute.name());
                    if (methodParams[i] == null && sessionAttribute.required()) {
                        addError(errors, "Session 中的 '" + sessionAttribute.name() + "' 不存在",
                                parameter, 400);
                    }
                } else {
                    methodParams[i] = request.getSession().getAttribute(sessionAttribute.name());
                }
            } else {
                String paramName = parameter.getName();
                Object paramValue = useUrlGetParam(paramName, request);

                validateParameter(parameter, paramValue, method, errors);
                if (paramValue != null && checkParamType(paramType)) {
                    methodParams[i] = paramValue;
                }
            }
        }
        if (!errors.isEmpty()) {
            handleValidationErrors(method.getAnnotation(ErrorHandler.class), errors, response);
            Arrays.fill(methodParams, null);
        }
    }

    private void validateParameter(Parameter parameter, Object paramValue, Method method,
                                   List<Map<String, Object>> errors) {
        ErrorHandler errorHandler = method.getAnnotation(ErrorHandler.class);
        AutumnNotNull autumnNotNull = parameter.getAnnotation(AutumnNotNull.class);
        if (autumnNotNull != null && paramValue == null) {
            addError(errors, "不能为NULL", parameter,
                    errorHandler != null ? errorHandler.errorCode() : 500);
        }
        AutumnNotBlank autumnNotBlank = parameter.getAnnotation(AutumnNotBlank.class);
        if (autumnNotBlank != null && paramValue == null
                && String.class.equals(parameter.getType())) {
            addError(errors, parameter.getName() + "不能为空", parameter,
                    errorHandler != null ? errorHandler.errorCode() : 500);
        }
    }

    private void addError(List<Map<String, Object>> errors, String message, Parameter parameter, int code) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("field", parameter);
        error.put("message", message);
        error.put("code", code);
        errors.add(error);
    }

    private void handleValidationErrors(ErrorHandler errorHandler, List<Map<String, Object>> errors,
                                        AutumnResponse response) {
        Map<String, Object> errorResponse = new LinkedHashMap<>();
        errorResponse.put("status", "VALIDATION_FAILED");
        errorResponse.put("errors", errors);
        int statusCode = errors.stream()
                .mapToInt(e -> (Integer) e.get("code"))
                .findFirst()
                .orElse(500);
        String title = errorHandler != null ? errorHandler.title() : "ERROR";
        response.outputErrorMessage(title, errorResponse.toString(), statusCode);

    }


    private Boolean checkParamType(Class paramType) {
        return paramType.equals(String.class) || paramType.equals(Integer.class) || paramType.equals(Long.class) || paramType.equals(Double.class) || paramType.equals(Float.class) || paramType.equals(Boolean.class);
    }

}
