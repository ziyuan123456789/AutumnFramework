package com.autumn.swaggerstarter;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.AutumnResponse;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.MyServers.ServletResponseAdapter;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2024.08
 */
@MyController
public class BaseController implements BeanFactoryAware {
    private AutumnBeanFactory beanFactory;

    @MyRequestMapping("/myswagger")
    public View myswagger() {
        return new View("MySwagger.html");
    }
    @MyRequestMapping("/ReactPage")
    public View minireactPage() {
        return new View("minireact.html");
    }


    @MyRequestMapping("/getminireact")
    public void getminireact (AutumnResponse response) {
        ServletResponseAdapter servletResponseAdapter=(ServletResponseAdapter) response;
        servletResponseAdapter.outputJavaScriptFile("js/minireact.js");
    }



    @MyRequestMapping("/urlMapping")
    public Map<String, Object> urlMapping() {
        Map<String, String> urlToMethodMap = (Map<String, String>) beanFactory.get("urlmapping");
        Map<String, Object> openApiMap = new HashMap<>();
        for (Map.Entry<String, String> entry : urlToMethodMap.entrySet()) {
            int lastIndex = entry.getValue().lastIndexOf(".");
            String classurl = entry.getValue().substring(0, lastIndex);
            String methodName = entry.getValue().substring(lastIndex + 1);
            try {
                Class<?> clazz = Class.forName(classurl);
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().equals(methodName)) {
                        List<String> methodsName = new ArrayList<>();
                        Class<?>[] paramTypes = method.getParameterTypes();
                        for (Class i : paramTypes) {
                            methodsName.add(i.getSimpleName());
                        }
                        Class<?> returnType = method.getReturnType();
                        Map<String, Object> methodInfo = new HashMap<>();
                        methodInfo.put("parameters", methodsName);
                        methodInfo.put("returnType", returnType.getSimpleName());
                        openApiMap.put(entry.getKey(), methodInfo);
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return openApiMap;
    }

    @Override
    public void setBeanFactory(AutumnBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
