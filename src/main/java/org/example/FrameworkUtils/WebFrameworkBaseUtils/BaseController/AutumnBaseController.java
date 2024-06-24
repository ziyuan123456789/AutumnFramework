package org.example.FrameworkUtils.WebFrameworkBaseUtils.BaseController;

import org.example.FrameworkUtils.AutumnCore.Annotation.MyAutoWired;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyController;
import org.example.FrameworkUtils.AutumnCore.Annotation.MyRequestMapping;
import org.example.FrameworkUtils.AutumnCore.Annotation.Value;
import org.example.FrameworkUtils.AutumnCore.AutoConfiguration.AutumnMvcConfiguration;
import org.example.FrameworkUtils.AutumnCore.Ioc.AutumnBeanFactory;
import org.example.FrameworkUtils.AutumnCore.Ioc.BeanFactoryAware;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Icon;
import org.example.FrameworkUtils.WebFrameworkBaseUtils.ResponseType.Views.View;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ziyuan
 * @since 2023.11
 */
@MyController
public class AutumnBaseController implements BeanFactoryAware {

    @Value("baseHtml")
    String baseHtml;

    @MyAutoWired
    private AutumnMvcConfiguration autumnMvcConfiguration;
    private AutumnBeanFactory beanFactory;

    @MyRequestMapping("/favicon.ico")
    public Icon getIcon() {
        return new Icon("myicon.ico");
    }

    @MyRequestMapping("/")
    public View getMainPage() {
        System.out.println("woao");
        return autumnMvcConfiguration.getMainPage();
    }

    @MyRequestMapping("/404")
    public View notFoundPage() {
        return autumnMvcConfiguration.get404Page();
    }

    @MyRequestMapping("/myswagger")
    public View myswagger() {
        return new View("MySwagger.html");
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
                        List<String>methodsName=new ArrayList<>();
                        Class<?>[] paramTypes = method.getParameterTypes();
                        for(Class i: paramTypes) {
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

            }catch (Exception e) {
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
